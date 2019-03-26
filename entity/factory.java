package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the factory model
public class factory extends solidObject{

	//the polygons of the model
	private polygon3D[] polygons; 
	private polygon3D[] doorUpper;
	private polygon3D[] doorLower;
	private polygon3D[] fanA;
	private polygon3D[] fanB;
	
	public static int maxHP = 850;
	
	public int countDownToDeath = 16;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	//factory occupies 6 tiles
	public int [] tileIndex = new int[6];
	
	public int[] tempInt;
	
	public float[] tempFloat;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,920, 762);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,648, 402);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(20,60,788, 482);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//a bitmap representation of the vision of the building for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	
	//factory never moves
	public final static vector movenment = new vector(0,0,0);
	
	//number of polygons
	public int numOfPolygons;
	
	//distortion polygon index
	public int distortionA, distortionB;
	
	//power tower center
	public vector powerTowerCenterA, powerTowerCenterB;
	
	public boolean canBuildLightTank, canBuildDrone, canBuildRocketTank, canBuildHarvester, canBuildStealthTank, canBuildHeavyTank, canBuildMCV;
	public int lightTankProgress, droneProgress, rocketTankProgress, harvesterProgress, stealthTankProgress, heavyTankProgress, MCVProgress;
	public int creditSpentOnBuilding;
	public baseInfo theBaseInfo;
	public byte[] productionQueue; 
	public int numOfLightTankOnQueue, numOfRocketTankOnQueue, numOfStealthTankOnQueue, numOfHarvesterOnQueue, numOfHeavyTankOnQueue, numOfDroneOnQueue, numOfMCVOnQueue;
	public int numOfDrones;
	public boolean isDeliveringUnit, doorOpened, doorClosed, openingDoor, closingDoor;
	public float doorHeightMark;
	public boolean doorHeightMarked;
	public solidObject deliveredUnit;
	
	
	public static int lightTankType = 0;
	public static int rocketTankType = 1;
	public static int harvesterType = 2;
	public static int droneType = 5;
	public static int MCVType = 3;
	public static int stealthTankType = 4;
	public static int heavyTankType = 6;
	
	public int currentStatus;
	public static int isBuilding = 1;
	public static int isIdle = 0;
	
	public vector rallyCenter;
	public vector[] rallyPoints;
	public boolean rallyPointChanged;
	
	public goldMine targetGoldMine;
	
	public drone[] myDrones;
	
	
	
	public factory(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 105;
		
		currentHP = 850;
		
		myDrones = new drone[3];
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfFactory++;
		
		this.teamNo = teamNo;
		
		ID = globalUniqID++;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
		}
		
		lightTankProgress = 255;
		droneProgress = 255;
		rocketTankProgress = 255;
		harvesterProgress = 255;
		stealthTankProgress = 255;
		heavyTankProgress = 255;
		MCVProgress = 255;
		
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 24, (int)(z*64) + 16, 48, 32);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		tileIndex[0] = (centerX - 16)/16 + (127 - (centerY + 8)/16)*128; 
		tileIndex[1] = (centerX + 16)/16 + (127 - (centerY + 8)/16)*128;
		tileIndex[2] = (centerX + 16)/16 + (127 - (centerY - 8)/16)*128;
		tileIndex[3] = (centerX - 16)/16 + (127 - (centerY - 8)/16)*128;
		tileIndex[4] = (centerX)/16 + (127 - (centerY + 8)/16)*128;
		tileIndex[5] = (centerX)/16 + (127 - (centerY - 8)/16)*128;
		
		
		
		mainThread.gridMap.tiles[tileIndex[0]][0] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][0] = this; 
		
		
		mainThread.gridMap.tiles[tileIndex[0]][1] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][1] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][2] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][2] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][3] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][3] = this; 


		mainThread.gridMap.tiles[tileIndex[0]][4] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[4]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[5]][4] = this; 
		
		int tileIndex6 = tileIndex[5] + 128;
		int tileIndex7 = tileIndex[5] + 128 - 1;
		int tileIndex8 = tileIndex[5] + 128 + 1;
		mainThread.gridMap.tiles[tileIndex6][4] = this;
		mainThread.gridMap.tiles[tileIndex7][4] = this;
		mainThread.gridMap.tiles[tileIndex8][4] = this;
		
		
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
		shadowvertex0.add(-0.45f,-0.2f, -0.3f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.45f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.3f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
		makePolygons();		
		
		productionQueue = new byte[1000];
		for(int i = 0; i < productionQueue.length; i++)
			productionQueue[i] = -1;
		
		doorClosed = true;
		
		rallyCenter = new vector(centre.x,-0.3f,centre.z-0.625f);
		float l = 0.25f;
		rallyPoints = new vector[]{                                             
				new vector(0,0,-l-0.03f),  new vector(l + 0.075f,0,-l-0.03f), new vector(-l -0.075f,0,-l-0.03f), 
				new vector(l+0.075f,0,0), new vector(-l-0.075f,0,0), new vector(0,0,0), 
				new vector(-l-0.1f,0,l), new vector(l,0,l), new vector(0,0,l)
				
		};
		
	}
	
	
	public void makePolygons(){
		polygons = new polygon3D[300];
		
		int polyIndex;
		
		int factorySkin = 51;
		int roofSkin = 44;
		if(teamNo == 1)
			roofSkin = 53;
		
		
		//roof
		v = new vector[]{put(-0.27, 0.56, 0.16), put(0.11, 0.56, 0.16), put(0.11, 0.56, 0.1), put(-0.27, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,2,1));
		
		v = new vector[]{put(0.11, 0.56, 0.16), put(-0.27, 0.56, 0.16), put(-0.27, 0.55, 0.16), put(0.11, 0.55, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,1,1));
		
		v = new vector[]{put(0.11, 0.55, 0.1), put(-0.27, 0.55, 0.1), put(-0.27, 0.56, 0.1), put(0.11, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,1,1));
		
		v = new vector[]{put(0.11, 0.56, 0.1), put(0.11, 0.56, 0.16), put(0.204, 0.3, 0.16), put(0.204, 0.3, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 1,10,1));
		
		v = new vector[]{put(0.11, 0.56, 0.1), put(0.204, 0.3, 0.1), put(0.19, 0.3, 0.1), put(0.094, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(0.094, 0.56, 0.16), put(0.19, 0.3, 0.16), put(0.204, 0.3, 0.16), put(0.11, 0.56, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(-0.27, 0.56, 0.16), put(-0.27, 0.56, 0.1), put(-0.364, 0.3, 0.1), put(-0.364, 0.3, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 1,10,1));
		
		v = new vector[]{put(-0.27, 0.56, 0.16),  put(-0.364, 0.3, 0.16), put(-0.35, 0.3, 0.16), put(-0.255, 0.56, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(-0.255, 0.56, 0.1), put(-0.35, 0.3, 0.1),  put(-0.364, 0.3, 0.1), put(-0.27, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));

		start.z-=0.27f;
		v = new vector[]{put(-0.27, 0.56, 0.16), put(0.11, 0.56, 0.16), put(0.11, 0.56, 0.1), put(-0.27, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,2,1));
		
		v = new vector[]{put(0.11, 0.56, 0.16), put(-0.27, 0.56, 0.16), put(-0.27, 0.55, 0.16), put(0.11, 0.55, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,1,1));
		
		v = new vector[]{put(0.11, 0.55, 0.1), put(-0.27, 0.55, 0.1), put(-0.27, 0.56, 0.1), put(0.11, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,1,1));
		
		v = new vector[]{put(0.11, 0.56, 0.1), put(0.11, 0.56, 0.16), put(0.204, 0.3, 0.16), put(0.204, 0.3, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 1,10,1));
		
		v = new vector[]{put(0.11, 0.56, 0.1), put(0.204, 0.3, 0.1), put(0.19, 0.3, 0.1), put(0.094, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(0.094, 0.56, 0.16), put(0.19, 0.3, 0.16), put(0.204, 0.3, 0.16), put(0.11, 0.56, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(-0.27, 0.56, 0.16), put(-0.27, 0.56, 0.1), put(-0.364, 0.3, 0.1), put(-0.364, 0.3, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 1,10,1));
		
		v = new vector[]{put(-0.27, 0.56, 0.16),  put(-0.364, 0.3, 0.16), put(-0.35, 0.3, 0.16), put(-0.255, 0.56, 0.16)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		
		v = new vector[]{put(-0.255, 0.56, 0.1), put(-0.35, 0.3, 0.1),  put(-0.364, 0.3, 0.1), put(-0.27, 0.56, 0.1)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[roofSkin], 10,10,1));
		start.z+=0.27f;
		

	
		
		//main structure
		v = new vector[]{put(-0.26, 0.55, 0.24), put(-0.26, 0.55, -0.24), put(-0.35, 0.3, -0.24), put(-0.35, 0.3, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		polygons[polyIndex].shadowBias = 10000;
		
		v = new vector[]{put(0.19, 0.3, 0.24), put(0.19, 0.3, -0.24), put(0.1, 0.55, -0.24), put(0.1, 0.55, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		polygons[polyIndex].shadowBias = 10000;
		
		v = new vector[]{put(-0.26, 0.55, 0.24), put(0.1, 0.55, 0.24), put(0.1, 0.55, -0.24), put(-0.26, 0.55, -0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		polygons[polyIndex].shadowBias = 10000;
		
		
		v = new vector[]{put(0.085, 0.55, 0.24),put(0.085, 0.55, -0.24), put(0.175, 0.3, -0.24),  put(0.175, 0.3, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(0.085, 0.55, 0.24), put(0.175, 0.3, 0.24), put(0.19, 0.3, 0.24), put(0.1, 0.55, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		
		v = new vector[]{put(-0.335, 0.3, 0.24), put(-0.335, 0.3, -0.24), put(-0.245, 0.55, -0.24), put(-0.245, 0.55, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(-0.245, 0.55, 0.24), put(-0.26, 0.55, 0.24), put(-0.35, 0.3, 0.24),put(-0.335, 0.3, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		
		
		v = new vector[]{put(0.1, 0.55, 0.225), put(-0.26, 0.55, 0.225), put(-0.35, 0.3, 0.225), put(0.19, 0.3, 0.225)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		polygons[polyIndex].shadowBias = 10000;
		
		v = new vector[]{put(0.1, 0.55, 0.24), put(-0.26, 0.55, 0.24), put(-0.26, 0.535, 0.24), put(0.1, 0.535, 0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(0.1, 0.535, -0.24), put(-0.26, 0.535, -0.24), put(-0.26, 0.55, -0.24), put(0.1, 0.55, -0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(0.1, 0.55, -0.24), put(0.19, 0.3, -0.24), put(0.175, 0.3, -0.24), put(0.085, 0.55, -0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(-0.335, 0.3, -0.24), put(-0.35, 0.3, -0.24), put(-0.26, 0.55, -0.24),put(-0.245, 0.55, -0.24)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[factorySkin], 1,1,1));
		
		v = new vector[]{put(-0.26, 0.55, -0.225),put(-0.21, 0.55, -0.225), put(-0.21, 0.3, -0.225), put(-0.35, 0.3, -0.225), };
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.26, 0.55, -0.225), put(0.1, 0.55, -0.225), put(-0.26, 0, -0.225), mainThread.textures[12], 1,1,1));
		polygons[polyIndex].diffuse_I-=5;
		
		v = new vector[]{  put(0.05, 0.55, -0.225),put(0.1, 0.55, -0.225),put(0.19, 0.3, -0.225), put(0.05, 0.3, -0.225)};
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.26, 0.55, -0.225), put(0.1, 0.55, -0.225), put(-0.26, 0, -0.225), mainThread.textures[12], 1,1,1));
		polygons[polyIndex].diffuse_I-=5;
		
		v= new vector[]{put(-0.21, 0.55, -0.225), put(0.05, 0.55, -0.225), put(0.05, 0.5, -0.225), put(-0.21, 0.5, -0.225)};
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.26, 0.55, -0.225), put(0.1, 0.55, -0.225), put(-0.26, 0, -0.225), mainThread.textures[12], 1,1,1));
		polygons[polyIndex].diffuse_I-=5;
		
		v = new vector[]{put(-0.21, 0.55, -0.225), put(-0.21, 0.55, -0.19), put(-0.21, 0.3, -0.19), put(-0.21, 0.3, -0.225)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.7f,1));
	
		v = new vector[]{put(0.05, 0.55, -0.19), put(0.05, 0.55, -0.225),put(0.05, 0.3, -0.225), put(0.05, 0.3, -0.19)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.7f,1));
		
		
		int doorIndex = 31;
		if(teamNo != 0)
			doorIndex = 32;
		
		
		//door lower
		float a = -0.215f;
		float b = a +0.03f;
		float c = b + 0.01f;
		float d = c + 0.03f;
		float e = d + 0.01f;
		float f = e + 0.03f;
		float g = f + 0.01f;
		float h = g + 0.03f;
		float i = h + 0.01f;
		float j = i + 0.03f;
		float k = j + 0.01f;
		float l = k + 0.03f;
		float m = l + 0.01f;
		float n = m + 0.03f;
		
	
		
		v = new vector[]{put(-0.21, 0.38, -0.215), put(0.05, 0.38, -0.215), put(0.05, 0.3, -0.215), put(-0.21, 0.3, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 1,0.5f,1));
		
		float v1 = a;
		float v2 = a;
		float v3 = b;
		float v4 = c;
		float v5 = d;
		
		
		v = new vector[]{put(v1, 0.38, -0.215), put(v2, 0.42, -0.215),put(v3, 0.42, -0.215), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42, -0.205), put(v2, 0.42, -0.215), put(v1, 0.38, -0.215), put(v1, 0.38, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.8f,1));
		
		v = new vector[]{put(v2, 0.42, -0.215), put(v2, 0.42, -0.205), put(v3, 0.42, -0.205), put(v3, 0.42, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.8f,1));
		
		v = new vector[]{put(v3, 0.42, -0.215), put(v3, 0.42, -0.205), put(v4, 0.38, -0.205), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.8f,1));
		
		v = new vector[]{put(v4, 0.38, -0.215), put(v4, 0.38, -0.205), put(v5, 0.38, -0.205), put(v5, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,0.8f,1));
		
		v1 = d;
		v2 = e;
		v3 = f;
		v4 = g;
		v5 = h;
		
		
		v = new vector[]{put(v1, 0.38, -0.215), put(v2, 0.42, -0.215),put(v3, 0.42, -0.215), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42, -0.205), put(v2, 0.42, -0.215), put(v1, 0.38, -0.215), put(v1, 0.38, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v2, 0.42, -0.215), put(v2, 0.42, -0.205), put(v3, 0.42, -0.205), put(v3, 0.42, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v3, 0.42, -0.215), put(v3, 0.42, -0.205), put(v4, 0.38, -0.205), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v4, 0.38, -0.215), put(v4, 0.38, -0.205), put(v5, 0.38, -0.205), put(v5, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v1 = h;
		v2 = i;
		v3 = j;
		v4 = k;
		v5 = l;
		
		
		v = new vector[]{put(v1, 0.38, -0.215), put(v2, 0.42, -0.215),put(v3, 0.42, -0.215), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42, -0.205), put(v2, 0.42, -0.215), put(v1, 0.38, -0.215), put(v1, 0.38, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v2, 0.42, -0.215), put(v2, 0.42, -0.205), put(v3, 0.42, -0.205), put(v3, 0.42, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v3, 0.42, -0.215), put(v3, 0.42, -0.205), put(v4, 0.38, -0.205), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v4, 0.38, -0.215), put(v4, 0.38, -0.205), put(v5, 0.38, -0.205), put(v5, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v1 = l;
		v2 = m;
		v3 = n;
		v4 = n;
		
		
		
		v = new vector[]{put(v1, 0.38, -0.215), put(v2, 0.42, -0.215),put(v3, 0.42, -0.215), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42, -0.205), put(v2, 0.42, -0.215), put(v1, 0.38, -0.215), put(v1, 0.38, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v2, 0.42, -0.215), put(v2, 0.42, -0.205), put(v3, 0.42, -0.205), put(v3, 0.42, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v3, 0.42, -0.215), put(v3, 0.42, -0.205), put(v4, 0.38, -0.205), put(v4, 0.38, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		
		doorLower = new polygon3D[20];
		for(int count = 34; count < 54; count++){
			doorLower[count - 34] = polygons[count];
		}
		
		
		
		
		//door upper
		v = new vector[]{put(-0.21, 0.57, -0.215), put(0.05, 0.57, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.42, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 1,0.5f,1));
		
		v = new vector[]{put(-0.21, 0.42, -0.205), put(0.05, 0.42, -0.205), put(0.05, 0.57, -0.205), put(-0.21, 0.57, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 1,0.5f,1));
		
		v = new vector[]{put(-0.21, 0.57, -0.215), put(-0.21, 0.57, -0.205), put(0.05, 0.57, -0.205), put(0.05, 0.57, -0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.1f,1f,1));
		
		v = new vector[]{put(-0.21, 0.57, -0.205), put(-0.21, 0.57, -0.215), put(-0.21, 0.42, -0.215), put(-0.21, 0.42, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.1f,1f,1));
		
		v = new vector[]{put(0.05, 0.42, -0.205), put(0.05, 0.42, -0.215), put(0.05, 0.57, -0.215), put(0.05, 0.57, -0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.1f,1f,1));
		

		v1 = b;
		v2 = e;
		v3 = d;
		v4 = c;
		
		v = new vector[]{put(v1, 0.42,-0.215), put(v2, 0.42,-0.215), put(v3, 0.38,-0.215), put(v4, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42,-0.215), put(v2, 0.42,-0.205), put(v3, 0.38,-0.205),put(v3, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v1, 0.42,-0.205), put(v1, 0.42,-0.215), put(v4, 0.38,-0.215), put(v4, 0.38,-0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v1 = f;
		v2 = i;
		v3 = h;
		v4 = g;
		
		v = new vector[]{put(v1, 0.42,-0.215), put(v2, 0.42,-0.215), put(v3, 0.38,-0.215), put(v4, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42,-0.215), put(v2, 0.42,-0.205), put(v3, 0.38,-0.205),put(v3, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v1, 0.42,-0.205), put(v1, 0.42,-0.215), put(v4, 0.38,-0.215), put(v4, 0.38,-0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v1 = j;
		v2 = m;
		v3 = l;
		v4 = k;
		
		v = new vector[]{put(v1, 0.42,-0.215), put(v2, 0.42,-0.215), put(v3, 0.38,-0.215), put(v4, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v,put(-0.21, 0.42, -0.215), put(0.05, 0.42, -0.215), put(-0.21, 0.38, -0.215), mainThread.textures[doorIndex], 7,0.7f,1));
		
		v = new vector[]{put(v2, 0.42,-0.215), put(v2, 0.42,-0.205), put(v3, 0.38,-0.205),put(v3, 0.38,-0.215)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		v = new vector[]{put(v1, 0.42,-0.205), put(v1, 0.42,-0.215), put(v4, 0.38,-0.215), put(v4, 0.38,-0.205)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[27], 0.1f,1f,1));
		
		doorUpper = new polygon3D[14];
		for(int count = 54; count < 68; count++){
			doorUpper[count - 54] = polygons[count];
		}
		
		
		//power tower A
		float r = 0.08f;
		float delta = (float)Math.PI/8;
		float w = 0.29f;
		h = 0.12f;
		powerTowerCenterA = put(w, 0, h);

		for(i = 0; i < 16; i++){
			v = new vector[]{put(r*Math.cos(i*delta) + w, 0.5, r*Math.sin(i*delta) + h),
							 put(r*Math.cos((i+1)*delta) + w, 0.5, r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos((i+1)*delta) + w, 0.3,  r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos(i*delta) + w, 0.3, r*Math.sin(i*delta)+h)
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			tempVector1.add(tempVector);
			
			int p = (int)i % 8;
		    for(j = 0; j < p; j++){
		    	tempVector0.subtract(tempVector);
		    	tempVector1.subtract(tempVector);
		    	tempVector3.subtract(tempVector);
		    }
			
		    change(w,0.5f,h, tempVector);
			polyIndex = addPolygon(polygons,  new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,1f,1));
			polygons[polyIndex].textureScaledWidth = (int)(polygons[polyIndex].myTexture.width*0.5);
			polygons[polyIndex].createShadeSpan(tempVector, v[0].myClone(), v[1]);
		}
		
		float r1= r - 0.02f;
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r1*Math.cos(i*delta) + w, 0.515, r1*Math.sin(i*delta) + h),
							 put(r1*Math.cos((i+1)*delta) + w, 0.515, r1*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos((i+1)*delta) + w, 0.5,  r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos(i*delta) + w, 0.5, r*Math.sin(i*delta)+h)
							};
			
		
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[12], 0.1f,0.1f,1));
		}
		
		for(i = 0; i < 16; i++){
			v = new vector[]{ put(r1*Math.cos(i*delta) + w, 0.3, r1*Math.sin(i*delta)+h),
					 	put(r1*Math.cos((i+1)*delta) + w, 0.3,  r1*Math.sin((i+1)*delta)+h),
					 	put(r1*Math.cos((i+1)*delta) + w, 0.515, r1*Math.sin((i+1)*delta)+h),
					 	put(r1*Math.cos(i*delta) + w, 0.515, r1*Math.sin(i*delta) + h)
							};
			
		
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[12], 0.1f,1f,1));
		}
		
		float r2 = r1-0.035f;
		v = new vector[16];
		for(i = 0; i < 16; i++){
			v[15 - (int)i] = put(r2*Math.cos((i+1)*delta) + w, 0.505, r2*Math.sin((i+1)*delta)+h);
		}
		polyIndex = addPolygon(polygons,  new polygon3D(v, put(0,0.505, 1),put(1,0.505, 1), put(0,0.505, 0),  mainThread.textures[26], 5f,5f,1));
		
		for(i = 0; i < 16; i++){
			v = new vector[]{ put(r2*Math.cos(i*delta) + w, 0.505, r2*Math.sin(i*delta) + h),
					put(r2*Math.cos((i+1)*delta) + w, 0.505, r2*Math.sin((i+1)*delta)+h),
					put(r2*Math.cos((i+1)*delta) + w, 0.47,  r2*Math.sin((i+1)*delta)+h),
					put(r2*Math.cos(i*delta) + w, 0.47, r2*Math.sin(i*delta)+h)
						};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[26], 1f,1f,1));
		}
		
		float r3 = r2 + 0.03f;
		fanA = new polygon3D[32];
		for(i = 0; i < 16; i++){
			v = new vector[]{put(w,0.505, h), put(r3*Math.cos(i*delta) + w, 0.505, r3*Math.sin(i*delta) + h), put(r3*Math.cos(i*delta) + w, 0.47, r3*Math.sin(i*delta) + h), put(w,0.47, h)};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 1f,1f,1));
			fanA[(int)i*2] = polygons[polyIndex];
			
			v = new vector[]{put(w,0.47, h), put(r3*Math.cos(i*delta) + w, 0.47, r3*Math.sin(i*delta) + h), put(r3*Math.cos(i*delta) + w, 0.505, r3*Math.sin(i*delta) + h), put(w,0.505, h)};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 1f,1f,1));
			fanA[(int)i*2 + 1] = polygons[polyIndex];
		}
		
	
		v = new vector[16];
		for(i = 0; i < 16; i++){
			v[15 - (int)i] = put(r1*Math.cos((i+1)*delta) + w, 0.49, r1*Math.sin((i+1)*delta)+h);
		}
		distortionA = addPolygon(polygons,  new polygon3D(v, put(0,0.49, 1),put(1,0.49, 1), put(0,0.49, 0),  mainThread.textures[54], 5f,5f,6));
		
		
		
		
		//power tower B
		h = -0.12f;
		powerTowerCenterB = put(w, 0, h);
		
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r*Math.cos(i*delta) + w, 0.5, r*Math.sin(i*delta) + h),
							 put(r*Math.cos((i+1)*delta) + w, 0.5, r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos((i+1)*delta) + w, 0.3,  r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos(i*delta) + w, 0.3, r*Math.sin(i*delta)+h)
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			tempVector1.add(tempVector);
			
			int p = (int)i % 8;
		    for(j = 0; j < p; j++){
		    	tempVector0.subtract(tempVector);
		    	tempVector1.subtract(tempVector);
		    	tempVector3.subtract(tempVector);
		    }
			
		    change(w,0.5f,h, tempVector);
			polyIndex = addPolygon(polygons,  new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,1f,1));
			polygons[polyIndex].textureScaledWidth = (int)(polygons[polyIndex].myTexture.width*0.5);
			polygons[polyIndex].createShadeSpan(tempVector, v[0].myClone(), v[1]);
		}
		
		r1= r - 0.02f;
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r1*Math.cos(i*delta) + w, 0.515, r1*Math.sin(i*delta) + h),
							 put(r1*Math.cos((i+1)*delta) + w, 0.515, r1*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos((i+1)*delta) + w, 0.5,  r*Math.sin((i+1)*delta)+h),
							 put(r*Math.cos(i*delta) + w, 0.5, r*Math.sin(i*delta)+h)
							};
			
		
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[12], 0.1f,0.1f,1));
		}
		
		for(i = 0; i < 16; i++){
			v = new vector[]{ put(r1*Math.cos(i*delta) + w, 0.3, r1*Math.sin(i*delta)+h),
					 	put(r1*Math.cos((i+1)*delta) + w, 0.3,  r1*Math.sin((i+1)*delta)+h),
					 	put(r1*Math.cos((i+1)*delta) + w, 0.515, r1*Math.sin((i+1)*delta)+h),
					 	put(r1*Math.cos(i*delta) + w, 0.515, r1*Math.sin(i*delta) + h)
							};
			
		
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[12], 0.1f,1f,1));
		}
		
		
		v = new vector[16];
		for(i = 0; i < 16; i++){
			v[15 - (int)i] = put(r2*Math.cos((i+1)*delta) + w, 0.505, r2*Math.sin((i+1)*delta)+h);
		}
		polyIndex = addPolygon(polygons,  new polygon3D(v, put(0,0.505, 1),put(1,0.505, 1), put(0,0.505, 0),  mainThread.textures[26], 5f,5f,1));
		
		for(i = 0; i < 16; i++){
			v = new vector[]{ put(r2*Math.cos(i*delta) + w, 0.505, r2*Math.sin(i*delta) + h),
					put(r2*Math.cos((i+1)*delta) + w, 0.505, r2*Math.sin((i+1)*delta)+h),
					put(r2*Math.cos((i+1)*delta) + w, 0.47,  r2*Math.sin((i+1)*delta)+h),
					put(r2*Math.cos(i*delta) + w, 0.47, r2*Math.sin(i*delta)+h)
						};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[26], 1f,1f,1));
		}
		
		fanB = new polygon3D[32];
		for(i = 0; i < 16; i++){
			v = new vector[]{put(w,0.505, h), put(r3*Math.cos(i*delta) + w, 0.505, r3*Math.sin(i*delta) + h), put(r3*Math.cos(i*delta) + w, 0.47, r3*Math.sin(i*delta) + h), put(w,0.47, h)};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 1f,1f,1));
			fanB[(int)i*2] = polygons[polyIndex];
			
			v = new vector[]{put(w,0.47, h), put(r3*Math.cos(i*delta) + w, 0.47, r3*Math.sin(i*delta) + h), put(r3*Math.cos(i*delta) + w, 0.505, r3*Math.sin(i*delta) + h), put(w,0.505, h)};
			polyIndex = addPolygon(polygons,  new polygon3D(v, v[0].myClone(),v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 1f,1f,1));
			fanB[(int)i*2 + 1] = polygons[polyIndex];
		}
		
		v = new vector[16];
		for(i = 0; i < 16; i++){
			v[15 - (int)i] = put(r1*Math.cos((i+1)*delta) + w, 0.48, r1*Math.sin((i+1)*delta)+h);
		}
		distortionB = addPolygon(polygons,  new polygon3D(v, put(0,0.48, 1),put(1,0.48, 1), put(0,0.48, 0),  mainThread.textures[54], 5f,5f,6));
		
		
		
		//Concrete foundation
		v = new vector[]{put(-0.38, 0.3, -0.2), put(0.38, 0.3, -0.2), put(0.38, 0.3, -0.225),  put(0.345, 0.3, -0.26), put(-0.345, 0.3, -0.26), put(-0.38, 0.3, -0.225)};
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;

		v = new vector[]{put(-0.38, 0.3, 0.225), put(-0.345, 0.3, 0.26),  put(0.345, 0.3, 0.26), put(0.38, 0.3, 0.225), put(0.38, 0.3, 0.2), put(-0.38, 0.3, 0.2)};
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
	
		v = new vector[]{put(-0.14, 0.3, 0.225), put(0.38, 0.3, 0.225), put(0.38, 0.3, -0.225), put(-0.14, 0.3, -0.225) };
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
	
		v = new vector[]{put(-0.38, 0.3, 0.225), put(-0.33, 0.3, 0.225), put(-0.33, 0.3, -0.225), put(-0.38, 0.3, -0.225) };
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
		
		v = new vector[]{put(-0.345, 0.3, 0.225), put(-0.14, 0.3, 0.225), put(-0.14, 0.3, -0.225), put(-0.345, 0.3, -0.225) };
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
		
		v = new vector[]{put(-0.345, 0.3, -0.26), put(0.345, 0.3, -0.26), put(0.345, 0.28, -0.26), put(-0.345, 0.28, -0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		polygons[polyIndex].shadowBias = 5000;
		
		v = new vector[]{put(-0.345, 0.28,0.26), put(0.345, 0.28, 0.26), put(0.345, 0.3, 0.26), put(-0.345, 0.3, 0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.38, 0.3, 0.225), put(-0.38, 0.3, -0.225),  put(-0.38, 0.28, -0.225), put(-0.38, 0.28, 0.225)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.38, 0.28, 0.225),  put(0.38, 0.28, -0.225), put(0.38, 0.3, -0.225), put(0.38, 0.3, 0.225)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.345, 0.3, 0.26), put(-0.38, 0.3, 0.225), put(-0.38, 0.28, 0.225), put(-0.345, 0.28, 0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.345, 0.28, 0.26), put(0.38, 0.28, 0.225), put(0.38, 0.3, 0.225), put(0.345, 0.3, 0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(-0.345, 0.28, -0.26), put(-0.38, 0.28, -0.225), put(-0.38, 0.3, -0.225), put(-0.345, 0.3, -0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
		v = new vector[]{put(0.345, 0.3, -0.26), put(0.38, 0.3, -0.225), put(0.38, 0.28, -0.225), put(0.345, 0.28, -0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
	
		
		for(int z = 0; z < numOfPolygons; z++){
			polygons[z].findDiffuse();
			polygons[z].parentObject = this;
			
		}
	}
	
	
	public int addPolygon(polygon3D[] polys, polygon3D poly){
		for(int i = 0; i < polys.length; i++){
			if(polys[i] == null){
				polys[i] = poly;
				numOfPolygons++;
				return i;
			}
		}
		return -1;
	}
	
	//update the model 
	public void update(){

		//update tech tree info
		canBuildLightTank = theBaseInfo.canBuildLightTank;
		canBuildRocketTank = theBaseInfo.canBuildRocketTank;
		canBuildHarvester = theBaseInfo.canBuildHarvester;
		canBuildDrone = theBaseInfo.canBuildDrone;
		canBuildMCV = theBaseInfo.canBuildMCV;
		canBuildStealthTank = theBaseInfo.canBuildStealthTank;
		canBuildHeavyTank = theBaseInfo.canBuildHeavyTank;
		
		
		//process emerging from  ground animation
		if(centre.y < -0.79f){
			centre.y+=0.02f;
			
			float delta_h = 0.02f;
			if(centre.y > -0.79f){
				delta_h = 0.02f -0.79f - centre.y;
				centre.y = -0.79f;
			}
			
			for(int i = 0; i < numOfPolygons; i++){		
				polygons[i].origin.y+=delta_h;
				polygons[i].rightEnd.y+=delta_h;
				polygons[i].bottomEnd.y+=delta_h;
				
				for(int j = 0; j < polygons[i].vertex3D.length; j++){
					polygons[i].vertex3D[j].y+=delta_h;
				}	
			}
			
			shadowvertex0.y+=delta_h;
			shadowvertex1.y+=delta_h;
			shadowvertex2.y+=delta_h;
			shadowvertex3.y+=delta_h;
			
			
			//the building is invulnerable during emerging stage
			currentHP = maxHP;
		}

		if(underAttackCountDown > 0)
			underAttackCountDown--;
		
		
		//check if building has been destroyed
		if(currentHP <= 0){
			countDownToDeath--;
			
			if(countDownToDeath == 0){
				//spawn an explosion when the object is destroyed
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x;
				tempFloat[1] = centre.y + 0.45f;
				tempFloat[2] = centre.z;
				tempFloat[3] = 3.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 7;
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				theAssetManager.removeObject(this); 
				
				
				//removeFromGridMap();
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][0] = null; 
				
				
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][1] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][2] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][3] = null; 


				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[4]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[5]][4] = null; 
				
				int tileIndex6 = tileIndex[5] + 128;
				int tileIndex7 = tileIndex[5] + 128 - 1;
				int tileIndex8 = tileIndex[5] + 128 + 1;
				mainThread.gridMap.tiles[tileIndex6][4] = null;
				mainThread.gridMap.tiles[tileIndex7][4] = null;
				mainThread.gridMap.tiles[tileIndex8][4] = null;
				
			
				theBaseInfo.numberOfFactory--;
				
				if(deliveredUnit != null)
					deliveredUnit.disableUnitLevelAI = false;
					
				cancelBuilding();
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=40;
				
				return;
			}else{
				
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x + (float)Math.random()*0.6f - 0.3f;
				tempFloat[1] = centre.y + 0.45f;
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
			if(mainThread.gameFrame%8==0 && theBaseInfo.currentCredit > 0 && currentHP <maxHP){
				currentHP+=2;
				theBaseInfo.currentCredit--;
				if(currentHP > maxHP)
					currentHP = maxHP;
			}
		}
		
		//process building event (at half speed when lower power)
		if(currentStatus == isBuilding){
			
			if(!(theBaseInfo.lowPower && mainThread.gameFrame%2==0)){
			
				//light tank event
				if(lightTankProgress < 240){
					grayAllOtherIcons(lightTankType, lightTankProgress);
					if(theBaseInfo.currentCredit >0){
						
						theBaseInfo.currentCredit--;
						creditSpentOnBuilding++;
						lightTankProgress = 240 * creditSpentOnBuilding/300;
					}
				}
				if(lightTankProgress == 240){
					if(!isDeliveringUnit){
						lightTank o = new lightTank(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
						
						if(teamNo == 0 && techCenter.lightTankResearched_player){
							o.attackRange = 1.99f;
						}else if(teamNo != 0 && techCenter.lightTankResearched_enemy){
							o.attackRange = 1.99f;
						}
						
						deliveredUnit = o;
						o.disableUnitLevelAI = true;
						
						mainThread.theAssetManager.addLightTank(o);
						lightTankProgress = 255;
						removelItemFromProductionQueue(lightTankType);
						isDeliveringUnit = true;
					}
				}
				
				//rocket tank event
				if(rocketTankProgress < 240){
					grayAllOtherIcons(rocketTankType, rocketTankProgress);
					if(theBaseInfo.currentCredit >0){
						
						theBaseInfo.currentCredit--;
						creditSpentOnBuilding++;
						rocketTankProgress = 240 * creditSpentOnBuilding/450;
					}
				}
				if(rocketTankProgress == 240){
					if(!isDeliveringUnit){
						rocketTank o = new rocketTank(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
						if(teamNo == 0 && techCenter.rocketTankResearched_player){
							o.damageMultiplier =2;
						}else if(teamNo != 0 && techCenter.rocketTankResearched_enemy){
							o.damageMultiplier =2;
						}
					
						deliveredUnit = o; 
						o.disableUnitLevelAI = true;
						
						mainThread.theAssetManager.addRocketTank(o);
						rocketTankProgress = 255;
						removelItemFromProductionQueue(rocketTankType);
						isDeliveringUnit = true;
					}
				}
				
				//harvester event
				if(harvesterProgress < 240){
					grayAllOtherIcons(harvesterType, harvesterProgress);
					
					if(!canBuildHarvester){
						int num = numOfHarvesterOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(harvesterType);
					}else{
						if(theBaseInfo.currentCredit >0){
							
							theBaseInfo.currentCredit--;
							creditSpentOnBuilding++;
							harvesterProgress = 240 * creditSpentOnBuilding/800;
						}
					}
				}
				if(harvesterProgress == 240){
					if(!canBuildHarvester){
						int num = numOfHarvesterOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(harvesterType);
					}else{
						if(!isDeliveringUnit){
							harvester o = new harvester(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
							
							if(teamNo == 0 && communicationCenter.harvesterSpeedResearched_player){
								o.speed = 0.014f;
								o.bodyTurnRate = 8;
							}else if(teamNo != 0 && communicationCenter.harvesterSpeedResearched_enemy){
								o.speed = 0.014f;
								o.bodyTurnRate = 8;
							}
							
							deliveredUnit = o;
							o.disableUnitLevelAI = true;
							
							mainThread.theAssetManager.addHarvester(o);
							harvesterProgress = 255;
							removelItemFromProductionQueue(harvesterType);
							isDeliveringUnit = true;
						}
					}
				}
				
				//MCV event
				if(MCVProgress < 240){
					grayAllOtherIcons(MCVType, MCVProgress);
					
					if(!canBuildMCV){
						int num = numOfMCVOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(MCVType);
					}else{
						if(theBaseInfo.currentCredit >0){
							theBaseInfo.currentCredit--;
							creditSpentOnBuilding++;
							MCVProgress = 240 * creditSpentOnBuilding/1700;
						}
					}
				}
				if(MCVProgress == 240){
					if(!canBuildMCV){
						int num = numOfMCVOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(MCVType);
					}else{
						if(!isDeliveringUnit){
							constructionVehicle o = new constructionVehicle(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
							deliveredUnit = o;
							o.disableUnitLevelAI = true;
							
							mainThread.theAssetManager.addConstructionVehicle(o);
							MCVProgress = 255;
							removelItemFromProductionQueue(MCVType);
							isDeliveringUnit = true;
						}
					}
				}
				
				
				//drone event
				if(droneProgress < 240){
					grayAllOtherIcons(droneType, droneProgress);
					if(theBaseInfo.currentCredit >0){
						
						theBaseInfo.currentCredit--;
						creditSpentOnBuilding++;
						droneProgress = 240 * creditSpentOnBuilding/250;
					}
				}
				if(droneProgress == 240){
					if(!isDeliveringUnit){
						drone o = new drone(new vector(centre.x -0.07f,-0.27f, centre.z - 0.03f), 180, this);
						numOfDrones++;
						deliveredUnit = o;
						o.disableUnitLevelAI = true;
						
						mainThread.theAssetManager.addDrone(o);
						droneProgress = 255;
						removelItemFromProductionQueue(droneType);
						isDeliveringUnit = true;
					}
				}
				
				//stealth tank event
				if(stealthTankProgress < 240){
					grayAllOtherIcons(stealthTankType, stealthTankProgress);
					if(!canBuildStealthTank){
						int num = numOfStealthTankOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(stealthTankType);
					}else{
					
						if(theBaseInfo.currentCredit >0){
						
							theBaseInfo.currentCredit--;
							creditSpentOnBuilding++;
							stealthTankProgress = 240 * creditSpentOnBuilding/600;
						}
					}
				}
				if(stealthTankProgress == 240){
					if(!canBuildStealthTank){
						int num = numOfStealthTankOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(stealthTankType);
					}else{
						if(!isDeliveringUnit){
							stealthTank o = new stealthTank(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
							if(teamNo == 0 && techCenter.stealthTankResearched_player){
								o.hasMultiShotUpgrade = true;
							}else if(teamNo != 0 && techCenter.stealthTankResearched_enemy){
								o.hasMultiShotUpgrade = true;
							}
							
							deliveredUnit = o;
							o.disableUnitLevelAI = true;
							
							mainThread.theAssetManager.addStealthTank(o);
							stealthTankProgress = 255;
							removelItemFromProductionQueue(stealthTankType);
							isDeliveringUnit = true;
						}
					}
				}
				
				//heavy tank event
				if(heavyTankProgress < 240){
					grayAllOtherIcons(heavyTankType, heavyTankProgress);
					if(!canBuildHeavyTank){
						int num = numOfHeavyTankOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(heavyTankType);
					}else{
					
						if(theBaseInfo.currentCredit >0){
						
							theBaseInfo.currentCredit--;
							creditSpentOnBuilding++;
							heavyTankProgress = 240 * creditSpentOnBuilding/1100;
						}
					}
				}
				if(heavyTankProgress == 240){
					if(!canBuildHeavyTank){
						int num = numOfHeavyTankOnQueue;
						for(int i = 0; i <= num; i++)
							cancelItemFromProductionQueue(heavyTankType);
					}else{
						if(!isDeliveringUnit){
							heavyTank o = new heavyTank(new vector(centre.x -0.07f,-0.3f, centre.z - 0.03f), 180, teamNo);
							if(teamNo == 0 && techCenter.heavyTankResearched_player){
								o.canSelfRepair = true;
							}else if(teamNo != 0 && techCenter.heavyTankResearched_enemy){
								o.canSelfRepair = true;
							}
							deliveredUnit = o;
							o.disableUnitLevelAI = true;
							
							mainThread.theAssetManager.addHeavyTank(o);
							heavyTankProgress = 255;
							removelItemFromProductionQueue(heavyTankType);
							isDeliveringUnit = true;
						}
					}
				}
				
			}
		}else{
			lightTankProgress = 255;
			rocketTankProgress = 255;
			harvesterProgress = 255;
			droneProgress = 255;
			MCVProgress = 255;
			stealthTankProgress = 255;
			heavyTankProgress = 255;
		}
		
		
		//process delivering Unit event
		if(isDeliveringUnit){
			
			if(deliveredUnit != null && deliveredUnit.currentHP <=0){
				deliveredUnit = null;
				closingDoor = true;
				openingDoor = false;
			}
			
			if(deliveredUnit!= null){
				if(doorClosed){
					closingDoor = false;
					doorClosed = false;
					openingDoor = true;
				}
				
				if(doorOpened){
					
					xPos = (int)((deliveredUnit.centre.x)*64) - 8;
					
					if(deliveredUnit.centre.z - (centre.z - 0.375f) <= 0.01f){
						if(deliveredUnit.type == droneType){
							deliveredUnit.disableUnitLevelAI = false;
						}else{
							deliveredUnit.movement.set(0,0,deliveredUnit.centre.z - (centre.z - 0.375f));
							deliveredUnit.disableUnitLevelAI = false;
							deliveredUnit.leftFactory = true;
							
							if(deliveredUnit.type == harvesterType){
								
								if(targetGoldMine != null)
									deliveredUnit.harvest(targetGoldMine);
								else
									moveDeliveredUnitToRallyPoint();
								harvester o = (harvester)deliveredUnit;
								o.heuristicRecalculationCountDown = 1;
							
								
							}else{
								moveDeliveredUnitToRallyPoint();
							}
						}
						
						deliveredUnit = null;
						closingDoor = true;
						openingDoor = false;
						doorOpened = false;
					
					}else{
					
						yPos = (int)((deliveredUnit.centre.z -0.01f)*64) + 8;
						boolean canMove = true;
						
						if(deliveredUnit.type != droneType){
							
							
							xPos_old = deliveredUnit.boundary2D.x1;
							yPos_old = deliveredUnit.boundary2D.y1;
							
							deliveredUnit.boundary2D.setOrigin(xPos, yPos);
							
							
							for(int i = 0; i < 4; i++){
								solidObject o = mainThread.gridMap.tiles[tileIndex[5]+128][i];
								if(o != null && o != deliveredUnit){
									if(o.boundary2D.intersect(deliveredUnit.boundary2D)){
										canMove = false;
										break;
									}
								}
							}
							
							for(int i = 0; i < 4; i++){
								solidObject o = mainThread.gridMap.tiles[tileIndex[5]+127][i];
								if(o != null && o != deliveredUnit){
									if(o.boundary2D.intersect(deliveredUnit.boundary2D)){
										canMove = false;
										break;
									}
								}
							}
						
						}
						if(!canMove){
							deliveredUnit.movement.reset();
							deliveredUnit.boundary2D.setOrigin(xPos_old, yPos_old);
						}else{
							
							if(deliveredUnit.type != droneType){
								deliveredUnit.boundary2D.setOrigin(xPos_old, yPos_old);
								deliveredUnit.movement.set(0,0,-0.01f);
							}else{
								deliveredUnit.centre.z-=0.01f;
							}
						}
					}
					
					

					
	
				}
			}
			
			float dHeight = 0.005f;
			if(openingDoor){
				for(int i = 0; i < doorUpper.length; i++){
					if(i == 0 && !doorHeightMarked){
						doorHeightMark = doorUpper[i].origin.y;
						doorHeightMarked = true;
					}
					
					doorUpper[i].origin.y+=dHeight;
					doorUpper[i].bottomEnd.y+=dHeight;
					doorUpper[i].rightEnd.y+=dHeight;
					
					for(int j = 0; j < doorUpper[i].vertex3D.length; j++){
						doorUpper[i].vertex3D[j].y+=dHeight;
					}
				}
				
				for(int i = 0; i < doorLower.length; i++){
					doorLower[i].origin.y-=dHeight;
					doorLower[i].bottomEnd.y-=dHeight;
					doorLower[i].rightEnd.y-=dHeight;
					
					for(int j = 0; j < doorLower[i].vertex3D.length; j++){
						doorLower[i].vertex3D[j].y-=dHeight;
					}
				}
				if(doorUpper[0].origin.y > -0.256){
					openingDoor = false;
					doorOpened = true;
				}
			}
			
			if(closingDoor){
				for(int i = 0; i < doorUpper.length; i++){
					doorUpper[i].origin.y-=dHeight;
					doorUpper[i].bottomEnd.y-=dHeight;
					doorUpper[i].rightEnd.y-=dHeight;
					
					for(int j = 0; j < doorUpper[i].vertex3D.length; j++){
						doorUpper[i].vertex3D[j].y-=dHeight;
					}
				}
				for(int i = 0; i < doorLower.length; i++){
					doorLower[i].origin.y+=dHeight;
					doorLower[i].bottomEnd.y+=dHeight;
					doorLower[i].rightEnd.y+=dHeight;
					
					for(int j = 0; j < doorLower[i].vertex3D.length; j++){
						doorLower[i].vertex3D[j].y+=dHeight;
					}
				}
				if(doorUpper[0].origin.y <= doorHeightMark){
					closingDoor = false;
					isDeliveringUnit = false;
					doorClosed = true;
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
			int xPos = boundary2D.x1/16 - 8 + 10 + 1;
			int yPos = 127 - boundary2D.y1/16 - 8 + 10;
			
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
		   theAssetManager.minimapBitmap[tileIndex[5]])
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
		
		//update power tower animation
		if(!visible)
			return;
		
		polygons[distortionA].origin.x-=0.001f;
		polygons[distortionA].rightEnd.x-=0.001f;
		polygons[distortionA].bottomEnd.x -= 0.001f;
		polygons[distortionA].origin.z-=0.001f;
		polygons[distortionA].rightEnd.z-=0.001f;
		polygons[distortionA].bottomEnd.z -= 0.001f;
		
		for(int i = 0; i < fanA.length; i++){
			fanA[i].origin.subtract(powerTowerCenterA);
			fanA[i].origin.rotate_XZ(4);
			fanA[i].origin.add(powerTowerCenterA);
			
				
			fanA[i].bottomEnd.subtract(powerTowerCenterA);
			fanA[i].bottomEnd.rotate_XZ(4);
			fanA[i].bottomEnd.add(powerTowerCenterA);
		
				
			fanA[i].rightEnd.subtract(powerTowerCenterA);
			fanA[i].rightEnd.rotate_XZ(4);
			fanA[i].rightEnd.add(powerTowerCenterA);
		
			for(int j = 0; j < fanA[i].vertex3D.length; j++){
				fanA[i].vertex3D[j].subtract(powerTowerCenterA);
				fanA[i].vertex3D[j].rotate_XZ(4);
				fanA[i].vertex3D[j].add(powerTowerCenterA);
			}
			fanA[i].normal.rotate_XZ(4);
			fanA[i].findDiffuse();
		}
		
		polygons[distortionB].origin.x-=0.001f;
		polygons[distortionB].rightEnd.x-=0.001f;
		polygons[distortionB].bottomEnd.x -= 0.001f;
		polygons[distortionB].origin.z-=0.001f;
		polygons[distortionB].rightEnd.z-=0.001f;
		polygons[distortionB].bottomEnd.z -= 0.001f;
		
		for(int i = 0; i < fanB.length; i++){
			fanB[i].origin.subtract(powerTowerCenterB);
			fanB[i].origin.rotate_XZ(4);
			fanB[i].origin.add(powerTowerCenterB);
			
				
			fanB[i].bottomEnd.subtract(powerTowerCenterB);
			fanB[i].bottomEnd.rotate_XZ(4);
			fanB[i].bottomEnd.add(powerTowerCenterB);
		
				
			fanB[i].rightEnd.subtract(powerTowerCenterB);
			fanB[i].rightEnd.rotate_XZ(4);
			fanB[i].rightEnd.add(powerTowerCenterB);
		
			for(int j = 0; j < fanB[i].vertex3D.length; j++){
				fanB[i].vertex3D[j].subtract(powerTowerCenterB);
				fanB[i].vertex3D[j].rotate_XZ(4);
				fanB[i].vertex3D[j].add(powerTowerCenterB);
			}
			fanB[i].normal.rotate_XZ(4);
			fanB[i].findDiffuse();
		}
	}
	
	public void buildLightTank(){
		if(numOfLightTankOnQueue >= 100)
			return;
		if(canBuildLightTank){
			if(currentStatus != isBuilding){
				lightTankProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfLightTankOnQueue++;
			addToProductionQueue(lightTankType);
		}
	}
	
	public void buildRocketTank(){
		if(numOfRocketTankOnQueue >= 100)
			return;
		if(canBuildRocketTank){
			if(currentStatus != isBuilding){
				rocketTankProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfRocketTankOnQueue++;
			addToProductionQueue(rocketTankType);
		}
	}
	
	public void buildHarvester(){
		if(numOfHarvesterOnQueue >= 100)
			return;
		if(canBuildHarvester){
			if(currentStatus != isBuilding){
				harvesterProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfHarvesterOnQueue++;
			addToProductionQueue(harvesterType);
		}
	}
	
	public void buildDrone(){
		if(numOfDroneOnQueue + numOfDrones == 3)
			return;
		if(canBuildDrone){
			if(currentStatus != isBuilding){
				droneProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfDroneOnQueue++;
			addToProductionQueue(droneType);
		}
	}
	
	public void buildMCV(){
		if(numOfMCVOnQueue >= 100)
			return;
		if(canBuildMCV){
			if(currentStatus != isBuilding){
				MCVProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfMCVOnQueue++;
			addToProductionQueue(MCVType);
		}
	}
	
	public void buildStealthTank(){
		if(numOfStealthTankOnQueue >= 100)
			return;
		if(canBuildStealthTank){
			if(currentStatus != isBuilding){
				stealthTankProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfStealthTankOnQueue++;
			addToProductionQueue(stealthTankType);
		}
	}
	
	public void buildHeavyTank(){
		if(numOfHeavyTankOnQueue >= 100)
			return;
		if(canBuildHeavyTank){
			if(currentStatus != isBuilding){
				heavyTankProgress = 0;
				
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			numOfHeavyTankOnQueue++;
			addToProductionQueue(heavyTankType);
		}
	}
	
	
	public void grayAllOtherIcons(int type, int progress){
		lightTankProgress = 254;
		rocketTankProgress = 254;
		harvesterProgress = 254;
		droneProgress = 254;
		MCVProgress = 254;
		stealthTankProgress = 254;
		heavyTankProgress = 254;
		
		if(type == lightTankType){
			lightTankProgress = progress;
		}
		if(type == rocketTankType){
			rocketTankProgress = progress;
		}
		if(type == harvesterType){
			harvesterProgress = progress;
		}
		if(type == droneType){
			droneProgress = progress;
		}
		if(type == MCVType){
			MCVProgress = progress;
		}
		if(type == stealthTankType){
			stealthTankProgress = progress;
		}
		if(type == heavyTankType){
			heavyTankProgress = progress;
		}
			
			
	}
	
	public void addToProductionQueue(int type){
		for(int i = 0; i < productionQueue.length; i++){
			if(productionQueue[i] == -1){
				productionQueue[i] = (byte)type;
				return;
			}
		}
	}
	
	public void cancelItemFromProductionQueue(int type){
		for(int i = productionQueue.length - 1; i >= 0; i--){
			if(productionQueue[i] == type){
				if(type == lightTankType){
					numOfLightTankOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						lightTankProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == rocketTankType){
					numOfRocketTankOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						rocketTankProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == harvesterType){
					numOfHarvesterOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						harvesterProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == droneType){
					numOfDroneOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						droneProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == MCVType){
					numOfMCVOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						MCVProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == stealthTankType){
					numOfStealthTankOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						stealthTankProgress = 255;
						currentStatus = isIdle;
					}
				}
				if(type == heavyTankType){
					numOfHeavyTankOnQueue--;
					if(i == 0){
						theBaseInfo.currentCredit+=creditSpentOnBuilding;
						creditSpentOnBuilding = 0;
						heavyTankProgress = 255;
						currentStatus = isIdle;
					}
				}
				
				for(int j = i; j < productionQueue.length - 1; j++){
					productionQueue[j] = productionQueue[j+1];
					if(productionQueue[j+1] == -1)
						break;
				}
				productionQueue[productionQueue.length - 1] = -1;
				
				if(i == 0){
					if(productionQueue[i] == lightTankType){
						lightTankProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == rocketTankType){
						rocketTankProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == harvesterType){
						harvesterProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == droneType){
						droneProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == MCVType){
						MCVProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == stealthTankType){
						stealthTankProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}else if(productionQueue[i] == heavyTankType){
						heavyTankProgress = 0;
						currentStatus = isBuilding;
						creditSpentOnBuilding = 0;
					}
				}
				
				return;
			}
		}
	}
	
	public void removelItemFromProductionQueue(int type){
		
		if(type == lightTankType){
			numOfLightTankOnQueue--;
			creditSpentOnBuilding = 0;
			lightTankProgress = 255;
			currentStatus = isIdle;
		}
		if(type == rocketTankType){
			numOfRocketTankOnQueue--;
			creditSpentOnBuilding = 0;
			rocketTankProgress = 255;
			currentStatus = isIdle;
		}
		if(type == harvesterType){
			numOfHarvesterOnQueue--;
			creditSpentOnBuilding = 0;
			harvesterProgress = 255;
			currentStatus = isIdle;
		}
		if(type == droneType){
			numOfDroneOnQueue--;
			creditSpentOnBuilding = 0;
			droneProgress = 255;
			currentStatus = isIdle;
		}
		if(type == MCVType){
			numOfMCVOnQueue--;
			creditSpentOnBuilding = 0;
			MCVProgress = 255;
			currentStatus = isIdle;
		}
		if(type == stealthTankType){
			numOfStealthTankOnQueue--;
			creditSpentOnBuilding = 0;
			stealthTankProgress = 255;
			currentStatus = isIdle;
		}
		if(type == heavyTankType){
			numOfHeavyTankOnQueue--;
			creditSpentOnBuilding = 0;
			heavyTankProgress = 255;
			currentStatus = isIdle;
		}
		
		
		for(int j = 0; j < productionQueue.length - 1; j++){
			productionQueue[j] = productionQueue[j+1];
			if(productionQueue[j+1] == -1)
				break;
		}
		productionQueue[productionQueue.length - 1] = -1;
				
		if(productionQueue[0] != -1){
			if(productionQueue[0] == lightTankType){
				lightTankProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == rocketTankType){
				rocketTankProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == harvesterType){
				harvesterProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == droneType){
				droneProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == MCVType){
				MCVProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == stealthTankType){
				stealthTankProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			if(productionQueue[0] == heavyTankType){
				heavyTankProgress = 0;
				currentStatus = isBuilding;
				creditSpentOnBuilding = 0;
			}
			
		}
	}
	
	//cancel all the building process, refund money spent on current process
	public void cancelBuilding(){
		theBaseInfo.currentCredit+=creditSpentOnBuilding;
		creditSpentOnBuilding = 0;
		currentStatus = isIdle;
	}
	
	//draw the model
	public void draw(){
		if(!visible)
			return;
		for(int i = 0; i < numOfPolygons; i++){
			
			polygons[i].update();
		}
		
		if(centre.y < -0.8f){
			polygons[distortionA].visible = false;
			polygons[distortionB].visible = false;
		}
		
		for(int i = 0; i < numOfPolygons; i++){
			polygons[i].draw();
		}
	}
	
	public void drawRallyPointLine(){
		if(isSelected && teamNo == 0 && mainThread.pc.theSideBarManager.onlyFactorySelected){
			geometry.drawLine(centre, rallyCenter, 0xff00, (byte)16);
		}
		
		if(isSelected && teamNo == 0 && mainThread.pc.theSideBarManager.onlyFactorySelected && targetGoldMine != null){
			geometry.drawLine(centre, targetGoldMine.centre, 0xffff00, (byte)15);
		}
	}
	
	public vector getMovement(){
		return movenment;
	}
	
	
	//factory can't not move, instead it will set its rally point to the destination position
	public void moveTo(float destinationX, float destinationY){	
		if(teamNo != 0 || mainThread.pc.theSideBarManager.onlyFactorySelected){
		
			rallyCenter.set(destinationX, -0.3f, destinationY);
			rallyPointChanged = true;
		}
	}
	
	public void moveDeliveredUnitToRallyPoint(){
		
		
		if(rallyPointChanged){
			if(deliveredUnit.type != 0 && deliveredUnit.type != 1 && deliveredUnit.type != 6 && deliveredUnit.type !=7 ){
				deliveredUnit.moveTo(rallyCenter.x, rallyCenter.z);
				deliveredUnit.currentCommand = solidObject.move;
				deliveredUnit.secondaryCommand = solidObject.StandBy;
				return;
			}else{
				deliveredUnit.attackMoveTo(rallyCenter.x, rallyCenter.z);
				deliveredUnit.currentCommand = solidObject.attackMove;
				deliveredUnit.secondaryCommand = solidObject.attackMove;
				return;
			}
		}
		
		//default rally points
		for(int i = 0; i < rallyPoints.length; i++){
			float rallyX = rallyCenter.x + rallyPoints[i].x;
			float rallyY = rallyCenter.z + rallyPoints[i].z;
		
	
			
			int tileIndex = ((int)(rallyX*64) - 8) /16 + (127 - (((int)(rallyY*64) + 8) - 1)/16)*128;
			
			
			
			
			
			boolean rallyPointClear = true;
			for(int j = 0; j < 4; j ++){
				if(mainThread.gridMap.tiles[tileIndex][j]!= null && mainThread.gridMap.tiles[tileIndex][j] != deliveredUnit){
					probeBlock.width = 8;
					probeBlock.height = 8;
					probeBlock.setOrigin((int)(rallyX*64)-4, (int)(rallyY*64)+4);
					
					if(mainThread.gridMap.tiles[tileIndex][j].boundary2D.intersect(probeBlock)){
	
						rallyPointClear = false;
						break;
					}
				}
			}
			
			if(rallyPointClear){
				deliveredUnit.moveTo(rallyX, rallyY);
				deliveredUnit.currentCommand = solidObject.move;
				deliveredUnit.secondaryCommand = solidObject.StandBy;
				return;
			}
		}
		
	}
	
	public boolean isIdle(){
		return lightTankProgress == 255 && rocketTankProgress == 255 && harvesterProgress == 255 && droneProgress == 255 && MCVProgress == 255 && stealthTankProgress == 255 && heavyTankProgress == 255;
	}
	
	public void harvest(solidObject o){    
		if(targetGoldMine != o)
			targetGoldMine =  (goldMine)o;
		else if(targetGoldMine == o &&  mainThread.pc.theSideBarManager.factoryRallyOnSameGoldMine)
			targetGoldMine = null;
	}
}
