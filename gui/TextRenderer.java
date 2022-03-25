package gui;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

import core.mainThread;

//handle text rendering


public class textRenderer {

	public  int[] fontBuffer, menuFontBuffer, star, halfStar;
	public  int[][] chars, menuChars;
	public  int[] menuCharsWidth;
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	
	public void init(){
		fontBuffer = new int[665*16];
		menuFontBuffer = new int[789*16];
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
		
		try{
			img = ImageIO.read(getClass().getResource("../images/" + "menuFont.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		pg = new PixelGrabber(img, 0, 0, 789, 16, menuFontBuffer, 0, 789);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		
		//create character bitmaps for in game text
		chars = new int[93][16*7];
		for(int i = 0; i < 93; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					chars[i][k+ j*7] = fontBuffer[i*7 + k + j*665];
				}
			}
		}
		
		//create character bitmaps for menu text
		menuChars = new int[95][];
  		int[] charEndPosition = new int[]{6,9,15,25,34,46,57,61,65,69,76,86,90,95,99, 105,114,120,130,138,146,155, 163,172,180,189,193,197,206,216,225,234,246,259,267,280,291,300,307,320,330,334,340, 350,357,372,382,396,404,419,428,435,443, 451,463,478,487,496,503,507,516,520,530,539,545,555,565,574,585,594,600,610,619,622,625,633,636,650,659,668,679,689,694,700,705,714,722,735,743,750,757,763,770,778,787};
		int[] charStartPosition = new int[95];
		menuCharsWidth = new int[95];
		for(int i = 1; i < 95; i++) {
			menuCharsWidth[i] = charEndPosition[i] - charEndPosition[i-1];
			charStartPosition[i] = charEndPosition[i-1] + 1;
		}
		menuCharsWidth[0] = 6;
		charStartPosition[0] = 0;
		for(int i = 0; i < 95; i++) {
			menuChars[i] = new int[menuCharsWidth[i] * 16];
			for(int j = 0; j < 16; j++) {
				for(int k = 0; k < menuCharsWidth[i]; k++) {
					menuChars[i][k+j*menuCharsWidth[i]] = menuFontBuffer[charStartPosition[i] + k + j*789];
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
	
	public void drawMenuText(int xPos, int yPos, char[] theText, int[] screen, int r, int g, int b, int filterBrightness) {
		
		int centerX_old = 768/2-1;
		int centery_old = 512/2-1;
		int dx = xPos - centerX_old;
		int dy = yPos - centery_old;
		int centerX_new = screen_width/2-1;
		int centerY_new = screen_height/2 -1;
		xPos = centerX_new + dx;
		yPos = centerY_new + dy;
		
		
		int pixel, SpriteValue, screenValue, overflow, screenIndex;
		int MASK7Bit = 0xFEFEFF;
		int xPos_initial = xPos;
		
		for(int i = 0; i < theText.length; i++){
			if(theText[i] == 10) {
				yPos+=16;
				xPos = xPos_initial;
				continue;
			}
			
			int charIndex = theText[i] - 32;
			int w = menuCharsWidth[charIndex];
			int pos = screen_width*yPos + xPos;
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < w; k++){
					screenIndex = pos + k + j*screen_width;
					screenValue = screen[screenIndex];
					SpriteValue = menuChars[charIndex][k+ j*w]&255; 
					
					if(SpriteValue < filterBrightness)
						continue;
					
					SpriteValue = (r*SpriteValue/256) << 16 | (g*SpriteValue/256) << 8 | (b*SpriteValue/256);
		
					pixel=(SpriteValue&MASK7Bit)+(screenValue&MASK7Bit);
					overflow=pixel&0x1010100;
					overflow=overflow-(overflow>>8);
					screen[screenIndex] = overflow|pixel;
				}
			}
			xPos+=w;
		}
	}
	
	public int getMenuTextWidth(char[] theText) {
		int w = 0;
		for(int i = 0; i< theText.length; i++)
			w+=menuCharsWidth[theText[i] - 32];
		return w;
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
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;
					
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
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;
					
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
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;	
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
						continue;
					
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					if((SpriteValue & 0xff) > 100){
						screen[screenIndex+1] =  outlineColor;
						screen[screenIndex-1] =  outlineColor;
						screen[screenIndex+screen_width] =  outlineColor;
						screen[screenIndex-screen_width] =  outlineColor;
						screen[screenIndex+screen_width+1] =  outlineColor;
						screen[screenIndex+screen_width-1] =  outlineColor;
						screen[screenIndex-(screen_width+1)] =  outlineColor;
						screen[screenIndex-(screen_width-1)] =  outlineColor;
						
					}
				}
			}
		}
		
		//draw inside
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
						continue;
					
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					if((SpriteValue & 0xff) > 100){
						screen[screenIndex] =  insideColor;
					}
				}
			}
		}
	}
	
	public void drawScoreBoardText(int xPos, int yPos, String text, int[] screen, int insideColor, int outlineColor){
	
		int centerX_old = 768/2-1;
		int centery_old = 512/2-1;
		int dx = xPos - centerX_old;
		int dy = yPos - centery_old;
		int centerX_new = screen_width/2-1;
		int centerY_new = screen_height/2 -1;
		
		
		xPos = centerX_new + dx;
		yPos = centerY_new + dy;
		
		int SpriteValue, screenIndex,  width, height;
		
		char[] theText = text.toCharArray();
		
		//draw outline first
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;	
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
						continue;
					
					SpriteValue = chars[theText[i] - 32][k+ j*7]&255; 
					if((SpriteValue & 0xff) > 100){
						screen[screenIndex+1] =  outlineColor;
						screen[screenIndex-1] =  outlineColor;
						screen[screenIndex+screen_width] =  outlineColor;
						screen[screenIndex-screen_width] =  outlineColor;
						screen[screenIndex+screen_width+1] =  outlineColor;
						screen[screenIndex+screen_width-1] =  outlineColor;
						screen[screenIndex-(screen_width+1)] =  outlineColor;
						screen[screenIndex-(screen_width-1)] =  outlineColor;
						
					}
				}
			}
		}
		
		//draw inside
		for(int i = 0; i < theText.length; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 7; k++){
					screenIndex = screen_width*yPos + xPos + i*7 + k + j*screen_width;
					
					width = xPos + i*7 + k;
					height = yPos + j;
					
					if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
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
				screenIndex = screen_width*yPos + xPos + k + j*screen_width;	
				
				width = xPos + k;
				height = yPos + j;
				
				if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
					continue;
				
				SpriteValue = (starCharacter[k+ j*12]&0xff0000) >> 16; 
				if((SpriteValue & 0xff) > 30){
					screen[screenIndex+1] =  outlineColor;
					screen[screenIndex-1] =  outlineColor;
					screen[screenIndex+screen_width] =  outlineColor;
					screen[screenIndex-screen_width] =  outlineColor;
					screen[screenIndex+screen_width+1] =  outlineColor;
					screen[screenIndex+screen_width-1] =  outlineColor;
					screen[screenIndex-(screen_width+1)] =  outlineColor;
					screen[screenIndex-(screen_width-1)] =  outlineColor;
					
				}
			}
		}
		
		for(int j = 0; j < 12; j++){
			for(int k = 0; k < 12; k++){
				screenIndex = screen_width*yPos + xPos + k + j*screen_width;
				
				width = xPos+ k;
				height = yPos + j;
				
				if(width < 1 || width > screen_width-2 || height < 1 || height > screen_height-2)
					continue;
				
				SpriteValue = (starCharacter[k+ j*12]&0xff0000) >> 16; 
				if((SpriteValue & 0xff) > 30){
					screen[screenIndex] =  insideColor;
				}
			}
		}
		
	}
	
}
