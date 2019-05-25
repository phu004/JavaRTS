package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import javax.imageio.ImageIO;
import core.*;

public class gameMenu {
	
	public int gameSuspendCount;
	
	public static int menuStatus = 0;
	public static final int mainMenu = 0;
	public static final int difficulitySelectionMenu = 1;
	public static final int helpMenu = 2;
	public static final int endGameMenu = 3;
	public static final int optionMenu = 4;
	public static final int highscoreMenu = 5;
	public int[] screen; 
	public int[] screenBlurBuffer;
	
	public String imageFolder = "";
	
	public int[] titleImage, lightTankImage, rocketTankImage, stealthTankImage, heavyTankImage;
	
	public button newGame, unpauseGame, showHelp, showOptions, showHighscores, quitGame, abortGame, easyGame, normalGame, hardGame, quitDifficulty, quitHelpMenu, quitOptionMenu, quitHighscoreMenu, nextPage, previousPage,
	              enableMouseCapture, disableMouseCapture, enableFogOfWar, disableFogOfWar, confirmErrorLoadingHighscore, normalToHardButton, normalToEasyButton, hardToNormalButton, easyToNormalButton,
	              backToMapDefeat, leaveGameDefeat, backToMapVictory, leaveGameVictory, uploadScore;
	
	public char[] easyDescription, normalDescription, hardDescription, helpPage1, helpPage2, helpPage3, helpPage4, mouseMode;
	
	public int currentHelpPage;
	public int highscoreLevel;
	
	public ArrayList<button> buttons = new ArrayList<button>();
	
	public highscoreManager theHighscoreManager;
	
	public char[] name;
	public String nameString;
	public static boolean uploadingScore, scoreUploaded;
	
	
	public void init() {
		if(titleImage == null) {
			titleImage = new int[288*46];
			lightTankImage = new int[44*44];
			rocketTankImage = new int[44*44];
			stealthTankImage = new int[44*44];
			heavyTankImage = new int[44*44];
		}
		if(screenBlurBuffer == null)
			screenBlurBuffer = new int[512 * 768];
	
		theHighscoreManager = new highscoreManager();
		Thread   t   =   new   Thread(theHighscoreManager);
		t.start();
		
		highscoreLevel = 1;
		name = new char[32]; 
		for(int i = 0; i< 32; i++)
			name[i] = 255;
		
		String folder = "../images/";
		loadTexture(folder + "title.png", titleImage, 216, 35);
		loadTexture(folder + "58.jpg", lightTankImage, 44, 44);
		loadTexture(folder + "59.jpg", rocketTankImage, 44, 44);
		loadTexture(folder + "72.jpg", stealthTankImage, 44, 44);
		loadTexture(folder + "83.jpg", heavyTankImage, 44, 44);
		
		
		newGame = new button("newGame", "New Game", 324, 110, 120, 28);
		buttons.add(newGame);
		
		unpauseGame = new button("unpauseGame", "Resume Game",  324, 110, 120, 28);
		buttons.add(unpauseGame);
		
		showHelp = new button("showHelp", "Help", 324, 160, 120, 28);
		buttons.add(showHelp);
		
		showOptions = new button("showOptions", "Options", 324, 210, 120, 28);
		buttons.add(showOptions);
		
		showHighscores = new button("showHighscores", "Highscores", 324, 260, 120, 28);
		buttons.add(showHighscores);
		
		quitGame = new button("quitGame", "Quit Game", 324, 345, 120, 28);
		buttons.add(quitGame);
		
		abortGame = new button("abortGame", "Abort Game", 324, 345, 120, 28);
		buttons.add(abortGame);
		
		easyGame = new button("easyGame", "Easy", 190, 120, 85, 28);
		buttons.add(easyGame);
		
		normalGame = new button("normalGame", "Normal", 190, 200, 85, 28);
		buttons.add(normalGame);
		
		hardGame = new button("hardGame", "Hard", 190, 280, 85, 28);
		buttons.add(hardGame);
		
		quitDifficulty = new button("quitDifficulty", "x", 570, 80, 18,16);
		buttons.add(quitDifficulty);
		
		easyDescription = "AI will attack blindly at player's base \nwithout thinking too much. ".toCharArray();
		normalDescription = "AI will launch timed attacks, it will also \nchange its army composition based on \nthe scouted information.".toCharArray();
		hardDescription = "AI will micro each of its units, expand \nmore aggressively and carry out high\nlevel maneuver such as harassing during \npeaceful peirod.".toCharArray();
		
		helpPage1 = ("                                               Controls             \n\n"
				   + "\"Esc\" -- Pause/Unpause the game.\n\n"
				   + "\"Left Click\" -- Select a unit. Left click + mouse drag can be used to select up to \n100 units at a time. Double left click on a unit will automatically select surrounding \nunits of the same type.\n\n"
				   + "\"Right Click\" -- Issue a move or attack command to the selected unit(s). You can \nalso use right click to set rally point or cancel build progress.\n\n"
				   + "\"a\" -- Force attack a unit. If no unit is under the cursor, then the selected units will \nbe set to attack move to the cursor location.\n\n"
				   + "\"s\" -- stop current action for the selected unit(s).\n\n"
				   + "\"Ctrl + number\" -- Create a control group and assigned the number to the group.\n\n"
				   + "\"Ctrl + Left Click\" -- Add/Remove a unit to/from the selected units.\n\n"
				   + "\"Ctrl + Mouse Drag\" -- Add units in the dragging box to the selected units.\n\n\n"
				   + "                                                  1/4                  ").toCharArray();
		
		helpPage2 = ("                                          Controls (Cont.)             \n\n"
					+ "\"Left and Right arrow keys\" -- Change camera view angle.\n\n"
					+ "\"c\" -- Toggle between different construction yards under your control.\n\n"
					+ "\"f\" -- Toggle between different factories under your control.\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
					+ "                                                  2/4                  ").toCharArray();
		
		helpPage3 = ("                                                  Units        \n\n"
				   + "There are 4 type of military units, each has its own strength and weakness.\n\n"
				   + "         Light Tank -- Cheap but lightly armored. Has moderate movement speed \n         and firepower. It can be considered as the jack of all trades. It can be \n         upgraded to have increased range.\n\n"
				   + "         Rocket Tank -- A slow moving and lightly armored unit. It has long reload \n         time but can out range static defenses. It does extra damage to buildings \n         and can be upgraded to deal even more damage to buildings.\n\n"
				   + "         Stealth Tank -- Fast but lightly armoured. It has a passive cloak ablility that \n         turns the tank invisible when not attacking. It does more damage to light \n         armoured unit but significantly less damage to heavy armoured unit. It can \n         be upgraded to damage multiple units with one shot.\n\n"
				   + "         Heavy Tank -- The Slowest and most expensive tank in the game. Equiped \n         with twin cannons, it is a moving fortress. It can be upgraded with self \n         repair capability so it can last even longer in battle field.\n\n\n\n"
				   + "                                                  3/4                  ").toCharArray(); 
		
		helpPage4 = ("                                              About Me             \n\n"
				   + "Hi everyone, my name is Pan Hu, I have a great interest in making video games. \n"
				   + "It has been a dream job for me since a very young age. Unfortunately I ended \n"
				   + "up make a living doing the \"boring\" job like most other folks. But it will not stop\n"
				   + "me from doing what I enjoy in my spare time!\n\n" 
				   + "In this project I am trying to create a small RTS game with somewhat challenging \n"
				   + "AI using pure Java. However the AI will not cheat by any means, i.e. Its vision is\n"
				   + "limited by fog of war, it doesn't have any advantage in resource gathering. Well,\n"
				   + "the only advantage is porbably the inhuman action per second the AI carries out \n"
				   + "in higher difficulty.\n\n"
				   + "This game is completely open source. You can find the source code at my github \n"
				   + "page: https://github.com/phu004/JavaRTS. If you are intersted in other projects of\n"
				   + "mine, feel free to check out my YouTube channel, user name is \"Pan Hu\".\n\n"
				   + "Have a nice Day!\n\n\n\n\n\n"
				   + "                                                  4/4").toCharArray();
		
		mouseMode = ("                                    Options \n\n\nMouse capture. When enabled the game will prevent \nthe mouse cursor from leaving the current window.\n\n\n"
				   + "Fog of war. When enabled, enemy units that are not \nin vision will be hidden. Note that your score will NOT \nbe saved when this option is disabled.").toCharArray();
		
		quitHelpMenu = new button("quitHelpMenu", "x", 670, 80, 18,16);
		buttons.add(quitHelpMenu);
		
		quitOptionMenu = new button("quitOptionMenu", "x", 620, 80, 18,16);
		buttons.add(quitOptionMenu);
		
		quitHighscoreMenu = new button("quitHighscoreMenu", "x", 570, 80, 18,16);
		buttons.add(quitHighscoreMenu);
		
		nextPage = new button("nextPage", "Next Page", 550, 450, 120, 28);
		buttons.add(nextPage);
		
		previousPage = new button("previousPage", "Previous Page", 98, 450, 120, 28);
		buttons.add(previousPage);
		
		enableMouseCapture = new button("enableMouseCapture", "Disabled", 545, 145, 80, 25);
		buttons.add(enableMouseCapture);
		
		disableMouseCapture = new button("disableMouseCapture", "Enabled", 545, 145, 80, 25);
		buttons.add(disableMouseCapture);
		
		enableFogOfWar = new button("enableFogOfWar", "Disabled", 545, 215, 80, 25);
		buttons.add(enableFogOfWar);
		
		disableFogOfWar = new button("disableFogOfWar", "Enabled", 545, 215, 80, 25);
		buttons.add(disableFogOfWar);
		
		confirmErrorLoadingHighscore = new button("confirmErrorLoadingHighscore", "Ok", 350, 280, 80, 25);
		buttons.add(confirmErrorLoadingHighscore);
		
		normalToHardButton = new button("normalToHardButton", ">", 543, 430, 40, 25);
		buttons.add(normalToHardButton);
		
		normalToEasyButton = new button("normalToEasyButton", "<", 185, 430, 40, 25);
		buttons.add(normalToEasyButton);
		
		hardToNormalButton = new button("hardToNormalButton", "<", 185, 430, 40, 25);
		buttons.add(hardToNormalButton);
		
		easyToNormalButton = new button("easyToNormalButton", ">", 543, 430, 40, 25);
		buttons.add(easyToNormalButton);
		
		backToMapDefeat =  new button("backToMap", "Back to Map", 210, 235, 120, 25);
		buttons.add(backToMapDefeat);
		
		leaveGameDefeat =  new button("abortGame", "Leave game", 440, 235, 120, 25);
		buttons.add(leaveGameDefeat);
		
		backToMapVictory =  new button("backToMap", "Back to Map", 135, 315, 120, 25);
		buttons.add(backToMapVictory);
		
		leaveGameVictory =  new button("abortGame", "Leave game", 515, 315, 120, 25);
		buttons.add(leaveGameVictory);
		
		uploadScore = new button("uploadScore", "Upload", 530, 250, 90, 25);
		buttons.add(uploadScore);
	}
	
	
	public void updateAndDraw(int[] screen, boolean gameStarted, boolean gamePaused, boolean playerVictory, boolean AIVictory) {
		this.screen = screen;
		textRenderer tRenderer = postProcessingThread.theTextRenderer;
		
		if(gamePaused){
			gameSuspendCount++;
		}else {
			gameSuspendCount = 0;
		}
		
		if(gameSuspendCount == 1) {
			for(int i = 0; i < 512*768; i++)
				screenBlurBuffer[i] = screen[i];
			
		}
		
		//make all buttons off screen and reduce their action cooldown
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).display = false;
			if(buttons.get(i).actionCooldown > 0)
				buttons.get(i).actionCooldown--;
		}
		
		
		if(playerVictory || AIVictory) {
			if(gameSuspendCount > 0) {
				drawBluredBackground();
			}
			
			if(AIVictory) {
				
				drawMenuFrame(400, 100, 70);	
				tRenderer.drawMenuText(320,178,"You Are Defeated!".toCharArray(), screen, 255,255,255, 0);
				
				backToMapDefeat.display = true;
				leaveGameDefeat.display = true;
			}else if(playerVictory) {
				drawMenuFrame(550, 210, 40);	
				tRenderer.drawMenuText(320,138,"You are Victorious!".toCharArray(), screen, 255,255,255, 0);
				
				String difficulty = "Normal";
				if(mainThread.ec.difficulty == 0)
					difficulty = "Easy";
				if(mainThread.ec.difficulty == 2)
					difficulty = "Hard";
				tRenderer.drawMenuText(205,198,("Difficulty:  "+ difficulty).toCharArray(), screen, 255,255,255, 0);
				
				String time = mainThread.timeString;
				
				tRenderer.drawMenuText(232,228,("Time:  "+ time).toCharArray(), screen, 255,255,255, 0);
				
				
				if(!postProcessingThread.fogOfWarDisabled) {
					tRenderer.drawMenuText(185,258,("Your Name:").toCharArray(), screen, 255,255,255, 0);
					uploadScore.display = true;
					uploadScore.disabled = true;
					
					//only accept 0-9, A-Z, a-Z, space and backspace characters
					char c = postProcessingThread.currentInputChar;
					
					if(!uploadingScore && !scoreUploaded) {
						if((c >= 48 && c < 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == 8 || c == 32) {
							if(c == 8) {
								for(int i = 31; i >= 0; i--) {
									if(name[i] != 255) {
										name[i] = 255;
										break;
									}
								}
							}else {
								for(int i = 0; i < 32; i++) {
									if(name[i] == 255) {
										name[i] = c;
										break;
									}
								}
							}
						}
					}
					
					//check if upload condition is met
					for(int i = 0; i < 32; i++) {
						if((name[i] >= 48 && name[i] < 57) || (name[i] >= 65 && name[i] <= 90) || (name[i] >= 97 && name[i] <= 122)) {
							uploadScore.disabled = false;
						}
					}
					
					//draw name string
					nameString = "";
					for(int i = 0; i < 32; i++) {
						if(name[i] != 255) {
							nameString+=name[i];
						}else {
							break;
						}
					}
					tRenderer.drawText_outline(282, 258, nameString, screen, 0xdddddd, 0);
					
					//draw place marker
					if(postProcessingThread.frameIndex%30 > 15 && !uploadingScore && !scoreUploaded)
						tRenderer.drawText_outline(282+nameString.length()*7, 258, "_", screen, 0xdddddd, 0);
					
					if(uploadingScore || scoreUploaded) {
						uploadScore.disabled = true;
					}
					
					if(uploadingScore && !scoreUploaded) {
						if(theHighscoreManager.status == theHighscoreManager.idle && theHighscoreManager.playerName.equals("")) {
							theHighscoreManager.playerName = nameString;
							theHighscoreManager.task = theHighscoreManager.uploadScore;
							
						}else if(theHighscoreManager.status == theHighscoreManager.error) {
							
						}else if(theHighscoreManager.status == theHighscoreManager.idle && theHighscoreManager.task== theHighscoreManager.none && !theHighscoreManager.playerName.equals("")) {
							scoreUploaded = true;
							theHighscoreManager.playerName = "";
						}
					}
					
					if(scoreUploaded) {
						uploadScore.text = "Uploaded!";
						uploadScore.theText = uploadScore.text.toCharArray();
						uploadScore.messageMode = true;
					}
					
				}
				
				backToMapVictory.display = true;
				leaveGameVictory.display = true;
			}
			
			updateButtons();
			drawButtons();
			
			return;
		}
		
		//only show game menu when the game is not started or game is paused
		if(!(!gameStarted || gamePaused))
			return;
		
		
		
		if(menuStatus == mainMenu) {
			currentHelpPage = 0;
			
			
			if(gameSuspendCount > 0) {
				drawBluredBackground();
			}
			
			drawTitle();
			
			drawMenuFrame(220, 300);	
			
			if(!gameStarted) {
				newGame.display = true;
				quitGame.display = true;
			}else {
				unpauseGame.display = true;
				abortGame.display = true;
			}
			
			showHelp.display = true;
			showOptions.display = true;
			showHighscores.display = true;
			
			
		}else if(menuStatus == difficulitySelectionMenu) {
			if(postProcessingThread.escapeKeyPressed) {
				menuStatus = mainMenu;
				
			}else {
				drawTitle();
				drawMenuFrame(420, 260);
			
				easyGame.display = true;
				tRenderer.drawMenuText(285,118,easyDescription, screen, 255,255,255, 0);
				
				normalGame.display = true;
				tRenderer.drawMenuText(285,188,normalDescription, screen, 255,255,255,0);
				
				hardGame.display = true;
				tRenderer.drawMenuText(285,265,hardDescription, screen, 255,255,255,0);
				
				quitDifficulty.display = true;
			}
			
			
		}else if(menuStatus == helpMenu) {
			if(postProcessingThread.escapeKeyPressed) {
				menuStatus = mainMenu;
			
			}else{
				if(gameSuspendCount > 0) {
					drawBluredBackground();
				}
				
				drawTitle();
				drawMenuFrame(620, 380);
				
				if(currentHelpPage == 0) {
					tRenderer.drawMenuText(82,90,helpPage1, screen, 255,255,255,0);
					nextPage.display = true;
				}else if(currentHelpPage == 1) {
					tRenderer.drawMenuText(82,90,helpPage2, screen, 255,255,255,0);
					nextPage.display = true;
					previousPage.display = true;
				}else if(currentHelpPage == 2) {
					tRenderer.drawMenuText(82,90,helpPage3, screen, 255,255,255,0);
					nextPage.display = true;
					previousPage.display = true;
					drawImage(83,157, 44, 44,lightTankImage);
					drawImage(83,220, 44, 44,rocketTankImage);
					drawImage(83,290, 44, 44,stealthTankImage);
					drawImage(83,364, 44, 44,heavyTankImage);
				}else if(currentHelpPage == 3) {
					tRenderer.drawMenuText(82,90,helpPage4, screen, 255,255,255,0);
					previousPage.display = true;
				}
				
				quitHelpMenu.display = true;
			}
			
			
		}else if(menuStatus == optionMenu) {
			if(postProcessingThread.escapeKeyPressed) {
				menuStatus = mainMenu;
			
			}else{
				if(gameSuspendCount > 0) {
					drawBluredBackground();
				}
			
				drawTitle();
				drawMenuFrame(520, 380);
				
				tRenderer.drawMenuText(135,95,mouseMode, screen, 255,255,255,0);
				
				if(postProcessingThread.capturedMouse == true) {
					disableMouseCapture.display = true;
					enableMouseCapture.display = false;
				}else {
					disableMouseCapture.display = false;
					enableMouseCapture.display = true;
				}
				
				if(postProcessingThread.fogOfWarDisabled) {
					disableFogOfWar.display = false;
					enableFogOfWar.display = true;
				}else {
					disableFogOfWar.display = true;
					enableFogOfWar.display = false;
				}
				
				quitOptionMenu.display = true;
				
				if(postProcessingThread.gameStarted) {
					disableFogOfWar.disabled  = true;
					enableFogOfWar.disabled = true;
				}else {
					disableFogOfWar.disabled  = false;
					enableFogOfWar.disabled = false;
				}
			}
			
		}else if(menuStatus == highscoreMenu) {
			if(postProcessingThread.escapeKeyPressed) {
				quitHighscoreMenu();
			
			}else {
				if(gameSuspendCount > 0) {
					drawBluredBackground();
				}
				
				drawTitle();
				drawMenuFrame(420, 360);
				
				if(theHighscoreManager.status == theHighscoreManager.processing) {
					drawLoadingScreen(screen);
				}else if(theHighscoreManager.status == theHighscoreManager.idle) {
					if(theHighscoreManager.task == theHighscoreManager.none && theHighscoreManager.result == null) {
						theHighscoreManager.task = theHighscoreManager.loadHighscores;
						drawLoadingScreen(screen);
					}else if(theHighscoreManager.task == theHighscoreManager.none && theHighscoreManager.result != null) {
						drawHighscore();
						
					}else if(theHighscoreManager.isSleeping && theHighscoreManager.result == null) {
						drawLoadingScreen(screen);
					}
				}else if(theHighscoreManager.status == theHighscoreManager.error) {
					tRenderer.drawMenuText(240,250,"Cannot load high scores, try again later.".toCharArray(), screen, 255,255,255,0);
					confirmErrorLoadingHighscore.display=true;
				}
				
				quitHighscoreMenu.display = true;
			}
		}
		
		
		updateButtons();
		drawButtons();
		
	}
	
	public void drawHighscore() {
		
		textRenderer tRenderer = postProcessingThread.theTextRenderer;
		String[][] result =  theHighscoreManager.result;
		int startRow = 0;
		//draw high scores
		if(highscoreLevel == 1) {
			tRenderer.drawText_outline(270,100,"Highscores For Normal Difficulty", screen, 0xffffff,0);	
			startRow = 10;
			normalToHardButton.display = true;
			normalToEasyButton.display = true;
		}else if(highscoreLevel == 0) {
			tRenderer.drawText_outline(270,100,"Highscores For Easy Difficulty", screen, 0xffffff,0);	
			startRow = 0;
			easyToNormalButton.display = true;
		}else if(highscoreLevel == 2) {
			tRenderer.drawText_outline(270,100,"Highscores For Hard Difficulty", screen, 0xffffff,0);	
			startRow = 20;
			hardToNormalButton.display = true;
		}
		
		tRenderer.drawText_outline(220,130," Rank           Player Name            Time", screen, 0xf2989d,0);
		tRenderer.drawText_outline(220,135,"_____________________________________________", screen, 0xaaaaaa,0);
		
		for(int i = startRow; i < startRow + 10; i++) {
			int color = 0xbbbbbb;
			if(i -startRow == 0)
				color = 0xffe559;
			if(i -startRow == 1)
				color = 0xe8e9ea;
			if(i -startRow == 2)
				color = 0xc99684;
			if(i -startRow == 9)
				tRenderer.drawText_outline(210,160 + (i -startRow)*25, "    " + (i -startRow + 1), screen, color,0);
			else
				tRenderer.drawText_outline(213,160 + (i -startRow)*25, "    " + (i -startRow + 1), screen, color,0);
			
			if(result[i][0] != null) {
				int l = (30 - result[i][0].length())/2;
				
				tRenderer.drawText_outline(220,160 + (i -startRow)*25, "                                       " + result[i][1], screen, color,0);
				tRenderer.drawText_outline(265 + l*7,160 + (i -startRow)*25, result[i][0], screen, color,0);
			}
		}
		
	}
	
	public void drawLoadingScreen(int[] screen) {
		textRenderer tRenderer = postProcessingThread.theTextRenderer;
		
		if(postProcessingThread.frameIndex%50 < 10) {
			tRenderer.drawMenuText(360,250,"Loading....".toCharArray(), screen, 255,255,255,0);
		}else if(postProcessingThread.frameIndex%50 >= 10 && postProcessingThread.frameIndex%50 < 20) {
			tRenderer.drawMenuText(360,250,"Loading".toCharArray(), screen, 255,255,255,0);
		}else if(postProcessingThread.frameIndex%50 >= 20 && postProcessingThread.frameIndex%50 < 30) {
			tRenderer.drawMenuText(360,250,"Loading.".toCharArray(), screen, 255,255,255,0);
		}else if(postProcessingThread.frameIndex%50 >= 30 && postProcessingThread.frameIndex%50 < 40) {
			tRenderer.drawMenuText(360,250,"Loading..".toCharArray(), screen, 255,255,255,0);
		}else {
			tRenderer.drawMenuText(360,250,"Loading...".toCharArray(), screen, 255,255,255,0);
		}
	}
	
	public void quitHighscoreMenu() {
		menuStatus = mainMenu;
		theHighscoreManager.task = theHighscoreManager.none;
		if(theHighscoreManager.status == theHighscoreManager.idle) {
			theHighscoreManager.result =  null;
		}
		highscoreLevel = 1;
	}
	
	public void updateButtons() {
		for(int i = 0; i < buttons.size(); i++) {
			if(buttons.get(i).checkIfCursorIsOnTop(postProcessingThread.mouse_x, postProcessingThread.mouse_y)) {
				if(postProcessingThread.leftMouseButtonReleased) {
					if(buttons.get(i).actionCooldown == 0 && buttons.get(i).display == true) {
						buttons.get(i).actionCooldown = 5;
						
						if(buttons.get(i).name == "newGame") {
							menuStatus = difficulitySelectionMenu;
						}else if(buttons.get(i).name == "showHelp") {
							menuStatus = helpMenu;
						}else if(buttons.get(i).name == "showOptions") {
							menuStatus = optionMenu;
						}else if(buttons.get(i).name == "quitDifficulty" || buttons.get(i).name == "quitHelpMenu" || buttons.get(i).name == "quitOptionMenu") {
							menuStatus = mainMenu;
						}else if(buttons.get(i).name == "nextPage") {
							currentHelpPage++;
						}else if(buttons.get(i).name == "previousPage") {
							currentHelpPage--;
						}else if(buttons.get(i).name == "showHighscores") {
							menuStatus = highscoreMenu;
							if(theHighscoreManager.status == theHighscoreManager.error) {
								theHighscoreManager.counter = 0;
								theHighscoreManager.status = theHighscoreManager.idle;
							}
						}else if(buttons.get(i).name == "quitHighscoreMenu" || buttons.get(i).name == "confirmErrorLoadingHighscore") {
							quitHighscoreMenu();
						}else if(buttons.get(i).name == "normalToHardButton") {
							highscoreLevel = 2;
						}else if(buttons.get(i).name == "hardToNormalButton") {
							highscoreLevel = 1;
						}else if(buttons.get(i).name == "easyToNormalButton") {
							highscoreLevel = 1;
						}else if(buttons.get(i).name == "normalToEasyButton") {
							highscoreLevel = 0;
						}else if(buttons.get(i).name == "abortGame") {
							menuStatus = mainMenu;
							for(int j = 0; j< 32; j++)
								name[j] = 255;
							scoreUploaded = false;
							uploadingScore = false;
							uploadScore.text = "Upload";
							uploadScore.theText = uploadScore.text.toCharArray();
							uploadScore.messageMode = false;
						}else if(buttons.get(i).name == "uploadScore") {
							uploadingScore = true;
						}
						
						postProcessingThread.buttonAction = buttons.get(i).name;
					}
				}
			}
		}
	}
	
	public void drawButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).draw(screen);
		}
	}
	
	public void drawBluredBackground() {
		if(gameSuspendCount < 4) {
			
			for(int k = 0; k < 3; k++)
			for(int i = 1; i < 511; i++ ) {
				for(int j = 1; j < 767; j++) {
					int index = j + i*768;
					
				
					
					int r = ((screenBlurBuffer[index]&0xff0000) >> 16) + ((screenBlurBuffer[index + 1]&0xff0000) >> 16) + ((screenBlurBuffer[index - 1]&0xff0000) >> 16) + ((screenBlurBuffer[index - 768]&0xff0000) >> 16) + ((screenBlurBuffer[index + 768]&0xff0000) >> 16);
					int g = ((screenBlurBuffer[index]&0xff00) >> 8) + ((screenBlurBuffer[index + 1]&0xff00) >> 8) + ((screenBlurBuffer[index - 1]&0xff00) >> 8) + ((screenBlurBuffer[index - 768]&0xff00) >> 8) + ((screenBlurBuffer[index + 768]&0xff00) >> 8);
					int b = (screenBlurBuffer[index]&0xff) + (screenBlurBuffer[index + 1]&0xff) + (screenBlurBuffer[index - 1]&0xff) + (screenBlurBuffer[index - 768]&0xff) + (screenBlurBuffer[index + 768]&0xff);
					
				
					
					screenBlurBuffer[index] = (r/5) << 16 | (g/5) << 8 | (b/5);
				}
			}
			
			
		}
		
		for(int i = 0; i < 512*768; i++)
			screen[i] = screenBlurBuffer[i];
	}
	
	public void drawMenuFrame(int width, int height){
		drawFrame(width, height, 0);
	}
	
	public void drawMenuFrame(int width, int height, int topDistance){
		drawFrame(width, height, topDistance);
	}
	
	public void drawFrame(int width, int height, int topDistance) {
	
		
		int R = 4;
		int G = 94;
		int B = 132;
		
		int R1 = 8;
		int G1 = 188;
		int B1 = 255;
		
		int R2 = 3;
		int G2 = 70;
		int B2 = 99;
		
		int pos = (768 - width)/2 + (90+topDistance) * 768;
		
		
		//background
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int pixel = screen[pos + j + i* 768];
				screen[pos + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R2/2) << 16 | (G2/2) << 8 | (B2/2));
			}
		}
		float d = 20f;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < width; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R2/2) << 16 | (G2/2) << 8 | (B2/2));
			}
		}
		
		pos+=(height+17)*768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j =0; j < width - delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R2/2) << 16 | (G2/2) << 8 | (B2/2));
			}
		}
		
		//left 
		pos = (768 - width)/2 + (90+topDistance) * 768;
		for(int i = 0; i < height + 17; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + (90+topDistance) * 768+1;
		for(int i = 0; i < height + 16; i++) {
			screen[pos + i*768] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + (90+topDistance) * 768 + 2;
		for(int i = 0; i < height + 15; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		//bottom
		pos = (768 - width)/2 + ((90+topDistance)+height+14) * 768;
		for(int i = 3; i < width - 18; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		pos = (768 - width)/2 + ((90+topDistance)+height+15) * 768;
		for(int i = 2; i < width - 18; i++) {
			screen[pos + i] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + ((90+topDistance)+height+16) * 768;
		for(int i = 1; i < width - 18; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		//bottom right
		pos = (768 - width)/2 + width - 18 + ((90+topDistance)+height+16) * 768;
		for(int i = 2; i < 20; i++) {
			int delta = (int)((17f/d)*i);
			int pixel = screen[pos + i -2 + (-delta)*768];
			screen[pos + i - 2 + (-delta)*768] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		for(int i = 2; i < 18; i++) {
			int delta = (int)((17f/d)*i);
			int pixel = screen[pos + i -2 + (-delta - 2)*768];
			screen[pos + i - 2 + (-delta - 2)*768] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		for(int i = 2; i < 19; i++) {
			int delta = (int)((17f/d)*i);
			screen[pos + i - 2 + (-delta-1)*768] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		screen[pos - 5*768 + 4] = ((R1) << 16 | (G1) << 8 | (B1));
		screen[pos - 11*768 + 11] = ((R1) << 16 | (G1) << 8 | (B1));
		
		
		
		//right
		pos = (768 - width)/2 + width -3 +  (75+topDistance) * 768;
		for(int i = 0; i < height + 15; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + width -2+ (74+topDistance) * 768;
		for(int i = 0; i < height + 16; i++) {
			screen[pos + i*768] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 +  width - 1 + (73+topDistance) * 768;
		for(int i = 0; i < height + 17; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		//top
		pos = (768 - width)/2 + ((90+topDistance)-17) * 768;
		for(int i = 20; i < width -1; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + ((90+topDistance)-16) * 768;
		for(int i = 20; i < width - 2; i++) {
			screen[pos + i] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + ((90+topDistance)-15) * 768;
		for(int i = 20; i < width - 3; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		
		//top left
		pos = (768 - width)/2 + (90+topDistance) * 768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = pixel + ((R/2) << 16 | (G/2) << 8 | (B/2));
			}
		}
		
		pos = (768 - width)/2 + 2 + (90+topDistance) * 768;
		for(int i = 2; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = pixel + ((R/3) << 16 | (G/3) << 8 | (B/3));
			}
		}
		
		pos = (768 - width)/2 + 1 + (90+topDistance) * 768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				screen[pos - 17*768 + j + i* 768] = ((R1) << 16 | (G1) << 8 | (B1));
			}
		}
		
	}
	
	public void drawTitle() {
		int pos = 276 + 35*768;
		
		for(int i = 0; i < 35; i++) {
			for(int j = 0; j < 216; j++) {
				int c = titleImage[j + i*216];
					if(!((c&0xff0000 >> 16) > 254 && (c&0x00ff00 >> 8) > 254  && ((c&0xff) > 254)))
						screen[pos+ 768*i + j] = c;
			}
		}
	}
	
	public void drawImage(int xPos, int yPos, int w, int h, int[] myImage) {
		int pos = xPos + yPos*768;
		for(int i = 0; i < h; i++) {
			for(int j = 0; j < w; j++) {
				screen[pos + j + i*768] = myImage[j+ i*h];
			}
		}
	}
	
	public void loadTexture(String imgName, int[] buffer, int width, int height){
		Image img = null;
		try{
			img = ImageIO.read(getClass().getResource(imageFolder + imgName));
		}catch(Exception e){
			e.printStackTrace();
		}
		

		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, buffer, 0, width);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}

}
