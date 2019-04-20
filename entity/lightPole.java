package entity;

import java.awt.Rectangle;

import core.*;

//palmTree model 
public class lightPole extends solidObject{
	//the polygons of the model
	public   polygon3D[] polygons; 
	
	public int angle;
	
	public int tileIndex;
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-50,-50,868, 612);
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432);
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(0,0,768, 512);
	
	//lightPole never moves
	public final static vector movenment = new vector(0,0,0);
	
	public boolean vanished;
	
	

	public lightPole(float x, float y, float z, int angle){
		//uncontrollable unit, but act as a small sized static collidable agent
		ID = -1;
		type = 100;
		teamNo = -1;
		
		this.angle = angle;
	
		boundary2D = new Rect((int)(x*64), (int)(z*64), 1, 1);
		
		tileIndex = boundary2D.x1/16 + (127 - (boundary2D.y1)/16)*128;
		if(tileIndex >= 0 && tileIndex < 128*128)
			mainThread.gridMap.tiles[tileIndex][4] = this;
		else
			tileIndex = 0;
		
		start = new vector(x,y,z);
		centre = start.myClone();
		tempCentre = start.myClone();
		
		shadowvertex0 =start.myClone();
		shadowvertex0.add(-0.35f,-0.2f, -0.35f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.35f,-0.2f, 0);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0,-0.2f, -0.35f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0,-0.2f, 0f);
		tempshadowvertex3 = new vector(0,0,0);
		
		//create main axis in object space
		iDirection = new vector(1f,0,0); 
		jDirection = new vector(0,1f,0);
		kDirection = new vector(0,0,1f);
		
		boundary2D.owner = this;
		currentCommand = StandBy;
		
		makePolygons();
	}
	
	
	
	//Construct polygons for this model.
	//The polygon data is hard-coded here
	private void makePolygons(){
	
		polygons = new polygon3D[57];
		vector[] v;
		
		//power tower A
		float r = 0.008f;
		float r1 = 0.006f;
		float delta = (float)Math.PI/8;
				
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r1*Math.cos(i*delta), 0.4, r1*Math.sin(i*delta)),
							put(r1*Math.cos((i+1)*delta), 0.4, r1*Math.sin((i+1)*delta)),
								put(r*Math.cos((i+1)*delta), 0,  r*Math.sin((i+1)*delta)),
							 put(r*Math.cos(i*delta), 0, r*Math.sin(i*delta))
								};
			polygons[i] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		}
		
		iDirection.rotate_YZ(30);
		jDirection.rotate_YZ(30);
		kDirection.rotate_YZ(30);
		
		start.z-=0.221f;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r1*Math.cos(i*delta), 0.502, r1*Math.sin(i*delta)),
							put(r1*Math.cos((i+1)*delta), 0.502, r1*Math.sin((i+1)*delta)),
								put(r1*Math.cos((i+1)*delta), 0.449,  r1*Math.sin((i+1)*delta)),
							 put(r1*Math.cos(i*delta), 0.449, r1*Math.sin(i*delta))
								};
			polygons[i+16] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		}
	
		
		iDirection.rotate_YZ(30);
		jDirection.rotate_YZ(30);
		kDirection.rotate_YZ(30);
		
		start.z-=0.14f;
		start.y+=0.2f;
		float r2 = 0.004f;
		for(int i = 0; i < 16; i++){
			v = new vector[]{put(r2*Math.cos(i*delta), 0.55, r2*Math.sin(i*delta)),
							put(r2*Math.cos((i+1)*delta), 0.55, r2*Math.sin((i+1)*delta)),
								put(r1*Math.cos((i+1)*delta), 0.45,  r1*Math.sin((i+1)*delta)),
							 put(r1*Math.cos(i*delta), 0.45, r1*Math.sin(i*delta))
								};
			polygons[i+32] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		}
		
		
		start.set(centre);
		start.z+=0.1f;
		start.y -=0.02f;
		iDirection.set(1f,0,0); 
		jDirection.set(0,1f,0);
		kDirection.set(0,0,1f);
		
		float h = 0.5f;
		
		float w1 = -0.01f*0.9f;
		float w2 = -0.005f*0.9f;
		float w3 = 0.005f*0.9f;
		float w4 = 0.01f*0.9f;
		
		float h1 = 0.04f*1f;
		float h2 = 0.035f*1f;
		float h3 = 0.015f*1f;
		float h4 = 0.01f*1f;
				
		float thickness = 0.01f;
		
		v = new vector[]{put(w2,h, h1), put(w3,h,h1), put(w4,h, h2), put(w4,h, h3), put(w3,h, h4), put(w2,h, h4), put(w1,h, h3), put(w1,h, h2)};
		polygons[48] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		
		v = new vector[]{put(w2, h, h4), put(w3, h, h4), put(w3, h-thickness, h4), put(w2, h-thickness, h4)};
		polygons[49] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w2, h-thickness, h1), put(w3, h-thickness, h1), put(w3, h, h1), put(w2, h, h1)};
		polygons[50] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		
		v = new vector[]{put(w3, h, h4), put(w4, h, h3), put(w4, h-thickness, h3), put(w3, h-thickness, h4) };
		polygons[51] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w1, h, h3), put(w2, h, h4), put(w2, h-thickness, h4), put(w1, h-thickness, h3) };
		polygons[52] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w4, h, h3), put(w4, h , h2), put(w4, h-thickness , h2), put(w4, h-thickness, h3)};
		polygons[53] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w1, h-thickness, h3), put(w1, h-thickness , h2), put(w1, h , h2), put(w1, h, h3)};
		polygons[54] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w4,h,h2), put(w3, h, h1),put(w3, h-thickness, h1), put(w4,h-thickness,h2)};
		polygons[55] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		v = new vector[]{put(w2, h, h1), put(w1,h,h2), put(w1,h-thickness,h2), put(w2, h-thickness, h1)};
		polygons[56] = new polygon3D(v, v[0], v[1], v[3],  mainThread.textures[25], 10f,10f,1);
		
		for(int i = 0; i < polygons.length; i++){
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].subtract(centre);
				polygons[i].vertex3D[j].rotate_XZ(angle);
				polygons[i].vertex3D[j].add(centre);
			}
			polygons[i].normal.rotate_XZ(angle);
			polygons[i].findDiffuse();
			
		
			polygons[i].parentObject = this;
		}
		
		
		
	}
	
	//update the model 
	public void update(){
		if(vanished)
			return; 
		
		mainThread.gridMap.currentObstacleMap[tileIndex] = false;
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		
		if(tempCentre.screenX > 918 || tempCentre.screenX < - 150 || tempCentre.screenY < - 150 || tempCentre.screenY > 662){
			visible = false;
			return;
		}
			
		//test if the light pole is visible in camera point of view
		if(visibleBoundary.contains(tempCentre.screenX, tempCentre.screenY)){
			visible = true;
			
			if(screenBoundary.contains(tempCentre.screenX, tempCentre.screenY))
				withinViewScreen = true;
			else
				withinViewScreen = false;
			
			
		}else{
			visible = false;
		}
		
		
		
		
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
		
	}
	
	public void vanish(){
		mainThread.gridMap.tiles[tileIndex][4] = null;
		mainThread.gridMap.currentObstacleMap[tileIndex] = true;
		vanished = true;
	}
	
	
	public vector getMovement(){
		return movenment;
	}
	
	//draw model
	public void draw(){
		
		if(!visible || vanished)
			return;
		for(int i = 0; i < polygons.length; i++){
			polygons[i].update();
		}
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].draw();
		}
	}

}
