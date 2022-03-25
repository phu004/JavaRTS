//This AI agent will perform hit and run tactics against the player.
//It will try to harass plahyer's mineral line, and destroy player's building from a distance.
//It will most likely act at the same time when the AI's main attack force is launching an attack, 

package enemyAI;

import core.baseInfo;
import core.gameData;
import core.mainThread;
import core.vector;
import entity.rocketTank;
import entity.solidObject;
import entity.stealthTank;

public class harassmentAI {
	
	public baseInfo theBaseInfo;
	
	
	public int frameAI;
	public int miniFrameAI;
	public stealthTank scout;
	public rocketTank[] squad;
	public int status;
	public final int gathering = 0;
	public final int positioning = 1;
	public final int harasing = 2;
	public final int retreating = 3;
	public stealthTank[] stealthTanksControlledByCombatAI;
	public rocketTank[] rocketTanksControlledByCombatAI;
	public vector targetLocation, gatherLocation, squadCenter, harassDirection;
	public int harassTimer;

	
	public harassmentAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;
		squad = new rocketTank[3];
		status = gathering;
		targetLocation = new vector(0,0,0);
		gatherLocation = new vector(0,0,0);
		squadCenter = new vector(0,0,0);
		harassDirection = new vector(0,0,0);
	}
	
	public void processAI(){
		miniFrameAI++;
		frameAI = mainThread.ec.frameAI;
			
		//only activate this AI after 660 game seconds (about 9 minutes in real time)
		if(frameAI < 660)
			return;
		
		stealthTanksControlledByCombatAI = mainThread.ec.theUnitProductionAI.stealthTanksControlledByCombatAI;
		rocketTanksControlledByCombatAI = mainThread.ec.theUnitProductionAI.rocketTanksControlledByCombatAI;
		
		
		
		
		if(status == gathering) {
		
			if(scout == null || scout.currentHP <=0) {
				for(int i = 0; i < stealthTanksControlledByCombatAI.length; i++) {
					if(stealthTanksControlledByCombatAI[i] != null && stealthTanksControlledByCombatAI[i].currentHP == stealthTank.maxHP && stealthTanksControlledByCombatAI[i].attackStatus != solidObject.isAttacking) {
						if(hasRoomToMove(stealthTanksControlledByCombatAI[i])) {
							scout = stealthTanksControlledByCombatAI[i];
							stealthTanksControlledByCombatAI[i] = null;
							break;
						}
					}
				}
			}
			
			if(scout != null) {
				scout.moveTo(mainThread.ec.theUnitProductionAI.rallyPoint.x - 1,mainThread.ec.theUnitProductionAI.rallyPoint.z);
				scout.currentCommand = solidObject.move;
				scout.secondaryCommand = solidObject.StandBy;
			}
			
			for(int i = 0; i < squad.length; i++) {
				if(squad[i] == null || squad[i].currentHP <=0) {
					for(int j = 0; j < rocketTanksControlledByCombatAI.length; j++) {
						if(rocketTanksControlledByCombatAI[j] != null && rocketTanksControlledByCombatAI[j].currentHP == rocketTank.maxHP && rocketTanksControlledByCombatAI[j].attackStatus != solidObject.isAttacking) {
							if(hasRoomToMove(rocketTanksControlledByCombatAI[j])) {
								squad[i] = rocketTanksControlledByCombatAI[j];
								rocketTanksControlledByCombatAI[j] = null;
								break;
							}
						}
					}
				}
			}
			int numberOfSquad = 0;
			for(int i = 0; i < squad.length; i++) {
				if(squad[i] != null && squad[i].currentHP > 0) {
					squad[i].attackMoveTo(mainThread.ec.theUnitProductionAI.rallyPoint.x - 1,mainThread.ec.theUnitProductionAI.rallyPoint.z);
					squad[i].currentCommand = solidObject.attackMove;
					squad[i].secondaryCommand = solidObject.attackMove;
					numberOfSquad++;
				}
			}
			
			if(numberOfSquad == squad.length && scout !=null && scout.currentHP > 0) {
				status = positioning;
				targetLocation.set(0,0,0);
				
				//find the location of the target location and gather location
				
				if(gameData.getRandom() >= 512) {
					if(playerBaseIsAround(mainThread.theAssetManager.goldMines[2].centre)) {
						targetLocation = mainThread.theAssetManager.goldMines[2].centre;
						gatherLocation.set(15, 0, 28);
					}else if(playerBaseIsAround(mainThread.theAssetManager.goldMines[3].centre)) {
						targetLocation = mainThread.theAssetManager.goldMines[3].centre;
						gatherLocation.set(28.5f, 0, 15);
					}
				}else {
					if(playerBaseIsAround(mainThread.theAssetManager.goldMines[3].centre)) {
						targetLocation = mainThread.theAssetManager.goldMines[3].centre;
						gatherLocation.set(28.5f, 0, 15);
					}else if(playerBaseIsAround(mainThread.theAssetManager.goldMines[2].centre)) {
						targetLocation = mainThread.theAssetManager.goldMines[2].centre;
						gatherLocation.set(15, 0, 28);
					}
				}
				
				if(targetLocation.x == 0 && targetLocation.z == 0) {
					targetLocation.set(1.5f, 0, 1.5f);
					if(gameData.getRandom() >= 512) {
						gatherLocation.set(1.5f, 0, 15);
					}else {
						gatherLocation.set(15, 0, 1.5f);
					}
				}
					
			}
		}else if(status == positioning) {
			//move squad and scout to gathering point
			int numberOfSquad = 0;
			for(int i = 0; i < squad.length; i++) {
				if(squad[i] != null && squad[i].currentHP > 0 ) {
					numberOfSquad++;
					if(squad[i].secondaryDestinationX != gatherLocation.x || squad[i].secondaryDestinationY != gatherLocation.z){
						squad[i].attackMoveTo(gatherLocation.x,gatherLocation.z);
						squad[i].currentCommand = solidObject.attackMove;
						squad[i].secondaryCommand = solidObject.attackMove;
					}
				}
			}
			
			
			if(scout != null) {
				scout.moveTo(gatherLocation.x,gatherLocation.z);
				scout.currentCommand = solidObject.move;
				scout.secondaryCommand = solidObject.StandBy;
			}
			
			boolean scoutInPosition = true;
			float x = scout.centre.x;
			float z = scout.centre.z;
			double d = Math.sqrt((x - gatherLocation.x)*(x - gatherLocation.x) + (z - gatherLocation.z)*(z - gatherLocation.z));
			if(d > 0.75)
				scoutInPosition = false;
			
			boolean squadInPosition = true;
			for(int i = 0; i < squad.length; i++) {
				x = squad[i].centre.x;
				z = squad[i].centre.z;
				d = Math.sqrt((x - gatherLocation.x)*(x - gatherLocation.x) + (z - gatherLocation.z)*(z - gatherLocation.z));
				if(d > 1) {
					squadInPosition = false;
					break;
				}
			}
			
			if(scoutInPosition && squadInPosition) {
				status = harasing;
				harassTimer = 0;
			}
			
			
			
			if(numberOfSquad < squad.length || scout == null || scout.currentHP <=0) {
				status = gathering;
			}
			
		}else if(status == harasing) {
			harassTimer++;
			
			//move squad towards target base
			squadCenter.reset();
			int numberOfSquad = 0;
			
			for(int i = 0; i < squad.length; i++) {
				if(squad[i] != null  && squad[i].currentHP > 0) {
					if(squad[i].secondaryDestinationX != targetLocation.x || squad[i].secondaryDestinationY != targetLocation.z) {
						if(harassTimer > 200) {  //delay the squad moment a little bit, make sure scout stays ahead
							squad[i].attackMoveTo(targetLocation.x,targetLocation.z);
							squad[i].currentCommand = solidObject.attackMove;
							squad[i].secondaryCommand = solidObject.attackMove;
						}
					}
					
					squadCenter.add(squad[i].centre);
					numberOfSquad++;
				}
			}
			
			if(numberOfSquad > 0) {
				squadCenter.x/=numberOfSquad;
				squadCenter.z/=numberOfSquad;
				
				harassDirection.set(targetLocation.x,0,targetLocation.z);
				harassDirection.subtract(squadCenter);
				harassDirection.unit();
				
				if(scout != null) {
					
					boolean squadIsUnderAttack = false;
					for(int i = 0; i < squad.length; i++) {
						if(squad[i] != null && squad[i].underAttackCountDown > 0) {
							squadIsUnderAttack = true;
						}
					}
					if(scout.underAttackCountDown > 0)
						squadIsUnderAttack = true;
				
					if(!squadIsUnderAttack) {
						scout.moveTo(squadCenter.x + harassDirection.x*1.5f, squadCenter.z + harassDirection.z*1.5f);
						scout.currentCommand = solidObject.move;
						scout.secondaryCommand = solidObject.StandBy;
					}else if(miniFrameAI%30 == 29){
						scout.attackMoveTo(squadCenter.x, squadCenter.z);
						scout.currentCommand = solidObject.attackMove;
						scout.secondaryCommand = solidObject.attackMove;
					}
				}
			}
			
			//attack the first building within range.
			solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
			for(int i = 0; i < playerStructures.length; i++) {
				if(playerStructures[i] != null && playerStructures[i].currentHP >0) {
					float x = playerStructures[i].centre.x;
					float z = playerStructures[i].centre.z;
					double d = Math.sqrt((squadCenter.x-x)*(squadCenter.x-x) + (squadCenter.z-z)*(squadCenter.z-z));
					if(d < 2.8) {
						for(int j = 0; j < squad.length; j++) {
							if(squad[j] != null  && squad[j].currentHP > 0) {
								double d1 = Math.sqrt((squad[j].centre.x-x)*(squad[j].centre.x-x) + (squad[j].centre.z-z)*(squad[j].centre.z-z));
								if(d1 < 2.86) {
									squad[j].attack(playerStructures[i]);
									squad[j].currentCommand = solidObject.attackCautiously;
								}
							}
						}
						break;
					}
				}
			}
			
			
			//if the target location is clear of player base, then chance status to gathering
			double distance = Math.sqrt((squadCenter.x-targetLocation.x)*(squadCenter.x-targetLocation.x) + (squadCenter.z-targetLocation.z)*(squadCenter.z-targetLocation.z));
			if(distance < 1) {
				if(!playerBaseIsAround(targetLocation)) {
					status = gathering;
				}
			}
			
			//if all squad members are dead, change status to gathering
			if(numberOfSquad == 0) {
				status = gathering;
			}
	
			
		}
		
	}
	
	public boolean playerBaseIsAround(vector v) {
		solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
		for(int i = 0; i < playerStructures.length; i++) {
			if(playerStructures[i] != null && playerStructures[i].currentHP >0) {
				float x = playerStructures[i].centre.x;
				float z = playerStructures[i].centre.z;
				double d = Math.sqrt((v.x-x)*(v.x-x) + (v.z-z)*(v.z-z));
				if(d < 3.5) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean hasRoomToMove(solidObject o) {
		float x = o.centre.x;
		float z = o.centre.x;
			
		solidObject[] s1 = mainThread.gridMap.tiles[(int)((x+0.25)*4) + (127 - (int)(z*4))*128];
		boolean hasRoomToMove = true;
		for(int i = 0; i < s1.length; i++) {
			if(s1[i] != null && s1[i] != o) {
				hasRoomToMove = false;
				break;
			}
		}
		
		if(hasRoomToMove)
			return true;
		
		solidObject[] s2 = mainThread.gridMap.tiles[(int)((x-0.25)*4) + (127 - (int)(z*4))*128];
		hasRoomToMove = true;
		for(int i = 0; i < s2.length; i++) {
			if(s2[i] != null && s2[i] != o) {
				hasRoomToMove = false;
				break;
			}
		}
		
		if(hasRoomToMove)
			return true;
		
		solidObject[] s3 = mainThread.gridMap.tiles[(int)(x*4) + (127 - (int)((z + 0.25)*4))*128];
		hasRoomToMove = true;
		for(int i = 0; i < s3.length; i++) {
			if(s3[i] != null && s3[i] != o) {
				hasRoomToMove = false;
				break;
			}
		}
		
		if(hasRoomToMove)
			return true;
		
		solidObject[] s4 = mainThread.gridMap.tiles[(int)(x*4) + (127 - (int)((z - 0.25)*4))*128];
		hasRoomToMove = true;
		for(int i = 0; i < s4.length; i++) {
			if(s4[i] != null && s4[i] != o) {
				hasRoomToMove = false;
				break;
			}
		}
		
		if(hasRoomToMove)
			return true;
		
		
		return false;
	}
}
