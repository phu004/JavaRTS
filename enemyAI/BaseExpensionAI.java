package enemyAI;

import core.BaseInfo;
import core.GameData;
import core.MainThread;
import core.vector;
import entity.*;

public class BaseExpensionAI {
	public BaseInfo theBaseInfo;
	public int[] expensionPiorityList;
	public boolean expensionListRerolled;
	public StealthTank[] scouts;
	public ConstructionVehicle myMCV;
	public boolean isExpanding;
	public int targetExpension;
	public GoldMine[] goldMines;
	public GoldMine expensionGoldMine;
	public int numberOfActiveScout;
	public int numberOfStealthTankScout;
	public int frameAI;
	public vector temp; 
	public boolean allExpansionOccupied;
	public int lastExpansionLocation;
	public int  lowGoldmineThreshold;
	
	
	public BaseExpensionAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
		temp = new vector(0,0,0);
		
		//generate a expension piority list
		expensionPiorityList = new int[5];
		
		int randomeNumber = GameData.getRandom();
		
		if(randomeNumber < 100)
			expensionPiorityList = new int[]{5,6,2,3,7};
		else if(randomeNumber >= 100 && randomeNumber < 200)
			expensionPiorityList = new int[]{5,6,3,2,7};
		else if(randomeNumber >= 200 && randomeNumber < 407)
			expensionPiorityList = new int[]{5,2,6,3,7};
		else if(randomeNumber >= 407 && randomeNumber < 614)
			expensionPiorityList = new int[]{5,2,3,6,7};
		else if(randomeNumber >= 614 && randomeNumber < 821)
			expensionPiorityList = new int[]{5,3,6,2,7};
		else
			expensionPiorityList = new int[]{5,3,2,6,7};
		
		scouts = new StealthTank[3];
		
		lastExpansionLocation = 7;
		
		lowGoldmineThreshold = 22500;
	}
	
	
	public void processAI(){
	
		frameAI = MainThread.enemyCommander.frameAI;
		
		//when all the expansion position has been utilized then do nothing 
		if(allExpansionOccupied)
			return;
		
		if(frameAI > 750 && frameAI < 1000 && !expensionListRerolled) {
			//if the AI has smaller force than player when it's time to grab a third base,  use the less aggressive base expansion route
			if(MainThread.enemyCommander.theCombatManagerAI.checkIfAIHasBiggerForce(1) == false && expensionPiorityList[targetExpension] == 6) {
				int randomeNumber = GameData.getRandom();
				if(randomeNumber < 512)
					expensionPiorityList = new int[]{5,2,3,6,7};
				else
					expensionPiorityList = new int[]{5,3,2,6,7};
				expensionListRerolled = true;
			}
			
		}
		
		if(goldMines == null)
			goldMines = MainThread.theAssetManager.goldMines;
		
		//find the next potential expansion location
		for(int i = 0; i<5; i++){	
			if(!hasRefineryNearTheGoldmine(goldMines[expensionPiorityList[i]]) && !hasConstructionYardNearGoldMine(goldMines[expensionPiorityList[i]]) && goldMines[expensionPiorityList[i]].goldDeposite > 17500){
				targetExpension = i;
				break;
			}
		}
		
		if(expensionGoldMine == null)
			expensionGoldMine = goldMines[expensionPiorityList[targetExpension]];
		else {
			if(goldMines[expensionPiorityList[targetExpension]].goldDeposite > expensionGoldMine.goldDeposite)
				expensionGoldMine = goldMines[expensionPiorityList[targetExpension]];
		}
		
		//produce a total of 3  scout units, check if there are any stealth tank in the production
		numberOfActiveScout = 0;
		numberOfStealthTankScout = 0;
		for(int i = 0; i < scouts.length; i++){
			if(scouts[i] != null && scouts[i].currentHP >0){
				numberOfActiveScout++;
				if(scouts[i].type == 6)
					numberOfStealthTankScout++;
			}
		}
		
		int numberOfStealthTankOnQueue = 0;
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
				numberOfStealthTankOnQueue += MainThread.theAssetManager.factories[i].numOfStealthTankOnQueue;
			}
		}
		
		int numberOfUnassignedStealthTank = 0;
		for(int i = 0; i < MainThread.theAssetManager.stealthTanks.length; i++){
			if(MainThread.theAssetManager.stealthTanks[i] != null && MainThread.theAssetManager.stealthTanks[i].teamNo != 0 && MainThread.enemyCommander.theMapAwarenessAI.mapAsset[MainThread.theAssetManager.stealthTanks[i].ID] == null)
				numberOfUnassignedStealthTank++;
				
		}

		
		int scoutsNumberLimit = scouts.length;
		if(frameAI <= 800)
			scoutsNumberLimit = scouts.length - 5;
		
			
		
		//pick an idle Factory to produce stealth tank. If there is no idle Factory, cancel the one that is building LightTank
		if(numberOfActiveScout + numberOfStealthTankOnQueue + numberOfUnassignedStealthTank < scoutsNumberLimit && theBaseInfo.canBuildStealthTank){
			for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
				if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
					if(MainThread.theAssetManager.factories[i].lightTankProgress < 240 || MainThread.theAssetManager.factories[i].isIdle()){
						MainThread.theAssetManager.factories[i].cancelItemFromProductionQueue(Factory.lightTankType);
						MainThread.theAssetManager.factories[i].buildStealthTank();
						break;
					}
				}
			}
			
		}
				
		//build a mcv when the current mine is running low
		myMCV = null;
		for(int i = 0; i <  MainThread.theAssetManager.constructionVehicles.length; i++){
			if( MainThread.theAssetManager.constructionVehicles[i] != null && MainThread.theAssetManager.constructionVehicles[i].currentHP >0 && MainThread.theAssetManager.constructionVehicles[i].teamNo != 0){
				myMCV = MainThread.theAssetManager.constructionVehicles[i];
			}
		}
		
		
		boolean playerHasLessUnits = MainThread.enemyCommander.theCombatManagerAI.checkIfAIHasBiggerForce(1f);
				
		if(playerHasLessUnits) {
			lowGoldmineThreshold = 32500;
			
			if(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine == MainThread.theAssetManager.goldMines[4])
				lowGoldmineThreshold = 30000;
			
			if(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine == MainThread.theAssetManager.goldMines[5]) {
				lowGoldmineThreshold = 38750;
			}
		}
		
		if(MainThread.enemyCommander.difficulty == 1)
			lowGoldmineThreshold = 22500;
		else if(MainThread.enemyCommander.difficulty == 0)
			lowGoldmineThreshold = 15000;
		
		if(myMCV == null && expensionGoldMine.goldDeposite >= 17500 && (MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine.goldDeposite < lowGoldmineThreshold ||
			(!hasRefineryNearTheGoldmine(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine) && !hasConstructionYardNearGoldMine(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine)) ||
			(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine == expensionGoldMine && !hasConstructionYardNearGoldMine(expensionGoldMine) && !hasRefineryNearTheGoldmine(expensionGoldMine)))){
			
			int numberOfMCVOnQueue = 0;
			for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
				if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
					numberOfMCVOnQueue += MainThread.theAssetManager.factories[i].numOfMCVOnQueue;
				}
			}
		
			if(numberOfMCVOnQueue == 0 &&  theBaseInfo.canBuildMCV){
				for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
					if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
						MainThread.theAssetManager.factories[i].cancelBuilding();
						MainThread.theAssetManager.factories[i].buildMCV();
						break;
					}
				}
			}
		}
		
		//move mcv to the next expension location
		if(myMCV != null){
			if(frameAI > 400 && frameAI < 550) {
				MainThread.enemyCommander.theUnitProductionAI.rallyPoint.set(expensionGoldMine.centre.x, 0, expensionGoldMine.centre.z - 1.5f);
			}
			
			
			isExpanding = true;
			if(myMCV.getDistance(expensionGoldMine) > 2 && !(myMCV.destinationX == expensionGoldMine.centre.x && myMCV.destinationY == expensionGoldMine.centre.z)){
				myMCV.moveTo(expensionGoldMine.centre.x, expensionGoldMine.centre.z); 
				myMCV.currentCommand = SolidObject.move;
				
			}else if(frameAI%5 == 0 && myMCV.getDistance(expensionGoldMine) <=2){
				myMCV.moveTo(expensionGoldMine.centre.x + (float)(GameData.getRandom() -512) * 2 / 1024, expensionGoldMine.centre.z + (float)(GameData.getRandom() -512) * 2 / 1024);
				myMCV.currentCommand = SolidObject.move;
			}
			
			//change the preferred gold mine to the one near the new expension once MCV is deployed
			if(myMCV.getDistance(expensionGoldMine) < 2 && myMCV.getDistance(expensionGoldMine) > 0.75 && myMCV.canBeDeployed()){
				if(expensionPiorityList[targetExpension] == lastExpansionLocation) {
					allExpansionOccupied = true;
					for(int i = 0; i < scouts.length; i++){
						if(scouts[i] != null && scouts[i].currentHP >0){
							MainThread.enemyCommander.theUnitProductionAI.addStealthTank((StealthTank)scouts[i]);
							scouts[i].moveTo(MainThread.enemyCommander.theUnitProductionAI.rallyPoint.x, MainThread.enemyCommander.theUnitProductionAI.rallyPoint.z);
							scouts[i].currentCommand = SolidObject.attackMove;
							scouts[i].secondaryCommand = SolidObject.attackMove;
						}
					}
				}
				myMCV.expand();
				MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine = expensionGoldMine;
			}	
		}else{
			isExpanding = false;
		}
		
		
		//send scouts to guard mining expansion 
		if(!isExpanding){
			boolean scoutReachesExpension = false;
			for(int i = 0; i < scouts.length; i++){
				if(scouts[i] != null && scouts[i].currentHP >0){
					if(scouts[i].getDistance(expensionGoldMine) > 3){
						scouts[i].moveTo(expensionGoldMine.centre.x, expensionGoldMine.centre.z); 
						scouts[i].currentCommand = SolidObject.move;
						scouts[i].secondaryCommand = SolidObject.StandBy;
					}else{
						scoutReachesExpension = true;
					}
					
				}
			}
			
			//retaliate if the scouts are underattack while guarding the mine expansion.
			//however if there are too many hostile units to deal with, then retreat and tell the expansion AI to find another gold expension
			if(scoutReachesExpension){
				int overallThreatLevel = threatLevelNearTarget(expensionGoldMine);
				int threatLevel = overallThreatLevel&0xfff;
				boolean playerHasStaticDefence = ((overallThreatLevel&0xf000000) >> 24) >0;
				int noneCombatID = ((overallThreatLevel&0xfff000) >> 12);
				boolean isUnderAttack = false;
				float targetX = 0;
				float targetZ = 0;
				for(int i = 0; i < scouts.length; i++){
					if(scouts[i] != null &&  scouts[i].underAttackCountDown > 0){
						isUnderAttack = true;
						targetX = scouts[i].attacker.centre.x;
						targetZ = scouts[i].attacker.centre.z;
						if(scouts[i].currentHP<=0)
							scouts[i] = null;
						break;
					}
				}
				
				//attack if a non combat unit show up and the treatLevel if not high
				if(threatLevel <= numberOfActiveScout*6 && noneCombatID > 0 && !playerHasStaticDefence){
					for(int i = 0; i < scouts.length; i++){
						if(scouts[i] != null && scouts[i].currentHP >0){
							scouts[i].attackMoveTo(MainThread.enemyCommander.theMapAwarenessAI.mapAsset[noneCombatID].centre.x, MainThread.enemyCommander.theMapAwarenessAI.mapAsset[noneCombatID].centre.z);
							scouts[i].currentCommand = SolidObject.attackMove;
							scouts[i].secondaryCommand = SolidObject.attackMove;
						}
					}
					
				}
				
				if(isUnderAttack){
					if(threatLevel <= numberOfActiveScout*6 && !playerHasStaticDefence){
						//retaliate if threatLevel is not high
						for(int i = 0; i < scouts.length; i++){
							if(scouts[i] != null && scouts[i].currentHP >0){
								scouts[i].attackMoveTo(targetX, targetZ); 
								scouts[i].currentCommand = SolidObject.attackMove;
								scouts[i].secondaryCommand = SolidObject.attackMove;
							}
						}
					}else{
						//find a new potential expansion location when threatLevel is very high (except for natural expansion which is too valuable to give up)
						if(expensionGoldMine != goldMines[5]) {
							for(int i = 0; i<5; i++){	
								if(!hasRefineryNearTheGoldmine(goldMines[expensionPiorityList[i]]) && !hasConstructionYardNearGoldMine(goldMines[expensionPiorityList[i]]) 
									&& goldMines[expensionPiorityList[i]].goldDeposite > 17500 && goldMines[expensionPiorityList[i]] != expensionGoldMine){
									int t = expensionPiorityList[targetExpension];
									expensionPiorityList[targetExpension] = expensionPiorityList[i];
									expensionPiorityList[i] = t;
									break;
								}
							}
							expensionGoldMine = goldMines[expensionPiorityList[targetExpension]];
						}
					}
				}
			}
			
			
		}else{
			if(myMCV != null){
				
				int threatLevel = threatLevelNearTarget(expensionGoldMine);
				
				if(threatLevel > 0 || hasRefineryNearTheGoldmine(expensionGoldMine) || hasConstructionYardNearGoldMine(expensionGoldMine)){

					if((threatLevel&0xfff) <= numberOfActiveScout*5 && !hasRefineryNearTheGoldmine(expensionGoldMine) && !hasConstructionYardNearGoldMine(expensionGoldMine)){
						//attack if threatLevel is low
						for(int i = 0; i < scouts.length; i++){
							if(scouts[i] != null && scouts[i].currentHP >0){
								scouts[i].attackMoveTo(expensionGoldMine.centre.x, expensionGoldMine.centre.z); 
								scouts[i].currentCommand = SolidObject.attackMove;
								scouts[i].secondaryCommand = SolidObject.attackMove;
							}
						}
					}else{
						//find a new potential expansion location when threatLevel is high (except for natural expansion which is too valuable to give up)
						if(expensionGoldMine != goldMines[5]) {
							for(int i = 0; i<5; i++){	
								if(!hasRefineryNearTheGoldmine(goldMines[expensionPiorityList[i]]) && !hasConstructionYardNearGoldMine(goldMines[expensionPiorityList[i]]) 
									&& goldMines[expensionPiorityList[i]].goldDeposite > 17500 && goldMines[expensionPiorityList[i]] != expensionGoldMine){
									int t = expensionPiorityList[targetExpension];
									expensionPiorityList[targetExpension] = expensionPiorityList[i];
									expensionPiorityList[i] = t;
									break;
								}
							}
							expensionGoldMine = goldMines[expensionPiorityList[targetExpension]];
						}
						
					
						for(int i = 0; i < scouts.length; i++){
							if(scouts[i] != null && scouts[i].currentHP >0){
								if(scouts[i].getDistance(expensionGoldMine) > 3){
									scouts[i].moveTo(expensionGoldMine.centre.x, expensionGoldMine.centre.z); 
									scouts[i].currentCommand = SolidObject.move;
									scouts[i].secondaryCommand = SolidObject.StandBy;
								}
							}
						}
						
					}
				}else{
					if(myMCV.underAttackCountDown > 0 && myMCV.attacker != null){
						for(int i = 0; i < scouts.length; i++){
							if(scouts[i] != null && scouts[i].currentHP >0){
								scouts[i].attackMoveTo(myMCV.attacker.centre.x, myMCV.attacker.centre.z); 
								scouts[i].currentCommand = SolidObject.attackMove;
								scouts[i].secondaryCommand = SolidObject.attackMove;
							}
						}
					}else{
						temp.set(expensionGoldMine.centre);
						temp.subtract(myMCV.centre);
						temp.unit();
						temp.scale(1.5f);
				
						for(int i = 1; i < scouts.length; i++){
							if(scouts[i] != null && scouts[i].currentHP >0){
								scouts[i].attackMoveTo(myMCV.centre.x + temp.x, myMCV.centre.z + temp.z); 
								scouts[i].currentCommand = SolidObject.attackMove;
								scouts[i].secondaryCommand = SolidObject.attackMove;
							}
						}
					}
					
				}
				
			}
		}
	}
	
	
	//check if the scout units can fend off  hostile aggression near a goldmine expension
	public int threatLevelNearTarget(SolidObject o){
		//the 0xfff bits store the treatLevel of hostile Unit
		//the 0xfff000 bits store the id number of a nearby Harvester/construction vehicle
		//the 0xf000000 bit tells if the player has static defense setup near the target area
		
		SolidObject[] playerUnitInMinimap = MainThread.enemyCommander.theMapAwarenessAI.playerUnitInMinimap;
		SolidObject[] playerStaticDefenceInMinimap = MainThread.enemyCommander.theMapAwarenessAI.playerStaticDefenceInMinimap;
		boolean playerNoneCombatUnitDetected = false;
		int threatLevel = 0;
		for(int i = 0; i < playerUnitInMinimap.length; i++){
			if(playerUnitInMinimap[i] != null  && playerUnitInMinimap[i].getDistance(o) < 3){
				if(playerUnitInMinimap[i].type == 0)
					threatLevel+=3;
				else if(playerUnitInMinimap[i].type == 1)
					threatLevel+=2;
				else if(playerUnitInMinimap[i].type == 6)
					threatLevel+=5;
				else if(playerUnitInMinimap[i].type == 7)
					threatLevel+=20;
				else if((playerUnitInMinimap[i].type == 2 || playerUnitInMinimap[i].type == 3) && !playerNoneCombatUnitDetected){
					playerNoneCombatUnitDetected = true;
					threatLevel+=(playerUnitInMinimap[i].ID << 12);
				}
			}
		}
		
		
		
		
		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++){
			if(playerStaticDefenceInMinimap[i] != null && playerStaticDefenceInMinimap[i].getDistance(expensionGoldMine) < 3){
				threatLevel += (1 << 24);  //increase the threat level dramatically if player already has a base near the expension
				break;
			}
		}
		
		return threatLevel;
	}
	
	
	//add a stealth tank to scouts
	public void addStealthTank(StealthTank o){
		for(int i = 0; i < scouts.length; i++){
			if(scouts[i] == null || scouts[i].currentHP <=0){
				scouts[i] = o;
				break;
			}
		}
	}
	
	//3 stealth tanks will make a perfect scout team for the base expansion exploration 
	public boolean needStealthTank(){
		if(MainThread.enemyCommander.difficulty == 0)
			return false;
		
		for(int i = 0; i < scouts.length; i++){
			if((scouts[i] == null || scouts[i].currentHP <=0) && frameAI > 800){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRefineryNearTheGoldmine(GoldMine g){
		for(int j = 0; j < MainThread.theAssetManager.refineries.length; j++){
			if(MainThread.theAssetManager.refineries[j] != null){
				if(MainThread.theAssetManager.refineries[j].getDistance(g) < 2){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasConstructionYardNearGoldMine(GoldMine g){
		for(int j = 0; j < MainThread.theAssetManager.constructionYards.length; j++){
			if(MainThread.theAssetManager.constructionYards[j] != null){
				if(MainThread.theAssetManager.constructionYards[j].getDistance(g) < 3){
					return true;
				}
			}
		}
		return false;
	}
	
}
