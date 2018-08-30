package entity;

import core.Rect;
import core.mainThread;
import core.postProcessingThread;
import core.vector;

//the only purpose of this object is to create an invisible boundary block
public class tokenObject extends solidObject{
	public int tileIndex;
	public boolean noNeedForThisToken;
	
	public tokenObject(float x, float y, float z, int color){
		ID = -1;
		type = 4;
		teamNo = -1;
		centre = new vector(x,y,z);
		
		currentCommand = StandBy;
		
		movement = new vector(0,0,0);
		
		boundary2D = new Rect((int)(x*64) - 8, (int)(z*64) + 8, 16, 16);
		
		
		tileIndex = boundary2D.x1/16 + (127 - (boundary2D.y1 - 1)/16)*128;
		
		
		if(x < 0 || mainThread.gridMap.tiles[tileIndex][0]!= null){
			noNeedForThisToken = true;
			return;
		}
		

		if(!(x == 0 && y ==0 && z ==0))
			updateOccupiedTiles(boundary2D.x1, boundary2D.y1);

		
		boundary2D.owner = this;
		postProcessingThread.theMiniMap.background[tileIndex] = color;
		
		
	}
	
	
	public vector getMovement(){
		return movement;
	}
	
}
