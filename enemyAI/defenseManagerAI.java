package enemyAI;

import core.baseInfo;
import core.mainThread;
import core.vector;
import entity.solidObject;
import entity.stealthTank;

public class defenseManagerAI {
	public baseInfo theBaseInfo;
	
	public int gameTime;
	
	public int currentState;
	public final int booming = 0;
	public final int aggressing = 1;
	public final int defending = 2;

	public solidObject[] observers;
	 
	public solidObject[] stealthTanksControlledByCombatAI;
	
	public solidObject[] defenders;
	
	public vector direction;
	
	public vector minorThreatLocation;
	public vector majorThreatLocation;
	
	public defenseManagerAI(baseInfo theBaseInfo){
		this.theBaseInfo = theBaseInfo;
		
		observers = new solidObject[4];
		
		defenders = new solidObject[5];
		
		direction = new vector(0,0,0);
		
		minorThreatLocation = new vector(0,0,0);
		majorThreatLocation = new vector(0,0,0);
			
	}
	
	
	//at 500 seconds mark, send 2 observers to the 2 western and southern side of the main base. After grabbing the northwest (or southeast, depends on which expansion get grabbed first) expansion 
	//send 2 additional observers to look for player sneak attacks
	
	
	public void processAI(){
		gameTime++;
		
		currentState = mainThread.ec.theCombatManagerAI.currentState;
		
		stealthTanksControlledByCombatAI = mainThread.ec.theUnitProductionAI.stealthTanksControlledByCombatAI;
		
		//after 500 seconds mark, borrow 2 stealth tanks from combat manager, and send them to guard western and southern side of the main base
		if(gameTime >= 480) {
			for(int i = 0; i < 2; i++) {
				if(observers[i] == null || observers[i].currentHP <=0) {
					for(int j = 0; j < stealthTanksControlledByCombatAI.length; j++) {
						if(stealthTanksControlledByCombatAI[j] != null && stealthTanksControlledByCombatAI[j].currentHP == 80 && stealthTanksControlledByCombatAI[j].attackStatus != solidObject.isAttacking) {
							observers[i] = stealthTanksControlledByCombatAI[j];
							stealthTanksControlledByCombatAI[j] = null;
							float xPos = 21f;
							float zPos = 30.5f;
							
							if(i == 1) {
								xPos = 30f;
								zPos = 20f;
							}
							observers[i].moveTo(xPos, zPos);
							observers[i].currentCommand = solidObject.move;
							observers[i].secondaryCommand = solidObject.StandBy;
							break;
						}
					}
				}
			}
		}
		
		//keep an eye on player units and avoid being detected
		for(int i = 0; i < observers.length; i++) {
			if(observers[i] != null) {
				
				if(!evadePlayerUnit(i)) {
					float xPos = 0;
					float zPos = 0;
					
					//if there is no player units in sight, return to patrol position
					if(i == 0) {
						if(gameTime%30 < 15) {
							xPos = 19f;
							zPos = 30.5f;
						}else {
							xPos = 19f;
							zPos = 22.5f;
						}
							
					}
					
					if(i == 1) {
						
						if(gameTime%20 < 10) {
							xPos = 30f;
							zPos = 20f;
						}else {
							xPos = 26f;
							zPos = 20f;
						}
						
					}
					observers[i].moveTo(xPos, zPos);
					observers[i].currentCommand = solidObject.move;
					observers[i].secondaryCommand = solidObject.StandBy;
				}
				
			}
		}
		
		//send units to deal with minor threat on the map if there is any
		vector mainPlayerForceLocation = mainThread.ec.theMapAwarenessAI.mainPlayerForceLocation;
		vector mainPlayerForceDirection = mainThread.ec.theMapAwarenessAI.mainPlayerForceDirection;
		int mainPlayerForceSize = mainThread.ec.theMapAwarenessAI.mainPlayerForceSize;
		
		minorThreatLocation.reset();
		majorThreatLocation.reset();
		
		// if the size of the player unit cluster is less than 5, and no heavy tanks in the cluster, then borrow some unites from combatAI to deal with the threat
		//if(mainPlayerForceSize < 5 && playerForceContainsNoHeavyTank(mainPlayerForceLocation) && playerForceIsNearBase(mainPlayerForceLocation)) {
			
		//}
		

	}
	
	
	
	public boolean evadePlayerUnit(int  observerIndex){
		//scan for hostile unit
		int[] tileCheckList = stealthTank.tileCheckList;
		
		int currentOccupiedTile = (int)(observers[observerIndex].centre.x*64)/16 + (127 - (int)(observers[observerIndex].centre.z*64)/16)*128;
		
	
		direction.set(0,0,0);
		boolean directionSet = false;
		
		for(int i = 0; i < tileCheckList.length; i++){
			if(tileCheckList[i] != Integer.MAX_VALUE){
				int index = currentOccupiedTile + tileCheckList[i];
				if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
					continue;
				solidObject[] tile = mainThread.gridMap.tiles[index];
				
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){
						if(tile[j].teamNo != observers[observerIndex].teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
					
							
							if(directionSet)
								break;
							
							double d = Math.sqrt((tile[j].centre.x - observers[observerIndex].centre.x)*(tile[j].centre.x - observers[observerIndex].centre.x)  
									           + (tile[j].centre.z - observers[observerIndex].centre.z)*(tile[j].centre.z - observers[observerIndex].centre.z));
							
							if(d < 0.75) {
								
								direction.set(observers[observerIndex].centre);
								direction.subtract(tile[j].centre);
								direction.unit();
								
								directionSet = true;
							}
						}
					}
				}
			}
		}
		
		
		if(directionSet) {
			observers[observerIndex].moveTo(observers[observerIndex].centre.x + direction.x , observers[observerIndex].centre.z + direction.z);
			observers[observerIndex].currentCommand = solidObject.move;
			observers[observerIndex].secondaryCommand = solidObject.StandBy;
		}
		
		return directionSet; 
		
	}
	
}
