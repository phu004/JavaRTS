package gui;

import core.mainThread;
import core.postProcessingThread;
import core.vector;

public class MiniMap {

	public int[] background;
	public boolean[] tempBitmap;
	public boolean[][] bitmapVision;
	public static vector corner1, corner2, corner3, corner4;
	public static boolean isDrawingWindow;
	
	
	public void init(){
		//load terrain image as background for minimap 
		background = new int[128*128];
		int r = (int)(193 * 0.95);
		int g = (int)(176 * 0.95);
		int b = (int)(128 * 0.95 );
		
		for(int i = 0; i < 128 * 128; i++)
			background[i]= (r << 16) | (g << 8) | b;
		
		tempBitmap = new boolean[148 * 148];
		
		bitmapVision = new boolean[4][];
		
		bitmapVision[0] = createBitmapVision(6);
		bitmapVision[1] = createBitmapVision(2);
		bitmapVision[2] = createBitmapVision(7);
		bitmapVision[3] = createBitmapVision(11);
		
		corner1 = new vector(0,0,0);
		corner2 = new vector(0,0,0);
		corner3 = new vector(0,0,0);
		corner4 = new vector(0,0,0);
	}
	
	
	public void draw(int[] screen, boolean[] minimapBitmap, int[][] unitsForMiniMap,  int unitsForMiniMapCount){
		//draw mini map window frame
		drawFrame(screen);
	
		//create bitmap
		createBitmap(minimapBitmap, unitsForMiniMap, unitsForMiniMapCount);
		
		//draw background  
		drawBackground(screen, minimapBitmap);
		
		//remove fog of war for testing
		for(int i = 0; i < minimapBitmap.length; i++)
			minimapBitmap[i] = true;
		
		
		//draw unit positions on minimap
		drawUnit(screen, minimapBitmap, unitsForMiniMap, unitsForMiniMapCount);
		
		//draw view window
		drawViewWindow(screen);
		
	}
	
	public void drawViewWindow(int[] screen){
		int xPos1, xPos2, xPos3,xPos4, yPos1, yPos2, yPos3, yPos4;
	
	
		xPos1 = (int)(corner1.x*64/16);
		yPos1 = 127 - (int)(corner1.z*64/16);
		xPos2 = (int)(corner2.x*64/16);
		yPos2 = 127 - (int)(corner2.z*64/16);
		xPos3 = (int)(corner3.x*64/16);
		yPos3 = 127 - (int)(corner3.z*64/16);
		xPos4 = (int)(corner4.x*64/16);
		yPos4 = 127 - (int)(corner4.z*64/16);
		
		
		drawLine(xPos1+1, yPos1, xPos2-1,yPos2, screen);
		drawLine(xPos2, yPos2, xPos3,yPos3, screen);
		drawLine(xPos3-1, yPos3, xPos4+1,yPos4, screen);
		drawLine(xPos4, yPos4, xPos1,yPos1, screen);
		
		
		
		
	}
	
	public void findCorners(){
		
		corner1.set(postProcessingThread.my2Dto3DFactory.get3DLocation(mainThread.theAssetManager.Terrain.ground[0], -40, -40));
		corner2.set(postProcessingThread.my2Dto3DFactory.get3DLocation(mainThread.theAssetManager.Terrain.ground[0], 807, -40));
		corner3.set(postProcessingThread.my2Dto3DFactory.get3DLocation(mainThread.theAssetManager.Terrain.ground[0], 807, 551));
		corner4.set(postProcessingThread.my2Dto3DFactory.get3DLocation(mainThread.theAssetManager.Terrain.ground[0], -40, 551));
		
	}
	
	public void drawLine(int xPos1, int yPos1, int xPos2, int yPos2, int[] screen){
		int start = 381 * 768 + 3;
		int  x, y;
		int xDirection, yDirection;
		
		if(xPos1 < xPos2)
			xDirection = 1;
		else
			xDirection = -1;
		
		if(yPos1 < yPos2)
			yDirection = 1;
		else
			yDirection = -1;
		
		int color;
		
		if(Math.abs(xPos2 - xPos1) > Math.abs(yPos2 - yPos1)){
			float slope = (float)(yPos2 - yPos1)/(xPos2 - xPos1);
			for(int i = 0; i <= Math.abs(xPos2 - xPos1); i ++){
				x = xPos1 + i*xDirection;
				y = (int)(yPos1 + slope*i*xDirection);
						
				if(x < 0 || x > 127 || y < 0 || y > 127)
					continue;
				color = screen[start + x + y*768];
				screen[start + x + y*768] = ((((color&0xFEFEFE)>>1)&0xFEFEFE)>>1) +  0xbfbfbf;
			}
			
		}else{
			float slope = (float)(xPos2 - xPos1)/(yPos2 - yPos1);
			for(int i = 0; i <= Math.abs(yPos2 - yPos1); i ++){
				y = yPos1 + i*yDirection;
				x = (int)(xPos1 + slope*i*yDirection);
				
				if(x < 0 || x > 127 || y < 0 || y > 127)
					continue;
				color = screen[start + x + y*768];
				screen[start + x + y*768] = ((((color&0xFEFEFE)>>1)&0xFEFEFE)>>1) +  0xbfbfbf;
				
			}
		}
	}
	
	
	public void drawUnit(int[] screen, boolean[] minimapBitmap, int[][] unitsForMiniMap,  int unitsForMiniMapCount){
		int xPos = 0;
		int yPos = 0;
		int start = 381 * 768 + 3;
		int index = 0;
		int friendlyUnitColor = 170 << 8;
		int friendlyBuildingColor = 46 << 16 | 114 << 8 | 22; 
		
		float p = (1f + (float)Math.sin((float)(postProcessingThread.frameIndex)/5))/2;
		int c = (int)(255*p);
		int underAttackColor = 0xffff0000 | (c << 8) | c;
		
		int enemyUnitColor = 224 << 16;
		int enemyBuildingColor1 = 153 << 16; 
		int enemyBuildingColor2 = 90 << 16; 
		int type = 0;
		
		for(int i = 0; i < unitsForMiniMapCount; i++){
			xPos = unitsForMiniMap[i][1];
			yPos = unitsForMiniMap[i][2];
			index = start + xPos + yPos*768;
			
			type = unitsForMiniMap[i][0] >> 8;
			
			if((unitsForMiniMap[i][0] & 0xff) == 0){
				int color;
				if(unitsForMiniMap[i][3] >= 2 && type != 6)
					color = friendlyBuildingColor;
				else
					color = friendlyUnitColor;
				if(unitsForMiniMap[i][4] == 10001){
					color = underAttackColor;
				}
				
				if((screen[index]>>24) == 0)
					screen[index] = color;
				if((screen[index + 1]>>24) == 0)
					screen[index+1] = color;
				if((screen[index+768]>>24) == 0)
					screen[index+768] = color;
				if((screen[index+768 + 1]>>24) == 0)
					screen[index+768 + 1] = color;
				
			}else{
				int position = xPos + yPos*128;
				if(position >=0 && position < 16384){
					if(minimapBitmap[xPos + yPos*128] || unitsForMiniMap[i][4] == 10000){
						int color;
						if(!minimapBitmap[xPos + yPos*128]){
							color = enemyBuildingColor2;
						}else{
							if(unitsForMiniMap[i][3] >= 2 && type != 6)
								color = enemyBuildingColor1;
							else
								color = enemyUnitColor;
						}
							
						screen[index] = color;
						screen[index+1] = color;
						screen[index+768] = color;
						screen[index+768 + 1] = color;
					}
				}
			}
		}
	}
	
	
	public boolean[] createBitmapVision(int radius){
		int l = radius*2+1;
		boolean[] vision = new boolean[l*l];
		for(int y = 0; y < l; y++){
			for(int x = 0; x < l; x++){
				if( (x - radius)*(x - radius) + (y - radius)*(y - radius)   <  ((float)radius+0.5f)*((float)radius+0.5f)){
					vision[x + y*l] = true;
				}
			}
		}
		return vision;
	}
	
	
	public void createBitmap(boolean[] minimapBitmap, int[][] unitsForMiniMap, int unitsForMiniMapCount){
		
		for(int i = 0 ;i < tempBitmap.length; i++){
			tempBitmap[i] = false;
		}
		
		boolean[] vision;
		int visionType = 0;
		int xPos = 0;
		int yPos = 0;
		int l = 0;
		int r = 0;
		for(int i = 0; i < unitsForMiniMapCount; i++){
			if((unitsForMiniMap[i][0] & 0xff) != 0){
				//if(unitsForMiniMap[i][4] != 1)
				//	continue;
				visionType = 1;
			}else{
				
				visionType = unitsForMiniMap[i][3];
			}
			
			vision = bitmapVision[visionType];
			
			if(visionType == 0){
				l = 13;
				r = 6;
			}else if(visionType == 1){
				l = 5;
				r = 2;
			}else if(visionType == 2){
				l = 15;
				r = 7;
			}else if(visionType == 3){
				l = 23;
				r = 11;
			}
			
			xPos = unitsForMiniMap[i][1] - r + 10;
			yPos = unitsForMiniMap[i][2] - r + 10;
			
			if((unitsForMiniMap[i][0] & 0xff) != 0 && (unitsForMiniMap[i][4] == 0 || unitsForMiniMap[i][4] == 10000)){
				continue;
			}
			
			for(int y = 0; y < l; y++){
				for(int x = 0; x < l; x++){
					if(vision[x+ y*l])
						tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}
		
		
		
		for(int y = 0; y < 128; y++){
			for(int x = 0; x < 128; x++){
				minimapBitmap[x + y*128] = tempBitmap[x + 10 + (y + 10)*148];
			}
		}
			
	}
	
	
	public void drawBackground(int[] screen, boolean[] minimapBitmap){
		int start = 381 * 768 + 3;
		int color = 0;
		
		
		
		int goldMineColor = 196 << 16 | 138 << 8 | 0;
		
		
		for(int y = 0; y < 128; y++){
			for(int x = 0; x < 128; x ++){
				color = background[x + y*128];
				if(minimapBitmap[x + y*128]){
					screen[start + x + y*768] = color;
				}else{
					if(((color & 0xf000000) >> 24) == 12)
						screen[start + x + y*768] = goldMineColor;
					else
						screen[start + x + y*768] = (color&0xFEFEFE)>>1;
				}
			}
		}
	}
	
	
	
	public void drawFrame(int[] screen){
		int color1 = 0xa0a0a0;
		int color2 = 0xe0e0e0;
		
		int start = 378*768;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color1;
		}
		start = 379*768;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = 380*768;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		
		start = 509*768;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		start = 510*768;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = 511*768;
		for(int x = 0; x < 134; x++){
			screen[start + x] = color1;
		}
		
		start = 378*768 + 133;
		for(int y = 1; y < 132; y++){
			screen[start + y*768] = color1;
		}
		
		start = 379*768 + 132;
		for(int y = 0; y < 132; y++){
			screen[start + y*768] = color2;
		}
		
		start = 380*768 + 131;
		for(int y = 0; y < 131; y++){
			screen[start + y*768] = color1;
		}
		
		start = 378*768;
		for(int y = 1; y < 132; y++){
			screen[start + y*768] = color1;
		}
		
		start = 379*768 + 1;
		for(int y = 0; y < 132; y++){
			screen[start + y*768] = color2;
		}
		
		start = 380*768 + 2;
		for(int y = 0; y < 131; y++){
			screen[start + y*768] = color1;
		}
	}
	
}
