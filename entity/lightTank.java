package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//light tank 3D model

public class lightTank extends Tank {
	public vector iDirectionBody, jDirectionBody, kDirectionBody, iDirectionTurret, jDirectionTurret, kDirectionTurret;
	public static int maxHP = 120;

	//attack range
	public int attackCoolDown;
	public vector firingPosition;
	
	//the offsreen angles/movement are the accumulated changes that the object  made during offscreen. 
	public int bodyAngleDelta_offscreen, turretAngleDelta_offscreen; 
	public vector movement_offscreen;
	
	//whether the geometry of the object in world coordinate neesd to be updated in the current frame
	public boolean geometryNeedModify;

	public int bodyTurnRate = 8; 
	public int turretTurnRate = 10;
	public int myAttackCooldown= 28;
	
	//once the  tank starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	
	//index of the tiles to check when the tank is idle
	public static int[] tileCheckList, tileCheckList_player, tileCheckList_enemy;
	
	
	public lightTank(vector origin, int bodyAngle, int teamNo){
		speed = 0.012f;
		attackRange = 1.60f;
		groupAttackRange = 1.2f;
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
		type = 0;
		myDamage = 10;
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
		
		//create main axis in object space
		iDirection = new vector(0.95f*0.97f,0,0); 
		jDirection = new vector(0,0.63f*0.97f,0);
		kDirection = new vector(0,0,0.95f*0.97f);
		
		iDirection.rotate_XZ(360-bodyAngle);
		kDirection.rotate_XZ(360-bodyAngle);
		
		//create axis for body and turret 
		iDirectionBody = iDirection.myClone();
		jDirectionBody = new vector(0,0.8f,0);
		kDirectionBody = kDirection.myClone();
		
		iDirectionTurret = iDirection.myClone();
		jDirectionTurret = jDirection.myClone();
		kDirectionTurret = kDirection.myClone();
		jDirectionTurret.scale(1.1f);
		kDirectionTurret.scale(0.9f);
		
	
		//create polygons 
		makePolygons();

		
		movement_offscreen = new vector(0,0,0);
		
	}
	
	public void makePolygons(){
		 
		bodyCenter.y-=0.18f;
		start.set(bodyCenter);
		
		int skinTextureIndex = 2;
		if(teamNo != 0)
			skinTextureIndex = 10;
		
		body = new polygon3D[15];
		v = new vector[]{put(-0.071, 0.025, 0.11), put(-0.071, 0.025, -0.11), put(-0.071, 0.005, -0.11), put(-0.071, -0.025, -0.08), put(-0.071, -0.025, 0.07), put(-0.071, 0.005, 0.11)};
		body[0] = new polygon3D(v, put(-0.071, 0.027, 0.11), put(-0.071, 0.027, -0.11), put(-0.071, -0.025, 0.11), mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(0.071, 0.005, 0.11), put(0.071, -0.025, 0.07), put(0.071, -0.025, -0.08), put(0.071, 0.005, -0.11), put(0.071, 0.025, -0.11), put(0.071, 0.025, 0.11)};
		body[1] = new polygon3D(v, put(0.071, 0.027, -0.11),put(0.071, 0.027, 0.11), put(0.071, -0.025, -0.11), mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(-0.06, 0.055, 0.05), put(0.06, 0.055, 0.05), put(0.06, 0.055, -0.1), put(-0.06, 0.055, -0.1)};
		body[2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.9f, 1);
		
		v = new vector[]{put(-0.07, 0.04, 0.11), put(0.07, 0.04, 0.11), put(0.06, 0.055, 0.05), put(-0.06, 0.055, 0.05)};
		body[3] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
	
		v = new vector[]{put(-0.06, 0.055, 0.05),put(-0.06, 0.055, -0.1), put(-0.07, 0.04, -0.11), put(-0.07, 0.04, 0.11)};
		body[4] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
	
		v = new vector[]{put(0.07, 0.04, 0.11), put(0.07, 0.04, -0.11), put(0.06, 0.055, -0.1),put(0.06, 0.055, 0.05)};
		body[5] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(-0.06, 0.055, -0.1), put(0.06, 0.055, -0.1), put(0.07, 0.04, -0.11), put(-0.07, 0.04, -0.11)};
		body[6] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(0.07, 0.04, 0.11), put(-0.07, 0.04, 0.11), put(-0.07, 0.01, 0.11), put(0.07, 0.01, 0.11)};
		body[7] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
	
		v = new vector[]{put(-0.07, 0.04, 0.11), put(-0.07, 0.04, -0.11), put(-0.07, 0.015, -0.11), put(-0.07, 0.005, -0.09), put(-0.07, 0.005, 0.09),put(-0.07, 0.015, 0.11)};
		body[8] = new polygon3D(v, put(-0.07, 0.04, 0.11), put(-0.07, 0.04, -0.11), put(-0.07, 0.025, 0.11), mainThread.textures[skinTextureIndex], 1,0.3f,1);
	
		v = new vector[]{put(0.07, 0.015, 0.11), put(0.07, 0.005, 0.09), put(0.07, 0.005, -0.09), put(0.07, 0.015, -0.11), put(0.07, 0.04, -0.11),put(0.07, 0.04, 0.11)};
		body[9] = new polygon3D(v, put(0.07, 0.04, 0.11), put(0.07, 0.04, -0.11), put(0.07, 0.025, 0.11), mainThread.textures[skinTextureIndex], 1,0.3f,1);
	
		v = new vector[]{put(-0.07, 0.04, -0.11), put(0.07, 0.04, -0.11), put(0.07, 0.015, -0.11), put(-0.07, 0.015, -0.11)};
		body[10] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
		
		v = new vector[]{put(-0.07, 0.005, -0.11), put(-0.04, 0.005, -0.11), put(-0.04, -0.025, -0.08), put(-0.07, -0.025, -0.08)};
		body[11] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(-0.07, 0.015, -0.11), put(-0.04, 0.015, -0.11), put(-0.04, 0.005, -0.11), put(-0.07, 0.005, -0.11)};
		body[12] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(0.04, 0.015, -0.11), put(0.07, 0.015, -0.11), put(0.07, 0.005, -0.11), put(0.04, 0.005, -0.11)};
		body[13] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
		
		v = new vector[]{put(0.04, 0.005, -0.11), put(0.07, 0.005, -0.11), put(0.07, -0.025, -0.08), put(0.04, -0.025, -0.08)};
		body[14] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
		
		
		
		for(int i = 0; i < body.length; i++){
			body[i].Ambient_I+=6;
			body[i].findDiffuse();
			body[i].parentObject = this;
			
		}
		

		turretCenter = put(0, 0.065, -0.0);
		start.set(turretCenter);
		
		turret = new polygon3D[11];
		kDirection.set(kDirectionTurret);
		
		v = new vector[]{put(0.04, 0.035, 0.06), put(-0.04, 0.035, 0.06), put(-0.04, 0, 0.06), put(0.04, 0, 0.06)};
		turret[0] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.6f,0.3f,1);
		
		v = new vector[]{put(0, 0.04, 0.18), put(0.006, 0.03, 0.18), put(0.008, 0.025, 0.06), put(0, 0.035, 0.06)};
		turret[1] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.1f,1,1);
		
		v = new vector[]{ put(0, 0.035, 0.06), put(-0.008, 0.025, 0.06), put(-0.006, 0.03, 0.18),put(0, 0.04, 0.18)};
		turret[2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.1f,1,1);
		
		v = new vector[]{put(-0.04, 0.035, 0.06), put(0.04, 0.035, 0.06), put(0.05, 0.035, 0.04), put(0.05, 0.035, -0.03), put(0.03, 0.035, -0.07),  put(-0.03, 0.035, -0.07),put(-0.05, 0.035, -0.03), put(-0.05, 0.035, 0.04)};
		turret[3] = new polygon3D(v, put(-0.04, 0.035, 0.19), put(0.04, 0.035, 0.19), put(-0.04, 0.035, 0.09), mainThread.textures[skinTextureIndex], 0.6f,0.6f,1);
		
		v = new vector[]{put(0.03, 0, -0.07), put(-0.03, 0, -0.07),  put(-0.03, 0.035, -0.07),   put(0.03, 0.035, -0.07)};
		turret[4] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.4f,0.2f,1);
		
		v = new vector[]{put(0.03, 0.035, -0.07), put(0.05, 0.035, -0.03), put(0.05, 0, -0.03), put(0.03, 0, -0.07)};
		turret[5] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.4f,0.2f,1);
		
		v = new vector[]{put(-0.03, 0, -0.07), put(-0.05, 0, -0.03), put(-0.05, 0.035, -0.03), put(-0.03, 0.035, -0.07)};
		turret[6] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.4f,0.2f,1);
		
		v = new vector[]{put(0.05, 0.035, -0.03), put(0.05, 0.035, 0.04), put(0.05, 0, 0.04), put(0.05, 0, -0.03)};
		turret[7] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.5f,0.3f,1);
		
		v = new vector[]{put(-0.05, 0, -0.03), put(-0.05, 0, 0.04), put(-0.05, 0.035, 0.04), put(-0.05, 0.035, -0.03)};
		turret[8] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.5f,0.3f,1);
		
		v = new vector[]{put(0.05, 0.035, 0.04), put(0.04, 0.035, 0.06), put(0.04, 0, 0.06), put(0.05, 0, 0.04)};
		turret[9] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.3f,1);
		
		v = new vector[]{put(-0.05, 0, 0.04), put(-0.04, 0, 0.06), put(-0.04, 0.035, 0.06), put(-0.05, 0.035, 0.04)};
		turret[10] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 0.3f,0.3f,1);
		
		for(int i = 0; i < turret.length; i++){
			turret[i].Ambient_I+=6;
			turret[i].findDiffuse();
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
				attacker.experience+=10;
			return;
		}
		
		if(experience >= 40){
			myDamage = 18;
			level = 1;
			if(experience >= 80){
				level = 2;
				myDamage = 30 ;
				if(currentHP < maxHP && mainThread.gameFrame%12==0)
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
		
		visionBoundary.x = (int) (tempCentre.screenX - visionW);
		visionBoundary.y = (int) (tempCentre.screenY - visionH);
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
			if(currentHP <= 60 && (mainThread.gameFrame + ID) % 3 ==0){
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
		if(teamNo == 0)
			tileCheckList = tileCheckList_player;
		else
			tileCheckList = tileCheckList_enemy;
		
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
					if(s.type > 100 && s.type < 200 && s != targetObject){
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
		if(teamNo == 0)
			tileCheckList = tileCheckList_player;
		else
			tileCheckList = tileCheckList_enemy;
		
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
		if(attackCoolDown == 0 && targetObject.currentHP >0  && hasLineOfSightToTarget){
			//if there is nothing between the tank and its target
			firingPosition.set(0, -0.4f, 0.18f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(centre.x, 0, centre.z);
			//deal bonus damage against heavy tank
			int theDamage = myDamage;
			if(targetObject.type == 7)
				theDamage*=1.5;
			if(targetObject.type == 0 || targetObject.type == 1)
				theDamage*=1.2;
			if(targetObject.type >= 100)
				theDamage*=0.6;
			
			theAssetManager.spawnBullet(attackAngle, theDamage, targetObject, firingPosition, this);
			attackCoolDown = myAttackCooldown;

			spawnMiniExplosion(firingPosition);
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
