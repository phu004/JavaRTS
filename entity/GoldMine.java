package entity;

import java.awt.Rectangle;

import core.*;

public class GoldMine extends SolidObject {
	//the polygons of the model
	private polygon3D[] polygons; 
	
	//the amount of gold available
	public int goldDeposite;
	public int maxDeposite;
	
	public int textureIndex;
	
	public static int maxHP = 9999;
	
	public static vector tempVector0 = new vector(0,0,0);
	public static vector tempVector1 = new vector(0,0,0);
	public static vector tempVector2 = new vector(0,0,0);
	public static vector tempVector3 = new vector(0,0,0);
	
	public static vector origin = new vector(0,0,0);
	public static vector top =  new vector(0,0,0);
	public static vector bot = new vector(0,0,0);
	public static vector deltaX = new vector(0,0,0);
	public static vector deltaZ = new vector(0,0,0);
	
	
	//gold mine occupies 4 tiles
	public int [] tileIndex = new int[6];
	
	
	public vector shadowvertex0, tempshadowvertex0,shadowvertex1, tempshadowvertex1,shadowvertex2, tempshadowvertex2,shadowvertex3, tempshadowvertex3;
	
	//a screen space boundary which is used to test if the  object is visible from Camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-85,-85,screen_width+152, screen_height+250);  
		
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(60,60,screen_width-120, screen_height-110);  
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(40,40,screen_width-40, screen_height-40);  
	
	//a screen space boundary which is used to test if the vision polygon of the  object is visible.
	//public final static Rectangle visionBoundary = new Rectangle(0,0,1600, 2000);
	
	//gold mine never moves
	public final static vector movenment = new vector(0,0,0);
	
	public int polygonCount;
	
	public GoldMine(float x, float y, float z, int amount){
		
		goldDeposite =amount;
		maxDeposite = amount;		
		
		//uncontrollable unit, but act as a big sized static collidable agent
		ID = -1;
		type = 103;
		teamNo = -1;
		currentHP = 9999;
		progressStatus = 100;
		textureIndex = 39;
		
		currentCommand = StandBy;
			
		start = new vector(x,y,z);
		
		if(start.y < -0.515f)
			isSelectable = false;

		
		//create 2D boundary
		boundary2D = new Rect((int)(x*64) - 16, (int)(z*64) + 16, 32, 32);  
		boundary2D.owner = this;
		int centerX = (int)(x*64);
		int centerY = (int)(z*64);
		
		if(!(start.y < -0.515f)){
		
		tileIndex[0] = (centerX - 8)/16 + (127 - (centerY + 8)/16)*128; 
		tileIndex[1] = (centerX + 8)/16 + (127 - (centerY + 8)/16)*128;
		tileIndex[2] = (centerX + 8)/16 + (127 - (centerY - 8)/16)*128;
		tileIndex[3] = (centerX - 8)/16 + (127 - (centerY - 8)/16)*128;
		
		
		MainThread.gridMap.tiles[tileIndex[0]][0] = this;
		MainThread.gridMap.tiles[tileIndex[1]][0] = this;
		MainThread.gridMap.tiles[tileIndex[2]][0] = this;
		MainThread.gridMap.tiles[tileIndex[3]][0] = this;
		
		MainThread.gridMap.tiles[tileIndex[0]][1] = this;
		MainThread.gridMap.tiles[tileIndex[1]][1] = this;
		MainThread.gridMap.tiles[tileIndex[2]][1] = this;
		MainThread.gridMap.tiles[tileIndex[3]][1] = this;
		
		MainThread.gridMap.tiles[tileIndex[0]][2] = this;
		MainThread.gridMap.tiles[tileIndex[1]][2] = this;
		MainThread.gridMap.tiles[tileIndex[2]][2] = this;
		MainThread.gridMap.tiles[tileIndex[3]][2] = this;
		
		MainThread.gridMap.tiles[tileIndex[0]][3] = this;
		MainThread.gridMap.tiles[tileIndex[1]][3] = this;
		MainThread.gridMap.tiles[tileIndex[2]][3] = this;
		MainThread.gridMap.tiles[tileIndex[3]][3] = this;

		MainThread.gridMap.tiles[tileIndex[0]][4] = this;
		MainThread.gridMap.tiles[tileIndex[1]][4] = this;
		MainThread.gridMap.tiles[tileIndex[2]][4] = this;
		MainThread.gridMap.tiles[tileIndex[3]][4] = this;
		}
		
		

		//init model
		iDirection = new vector(1f,0,0);
		jDirection = new vector(0,1f,0);
		kDirection = new vector(0,0,1f);	
		centre = start.myClone();
		tempCentre = start.myClone();
		
		shadowvertex0 =start.myClone();
		shadowvertex0.add(-0.45f,-0.2f, -0.45f);
		tempshadowvertex0 = new vector(0,0,0);
		
		shadowvertex1 =start.myClone();
		shadowvertex1.add(-0.45f,-0.2f, 0.2f);
		tempshadowvertex1 = new vector(0,0,0);
		
		shadowvertex2 =start.myClone();
		shadowvertex2.add(0.2f,-0.2f, -0.45f);
		tempshadowvertex2 = new vector(0,0,0);
		
		shadowvertex3 =start.myClone();
		shadowvertex3.add(0.2f,-0.2f, 0.2f);
		tempshadowvertex3 = new vector(0,0,0);
		
		if(!(start.y < -0.515f)){
			int color = 12 << 24 | 245 << 16 | 198 << 8 | 20;
		
			int position = tileIndex[0];
			postProcessingThread.theMiniMap.background[position -128] = color;
			
			postProcessingThread.theMiniMap.background[position - 129] = color;
			postProcessingThread.theMiniMap.background[position - 127] = color;
			postProcessingThread.theMiniMap.background[position-1] = color;
			postProcessingThread.theMiniMap.background[position] = color;
			postProcessingThread.theMiniMap.background[position+1] = color;
			postProcessingThread.theMiniMap.background[position + 127] = color;
			postProcessingThread.theMiniMap.background[position + 128] = color;
			postProcessingThread.theMiniMap.background[position + 129] = color;
			
		}
		
		
		makePolygons();	
	}
	
	private void makePolygons(){
		polygons = new polygon3D[32*32*2];
		
		//load height map
		float[] heightmap = new float[(32+1)*(32+1)];
		
		int[] hm = MainThread.textures[38].heightmap;
		
		
	
		
		
		for(int i = 0; i < 33; i++){
			for(int j =0; j< 33; j++){
				heightmap[j + i * 33] = ((float)hm[j*8 + i*8*257])*0.0014f + centre.y;
				
			}
		}
		
		
		
		float dx = 0.56f / 32;
		float dz = -0.56f / 32;
		float x_start =  start.x - 0.32f;
		float z_start =  start.z + 0.30f;
		
		int index = 0;
		
		for(int i = 0; i < 32; i++){
			for(int j = 0; j < 32; j++){
				
				
				
				tempVector0.set(x_start + dx*j, heightmap[j + i*33], z_start + dz*i);
				tempVector1.set(x_start + dx*(j+1), heightmap[j + 1 + i*33], z_start + dz*i);
				tempVector2.set(x_start + dx*(j+1), heightmap[j + 1 + (i +1)*33], z_start + dz*(i+1));
				tempVector3.set(x_start + dx*j, heightmap[j + (i +1)*33], z_start + dz*(i+1));
				
				if(start.y < -0.515f){
					
					if(tempVector0.y < -0.755){
						continue;
					}
				
				}
				
				
				v = new vector[]{tempVector0.myClone(), tempVector1.myClone(), tempVector3.myClone()};
				deltaX.set(tempVector0);
				deltaX.subtract(tempVector1);
				
				deltaZ.set(tempVector0);
				deltaZ.subtract(tempVector3);
				
				origin.set(tempVector0);
				origin.add(deltaX, j);
				origin.add(deltaZ, i);
				
				top.set(origin);
				top.add(deltaX, -32);
				
				bot.set(origin);
				bot.add(deltaZ, -32);
				
				polygons[index] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), MainThread.textures[textureIndex], 1,1,1);
				deltaX.set(tempVector3);
				deltaX.subtract(tempVector2);
				
				deltaZ.set(tempVector1);
				deltaZ.subtract(tempVector2);
				
				origin.set(tempVector2);
				origin.add(deltaX);
				origin.add(deltaZ);
				origin.add(deltaX, j);
				origin.add(deltaZ, i);
				
				top.set(origin);
				top.add(deltaX, -32);
				
				bot.set(origin);
				bot.add(deltaZ, -32);
				
				v = new vector[]{tempVector1.myClone(), tempVector2.myClone(), tempVector3.myClone()};
				
				
				
				polygons[index+1] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), MainThread.textures[39], 1,1,1);
				
				
						
				index+=2;
			}
		}
		polygonCount = index;
		
		for(int i = 0; i < polygonCount; i++){
			polygons[i].findDiffuse();
			polygons[i].parentObject = this;
			
		}
	}
	
	//update the model 
	public void update(){
		if(!MainThread.gameStarted) {
			isRevealed = true;
		}else {
			if(MainThread.theAssetManager.minimapBitmap[tileIndex[0]] ||
			   MainThread.theAssetManager.minimapBitmap[tileIndex[1]] ||
			   MainThread.theAssetManager.minimapBitmap[tileIndex[2]] ||
			   MainThread.theAssetManager.minimapBitmap[tileIndex[3]] )
						isRevealed = true;
			else
				isRevealed = false;
		}
		
		if(isRevealed){
			//check if gold mine has been depleted
			progressStatus = 100*goldDeposite/maxDeposite;
			
			if(progressStatus == 0 && textureIndex != 41){
				textureIndex = 41;
				for(int i = 0; i < polygons.length; i++)
					polygons[i].myTexture = MainThread.textures[textureIndex];
			}else if(progressStatus == 100 && textureIndex == 41) {
				textureIndex = 39;
				for(int i = 0; i < polygons.length; i++) {
					polygons[i].myTexture = MainThread.textures[textureIndex];
				}
			}
		}else {
			if(textureIndex == 41 && goldDeposite == maxDeposite) {
				textureIndex = 39;
				for(int i = 0; i < polygons.length; i++) {
					polygons[i].myTexture = MainThread.textures[textureIndex];
				}
			}
		}
			
			
		//mark itself on obstacle map
		MainThread.gridMap.currentObstacleMap[tileIndex[0]] = false;
		MainThread.gridMap.currentObstacleMap[tileIndex[1]] = false;
		MainThread.gridMap.currentObstacleMap[tileIndex[2]] = false;
		MainThread.gridMap.currentObstacleMap[tileIndex[3]] = false;
		
		
		
		
		//update center in Camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(Camera.position);
		tempCentre.rotate_XZ(Camera.XZ_angle);
		tempCentre.rotate_YZ(Camera.YZ_angle);
		tempCentre.updateLocation();
			
		theAssetManager = MainThread.theAssetManager;
		
		//test if the palm tree is visible in Camera point of view
		if(visibleBoundary.contains(tempCentre.screenX, tempCentre.screenY)){
			visible = true;
			
		
			
			if(screenBoundary.contains(tempCentre.screenX, tempCentre.screenY))
				withinViewScreen = true;
			else
				withinViewScreen = false;
			
			tempshadowvertex0.set(shadowvertex0);
			tempshadowvertex0.subtract(Camera.position);
			tempshadowvertex0.rotate_XZ(Camera.XZ_angle);
			tempshadowvertex0.rotate_YZ(Camera.YZ_angle);
			tempshadowvertex0.updateLocation();
			
			
			
			tempshadowvertex1.set(shadowvertex1);
			tempshadowvertex1.subtract(Camera.position);
			tempshadowvertex1.rotate_XZ(Camera.XZ_angle);
			tempshadowvertex1.rotate_YZ(Camera.YZ_angle);
			tempshadowvertex1.updateLocation();
			
			tempshadowvertex2.set(shadowvertex2);
			tempshadowvertex2.subtract(Camera.position);
			tempshadowvertex2.rotate_XZ(Camera.XZ_angle);
			tempshadowvertex2.rotate_YZ(Camera.YZ_angle);
			tempshadowvertex2.updateLocation();
			
			tempshadowvertex3.set(shadowvertex3);
			tempshadowvertex3.subtract(Camera.position);
			tempshadowvertex3.rotate_XZ(Camera.XZ_angle);
			tempshadowvertex3.rotate_YZ(Camera.YZ_angle);
			tempshadowvertex3.updateLocation();

			

			//if the  object is visible then draw it on the shadow buffer from light point of view
			if(shadowBoundary1.contains(tempshadowvertex0.screenX, tempshadowvertex0.screenY) ||
					shadowBoundary1.contains(tempshadowvertex1.screenX, tempshadowvertex1.screenY) ||
					shadowBoundary1.contains(tempshadowvertex2.screenX, tempshadowvertex2.screenY) ||
					shadowBoundary1.contains(tempshadowvertex3.screenX, tempshadowvertex3.screenY) 
					){
				for(int i = 0; i < polygonCount; i++){
					polygons[i].update_lightspace();
					
					
				}
		
			}
			
			//add this object to visible unit list
			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;
			
		}else{
			visible = false;
		}
		
		
		if(visible_minimap){
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1/16;
			tempInt[2] = 127 - boundary2D.y1/16;
			tempInt[3] = 2;
			tempInt[4] = 10000;
			theAssetManager.unitsForMiniMapCount++;	
		}
	}
	
	
	
	
	//draw the model
	public void draw(){
		if(!visible)
			return;
		
		for(int i = 0; i < polygonCount; i++){
			
			polygons[i].update();
		}
		
		for(int i = 0; i < polygonCount; i++){
			polygons[i].draw();
		}
	}
		
	public vector getMovement(){
		return movenment;
	}


    //clone a group of polygons (doesn't work on smooth shaded polygons)
    public polygon3D[] clonePolygons(polygon3D[] polys, boolean createNewOUV){
        int l = polys.length;

        polygon3D[] clone = new polygon3D[l];

        for(int i = 0; i < l; i++){
            if(polys[i] == null)
                continue;
            int length = polys[i].vertex3D.length;
            v = new vector[length];
            for(int j = 0; j < length; j++){
                v[j] = polys[i].vertex3D[j].myClone();
            }

            int myType = polys[i].type;
            float scaleX = polys[i].scaleX;
            float scaleY = polys[i].scaleY;
            texture myTexture = polys[i].myTexture;
            if(createNewOUV)
                clone[i] = new polygon3D(v, polys[i].origin.myClone(), polys[i].rightEnd.myClone(), polys[i].bottomEnd.myClone(), myTexture, scaleX, scaleY, myType);
            else
                clone[i] = new polygon3D(v, v[0], v[1], v[3], myTexture, scaleX, scaleY, myType);
            clone[i].shadowBias = polys[i].shadowBias;
            clone[i].diffuse_I = polys[i].diffuse_I;
            clone[i].Ambient_I = polys[i].Ambient_I;
        }


        return clone;
    }
}
