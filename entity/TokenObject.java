package entity;

import core.*;

//the only purpose of this object is to create an invisible boundary block
public class TokenObject extends SolidObject {
	public int tileIndex;
	public boolean noNeedForThisToken;
	
	public TokenObject(float x, float y, float z, int color){
		ID = -1;
		type = 4;
		teamNo = -1;
		centre = new vector(x,y,z);
		
		currentCommand = StandBy;
		
		movement = new vector(0,0,0);
		
		boundary2D = new Rect((int)(x*64) - 8, (int)(z*64) + 8, 16, 16);
		
		
		tileIndex = boundary2D.x1/16 + (127 - (boundary2D.y1 - 1)/16)*128;
		
		
		if(x < 0 || MainThread.gridMap.tiles[tileIndex][0]!= null){
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
