package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;


//rocket tank 3D model

public class rocketTank extends solidObject{
	
	public vector iDirectionBody, jDirectionBody, kDirectionBody, iDirectionTurret, jDirectionTurret, kDirectionTurret;
	
	public vector bodyCenter, turretCenter, turretCenterClone;
	
	public polygon3D[] body, turret, turretClone;
	
	public static int maxHP = 70;
	
	//a screen space boundary which is used to test if the tank object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-70,-25,908, 597);
	
	//a screen space boundary which is used to test if the entire tank object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432);
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1400, 1300);
	
	//a bitmap representation of the vision of the tank for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	public static boolean[] bitmapVisionGainFromAttackingUnit;
	
	//the oreintation of the tank
	public int bodyAngle, turretAngle, turretAngleClone;
	
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
	

	public int bodyTurnRate = 6; 
	public int turretTurnRate = 8;
	public int myAttackCooldown= 200;
	

	//once the  tank starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	

	
	//index of the tiles to check when the tank is idle
	public static int[] tileCheckList;
	
	public int damageMultiplier = 1;
		
		
	public rocketTank(vector origin, int bodyAngle, int teamNo){
		speed = 0.01f;
		attackRange = 2.86f;
		groupAttackRange = 2.6f;
		start = origin.myClone();
		centre = origin.myClone();
		tempCentre = origin.myClone();
		bodyCenter = origin.myClone();
		this.bodyAngle = bodyAngle;
		this.immediateDestinationAngle = bodyAngle;
		turretAngle  = bodyAngle;
		turretAngleClone = bodyAngle;
		destinationAngle = bodyAngle;
		bodyAngleSum = bodyAngle;
		this.teamNo = teamNo;
		currentHP = maxHP;
		type = 1;  
		myDamage = 27;
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(6);
			bitmapVisionGainFromAttackingUnit = createBitmapVision(2);
		}
		
		
		ID = globalUniqID++;
		randomNumber = gameData.getRandom();
		height = centre.y + 0.5f;  //?
		theAssetManager = mainThread.theAssetManager; 
		boundary2D = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		movement = new vector(0,0,0);
		updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
		boundary2D.owner = this;
		destinationBlock = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		probeBlock = new Rect((int)(origin.x*64) - 6, (int)(origin.z*64) + 6, 12, 12);
		firingPosition = new vector(0,-0.1f,0);  //??
		
		
		
		//create main axis in object space
		iDirection = new vector(0.92f,0,0);   
		jDirection = new vector(0,0.92f,0);   
		kDirection = new vector(0,0,0.92f);  
		
		
		
		//create axis for body and turret 
		iDirectionBody = iDirection.myClone();
		jDirectionBody = jDirection.myClone();
		kDirectionBody = kDirection.myClone();
		
		iDirectionBody.rotate_XZ(360-bodyAngle);
		kDirectionBody.rotate_XZ(360-bodyAngle);
		
		
		iDirectionBody.scale(0.85f);
		jDirectionBody.scale(0.6f);
		kDirectionBody.scale(0.95f);
		
		
		//create axis for turret
		iDirectionTurret = iDirection.myClone();
		jDirectionTurret = jDirection.myClone();
		kDirectionTurret = kDirection.myClone();
		
		jDirectionTurret.rotate_YZ(340);
		kDirectionTurret.rotate_YZ(340);
		
		iDirectionTurret.rotate_XZ(360-bodyAngle);
		jDirectionTurret.rotate_XZ(360-bodyAngle);
		kDirectionTurret.rotate_XZ(360-bodyAngle);
				
		
		iDirectionTurret.scale(0.9f);
		jDirectionTurret.scale(0.75f);
		kDirectionTurret.scale(0.95f);
		
		
	
		//create polygons 
		makePolygons();

		
		movement_offscreen = new vector(0,0,0);
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(12f);   //?
		}
		
		
	}
	
	public void makePolygons(){
		start.set(bodyCenter);
		start.y-=0.18f;
		
		iDirection.set(iDirectionBody);
		jDirection.set(jDirectionBody);
		kDirection.set(kDirectionBody);
		
		int skinTextureIndex = 18;
		if(teamNo != 0)
			skinTextureIndex = 10;
		
		body = new polygon3D[14];
		v = new vector[]{put(-0.07, 0.055, 0.07), put(0.07, 0.055, 0.07), put(0.07, 0.055, -0.13), put(-0.07, 0.055, -0.13)};
		body[0] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,1,1);
	
		v = new vector[]{put(-0.069, 0.055, 0.13), put(-0.069, 0.055, -0.13), put(-0.069, 0.02, -0.13), put(-0.069, 0.02, 0.13)};
		body[1] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.1f,1);
		
		v = new vector[]{put(0.069, 0.02, 0.13), put(0.069, 0.02, -0.13), put(0.069, 0.055, -0.13), put(0.069, 0.055, 0.13)};
		body[2] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.1f,1);
		
		v = new vector[]{put(0.07, 0.1, 0.13), put(-0.07, 0.1, 0.13), put(-0.07, 0.02, 0.13), put(0.07, 0.02, 0.13)};
		body[3] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.07, 0.14, 0.11), put(-0.07, 0.14, 0.11), put(-0.07, 0.1, 0.13), put(0.07, 0.1, 0.13)};
		body[4] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[21], 1,0.3f,1);
		
		v = new vector[]{put(0.07, 0.14, 0.07), put(0.07, 0.14, 0.11), put(0.07, 0.1, 0.13),  put(0.07, 0.055, 0.13), put(0.07, 0.055, 0.07)};
		body[5] = new polygon3D(v, v[0], v[1], v[4], mainThread.textures[skinTextureIndex], 0.4f,0.3f,1);
		
		v = new vector[]{put(-0.07, 0.055, 0.07),  put(-0.07, 0.055, 0.13), put(-0.07, 0.1, 0.13),put(-0.07, 0.14, 0.11), put(-0.07, 0.14, 0.07), };
		body[6] = new polygon3D(v, v[0], v[1], v[4], mainThread.textures[skinTextureIndex], 0.4f,0.3f,1);
		
		v = new vector[]{put(-0.07, 0.14, 0.11), put(0.07, 0.14, 0.11), put(0.07, 0.14, 0.07), put(-0.07, 0.14, 0.07)};
		body[7] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(-0.07, 0.14, 0.07), put(0.07, 0.14, 0.07), put(0.07, 0.055, 0.07), put(-0.07, 0.055, 0.07)};
		body[8] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(-0.07, 0.055, -0.13), put(0.07, 0.055, -0.13), put(0.07, 0.02, -0.13), put(-0.07, 0.02, -0.13)};
		body[9] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(-0.068, 0.021, 0.13), put(-0.068, 0.021, -0.13), put(-0.068, -0.03, -0.11), put(-0.068, -0.03, 0.11)};
		body[10] = new polygon3D(v, put(-0.068, 0.021, 0.13), put(-0.068, 0.021, -0.13), put(-0.068, -0.03, 0.13), mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(0.068, -0.03, 0.11), put(0.068, -0.03, -0.11), put(0.068, 0.021, -0.13), put(0.068, 0.021, 0.13)};
		body[11] = new polygon3D(v, put(0.068, 0.021, -0.13), put(0.068, 0.021, 0.13), put(0.068, -0.03, -0.13), mainThread.textures[3], 1,1,1);
		
		
		
		v = new vector[]{put(0.068, 0.021, 0.13), put(0.04, 0.021, 0.13), put(0.04, -0.03, 0.11),  put(0.068, -0.03, 0.11)};
		body[12] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(-0.04, 0.021, 0.13), put(-0.068, 0.021, 0.13), put(-0.068, -0.03, 0.11),  put(-0.04, -0.03, 0.11)};
		body[13] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[3], 1,1,1);
		
		turretCenter = put(0, 0.08, -0.05);
		start.set(turretCenter);
		
		iDirection.set(iDirectionTurret);
		jDirection.set(jDirectionTurret);
		kDirection.set(kDirectionTurret);
		
		
		turret = new polygon3D[5];
		v = new vector[]{put(-0.06, 0.065, 0.09), put(0.06, 0.065, 0.09), put(0.06, 0.065, -0.08), put(-0.06, 0.065, -0.08)};
		turret[0] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,1,1);
		
		v = new vector[]{put(-0.06, 0.065, 0.09), put(-0.06, 0.065, -0.08), put(-0.06, 0.01, -0.08), put(-0.06, 0.01, 0.09)};
		turret[1] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.06, 0.01, 0.09), put(0.06, 0.01, -0.08), put(0.06, 0.065, -0.08), put(0.06, 0.065, 0.09)};
		turret[2] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.06, 0.065, 0.09), put(-0.06, 0.065, 0.09), put(-0.06, 0.01, 0.09),  put(0.06, 0.01, 0.09)};
		turret[3] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[22], 1,0.5f,1);
		turret[3].shadowBias = 80000;
		
		
		v = new vector[]{put(0.06, 0.01, -0.08), put(-0.06, 0.01, -0.08), put(-0.06, 0.065, -0.08) , put(0.06, 0.065, -0.08)};
		turret[4] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.5f,1);
		
		
		turretCenterClone = turretCenter.myClone();
		turretClone = new polygon3D[5];
		v = new vector[]{put(-0.06, 0.065, 0.09), put(0.06, 0.065, 0.09), put(0.06, 0.065, -0.08), put(-0.06, 0.065, -0.08)};
		turretClone[0] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,1,1);
		
		v = new vector[]{put(-0.06, 0.065, 0.09), put(-0.06, 0.065, -0.08), put(-0.06, 0.01, -0.08), put(-0.06, 0.01, 0.09)};
		turretClone[1] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.06, 0.01, 0.09), put(0.06, 0.01, -0.08), put(0.06, 0.065, -0.08), put(0.06, 0.065, 0.09)};
		turretClone[2] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.06, 0.065, 0.09), put(-0.06, 0.065, 0.09), put(-0.06, 0.01, 0.09),  put(0.06, 0.01, 0.09)};
		turretClone[3] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[22], 1,0.5f,1);
		
		v = new vector[]{put(0.06, 0.01, -0.08), put(-0.06, 0.01, -0.08), put(-0.06, 0.065, -0.08) , put(0.06, 0.065, -0.08)};
		turretClone[4] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.5f,1);
		
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
				attacker.experience+=15;
			return;
		}
		
		
		if(experience >= 50){
			myDamage = 33;
			level = 1;
			if(experience >= 100){
				level = 2;
				myDamage = 44;
				if(currentHP < maxHP && mainThread.frameIndex%16==0)
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
			//update centre
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
		
		
		
		visionBoundary.x = (int)(tempCentre.screenX - 700);
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
			if(currentHP <= maxHP/2 && (mainThread.frameIndex + ID) % 3 ==0){
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
		turretCenter.subtract(centre);
		turretCenter.rotate_XZ(angle);
		turretCenter.add(centre);
		tempVector.set(turretCenter);
		tempVector.subtract(turretCenterClone);
		
		
		//update turret polygons
		for(int i = 0; i < turret.length; i++){
			if(turret[i].textureFitPolygon == false){
				//perform vertex updates in world coordinate
				turret[i].origin.set(turretClone[i].origin);
				turret[i].origin.subtract(turretCenterClone);
				turret[i].origin.rotate_XZ(turretAngleClone);
				turret[i].origin.rotate_XZ(360-turretAngle);
				turret[i].origin.add(turretCenter);
				
				
				turret[i].bottomEnd.set(turretClone[i].bottomEnd);
				turret[i].bottomEnd.subtract(turretCenterClone);
				turret[i].bottomEnd.rotate_XZ(turretAngleClone);
				turret[i].bottomEnd.rotate_XZ(360-turretAngle);
				turret[i].bottomEnd.add(turretCenter);
				
				
				turret[i].rightEnd.set(turretClone[i].rightEnd);
				turret[i].rightEnd.subtract(turretCenterClone);
				turret[i].rightEnd.rotate_XZ(turretAngleClone);
				turret[i].rightEnd.rotate_XZ(360-turretAngle);
				turret[i].rightEnd.add(turretCenter);
				
			}
			
			for(int j = 0; j < turret[i].vertex3D.length; j++){
				turret[i].vertex3D[j].set(turretClone[i].vertex3D[j]);
				turret[i].vertex3D[j].subtract(turretCenterClone);
				turret[i].vertex3D[j].rotate_XZ(turretAngleClone);
				turret[i].vertex3D[j].rotate_XZ(360-turretAngle);
				turret[i].vertex3D[j].add(turretCenter);
			}
			
			
			turret[i].normal.set(turretClone[i].normal);
			turret[i].normal.rotate_XZ(turretAngleClone);
			turret[i].normal.rotate_XZ(360-turretAngle);
			
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
	
		}else if(currentCommand == follow){
			
		}else if(currentCommand == attackMove){
			performAttackMoveLogic();
			avoidGettingStucked();
		}
		
	}
	
	//the tank will attack with any hostile unit that moved into its firing range
	public void performStandByLogic(){
		
		//scan for hostile unit
		boolean[] bitmapVision;
		if(teamNo == 0)
			bitmapVision = theAssetManager.minimapBitmap;
		else
			bitmapVision = enemyCommander.visionMap;
		
		if((ID + mainThread.frameIndex)%32 == 0){
			currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
			
			for(int i = 0; i < tileCheckList.length; i++){
				if(tileCheckList[i] != Integer.MAX_VALUE){
					int index = currentOccupiedTile + tileCheckList[i];
					if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
						continue;
					
					tile = mainThread.gridMap.tiles[index];
					
					if(!bitmapVision[index]){
						boolean isRevealedBuilding = false;
						if(tile[4] != null)
							if(tile[4].type > 100 && tile[4].ID != -1)
								if((tile[4].isRevealed == true && teamNo == 0) || (mainThread.ec.theMapAwarenessAI.mapAsset[tile[4].ID] != null && teamNo != 0) )
									isRevealedBuilding = true;
						if(!isRevealedBuilding)
							continue;
					}
					
					
					
					for(int j = 0; j < 4; j++){
						if(tile[j] != null){
							if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){	
								attack(tile[j]); 
								currentCommand = solidObject.attackCautiously;
								secondaryCommand = solidObject.StandBy;
								
								/*
								attackMoveTo((tile[j].centre.x + centre.x)/2, (tile[j].centre.z+centre.z)/2);
								currentCommand = solidObject.attackMove;
								secondaryCommand = solidObject.attackMove;
								*/
								
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
		
		
		if(currentMovementStatus !=  hugRight && currentMovementStatus != hugLeft){
			calculateMovement();
			destinationAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			immediateDestinationAngle = destinationAngle;
		}
		
		
		if((currentCommand == attackInNumbers && distanceToDesination <=  groupAttackRange) || (currentCommand == attackCautiously && distanceToDesination <  attackRange)){
			movement.reset();
			currentMovementStatus = freeToMove; 
			obstacle = null;
		}
		
		if(distanceToDesination <= attackRange){
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
					fireRocket(attackAngle);
			}else{
				fireRocket(attackAngle);
				
			
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
			if(!(distanceToDesination <  attackRange &&  movement.x ==0 && movement.z ==0)){
				bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
				bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
				movement.reset();
			}else{
				bodyAngleDelta = 0;
			}
			
		}else{
			if(bodyAngle != immediateDestinationAngle){
				if(!(distanceToDesination <  attackRange && movement.x ==0 && movement.z ==0)){
					bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
					bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					
				}
			}else{
			
				bodyAngleDelta = 0;
			}
		
			if(currentMovementStatus == hugRight || currentMovementStatus == hugLeft){
			
				hugWalls();
				
				if(distanceToDesination <= attackRange){
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
			
			if(distanceToDesination <= attackRange){
				
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
		
		boolean[] bitmapVision;
		if(teamNo == 0)
			bitmapVision = theAssetManager.minimapBitmap;
		else
			bitmapVision = enemyCommander.visionMap;
		
		solidObject target = null;
		for(int i = 0; i < tileCheckList.length; i++){
			if(tileCheckList[i] != Integer.MAX_VALUE){
				int index = currentOccupiedTile + tileCheckList[i];
				if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
					continue;
				
				tile = mainThread.gridMap.tiles[index];
				
				//ignore unrevealed buildings
				if(!bitmapVision[index]){
					boolean isRevealedBuilding = false;
					if(tile[4] != null)
						if(tile[4].type > 100 && tile[4].ID != -1)
							if((tile[4].isRevealed == true && teamNo == 0) || (mainThread.ec.theMapAwarenessAI.mapAsset[tile[4].ID] != null && teamNo != 0))
								isRevealedBuilding = true;
					if(!isRevealedBuilding)
						continue;
				}
				
				//target hostile  static  defense first, then hostile units, and finally hostile buildings
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){
						if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
							distanceToDesination = (float)Math.sqrt((tile[j].centre.x - centre.x) * (tile[j].centre.x - centre.x) + (tile[j].centre.z - centre.z) * (tile[j].centre.z - centre.z));
							if(distanceToDesination <= attackRange){
								if((tile[j].type == 199 || tile[j].type == 200)  && (tile[j].visible_minimap || (teamNo !=0 && tile[j].isRevealed_AI) )){
									attack(tile[j]);
									currentCommand = solidObject.attackInNumbers;
									return;
								}else{
									if(target == null){
										target = tile[j];
									}else if(target.type >= 100 && tile[j].type < 100){
										target = tile[j];
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(target != null && (target.visible_minimap || teamNo != 0)){
			attack(target);
			currentCommand = solidObject.attackInNumbers;
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
				movement.scale(speed - distanceToDesination);
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
	
	public void calculateMovement_unit(){
		movement.set(destinationX - centre.x, 0, destinationY - centre.z);
		movement.unit();
		
	}
	
	

	
	public void fireRocket(int attackAngle){
		if(visible)
			tempVector.set(turretCenter);
		else{
			tempVector.set(centre);
			
		}
		
		float multiplier = 1; 
		if(targetObject.type > 100){
			multiplier = damageMultiplier;
			multiplier*=1.25f;
		}
		
		if(attackCoolDown == 0 && targetObject.currentHP >0 ){
			
			firingPosition.set(-0.03f, -0.3f, 0.12f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(tempVector.x, 0, tempVector.z);
			theAssetManager.spawnRocket(attackAngle, (int)(myDamage*multiplier), targetObject, firingPosition, this);
			attackCoolDown = myAttackCooldown;
			
			//spawn a mini explosion  
			firingPosition.set(-0.03f, -0.35f, 0.08f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(tempVector.x, 0, tempVector.z);
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = firingPosition.x;
			tempFloat[1] = firingPosition.y;
			tempFloat[2] = firingPosition.z;
			tempFloat[3] = 0.5f;
			tempFloat[4] = 2;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = centre.y;
			theAssetManager.explosionCount++;
			
		}
		
		if(attackCoolDown == myAttackCooldown - 10 && targetObject.currentHP >0 ){
			firingPosition.set(0.03f, -0.3f, 0.12f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(tempVector.x, 0, tempVector.z);
			theAssetManager.spawnRocket(attackAngle, (int)(myDamage*multiplier), targetObject, firingPosition, this);
			
			//spawn a mini explosion  
			firingPosition.set(0.03f, -0.35f, 0.08f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(tempVector.x, 0, tempVector.z);
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = firingPosition.x;
			tempFloat[1] = firingPosition.y;
			tempFloat[2] = firingPosition.z;
			tempFloat[3] = 0.5f;
			tempFloat[4] = 2;
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
