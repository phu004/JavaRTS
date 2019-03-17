package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the communication center model

public class communicationCenter extends solidObject{

	//the polygons of the model
	private polygon3D[] polygons; 
	
	public static int maxHP = 550;
	
	public int countDownToDeath = 16;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	public vector tempVector4 = new vector(0,0,0);
	
	public vector radarDiskCorner0, radarDiskCorner1, radarDiskCorner2, radarDiskCorner3;
	
	public int [] tileIndex = new int[9];
	public int[] tempInt;
	
	public float[] tempFloat;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,960, 762);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,648, 402);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(0,0,768, 512);  
	
	//a bitmap representation of the vision of the power plant for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	
	//Communication center  never moves
	public final static vector movenment = new vector(0,0,0);
	
	public int numOfPolygons;
	
	public static int rotationPartIndexStart, rotationPartIndexEnd, radarDiskIndexStart, radarDiskIndexEnd;
	
	//index of the tiles to scan for cloaked unitsl
	public static int[] tileCheckList;
	
	public baseInfo theBaseInfo;
	
	public static boolean harvesterSpeedResearched_player, harvesterSpeedResearched_enemy;
	public static boolean rapidfireResearched_player,  rapidfireResearched_enemy;
	public static int harvesterSpeedResearchProgress_player = 255, harvesterSpeedResearchProgress_enemy = 255;
	public static int rapidfireResearchProgress_player = 255,  rapidfireResearchProgress_enemy = 255; 
	public static int creditSpentOnResearching_player, creditSpentOnResearching_enemy;
	
	public static int intendedDeployLocation = -1;
	
	public communicationCenter(float x, float y, float z,  int teamNo){	
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 106;
		
		ID = globalUniqID++;
		

		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfCommunicationCenter++;
		
		currentHP = 550;
		
		this.teamNo = teamNo;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(12);
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
		shadowvertex0.add(-0.5f,-0.2f, -0.5f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.5f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.5f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
	
		makePolygons();
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(12f);
		}
				
	}
	
	public void makePolygons(){
		polygons = new polygon3D[600];
		vector[] v;
		vector[] v2;
		vector[] v3;
		int polygonIndex;
		
		int beamTexture = 44;
		if(teamNo == 1)
			beamTexture = 53;
		
		float delta = (float)Math.PI/6;
		float r1 = 0.25f;
		float r2 = 0.22f;
		float r3 = 0.09f;
		float r4 = 0.095f;
		float r5 = 0.085f;
		float r6 = 0.23f;
		
		
		tempVector.set(0,0,0.005f); 
		tempVector4.set(-0.01f,0,0); 
		
		tempVector.rotate_XZ(15);
		tempVector4.rotate_XZ(15);
		
		for(int i = 0; i < 12; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), -0.04, r2*Math.sin(i*delta)),
							 put(r2*Math.cos((i+1)*delta), -0.04, r2*Math.sin((i+1)*delta)),
							 put(r1*Math.cos((i+1)*delta), -0.2,  r1*Math.sin((i+1)*delta)),
							 put(r1*Math.cos(i*delta), -0.2, r1*Math.sin(i*delta))
							};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[13], 1f,1f,1));
			
			
			polygons[polygonIndex].shadowBias = 20000;
			
			
			v = new vector[]{put(r3*Math.cos(i*delta), 0.02, r3*Math.sin(i*delta)),
					 put(r3*Math.cos((i+1)*delta), 0.02, r3*Math.sin((i+1)*delta)),
					 put(r2*Math.cos((i+1)*delta), -0.04,  r2*Math.sin((i+1)*delta)),
					 put(r2*Math.cos(i*delta), -0.04, r2*Math.sin(i*delta))
					};
	
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[14], 1f,1f,1));
		
			polygons[polygonIndex].shadowBias = 10000;
			
			v = new vector[]{
					put(r5*Math.cos(i*delta), 0.03, r5*Math.sin(i*delta)),
				 put(r5*Math.cos((i+1)*delta), 0.03, r5*Math.sin((i+1)*delta)),
				 put(r4*Math.cos((i+1)*delta), 0.03,  r4*Math.sin((i+1)*delta)),
				 put(r4*Math.cos(i*delta), 0.03, r4*Math.sin(i*delta))
				
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v = new vector[]{
					 put(r4*Math.cos(i*delta), 0.03, r4*Math.sin(i*delta)),
					 put(r4*Math.cos((i+1)*delta), 0.03,  r4*Math.sin((i+1)*delta)),
					 put(r4*Math.cos((i+1)*delta), 0.01,  r4*Math.sin((i+1)*delta)),
					 put(r4*Math.cos(i*delta), 0.01, r4*Math.sin(i*delta)),		
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			
			v = new vector[]{
					
				 put(r5*Math.cos((i+1)*delta), 0.03, r5*Math.sin((i+1)*delta)),
				 put(r5*Math.cos(i*delta), 0.03, r5*Math.sin(i*delta)),
				 put(r5*Math.cos(i*delta), 0.02, r5*Math.sin(i*delta)),
				 put(r5*Math.cos((i+1)*delta), 0.02, r5*Math.sin((i+1)*delta)),
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			
			change((float)(r3*Math.cos(i*delta)), 0.03f, (float)(r3*Math.sin(i*delta)), tempVector0);
			tempVector0.subtract(tempVector);
			change((float)(r3*Math.cos(i*delta)), 0.03f, (float)(r3*Math.sin(i*delta)), tempVector1);
			tempVector1.add(tempVector);
			change((float)(r6*Math.cos(i*delta)), -0.03f, (float)(r6*Math.sin(i*delta)), tempVector2);
			tempVector2.add(tempVector);
			change((float)(r6*Math.cos(i*delta)), -0.03f, (float)(r6*Math.sin(i*delta)), tempVector3);
			tempVector3.subtract(tempVector);
			
			v = new vector[]{tempVector0.myClone(), tempVector1.myClone(), tempVector2.myClone(), tempVector3.myClone()};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v = new vector[4];
			v[0] = tempVector2.myClone();
			v[1] = tempVector1.myClone();
			v[2] = tempVector1.myClone();
			v[2].y-=0.01f;
			v[3] = tempVector2.myClone();
			v[3].y-=0.01f;
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v = new vector[4];
			v[0] = tempVector0.myClone();
			v[1] = tempVector3.myClone();
			v[2] = tempVector3.myClone();
			v[2].y-=0.01f;
			v[3] = tempVector0.myClone();
			v[3].y-=0.01f;
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v = new vector[4];
			v[0] = tempVector2.myClone();
			v[1] = tempVector2.myClone();
			v[1].add(tempVector4,2);
			v[3] = tempVector2.myClone();
			v[3].y-=0.17f;
			v[3].add(tempVector4, -3);
			v[2]=v[3].myClone();
			v[2].add(tempVector4,2);
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v2 = new vector[4];
			v2[0] = v[3].myClone();
			v2[1] = v[2].myClone();
			v2[2] = v[1].myClone();
			v2[3] = v[0].myClone();
			v2[0].add(tempVector, -2);
			v2[1].add(tempVector, -2);
			v2[2].add(tempVector, -2);
			v2[3].add(tempVector, -2);
			polygonIndex = addPolygon(polygons, new polygon3D(v2, v2[0], v2[1], v2[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			v3 = new vector[4];
			v3[0] = v2[3].myClone();
			v3[1] = v[0].myClone();
			v3[2] = v[3].myClone();
			v3[3] = v2[0].myClone();
			polygonIndex = addPolygon(polygons, new polygon3D(v3, v3[0], v3[1], v3[3], mainThread.textures[beamTexture], 5f,5f,1));
			
			tempVector.rotate_XZ(30);
			tempVector4.rotate_XZ(30);
			
		}
		
		
		
		v = new vector[12];
		for(int i = 0; i < 12; i++){
			v[11 - i] = put(r3*Math.cos(i*delta), 0.02, r3*Math.sin(i*delta));
		}
		
		start.x-=0.005;
		
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 10f,10f,1));
		polygons[polygonIndex].diffuse_I+=10;
		
		//radar disk support structure
		rotationPartIndexStart = polygonIndex+1;
		v = new vector[]{put(-0.045,0.1, -0.03), put(-0.02,0.1, -0.03), put(-0.02,0.02, -0.05), put(-0.045,0.02, -0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.045,0.02, 0.05), put(-0.02,0.02, 0.05), put(-0.02,0.1, 0.03), put(-0.045,0.1, 0.03)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.045, 0.1, 0.03), put(-0.045,0.1, -0.03), put(-0.045,0.02, -0.05), put(-0.045,0.02, 0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.02,0.02, 0.05), put(-0.02,0.02, -0.05), put(-0.02,0.1, -0.03), put(-0.02, 0.1, 0.03)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		
		float r = 0.03f;
		delta = (float)Math.PI/16;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(-0.02, r*Math.cos((i+25)*delta)+0.1,r*Math.sin((i+25)*delta)),
					put(-0.02, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta)),
					 put(-0.045, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta)),
					 put(-0.045, r*Math.cos((i+25)*delta)+0.1, r*Math.sin((i+25)*delta)),
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		}
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[16-i] = put(-0.045, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[i] = put(-0.02, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		start.x+=0.075;
	
		
		v = new vector[]{put(-0.045,0.1, -0.03), put(-0.02,0.1, -0.03), put(-0.02,0.02, -0.05), put(-0.045,0.02, -0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.045,0.02, 0.05), put(-0.02,0.02, 0.05), put(-0.02,0.1, 0.03), put(-0.045,0.1, 0.03)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.045, 0.1, 0.03), put(-0.045,0.1, -0.03), put(-0.045,0.02, -0.05), put(-0.045,0.02, 0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[]{put(-0.02,0.02, 0.05), put(-0.02,0.02, -0.05), put(-0.02,0.1, -0.03), put(-0.02, 0.1, 0.03)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(-0.02, r*Math.cos((i+25)*delta)+0.1,r*Math.sin((i+25)*delta)),
					put(-0.02, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta)),
					 put(-0.045, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta)),
					 put(-0.045, r*Math.cos((i+25)*delta)+0.1, r*Math.sin((i+25)*delta)),
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		}
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[16-i] = put(-0.045, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[i] = put(-0.02, r*Math.cos((i+24)*delta)+0.1, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 1f,2f,1));
		
		start.x-=0.07;
		
		v = new vector[]{put(-0.025,0.06, -0.04), put(0.025,0.06, -0.04), put(0.025,0.02, -0.05),put(-0.025,0.02, -0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 2f,1f,1));
		
		v = new vector[]{put(-0.025,0.02, 0.05), put(0.025,0.02, 0.05), put(0.025,0.06, 0.04),put(-0.025,0.06, 0.04)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 2f,1f,1));
		
		v = new vector[]{put(0.025,0.06, -0.05), put(-0.025,0.06, -0.05), put(-0.025,0.06, 0.05), put(0.025,0.06, 0.05)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[beamTexture], 2f,1f,1));
		
		
		start.z-=0.03;
		
		v = new vector[]{put(-0.025, 0.27, -0.02), put(0.025, 0.27, -0.02), put(0.025, 0.08, -0.02), put(-0.025, 0.08, -0.02) };
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,1f,1));
		polygons[polygonIndex].shadowBias = 15000;
		
		v = new vector[]{put(-0.025, 0.08, 0.02), put(0.025, 0.08, 0.02), put(0.025, 0.27, 0.02),  put(-0.025, 0.27, 0.02)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,1f,1));
		polygons[polygonIndex].shadowBias = 15000;
		
		v = new vector[]{put(0.025, 0.27, -0.02), put(0.025, 0.27, 0.02), put(0.025, 0.08, 0.02), put(0.025, 0.08, -0.02)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,1f,1));
		polygons[polygonIndex].shadowBias = 15000;
		
		v = new vector[]{put(-0.025, 0.08, -0.02), put(-0.025, 0.08, 0.02), put(-0.025, 0.27, 0.02), put(-0.025, 0.27, -0.02)};
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,1f,1));
		polygons[polygonIndex].shadowBias = 15000;
		
		r = 0.02f;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(0.025, r*Math.cos((i+25)*delta)+0.27,r*Math.sin((i+25)*delta)),
					put(0.025, r*Math.cos((i+24)*delta)+0.27, r*Math.sin((i+24)*delta)),
					 put(-0.025, r*Math.cos((i+24)*delta)+0.27, r*Math.sin((i+24)*delta)),
					 put(-0.025, r*Math.cos((i+25)*delta)+0.27, r*Math.sin((i+25)*delta)),
			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,2f,1));
			polygons[polygonIndex].shadowBias = 15000;
		}
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[16-i] = put(-0.025, r*Math.cos((i+24)*delta)+0.27, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,2f,1));
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[i] = put(0.025, r*Math.cos((i+24)*delta)+0.27, r*Math.sin((i+24)*delta));
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1f,2f,1));
	
		//radar antenna 
		start.z-=0.27f;
		start.y+=0.25f;
		delta = (float)Math.PI/8;
		r = 0.015f;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), -0.005f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), 0.03f),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), 0.03f),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), -0.005f),
				};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 10f,10f,1));
		}
		
		v = new vector[16];
		for(int i = 0; i < 16; i++){
			v[i] = put(r*Math.sin(i*delta), r*Math.cos(i*delta), -0.005f);
		}
		polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 10f,10f,1));
		
		r = 0.004f;
		int angle1 = 50;
		int angle2 = 310;
		
		
		float length = 0.24f;
		
		iDirection.rotate_XZ(angle1);
		jDirection.rotate_XZ(angle1);
		kDirection.rotate_XZ(angle1);
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), 0f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), 0f),
				};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 10f,10f,1));
		}
		
		iDirection.rotate_XZ(angle2);
		jDirection.rotate_XZ(angle2);
		kDirection.rotate_XZ(angle2);
		
		
		iDirection.rotate_XZ(angle2);
		jDirection.rotate_XZ(angle2);
		kDirection.rotate_XZ(angle2);
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), 0f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), 0f),
				};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 10f,10f,1));
		}
		
		iDirection.rotate_XZ(angle1);
		jDirection.rotate_XZ(angle1);
		kDirection.rotate_XZ(angle1);
		
		iDirection.rotate_YZ(angle2);
		jDirection.rotate_YZ(angle2);
		kDirection.rotate_YZ(angle2);
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), 0f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta),length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), 0f),
				};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 10f,10f,1));
		}
		
		iDirection.rotate_YZ(angle1);
		jDirection.rotate_YZ(angle1);
		kDirection.rotate_YZ(angle1);
		
		iDirection.rotate_YZ(angle1);
		jDirection.rotate_YZ(angle1);
		kDirection.rotate_YZ(angle1);
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), 0f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), length),
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta), 0f),
				};
			
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 10f,10f,1));
		}
		
		iDirection.rotate_YZ(angle2);
		jDirection.rotate_YZ(angle2);
		kDirection.rotate_YZ(angle2);
		
		
		start.z+=0.27f;
		start.y-=0.25f;
		
		rotationPartIndexEnd=polygonIndex;
		
		//radar disk front
		
		radarDiskIndexStart = polygonIndex+1;
		delta = (float)Math.PI/12;
		r = 0f;
		float h = 0.25f;
		float l = -0.03f;
		float dl = 0;
		
		//init radar disk corners
		radarDiskCorner0 = start.myClone();
		radarDiskCorner0.y+=(h + 0.038f*5);
		radarDiskCorner0.x-=(0.038f*5);
		
		
		radarDiskCorner1 = start.myClone();
		radarDiskCorner1.y+=(h + 0.038f*5);
		radarDiskCorner1.x+=(0.038f*5);
		
		
		radarDiskCorner3 = start.myClone();
		radarDiskCorner3.y+=(h - 0.038f*5);
		radarDiskCorner3.x-=(0.038f*5);
		
		
		
		for(int j = 0; j < 5; j++){
			for(int i = 0; i < 24; i++){
				//most inner circle
				dl = (float)Math.sin(delta*(j+1))/36;
				if(j==0){
					v = new vector[]{
							put(0, 0 + h, l),
							put(0.038*Math.sin(i*delta), 0.038*Math.cos(i*delta) + h, l - dl),
							put(0.038*Math.sin((i+1)*delta), 0.038*Math.cos((i+1)*delta) + h, l-dl),
					};
					polygonIndex = createRadarDiskPolygon(v, mainThread.textures[65], 1f,1f,1);
					polygons[polygonIndex].Ambient_I+=5;
					polygons[polygonIndex].reflectance -=45;
					polygons[polygonIndex].findDiffuse();
				
				}else{
					v = new vector[]{
						put(r*Math.sin(i*delta), r*Math.cos(i*delta) + h, l),
						put((r+0.038)*Math.sin(i*delta), (r+0.038)*Math.cos(i*delta) + h, l-dl),
						put((r+0.038)*Math.sin((i+1)*delta), (r+0.038)*Math.cos((i+1)*delta) + h, l-dl),
						put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta) + h, l),
					};
					polygonIndex = createRadarDiskPolygon(v, mainThread.textures[65], 1f,1f,1);
					polygons[polygonIndex].Ambient_I+=5;
					polygons[polygonIndex].reflectance -=45;
					polygons[polygonIndex].findDiffuse();
				
					if(j == 4 && (i%6 == 0 ||  i%6 ==1  || i%6 ==5))
						polygons[polygonIndex].shadowBias = 15000;
					else
						polygons[polygonIndex].shadowBias = 30000;
				}
				
			}
			r+=0.038f;
			l-=dl;
		}
		
		//radar disk back
		r = 0f;
		h = 0.25f;
		l = -0.02f;
		dl = 0;
		
		for(int j = 0; j < 5; j++){
			for(int i = 0; i < 24; i++){
				//most inner circle
				dl = (float)Math.sin(delta*(j+1))/36;
				if(j==0){
					v = new vector[]{
							put(0.038*Math.sin((i+1)*delta), 0.038*Math.cos((i+1)*delta) + h, l-dl),
							put(0.038*Math.sin(i*delta), 0.038*Math.cos(i*delta) + h, l - dl),
							put(0, 0 + h, l),
							
							
					};
					
					polygonIndex = createRadarDiskPolygon(v, mainThread.textures[65], 1f,1f,1);
					polygons[polygonIndex].Ambient_I+=5;
					polygons[polygonIndex].reflectance -=45;
					polygons[polygonIndex].findDiffuse();
					 
					
				}else{
					v = new vector[]{
							put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta) + h, l),
							put((r+0.038)*Math.sin((i+1)*delta), (r+0.038)*Math.cos((i+1)*delta) + h, l-dl),
							put((r+0.038)*Math.sin(i*delta), (r+0.038)*Math.cos(i*delta) + h, l-dl),
						put(r*Math.sin(i*delta), r*Math.cos(i*delta) + h, l),
						
						
						
					};
					polygonIndex = createRadarDiskPolygon(v, mainThread.textures[65], 1f,1f,1);
					polygons[polygonIndex].Ambient_I+=5;
					polygons[polygonIndex].reflectance -=45;
					polygons[polygonIndex].findDiffuse();
					polygons[polygonIndex].shadowBias = 70000;
				}
				
			}
			r+=0.038f;
			l-=dl;
		}
	
		//radar disk side
		for(int i = 0; i < 24; i++){
			v = new vector[]{
					put(r*Math.sin((i+1)*delta), r*Math.cos((i+1)*delta) + h, l),
					put((r)*Math.sin((i+1)*delta), (r)*Math.cos((i+1)*delta) + h, l-0.01f),
					put((r)*Math.sin(i*delta), (r)*Math.cos(i*delta) + h, l-0.01f),
					put(r*Math.sin(i*delta), r*Math.cos(i*delta) + h, l),
				

			};
			polygonIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 1f,2f,1));
			polygons[polygonIndex].Ambient_I+=20;
			polygons[polygonIndex].reflectance = 64;
			polygons[polygonIndex].findDiffuse();
		}
		
		start.z+=0.03;
		
		radarDiskIndexEnd = polygonIndex;
		
		for(int i = 180; i <= rotationPartIndexEnd; i++){
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(start);
				polygons[i].vertex3D[j].rotate_YZ(30);
				polygons[i].vertex3D[j].add(start);
				polygons[i].findNormal();
				polygons[i].findDiffuse();
			}
		}
		
		for(int i = radarDiskIndexStart; i <= radarDiskIndexEnd; i++){
			polygons[i].origin.subtract(start);
			polygons[i].origin.rotate_YZ(30);
			polygons[i].origin.add(start);
			
			polygons[i].rightEnd.subtract(start);
			polygons[i].rightEnd.rotate_YZ(30);
			polygons[i].rightEnd.add(start);
			
			polygons[i].bottomEnd.subtract(start);
			polygons[i].bottomEnd.rotate_YZ(30);
			polygons[i].bottomEnd.add(start);
			
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(start);
				polygons[i].vertex3D[j].rotate_YZ(30);
				polygons[i].vertex3D[j].add(start);
				polygons[i].findNormal();
				polygons[i].findDiffuse();
			}
		}
		
	} 
	
	
	public int createRadarDiskPolygon(vector[] v, texture theTexture, float scaleX, float scaleY, int type){
		polygon3D poly = null;
		
		//find a linear combination of the basis vectors in texture space
		tempVector0.set(v[0]);
		tempVector0.subtract(v[1]);
		tempVector0.z = 0;
		tempVector0.unit();
		
		tempVector1.set(v[2]);
		tempVector1.subtract(v[1]);
		tempVector1.z = 0;
		tempVector1.unit();
		
		tempVector2.cross(tempVector0, tempVector1);
		tempVector3.cross(tempVector2, tempVector0);
		
		geometry.solveLinerEquation2D(tempVector0.x, tempVector0.y, tempVector3.x, tempVector3.y, radarDiskCorner0.x - v[1].x , radarDiskCorner0.y - v[1].y);
		float X0 = geometry.X;
		float Y0 = geometry.Y;
		
		geometry.solveLinerEquation2D(tempVector0.x, tempVector0.y, tempVector3.x, tempVector3.y, radarDiskCorner1.x - v[1].x , radarDiskCorner1.y - v[1].y);
		float X1 = geometry.X;
		float Y1 = geometry.Y;
		
		geometry.solveLinerEquation2D(tempVector0.x, tempVector0.y, tempVector3.x, tempVector3.y, radarDiskCorner3.x - v[1].x , radarDiskCorner3.y - v[1].y);
		float X3 = geometry.X;
		float Y3 = geometry.Y;
		
		//apply the combination in 3d space to find out OUV coordinates
		tempVector0.set(v[0]);
		tempVector0.subtract(v[1]);
		tempVector0.unit();
		
		tempVector1.set(v[2]);
		tempVector1.subtract(v[1]);
		tempVector1.unit();
		
		tempVector2.cross(tempVector0, tempVector1);
		tempVector3.cross(tempVector2, tempVector0);
		
		vector O = v[1].myClone();
		O.add(tempVector0, X0);
		O.add(tempVector3, Y0);
		
		vector U = v[1].myClone();
		U.add(tempVector0, X1);
		U.add(tempVector3, Y1);
		
		vector V = v[1].myClone();
		V.add(tempVector0, X3);
		V.add(tempVector3, Y3);
		
		return addPolygon(polygons, new polygon3D(v, O, U, V, theTexture, scaleX,scaleY,type));
	}
	
	
	
	
	

	
	//update the model 
	public void update(){
		//process emerging from  ground animation
		if(centre.y < -0.32f){
			centre.y+=0.02f;
			
				for(int i = 0; i < radarDiskIndexStart; i++){		
					
					for(int j = 0; j < polygons[i].vertex3D.length; j++){
						polygons[i].vertex3D[j].y+=0.02f;
					}
				}
				
				for(int i = radarDiskIndexStart; i <= radarDiskIndexEnd; i++){
					polygons[i].origin.y+=0.02f;
					polygons[i].rightEnd.y+=0.02f;
					polygons[i].bottomEnd.y+=0.02f;
					
					for(int j = 0; j < polygons[i].vertex3D.length; j++){
						polygons[i].vertex3D[j].y+=0.02f;
						
					}
					polygons[i].findDiffuse();
				}
			
				shadowvertex0.y+=0.02f;
				shadowvertex1.y+=0.02f;
				shadowvertex2.y+=0.02f;
				shadowvertex3.y+=0.02f;
			
			
			//the building is invulnerable during emerging stage
			currentHP = maxHP;
		}
		
		
		//check if the building has been destroyed
		if(currentHP <= 0){
			countDownToDeath--;
	
			if(countDownToDeath == 0){
				//spawn an explosion when the building is destroyed
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
				if(theBaseInfo.numberOfCommunicationCenter == 1)
					cancelResearch(teamNo);
				
				if(teamNo == 0)
					mainThread.pc.theBaseInfo.numberOfCommunicationCenter--;
				else
					mainThread.ec.theBaseInfo.numberOfCommunicationCenter--;
				
				
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
					attacker.experience+=35;
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
		
		if(isRepairing && currentHP >0){
			if(mainThread.frameIndex%8==0 && theBaseInfo.currentCredit > 0 && currentHP <maxHP){
				currentHP+=2;
				theBaseInfo.currentCredit--;
				if(currentHP > maxHP)
					currentHP = maxHP;
			}
		}
		
		//process researching
		if(mainThread.frameIndex%2==0 && (!(theBaseInfo.lowPower && mainThread.frameIndex%4==0))){
			
			if(teamNo == 0){
				if(harvesterSpeedResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						harvesterSpeedResearchProgress_player = 240 * creditSpentOnResearching_player/1200;
					}
					
					if(harvesterSpeedResearchProgress_player == 240){
						harvesterSpeedResearched_player = true;
						rapidfireResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
						upgradeHarvester(0);
					}
				}
				
				if(rapidfireResearchProgress_player < 240){
					if(mainThread.pc.theBaseInfo.currentCredit >0){
						mainThread.pc.theBaseInfo.currentCredit--;
						creditSpentOnResearching_player++;
						rapidfireResearchProgress_player = 240 * creditSpentOnResearching_player/1200;
					}
					
					if(rapidfireResearchProgress_player == 240){
						rapidfireResearched_player = true;
						harvesterSpeedResearchProgress_player = 255;
						creditSpentOnResearching_player = 0;
					}
				}
				
			}else{
				if(harvesterSpeedResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						harvesterSpeedResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/1500;
					}
				}
				
				if(harvesterSpeedResearchProgress_enemy == 240){
					harvesterSpeedResearched_enemy = true;
					rapidfireResearchProgress_enemy = 255;
					creditSpentOnResearching_enemy = 0;
					upgradeHarvester(1);
				}
				
				if(rapidfireResearchProgress_enemy < 240){
					if(mainThread.ec.theBaseInfo.currentCredit >0){
						mainThread.ec.theBaseInfo.currentCredit--;
						creditSpentOnResearching_enemy++;
						rapidfireResearchProgress_enemy = 240 * creditSpentOnResearching_enemy/1500;
					}
					
					if(rapidfireResearchProgress_enemy == 240){
						rapidfireResearched_enemy = true;
						harvesterSpeedResearchProgress_enemy = 255;
						creditSpentOnResearching_enemy = 0;
					}
				}
				
			}
		}
		
		
		
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;
		
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
		
		//test if the building is visible in camera point of view
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

			updateGeometry();
			

			//if the  object is visible then draw it on the shadow buffer from light point of view
			if(shadowBoundary1.contains(tempshadowvertex0.screenX, tempshadowvertex0.screenY) ||
					shadowBoundary1.contains(tempshadowvertex1.screenX, tempshadowvertex1.screenY) ||
					shadowBoundary1.contains(tempshadowvertex2.screenX, tempshadowvertex2.screenY) ||
					shadowBoundary1.contains(tempshadowvertex3.screenX, tempshadowvertex3.screenY) 
					){
				for(int i = 0; i < numOfPolygons; i++){
					polygons[i].update_lightspace();
					
					
				}
		
			}
			
			//add this object to visible unit list
			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;
			
			
		}else{
			visible = false;
		}
		
		
		//create vision for enemy commander
		if(teamNo == 1){
			int xPos = boundary2D.x1/16 - 12 + 10;
			int yPos = 127 - boundary2D.y1/16 - 12 + 10;
			
			for(int y = 0; y < 25; y++){
				for(int x = 0; x < 25; x++){
					if(bitmapVisionForEnemy[x+ y*25])
						enemyCommander.tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}
		
	
		visionInsideScreen = true;
		
		
		
		if(visionInsideScreen){
			if(teamNo == 0){
				tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
				tempFloat[0] = teamNo;
				tempFloat[1] = centre.x;
				tempFloat[2] = -0.4f;
				tempFloat[3] = centre.z;
				tempFloat[4] = 3;
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
			tempInt[3] = 3;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else
				tempInt[4] = 10000;
			theAssetManager.unitsForMiniMapCount++;
			
		}
		
		//scan for clocked unit
		if((ID + mainThread.frameIndex)%10 == 0 && !theBaseInfo.lowPower){
			currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
			
			for(int i = 0; i < tileCheckList.length; i++){
				if(tileCheckList[i] != Integer.MAX_VALUE){
					int index = currentOccupiedTile + tileCheckList[i];
					if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
						continue;
					tile = mainThread.gridMap.tiles[index];
					
					for(int j = 0; j < 4; j++){
						if(tile[j] != null){
							if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0){
								tile[j].cloakCooldownCount = 60;	
							}
						}
					}
				}
			}
		}
		
	}
	
	public void updateGeometry(){
		if(centre.y < -0.32f)
			return;
		
		int angle = 2;
		if(theBaseInfo.lowPower)
			angle=0;
		
		for(int i = rotationPartIndexStart; i <= rotationPartIndexEnd; i++){
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(start);
				polygons[i].vertex3D[j].rotate_XZ(angle);
				polygons[i].vertex3D[j].add(start);
				polygons[i].findDiffuse();
			}
			polygons[i].normal.rotate_XZ(angle);
		}
		
		for(int i = radarDiskIndexStart; i <= radarDiskIndexEnd; i++){
			polygons[i].origin.subtract(start);
			polygons[i].origin.rotate_XZ(angle);
			polygons[i].origin.add(start);
			
			polygons[i].rightEnd.subtract(start);
			polygons[i].rightEnd.rotate_XZ(angle);
			polygons[i].rightEnd.add(start);
			
			polygons[i].bottomEnd.subtract(start);
			polygons[i].bottomEnd.rotate_XZ(angle);
			polygons[i].bottomEnd.add(start);
			
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(start);
				polygons[i].vertex3D[j].rotate_XZ(angle);
				polygons[i].vertex3D[j].add(start);
				
			}
			
			polygons[i].normal.rotate_XZ(angle);
			polygons[i].findDiffuse();
		}
	}
	
	//draw the model
	public void draw(){
		if(!visible)
			return;
		for(int i = 0; i < numOfPolygons; i++){
			polygons[i].update();
			polygons[i].draw();
			
		}
		
		
	}
	
	public int  addPolygon(polygon3D[] polys, polygon3D poly){
		for(int i = 0; i < polys.length; i++){
			if(polys[i] == null){
				polys[i] = poly;
				numOfPolygons++;
				return i;
			}
		}
		return -1;
	}
	
	public static void researchHarvesterSpeed(int teamNo){
		if(teamNo == 0){
		
			harvesterSpeedResearchProgress_player = 0;
			rapidfireResearchProgress_player = 254;
		}else{
			harvesterSpeedResearchProgress_enemy = 0;
			rapidfireResearchProgress_enemy = 254;
		}
	}
	
	
	public static void researchRapidfire(int teamNo){
		if(teamNo == 0){
			
			rapidfireResearchProgress_player = 0;
			harvesterSpeedResearchProgress_player = 254;
		}else{
			rapidfireResearchProgress_enemy = 0;
			harvesterSpeedResearchProgress_enemy = 254;
		}
	}
	
	//cancel research
	public static void cancelResearch(int teamNo){
		if(teamNo == 0){
			harvesterSpeedResearchProgress_player = 255;
			rapidfireResearchProgress_player = 255;
			mainThread.pc.theBaseInfo.currentCredit+=creditSpentOnResearching_player;
			creditSpentOnResearching_player = 0;
		}else{
			harvesterSpeedResearchProgress_enemy = 255;
			rapidfireResearchProgress_enemy = 255;
			mainThread.ec.theBaseInfo.currentCredit+=creditSpentOnResearching_enemy;
			creditSpentOnResearching_enemy = 0;
		}
	}
	
	
	public void upgradeHarvester(int teamNo){
		for(int i = 0; i < mainThread.theAssetManager.harvesters.length; i++){
			if(mainThread.theAssetManager.harvesters[i] != null &&  mainThread.theAssetManager.harvesters[i].teamNo == teamNo){
				mainThread.theAssetManager.harvesters[i].speed =  0.014f;
				mainThread.theAssetManager.harvesters[i].bodyTurnRate = 8;
			}
		}		
	}



	public vector getMovement(){
		return movenment;
	}
}
