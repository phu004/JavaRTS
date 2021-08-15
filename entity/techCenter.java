package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the power plant model
public class techCenter extends solidObject{
	
	//the polygons of the model
	private polygon3D[] polygons; 
	
	public static int maxHP = 600;
	
	public int countDownToDeath = 16;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	public int [] tileIndex = new int[9];
	public int[] tempInt;
	
	public float[] tempFloat;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,screen_width+152, screen_height+250);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,screen_width-120, screen_height-110);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(0,0,screen_width, screen_height);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//a bitmap representation of the vision of the power plant for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	
	//Tech center never moves
	public final static vector movenment = new vector(0,0,0);
	
	public baseInfo theBaseInfo;
	
	public int towerTopRed = 31;   
	public int towerTopGreen = 0;
	public int towerTopBlue = 0;
	
	public int towerTopRedBase = 12;
	public int towerTopGreenBase = 4;
	public int towerTopBlueBase = 4;
	
	public static boolean lightTankResearched_player, lightTankResearched_enemy,
						  rocketTankResearched_player,rocketTankResearched_enemy,
						  stealthTankResearched_player, stealthTankResearched_enemy,
						  heavyTankResearched_player,heavyTankResearched_enemy;
	
	public static int lightTankResearchProgress_player = 255, lightTankResearchProgress_enemy = 255,
					  rocketTankResearchProgress_player = 255, rocketTankResearchProgress_enemy = 255,
					  stealthTankResearchProgress_player = 255, stealthTankResearchProgress_enemy = 255,
					  heavyTankResearchProgress_player = 255, heavyTankResearchProgress_enemy = 255;
	
	public static int creditSpentOnResearching_player, creditSpentOnResearching_enemy;
	
	public static int intendedDeployLocation = -1;
	
	public static void resetResarchStatus() {
		lightTankResearched_player = false;
		lightTankResearched_enemy = false;
		rocketTankResearched_player = false;
		rocketTankResearched_enemy = false;
		stealthTankResearched_player = false;
		stealthTankResearched_enemy = false;
		heavyTankResearched_player = false;
		heavyTankResearched_enemy = false;
		
		lightTankResearchProgress_player = 255;
		lightTankResearchProgress_enemy = 255;
		rocketTankResearchProgress_player = 255;
		rocketTankResearchProgress_enemy = 255;
		stealthTankResearchProgress_player = 255;
		stealthTankResearchProgress_enemy = 255;
		heavyTankResearchProgress_player = 255;
		heavyTankResearchProgress_enemy = 255;
		
		creditSpentOnResearching_player = 0;
		creditSpentOnResearching_enemy = 0;
		intendedDeployLocation = -1;
	}

	public techCenter(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 107;
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		ID = globalUniqID++;
		
		theBaseInfo.numberOfTechCenter++;
		
		currentHP = 600;
		
		this.teamNo = teamNo;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
		}
		
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 16, (int)(z*64) + 16, 32, 32);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		tileIndex[0] = (centerX - 8)/16 + (127 - (centerY + 8)/16)*128; 
		tileIndex[1] = (centerX + 8)/16 + (127 - (centerY + 8)/16)*128;
		tileIndex[2] = (centerX + 8)/16 + (127 - (centerY - 8)/16)*128;
		tileIndex[3] = (centerX - 8)/16 + (127 - (centerY - 8)/16)*128;
		
		mainThread.gridMap.tiles[tileIndex[0]][0] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][0] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][1] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][1] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][2] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][2] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][3] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][3] = this; 

		mainThread.gridMap.tiles[tileIndex[0]][4] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][4] = this; 
		
		if(teamNo != 0){
			tileIndex[4] = tileIndex[1] - 128; 
			tileIndex[5] = tileIndex[1] - 130;
			tileIndex[6] = tileIndex[1] + 256;
			tileIndex[7] = tileIndex[1] + 254;
			tileIndex[8] = tileIndex[1] + 126;
			
			mainThread.gridMap.tiles[tileIndex[4]][4] = this;  
			mainThread.gridMap.tiles[tileIndex[5]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[6]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[7]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[8]][4] = this; 
		}
		
		

		//init model
		start = new vector(x,y,z);
		iDirection = new vector(1f,0,0);
		jDirection = new vector(0,1f,0);
		kDirection = new vector(0,0,1f);
		
		
		//define centre of the model in world coordinate
		start = new vector(x,y,z);
		centre = start.myClone();
		tempCentre = start.myClone();
		
		shadowvertex0 =start.myClone();
		shadowvertex0.add(-0.45f,-0.2f, -0.15f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.45f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.15f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
	
		makePolygons();
				
	}

	public void makePolygons(){
		int textureIndex = 44;
		if(teamNo == 1)
			textureIndex = 53;
		
		polygons = new polygon3D[365];
		
		v = new vector[]{put(-0.25, 0.01, 0.22), put(-0.215, 0.01, 0.255), put(0.215, 0.01, 0.255), put(0.25, 0.01, 0.22), put(0.25, 0.01, -0.22),  put(0.215, 0.01, -0.255), put(-0.215, 0.01, -0.255), put(-0.25, 0.01, -0.22)};
		polygons[0] =  new polygon3D(v, put(-0.38, 0.01, 0.385), put(0.38, 0.01, 0.385), put(-0.38, 0.01, -0.385), mainThread.textures[30], 0.66f,0.66f,1);
		polygons[0].shadowBias = 10000;
		
		v = new vector[]{put(-0.215, 0.01, 0.255), put(-0.25, 0.01, 0.22), put(-0.25, 0.00, 0.22), put(-0.215, 0.0, 0.255)};
		polygons[1] = new polygon3D(v, put(-0.215, 0.01, 0.255), put(-0.25, 0.01, 0.22), put(-0.215, 0.0, 0.255), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(0.215, 0.01, 0.255), put(-0.215, 0.01, 0.255), put(-0.215, 0, 0.255), put(0.215, 0.0, 0.255)};
		polygons[2] = new polygon3D(v, put(0.215, 0.01, 0.255), put(-0.215, 0.01, 0.255), put(0.215, 0.0, 0.255), mainThread.textures[30], 0.55f, 0.1f, 1);
		
		v = new vector[]{put(0.25, 0.01, 0.22), put(0.215, 0.01, 0.255), put(0.215, 0.0, 0.255), put(0.25, 0.0, 0.22)};
		polygons[3] = new polygon3D(v, put(0.25, 0.01, 0.22), put(0.215, 0.01, 0.255), put(0.25, 0.0, 0.22), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(0.25, 0.01, -0.22), put(0.25, 0.01, 0.22), put(0.25, 0.0, 0.22), put(0.25, 0.0, -0.22)};
		polygons[4] = new polygon3D(v, put(0.25, 0.01, -0.22), put(0.25, 0.01, 0.22), put(0.25, 0.0, -0.22), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(0.215, 0.01, -0.255), put(0.25, 0.01, -0.22), put(0.25, 0.0, -0.22), put(0.215, 0.0, -0.255)};
		polygons[5] = new polygon3D(v, put(0.215, 0.01, -0.255), put(0.25, 0.01, -0.22), put(0.215, 0.0, -0.255), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(-0.215, 0.01, -0.255), put(0.215, 0.01, -0.255), put(0.215, 0.0, -0.255), put(-0.215, 0.0, -0.255)};
		polygons[6] = new polygon3D(v, put(-0.215, 0.01, -0.255), put(0.215, 0.01, -0.255), put(-0.215, 0.0, -0.255), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(-0.25, 0.01, -0.22), put(-0.215, 0.01, -0.255), put(-0.215, 0, -0.255), put(-0.25, 0, -0.22)};
		polygons[7] =  new polygon3D(v, put(-0.25, 0.01, -0.22), put(-0.215, 0.01, -0.255), put(-0.25, 0, -0.22), mainThread.textures[30], 0.66f,0.1f,1);
		
		v = new vector[]{put(-0.25, 0.01, 0.22), put(-0.25, 0.01, -0.22), put(-0.25, 0, -0.22), put(-0.25, 0, 0.22)};
		polygons[8] = new polygon3D(v, put(-0.25, 0.01, 0.22),  put(-0.25, 0.01, -0.22), put(-0.25, 0, 0.22), mainThread.textures[30], 0.66f,0.1f,1);
		
		float w = 0.1105f;
		float l = 0.17425f;
		float h = 0.16f;
		
		start.z+=0.11;
		
		vector [] a = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		polygons[9] = new polygon3D(a, put(-l,h, w), put(l,h, w), put(-l,h, -w), mainThread.textures[51], 1,0.5f,1);
		polygons[9].diffuse_I-=10;
		polygons[9].shadowBias = 5000;
		
		w = 0.13f;
		l = 0.205f;
		h = 0.009f;
		vector [] b = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		byte[] diffuse = new byte[]{16,16,16,20,26,34,40,44,47,44,40,34,26,20,16,16};
		
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
				a[(i+1)%16].myClone(),
				a[i].myClone(),
				b[i].myClone(),
				b[(i+1)%16].myClone()
			};
			
			polygons[10 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[51], 0.5f,0.5f,1);
			polygons[10 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1) ;
		}
		
		//outer
		w = 0.13f * 0.86f;
		l = 0.205f * 0.86f;
		h = 0.18f;
		vector [] c = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		//outer
		w = 0.13f * 0.86f;
		l = 0.205f * 0.86f;
		h = 0.15f;
		
		vector [] d = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		for(int i = 0; i < 16; i++){
			
			if(i !=14){
				v = new vector[]{
						c[(i+1)%16].myClone(),
						c[i].myClone(),
						d[i].myClone(),
						d[(i+1)%16].myClone()
					};
					
					
					
					polygons[26 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
					polygons[26 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1) ;
			}else{
				v = new vector[]{
						c[(i+1)%16].myClone(),
						put(l-0.3f,0.18, -w),
						put(l-0.3f,0.15, -w),
						d[(i+1)%16].myClone()
					};
				
			
				
				polygons[26 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
				polygons[26 + i].diffuse_I = 16;
				
				
			}

		}
		
		
		//inner
		w = 0.13f * 0.78f;
		l = 0.205f * 0.82f;
		h = 0.18f;
		vector [] e = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		
		//inner
		w = 0.13f * 0.78f;
		l = 0.205f * 0.82f;
		h = 0.15f;
		
		vector [] f = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		for(int i = 0; i < 16; i++){
			
			if(i != 14){
				v = new vector[]{
						f[(i+1)%16].myClone(),
						f[i].myClone(),
						e[i].myClone(),
						e[(i+1)%16].myClone()
					};
					
					polygons[42 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
					polygons[42 + i].diffuse_I = diffuse[(i+8)%16] + (byte)((diffuse[(i+8)%16] - 16)*1.1);
			}else{
				v = new vector[]{
						f[(i+1)%16].myClone(),
						put(-0.12370001f,0.15, -w),
						put(-0.12370001f,0.18, -w),
						e[(i+1)%16].myClone()
						
				};
				polygons[42 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
				polygons[42 + i].diffuse_I = 66;
				
			}
		}
		
		v = new vector[]{
				put(-0.12370001f,0.18, -(0.13f * 0.86f)),
				put(-0.12370001f,0.18, -w),
				put(-0.12370001f,0.15, -w),
				put(-0.12370001f,0.15, -(0.13f * 0.86f)),
				
		};
		polygons[58] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
		
		
		//top
		for(int i = 0; i < 16; i++){
			if(i != 15){
				v = new vector[]{
						e[i].myClone(),
						e[(i+15)%16].myClone(),
						c[(i+15)%16].myClone(),
						c[i].myClone()
					};
					
				polygons[59+ i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			}else{
				v = new vector[]{
						e[i].myClone(),
						put(-0.12370001f,0.18, -w),
						put(-0.12370001f,0.18, -(0.13f * 0.86f)),
						c[i].myClone()
					};
					
				polygons[59+ i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex],10f,10f,1);
			}
		}
		
		v = new vector[]{
				put(l-0.1f,0.18, -(0.13f * 0.86f)),
				c[14].myClone(),
				d[14].myClone(),
				put(l-0.1f,0.15, -(0.13f * 0.86f)),
				
		};
		polygons[75] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex],10f,10f,1);
		
		v = new vector[]{
				put(l-0.1f,0.15, -(0.13f * 0.78f)),
				f[14].myClone(),
				e[14].myClone(),
				put(l-0.1f,0.18, -(0.13f * 0.78f))
		};
		polygons[76] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex],10f,10f,1);
		
		
		v = new vector[]{
				put(l-0.1f,0.18, -(0.13f * 0.86f)),
				put(l-0.1f,0.18, -(0.13f * 0.78f)),
				e[14].myClone(),
				c[14].myClone(),
		};
		polygons[77] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex],10f,10f,1);
		
		
		v = new vector[]{
				put(l-0.1f,0.18, -(0.13f * 0.78f)),
				put(l-0.1f,0.18, -(0.13f * 0.86f)),
				put(l-0.1f,0.15, -(0.13f * 0.86f)),
				put(l-0.1f,0.15, -(0.13f * 0.78f)),
		};
		polygons[78] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex],10f,10f,1);
		
		
		//south part of the building
	
		start.z-=0.195;
		start.x-=0.03;
		
		w = 0.15f*0.7f;
		l = 0.1f*0.7f;
		h = 0.16f;
		
		a = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		polygons[79] = new polygon3D(a, put(-l,h, w), put(l,h, w), put(-l,h, -w), mainThread.textures[51], 1,0.5f,1);
		polygons[79].diffuse_I-=10;
		polygons[79].shadowBias = 5000;
		
		w = 0.15f;
		l = 0.1f;
		h = 0.009f;
		b = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
				a[(i+1)%16].myClone(),
				a[i].myClone(),
				b[i].myClone(),
				b[(i+1)%16].myClone()
			};
			
			polygons[80 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[51], 0.5f,0.5f,1);
			polygons[80 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1) ;
		}
		
		//inner
		start.z +=0.01f;
		start.x+=0.003f;
		
		w = 0.12f*0.9f;
		l = 0.08f*0.85f;
		h = 0.18f;
		
		a = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		h = 0.15f;
		c = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
				
		//outer
		w = 0.12f;
		l = 0.08f;
		h = 0.18f;
		b = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		h = 0.15f;
		d = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		for(int i = 0; i < 16; i++){
			if(i >= 3 && i <=9){
				continue;
			}
			
			v = new vector[]{
				a[(i+1)%16].myClone(),
				a[i].myClone(),
				b[i].myClone(),
				b[(i+1)%16].myClone()
			};
			
			polygons[96 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
		}
		
		//outer
		for(int i = 0; i < 16; i++){
			if(i >= 4 && i <=10)
				continue;
			
			v = new vector[]{
				b[i].myClone(),
				b[(i+15)%16].myClone(),
				d[(i+15)%16].myClone(),
				d[i].myClone(),
			};
			polygons[112 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			polygons[112 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1);
			polygons[112 + i].shadowBias = 10000;
			
		}
		
		//inner
		for(int i = 0; i < 16; i++){
			if(i >= 4 && i <=10)
				continue;
			
			v = new vector[]{
					c[i].myClone(),
					c[(i+15)%16].myClone(),
					a[(i+15)%16].myClone(),
					a[i].myClone()
			};
			polygons[128 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			polygons[128 + i].diffuse_I = diffuse[(i+8)%16] + (byte)((diffuse[(i+8)%16] - 16)*1.1);
			
		}
		
		
		//top part of the building
		start.z+=0.18;
		start.x-=0.04;
		
		w = 0.15f*0.55f;
		l = 0.12f*0.45f;
		h = 0.3f;
		
		a = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		polygons[144] = new polygon3D(a, put(-l,h, w), put(l,h, w), put(-l,h, -w), mainThread.textures[13], 0.5f,0.5f,1);
		polygons[144].diffuse_I-=10;
		polygons[144].shadowBias = 5000;
		
		w = 0.15f*0.55f;
		l = 0.12f*0.6f;
		h = 0.16f;
		b = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
				a[(i+1)%16].myClone(),
				a[i].myClone(),
				b[i].myClone(),
				b[(i+1)%16].myClone()
			};
			
			polygons[145 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[13], 0.5f,0.5f,1);
			polygons[145 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1) ;
		}
		
		
		//outer
		w = 0.15f*0.55f;
		l = 0.12f*0.45f;
		h = 0.32f;
		
		a = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		h = 0.29f;
		c = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		//inner
		w = 0.15f*0.49f;
		l = 0.12f*0.38f;
		h = 0.32f;
		
		b = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		h = 0.29f;
		
		d = new vector[]{
				put(-l - 0.01f, h, -w + 0.008f ),
				put(-l - 0.02f, h, -w + 0.018f ),
				put(-l - 0.03f, h, -w + 0.035f ),
				put(-l - 0.03f, h, w - 0.035f ),
				put(-l - 0.02f, h, w - 0.018f ),
				put(-l - 0.01f, h, w - 0.008f ),
			
				put(-l +0.01f,h, w),
				
				put(l - 0.01f,h, w),
				
				put(l + 0.01f, h, w - 0.008f ),
				put(l + 0.02f, h, w - 0.018f ),
				put(l + 0.03f, h, w - 0.035f ),
				put(l + 0.03f, h, -w + 0.035f ),
				put(l + 0.02f, h, -w + 0.018f ),
				put(l + 0.01f, h, -w + 0.008f ),
				
				put(l-0.01f,h, -w),
				
				put(-l+0.01f,h, -w),
			};
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					b[i].myClone(),
					b[(i+15)%16].myClone(),
					a[(i+15)%16].myClone(),
					a[i].myClone(),
			};
			polygons[161 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			
		}
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					a[i].myClone(),
					a[(i+15)%16].myClone(),
					c[(i+15)%16].myClone(),
					c[i].myClone(),
			};
			polygons[177 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			polygons[177 + i].diffuse_I = diffuse[i] + (byte)((diffuse[i] - 16)*1.1);
		}
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					d[i].myClone(),
					d[(i+15)%16].myClone(),
					b[(i+15)%16].myClone(),
					b[i].myClone(),
			};
			polygons[193 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 10f,10f,1);
			polygons[193 + i].diffuse_I = diffuse[(i+8)%16] + (byte)((diffuse[(i+8)%16] - 16)*1.1);
		}
		
		start.z-=0.24f;
		start.y-=0.04f;
		start.x+=0.01f;
		v = new vector[]{put(-0.04, 0.3, 0.04), put(0.04, 0.24, 0.04), put(0.04, 0.24, -0.04), put(-0.04, 0.3, -0.04)};
		polygons[209] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[70], 1f,1f,1);
		
		v = new vector[]{put(-0.007,0.27, 0.007), put(-0.007,0.27, -0.007), put(-0.007, 0.2, -0.007), put(-0.007, 0.2, 0.007)};
		polygons[210] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.27, -0.007), put(0.007,0.27, -0.007), put(0.007,0.2, -0.007), put(-0.007,0.2, -0.007)};
		polygons[211] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.2, 0.007), put(0.007,0.2, 0.007), put(0.007,0.27, 0.007), put(-0.007,0.27, 0.007)};
		polygons[212] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(0.007, 0.2, 0.007), put(0.007, 0.2, -0.007), put(0.007,0.27, -0.007), put(0.007,0.27, 0.007)};
		polygons[213] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, 0.04), put(-0.04, 0.3, -0.04), put(-0.04, 0.29, -0.04), put(-0.04, 0.29, 0.04)};
		polygons[214] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.23, -0.04), put(-0.04, 0.29, -0.04)};
		polygons[215] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.29, 0.04), put(0.04, 0.23, 0.04), put(0.04, 0.24, 0.04), put(-0.04, 0.3, 0.04)};
		polygons[216] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(0.04, 0.235, 0.04), put(0.04, 0.235, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.24, 0.04)};
		polygons[217] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		
		
		start.z+=0.1f;
		start.x+=0.06f;
		v = new vector[]{put(-0.04, 0.3, 0.04), put(0.04, 0.24, 0.04), put(0.04, 0.24, -0.04), put(-0.04, 0.3, -0.04)};
		polygons[218] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[70], 1f,1f,1);
		
		v = new vector[]{put(-0.007,0.27, 0.007), put(-0.007,0.27, -0.007), put(-0.007, 0.2, -0.007), put(-0.007, 0.2, 0.007)};
		polygons[219] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.27, -0.007), put(0.007,0.27, -0.007), put(0.007,0.2, -0.007), put(-0.007,0.2, -0.007)};
		polygons[220] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.2, 0.007), put(0.007,0.2, 0.007), put(0.007,0.27, 0.007), put(-0.007,0.27, 0.007)};
		polygons[221] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(0.007, 0.2, 0.007), put(0.007, 0.2, -0.007), put(0.007,0.27, -0.007), put(0.007,0.27, 0.007)};
		polygons[222] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, 0.04), put(-0.04, 0.3, -0.04), put(-0.04, 0.29, -0.04), put(-0.04, 0.29, 0.04)};
		polygons[223] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.23, -0.04), put(-0.04, 0.29, -0.04)};
		polygons[224] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.29, 0.04), put(0.04, 0.23, 0.04), put(0.04, 0.24, 0.04), put(-0.04, 0.3, 0.04)};
		polygons[225] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(0.04, 0.235, 0.04), put(0.04, 0.235, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.24, 0.04)};
		polygons[226] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		
		start.z+=0.09f;
		start.x+=0.11f;
		
		
		v = new vector[]{put(-0.04, 0.3, 0.04), put(0.04, 0.24, 0.04), put(0.04, 0.24, -0.04), put(-0.04, 0.3, -0.04)};
		polygons[227] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[70], 1f,1f,1);
		
		v = new vector[]{put(-0.007,0.27, 0.007), put(-0.007,0.27, -0.007), put(-0.007, 0.2, -0.007), put(-0.007, 0.2, 0.007)};
		polygons[228] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.27, -0.007), put(0.007,0.27, -0.007), put(0.007,0.2, -0.007), put(-0.007,0.2, -0.007)};
		polygons[229] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.2, 0.007), put(0.007,0.2, 0.007), put(0.007,0.27, 0.007), put(-0.007,0.27, 0.007)};
		polygons[230] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(0.007, 0.2, 0.007), put(0.007, 0.2, -0.007), put(0.007,0.27, -0.007), put(0.007,0.27, 0.007)};
		polygons[231] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, 0.04), put(-0.04, 0.3, -0.04), put(-0.04, 0.29, -0.04), put(-0.04, 0.29, 0.04)};
		polygons[232] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.23, -0.04), put(-0.04, 0.29, -0.04)};
		polygons[233] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.29, 0.04), put(0.04, 0.23, 0.04), put(0.04, 0.24, 0.04), put(-0.04, 0.3, 0.04)};
		polygons[234] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(0.04, 0.235, 0.04), put(0.04, 0.235, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.24, 0.04)};
		polygons[235] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		

		start.z+=0.1f;
		v = new vector[]{put(-0.04, 0.3, 0.04), put(0.04, 0.24, 0.04), put(0.04, 0.24, -0.04), put(-0.04, 0.3, -0.04)};
		polygons[236] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[70], 1f,1f,1);
		
		v = new vector[]{put(-0.007,0.27, 0.007), put(-0.007,0.27, -0.007), put(-0.007, 0.2, -0.007), put(-0.007, 0.2, 0.007)};
		polygons[237] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.27, -0.007), put(0.007,0.27, -0.007), put(0.007,0.2, -0.007), put(-0.007,0.2, -0.007)};
		polygons[238] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.007,0.2, 0.007), put(0.007,0.2, 0.007), put(0.007,0.27, 0.007), put(-0.007,0.27, 0.007)};
		polygons[239] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(0.007, 0.2, 0.007), put(0.007, 0.2, -0.007), put(0.007,0.27, -0.007), put(0.007,0.27, 0.007)};
		polygons[240] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, 0.04), put(-0.04, 0.3, -0.04), put(-0.04, 0.29, -0.04), put(-0.04, 0.29, 0.04)};
		polygons[241] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.3, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.23, -0.04), put(-0.04, 0.29, -0.04)};
		polygons[242] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(-0.04, 0.29, 0.04), put(0.04, 0.23, 0.04), put(0.04, 0.24, 0.04), put(-0.04, 0.3, 0.04)};
		polygons[243] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(0.04, 0.235, 0.04), put(0.04, 0.235, -0.04), put(0.04, 0.24, -0.04), put(0.04, 0.24, 0.04)};
		polygons[244] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 10f,10f,1);
		
		
		double r1 = 0.004;
		double r2 = 0.008;
		double theta = Math.PI/8;
	
		start.y+=0.25;
		start.x-=0.15;
		start.z-=0.05;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r1*Math.cos(i*theta), 0.45, r1*Math.sin(i*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.45,  r1*Math.sin((i+1)*theta)),
					 put(r2*Math.cos((i+1)*theta), 0.05, r2*Math.sin((i+1)*theta)),
					put(r2*Math.cos(i*theta), 0.05, r2*Math.sin(i*theta))
							};
			polygons[245 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		}
		
		for(int i = 0; i < 16; i++){
				v = new vector[]{
					put(r1*Math.cos(i*theta), 0.457, r1*Math.sin(i*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.457,  r1*Math.sin((i+1)*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.45, r1*Math.sin((i+1)*theta)),
					put(r1*Math.cos(i*theta), 0.45, r1*Math.sin(i*theta))
							};
			polygons[261 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  null, 10,10,0);
		}
		
		v = new vector[16];
		for(int i = 0; i < 16; i++){
			v[15-i] = put(r1*Math.cos(i*theta), 0.457, r1*Math.sin(i*theta));
		}
		polygons[277] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  null, 10,10,0);
		
		
		start.x-=0.05;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r1*Math.cos(i*theta), 0.38, r1*Math.sin(i*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.38,  r1*Math.sin((i+1)*theta)),
					 put(r2*Math.cos((i+1)*theta), 0.05, r2*Math.sin((i+1)*theta)),
					put(r2*Math.cos(i*theta), 0.05, r2*Math.sin(i*theta))
							};
			polygons[278 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		}
		
		for(int i = 0; i < 16; i++){
				v = new vector[]{
					put(r1*Math.cos(i*theta), 0.387, r1*Math.sin(i*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.387,  r1*Math.sin((i+1)*theta)),
					 put(r1*Math.cos((i+1)*theta), 0.38, r1*Math.sin((i+1)*theta)),
					put(r1*Math.cos(i*theta), 0.38, r1*Math.sin(i*theta))
							};
			polygons[294 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  null, 10,10,0);
		}
		
		
		v = new vector[16];
		for(int i = 0; i < 16; i++){
			v[15-i] = put(r1*Math.cos(i*theta), 0.387, r1*Math.sin(i*theta));
		}
		polygons[310] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  null, 10,10,0);
		
		
		
		start.y+=0.26;
		start.x-=0.25;
		r1 = 0.005f;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(0.25, r1*Math.cos(i*theta),r1*Math.sin(i*theta)),
					 put(0.25, r1*Math.cos((i+1)*theta), r1*Math.sin((i+1)*theta)),
					 put(0.3, r1*Math.cos((i+1)*theta),  r1*Math.sin((i+1)*theta)),
					put(0.3, r1*Math.cos(i*theta),r1*Math.sin(i*theta))
						};
			polygons[311 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[25], 10,10,1);
		}
		
		start.y+=0.04;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(0.25, r1*Math.cos(i*theta),r1*Math.sin(i*theta)),
					 put(0.25, r1*Math.cos((i+1)*theta), r1*Math.sin((i+1)*theta)),
					 put(0.3, r1*Math.cos((i+1)*theta),  r1*Math.sin((i+1)*theta)),
					put(0.3, r1*Math.cos(i*theta),r1*Math.sin(i*theta))
						};
			polygons[327 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[25], 10,10,1);
		}
		
		
		
		start.x+=0.26f;
		start.y-=0.05f;
		start.z+=0.01f;
		iDirection.rotate_XZ(125);
		iDirection.scale(0.85f);
		kDirection.rotate_XZ(125);
		
		v = new vector[]{put(-0.019, 0.017, 0.017), put(0.019, 0.017, 0.017), put(0.019, -0.017, 0.017), put(-0.019, -0.017, 0.017)};
		polygons[343] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.017, -0.017, 0.023), put(0.017, -0.017, 0.023), put(0.017, 0.017, 0.023),put(-0.017, 0.017, 0.023)};
		polygons[344] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.017, 0.017, 0.023), put(0.017, 0.017, 0.023), put(0.019, 0.017, 0.017), put(-0.019, 0.017, 0.017)};
		polygons[345] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.017, 0.017, 0.023), put(0.047, 0.013, 0.04), put(0.048, 0.013, 0.035), put(0.019, 0.017, 0.017)};
		polygons[346] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.019, 0.017, 0.017), put(0.048, 0.013, 0.035), put(0.048, -0.013, 0.035), put(0.019, -0.017, 0.017)};
		polygons[347] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.048, 0.013, 0.035), put(0.047, 0.013, 0.04), put(0.047, -0.013, 0.04), put(0.048, -0.013, 0.035)};
		polygons[348] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.047, 0.013, 0.04), put(0.017, 0.017, 0.023), put(0.017, -0.017, 0.023), put(0.047, -0.013, 0.04)};
		polygons[349] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.019, 0.017, 0.017), put(-0.048, 0.013, 0.035), put(-0.047, 0.013, 0.04) ,put(-0.017, 0.017, 0.023)};
		polygons[350] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.019, -0.017, 0.017), put(-0.048, -0.013, 0.035), put(-0.048, 0.013, 0.035) ,put(-0.019, 0.017, 0.017)};
		polygons[351] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.048, -0.013, 0.035), put(-0.047, -0.013, 0.04), put(-0.047, 0.013, 0.04), put(-0.048, 0.013, 0.035)};
		polygons[352] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.047, -0.013, 0.04), put(-0.017, -0.017, 0.023), put(-0.017, 0.017, 0.023), put(-0.047, 0.013, 0.04)};
		polygons[353] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		
		start.y+=0.08f;
		start.x+=0.024f;
		start.z-=0.01f;
		iDirection.rotate_XZ(165);
		kDirection.rotate_XZ(165);
		
		v = new vector[]{put(-0.019, 0.017, 0.017), put(0.019, 0.017, 0.017), put(0.019, -0.017, 0.017), put(-0.019, -0.017, 0.017)};
		polygons[354] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.017, -0.017, 0.023), put(0.017, -0.017, 0.023), put(0.017, 0.017, 0.023),put(-0.017, 0.017, 0.023)};
		polygons[355] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.017, 0.017, 0.023), put(0.017, 0.017, 0.023), put(0.019, 0.017, 0.017), put(-0.019, 0.017, 0.017)};
		polygons[356] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.017, 0.017, 0.023), put(0.047, 0.013, 0.04), put(0.048, 0.013, 0.035), put(0.019, 0.017, 0.017)};
		polygons[357] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.019, 0.017, 0.017), put(0.048, 0.013, 0.035), put(0.048, -0.013, 0.035), put(0.019, -0.017, 0.017)};
		polygons[358] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.048, 0.013, 0.035), put(0.047, 0.013, 0.04), put(0.047, -0.013, 0.04), put(0.048, -0.013, 0.035)};
		polygons[359] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(0.047, 0.013, 0.04), put(0.017, 0.017, 0.023), put(0.017, -0.017, 0.023), put(0.047, -0.013, 0.04)};
		polygons[360] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.019, 0.017, 0.017), put(-0.048, 0.013, 0.035), put(-0.047, 0.013, 0.04) ,put(-0.017, 0.017, 0.023)};
		polygons[361] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.019, -0.017, 0.017), put(-0.048, -0.013, 0.035), put(-0.048, 0.013, 0.035) ,put(-0.019, 0.017, 0.017)};
		polygons[362] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.048, -0.013, 0.035), put(-0.047, -0.013, 0.04), put(-0.047, 0.013, 0.04), put(-0.048, 0.013, 0.035)};
		polygons[363] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		v = new vector[]{put(-0.047, -0.013, 0.04), put(-0.017, -0.017, 0.023), put(-0.017, 0.017, 0.023), put(-0.047, 0.013, 0.04)};
		polygons[364] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),   mainThread.textures[65], 1,1,1);
		
		
	}
	
	//update the model 
	public void update(){

		//process emerging from  ground animation
		if(centre.y < -0.5f){
			centre.y+=0.02f;
			
			if(centre.y > -0.5){
				for(int i = 0; i < polygons.length; i++){		
					if(polygons[i] == null)
						continue;
					polygons[i].origin.y+=0.0000005;
					polygons[i].rightEnd.y+=0.0000005;
					polygons[i].bottomEnd.y+=0.0000005;
					
					for(int j = 0; j < polygons[i].vertex3D.length; j++){
						polygons[i].vertex3D[j].y+=0.0000005;
					}
					
					
					
				}
				shadowvertex0.y+=0.0000005;
				shadowvertex1.y+=0.0000005;
				shadowvertex2.y+=0.0000005;
				shadowvertex3.y+=0.0000005;
				
				centre.y = -0.5f;
			}else{
				for(int i = 0; i < polygons.length; i++){		
					if(polygons[i] == null)
						continue;
					
					polygons[i].origin.y+=0.02;
					polygons[i].rightEnd.y+=0.02;
					polygons[i].bottomEnd.y+=0.02;
					
					for(int j = 0; j < polygons[i].vertex3D.length; j++){
						polygons[i].vertex3D[j].y+=0.02;
					}
					
					
				}
				shadowvertex0.y+=0.02;
				shadowvertex1.y+=0.02;
				shadowvertex2.y+=0.02;
				shadowvertex3.y+=0.02;
			}
			
			//the building is invulnerable during emerging stage
			currentHP = maxHP;
		}
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;
		
		//check if power plant has been destroyed
		if(currentHP <= 0){
			countDownToDeath--;
	
			if(countDownToDeath == 0){
				//spawn an explosion when the tank is destroyed
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x;
				tempFloat[1] = centre.y + 0.15f;
				tempFloat[2] = centre.z;
				tempFloat[3] = 3.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 7;
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				theAssetManager.removeObject(this); 
				if(theBaseInfo.numberOfTechCenter == 1)
					cancelResearch(teamNo);
				
				theBaseInfo.numberOfTechCenter--;
			
				//removeFromGridMap();
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][0] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][1] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][2] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][3] = null; 

				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][4] = null; 
				
				if(teamNo != 0){
					mainThread.gridMap.tiles[tileIndex[4]][4] = null;  
					mainThread.gridMap.tiles[tileIndex[5]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[6]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[7]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[8]][4] = null; 
				}
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=50;
				return;
			}else{
				
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x + (float)Math.random()/2.5f - 0.2f;
				tempFloat[1] = centre.y + 0.15f;
				tempFloat[2] = centre.z + (float)Math.random()/2.5f - 0.2f;
				tempFloat[3] = 1.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 6 + (gameData.getRandom()%4);
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				
				
			}
		}
		
		//processing repair event
		if(isRepairing && currentHP >0){
			if(mainThread.gameFrame%8==0 && theBaseInfo.currentCredit > 0 && currentHP <maxHP){
				currentHP+=2;
				theBaseInfo.currentCredit--;
				if(currentHP > maxHP)
					currentHP = maxHP;
			}
		}
		
		//process researching
		if(mainThread.gameFrame%2==0 && (!(theBaseInfo.lowPower && mainThread.gameFrame%4==0))){
			
			//light tank research
			if(teamNo == 0){
				if(lightTankResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						lightTankResearchProgress_player = 240 * creditSpentOnResearching_player/1500;
					}
					
					if(lightTankResearchProgress_player == 240){
						lightTankResearched_player = true;
						rocketTankResearchProgress_player = 255;
						stealthTankResearchProgress_player = 255;
						heavyTankResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
						upgradeLightTank(0);
					}
				}
			}else{
				if(lightTankResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						lightTankResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/1500;
					}
				}
				
				if(lightTankResearchProgress_enemy == 240){
					lightTankResearched_enemy = true;
					rocketTankResearchProgress_enemy = 255;
					stealthTankResearchProgress_enemy = 255;
					heavyTankResearchProgress_enemy = 255;
					creditSpentOnResearching_enemy = 0;
					upgradeLightTank(1);
				}
			}
			
			//rocket tank research
			if(teamNo == 0){
				if(rocketTankResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						rocketTankResearchProgress_player = 240 * creditSpentOnResearching_player/2000;
					}
					
					if(rocketTankResearchProgress_player == 240){
						rocketTankResearched_player = true;
						lightTankResearchProgress_player = 255;
						stealthTankResearchProgress_player = 255;
						heavyTankResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
						upgradeRocketTank(0);
					}
				}
			}else{
				if(rocketTankResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						rocketTankResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/2000;
					}
				}
				
				if(rocketTankResearchProgress_enemy == 240){
					rocketTankResearched_enemy = true;
					lightTankResearchProgress_enemy = 255;
					stealthTankResearchProgress_enemy = 255;
					heavyTankResearchProgress_enemy = 255;
					creditSpentOnResearching_enemy = 0;
					upgradeRocketTank(1);
				}
			}
			
			//stealth tank research
			if(teamNo == 0){
				if(stealthTankResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						stealthTankResearchProgress_player = 240 * creditSpentOnResearching_player/2000;
					}
					
					if(stealthTankResearchProgress_player == 240){
						stealthTankResearched_player = true;
						lightTankResearchProgress_player = 255;
						rocketTankResearchProgress_player = 255;
						heavyTankResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
						upgradeStealthTank(0);
					}
				}
			}else{
				if(stealthTankResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						stealthTankResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/2000;
					}
				}
				
				if(stealthTankResearchProgress_enemy == 240){
					stealthTankResearched_enemy = true;
					lightTankResearchProgress_enemy = 255;
					rocketTankResearchProgress_enemy = 255;
					heavyTankResearchProgress_enemy = 255;
					creditSpentOnResearching_enemy = 0;
					upgradeStealthTank(1);
				}
			}
			
			//heavy tank research
			if(teamNo == 0){
				if(heavyTankResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						heavyTankResearchProgress_player = 240 * creditSpentOnResearching_player/2500;
					}
					
					if(heavyTankResearchProgress_player == 240){
						heavyTankResearched_player = true;
						lightTankResearchProgress_player = 255;
						rocketTankResearchProgress_player = 255;
						stealthTankResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
						upgradeHeavyTank(0);
					}
				}
			}else{
				if(heavyTankResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						heavyTankResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/2500;
					}
				}
				
				if(heavyTankResearchProgress_enemy == 240){
					heavyTankResearched_enemy = true;
					lightTankResearchProgress_enemy = 255;
					rocketTankResearchProgress_enemy = 255;
					stealthTankResearchProgress_enemy = 255;
					creditSpentOnResearching_enemy = 0;
					upgradeHeavyTank(1);
				}
			}
		}
		
		
		
		//mark itself on obstacle map
		mainThread.gridMap.currentObstacleMap[tileIndex[0]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[1]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[2]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[3]] = false;
		
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
			
		theAssetManager = mainThread.theAssetManager;
		
		//test if the palm tree is visible in camera point of view
		if(visibleBoundary.contains(tempCentre.screenX, tempCentre.screenY) && isRevealed){
			visible = true;
			
			if(screenBoundary.contains(tempCentre.screenX, tempCentre.screenY))
				withinViewScreen = true;
			else
				withinViewScreen = false;
			
			tempshadowvertex0.set(shadowvertex0);
			tempshadowvertex0.subtract(camera.position);
			tempshadowvertex0.rotate_XZ(camera.XZ_angle);
			tempshadowvertex0.rotate_YZ(camera.YZ_angle); 
			tempshadowvertex0.updateLocation();
			
			tempshadowvertex1.set(shadowvertex1);
			tempshadowvertex1.subtract(camera.position);
			tempshadowvertex1.rotate_XZ(camera.XZ_angle);
			tempshadowvertex1.rotate_YZ(camera.YZ_angle); 
			tempshadowvertex1.updateLocation();
			
			tempshadowvertex2.set(shadowvertex2);
			tempshadowvertex2.subtract(camera.position);
			tempshadowvertex2.rotate_XZ(camera.XZ_angle);
			tempshadowvertex2.rotate_YZ(camera.YZ_angle); 
			tempshadowvertex2.updateLocation();
			
			tempshadowvertex3.set(shadowvertex3);
			tempshadowvertex3.subtract(camera.position);
			tempshadowvertex3.rotate_XZ(camera.XZ_angle);
			tempshadowvertex3.rotate_YZ(camera.YZ_angle); 
			tempshadowvertex3.updateLocation();

			

			//if the  object is visible then draw it on the shadow buffer from light point of view
			if(shadowBoundary1.contains(tempshadowvertex0.screenX, tempshadowvertex0.screenY) ||
					shadowBoundary1.contains(tempshadowvertex1.screenX, tempshadowvertex1.screenY) ||
					shadowBoundary1.contains(tempshadowvertex2.screenX, tempshadowvertex2.screenY) ||
					shadowBoundary1.contains(tempshadowvertex3.screenX, tempshadowvertex3.screenY) 
					){
				for(int i = 0; i < polygons.length; i++){
					if(polygons[i] != null)
						polygons[i].update_lightspace();

				}
		
			}
			
			//add this object to visible unit list
			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;
			
			
		}else{
			visible = false;
		}
		
		if(visible){
			float ratio = ((float)Math.sin((float)(mainThread.gameFrame + ID)/10) + 1)/2;
		
			
		
			int color = (int)(towerTopRedBase + ratio * (towerTopRed - towerTopRedBase)) << 10 | (int)(towerTopGreenBase + ratio * (towerTopGreen - towerTopGreenBase)) << 5 | (int)(towerTopBlueBase + ratio * (towerTopBlue - towerTopBlueBase));
		
			for(int i = 261; i < 278; i++){
				polygons[i].color = color;
				polygons[i].diffuse_I = 127;
			}
	
			ratio = 1 - ratio;
			color = (int)(towerTopRedBase + ratio * (towerTopRed - towerTopRedBase)) << 10 | (int)(towerTopGreenBase + ratio * (towerTopGreen - towerTopGreenBase)) << 5 | (int)(towerTopBlueBase + ratio * (towerTopBlue - towerTopBlueBase));
			
			for(int i = 294; i < 311; i ++){
				polygons[i].color = color;
				polygons[i].diffuse_I = 127;
			}
		}
		
		
		//create vision for enemy commander
		if(teamNo == 1){
			int xPos = boundary2D.x1/16 - 8 + 10;
			int yPos = 127 - boundary2D.y1/16 - 8 + 10;
			
			for(int y = 0; y < 17; y++){
				for(int x = 0; x < 17; x++){
					if(bitmapVisionForEnemy[x+ y*17])
						enemyCommander.tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}
		
		visionBoundary.x = (int)(tempCentre.screenX - 800);
		visionBoundary.y = (int)(tempCentre.screenY - 1200);
		visionInsideScreen = camera.screen.intersects(visionBoundary);
		
		
		if(visionInsideScreen){
			if(teamNo == 0){
				tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
				tempFloat[0] = teamNo;
				tempFloat[1] = centre.x;
				tempFloat[2] = -0.4f;
				tempFloat[3] = centre.z;
				tempFloat[4] = 2;
				theAssetManager.visionPolygonCount++;
			}
		}
		
		if(theAssetManager.minimapBitmap[tileIndex[0]] ||
		   theAssetManager.minimapBitmap[tileIndex[1]] ||	 
		   theAssetManager.minimapBitmap[tileIndex[2]] ||		
		   theAssetManager.minimapBitmap[tileIndex[3]] )
			isRevealed = true;
		visible_minimap = isRevealed;
		
		if(visible_minimap){
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1/16;
			tempInt[2] = 127 - boundary2D.y1/16;
			tempInt[3] = 2;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else
				tempInt[4] = 10000;
			theAssetManager.unitsForMiniMapCount++;
		}
		
	}
	
	public static void researchLightTank(int teamNo){
		if(teamNo == 0){
			lightTankResearchProgress_player = 0;
			rocketTankResearchProgress_player = 254;
			stealthTankResearchProgress_player = 254;
			heavyTankResearchProgress_player = 254;
		}else{
			lightTankResearchProgress_enemy = 0;
			rocketTankResearchProgress_enemy = 254;
			stealthTankResearchProgress_enemy = 254;
			heavyTankResearchProgress_enemy = 254;
		}
	}
	
	public static void researchRocketTank(int teamNo){
		if(teamNo == 0){
			lightTankResearchProgress_player = 254;
			rocketTankResearchProgress_player = 0;
			stealthTankResearchProgress_player = 254;
			heavyTankResearchProgress_player = 254;
		}else{
			lightTankResearchProgress_enemy = 254;
			rocketTankResearchProgress_enemy = 0;
			stealthTankResearchProgress_enemy = 254;
			heavyTankResearchProgress_enemy = 254;
		}
	}
	
	public static void researchStealthTank(int teamNo){
		if(teamNo == 0){
			lightTankResearchProgress_player = 254;
			rocketTankResearchProgress_player = 254;
			stealthTankResearchProgress_player = 0;
			heavyTankResearchProgress_player = 254;
		}else{
			lightTankResearchProgress_enemy = 254;
			rocketTankResearchProgress_enemy = 254;
			stealthTankResearchProgress_enemy = 0;
			heavyTankResearchProgress_enemy = 254;
		}
	}
	
	public static void researchHeavyTank(int teamNo){
		if(teamNo == 0){
			lightTankResearchProgress_player = 254;
			rocketTankResearchProgress_player = 254;
			stealthTankResearchProgress_player = 254;
			heavyTankResearchProgress_player = 0;
		}else{
			lightTankResearchProgress_enemy = 254;
			rocketTankResearchProgress_enemy = 254;
			stealthTankResearchProgress_enemy = 254;
			heavyTankResearchProgress_enemy = 0;
		}
	}
	
	//cancel research
	public static void cancelResearch(int teamNo){
		if(teamNo == 0){
			lightTankResearchProgress_player = 255;
			rocketTankResearchProgress_player = 255;
			stealthTankResearchProgress_player = 255;
			heavyTankResearchProgress_player = 255;
			mainThread.pc.theBaseInfo.currentCredit+=creditSpentOnResearching_player;
			creditSpentOnResearching_player = 0;
		}else{
			lightTankResearchProgress_enemy = 255;
			rocketTankResearchProgress_enemy = 255;
			stealthTankResearchProgress_enemy = 255;
			heavyTankResearchProgress_enemy = 255;
			mainThread.pc.theBaseInfo.currentCredit+=creditSpentOnResearching_enemy;
			creditSpentOnResearching_enemy = 0;
		}
	}
	
	
	public void upgradeLightTank(int teamNo){
		for(int i = 0; i < mainThread.theAssetManager.lightTanks.length; i++){
			if(mainThread.theAssetManager.lightTanks[i] != null &&  mainThread.theAssetManager.lightTanks[i].teamNo == teamNo){
				mainThread.theAssetManager.lightTanks[i].attackRange = 1.99f;
			}
		}	
		
		if(teamNo == 0)
			lightTank.tileCheckList_player = generateTileCheckList(6);
		else
			lightTank.tileCheckList_enemy = generateTileCheckList(6);
	}
	
	public void upgradeRocketTank(int teamNo){
		for(int i = 0; i < mainThread.theAssetManager.rocketTanks.length; i++){
			if(mainThread.theAssetManager.rocketTanks[i] != null &&  mainThread.theAssetManager.rocketTanks[i].teamNo == teamNo){
				mainThread.theAssetManager.rocketTanks[i].damageMultiplier =2;
			}
		}	
	}
	
	public void upgradeStealthTank(int teamNo){
		for(int i = 0; i < mainThread.theAssetManager.stealthTanks.length; i++){
			if(mainThread.theAssetManager.stealthTanks[i] != null &&  mainThread.theAssetManager.stealthTanks[i].teamNo == teamNo){
				mainThread.theAssetManager.stealthTanks[i].hasMultiShotUpgrade = true;
			}
		}	
	}
	
	public void upgradeHeavyTank(int teamNo){
		for(int i = 0; i < mainThread.theAssetManager.heavyTanks.length; i++){
			if(mainThread.theAssetManager.heavyTanks[i] != null &&  mainThread.theAssetManager.heavyTanks[i].teamNo == teamNo){
				mainThread.theAssetManager.heavyTanks[i].canSelfRepair = true;
			}
		}	
	}
		
	//draw the model
	public void draw(){
		if(!visible)
			return;
	
		for(int i = polygons.length - 1; i >= 0; i--){
			if(polygons[i] != null){
				polygons[i].update();
				polygons[i].draw();
			}
		}
	}
	
	public vector getMovement(){
		return movenment;
	}
}