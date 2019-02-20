package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;
import gui.deployGrid;

//the construction yard model

public class constructionYard extends solidObject{
	//the polygons of the model
	private polygon3D[] polygons; 
	
	public static int maxHP = 1000;
	public int countDownToDeath = 16;
	
	public boolean needToDrawDeploymentGrid;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,920, 762);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,648, 402);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(40,70,828, 502);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//a bitmap representation of the vision of the power plant for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	
	//construction yard never moves
	public final static vector movenment = new vector(0,0,0);
	
	//construction yard occupies 9 tiles
	public int [] tileIndex = new int[9];
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	//positions of the vents
	public vector ventCenter1 = new vector(0,0,0);
	public polygon3D[] vent1;
	public polygon3D[]  vent1Clone;
	public int vent1Angle = 0;
	
	public vector ventCenter2 = new vector(0,0,0);
	public polygon3D[] vent2;
	public polygon3D[]  vent2Clone;
	public int vent2Angle = 35;
	
	//crane
	vector armCenter;
	vector pillarCenter;
	
	public boolean emergingStarted;
	
	public int currentStatus;
	//public static int isBuilding = 1;
	
	public boolean canBuildPowerPlant, canBuildRefinery, canBuildFactory, canBuildCommunicationCenter, canBuildTechCenter, canBuildGunTurret, canBuildMissileTurret;
	public int powerPlantProgress, refineryProgress, factoryProgress, communicationCenterProgress, techCenterProgress, gunTurretProgress, missileTurretProgress;
	public int creditSpentOnBuilding;
	
	public deployGrid dg;
	
	public baseInfo theBaseInfo;
	
	public static int intendedDeployLocation = -1;
	
	public int currentBuildingType = -1;
	
	public constructionYard(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 104;
		
		currentHP = 1000;
		
		this.teamNo = teamNo;
		
		ID = globalUniqID++;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfConstructionYard++;
		
		
		dg = new deployGrid();
		
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
		}
		
		powerPlantProgress = 255;
		refineryProgress = 255;
		factoryProgress = 255;
		communicationCenterProgress = 255;
		techCenterProgress = 255;
		gunTurretProgress = 255;
		missileTurretProgress = 255;

		
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 24, (int)(z*64) + 24, 48, 48);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		tileIndex[0] = (centerX - 16)/16 + (127 - (centerY + 16)/16)*128; 
		tileIndex[1] = (centerX + 16)/16 + (127 - (centerY + 16)/16)*128;
		tileIndex[2] = (centerX + 16)/16 + (127 - (centerY - 16)/16)*128;
		tileIndex[3] = (centerX - 16)/16 + (127 - (centerY - 16)/16)*128;
		tileIndex[4] = (centerX)/16 + (127 - (centerY + 16)/16)*128;
		tileIndex[5] = (centerX)/16 + (127 - (centerY - 16)/16)*128;
		tileIndex[6] = (centerX - 16)/16 + (127 - centerY/16)*128; 
		tileIndex[7] = centerX/16 + (127 - centerY/16)*128; 
		tileIndex[8] = (centerX + 16)/16 + (127 - centerY/16)*128; 
		
		
		mainThread.gridMap.tiles[tileIndex[0]][0] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[6]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[7]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[8]][0] = this; 
		
		
		mainThread.gridMap.tiles[tileIndex[0]][1] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[6]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[7]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[8]][1] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][2] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[6]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[7]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[8]][2] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][3] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[6]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[7]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[8]][3] = this; 


		mainThread.gridMap.tiles[tileIndex[0]][4] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[6]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[7]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[8]][4] = this; 
		
		
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
		shadowvertex0.add(-0.4f,-0.2f, -0.45f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.45f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.45f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
		polygon3D.recreateTextureCoordinateFlag = true;
		makePolygons();		
		polygon3D.recreateTextureCoordinateFlag = false;
	}
	
	//create polygons
	public void makePolygons(){
		polygons = new polygon3D[240 + 73+77];
		
		int polyIndex;
		
		int doorTextureIndex = 44;
		int upperBodyTExture = 44;
		int armTop = 31;
		if(teamNo == 1)
			doorTextureIndex = 53;
		
		v = new vector[]{put(-0.38, 0.3, 0.35), put(-0.345, 0.3, 0.385), put(0.345, 0.3, 0.385), put(0.38, 0.3, 0.35), put(0.38, 0.3, -0.35),  put(0.345, 0.3, -0.385), put(-0.345, 0.3, -0.385), put(-0.38, 0.3, -0.35)};
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.385), put(0.38, 0.3, 0.385), put(-0.38, 0.3, -0.385), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
		
		v = new vector[]{put(0.345, 0.35, -0.1), put(0.345, 0.35, 0.345), put(0.345, 0.30, 0.345), put(0.345, 0.30, -0.1) };
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(-0.1, 0.30, -0.1), put(-0.1, 0.30, 0.345), put(-0.1, 0.35, 0.345),  put(-0.1, 0.35, -0.1)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(-0.0875, 0.35, -0.1), put(-0.0875, 0.35, 0.345), put(-0.0875, 0.30, 0.345), put(-0.0875, 0.30, -0.1) };
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(0.3325, 0.30, -0.1), put(0.3325, 0.30, 0.345), put(0.3325, 0.35, 0.345),  put(0.3325, 0.35, -0.1)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(0.3325, 0.35, -0.1), put(0.345, 0.35, -0.1), put(0.345, 0.30, -0.1), put(0.3325, 0.30, -0.1)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(0.3325, 0.30, 0.345), put(0.345, 0.30, 0.345), put(0.345, 0.35, 0.345), put(0.3325, 0.35, 0.345)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(-0.1, 0.35, -0.1), put(-0.0875, 0.35, -0.1), put(-0.0875, 0.30, -0.1), put(-0.1, 0.30, -0.1)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		v = new vector[]{put(-0.1, 0.30, 0.345), put(-0.0875, 0.30, 0.345), put(-0.0875, 0.35, 0.345), put(-0.1, 0.35, 0.345)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,0.25f,1));
		
		
		
		double r = 0.2225;
		double delta = Math.PI/16;
		
		float w = 0.1225f;
		float h = 0.35f;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r*Math.cos(i*delta) + w,  r*Math.sin(i*delta) +h, -0.1),
							 put(r*Math.cos((i+1)*delta) + w,   r*Math.sin((i+1)*delta) + h, -0.1),
						     put(r*Math.cos((i+1)*delta) + w, r*Math.sin((i+1)*delta) + h, 0.345),
							 put(r*Math.cos(i*delta) + w, r*Math.sin(i*delta) +h, 0.345)
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			
			tempVector0.add(tempVector, -i);
			tempVector3.add(tempVector, -i);
			tempVector1.add(tempVector, 15 - i);
			
			
		    change(w,h,-0.1f, tempVector);
			polyIndex = addPolygon(polygons,  new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[51], 1f,1f,1));
			polygons[polyIndex].textureScaledWidth = (int)(polygons[polyIndex].myTexture.width/16);
			polygons[polyIndex].createShadeSpan(tempVector, v[0], v[1]);
			
		}
		
		double r2 = 0.21;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta) + w, r2*Math.sin(i*delta) +h, 0.345),
							 put(r2*Math.cos((i+1)*delta) + w, r2*Math.sin((i+1)*delta) + h, 0.345),
							 put(r2*Math.cos((i+1)*delta) + w,   r2*Math.sin((i+1)*delta) + h, -0.1),
							 put(r2*Math.cos(i*delta) + w,  r2*Math.sin(i*delta) +h, -0.1)
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			
			tempVector0.add(tempVector, -i);
			tempVector3.add(tempVector, -i);
			tempVector1.add(tempVector, 15 - i);
		
			polyIndex = addPolygon(polygons,  new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[51], 1f,1f,1));
		}
		
		for(int i = 0; i < 16; i ++){
			v = new vector[]{put(r*Math.cos((i+1)*delta) + w,   r*Math.sin((i+1)*delta) + h, -0.1),
					         put(r*Math.cos(i*delta) + w,  r*Math.sin(i*delta) +h, -0.1),
					         put(r2*Math.cos(i*delta) + w,  r2*Math.sin(i*delta) +h, -0.1),
					         put(r2*Math.cos((i+1)*delta) + w,   r2*Math.sin((i+1)*delta) + h, -0.1)
					};
			addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		}
		
		for(int i = 0; i < 16; i ++){
			v = new vector[]{put(r2*Math.cos((i+1)*delta) + w,   r2*Math.sin((i+1)*delta) + h, 0.345),
					         put(r2*Math.cos(i*delta) + w,  r2*Math.sin(i*delta) +h, 0.345),
					         put(r*Math.cos(i*delta) + w,  r*Math.sin(i*delta) +h, 0.345),
					         put(r*Math.cos((i+1)*delta) + w,   r*Math.sin((i+1)*delta) + h, 0.345)
					};
			addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		}
		
		v = new vector[17];
		
		for(int i = 0; i < 17; i++){
			v[i] = put(r*Math.cos(i*delta) + w, r*Math.sin(i*delta) +h, 0.33);
		}
		tempVector.set(v[0]);
		tempVector.y -=0.2;
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[16], tempVector.myClone(), mainThread.textures[51], 1,1f,1));
		polygons[polyIndex].shadowBias = 9500;
		
		v = new vector[]{put(0.3325, 0.35, 0.33), put(-0.0875, 0.35, 0.33), put(-0.0875, 0.30, 0.33), put(0.3325, 0.30, 0.33)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		polygons[polyIndex].shadowBias = 9500;
		
		
		v = new vector[17];
		for(int i = 0; i < 17; i++){
			v[16 - i] = put(r*Math.cos(i*delta) + w, r*Math.sin(i*delta) +h, -0.09);
		}
		tempVector.set(v[0]);
		tempVector.y -=0.2;
		addPolygon(polygons, new polygon3D(v, v[0], v[16], tempVector.myClone(), mainThread.textures[12], 1,1,1));
		
		tempVector0.set(v[0]);
		tempVector1.set(v[16]);
		v = new vector[]{put(0.3325, 0.30, -0.09),put(-0.0875, 0.30, -0.09), put(-0.0875, 0.35, -0.09), put(0.3325, 0.35, -0.09)};
		polyIndex = addPolygon(polygons, new polygon3D(v, tempVector0.myClone(), tempVector1.myClone(), tempVector.myClone(), mainThread.textures[12], 1,1f,1));
		
		v = new vector[]{put(w - 0.11, 0.48, -0.091), put(w + 0.11, 0.48, -0.091), put(w + 0.11, 0.3, -0.091), put(w - 0.11, 0.3, -0.091)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[52], 1,0.8f,1));
		
		v = new vector[]{put(w - 0.11, 0.49, -0.092), put(w + 0.11, 0.49, -0.092), put(w + 0.11, 0.47, -0.092), put(w - 0.11, 0.47, -0.092)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 3,0.5f,1));
		
		
		v = new vector[]{put(-0.32, 0.41, 0.31), put(-0.08, 0.41, 0.31), put(-0.08, 0.41, -0.06), put(-0.32, 0.41, -0.06)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		v = new vector[]{put(-0.32, 0.41, 0.31), put(-0.32, 0.41, -0.06), put(-0.33, 0.405, -0.07), put(-0.33, 0.405, 0.32)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		v = new vector[]{put(-0.33, 0.405, -0.07), put(-0.32, 0.41, -0.06),  put(-0.08, 0.41, -0.06), put(-0.06, 0.405, -0.07)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		v = new vector[]{put(-0.32, 0.41, 0.31), put(-0.33, 0.405, 0.32), put(-0.06, 0.405, 0.32), put(-0.08, 0.41, 0.31)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		v = new vector[]{put(-0.06, 0.405, 0.32), put(-0.33, 0.405, 0.32), put(-0.33, 0.3, 0.32), put(-0.06, 0.3, 0.32) };
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		v = new vector[]{put(-0.06, 0.3, -0.07), put(-0.33, 0.3, -0.07), put(-0.33, 0.405, -0.07) , put(-0.06, 0.405, -0.07)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
	
		
		v = new vector[]{put(-0.33, 0.405, 0.32), put(-0.33, 0.405, -0.07), put(-0.33, 0.3, -0.07), put(-0.33, 0.3, 0.32)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		//create crane start --------------------------------------------
		double theta = Math.PI/32;
		r = 0.08;
		double angleOffset = Math.PI/4*5 - 0.06;
		int startIndex = polyIndex+1;
		

		float startz = 0.13f;
		float startx = 0.24f;
		
		start.y+=0.61f;
		start.z-=startz;
		start.x+=startx;
			
		start.z -=0.08f;
		tempVector0 = new vector(0,0,0);
		tempVector1 = new vector(0,0,0);
		tempVector2 = new vector(0,0,0);
		tempVector3 = new vector(0,0,0);
		
		for(int i = 0; i < 18; i++){
			v = new vector[]{put(r*Math.cos((i+1)*theta + angleOffset), 0.04,  r*Math.sin((i+1)*theta + angleOffset)),
					 put(r*Math.cos(i*theta + angleOffset), 0.04, r*Math.sin(i*theta + angleOffset)),
					 put(r*Math.cos(i*theta + angleOffset), 0.09, r*Math.sin(i*theta + angleOffset)),
					 put(r*Math.cos((i+1)*theta + angleOffset), 0.09, r*Math.sin((i+1)*theta + angleOffset))
							};
			
			if(i == 0){
				
				tempVector1 = v[2].myClone();
			}
			
			if(i == 17){
				
				tempVector3 = v[3].myClone();
			}
			addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 1,1,1));
			
		}
		start.z +=0.08f;
		
		float the_x = tempVector1.x;
		float the_y = tempVector1.y;
		float the_z = tempVector1.z;
		
		v = new vector[]{new vector(the_x, the_y, the_z + 0.1f), tempVector1.myClone(), new vector(the_x, the_y - 0.05f, the_z), new vector(the_x, the_y - 0.05f, the_z + 0.1f) };
		addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 1,1,1));
		tempVector0 = new vector(the_x, the_y, the_z + 0.1f);
		
		
		float the_x1 = tempVector3.x;
		float the_y1 = tempVector3.y;
		float the_z1 = tempVector3.z;
		
		v = new vector[]{tempVector3.myClone(), new vector(the_x1, the_y1, the_z1 + 0.1f),  new vector(the_x1, the_y1-0.05f, the_z1 + 0.1f), new vector(the_x1, the_y1-0.05f, the_z1)};
		addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 1,1,1));
		tempVector2 = new vector(the_x1, the_y1, the_z1 + 0.1f);
		
		
		start.z -=0.08f;
		v = new vector[21];
		for(int i = 0; i < 19; i++){
			v[i] = put(r*Math.cos((18-i)*theta + angleOffset), 0.09, r*Math.sin((18-i)*theta + angleOffset));
		}
		v[19] = tempVector0.myClone();
		v[20] = tempVector2.myClone();
		
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));
		polygons[polyIndex].Ambient_I -=11;
		polygons[polyIndex].shadowBias = 10000;
		start.z +=0.08f;
		
		v = new vector[]{tempVector2.myClone(), tempVector0.myClone(), new vector(tempVector0.x, tempVector0.y - 0.08f, tempVector0.z), new vector(tempVector2.x, tempVector2.y - 0.08f, tempVector2.z)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));
		
		v = new vector[]{new vector(the_x1, tempVector0.y+0.05f, tempVector0.z-0.07f), new vector(the_x1, tempVector0.y+0.05f, tempVector0.z - 0.015f), new vector(the_x1, the_y1, tempVector0.z), new vector(the_x1, the_y1, tempVector0.z - 0.08f)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));

		v = new vector[]{new vector(the_x1-0.05f, the_y1, tempVector0.z - 0.08f) , new vector(the_x1-0.05f, the_y1, tempVector0.z), new vector(the_x1 - 0.05f, tempVector0.y+0.05f, tempVector0.z - 0.015f) , new vector(the_x1 - 0.05f, tempVector0.y+0.05f, tempVector0.z-0.07f)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));
		
		v = new vector[]{new vector(the_x1 - 0.05f, tempVector0.y+0.05f, tempVector0.z-0.07f), new vector(the_x1, tempVector0.y+0.05f, tempVector0.z-0.07f), new vector(the_x1, the_y1, tempVector0.z - 0.08f), new vector(the_x1- 0.05f, the_y1, tempVector0.z - 0.08f) };
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));
		
		v = new vector[]{new vector(the_x1-0.05f, tempVector0.y+0.05f, tempVector0.z - 0.015f), new vector(the_x1, tempVector0.y+0.05f, tempVector0.z - 0.015f), new vector(the_x1, tempVector0.y+0.05f, tempVector0.z-0.07f), new vector(the_x1 - 0.05f, tempVector0.y+0.05f, tempVector0.z-0.07f)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[upperBodyTExture], 2,2,1));
	
		v = new vector[]{new vector(the_x1+0.001f, tempVector0.y+0.045f, tempVector0.z-0.05f), new vector(the_x1+0.001f, tempVector0.y+0.045f, tempVector0.z - 0.02f), new vector(the_x1+0.001f, the_y1, tempVector0.z - 0.005f), new vector(the_x1+0.001f, the_y1+0.01f, tempVector0.z - 0.05f)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[45], 2,2,1));
		
		v = new vector[]{new vector(the_x1, tempVector0.y+0.05f, tempVector0.z - 0.015f), new vector(the_x1 - 0.05f, tempVector0.y+0.05f, tempVector0.z - 0.015f), new vector(the_x1-0.05f, the_y1, tempVector0.z), new vector(the_x1, the_y1, tempVector0.z)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[46], 1,1,1));
		
		v = new vector[]{new vector(the_x1-0.051f, the_y1+0.01f, tempVector0.z - 0.05f), new vector(the_x1-0.051f, the_y1, tempVector0.z - 0.005f), new vector(the_x1-0.051f, tempVector0.y+0.045f, tempVector0.z - 0.02f), new vector(the_x1-0.051f, tempVector0.y+0.045f, tempVector0.z-0.05f) };
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[45], 2,2,1));
		
		
		
		angleOffset = Math.PI * 1.65;
		theta = Math.PI/22;
		r = 0.02;
		
		h = 0.11f;
		float l = -0.12f;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{ put(0.005f, r*Math.cos((i+1)*theta + angleOffset) + h,  r*Math.sin((i+1)*theta + angleOffset) + l),
					 put( 0.005f, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put( -0.005, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put(-0.005f, r*Math.cos((i+1)*theta + angleOffset) + h, r*Math.sin((i+1)*theta + angleOffset) + l)
							};
			
			if(i ==0){
				the_x = v[2].x;
				the_y = v[2].y;
				the_z = v[2].z;
			}
			if(i == 15){
				the_x1 = v[0].x;
				the_y1 = v[0].y;
				the_z1 = v[0].z;
			}
				
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		}
		
		
		v = new vector[]{new vector(the_x, the_y, the_z), new vector(the_x + 0.01f, the_y, the_z), new vector(the_x + 0.01f, the_y- 0.08f, the_z ), new vector(the_x, the_y- 0.08f, the_z)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,10,1));
		
		v = new vector[]{new vector(the_x1, the_y1, the_z1), new vector(the_x1 - 0.01f, the_y1, the_z1), new vector(the_x1 - 0.01f, the_y1-0.08f, the_z1 + 0.01f), new vector(the_x1, the_y1-0.08f, the_z1 + 0.01f)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,10,1));
		
		v = new vector[19];
		for(int i = 0; i < 17; i ++){
			v[i] = put(-0.005f, r*Math.cos((16-i)*theta + angleOffset)+h, r*Math.sin((16-i)*theta + angleOffset)+l);
		}
		v[17] = new vector(v[0].x, the_y- 0.08f, the_z);
		v[18] = new vector(v[0].x, the_y1- 0.08f, the_z1 + 0.01f);
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		
		v = new vector[19];
		for(int i = 0; i < 19; i ++){
			v[i] = polygons[polyIndex].vertex3D[18- i].myClone();
			v[i].x +=0.01f;
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		
		start.x-=0.05;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{ put(0.005f, r*Math.cos((i+1)*theta + angleOffset) + h,  r*Math.sin((i+1)*theta + angleOffset) + l),
					 put( 0.005f, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put( -0.005, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put(-0.005f, r*Math.cos((i+1)*theta + angleOffset) + h, r*Math.sin((i+1)*theta + angleOffset) + l)
							};
			
			if(i ==0){
				the_x = v[2].x;
				the_y = v[2].y;
				the_z = v[2].z;
			}
			if(i == 15){
				the_x1 = v[0].x;
				the_y1 = v[0].y;
				the_z1 = v[0].z;
			}
				
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		}
		
		v = new vector[]{new vector(the_x, the_y, the_z), new vector(the_x + 0.01f, the_y, the_z), new vector(the_x + 0.01f, the_y- 0.08f, the_z), new vector(the_x, the_y- 0.08f, the_z)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,10,1));
		
		v = new vector[]{new vector(the_x1, the_y1, the_z1), new vector(the_x1 - 0.01f, the_y1, the_z1), new vector(the_x1 - 0.01f, the_y1-0.08f, the_z1 + 0.01f), new vector(the_x1, the_y1-0.08f, the_z1 + 0.01f)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,10,1));
		
		v = new vector[19];
		for(int i = 0; i < 17; i ++){
			v[i] = put(-0.005f, r*Math.cos((16-i)*theta + angleOffset)+h, r*Math.sin((16-i)*theta + angleOffset)+l);
		}
		v[17] = new vector(v[0].x, the_y- 0.08f, the_z);
		v[18] = new vector(v[0].x, the_y1- 0.08f, the_z1 + 0.01f);
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		
		v = new vector[19];
		for(int i = 0; i < 19; i ++){
			v[i] = polygons[polyIndex].vertex3D[18- i].myClone();
			v[i].x +=0.01f;
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1,1));
		
		start.x+=0.05;
		
		//crane arm
		tempVector.set(start);
		start.set(0,0,0);
		armCenter = new vector(-0.023f, 0.12f, -0.11f);
		
		int armIndexStart = polyIndex + 1;
		v = new vector[]{put(-0.02f,0.025f, 0.23), put(0.02f,0.025f, 0.23), put(0.02f,0.025f, -0.02),  put(-0.02f,0.025f, -0.02)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		polygons[polyIndex].shadowBias = 100000;
		
		
		v = new vector[]{ put(-0.02f,-0.015f, -0.02), put(0.02f,-0.015f, -0.02), put(0.02f,-0.015f, 0.23), put(-0.02f,-0.015f, 0.23)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		polygons[polyIndex].shadowBias = 100000;
		
		v = new vector[]{put(0.02f,0.025f, -0.02), put(0.02f,0.025f, 0.23), put(0.02f,-0.015f, 0.23), put(0.02f,-0.015f, -0.02)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		polygons[polyIndex].shadowBias = 100000;
		
		v = new vector[]{put(-0.02f,-0.015f, -0.02), put(-0.02f,-0.015f, 0.23), put(-0.02f,0.025f, 0.23), put(-0.02f,0.025f, -0.02)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		polygons[polyIndex].shadowBias = 100000;
		
		r = 0.02f;
		theta = Math.PI/16;
		angleOffset = Math.PI;
		h = 0.005f;
		l = -0.02f;
		
		for(int i = 0; i < 16; i++){
			v = new vector[]{ put(0.02f, r*Math.cos((i+1)*theta + angleOffset) + h,  r*Math.sin((i+1)*theta + angleOffset) + l),
					 put( 0.02f, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put(-0.02f, r*Math.cos(i*theta + angleOffset) + h, r*Math.sin(i*theta + angleOffset) + l),
					 put(-0.02f, r*Math.cos((i+1)*theta + angleOffset) + h, r*Math.sin((i+1)*theta + angleOffset) + l)
							};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		}
		
		v = new vector[17];
		for(int i = 0; i < 17; i ++){
			v[i] = put(-0.02f, r*Math.cos((16-i)*theta + angleOffset)+h, r*Math.sin((16-i)*theta + angleOffset)+l);
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		
		v = new vector[17];
		for(int i = 0; i < 17; i ++){
			v[i] = polygons[polyIndex].vertex3D[16- i].myClone();
			v[i].x +=0.04f;
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		
		v = new vector[]{put(0.02f,0.025f, 0.23), put(-0.02f,0.025f, 0.23), put(-0.02f,-0.015f, 0.23), put(0.02f,-0.015f, 0.23)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 10,10,1));
		
		v = new vector[]{put(-0.015f,0.02f, 0.27), put(0.015f,0.02f, 0.27), put(0.015f,0.02f, 0.03),  put(-0.015f,0.02f, 0.03)};
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0], v[1], v[3], mainThread.textures[47], 10,10,1));
		
		v = new vector[]{ put(-0.015f,-0.01f, 0.03), put(0.015f,-0.01f, 0.03), put(0.015f,-0.01f, 0.27), put(-0.015f,-0.01f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[47], 10,10,1));
	
		v = new vector[]{put(0.015f,0.02f, 0.03), put(0.015f,0.02f, 0.27), put(0.015f,-0.01f, 0.27), put(0.015f,-0.01f, 0.03)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[47], 10,10,1));
		
		v = new vector[]{put(-0.015f,-0.01f, 0.03), put(-0.015f,-0.01f, 0.27), put(-0.015f,0.02f, 0.27), put(-0.015f,0.02f, 0.03)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[47], 10,10,1));
	
		
		v = new vector[]{put(-0.02f,0.025f, 0.29), put(0.02f,0.025f, 0.29), put(0.02f,0.025f, 0.27),  put(-0.02f,0.025f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1.2f,1f,1));
		
		v = new vector[]{put(0.02f,0.025f, 0.27), put(0.02f,0.025f, 0.29), put(0.02f,0f, 0.29), put(0.02f,-0.015f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1f,1f,1));
		
		v = new vector[]{put(-0.02f,-0.015f, 0.27), put(-0.02f,0f, 0.29), put(-0.02f,0.025f, 0.29), put(-0.02f,0.025f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1f,1f,1));
		
		v = new vector[]{put(0.02f,0.025f, 0.29), put(-0.02f,0.025f, 0.29), put(-0.02f,0, 0.29), put(0.02f,0, 0.29)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1.2f,1f,1));
		
		v = new vector[]{put(-0.02f,0.025f, 0.27), put(0.02f,0.025f, 0.27), put(0.02f,-0.015f, 0.27), put(-0.02f,-0.015f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1.2f,1f,1));
		
		v = new vector[]{put(0.02f,0, 0.29), put(-0.02f,0, 0.29), put(-0.02f,-0.015f, 0.27), put(0.02f,-0.015f, 0.27)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[armTop], 1.2f,1f,1));
		
		int armIndexEnd = polyIndex;
		
		for(int i = armIndexStart; i <= armIndexEnd; i++){
			polygons[i].origin = polygons[i].origin.myClone();
			polygons[i].origin.rotate_YZ(310);
			polygons[i].origin.add(tempVector);
			polygons[i].origin.add(armCenter);
			
			polygons[i].bottomEnd = polygons[i].bottomEnd.myClone();
			polygons[i].bottomEnd.rotate_YZ(310);
			polygons[i].bottomEnd.add(tempVector);
			polygons[i].bottomEnd.add(armCenter);
			
			polygons[i].rightEnd = polygons[i].rightEnd.myClone();
			polygons[i].rightEnd.rotate_YZ(310);
			polygons[i].rightEnd.add(tempVector);
			polygons[i].rightEnd.add(armCenter);
			
			
			
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].rotate_YZ(310);
				polygons[i].vertex3D[j].add(tempVector);
				polygons[i].vertex3D[j].add(armCenter);
			}
			
			polygons[i].findNormal();
			polygons[i].findDiffuse();
			polygons[i].diffuse_I = 0;
		}
		
		//crane pillar
		pillarCenter = new vector(-0.025f, 0.09f, -0.04f);
		int pillarIndexStart = polyIndex + 1;
		
		theta = Math.PI/12;
		r = 0.01;
		
		for(int i = 0; i < 24; i++){
			v = new vector[]{put(r*Math.cos((i+1)*theta) ,  r*Math.sin((i+1)*theta), 0.08),
							put(r*Math.cos(i*theta) , r*Math.sin(i*theta), 0.08),
							put(r*Math.cos(i*theta) , r*Math.sin(i*theta), 0 ),
					 		put(r*Math.cos((i+1)*theta), r*Math.sin((i+1)*theta), 0),
							
							};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 4f,4f,1));
		}
		
		v = new vector[24];
		for(int i = 0; i < 24; i ++){
			v[i] = put(r*Math.cos(i*theta), r*Math.sin(i*theta), 0.08);
		}
		polyIndex = addPolygon(polygons,  new polygon3D(v, v[0], v[1], v[3], mainThread.textures[upperBodyTExture], 4f,4f,1));
		
		r = 0.005;
		for(int i = 0; i < 24; i++){
			v = new vector[]{put(r*Math.cos((i+1)*theta),  r*Math.sin((i+1)*theta), 0.18),
							put(r*Math.cos(i*theta), r*Math.sin(i*theta), 0.18),
							put(r*Math.cos(i*theta), r*Math.sin(i*theta), 0.08 ),
					 		put(r*Math.cos((i+1)*theta), r*Math.sin((i+1)*theta), 0.08),
							
							};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0], v[1], v[3], mainThread.textures[29], 4f,4f,1));
		}
		
		int pillarIndexEnd = polyIndex;
		
		for(int i = pillarIndexStart; i <= pillarIndexEnd; i++){
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].rotate_YZ(290);
				polygons[i].vertex3D[j].add(tempVector);
				polygons[i].vertex3D[j].add(pillarCenter);
				
				polygons[i].findNormal();
				polygons[i].findDiffuse();
			}
		}
		
		
		
		int endIndex = polyIndex;
		
		start.set(tempVector);
		start.y-=0.61f;
		start.z+=startz;
		start.x-=startx;
		
		h = -0.3f;
		
		for(int i = startIndex; i <= endIndex; i++){
			polygons[i].origin = polygons[i].origin.myClone();
			polygons[i].origin.subtract(start);
			polygons[i].origin.rotate_XZ(270);
			polygons[i].origin.add(start);
			polygons[i].origin.y+=h;
			
			polygons[i].bottomEnd = polygons[i].bottomEnd.myClone();
			polygons[i].bottomEnd.subtract(start);
			polygons[i].bottomEnd.rotate_XZ(270);
			polygons[i].bottomEnd.add(start);
			polygons[i].bottomEnd.y+=h;
			
			polygons[i].rightEnd = polygons[i].rightEnd.myClone();
			polygons[i].rightEnd.subtract(start);
			polygons[i].rightEnd.rotate_XZ(270);
			polygons[i].rightEnd.add(start);
			polygons[i].rightEnd.y+=h;
		
		
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(start);
				polygons[i].vertex3D[j].rotate_XZ(270);
				polygons[i].vertex3D[j].add(start);
				polygons[i].vertex3D[j].y+=h;
				
				polygons[i].findNormal();
				polygons[i].findDiffuse();
			}
			
			
		}
		
		//create crane end-----------------------------------------------
		
		//create vent1
		ventCenter1.set(centre);
		ventCenter1.x-=0.21f;
		ventCenter1.z+=0.2f;
		
		r = 0.05;
		delta = Math.PI/16;
		start.set(ventCenter1);
		
		v = new vector[32];
		for(int i = 0; i < 32; i++){
			v[31 - i] = put(r*Math.cos(i*delta), 0.411,  r*Math.sin(i*delta));
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], null, 1,1f,4));
		
		v = new vector[32];
		for(int i = 0; i < 32; i++){
			v[31 - i] = put(r*Math.cos(i*delta), 0.341,  r*Math.sin(i*delta));
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[51], 1,1f,1));
		
		
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r*Math.cos(i*delta), 0.311,  r*Math.sin(i*delta)),
					put(r*Math.cos((i +1)*delta), 0.311,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos((i +1)*delta), 0.411,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos(i*delta), 0.411,  r*Math.sin(i*delta))
							};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		}
		
		r = 0.0075f;
		r2 = 0.0001f;
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.4,  r2*Math.sin(i*delta)),
					put(r2*Math.cos((i +1)*delta), 0.4,  r2*Math.sin((i + 1)*delta)),
					put(r*Math.cos((i +1)*delta), 0.38,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos(i*delta), 0.38,  r*Math.sin(i*delta))
							};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1,1f,1));
		}
		
		//create vent2
		ventCenter2.set(centre);
		ventCenter2.x-=0.21f;
		ventCenter2.z+=0.03f;
		
		r = 0.05;
		delta = Math.PI/16;
		start.set(ventCenter2);
		
		v = new vector[32];
		for(int i = 0; i < 32; i++){
			v[31 - i] = put(r*Math.cos(i*delta), 0.411,  r*Math.sin(i*delta));
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], null, 1,1f,4));
		
		v = new vector[32];
		for(int i = 0; i < 32; i++){
			v[31 - i] = put(r*Math.cos(i*delta), 0.341,  r*Math.sin(i*delta));
		}
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[51], 1,1f,1));
		
		
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r*Math.cos(i*delta), 0.311,  r*Math.sin(i*delta)),
					put(r*Math.cos((i +1)*delta), 0.311,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos((i +1)*delta), 0.411,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos(i*delta), 0.411,  r*Math.sin(i*delta))
							};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		}
		
		r = 0.0075f;
		r2 = 0.0001f;
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.4,  r2*Math.sin(i*delta)),
					put(r2*Math.cos((i +1)*delta), 0.4,  r2*Math.sin((i + 1)*delta)),
					put(r*Math.cos((i +1)*delta), 0.38,  r*Math.sin((i + 1)*delta)),
					put(r*Math.cos(i*delta), 0.38,  r*Math.sin(i*delta))
							};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1,1f,1));
		}
		
		start.x=0;
		start.z=0;
	
		r = 0.04;
		delta = Math.PI/15;
		h = 0.38f;
		
			vent1 = new polygon3D[3];
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos(i*delta), h,  r*Math.sin(i*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent1, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent1[0].shadowBias = 40000;
			
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos((i+10)*delta), h,  r*Math.sin((i+10)*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent1, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent1[1].shadowBias = 40000;
			
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos((i+20)*delta), h,  r*Math.sin((i+20)*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent1, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent1[2].shadowBias = 40000;
			
			vent2 = new polygon3D[3];
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos(i*delta), h,  r*Math.sin(i*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent2, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent2[0].shadowBias = 40000;
			
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos((i+10)*delta), h,  r*Math.sin((i+10)*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent2, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent2[1].shadowBias = 40000;
			
			v = new vector[5];
			for(int i = 0; i < 4; i++){
				v[3 - i] = put(r*Math.cos((i+20)*delta), h,  r*Math.sin((i+20)*delta));
			}
			v[4] = put(0,h,0);
			addPolygon(vent2, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[25], 1,1f,1));
			vent2[2].shadowBias = 40000;
			
			start.set(tempVector);
			start.x-=0.46;
			start.z-=0.12;
			
			
		
		
		v = new vector[]{put(-0.08, -0.26, 0.08), put(0.08, -0.26, 0.08), put(0.08, -0.26, -0.08), put(-0.08, -0.26, -0.08)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		v = new vector[]{put(-0.08, -0.26, -0.08),  put(0.08, -0.26, -0.08),  put(0.13, -0.35, -0.13), put(-0.13, -0.35, -0.13)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		v = new vector[]{put(-0.13, -0.35, 0.13),  put(0.13, -0.35, 0.13),  put(0.08, -0.26, 0.08), put(-0.08, -0.26, 0.08)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		v = new vector[]{put(0.08, -0.26, -0.08), put(0.08, -0.26, 0.08), put(0.13, -0.35, 0.13), put(0.13, -0.35, -0.13)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		v = new vector[]{put(-0.13, -0.35, -0.13), put(-0.13, -0.35, 0.13), put(-0.08, -0.26, 0.08), put(-0.08, -0.26, -0.08)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[51], 1,1f,1));
		
		
		
		v = new vector[]{put(0.23, -0.3095, 0.12), put(0.31, -0.3095, 0.12), put(0.31, -0.3095, 0.11), put(0.23, -0.3095, 0.11)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.37, -0.3095, 0.12), put(0.45, -0.3095, 0.12), put(0.45, -0.3095, 0.11), put(0.37, -0.3095, 0.11)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.23, -0.3095, -0.09), put(0.31, -0.3095, -0.09), put(0.31, -0.3095, -0.1), put(0.23, -0.3095, -0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.37, -0.3095, -0.09), put(0.45, -0.3095, -0.09), put(0.45, -0.3095, -0.1), put(0.37, -0.3095, -0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.23, -0.3095, 0.12), put(0.24, -0.3095, 0.12), put(0.24, -0.3095, 0.04), put(0.23, -0.3095, 0.04)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.23, -0.3095, -0.02), put(0.24, -0.3095, -0.02), put(0.24, -0.3095,-0.1), put(0.23, -0.3095, -0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.44, -0.3095, 0.12), put(0.45, -0.3095, 0.12), put(0.45, -0.3095, 0.04), put(0.44, -0.3095, 0.04)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		v = new vector[]{put(0.44, -0.3095, -0.02), put(0.45, -0.3095, -0.02), put(0.45, -0.3095,-0.1), put(0.44, -0.3095, -0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[doorTextureIndex], 1,1f,1));
		polygons[polyIndex].diffuse_I-=20;
		
		ventCenter1.y = 0;
		vent1Clone = clonePolygons(vent1,true);
		
		ventCenter2.y = 0;
		vent2Clone = clonePolygons(vent2,true);
		
		start.set(centre);
		
		v = new vector[]{put(-0.345, 0.3, -0.385), put(0.345, 0.3, -0.385), put(0.345, 0.28, -0.385), put(-0.345, 0.28, -0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.345, 0.28,0.385), put(0.345, 0.28, 0.385), put(0.345, 0.3, 0.385), put(-0.345, 0.3, 0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.38, 0.3, 0.35), put(-0.38, 0.3, -0.35),  put(-0.38, 0.28, -0.35), put(-0.38, 0.28, 0.35)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.38, 0.28, 0.35),  put(0.38, 0.28, -0.35), put(0.38, 0.3, -0.35), put(0.38, 0.3, 0.35)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.345, 0.3, 0.385), put(-0.38, 0.3, 0.35), put(-0.38, 0.28, 0.35), put(-0.345, 0.28, 0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.345, 0.28, 0.385), put(0.38, 0.28, 0.35), put(0.38, 0.3, 0.35), put(0.345, 0.3, 0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.345, 0.28, -0.385), put(-0.38, 0.28, -0.35), put(-0.38, 0.3, -0.35), put(-0.345, 0.3, -0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.345, 0.3, -0.385), put(0.38, 0.3, -0.35), put(0.38, 0.28, -0.35), put(0.345, 0.28, -0.385)};
		addPolygon(polygons, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[30], 1,1f,1));
		
		if(teamNo != 0){
			for(int i = 0; i < polygons.length; i++){
				if(polygons[i].myTexture == null)
					continue;
				
				if(polygons[i].myTexture.ID == 42)
					polygons[i].myTexture = mainThread.textures[10];
				
				if(polygons[i].myTexture.ID == upperBodyTExture)
					polygons[i].myTexture = mainThread.textures[48];
				
				if(polygons[i].myTexture.ID == 46)
					polygons[i].myTexture = mainThread.textures[50];
				
				if(polygons[i].myTexture.ID == armTop)
					polygons[i].myTexture = mainThread.textures[49];
			}
		}
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
			
		}
	}
	
	
	
	//add a polygon to the mesh
	public int  addPolygon(polygon3D[] polys, polygon3D poly){
		for(int i = 0; i < polys.length; i++){
			if(polys[i] == null){
				polys[i] = poly;
				return i;
			}
		}
		return -1;
	}
	
	//update the model 
	public void update(){
		
		//update tech tree info
		canBuildPowerPlant = theBaseInfo.canBuildPowerPlant;
		canBuildRefinery = theBaseInfo.canBuildRefinery;
		canBuildFactory = theBaseInfo.canBuildFactory;
		canBuildCommunicationCenter = theBaseInfo.canBuildCommunicationCenter;
		canBuildGunTurret = theBaseInfo.canBuildGunTurret;
		canBuildMissileTurret = theBaseInfo.canBuildMissileTurret;
		canBuildTechCenter = theBaseInfo.canBuildTechCenter;
		
		if(canBuildRefinery == false && refineryProgress <=240){
			cancelBuilding();
		}
		
		if(canBuildFactory == false && factoryProgress <=240){
			cancelBuilding();
		}
		
		if(canBuildCommunicationCenter == false && communicationCenterProgress <=240){
			cancelBuilding();
		}
		
		if(canBuildGunTurret == false && gunTurretProgress <=240){
			cancelBuilding();
		}
		
		if(canBuildMissileTurret == false && missileTurretProgress <=240)
			cancelBuilding();
		
		if(canBuildTechCenter == false && techCenterProgress <= 240)
			cancelBuilding();
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;
		
		
		//process emerging from  ground animation
		if(centre.y < -0.79f){
			centre.y+=0.01;
			for(int i = 0; i < polygons.length; i++){		
				polygons[i].origin.y+=0.01;
				polygons[i].rightEnd.y+=0.01;
				polygons[i].bottomEnd.y+=0.01;
				
				for(int j = 0; j < polygons[i].vertex3D.length; j++){
					polygons[i].vertex3D[j].y+=0.01;
				}
			}
			
			
			shadowvertex0.y+=0.01;
			shadowvertex1.y+=0.01;
			shadowvertex2.y+=0.01;
			shadowvertex3.y+=0.01;
			
			if(centre.y >= -0.79f){
				centre.y = -0.79f;
				start = centre.myClone();
				tempCentre = start.myClone();
				
				makePolygons();
			}
			
			if(centre.y > -1.2f){
				emergingStarted = true;
				isSelectable = true;
			}
			
			//the building is invulnerable during emerging stage
			currentHP = maxHP;
		}
		
		
		//check if construction yard has been destroyed
		if(currentHP <= 0){
			countDownToDeath--;
			
			if(countDownToDeath <= 0){
				//spawn an explosion when the object is destroyed
				if(countDownToDeath == 0){
					float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
					tempFloat[0] = centre.x;
					tempFloat[1] = centre.y + 0.45f;
					tempFloat[2] = centre.z;
					tempFloat[3] = 4f;
					tempFloat[4] = 1;
					tempFloat[5] = 0;
					tempFloat[6] = 7;
					tempFloat[7] = this.height;
					theAssetManager.explosionCount++; 
					cancelBuilding();
					
					if(needToDrawDeploymentGrid){
						mainThread.pc.isDeployingBuilding = false;
						mainThread.pc.selectedConstructionYard = null;
					}
				}
				
				theAssetManager.removeObject(this); 
					
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[6]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[7]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[8]][0] = null; 
				
				
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[6]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[7]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[8]][1] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[6]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[7]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[8]][2] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[6]][3] = null;
				mainThread.gridMap.tiles[tileIndex[7]][3] = null;
				mainThread.gridMap.tiles[tileIndex[8]][3] = null;


				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[6]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[7]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[8]][4] = null; 
				
				theBaseInfo.numberOfConstructionYard--;
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=50;
				
				return;
			}else{
				
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x + (float)Math.random()*0.6f - 0.3f;
				tempFloat[1] = centre.y + 0.45f;
				tempFloat[2] = centre.z + (float)Math.random()*0.6f - 0.3f;
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
		
		//process building event
		
		if(!(theBaseInfo.lowPower && mainThread.frameIndex%2==0)){
			if(powerPlantProgress < 240){
				if(theBaseInfo.currentCredit >0){
					
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					powerPlantProgress = 240 * creditSpentOnBuilding/500;
				}
			}else if(refineryProgress < 240){
				if(theBaseInfo.currentCredit >0){
					
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					refineryProgress = 240 * creditSpentOnBuilding/1200;
				}
			}else if(factoryProgress < 240){
				if(theBaseInfo.currentCredit >0){
					
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					factoryProgress = 240 * creditSpentOnBuilding/1400;
				}
			}else if(communicationCenterProgress < 240){
				if(theBaseInfo.currentCredit >0){
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					communicationCenterProgress = 240 * creditSpentOnBuilding/1000;
				}
			}else if(gunTurretProgress < 240){
				if(theBaseInfo.currentCredit >0){
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					gunTurretProgress = 240 * creditSpentOnBuilding/400;
				}	
			}else if(missileTurretProgress < 240){
				if(theBaseInfo.currentCredit >0){
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					missileTurretProgress = 240 * creditSpentOnBuilding/750;
				}	
			}else if(techCenterProgress < 240){
				if(theBaseInfo.currentCredit >0){
					theBaseInfo.currentCredit--;
					creditSpentOnBuilding++;
					techCenterProgress = 240 * creditSpentOnBuilding/1500;
				}	
			}
		}
			
		//mark itself on obstacle map
		mainThread.gridMap.currentObstacleMap[tileIndex[0]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[1]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[2]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[3]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[4]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[5]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[6]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[7]] = false;
		mainThread.gridMap.currentObstacleMap[tileIndex[8]] = false;
		
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
			
		theAssetManager = mainThread.theAssetManager;
		
		//test if the object is visible in camera point of view
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
	
				
				if(emergingStarted){
					for(int i = 0; i < polygons.length; i++){
						polygons[i].update_lightspace();	
					}
					
					for(int i = 0; i < vent1Clone.length; i++){
						vent1Clone[i].update_lightspace();	
					}
					
					for(int i = 0; i < vent2Clone.length; i++){
						vent2Clone[i].update_lightspace();	
					}
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
			int xPos = boundary2D.x1/16 - 8 + 10 + 1;
			int yPos = 127 - boundary2D.y1/16 - 8 + 10 + 1;
			
			for(int y = 0; y < 17; y++){
				for(int x = 0; x < 17; x++){
					if(bitmapVisionForEnemy[x+ y*17])
						enemyCommander.tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}
		
		visionBoundary.x = (int)(tempCentre.screenX - 800);
		visionBoundary.y = (int)(tempCentre.screenY - 1000);
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
		   theAssetManager.minimapBitmap[tileIndex[3]] || 
		   theAssetManager.minimapBitmap[tileIndex[4]] ||	 
		   theAssetManager.minimapBitmap[tileIndex[5]] ||
		   theAssetManager.minimapBitmap[tileIndex[6]] ||
		   theAssetManager.minimapBitmap[tileIndex[7]] ||
		   theAssetManager.minimapBitmap[tileIndex[8]])
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
		
		
		updateGeometry();
		
		
	}
	
	public void updateGeometry(){
		
		//update vent1
		vent1Angle = (vent1Angle + 10)%360;
		
		
		for(int i = 0; i < vent1.length; i++){
			vent1Clone[i].origin.set(vent1[i].origin);
			
			vent1Clone[i].origin.rotate_XZ(vent1Angle);
			vent1Clone[i].origin.add(ventCenter1);
			
				
			vent1Clone[i].bottomEnd.set(vent1[i].bottomEnd);
			vent1Clone[i].bottomEnd.rotate_XZ(vent1Angle);
			vent1Clone[i].bottomEnd.add(ventCenter1);
		
				
			vent1Clone[i].rightEnd.set(vent1[i].rightEnd);
			vent1Clone[i].rightEnd.rotate_XZ(vent1Angle);
			vent1Clone[i].rightEnd.add(ventCenter1);
		
			for(int j = 0; j < vent1Clone[i].vertex3D.length; j++){
				vent1Clone[i].vertex3D[j].set(vent1[i].vertex3D[j]);
				vent1Clone[i].vertex3D[j].rotate_XZ(vent1Angle);
				vent1Clone[i].vertex3D[j].add(ventCenter1);
				
				
			}
		}
		
		//update vent2
		vent2Angle = (vent2Angle + 8)%360;
		
		
		for(int i = 0; i < vent2.length; i++){
			vent2Clone[i].origin.set(vent2[i].origin);
			
			vent2Clone[i].origin.rotate_XZ(vent2Angle);
			vent2Clone[i].origin.add(ventCenter2);
			
				
			vent2Clone[i].bottomEnd.set(vent2[i].bottomEnd);
			vent2Clone[i].bottomEnd.rotate_XZ(vent2Angle);
			vent2Clone[i].bottomEnd.add(ventCenter2);
		
				
			vent2Clone[i].rightEnd.set(vent2[i].rightEnd);
			vent2Clone[i].rightEnd.rotate_XZ(vent2Angle);
			vent2Clone[i].rightEnd.add(ventCenter2);
		
			for(int j = 0; j < vent2Clone[i].vertex3D.length; j++){
				vent2Clone[i].vertex3D[j].set(vent2[i].vertex3D[j]);
				vent2Clone[i].vertex3D[j].rotate_XZ(vent2Angle);
				vent2Clone[i].vertex3D[j].add(ventCenter2);
				
				
			}
		}
	}
	
	//build structure
	public void build(int buildingType){
		if(buildingType == 101)
			buildPowerPlant();
		else if(buildingType == 102)
			buildRefinery();
		else if(buildingType == 105)
			buildFactory();
		else if(buildingType == 106)
			buildCommunicationCentre();
		else if(buildingType == 200)
			buildGunTurret();
		else if(buildingType == 199)
			buildMissileTurret();
		else if(buildingType == 107)
			buildTechCenter();
	}
	
	//building power plant
	public void buildPowerPlant(){
		if(canBuildPowerPlant){
			powerPlantProgress = 0;
			refineryProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 254;
			techCenterProgress = 254;
			gunTurretProgress = 254;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 101;
		}
	}
	
	//building power plant
	public void buildRefinery(){
		if(canBuildRefinery){
			refineryProgress = 0;
			powerPlantProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 254;
			techCenterProgress = 254;
			gunTurretProgress = 254;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 102;
		}
	}
	
	//building power plant
	public void buildFactory(){
		if(canBuildFactory){
			refineryProgress = 254;
			powerPlantProgress = 254;
			factoryProgress = 0;
			communicationCenterProgress = 254;
			techCenterProgress = 254;
			gunTurretProgress = 254;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 105;
		}
	}
	
	//build communication centre
	public void buildCommunicationCentre(){
		if(canBuildCommunicationCenter){
			refineryProgress = 254;
			powerPlantProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 0;
			techCenterProgress = 254;
			gunTurretProgress = 254;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 106;
		}
	}
	
	//build gun turret
	public void buildGunTurret(){
		if(canBuildGunTurret){
			refineryProgress = 254;
			powerPlantProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 254;
			techCenterProgress = 254;
			gunTurretProgress = 0;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 200;
		}
	}
	
	//build Missile turret
	public void buildMissileTurret(){
		if(canBuildGunTurret){
			refineryProgress = 254;
			powerPlantProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 254;
			techCenterProgress = 254;
			gunTurretProgress = 254;
			missileTurretProgress = 0;
			creditSpentOnBuilding = 0;
			currentBuildingType = 199;
		}
	}
	
	//build Tech Center
	public void buildTechCenter(){
		if(canBuildTechCenter){
			refineryProgress = 254;
			powerPlantProgress = 254;
			factoryProgress = 254;
			communicationCenterProgress = 254;
			techCenterProgress = 0;
			gunTurretProgress = 254;
			missileTurretProgress = 254;
			creditSpentOnBuilding = 0;
			currentBuildingType = 107;
		}
	}
	
	
	//cancel building
	public void cancelBuilding(){
		powerPlantProgress = 255;
		refineryProgress = 255;
		factoryProgress = 255;
		communicationCenterProgress = 255;
		techCenterProgress = 255;
		gunTurretProgress = 255;
		missileTurretProgress = 255;
		if(teamNo == 0)
			mainThread.pc.theBaseInfo.currentCredit+=creditSpentOnBuilding;
		else
			mainThread.ec.theBaseInfo.currentCredit+=creditSpentOnBuilding;
		creditSpentOnBuilding = 0;
		currentBuildingType = -1;
	}
	
	//finishing deployment
	public void finishDeployment(){
		powerPlantProgress = 255;
		refineryProgress = 255;
		factoryProgress = 255;
		communicationCenterProgress = 255;
		techCenterProgress = 255;
		gunTurretProgress = 255;
		missileTurretProgress = 255;
		creditSpentOnBuilding = 0;
		currentBuildingType = -1;
	}
	
	public boolean isIdle(){
		return powerPlantProgress == 255 && refineryProgress == 255 && factoryProgress == 255 && communicationCenterProgress == 255 && techCenterProgress == 255 && gunTurretProgress == 255 && missileTurretProgress == 255;
	}
	
	//create building
	public void createBuilding(){
		if(powerPlantProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			powerPlant o = new powerPlant(x*0.25f, -1f, y*0.25f, 0);
			mainThread.theAssetManager.addPowerPlant(o);
		}else if(refineryProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			refinery o = new refinery(x*0.25f + 0.125f, -1.43f, y*0.25f, 0);
			mainThread.theAssetManager.addRefinery(o);
			harvester h = new harvester(new vector(x*0.25f + 0.125f,-0.3f, y*0.25f - 0.375f), 180, 0);
			if(communicationCenter.harvesterSpeedResearched_player){
				h.speed = 0.014f;
				h.bodyTurnRate = 8;
			}
			mainThread.theAssetManager.addHarvester(h);
			h.goToTheNearestGoldMine();  
		}else if(factoryProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			factory o = new factory(x*0.25f + 0.125f, -1.13f, y*0.25f, 0);
			mainThread.theAssetManager.addFactory(o);
		}else if(communicationCenterProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			communicationCenter o = new communicationCenter(x*0.25f, -1f, y*0.25f, 0);
			mainThread.theAssetManager.addCommunicationCenter(o);
		}else if(gunTurretProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			gunTurret o = new gunTurret(x*0.25f -0.125f, -0.65f, y*0.25f + 0.125f, 0);
			mainThread.theAssetManager.addGunTurret(o);
		}else if(missileTurretProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			missileTurret o = new missileTurret(x*0.25f -0.125f, -0.95f, y*0.25f + 0.125f, 0);
			mainThread.theAssetManager.addMissileTurret(o);
		}else if(techCenterProgress == 240){
			int y = 127 - dg.gridOneIndex/128;
			int x = dg.gridOneIndex%128 + 1;
			techCenter o = new techCenter(x*0.25f, -1f, y*0.25f, 0);
			mainThread.theAssetManager.addTechCenter(o);
		}
		
		
	}
	
	
	
	
	//draw the model
	public void draw(){
		
		if(!visible || !emergingStarted)
			return;
		for(int i = 0; i < polygons.length; i++){
			polygons[i].update();
		}
		
		for(int i = 0; i < vent1Clone.length; i++){
			vent1Clone[i].update();
		}
		
		for(int i = 0; i < vent2Clone.length; i++){
			vent2Clone[i].update();
		}
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].draw();
		}
		
		for(int i = 0; i < vent1Clone.length; i++){
			vent1Clone[i].draw();
		}
		
		for(int i = 0; i < vent2Clone.length; i++){
			vent2Clone[i].draw();
		}
		
	}
	
	public void drawDeploymentGrid(){
		
		if(needToDrawDeploymentGrid){	
			dg.update();
			dg.draw();
		}
	}
	
	public vector getMovement(){
		return movenment;
	}
	
	public void printCurrentBuilding(){
		if(refineryProgress <= 240){
			System.out.println("building refinery: " + 100*refineryProgress/240 + "%");
		}
		
		if(powerPlantProgress <= 240){
			System.out.println("building power plant: " + 100*powerPlantProgress/240 + "%");
		}
		
		if(factoryProgress <= 240){
			System.out.println("building factory: " + 100*factoryProgress/240 + "%");
		}
		
		if(communicationCenterProgress <= 240){
			System.out.println("building communication center: " + 100*communicationCenterProgress/240 + "%");
		}
		
		if(techCenterProgress <= 240){
			System.out.println("building tech Center: " + 100*techCenterProgress/240 + "%");
		}
		
		if(gunTurretProgress <= 240){
			System.out.println("building gun turret: " + 100*gunTurretProgress/240 + "%");
		}
		
		if(missileTurretProgress <= 240){
			System.out.println("building missile turret: " + 100*missileTurretProgress/240 + "%");
		}

	}
}
