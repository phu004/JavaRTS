package gui;

import java.awt.image.PixelGrabber;
import java.awt.*;

import javax.imageio.ImageIO;

import core.mainThread;
import core.postProcessingThread;

public class SideBar {
	
	public int[][] iconImages;
	public int[][] iconImages_dark;
	public int[] xStart; 
	public int[] yStart;
	
	public int[] autoRepairMark;
	
	public boolean[][] progressBitmaps;
	
	public int onScreenPlayerMoney;
	
	public int MASK7Bit = 0xFEFEFF;
	public int pixel, overflow, screenIndex;
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	
	public void init(){		
		xStart = new int[]{screen_width - 134, screen_width-91, screen_width-46, screen_width-134, screen_width-91, screen_width-46,screen_width-134, screen_width-91, screen_width-46};
		yStart = new int[]{screen_height-131, screen_height-131, screen_height-131, screen_height-87, screen_height-87, screen_height-87,screen_height-44, screen_height-44, screen_height-44};
		
		
		progressBitmaps = new boolean[240][44*44];
		prepareProgressBitmaps();
		
		autoRepairMark = new int[74];
		for(int i = 0; i < 20; i++){
			autoRepairMark[i] = (screen_width-44) + i*2 + (screen_height-42)*screen_width;
		}
		
		for(int i = 20; i < 37; i++){
			autoRepairMark[i] = (screen_width-44) + 38 + ((screen_height-40)+(i-20)*2)*screen_width;
		}
		for(int i = 37; i < 57; i++){
			autoRepairMark[i] = (screen_width-44) + 38 - (i-37)*2 + ((screen_height-42)+36)*screen_width;
			
		}
		for(int i = 57; i < 74; i++){
			autoRepairMark[i] = (screen_width-44) + ((screen_height-42)+(74-i)*2)*screen_width;
			
		}
		
		
		iconImages = new int[25][44 * 44];
		iconImages_dark = new int[25][44 * 44];
		String folder = "../images/";
		loadTexture(folder + "44.jpg", iconImages[0], 44, 44, iconImages_dark[0]);
		loadTexture(folder + "47.jpg", iconImages[1], 44, 44, iconImages_dark[1]);
		loadTexture(folder + "48.jpg", iconImages[2], 44, 44, iconImages_dark[2]);
		loadTexture(folder + "49.jpg", iconImages[3], 16, 16, iconImages_dark[3]);
		loadTexture(folder + "50.jpg", iconImages[4], 16, 16, iconImages_dark[4]);
		loadTexture(folder + "57.jpg", iconImages[5], 44, 44, iconImages_dark[5]);
		loadTexture(folder + "58.jpg", iconImages[6], 44, 44, iconImages_dark[6]);
		loadTexture(folder + "59.jpg", iconImages[7], 44, 44, iconImages_dark[7]);
		loadTexture(folder + "60.jpg", iconImages[8], 44, 44, iconImages_dark[8]);
		loadTexture(folder + "65.jpg", iconImages[9], 44, 44, iconImages_dark[9]);
		loadTexture(folder + "66.jpg", iconImages[10], 44, 44, iconImages_dark[10]);
		loadTexture(folder + "67.jpg", iconImages[11], 44, 44, iconImages_dark[11]);
		loadTexture(folder + "72.jpg", iconImages[12], 44, 44, iconImages_dark[12]);
		loadTexture(folder + "75.jpg", iconImages[13], 44, 44, iconImages_dark[13]);
		loadTexture(folder + "76.jpg", iconImages[14], 44, 44, iconImages_dark[14]);
		loadTexture(folder + "77.jpg", iconImages[15], 44, 44, iconImages_dark[15]);
		loadTexture(folder + "78.jpg", iconImages[16], 44, 44, iconImages_dark[16]);
		loadTexture(folder + "79.jpg", iconImages[17], 44, 44, iconImages_dark[17]);
		loadTexture(folder + "81.jpg", iconImages[18], 44, 44, iconImages_dark[18]);
		loadTexture(folder + "83.jpg", iconImages[19], 44, 44, iconImages_dark[19]);
		loadTexture(folder + "86.jpg", iconImages[20], 44, 44, iconImages_dark[20]);
		loadTexture(folder + "87.jpg", iconImages[21], 44, 44, iconImages_dark[21]);
		loadTexture(folder + "88.jpg", iconImages[22], 44, 44, iconImages_dark[22]);
		loadTexture(folder + "89.jpg", iconImages[23], 44, 44, iconImages_dark[23]);
		loadTexture(folder + "time.png", iconImages[24], 16, 16, iconImages_dark[24]);
		
		
	}
	
	public void draw(int[] screen, int[] sideBarInfo){
		
		drawBackground(screen);

		drawSideBarInfo(screen, sideBarInfo);
		
		drawCreditAndPowerLevelAndTime(screen, postProcessingThread.playerMoney, postProcessingThread.playerPowerStatus);
		
		drawFrame(screen);
	}
	
	
	public void drawCreditAndPowerLevelAndTime(int[] screen, int playerMoney, int playerPowerStatus){

		drawInfoBackGround(screen, 2, 3, 65, 16, 100);
		drawIcon(screen, 5 + 2 * screen_width, 24);
		postProcessingThread.theTextRenderer.drawText(23, 3, postProcessingThread.timeString, screen, 255,255,255);
		
		drawInfoBackGround(screen, screen_width-131, 3, 129, 16, 100);
		int startIndex = screen_width-129 + 3 * screen_width;
		
		drawIcon(screen, startIndex, 3);
		
		calculateOnScreenPlayerMoney(playerMoney);
		
		postProcessingThread.theTextRenderer.drawText(screen_width-111, 3, onScreenPlayerMoney+"", screen, 245,197,51);
		
		if(playerPowerStatus != -1){
			drawIcon(screen, startIndex + 60, 4);
			
			int currentPowerConsumption = playerPowerStatus >> 16;
			int currentPowerLevel = playerPowerStatus&0xffff;

			if(currentPowerConsumption <= currentPowerLevel){
				postProcessingThread.theTextRenderer.drawText(screen_width-54, 3, currentPowerConsumption/50 + "/"+currentPowerLevel/50, screen, 0,205,0);
			}else{
				
				postProcessingThread.theTextRenderer.drawText(screen_width-54, 3, currentPowerConsumption/50 + "", screen, 255,0,0);
				int l = (currentPowerConsumption/50 + "").length();
				postProcessingThread.theTextRenderer.drawText(screen_width-54+l*7, 3, "/"+currentPowerLevel/50, screen, 0,205,0);
			}
		}
		
	}
	
	public void drawIcon(int[] screen, int startIndex, int iconIndex){
		int screenIndex = 0;
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 16; j++){
				screenIndex = startIndex + j + (i+1)*screen_width;
				pixel=(iconImages[iconIndex][j + i*16]&MASK7Bit)+(screen[screenIndex]&MASK7Bit);
				overflow=pixel&0x1010100;
				overflow=overflow-(overflow>>8);
				screen[screenIndex] = overflow|pixel;
			}
		}
	}

	public void calculateOnScreenPlayerMoney(int playerMoney){
		
		int difference = playerMoney - onScreenPlayerMoney;
		
		if(difference > 1000)
			onScreenPlayerMoney+=73;
		else if(difference > 23 && difference <= 750)
			onScreenPlayerMoney+=23;
		else if(difference <= 23 && difference > 0)
			onScreenPlayerMoney+=difference;
		else
			onScreenPlayerMoney+=difference;
		
	}
	
	public void drawSideBarInfo(int[] screen, int[] sideBarInfo){
		int displayInfo, iconTextureIndex, progress, text;
		
		int powerPlantInfo = 1;
		int deployMCVInfo = 2;
		int refineryInfo = 3;
		int factoryInfo = 4;
		int lightTankInfo = 5;
		int rocketTankInfo = 6;
		int harvesterInfo = 7;
		int droneInfo = 8;
		int communicationCenterInfo = 9;
		int MCVInfo = 10;
		int stealthTankInfo = 11;
		int gunTurretInfo = 12;
		int repairBuildingInfo = 13;
		int missileTurretInfo = 14;
		int harvesterResearchInfo = 15;
		int missileTurretResearchInfo = 16;
		int rapidfireTooltipInfo = 17;
		int techCenterInfo = 18;
		int heavyTankInfo = 19;
		int lightTankResearchInfo = 20;
		int rocketTankResearchInfo = 21;
		int stealthTankResearchInfo = 22;
		int heavyTankResearchInfo = 23;
		
		for(int i = 0; i < 9; i ++){
			if(sideBarInfo[i] != -1){
				displayInfo = (sideBarInfo[i] >> 24) & 255;
				iconTextureIndex = (sideBarInfo[i] >> 16) & 255;
				progress = (sideBarInfo[i] >> 8) & 255;
				text = sideBarInfo[i] & 255;
				
				if(progress == 254){
					drawIconImage(xStart[i],yStart[i],screen,iconImages_dark[iconTextureIndex]);
				}else if(progress >= 240){
					drawIconImage(xStart[i],yStart[i],screen,iconImages[iconTextureIndex]);
				}else if(progress == 0){
					drawIconImage(xStart[i],yStart[i],screen,iconImages_dark[iconTextureIndex]);
				}else{
					drawIconImage(xStart[i],yStart[i],screen,iconImages[iconTextureIndex], iconImages_dark[iconTextureIndex], progress);
				}
				
				if(text == 1){
					postProcessingThread.theTextRenderer.drawFlashingText(xStart[i] + 5, yStart[i] + 11, "Ready", screen);
				}else if(text > 100){
					postProcessingThread.theTextRenderer.drawText(xStart[i] + 5, yStart[i] + 0, ""+ (text - 100), screen, 255,255,255);
				}
				
				//draw display infos
				if(displayInfo == powerPlantInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129, 32, 128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Power Plant", screen, 255,255,200);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:500", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "+10", screen, 0,205,0);
					
				}else if(displayInfo == deployMCVInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-152, 129,16,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "Deploy MCV", screen, 255,255,255);
				}else if(displayInfo == refineryInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Refinery", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1200", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-3", screen, 255,0,0);
				}else if(displayInfo == factoryInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Factory", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1400", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-4", screen, 255,0,0);
				}else if(displayInfo == lightTankInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Light Tank", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:300", screen, 245,197,51);
				}else if(displayInfo == rocketTankInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Rocket Tank", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:450", screen, 245,197,51);
				}else if(displayInfo == harvesterInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Harvester", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:800", screen, 245,197,51);
				}else if(displayInfo == droneInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Repair Drone", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:250", screen, 245,197,51);
				}else if(displayInfo == communicationCenterInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "COMM. Centre", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1000", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-5", screen, 255,0,0);
				}else if(displayInfo == MCVInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "MCV", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1700", screen, 245,197,51);
				}else if(displayInfo == stealthTankInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Stealth Tank", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:600", screen, 245,197,51);
				}else if(displayInfo == gunTurretInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Gun Turret", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:400", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-2", screen, 255,0,0);
				}else if(displayInfo == repairBuildingInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-152, 129,16,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "Repair Structure", screen, 255,255,255);
				}else if(displayInfo == missileTurretInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Missile Turret", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:750", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-4", screen, 255,0,0);
				}else if(displayInfo == harvesterResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height - 184, 129,48,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "Research harvester", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-128, screen_height-168, "movement speed.", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1200", screen, 245,197,51);
				}else if(displayInfo == missileTurretResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-200, 129,64,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-200, "Research rapidfire", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "ability for the", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "missile turret.", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1500", screen, 245,197,51);
				}else if(displayInfo == rapidfireTooltipInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-200, 129,64,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-200, "Fire missile more", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "rapidly, but draws", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "addtional power.", screen, 255,255,255);
					drawIcon(screen, screen_width-129 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-113, screen_height-152, "-3", screen, 255,0,0);
				}else if(displayInfo == techCenterInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Tech Center", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1500", screen, 245,197,51);
					drawIcon(screen, screen_width-78 + (screen_height-152)*screen_width, 4);
					postProcessingThread.theTextRenderer.drawText(screen_width-62, screen_height-152, "-8", screen, 255,0,0);
				}else if(displayInfo == heavyTankInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-168, 129,32,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "Heavy Tank", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1100", screen, 245,197,51);
				}else if(displayInfo == lightTankResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height - 184, 129,48,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "Research light", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-128, screen_height-168, "tank's fire range", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:1500", screen, 245,197,51);
				}else if(displayInfo == rocketTankResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-200, 129,64,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-200, "Research rocket", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "tank's damage", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "against building", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:2000", screen, 245,197,51);
				}else if(displayInfo == stealthTankResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-200, 129,64,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-200, "Research stealth", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "tank's ability to", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "hit multiple units", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:2000", screen, 245,197,51);
				}else if(displayInfo == heavyTankResearchInfo){
					drawInfoBackGround(screen, screen_width-131, screen_height-200, 129,64,128);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-200, "Research heavy", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height - 184, "tank's ability to", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-168, "self repair", screen, 255,255,255);
					postProcessingThread.theTextRenderer.drawText(screen_width-129, screen_height-152, "$:2500", screen, 245,197,51);
				}
				
				
				if((iconTextureIndex == 14 || iconTextureIndex == 17)  && text == 32){
					
					int d = 0;
					if(iconTextureIndex == 17)
						d = screen_width*44;
					
					int startIndex1 = (int)(mainThread.gameFrame/1.5)%74;
					int startIndex2 = ((int)(mainThread.gameFrame/1.5)%74 + 18)%74;
					int startIndex3 = ((int)(mainThread.gameFrame/1.5)%74 + 37)%74;
					int startIndex4 = ((int)(mainThread.gameFrame/1.5)%74 + 55)%74;
					
					int markerR = 255;
					int markerG = 255;
					int markerB =64;
					int pixelColor;
					int pixelR, pixelG, pixelB;
					int p = screen_width + 1;
					
					for(int j = 0; j < 16; j++){
						int index = autoRepairMark[(startIndex1+j)%74] -d;
						float t = 0.9375f - 0.0625f*j;
						pixelColor = screen[index];
						pixelR = (pixelColor & 0xff0000) >> 16;
						pixelG = (pixelColor & 0xff00) >> 8;	
						pixelB = (pixelColor & 0xff);
						
						pixelColor = (int)(pixelR*t + markerR*(1-t)) << 16 | (int)(pixelG*t + markerG*(1-t)) << 8 | (int)(pixelB*t + markerB*(1-t));
						
						screen[index] = pixelColor;
						screen[index+1] = pixelColor;
						screen[index + screen_width] = pixelColor;
						screen[index + p] = pixelColor;
					}
					
					
					for(int j = 0; j < 16; j++){
						int index = autoRepairMark[(startIndex2+j)%74] - d;
						float t = 0.9375f - 0.0625f*j;
						pixelColor = screen[index];
						pixelR = (pixelColor & 0xff0000) >> 16;
						pixelG = (pixelColor & 0xff00) >> 8;	
						pixelB = (pixelColor & 0xff);
						
						pixelColor = (int)(pixelR*t + markerR*(1-t)) << 16 | (int)(pixelG*t + markerG*(1-t)) << 8 | (int)(pixelB*t + markerB*(1-t));
						
						screen[index] = pixelColor;
						screen[index+1] = pixelColor;
						screen[index + screen_width] = pixelColor;
						screen[index + p] = pixelColor;
					}
					
					for(int j = 0; j < 16; j++){
						int index = autoRepairMark[(startIndex3+j)%74] - d;
						float t = 0.9375f - 0.0625f*j;
						pixelColor = screen[index];
						pixelR = (pixelColor & 0xff0000) >> 16;
						pixelG = (pixelColor & 0xff00) >> 8;	
						pixelB = (pixelColor & 0xff);
						
						pixelColor = (int)(pixelR*t + markerR*(1-t)) << 16 | (int)(pixelG*t + markerG*(1-t)) << 8 | (int)(pixelB*t + markerB*(1-t));
						
						screen[index] = pixelColor;
						screen[index+1] = pixelColor;
						screen[index + screen_width] = pixelColor;
						screen[index + p] = pixelColor;
					}
					
					for(int j = 0; j < 16; j++){
						int index = autoRepairMark[(startIndex4+j)%74] - d;
						float t = 0.9375f - 0.0625f*j;
						pixelColor = screen[index];
						pixelR = (pixelColor & 0xff0000) >> 16;
						pixelG = (pixelColor & 0xff00) >> 8;	
						pixelB = (pixelColor & 0xff);
						
						pixelColor = (int)(pixelR*t + markerR*(1-t)) << 16 | (int)(pixelG*t + markerG*(1-t)) << 8 | (int)(pixelB*t + markerB*(1-t));
						
						screen[index] = pixelColor;
						screen[index+1] = pixelColor;
						screen[index + screen_width] = pixelColor;
						screen[index + p] = pixelColor;
					}
				}
			}
		}
	}
	
	public void drawInfoBackGround(int[] screen, int xPos, int yPos, int w, int h, int alpha){
		int start = xPos + yPos*screen_width;
		if(alpha == 128){
			for(int i = 1; i < w - 1; i++){
				screen[start -screen_width + i] = (screen[start -screen_width + i]&0xFEFEFE)>>1;
			}
					
			for(int i = 0; i < h; i++){
				for(int j = 0; j < w; j++){
					screen[start + j + i*screen_width] = (screen[start + j + i*screen_width]&0xFEFEFE)>>1;
				}
			}
			
			for(int i = 1; i < w - 1; i++)
				screen[start + screen_width*h + i] = (screen[start + screen_width*h + i]&0xFEFEFE)>>1;
		}else{
			for(int i = 1; i < w - 1; i++){
				int value = screen[start -screen_width + i];
				screen[start -screen_width + i] = (((((value&0xff0000) >> 16) * alpha) >> 8) << 16) | (((((value&0xff00) >> 8) * alpha) >> 8) << 8) | (((((value&0xff)) * alpha) >> 8));
				
				
			}
					
			for(int i = 0; i < h; i++){
				for(int j = 0; j < w; j++){
					int value = screen[start + j + i*screen_width];
					screen[start + j + i*screen_width] = (((((value&0xff0000) >> 16) * alpha) >> 8) << 16) | (((((value&0xff00) >> 8) * alpha) >> 8) << 8) | (((((value&0xff)) * alpha) >> 8));
				}
			}
			
			for(int i = 1; i < w - 1; i++){
				int value = screen[start + screen_width*h + i];
				screen[start + screen_width*h + i] =  (((((value&0xff0000) >> 16) * alpha) >> 8) << 16) | (((((value&0xff00) >> 8) * alpha) >> 8) << 8) | (((((value&0xff)) * alpha) >> 8));
			}
		}
		
	}
	
	public void prepareProgressBitmaps(){
		double theta = Math.PI/120;
		for(int i = 0; i < 240; i++){
			
			double angle = i*theta;
			
			for(int j = 0; j < 44*44; j++){
				double x = j%44 -22;
				double y = 22 - j/44 ;
				if(angle > (Math.PI*2.5 - Math.atan2(y, x))%(Math.PI*2))
					progressBitmaps[i][j] = true;
				
			}	
		}
	}
	
	public void drawIconImage(int xPos, int yPos, int[] screen, int[] iconImage){
		int start = xPos + yPos * screen_width;
		for(int i = 0; i < 44; i++){
			for(int j = 0; j < 44; j++){
				screen[start + j + i*screen_width] = iconImage[j + i*44];
			}
		}
		
	}
	
	public void drawIconImage(int xPos, int yPos, int[] screen, int[] iconImage, int[] iconImages_dark, int progress){
		int start = xPos + yPos * screen_width;
		for(int i = 0; i < 44; i++){
			for(int j = 0; j < 44; j++){
				if(progressBitmaps[progress][j + i*44])
					screen[start + j + i*screen_width] = iconImage[j + i*44];
				else
					screen[start + j + i*screen_width] = iconImages_dark[j + i*44];
			}
		}
	}
	
	public void drawBackground(int[] screen){
		int start = (screen_height-131)*screen_width + screen_width-131;
		for(int i = 0; i< 128; i++){
			for(int j = 0; j < 128; j++){
				screen[start + j + i*screen_width] = 0x666655;
			}
		}
		
	}
	
	public void drawFrame(int[] screen){
		int color1 = 0xa0a0a0;
		int color2 = 0xe0e0e0;
		
		int shift= screen_width-134;
		
		int start = (screen_height-134)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color1;
		}
		start = (screen_height-133)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = (screen_height-132)*screen_width + shift;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		
		start = (screen_height-90)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color1;
		}
		start = (screen_height-89)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = (screen_height-88)*screen_width + shift;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		
		start = (screen_height-46)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color1;
		}
		start = (screen_height-45)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = (screen_height-44)*screen_width + shift;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		
		start = (screen_height-3)*screen_width + shift;
		for(int x = 2; x < 132; x++){
			screen[start + x] = color1;
		}
		start = (screen_height-2)*screen_width + shift;
		for(int x = 1; x < 133; x++){
			screen[start + x] = color2;
		}
		start = (screen_height-1)*screen_width + shift;
		for(int x = 0; x < 134; x++){
			screen[start + x] = color1;
		}
		
		
		
		
		
		start = (screen_height-134)*screen_width + 133 + shift;
		for(int y = 1; y < 132; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-133)*screen_width + 132 + shift;
		for(int y = 0; y < 132; y++){
			screen[start + y*screen_width] = color2;
		}
		
		start = (screen_height-132)*screen_width + 131 + shift;
		for(int y = 0; y < 131; y++){
			screen[start + y*screen_width] = color1;
		}
		
		
		start = (screen_height-134)*screen_width + shift;
		for(int y = 1; y < 132; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-133)*screen_width + 1 + shift;
		for(int y = 0; y < 132; y++){
			screen[start + y*screen_width] = color2;
		}
		
		start = (screen_height-132)*screen_width + 2 + shift;
		for(int y = 0; y < 131; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-134)*screen_width + 42 + shift;
		for(int y = 1; y < 132; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-133)*screen_width + 43 + shift;
		for(int y = 0; y < 132; y++){
			screen[start + y*screen_width] = color2;
		}
		
		start = (screen_height-132)*screen_width + 44 + shift;
		for(int y = 0; y < 131; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-134)*screen_width + 86 + shift;
		for(int y = 1; y < 132; y++){
			screen[start + y*screen_width] = color1;
		}
		
		start = (screen_height-133)*screen_width + 87 + shift;
		for(int y = 0; y < 132; y++){
			screen[start + y*screen_width] = color2;
		}
		
		start = (screen_height-132)*screen_width + 88 + shift;
		for(int y = 0; y < 131; y++){
			screen[start + y*screen_width] = color1;
		}
	}
	
	
	
	
	public void loadTexture(String imgName, int[] buffer, int width, int height, int[] buffer_dark){
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
		
		for(int i = 0; i < buffer_dark.length; i++){
			buffer_dark[i] = (buffer[i]&0xFEFEFE)>>1;
		}
	}
}
