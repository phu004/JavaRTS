package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

public class harvester extends solidObject{
	 
	public vector iDirectionBody, jDirectionBody, kDirectionBody;
	
	public static vector cargoCenter;
	public vector cargoCenterClone;
	public int cargoAngle = 360;
	
	public static vector pillarCenter;
	public vector pillarCenterClone;
	public int pillarAngle = 360;

	public int unloadingCount;
	
	public static polygon3D[]  body, drill0, drill1, drill2, cargo, pillars;
	
	public int drillIndex = 2;
	public int drillingCount;
	
	public polygon3D[] bodyClone, drillClone0, drillClone1, drillClone2, cargoClone, pillarsClone;
	
	
	public static int maxHP = 260;
	
	//a screen space boundary which is used to test if the harvester object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-70,-25,908, 597);
	
	//a screen space boundary which is used to test if the entire harvester object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432);
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0,0,1400, 1300);
	
	//a bitmap representation of the vision of the harvester for enemy commander
	public static boolean[] bitmapVisionForEnemy; 
		
	//the oreintation of the harvester
	public int bodyAngle;
	
	//destination angle
	public int destinationAngle; 
			
	public static Rect border, destinationBlock, probeBlock, pointBlock;
	
	public goldMine myGoldMine;
	public refinery myRefinery;
	public int[] miningPositions;
	public int cargoDeposite;
	
	public int myMiningPosition;
	public int myDropPosition;
	public float insideRefineryPositionX;
	public float insideRefineryPositionY;
	
	
	public int jobStatus = 0;
	public final int idle = 0;
	public final int isDrilling = 1;
	public final int headingToMine=2;
	public final int returningToRefinery = 3;
	public final int unloadingCargo = 4;
	public final int enteringRefinery = 5;
	public final int leavingRefinery = 6;
	public final int facingGoldMine = 7;
	public final int facingRefinery = 8;
	public final int facingRight = 9; 
	public final int facingDownward = 10;
	
	public int waitingCount = 0;
	
	
	public int heuristicRecalculationCountDown;
	public byte[] heuristicMap;
	public boolean pathIsFound;
	public float nextNodeX, nextNodeY;	
	public int bodyTurnRate = 5;  
	
	public boolean insideRefinery;
	
	public boolean isEvadingFromAttack;
				

	public harvester(vector origin, int bodyAngle, int teamNo){
		speed = 0.008f;
		start = new vector(0,0,0);
		centre = origin.myClone();
		tempCentre = origin.myClone();
		this.bodyAngle = bodyAngle;
		immediateDestinationAngle = bodyAngle;
		progressStatus = 0;
		attackStatus = isAttacking;   
		miningPositions = new int[8];
		
		destinationAngle = bodyAngle;
		this.teamNo = teamNo;
		currentHP = maxHP;
		type = 2;  
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(6);
		}
		
		
		ID = globalUniqID++;
		randomNumber = gameData.getRandom();
		height = centre.y + 0.5f;  //?
		theAssetManager = mainThread.theAssetManager; 
		boundary2D = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		border = new Rect(0,0,16,16);
		movement = new vector(0,0,0);
		updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
		
		
		boundary2D.owner = this;
		destinationBlock = new Rect((int)(origin.x*64) - 8, (int)(origin.z*64) + 8, 16, 16);
		probeBlock = new Rect((int)(origin.x*64) - 6, (int)(origin.z*64) + 6, 12, 12);
		pointBlock = new Rect((int)(origin.x*64) - 6, (int)(origin.z*64) + 6, 12, 12);
		
		//create main axis in object space
		iDirection = new vector(1f,0,0);   
		jDirection = new vector(0,1f,0);   
		kDirection = new vector(0,0,1f);  
		
		//create polygons 
		makePolygons();
		
		heuristicMap = new byte[128 * 128];
	}
	
	public void makePolygons(){
		

		int skinTextureIndex = 23;
		
		if(body == null){
				
			body = new polygon3D[52];
			v = new vector[]{put(-0.071, 0.025, 0.11), put(-0.071, 0.025, -0.15), put(-0.071, 0.005, -0.15), put(-0.071, -0.025, -0.08), put(-0.071, -0.025, 0.07), put(-0.071, 0.005, 0.11)};
			body[0] = new polygon3D(v, put(-0.071, 0.027, 0.11), put(-0.071, 0.027, -0.15), put(-0.071, -0.025, 0.11), mainThread.textures[3], 1,1,1);
			
			v = new vector[]{put(0.071, 0.005, 0.11), put(0.071, -0.025, 0.07), put(0.071, -0.025, -0.08), put(0.071, 0.005, -0.15), put(0.071, 0.025, -0.15), put(0.071, 0.025, 0.11)};
			body[1] = new polygon3D(v, put(0.071, 0.027, -0.15),put(0.071, 0.027, 0.11), put(0.071, -0.025, -0.15), mainThread.textures[3], 1,1,1);
		
			v = new vector[]{put(-0.07, 0.04, -0.15), put(0.07, 0.04, -0.15), put(0.07, 0.015, -0.15), put(-0.07, 0.015, -0.15)};
			body[2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[skinTextureIndex], 1,0.3f,1);
			
			v = new vector[]{put(-0.07, 0.005, -0.15), put(-0.04, 0.005, -0.15), put(-0.04, -0.025, -0.08), put(-0.07, -0.025, -0.08)};
			body[3] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
			
			v = new vector[]{put(-0.07, 0.015, -0.15), put(-0.04, 0.015, -0.15), put(-0.04, 0.005, -0.15), put(-0.07, 0.005, -0.15)};
			body[4] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
			
			v = new vector[]{put(0.04, 0.015, -0.15), put(0.07, 0.015, -0.15), put(0.07, 0.005, -0.15), put(0.04, 0.005, -0.15)};
			body[5] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
			
			v = new vector[]{put(0.04, 0.005, -0.15), put(0.07, 0.005, -0.15), put(0.07, -0.025, -0.08), put(0.04, -0.025, -0.08)};
			body[6] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[3], 1,1,1);
			
			v = new vector[]{put(0.07, 0.04, -0.15),  put(0.07, 0.04, 0.11), put(0.07, 0.015, 0.11),put(0.07, 0.015, -0.15)};
			body[7] = new polygon3D(v, put(0.07, 0.04, -0.15), put(0.07, 0.04, 0.11), put(0.07, 0.015, -0.15), mainThread.textures[skinTextureIndex], 1,0.3f,1);
			
			v = new vector[]{put(-0.07, 0.04, 0.11), put(-0.07, 0.04, -0.15), put(-0.07, 0.015, -0.15), put(-0.07, 0.015, 0.11)};
			body[8] = new polygon3D(v, put(-0.07, 0.04, 0.11), put(-0.07, 0.04, -0.15), put(-0.07, 0.015, 0.11), mainThread.textures[skinTextureIndex], 1,0.3f,1);
			
			v = new vector[]{put(0.07, 0.04, 0.11), put(-0.07, 0.04, 0.11), put(-0.07, 0.01, 0.11), put(0.07, 0.01, 0.11)};
			body[9] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
			
			v = new vector[]{put(0.07, 0.04, 0.11), put(-0.07, 0.04, 0.11), put(-0.07, 0.01, 0.11), put(0.07, 0.01, 0.11)};
			body[10] = new polygon3D(v, v[2], v[3], v [1], mainThread.textures[skinTextureIndex], 1,0.3f,1);
			
			v = new vector[]{put(0.07, 0.04, 0.11), put(0.07, 0.04, -0.15), put(-0.07, 0.04, -0.15),put(-0.07, 0.04, 0.11)};
			body[11] = new polygon3D(v, v[1], v[2], v[0], mainThread.textures[skinTextureIndex], 1,2f,1);
			body[11].shadowBias = 1000;
			
			v = new vector[]{put(-0.07, 0.12, 0.07), put(-0.07, 0.12, 0.02), put(-0.07, 0.04, 0.02), put(-0.07, 0.04, 0.11), put(-0.07, 0.07, 0.11)};
			body[12] = new polygon3D(v, put(-0.07, 0.12, 0.11), put(-0.07, 0.12, 0.02), put(-0.07, 0.04, 0.11), mainThread.textures[skinTextureIndex], 0.7f,0.7f,1);
			
			v = new vector[]{put(0, 0.07, 0.11), put(0, 0.04, 0.11), put(0, 0.04, 0.02), put(0, 0.12, 0.02), put(0, 0.12, 0.07)};
			body[13] = new polygon3D(v, put(0, 0.12, 0.02), put(0, 0.12, 0.11), put(0, 0.04, 0.02), mainThread.textures[skinTextureIndex], 0.7f,0.7f,1);
			
			v = new vector[]{put(-0.07, 0.12, 0.02), put(0.07, 0.12, 0.02), put(0.07, 0.04, 0.02), put(-0.07, 0.04, 0.02)};
			body[14] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.7f,1);
			
			v = new vector[]{put(0, 0.07, 0.11 ), put(-0.07, 0.07, 0.11 ), put(-0.07, 0.04, 0.11 ), put(0, 0.04, 0.11 )};
			body[15] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.5f,1);
			
			v = new vector[]{put(0, 0.12, 0.02), put(-0.07, 0.12, 0.02), put(-0.07, 0.12, 0.07), put(0, 0.12, 0.07)};
			body[16] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.5f,1);
			
			v= new vector[]{put(0, 0.12, 0.07), put(-0.07, 0.12, 0.07), put(-0.07, 0.07, 0.11), put(0, 0.07, 0.11)};
			body[17] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[24], 1f,1f,1);
			body[17].shadowBias = 40000;
			
			v = new vector[]{put(-0.07, 0.04, 0.02), put(0.07, 0.04, 0.02), put(0.07, 0.12, 0.02), put(-0.07, 0.12, 0.02)};
			body[18] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1,0.7f,1);
			
			v = new vector[]{put(0.07, 0.12, 0.02),put(0.07, 0.15, 0.04), put(0.07, 0.16, 0.10), put(0.07, 0.15, 0.10), put(0.07, 0.12, 0.07)};
			body[19] = new polygon3D(v, put(0.07, 0.12, 0.02), put(0.07, 0.12, 0.13), put(0.07, 0.02, 0.02), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			v = new vector[]{put(0, 0.12, 0.07), put(0, 0.15, 0.10), put(0, 0.16, 0.10),put(0, 0.15, 0.04),put(0, 0.12, 0.02) };
			body[20] = new polygon3D(v, put(0, 0.12, 0.13),put(0, 0.12, 0.02),  put(0, 0.02, 0.13), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			v = new vector[]{put(0, 0.15, 0.04), put(0.07, 0.15, 0.04), put(0.07, 0.12, 0.02), put(0, 0.12, 0.02)};
			body[21] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.25f,1);
			
			v = new vector[]{put(0, 0.16, 0.10), put(0.07, 0.16, 0.10), put(0.07, 0.15, 0.04), put(0, 0.15, 0.04) };
			body[22] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.5f,1);
			
			v = new vector[]{put(0, 0.12, 0.02),put(0, 0.15, 0.04), put(0, 0.16, 0.10), put(0, 0.15, 0.10), put(0, 0.12, 0.07)};
			body[23] = new polygon3D(v, put(0, 0.12, 0.02), put(0, 0.12, 0.13), put(0, 0.02, 0.02), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			v = new vector[]{put(0.07, 0.12, 0.07), put(0.07, 0.15, 0.10), put(0.07, 0.16, 0.10),put(0.07, 0.15, 0.04),put(0.07, 0.12, 0.02) };
			body[24] = new polygon3D(v, put(0.07, 0.12, 0.13),put(0.07, 0.12, 0.02),  put(0.07, 0.02, 0.13), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			v = new vector[]{put(0.07, 0.16, 0.10), put(0, 0.16, 0.10), put(0, 0.15, 0.10), put(0.07, 0.15, 0.10)};
			body[25] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.1f,1);
			
			v = new vector[]{put(0.07, 0.12, 0.02), put(0.07, 0.12, 0.07), put(0.07, 0.04, 0.07), put(0.07, 0.04, 0.02) };
			body[26] = new polygon3D(v, put(0.07, 0.12, 0.02), put(0.07, 0.12, 0.13), put(0.07, 0.02, 0.02), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			v = new vector[]{put(0.07, 0.04, 0.02), put(0.07, 0.04, 0.07), put(0.07, 0.12, 0.07) , put(0.07, 0.12, 0.02)};
			body[27] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.1f,1);
			
			v = new vector[]{put(0.07, 0.12, 0.07), put(0.06, 0.12, 0.07), put(0.06, 0.04, 0.07), put(0.07, 0.04, 0.07)};
			body[28] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.1f,0.5f,1);
			
			v = new vector[]{put(0.01, 0.12, 0.07), put(0, 0.12, 0.07), put(0, 0.04, 0.07), put(0.01, 0.04, 0.07)};
			body[29] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.1f,0.5f,1);
			
			v = new vector[]{put(0.07, 0.08, 0.07), put(0, 0.08, 0.07), put(0, 0.04, 0.11), put(0.07, 0.04, 0.11)};
			body[30] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.5f,0.5f,1);
			
			v = new vector[]{put(0.07, 0.08, 0.07), put(0.07, 0.04, 0.11), put(0.07, 0.04, 0.07)};
			body[31] = new polygon3D(v, put(0.07, 0.12, 0.02), put(0.07, 0.12, 0.13), put(0.07, 0.02, 0.02), mainThread.textures[skinTextureIndex], 1, 1,1);
			
			
			v = new vector[]{put(0.055, 0.21, 0), put(0.055, 0.21, 0.06), put(0.055, 0.18, 0.06),put(0.055, 0.18, 0)};
			body[32] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 4f,4f,1);
			
			v = new vector[]{put(0.015, 0.18, 0), put(0.015, 0.18, 0.06), put(0.015, 0.21, 0.06),put(0.015, 0.21, 0)};
			body[33] = new polygon3D(v, v[2].myClone(), v[3].myClone(), v[1].myClone(), mainThread.textures[25], 4f,4f,1);
			
			v = new vector[]{put(0.055, 0.21, 0), put(0.015, 0.21, 0), put(0.015, 0.21, 0.06), put(0.055, 0.21, 0.06)};
			body[34] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 4f,4f,1);
			body[34].shadowBias = 30000;
			
			v = new vector[]{put(0.055, 0.21, 0.06), put(0.055, 0.21, 0.12), put(0.055, 0.18, 0.12),put(0.055, 0.18, 0)};
			body[35] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 4f,4f,1);
			
			v = new vector[]{put(0.015, 0.18, 0.06), put(0.015, 0.18, 0.12), put(0.015, 0.21, 0.12),put(0.015, 0.21, 0)};
			body[36] = new polygon3D(v, v[2].myClone(), v[3].myClone(), v[1].myClone(), mainThread.textures[25], 4f,4f,1);
			
			v = new vector[]{put(0.055, 0.21, 0.06), put(0.015, 0.21, 0.06), put(0.015, 0.21, 0.12), put(0.055, 0.21, 0.12)};
			body[37] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 4f,4f,1);
			body[37].shadowBias = 50000;
			
			
			double theta = Math.PI/12;
			double r = 0.015;
			
			start.add(0,(float)(0.18 + r),0.12f);
			for(int i = 0; i < 12; i++){
				v = new vector[]{put(0.055, r*Math.cos((i+1)*theta), r*Math.sin((i+1)*theta)),
								 put(0.055, r*Math.cos(i*theta), r*Math.sin(i*theta)),
								 put(0.015, r*Math.cos(i*theta), r*Math.sin(i*theta)),
								 put(0.015, r*Math.cos((i+1)*theta),  r*Math.sin((i+1)*theta))
								
								};
				body[38 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[25], 4f,4f,1);
				
			}
			
			
			v = new vector[13];
			for(int i = 0; i < 13; i ++){
				v[i] = put(0.055, r*Math.cos(i*theta), r*Math.sin(i*theta));
			}
			body[50] = new polygon3D(v, put(0.055, 0.21, 0.06), put(0.055, 0.21, 0.12), put(0.055, 0.18, 0), mainThread.textures[25], 4f,4f,1);
			
			vector[] v2 = new vector[13];
			for(int i = 0; i < 13; i ++){
				v2[i] = v[12 -i].myClone();
				v2[i].x = 0.015f;
			}
			body[51] = new polygon3D(v2, put(0.015, 0.21, 0.12), put(0.015, 0.21, 0), put(0.015, 0.18, 0.12), mainThread.textures[25], 4f,4f,1);
			
			start.add(0,(float)(-0.18 - r),-0.12f);
			
			drill0 = new polygon3D[32];
			
			makeTriangle(drill0, 0, 0, 0,0,0.01f);
			makeTriangle(drill0, 4, 0, 0,0,0.03f);
			makeTriangle(drill0, 8, 0, 0,0,0.05f);
			makeTriangle(drill0, 12, 0, 0,0,0.07f);
			makeTriangle(drill0, 16, 0, 0,0,0.09f);
			makeTriangle(drill0, 20, 0, 0,0,0.11f);
			makeTriangle(drill0, 24, 55, 0,0.085f,-0.045f);
			makeTriangle(drill0, 28, 100, 0, 0.23f,-0.075f);
			
			
			drill1 = new polygon3D[32];
			
			makeTriangle(drill1, 0, 0, 0,0,0.0166f);
			makeTriangle(drill1, 4, 0, 0,0,0.0366f);
			makeTriangle(drill1, 8, 0, 0,0,0.0566f);
			makeTriangle(drill1, 12, 0, 0,0,0.0766f);
			makeTriangle(drill1, 16, 0, 0,0,0.0966f);
			makeTriangle(drill1, 20, 15, 0,0.0065f,0.062f);
			makeTriangle(drill1, 24, 70, 0,0.13f,-0.068f);
			makeTriangle(drill1, 28, 0, 0,0, -0.0034f);
			
			
			drill2 = new polygon3D[32];
			
			makeTriangle(drill2, 0, 0, 0,0,0.0166f + 0.0066f);
			makeTriangle(drill2, 4, 0, 0,0,0.0366f + 0.0066f);
			makeTriangle(drill2, 8, 0, 0,0,0.0566f + 0.0066f);
			makeTriangle(drill2, 12, 0, 0,0,0.0766f + 0.0066f);
			makeTriangle(drill2, 16, 0, 0,0,0.0966f + 0.0066f);
			makeTriangle(drill2, 20, 40, 0,0.05f,-0.012f);
			makeTriangle(drill2, 24, 80, 0,0.16f,-0.075f);
			makeTriangle(drill2, 28, 0, 0,0, -0.0034f + 0.0066f);
			
							
			
			int YZ_angle = 50;
			tempVector.set(0,-0.01f, -0.08f);
			for(int i = 32; i < body.length; i++){
				
				body[i].origin.rotate_YZ(YZ_angle);
				body[i].origin.add(tempVector);
				
				body[i].bottomEnd.rotate_YZ(YZ_angle);
				body[i].bottomEnd.add(tempVector);
				
				body[i].rightEnd.rotate_YZ(YZ_angle);
				body[i].rightEnd.add(tempVector);
			
			
					
				for(int j = 0; j < body[i].vertex3D.length; j++){
					
					body[i].vertex3D[j].rotate_YZ(YZ_angle);
					body[i].vertex3D[j].add(tempVector);
					
					
					
				}
				body[i].normal.rotate_YZ(YZ_angle);
			}
			
			for(int i = 0; i < body.length; i++){
				body[i].findDiffuse();
				body[i].parentObject = this;
				
			}
			
			
			for(int i = 0; i < drill0.length; i++){
				
				drill0[i].origin.rotate_YZ(YZ_angle);
				drill0[i].origin.add(tempVector);
				
				drill0[i].bottomEnd.rotate_YZ(YZ_angle);
				drill0[i].bottomEnd.add(tempVector);
				
				drill0[i].rightEnd.rotate_YZ(YZ_angle);
				drill0[i].rightEnd.add(tempVector);
				
				drill1[i].origin.rotate_YZ(YZ_angle);
				drill1[i].origin.add(tempVector);
				
				drill1[i].bottomEnd.rotate_YZ(YZ_angle);
				drill1[i].bottomEnd.add(tempVector);
				
				drill1[i].rightEnd.rotate_YZ(YZ_angle);
				drill1[i].rightEnd.add(tempVector);
				
				drill2[i].origin.rotate_YZ(YZ_angle);
				drill2[i].origin.add(tempVector);
				
				drill2[i].bottomEnd.rotate_YZ(YZ_angle);
				drill2[i].bottomEnd.add(tempVector);
				
				drill2[i].rightEnd.rotate_YZ(YZ_angle);
				drill2[i].rightEnd.add(tempVector);
			
			
					
				for(int j = 0; j < drill0[i].vertex3D.length; j++){
					
					drill0[i].vertex3D[j].rotate_YZ(YZ_angle);
					drill0[i].vertex3D[j].add(tempVector);
					
					drill1[i].vertex3D[j].rotate_YZ(YZ_angle);
					drill1[i].vertex3D[j].add(tempVector);
					
					drill2[i].vertex3D[j].rotate_YZ(YZ_angle);
					drill2[i].vertex3D[j].add(tempVector);
					
				}
				
				drill0[i].normal.rotate_YZ(YZ_angle);
				drill0[i].findDiffuse();
				drill0[i].parentObject = this;
				
				drill1[i].normal.rotate_YZ(YZ_angle);
				drill1[i].findDiffuse();
				drill1[i].parentObject = this;
				
				drill2[i].normal.rotate_YZ(YZ_angle);
				drill2[i].findDiffuse();
				drill2[i].parentObject = this;
				
				
			}
			
			start.set(0,0,0);
			cargoCenter = put(0, 0.04, -0.15);
			cargo = new polygon3D[11];
			v =  new vector[]{put(-0.06, 0, -0.02), put(0.06, 0, -0.02), put(0.06, 0, 0.16), put(-0.06, 0, 0.16)};
			cargo[0] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1f,1.5f,1);
			
			v = new vector[]{put(0.07, 0.01, -0.02), put(0.07, 0.01, 0.16), put(0.06, 0, 0.16), put(0.06, 0, -0.02)};
			cargo[1] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.1f,1);
			
			v = new vector[]{put(-0.06, 0, -0.02), put(-0.06, 0, 0.16), put(-0.07, 0.01, 0.16), put(-0.07, 0.01, -0.02)};
			cargo[2] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.1f,1);
			
			v = new vector[]{put(0.07, 0.06, -0.02),put(0.07, 0.06, 0.16), put(0.07, 0.01, 0.16), put(0.07, 0.01, -0.02)};
			cargo[3] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.5f,1);
			
			v = new vector[]{put(-0.07, 0.01, -0.02), put(-0.07, 0.01, 0.16),put(-0.07, 0.06, 0.16), put(-0.07, 0.06, -0.02)};
			cargo[4] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.5f,1);
			
			v = new vector[]{put(-0.07, 0.06, -0.02), put(-0.06, 0.07, -0.02), put(0.06, 0.07, -0.02), put(0.07, 0.06, -0.02), put(0.07, 0.01, -0.02), put(0.06, 0, -0.02), put(-0.06, 0, -0.02), put(-0.07, 0.01, -0.02)};
			cargo[5] = new polygon3D(v, put(-0.07, 0.07, -0.02), put(0.07, 0.07, -0.02),put(-0.07, 0.01, -0.02), mainThread.textures[skinTextureIndex], 1f,0.5f,1);
			
			v = new vector[]{put(-0.07, 0.01, 0.16), put(-0.06, 0, 0.16), put(0.06, 0, 0.16), put(0.07, 0.01, 0.16), put(0.07, 0.06, 0.16), put(0.06, 0.07, 0.16),  put(-0.06, 0.07, 0.16), put(-0.07, 0.06, 0.16)};
			cargo[6] = new polygon3D(v, put(0.07, 0.07, 0.16), put(-0.07, 0.07, 0.16), put(0.07, 0.01, 0.16), mainThread.textures[skinTextureIndex], 1f,0.5f,1);
			
			v = new vector[]{put(-0.06, 0.07, 0.16), put(0.06, 0.07, 0.16), put(0.06, 0.07, -0.02), put(-0.06, 0.07, -0.02)};
			cargo[7] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 0.8f,1.3f,1);
			
			v = new vector[]{put(0.065, 0.06, 0.161), put(0.005, 0.06, 0.161), put(0.005, 0.02, 0.161), put(0.065, 0.02, 0.161)};
			cargo[8] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[27], 1f,1f,1);
			
			v = new vector[]{put(0.06, 0.07, -0.02), put(0.06, 0.07, 0.16), put(0.07, 0.06, 0.16),  put(0.07, 0.06, -0.02)};
			cargo[9] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.1f,1);
			
			v = new vector[]{put(-0.07, 0.06, -0.02), put(-0.07, 0.06, 0.16), put(-0.06, 0.07, 0.16),  put(-0.06, 0.07, -0.02)};
			cargo[10] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[skinTextureIndex], 1.5f,0.1f,1);
			
			
			start.set(0,0,0);
			pillarCenter = put(0, 0.035,0);
			pillars = new polygon3D[98];
			
			theta = Math.PI/12;
			r = 0.008;
			
			for(int i = 0; i < 24; i++){
				v = new vector[]{put(r*Math.cos((i+1)*theta) - 0.03,  r*Math.sin((i+1)*theta), 0),
								put(r*Math.cos(i*theta) - 0.03, r*Math.sin(i*theta), 0),
								put(r*Math.cos(i*theta) - 0.03, r*Math.sin(i*theta), -0.07 ),
						 		put(r*Math.cos((i+1)*theta) - 0.03, r*Math.sin((i+1)*theta), -0.07),
								
								};
				pillars[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[28], 4f,4f,1);
			}
			
			v = new vector[24];
			for(int i = 0; i < 24; i ++){
				v[23 -i] = put(r*Math.cos(i*theta) - 0.03, r*Math.sin(i*theta), -0.07);
			}
			pillars[24] = new polygon3D(v, put(0.21 - 0.03, 0.06, -0.07), put(0.21 - 0.03, 0.12, -0.07), put(0.18 - 0.03, 0, -0.07), mainThread.textures[28], 4f,4f,1);
			
			for(int i = 0; i < 24; i++){
				v = new vector[]{put(r*Math.cos((i+1)*theta) + 0.03,  r*Math.sin((i+1)*theta), 0),
								put(r*Math.cos(i*theta) + 0.03, r*Math.sin(i*theta), 0),
								put(r*Math.cos(i*theta) + 0.03, r*Math.sin(i*theta), -0.07 ),
						 		put(r*Math.cos((i+1)*theta) + 0.03, r*Math.sin((i+1)*theta), -0.07),
								
								};
				pillars[25 + i] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[28], 4f,4f,1);
			}
			
			v = new vector[24];
			for(int i = 0; i < 24; i ++){
				v[23 -i] = put(r*Math.cos(i*theta) + 0.03, r*Math.sin(i*theta), -0.07);
			}
			pillars[49] = new polygon3D(v, put(0.21 + 0.03, 0.06, -0.07), put(0.21 + 0.03, 0.12, -0.07), put(0.18 + 0.03, 0, -0.07), mainThread.textures[28], 4f,4f,1);
			
			r = 0.004;
			for(int i = 0; i < 24; i++){
				v = new vector[]{put(r*Math.cos((i+1)*theta) - 0.03,  r*Math.sin((i+1)*theta), -0.07),
								put(r*Math.cos(i*theta) - 0.03, r*Math.sin(i*theta), -0.07),
								put(r*Math.cos(i*theta) - 0.03, r*Math.sin(i*theta), -0.15 ),
						 		put(r*Math.cos((i+1)*theta) - 0.03, r*Math.sin((i+1)*theta), -0.15),
								
								};
				pillars[50 + i ] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[29], 4f,4f,1);
			}
			
			for(int i = 0; i < 24; i++){
				v = new vector[]{put(r*Math.cos((i+1)*theta) + 0.03,  r*Math.sin((i+1)*theta), -0.07),
								put(r*Math.cos(i*theta) + 0.03, r*Math.sin(i*theta), -0.07),
								put(r*Math.cos(i*theta) + 0.03, r*Math.sin(i*theta), -0.15 ),
						 		put(r*Math.cos((i+1)*theta) + 0.03, r*Math.sin((i+1)*theta), -0.15),
								
								};
				pillars[74 + i ] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[29], 4f,4f,1);
			}
			
		}
		
		bodyClone = clonePolygons(body,true);
		drillClone0 = clonePolygons(drill0,true); 
		drillClone1 = clonePolygons(drill1,true); 
		drillClone2 = clonePolygons(drill2,true); 
		cargoClone = clonePolygons(cargo,true);
		cargoCenterClone = new vector(0,0,0);
		pillarsClone = clonePolygons(pillars,true);
		pillarCenterClone = new vector(0,0,0);
		
		if(teamNo != 0){
			for(int i = 0; i < body.length; i++){
				if(body[i].myTexture.ID == 23)
					bodyClone[i].myTexture = mainThread.textures[10];
			}
			
			for(int i = 0; i < cargo.length; i++){
				if(cargo[i].myTexture.ID == 23)
					cargoClone[i].myTexture = mainThread.textures[10];
			}
		}
	}
	
	public void makeTriangle(polygon3D[] triangles, int startIndex, int angle, float x, float y, float z){
		float x_old = start.x;
		float y_old = start.y;
		float z_old = start.z;
		start.set(x,y,z);
		jDirection.rotate_YZ(angle);
		kDirection.rotate_YZ(angle);
		
		v = new vector[]{put(0.02, 0.225, 0), put(0.05, 0.225, 0), put(0.05, 0.21, 0), put(0.02, 0.21, 0)};
		triangles[startIndex] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 4f,4f,1);
		
		v = new vector[]{put(0.05, 0.225, 0), put(0.02, 0.225, 0), put(0.02, 0.21, 0.01f), put(0.05, 0.21, 0.01f)};
		triangles[startIndex + 1] = new polygon3D(v, v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[26], 4f,4f,1);
		
		v = new vector[]{put(0.05, 0.225, 0),  put(0.05, 0.21, 0.01f), put(0.05, 0.21, 0)};
		triangles[startIndex + 2] = new polygon3D(v, v[0].myClone(), put(0.05, 0.225, 0.01), v[2].myClone(), mainThread.textures[26], 4f,4f,1);
		
		v = new vector[]{put(0.02, 0.21, 0),  put(0.02, 0.21, 0.01f), put(0.02, 0.225, 0)};
		triangles[startIndex + 3] = new polygon3D(v, put(0.02, 0.21, 0f), put(0.02, 0.21, 0.01), put(0.02, 0.225, 0f), mainThread.textures[26], 4f,4f,1);
		
		
		start.set(x_old, y_old, z_old);
		jDirection.rotate_YZ(360 - angle);
		kDirection.rotate_YZ(360 -angle);
		
	}
	
	
	
	//update the model 
	public void update(){		
		
	
		//handle unloading gold ore event
		if(unloadingCount > 0){
			if(unloadingCount > 69 && cargoAngle > 300)
				cargoAngle-=2;
				
			
			if(unloadingCount < 31 && cargoAngle < 360)
				cargoAngle+=2;
			
			unloadingCount--;
		}
		
		//handle drilling event
		if(drillingCount > 0){
			drillIndex--;
			drillIndex = (drillIndex + 3)%3;
			drillingCount --;
		}
		
		if(waitingCount > 0)
			waitingCount --;
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;
		
		
		//check if harvester has been destroyed
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
				attacker.experience+=25;
			if(insideRefinery){
				if(myRefinery!= null)
					myRefinery.isBusy = false;
			}
				
			return;
		}
		
		//carry out commands given by the player or AI
		if(!disableUnitLevelAI)
			carryOutCommands();
		
		
		
		if(tightSpaceManeuverCountDown > 0)
			tightSpaceManeuverCountDown--;
		
		if(heuristicRecalculationCountDown > 0)
			heuristicRecalculationCountDown--;

		//update centre
		if(Math.abs(movement.x) + Math.abs(movement.z) < 0.25f){
			centre.add(movement);
			boundary2D.setOrigin((int)(centre.x*64) - 8, (int)(centre.z*64) + 8);
			updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
		}else{
			movement.reset();
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
		}
		
		if(visionInsideScreen && teamNo == 0){
			
			tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
			tempFloat[0] = teamNo;
			tempFloat[1] = centre.x;
			tempFloat[2] = -0.4f;
			tempFloat[3] = centre.z;
			tempFloat[4] = 0;
			theAssetManager.visionPolygonCount++;
		}
		
		//check if the tank object is visible in mini map
		visible_minimap = theAssetManager.minimapBitmap[boundary2D.x1/16 + (127 - boundary2D.y1/16)*128];

		if(teamNo == 0 || visible_minimap){
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1/16;
			tempInt[2] = 127 - boundary2D.y1/16;
			tempInt[3] = 0;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else
				tempInt[4] = 0;
			theAssetManager.unitsForMiniMapCount++;
		}
		
		
		
		//test if the tank object is visible in camera point of view
		if(visible_minimap){
			if(currentHP <= 130 && (mainThread.frameIndex + ID) % 3 ==0){
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
			updateGeometry();
				
			
			for(int i = 0; i < bodyClone.length; i++){
				bodyClone[i].update_lightspace();	
			}
			
			polygon3D[] drillClone;
			drillClone = null;		
			if(drillIndex == 0){
				drillClone = drillClone0;
			}
			if(drillIndex == 1){
				drillClone = drillClone1;
			}
			if(drillIndex == 2){
				drillClone = drillClone2;
			}
			
			for(int i = 0; i < drillClone.length; i++){
				drillClone[i].update_lightspace();	
			}
			
			for(int i = 0; i < cargoClone.length; i++){
				cargoClone[i].update_lightspace();	
			}
			
			if(unloadingCount > 0){
				for(int i = 0; i < pillarsClone.length; i++){
					pillarsClone[i].update_lightspace();	
				}
			}
			
			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;
			
			if(waitingCount > 0)
				movement.reset();
		}
		
	}
	
	//carry out commands given by player or  AI commander
	public void carryOutCommands(){
		
		
		if(currentCommand == StandBy){
			resetLogicStatus();
			jobStatus = idle;
			
		}else if(currentCommand == move){
			float d = Math.abs(destinationX - centre.x) + Math.abs(destinationY - centre.z);
			
			if(waitingCount <= 1){
				if(d < 1.5f){
					if(jobStatus == headingToMine){
						//check if the mining spot is already occupied
						tile = mainThread.gridMap.tiles[myMiningPosition];
						for(int i = 0; i < 5; i++){
							if(tile[i] != null && tile[i] != this){
								if(tile[i].getMovement().x ==0 && tile[i].getMovement().z == 0){
									boolean foundFreeSpot = false;
									for(int j = 0; j < 7; j++){
										int p = miningPositions[(myMiningPosition + j)%8];
										tile = mainThread.gridMap.tiles[p];
										boolean freespot = true;
										
										for(int k = 0; k < 5; k++){
											if(tile[k] != null){
												freespot = false;
												break;
											}
										}
										
										if(freespot){
											myMiningPosition = p;
											int xPosition = myMiningPosition%128;
											int yPosition = 127 - myMiningPosition/128;
											this.destinationX = xPosition*0.25f +0.125f;
											this.destinationY = yPosition*0.25f +0.125f;
											foundFreeSpot = true;
											break;
										}
									}
									
									if(!foundFreeSpot)
										waitingCount = 15;
									
									break;
								}else{
									waitingCount = 15;
								}
							}
						}
						
					}
				}
				
				if(d < 2){
					if(jobStatus == returningToRefinery){
						if(myRefinery == null || myRefinery.currentHP <=0 || myRefinery.isBusy || myRefinery.droppingAreaIsFull(this)){
							returnToRefinery(null);
							movement.reset();
						}
					}
				}
			}
			
			
			 
			if(waitingCount == 0){
				if(jobStatus == leavingRefinery){
					if(centre.z != insideRefineryPositionY - 0.25f){
						if(centre.z - (insideRefineryPositionY - 0.25f)  < speed) 
							movement.set(0,0, (insideRefineryPositionY - 0.25f) - centre.z);
						else
							movement.set(0,0,-speed);
						
						xPos_old = boundary2D.x1;
						yPos_old = boundary2D.y1;
						xPos = (int)((centre.x + movement.x)*64) - 8;
						yPos = (int)((centre.z + movement.z)*64) + 8;
						boundary2D.setOrigin(xPos, yPos);
						
						boolean canMove = true;
						for(int i = 0; i < 4; i++){
							solidObject o = mainThread.gridMap.tiles[myRefinery.tileIndex[5]+128][i];
							if(o != null && o != this){
								if(o.boundary2D.intersect(boundary2D)){
									canMove = false;
									if(o.teamNo != teamNo)           //deactivate  enemy unit's cloak ability on collision
										o.cloakCooldownCount = 60;
									break;
								}
							}
						}
						
						if(!canMove){
							movement.reset();
						}
						
						boundary2D.x1 = xPos_old;
						boundary2D.y1 = yPos_old;
						
					}else{
						jobStatus = headingToMine;
						myRefinery.isBusy = false;
						insideRefinery = false;
						harvest(myGoldMine);
					}
					if(myRefinery.currentHP <= 0)
						harvest(myGoldMine);
				}else if(jobStatus == facingDownward){
					if(bodyAngle != 180){
						int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, 180, bodyTurnRate) + 360)%360;
						bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					}else{
						if(myRefinery.hasExit()) 
							jobStatus = leavingRefinery;
						else
							waitingCount = 15;
					}
					movement.reset();
					if(myRefinery.currentHP <= 0)
						returnToRefinery(null);
				}else if(jobStatus == unloadingCargo){
					if(unloadingCount == 0)
						unloadingCount = 100;
					if(unloadingCount == 50){
						if(teamNo == 0)
							mainThread.pc.theBaseInfo.currentCredit +=cargoDeposite;
						else
							mainThread.ec.theBaseInfo.currentCredit +=cargoDeposite;
						cargoDeposite = 0;
						progressStatus = 100*cargoDeposite/700;
					}
					if(unloadingCount == 1){
						jobStatus = facingDownward;
					}
					movement.reset();
					if(myRefinery.currentHP <= 0)
						returnToRefinery(null);
				}else if(jobStatus == facingRight){
					if(bodyAngle != 90){
						int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, 90, bodyTurnRate) + 360)%360;
						bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					}else{
					
						jobStatus = unloadingCargo;
					
					}
					movement.reset();
					if(myRefinery.currentHP <= 0)
						returnToRefinery(null);
				}else if(jobStatus == enteringRefinery){
					if(centre.z != insideRefineryPositionY){
						if(insideRefineryPositionY - centre.z < speed)
							movement.set(0,0,insideRefineryPositionY - centre.z);
						else
							movement.set(0,0,speed);
					}else{
						jobStatus = facingRight;
						movement.reset();
					}
					if(myRefinery.currentHP <= 0)
						returnToRefinery(null);
				}else if(jobStatus == facingGoldMine){
					destinationAngle = geometry.findAngle(centre.x, centre.z, myGoldMine.centre.x, myGoldMine.centre.z);
					immediateDestinationAngle = destinationAngle;	
					if(bodyAngle != immediateDestinationAngle){
						int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
						bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					}else{
						jobStatus = isDrilling;
						drillingCount = 700;
					}
					movement.reset();
				}else if(jobStatus == facingRefinery){
					
					if((bodyAngle%360) != 0){
						int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, 0, bodyTurnRate) + 360)%360;
						bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
					}else{
						jobStatus = enteringRefinery;
						myRefinery.isBusy = true;
						insideRefinery = true;
						myRefinery.unloadOreCountDown = 200;
					}
					movement.reset();
					if(myRefinery.currentHP <= 0)
						returnToRefinery(null);
					
				}else if(jobStatus == isDrilling){
					if(myGoldMine.goldDeposite > 1){
						myGoldMine.goldDeposite-=1;
						cargoDeposite+=1;
						progressStatus = 100*cargoDeposite/700;
					}else if(cargoDeposite >0)
						returnToRefinery(null);
					
					if(drillingCount == 0 || cargoDeposite == 700 || myGoldMine.goldDeposite == 0){	
						returnToRefinery(null);
					}
					movement.reset();
				}else{
					//check if the harvest is at a position that is suitable for mining 
					if(jobStatus == headingToMine ) {
						int nodeX = (int)(centre.x * 64)/16;
						int nodeY = 127 - (int)(centre.z * 64)/16;
						int modX = (int)(centre.x * 64) % 16;
						int modY = (int)(centre.z * 64) % 16;
						
						if(modX == 8 && modY == 8 && d < 1) {
							for(int i =0; i < 8; i++) {
								int miningNodeX = miningPositions[i] % 128;
								int miningNodeY = miningPositions[i] / 128;
								
								if(miningNodeX == nodeX && miningNodeY == nodeY) {
									int xPosition = miningPositions[i]%128;
									int yPosition = 127 - miningPositions[i]/128;
									destinationX = xPosition*0.25f +0.125f;
									destinationY = yPosition*0.25f +0.125f;
									break;
								}
							 
							}
						}
					} 
					
					performPathFindingLogic();
				}
			}else{
				movement.reset();
			}
			
		}
	}
	
	//use a path finder to move to desination
	public void performPathFindingLogic(){
		
		
		if(!pathIsFound  && heuristicRecalculationCountDown == 0){
			
			
			
			int destX  = (int)(destinationX * 64)/16;
			int destY =  127 - (int)(destinationY * 64)/16;
			
			pathIsFound = PathFinder.createHeuristicMap(heuristicMap,occupiedTile0, occupiedTile1, occupiedTile2, occupiedTile3, destX, destY);
			heuristicRecalculationCountDown = 32;
			
			
			
			if(pathIsFound){
					
				//find the first node in the path
				int  nextTile0 = findAdjacentTileWithSmallestHeuristic(occupiedTile0);
				int  nextTile1 = findAdjacentTileWithSmallestHeuristic(occupiedTile1);
				int  nextTile2 = findAdjacentTileWithSmallestHeuristic(occupiedTile2);
				int  nextTile3 = findAdjacentTileWithSmallestHeuristic(occupiedTile3);
				
				if(occupiedTile1 == -1)
					nextTile1 = occupiedTile1;
				if(occupiedTile2 == -1)
					nextTile2 = occupiedTile2;
				if(occupiedTile3 == -1)
					nextTile3 = occupiedTile3;
				
				if(nextTile0 != occupiedTile0){
					nextNodeX = 0.125f + (nextTile0%128) * 0.25f;
					nextNodeY = 0.125f +  (127 - (nextTile0/128)) * 0.25f;
				
					
				}else if(nextTile1 != occupiedTile1){
					nextNodeX = 0.125f + (nextTile1%128) * 0.25f;
					nextNodeY = 0.125f +  (127 - (nextTile1/128)) * 0.25f;
				
				}else if(nextTile2 != occupiedTile2){
					nextNodeX = 0.125f + (nextTile2%128) * 0.25f;
					nextNodeY = 0.125f +  (127 - (nextTile2/128)) * 0.25f;
					
				}else if(nextTile3 != occupiedTile3){
					nextNodeX = 0.125f + (nextTile3%128) * 0.25f;
					nextNodeY = 0.125f +  (127 - (nextTile3/128)) * 0.25f;
					
				}
			}
		}
		
		if(pathIsFound){
			
			
			
			movement.reset();
		
			//check if the harvester has reached next node in the path
			if(centre.x == nextNodeX && centre.z == nextNodeY){
				//check if the harvester has reached the destination
				int destX  = (int)(destinationX * 64)/16;
				int destY =  127 - (int)(destinationY * 64)/16;
				int nodeX = (int)(centre.x * 64)/16;
				int nodeY = 127 - (int)(centre.z * 64)/16;
				if(destX == nodeX && destY == nodeY){
					
					
					pathIsFound = false;
					resetLogicStatus();
					
					if(jobStatus == idle)
						currentCommand = StandBy;
					else{
						if(jobStatus == headingToMine){
							jobStatus = facingGoldMine;
							if(myGoldMine.goldDeposite ==0){
								currentCommand = StandBy;
							}
						}
						
						if(jobStatus == returningToRefinery){
							jobStatus = facingRefinery;
							if(myRefinery.currentHP == 0){
								returnToRefinery(null);
							}
						}
						
					}
						
					return;
				}else{
					//if destination hasn't reached, find the next node
					int nextTile0 = findAdjacentTileWithSmallestHeuristic(occupiedTile0);
					nextNodeX = 0.125f + (nextTile0%128) * 0.25f;
					nextNodeY = 0.125f +  (127 - (nextTile0/128)) * 0.25f;
					
				}
			}
			
			
			
			float distanceToNextNode = (float)Math.sqrt((nextNodeX - centre.x) * (nextNodeX - centre.x) + (nextNodeY - centre.z) * (nextNodeY - centre.z));
			calculateMovement();
			destinationAngle = geometry.findAngle(centre.x, centre.z, nextNodeX, nextNodeY);
			immediateDestinationAngle = destinationAngle;	
			
			if(Math.abs(bodyAngle - immediateDestinationAngle) > 45 && Math.abs(bodyAngle - immediateDestinationAngle) < 315){
				
				int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
				bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
				movement.reset();
				
			}else{
				if(bodyAngle != immediateDestinationAngle){
					int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
					bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
				}
				
				movement.set(nextNodeX - centre.x, 0, nextNodeY - centre.z);
				
				if(speed < distanceToNextNode){
					movement.unit();
					movement.scale(speed);
				}
				
				//check collision
				xPos_old = boundary2D.x1;
				yPos_old = boundary2D.y1;
				xPos = (int)((centre.x + movement.x)*64) - 8;
				yPos = (int)((centre.z + movement.z)*64) + 8;
				boundary2D.setOrigin(xPos, yPos);
				
				Rect r = checkForCollision(boundary2D);
				boundary2D.setOrigin(xPos_old, yPos_old);
				
				if(r != null){
					movement.reset();
					pathIsFound = false;
					if(r.owner.teamNo != teamNo)           //deactivate  enemy unit's cloak ability on collision
						r.owner.cloakCooldownCount = 60;
				}
				
				
			}
			
			return;
		}
			
		
		if(!pathIsFound){
			if((movement.x == 0 && movement.z == 0) || mainThread.gridMap.tiles[occupiedTile0][4] != null){
				if((Math.abs(destinationX - centre.x) + Math.abs(destinationY - centre.z) > 0.5) ||(jobStatus == idle)){
					heuristicRecalculationCountDown = 64;
				}
			}
			performMovementLogic();
			avoidGettingStucked();
			
			//harvester is always on the move
			if(jobStatus != idle){
				currentCommand = move;
			}
		}
		
	}
	
	public int findAdjacentTileWithSmallestHeuristic(int currentTile){
		int smallestHeurstic = 127;
		int nextTile = currentTile;
		
		boolean[] obstacleMap = mainThread.gridMap.previousObstacleMap;
		
		//check north west tile
		int northWestTile = currentTile - 128 - 1;
		int northTile = currentTile - 128;
		int northEastTile = currentTile - 128 + 1;
		int eastTile = currentTile + 1;
		int southEastTile = currentTile + 1 + 128;
		int southTile = currentTile + 128;
		int southWestTile = currentTile + 128 - 1;
		int westTile = currentTile - 1;
		
		if(northWestTile > 0 && northWestTile < 16384 && obstacleMap[northTile] && obstacleMap[westTile]){
			if(heuristicMap[northWestTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[northWestTile];
				nextTile = northWestTile;
			}
		}
		
		//check north tile
		
		if(northTile > 0 && northTile < 16384){
			if(heuristicMap[northTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[northTile];
				nextTile = northTile;
			}
		}
		
		//check north east tile
		if(northEastTile > 0 && northEastTile < 16384 && obstacleMap[northTile] && obstacleMap[eastTile]){
			if(heuristicMap[northEastTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[northEastTile];
				nextTile = northEastTile;
			}
		}
		
		//check east tile
		
		if(eastTile > 0 && eastTile < 16384){
			if(heuristicMap[eastTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[eastTile];
				nextTile = eastTile;
			}
		}
		
		//check south east tile
		
		if(southEastTile > 0 && southEastTile < 16384 && obstacleMap[southTile] && obstacleMap[eastTile]){
			if(heuristicMap[southEastTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[southEastTile];
				nextTile = southEastTile;
			}
		}
		
		//check south tile
		
		if(southTile > 0 && southTile < 16384){
			if(heuristicMap[southTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[southTile];
				nextTile = southTile;
			}
		}
		
		//check south west tile
		
		if(southWestTile > 0 && southWestTile < 16384 && obstacleMap[southTile] && obstacleMap[westTile]){
			if(heuristicMap[southWestTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[southWestTile];
				nextTile = southWestTile;
			}
		}
		
		//check west tile
		if(westTile > 0 && westTile < 16384){
			if(heuristicMap[westTile] < smallestHeurstic){
				smallestHeurstic = heuristicMap[westTile]; 
				nextTile = westTile;
			}
		}
		
		return nextTile;
	}
	
	//move to a destination position,  ignore any hostile units it encounters 
	public void performMovementLogic(){
		
		//clear things a bit
		unStableObstacle = null;
		
		if(newDestinationisGiven){
			newDestinationisGiven = false;
			
			distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
			calculateMovement();
			destinationAngle = geometry.findAngle(centre.x, centre.z, destinationX, destinationY);
			immediateDestinationAngle = destinationAngle;	
		}
		
		if(Math.abs(bodyAngle - immediateDestinationAngle) > 45 && Math.abs(bodyAngle - immediateDestinationAngle) < 315){
			
			int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
			bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
			movement.reset();
			
		}else{
			if(bodyAngle != immediateDestinationAngle){
				int bodyAngleDelta = 360 - (geometry.findAngleDelta(bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360)%360;
				bodyAngle= (bodyAngle - bodyAngleDelta + 360)%360;
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
		
	public void avoidGettingStucked(){
		//if the object can't move for some period then recalculate the path
		if(movement.x == 0 && movement.z == 0){
			stuckCount++;
		}
		

		if(obstacle != null){
			if((unStableObstacle != null ||  !isStable(obstacle.owner)) && (ID + randomNumber + mainThread.frameIndex)%128 ==0){
				
				newDestinationisGiven = true;
				currentMovementStatus = freeToMove;
				hugWallCoolDown = 0;
				stuckCount = 0;
				randomNumber = gameData.getRandom();
			}
		}
		
		
		
		if(stuckCount > 128){
			newDestinationisGiven = true; 
			stuckCount = 0;
			currentMovementStatus = freeToMove;
			hugWallCoolDown = 0;
			
		}
	}
	
	public void draw(){
		if(!visible)
			return;
		
		for(int i = 0; i < bodyClone.length; i++){
			bodyClone[i].update();
			bodyClone[i].draw();
		}
		
		polygon3D[] drillClone;
		drillClone = null;		
		if(drillIndex == 0){
			drillClone = drillClone0;
		}
		if(drillIndex == 1){
			drillClone = drillClone1;
		}
		if(drillIndex == 2){
			drillClone = drillClone2;
		}
		
		for(int i = 0; i < drillClone.length; i++){
			drillClone[i].update();
			drillClone[i].draw();
		}
		
		for(int i = 0; i < cargoClone.length; i++){
			cargoClone[i].update();
			cargoClone[i].draw();
		}
		
		if(unloadingCount > 0){
			for(int i = 0; i < pillarsClone.length; i++){
				pillarsClone[i].update();
				pillarsClone[i].draw();
			}
		}
		
	}
	
	public vector getMovement(){
		return movement;
	}
	
	
	public void updateGeometry(){
		centre.y-=0.18f;
		
		for(int i = 0; i < bodyClone.length; i++){
			bodyClone[i].origin.set(body[i].origin);
			
			bodyClone[i].origin.rotate_XZ(360 - bodyAngle);
			bodyClone[i].origin.add(centre);
			
				
				
			bodyClone[i].bottomEnd.set(body[i].bottomEnd);
			
			bodyClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			bodyClone[i].bottomEnd.add(centre);
		
				
				
			bodyClone[i].rightEnd.set(body[i].rightEnd);
			bodyClone[i].rightEnd.rotate_XZ( 360 - bodyAngle);
			bodyClone[i].rightEnd.add(centre);
		
				
			
			
			for(int j = 0; j < bodyClone[i].vertex3D.length; j++){
				bodyClone[i].vertex3D[j].set(body[i].vertex3D[j]);
				bodyClone[i].vertex3D[j].rotate_XZ(360 -bodyAngle);
				bodyClone[i].vertex3D[j].add(centre);
				
				
				bodyClone[i].normal.set(body[i].normal);
				bodyClone[i].normal.rotate_XZ(360 -bodyAngle);
				bodyClone[i].findDiffuse();
			}
		}
		
		polygon3D[] drill, drillClone;
		drill = null;
		drillClone = null;		
		if(drillIndex == 0){
			drill = drill0;
			drillClone = drillClone0;
		}
		if(drillIndex == 1){
			drill = drill1;
			drillClone = drillClone1;
		}
		if(drillIndex == 2){
			drill = drill2;
			drillClone = drillClone2;
		}
		
		for(int i = 0; i < drillClone.length; i++){
			drillClone[i].origin.set(drill[i].origin);
			drillClone[i].origin.rotate_XZ(360 - bodyAngle);
			drillClone[i].origin.add(centre);
			
				
				
			drillClone[i].bottomEnd.set(drill[i].bottomEnd);
			
			drillClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			drillClone[i].bottomEnd.add(centre);
		
				
				
			drillClone[i].rightEnd.set(drill[i].rightEnd);
			drillClone[i].rightEnd.rotate_XZ( 360 - bodyAngle);
			drillClone[i].rightEnd.add(centre);
		
				
			
			
			for(int j = 0; j < drillClone[i].vertex3D.length; j++){
				drillClone[i].vertex3D[j].set(drill[i].vertex3D[j]);
				drillClone[i].vertex3D[j].rotate_XZ(360 -bodyAngle);
				drillClone[i].vertex3D[j].add(centre);
				
				drillClone[i].normal.set(drill[i].normal);
				drillClone[i].normal.rotate_XZ(360 -bodyAngle);
				drillClone[i].findDiffuse();
			}
		}
		
		
		//update cargo center
		cargoCenterClone.set(cargoCenter);
		cargoCenterClone.rotate_XZ(360 -bodyAngle);
		cargoCenterClone.add(centre);
		
		
		for(int i = 0; i < cargoClone.length; i++){
			cargoClone[i].origin.set(cargo[i].origin);
			cargoClone[i].origin.rotate_YZ(cargoAngle);
			cargoClone[i].origin.rotate_XZ(360 - bodyAngle);
			cargoClone[i].origin.add(cargoCenterClone);
			
			cargoClone[i].bottomEnd.set(cargo[i].bottomEnd);
			cargoClone[i].bottomEnd.rotate_YZ(cargoAngle);
			cargoClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			cargoClone[i].bottomEnd.add(cargoCenterClone);
		
				
				
			cargoClone[i].rightEnd.set(cargo[i].rightEnd);
			cargoClone[i].rightEnd.rotate_YZ(cargoAngle);
			cargoClone[i].rightEnd.rotate_XZ( 360 - bodyAngle);
			cargoClone[i].rightEnd.add(cargoCenterClone);
		
				
			
			
			for(int j = 0; j < cargoClone[i].vertex3D.length; j++){
				cargoClone[i].vertex3D[j].set(cargo[i].vertex3D[j]);
				cargoClone[i].vertex3D[j].rotate_YZ(cargoAngle);
				cargoClone[i].vertex3D[j].rotate_XZ(360 -bodyAngle);
				cargoClone[i].vertex3D[j].add(cargoCenterClone);
				
				
				cargoClone[i].normal.set(cargo[i].normal);
				cargoClone[i].normal.rotate_YZ(cargoAngle);
				cargoClone[i].normal.rotate_XZ(360 -bodyAngle);
				cargoClone[i].findDiffuse();
			}
		}
		
		if(unloadingCount > 0){
			//update pillars 
			pillarCenterClone.set(pillarCenter);
			pillarCenterClone.rotate_XZ(360 -bodyAngle);
			pillarCenterClone.add(centre);
			pillarAngle = (360- cargoAngle)/5*4;
			
			
			for(int i = 0; i < pillarsClone.length; i++){
				pillarsClone[i].origin.set(pillars[i].origin);
				pillarsClone[i].origin.rotate_YZ(pillarAngle);
				pillarsClone[i].origin.rotate_XZ(360 - bodyAngle);
				pillarsClone[i].origin.add(pillarCenterClone);
				
					
					
				pillarsClone[i].bottomEnd.set(pillars[i].bottomEnd);
				pillarsClone[i].bottomEnd.rotate_YZ(pillarAngle);
				pillarsClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
				pillarsClone[i].bottomEnd.add(pillarCenterClone);
			
					
					
				pillarsClone[i].rightEnd.set(pillars[i].rightEnd);
				pillarsClone[i].rightEnd.rotate_YZ(pillarAngle);
				pillarsClone[i].rightEnd.rotate_XZ( 360 - bodyAngle);
				pillarsClone[i].rightEnd.add(pillarCenterClone);
			
					
				
				
				for(int j = 0; j < pillarsClone[i].vertex3D.length; j++){
					pillarsClone[i].vertex3D[j].set(pillars[i].vertex3D[j]);
					pillarsClone[i].vertex3D[j].rotate_YZ(pillarAngle);
					pillarsClone[i].vertex3D[j].rotate_XZ(360 -bodyAngle);
					pillarsClone[i].vertex3D[j].add(pillarCenterClone);
					
					
					pillarsClone[i].normal.set(pillars[i].normal);
					pillarsClone[i].normal.rotate_YZ(pillarAngle);
					pillarsClone[i].normal.rotate_XZ(360 -bodyAngle);
					pillarsClone[i].findDiffuse();
				}
			}
		}
		
		centre.y+=0.18f;
	}
	
	public void resetLogicStatus(){
		movement.reset();
		currentMovementStatus = freeToMove;
		stuckCount = 0;
		destinationX = centre.x;
		destinationY = centre.z;
		insideDeistinationRadiusCount = 0;
		obstacle = null;
		drillingCount = 0;
		closeToDestination = false;
		
	}
	
	public void moveTo(float destinationX, float destinationY){		
		if(myRefinery != null){
			if(myRefinery.currentHP >0){
				if(jobStatus >3 )
					return;
			}
		}
		
		
		resetLogicStatus();
		pathIsFound = false;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		newDestinationisGiven = true;
		heuristicRecalculationCountDown = 0;
		jobStatus = idle;
			
	}
	
	
	public void harvest(solidObject o){      
		
	
		if(drillingCount > 0 && o == myGoldMine)
			return;
		
		if(cargoDeposite == 700){
			return;
		}
		
		if(jobStatus > 3){
			return;
		}
			
		myGoldMine = (goldMine)o;
		
		if(myGoldMine.goldDeposite == 0){
			myGoldMine = null;
			return;
		}
		
		resetLogicStatus();
		pathIsFound = false;
		newDestinationisGiven = true;
		heuristicRecalculationCountDown = 0;
		currentCommand = move;
		jobStatus = headingToMine;
		
		
		//set destination to one of the 8 adjacent tiles around the gold mine
		int goldMineTile = myGoldMine.tileIndex[0];
		miningPositions[0] = goldMineTile - 128;
		miningPositions[1] = goldMineTile - 127;
		miningPositions[2] = goldMineTile + 2;
		miningPositions[3] = goldMineTile + 130;
		miningPositions[4] = goldMineTile + 257;
		miningPositions[5] = goldMineTile + 256;
		miningPositions[6] = goldMineTile + 127;
		miningPositions[7] = goldMineTile -1;
												
		myMiningPosition = miningPositions[(int)((Math.random()*8))];
		int xPosition = myMiningPosition%128;
		int yPosition = 127 - myMiningPosition/128;
		this.destinationX = xPosition*0.25f +0.125f;
		this.destinationY = yPosition*0.25f +0.125f;
	}
	
	
	public void returnToRefinery(solidObject o){
		if(jobStatus == enteringRefinery || jobStatus == leavingRefinery || jobStatus == unloadingCargo || jobStatus == facingRight || jobStatus == facingDownward)
			return;
		
		if(cargoDeposite == 0){
			if(myGoldMine != null){
				harvest(myGoldMine);
			}
			return;
		}
		
		if(o == null){
			//find a nearest refinary
			myRefinery = findNearestRefinery();
			
		}else{
			myRefinery = (refinery)o;
			if(myRefinery.currentHP <=0){
				//find a nearest refinary
				myRefinery = findNearestRefinery();
			}
		}
		
		if(myRefinery != null){
			resetLogicStatus();
			pathIsFound = false;
			newDestinationisGiven = true;
			heuristicRecalculationCountDown = 0;
			currentCommand = move;
			jobStatus = returningToRefinery;
			
			myDropPosition = myRefinery.tileIndex[5] + 128;
			int xPosition = myDropPosition%128;
			int yPosition = 127 - myDropPosition/128;
			this.destinationX = xPosition*0.25f +0.125f;
			this.destinationY = yPosition*0.25f +0.125f;
			
			int insdieRefinery =  myRefinery.tileIndex[5];
			xPosition = insdieRefinery%128;
			yPosition = 127 - insdieRefinery/128;
			
			insideRefineryPositionX = xPosition*0.25f +0.125f;;
			insideRefineryPositionY = yPosition*0.25f +0.125f;
			
		}else{
			currentCommand = StandBy;
		}
	}
	
	public refinery findNearestRefinery(){
		refinery[] refineries = (refinery[])mainThread.theAssetManager.refineries;
		
		for(int i = 1; i < refineries.length; i++){
			for(int j = 0; j <refineries.length - i; j++){
				if(refineries[j] == null){
					refinery temp = refineries[j+1];
					refineries[j+1] = refineries[j];
					refineries[j] = temp;
				}else if(refineries[j +1] != null){
					float d1 = Math.abs(refineries[j].centre.x - centre.x) + Math.abs(refineries[j].centre.z - centre.z);
					float d2 = Math.abs(refineries[j + 1].centre.x - centre.x) + Math.abs(refineries[j+1].centre.z - centre.z);
					if(d1 > d2){
						refinery temp = refineries[j+1];
						refineries[j+1] = refineries[j];
						refineries[j] = temp;
					}
				}
			}
		}
		
		refinery nearestBusyRefinery = null;
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] != null){
				if(!refineries[i].isBusy && !refineries[i].droppingAreaIsFull(this) && refineries[i].currentHP >0 && (refineries[i].teamNo == teamNo)){	
					if(checkDistance(refineries[i]) > 4){
						if(nearestBusyRefinery == null)
							return refineries[i];
					}else{
						return refineries[i];
					}
				}
				if(nearestBusyRefinery == null  && (refineries[i].isBusy || refineries[i].droppingAreaIsFull(this)) && refineries[i].currentHP >0 && (refineries[i].teamNo == teamNo)){
					nearestBusyRefinery = refineries[i];
				}
			}
			
		}
			
		if(nearestBusyRefinery != null){
			waitingCount = 15;
			return nearestBusyRefinery;
		}
		
		return null;
	}
	
	public void goToTheNearestGoldMine(){
		int goldMineIndex = -1;
		double distance = 10;
		for(int i = 0; i < mainThread.theAssetManager.goldMines.length; i++){
			if(mainThread.theAssetManager.goldMines[i] == null)
				continue;
			
			double newDistance = getDistance(mainThread.theAssetManager.goldMines[i]);
			if(newDistance < distance  && mainThread.theAssetManager.goldMines[i].goldDeposite > 1){
				distance = newDistance;
				goldMineIndex = i;
			}
		}
		
		if(goldMineIndex != -1){
			myGoldMine = mainThread.theAssetManager.goldMines[goldMineIndex];
			//waitingCount = 15;
			returnToRefinery(null);
		}
	}
	
	public float checkDistance(solidObject o){
		return Math.abs(o.centre.x - centre.x) + Math.abs(o.centre.z - centre.z);
	}
	
	public void hold(){
		if(jobStatus < 4)
			currentCommand = StandBy;
	}
	
	public int getMaxHp(){return maxHP;}
	
}
