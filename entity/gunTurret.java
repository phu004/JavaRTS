package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the power plant model
public class gunTurret extends solidObject{
	
	public static int maxHP = 250;
	
	public int countDownToDeath = 16;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	public int [] tileIndex = new int[1];
	public int[] tempInt;
	
	public float[] tempFloat;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,920, 762);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,648, 402);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(0,0,768, 512);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//a bitmap representation of the vision of the power plant for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	public static boolean[] bitmapVisionGainFromAttackingUnit;
	
	//gunTurret never moves
	public final static vector movenment = new vector(0,0,0);
	
	
	//the oreintation of the turret
	public int turretAngle;
	
	//attack range
	public final static float attackRange = 2f;
		
	//the angle that the turret have rotated between current frame and previous frame
	public int turretAngleDelta, accumulatedDelta;
	
	public int turretTurnRate = 8;
	public int myAttackCooldown= 25;
	public int attackCoolDown;
	public vector firingPosition;
	
	
	//index of the tiles to check when the turret is in standby mode
	public static int[] tileCheckList;
	
	//once the  turret starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	
	public baseInfo theBaseInfo;
	
	public int attackAngle;
	public int randomInt;
	
	public gunTurret(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 200;
		
		myDamage = 15;
		
		ID = globalUniqID++;
		randomInt = gameData.getRandom();
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfGunTurret++;
		
		
		currentHP = maxHP;
		
		this.teamNo = teamNo;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(8f);
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(6);
			bitmapVisionGainFromAttackingUnit = createBitmapVision(2);
		}
	
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 8, (int)(z*64) + 8, 16, 16);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		tileIndex[0] = centerX/16 + (127 - centerY/16)*128; 
	
		
		mainThread.gridMap.tiles[tileIndex[0]][0] = this;  
		mainThread.gridMap.tiles[tileIndex[0]][1] = this;  
	    mainThread.gridMap.tiles[tileIndex[0]][2] = this;  
		mainThread.gridMap.tiles[tileIndex[0]][3] = this;  
		mainThread.gridMap.tiles[tileIndex[0]][4] = this;  
	
		

		//init model
		start = new vector(x,y,z);
		iDirection = new vector(1f,0,0);
		jDirection = new vector(0,1f,0);
		kDirection = new vector(0,0,1f);
		
		firingPosition = new vector(0,0,0);
		
		
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
		
		turretAngle = (int)(360*Math.random());
		
		makePolygons();
		
	}
	
	//create polygons
	public void makePolygons(){
		polygons = new polygon3D[46];	
		vector[] v;
		
		//turret base
		
		float l =0.07f;
		float h = 0.07f;
		v = new vector[]{put(-l,h, l), put(l,h, l), put(l,h, -l), put(-l,h, -l)};
		polygons[0] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 0.5f, 1);
		
		v = new vector[]{put(-l,h, -l), put(l,h, -l), put(l+0.04,h - 0.15f, -l-0.04), put(-l-0.04,h - 0.15f, -l-0.04)};
		polygons[1] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 0.5f, 1);
		
		
		v = new vector[]{put(-l-0.04,h - 0.15f, l+0.04), put(l+0.04,h - 0.15f, l+0.04), put(l,h, l), put(-l,h, l)};
		polygons[2] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 0.5f, 1);
		
		
		v = new vector[]{put(l,h, -l), put(l,h, l), put(l+0.04,h - 0.15f, l+0.04), put(l+0.04,h - 0.15f, -l-0.04)};
		polygons[3] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 0.5f, 1);
		
		v = new vector[]{put(-l-0.04,h - 0.15f, -l-0.04), put(-l-0.04,h - 0.15f, l+0.04), put(-l,h, l), put(-l,h, -l)};
		polygons[4] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 0.5f, 1);
		
		
		for(int i = 0; i < 5; i++){
			polygons[i].Ambient_I-=5;
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
			
		}
		
		//turret tower
		iDirection.scale(0.82f);
		kDirection.scale(0.75f);
		
		iDirection.rotate_XZ(360-turretAngle);
		jDirection.rotate_XZ(360-turretAngle);
		kDirection.rotate_XZ(360-turretAngle);
		
		h = 0.11f;
		
		
		vector a1 = put(-0.035, h, 0.08);
		vector a2 =  put(0.035, h, 0.08);
		vector a3 = put(0.06, h, 0.03);
		vector a4 = put(0.06, h, -0.05);
		vector a5 =  put(0.04, h, -0.07);
		vector a6 = put(-0.04, h, -0.07);
		vector a7 = put(-0.06, h, -0.05);
		vector a8  = put(-0.06, h, 0.03);
		
		int textureIndex = 66;
		if(teamNo != 0)
			textureIndex = 67;
		
		v = new vector[]{a1, a2, a3, a4,a5, a6, a7, a8};
		polygons[5] =  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[5].myClone(), mainThread.textures[textureIndex], 0.7f, 1f, 1);
		
		iDirection.scale(1.2f);
		kDirection.scale(1.2f);
		
		vector b1 = put(-0.035, 0.07, 0.08);
		vector b2 =  put(0.035, 0.07, 0.08);
		vector b3 = put(0.06, 0.07, 0.03);
		vector b4 = put(0.06, 0.07, -0.05);
		vector b5 =  put(0.04, 0.07, -0.07);
		vector b6 = put(-0.04, 0.07, -0.07);
		vector b7 = put(-0.06, 0.07, -0.05);
		vector b8  = put(-0.06, 0.07, 0.03);
		
		
		v = new vector[]{a2.myClone(), a1.myClone(), b1.myClone(), b2.myClone()};
		polygons[6] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		polygons[6].shadowBias = 20000;
		
		
		v = new vector[]{a1.myClone(), a8.myClone(), b8.myClone(), b1.myClone()};
		polygons[7] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[2].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a3.myClone(), a2.myClone(), b2.myClone(), b3.myClone()}; 
		polygons[8] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a4.myClone(), a3.myClone(), b3.myClone(), b4.myClone()};
		polygons[9] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a5.myClone(), a4.myClone(), b4.myClone(), b5.myClone()};
		polygons[10] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a6.myClone(), a5.myClone(), b5.myClone(), b6.myClone()};
		polygons[11] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a7.myClone(), a6.myClone(), b6.myClone(), b7.myClone()};
		polygons[12] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		v = new vector[]{a8.myClone(), a7.myClone(), b7.myClone(), b8.myClone()};
		polygons[13] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[textureIndex], 0.5f, 0.1f, 1);
		
		
		double r1 = 0.007;
		double r2 = 0.01;
		
		double theta = Math.PI/16;
	
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r2*Math.cos(i*theta), r2*Math.sin(i*theta)+0.093, 0.05),
							 put(r2*Math.cos((i+1)*theta), r2*Math.sin((i+1)*theta)+0.093, 0.05),
							 put(r1*Math.cos((i+1)*theta), r1*Math.sin((i+1)*theta)+0.093, 0.17),
							 put(r1*Math.cos(i*theta), r1*Math.sin(i*theta)+0.093, 0.17)
							};
			polygons[14 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 10,10,1);
			polygons[14 +i].Ambient_I -=15;
			polygons[14 +i].reflectance -=30;
			polygons[14 +i].findDiffuse();
		}
		
	}
		
		
	
	//update the model 
	public void update(){	
		//process emerging from  ground animation
		if(centre.y < -0.5f){
			centre.y+=0.01f;
			
			if(centre.y > -0.5){
				for(int i = 0; i < polygons.length; i++){		
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
				tempFloat[3] = 2.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 7;
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				theAssetManager.removeObject(this); 
				
				if(teamNo == 0)
					mainThread.pc.theBaseInfo.numberOfGunTurret--;
				else
					mainThread.ec.theBaseInfo.numberOfGunTurret--;
				
				
				//removeFromGridMap();
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=20;
				
				return;
			}else{
				
				if(mainThread.frameIndex%2==0){
					float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
					tempFloat[0] = centre.x + (float)Math.random()/4f - 0.125f;
					tempFloat[1] = centre.y + 0.15f;
					tempFloat[2] = centre.z + (float)Math.random()/4f - 0.125f;
					tempFloat[3] = 1.5f;
					tempFloat[4] = 1;
					tempFloat[5] = 0;
					tempFloat[6] = 6 + (gameData.getRandom()%4);
					tempFloat[7] = this.height;
					theAssetManager.explosionCount++; 
				}
				return;
			}
		}
		
		if(isRepairing && currentHP >0){
			if(mainThread.frameIndex%5==0 && theBaseInfo.currentCredit > 0 && currentHP <maxHP){
				currentHP+=1;
				theBaseInfo.currentCredit--;
			}
		}
		
		//process turret AI
		carryOutCommands();
		
		if(attackCoolDown > 0)
			attackCoolDown--;
		
		if(exposedCountDown > 0)
			exposedCountDown --;
		
		//mark itself on obstacle map
		mainThread.gridMap.currentObstacleMap[tileIndex[0]] = false;
	
	
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
			
		theAssetManager = mainThread.theAssetManager;
		
		//test if the gun turret is visible in camera point of view
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
			int xPos = boundary2D.x1/16 - 6 + 10;
			int yPos = 127 - boundary2D.y1/16 - 6 + 10;
			
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
		

		visionBoundary.x = (int)(tempCentre.screenX - 800);
		visionBoundary.y = (int)(tempCentre.screenY - 1000);
		visionInsideScreen = camera.screen.intersects(visionBoundary);
		
		
		if(visionInsideScreen){
			if(teamNo != 0){
				if(exposedCountDown > 0){
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
		
		
		if(theAssetManager.minimapBitmap[tileIndex[0]]){
			isRevealed = true;
		}
		visible_minimap = isRevealed;
		
		
		if(teamNo == 0 || attackStatus == isAttacking || exposedCountDown > 0 || visible_minimap){
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1/16;
			tempInt[2] = 127 - boundary2D.y1/16;
			tempInt[3] = 2;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else{
				if(exposedCountDown > 0)
					tempInt[4] = exposedCountDown;
				else
					tempInt[4] = 10000;
			}
			theAssetManager.unitsForMiniMapCount++;
			
		}
		
	
		
		
		accumulatedDelta+=turretAngleDelta;
		accumulatedDelta= accumulatedDelta%360;
		if(visible){
			//update turret polygons
			for(int i = 5; i < 46; i++){

				polygons[i].origin.subtract(centre);
				polygons[i].origin.rotate_XZ(accumulatedDelta);
				polygons[i].origin.add(centre);
				
				
				polygons[i].bottomEnd.subtract(centre);
				polygons[i].bottomEnd.rotate_XZ(accumulatedDelta);
				polygons[i].bottomEnd.add(centre);
				
				
				polygons[i].rightEnd.subtract(centre);
				polygons[i].rightEnd.rotate_XZ(accumulatedDelta);
				polygons[i].rightEnd.add(centre);
				
				
				for(int j = 0; j < polygons[i].vertex3D.length; j++){
				
					polygons[i].vertex3D[j].subtract(centre);
					polygons[i].vertex3D[j].rotate_XZ(accumulatedDelta);
					polygons[i].vertex3D[j].add(centre);
				}
				
				
				polygons[i].normal.rotate_XZ(accumulatedDelta);
				polygons[i].findDiffuse();
			}
			accumulatedDelta = 0;
			
		}
		
	}
	
	//process turret AI
	public void carryOutCommands(){
		if(targetObject != null){
			destinationX = targetObject.getRealCentre().x;
			destinationY = targetObject.getRealCentre().z;
			
			distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
			
			//check if light tank has the line of sight to its target
			boolean hasLineOfSightToTarget = true;
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
				
			}else{
				hasLineOfSightToTarget = false;
			}
			
			
			if(targetObject.currentHP <=0 || (targetObject.isCloaked && teamNo != targetObject.teamNo)){
				targetObject = null;
				turretAngleDelta = 0;
				return;
			}

			if(hasLineOfSightToTarget){
				attackAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
				
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
				turretAngleDelta = 0;
				targetObject = null;
			}
		}else{
			//if there is no target, perform standby logic
			//scan for hostile unit
			
			if((randomInt + mainThread.frameIndex)%240 == 0){
				attackAngle = (int)(Math.random()*360);
			}
			if(turretAngle != attackAngle){
				
				turretAngleDelta = 360 - (geometry.findAngleDelta(turretAngle, attackAngle, 2) + 360)%360;
				turretAngle= (turretAngle - turretAngleDelta + 360)%360;
				
			}else{
			
				turretAngleDelta = 0;
			}
			
			
			if((ID + mainThread.frameIndex)%4 == 0){
				currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
				
				for(int i = 0; i < tileCheckList.length; i++){
					if(tileCheckList[i] != Integer.MAX_VALUE){
						int index = currentOccupiedTile + tileCheckList[i];
						if(index < 0 || index >= 16384)
							continue;
						tile = mainThread.gridMap.tiles[index];
						for(int j = 0; j < 4; j++){
							if(tile[j] != null){
								if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
									targetObject = tile[j];
									return;
								}
							}
						}
					}
				}
			}
			
			
			
		}
	}
		
		
	//draw the model
	public void draw(){
		
		if(!visible)
			return;
		for(int i = 0; i < polygons.length; i++){
			polygons[i].update();
			polygons[i].draw();
		}
	}
	
	public void attack(solidObject o){
		if(targetObject != o){
			
			distanceToDesination = (float)Math.sqrt((o.centre.x - centre.x) * (o.centre.x - centre.x) + (o.centre.z - centre.z) * (o.centre.z - centre.z));
			
			//check if target is within range
			if(distanceToDesination <=  attackRange){
				
				
				//check if there is any obstacles between target and the turret
				//check if light tank has the line of sight to its target
				boolean hasLineOfSightToTarget = true;
				
				int numberOfIterations = (int)(distanceToDesination * 8);
				float dx = (o.centre.x - centre.x)/numberOfIterations;
				float dy = (o.centre.z - centre.z)/numberOfIterations;
				float xStart = centre.x;
				float yStart = centre.z;
				
				for(int i = 0; i < numberOfIterations; i++){
					xStart+=dx;
					yStart+=dy;
					solidObject s = mainThread.gridMap.tiles[(int)(xStart*4) + (127 - (int)(yStart*4))*128][0];
					if(s != null){
						if(s.type > 100 && s .type < 200 && s != o){
							hasLineOfSightToTarget = false;
							break;
						}
					}
				}
				
				if(hasLineOfSightToTarget){
					targetObject = o;
				}
					
			}
			
		}
	}
	
	public void fireBullet(int attackAngle){
		if(targetObject != null && targetObject.teamNo != teamNo){
			exposedCountDown = 64;
			isRevealed = true;
		}
		
		int theDamage = myDamage;
		if(targetObject.type == 0)
			theDamage = 20;
		
		if(attackCoolDown == 0 ){
			//if there is nothing between the tank and its target
			firingPosition.set(0, -0.4f, 0.18f);
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
	}
	
	public void hold(){
		targetObject = null;
		turretAngleDelta = 0;
	}
	
	public vector getMovement(){
		return movenment;
	}
}
