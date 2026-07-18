package audio;

import java.util.Random;

//Generates every sound effect in code as 16-bit mono PCM, so the game ships with
//zero audio assets and zero licensing to worry about.
public class SoundSynth {

	public static final float SAMPLE_RATE = 22050f;

	private static final Random RNG = new Random();

	private interface Waveform {
		double sample(double phase);
	}

	private static final Waveform SINE = Math::sin;
	private static final Waveform TRIANGLE = phase -> {
		double x = ((phase / (2 * Math.PI)) % 1 + 1) % 1;
		return 4 * Math.abs(x - 0.5) - 1;
	};
	private static final Waveform SQUARE = phase -> Math.sin(phase) >= 0 ? 1 : -1;
	private static final Waveform SAWTOOTH = phase -> {
		double x = ((phase / (2 * Math.PI)) % 1 + 1) % 1;
		return 2 * x - 1;
	};

	private static int ms(int milliseconds) {
		return (int) (SAMPLE_RATE * milliseconds / 1000.0);
	}

	//linear frequency sweep from freqStart to freqEnd over n samples
	private static double[] tone(double freqStart, double freqEnd, int n, Waveform wf) {
		double[] buf = new double[n];
		double phase = 0;
		for (int i = 0; i < n; i++) {
			double frac = n <= 1 ? 0 : i / (double) (n - 1);
			double freq = freqStart + (freqEnd - freqStart) * frac;
			phase += 2 * Math.PI * freq / SAMPLE_RATE;
			buf[i] = wf.sample(phase);
		}
		return buf;
	}

	//one-pole lowpass filtered white noise; smaller alpha = duller/bassier
	private static double[] filteredNoise(int n, double alpha) {
		double[] buf = new double[n];
		double y = 0;
		for (int i = 0; i < n; i++) {
			double x = RNG.nextDouble() * 2 - 1;
			y += alpha * (x - y);
			buf[i] = y;
		}
		return buf;
	}

	//filtered noise whose cutoff opens up over time, for a rocket "whoosh"
	private static double[] sweepingNoise(int n, double alphaStart, double alphaEnd) {
		double[] buf = new double[n];
		double y = 0;
		for (int i = 0; i < n; i++) {
			double frac = i / (double) n;
			double alpha = alphaStart + (alphaEnd - alphaStart) * frac;
			double x = RNG.nextDouble() * 2 - 1;
			y += alpha * (x - y);
			buf[i] = y;
		}
		return buf;
	}

	private static double expDecay(int i, int n, double rate) {
		return Math.exp(-rate * i / n);
	}

	//envelope that rises then falls, peaking at peakFrac through the buffer
	private static double bell(int i, int n, double peakFrac) {
		double frac = i / (double) n;
		if (frac < peakFrac)
			return frac / peakFrac;
		return Math.max(0, 1 - (frac - peakFrac) / (1 - peakFrac));
	}

	private static double[] envelope(double[] buf, java.util.function.IntToDoubleFunction env) {
		double[] out = new double[buf.length];
		for (int i = 0; i < buf.length; i++)
			out[i] = buf[i] * env.applyAsDouble(i);
		return out;
	}

	private static double[] gain(double[] buf, double g) {
		double[] out = new double[buf.length];
		for (int i = 0; i < buf.length; i++)
			out[i] = buf[i] * g;
		return out;
	}

	//soft-clip saturation for punch/grit without harsh digital clipping; drive > 1 pushes harder
	private static double[] saturate(double[] buf, double drive) {
		double[] out = new double[buf.length];
		for (int i = 0; i < buf.length; i++)
			out[i] = Math.tanh(buf[i] * drive);
		return out;
	}

	private static double[] padStart(double[] buf, int totalLength) {
		double[] out = new double[totalLength];
		System.arraycopy(buf, 0, out, 0, Math.min(buf.length, totalLength));
		return out;
	}

	private static double[] mix(double[]... parts) {
		int n = 0;
		for (double[] p : parts)
			n = Math.max(n, p.length);
		double[] out = new double[n];
		for (double[] p : parts)
			for (int i = 0; i < p.length; i++)
				out[i] += p[i];
		return out;
	}

	private static double[] concat(double[]... parts) {
		int total = 0;
		for (double[] p : parts)
			total += p.length;
		double[] out = new double[total];
		int pos = 0;
		for (double[] p : parts) {
			System.arraycopy(p, 0, out, pos, p.length);
			pos += p.length;
		}
		return out;
	}

	private static byte[] toPcm(double[] buf, double masterGain) {
		byte[] out = new byte[buf.length * 2];
		for (int i = 0; i < buf.length; i++) {
			double v = buf[i] * masterGain;
			if (v > 1)
				v = 1;
			if (v < -1)
				v = -1;
			short s = (short) Math.round(v * Short.MAX_VALUE);
			out[i * 2] = (byte) (s & 0xff);
			out[i * 2 + 1] = (byte) ((s >> 8) & 0xff);
		}
		return out;
	}

	//a crisp UI button tap: bright, very short, no low-end thump so it reads as a press, not a thud
	public static byte[] click() {
		int n = ms(18);
		double[] tick = envelope(filteredNoise(n, 0.9), i -> expDecay(i, n, 17));
		double[] snap = envelope(tone(1900, 900, n, SINE), i -> expDecay(i, n, 19));
		return toPcm(saturate(mix(gain(tick, 0.55), gain(snap, 0.6)), 1.15), 0.3);
	}

	//a single "ding": the exact same tone+noise click structure as creditSpend() below, just pitched up
	private static double[] ding(int n, double startPitch, double endPitch) {
		return envelope(mix(tone(startPitch, endPitch, n, SINE), gain(filteredNoise(n, 0.6), 0.4)), i -> expDecay(i, n, 14));
	}

	//a longer "ding-ding-ding-ding..." for a credit increase (harvester delivering ore, a cancelled
	//building refunding): the same simple click creditSpend() uses, just pitched higher and repeated
	//several times, rather than trying to synthesize a more elaborate/realistic coin sound
	public static byte[] creditEarn() {
		int dingLen = ms(50);
		int gapLen = ms(20);
		int dingCount = 12;

		double[] silence = new double[gapLen];
		double[] result = new double[0];
		for (int i = 0; i < dingCount; i++) {
			double pitch = 3200 + RNG.nextDouble() * 400;
			double[] d = ding(dingLen, pitch, pitch * 0.65);
			result = i == 0 ? d : concat(result, silence, d);
		}
		return toPcm(result, 0.22);
	}

	//a quiet metallic coin-click for a credit decrease (construction, repair, research draining the
	//treasury) - kept short and quiet since it can fire repeatedly during a fast trickle
	public static byte[] creditSpend() {
		int n = ms(35);
		double[] click = envelope(mix(tone(950, 550, n, SINE), gain(filteredNoise(n, 0.6), 0.4)), i -> expDecay(i, n, 14));
		return toPcm(click, 0.12);
	}

	//unit selection blip: kept soft/melodic, matches the softer command-confirmation style
	public static byte[] select() {
		int n1 = ms(70), gap = ms(15), n2 = ms(70);
		double[] blip1 = envelope(tone(880, 880, n1, SINE), i -> expDecay(i, n1, 5));
		double[] silence = new double[gap];
		double[] blip2 = envelope(tone(1320, 1320, n2, SINE), i -> expDecay(i, n2, 5));
		return toPcm(concat(blip1, silence, blip2), 0.32);
	}

	//command-confirmation chirp: kept soft/melodic rather than industrial, reads better as a quick acknowledgement
	public static byte[] move() {
		int n = ms(110);
		double[] buf = envelope(tone(700, 450, n, TRIANGLE), i -> expDecay(i, n, 4));
		return toPcm(buf, 0.32);
	}

	public static byte[] attack() {
		int n = ms(140);
		double[] toneBuf = envelope(tone(320, 220, n, SQUARE), i -> expDecay(i, n, 3.5));
		double[] grit = envelope(filteredNoise(n, 0.6), i -> expDecay(i, n, 6));
		return toPcm(mix(toneBuf, gain(grit, 0.25)), 0.3);
	}

	//a mechanical valve-clunk, not a soft chirp
	public static byte[] harvest() {
		int n = ms(85);
		double[] clunk = envelope(tone(170, 105, n, SQUARE), i -> expDecay(i, n, 7));
		double[] grit = envelope(filteredNoise(n, 0.45), i -> expDecay(i, n, 8));
		return toPcm(saturate(mix(clunk, gain(grit, 0.4)), 1.25), 0.3);
	}

	public static byte[] shootCannon() {
		int n = ms(140);
		double[] thump = envelope(tone(95, 60, n, SINE), i -> expDecay(i, n, 7));
		double[] crack = envelope(filteredNoise(n, 0.5), i -> expDecay(i, n, 9));
		return toPcm(saturate(mix(thump, gain(crack, 0.65)), 1.3), 0.58);
	}

	public static byte[] shootRocket() {
		int n = ms(230);
		double[] whoosh = envelope(sweepingNoise(n, 0.08, 0.45), i -> bell(i, n, 0.35));
		double[] sweep = envelope(tone(150, 550, n, SINE), i -> bell(i, n, 0.35));
		return toPcm(saturate(mix(gain(whoosh, 0.8), gain(sweep, 0.35)), 1.15), 0.52);
	}

	//heavy tank's main gun: a big, saturated boom with a sub-bass layer and a breech clank tail
	public static byte[] shootCannonHeavy() {
		int n = ms(260);
		double[] sub = envelope(tone(38, 20, n, SINE), i -> expDecay(i, n, 3.6));
		double[] thump = envelope(tone(70, 38, n, SINE), i -> expDecay(i, n, 4.8));
		double[] crack = envelope(filteredNoise(n, 0.32), i -> expDecay(i, n, 6));

		int clankLen = ms(50);
		double[] clank = envelope(tone(190, 130, clankLen, TRIANGLE), i -> expDecay(i, clankLen, 6));
		double[] clankPadded = padStart(shiftRight(clank, ms(95)), n);

		double[] mixed = mix(gain(sub, 0.8), thump, gain(crack, 0.85), gain(clankPadded, 0.35));
		return toPcm(saturate(mixed, 1.5), 0.68);
	}

	//gun turret's rapid-fire burst: three metallic clacks, not three cute beeps
	public static byte[] shootAutocannon() {
		int tapLen = ms(28), gap = ms(24);
		double[] tap = envelope(mix(gain(filteredNoise(tapLen, 0.6), 0.9), tone(240, 150, tapLen, SQUARE)),
				i -> expDecay(i, tapLen, 11));
		double[] tapSat = saturate(tap, 1.4);
		double[] silence = new double[gap];
		return toPcm(concat(tapSat, silence, tapSat, silence, tapSat), 0.42);
	}

	//missile turret's warhead: bigger and bassier than the tank rocket
	public static byte[] shootMissile() {
		int n = ms(320);
		double[] whoosh = envelope(sweepingNoise(n, 0.05, 0.3), i -> bell(i, n, 0.3));
		double[] sweep = envelope(tone(90, 320, n, SINE), i -> bell(i, n, 0.3));
		double[] thump = envelope(tone(60, 40, ms(120), SINE), i -> expDecay(i, ms(120), 5));
		return toPcm(saturate(mix(gain(whoosh, 0.85), gain(sweep, 0.3), gain(thump, 0.5)), 1.2), 0.62);
	}

	//stealth tank's railgun: a sci-fi energy zap, unlike anything mechanical
	public static byte[] shootRailgun() {
		int n = ms(160);
		double[] zap = envelope(tone(2400, 300, n, SAWTOOTH), i -> expDecay(i, n, 5));
		int ringLen = ms(60);
		double[] ring = new double[n];
		double[] ringTone = envelope(tone(900, 900, ringLen, SINE), i -> expDecay(i, ringLen, 6));
		System.arraycopy(ringTone, 0, ring, ms(20), Math.min(ringLen, n - ms(20)));
		return toPcm(mix(zap, gain(ring, 0.4)), 0.4);
	}

	//a building locking into place on the deployment grid: two heavy metal thunks with a bit of clang
	public static byte[] deployBuilding() {
		int thunkLen = ms(75), gap = ms(35);
		double[] thunk1 = envelope(mix(tone(130, 70, thunkLen, SINE), gain(filteredNoise(thunkLen, 0.3), 0.55)),
				i -> expDecay(i, thunkLen, 5.5));
		double[] thunk2 = envelope(mix(tone(100, 55, thunkLen, SINE), gain(filteredNoise(thunkLen, 0.3), 0.55)),
				i -> expDecay(i, thunkLen, 5.5));
		double[] clank = envelope(tone(1400, 900, ms(15), SINE), i -> expDecay(i, ms(15), 6));
		double[] silence = new double[gap];
		double[] mixed = mix(concat(thunk1, silence, thunk2), gain(shiftRight(clank, 2 * thunkLen + gap + ms(15)), 0.45));
		return toPcm(saturate(mixed, 1.3), 0.55);
	}

	//the MCV unfolding into a construction yard: unlock click, dull mechanical grind, panel clunks, final thuds
	public static byte[] deployVehicle() {
		int n = ms(650);

		//every tonal whine tried here (sawtooth, square, pulsed) has read as electronic/balloon-like -
		//drop the tone entirely and use only a dull, heavily-filtered noise swell for the extension
		//movement, which reads as mechanical grinding rather than a synth whistle
		int unlockLen = ms(25);
		double[] unlock = envelope(mix(filteredNoise(unlockLen, 0.8), gain(tone(600, 300, unlockLen, SINE), 0.5)),
				i -> expDecay(i, unlockLen, 10));

		int grindLen = ms(420);
		double[] grind = envelope(filteredNoise(grindLen, 0.18), i -> bell(i, grindLen, 0.5));

		//three heavy metallic clunks as panels unfold, spread across the sequence
		int clunkLen = ms(60);
		double[] clunk1 = envelope(mix(tone(150, 90, clunkLen, SINE), gain(filteredNoise(clunkLen, 0.35), 0.6)),
				i -> expDecay(i, clunkLen, 6));
		double[] clunk2 = envelope(mix(tone(140, 85, clunkLen, SINE), gain(filteredNoise(clunkLen, 0.35), 0.6)),
				i -> expDecay(i, clunkLen, 6));
		double[] clunk3 = envelope(mix(tone(130, 80, clunkLen, SINE), gain(filteredNoise(clunkLen, 0.35), 0.6)),
				i -> expDecay(i, clunkLen, 6));

		//final heavy double-thud as the whole assembly locks into place
		int thudLen = ms(90);
		double[] thud1 = envelope(mix(tone(65, 32, thudLen, SINE), gain(filteredNoise(thudLen, 0.3), 0.6)),
				i -> expDecay(i, thudLen, 5));
		double[] thud2 = envelope(mix(tone(58, 28, thudLen, SINE), gain(filteredNoise(thudLen, 0.3), 0.6)),
				i -> expDecay(i, thudLen, 5));

		double[] mixed = mix(
				padStart(unlock, n),
				gain(padStart(shiftRight(grind, ms(40)), n), 0.55),
				padStart(shiftRight(clunk1, ms(150)), n),
				padStart(shiftRight(clunk2, ms(320)), n),
				padStart(shiftRight(clunk3, ms(470)), n),
				padStart(shiftRight(thud1, n - thudLen * 2), n),
				padStart(shiftRight(thud2, n - thudLen), n));
		return toPcm(saturate(mixed, 1.15), 0.55);
	}

	private static double[] shiftRight(double[] buf, int offset) {
		double[] out = new double[offset + buf.length];
		System.arraycopy(buf, 0, out, offset, buf.length);
		return out;
	}

	public static byte[] explosionSmall() {
		int n = ms(320);
		int crackLen = ms(12);
		double[] crack = padStart(envelope(filteredNoise(crackLen, 0.85), i -> expDecay(i, crackLen, 7)), n);
		double[] boom = envelope(filteredNoise(n, 0.14), i -> expDecay(i, n, 3.6));
		double[] rumble = envelope(tone(60, 32, n, SINE), i -> expDecay(i, n, 4.2));
		double[] mixed = mix(gain(crack, 0.9), boom, gain(rumble, 0.65));
		return toPcm(saturate(mixed, 1.35), 0.65);
	}

	public static byte[] explosionLarge() {
		int n = ms(650);
		int crackLen = ms(20);
		double[] crack = padStart(envelope(filteredNoise(crackLen, 0.9), i -> expDecay(i, crackLen, 6)), n);
		double[] boom = envelope(filteredNoise(n, 0.075), i -> expDecay(i, n, 2.1));
		double[] rumble = envelope(tone(35, 18, n, SINE), i -> expDecay(i, n, 2.5));

		int crackleStart = ms(110);
		int crackleLen = n - crackleStart;
		double[] crackle = new double[n];
		double[] crackleNoise = envelope(filteredNoise(crackleLen, 0.35), i -> expDecay(i, crackleLen, 5));
		System.arraycopy(crackleNoise, 0, crackle, crackleStart, crackleLen);

		double[] mixed = mix(gain(crack, 0.8), boom, gain(rumble, 0.75), gain(crackle, 0.45));
		return toPcm(saturate(mixed, 1.4), 0.85);
	}

	//a distorted sawtooth "horn" note, for an industrial fanfare instead of a chiptune melody
	private static double[] hornNote(double freq, int n, double rate) {
		double[] toneBuf = envelope(tone(freq, freq, n, SAWTOOTH), i -> expDecay(i, n, rate));
		double[] grit = envelope(filteredNoise(n, 0.4), i -> expDecay(i, n, rate + 2));
		return saturate(mix(toneBuf, gain(grit, 0.2)), 1.8);
	}

	public static byte[] victory() {
		int noteLen = ms(150);
		double[] c4 = hornNote(262, noteLen, 2.5);
		double[] e4 = hornNote(330, noteLen, 2.5);
		double[] g4 = hornNote(392, noteLen, 2.5);
		double[] c5 = hornNote(523, noteLen * 2, 1.8);
		return toPcm(concat(c4, e4, g4, c5), 0.42);
	}

	public static byte[] defeat() {
		int noteLen = ms(190);
		double[] a3 = hornNote(220, noteLen, 2.5);
		double[] f3 = hornNote(175, noteLen, 2.5);
		double[] d3 = hornNote(147, noteLen, 2.5);
		double[] a2 = hornNote(110, noteLen * 2, 1.8);
		return toPcm(concat(a3, f3, d3, a2), 0.42);
	}
}
