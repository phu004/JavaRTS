package core;

import entity.*;

public class grid {
	
	public int size;
	public solidObject[][] tiles; //a list of colliable objects, used by  local path finding 
	public boolean[] previousObstacleMap, currentObstacleMap;  //a boolean  representation of the collideble objects, used by A star
	
	public grid(int size){
		this.size = size;
		tiles = new solidObject[size * size][5];
		
		previousObstacleMap = new boolean[size * size];
		currentObstacleMap = new boolean[size * size];
		
		for(int i = 0; i < size * size; i++){
			previousObstacleMap[i] = true;
			currentObstacleMap[i] = true;
		}
		
	}
	
	public void update(){
		int l = size * size;
		for(int i = 0; i < l; i++){
			previousObstacleMap[i] = currentObstacleMap[i];
			currentObstacleMap[i] = true;
		}
		
		for(int i = 0; i < size; i++){
			currentObstacleMap[i] = false;
			currentObstacleMap[l - 1 - i] = false;
			currentObstacleMap[i * 128] = false;
			currentObstacleMap[(i+1) * 128 - 1] = false;
		}
	}
	
	
	
	
	public void draw(){
		int pos = 2 + 20 * 768;
		boolean tile;
		int[] screen = mainThread.screen2;
		for(int i = 0; i < 128; i++){
			for(int j = 0; j < 128; j++){
				tile = previousObstacleMap[j + i*128];
				if(!tile)
					screen[pos + j + i*768] = 0; 
				
			}
		}
	}
	
}


