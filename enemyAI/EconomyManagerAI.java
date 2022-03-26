package enemyAI;

import core.BaseInfo;
import core.MainThread;
import core.vector;
import entity.Harvester;
import entity.ConstructionYard;
import entity.GoldMine;
import entity.Refinery;

public class EconomyManagerAI {

	//this represent the cloest gold mine to the enemy player's constructionyard
	public GoldMine preferedGoldMine;
	public int preferedGoldMineLocation;
	public BaseInfo theBaseInfo;
	
	public int numberOfharvesters;
	public vector evadeDirection;
    public int numberOfFunctionalRefinery;
	
	public EconomyManagerAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;;
		
		evadeDirection = new vector(0,0,0);
		
	}
	
	public void processAI(){
		//find the number of functional Refinery, (Refinery with depleted gold mine are not considered functional)
		numberOfFunctionalRefinery = 0;
		Refinery[] refineries = MainThread.theAssetManager.refineries;
		for(int i = 0; i < refineries.length; i++) {
			if(refineries[i] != null && refineries[i].teamNo == 1 && refineries[i].currentHP> 0) {
				if(refineries[i].nearestGoldMine != null && refineries[i].nearestGoldMine.goldDeposite > 0)
					numberOfFunctionalRefinery++;
			}
		
				
		}
		
		//find an ideal goldmine 
		GoldMine[] goldMines = MainThread.theAssetManager.goldMines;
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		
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
		
		
		//count number of Harvesters and prevent them from doing something stupid
		int numberOfHarvesterOnQueue = 0;
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
				if(preferedGoldMine != null)
					MainThread.theAssetManager.factories[i].targetGoldMine = preferedGoldMine;
				numberOfHarvesterOnQueue += MainThread.theAssetManager.factories[i].numOfHarvesterOnQueue;
			}
		}
		
		for(int i = 0; i < MainThread.theAssetManager.constructionYards.length; i++){
			if(MainThread.theAssetManager.constructionYards[i] != null && MainThread.theAssetManager.constructionYards[i].teamNo != 0){
				if(MainThread.theAssetManager.constructionYards[i].currentBuildingType == 102)
					numberOfHarvesterOnQueue += 1;
			}
		}
		
		numberOfharvesters = 0;
		for(int i = 0; i < MainThread.theAssetManager.Harvesters.length; i++){
			if(MainThread.theAssetManager.Harvesters[i] != null && MainThread.theAssetManager.Harvesters[i].teamNo != 0){
				numberOfharvesters++;
				Harvester o = MainThread.theAssetManager.Harvesters[i];
				if(o.movement.x == 0 && o.movement.z == 0){
					if(o.cargoDeposite > 0 && o.jobStatus == 0){
						o.returnToRefinery(null);
					}
				}
				
				//when current gold mine run out of gold, direct Harvester to the nearest goldmines which still have deposit
				if(o.cargoDeposite == 0 &&  o.myGoldMine != null && o.myGoldMine.goldDeposite <= 1){
					if(preferedGoldMine != null){
						boolean hasRefineryNearby = false;
						for(int j = 0; j < MainThread.theAssetManager.refineries.length; j++){
							if(MainThread.theAssetManager.refineries[j] != null && MainThread.theAssetManager.refineries[j].teamNo !=0){
								if(MainThread.theAssetManager.refineries[j].getDistance(preferedGoldMine) < 2){
									hasRefineryNearby = true;
									break;
								}
							}
						}
						
						int numberOfHarvestersOnTheMine = 0;
						for(int j = 0; j < MainThread.theAssetManager.Harvesters.length; j++){
							if(MainThread.theAssetManager.Harvesters[j] != null && MainThread.theAssetManager.Harvesters[j].teamNo !=0 && MainThread.theAssetManager.Harvesters[j].myGoldMine == preferedGoldMine)
								numberOfHarvestersOnTheMine++;
						}
						
						//only go to the gold mine that has a Refinery nearby and its not saturated with Harvesters
						if(numberOfHarvestersOnTheMine < 6 && hasRefineryNearby)
							o.myGoldMine = preferedGoldMine;
					}
				}
				
				//when Harvester is under attack then temporarily move away from mining until the danger passes
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
	
		//the ration between Harvester and Refinery should be 2:1
		//economyManager has a higher priority than combat manager AI, so the enemy AI will always queue Harvester first if lost any.
		if(theBaseInfo.numberOfRefinery > 0 && numberOfharvesters < 6){
			if(numberOfharvesters < theBaseInfo.numberOfRefinery + 2){
				for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
					if(MainThread.theAssetManager.factories[i] != null && MainThread.theAssetManager.factories[i].teamNo != 0){
						MainThread.theAssetManager.factories[i].cancelBuilding();
						MainThread.theAssetManager.factories[i].buildHarvester();
						break;
						
					}
				}
			}
		}
	}
	
}
