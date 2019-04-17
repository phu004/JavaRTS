package gui;

import java.util.ArrayList;
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
	public int[] screen; 
	public int[] screenBlurBuffer;
	public boolean gameStarted, gamePaused, gameEnded;
	
	public String imageFolder = "";
	
	public int[] titleImage, lightTankImage, rocketTankImage, stealthTankImage, heavyTankImage;
	
	public button newGame, unpauseGame, showHelp, quitGame, abortGame, easyGame, normalGame, hardGame, quitDifficulty, quitHelpMenu, nextPage, previousPage;
	
	public char[] easyDescription, normalDescription, hardDescription, helpPage1, helpPage2, helpPage3, helpPage4;
	
	public int currentHelpPage;
	
	public ArrayList<button> buttons = new ArrayList<button>();
	
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
				   + "\"h\" -- stop current action for the selected unit(s).\n\n"
				   + "\"Ctrl + number\" -- Create a control group and assigned the number to the group.\n\n"
				   + "\"Ctrl + Left Click\" -- Add/Remove the a unit to/from the selected units.\n\n"
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
				   + "                                                  4/4"
				    ).toCharArray();
		
		quitHelpMenu = new button("quitHelpMenu", "x", 670, 80, 18,16);
		buttons.add(quitHelpMenu);
		
		nextPage = new button("nextPage", "Next Page", 550, 450, 120, 28);
		buttons.add(nextPage);
		
		previousPage = new button("previousPage", "Previous Page", 98, 450, 120, 28);
		buttons.add(previousPage);
	}
	
	
	public void updateAndDraw(int[] screen, boolean gameStarted, boolean gamePaused, boolean gameEnded) {
		this.screen = screen;
		this.gameStarted = gameStarted;
		this.gamePaused = gamePaused;
		this.gameEnded = gameEnded;
		
		if(gamePaused){
			gameSuspendCount++;
		}else {
			gameSuspendCount = 0;
		}
		
		if(gameSuspendCount == 1) {
			for(int i = 0; i < 512*768; i++)
				screenBlurBuffer[i] = screen[i];
		}
		
		
		//only show game menu when the game is not started or game is paused or game has ended
		if(!(!gameStarted || gamePaused || gameEnded))
			return;
		
		//make all buttons off screen and reduce their action cooldown
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).display = false;
			if(buttons.get(i).actionCooldown > 0)
				buttons.get(i).actionCooldown--;
		}
		
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
			
		}else if(menuStatus == difficulitySelectionMenu) {
			if(postProcessingThread.escapeKeyPressed) {
				menuStatus = mainMenu;
				
			}else {
				drawTitle();
				drawMenuFrame(420, 260);
				
				textRenderer tRenderer = postProcessingThread.theTextRenderer;
				easyGame.display = true;
				tRenderer.drawMenuText(285,118,easyDescription, screen, 255,255,50, 0);
				
				normalGame.display = true;
				tRenderer.drawMenuText(285,188,normalDescription, screen, 255,255,50,0);
				
				hardGame.display = true;
				tRenderer.drawMenuText(285,265,hardDescription, screen, 255,255,50,0);
				
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
				
				textRenderer tRenderer = postProcessingThread.theTextRenderer;
				
				if(currentHelpPage == 0) {
					tRenderer.drawMenuText(82,90,helpPage1, screen, 255,255,255,11);
					nextPage.display = true;
				}else if(currentHelpPage == 1) {
					tRenderer.drawMenuText(82,90,helpPage2, screen, 255,255,255,11);
					nextPage.display = true;
					previousPage.display = true;
				}else if(currentHelpPage == 2) {
					tRenderer.drawMenuText(82,90,helpPage3, screen, 255,255,255,11);
					nextPage.display = true;
					previousPage.display = true;
					drawImage(83,157, 44, 44,lightTankImage);
					drawImage(83,220, 44, 44,rocketTankImage);
					drawImage(83,290, 44, 44,stealthTankImage);
					drawImage(83,364, 44, 44,heavyTankImage);
				}else if(currentHelpPage == 3) {
					tRenderer.drawMenuText(82,90,helpPage4, screen, 255,255,255,11);
					previousPage.display = true;
				}
				
				quitHelpMenu.display = true;
			}
		}
		
		
		updateButtons();
		drawButtons();
		
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
						}else if(buttons.get(i).name == "quitDifficulty" || buttons.get(i).name == "quitHelpMenu") {
							menuStatus = mainMenu;
						}else if(buttons.get(i).name == "nextPage") {
							currentHelpPage++;
						}else if(buttons.get(i).name == "previousPage") {
							currentHelpPage--;
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
		if(gameSuspendCount < 6) {
			
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
	
	public void drawMenuFrame(int width, int height) {
	
		
		int R = 4;
		int G = 94;
		int B = 132;
		
		int R1 = 8;
		int G1 = 188;
		int B1 = 255;
		
		int R2 = 3;
		int G2 = 70;
		int B2 = 99;
		
		int pos = (768 - width)/2 + 90 * 768;
		
		
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
		pos = (768 - width)/2 + 90 * 768;
		for(int i = 0; i < height + 17; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + 90 * 768+1;
		for(int i = 0; i < height + 16; i++) {
			screen[pos + i*768] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + 90 * 768 + 2;
		for(int i = 0; i < height + 15; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		//bottom
		pos = (768 - width)/2 + (90+height+14) * 768;
		for(int i = 3; i < width - 18; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		pos = (768 - width)/2 + (90+height+15) * 768;
		for(int i = 2; i < width - 18; i++) {
			screen[pos + i] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + (90+height+16) * 768;
		for(int i = 1; i < width - 18; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		//bottom right
		pos = (768 - width)/2 + width - 18 + (90+height+16) * 768;
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
		pos = (768 - width)/2 + width -3 +  75 * 768;
		for(int i = 0; i < height + 15; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + width -2+ 74 * 768;
		for(int i = 0; i < height + 16; i++) {
			screen[pos + i*768] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 +  width - 1 + 73 * 768;
		for(int i = 0; i < height + 17; i++) {
			int pixel = screen[pos + i*768];
			screen[pos + i*768] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		//top
		pos = (768 - width)/2 + (90-17) * 768;
		for(int i = 20; i < width -1; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/2) << 16 | (G/2) << 8 | (B/2));
		}
		
		pos = (768 - width)/2 + (90-16) * 768;
		for(int i = 20; i < width - 2; i++) {
			screen[pos + i] = ((R1) << 16 | (G1) << 8 | (B1));
		}
		
		pos = (768 - width)/2 + (90-15) * 768;
		for(int i = 20; i < width - 3; i++) {
			int pixel = screen[pos + i];
			screen[pos + i] = pixel +  ((R/3) << 16 | (G/3) << 8 | (B/3));
		}
		
		
		//top left
		pos = (768 - width)/2 + 90 * 768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = pixel + ((R/2) << 16 | (G/2) << 8 | (B/2));
			}
		}
		
		pos = (768 - width)/2 + 2 + 90 * 768;
		for(int i = 2; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = pixel + ((R/3) << 16 | (G/3) << 8 | (B/3));
			}
		}
		
		pos = (768 - width)/2 + 1 + 90 * 768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < 20-delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
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
