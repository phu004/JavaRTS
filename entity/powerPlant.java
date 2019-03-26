package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.enemyCommander;

//the power plant model
public class powerPlant extends solidObject{
	
	//the polygons of the model
	private polygon3D[] polygons; 
	
	public static int maxHP = 400;
	
	public int countDownToDeath = 16;
	
	public vector tempVector = new vector(0,0,0);
	public vector tempVector0 = new vector(0,0,0);
	public vector tempVector1 = new vector(0,0,0);
	public vector tempVector2 = new vector(0,0,0);
	public vector tempVector3 = new vector(0,0,0);
	
	public int [] tileIndex = new int[9];
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
	
	//power plant never moves
	public final static vector movenment = new vector(0,0,0);
	
	public baseInfo theBaseInfo;
	
	public static int intendedDeployLocation = -1;
	
	public powerPlant(float x, float y, float z,  int teamNo){
		//uncontrollable unit, but act as a big sized static collidable agent
		type = 101;
		
		if(teamNo == 0){
			isRevealed = true;
			theBaseInfo = mainThread.pc.theBaseInfo;
		}else{
			theBaseInfo = mainThread.ec.theBaseInfo;
		}
		
		theBaseInfo.numberOfPowerPlant++;
		
		ID = globalUniqID++;
		
		currentHP = 400;
		
		this.teamNo = teamNo;
			
		currentCommand = this.StandBy;
		
		if(teamNo == 0){
			isRevealed = true;
		}
		
		if(bitmapVisionForEnemy == null){
			bitmapVisionForEnemy = createBitmapVision(8);
		}
		
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 16, (int)(z*64) + 16, 32, 32);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		tileIndex[0] = (centerX - 8)/16 + (127 - (centerY + 8)/16)*128; 
		tileIndex[1] = (centerX + 8)/16 + (127 - (centerY + 8)/16)*128;
		tileIndex[2] = (centerX + 8)/16 + (127 - (centerY - 8)/16)*128;
		tileIndex[3] = (centerX - 8)/16 + (127 - (centerY - 8)/16)*128;
		
		mainThread.gridMap.tiles[tileIndex[0]][0] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][0] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][0] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][1] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][1] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][1] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][2] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][2] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][2] = this; 
		
		mainThread.gridMap.tiles[tileIndex[0]][3] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][3] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][3] = this; 

		mainThread.gridMap.tiles[tileIndex[0]][4] = this;  
		mainThread.gridMap.tiles[tileIndex[1]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[2]][4] = this; 
		mainThread.gridMap.tiles[tileIndex[3]][4] = this; 
		
		//the size of the building is larger for AI, to prevent it from building everything close together
		if(teamNo != 0){
			tileIndex[4] = tileIndex[1] - 128; 
			tileIndex[5] = tileIndex[1] - 130;
			tileIndex[6] = tileIndex[1] + 256;
			tileIndex[7] = tileIndex[1] + 254;
			tileIndex[8] = tileIndex[1] + 126;
			
			mainThread.gridMap.tiles[tileIndex[4]][4] = this;  
			mainThread.gridMap.tiles[tileIndex[5]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[6]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[7]][4] = this; 
			mainThread.gridMap.tiles[tileIndex[8]][4] = this; 
		}
		
		

		//init model
		x-=0.03f;
		start = new vector(x,y,z);
		iDirection = new vector(1.2f,0,0);
		jDirection = new vector(0,1.2f,0);
		kDirection = new vector(0,0,1.2f);
		
		//adjust orientation of the model
		iDirection.rotate_XZ(255);
		kDirection.rotate_XZ(255);
		
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
		polygons = new polygon3D[36+36+2+32+32 + 32 + 1];	
		vector[] v;
		
		int index = 0;
		
		double theta = Math.PI/16;
		
		double r = 0.12;
		
		for(int i = 0; i < 18; i++){
			v = new vector[]{put(r*Math.cos((i+1)*theta), 0.18, r*Math.sin((i+1)*theta)),
							 put(r*Math.cos(i*theta), 0.18, r*Math.sin(i*theta)),
							 put(r*Math.cos(i*theta), 0, r*Math.sin(i*theta)),
							 put(r*Math.cos((i+1)*theta), 0,  r*Math.sin((i+1)*theta))
							
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
			
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[13], 0.5f,1, 1);
		}
		
		index+=18;
		
		v = new vector[]{put(0.12*Math.cos(0*theta), 0.18, 0.12*Math.sin(0*theta)), 
				 put(0.17*Math.cos(0*theta), 0.16, 0.17*Math.sin(0*theta)), 
				 put(0.2*Math.cos(0*theta), 0.13, 0.2*Math.sin(0*theta)), 
				 put(0.2*Math.cos(0*theta), 0, 0.2*Math.sin(0*theta)), 
				 put(0.12*Math.cos(0*theta), 0, 0.12*Math.sin(0*theta)), 
				};
		polygons[index] = new polygon3D(v, v[0].myClone(), put(0.2*Math.cos(0*theta), 0.18, 0.2*Math.sin(0*theta)), v[4].myClone(), mainThread.textures[13], 0.5f, 1, 1);

		index+=1;

		v = new vector[]{ put(0.12*Math.cos(18*theta), 0, 0.12*Math.sin(18*theta)), 
				  put(0.2*Math.cos(18*theta), 0, 0.2*Math.sin(18*theta)), 
				  put(0.2*Math.cos(18*theta), 0.13, 0.2*Math.sin(18*theta)), 
				  put(0.17*Math.cos(18*theta), 0.16, 0.17*Math.sin(18*theta)), 
				  put(0.12*Math.cos(18*theta), 0.18, 0.12*Math.sin(18*theta))
		};
		polygons[index] = new polygon3D(v, put(0.2*Math.cos(18*theta), 0.18, 0.2*Math.sin(18*theta)), v[4].myClone(), v[1].myClone(), mainThread.textures[13], 0.5f, 1, 1);
		index+=1;
		
		
		
		double delta = Math.PI/8;
		start.add(-0.05f,0,0);
		r = 0.085;
		double r2 = 0.06;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.18, r2*Math.sin(i*delta)),
							 put(r2*Math.cos((i+1)*delta), 0.18, r2*Math.sin((i+1)*delta)),
							 put(r*Math.cos((i+1)*delta), 0,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0, r*Math.sin(i*delta))
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
			
		    change(0,0.18f,0, tempVector);
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,0.25f,1);
			polygons[i + index].textureScaledWidth = (int)(polygons[i + index].myTexture.width*0.5);
			polygons[i + index].createShadeSpan(tempVector, v[0], v[1]);
		}
		start.add(0.05f,0,-0);
		index+=16;
		
		
		
		r = 0.2;
		
		for(int i = 0; i < 18; i++){
			v = new vector[]{put(r*Math.cos(i*theta), 0.13, r*Math.sin(i*theta)),
							 put(r*Math.cos((i+1)*theta), 0.13, r*Math.sin((i+1)*theta)),
							 put(r*Math.cos((i+1)*theta), 0,  r*Math.sin((i+1)*theta)),
							 put(r*Math.cos(i*theta), 0, r*Math.sin(i*theta))
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
			
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[13], 0.5f,1,1);
		}
		
		index += 18;
		
		r2 = 0.17;
		
		for(int i = 0; i < 18; i++){
			v = new vector[]{put(r2*Math.cos(i*theta), 0.16, r2*Math.sin(i*theta)),
							 put(r2*Math.cos((i+1)*theta), 0.16, r2*Math.sin((i+1)*theta)),
							 put(r*Math.cos((i+1)*theta), 0.13,  r*Math.sin((i+1)*theta)),
							 put(r*Math.cos(i*theta), 0.13, r*Math.sin(i*theta))
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
			
			if(teamNo == 0)
				polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[15], 0.5f,1,1);
			else
				polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[16], 0.5f,1,1);
		}
		
		index += 18;
	
		r = 0.12;
		
		for(int i = 0; i < 18; i++){
			v = new vector[]{put(r*Math.cos(i*theta), 0.18, r*Math.sin(i*theta)),
							 put(r*Math.cos((i+1)*theta), 0.18, r*Math.sin((i+1)*theta)),
							 put(r2*Math.cos((i+1)*theta), 0.16,  r2*Math.sin((i+1)*theta)),
							 put(r2*Math.cos(i*theta), 0.16, r2*Math.sin(i*theta))
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
			
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[14], 0.5f,0.4f,1);
		}
		
		index +=18;
		
		start.add(-0.05f,0,0);
		r = 0.05;
		r2 = 0.05;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					 put(r*Math.cos(i*delta), 0.18, r*Math.sin(i*delta)),
					 put(r*Math.cos((i+1)*delta), 0.18,  r*Math.sin((i+1)*delta)),
					 put(r2*Math.cos((i+1)*delta), 0.38, r2*Math.sin((i+1)*delta)),
					 put(r2*Math.cos(i*delta), 0.38, r2*Math.sin(i*delta))
							
							
							
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
			
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 0.5f,1,1);
		}
		
		index+=16;
		double h = 0.31;
		double r3 = 0.056;
		v = new vector[16];
		for(int i = 0; i <16; i++){
			v[15 - i] = put(r3*Math.cos(i*delta), h, r3*Math.sin(i*delta));
		}
		polygons[index] = new polygon3D(v, put(-r3, h, r3), put(r3, h, r3), put(-r3, h, -r3), mainThread.textures[17], 1f,1,1);
		polygons[index].shadowBias = 15000;
		index++;
		
		start.add(0.05f,0,-0);
		
		start.add(-0.05f,0,0);
		r = 0.06;
		r2 = 0.058;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.26, r2*Math.sin(i*delta)),
							 put(r2*Math.cos((i+1)*delta), 0.26, r2*Math.sin((i+1)*delta)),
							 put(r*Math.cos((i+1)*delta), 0.18,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0.18, r*Math.sin(i*delta))
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
			
		    change(0,0.26f,0, tempVector);
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,0.25f,1);
			polygons[i + index].textureScaledWidth = (int)(polygons[i + index].myTexture.width*0.5);
			polygons[i + index].createShadeSpan(tempVector, v[0], v[1]);
		}
		start.add(0.05f,0,-0);
		index+=16;
		
		
		
		
		
		
		start.add(-0.05f,0,0);
		r = 0.058;
		r2 = 0.059;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.32, r2*Math.sin(i*delta)),
							 put(r2*Math.cos((i+1)*delta), 0.32, r2*Math.sin((i+1)*delta)),
							 put(r*Math.cos((i+1)*delta), 0.26,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0.26, r*Math.sin(i*delta))
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
			
		    change(0,0.32f,0, tempVector);
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,0.25f,1);
			polygons[i + index].textureScaledWidth = (int)(polygons[i + index].myTexture.width*0.5);
			polygons[i + index].createShadeSpan(tempVector, v[0], v[1]);
		}
		start.add(0.05f,0,-0);
		index+=16;
		
		
		
		
		start.add(-0.05f,0,0);
		r = 0.059;
		r2 = 0.06;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.38, r2*Math.sin(i*delta)),
							 put(r2*Math.cos((i+1)*delta), 0.38, r2*Math.sin((i+1)*delta)),
							 put(r*Math.cos((i+1)*delta), 0.32,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0.32, r*Math.sin(i*delta))
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
			
		    change(0,0.38f,0, tempVector);
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 1f,0.25f,1);
			polygons[i + index].textureScaledWidth = (int)(polygons[i + index].myTexture.width*0.5);
			polygons[i + index].createShadeSpan(tempVector, v[0], v[1]);
			
		}
		start.add(0.05f,0,-0);
		index+=16;
		
		start.add(-0.05f,0,0);
		r = 0.05;
		r2 = 0.06;
		for(int i = 0; i < 16; i++){
			v = new vector[]{
					 put(r*Math.cos(i*delta), 0.38, r*Math.sin(i*delta)),
					 put(r*Math.cos((i+1)*delta), 0.38,  r*Math.sin((i+1)*delta)),		
					 put(r2*Math.cos((i+1)*delta), 0.38, r2*Math.sin((i+1)*delta)),
					 put(r2*Math.cos(i*delta), 0.38, r2*Math.sin(i*delta))
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
			
			polygons[i + index] = new polygon3D(v, tempVector0.myClone(),tempVector1.myClone(), tempVector3.myClone(),  mainThread.textures[12], 0.5f,0.25f,1);
		}
		start.add(0.05f,0,-0);
		index+=16;
		
		
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
			
		}
	}
	
	
	//update the model 
	public void update(){

		//process emerging from  ground animation
		if(centre.y < -0.5f){
			centre.y+=0.02f;
			
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
					polygons[i].origin.y+=0.02;
					polygons[i].rightEnd.y+=0.02;
					polygons[i].bottomEnd.y+=0.02;
					
					for(int j = 0; j < polygons[i].vertex3D.length; j++){
						polygons[i].vertex3D[j].y+=0.02;
					}
					
					
				}
				shadowvertex0.y+=0.02;
				shadowvertex1.y+=0.02;
				shadowvertex2.y+=0.02;
				shadowvertex3.y+=0.02;
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
				tempFloat[3] = 3.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 7;
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				theAssetManager.removeObject(this); 
				
				
				theBaseInfo.numberOfPowerPlant--;
			
				
				
				//removeFromGridMap();
				mainThread.gridMap.tiles[tileIndex[0]][0] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][0] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][0] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][1] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][1] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][1] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][2] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][2] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][2] = null; 
				
				mainThread.gridMap.tiles[tileIndex[0]][3] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][3] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][3] = null; 

				mainThread.gridMap.tiles[tileIndex[0]][4] = null;  
				mainThread.gridMap.tiles[tileIndex[1]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[2]][4] = null; 
				mainThread.gridMap.tiles[tileIndex[3]][4] = null; 
				
				if(teamNo != 0){
					mainThread.gridMap.tiles[tileIndex[4]][4] = null;  
					mainThread.gridMap.tiles[tileIndex[5]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[6]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[7]][4] = null; 
					mainThread.gridMap.tiles[tileIndex[8]][4] = null; 
				}
				
				if(attacker.teamNo != teamNo)
					attacker.experience+=25;
				return;
			}else{
				
				float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
				tempFloat[0] = centre.x + (float)Math.random()/2.5f - 0.2f;
				tempFloat[1] = centre.y + 0.15f;
				tempFloat[2] = centre.z + (float)Math.random()/2.5f - 0.2f;
				tempFloat[3] = 1.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 0;
				tempFloat[6] = 6 + (gameData.getRandom()%4);
				tempFloat[7] = this.height;
				theAssetManager.explosionCount++; 
				
				
			}
		}
		
		//processing repair event
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
		
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
			
		theAssetManager = mainThread.theAssetManager;
		
		//test if the palm tree is visible in camera point of view
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
			int xPos = boundary2D.x1/16 - 8 + 10;
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
		   theAssetManager.minimapBitmap[tileIndex[3]] )
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
			if((mainThread.gameFrame + ID) % 5 ==0 && centre.y >= -0.5f){
				float[] tempFloat = theAssetManager.smokeEmmiterList[theAssetManager.smokeEmmiterCount];
				tempFloat[0] = centre.x - 0.053f + (float)(Math.random()/20) - 0.025f;
				tempFloat[1] = centre.y + 0.45f;
				tempFloat[2] = centre.z + (float)(Math.random()/20) - 0.025f;
				tempFloat[3] = 1.5f;
				tempFloat[4] = 1;
				tempFloat[5] = 11;
				tempFloat[6] = this.height;
				theAssetManager.smokeEmmiterCount++;
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
	
	public vector getMovement(){
		return movenment;
	}
}
