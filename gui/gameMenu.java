package gui;
import java.util.ArrayList;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

import core.*;

public class gameMenu {
	
	public int gameSuspendCount;
	
	public int menuStatus = 0;
	public static final int mainMenu = 0;
	public static final int difficulitySelectionMenu = 1;
	public static final int helpMenu = 2;
	public static final int endGameMenu = 3;
	public int[] screen; 
	public int[] screenBlurBuffer;
	public boolean gameStarted, gamePaused, gameEnded;
	
	public String imageFolder = "";
	
	public int[] titleImage;
	
	public button newGame, unpauseGame, showHelp, quitGame, restartGame;
	
	ArrayList<button> buttons = new ArrayList<button>();
	
	public void init() {
		if(titleImage == null)
			titleImage = new int[288*46];
		if(screenBlurBuffer == null)
			screenBlurBuffer = new int[512 * 768];
		
		String folder = "../images/";
		loadTexture(folder + "title.png", titleImage, 216, 35);
		
		newGame = new button("newGame", "New Game", 324, 110, 120, 28);
		buttons.add(newGame);
		
		unpauseGame = new button("unpauseGame", "Resume Game",  324, 110, 120, 28);
		buttons.add(unpauseGame);
		
		showHelp = new button("showHelp", "Help", 324, 160, 120, 28);
		buttons.add(showHelp);
		
		quitGame = new button("quitGame", "Quit Game", 324, 345, 120, 28);
		buttons.add(quitGame);
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
				
			}
			
			showHelp.display = true;
			
			updateButtons();
			
			drawButtons();
		}
		
		
		
		
	}
	
	public void updateButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			if(buttons.get(i).checkIfCursorIsOnTop(postProcessingThread.mouse_x, postProcessingThread.mouse_y)) {
				if(postProcessingThread.leftMouseButtonReleased) {
					if(buttons.get(i).actionCooldown == 0) {
						postProcessingThread.buttonAction = buttons.get(i).name;
						buttons.get(i).actionCooldown = 15;
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
		
		int pos = (768 - width)/2 + 90 * 768;
		
		
		//background
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int pixel = screen[pos + j + i* 768];
				screen[pos + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R/2) << 16 | (G/2) << 8 | (B/2));
			}
		}
		float d = 20f;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j = 20-delta-1; j < width; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R/2) << 16 | (G/2) << 8 | (B/2));
			}
		}
		
		pos+=(height+17)*768;
		for(int i = 0; i < 17; i++) {
			int delta = (int)((d/17)*i);
			for(int j =0; j < width - delta; j++) {
				int pixel = screen[pos - 17*768 + j + i* 768];
				screen[pos - 17*768 + j + i* 768] = ((pixel&0xFEFEFE)>>1) +  ((R/2) << 16 | (G/2) << 8 | (B/2));
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
