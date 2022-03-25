package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the refinery plant model
public class refinery extends solidObject{
	
	//the polygons of the model
	private polygon3D[] polygons; 
	
	public polygon3D storageCoverLeft;
	public polygon3D storageCoverRight;
	public int unloadOreCountDown;
	public final int unloadOreTime = 190;
	
	public polygon3D[][] cargos;
	
	public float cargoX_left;
	public float cargoY_left;
	
	public float cargoX_MaxRight;
	
	public static int maxHP = 750;
	
	public int countDownToDeath = 16;
	
	public boolean isBusy;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	
	//refinery occupies 6 tiles
	public int [] tileIndex = new int[6];
	
	public int[] tempInt;
	
	public float[] tempFloat;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,screen_width+152, screen_height+250);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,screen_width-120, screen_height-110);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(30,70,screen_width-30, screen_height-30);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//a bitmap representation of the vision of the refinery for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
	
	//refinery never moves
	public final static vector movenment = new vector(0,0,0);
	
	public baseInfo theBaseInfo;
	
	public static int intendedDeployLocation = -1;
	
	public goldMine nearestGoldMine;
	
	public refinery(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 102;
		
		currentHP = 750;
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		ID = globalUniqID++;
		
		theBaseInfo.numberOfRefinery++;
		
		this.teamNo = teamNo;
		
		currentCommand = this.StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
		}
		
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
		shadowvertex0.add(-0.45f,-0.2f, -0.4f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.45f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.4f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
	
		makePolygons();
				
	}
	
	//create polygons
	public void makePolygons(){
		polygons = new polygon3D[157 + 4 * 13];	
		
		int polyIndex;
		
		cargos = new polygon3D[5][13];
		float referenceX = -0.28f - 0.02f;
		float referenceY = 0.34f - 0.012f;
		
	
		
		for(int i = 0; i < cargos.length; i++){

			v = new vector[]{put(referenceX, referenceY,  0.1), put(referenceX+0.07, referenceY,  0.1), put(referenceX+0.06, referenceY-0.05,  0.1), put(referenceX+0.01, referenceY-0.05,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][0] = polygons[polyIndex];
			
			if(i == 0){
				cargoX_left = v[0].x;
				cargoY_left = v[0].y;
				
				cargoX_MaxRight = cargoX_left + 0.5f;
			}
			
			v = new vector[]{put(referenceX, referenceY,  0.175), put(referenceX+0.07, referenceY,  0.175), put(referenceX+0.06, referenceY-0.05,  0.175), put(referenceX+0.01, referenceY-0.05,  0.175)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][1] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.01, referenceY-0.05,  0.18), put(referenceX+0.06, referenceY-0.05,  0.18), put(referenceX+0.07, referenceY,  0.18), put(referenceX, referenceY,  0.18)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][2] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.01, referenceY-0.05,  0.105), put(referenceX+0.06, referenceY-0.05,  0.105), put(referenceX+0.07, referenceY,  0.105), put(referenceX, referenceY,  0.105)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][3] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.07, referenceY,  0.1), put(referenceX+0.07, referenceY,  0.18), put(referenceX+0.06, referenceY-0.05,  0.18), put(referenceX+0.06, referenceY -0.05,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][4] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.055, referenceY-0.05,  0.1), put(referenceX+0.055, referenceY-0.05,  0.18), put(referenceX+0.065, referenceY,  0.18), put(referenceX+0.065, referenceY,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][5] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX, referenceY,  0.18), put(referenceX, referenceY,  0.1), put(referenceX+0.01, referenceY-0.05,  0.1), put(referenceX+0.01, referenceY-0.05,  0.18)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][6] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.015, referenceY- 0.05,  0.18), put(referenceX+0.015, referenceY-0.05,  0.1), put(referenceX+0.005, referenceY,  0.1), put(referenceX+0.005, referenceY,  0.18)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,1f,1));
			cargos[i][7] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX, referenceY,  0.105), put(referenceX+0.07, referenceY,  0.105), put(referenceX+0.07, referenceY,  0.1), put(referenceX, referenceY,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,0.2f,1));
			cargos[i][8] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX, referenceY,  0.18), put(referenceX+0.07, referenceY,  0.18), put(referenceX+0.07, referenceY,  0.175), put(referenceX, referenceY,  0.175)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1,0.2f,1));
			cargos[i][9] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX, referenceY,  0.18), put(referenceX+0.005, referenceY,  0.18), put(referenceX+0.005, referenceY,  0.1), put(referenceX, referenceY,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.2f,1f,1));
			cargos[i][10] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.065, referenceY,  0.18), put(referenceX+0.07, referenceY,  0.18), put(referenceX+0.07, referenceY,  0.1), put(referenceX+0.065, referenceY,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.2f,1f,1));
			cargos[i][11] = polygons[polyIndex];
			
			v = new vector[]{put(referenceX+0.01, referenceY-0.01,  0.18), put(referenceX + 0.07, referenceY-0.01,  0.18), put(referenceX + 0.07, referenceY-0.01,  0.1), put(referenceX+0.01, referenceY-0.01,  0.1)};
			polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[34], 0.8f,1f,1));
			cargos[i][12] = polygons[polyIndex];
			
			referenceX +=0.1f;
			referenceY +=0.06f;
		}
		
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
		
		v = new vector[]{put(-0.345, 0.3, 0.225), put(-0.14, 0.3, 0.225), put(-0.14, 0.3, -0.03), put(-0.345, 0.3, -0.03) };
		polyIndex = addPolygon(polygons, new polygon3D(v, put(-0.38, 0.3, 0.26), put(0.38, 0.3, 0.26), put(-0.38, 0.3, -0.26), mainThread.textures[30], 1,1,1));
		polygons[polyIndex].shadowBias = 5000;
		
		
		v = new vector[]{put(-0.345, 0.3, -0.26), put(0.345, 0.3, -0.26), put(0.345, 0.28, -0.26), put(-0.345, 0.28, -0.26)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[30], 1,1f,1));
		
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
		
		texture stripeTExture = mainThread.textures[31];
		if(teamNo == 1)
			stripeTExture = mainThread.textures[32];
		
		
		float h = 0.315f;
		float h2 = 0.29f;
		v = new vector[]{put(-0.34, h, -0.03), put(-0.13f, h, -0.03), put(-0.13, h, -0.05), put(-0.34, h, -0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		v = new vector[]{put(-0.34, h, -0.2), put(-0.13f, h, -0.2), put(-0.13, h, -0.22), put(-0.34, h, -0.22)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		v = new vector[]{put(-0.13, h, -0.05), put(-0.13, h, -0.2), put(-0.15, h, -0.2), put(-0.15, h, -0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 3,0.5f,1));
		
		v = new vector[]{put(-0.32, h, -0.05), put(-0.32, h, -0.2), put(-0.34, h, -0.2), put(-0.34, h, -0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 3,0.5f,1));
		
		v = new vector[]{put(-0.34, h, -0.03), put(-0.34, h, -0.22), put(-0.34, h2, -0.22), put(-0.34, h2, -0.03)};
		addPolygon(polygons, new polygon3D(v, put(-0.34, h, -0.05), put(-0.34, h, -0.2), put(-0.34, h2, -0.05), stripeTExture, 3f,0.5f,1));
		
		v = new vector[]{put(-0.32, h2, -0.03), put(-0.32, h2, -0.22), put(-0.32, h, -0.22), put(-0.32, h, -0.03)};
		addPolygon(polygons, new polygon3D(v, put(-0.32, h, -0.2), put(-0.32, h, -0.05), put(-0.32, h2, -0.25), stripeTExture, 3f,0.5f,1));
		
		v = new vector[]{put(-0.15, h, -0.03), put(-0.15, h, -0.22), put(-0.15, h2, -0.22), put(-0.15, h2, -0.03)};
		addPolygon(polygons, new polygon3D(v, put(-0.15, h, -0.05), put(-0.15, h, -0.2), put(-0.15, h2, -0.05), stripeTExture, 3f,0.5f,1));
		
		v = new vector[]{put(-0.13, h2, -0.03), put(-0.13, h2, -0.22), put(-0.13, h, -0.22), put(-0.13, h, -0.03)};
		addPolygon(polygons, new polygon3D(v, put(-0.13, h, -0.2), put(-0.13, h, -0.05), put(-0.13, h2, -0.2), stripeTExture, 3f,0.5f,1));
		
		v = new vector[]{put(-0.34, h, -0.05), put(-0.13, h, -0.05), put(-0.13, h2, -0.05), put(-0.34, h2, -0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		v = new vector[]{put(-0.34, h, -0.22), put(-0.13, h, -0.22), put(-0.13, h2, -0.22), put(-0.34, h2, -0.22)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		v = new vector[]{put(-0.34, h2, -0.03), put(-0.13, h2, -0.03), put(-0.13, h, -0.03), put(-0.34, h, -0.03)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		v = new vector[]{put(-0.34, h2, -0.2), put(-0.13, h2, -0.2), put(-0.13, h, -0.2), put(-0.34, h, -0.2)};
		polyIndex = addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), stripeTExture, 4,0.5f,1));
		
		
		v = new vector[]{put(-0.32, 0.3, -0.05), put(-0.235, 0.3, -0.05), put(-0.235, 0.3, -0.2),  put(-0.32, 0.3, -0.2)};
		storageCoverLeft = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[33], 4,4,1);
		storageCoverLeft.shadowBias = 5000;
		addPolygon(polygons, storageCoverLeft);
		
		
		v = new vector[]{put(-0.235, 0.3, -0.05), put(-0.15, 0.3, -0.05), put(-0.15, 0.3, -0.2),  put(-0.235, 0.3, -0.2)};
		storageCoverRight = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[33], 4,4f,1);
		storageCoverRight.shadowBias = 5000;
		addPolygon(polygons, storageCoverRight);
		
		v = new vector[]{put(-0.34, 0.27, -0.01), put(-0.13, 0.27, -0.01), put(-0.13, 0.27, -0.24),  put(-0.34, 0.27, -0.24)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[34], 1,1f,3));
		
		v = new vector[]{put(-0.34,0.3,0.08), put(-0.34,0.3,0.2), put(-0.2,0.4,0.2),  put(-0.2,0.4,0.08)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.34,0.3,0.08), put(-0.2,0.4,0.08), put(-0.2,0.3,0.08)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.08), put(-0.2,0.4,0.08), put(-0.34,0.3,0.08), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.2,0.3,0.2), put(-0.2,0.4,0.2), put(-0.34,0.3,0.2)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.2), put(-0.2,0.4,0.2), put(-0.34,0.3,0.2), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.34,0.3,0.19), put(-0.2,0.4,0.19), put(-0.2,0.3,0.19)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.19), put(-0.2,0.4,0.19), put(-0.34,0.3,0.19), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.2,0.3,0.09), put(-0.2,0.4,0.09), put(-0.34,0.3,0.09)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.09), put(-0.2,0.4,0.09), put(-0.34,0.3,0.09), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.2,0.4,0.08), put(-0.2,0.4,0.09), put(-0.2,0.3,0.09), put(-0.2,0.3,0.08)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,0.5f,1));
		
		v = new vector[]{put(-0.2,0.4,0.19), put(-0.2,0.4,0.2), put(-0.2,0.3,0.2), put(-0.2,0.3,0.19)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,0.5f,1));
		
		v = new vector[]{put(-0.2,0.4,0.08), put(-0.2,0.4,0.2), put(-0.2,0.39,0.2), put(-0.2,0.39,0.08)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.5f,0.1f,1));
		
		v = new vector[]{put(-0.22, 0.35, 0.09), put(0.23, 0.6, 0.09), put(0.23, 0.58, 0.09), put(-0.22, 0.33, 0.09)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.09), put(-0.2,0.4,0.09), put(-0.34,0.3,0.09), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.22, 0.33, 0.1), put(0.23, 0.58, 0.1), put(0.23, 0.6, 0.1), put(-0.22, 0.35, 0.1)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.1), put(-0.2,0.4,0.1), put(-0.34,0.3,0.1), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.22, 0.35, 0.09), put(-0.22, 0.35, 0.1), put(0.23, 0.6, 0.1), put(0.23, 0.6, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.22, 0.35, 0.18), put(0.23, 0.6, 0.18), put(0.23, 0.58, 0.18), put(-0.22, 0.33, 0.18)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.18), put(-0.2,0.4,0.18), put(-0.34,0.3,0.18), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.22, 0.33, 0.19), put(0.23, 0.58, 0.19), put(0.23, 0.6, 0.19), put(-0.22, 0.35, 0.19)};
		addPolygon(polygons, new polygon3D(v, put(-0.34,0.4,0.19), put(-0.2,0.4,0.19), put(-0.34,0.3,0.19), mainThread.textures[35], 0.5f,0.3f,1));
		
		v = new vector[]{put(-0.22, 0.35, 0.18), put(-0.22, 0.35, 0.19), put(0.23, 0.6, 0.19), put(0.23, 0.6, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(0.16, 0.65, 0.05), put(0.35, 0.75, 0.05), put(0.35, 0.3, 0.05), put(0.16, 0.3, 0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.35, 0.65, 0.05), v[3].myClone(), mainThread.textures[36], 1f,1f,1));
		
		v = new vector[]{put(0.16, 0.3, 0.23), put(0.35, 0.3, 0.23), put(0.35, 0.75, 0.23), put(0.16, 0.65, 0.23)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.35, 0.65, 0.23), v[3].myClone(), mainThread.textures[36], 1f,2f,1));
		
		v = new vector[]{put(0.35, 0.75, 0.05), put(0.35, 0.75, 0.23),  put(0.35, 0.3, 0.23), put(0.35, 0.3, 0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.35, 0.65, 0.23), v[3].myClone(), mainThread.textures[36], 1f,2f,1));
		
		v = new vector[]{put(0.16, 0.5, 0.23), put(0.16, 0.5, 0.05), put(0.16, 0.3, 0.05), put(0.16, 0.3, 0.23)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 1f,1f,1));
		
		v = new vector[]{put(0.16, 0.5, 0.05), put(0.16, 0.5, 0.23), put(0.34, 0.6, 0.23), put(0.34, 0.6, 0.05)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,1f,1));
		
		v = new vector[]{put(0.16, 0.65, 0.23), put(0.16, 0.65, 0.05), put(0.16, 0.6, 0.05), put(0.16, 0.6, 0.23)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 1f,0.5f,1));
		
		v = new vector[]{put(0.16, 0.6, 0.23), put(0.16, 0.6, 0.19), put(0.16, 0.5, 0.19), put(0.16, 0.5, 0.23)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,0.5f,1));
		
		v = new vector[]{put(0.16, 0.6, 0.09), put(0.16, 0.6, 0.05), put(0.16, 0.5, 0.05), put(0.16, 0.5, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,0.5f,1));
		
		v = new vector[]{put(0.16, 0.65, 0.19), put(0.35, 0.75, 0.19), put(0.35, 0.3, 0.19), put(0.16, 0.3, 0.19)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.35, 0.65, 0.19), v[3].myClone(), mainThread.textures[36], 1f,1f,1));
		
		v = new vector[]{put(0.16, 0.3, 0.09), put(0.35, 0.3, 0.09), put(0.35, 0.75, 0.09), put(0.16, 0.65, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.35, 0.65, 0.09), v[3].myClone(), mainThread.textures[36], 1f,2f,1));
		
		v = new vector[]{put(0.15, 0.66, 0.24), put(0.36, 0.76, 0.24), put(0.36, 0.76, 0.04), put(0.15, 0.66, 0.04)};
		addPolygon(polygons, new polygon3D(v, v[1].myClone(), v[2].myClone(), v[0].myClone(), mainThread.textures[37], 1f,2f,1));
		
		v = new vector[]{put(0.15, 0.66, 0.04), put(0.36, 0.76, 0.04), put(0.36, 0.75, 0.04), put(0.15, 0.65, 0.04)};
		addPolygon(polygons, new polygon3D(v, v[1].myClone(), v[2].myClone(), v[0].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.15, 0.65, 0.24), put(0.36, 0.75, 0.24), put(0.36, 0.76, 0.24), put(0.15, 0.66, 0.24)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.36, 0.76, 0.04), put(0.36, 0.76, 0.24), put(0.36, 0.75, 0.24), put(0.36, 0.75, 0.04)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.15, 0.66, 0.24), put(0.15, 0.66, 0.04), put(0.15, 0.65, 0.04), put(0.15, 0.65, 0.24)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.18, 0.58, 0.05), put(0.33, 0.58, 0.05), put(0.33, 0.5, -0.15), put(0.18, 0.5, -0.15)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 0.8f,1f,1));
		
		v = new vector[]{put(0.18, 0.5, -0.15), put(0.33, 0.5, -0.15), put(0.33, 0.49, -0.15), put(0.18, 0.49, -0.15)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.33, 0.5, -0.15), put(0.33, 0.58, 0.05), put(0.33, 0.57, 0.05), put(0.33, 0.49, -0.15)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.18, 0.49, -0.15), put(0.18, 0.57, 0.05), put(0.18, 0.58, 0.05), put(0.18, 0.5, -0.15)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,1f,1));
		
		v = new vector[]{put(0.19, 0.5, -0.14), put(0.32, 0.5, -0.14), put(0.32, 0.43, -0.14), put(0.19, 0.43, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[37], 1f,0.5f,1));
		
		v = new vector[]{put(0.32, 0.5, -0.14), put(0.32, 0.58, 0.05), put(0.32, 0.43, 0.05), put(0.32, 0.43, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.32, 0.5, 0.05), v[3].myClone(), mainThread.textures[36], 1f,0.5f,1));
		
		v = new vector[]{put(0.19, 0.43, -0.14), put(0.19, 0.43, 0.05), put(0.19, 0.58, 0.05), put(0.19, 0.5, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), put(0.19, 0.5, 0.05), v[3].myClone(), mainThread.textures[36], 1f,0.5f,1));
		
		
		v = new vector[]{put(0.3, 0.43, -0.14), put(0.32, 0.43, -0.14), put(0.32, 0.3, -0.14), put(0.3, 0.3, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.3, 0.3, -0.12), put(0.32, 0.3, -0.12), put(0.32, 0.43, -0.12) ,put(0.3, 0.43, -0.12)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.32, 0.43, -0.14), put(0.32, 0.43, -0.12), put(0.32, 0.3, -0.12), put(0.32, 0.3, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.3, 0.3, -0.14), put(0.3, 0.3, -0.12), put(0.3, 0.43, -0.12), put(0.3, 0.43, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		
		v = new vector[]{put(0.19, 0.43, -0.14), put(0.21, 0.43, -0.14), put(0.21, 0.3, -0.14), put(0.19, 0.3, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.19, 0.3, -0.12), put(0.21, 0.3, -0.12), put(0.21, 0.43, -0.12) ,put(0.19, 0.43, -0.12)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.21, 0.43, -0.14), put(0.21, 0.43, -0.12), put(0.21, 0.3, -0.12), put(0.21, 0.3, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
		v = new vector[]{put(0.19, 0.3, -0.14), put(0.19, 0.3, -0.12), put(0.19, 0.43, -0.12), put(0.19, 0.43, -0.14)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[36], 0.2f,1f,1));
		
	
		
		
		start.add(0.27f,0.5f,0.14f);
		double r = 0.028;
	
		double delta = Math.PI/8;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r*Math.cos(i*delta), 0.42, r*Math.sin(i*delta)),
							 put(r*Math.cos((i+1)*delta), 0.42, r*Math.sin((i+1)*delta)),
							 put(r*Math.cos((i+1)*delta), 0.2,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0.2, r*Math.sin(i*delta))
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			tempVector1.add(tempVector);
			
			int p = i % 2;
		    for(int j = 0; j < p; j++){
		    	tempVector0.subtract(tempVector);
		    	tempVector1.subtract(tempVector);
		    	tempVector3.subtract(tempVector);
		    }
			
		    change(0,0.42f,0, tempVector);
			polyIndex = addPolygon(polygons,  new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[40], 1f,2f,1));
			polygons[polyIndex].textureScaledWidth = (int)(polygons[polyIndex].myTexture.width*0.5);
			polygons[polyIndex].createShadeSpan(tempVector, v[0].myClone(), v[1]);
			
		}
		
		double r2 = 0.028;
		r = 0.02;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					 put(r*Math.cos(i*delta), 0.42, r*Math.sin(i*delta)),
					 put(r*Math.cos((i+1)*delta), 0.42,  r*Math.sin((i+1)*delta)),		
					 put(r2*Math.cos((i+1)*delta), 0.42, r2*Math.sin((i+1)*delta)),
					 put(r2*Math.cos(i*delta), 0.42, r2*Math.sin(i*delta))
					};
			
			
			
		    addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(),  mainThread.textures[40], 1f,1f,1));
		}
		
		r = 0.02;
		r2 = 0.02;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					 put(r*Math.cos(i*delta), 0.2, r*Math.sin(i*delta)),
					 put(r*Math.cos((i+1)*delta), 0.2,  r*Math.sin((i+1)*delta)),
					 put(r2*Math.cos((i+1)*delta), 0.42, r2*Math.sin((i+1)*delta)),
					 put(r2*Math.cos(i*delta), 0.42, r2*Math.sin(i*delta))
							
			
							};
			
			tempVector.set(v[1]);
	    	tempVector.subtract(v[0]);	
			tempVector0.set(v[0]);
			tempVector1.set(v[1]);
			tempVector3.set(v[3]);
			tempVector1.add(tempVector);
			
			int p = i % 2;
		    for(int j = 0; j < p; j++){
		    	tempVector0.subtract(tempVector);
		    	tempVector1.subtract(tempVector);
		    	tempVector3.subtract(tempVector);
		    }
			
		    addPolygon(polygons, new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[40], 0.5f,1,1));
		}
		
		start.add(-0.27f,-0.5f,-0.14f);
		
		
		v = new vector[]{put(-0.095, 0.42, 0.09), put(-0.08, 0.42, 0.09), put(-0.08, 0.3, 0.09), put(-0.095, 0.3, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.1), put(-0.08, 0.3, 0.1), put(-0.08, 0.42, 0.1), put(-0.095, 0.42, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.08, 0.42, 0.09), put(-0.08, 0.42, 0.1), put(-0.08, 0.3, 0.1), put(-0.08, 0.3, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.09), put(-0.095, 0.3, 0.1), put(-0.095, 0.42, 0.1), put(-0.095, 0.42, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.42, 0.18), put(-0.08, 0.42, 0.18), put(-0.08, 0.3, 0.18), put(-0.095, 0.3, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.19), put(-0.08, 0.3, 0.19), put(-0.08, 0.42, 0.19), put(-0.095, 0.42, 0.19)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.08, 0.42, 0.18), put(-0.08, 0.42, 0.19), put(-0.08, 0.3, 0.19), put(-0.08, 0.3, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.18), put(-0.095, 0.3, 0.19), put(-0.095, 0.42, 0.19), put(-0.095, 0.42, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		start.add(0,-0.01f,0);
		v = new vector[]{put(-0.08, 0.365, 0.1), put(-0.08, 0.365, 0.18), put(-0.08, 0.35, 0.18), put(-0.08, 0.35, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.095, 0.35, 0.1), put(-0.095, 0.35, 0.18), put(-0.095, 0.365, 0.18), put(-0.095, 0.365, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.095, 0.365, 0.1), put(-0.095, 0.365, 0.18), put(-0.08, 0.365, 0.18), put(-0.08, 0.365, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.08, 0.35, 0.1), put(-0.08, 0.35, 0.18), put(-0.095, 0.35, 0.18), put(-0.095, 0.35, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		start.add(0,0.01f,0);
		
		start.add(0.13f,0,0);
		v = new vector[]{put(-0.095, 0.48, 0.09), put(-0.08, 0.48, 0.09), put(-0.08, 0.3, 0.09), put(-0.095, 0.3, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.1), put(-0.08, 0.3, 0.1), put(-0.08, 0.48, 0.1), put(-0.095, 0.48, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.08, 0.48, 0.09), put(-0.08, 0.48, 0.1), put(-0.08, 0.3, 0.1), put(-0.08, 0.3, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.09), put(-0.095, 0.3, 0.1), put(-0.095, 0.48, 0.1), put(-0.095, 0.48, 0.09)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.48, 0.18), put(-0.08, 0.48, 0.18), put(-0.08, 0.3, 0.18), put(-0.095, 0.3, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.19), put(-0.08, 0.3, 0.19), put(-0.08, 0.48, 0.19), put(-0.095, 0.48, 0.19)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.08, 0.48, 0.18), put(-0.08, 0.48, 0.19), put(-0.08, 0.3, 0.19), put(-0.08, 0.3, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		v = new vector[]{put(-0.095, 0.3, 0.18), put(-0.095, 0.3, 0.19), put(-0.095, 0.48, 0.19), put(-0.095, 0.48, 0.18)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 0.1f,1f,1));
		
		start.add(0, 0.05f,0);
		
		v = new vector[]{put(-0.08, 0.365, 0.1), put(-0.08, 0.365, 0.18), put(-0.08, 0.35, 0.18), put(-0.08, 0.35, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.095, 0.35, 0.1), put(-0.095, 0.35, 0.18), put(-0.095, 0.365, 0.18), put(-0.095, 0.365, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.095, 0.365, 0.1), put(-0.095, 0.365, 0.18), put(-0.08, 0.365, 0.18), put(-0.08, 0.365, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		v = new vector[]{put(-0.08, 0.35, 0.1), put(-0.08, 0.35, 0.18), put(-0.095, 0.35, 0.18), put(-0.095, 0.35, 0.1)};
		addPolygon(polygons, new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[35], 1f,0.1f,1));
		
		start.add(-0.13f,-0.05f,0);
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
			
		}
		
		theAssetManager = mainThread.theAssetManager;
		double distance = 10;
		for(int i = 0; i < mainThread.theAssetManager.goldMines.length; i++){
			if(mainThread.theAssetManager.goldMines[i] == null)
				continue;
			
			double newDistance = getDistance(mainThread.theAssetManager.goldMines[i]);
			if(newDistance < distance  && mainThread.theAssetManager.goldMines[i].goldDeposite > 1){
				distance = newDistance;
				nearestGoldMine = theAssetManager.goldMines[i];
			}
		}
		
	}
	
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
		//process emerging from  ground animation
		if(centre.y < -0.79f){
			centre.y+=0.02f;
			
			float delta_h = 0.02f;
			if(centre.y > -0.79f){
				delta_h = 0.0000007f;
				centre.y = -0.79f;
			}
			
			cargoY_left+=delta_h;
			
			for(int i = 0; i < polygons.length; i++){		
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
		
		//check if power plant has been destroyed
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
				
				if(teamNo == 0)
					mainThread.pc.theBaseInfo.numberOfRefinery--;
				else
					mainThread.ec.theBaseInfo.numberOfRefinery--;
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=35;
				
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
			
		
			//spawn smoke particle
			if((mainThread.gameFrame + ID) % 5 ==0 && centre.y >= -0.79f){
				float[] tempFloat = theAssetManager.smokeEmmiterList[theAssetManager.smokeEmmiterCount];
				tempFloat[0] = centre.x + 0.265f + (float)(Math.random()/40) - 0.0125f;
				tempFloat[1] = centre.y + 0.4f + 0.5f;
				tempFloat[2] = centre.z + 0.14f + (float)(Math.random()/40) - 0.0125f;
				tempFloat[3] = 0.9f;
				tempFloat[4] = 1;
				tempFloat[5] = 11;
				tempFloat[6] = this.height;
				theAssetManager.smokeEmmiterCount++;
			}
			
		}
		
		
		
		//handle cargo unloading 
		if(unloadOreCountDown > 0){
			
			float doorSpeed = 0.0034f;
			//open cargo door
			if(unloadOreCountDown <= (unloadOreTime -1) && unloadOreCountDown > (unloadOreTime - 25)){
				storageCoverLeft.vertex3D[1].x -= doorSpeed;
				storageCoverLeft.vertex3D[2].x -= doorSpeed;
				storageCoverRight.vertex3D[0].x+=doorSpeed;
				storageCoverRight.vertex3D[3].x+=doorSpeed;
				
			}

			if(unloadOreCountDown <= 24){
				storageCoverLeft.vertex3D[1].x += doorSpeed;
				storageCoverLeft.vertex3D[2].x += doorSpeed;
				storageCoverRight.vertex3D[0].x-=doorSpeed;
				storageCoverRight.vertex3D[3].x-=doorSpeed;
				
			}

			unloadOreCountDown--;
		}
		
		//running cargo thread
		if(centre.y >= -0.79f){
			for(int i = 0; i <cargos.length; i++){
				
				float cargoX = 0;
				float cargoY = 0;
				
				for(int j = 0; j < cargos[i].length; j++){
					vector[] cargoVertex = cargos[i][j].vertex3D;
					for(int k = 0; k < cargoVertex.length; k++){
						cargoVertex[k].x+=0.002f;
						cargoVertex[k].y+=0.0012f;
					}
					
					cargos[i][j].origin.x+=0.002f;
					cargos[i][j].origin.y+=0.0012f;
					
					cargos[i][j].rightEnd.x+=0.002f;
					cargos[i][j].rightEnd.y+=0.0012f;
					
					cargos[i][j].bottomEnd.x+=0.002f;
					cargos[i][j].bottomEnd.y+=0.0012f;

				}
				
				cargoX = cargos[i][0].vertex3D[0].x;
				cargoY = cargos[i][0].vertex3D[0].y;
				
				if(cargoX > cargoX_MaxRight){
					float dx = cargoX_left - cargoX;
					float dy = cargoY_left - cargoY;
					for(int j = 0; j < cargos[i].length; j++){
						vector[] cargoVertex = cargos[i][j].vertex3D;
						for(int k = 0; k < cargoVertex.length; k++){
							cargoVertex[k].x+=dx;
							cargoVertex[k].y+=dy;
						}
						
						cargos[i][j].origin.x+=dx;
						cargos[i][j].origin.y+=dy;
						
						cargos[i][j].rightEnd.x+=dx;
						cargos[i][j].rightEnd.y+=dy;
						
						cargos[i][j].bottomEnd.x+=dx;
						cargos[i][j].bottomEnd.y+=dy;
					}
				}
			}
		}
	}
	
	public boolean droppingAreaIsFull(solidObject harvester){
		int tileIndex1 = tileIndex[5] + 128;
		int tileIndex2 = tileIndex[5] + 127;
		int tileIndex3 = tileIndex[5] + 129;
		
		boolean tile1Occpied = false;
		boolean tile2Occpied = false;
		boolean tile3Occpied = false;
		
		for(int i = 0; i < 4; i++){
			if(mainThread.gridMap.tiles[tileIndex1][i] != null && mainThread.gridMap.tiles[tileIndex1][i] != harvester  && !(mainThread.gridMap.tiles[tileIndex1][i].isCloaked && mainThread.gridMap.tiles[tileIndex1][i].teamNo != teamNo))
				tile1Occpied = true;
			if(mainThread.gridMap.tiles[tileIndex2][i] != null && mainThread.gridMap.tiles[tileIndex2][i] != harvester && !(mainThread.gridMap.tiles[tileIndex2][i].isCloaked && mainThread.gridMap.tiles[tileIndex2][i].teamNo != teamNo))
				tile2Occpied = true;
			if(mainThread.gridMap.tiles[tileIndex3][i] != null && mainThread.gridMap.tiles[tileIndex3][i] != harvester && !(mainThread.gridMap.tiles[tileIndex3][i].isCloaked && mainThread.gridMap.tiles[tileIndex3][i].teamNo != teamNo))
				tile3Occpied = true;
		}
		
		return tile1Occpied || tile2Occpied || tile3Occpied;
	}
	
	public boolean hasExit(){
		int tileIndex2 = tileIndex[5] + 127;
		int tileIndex3 = tileIndex[5] + 129;
		
		boolean tile2Occpied = false;
		boolean tile3Occpied = false;
		
		for(int i = 0; i < 4; i++){
			if(mainThread.gridMap.tiles[tileIndex2][i] != null)
				tile2Occpied = true;
			if(mainThread.gridMap.tiles[tileIndex3][i] != null)
				tile3Occpied = true;
		}
		
		
		return !(tile2Occpied && tile3Occpied);
	}
	
	//draw the model
	public void draw(){
		if(!visible)
			return;
		for(int i = 0; i < polygons.length; i++){
			
			polygons[i].update();
		}
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].draw();
		}
	}
	
	public vector getMovement(){
		return movenment;
	}
	

}
