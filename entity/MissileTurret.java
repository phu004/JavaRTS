package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;


//the power plant model
public class missileTurret extends solidObject{
	
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
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,screen_width+152, screen_height+250);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,screen_width-120, screen_height-110);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary = new Rectangle(0,0,screen_width, screen_height);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
		public final static Rectangle visionBoundary = new Rectangle(0,0,1000, 1500);
	
	//a bitmap representation of the vision of the power plant for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	public static boolean[] bitmapVisionGainFromAttackingUnit;
	
	//missile never moves
	public final static vector movenment = new vector(0,0,0);
	
	//the oreintation of the turret
	public int turretAngle;
	
	//attack range
	public final static float attackRange = 2.4f;
		
	//the angle that the turret have rotated between current frame and previous frame
	public int turretAngleDelta, accumulatedDelta;
	
	public int turretTurnRate = 8;
	public int myAttackCooldown= 23;
	public int attackCoolDown;
	public vector firingPosition;
	
	
	//index of the tiles to check when the turret is in standby mode
	public static int[] tileCheckList;
	
	//once the  turret starts attacking, it exposed itself to the enemy
	public int exposedCountDown;
	
	public baseInfo theBaseInfo;
	
	public boolean overCharge;
	
	public int noOverChargeRed = 4;   
	public int noOverChargeGreen = 21;
	public int noOverChargeBlue = 31;
	
	public int noOverChargeRedBase = 6;
	public int noOverChargeGreenBase = 12;
	public int noOverChargeBlueBase = 16;
	
	public int OverChargeRed = 30;   
	public int OverChargeGreen = 19;
	public int OverChargeBlue = 4;
	
	public int OverChargeRedBase = 16;
	public int OverChargeGreenBase = 12;
	public int OverChargeBlueBase = 6;
	
	public int attackAngle;
	
	public int randomInt;
	
	public boolean attackLock;
	
	public missileTurret(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 199;
		
		ID = globalUniqID++;
		randomInt = gameData.getRandom();
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfMissileTurret++;
		
		
		currentHP = maxHP;
		myDamage = 30;
		
		this.teamNo = teamNo;
		
		currentCommand = StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(10f);
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
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
		
		
		
		makePolygons();
		
	}
	
	//create polygons
	public void makePolygons(){
		polygons = new polygon3D[94];	
		vector[] v;
		
		//turret base
		
		float l =0.07f;
		float h = 0.07f;
		
		iDirection.scale(0.65f);
		kDirection.scale(0.65f);
		
		h = 0.3f;
		
		vector a1 = put(-0.06, h, 0.08);
		vector a2 =  put(0.06, h, 0.08);
		vector a3 = put(0.08, h, 0.06);
		vector a4 = put(0.08, h, -0.06);
		vector a5 =  put(0.06, h, -0.08);
		vector a6 = put(-0.06, h, -0.08);
		vector a7 = put(-0.08, h, -0.06);
		vector a8  = put(-0.08, h, 0.06);
		
		int textureIndex = 66;
		if(teamNo != 0)
			textureIndex = 67;
		
		v = new vector[]{a1, a2, a3, a4,a5, a6, a7, a8};
		polygons[0] =  new polygon3D(v, v[0].myClone(), v[1].myClone(), v[5].myClone(), mainThread.textures[12], 0.7f, 1f, 1);
		
		iDirection.scale(1.4f);
		kDirection.scale(1.4f);
		
		vector b1 = put(-0.06, 0, 0.08);
		vector b2 =  put(0.06, 0, 0.08);
		vector b3 = put(0.08, 0, 0.06);
		vector b4 = put(0.08, 0, -0.06);
		vector b5 =  put(0.06, 0, -0.08);
		vector b6 = put(-0.06, 0, -0.08);
		vector b7 = put(-0.08, 0, -0.06);
		vector b8  = put(-0.08, 0, 0.06);
		
		
		v = new vector[]{a2.myClone(), a1.myClone(), b1.myClone(), b2.myClone()};
		polygons[1] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		polygons[1].shadowBias = 20000;
		
		
		v = new vector[]{a1.myClone(), a8.myClone(), b8.myClone(), b1.myClone()};
		polygons[2] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[2].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a3.myClone(), a2.myClone(), b2.myClone(), b3.myClone()}; 
		polygons[3] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a4.myClone(), a3.myClone(), b3.myClone(), b4.myClone()};
		polygons[4] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a5.myClone(), a4.myClone(), b4.myClone(), b5.myClone()};
		polygons[5] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a6.myClone(), a5.myClone(), b5.myClone(), b6.myClone()};
		polygons[6] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a7.myClone(), a6.myClone(), b6.myClone(), b7.myClone()};
		polygons[7] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		v = new vector[]{a8.myClone(), a7.myClone(), b7.myClone(), b8.myClone()};
		polygons[8] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[12], 0.5f, 1f, 1);
		
		float r = 0.052f;
		float r2 = 0.046f;
	
		
		double theta = Math.PI/16;
	
		for(int i = 0; i < 32; i++){
			v = new vector[]{
					put(r2*Math.cos(i*theta), 0.3101, r2*Math.sin(i*theta)),
					put(r2*Math.cos((i+1)*theta), 0.3101, r2*Math.sin((i+1)*theta)),
					put(r*Math.cos((i+1)*theta),  0.3101, r*Math.sin((i+1)*theta)),
					put(r*Math.cos(i*theta),  0.3101, r*Math.sin(i*theta))
					 	
					 	
					 	
							};
			polygons[9 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[26], 10,10,0);
			polygons[9 +i].color = 8 << 10 |  16 << 5  | 21;
		}
		
		for(int i = 0; i < 32; i++){
			v = new vector[]{put(r*Math.cos(i*theta),  0.31, r*Math.sin(i*theta)),
					 	put(r*Math.cos((i+1)*theta),  0.31, r*Math.sin((i+1)*theta)),
					 	put(r*Math.cos((i+1)*theta), 0.3, r*Math.sin((i+1)*theta)),
					 	put(r*Math.cos(i*theta), 0.3, r*Math.sin(i*theta))
							};
			polygons[41 +i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[26], 10,10,0);
			polygons[41 +i].color = 8 << 10 |  16 << 5  | 21;
		}
		
		
		
		
		v = new vector[32];
		for(int i = 0; i < 32; i++){
			v[31-i] = put(r*Math.cos(i*theta),  0.31, r*Math.sin(i*theta));
		}
		polygons[73] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[26], 10,10,1);
		polygons[73].shadowBias = 5000;
		
		
		//turret tower
		iDirection.rotate_XZ(360-turretAngle);
		jDirection.rotate_XZ(360-turretAngle);
		kDirection.rotate_XZ(360-turretAngle);
		
		
		v = new vector[]{put(-0.02f, 0.4f, 0.0f), put(-0.02f, 0.4f, -0.04f), put(-0.02f, 0.31f, -0.02f), put(-0.02f, 0.31f, 0.02f)};
		polygons[74] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		
		v = new vector[]{put(0.02f, 0.31f, 0.02f), put(0.02f, 0.31f, -0.02f), put(0.02f, 0.4f, -0.04f), put(0.02f, 0.4f, 0.0f)};
		polygons[75] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(0.02f, 0.4f, 0.0f), put(-0.02f, 0.4f, 0.0f), put(-0.02f, 0.31f, 0.02f), put(0.02f, 0.31f, 0.02f)};
		polygons[76] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(0.02f, 0.31f, -0.02f), put(-0.02f, 0.31f, -0.02f), put(-0.02f, 0.4f, -0.04f), put(0.02f, 0.4f, -0.04f)};
		polygons[77] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.02f, 0.4f, 0.0f), put(0.02f, 0.4f, 0.0f), put(0.02f, 0.4f, -0.04f), put(-0.02f, 0.4f, -0.04f)};
		polygons[78] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		
		v = new vector[]{put(-0.07f, 0.41f, 0.09f), put(-0.04f, 0.41f, 0.09f), put(-0.04f, 0.41f, -0.07f), put(-0.07f, 0.41f, -0.07f)};
		polygons[79] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.07f, 0.41f, 0.09f), put(-0.07f, 0.41f, -0.07f), put(-0.075f, 0.405f, -0.07f), put(-0.075f, 0.405f, 0.09f)};
		polygons[80] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{ put(-0.035f, 0.405f, 0.09f), put(-0.035f, 0.405f, -0.07f), put(-0.04f, 0.41f, -0.07f),put(-0.04f, 0.41f, 0.09f)};
		polygons[81] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.075f, 0.405f, 0.09f), put(-0.075f, 0.405f, -0.07f), put(-0.075f, 0.37f, -0.07f), put(-0.075f, 0.37f, 0.09f)};
		polygons[82] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.035f, 0.37f, 0.09f), put(-0.035f, 0.37f, -0.07f), put(-0.035f, 0.405f, -0.07f), put(-0.035f, 0.405f, 0.09f)};
		polygons[83] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.04f, 0.41f, 0.09f), put(-0.07f, 0.41f, 0.09f), put(-0.075f, 0.405f, 0.09f), put(-0.075f, 0.37f, 0.09f), put(-0.07f, 0.365f, 0.09f), put(-0.04f, 0.365f, 0.09f), put(-0.035f, 0.37f, 0.09f), put(-0.035f, 0.405f, 0.09f) };
		polygons[84] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.042f, 0.401f, 0.091f), put(-0.067f, 0.401f, 0.091f),  put(-0.067f, 0.375f, 0.091f),  put(-0.042f, 0.375f, 0.091f) };
		polygons[85] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[68], 0.5f,0.5f,1);
		polygons[85].Ambient_I+=20;
		
		v = new vector[]{put(-0.035f, 0.405f, -0.07f),  put(-0.035f, 0.37f, -0.07f), put(-0.04f, 0.365f, -0.07f), put(-0.07f, 0.365f, -0.07f), put(-0.075f, 0.37f, -0.07f), put(-0.075f, 0.405f, -0.07f), put(-0.07f, 0.41f, -0.07f), put(-0.04f, 0.41f, -0.07f)};
		polygons[86] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[33], 0.5f,0.5f,1);
		
		
		
		jDirection.scale(1.5f);
		iDirection.scale(1.6f);
		kDirection.scale(0.3f);
		
		start.y-=0.19;
		start.x+=0.032;
		start.z-=0.02;
		
		
		v = new vector[]{put(-0.07f, 0.41f, 0.09f), put(-0.04f, 0.41f, 0.09f), put(-0.04f, 0.41f, -0.07f), put(-0.07f, 0.41f, -0.07f)};
		polygons[87] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.07f, 0.41f, 0.09f), put(-0.07f, 0.41f, -0.07f), put(-0.075f, 0.405f, -0.07f), put(-0.075f, 0.405f, 0.09f)};
		polygons[88] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{ put(-0.035f, 0.405f, 0.09f), put(-0.035f, 0.405f, -0.07f), put(-0.04f, 0.41f, -0.07f),put(-0.04f, 0.41f, 0.09f)};
		polygons[89] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.075f, 0.405f, 0.09f), put(-0.075f, 0.405f, -0.07f), put(-0.075f, 0.37f, -0.07f), put(-0.075f, 0.37f, 0.09f)};
		polygons[90] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.035f, 0.37f, 0.09f), put(-0.035f, 0.37f, -0.07f), put(-0.035f, 0.405f, -0.07f), put(-0.035f, 0.405f, 0.09f)};
		polygons[91] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.04f, 0.41f, 0.09f), put(-0.07f, 0.41f, 0.09f), put(-0.075f, 0.405f, 0.09f), put(-0.075f, 0.37f, 0.09f), put(-0.07f, 0.365f, 0.09f), put(-0.04f, 0.365f, 0.09f), put(-0.035f, 0.37f, 0.09f), put(-0.035f, 0.405f, 0.09f) };
		polygons[92] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		v = new vector[]{put(-0.035f, 0.405f, -0.07f),  put(-0.035f, 0.37f, -0.07f), put(-0.04f, 0.365f, -0.07f), put(-0.07f, 0.365f, -0.07f), put(-0.075f, 0.37f, -0.07f), put(-0.075f, 0.405f, -0.07f), put(-0.07f, 0.41f, -0.07f), put(-0.04f, 0.41f, -0.07f)};
		polygons[93] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[textureIndex], 0.5f,0.5f,1);
		
		
		jDirection.scale(1f/1.5f);
		iDirection.scale(1f/1.6f);
		kDirection.scale(1f/0.3f);
		
		start.y+=0.19;
		start.x-=0.032;
		start.z+=0.02;
		
		
		
		
		
	}
		
		
	
	//update the model 
	public void update(){	
		theAssetManager = mainThread.theAssetManager;
		
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
				
				
				theBaseInfo.numberOfMissileTurret--;
				
				if(overCharge)
					theBaseInfo.numberOfOverChargedMissileTurret--;
				
				//removeFromGridMap();
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=35;
				
				return;
			}else{
				
				if(mainThread.gameFrame%2==0){
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
			if(mainThread.gameFrame%5==0 && theBaseInfo.currentCredit > 0 && currentHP <maxHP){
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
		
		if(overCharge)
			myAttackCooldown = 9;
	
		//mark itself on obstacle map
		mainThread.gridMap.currentObstacleMap[tileIndex[0]] = false;
	
	
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.y+=0.4f;
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		screenX_gui = (int)tempCentre.screenX;
		screenY_gui = (int)tempCentre.screenY;
		
		tempCentre.set(centre);
		tempCentre.y+=0.1f;
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
			
		
		
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
			if(shadowBoundary.contains(tempshadowvertex0.screenX, tempshadowvertex0.screenY) ||
					shadowBoundary.contains(tempshadowvertex1.screenX, tempshadowvertex1.screenY) ||
					shadowBoundary.contains(tempshadowvertex2.screenX, tempshadowvertex2.screenY) ||
					shadowBoundary.contains(tempshadowvertex3.screenX, tempshadowvertex3.screenY) 
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
		

		visionBoundary.x = (int)(tempCentre.screenX - 500);
		visionBoundary.y = (int)(tempCentre.screenY - 1200);
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
				tempFloat[4] = 2;
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
			if(!overCharge){
				float ratio = ((float)Math.sin((float)(mainThread.gameFrame + ID)/10) + 1)/2;
				
				if(theBaseInfo.lowPower)
					ratio = 0;
				
				int color = (int)(noOverChargeRedBase + ratio * (noOverChargeRed - noOverChargeRedBase)) << 10 | (int)(noOverChargeGreenBase + ratio * (noOverChargeGreen - noOverChargeGreenBase)) << 5 | (int)(noOverChargeBlueBase + ratio * (noOverChargeBlue - noOverChargeBlueBase));
				
				for(int i = 9; i < 73; i++){
					polygons[i].color = color;
					polygons[i].diffuse_I = 100;
				}
			}else{
				float ratio = ((float)Math.sin((float)(mainThread.gameFrame + ID)/10) + 1)/2;
				
				if(theBaseInfo.lowPower)
					ratio = 0;
				
				int color = (int)(OverChargeRedBase + ratio * (OverChargeRed - OverChargeRedBase)) << 10 | (int)(OverChargeGreenBase + ratio * (OverChargeGreen - OverChargeGreenBase)) << 5 | (int)(OverChargeBlueBase + ratio * (OverChargeBlue - OverChargeBlueBase));
				
				for(int i = 9; i < 73; i++){
					polygons[i].color = color;
					polygons[i].diffuse_I = 100;
				}
				
				
			}
			
			//update turret polygons
			for(int i = 74; i < polygons.length; i++){

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
		if(theBaseInfo.lowPower){
			targetObject = null;
			turretAngleDelta = 0;
			return;
		}
			
		if(targetObject != null){
			
			//target enemy military unit first
			if((targetObject.type > 100 ||targetObject.type <199) && !attackLock && (randomInt + mainThread.gameFrame)%4   == 2){
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
									destinationX = tile[j].getRealCentre().x;
									destinationY = tile[j].getRealCentre().z;
									distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
									
									if(distanceToDesination <=  attackRange){
										if(tile[j].type < 100 || tile[j].type >= 199){
											targetObject = tile[j];
											break;
										}
									}
								}
							}
						}
						if(targetObject.type < 100 || targetObject.type >= 199)
							break;
					}
				}
			}
			
			destinationX = targetObject.getRealCentre().x;
			destinationY = targetObject.getRealCentre().z;
			
			distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
			
	
				
			if(targetObject.currentHP <=0 || (targetObject.isCloaked && teamNo != targetObject.teamNo) || distanceToDesination >  attackRange){
				targetObject = null;
				turretAngleDelta = 0;
				attackLock = false;
				return;
			}
		
			attackAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			
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
			//if there is no target, perform standby logic
			//scan for hostile unit
			
			boolean[] bitmapVision;
			if(teamNo == 0)
				bitmapVision = mainThread.theAssetManager.minimapBitmap;
			else
				bitmapVision = enemyCommander.visionMap;
			
			attackLock = false;
			
			if((randomInt + mainThread.gameFrame)%240 == 0){
				attackAngle = (int)(Math.random()*360);
			}
			if(turretAngle != attackAngle){
				
				turretAngleDelta = 360 - (geometry.findAngleDelta(turretAngle, attackAngle, 2) + 360)%360;
				turretAngle= (turretAngle - turretAngleDelta + 360)%360;
				
			}else{
			
				turretAngleDelta = 0;
			}
			
	
			if((ID + mainThread.gameFrame)%4 == 0){
				currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
				
				for(int i = 0; i < tileCheckList.length; i++){
					if(tileCheckList[i] != Integer.MAX_VALUE){
						int index = currentOccupiedTile + tileCheckList[i];
						if(index < 0 || index >= 16384)
							continue;
						tile = mainThread.gridMap.tiles[index];
						
						if(!bitmapVision[index]){
							boolean isRevealedBuilding = false;
							if(tile[4] != null)
								if(tile[4].type > 100)
									if(tile[4].isRevealed == true)
										isRevealedBuilding = true;
							if(!isRevealedBuilding)
								continue;
						}
						
						for(int j = 0; j < 4; j++){
							if(tile[j] != null){
								if(tile[j].teamNo !=  teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
									destinationX = tile[j].getRealCentre().x;
									destinationY = tile[j].getRealCentre().z;
									distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
									
									if(distanceToDesination <=  attackRange){
										targetObject = tile[j];
										if(targetObject.type < 100 || targetObject.type >= 199)
											return;
									}
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
				targetObject = o;
				attackLock = true;
			}
			
		}
	}
	
	public void fireRocket(int attackAngle){
		if(targetObject != null && targetObject.teamNo != teamNo){
			exposedCountDown = 64;
			isRevealed = true;
		}
		
		tempVector.set(centre);
		
		
		if(attackCoolDown == 0 ){
			firingPosition.set(-0.05f, -0.1f, 0.14f);
			firingPosition.rotate_XZ(360 - attackAngle);
			firingPosition.add(tempVector.x, 0, tempVector.z);
			theAssetManager.spawnRocket(attackAngle, myDamage, targetObject, firingPosition, this);
			attackCoolDown = myAttackCooldown;
			
			//spawn a mini explosion  
			firingPosition.set(-0.05f, -0.1f, 0.13f);
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
	
	public void hold(){
		targetObject = null;
		turretAngleDelta = 0;
	}
	
	public vector getMovement(){
		return movenment;
	}
}
