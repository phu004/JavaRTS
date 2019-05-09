//This AI agent will perform hit and run tactics against the player.
//It will try to harass plahyer's mineral line, and destroy player's building from a distance.
//It will most likely act at the same time when the AI's main attack force is launching an attack, 

package enemyAI;

import core.baseInfo;
import core.mainThread;
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

	
	public harassmentAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;
		squad = new rocketTank[3];
		status = gathering;
	}
	
	public void processAI(){
		miniFrameAI++;
		frameAI = mainThread.ec.frameAI;
		
		if(miniFrameAI%30 == 29) {
			System.out.println(frameAI + "    "  + mainThread.ec.theUnitProductionAI.numberOfRocketTanksControlledByCombatAI);
		}
			
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
				if(squad[i] != null) {
					squad[i].attackMoveTo(mainThread.ec.theUnitProductionAI.rallyPoint.x - 1,mainThread.ec.theUnitProductionAI.rallyPoint.z);
					squad[i].currentCommand = solidObject.attackMove;
					squad[i].secondaryCommand = solidObject.attackMove;
					numberOfSquad++;
				}
			}
			
			if(numberOfSquad == squad.length && scout !=null) {
				status = positioning;
			}
		}else if(status == positioning) {
			
		}
		
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
