package entity;

import java.awt.Rectangle;

import core.*;

//palmTree model 
public class palmTree extends solidObject{
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
	public final static Rectangle shadowBoundary1 = new Rectangle(-20, -20,808, 552);
	
	//palmTrees never moves
	public final static vector movenment = new vector(0,0,0);
	
	
	//angles between leave branch
	public static int[] angles = new int[5];
	
	public palmTree(float x, float y, float z){
		//uncontrollable unit, but act as a small sized static collidable agent
		ID = -1;
		
		type = 100;
		
	
		boundary2D = new Rect((int)(x*64), (int)(z*64), 1, 1);
		
		tileIndex = boundary2D.x1/16 + (127 - (boundary2D.y1)/16)*128;
		
		if(tileIndex >= 0 && tileIndex < 128*128)
			mainThread.gridMap.tiles[tileIndex][4] = this;
		else
			tileIndex = 0;
		

		this.angle = (int)(360*Math.random());
		
		x = x+0.05f - (float)(Math.random()/10);
		y = y+0.05f - (float)(Math.random()/10);
		z = z+0.05f - (float)(Math.random()/10);
		
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
		
		float scale_i  = (float)(Math.random() * 0.3) - 0.15f;
		float scale_j  = (float)(Math.random() * 0.3) - 0.15f;
		float scale_k  = (float)(Math.random() * 0.3) - 0.15f;
		
		float scale_j_x =  (float)(Math.random() * 0.3) - 0.15f;
		float scale_j_y =  (float)(Math.random() * 0.3) - 0.15f;
		
		iDirection = new vector(0.7f+0.3f*1.1f + scale_i,0,0);
		jDirection = new vector(scale_j_x,(1.1f+0.3f) + scale_j ,scale_j_y);
		kDirection = new vector(0,0,0.7f+0.3f*1.1f + scale_k);
		
		//adjust orientation of the model
		iDirection.rotate_XZ(angle);
		kDirection.rotate_XZ(angle);	
		
		int color = 110 << 16 | 205 << 8 | 10;
		
		postProcessingThread.theMiniMap.background[tileIndex] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 1] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 128] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 129] = color;
		
		boundary2D.owner = this;
		currentCommand = StandBy;
		angles[0] = 0;
		makePolygons();
	}
	
	public palmTree(float x, float y, float z, float scale_i, float scale_j, float scale_k, float scale_j_x, float scale_j_y, int angle, int angle1, int angle2, int angle3, int angle4, int angle5){
		//uncontrollable unit, but act as a small sized static collidable agent
		type = 100;
		boundary2D = new Rect((int)(x*64), (int)(z*64), 1, 1);
				
		tileIndex = boundary2D.x1/16 + (127 - (boundary2D.y1)/16)*128;
		mainThread.gridMap.tiles[tileIndex][4] = this;
		
		start = new vector(x,y,z);
		centre = start.myClone();
		tempCentre = start.myClone();
		
		shadowvertex0 =start.myClone();
		shadowvertex0.add(-0.75f,-0.2f, -0.95f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.75f,-0.2f, 0);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0,-0.2f, -0.95f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0,-0.2f, 0f);
		tempshadowvertex3 = new vector(0,0,0);
		
		iDirection = new vector(0.7f+0.3f*1.1f + scale_i,0,0);
		jDirection = new vector(scale_j_x,(1.1f+0.3f) + scale_j ,scale_j_y);
		kDirection = new vector(0,0,0.7f+0.3f*1.1f + scale_k);
		
		iDirection.rotate_XZ(angle);
		kDirection.rotate_XZ(angle);
		
		int color = 110 << 16 | 205 << 8 | 10;
		
		postProcessingThread.theMiniMap.background[tileIndex] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 1] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 128] = color;
		postProcessingThread.theMiniMap.background[tileIndex + 129] = color;
		
		boundary2D.owner = this;
		currentCommand = StandBy;
		angles[0] = angle1;
		angles[1] = angle2;
		angles[2] = angle3;
		angles[3] = angle4;
		angles[4] = angle5;
		
		makePolygons();
		
	}
	
	//Construct polygons for this model.
	//The polygon data is hard-coded here
	private void makePolygons(){
		
		
		vector[] v;
		
		start.add(0,-0.25f,0);
		
		polygons = new polygon3D[8 + 5*6]; 
		
		if(angles[0] ==0){
			for(int i = 0; i < 5; i++){
				angles[i] = 72*(i+1) + 15 - (int)(30*Math.random());

			}
		}
		
	
		//body
		v = new vector[]{put(-0.001, 0.1, -0.01), put(0.016, 0.1, -0.01), put(0.01, 0, -0.01), put(-0.014, 0, -0.01)};
		polygons[0] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(-0.001, 0.1, 0.01), put(-0.001, 0.1, -0.01), put(-0.014, 0, -0.01), put(-0.014, 0, 0.014)};
		polygons[1] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.016, 0.1, 0.01), put(-0.001, 0.1, 0.01), put(-0.014, 0, 0.014), put(0.01, 0, 0.014)};
		polygons[2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.016, 0.1, -0.01), put(0.016, 0.1, 0.01), put(0.01, 0, 0.014), put(0.01, 0, -0.01)};
		polygons[3] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.002, 0.3, -0.008), put(0.013, 0.3, -0.008), put(0.016, 0.1, -0.01), put(-0.001, 0.1, -0.01)};
		polygons[4] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.002, 0.3, 0.006), put(0.002, 0.3, -0.008), put(-0.001, 0.1, -0.01),put(-0.001, 0.1, 0.01)};
		polygons[5] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.013, 0.3, 0.006), put(0.002, 0.3, 0.006), put(-0.001, 0.1, 0.01),put(0.016, 0.1, 0.01)};
		polygons[6] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		v = new vector[]{put(0.013, 0.3, -0.008), put(0.013, 0.3, 0.006), put(0.016, 0.1, 0.01), put(0.016, 0.1, -0.01)};
		polygons[7] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[4], 0.1f,0.5f,1);
		
		//leaves
		start.add(0.005f, 0,0);
		int index = 8;
		int currentAngle = 0;
		for(int i = 0; i < 5; i++){
			
			v = new vector[]{put(0.015, 0.3, 0.01), put(0, 0.3, 0), put(0, 0.34, 0.05), put(0.015, 0.32, 0.05)};
			polygons[index] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[5], 1,1,1);
			
			v = new vector[]{put(0, 0.3, 0), put(-0.015, 0.3, 0.01), put(-0.015, 0.32, 0.05), put(0, 0.34, 0.05)};
			polygons[index+1] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[5], 1,1,1);
			
			v = new vector[]{put(0, 0.34, 0.05), put(0, 0.33, 0.09), put(0.015, 0.31, 0.09), put(0.015, 0.32, 0.05)};
			polygons[index + 2] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[5], 1,1,1);
		
			v = new vector[]{put(0, 0.34, 0.05), put(-0.015, 0.32, 0.05), put(-0.015, 0.31, 0.09), put(0, 0.33, 0.09)};
			polygons[index + 3] = new polygon3D(v, v[0], v[1], v [3], mainThread.textures[5], 1,1,1);
			
			v = new vector[]{put(0, 0.33, 0.09), put(-0.015, 0.31, 0.09), put(0, 0.29, 0.12)};
			polygons[index + 4] = new polygon3D(v, v[0], v[1], v [2], mainThread.textures[5], 1,1,1);
			
			v = new vector[]{put(0.015, 0.31, 0.09), put(0, 0.33, 0.09), put(0, 0.29, 0.12)};
			polygons[index + 5] = new polygon3D(v, v[0], v[1], v [2], mainThread.textures[5], 1,1,1);
			
			iDirection.rotate_XZ(angles[i]-currentAngle);
			kDirection.rotate_XZ(angles[i]-currentAngle);
			
			currentAngle = angles[i];
			
			index+=6;
		}
		
		
		
		
		
		for(int i = 0; i < polygons.length; i++){
			polygons[i].Ambient_I+=10;
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
		}
		
	}
	
	//update the model 
	public void update(){
		mainThread.gridMap.currentObstacleMap[tileIndex] = false;
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		
		if(tempCentre.screenX > 918 || tempCentre.screenX < - 50 || tempCentre.screenY < - 125 || tempCentre.screenY > 662){
			visible = false;
			return;
		}
			
		//test if the palm tree is visible in camera point of view
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
	
	
	
	
	public vector getMovement(){
		return movenment;
	}
	
	//draw model
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

}
