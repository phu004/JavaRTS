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
	public int[][] iconOverWriteBuffer;
	public int iconOverWriteBufferIndex;
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	public static int screen_size = mainThread.screen_size;
	
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
		
		iconOverWriteBuffer = new int[1024][2];
		for(int i = 0; i < 1024; i++) {
			iconOverWriteBuffer[i][0] = -1;
		}
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
		
		
		for(int i = 0; i < 1024; i++) {
			if(iconOverWriteBuffer[i][0] == -1)
				break;
			
			screen[iconOverWriteBuffer[i][0]] = iconOverWriteBuffer[i][1];
			iconOverWriteBuffer[i][0] = -1;
		}
		iconOverWriteBufferIndex = 0;
		
		
		if(!mainThread.gamePaused  && mainThread.gameStarted) {
			//draw arrow icons if the player is scrolling the screen with the mouse
			int cursorX = 0;
			int cursorY = 0;
			if(camera.MOVE_DOWN && !camera.MOVE_LEFT && ! camera.MOVE_RIGHT) {
				drawIcon(arrowIcons[1], mouseX-12,screen_height - 23);
			}else if(camera.MOVE_UP && !camera.MOVE_LEFT && ! camera.MOVE_RIGHT) {
				drawIcon(arrowIcons[3], mouseX-12,0);
			}else if(camera.MOVE_LEFT && !camera.MOVE_UP && ! camera.MOVE_DOWN) {
				drawIcon(arrowIcons[2], 0,mouseY-12);
			}else if(camera.MOVE_RIGHT && !camera.MOVE_UP && ! camera.MOVE_DOWN) {
				drawIcon(arrowIcons[0], screen_width-23 ,mouseY-12);
			}else if(camera.MOVE_RIGHT && camera.MOVE_UP) {
				if(mouseY> screen_width - mouseX) {
					cursorX = screen_width-21;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = -3;
				}
				if(cursorX > screen_width-21)
					cursorX = screen_width-21;
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
				if(screen_height - mouseY > mouseX) {
					cursorX = -3;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = screen_height - 21;
				}
				
				if(cursorX < -3)
					cursorX = -3;
				if(cursorY > screen_height - 21)
					cursorY = screen_height - 21;
				drawIcon(arrowIcons[6], cursorX, cursorY);
			}else if(camera.MOVE_RIGHT && camera.MOVE_DOWN) {
				if(screen_height - mouseY > screen_width -mouseX) {
					cursorX = screen_width-21;
					cursorY = mouseY-12;
				}else {
					cursorX = mouseX-12;
					cursorY = screen_height - 21;
				}
				if(cursorX >screen_width-21)
					cursorX = screen_width-21;
				if(cursorY > screen_height - 21)
					cursorY = screen_height - 21;
				
				drawIcon(arrowIcons[5], cursorX, cursorY);
			}else if(mouseOverSelectableUnit && !cursorIsInMiniMap && !cursorIsInSideBar){
				if((hasTroopsSelected || hasTowerSelected) && mouseOverUnitTeam == 1) {
					drawActionIcon(mouseX, mouseY, 1);
				}else if(!hasHarvesterSelected && !hasTroopsSelected && !hasTowerSelected) {
					if(!mouseOverUnitIsSelected)
						drawSelectionIcon(mouseX, mouseY);
					else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if(mouseOverUnitTeam == 0 && !(attackKeyPressed && (hasTroopsSelected || hasTowerSelected)) && !(hasHarvesterSelected &&  mouseOverUnitType == 102)) {
					if(!mouseOverUnitIsSelected)
						drawSelectionIcon(mouseX, mouseY);
					else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if(mouseOverUnitType == 103 && !hasHarvesterSelected && !((hasTroopsSelected || hasTowerSelected) && attackKeyPressed)) {
					if(!mouseOverUnitIsSelected)
						drawSelectionIcon(mouseX, mouseY);
					else
						drawIcon(cursorIcon, mouseX, mouseY);
				}else if((hasTroopsSelected || hasTowerSelected) && attackKeyPressed) {
					drawActionIcon(mouseX, mouseY, 1);
				}else if(hasHarvesterSelected && (mouseOverUnitType == 102 || mouseOverUnitType == 103)) {
					drawActionIcon(mouseX, mouseY, 2);
					//drawIcon(cursorIcon, mouseX, mouseY);
				}else {
					drawIcon(cursorIcon, mouseX, mouseY);
				}
			
			}else if(!mouseOverSelectableUnit && !cursorIsInMiniMap && !cursorIsInSideBar){
				if(!hasHarvesterSelected && !hasTroopsSelected && !(hasTowerSelected && attackKeyPressed) && !hasConVehicleSelected) {
					drawIcon(cursorIcon, mouseX, mouseY);
				}else if(((hasHarvesterSelected || hasConVehicleSelected) && !(hasTroopsSelected)) || ((hasTroopsSelected) && !attackKeyPressed) ) {
					drawActionIcon(mouseX, mouseY, 0);
					//drawIcon(cursorIcon, mouseX, mouseY);
				}else if((hasTroopsSelected || hasTowerSelected) && attackKeyPressed) {
					drawActionIcon(mouseX, mouseY, 1);
				}
			}else if(cursorIsInMiniMap && attackKeyPressed && hasTroopsSelected){
				drawMinimapAttackIcon(mouseX, mouseY);
			}else if(cursorIsInMiniMap && (hasTroopsSelected || hasConVehicleSelected || hasHarvesterSelected)){
				drawMinimapMoveIcon(mouseX, mouseY);
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
		
		int start = xPos - 10 + (yPos-10)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
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
	
	public void drawMinimapMoveIcon(int xPos, int yPos) {
		int arrowColor = 34 << 16 | 200 << 8 | 76;
		int index = 0;
		int color = 0;
		int blue = 0;
		int red = 0;
		
		int start = xPos - 10 + (yPos-10)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
					color = smallArrowIcons4[j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(!pixelInsideSideArea(index))
						continue;

					if(red > 150)
						color = arrowColor;
					iconOverWriteBuffer[iconOverWriteBufferIndex][0] = index;
					iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[index];
					iconOverWriteBufferIndex++;
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
		int start = xPos - r + (yPos-r)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
					color = smallArrowIcons[2][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					iconOverWriteBuffer[iconOverWriteBufferIndex][0] = index;
					iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[index];
					iconOverWriteBufferIndex++;
					screen[index] = color;
				}
			}
		}
		
		//draw up right arrow
		start = xPos + r + (yPos-r)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
					color = smallArrowIcons[3][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					iconOverWriteBuffer[iconOverWriteBufferIndex][0] = index;
					iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[index];
					iconOverWriteBufferIndex++;
					screen[index] = color;
				}
			}
		}
		
		//draw down right arrow
		start = xPos + r + (yPos + r)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
					color = smallArrowIcons[0][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					iconOverWriteBuffer[iconOverWriteBufferIndex][0] = index;
					iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[index];
					iconOverWriteBufferIndex++;
					screen[index] = color;
				}
			}
		}
		
		//draw down left arrow
		start = xPos -r + (yPos + r)*screen_width;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				index = start + j + i*screen_width;
				if(index > 0 && index < screen_size) {
					color = smallArrowIcons[1][j+i*20];
					
					blue = color&0xff;
					red =  (color&0xff0000) >> 16;
					if(red < 100 && blue > 100)
						continue;
					
					if(pixelInsideSideArea(index))
						continue;

					if(red > 200)
						color = arrowColor;
					
					iconOverWriteBuffer[iconOverWriteBufferIndex][0] = index;
					iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[index];
					iconOverWriteBufferIndex++;
					screen[index] = color;
				}
			}
		}
		
	}
	
	public boolean pixelInsideSideArea(int index){
		int x = index%screen_width;
		int y = index/screen_width;
		
		if(x >=3 && x <=133 && y >= screen_height-134 && y <= screen_height-3)
			return true;
		
		if(x >=screen_width-133 && x <=screen_width-3 && y >= screen_height-134 && y <= screen_height-3)
			return true;
		
		return false;
	}
	
	
	
	
	public void drawIcon(int[] icon, int xPos, int yPos) {
		int color = 0;
		for(int i = 0; i < 24; i++) {
			for(int j = 0; j < 24; j++) {
				int x = xPos +  j;
				int y = yPos + i;
				
				if(x < 0 || x >= screen_width)
					continue;
				if(y < 0 || y >= screen_height)
					continue;
				
				color = icon[j+i*24];
				
				int blue = color&0xff;
				int red =  (color&0xff0000) >> 16;
				if(red < 100 && blue > 100)
					continue;
				
				iconOverWriteBuffer[iconOverWriteBufferIndex][0] = x + y*screen_width;
				iconOverWriteBuffer[iconOverWriteBufferIndex][1] = screen[x + y*screen_width];
				iconOverWriteBufferIndex++;
				screen[x + y*screen_width] = color;
				
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
		int start = xPos - r + (yPos-r)*screen_width;
		for(int i = 0; i < w + 2; i++) {
			index = start - screen_width - 2 + i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + screen_width + i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start + w - screen_width + i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start - 2 + i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 1 + i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + screen_width + i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start -1  + w*screen_width;
		if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw top right
		start = xPos + r + (yPos-r)*screen_width;
		for(int i = 0; i < w + 2; i++) {
			index = start - screen_width + 2 - i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - i;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + screen_width - i;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start - w - screen_width + i*screen_width;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start + 2 + i*screen_width;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 1 + i*screen_width;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + screen_width + i*screen_width;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start +1  + w*screen_width;
		if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw bottom left
		start = xPos - r + (yPos+r)*screen_width;
		for(int i = 0; i < w + 2; i++) {
			index = start + screen_width - 2 + i;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + i;
			if(index > 0 && index < screen_size  && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - screen_width + i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start + w + screen_width - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start - 2 - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - 1 - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - screen_width - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start -1  - w*screen_width;
		if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
			screen[index] = 0x0;
		
		//draw bottom right
		start = xPos + r + (yPos+r)*screen_width;
		for(int i = 0; i < w + 2; i++) {
			index = start + screen_width + 2 - i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - screen_width - i;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < 3; i++) {
			index  = start - w + screen_width - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w + 1; i++) {
			index = start + 2 - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start + 1 - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = lightGray;
		}
		
		for(int i = 0; i < w; i++) {
			index = start - screen_width - i*screen_width;
			if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
				screen[index] = darkGray;
		}
		
		index = start +1  - w*screen_width;
		if(index > 0 && index < screen_size && !pixelInsideSideArea(index))
			screen[index] = 0x0;

	}

}
