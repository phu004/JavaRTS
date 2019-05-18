package gui;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

import core.camera;
import core.mainThread;

public class gameCursor {
	
	public int[][] arrowIcons;
	public int[][] smallArrowIcons;
	public int[] smallArrowIcons4;
	public int[] cursorIcon;
	public int[] screen;
	
	public void init() {
		
		String folder = "../images/";
		
		arrowIcons = new int[8][24*24];
		for(int i = 0; i < 8; i++) {
			loadTexture(folder + "arrow"+i+".png", arrowIcons[i], 24,24);
		}
		
		smallArrowIcons = new int[4][20*20];
		for(int i = 0; i < 4; i++) {
			loadTexture(folder + "smallArrow"+i+".png", smallArrowIcons[i], 20,20);
		}
		
		cursorIcon = new int[24*24];
		loadTexture(folder + "cursor.png", cursorIcon, 24,24);
		
		smallArrowIcons4 = new int[20*20];
		loadTexture(folder + "smallArrow4.png", smallArrowIcons4, 20,20);
	}

	public void updateAndDraw(int[] screen) {
		this.screen = screen;
		int mouseX = inputHandler.mouse_x;
		int mouseY = inputHandler.mouse_y;
		
		boolean mouseOverSelectableUnit = mainThread.pc.mouseOverSelectableUnit;
		int mouseOverUnitType = mainThread.pc.mouseOverUnitType;
		int mouseOverUnitTeam =  mainThread.pc.mouseOverUnitTeam;
		boolean mouseOverUnitIsSelected = mainThread.pc.mouseOverUnitIsSelected;
		boolean hasConVehicleSelected = mainThread.pc.hasConVehicleSelected;
		boolean hasHarvesterSelected = mainThread.pc.hasHarvesterSelected;
		boolean hasTroopsSelected = mainThread.pc.hasTroopsSelected;
		boolean hasTowerSelected = mainThread.pc.hasTowerSelected;
		boolean attackKeyPressed = mainThread.pc.attackKeyPressed;
		boolean cursorIsInMiniMap =  mainThread.pc.cursorIsInMiniMap();
		boolean cursorIsInSideBar = mainThread.pc.cursorIsInSideBar();
		
		
		
		
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
			}else if(mouseOverSelectableUnit && !cursorIsInMiniMap && !cursorIsInSideBar){
				if(!hasHarvesterSelected && !hasTroopsSelected && !hasTowerSelected) {
					//if(!mouseOverUnitIsSelected)
					//	drawSelectionIcon(mouseX, mouseY);
					//else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if(mouseOverUnitTeam == 0 && !(attackKeyPressed && (hasTroopsSelected || hasTowerSelected)) && !(hasHarvesterSelected &&  mouseOverUnitType == 102)) {
					//if(!mouseOverUnitIsSelected)
					//	drawSelectionIcon(mouseX, mouseY);
					//else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if(mouseOverUnitType == 103 && !hasHarvesterSelected && !((hasTroopsSelected || hasTowerSelected) && attackKeyPressed)) {
					//if(!mouseOverUnitIsSelected)
					//	drawSelectionIcon(mouseX, mouseY);
					//else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if((hasTroopsSelected || hasTowerSelected) && attackKeyPressed) {
					drawActionIcon(mouseX, mouseY, 1);
				}else if(hasHarvesterSelected && (mouseOverUnitType == 102 || mouseOverUnitType == 103)) {
					//drawActionIcon(mouseX, mouseY, 2);
					drawIcon(cursorIcon, mouseX, mouseY);
				}else {
					drawIcon(cursorIcon, mouseX, mouseY);
				}
			
			}else if(!mouseOverSelectableUnit && !cursorIsInMiniMap && !cursorIsInSideBar){
				if(!hasHarvesterSelected && !hasTroopsSelected && !hasTowerSelected && !hasConVehicleSelected) {
					drawIcon(cursorIcon, mouseX, mouseY);
				}else if(((hasHarvesterSelected || hasConVehicleSelected) && !(hasTroopsSelected)) || (hasTroopsSelected && !attackKeyPressed) ) {
					//drawActionIcon(mouseX, mouseY, 0);
					drawIcon(cursorIcon, mouseX, mouseY);
				}else if(hasTroopsSelected && attackKeyPressed) {
					drawActionIcon(mouseX, mouseY, 1);
				}
			}else if(cursorIsInMiniMap && attackKeyPressed && hasTroopsSelected){
				drawMinimapAttackIcon(mouseX, mouseY);
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
	
	public void drawMinimapAttackIcon(int xPos, int yPos) {
		int arrowColor = 240 << 16 | 76 << 8 | 34;
		int index = 0;
		int color = 0;
		int blue = 0;
		int red = 0;
		
		int start = xPos - 10 + (yPos-10)*768;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*768;
				if(index > 0 && index < 393216) {
					color = smallArrowIcons4[j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(!pixelInsideSideArea(index))
						continue;

					if(red > 150)
						color = arrowColor;
					screen[index] = color;
				}
			}
		}
			
		
	}
	
	public void drawActionIcon(int xPos, int yPos, int type) {
		xPos-=10;
		yPos-=10;
	
		int r = (7 - (mainThread.gameFrame%21)/3) + 9;
		
		int index = 0;
		int color = 0;
		int blue = 0;
		int red = 0;
		int arrowColor = 0;
		
		
		if(type == 0)
			arrowColor = 34 << 16 | 200 << 8 | 76;
		if(type == 1)
			arrowColor = 240 << 16 | 76 << 8 | 34;
		if(type == 2)
			arrowColor = 255 << 16 | 242 << 8 | 0;
		
		//draw up left arrow
		int start = xPos - r + (yPos-r)*768;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*768;
				if(index > 0 && index < 393216) {
					color = smallArrowIcons[2][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					screen[index] = color;
				}
			}
		}
		
		//draw up right arrow
		start = xPos + r + (yPos-r)*768;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*768;
				if(index > 0 && index < 393216) {
					color = smallArrowIcons[3][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					screen[index] = color;
				}
			}
		}
		
		//draw down right arrow
		start = xPos + r + (yPos + r)*768;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*768;
				if(index > 0 && index < 393216) {
					color = smallArrowIcons[0][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					screen[index] = color;
				}
			}
		}
		
		//draw down left arrow
		start = xPos -r + (yPos + r)*768;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*768;
				if(index > 0 && index < 393216) {
					color = smallArrowIcons[1][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					screen[index] = color;
				}
			}
		}
		
	}
	
	public boolean pixelInsideSideArea(int index){
		int x = index%768;
		int y = index/768;
		
		if(x >=3 && x <=133 && y >= 378 && y <= 509)
			return true;
		
		if(x >=635 && x <=765 && y >= 378 && y <= 509)
			return true;
		
		return false;
	}
	
	
	
	
	public void drawIcon(int[] icon, int xPos, int yPos) {
		int color = 0;
		for(int i = 0; i < 24; i++) {
			for(int j = 0; j < 24; j++) {
				int x = xPos +  j;
				int y = yPos + i;
				
				if(x < 0 || x >= 768)
					continue;
				if(y < 0 || y >= 512)
					continue;
				
				color = icon[j+i*24];
				
				int blue = color&0xff;
				int red =  (color&0xff0000) >> 16;
				if(red < 100 && blue > 100)
					continue;
				screen[x + y*768] = color;
			}
		}
		
	}
	
	public void drawSelectionIcon(int xPos, int yPos) {
		
		
		int r = (9 - (mainThread.gameFrame%18)/2) + 10;
		int w = 8;
		int index = 0;
		int lightGray = 0xffffff;
		int darkGray = 0x222222;
		
		//draw top left
		int start = xPos - r + (yPos-r)*768;
		for(int i = 0; i < w + 2; i++) {
			index = start - 768 - 2 + i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 768 + i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start + w - 768 + i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start - 2 + i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 1 + i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 768 + i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start -1  + w*768;
		if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw top right
		start = xPos + r + (yPos-r)*768;
		for(int i = 0; i < w + 2; i++) {
			index = start - 768 + 2 - i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - i;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 768 - i;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start - w - 768 + i*768;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start + 2 + i*768;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 1 + i*768;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 768 + i*768;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start +1  + w*768;
		if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw bottom left
		start = xPos - r + (yPos+r)*768;
		for(int i = 0; i < w + 2; i++) {
			index = start + 768 - 2 + i;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + i;
			if(index > 0 && index < 393216  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 768 + i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start + w + 768 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start - 2 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 1 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 768 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start -1  - w*768;
		if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw bottom right
		start = xPos + r + (yPos+r)*768;
		for(int i = 0; i < w + 2; i++) {
			index = start + 768 + 2 - i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 768 - i;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start - w + 768 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start + 2 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 1 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 768 - i*768;
			if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start +1  - w*768;
		if(index > 0 && index < 393216 && !pixelInsideSideArea(index))
			screen[index] = 0x0;

	}

}
