package enemyAI;

import core.BaseInfo;
import core.MainThread;
import core.Rect;
import core.vector;
import entity.SolidObject;
import entity.LightTank;

public class ScoutingManagerAI {

	public BaseInfo theBaseInfo;
	
	public int frameAI;
	
	public int scoutingMode;
	
	public final int patrolling = 0;
	public final int exploring = 1;
	
	public float[][] patrolNodes;
	public float[][] exploringNodes;
	
	public int destinationNode;
	
	public boolean movementOrderIssued;
	
	public vector tempVector1, tempVector2, tempVector3;
	
	public int avoidingIncomingPlayerUnitCooldown;
	
	//scout unit consists a sole light tank
	public SolidObject scout;
	
	public ScoutingManagerAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
		
		patrolNodes = new float[][]{
				{16, 30}, {2, 29}, {15, 17}, {16, 14}, {27f, 1}, {30, 16}, {16, 14}, {15, 17}
		};
		
		exploringNodes = new float[][]{
				{8f, 3f}, {2,2}, {3, 6},{11.5f, 7.5f}
				
				
		};
		
		destinationNode = 0;
		
		tempVector1 = new vector(0,0,0);
		tempVector2 = new vector(0,1,0);
		tempVector3 = new vector(0,0,0);
		
	}
	
	public void processAI(){
		
		frameAI = MainThread.enemyCommander.frameAI;
		
		if(avoidingIncomingPlayerUnitCooldown > 0)
			avoidingIncomingPlayerUnitCooldown--;
		
		boolean scoutIsLightTank = scout != null && scout.type == 0;
		if((frameAI%275 > 235 && frameAI%275 < 275 && !scoutIsLightTank) && frameAI < 900 && scoutingMode == patrolling || (scoutIsLightTank && frameAI < 240)){
			scoutingMode = exploring;
			destinationNode = 0;
			movementOrderIssued = false;
		}
		
		//produce a scout unit if there is no scout unit on the map or the scout unit has been destroyed

		int numberOfLightTankOnQueue = 0;
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
				numberOfLightTankOnQueue += MainThread.theAssetManager.factories[i].numOfLightTankOnQueue;
			}
		}
		if(numberOfLightTankOnQueue == 0 && needLightTank()){
			for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
				if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
					if(MainThread.theAssetManager.factories[i].isIdle()){
						MainThread.theAssetManager.factories[i].buildLightTank();
						break;
					}
				}
			}
		}
		
		int numberOfStealthTankOnQueue = 0;
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
				numberOfStealthTankOnQueue += MainThread.theAssetManager.factories[i].numOfStealthTankOnQueue;
			}
		}
		if(numberOfStealthTankOnQueue == 0 && needStealthTank()){
			for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
				if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
					if(MainThread.theAssetManager.factories[i].isIdle()){
						MainThread.theAssetManager.factories[i].buildStealthTank();
						break;
					}
				}
			}
		}
		
		if(scout == null || scout.currentHP <=0){
			scout = null;
			movementOrderIssued= false;
			scoutingMode = patrolling;
			destinationNode = (int)(Math.random()*patrolNodes.length); 
		}
		
		if(scout != null){
			
			
			
			if(avoidingIncomingPlayerUnitCooldown == 0){
			
				if(scoutingMode == patrolling){
					destinationNode = destinationNode%patrolNodes.length;
					if(!scountReachedDestination(scout, patrolNodes, destinationNode)){
						
						if(!movementOrderIssued){
							
							scout.moveTo(patrolNodes[destinationNode][0], patrolNodes[destinationNode][1]);
							scout.currentCommand = SolidObject.move;
							scout.secondaryCommand = SolidObject.StandBy;
							
							if(scout.leftFactory)
								movementOrderIssued = true;
						
						}
						
					}else{
						destinationNode++;
						movementOrderIssued = false;
						
					}
				}
				
				if(scoutingMode == exploring){
					
					
					if(!scountReachedDestination(scout, exploringNodes, destinationNode)){
						
						if(!movementOrderIssued){
							
							scout.moveTo(exploringNodes[destinationNode][0], exploringNodes[destinationNode][1]);
							scout.currentCommand = SolidObject.move;
							scout.secondaryCommand = SolidObject.StandBy;
							
				 
							if(scout.leftFactory)
								movementOrderIssued = true;
						}
						
					}else{
						destinationNode++;
						movementOrderIssued = false;
					}
					
					if(destinationNode >0 && destinationNode %exploringNodes.length == 0){
						scoutingMode = patrolling;
						destinationNode = 0;
						
					}
				}
			
			
				
				if(scout.type == 0) {
					
					
					
					
					if(MainThread.enemyCommander.theDefenseManagerAI.minorThreatLocation.x != 0 || MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.x != 0 || (!MainThread.enemyCommander.theMapAwarenessAI.canRushPlayer && frameAI > 240)) {
						if(scout.currentHP > 0) {							
							scout.moveTo(MainThread.enemyCommander.theUnitProductionAI.rallyPoint.x, MainThread.enemyCommander.theUnitProductionAI.rallyPoint.z);
							scout.currentCommand = SolidObject.attackMove;
							scout.secondaryCommand = SolidObject.attackMove;
							
							if(frameAI > 310) {
								MainThread.enemyCommander.theUnitProductionAI.addLightTank((LightTank)scout);
								scout = null;
							}
						}
					}else if(MainThread.enemyCommander.theMapAwarenessAI.canRushPlayer && frameAI > 290) {
						MainThread.enemyCommander.theUnitProductionAI.addLightTank((LightTank)scout);
						
						scout.moveTo(MainThread.enemyCommander.theUnitProductionAI.rallyPoint.x, MainThread.enemyCommander.theUnitProductionAI.rallyPoint.z);
						scout.currentCommand = SolidObject.attackMove;
						scout.secondaryCommand = SolidObject.attackMove;
						scout = null;
					}
					
					return;
				}
				
				//try to avoid collision with player units
				int xPos_old = scout.boundary2D.x1;
				int yPos_old = scout.boundary2D.y1;
				
				int xPos = xPos_old;
				int yPos = yPos_old;
				
				if(scout.movement.x != 0 || scout.movement.z != 0){
					tempVector1.set(scout.movement);
					tempVector1.unit();
					tempVector1.scale(0.1f);
					
					for(int i = 1; i < 20; i ++){
						xPos = (int)((scout.centre.x + tempVector1.x*i)*64) - 8;
						yPos = (int)((scout.centre.z + tempVector1.z*i)*64) + 8;
						
						scout.boundary2D.setOrigin(xPos, yPos);
						
						//increased size of the 2D boundary of the scount for better collision detection.
						scout.boundary2D.expand(8);
					
						SolidObject obstacle = checkForCollision(scout.boundary2D);
						
						scout.boundary2D.shrink(8);
						
						
						//ignore Harvesters
						if(obstacle != null && obstacle.type == 2)
							continue;
						
						
						if(obstacle != null && !(i > 10 && ((obstacle.movement.x == 0 && obstacle.movement.z == 0) || (tempVector1.dot(obstacle.movement) > 0)))){
							
							tempVector3.cross(tempVector1, tempVector2);
							tempVector3.unit();
							tempVector3.scale(2);
							
							if(tempVector3.dot(obstacle.movement) > 0)
								tempVector3.scale(-1);
							
							avoidingIncomingPlayerUnitCooldown = 2;
							
							scout.moveTo(scout.centre.x + tempVector3.x, scout.centre.z + tempVector3.z);
							scout.currentCommand = SolidObject.move;
							scout.secondaryCommand = SolidObject.StandBy;
							movementOrderIssued = false;
							break;
						}
						
					}
				}
				scout.boundary2D.setOrigin(xPos_old, yPos_old);
			}
			
		}

		
	}
		
	public SolidObject checkForCollision(Rect myRect){
			
		//check if the tank collide with the border
		if(myRect.x1 < 0 || myRect.x2 > 2047 || myRect.y2 < 1 || myRect.y1 > 2048){
			return null;
		}
		

		int newOccupiedTile0= myRect.x1/16 + (127 - myRect.y1/16)*128;	
		int newOccupiedTile1 = newOccupiedTile0 + 1;
		int newOccupiedTile2 = newOccupiedTile0 + 128;
		int newOccupiedTile3 = newOccupiedTile0 + 129;
		
		SolidObject tempObstacle = null;
		SolidObject[] tile;
		
		if(newOccupiedTile0 >= 0 && newOccupiedTile0 < 16384){
			tile = MainThread.gridMap.tiles[newOccupiedTile0];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].teamNo == 0 && tile[i].type < 100 && !tile[i].isCloaked)
						return tile[i];
				}
			}
		}
		
		if(newOccupiedTile1 >= 0 && newOccupiedTile1 < 16384){
			tile = MainThread.gridMap.tiles[newOccupiedTile1];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].teamNo == 0 && tile[i].type < 100 && !tile[i].isCloaked)
						return tile[i];
				}
			}
		}
	
		if(newOccupiedTile2 >= 0 && newOccupiedTile2 < 16384){
			tile = MainThread.gridMap.tiles[newOccupiedTile2];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].teamNo == 0 && tile[i].type < 100 && !tile[i].isCloaked)
						return tile[i];
				}
			}
		}
	
		if(newOccupiedTile3 >= 0 && newOccupiedTile3 < 16384){
			tile = MainThread.gridMap.tiles[newOccupiedTile3];
			for(int i = 0; i < 5; i++){
				if(tile[i] != null){
					if(tile[i].boundary2D.intersect(myRect) && tile[i].teamNo == 0 && tile[i].type < 100 && !tile[i].isCloaked)
						return tile[i];
				}
			}
		}
	
		return tempObstacle;
	}
	
	public boolean scountReachedDestination(SolidObject o, float[][] nodes, int nodeIndex){
		float distanceToDestination = (float)Math.sqrt((o.centre.x - nodes[nodeIndex][0]) * (o.centre.x - nodes[nodeIndex][0]) + (o.centre.z - nodes[nodeIndex][1]) * (o.centre.z - nodes[nodeIndex][1]));
	
		
		if(distanceToDestination <= 0.1f)
			return true;
		
		if(distanceToDestination <= 1.5f &&  (o.currentMovementStatus == o.hugLeft || o.currentMovementStatus == o.hugRight))
			return true;
			
		return false;
	}
	
	
	//build light tank as scout when stealth tank tech is locked
	public boolean needLightTank(){
		if(frameAI < 200 &&  scout == null  && !theBaseInfo.canBuildStealthTank && MainThread.enemyCommander.theDefenseManagerAI.minorThreatLocation.x == 0 && MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.x == 0){
			return true;
		}
		
		return false;
	}
	
	public boolean needStealthTank(){
		if(theBaseInfo.canBuildStealthTank){
			if((scout == null || scout.currentHP <= 0 || scout.type != 6) && frameAI > 380){
				return true;
			}
		}
		return false;
	}
	
	public void addLightTank(SolidObject o){
		scout = o;
	}

}
