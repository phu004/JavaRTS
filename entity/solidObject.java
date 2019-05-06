package entity;

import core.*;

//this is the class for storing geometry information of a 3D model
public abstract class solidObject{
	
	//reference point of the model (in world coordinate)
	public vector start;
	
	//the reference axis of the model  (in world coordinate)
	public vector iDirection, jDirection, kDirection;
	 	
	//whether this model need to be sent to drawing pipeline
	public boolean visible;
	
	//whether this model (only apply to static structures) has been revealed by player
	public boolean isRevealed;
	
	//whether this model (only apply to static structures) has been revealed by AI
	public boolean isRevealed_AI;
	
	//whether this model is completely bounded by the screen area 
	public boolean withinViewScreen;
	
	//whether this model is selected;
	public boolean isSelected;
	
	//whether this model is selectable;
	public boolean isSelectable = true;
	
	//change in  center position between current frame and previous frame
	public vector movement;
	
	//distance to desination
	public float distanceToDesination;
	
	public boolean disableUnitLevelAI;
	
	public float distanceToDesination_PreviousFrame = 9999;
	
	public boolean closeToDestination;
	
	public int hugWallCoolDown;
	
	public int tightSpaceManeuverCountDown; 
	
	public int stuckCount, insideDeistinationRadiusCount;
	
	public int bodyAngleDelta, turretAngleDelta;
	
	public float speed;
	
	public boolean visionInsideScreen, visible_lightspace, visible_minimap;;
	
	public int teamNo, type, currentHP; 
	
	//this value represents the total amount of the inevitable damage that the unit will suffer, e.g damage from a incoming missile
	public int incomingDamage;
	
	public float destinationX, destinationY, secondaryDestinationX, secondaryDestinationY;
	
	public int destinationX_, destinationY_;
	
	public boolean newDestinationisGiven;
	
	public solidObject attacker;
	
	public int myDamage;
	
	public Rect obstacle,tempObstacle;
	public Rect unStableObstacle;
	
	//immediate destination Angle
	public int immediateDestinationAngle;
	public int tempAngle1, tempAngle2, tempAngle3,tempAngle4;
	
	//the index of tiles the harvester has occupied on the grid map
	public int currentOccupiedTile, occupiedTile0, occupiedTile1,occupiedTile2,occupiedTile3, 
						   previousOccupiedTile0, previousOccupiedTile1,previousOccupiedTile2,previousOccupiedTile3,
						   newOccupiedTile0, newOccupiedTile1, newOccupiedTile2,newOccupiedTile3,
						   tempTile0, tempTile1, tempTile2, tempTile3;	
	
	public solidObject[] tile;
	public int xPos, yPos, xPos2, yPos2, xPos_old, yPos_old;
	public float[] tempFloat;
	public int[] tempInt;
	public int randomNumber;
	public static Rect border = new Rect(0,0,16,16);
	public static Rect destinationBlock = new Rect(0,0,0,0); 
	public static Rect probeBlock = new Rect(0,0,0,0);
	
	
	public int currentCommand;
	public static final int StandBy = 0;
	public static final int move = 1;
	public static final int attackCautiously = 2;
	public static final int attackInNumbers = 3;
	public static final int follow = 4;
	public static final int attackMove = 5;
	
	public int secondaryCommand;
	
	
	//movment status
	public int currentMovementStatus;
	public final static int freeToMove = 0;
	public final static int hugLeft = 1;
	public final static int hugRight = 2;
	
	//attack status
	public int attackStatus;
	public final static int noTarget = 0;
	public final static int isAttacking = 1;
	public final static int notInRange = 2;
	public float attackRange, groupAttackRange;
	
	public int experience, level;
	
	//wether this object is under attack;
	public int underAttackCountDown;
	
	//A cool down which prevents the object to change target too often
	//public int changeTargetCountDown;
	
	//a rectangle which represent the object boundary in grid map
	public Rect boundary2D;
	
	//vertices
	public vector[] v;
	
	//the 3D polygons of the object
	public polygon3D[] polygons;
	
	//the centre of the model in world coordinate
	public vector centre;
	
	//the centre of the model in camera coordinate 
	public vector tempCentre = new vector(0,0,0);
	public vector tempVector = new vector(0,0,0);
	
	public static int globalUniqID = 0;
	
	public int ID;
	
	public AssetManager theAssetManager;
	
	public float height;
	
	public static Rect fullSizedProbe = new Rect(0,0, 16, 16);
	
	public int progressStatus = -1;
	
	public drone myHealer;
	
	public boolean isCloaked;
	public int cloakCooldownCount;
	
	public boolean isRepairing = true;
	
	public int screenX_gui, screenY_gui;
	
	//object that the unit is trying to attack
	public solidObject targetObject;
	
	public int groupNo = 255;

	public boolean leftFactory;

	//get centre of this model in camera coordinate
	public  vector getCentre(){
		return tempCentre;
	}
	
	//return centre in world coordinate
	public  vector getRealCentre(){
		return centre;
	}

	//return visibility
	public boolean getVisibility(){
		return visible;
	}
		
	//create a arbitrary vertex
	public  vector put(double i, double j, double k){
		vector temp = start.myClone();
		temp.add(iDirection, (float)i);
		temp.add(jDirection, (float)j);
		temp.add(kDirection, (float)k);
		return temp;
	}
	
	//change the 3d geometry of a vertex
	public  void change(float i, float j, float k, vector v){
		v.set(start);
		v.add(iDirection, i);
		v.add(jDirection, j);
		v.add(kDirection, k);
	}	
	
	//create color in binary format 
	public  int createColor(int r, int g, int b){
		return  b + (g << 5) + (r << 10);
	}

	
	//get ID
	public int getID(){
		return ID;
	}
	

	//generate indexes for surrounding tile  
	public static int[] generateTileCheckList(float r){
		int[] list = new int[(int)((r+1)*(r+1)*Math.PI)];
		float[] distance = new float[list.length];
		
		for(int i = 0; i < list.length; i++){
			list[i] = Integer.MAX_VALUE;
			distance[i] = 10000000;
		}
		
		int currentIndex = 0;
		int R = (int)r;
		if(R < r)
			R++;
		
		for(int y = 0; y < R * 2 + 1; y++){
			for(int x = 0; x < R * 2 + 1; x++){
				int a = Math.abs(x-R);
				int b = Math.abs(y-R);
				if(r >= Math.sqrt(a*a + b*b)){
					list[currentIndex] = x - R  + (y - R)*(32*4);
					distance[currentIndex] = (float)Math.sqrt(a*a + b*b);
					currentIndex++;
				}
			}
		}
		
		for(int i = 1; i < list.length; i++){
			for(int j = 0; j <list.length - i; j++){
				if(distance[j] > distance[j+1]){
					float tempFloat = distance[j+1];
					distance[j+1] = distance[j];
					distance[j] = tempFloat;
					
					int tempInt = list[j+1];
					list[j+1] = list[j];
					list[j] = tempInt;
				}
			}
		}
		
		return list;
	}
	
	public boolean[] createBitmapVision(int radius){
		int l = radius*2+1;
		boolean[] vision = new boolean[l*l];
		for(int y = 0; y < l; y++){
			for(int x = 0; x < l; x++){
				if( (x - radius)*(x - radius) + (y - radius)*(y - radius)   <  ((float)radius+0.5f)*((float)radius+0.5f)){
					vision[x + y*l] = true;
				}
			}
		}
		return vision;
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
	
	public boolean isStable(solidObject o){
		if(o != null){
			if(o.currentCommand == StandBy || (o.attackStatus == isAttacking && o.getMovement().x ==0 && o.getMovement().z ==0)  || o.type > 100){
			
				return true;
			}
		}
			
		return false;
	}
	
	public void removeFromGridMap(){
		boundary2D.setOrigin(100000, 100000);
		if(occupiedTile0 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile0];
			for(int i = 0; i < 5; i++){
				if(tile[i] == this){
					tile[i] = null;
					
				}
			}
		}
		
		if(occupiedTile1 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile1];
			for(int i = 0; i < 5; i++){
				if(tile[i] == this){
					tile[i] = null;
					
				}
			}
		}
		
		if(occupiedTile2 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile2];
			for(int i = 0; i < 5; i++){
				if(tile[i] == this){
					tile[i] = null;
					
				}
			}
		}
		
		if(occupiedTile3 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile3];
			for(int i = 0; i < 5; i++){
				if(tile[i] == this){
					tile[i] = null;
					
				}
			}
		}
		
	}
	
	public void updateOccupiedTiles(int x, int y){
		
		previousOccupiedTile0 = occupiedTile0;
		previousOccupiedTile1 = occupiedTile1;
		previousOccupiedTile2 = occupiedTile2;
		previousOccupiedTile3 = occupiedTile3;
		
		occupiedTile0= x/16 + (127 - (y-1)/16)*128;
		occupiedTile1= -1;
		occupiedTile2= -1;
		occupiedTile3= -1;
		if(movement.x == 0 && movement.z == 0){
			mainThread.gridMap.currentObstacleMap[occupiedTile0] = false;
		}
		
		if(x%16 >0 && y%16 > 0){
			occupiedTile1= occupiedTile0 + 1;
			occupiedTile2= occupiedTile0 + 128;
			occupiedTile3= occupiedTile2 + 1;
			
			if(movement.x == 0 && movement.z == 0){
				mainThread.gridMap.currentObstacleMap[occupiedTile1] = false;
				mainThread.gridMap.currentObstacleMap[occupiedTile2] = false;
				mainThread.gridMap.currentObstacleMap[occupiedTile3] = false;
			}
			
		}else if(x%16 > 0){
			occupiedTile1= occupiedTile0 + 1;
			if(movement.x == 0 && movement.z == 0){
				mainThread.gridMap.currentObstacleMap[occupiedTile1] = false;
			}
		}else if(y%16 > 0){
			occupiedTile2= occupiedTile0 + 128;
			if(movement.x == 0 && movement.z == 0){
				mainThread.gridMap.currentObstacleMap[occupiedTile2] = false;
			}
		}
		
		
		//object occupies exactly the same tiles as before, then do nothing
		if(previousOccupiedTile0 == occupiedTile0 && previousOccupiedTile1 == occupiedTile1 && previousOccupiedTile2 == occupiedTile2 && previousOccupiedTile3 == occupiedTile3){
			return;
		}
		
		//remove the object from the old tiles
		if(previousOccupiedTile0 != -1){
			tile = mainThread.gridMap.tiles[previousOccupiedTile0];
			for(int i = 0; i < 4; i++){
				if(tile[i] == this){
					tile[i] = null;
					break;
				}
			}
		}
		if(previousOccupiedTile1 != -1){
			tile = mainThread.gridMap.tiles[previousOccupiedTile1];
			for(int i = 0; i < 4; i++){
				if(tile[i] == this){
					tile[i] = null;
					break;
				}
			}
		}
		if(previousOccupiedTile2 != -1){
			tile = mainThread.gridMap.tiles[previousOccupiedTile2];
			for(int i = 0; i < 4; i++){
				if(tile[i] == this){
					tile[i] = null;
					break;
				}
			}
		}
		if(previousOccupiedTile3 != -1){
			tile = mainThread.gridMap.tiles[previousOccupiedTile3];
			for(int i = 0; i < 4; i++){
				if(tile[i] == this){
					tile[i] = null;
					break;
				}
			}
		}
		
		//add the object to the new tiles
		if(occupiedTile0 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile0];
			for(int i = 0; i < 4; i++){
				if(tile[i] == null){
					tile[i] = this;
					break;
				}
			}
		}
		if(occupiedTile1 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile1];
			for(int i = 0; i < 4; i++){
				if(tile[i] == null){
					tile[i] = this;
					break;
				}
			}
		}
		if(occupiedTile2 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile2];
			for(int i = 0; i < 4; i++){
				if(tile[i] == null){
					tile[i] = this;
					break;
				}
			}
		}
		if(occupiedTile3 != -1){
			tile = mainThread.gridMap.tiles[occupiedTile3];
			for(int i = 0; i < 4; i++){
				if(tile[i] == null){
					tile[i] = this;
					break;
				}
			}
		}
	}
	
	public Rect retriveSurroundingObject(int xPos, int yPos){ 
		tempTile0  = xPos/16 + (127 - yPos/16)*128;
		tempTile1 = tempTile0 + 1;
		tempTile2 = tempTile0 + 128;
		tempTile3 = tempTile0 + 129;
		
		Rect r = null;
		
		if(tempTile0 >=0 && tempTile0 < 16384){
			tile = mainThread.gridMap.tiles[tempTile0];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].ID != ID){
						if(isStable(tile[i]))
							return tile[i].boundary2D;
						else
							r = tile[i].boundary2D;
					}
				}
			}
		}
		
		if(tempTile1 >=0 && tempTile1 < 16384){
			tile = mainThread.gridMap.tiles[tempTile1];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if( tile[i].ID != ID){
						if(isStable(tile[i]))
							return tile[i].boundary2D;
						else
							r = tile[i].boundary2D;
					}
				}
			}
		}
		
		if(tempTile2 >=0 && tempTile2 < 16384){
			tile = mainThread.gridMap.tiles[tempTile2];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].ID != ID){
						if(isStable(tile[i]))
							return tile[i].boundary2D;
						else
							r = tile[i].boundary2D;
					}
				}
			}
		}
	
		if(tempTile3 >=0 && tempTile3 < 16384){
			tile = mainThread.gridMap.tiles[tempTile3];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].ID != ID){
						if(isStable(tile[i]))
							return tile[i].boundary2D;
						else
							r = tile[i].boundary2D;
					}
				}
			}
		}
		

		return r;
	}
	
	
	public int validateMovement(){
		
		
		
		xPos_old = boundary2D.x1;
		yPos_old = boundary2D.y1;
		xPos = (int)((centre.x + movement.x)*64) - 8;
		yPos = (int)((centre.z + movement.z)*64) + 8;
		boundary2D.setOrigin(xPos, yPos);
	
		obstacle = checkForCollision(boundary2D);
		
		boundary2D.setOrigin(xPos_old, yPos_old);
		
		if(obstacle == null){	
			
			return freeToMove;
		}else{
			destinationX_ = (int)(destinationX*64);
			destinationY_ = (int)(destinationY*64);
			
			int x = 0;
			int y = 0;
			if(obstacle.owner!= null){
				x = obstacle.owner.boundary2D.x1;
				y = obstacle.owner.boundary2D.y1;
				
				if(x - xPos_old < -16)
					x+=16;
				
				if(y - yPos_old > 16)
					y-=16;
				
				if(x - xPos_old > 16)
					x-=16;
				
				if(y - yPos_old < -16)
					y+=16;
				
				if(obstacle.owner.teamNo != teamNo)
					obstacle.owner.cloakCooldownCount = 60;
				
			}else{
				x = obstacle.x1;
				y = obstacle.y1;
			}
			
			float dx = Math.abs(centre.x - destinationX);
			float dy = Math.abs(centre.z - destinationY);
			
			float dx_ = 0;
			float dy_ = 0;
			if(obstacle.owner != null){
				dx_ = centre.x - obstacle.owner.centre.x;
				dy_ = centre.z - obstacle.owner.centre.z;
			}
			
			
			
			
			
			//figure out which action should be taken next
			float upDistance = 0;
			float downDistance =0;
			float leftDistance = 0;
			float rightDistance = 0;
			
			if(obstacle.x1 > boundary2D.x2){
				
				if((currentCommand == attackCautiously || currentCommand == attackInNumbers) && distanceToDesination  <= 1.5f){
					upDistance = countOccupiedBlocksDuringAttack(x, y, 0, 16, 16, 0) * 0.25f;
					downDistance = countOccupiedBlocksDuringAttack(x, y, 0, -16, 16, 0) * 0.25f;
				}else{
					upDistance = countOccupiedBlocks(x, y, 0, 16, 16, 0) * 0.25f;
					downDistance = countOccupiedBlocks(x, y, 0, -16, 16, 0) * 0.25f;
				}
				
				if(destinationY < centre.z){

					if(downDistance <= dy || dx < 0.125f){
						immediateDestinationAngle = 180;
						return hugLeft;
					}
					
					if(downDistance < upDistance){
						immediateDestinationAngle = 180;
						return hugLeft;
					}else{
						if(upDistance == downDistance){
							if(dy_ < 0){
								immediateDestinationAngle = 180;
								return hugLeft;
							}
						}
						
						immediateDestinationAngle = 0;
						return hugRight;
					}
				}else{
					
					
					
					if(upDistance <= dy || dx < 0.125f){
						immediateDestinationAngle = 0;
						return hugRight;
					}
					
					if(upDistance < downDistance){
						immediateDestinationAngle = 0;
						return hugRight;
					}else{
						
						immediateDestinationAngle = 180;
						return hugLeft;
					}
					
				}

			}
			
			if(obstacle.x2 < boundary2D.x1){
				
				if((currentCommand == attackCautiously || currentCommand == attackInNumbers) && distanceToDesination  <= 1.5f){
					upDistance = countOccupiedBlocksDuringAttack(x, y, 0, 16, -16, 0) * 0.25f;
					downDistance = countOccupiedBlocksDuringAttack(x, y, 0, -16, -16, 0) * 0.25f;
					
				}else{
					upDistance = countOccupiedBlocks(x, y, 0, 16, -16, 0) * 0.25f;
					downDistance = countOccupiedBlocks(x, y, 0, -16, -16, 0) * 0.25f;
				}
				
			
				
				if(destinationY < centre.z){
					if(downDistance <= dy || dx < 0.125f){
						immediateDestinationAngle = 180;
						return hugRight;
					}
					
					if(downDistance < upDistance){
						immediateDestinationAngle = 180;
						return hugRight;
					}else{
						
						if(upDistance == downDistance){
							if(dy_ < 0){
								immediateDestinationAngle = 180;
								return hugRight;
							}
						}
						
						immediateDestinationAngle = 0;
						return hugLeft;
					}
				}else{
					if(upDistance <= dy || dx < 0.125f){
						immediateDestinationAngle = 0;
						return hugLeft;
					}
					
					if(upDistance < downDistance){
						immediateDestinationAngle = 0;
						return hugLeft;
					}else{
						
						immediateDestinationAngle = 180;
						return hugRight;
					}
					
				}
			}
			
			if(obstacle.y2 > boundary2D.y1){
				
				if((currentCommand == attackCautiously || currentCommand == attackInNumbers) && distanceToDesination  <= 1.5f){
					leftDistance = countOccupiedBlocksDuringAttack(x, y, -16, 0, 0, 16) * 0.25f;
					rightDistance = countOccupiedBlocksDuringAttack(x, y, 16, 0, 0, 16) * 0.25f;
				}else{
					leftDistance = countOccupiedBlocks(x, y, -16, 0, 0, 16) * 0.25f;
					rightDistance = countOccupiedBlocks(x, y, 16, 0, 0, 16) * 0.25f;
				}
				
				if(destinationX < centre.x){
					if(leftDistance <= dx || dy < 0.125f){
						immediateDestinationAngle = 270;
						return hugRight;
					}
						
					if(leftDistance < rightDistance){
						immediateDestinationAngle = 270;
						return hugRight;
					}else{
						immediateDestinationAngle = 90;
						return hugLeft;
					}
				}else{
					
					
					if(rightDistance <= dx || dy < 0.125f){
						immediateDestinationAngle = 90;
						
						
						return hugLeft;
					}
					if(rightDistance < leftDistance){
						immediateDestinationAngle = 90;
						return hugLeft;
					}else{
						
						if(rightDistance == leftDistance){
							if(dx_ > 0){
								immediateDestinationAngle = 90;
								return hugLeft;
							}
						}
						
						immediateDestinationAngle = 270;
						return hugRight;
					}
				}
			}
			
			if(obstacle.y1 < boundary2D.y2){
				
				
				if((currentCommand == attackCautiously || currentCommand == attackInNumbers) && distanceToDesination  <= 1.5f){
					leftDistance = countOccupiedBlocksDuringAttack(x, y, -16, 0, 0, -16) * 0.25f;
					rightDistance = countOccupiedBlocksDuringAttack(x, y, 16, 0, 0, -16) * 0.25f;
				}else{
					leftDistance = countOccupiedBlocks(x, y, -16, 0, 0, -16) * 0.25f;
					rightDistance = countOccupiedBlocks(x, y, 16, 0, 0, -16) * 0.25f;
				}
				
				if(destinationX < centre.x){
	
					if(leftDistance <= dx || dy < 0.125f){
						immediateDestinationAngle = 270;
						return hugLeft;
					}
						
					if(leftDistance < rightDistance){
						immediateDestinationAngle = 270;
						return hugLeft;
					}else{
						immediateDestinationAngle = 90;
						return hugRight;
					}
				}else{
					if(rightDistance <= dx || dy < 0.125f){
						immediateDestinationAngle = 90;
						return hugRight;
					}
					if(rightDistance < leftDistance){
						immediateDestinationAngle = 90;
					
						return hugRight;
					}else{
						if(rightDistance == leftDistance){
							if(dx_ > 0){
								immediateDestinationAngle = 90;
								return hugRight;
							}
						}
						
						
						immediateDestinationAngle = 270;
						return hugLeft;
					}
				}
			}
		
			return -1;
		}
	}
	
	//count the number of occupied block (up to 10 blocks) in a given direction
	public int countOccupiedBlocks(int x, int y, int dx, int dy, int dx_, int dy_){
		int count = 0;
		for(int i = 0; i < 10; i++, x+=dx, y +=dy){
			if(x > 127*16 || x < 0 || y > 127*16 || y < 0)
				continue;
			fullSizedProbe.setOrigin(x, y);
			if(checkForCollision(fullSizedProbe) != null){
				count++;
			}else{
				if(fullSizedProbe.contains(destinationX_, destinationY_)){
					break;
				}
				
				fullSizedProbe.setOrigin(x+dx_, y+dy_);
				if(checkForCollision(fullSizedProbe) != null){
					fullSizedProbe.setOrigin(x+dx, y+dy);
					if(checkForCollision(fullSizedProbe) != null){
						count++;
					}else{
						break;
					}
				}else
					break;
			}
			
		}
		count-=1;
		
		if(count < 0)
			count = 0;
		
		return count;
	}
	
	//count the number of occupied block (up to 10 blocks) in a given direction
	public int countOccupiedBlocksDuringAttack(int x, int y, int dx, int dy, int dx_, int dy_){
		int count = 0;
		Rect tempRect;
		for(int i = 0; i < 10; i++, x+=dx, y +=dy){
			if(x > 127*16 || x < 0 || y > 127*16 || y < 0)
				continue;
			fullSizedProbe.setOrigin(x, y);
			
			tempRect = checkForCollision(fullSizedProbe);
			if(tempRect != null){
				if(tempRect.owner != null && tempRect.owner == targetObject)
					return 0;
				count++;
			}else{
				if(fullSizedProbe.contains(destinationX_, destinationY_)){
					break;
				}
				
				fullSizedProbe.setOrigin(x+dx_, y+dy_);
				if(checkForCollision(fullSizedProbe) != null){
					fullSizedProbe.setOrigin(x+dx, y+dy);
					if(checkForCollision(fullSizedProbe) != null){
						count++;
					}else{
						break;
					}
				}else
					break;
			}
			
		}
		count-=1;
		
		if(count < 0)
			count = 0;
		
		return count;
	}
	
	public Rect checkForCollision(Rect myRect){
		if(obstacle != null){
			if(myRect.intersect(obstacle) && (isStable(obstacle.owner))){
				return obstacle;
			}
		}
		
			
		//check if the tank collide with the border
		if(myRect.x1 < 0){
			border.setOrigin(-16, myRect.y1); 
			
			return border;
		}
		
		if(myRect.x2 > 2047){
			border.setOrigin(2048, myRect.y1);
			return border;
		}
		
		if(myRect.y2 < 1){
			border.setOrigin(myRect.x1, 0);
			return border;
		}
		
		if(myRect.y1 > 2048){
			border.setOrigin(myRect.x1, 2064);
			return border;
		}		

		newOccupiedTile0= myRect.x1/16 + (127 - myRect.y1/16)*128;	
		
		if(myRect.y1 == 2048)
			newOccupiedTile0= myRect.x1/16 + (127 - (myRect.y1-1)/16)*128;	
		
		newOccupiedTile1 = newOccupiedTile0 + 1;
		newOccupiedTile2 = newOccupiedTile0 + 128;
		newOccupiedTile3 = newOccupiedTile0 + 129;
		
		
		
	
		tempObstacle = null;
		
		if(newOccupiedTile0 >= 0 && newOccupiedTile0 < 16384){
			tile = mainThread.gridMap.tiles[newOccupiedTile0];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].ID != ID)
						if(isStable(tile[i]) ){
							return tile[i].boundary2D;
						}else{
							if(tempObstacle == null)
								tempObstacle = tile[i].boundary2D;
						}
				}
			}
		}
		
		if(newOccupiedTile1 >= 0 && newOccupiedTile1 < 16384){
			tile = mainThread.gridMap.tiles[newOccupiedTile1];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].ID != ID)
						if(isStable(tile[i]) ){
							return tile[i].boundary2D;
						}else{
							if(tempObstacle == null)
								tempObstacle = tile[i].boundary2D;
						}
				}
			}
		}
	
		if(newOccupiedTile2 >= 0 && newOccupiedTile2 < 16384){
			tile = mainThread.gridMap.tiles[newOccupiedTile2];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].ID != ID)
						if(isStable(tile[i]) ){
							return tile[i].boundary2D;
						}else{
							if(tempObstacle == null)
								tempObstacle = tile[i].boundary2D;
						}
				}
			}
		}
	
		if(newOccupiedTile3 >= 0 && newOccupiedTile3 < 16384){
			tile = mainThread.gridMap.tiles[newOccupiedTile3];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].ID != ID)
						if(isStable(tile[i]) ){
							return tile[i].boundary2D;
						}else{
							if(tempObstacle == null)
								tempObstacle = tile[i].boundary2D;
						}
				}
			}
		}
		
		if(tempObstacle != null)
			unStableObstacle = tempObstacle;
		
		return tempObstacle;
	}
	
	public void calculateMovement(){
		movement.set(destinationX - centre.x, 0, destinationY - centre.z);
		movement.unit();
		movement.scale(speed);
	}
	
	public void changeMovement(int angle){
		if(angle == 0)
			movement.set(0,0,speed);
		if(angle == 90)
			movement.set(speed,0,0);
		if(angle == 180)
			movement.set(0,0,-speed);
		if(angle == 270)
			movement.set(-speed,0,0);
	}
	
	public boolean checkIfTileIsOccupiedByStaticUnitProbe(float x, float y){
		xPos = (int)(x*64);
		yPos = (int)(y*64);
		if(xPos <= 0 || yPos <= 0 || xPos >= 2048 || yPos >=2048)
			return true;
		
		probeBlock.setOrigin(xPos-6, yPos+6);
		tile = mainThread.gridMap.tiles[xPos/16 + (127 - yPos/16)*128];

		for(int i = 0; i < 4; i++){
			if(tile[i] != null){
				if(tile[i].boundary2D.intersect(probeBlock) && isStable(tile[i]) )
					return true;
			}
		}
		
		return false;
	}
	
	public boolean checkIfTileIsOccupiedByStaticUnitPoint(float x, float y){
		xPos = (int)(x*64);
		yPos = (int)(y*64);
		
		if(xPos <= 0 || yPos <= 0 || xPos >= 2048 || yPos >=2048)
			return true;
		tile = mainThread.gridMap.tiles[xPos/16 + (127 - yPos/16)*128];

		for(int i = 0; i < 4; i++){
			if(tile[i] != null){
				if(tile[i].boundary2D.contains(xPos, yPos) && isStable(tile[i]) )
					return true;
			}
		}
		
		return false;
	}
	
	//a maneuver that follow the edge of the obstacles until the path to its destination is clear
	public void hugWalls(){
		
		
		xPos_old = boundary2D.x1;
		yPos_old = boundary2D.y1;
		
		tempVector.set(movement);
		calculateMovement();
		
		xPos = (int)((centre.x + movement.x)*64) - 8;
		yPos = (int)((centre.z + movement.z)*64) + 8;
		
		
		//if there is nothing nearby then re-issue the command to move towards  destination 
		
		boundary2D.setOrigin(xPos, yPos);
		boolean destinationImmediatelyReachable = true;
		
		//check if the next move towards the destination will result in collision 
		if(checkForCollision(boundary2D) == null){
			movement.scale(4);
			xPos = (int)((centre.x + movement.x)*64) - 8;
			yPos = (int)((centre.z + movement.z)*64) + 8;
			boundary2D.setOrigin(xPos, yPos);
			
			//if there is no collision  then check if the obstacle found in previous move is static or not
			if(checkForCollision(boundary2D) == null){
				if(obstacle.owner != null){
					//if the obstacle is not static (e.g another moving tank) then re-issue the command to move towards destination
					if(obstacle.owner.getMovement().x != 0 || obstacle.owner.getMovement().z !=0 || (Math.abs(obstacle.owner.immediateDestinationAngle - immediateDestinationAngle) < 10 && !isStable(obstacle.owner))){
						newDestinationisGiven = true;
						movement.reset();
						currentMovementStatus = freeToMove;
						hugWallCoolDown = 0;
						obstacle = null;
						boundary2D.setOrigin(xPos_old, yPos_old);
						return;
					}
				}
				
				//check more moves towards destination for potential collisions to ensure the path is really clear
				movement.scale(4);
				xPos = (int)((centre.x + movement.x)*64) - 8;
				yPos = (int)((centre.z + movement.z)*64) + 8;
				boundary2D.setOrigin(xPos, yPos);
				if(checkForCollision(boundary2D) == null){
					movement.scale(2);
					xPos = (int)((centre.x + movement.x)*64) - 8;
					yPos = (int)((centre.z + movement.z)*64) + 8;
					boundary2D.setOrigin(xPos, yPos);
					
					
					if(checkForCollision(boundary2D) == null){
						//finally if no collision is found, and the destination direction is not opposite to the tank's current direction, 
						//then re-issue the command to move towards destination
						

						Rect surroundingObject = retriveSurroundingObject(xPos, yPos);
						boolean shouldMoveTowardsDestination = false;
						
						if(surroundingObject != null){
							if(surroundingObject.owner != null){
								if(!isStable(surroundingObject.owner)){
								  
									shouldMoveTowardsDestination = true;
								}
							}
						}
						
						
						
						
						if(tempVector.dot(movement) > 0 || shouldMoveTowardsDestination){
							
							
							newDestinationisGiven = true;
							calculateMovement();
							currentMovementStatus = freeToMove;
							boundary2D.setOrigin(xPos_old, yPos_old);
							hugWallCoolDown = 0;
							obstacle = null;
							return;
						}
						movement.reset();
						boundary2D.setOrigin(xPos_old, yPos_old);
					}
				}
				
				
			}
		}else{
			obstacle = checkForCollision(boundary2D);
			if(isStable(obstacle.owner))
				destinationImmediatelyReachable = false;
		}
		
		
		
		
		if(currentMovementStatus == hugRight){
			
			tempAngle1 = (immediateDestinationAngle + 90)%360;
			tempAngle2 = immediateDestinationAngle;
			tempAngle3 = (immediateDestinationAngle - 90 + 360)%360;
			tempAngle4 = (immediateDestinationAngle - 180 + 360)%360;
			
		}else{
			
			tempAngle1 = (immediateDestinationAngle - 90 + 360)%360;
			tempAngle2 = immediateDestinationAngle;
			tempAngle3 = (immediateDestinationAngle + 90)%360;
			tempAngle4 = (immediateDestinationAngle + 180)%360;
		}
		
		if(hugWallCoolDown >0){
			hugWallCoolDown--;
			
		}else{
			if(obstacle.owner != null){
				if(obstacle.owner.type >= 100){
					changeMovement(tempAngle2);
					movement.scale(16);
					xPos = (int)((centre.x + movement.x)*64) - 8;
					yPos = (int)((centre.z + movement.z)*64) + 8;
					boundary2D.setOrigin(xPos, yPos);
					
					if(checkForCollision(boundary2D) == null){
						float x = movement.x;
						float z = movement.z;
						changeMovement(tempAngle1);
						movement.scale(16);
						
						if(tightSpaceManeuverCountDown ==0){
							if(x * (destinationX - centre.x) + z * (destinationY - centre.z) > movement.x * (destinationX - centre.x) + movement.z * (destinationY - centre.z)){
								boundary2D.setOrigin(xPos_old, yPos_old);
								changeMovement(tempAngle2);
								tightSpaceManeuverCountDown = 64;
								return;
							}
						}else{
							if((movement.x * (destinationX - centre.x) + movement.z * (destinationY - centre.z)) > 0){
								boundary2D.setOrigin(xPos_old, yPos_old);
								changeMovement(tempAngle2);
								return;
							}
						}
					}
				}
			}
			
			//Check if turning is possible
			if(obstacle.owner != null){
				if(obstacle.owner.getMovement().x == 0 && obstacle.owner.getMovement().z ==0){
					changeMovement(tempAngle1);
					movement.scale(16);
					xPos = (int)((centre.x + movement.x)*64) - 8;
					yPos = (int)((centre.z + movement.z)*64) + 8;
					boundary2D.setOrigin(xPos, yPos);
					//if turning is possible but it drives the tank further away from the destination then change 
					if(checkForCollision(boundary2D) == null){
						
						boolean shouldTurn = true;
						movement.scale(3.3f);
						float l = movement.getLength();
						if(distanceToDesination >= l){
							xPos = (int)((centre.x + movement.x)*64) - 8;
							yPos = (int)((centre.z + movement.z)*64) + 8;
							boundary2D.setOrigin(xPos, yPos);
							if(checkForCollision(boundary2D) != null && (!checkIfTileIsOccupiedByStaticUnitPoint(destinationX, destinationY) || distanceToDesination > 1.6)){
								
								shouldTurn = false;
							}
						}else{
							movement.scale(0.9f);
							xPos = (int)((centre.x + movement.x)*64) - 8;
							yPos = (int)((centre.z + movement.z)*64) + 8;
							boundary2D.setOrigin(xPos, yPos);
							if(checkForCollision(boundary2D) != null && !checkIfTileIsOccupiedByStaticUnitPoint(destinationX, destinationY) && !(distanceToDesination < 0.4f)){
								
								shouldTurn = false;
							}
						}
						
						if(shouldTurn){
							if((movement.x * (destinationX - centre.x) + movement.z * (destinationY - centre.z)) < 0){
								
								changeMovement(tempAngle3);
								xPos = (int)((centre.x + movement.x*8)*64) - 8;
								yPos = (int)((centre.z + movement.z*8)*64) + 8;
								boundary2D.setOrigin(xPos, yPos);
								
								if(checkForCollision(boundary2D) == null){
									
									
									changeMovement(tempAngle4);
									xPos2 = (int)((centre.x + movement.x*8)*64) - 8;
									yPos2 = (int)((centre.z + movement.z*8)*64) + 8;
									boundary2D.setOrigin((xPos+xPos2)/2, (yPos+yPos2)/2);
									
									
									
									if(checkForCollision(boundary2D) != null && isStable(obstacle.owner)){
										obstacle = checkForCollision(boundary2D);
										boundary2D.setOrigin(xPos_old, yPos_old);
										movement.reset();
										immediateDestinationAngle = tempAngle3;
										hugWallCoolDown = 15;
										
										
										if(currentMovementStatus == hugRight)
											currentMovementStatus = hugLeft;
										else
											currentMovementStatus = hugRight;
										return;
									}
								}
							}
							boundary2D.setOrigin(xPos_old, yPos_old);
							movement.reset();
							immediateDestinationAngle = tempAngle1;
							if(destinationImmediatelyReachable)
								hugWallCoolDown = 60;
							else
								hugWallCoolDown = 15;
							return;
						}
						
					}
				}
			}
		}
		
		
		changeMovement(tempAngle2);
		xPos = (int)((centre.x + movement.x)*64) - 8;
		yPos = (int)((centre.z + movement.z)*64) + 8;
		boundary2D.setOrigin(xPos, yPos);
		
		tempObstacle = checkForCollision(boundary2D);
		if(tempObstacle == null){
			changeMovement(tempAngle3);
			movement.scale(16);
			xPos = (int)((centre.x + movement.x)*64) - 8;
			yPos = (int)((centre.z + movement.z)*64) + 8;
			boundary2D.setOrigin(xPos, yPos);
			tempObstacle = checkForCollision(boundary2D);
			if(tempObstacle != null){
				if(tempObstacle.owner != null)
					if(isStable(tempObstacle.owner)){
						if((movement.x * (destinationX - centre.x) + movement.z * (destinationY - centre.z)) > 0){
							
							if(currentMovementStatus == hugRight)
								currentMovementStatus = hugLeft;
							else
								currentMovementStatus = hugRight;
						}
					}
			}
			
			changeMovement(tempAngle2);
			boundary2D.setOrigin(xPos_old, yPos_old);
			return;
		} else if(tempObstacle.owner != null){
			if((tempObstacle.owner.getMovement().x != 0 || tempObstacle.owner.getMovement().z != 0)  && !destinationImmediatelyReachable){ 

				movement.reset();
				boundary2D.setOrigin(xPos_old, yPos_old);
				return;
			}
		}
		
		
		if(obstacle.owner != null){
			if(obstacle.owner.getMovement().x == 0 && obstacle.owner.getMovement().z ==0){
				changeMovement(tempAngle3);
				xPos = (int)((centre.x + movement.x)*64) - 8;
				yPos = (int)((centre.z + movement.z)*64) + 8;
				boundary2D.setOrigin(xPos, yPos);
				if(checkForCollision(boundary2D) == null){
					boundary2D.setOrigin(xPos_old, yPos_old);
					movement.reset();
					immediateDestinationAngle = tempAngle3;
					return;
				}
			}
			
			if(obstacle.owner.getMovement().x == 0 && obstacle.owner.getMovement().z ==0){
				changeMovement(tempAngle4);
				xPos = (int)((centre.x + movement.x)*64) - 8;
				yPos = (int)((centre.z + movement.z)*64) + 8;
				boundary2D.setOrigin(xPos, yPos);
				if(checkForCollision(boundary2D) == null){
					boundary2D.setOrigin(xPos_old, yPos_old);
					movement.reset();
					immediateDestinationAngle = tempAngle4;
					return;
				}
			}
		}
		
		boundary2D.setOrigin(xPos_old, yPos_old);
		movement.reset();
		
	}
	
	public boolean checkIfDestinationReached(){
		distanceToDesination = (float)Math.sqrt((destinationX - centre.x) * (destinationX - centre.x) + (destinationY - centre.z) * (destinationY - centre.z));
		
		
		
		if(distanceToDesination < 1.5 && checkIfTileIsOccupiedByStaticUnitPoint(destinationX, destinationY)){
			closeToDestination = true;
			
			if(distanceToDesination > distanceToDesination_PreviousFrame){
				distanceToDesination_PreviousFrame = 9999;
				return true;
			}
			
			distanceToDesination_PreviousFrame = distanceToDesination;
			
		}else{
			if(closeToDestination){
				closeToDestination = false;
				distanceToDesination_PreviousFrame = 9999;
				return true;
			}
			
		}
		
		if(distanceToDesination > 1.5)
			return false;
		
		if(distanceToDesination < 0.1){
			insideDeistinationRadiusCount = 0;
			distanceToDesination_PreviousFrame = 9999;
			return true;
		}
		
		if(distanceToDesination < 0.5){
			insideDeistinationRadiusCount++;
		}else{
			insideDeistinationRadiusCount = 0;
		}
		
		if(insideDeistinationRadiusCount >= 64){
			insideDeistinationRadiusCount = 0;
			distanceToDesination_PreviousFrame = 9999;
			return true;
		}
		
		
		//if the desitnation is already occupied, test if the tank is adjacent  to destination tile
		xPos_old = boundary2D.x1;
		yPos_old = boundary2D.y1;
		xPos = (int)((centre.x + movement.x)*64) - 8;
		yPos = (int)((centre.z + movement.z)*64) + 8;
		boundary2D.setOrigin(xPos, yPos);
		if(checkForCollision(boundary2D) == null){
			
			if(distanceToDesination < 0.5 && checkIfTileIsOccupiedByStaticUnitPoint(destinationX, destinationY)){
				destinationBlock.setOrigin((int)(destinationX*64)-8, (int)(destinationY*64)+8);
				xPos = (int)((centre.x + (destinationX - centre.x)/32)*64) - 8;
				yPos = (int)((centre.z + (destinationY - centre.z)/32)*64) + 8;
				boundary2D.setOrigin(xPos, yPos);
				if(boundary2D.intersect(destinationBlock)){
					boundary2D.setOrigin(xPos_old, yPos_old);
					distanceToDesination_PreviousFrame = 9999;
					return true;
				}
			}
			
		
			
			boundary2D.setOrigin(xPos_old, yPos_old);
			return false;
		}
		
		if(distanceToDesination < 0.5){
			if(checkIfTileIsOccupiedByStaticUnitPoint(destinationX, destinationY)){
				boundary2D.setOrigin(xPos_old, yPos_old);
				distanceToDesination_PreviousFrame = 9999;
				return true;
			}
		}
		
			
		if(distanceToDesination < 1.2){
	
			if(checkIfTileIsOccupiedByStaticUnitProbe(destinationX + 0.25f, destinationY + 0.25f) &&
				checkIfTileIsOccupiedByStaticUnitProbe(destinationX + 0.25f, destinationY - 0.25f) &&	
			   checkIfTileIsOccupiedByStaticUnitProbe(destinationX - 0.25f, destinationY + 0.25f) &&
			   checkIfTileIsOccupiedByStaticUnitProbe(destinationX - 0.25f, destinationY - 0.25f)){
				boundary2D.setOrigin(xPos_old, yPos_old);
				distanceToDesination_PreviousFrame = 9999;
				return true;
			}
		}
		
		boundary2D.setOrigin(xPos_old, yPos_old);
		
		return false;
	}
	
	public void avoidGettingStucked(){
		//if the object can't move for some period then recalculate the path
		if(movement.x == 0 && movement.z == 0 && bodyAngleDelta == 0 && attackStatus != isAttacking){
			stuckCount++;
		}
		

		if(obstacle != null && attackStatus != isAttacking){
			if((unStableObstacle != null ||  !isStable(obstacle.owner)) && (ID + randomNumber + mainThread.gameFrame)%128 ==0){
				
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
	
	public String toString(){
		String label = "";
		if(type == 0)
			label+="lightTank";
		if(type == 1)
			label+="rocketTank";
		if(type == 101)
			label+="powerPlant";
		if(type == 2)
			label+="harvester";
		if(type==102)
			label+="refinery";
		if(type==3)
			label+="constructionVehichle";
		if(type ==4)
			label+="tokenObject";
		if(type== 6)
			label+="stealthTank";
		if(type == 7)
			label+="heavyTank";
		if(type==103)
			label+="goldMine";
		if(type==104)
			label+="constructionYard";
		if(type==105)
			label+="factory";
		if(type==106)
			label+="communicationCenter";
		if(type==107)
			label+="techCenter";
		if(type == 200)
			label+="gunTurret";
		if(type == 199)
			label+="missileTurret";
			
		return label + "    "  + centre.x + "   " + centre.z;
	}
	
	
	public void printCurrentCommand(){
		if(currentCommand == StandBy)
			System.out.println("currentCommand = Standby");
		if(currentCommand == move)
			System.out.println("currentCommand = move");
		if(currentCommand == attackCautiously)
			System.out.println("currentCommand = attackCautiously");
		if(currentCommand == attackInNumbers)
			System.out.println("currentCommand = attackInNumbers");
		if(currentCommand == attackMove)
			System.out.println("currentCommand = attackMove");
	}	
	
	public void printMovementStatus(){
		if(currentMovementStatus == freeToMove)
			System.out.println("currentMovementStatus = freeToMove");
		if(currentMovementStatus == hugLeft)
			System.out.println("currentMovementStatus = hugLeft");
		if(currentMovementStatus == hugRight)
			System.out.println("currentMovementStatus = hugRight");
	}
	
	public void printAttackStatus(){
		if(attackStatus == noTarget)
			System.out.println("attackStatus = noTarget");
		if(attackStatus == isAttacking)
			System.out.println("attackStatus = isAttacking");
		if(attackStatus == notInRange)
			System.out.println("attackStatus = notInRange");
	}
	
	
	public double getDistance(solidObject o){
		return Math.sqrt((centre.x - o.centre.x)*(centre.x - o.centre.x) + (centre.z - o.centre.z)*(centre.z - o.centre.z));
	}
	
	public void attackMoveTo(float destinationX, float destinationY){		
		
		
		movement.reset();
		turretAngleDelta = 0;
		bodyAngleDelta = 0;
		currentMovementStatus = freeToMove;
		attackStatus = noTarget;
		stuckCount = 0;
		//destinationX = centre.x;
		//destinationY = centre.z;
		insideDeistinationRadiusCount = 0;
		obstacle = null;
		closeToDestination = false;
		
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		newDestinationisGiven = true;
		
		secondaryDestinationX = destinationX;
		secondaryDestinationY = destinationY;	
	}
	
	public vector getMovement(){
		return movement;
	}
	
	public void moveTo(float destinationX, float destinationY){		
		float distanceToDestination = (destinationX - centre.x)*(destinationX - centre.x)  + (destinationY - centre.z)*(destinationY - centre.z);
		
		if((destinationX - this.destinationX)*(destinationX - this.destinationX)  + (destinationY - this.destinationY)*(destinationY - this.destinationY) > 0.05 || distanceToDestination  < 0.1){
			resetLogicStatus();
			
			this.destinationX = destinationX;
			this.destinationY = destinationY;
			
			
			newDestinationisGiven = true;
			
		}
	}
	

	
	
	
	public void attack(solidObject o){
		if(targetObject != o){
			targetObject = o;
			resetLogicStatus();
		}
	}
	
	public void resetLogicStatus(){
		if(movement != null)
			movement.reset();
		turretAngleDelta = 0;
		bodyAngleDelta = 0;
		currentMovementStatus = freeToMove;
		attackStatus = noTarget;
		stuckCount = 0;
		destinationX = centre.x;
		destinationY = centre.z;
		insideDeistinationRadiusCount = 0;
		obstacle = null;
		closeToDestination = false;
	}
	
	
	public boolean willDieFromIncomingAttack() {
		return currentHP - incomingDamage <= 0;
	}
	
	//to be implemented in child classes
	public void update(){}
	public void draw(){}
	public void harvest(solidObject o){}
	public void returnToRefinery(solidObject o){}
	public void hold(){currentCommand = StandBy;}
	public int getMaxHp(){return 0;}

}
