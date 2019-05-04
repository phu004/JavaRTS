package enemyAI;

import core.baseInfo;
import core.mainThread;
import core.vector;
import entity.constructionYard;
import entity.goldMine;
import entity.harvester;
import entity.refinery;

public class economyManagerAI {

	//this represent the cloest gold mine to the enemy player's constructionyard
	public goldMine preferedGoldMine;
	public int preferedGoldMineLocation;
	public baseInfo theBaseInfo;
	
	public int numberOfharvesters;
	public vector evadeDirection;
    public int numberOfFunctionalRefinery;
	
	public economyManagerAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;;
		
		evadeDirection = new vector(0,0,0);
		
	}
	
	public void processAI(){
		//find the number of functional refinery, (refinery with depleted gold mine are not considered functional)
		numberOfFunctionalRefinery = 0;
		refinery[] refineries = mainThread.theAssetManager.refineries;
		for(int i = 0; i < refineries.length; i++) {
			if(refineries[i] != null && refineries[i].teamNo == 1 && refineries[i].currentHP> 0) {
				if(refineries[i].nearestGoldMine != null && refineries[i].nearestGoldMine.goldDeposite > 0)
					numberOfFunctionalRefinery++;
			}
		
				
		}
		
		//find an ideal goldmine 
		goldMine[] goldMines = mainThread.theAssetManager.goldMines;
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
		
		if(preferedGoldMine == null || preferedGoldMine.goldDeposite <= 10){
			float distance = 100000;
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
					for(int j = 0; j < goldMines.length; j++){
						if(goldMines[j] == null || goldMines[j].goldDeposite <=10)
							continue;
						
						float newDistance = (goldMines[j].centre.x - constructionYards[i].centre.x)*(goldMines[j].centre.x - constructionYards[i].centre.x) 
						          +(goldMines[j].centre.z - constructionYards[i].centre.z)*(goldMines[j].centre.z - constructionYards[i].centre.z);
				
						if( newDistance  <  distance){
							preferedGoldMine = goldMines[j];
							distance = newDistance;
						}
					}
					
				}
			}
		}
		
		preferedGoldMineLocation = (int)(preferedGoldMine.centre.x*64)/16 + (127 - (int)(preferedGoldMine.centre.z*64)/16)*128;
		
		
		//count number of harvesters and prevent them from doing something stupid
		int numberOfHarvesterOnQueue = 0;
		for(int i = 0; i < mainThread.theAssetManager.factories.length; i++){
			if(mainThread.theAssetManager.factories[i] != null && mainThread.theAssetManager.factories[i].teamNo != 0){
				if(preferedGoldMine != null)
					mainThread.theAssetManager.factories[i].targetGoldMine = preferedGoldMine;
				numberOfHarvesterOnQueue += mainThread.theAssetManager.factories[i].numOfHarvesterOnQueue;
			}
		}
		
		for(int i = 0; i < mainThread.theAssetManager.constructionYards.length; i++){
			if(mainThread.theAssetManager.constructionYards[i] != null && mainThread.theAssetManager.constructionYards[i].teamNo != 0){
				if(mainThread.theAssetManager.constructionYards[i].currentBuildingType == 102)
					numberOfHarvesterOnQueue += 1;
			}
		}
		
		numberOfharvesters = 0;
		for(int i = 0; i < mainThread.theAssetManager.harvesters.length; i++){
			if(mainThread.theAssetManager.harvesters[i] != null && mainThread.theAssetManager.harvesters[i].teamNo != 0){
				numberOfharvesters++;
				harvester o = mainThread.theAssetManager.harvesters[i];
				if(o.movement.x == 0 && o.movement.z == 0){
					if(o.cargoDeposite > 0 && o.jobStatus == 0){
						o.returnToRefinery(null);
					}
				}
				
				//when current gold mine run out of gold, direct harvester to the nearest goldmines which still have deposit
				if(o.cargoDeposite == 0 &&  o.myGoldMine != null && o.myGoldMine.goldDeposite <= 1){
					if(preferedGoldMine != null){
						boolean hasRefineryNearby = false;
						for(int j = 0; j < mainThread.theAssetManager.refineries.length; j++){
							if(mainThread.theAssetManager.refineries[j] != null && mainThread.theAssetManager.refineries[j].teamNo !=0){
								if(mainThread.theAssetManager.refineries[j].getDistance(preferedGoldMine) < 2){
									hasRefineryNearby = true;
									break;
								}
							}
						}
						
						int numberOfHarvestersOnTheMine = 0;
						for(int j = 0; j < mainThread.theAssetManager.harvesters.length; j++){
							if(mainThread.theAssetManager.harvesters[j] != null && mainThread.theAssetManager.harvesters[j].teamNo !=0 && mainThread.theAssetManager.harvesters[j].myGoldMine == preferedGoldMine)
								numberOfHarvestersOnTheMine++;
						}
						
						//only go to the gold mine that has a refinery nearby and its not saturated with harvesters
						if(numberOfHarvestersOnTheMine < 6 && hasRefineryNearby)
							o.myGoldMine = preferedGoldMine;
					}
				}
				
				//when harvester is under attack then temporarily move away from mining until the danger passes
				if(o.underAttackCountDown > 0) {
					o.isEvadingFromAttack = true;
					if(o.attacker != null) {
						evadeDirection.set(o.centre);
						evadeDirection.subtract(o.attacker.centre);
						evadeDirection.unit();
						
						float desX = o.centre.x + evadeDirection.x*2;
						float desZ = o.centre.z + evadeDirection.z*2;
						
						if(desX > 31)
							desX = 31;
						if(desX < 1)
							desX = 1;

						if(desZ > 31)
							desZ = 31;
						if(desZ < 1)
							desZ = 1;
						
						o.moveTo(desX, desZ);
					}
				}else {
					if(o.isEvadingFromAttack == true) {
						o.returnToRefinery(null);
						o.isEvadingFromAttack = false;
					}
				}
				
				
			}
		}
		numberOfharvesters+=numberOfHarvesterOnQueue;
	
		//the ration between harvester and refinery should be 2:1
		//economyManager has a higher priority than combat manager AI, so the enemy AI will always queue harvester first if lost any.
		if(theBaseInfo.numberOfRefinery > 0 && numberOfharvesters < 6){
			if(numberOfharvesters < theBaseInfo.numberOfRefinery + 2){
				for(int i = 0; i < mainThread.theAssetManager.factories.length; i++){
					if(mainThread.theAssetManager.factories[i] != null && mainThread.theAssetManager.factories[i].teamNo != 0){
						mainThread.theAssetManager.factories[i].cancelBuilding();
						mainThread.theAssetManager.factories[i].buildHarvester();
						break;
						
					}
				}
			}
		}
	}
	
}
