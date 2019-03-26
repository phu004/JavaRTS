package gui;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

import core.mainThread;

//handle text rendering


public class textRenderer {

	public  int[] fontBuffer, star, halfStar;
	public  int[][] chars;
	
	public void init(){
		fontBuffer = new int[665*16];
		star = new int[12*12];
		halfStar = new int[12*12];
		
		//load font image
		Image img = null;
		try{
			img = ImageIO.read(getClass().getResource("../images/" + "font.jpg"));
		}catch(Exception e){
			e.printStackTrace();
		}
		

		PixelGrabber pg = new PixelGrabber(img, 0, 0, 665, 16, fontBuffer, 0, 665);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		//create character bitmaps
		chars = new int[93][16*7];
		for(int i = 0; i < 93; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					chars[i][k+ j*7] = fontBuffer[i*7 + k + j*665];
				}
			}
		}
		
		//load half star images
		try{
			img = ImageIO.read(getClass().getResource("../images/" + "84.jpg"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		pg = new PixelGrabber(img, 0, 0, 12, 12, halfStar, 0, 12);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		
		//load star images
		try{
			img = ImageIO.read(getClass().getResource("../images/" + "85.jpg"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		pg = new PixelGrabber(img, 0, 0, 12, 12, star, 0, 12);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();	
		}
		
	}
	
	public void drawFlashingText(int xPos, int yPos, String text, int[] screen){
		int pixel, SpriteValue, screenValue, overflow, screenIndex;
		int MASK7Bit = 0xFEFEFF;
		
		char[] theText = text.toCharArray();
		
		int t = (int)(35 * Math.sin((double)mainThread.gameFrame/7)) + 35;
		if(t > 64)
			 t = 64;
		
	

		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = 768*yPos + xPos + i*7 + k + j*768;
					
					screenValue = screen[screenIndex];
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					SpriteValue = SpriteValue * t / 64;
					SpriteValue = SpriteValue << 16 | SpriteValue << 8 | SpriteValue;
					
					
					pixel=(SpriteValue&MASK7Bit)+(screenValue&MASK7Bit);
					overflow=pixel&0x1010100;
					overflow=overflow-(overflow>>8);
					screen[screenIndex] = overflow|pixel;
				}
			}
		}
		
	}
	
	public void drawText(int xPos, int yPos, String text, int[] screen, int r, int g, int b){
		int pixel, SpriteValue, screenValue, overflow, screenIndex;
		int MASK7Bit = 0xFEFEFF;
		
		char[] theText = text.toCharArray();
		
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = 768*yPos + xPos + i*7 + k + j*768;
					
					screenValue = screen[screenIndex];
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					SpriteValue = (r*SpriteValue/256) << 16 | (g*SpriteValue/256) << 8 | (b*SpriteValue/256);
					
					
					pixel=(SpriteValue&MASK7Bit)+(screenValue&MASK7Bit);
					overflow=pixel&0x1010100;
					overflow=overflow-(overflow>>8);
					screen[screenIndex] = overflow|pixel;
				}
			}
		}
	}
	
	public void drawText_outline(int xPos, int yPos, String text, int[] screen, int insideColor, int outlineColor){
		int SpriteValue, screenIndex,  width, height;
		
		char[] theText = text.toCharArray();
		
		//draw outline first
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = 768*yPos + xPos + i*7 + k + j*768;	
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > 766 || height < 1 || height > 510)
						continue;
					
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					if((SpriteValue & 0xff) > 100){
						screen[screenIndex+1] =  outlineColor;
						screen[screenIndex-1] =  outlineColor;
						screen[screenIndex+768] =  outlineColor;
						screen[screenIndex-768] =  outlineColor;
						screen[screenIndex+769] =  outlineColor;
						screen[screenIndex+767] =  outlineColor;
						screen[screenIndex-769] =  outlineColor;
						screen[screenIndex-767] =  outlineColor;
						
					}
				}
			}
		}
		
		//draw inside
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = 768*yPos + xPos + i*7 + k + j*768;
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > 766 || height < 1 || height > 510)
						continue;
					
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					if((SpriteValue & 0xff) > 100){
						screen[screenIndex] =  insideColor;
					}
				}
			}
		}
	}
	
	
	public void drawStarCharacter(int xPos, int yPos, int starShape, int[] screen, int insideColor, int outlineColor){
		//draw outline first
		int[] starCharacter = star;
		if(starShape == 1)
			starCharacter = halfStar;
		
		int SpriteValue, screenIndex,  width, height;
		for(int j = 0; j < 12; j++){
			for(int k = 0; k < 12; k++){
				screenIndex = 768*yPos + xPos + k + j*768;	
				
				width = xPos + k;
				height = yPos + j;
				
				if(width < 1 || width > 766 || height < 1 || height > 510)
					continue;
				
				SpriteValue = (starCharacter[k+ j*12]&0xff0000) >> 16; 
				if((SpriteValue & 0xff) > 30){
					screen[screenIndex+1] =  outlineColor;
					screen[screenIndex-1] =  outlineColor;
					screen[screenIndex+768] =  outlineColor;
					screen[screenIndex-768] =  outlineColor;
					screen[screenIndex+769] =  outlineColor;
					screen[screenIndex+767] =  outlineColor;
					screen[screenIndex-769] =  outlineColor;
					screen[screenIndex-767] =  outlineColor;
					
				}
			}
		}
		
		for(int j = 0; j < 12; j++){
			for(int k = 0; k < 12; k++){
				screenIndex = 768*yPos + xPos + k + j*768;
				
				width = xPos+ k;
				height = yPos + j;
				
				if(width < 1 || width > 766 || height < 1 || height > 510)
					continue;
				
				SpriteValue = (starCharacter[k+ j*12]&0xff0000) >> 16; 
				if((SpriteValue & 0xff) > 30){
					screen[screenIndex] =  insideColor;
				}
			}
		}
		
	}
	
}
