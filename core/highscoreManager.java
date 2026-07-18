package core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class highscoreManager implements Runnable{

	//Firebase project details (Project settings > General page has the Project ID and Web API Key -
	//no service account or Auth needed). The API key is safe to ship inside the game jar: Firestore
	//Security Rules on the "highscores" collection (see the setup notes) restrict it to creating one
	//valid score document and reading scores - nothing else, unlike the old raw JDBC connection
	//string which gave anyone full read/write/drop access.
	private static final String FIREBASE_PROJECT_ID = "battle-tank-3";
	private static final String FIREBASE_API_KEY = "AIzaSyCbKB324xgpJkY2Ei4bRoy_6dutUZ-bDMc";

	private static final String BASE_URL = "https://firestore.googleapis.com/v1/projects/"
			+ FIREBASE_PROJECT_ID + "/databases/(default)/documents";

	private static final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	//tolerant of the whitespace Firestore's pretty-printed REST responses put around colons/braces
	//(e.g. "player_name": { "stringValue": ... } rather than the compact form) - see fetchSkillLevel
	//for how these two are paired up per-document
	private static final Pattern NAME_PATTERN = Pattern.compile("\"player_name\"\\s*:\\s*\\{\\s*\"stringValue\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
	private static final Pattern TIME_PATTERN = Pattern.compile("\"finishing_time\"\\s*:\\s*\\{\\s*\"integerValue\"\\s*:\\s*\"(-?\\d+)\"");
	private static final Pattern ID_TOKEN_PATTERN = Pattern.compile("\"idToken\":\\s*\"([^\"]+)\"");
	private static final Pattern LOCAL_ID_PATTERN = Pattern.compile("\"localId\":\\s*\"([^\"]+)\"");

	public int counter;

	public int status;
	public static final int idle = 0;
	public static final int processing = 1;
	public static final int error = 2;

	public int task;
	public static final int none = 0;
	public static final int loadHighscores = 1;
	public static final int uploadScore = 2;

	public boolean isSleeping;

	public String playerName;

	public String[][] result;

	public highscoreManager(){
		status = processing;
		playerName = "";
	}


	@Override
	public void run() {
		while(true) {
			if(counter == 0) {
				status = idle;
			}

			if(status == idle) {

				if(task != none) {
					status = processing;

					if(task == loadHighscores) {
						//get high scores from Firestore, one skill level at a time
						try {
							String[][] myResult = new String[30][2];

							fetchSkillLevel(0, myResult, 0);
							fetchSkillLevel(1, myResult, 10);
							fetchSkillLevel(2, myResult, 20);

							result = myResult;
							playerName = "";

						} catch (Exception e) {
							e.printStackTrace();
							status = error;
							result = null;
							playerName = "";
						}

					}else if(task == uploadScore) {
						try {
							submitScoreWithRateLimit(playerName, (int)(mainThread.matchElapsedMs/1000), mainThread.ec.difficulty);
							//invalidate the cached leaderboard so the next time it's viewed, it re-fetches
							//and shows the score that was just submitted, instead of stale cached data
							//from whenever the board was first opened this session
							result = null;

						}catch (Exception e) {
							e.printStackTrace();
							status = error;
							playerName = "";
						}
					}

					if(status != error)
						status = idle;
					task = none;
				}
			}

			isSleeping = true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isSleeping = false;

			counter++;
		}

	}

	//fetches up to 10 unique-named top scores for one skill level, writing them into myResult
	//starting at index "start" - mirrors the original JDBC version's per-level dedup/limit behaviour
	private void fetchSkillLevel(int skillLevel, String[][] myResult, int start) throws Exception {
		//ask for a generous buffer since duplicate names get filtered out client-side below
		String query = "{"
				+ "\"structuredQuery\":{"
				+ "\"from\":[{\"collectionId\":\"highscores\"}],"
				+ "\"where\":{\"fieldFilter\":{\"field\":{\"fieldPath\":\"skill_level\"},\"op\":\"EQUAL\",\"value\":{\"integerValue\":\"" + skillLevel + "\"}}},"
				+ "\"orderBy\":[{\"field\":{\"fieldPath\":\"finishing_time\"},\"direction\":\"ASCENDING\"}],"
				+ "\"limit\":50"
				+ "}}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + ":runQuery?key=" + FIREBASE_API_KEY))
				.header("Content-Type", "application/json")
				.timeout(Duration.ofSeconds(10))
				.POST(HttpRequest.BodyPublishers.ofString(query))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200)
			throw new RuntimeException("Firestore query failed: " + response.statusCode() + " " + response.body());

		//each result document contributes exactly one player_name and one finishing_time, appearing
		//in the same relative order (name before time) with no interleaving between documents - so
		//advancing both matchers in lockstep pairs them correctly without needing to first isolate
		//each document's own "fields" block (which isn't actually flat - every value is wrapped in
		//its own type-descriptor object, e.g. "player_name": {"stringValue": ...}, so a naive
		//first-brace-to-first-brace scan was cutting off after only the first nested field)
		int numOfRows = start;
		Matcher nameM = NAME_PATTERN.matcher(response.body());
		Matcher timeM = TIME_PATTERN.matcher(response.body());
		while(nameM.find() && timeM.find() && numOfRows < start + 10){
			String name = unescapeJson(nameM.group(1));
			int seconds = Integer.parseInt(timeM.group(1));

			if(!hasDuplicateName(start, numOfRows, myResult, name)){
				myResult[numOfRows][0] = name;
				myResult[numOfRows][1] = secondsToString(seconds);
				numOfRows++;
			}
		}
	}

	//signs in anonymously (giving this submission a verifiable identity the security rules can key a
	//cooldown off of), submits the score, then marks the identity's last-submission time so the next
	//attempt gets throttled. A fresh anonymous identity is created per submission rather than reused -
	//it doesn't need to persist across game sessions, since the cooldown only needs to catch someone
	//hammering the same script/loop, not fingerprint a specific player across launches.
	private void submitScoreWithRateLimit(String name, int finishingTimeSeconds, int skillLevel) throws Exception {
		String[] auth = signInAnonymously();
		String idToken = auth[0];
		String uid = auth[1];

		//submit first, against whatever the rate-limit doc currently says (or its absence) - touching
		//the rate limit only AFTER a successful submission, so this attempt isn't blocked by its own update
		uploadScore(idToken, name, finishingTimeSeconds, skillLevel);
		touchRateLimit(idToken, uid);
	}

	private String[] signInAnonymously() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY))
				.header("Content-Type", "application/json")
				.timeout(Duration.ofSeconds(10))
				.POST(HttpRequest.BodyPublishers.ofString("{\"returnSecureToken\":true}"))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200)
			throw new RuntimeException("Anonymous sign-in failed: " + response.statusCode() + " " + response.body());

		Matcher tokenM = ID_TOKEN_PATTERN.matcher(response.body());
		Matcher uidM = LOCAL_ID_PATTERN.matcher(response.body());
		if(!tokenM.find() || !uidM.find())
			throw new RuntimeException("Anonymous sign-in response missing idToken/localId: " + response.body());

		return new String[]{tokenM.group(1), uidM.group(1)};
	}

	private void uploadScore(String idToken, String name, int finishingTimeSeconds, int skillLevel) throws Exception {
		String body = "{\"fields\":{"
				+ "\"player_name\":{\"stringValue\":\"" + escapeJson(name) + "\"},"
				+ "\"finishing_time\":{\"integerValue\":\"" + finishingTimeSeconds + "\"},"
				+ "\"skill_level\":{\"integerValue\":\"" + skillLevel + "\"}"
				+ "}}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/highscores?key=" + FIREBASE_API_KEY))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + idToken)
				.timeout(Duration.ofSeconds(10))
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() / 100 != 2)
			throw new RuntimeException("Firestore insert failed: " + response.statusCode() + " " + response.body());
	}

	//records "now" (by this client's clock) against this anonymous identity so the next submission can
	//be throttled by the security rules; those rules cross-check this value against Firestore's own
	//server-perceived request time within a tolerance, so a client can't just backdate it to permanently
	//bypass its own cooldown
	private void touchRateLimit(String idToken, String uid) throws Exception {
		String body = "{\"fields\":{\"lastSubmission\":{\"timestampValue\":\"" + Instant.now().toString() + "\"}}}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/rateLimits/" + uid + "?key=" + FIREBASE_API_KEY + "&updateMask.fieldPaths=lastSubmission"))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + idToken)
				.timeout(Duration.ofSeconds(10))
				.method("PATCH", HttpRequest.BodyPublishers.ofString(body))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() / 100 != 2)
			throw new RuntimeException("Rate-limit update failed: " + response.statusCode() + " " + response.body());
	}

	private static String escapeJson(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String unescapeJson(String s) {
		return s.replace("\\\"", "\"").replace("\\\\", "\\");
	}

	public boolean hasDuplicateName(int start,  int current, String[][] myResult,  String name) {
		for(int i = start; i < current; i++) {
			if(myResult[i][0].toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	public String secondsToString(int pTime) {
	    int min = pTime/60;
	    int sec = pTime-(min*60);

	    String strMin = placeZeroIfNeede(min);
	    String strSec = placeZeroIfNeede(sec);
	    return String.format("%s:%s",strMin,strSec);
	}

	public String placeZeroIfNeede(int number) {
	    return (number >=10)? Integer.toString(number):String.format("0%s",Integer.toString(number));
	}
}
