package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//light tank 3D model

public class heavyTank extends solidObject{

	
	public vector bodyCenter, turretCenter;
	
	public polygon3D[] body, turret;
	
	public static int maxHP = 320;
	
	//a screen space boundary which is used to test if the tank object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-70,-25,908, 597);
	
	//a screen space boundary which is used to test if the entire tank object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432); 
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1000, 800);
	
	//a bitmap representation of the vision of the tank for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	public static boolean[] bitmapVisionGainFromAttackingUnit;
	
	//the oreintation of the tank
	public int bodyAngle, turretAngle;
	
	//the angle that the tank have rotated between current  frame and previous frame
	public int bodyAngleSum;
	
	//destination angle
	public int destinationAngle;
	
	//whether light tank has ling of sight to its target
	public boolean hasLineOfSightToTarget;
	
	
	//attack range
	public int attackCoolDown;
	public vector firingPosition;
	
	//the offsreen angles/movement are the accumulated changes that the object  made during offscreen. 
	public int bodyAngleDelta_offscreen, turretAngleDelta_offscreen; 
	public vector movement_offscreen;
	
	//whether the geometry of the object in world coordinate neesd to be updated in the current frame
	public boolean geometryNeedModify;

	public int bodyTurnRate = 4; 
	public int turretTurnRate = 6;
	public int myAttackCooldown= 45;
	
	//once the  tank starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	
	//index of the tiles to check when the tank is idle
	public static int[] tileCheckList;
	
	public boolean canSelfRepair;
	
	
	public heavyTank(vector origin, int bodyAngle, int teamNo){
		speed = 0.0085f;
		attackRange = 1.7f;
		groupAttackRange = 1.1f;
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
		type = 7;
		myDamage = 15;
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(6);
			bitmapVisionGainFromAttackingUnit = createBitmapVision(2);
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
		
	
		//create polygons 
		makePolygons();

		movement_offscreen = new vector(0,0,0);
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(6f);
		}
		
	}
	
	public void makePolygons(){
		 
		bodyCenter.y-=0.18f;
		start.set(bodyCenter);
		
		int skinTextureIndex = 71;
		if(teamNo != 0)
			skinTextureIndex = 10;
		
		
		iDirection = new vector(0.85f,0,0);
		jDirection = new vector(0,0.85f,0);
		kDirection = new vector(0,0,0.92f);
		iDirection.rotate_XZ(360-bodyAngle);
		kDirection.rotate_XZ(360-bodyAngle);
		
		
		body = new polygon3D[19];
		v = new vector[]{put(0.1, 0, 0.15), put(0.06, 0, 0.15), put(0.06, -0.04, 0.14), put(0.1, -0.04, 0.14)};
		body[0] = new polygon3D(v,v[0], v[1],  v[3], mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{put(-0.1, -0.04, 0.14), put(-0.06, -0.04, 0.14), put(-0.06, 0, 0.15), put(-0.1, 0, 0.15)};
		body[1] = new polygon3D(v,v[0], v[1],  v[3], mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{put(0.06, 0, -0.14), put(0.1, 0, -0.14), put(0.1, -0.04, -0.12), put(0.06, -0.04, -0.12)};
		body[2] = new polygon3D(v,v[0], v[1],  v[3], mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{ put(-0.06, -0.04, -0.12), put(-0.1, -0.04, -0.12), put(-0.1, 0, -0.14),put(-0.06, 0, -0.14)};
		body[3] = new polygon3D(v,v[0], v[1],  v[3], mainThread.textures[3], 1,0.5f,1);
		
		int i = 4;
		
		v = new vector[]{put(0.06, 0.06, 0.13), put(0.06, 0.06, 0.08), put(0.06, -0.01, 0.08), put(0.06, -0.01, 0.15)};
		body[0+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.1f,1);
		
		v = new vector[]{put(-0.06, -0.01, 0.15), put(-0.06, -0.01, 0.08), put(-0.06, 0.06, 0.08), put(-0.06, 0.06, 0.13)};
		body[1+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.1f,1);
		
		v = new vector[]{put(-0.06, 0.06, 0.09), put(0.06, 0.06, 0.09), put(0.06, 0.06, -0.13), put(-0.06, 0.06, -0.13)};
		body[2+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.1f,1);
		
		v = new vector[]{put(0.06, 0.06, 0.09), put(-0.06, 0.06, 0.09), put(-0.06, 0, 0.15), put(0.06, 0, 0.15)};
		body[3+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,0.4f,1);
		
		v = new vector[]{put(-0.1, 0.06, -0.13), put(0.1, 0.06, -0.13), put(0.1, 0, -0.14),  put(-0.1, 0, -0.14)};
		body[4+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,0.3f,1);
		
		v = new vector[]{put(0.06, 0.06, 0.13), put(0.1, 0.06, 0.13), put(0.1, 0.06, -0.13), put(0.06, 0.06, -0.13)};
		body[5+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.3f,0.8f,1);
		
		v = new vector[]{put(-0.06, 0.06, -0.13), put(-0.1, 0.06, -0.13), put(-0.1, 0.06, 0.13), put(-0.06, 0.06, 0.13)};
		body[6+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.3f,0.8f,1);
		
		v = new vector[]{put(0.1, 0.06, 0.13), put(0.06, 0.06, 0.13), put(0.06, 0., 0.15), put(0.1, 0., 0.15)};
		body[7+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.1f,1);
		
		v = new vector[]{put(-0.1, 0., 0.15), put(-0.06, 0., 0.15), put(-0.06, 0.06, 0.13),put(-0.1, 0.06, 0.13)};
		body[8+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.1f,1);
		
		v = new vector[]{put(0.1, 0.06, -0.13), put(0.1, 0.06, 0.13), put(0.1, 0, 0.15), put(0.1, 0, -0.14)};
		body[9+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,0.2f,1);
		
		v = new vector[]{put(-0.1, 0, -0.14), put(-0.1, 0, 0.15), put(-0.1, 0.06, 0.13), put(-0.1, 0.06, -0.13)};
		body[10+i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,0.2f,1);
		
		v = new vector[]{put(0.1, 0, 0.01), put(0.1, 0, 0.15), put(0.1, -0.04, 0.14), put(0.1, -0.04, 0.03)};
		body[11+i] = new polygon3D(v, put(0.1, 0.1, 0.03), put(0.1, 0.1, 0.13),  put(0.1, -0.04, 0.03), mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{put(0.1, 0, -0.14), put(0.1, 0, -0.01), put(0.1, -0.04, -0.03), put(0.1, -0.04, -0.12)};
		body[12+i] = new polygon3D(v, put(0.1, 0.1, -0.15), put(0.1, 0.1, -0.01),  put(0.1, -0.04, -0.15), mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{put(-0.1, -0.04, 0.03), put(-0.1, -0.04, 0.14), put(-0.1, 0, 0.15), put(-0.1, 0, 0.01)};
		body[13+i] = new polygon3D(v, put(-0.1, 0.1, 0.03), put(-0.1, 0.1, 0.13),  put(-0.1, -0.04, 0.03), mainThread.textures[3], 1,0.5f,1);
		
		v = new vector[]{put(-0.1, -0.04, -0.12), put(-0.1, -0.04, -0.03), put(-0.1, 0, -0.01), put(-0.1, 0, -0.14)};
		body[14+i] = new polygon3D(v, put(-0.1, 0.1, -0.15), put(-0.1, 0.1, -0.01),  put(-0.1, -0.04, -0.15), mainThread.textures[3], 1,0.5f,1);
		
		
		
		for(i = 0; i < body.length; i++){
		
			body[i].parentObject = this;
			
		}
		

		turretCenter = put(0, 0.065, -0.0);
		start.set(turretCenter);
		
		iDirection = new vector(1.1f,0,0);
		jDirection = new vector(0,1.1f,0);
		kDirection = new vector(0,0,1.05f);
		
		iDirection.rotate_XZ(360-turretAngle);
		kDirection.rotate_XZ(360-turretAngle);
		
		
		
		turret = new polygon3D[41 + 18 + 18];

		iDirection.scale(0.9f);
		kDirection.scale(0.9f);
		
		float f = 0.01f;
		vector [] v1 = new vector[]{
			put(-0.04, 0.036, 0.06 -f), put(0.04, 0.036, 0.06 -f), put(0.05, 0.036, 0.04-f), put(0.05, 0.036, -0.03-f), put(0.03, 0.036, -0.07-f),  put(-0.03, 0.036, -0.07-f),put(-0.05, 0.036, -0.03-f), put(-0.05, 0.036, 0.04-f)
		};
		
		
		v = new vector[]{
			v1[0].myClone(),
			v1[1].myClone(),
			v1[2].myClone(),
			v1[3].myClone(),
			v1[4].myClone(),
			v1[5].myClone(),
			v1[6].myClone(),
			v1[7].myClone()
		};
		
		turret[0] = new polygon3D(v, put(-0.04, 0.04, 0.19-f), put(0.04, 0.04, 0.19-f), put(-0.04, 0.04, 0.09-f), mainThread.textures[skinTextureIndex], 0.6f,0.6f,1);
		
		
		iDirection.scale(1f/0.75f);
		kDirection.scale(1f/0.8f);
		
		vector [] v2 = new vector[]{
			put(-0.04, 0, 0.06-f), put(0.04, 0, 0.06-f), put(0.05, 0, 0.04-f), put(0.05, 0, -0.03-f), put(0.03, 0, -0.07-f),  put(-0.03, 0, -0.07-f),put(-0.05, 0, -0.03-f), put(-0.05, 0, 0.04-f)
		};
		
		for(i = 0; i < 8; i++){
		
			v = new vector[]{v1[i].myClone(),v1[(i+7)%8].myClone(), v2[(i+7)%8].myClone(), v2[i].myClone()};	
			turret[1 + i] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.3f,0.3f,1);
		}
		
		
		
		double r1 = 0.0055;
		double r2 = 0.0075;
		double theta = Math.PI/8;
	
		start.y-=0.08f;
		
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*theta) - 0.018f, r2*Math.sin(i*theta)+0.093, 0.03),
							 put(r2*Math.cos((i+1)*theta) - 0.018f, r2*Math.sin((i+1)*theta)+0.093, 0.03),
							 put(r1*Math.cos((i+1)*theta)- 0.018f, r1*Math.sin((i+1)*theta)+0.093, 0.15),
							 put(r1*Math.cos(i*theta) - 0.018f, r1*Math.sin(i*theta)+0.093, 0.15)
							};
			turret[9 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[72], 10,10,1);
			turret[9 +i].Ambient_I -=15;
			turret[9 +i].reflectance -=30;
			turret[9 +i].findDiffuse();
		}
		
		
		
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*theta) + 0.018f, r2*Math.sin(i*theta)+0.093, 0.03),
							 put(r2*Math.cos((i+1)*theta) + 0.018f, r2*Math.sin((i+1)*theta)+0.093, 0.03),
							 put(r1*Math.cos((i+1)*theta)+ 0.018f, r1*Math.sin((i+1)*theta)+0.093, 0.15),
							 put(r1*Math.cos(i*theta) + 0.018f, r1*Math.sin(i*theta)+0.093, 0.15)
							};
			turret[25 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[72], 10,10,1);
			turret[25 +i].Ambient_I -=15;
			turret[25 +i].reflectance -=30;
			turret[25 +i].findDiffuse();
		}
		
		
		
		
		double r3 = 0.009;
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r3*Math.cos(i*theta) + 0.018f, r3*Math.sin(i*theta)+0.093, 0.08),
							 put(r3*Math.cos((i+1)*theta) + 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.08),
							 put(r3*Math.cos((i+1)*theta)+ 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.1),
							 put(r3*Math.cos(i*theta) + 0.018f, r3*Math.sin(i*theta)+0.093, 0.1)
							};
			turret[41 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
			turret[41 +i].Ambient_I -=15;
			turret[41 +i].reflectance -=30;
			turret[41 +i].findDiffuse();
		
		}
		
		v = new vector[16];
		for(i = 0; i < 16; i ++){
			v[i] = put(r3*Math.cos((i+1)*theta)+ 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.1);
		}
		turret[57] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		
		v = new vector[16];
		for(i = 0; i < 16; i ++){
			v[15 - i] = put(r3*Math.cos((i+1)*theta) + 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.08);
		}
		turret[58] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		
		
		for(i = 0; i < 16; i++){
			v = new vector[]{put(r3*Math.cos(i*theta) - 0.018f, r3*Math.sin(i*theta)+0.093, 0.08),
							 put(r3*Math.cos((i+1)*theta) - 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.08),
							 put(r3*Math.cos((i+1)*theta)- 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.1),
							 put(r3*Math.cos(i*theta) - 0.018f, r3*Math.sin(i*theta)+0.093, 0.1)
							};
			turret[59 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
			turret[59 +i].Ambient_I -=15;
			turret[59 +i].reflectance -=30;
			turret[59 +i].findDiffuse();
		
		}
		
		v = new vector[16];
		for(i = 0; i < 16; i ++){
			v[i] = put(r3*Math.cos((i+1)*theta)- 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.1);
		}
		turret[75] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		
		v = new vector[16];
		for(i = 0; i < 16; i ++){
			v[15 - i] = put(r3*Math.cos((i+1)*theta) - 0.018f, r3*Math.sin((i+1)*theta)+0.093, 0.08);
		}
		turret[76] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[25], 10,10,1);
		
		
		for(i = 0; i < turret.length; i++){
		
			turret[i].parentObject = this;
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
				attacker.experience+=50;
			return;
		}
		
		if(experience >= 80){
			myDamage = 25;
			level = 1;
			if(experience >= 160){
				level = 2;
				myDamage = 40;
				if(currentHP < maxHP && mainThread.frameIndex%8==0)
					currentHP++;
			}
		}
		
		if(canSelfRepair && currentHP < maxHP && mainThread.frameIndex%6==0){
			currentHP++;
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
		visionBoundary.y = (int)(tempCentre.screenY - 650);
		visionInsideScreen = camera.screen.intersects(visionBoundary);
		
		if(attackStatus == isAttacking && targetObject != null &&  targetObject.teamNo != teamNo)
			exposedCountDown = 64;
		
		//create vision for enemy commander
		if(teamNo == 1){
			xPos = boundary2D.x1/16 - 6 + 10;
			yPos = 127 - boundary2D.y1/16 - 6 + 10;
			
			for(int y = 0; y < 13; y++){
				for(int x = 0; x < 13; x++){
					if(bitmapVisionForEnemy[x+ y*13])
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
				tempFloat[4] = 0;
				theAssetManager.visionPolygonCount++;
			}
		}
		
		//check if the tank object is visible in mini map
		visible_minimap = theAssetManager.minimapBitmap[boundary2D.x1/16 + (127 - (boundary2D.y1-1)/16)*128];

		if(teamNo == 0 || attackStatus == isAttacking || exposedCountDown > 0 || visible_minimap){
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1/16;
			tempInt[2] = 127 - boundary2D.y1/16;
			tempInt[3] = 0;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else
				tempInt[4] = exposedCountDown;
			theAssetManager.unitsForMiniMapCount++;
		}
		
		
		
		
		//test if the tank object is visible in camera point of view
		if(visible_minimap){
			if(currentHP <= 160 && (mainThread.frameIndex + ID) % 3 ==0){
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
			
			//if the tank object is visible then draw it on the shadow buffer from light point of view
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
			performStandByLogic();
			
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
	
	//the tank will attack with any hostile unit that moved into its firing range
	public void performStandByLogic(){
		//scan for hostile unit
		if((ID + mainThread.frameIndex)%32 == 0){
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
					fireBullet(attackAngle);
			}else{
				fireBullet(attackAngle);
				
			
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
						if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
							if(tile[j].type < 100 || tile[j].type >= 199){
								if((tile[j].centre.x - centre.x)*(tile[j].centre.x - centre.x) + (tile[j].centre.z - centre.z)*(tile[j].centre.z - centre.z) <= attackRange*attackRange){
								attack(tile[j]);
								currentCommand = attackInNumbers;
								return;
								}
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
	

	public void fireBullet(int attackAngle){
		
		int theDamage = myDamage;
		
		if(attackCoolDown == 0 && targetObject.currentHP >0  && hasLineOfSightToTarget){
			//if there is nothing between the tank and its target
			
			
			
			if(targetObject.type == 6)
				theDamage = 20;
			
			firingPosition.set(0.022f, -0.4f, 0.2f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(centre.x, 0, centre.z);
			theAssetManager.spawnBullet(attackAngle, theDamage, targetObject, firingPosition, this);
			attackCoolDown = myAttackCooldown;
			
			//spawn a mini explosion  
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = firingPosition.x;
			tempFloat[1] = firingPosition.y;
			tempFloat[2] = firingPosition.z;
			tempFloat[3] = 0.4f;
			tempFloat[4] = 3;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = centre.y;
			theAssetManager.explosionCount++;
		}
		
		if(attackCoolDown == myAttackCooldown - 8 && targetObject.currentHP >0 && hasLineOfSightToTarget){
			
			if(targetObject.type == 6)
				theDamage = 20;
			
			firingPosition.set(-0.022f, -0.4f, 0.2f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(centre.x, 0, centre.z);
			theAssetManager.spawnBullet(attackAngle, theDamage, targetObject, firingPosition, this);
			
			//spawn a mini explosion  
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = firingPosition.x;
			tempFloat[1] = firingPosition.y;
			tempFloat[2] = firingPosition.z;
			tempFloat[3] = 0.4f;
			tempFloat[4] = 3;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = centre.y;
			theAssetManager.explosionCount++;
		
		}
	}
	
	
	public void draw(){
		if(!visible)
			return;
		
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
