package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

public class stealthTank extends solidObject{
	
	public vector iDirectionBody, jDirectionBody, kDirectionBody, iDirectionTurret, jDirectionTurret, kDirectionTurret;
	
	public vector bodyCenter, turretCenter;
	
	public polygon3D[] body, turret;
	
	public static int maxHP = 80;
	
	//a screen space boundary which is used to test if the tank object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-70,-25,908, 597);
	
	//a screen space boundary which is used to test if the entire tank object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432); 
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1000, 1500);
	
	//a bitmap representation of the vision of the tank for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	public static boolean[] bitmapVisionGainFromAttackingUnit;
	
	//the oreintation of the tank
	public int bodyAngle, turretAngle;
	
	//the angle that the tank have rotated between current  frame and previous frame
	public int bodyAngleSum;
	
	//destination angle
	public int destinationAngle;
	
	//whether the tank has ling of sight to its target
	public boolean hasLineOfSightToTarget;
	
	//attack range
	public int attackCoolDown;
	public vector firingPosition;
	
	//the offsreen angles/movement are the accumulated changes that the object  made during offscreen. 
	public int bodyAngleDelta_offscreen, turretAngleDelta_offscreen; 
	public vector movement_offscreen;
	
	//whether the geometry of the object in world coordinate neesd to be updated in the current frame
	public boolean geometryNeedModify;
	

	public int bodyTurnRate = 10; 
	public int turretTurnRate = 12;
	public int myAttackCooldown= 80;
	
	//once the  tank starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	
	//the time left for stealth tank to become invisible again
	//public int stealthCountDown;
	
	//index of the tiles to check when the tank is idle
	public static int[] tileCheckList;
	
	public static int[] tiles3x3 = new int[]{-129, -128, -127, -1, 0, 1, 127, 128, 129};
	
	//public boolean wasCloaked;
	public int targetCloakingStatus, currentCloakingStatus, currentShadowStatus;
	
	public boolean hasMultiShotUpgrade;
	public static solidObject[] secondaryTargets;
	public static int[] secondaryAttackCheckList;
	
	public stealthTank(vector origin, int bodyAngle, int teamNo){
		speed = 0.015f;
		attackRange = 1.91f;
		groupAttackRange = 1.2f;
		myDamage = 30;
		start = origin.myClone();
		centre = origin.myClone();
		tempCentre = origin.myClone();
		bodyCenter = origin.myClone();
		this.bodyAngle = bodyAngle;
		turretAngle  = bodyAngle;
		destinationAngle = bodyAngle;
		this.immediateDestinationAngle = bodyAngle;
		bodyAngleSum = bodyAngle;
		this.teamNo = teamNo;
		currentHP = maxHP;
		type = 6;
		cloakCooldownCount = 120; 
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
			bitmapVisionGainFromAttackingUnit = createBitmapVision(2);
		}
		
		if(secondaryTargets == null){
			secondaryTargets = new solidObject[3];
		}
		
		ID = globalUniqID++;
		randomNumber = gameData.getRandom();
		height = centre.y + 0.2f;
		theAssetManager = mainThread.theAssetManager; 
		boundary2D = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		movement = new vector(0,0,0);
		updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
		boundary2D.owner = this;
		destinationBlock = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		probeBlock = new Rect((int)(origin.x*64) - 6, (int)(origin.z*64) + 6, 12, 12);
		firingPosition = new vector(0,-0.1f,0);
		
		//create main axis in object space
		iDirection = new vector(1,0,0);
		jDirection = new vector(0,1,0);
		kDirection = new vector(0,0,1);
		
		iDirection.rotate_XZ(360-bodyAngle);
		kDirection.rotate_XZ(360-bodyAngle);
		
		//create axis for body and turret 
		iDirectionBody = iDirection.myClone();
		jDirectionBody = jDirection.myClone();
		kDirectionBody = kDirection.myClone();
		
		iDirectionTurret = iDirection.myClone();
		jDirectionTurret = jDirection.myClone();
		kDirectionTurret = kDirection.myClone();
	
		//create polygons 
		makePolygons();

		movement_offscreen = new vector(0,0,0);
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(6f);
			secondaryAttackCheckList = generateTileCheckList(4);
			
			//shaffule secondaryAttackCheckList
			for(int i = 0; i < 100; i++){
				int temp = (gameData.getRandom() * secondaryAttackCheckList.length) >> 10;
				int temp1 = (gameData.getRandom() * secondaryAttackCheckList.length) >> 10;
					
				int a = secondaryAttackCheckList[temp];
				secondaryAttackCheckList[temp] = secondaryAttackCheckList[temp1];
				secondaryAttackCheckList[temp1] = a;
			}
		}
		
	}
	
	
	public void makePolygons(){ 
		bodyCenter.y-=0.18f;
		start.set(bodyCenter);
		
		body = new polygon3D[43];
		
		int skinTextureIndex = 23;
		if(teamNo != 0)
			skinTextureIndex = 10;
		
		v = new vector[]{put(-0.04, 0.03, 0.07), put(-0.04, 0.055, 0.04), put(-0.04, 0.055, -0.05), put(-0.04, 0.03, -0.07),  put(-0.04, 0, -0.07),  put(-0.04, 0, 0.07)};
		body[0] = new polygon3D(v, put(-0.04, 0.055, 0.07), put(-0.04, 0.055, -0.07), put(-0.04, 0.01, 0.07), mainThread.textures[skinTextureIndex], 1,0.2f,9);
		
		v = new vector[]{put(0.04, 0, 0.07),  put(0.04, 0, -0.07), put(0.04, 0.03, -0.07), put(0.04, 0.055, -0.05), put(0.04, 0.055, 0.04),  put(0.04, 0.03, 0.07)};
		body[1] = new polygon3D(v, put(0.04, 0.055, 0.07), put(0.04, 0.055, -0.07), put(0.04, 0.01, 0.07), mainThread.textures[skinTextureIndex], 1,0.2f,9);
		
		v = new vector[]{put(-0.04, 0.03, 0.07), put(0.04, 0.03, 0.07), put(0.04, 0.055, 0.04), put(-0.04, 0.055, 0.04)};
		body[2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,9);
		
		v  = new vector[]{put(-0.04, 0.055, 0.04), put(0.04, 0.055, 0.04), put(0.04, 0.055, -0.05), put(-0.04, 0.055, -0.05)};
		body[3] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,1,9);
		
		v = new vector[]{put(-0.04, 0.055, -0.05), put(0.04, 0.055, -0.05), put(0.04, 0.03, -0.07), put(-0.04, 0.03, -0.07)};
		body[4] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,9);
		
		v = new vector[]{put(0.04, 0.03, 0.07),put(-0.04, 0.03, 0.07), put(-0.04, 0, 0.07), put(0.04, 0, 0.07)};
		body[5] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,9);
		
		v = new vector[]{put(-0.04, 0.03, -0.07), put(0.04, 0.03, -0.07), put(0.04, 0., -0.07),  put(-0.04, 0., -0.07)};
		body[6] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,9);
		
		
		tempVector.set(start);
		start = put(0,0,0.01);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.04, 0.03,0.1), put(-0.04, 0.03,0.03), put(-0.065, 0.03,0.03)};
		body[7] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.03,0.1), put(-0.065, 0.03,0.1), put(-0.065, 0.01,0.11), put(-0.04, 0.01,0.11)};
		body[8] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.03), put(-0.04, 0.03,0.03), put(-0.04, 0.01,0.029), put(-0.065, 0.01,0.029)};
		body[9] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.065, 0.03,0.03), put(-0.065, 0.01,0.029), put(-0.065, 0.01,0.11)};
		body[10] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.01,0.11), put(-0.04, 0.01,0.029), put(-0.04, 0.03,0.03) ,put(-0.04, 0.03,0.1)};
		body[11] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, 0.01,0.029), put(-0.065, -0.01,0.031), put(-0.065, -0.01,0.1)};
		body[12] = new polygon3D(v, put(-0.065, 0.03,0.11), put(-0.065, 0.03,0.029), put(-0.065, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.04, -0.01,0.1), put(-0.04, -0.01,0.031), put(-0.04, 0.01,0.029), put(-0.04, 0.01,0.11)};
		body[13] = new polygon3D(v, put(-0.04, 0.03,0.11), put(-0.04, 0.03,0.029), put(-0.04, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, -0.01,0.1), put(-0.04, -0.01,0.1), put(-0.04, 0.01,0.11)};
		body[14] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, -0.01,0.031), put(-0.065, 0.01,0.029), put(-0.04, 0.01,0.029), put(-0.04, -0.01,0.031)};
		body[15] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		start.set(tempVector);
		start = put(0,0,-0.12);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.04, 0.03,0.1), put(-0.04, 0.03,0.03), put(-0.065, 0.03,0.03)};
		body[16] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.03,0.1), put(-0.065, 0.03,0.1), put(-0.065, 0.01,0.11), put(-0.04, 0.01,0.11)};
		body[17] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.03), put(-0.04, 0.03,0.03), put(-0.04, 0.01,0.029), put(-0.065, 0.01,0.029)};
		body[18] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.065, 0.03,0.03), put(-0.065, 0.01,0.029), put(-0.065, 0.01,0.11)};
		body[19] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.01,0.11), put(-0.04, 0.01,0.029), put(-0.04, 0.03,0.03) ,put(-0.04, 0.03,0.1)};
		body[20] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, 0.01,0.029), put(-0.065, -0.01,0.031), put(-0.065, -0.01,0.1)};
		body[21] = new polygon3D(v, put(-0.065, 0.03,0.11), put(-0.065, 0.03,0.029), put(-0.065, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.04, -0.01,0.1), put(-0.04, -0.01,0.031), put(-0.04, 0.01,0.029), put(-0.04, 0.01,0.11)};
		body[22] = new polygon3D(v, put(-0.04, 0.03,0.11), put(-0.04, 0.03,0.029), put(-0.04, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, -0.01,0.1), put(-0.04, -0.01,0.1), put(-0.04, 0.01,0.11)};
		body[23] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, -0.01,0.031), put(-0.065, 0.01,0.029), put(-0.04, 0.01,0.029), put(-0.04, -0.01,0.031)};
		body[24] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		start.set(tempVector);
		start = put(0.105,0,-0.12);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.04, 0.03,0.1), put(-0.04, 0.03,0.03), put(-0.065, 0.03,0.03)};
		body[25] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.03,0.1), put(-0.065, 0.03,0.1), put(-0.065, 0.01,0.11), put(-0.04, 0.01,0.11)};
		body[26] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.03), put(-0.04, 0.03,0.03), put(-0.04, 0.01,0.029), put(-0.065, 0.01,0.029)};
		body[27] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.065, 0.03,0.03), put(-0.065, 0.01,0.029), put(-0.065, 0.01,0.11)};
		body[28] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.01,0.11), put(-0.04, 0.01,0.029), put(-0.04, 0.03,0.03) ,put(-0.04, 0.03,0.1)};
		body[29] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, 0.01,0.029), put(-0.065, -0.01,0.031), put(-0.065, -0.01,0.1)};
		body[30] = new polygon3D(v, put(-0.065, 0.03,0.11), put(-0.065, 0.03,0.029), put(-0.065, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.04, -0.01,0.1), put(-0.04, -0.01,0.031), put(-0.04, 0.01,0.029), put(-0.04, 0.01,0.11)};
		body[31] = new polygon3D(v, put(-0.04, 0.03,0.11), put(-0.04, 0.03,0.029), put(-0.04, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, -0.01,0.1), put(-0.04, -0.01,0.1), put(-0.04, 0.01,0.11)};
		body[32] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, -0.01,0.031), put(-0.065, 0.01,0.029), put(-0.04, 0.01,0.029), put(-0.04, -0.01,0.031)};
		body[33] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		start.set(tempVector);
		start = put(0.105,0,0.01);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.04, 0.03,0.1), put(-0.04, 0.03,0.03), put(-0.065, 0.03,0.03)};
		body[34] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.03,0.1), put(-0.065, 0.03,0.1), put(-0.065, 0.01,0.11), put(-0.04, 0.01,0.11)};
		body[35] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.03), put(-0.04, 0.03,0.03), put(-0.04, 0.01,0.029), put(-0.065, 0.01,0.029)};
		body[36] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.03,0.1), put(-0.065, 0.03,0.03), put(-0.065, 0.01,0.029), put(-0.065, 0.01,0.11)};
		body[37] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.04, 0.01,0.11), put(-0.04, 0.01,0.029), put(-0.04, 0.03,0.03) ,put(-0.04, 0.03,0.1)};
		body[38] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, 0.01,0.029), put(-0.065, -0.01,0.031), put(-0.065, -0.01,0.1)};
		body[39] = new polygon3D(v, put(-0.065, 0.03,0.11), put(-0.065, 0.03,0.029), put(-0.065, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.04, -0.01,0.1), put(-0.04, -0.01,0.031), put(-0.04, 0.01,0.029), put(-0.04, 0.01,0.11)};
		body[40] = new polygon3D(v, put(-0.04, 0.03,0.11), put(-0.04, 0.03,0.029), put(-0.04, -0.01,0.11), mainThread.textures[3], 1,1,9);
		
		v = new vector[]{put(-0.065, 0.01,0.11), put(-0.065, -0.01,0.1), put(-0.04, -0.01,0.1), put(-0.04, 0.01,0.11)};
		body[41] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		v = new vector[]{put(-0.065, -0.01,0.031), put(-0.065, 0.01,0.029), put(-0.04, 0.01,0.029), put(-0.04, -0.01,0.031)};
		body[42] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 0.3f,0.5f,9);
		
		start.set(tempVector);
	
		turretCenter = put(0, 0.065, 0.0);
		start.set(turretCenter);
		
		turret = new polygon3D[66];
		
		int turretSkinTexture = 64;
		if(teamNo != 0)
			turretSkinTexture = 26;
		
		double r1 = 0.031;
		double r2 = 0.02;
		double r3 = 0.025;
		double theta = Math.PI/16;
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r2*Math.cos(i*theta), r2*Math.sin(i*theta), -0.075),
							 put(r2*Math.cos((i+1)*theta), r2*Math.sin((i+1)*theta), -0.075),
							 put(r1*Math.cos((i+1)*theta), r1*Math.sin((i+1)*theta), 0.035),
							 put(r1*Math.cos(i*theta), r1*Math.sin(i*theta), 0.035)
							};
			turret[i] = new polygon3D(v, v[0], v[1], v [3],  mainThread.textures[turretSkinTexture], 10,10,9);
		}
		
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r1*Math.cos(i*theta), r1*Math.sin(i*theta), 0.035),
							 put(r1*Math.cos((i+1)*theta), r1*Math.sin((i+1)*theta), 0.035),
							 put(r3*Math.cos((i+1)*theta), r3*Math.sin((i+1)*theta), 0.08),
							 put(r3*Math.cos(i*theta), r3*Math.sin(i*theta), 0.08)
							};
			turret[i +32] = new polygon3D(v, v[0], v[1], v [3],  mainThread.textures[turretSkinTexture], 10,10,9);
		
		}
		
		v = new vector[32];
		for(int i = 1; i < 33; i++)
			v[32 - i] = put(r2*Math.cos(i*theta), r2*Math.sin(i*theta), -0.075);
		turret[64] = new polygon3D(v, v[0], v[1], v [3],  mainThread.textures[turretSkinTexture], 10,10,9);
	
		
		v = new vector[32];
		for(int i = 1; i < 33; i++)
			v[i-1] = put(r3*Math.cos(i*theta), r3*Math.sin(i*theta), 0.08);
		turret[65] = new polygon3D(v, v[0], v[1], v [3],  mainThread.textures[turretSkinTexture], 10,10,9);
		
		for(int i = 0; i < 66; i++){
			turret[i].Ambient_I+=20;
			turret[i].reflectance = 70;
			turret[i].findDiffuse();
		}
		
	}
	
	//update and draw model 
	public void update(){

				
		//check if tank has been destroyed
		if(currentHP <= 0){
			//spawn an explosion when the tank is destroyed
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = centre.x;
			tempFloat[1] = centre.y - 0.05f;
			tempFloat[2] = centre.z;
			tempFloat[3] = 2.5f;
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 7;
			tempFloat[7] = this.height;
			theAssetManager.explosionCount++; 
			theAssetManager.removeObject(this); 
			removeFromGridMap();
			if(attacker.teamNo != teamNo)
				attacker.experience+=20;
			return;
		}
		
		if(experience >= 60){
			myDamage = 45;
			level = 1;
			if(experience >= 120){
				level = 2;
				myDamage = 60;
				if(currentHP < maxHP && mainThread.gameFrame%16==0)
					currentHP++;
			}
		}
		
		
		//carry out commands given by the player or AI
		if(!disableUnitLevelAI)
			carryOutCommands();
		
		if(attackCoolDown > 0)
			attackCoolDown--;
		
		if(exposedCountDown > 0)
			exposedCountDown --;
		
		if(tightSpaceManeuverCountDown > 0)
			tightSpaceManeuverCountDown--;
		
		if(isCloaked){
			if(teamNo!=0){
				isSelectable = false;
			}
		}else{
			isSelectable = true;
		}
		
		if(cloakCooldownCount > 0){
			cloakCooldownCount--;
			isCloaked = false;
		}
			
		if(cloakCooldownCount ==0)
			isCloaked = true;
		
		if(isCloaked){
			if(teamNo != 0){
				//if(currentCloakingStatus < 127)
				//	currentCloakingStatus+=3;
			}else{
				if(currentCloakingStatus < 70){
					currentCloakingStatus+=3;
				}
			}
			
			if(currentShadowStatus < 127)
				currentShadowStatus+=6;
		}else{
			if(currentCloakingStatus > 0){
				currentCloakingStatus-=3;
				if(currentCloakingStatus < 0)
					currentCloakingStatus = 0;
			}
			
			if(currentShadowStatus > 0){
				currentShadowStatus-=6;
				if(currentShadowStatus < 0)
					currentShadowStatus = 0;
			}
			
		}
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;
			
		//find out if the geometry of the object need to be modified
		geometryNeedModify = true;
		if(movement.x == 0 && movement.z == 0){
			if(turretAngleDelta == 0 && bodyAngleDelta == 0){
				geometryNeedModify = false;
			}
			if(occupiedTile0 != -1)
				mainThread.gridMap.currentObstacleMap[occupiedTile0] = false;
			if(occupiedTile1 != -1)
				mainThread.gridMap.currentObstacleMap[occupiedTile1] = false;
			if(occupiedTile2 != -1)
				mainThread.gridMap.currentObstacleMap[occupiedTile2] = false;
			if(occupiedTile3 != -1)
				mainThread.gridMap.currentObstacleMap[occupiedTile3] = false;
		}else{
			//update centre, make sure the tank isnt moving at a ridiculous speed
			if (Math.abs(movement.x) + Math.abs(movement.z) < 0.25f) { 
				
				centre.add(movement);
			
				boundary2D.setOrigin((int)(centre.x*64) - 8, (int)(centre.z*64) + 8);
				updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
				
				
			}else{
				movement.reset();
				if(occupiedTile0 != -1)
					mainThread.gridMap.currentObstacleMap[occupiedTile0] = false;
				if(occupiedTile1 != -1)
					mainThread.gridMap.currentObstacleMap[occupiedTile1] = false;
				if(occupiedTile2 != -1)
					mainThread.gridMap.currentObstacleMap[occupiedTile2] = false;
				if(occupiedTile3 != -1)
					mainThread.gridMap.currentObstacleMap[occupiedTile3] = false;
			}
		}
		
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.y -= 0.2f;
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		
		visionBoundary.x = (int)(tempCentre.screenX - 500);
		visionBoundary.y = (int)(tempCentre.screenY - 1000);
		visionInsideScreen = camera.screen.intersects(visionBoundary);
		
		if(attackStatus == isAttacking && targetObject != null &&  targetObject.teamNo != teamNo)
			exposedCountDown = 64;
		
		//create vision for enemy commander
		if(teamNo == 1){
			xPos = boundary2D.x1/16 - 6 + 10;
			yPos = 127 - boundary2D.y1/16 - 6 + 10;
			
			for(int y = 0; y < 17; y++){
				for(int x = 0; x < 17; x++){
					if(bitmapVisionForEnemy[x+ y*17])
						enemyCommander.tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}else if(exposedCountDown > 0){
			xPos = boundary2D.x1/16 - 2 + 10;
			yPos = 127 - boundary2D.y1/16 - 2 + 10;
			
			for(int y = 0; y < 5; y++){
				for(int x = 0; x < 5; x++){
					if(bitmapVisionGainFromAttackingUnit[x+ y*5])
						enemyCommander.tempBitmap[xPos + x + (yPos+y)*148] =true;
				}
			}
		}
		
		if(visionInsideScreen){
			if(teamNo != 0){
				if(attackStatus == isAttacking || exposedCountDown > 0){
					tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
					tempFloat[0] = teamNo;
					tempFloat[1] = centre.x;
					tempFloat[2] = -0.4f;
					tempFloat[3] = centre.z;
					tempFloat[4] = 1;
					theAssetManager.visionPolygonCount++; 
					
				}
			
			}else{
				tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
				tempFloat[0] = teamNo;
				tempFloat[1] = centre.x;
				tempFloat[2] = -0.4f;
				tempFloat[3] = centre.z;
				tempFloat[4] = 2;
				theAssetManager.visionPolygonCount++;
			}
		}
		
		//check if the tank object is visible in mini map
		visible_minimap = theAssetManager.minimapBitmap[boundary2D.x1/16 + (127 - (boundary2D.y1-1)/16)*128];
	
		if(teamNo == 0 || attackStatus == isAttacking || exposedCountDown > 0 || visible_minimap){
			if(!(isCloaked && teamNo!=0)){
				tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
				tempInt[0] = teamNo + (this.type << 8);
				tempInt[1] = boundary2D.x1/16;
				tempInt[2] = 127 - boundary2D.y1/16;
				tempInt[3] = 2;
				if(teamNo == 0 && underAttackCountDown > 0)
					tempInt[4] = 10001;
				else
					tempInt[4] = exposedCountDown;
				theAssetManager.unitsForMiniMapCount++;
			}
		}
		
		
		//test if the tank object is visible in camera point of view
		if(visible_minimap){
			if(currentHP <= (maxHP/2) && (mainThread.gameFrame + ID) % 3 ==0 && !isCloaked){
				//spawn smoke particle if the tank is badly damaged
				float[] tempFloat = theAssetManager.smokeEmmiterList[theAssetManager.smokeEmmiterCount];
				tempFloat[0] = centre.x + (float)(Math.random()/20) - 0.025f;
				tempFloat[1] = centre.y - 0.06f;
				tempFloat[2] = centre.z + (float)(Math.random()/20) - 0.025f;
				tempFloat[3] = 0.7f;
				tempFloat[4] = 1;
				tempFloat[5] = 11;
				tempFloat[6] = this.height;
				
				theAssetManager.smokeEmmiterCount++;
			}
			
			
			
			if(visibleBoundary.contains(tempCentre.screenX, tempCentre.screenY)){
				visible = true;
				if(screenBoundary.contains(tempCentre.screenX, tempCentre.screenY))
					withinViewScreen = true;
				else
					withinViewScreen = false;
			}else{
				visible = false;
				
			}
		}else{
			mainThread.pc.deSelect(this);			
			visible = false;
		}
		
		if(isCloaked && teamNo!=0){
			visible_minimap = false;
			//if the stealth tank completely fades into back ground, then it is invisible
			if(teamNo != 0 && currentCloakingStatus >=120)
				visible = false;
		}
		
		
		if(visible){
			if(movement_offscreen.x != 0 || movement_offscreen.z!= 0 || turretAngleDelta_offscreen != 0 || bodyAngleDelta_offscreen != 0){
				geometryNeedModify = true;
			}
			
			
			
			if(geometryNeedModify){
				movement.add(movement_offscreen);
			
				turretAngleDelta= (turretAngleDelta +turretAngleDelta_offscreen)%360;
				turretAngleDelta_offscreen = 0;
			
				bodyAngleDelta = (bodyAngleDelta + bodyAngleDelta_offscreen)%360;
				bodyAngleDelta_offscreen = 0;
			
				updateGeometry();
				bodyAngleDelta = 0;
				turretAngleDelta = 0;
			
				movement.subtract(movement_offscreen);
				movement_offscreen.set(0,0,0);
			}
			
			
			
			
		
				
			rasterizer.cloakTexture = gameData.cloakTextures[0];
			rasterizer.cloakedShadowThreshold = currentShadowStatus;
			
			for(int i = 0; i < turret.length; i++){
				turret[i].update_lightspace();
				
			}
			
			for(int i = 0; i < body.length; i++){
				body[i].update_lightspace();	
			}
			
			
			
			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;
			
		}else if(geometryNeedModify){
			movement_offscreen.add(movement);
			turretAngleDelta_offscreen += turretAngleDelta;
			turretAngleDelta_offscreen = (turretAngleDelta_offscreen + 360)%360;
			bodyAngleDelta_offscreen += bodyAngleDelta;
			bodyAngleDelta_offscreen = (bodyAngleDelta_offscreen + 360)%360;
		}
		
	}
	
	public void updateGeometry(){
		//correct body angle  if the visual body angle differs from the logical one
		bodyAngleSum = (360 + bodyAngleSum - bodyAngleDelta)%360;
		int angle = bodyAngleDelta;
		if(bodyAngleSum != bodyAngle){
			angle = (360 + bodyAngleDelta - (360 + bodyAngle - bodyAngleSum) % 360)%360;
			bodyAngleSum = bodyAngle;
		}
		
		
		//update body polygons
		for(int i = 0; i < body.length; i++){
			if(body[i].textureFitPolygon == false){
				//perform vertex updates in world coordinate
				body[i].origin.add(movement);
				body[i].origin.subtract(centre);
				body[i].origin.rotate_XZ(angle);
				body[i].origin.add(centre);
				
				body[i].bottomEnd.add(movement);
				body[i].bottomEnd.subtract(centre);
				body[i].bottomEnd.rotate_XZ(angle);
				body[i].bottomEnd.add(centre);
				
				body[i].rightEnd.add(movement);
				body[i].rightEnd.subtract(centre);
				body[i].rightEnd.rotate_XZ(angle);
				body[i].rightEnd.add(centre);
			}
			
			for(int j = 0; j < body[i].vertex3D.length; j++){
				body[i].vertex3D[j].add(movement);
				body[i].vertex3D[j].subtract(centre);
				body[i].vertex3D[j].rotate_XZ(angle);
				body[i].vertex3D[j].add(centre);
			}
			

			body[i].normal.rotate_XZ(angle);
			body[i].findDiffuse();
		}
		
		
		//update turret center
		turretCenter.add(movement);
		
		//update turret polygons
		for(int i = 0; i < turret.length; i++){
			if(turret[i].textureFitPolygon == false){
				//perform vertex updates in world coordinate
				turret[i].origin.add(movement);
				turret[i].origin.subtract(turretCenter);
				turret[i].origin.rotate_XZ(turretAngleDelta);
				turret[i].origin.add(turretCenter);
				
				turret[i].bottomEnd.add(movement);
				turret[i].bottomEnd.subtract(turretCenter);
				turret[i].bottomEnd.rotate_XZ(turretAngleDelta);
				turret[i].bottomEnd.add(turretCenter);
				
				turret[i].rightEnd.add(movement);
				turret[i].rightEnd.subtract(turretCenter);
				turret[i].rightEnd.rotate_XZ(turretAngleDelta);
				turret[i].rightEnd.add(turretCenter);
			}
			
			for(int j = 0; j < turret[i].vertex3D.length; j++){
				turret[i].vertex3D[j].add(movement);
				turret[i].vertex3D[j].subtract(turretCenter);
				turret[i].vertex3D[j].rotate_XZ(turretAngleDelta);
				turret[i].vertex3D[j].add(turretCenter);
			}
			
			
			turret[i].normal.rotate_XZ(turretAngleDelta);
			turret[i].findDiffuse();
		}
	}
	
	//carry out commands given by player or  AI commander
	public void carryOutCommands(){
		if(currentCommand == StandBy){
			resetLogicStatus();
			if(!isCloaked){
				performStandByLogic();
			}
			
		}else if(currentCommand == move){
			performMovementLogic();
			avoidGettingStucked();
			
		}else if(currentCommand == attackInNumbers || currentCommand == attackCautiously){
			performAttackLogic();
			avoidGettingStucked();
	
		}else if(currentCommand == attackMove){
			performAttackMoveLogic();
			avoidGettingStucked();
		}
	}
	
	public void resetLogicStatus(){
		movement.reset();
		turretAngleDelta = 0;
		bodyAngleDelta = 0;
		currentMovementStatus = freeToMove;
		attackStatus = noTarget;
		stuckCount = 0;
		destinationX = centre.x;
		destinationY = centre.z;
		insideDeistinationRadiusCount = 0;
		obstacle = null;
		closeToDestination = false;
	}
	
	//the tank will attack with any hostile unit that moved into its firing range
	public void performStandByLogic(){
		//scan for hostile unit
		if((ID + mainThread.gameFrame)%32 == 0){
			currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
			
			for(int i = 0; i < tileCheckList.length; i++){
				if(tileCheckList[i] != Integer.MAX_VALUE){
					int index = currentOccupiedTile + tileCheckList[i];
					if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
						continue;
					tile = mainThread.gridMap.tiles[index];
					
					for(int j = 0; j < 4; j++){
						if(tile[j] != null){
							if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
								attackMoveTo((tile[j].centre.x + centre.x)/2, (tile[j].centre.z+centre.z)/2);
								currentCommand = solidObject.attackMove;
								secondaryCommand = solidObject.attackMove;
								return;
							}
						}
					}
				}
			}
		}
	}
	
	
	//move to a destination position,  ignore any hostile units it encounters 
	public void performMovementLogic(){
		attackStatus = solidObject.noTarget;
		
		//clear things a bit
		unStableObstacle = null;
		
		if(newDestinationisGiven){
			newDestinationisGiven = false;
			
			distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
			calculateMovement();
			destinationAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			immediateDestinationAngle = destinationAngle;
			
			//currentMovementStatus = validateMovement();
		}
		
		
		if(turretAngle != immediateDestinationAngle){
			turretAngleDelta = 360 - (geometry.findAngleDelta(turretAngle, immediateDestinationAngle, turretTurnRate) + 360)%360;
			turretAngle= (turretAngle - turretAngleDelta + 360)%360;
		}else{
			turretAngleDelta = 0;
		}
		
		if(Math.abs(bodyAngle - immediateDestinationAngle) > 45 && Math.abs(bodyAngle - immediateDestinationAngle) < 315){
			
			bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
			bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
			movement.reset();
			
		}else{
			if(bodyAngle != immediateDestinationAngle){
				bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
				bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
			}else{
			
				bodyAngleDelta = 0;
			}
			
			
			if(currentMovementStatus == hugRight || currentMovementStatus == hugLeft){
				
				if(checkIfDestinationReached() == true){
				
					movement.reset();
					
					currentCommand = StandBy;
					secondaryCommand = StandBy;
					return;
				}
				
		
				hugWalls();
				return;
			}
			
			
			if(movement.x == 0 && movement.z == 0)
				calculateMovement();
			if(distanceToDesination - speed <= 0){
				//movement.scale(speed - distanceToDesination);
				movement.set(destinationX - centre.x, 0, destinationY - centre.z);
				
				//validate movement
				currentMovementStatus = validateMovement();
				
				if(currentMovementStatus == freeToMove){
					resetLogicStatus();
					currentCommand = StandBy;
					secondaryCommand = StandBy;
				}else{
					movement.reset();
					
				}
			}else {
				//validate movement
				currentMovementStatus = validateMovement();
				
				if(currentMovementStatus == freeToMove){
					distanceToDesination -= speed;
				}else{
					movement.reset();
					
				}
			}
		}
	}
	
	//attack a single unit, ignore any hostile units it encounters
	public void performAttackLogic(){
		
		destinationX = targetObject.getRealCentre().x;
		destinationY = targetObject.getRealCentre().z;
		
		//clear things a bit
		unStableObstacle = null;
		
		distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
		
		//check if light tank has the line of sight to its target
		hasLineOfSightToTarget = true;
		if(distanceToDesination <= attackRange){
			int numberOfIterations = (int)(distanceToDesination * 8);
			float dx = (destinationX - centre.x)/numberOfIterations;
			float dy = (destinationY - centre.z)/numberOfIterations;
			float xStart = centre.x;
			float yStart = centre.z;
			
			for(int i = 0; i < numberOfIterations; i++){
				xStart+=dx;
				yStart+=dy;
				solidObject s = mainThread.gridMap.tiles[(int)(xStart*4) + (127 - (int)(yStart*4))*128][0];
				if(s != null){
					if(s.type > 100 && s .type < 200 && s != targetObject){
						hasLineOfSightToTarget = false;
						break;
					}
				}
			}
			
		}
		
		if(currentMovementStatus !=  hugRight && currentMovementStatus != hugLeft){
			calculateMovement();
			destinationAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			immediateDestinationAngle = destinationAngle;
		}
		
		
		if((currentCommand == attackInNumbers && distanceToDesination <=  groupAttackRange && hasLineOfSightToTarget) || (currentCommand == attackCautiously && distanceToDesination <  attackRange && hasLineOfSightToTarget)){
			movement.reset();
			currentMovementStatus = freeToMove; 
			obstacle = null;
		}
		
		if(distanceToDesination <= attackRange){
			
			if(hasLineOfSightToTarget)
				attackStatus = isAttacking;	
		}else{
			attackStatus = notInRange;
			
			if(secondaryCommand == attackMove){
				
				resetLogicStatus();
				destinationX = secondaryDestinationX;
				destinationY = secondaryDestinationY;
				currentCommand = attackMove;
				newDestinationisGiven = true;
				return;
			}
		
		}
		
		
		if(attackStatus == isAttacking){
			int attackAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			
			if(turretAngle != attackAngle){
				
				turretAngleDelta = 360 - (geometry.findAngleDelta(turretAngle, attackAngle, turretTurnRate) + 360)%360;
				turretAngle= (turretAngle - turretAngleDelta + 360)%360;
				
				if(Math.abs(turretAngle - attackAngle) < 10)
					fireRailgunShot(attackAngle);
			}else{
				fireRailgunShot(attackAngle);
				
			
				turretAngleDelta = 0;
			}
		
		}else{
			if(turretAngle != immediateDestinationAngle){
				turretAngleDelta = 360 - (geometry.findAngleDelta(turretAngle, immediateDestinationAngle, turretTurnRate) + 360)%360;
				turretAngle= (turretAngle - turretAngleDelta + 360)%360;
			}else{
				turretAngleDelta = 0;
			}
		}
		
		if(Math.abs(bodyAngle - immediateDestinationAngle) > 45 && Math.abs(bodyAngle - immediateDestinationAngle) < 315){
			if(!(distanceToDesination <  attackRange && hasLineOfSightToTarget && movement.x ==0 && movement.z ==0)){
				bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
				bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
				movement.reset();
			}else{
				bodyAngleDelta = 0;
			}
			
		}else{
			if(bodyAngle != immediateDestinationAngle){
				if(!(distanceToDesination <  attackRange && hasLineOfSightToTarget && movement.x ==0 && movement.z ==0)){
					bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
					bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					
				}
			}else{
			
				bodyAngleDelta = 0;
			}
		
			if(currentMovementStatus == hugRight || currentMovementStatus == hugLeft){
			
				hugWalls();
				
				if(distanceToDesination <= attackRange && hasLineOfSightToTarget){
					attackStatus = isAttacking;	
					
					if(currentCommand == attackInNumbers && distanceToDesination >  groupAttackRange){
						if(movement.x != 0 || movement.z !=0){	
							if(Math.sqrt((destinationX - centre.x - movement.x) * (destinationX - centre.x - movement.x) + (destinationY - centre.z - movement.z) * (destinationY - centre.z - movement.z)) > attackRange){
								currentMovementStatus = freeToMove;
								movement.reset();
								currentCommand = attackCautiously;
							}	
						}
					}else{
						movement.reset();
						currentMovementStatus = freeToMove;
					}	
				}
				
				return;
			}
			
			
			
			if(movement.x == 0 && movement.z == 0)
				calculateMovement();
			
			if(distanceToDesination <= attackRange && hasLineOfSightToTarget){
				
				attackStatus = isAttacking;	
				
				if(currentCommand == attackInNumbers && distanceToDesination > groupAttackRange){
					currentMovementStatus = validateMovement();
					if(currentMovementStatus == freeToMove){
						distanceToDesination -= speed;
					}else{
						hugWalls();
						if(movement.x != 0 || movement.z !=0){	
							if(Math.sqrt((destinationX - centre.x - movement.x) * (destinationX - centre.x - movement.x) + (destinationY - centre.z - movement.z) * (destinationY - centre.z - movement.z)) > attackRange){
								currentMovementStatus = freeToMove;
								movement.reset();
								currentCommand = attackCautiously;
							}	
						}
					}
				}else{
					currentMovementStatus = freeToMove;
					movement.reset();
				}
				
			}else {
				//validate movement
				currentMovementStatus = validateMovement();
				
				if(currentMovementStatus == freeToMove){
					distanceToDesination -= speed;
				}else{
					movement.reset();
					
				}
			}
		}
		
		if(targetObject.currentHP <=0 || (targetObject.isCloaked && teamNo != targetObject.teamNo)){
			currentCommand = StandBy;
			targetObject = null;
			if(secondaryCommand == attackMove){
				destinationX = secondaryDestinationX;
				destinationY = secondaryDestinationY;
				currentCommand = attackMove;
				newDestinationisGiven = true;
			}
			
			return;
		}
	}
	
	//move to a  destination position, engage with any hostile units (moving units first, then buildings) in its path
	public void performAttackMoveLogic(){

		currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
		
		solidObject target = null;
		for(int i = 0; i < tileCheckList.length; i++){
			if(tileCheckList[i] != Integer.MAX_VALUE){
				int index = currentOccupiedTile + tileCheckList[i];
				if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
					continue;
				tile = mainThread.gridMap.tiles[index];
				
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){
						if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0  && !tile[j].isCloaked){
							if(tile[j].type < 100 || tile[j].type >= 199){
								attack(tile[j]);
								currentCommand = attackInNumbers;
								return;
							}else{
								if(target == null)
									target = tile[j];
							}
						}
					}
				}
			}
		}
		
		if(target != null && ((target.centre.x - centre.x)*(target.centre.x - centre.x) + (target.centre.z - centre.z)*(target.centre.z - centre.z)) <= attackRange*attackRange){
			attack(target);
			currentCommand = attackInNumbers;
			return;
		}
		
		performMovementLogic();
	}
	
	
	public void fireRailgunShot(int attackAngle){
		if(attackCoolDown == 0 && targetObject.currentHP >0  && hasLineOfSightToTarget){
			//if there is nothing between the tank and its target fire a railgun shot
			firingPosition.set(0, -0.4f, 0.12f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(centre.x, 0, centre.z);
			
			tempVector.set(0, 0, 0.1f);
			tempVector.rotate_XZ(360 - attackAngle);
			
			attackCoolDown = myAttackCooldown;
			cloakCooldownCount = 120; 

			for(float i = 0.1f; i < distanceToDesination; i+=0.1f){
				if(theAssetManager.helixCount >= theAssetManager.helixInfo.length)
					break;
				
				//spawn railgun trail
				tempFloat = theAssetManager.helixInfo[theAssetManager.helixCount];
				tempFloat[0] = firingPosition.x;
				tempFloat[1] = firingPosition.y;
				tempFloat[2] = firingPosition.z;
				tempFloat[3] = attackAngle;
				theAssetManager.helixCount++;
				firingPosition.add(tempVector);
			}
			
			
			//spawn a mini explosion at target location
			tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = targetObject.centre.x;
			tempFloat[1] = firingPosition.y;
			tempFloat[2] = targetObject.centre.z;
			tempFloat[3] = 0.8f;
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = firingPosition.y + 1.75f;
			theAssetManager.explosionCount++;
			
			int myDamageModified = myDamage;
			
			if(targetObject.type==0){
				myDamageModified=(int)(myDamage*2);
			}else if(targetObject.type==1 || targetObject.type==6){
				myDamageModified=(int)(myDamage*1.75);
			}else if(targetObject.type == 7 || targetObject.type > 100 || targetObject.type == 2 || targetObject.type == 3){
				myDamageModified=(int)(myDamage*0.4);
			}
			
			//damage and alert target unit
			targetObject.currentHP-=myDamageModified;
			targetObject.underAttackCountDown = 120;
			targetObject.attacker = this;
			
			int xPos = (int)(targetObject.centre.x*64);
			int yPos = (int)(targetObject.centre.z*64);
			int start = xPos/16 + (127 - yPos/16)*128;
			int targetTeamNo = targetObject.teamNo;
			solidObject[] tile;
			
			for(int i  = 0; i < 9; i++){
				int index = start + tiles3x3[i];
				if(index > 16383 || index < 0)
					continue;
				tile = mainThread.gridMap.tiles[index];
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){
						if(tile[j].teamNo == targetTeamNo && tile[j].currentCommand == solidObject.StandBy && targetTeamNo != teamNo && tile[j].isCloaked == false){
							if(tile[j].type < 100){
								tile[j].attack(this);
								tile[j].currentCommand = solidObject.attackInNumbers; 
							}
						}else if(tile[j].teamNo == targetTeamNo && tile[j].currentCommand == solidObject.attackMove && targetTeamNo != teamNo && tile[j].isCloaked == false){
							if(tile[j].attackStatus != solidObject.isAttacking ||
									(tile[j].attackStatus == isAttacking && tile[j].targetObject != null && tile[j].targetObject.type < 199 && tile[j].targetObject.type > 7)){
								targetObject.attack(this);
								targetObject.currentCommand = solidObject.attackInNumbers; 
							}
						}
					}
				}
			}
					
		
			if(hasMultiShotUpgrade && targetObject.type < 100){
				//find up to 3 random secondary targets around main targets
				for(int i = 0; i < secondaryTargets.length; i++)
					secondaryTargets[i] = null;
				
				int targetCount = 0;
				int randomNumber = gameData.getRandom()%secondaryAttackCheckList.length;
				
				for(int k = randomNumber; k < secondaryAttackCheckList.length + randomNumber; k++){
					int i = k%secondaryAttackCheckList.length;
					if(secondaryAttackCheckList[i] != Integer.MAX_VALUE){
						int index = start + secondaryAttackCheckList[i];
						if(index < 0 || index >= 16384)
							continue;
						tile = mainThread.gridMap.tiles[index];
						
						for(int j = 0; j < 4; j++){
							if(tile[j] != null){
								if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0  && !tile[j].isCloaked && tile[j].type < 100){
									if(tile[j] != secondaryTargets[0] && tile[j] != secondaryTargets[1] && tile[j] != secondaryTargets[2] && tile[j] != targetObject){
										secondaryTargets[targetCount] = tile[j];
										targetCount++;
										if(targetCount == 3)
											break;
									}
								}
							}
						}
						
						if(targetCount == 3)
							break;
					}
				}
				
				
				for(int i = 0; i < 3; i++){
					if( secondaryTargets[i] != null){
						double distance = Math.sqrt((secondaryTargets[i].centre.x - targetObject.centre.x)*(secondaryTargets[i].centre.x - targetObject.centre.x) + (secondaryTargets[i].centre.z - targetObject.centre.z)*(secondaryTargets[i].centre.z - targetObject.centre.z));
						
						
						tempVector.set(secondaryTargets[i].centre);
						tempVector.subtract(targetObject.centre);
						tempVector.y = 0;
						tempVector.unit();
						tempVector.scale(0.1f);
						
						firingPosition.set(targetObject.centre);
						firingPosition.y = -0.4f;
						
						int secondaryAttackAngle = geometry.findAngle(targetObject.centre.x, targetObject.centre.z, secondaryTargets[i].centre.x, secondaryTargets[i].centre.z);
						
						
						for(float j = 0; j < distance; j+=0.1f){
							if(theAssetManager.helixCount >= theAssetManager.helixInfo.length)
								break;
							
							//spawn railgun trail
							tempFloat = theAssetManager.helixInfo[theAssetManager.helixCount];
							tempFloat[0] = firingPosition.x;
							tempFloat[1] = firingPosition.y;
							tempFloat[2] = firingPosition.z;
							tempFloat[3] = secondaryAttackAngle;
							theAssetManager.helixCount++;
							firingPosition.add(tempVector);
						}
						
						
						//spawn a mini explosion at target location
						tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
						tempFloat[0] = secondaryTargets[i].centre.x;
						tempFloat[1] = -0.4f;
						tempFloat[2] = secondaryTargets[i].centre.z;
						tempFloat[3] = 0.8f;
						tempFloat[4] = 1;
						tempFloat[5] = 0;
						tempFloat[6] = 6 + (gameData.getRandom()%4);
						tempFloat[7] = -0.4f + 1.75f;
						theAssetManager.explosionCount++;
						
						//damage and alert target unit
						
						myDamageModified = myDamage;
					
						if(secondaryTargets[i].type==0 || targetObject.type==1){
							myDamageModified=(int)(myDamage*3);
							
						}else if(targetObject.type==6){
							myDamageModified=(int)(myDamage*1.75);
						}else if(secondaryTargets[i].type == 7 || targetObject.type == 2 || targetObject.type == 3){
							myDamageModified=(int)(myDamage*0.4);
						}
						
						
						secondaryTargets[i].currentHP-=((int)(myDamageModified/3));
						secondaryTargets[i].underAttackCountDown = 120;
						secondaryTargets[i].attacker = this;
						
						if((secondaryTargets[i].secondaryCommand == solidObject.attackMove || secondaryTargets[i].currentCommand == solidObject.StandBy) && (secondaryTargets[i].attackStatus != solidObject.isAttacking || 
								(secondaryTargets[i].attackStatus == isAttacking && secondaryTargets[i].targetObject != null && secondaryTargets[i].targetObject.type < 199 && secondaryTargets[i].targetObject.type > 7))){
							secondaryTargets[i].attack(this);
							secondaryTargets[i].currentCommand = solidObject.attackInNumbers; 
						}
						
					}
				}
				
			}
		}
	}
	

	
	
	public void draw(){
		if(!visible)
			return;
		
		rasterizer.modelCenterX = (int)(tempCentre.screenX);
		rasterizer.modelCenterY = (int)(tempCentre.screenY);
		rasterizer.cloakTexture = gameData.cloakTextures[(randomNumber + mainThread.gameFrame * 2)%120];
		rasterizer.cloakedThreshold = currentCloakingStatus;
		
		
		for(int i = 0; i < turret.length; i++){
			turret[i].update();
			turret[i].draw();
		}
		
		
		for(int i = 0; i < body.length; i++){
			body[i].update();
			body[i].draw();
		}
		
	}
	

	
	public int getMaxHp(){return maxHP;}
}
