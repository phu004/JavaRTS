package gui;

import core.postProcessingThread;

public class button {
	
	public int xPos, yPos, width, height;
	public String name, text;
	public char[] theText;
	public boolean display, cursorIsOnTop;
	public int actionCooldown;
	public int red, green, blue;
	public boolean disabled;
	public boolean messageMode;
	
	public button(String name, String text, int xPos, int yPos, int width, int height) {
		this.name = name;
		this.text = text;
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		theText = text.toCharArray();
		
	}
	
	public boolean checkIfCursorIsOnTop(int mouse_x, int mouse_y) {
		cursorIsOnTop = mouse_x > xPos  && mouse_x < xPos + width && mouse_y > yPos && mouse_y < yPos + height;
		
		return cursorIsOnTop && display;
	}
	
	public void draw(int[] screen) {
		if(disabled && !messageMode) {
			red = 55;
			green = 55;
			blue = 55;
		}else {
			red = 255;
			green = 255;
			blue = 255;
		}
			
		
		if(display == false) {
			cursorIsOnTop = false;
			return;
		}
		
		
		int R = 6;
		int G = 141;
		int B = 198;
		
		//drawButton;
		int color = ((R) << 16 | (G) << 8 | (B));
		int pos = xPos + yPos* 768;
		for(int i = 0; i < height/3; i++) {
			for(int j = height/3 -i; j < width; j++) {
				screen[pos+ j+ i*768] = color;
			}
		}
		
		for(int i = height/3; i < height/3*2; i++) {
			for(int j = 0; j < width; j++) {
				screen[pos+ j+ i*768] = color;
			}
		}
		
		for(int i = height/3*2; i < height; i++) {
			for(int j = 0; j < width - (i - height/3*2); j++) {
				screen[pos+ j+ i*768] = color;
			}
		}
		
		//draw highlight of cursor is on top of the button
		if(cursorIsOnTop && !disabled) {
			
			R = 239;
			G = 253;
			B = 155;
			color = ((R) << 16 | (G) << 8 | (B));
			
			//inner layer
			for(int i = 0; i < 1; i++) {
				for(int j = height/3 -i; j < width; j++) {
					int pixel = screen[pos+ j+ (i-1)*768];
					int R1 = (pixel&0xff0000) >> 16;
					int G1 = (pixel&0xff00) >> 8;
					int B1 = (pixel&0xff);
					screen[pos+ j+ (i-1)*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
				}
			}
			
			for(int i = height; i < height+1; i++) {
				for(int j = 0; j < width + 1 - (i + 1 - height/3*2); j++) {
					int pixel = screen[pos+ j+ i*768];
					int R1 = (pixel&0xff0000) >> 16;
					int G1 = (pixel&0xff00) >> 8;
					int B1 = (pixel&0xff);
					screen[pos+ j+ i*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
				}
			}
			
			for(int i = height/3+1; i < height + 1; i++) {
				int pixel = screen[pos -1 + i*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos -1 + i*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
			}
			
			for(int i = -1; i < height/3*2 + 1; i++) {
				int pixel = screen[pos + width + i*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + width + i*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
			}
			
			for(int i = height/3 + 1; i > 0; i--) {
				int pixel = screen[pos + height/3- i + (i-1)*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + height/3- i + (i-1)*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
			}
			
			for(int i = height; i > height/3*2; i--) {
				int pixel = screen[pos + width + height/3*2- i + (i)*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + width + height/3*2- i + (i)*768] = (R1 + (R - R1)/4*3) << 16 | (G1 + (G - G1)/4*3) << 8 | (B1 + (B - B1)/4*3);
			}
			
			//outer layer
			for(int i = 0; i < 1; i++) {
				for(int j = height/3 -i; j < width+2; j++) {
					int pixel = screen[pos+ j+ (i-2)*768];
					int R1 = (pixel&0xff0000) >> 16;
					int G1 = (pixel&0xff00) >> 8;
					int B1 = (pixel&0xff);
					screen[pos+ j+ (i-2)*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
				}
			}
			
			for(int i = height + 1; i < height+2; i++) {
				for(int j = -2; j < width + 2 - (i + 1 - height/3*2); j++) {
					int pixel = screen[pos+ j+ i*768];
					int R1 = (pixel&0xff0000) >> 16;
					int G1 = (pixel&0xff00) >> 8;
					int B1 = (pixel&0xff);
					screen[pos+ j+ i*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
				}
			}
			
			for(int i = height/3; i < height + 1; i++) {
				int pixel = screen[pos -2 + i*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos -2 + i*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
			}
			
			for(int i = -1; i < height/3*2 + 1; i++) {
				int pixel = screen[pos + 1 + width + i*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + width + 1 + i*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
			}
			
			for(int i = height/3 + 1; i > 0; i--) {
				int pixel = screen[pos  + height/3- i + (i-2)*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + height/3- i + (i-2)*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
			}
			
			for(int i = height + 1; i > height/3*2; i--) {
				int pixel = screen[pos +1 + width + height/3*2- i + (i)*768];
				int R1 = (pixel&0xff0000) >> 16;
				int G1 = (pixel&0xff00) >> 8;
				int B1 = (pixel&0xff);
				screen[pos + 1 + width + height/3*2- i + (i)*768] = (R1 + (R - R1)/3) << 16 | (G1 + (G - G1)/3) << 8 | (B1 + (B - B1)/3);
			}
			
		}
		
		//draw text
		textRenderer tRenderer = postProcessingThread.theTextRenderer;
		if(text != "x")
			tRenderer.drawMenuText(xPos+ (width-tRenderer.getMenuTextWidth(theText))/2,yPos+6,theText, screen, red,green,blue,11);
		else{
			tRenderer.drawMenuText(xPos+ (width-tRenderer.getMenuTextWidth(theText))/2-1,yPos,theText, screen, red,green,blue, 11);
		}
		
		
		
		cursorIsOnTop = false;
	}

}
