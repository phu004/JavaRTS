package gui;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

import core.camera;
import core.mainThread;

public class gameCursor {
	
	public int[][] arrowIcons;
	public int[] cursorIcon;
	public int[] screen;
	
	public void init() {
		
		String folder = "../images/";
		
		arrowIcons = new int[8][24*24];
		for(int i = 0; i < 8; i++) {
			loadTexture(folder + "arrow"+i+".png", arrowIcons[i], 24,24);
		}
		
		cursorIcon = new int[24*24];
		loadTexture(folder + "cursor.png", cursorIcon, 24,24);
	}

	public void updateAndDraw(int[] screen) {
		this.screen = screen;
		int mouseX = inputHandler.mouse_x;
		int mouseY = inputHandler.mouse_y;
		
		
		
		if(!mainThread.gamePaused  && mainThread.gameStarted) {
			
			//draw arrow icons if the player is scrolling the screen with the mouse
			int cursorX = 0;
			int cursorY = 0;
			if(camera.MOVE_DOWN && !camera.MOVE_LEFT && ! camera.MOVE_RIGHT) {
				drawIcon(arrowIcons[1], mouseX-12,489);
			}else if(camera.MOVE_UP && !camera.MOVE_LEFT && ! camera.MOVE_RIGHT) {
				drawIcon(arrowIcons[3], mouseX-12,0);
			}else if(camera.MOVE_LEFT && !camera.MOVE_UP && ! camera.MOVE_DOWN) {
				drawIcon(arrowIcons[2], 0,mouseY-12);
			}else if(camera.MOVE_RIGHT && !camera.MOVE_UP && ! camera.MOVE_DOWN) {
				drawIcon(arrowIcons[0], 745,mouseY-12);
			}else if(camera.MOVE_RIGHT && camera.MOVE_UP) {
				if(mouseY> 768 - mouseX) {
					cursorX = 747;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = -3;
				}
				if(cursorX > 747)
					cursorX = 747;
				if(cursorY < -3)
					cursorY = -3;
				drawIcon(arrowIcons[4], cursorX, cursorY);
			}else if(camera.MOVE_LEFT && camera.MOVE_UP) {
				if(mouseY > mouseX) {
					cursorX = -3;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX - 12;
					cursorY = -3;
				}
				if(cursorX < -3)
					cursorX = -3;
				if(cursorY < -3)
					cursorY = -3;
				drawIcon(arrowIcons[7], cursorX, cursorY);
			}else if(camera.MOVE_LEFT && camera.MOVE_DOWN) {
				if(512 - mouseY > mouseX) {
					cursorX = -3;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = 491;
				}
				
				if(cursorX < -3)
					cursorX = -3;
				if(cursorY > 491)
					cursorY = 491;
				drawIcon(arrowIcons[6], cursorX, cursorY);
			}else if(camera.MOVE_RIGHT && camera.MOVE_DOWN) {
				if(512 - mouseY > 768 -mouseX) {
					cursorX = 747;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = 491;
				}
				if(cursorX >747)
					cursorX = 747;
				if(cursorY > 491)
					cursorY = 491;
				
				drawIcon(arrowIcons[5], cursorX, cursorY);
			}else {
				
				//draw default  icon
				
				drawIcon(cursorIcon, mouseX, mouseY);
			}
		}else {
			//draw default  icon
			
			drawIcon(cursorIcon, mouseX, mouseY);
		}
	}
	
	public void loadTexture(String imgName, int[] buffer, int width, int height){
		Image img = null;
		try{
			img = ImageIO.read(getClass().getResource(imgName));
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
	
	public void drawIcon(int[] icon, int xPos, int yPos) {
		for(int i = 0; i < 24; i++) {
			for(int j = 0; j < 24; j++) {
				int x = xPos +  j;
				int y = yPos + i;
				
				if(x < 0 || x >= 768)
					continue;
				if(y < 0 || y >= 512)
					continue;
				
				int color = icon[j+i*24];
				
				int blue = color&0xff;
				int red =  (color&0xff0000) >> 16;
				if(red < 100 && blue > 100)
					continue;
				screen[x + y*768] = color;
			}
		}
		
	}

}
