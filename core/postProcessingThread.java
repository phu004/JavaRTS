package core;

import entity.*;
import gui.MiniMap;
import gui.SideBar;
import gui.confirmationIcon;
import gui.gameMenu;
import particles.explosion;
import particles.helix;
import particles.smokeParticle;

//this class handles all the post processing effect
public class postProcessingThread implements Runnable{

	public static int[] currentScreen;
	public static int[] currentZbuffer;
	public static int[][] currentSelectedUnitsInfo; 
	public static int[][] unitInfoTable;
	public static float[][] visionPolygonInfo;
	public static float[][] explosionInfo;
	public static float[][] helixInfo;
	public static int visionPolygonCount;
	public static int visibleUnitCount;
	public static int explosionCount;
	public static int helixCount;
	
	public static boolean[] minimapBitmap;
	
	public static float[][] smokeEmmiterList;
	public static int smokeEmmiterCount;
	
	public static int[][] unitsForMiniMap;
	public static int unitsForMiniMapCount;
	
	public static MiniMap theMiniMap;
	public static SideBar theSideBar;
	
	private boolean isWorking;
	public static int sleepTime;
	
	public static byte[] fogOfWarBuffer;
	public static byte[] fogOfWarBuffer2;
	
	public static byte[] shadowBitmap;
	public static byte[] smoothedShadowBitmap;
	public static short[] displacementBuffer;
	public static int[] offScreen;
	
	//2 arrays that define the scan lines of the polygon
	public static int[] xLeft = new int[512], xRight = new int[512];
	public static int[] xMin = new int[512], xMax = new int[512];
	
	public static vector cameraPosition = new vector(0,0,0);
	public static float sinXZ,cosXZ,sinYZ,cosYZ ;
	public static int cameraXZAngle;
	
	public static Turn2DTo3DFactory my2Dto3DFactory;

	public static explosion[] explosions;
	
	public static smokeParticle[] smokeParticles;
	public int currentParticleIndex;
	
	public static helix[] railgunHelix;
	public int currentHelix;
	
	public static int[] sideBarInfo;
	
	public static confirmationIcon theConfirmationIcon;
	
	public static double[] confirmationIconInfo;
	
	public static textRenderer theTextRenderer;
	
	public static boolean gamePaused, gameStarted, gameEnded;
	
	
	//A pool of vectors which will be used for vector arithmetic
	public static vector 
		tempVector1 = new vector(0,0,0),
		tempVector2 = new vector(0,0,0),
		tempVector3 = new vector(0,0,0),
		tempVector4 = new vector(0,0,0);
	
	//the polygon that post processing filter is working on
	public static polygon3D poly;
	
	//these variables will represent their equivalents in the polygon3D class during rasterization
	public static vector[] vertex2D;

	
	//temporary variables that will be used in texture mapping
	public static float aDotW, bDotW, cDotW, cDotWInverse, w, textureHeight, textureWidth;
	public static int BigX, BigY, d_x, d_y, k, X1, Y1, BigDx, BigDy, dx, dy, dz, X, Y, textureIndex,  temp, temp1, temp2, temp3, r,g,b, scale, yOffset, xOffset, x_right, x_left, z_left, z_right,  start, end, teamNo, type;
	
		
	//the amount of vertex after clipping
	public static int visibleCount;
	
	//the number of avaliable credit for  player commander
	public static int playerMoney;
	
	//the  power status for player commander
	public static int playerPowerStatus;
	
	//frame counter
	public static int frameIndex;
	
	public static vector lightReflect;
	public static vector eyeDirection;
	
	public static void init(){
		//create font bitmaps
		theTextRenderer = new textRenderer();
		theTextRenderer.init();
		
		
		fogOfWarBuffer = new byte[512 * 768];
		fogOfWarBuffer2 = new byte[512 * 768];
		
		smoothedShadowBitmap = new byte[512 * 768];
		
		offScreen = new int[512*768];
		
		//init minimap hud
		theMiniMap = new MiniMap();
		theMiniMap.init();
		
		//init sidebar interface
		theSideBar = new SideBar();
		theSideBar.init();
		
		
		unitInfoTable = new int[201][4];
		
		//							 Max health		      Health_bar Length	     Health_bar Xpos	Health_bar yPos       
		unitInfoTable[0] = new int[]{lightTank.maxHP,            44,                  -22,                -25};
		unitInfoTable[1] = new int[]{rocketTank.maxHP,           44,                  -22,                -36};
		unitInfoTable[2] = new int[]{harvester.maxHP,            58,                  -29,                -46}; 
		unitInfoTable[3] = new int[]{constructionVehicle.maxHP,  58,                  -29,                -40}; 
		unitInfoTable[6] = new int[]{stealthTank.maxHP,          44,                  -22,                -30}; 
		unitInfoTable[7] = new int[]{heavyTank.maxHP,            58,                  -29,                -37}; 
		unitInfoTable[101] = new int[]{powerPlant.maxHP,         88,                  -37,                -80};  
		unitInfoTable[102] = new int[]{refinery.maxHP,           132,                 -65,                -130};   
		unitInfoTable[103] = new int[]{goldMine.maxHP,           100,                 -49,                -80};   
		unitInfoTable[104] = new int[]{constructionYard.maxHP,   132,                 -65,                -130};   
		unitInfoTable[105] = new int[]{factory.maxHP,            132,                 -65,                -130};   
		unitInfoTable[106] = new int[]{communicationCenter.maxHP, 88,                 -37,                -70};  
		unitInfoTable[107] = new int[]{techCenter.maxHP,          88,                 -37,                -115};  
		unitInfoTable[199] = new int[]{missileTurret.maxHP, 	  44,                 -22,                -20};
		unitInfoTable[200] = new int[]{gunTurret.maxHP, 		  44,                 -22,                -35};
		
		my2Dto3DFactory = new Turn2DTo3DFactory();
		my2Dto3DFactory.init();
		
		explosions = new explosion[200];
		for(int i = 0; i < 200; i++)
			explosions[i] = new explosion();
		
		
		smokeParticles = new smokeParticle[2000]; 
		for(int i = 0; i < 2000; i++)
			smokeParticles[i] = new smokeParticle();
		
		railgunHelix = new helix[1500];
		for(int i = 0; i < 1500; i++)
			railgunHelix[i] = new helix();
		
		theConfirmationIcon = new confirmationIcon();
		
		frameIndex =  0;
		
		lightReflect = new vector(0,0,0);
		eyeDirection = new vector(0,0,0);
	}
	
	public void run(){
		
		while(true){
			synchronized (this) {
				try {	
					synchronized (mainThread.PPT_Lock) {
						mainThread.PPT_Lock.notify();
						isWorking = false;
					}
					
					long time = System.currentTimeMillis();
					wait();
					sleepTime = (int)(System.currentTimeMillis() - time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isWorking = true;
			}
			
			if(!gamePaused)
				doPostProcesssing();
			
			gameMenu.updateAndDraw(currentScreen, gameStarted, gamePaused);
			
			frameIndex++;
			
		}
	}
	
	
	public boolean isWorking(){
		if(isWorking)
			return true;
		else
			return false;
	}
	
	

	
	public  void doPostProcesssing(){	
		int ObjectType, groupNo, level,  maxHealth, healthBarLength, remainingHealth, xPos, yPos, selectAreaWidth, selectAreaHeight, color = 0;
				
		
		//load explosion animation instances 
		float[] tempFloat;
		for(int i = 0, j = 0; i < 200; i ++){
			if(!explosions[i].isInAction){
				if(j < explosionCount){
					tempFloat = explosionInfo[j];
					explosions[i].setActive(tempFloat[0], tempFloat[1], tempFloat[2], tempFloat[3], (int)tempFloat[4], (int)tempFloat[5], (int)tempFloat[6], tempFloat[7]);					
					j++;
				}else {
					break;
				}
			}
		}
		
		
		//sort explosion according to its size
		//for(int i = 1; i < 200; i++){
		//	for(int j = 0; j <200 - i; j++){ 
		//		if(explosions[j].size > explosions[j+1].size){
		//			explosion temp = explosions[j+1];
		//			explosions[j+1] = explosions[j];
		//			explosions[j] = temp;
		//		}
		//	}
		//}
		
	
		//blur shadow bitmap to create an illusion of antialiased shadow
		blurShadow();
		
		
		//draw explosion animations
		for(int i = 0; i < 200; i++)
			explosions[i].updateAndDrawExplosionAura();
		
		
	
		//blend shadow bitmap with screen buffer
		blendShadow();
		
		
		//apply distortion map
		int xMap;
		int yMap;
		int distortionIndex;
		
		for(int i = 0; i < 393216; i++){
			if(displacementBuffer[i] != 31){
				xMap = ((displacementBuffer[i]&992) >> 5) - 16;
				yMap = (displacementBuffer[i]&31) - 16;
				distortionIndex = i + xMap + yMap*768;
				if(distortionIndex > 0 && distortionIndex < 393216 ){
					if(currentZbuffer[i] - currentZbuffer[distortionIndex] > -1000000)
						offScreen[i] = currentScreen[distortionIndex];
					else
						offScreen[i] = currentScreen[i];
				}
			}
		}
		int r1, r2, r3, r4,r5, g1,g2,g3,g4,g5,b1,b2,b3,b4,b5;
		
		int c = (30/2) << 16 |  (30)<<8| 31;
		
		
		
	
		lightReflect.set(0,0,1);
		lightReflect.rotate_YZ(230);
		lightReflect.rotate_XZ(13);
		lightReflect.y*=-1;
		lightReflect.x*=-1;
		lightReflect.set(lightReflect.x, -lightReflect.y, lightReflect.z);
		lightReflect.rotate_XZ(camera.XZ_angle);
		lightReflect.rotate_YZ(camera.YZ_angle); 
		
		
		int SpriteValue = 0;
		int MASK7Bit = 0xFEFEFF;
		int overflow = 0;
		int pixel = 0;
		
		for(int i = 768; i < 392448; i++){
			if(displacementBuffer[i] != 31){
				
				r1 = (offScreen[i + 1]&0xff0000) >> 16;
				r2 = (offScreen[i - 767]&0xff0000) >> 16;
				r3 = (offScreen[i - 768]&0xff0000) >> 16;
				r4 = (offScreen[i - 1]&0xff0000) >> 16;
			
			
				g1 = (offScreen[i + 1]&0xff00) >> 8;
				g2 = (offScreen[i - 767]&0xff00) >> 8;
				g3 = (offScreen[i - 768]&0xff00) >> 8;
				g4 = (offScreen[i]&0xff00) >> 8;
			
			
				b1 = (offScreen[i + 1]&0xff);
				b2 = (offScreen[i - 767]&0xff);
				b3 = (offScreen[i - 768]&0xff);
				b4 = (offScreen[i]&0xff);
				
			
				
				
				currentScreen[i] = (((r1 + r2 + r3 + r4)>>3) << 16 | ((g1 + g2 + g3 + g4)>>3) << 8 | ((b1 + b2 + b3 + b4)>>3 )) + c;
				
				yMap = (displacementBuffer[i]&64512) >> 10;
				
				
				if(yMap > 0){
					eyeDirection.set(-i%768+384, -256 + i/768, -650);
					eyeDirection.unit();
					float I = eyeDirection.dot(lightReflect);
				
					if(I > 0.985){
						int I_ = (int)((I-0.985) *24000);
						
						yMap = yMap * I_ /90;
						
						SpriteValue = 0x010101*yMap;
						
						pixel=(SpriteValue&MASK7Bit)+(currentScreen[i]&MASK7Bit);
						overflow=pixel&0x1010100;
						overflow=overflow-(overflow>>8);
						currentScreen[i] = overflow|pixel;
					}
					
				}
				
				displacementBuffer[i] = 31;
			}else{
				
				
			}
			
		}
		
		if(gameStarted) {
			//create helix particles that are spawned by stealth tank's railgun trail.
			for(int i = 0; i < helixCount; i++){
				tempFloat = helixInfo[i];
				railgunHelix[currentHelix].setActive(tempFloat[0], tempFloat[1], tempFloat[2], (int)tempFloat[3]);
				currentHelix++;
				currentHelix%=1500;
			}
			
			//draw helix particles
			for(int i = 0; i < 1500; i++){
				 if(railgunHelix[i].isInAction)
					 railgunHelix[i].updateAndDraw();
			}
			
			
			//draw explosion sprite
			for(int i = 0; i < 200; i++)
				explosions[i].drawExplosionSprite();
			
			
		
			//create smoke particles
			for(int i = 0; i < smokeEmmiterCount; i++){
				tempFloat = smokeEmmiterList[i];
				smokeParticles[currentParticleIndex].setActive(tempFloat[0], tempFloat[1], tempFloat[2],  tempFloat[3], (int)tempFloat[4] ,  (int)tempFloat[5], tempFloat[6]);
				currentParticleIndex++;
				currentParticleIndex%=2000;
			}
			
			
			//draw smoke particles
			for(int i = 0; i < 2000; i++){ 
				if(smokeParticles[i].isInAction)
					smokeParticles[i].updateAndDraw();
			}
			
			
			
			
			//draw health bar/Group info/unit level  for every selected unit 
			for(int i = 0; i < 100; i++){
				if(currentSelectedUnitsInfo[i][0] != -1){
					ObjectType = (currentSelectedUnitsInfo[i][0] & 0xff);
					groupNo = ((currentSelectedUnitsInfo[i][0] & 0xff00) >> 8);
					level =  ((currentSelectedUnitsInfo[i][0] & 0xff0000) >> 16);
					maxHealth = unitInfoTable[ObjectType][0];
					healthBarLength = unitInfoTable[ObjectType][1];
					xPos = currentSelectedUnitsInfo[i][1] + unitInfoTable[ObjectType][2];
					yPos = currentSelectedUnitsInfo[i][2] + unitInfoTable[ObjectType][3];
					remainingHealth = healthBarLength * currentSelectedUnitsInfo[i][4] / maxHealth;
					
					//draw group info
					if(groupNo != 255){
						theTextRenderer.drawText_outline(xPos, yPos + 3, ""+(groupNo+1), currentScreen, 0xffffff, 0);
					}
					if(level != 0){
						theTextRenderer.drawStarCharacter(xPos + healthBarLength - 13, yPos + 5, level, currentScreen, 0xffff33, 0);
					}
					
					if(remainingHealth <= 2 && remainingHealth != 0)
						remainingHealth = 2;
					
					if(ObjectType != 103){
						if(yPos > 0 && yPos < 512){
							
							if(xPos >= 0 && xPos < 768)
								currentScreen[xPos + yPos*768] = (currentScreen[xPos + yPos*768]&0xFEFEFE)>>1;
							for(int j = xPos+1;  j < xPos + healthBarLength-1; j++){
								if(j < 0 || j >= 768)
									continue;
								currentScreen[j + yPos*768] = 0;
							}
							if(xPos + healthBarLength-1 >= 0 && xPos + healthBarLength-1 < 768)
								currentScreen[xPos + healthBarLength-1 + yPos*768] = (currentScreen[xPos + healthBarLength-1 + yPos*768]&0xFEFEFE)>>1;	
						}
						
						if((float)remainingHealth / healthBarLength > 0.5)
							color = 0xdd00;
						else if((float)remainingHealth / healthBarLength > 0.25)
							color = 0xdddd00;
						else 
							color = 0xdd0000;
						
						
						for(int k = 0; k < 2; k++){
							yPos++;
							
							if(yPos > 0 && yPos < 512){
								for(int j = xPos;  j < xPos + healthBarLength; j++){
									if(j < 0 || j >= 768)
										continue;
									if(j < xPos + remainingHealth)
										currentScreen[j + yPos*768] = color;
									else
										currentScreen[j + yPos*768] = (currentScreen[j + yPos*768]&0xFEFEFE)>>1;
								}
								if(xPos > 0 && xPos < 768)
									currentScreen[xPos + yPos*768] = 0;
								if(xPos + healthBarLength -1 >0 && xPos + healthBarLength - 1 < 768)
									currentScreen[xPos + healthBarLength -1 + yPos*768] = 0;
								
							}
						}
						
						yPos++;
						if(yPos > 0 && yPos < 512){
							if(xPos >= 0 && xPos < 768)
								currentScreen[xPos + yPos*768] = (currentScreen[xPos + yPos*768]&0xFEFEFE)>>1;
							for(int j = xPos+1;  j < xPos + healthBarLength-1; j++){
								if(j < 0 || j >= 768)
									continue;
								currentScreen[j + yPos*768] = 0;
							}
							if(xPos + healthBarLength-1 >= 0 && xPos + healthBarLength-1 < 768)
								currentScreen[xPos + healthBarLength-1 + yPos*768] = (currentScreen[xPos + healthBarLength-1 + yPos*768]&0xFEFEFE)>>1;
						}
					}
					
					//draw progress bar if appliable
					if(currentSelectedUnitsInfo[i][5] >=0){
						int progress = healthBarLength * currentSelectedUnitsInfo[i][5] / 100;
						
						if(yPos > 0 && yPos < 512){
							if(xPos >= 0 && xPos < 768)
								currentScreen[xPos + yPos*768] = (currentScreen[xPos + yPos*768]&0xFEFEFE)>>1;
							for(int j = xPos+1;  j < xPos + healthBarLength-1; j++){
								if(j < 0 || j >= 768)
									continue;
								currentScreen[j + yPos*768] = 0;
							}
							if(xPos + healthBarLength-1 >= 0 && xPos + healthBarLength-1 < 768)
								currentScreen[xPos + healthBarLength-1 + yPos*768] = (currentScreen[xPos + healthBarLength-1 + yPos*768]&0xFEFEFE)>>1;	
						}
						
						
						color = 0xd0b000;
						
						
						for(int k = 0; k < 2; k++){
							yPos++;
							
							if(yPos > 0 && yPos < 512){
								for(int j = xPos;  j < xPos + healthBarLength; j++){
									if(j < 0 || j >= 768)
										continue;
									if(j < xPos + progress)
										currentScreen[j + yPos*768] = color;
									else
										currentScreen[j + yPos*768] = (currentScreen[j + yPos*768]&0xFEFEFE)>>1;
								}
								if(xPos > 0 && xPos < 768)
									currentScreen[xPos + yPos*768] = 0;
								if(xPos + healthBarLength -1 >0 && xPos + healthBarLength - 1 < 768)
									currentScreen[xPos + healthBarLength -1 + yPos*768] = 0;
								
							}
						}
						
						yPos++;
						if(yPos > 0 && yPos < 512){
							if(xPos >= 0 && xPos < 768)
								currentScreen[xPos + yPos*768] = (currentScreen[xPos + yPos*768]&0xFEFEFE)>>1;
							for(int j = xPos+1;  j < xPos + healthBarLength-1; j++){
								if(j < 0 || j >= 768)
									continue;
								currentScreen[j + yPos*768] = 0;
							}
							if(xPos + healthBarLength-1 >= 0 && xPos + healthBarLength-1 < 768)
								currentScreen[xPos + healthBarLength-1 + yPos*768] = (currentScreen[xPos + healthBarLength-1 + yPos*768]&0xFEFEFE)>>1;
						}
					}
				}
			}
			
			
			
			//reset fogOfWarBuffer
			fogOfWarBuffer[0] = 0;
			for(int i = 1; i < 393216; i+=i){
				System.arraycopy(fogOfWarBuffer, 0, fogOfWarBuffer, i, 393216 - i >= i ? i : 393216 - i);
				
			}
			
			for(int i = 0; i < 512; i++){
				xMin[i] = 378;
				xMax[i] = 378;
				
			}
			
			float[] list;
			//shaffule vision polygons
			for(int i = 0; i < 400; i++){
				temp = (gameData.getRandom() * visionPolygonCount) >> 10;
				temp1 = (gameData.getRandom() * visionPolygonCount) >> 10;
					
				list = visionPolygonInfo[temp];
				visionPolygonInfo[temp] = visionPolygonInfo[temp1];
				visionPolygonInfo[temp1] = list;
			}
			
			//update vision polygons
			for(int i = 0; i < visionPolygonCount; i++){
				tempVector1.set(visionPolygonInfo[i][1], visionPolygonInfo[i][2], visionPolygonInfo[i][3]);
				if(visionPolygonInfo[i][0] != 0){
					poly = mainThread.theAssetManager.visionPolygon[1];
				}else{
					poly = mainThread.theAssetManager.visionPolygon[(int)visionPolygonInfo[i][4]];
				}
				tempVector1.subtract(poly.centre);
				for(int j = 0; j < 48; j++)
					poly.vertex3D[j].add(tempVector1);
				
				
				poly.centre.add(tempVector1);
				poly.update_visionPolygon();
				
				rasterize(poly);	
			}
			
			
			
			//blur fog of war buffer, use cross shaped kernel
			int radius = 16;
			int radiusBit = 5;
			int destIndex = 0;
			int index = 0;
			for(int i = 0; i < 512; i++){
				//init the first element in the row
				temp = 0;
				destIndex = i + 512 * 767 ;
				
				for(int j = 0; j < radius -1; j++){
					temp+=fogOfWarBuffer[index + j];
				}
				temp+=43*radius;
				fogOfWarBuffer2[destIndex] =  (byte)(temp >> radiusBit);
				index++;
				destIndex-=512;
				
				for(int j = 1; j < radius; j++, index++, destIndex -=512){
					temp = temp + fogOfWarBuffer[index + radius -2] - 43;
					fogOfWarBuffer2[destIndex] =  (byte)(temp >> radiusBit);
				}
				
				for(int j = radius; j < 768 - radius; j++, index++, destIndex -=512){
					temp = temp + fogOfWarBuffer[index + radius -2] - fogOfWarBuffer[index - radius];
					fogOfWarBuffer2[destIndex] =  (byte)(temp >> radiusBit);
					
				}
				for(int j = 0; j < radius; j++, index++, destIndex -=512){
					temp = temp - fogOfWarBuffer[index - radius] + 43;
					fogOfWarBuffer2[destIndex] =  (byte)(temp >> radiusBit);
				}
			}
			
			destIndex = 0;
			index = 0;
			for(int i = 0; i < 768; i++){
				//init the first element in the row
				temp = 0;
				destIndex = 767 - i;
				
				for(int j = 0; j < radius -1 ; j++){
					temp+=fogOfWarBuffer2[index + j];
				}
				temp+=43*radius;
				fogOfWarBuffer[destIndex] =  (byte)(temp >> radiusBit);
				index++;
				destIndex+=768;
				
				for(int j = 1; j < radius; j++, index++, destIndex +=768){
					temp = temp + fogOfWarBuffer2[index + radius -2] - 43;
					fogOfWarBuffer[destIndex] =  (byte)(temp >> radiusBit);
				}
				
				for(int j = radius; j < 512 - radius; j++, index++, destIndex +=768){
					temp = temp + fogOfWarBuffer2[index + radius -2] - fogOfWarBuffer2[index - radius];
					fogOfWarBuffer[destIndex] =  (byte)(temp >> radiusBit);
				}
				for(int j = 0; j < radius; j++, index++, destIndex +=768){
					temp  = temp - fogOfWarBuffer2[index - radius] + 43;
					fogOfWarBuffer[destIndex] =  (byte)(temp >> radiusBit);
				}
			}
			
			
			//blend fog of war to the frame buffer
			for(int i = 0; i < 512 * 768; i++){
				temp = fogOfWarBuffer[i];
				if(temp < 112) {
					r = (((currentScreen[i] & 0xff0000) >> 16) * (temp + 143)) >> 8;
					g = (((currentScreen[i] & 0xff00) >> 8) * (temp + 143)) >> 8 ;
					b = ((currentScreen[i] & 0xff) * (temp + 143)) >> 8;
					currentScreen[i] = r << 16 | g << 8 | b;
				}
			}
		
		
			//draw select rectangle
			if(mainThread.pc.isSelectingUnit){
				xPos = mainThread.pc.area.x;
				yPos = mainThread.pc.area.y;
				selectAreaWidth = mainThread.pc.area.width;
				selectAreaHeight = mainThread.pc.area.height;
				
				try{
					if(!(yPos == 511 || xPos == 767)){
						for(int i = xPos; i < xPos + selectAreaWidth; i++)
							currentScreen[i + yPos*768] = 0xaa00;
						for(int i = xPos; i < xPos + selectAreaWidth; i++)
							currentScreen[i + (yPos + 1)*768] = 0xcc00;
						
						for(int i = xPos; i < xPos + selectAreaWidth; i++)
							currentScreen[i + (yPos+selectAreaHeight-1)*768] = 0xcc00;
						for(int i = xPos; i < xPos + selectAreaWidth; i++)
							currentScreen[i + (yPos + selectAreaHeight)*768] = 0xaa00;
						
						
						for(int i = yPos; i < yPos + selectAreaHeight; i++)
							currentScreen[xPos + i*768] = 0xaa00;
						
						for(int i = yPos+1; i < yPos + selectAreaHeight-1; i++)
							currentScreen[xPos + 1 + i*768] = 0xcc00;
						
						for(int i = yPos; i < yPos + selectAreaHeight; i++)
							currentScreen[xPos + selectAreaWidth + i*768] = 0xaa00;
						
						for(int i = yPos + 1; i < yPos + selectAreaHeight - 1; i++)
							currentScreen[xPos - 1 + selectAreaWidth + i*768] = 0xcc00;
					}
				}catch(Exception e){}
			}
			
			//draw confirmation icon
			if(confirmationIconInfo[0] != 0){
				theConfirmationIcon.setActive((float)confirmationIconInfo[1], (float)confirmationIconInfo[2], (int)confirmationIconInfo[3]);
			}
			
			theConfirmationIcon.updateAndDraw();
			
			for(int i = 0; i < confirmationIconInfo.length; i++){
				confirmationIconInfo[i] = 0;
			}
		
			//draw mini map
			theMiniMap.draw(currentScreen, minimapBitmap, unitsForMiniMap, unitsForMiniMapCount);
			theSideBar.draw(currentScreen, sideBarInfo);
		}
		
	}
			
	//start rasterization
	public static void rasterize(polygon3D polygon){
		poly = polygon;
		vertex2D = poly.vertex2D;
		visibleCount = poly.visibleCount;	
		scanPolygon();
	}
	
	
	
	
	//convert a polygon to scan lines
	public static void scanPolygon(){
		start = 512;
		end = -1; 
		int startX, g, startY, endY, temp_x;
		float gradient;
	
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			vector v2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
			}else{
				v2 = vertex2D[i+1];
			}

			boolean downwards = false;

			//ensure v1.y < v2.y;
			if (v1.screenY> v2.screenY) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			float dy = v2.screenY - v1.screenY;
			
			// ignore horizontal lines
			if (dy == 0) {
				
				continue;
			}
			
			
			startY = Math.max((int)(v1.screenY) + 1, 0);
			endY = Math.min((int)(v2.screenY), 511);
			
			
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
				
		
			//calculate x increment along this edge
			gradient = (v2.screenX - v1.screenX)* 2048 /dy;
			startX = (int)((v1.screenX *2048) +  (startY - v1.screenY) * gradient);
			g = (int)(gradient);
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
	
				if(downwards){
					if(temp_x >= 0)
						xLeft[y] = temp_x;
					else
						xLeft[y] = 0;
				}else{
					if(temp_x <= 767)
						xRight[y] = temp_x;
					else
						xRight[y] = 768;
				}
				startX+=g;
	
			}
		}
	
		
		for(int i = start; i <= end; i++){
			temp = xLeft[i];
			temp1 = xRight[i];
			temp2 = xMin[i];
			temp3 = xMax[i];
			
			if(temp1 <= temp3 && temp >= temp2)
					continue;
			
			if(!(temp > temp3) && !(temp2 > temp1)){
				xMax[i] = Math.max(temp3, temp1);
				xMin[i] = Math.min(temp2, temp);
				for(int j = xMin[i]; j < xMax[i]; j++){
					fogOfWarBuffer[j + i*768] = (byte)127;
				
				}
				continue;
			}
				
			xMax[i] = temp1; 
			xMin[i] = temp;
			for(int j = temp; j < temp1; j++){
				fogOfWarBuffer[j + i*768] = (byte)127;	
			}
		}
	}
	
	public static void blurShadow(){
		int index = 0;		
		if(cameraXZAngle <= 45 || cameraXZAngle >315){
			for(int i = 0; i < 511; i ++){
				for(int j = 1; j < 768; j++){
					index = j + i*768;
					if(shadowBitmap[index] < 0){
						if(shadowBitmap[index] == -127)
							smoothedShadowBitmap[index] = 32;
						else
							smoothedShadowBitmap[index]= (byte)(shadowBitmap[index] + 127);
						
					}else{
						smoothedShadowBitmap[index] = (byte)((shadowBitmap[index] + shadowBitmap[index - 1] + shadowBitmap[index + 768] + shadowBitmap[index + 767]) >> 2);
						//smoothedShadowBitmap[index] = (byte)((shadowBitmap[index+769] + shadowBitmap[index+767] + shadowBitmap[index-769] + shadowBitmap[index-767] + shadowBitmap[index-768] + shadowBitmap[index] + shadowBitmap[index - 1] + shadowBitmap[index + 768] + shadowBitmap[index + 1])>>3);

						
					}
				}
			}
			for(int i = 0; i < 393216; i+= 768)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			for(int i = 392448; i < 393216; i++)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			
		}
		
		if(cameraXZAngle <= 315 && cameraXZAngle > 225){
			for(int i = 511; i > 0; i --){
				for(int j = 1; j < 768; j++){
					index = j + i*768;
					if(shadowBitmap[index] < 0){
						if(shadowBitmap[index] == -127)
							smoothedShadowBitmap[index] = 32;
						else
							smoothedShadowBitmap[index]= (byte)(shadowBitmap[index] + 127);
					}else{
						smoothedShadowBitmap[index] = (byte)((shadowBitmap[index] + shadowBitmap[index - 1] + shadowBitmap[index - 768] + shadowBitmap[index - 769]) >> 2);
					
						
					}
				}
			}
			for(int i = 0; i < 393216; i+= 768)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			for(int i = 0; i < 768; i++)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			
			
		}
		
		if(cameraXZAngle <= 225 && cameraXZAngle > 135){
			for(int i = 511; i > 0; i --){
				for(int j = 0; j < 767; j++){
					index = j + i*768;
					if(shadowBitmap[index] < 0){
						if(shadowBitmap[index] == -127)
							smoothedShadowBitmap[index] = 32;
						else
							smoothedShadowBitmap[index]= (byte)(shadowBitmap[index] + 127);
					}else{
						smoothedShadowBitmap[index] = (byte)((shadowBitmap[index] + shadowBitmap[index + 1] + shadowBitmap[index - 768] + shadowBitmap[index - 767]) >> 2);
					
						
					}
				}
			}
			for(int i = 767; i < 393216; i+= 768)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			for(int i = 0; i < 768; i++)
				smoothedShadowBitmap[i] = shadowBitmap[i];
		}
		
		if(cameraXZAngle <= 135 && cameraXZAngle > 45){
			for(int i = 0; i < 511; i ++){
				for(int j = 0; j < 767; j++){
					index = j + i*768;
					if(shadowBitmap[index] < 0){
						if(shadowBitmap[index] == -127)
							smoothedShadowBitmap[index] = 32;
						else
							smoothedShadowBitmap[index]= (byte)(shadowBitmap[index] + 127);
					}else{
						smoothedShadowBitmap[index] = (byte)((shadowBitmap[index] + shadowBitmap[index + 1] + shadowBitmap[index + 768] + shadowBitmap[index + 769]) >> 2);
					
					}
				}
			}
			for(int i = 767; i < 393216; i+= 768)
				smoothedShadowBitmap[i] = shadowBitmap[i];
			for(int i = 392448; i < 393216; i++)
				smoothedShadowBitmap[i] = shadowBitmap[i];
		}
	}
	
	public static void blendShadow(){
		//blend shadow bitmap to the frame buffer
		for(int i = 0; i < 512 * 768; i++){
			temp = smoothedShadowBitmap[i];
			if(temp <= 0) {
				temp = shadowBitmap[i];
			}
			
			
			r = (((currentScreen[i] & 0xff0000) >> 16) * temp ) >> 5;
			g = (((currentScreen[i] & 0xff00) >> 8) * temp) >> 5 ;
			b = ((currentScreen[i] & 0xff) * temp) >> 5;
			
			if(r > 255)
				r = 255;
			if(g > 255)
				g = 255;
			if(b > 255)
				b = 255;
			
			currentScreen[i] = r << 16 | g << 8 | b;
		}
	}
	
	public static void prepareResources(){
		
		gamePaused = mainThread.gamePaused;
		gameStarted = mainThread.gameStarted;
		gameEnded = mainThread.gameEnded;
		
		currentScreen = mainThread.screen;
		currentZbuffer = mainThread.zBuffer;
		displacementBuffer = mainThread.displacementBuffer;
		shadowBitmap = mainThread.shadowBitmap;
		currentSelectedUnitsInfo = mainThread.theAssetManager.selectedUnitsInfo;
		visionPolygonInfo = mainThread.theAssetManager.visionPolygonInfo;
		visionPolygonCount = mainThread.theAssetManager.visionPolygonCount;
		unitsForMiniMap = mainThread.theAssetManager.unitsForMiniMap;
		unitsForMiniMapCount = mainThread.theAssetManager.unitsForMiniMapCount;
		minimapBitmap = mainThread.theAssetManager.minimapBitmap;
		explosionInfo = mainThread.theAssetManager.explosionInfo;
		explosionCount =  mainThread.theAssetManager.explosionCount;
		helixInfo = mainThread.theAssetManager.helixInfo;
		helixCount = mainThread.theAssetManager.helixCount;
		smokeEmmiterList = mainThread.theAssetManager.smokeEmmiterList;
		smokeEmmiterCount = mainThread.theAssetManager.smokeEmmiterCount;
		if(gameStarted) {
			sideBarInfo = mainThread.pc.theSideBarManager.sideBarInfo;
			playerMoney = mainThread.pc.theBaseInfo.currentCredit;
			playerPowerStatus = mainThread.pc.theBaseInfo.powerStatus;	
		}
		confirmationIconInfo = mainThread.theAssetManager.confirmationIconInfo;
	
		cameraPosition.set(camera.position);
		sinXZ = camera.sinXZ_angle;
		cosXZ = camera.cosXZ_angle;
		sinYZ = camera.sinYZ_angle;
		cosYZ = camera.cosYZ_angle;
		cameraXZAngle = camera.XZ_angle;
		
		theMiniMap.findCorners();
		
		
		
	}
	


}
