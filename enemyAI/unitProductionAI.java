package enemyAI;

import core.baseInfo;
import core.gameData;
import core.mainThread;
import core.vector;
import entity.*;

//decide which unit to produce to counter player's force
//keep track of the units that are under control by combatAI.

public class unitProductionAI {
	
	public baseInfo theBaseInfo;
	
	public lightTank[] lightTanksControlledByCombatAI;
	public rocketTank[] rocketTanksControlledByCombatAI;
	public stealthTank[] stealthTanksControlledByCombatAI;
	public heavyTank[] heavyTanksControlledByCombatAI;
	public solidObject[] troopsControlledByCombatAI;
	
	public float combatAICenterX;
	public float combatAICenterZ;
	
	public int currentProductionOrder;
	public final int produceLightTank = 0;
	public final int produceRocketTank = 1;
	public final int produceStealthTank = 2;
	public final int produceHeavyTank = 3;
	
	public vector rallyPoint;
	//public int unitProduced;
	public int numberOfCombatUnit;
	public int numberOfUnitInCombatRadius;
	public int numberOfUnitOutsideCombatRadius;
	public int numberOfCombatUnitsUnderAttack;
	
	public int numberOfLightTanksControlledByCombatAI;
	public int numberOfRocketTanksControlledByCombatAI;
	public int numberOfStealthTanksControlledByCombatAI;
	public int numberOfHeavyTanksControlledByCombatAI;
	
	public solidObject[] unitInCombatRadius;
	public solidObject[] unitOutsideCombatRadius;
	
	public float rushRallyPointX, rushRallyPointZ;
	
	public int frameAI;
	
	public unitProductionAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;
		rallyPoint = new vector(0,0,0);
		
		
		rushRallyPointX = 9.5f+2;
		rushRallyPointZ = 5.5f+2;
	
		
		
		lightTanksControlledByCombatAI = new lightTank[192];
		rocketTanksControlledByCombatAI = new rocketTank[72];
		stealthTanksControlledByCombatAI = new stealthTank[96];
		heavyTanksControlledByCombatAI = new heavyTank[60];
		
		troopsControlledByCombatAI = new solidObject[512];
		unitInCombatRadius = new solidObject[384];
		unitOutsideCombatRadius = new solidObject[128];
		
		combatAICenterX = -1;
		combatAICenterZ = -1;
	
	}
	

	
	public void processAI(){
		frameAI = mainThread.ec.frameAI;
		
		//set the rally point to near the construction yard which is closest to the AI player's starting position
		float x = 0;
		float z = 999999;
		
		
		int numberOfLightTanks_AI = mainThread.ec.theUnitProductionAI.numberOfLightTanksControlledByCombatAI;
		int numberOfRocketTanks_AI = mainThread.ec.theUnitProductionAI.numberOfRocketTanksControlledByCombatAI;
		int numberOfStealthTanks_AI = mainThread.ec.theUnitProductionAI.numberOfStealthTanksControlledByCombatAI;
		int numberOfHeavyTanks_AI = mainThread.ec.theUnitProductionAI.numberOfHeavyTanksControlledByCombatAI;
		boolean unitCountLow = mainThread.ec.theCombatManagerAI.unitCountLow;
		
		int index = 0;
		for(int i = 0; i < mainThread.theAssetManager.constructionYards.length; i++){
			if(mainThread.theAssetManager.constructionYards[i] != null && mainThread.theAssetManager.constructionYards[i].currentHP > 0 &&  mainThread.theAssetManager.constructionYards[i].teamNo != 0){
				if(unitCountLow && mainThread.ec.theDefenseManagerAI.majorThreatLocation.x != 0) {
					float xPos1 = mainThread.theAssetManager.constructionYards[i].centre.x;
					float zPos1 = mainThread.theAssetManager.constructionYards[i].centre.z;
					float xPos2 = mainThread.ec.theDefenseManagerAI.majorThreatLocation.x;
					float zPos2 = mainThread.ec.theDefenseManagerAI.majorThreatLocation.z;
					float d = (xPos1 - xPos2) * (xPos1 - xPos2) + (zPos1 - zPos2) * (zPos1 - zPos2);
					if(d < 9) {
						continue;
					}
				}
				
				index = i;
				if(mainThread.theAssetManager.constructionYards[i].centre.z < z && mainThread.theAssetManager.constructionYards[i].centre.z > 7 && mainThread.theAssetManager.constructionYards[i].centre.x > 7){
					x = mainThread.theAssetManager.constructionYards[i].centre.x;
					z = mainThread.theAssetManager.constructionYards[i].centre.z;
				}
			}
		}
		if(z != 999999) {
			
			rallyPoint.set(x - 2.25f, 0, z - 1.75f);
			
			if(frameAI < 240) {
				rallyPoint.set(mainThread.theAssetManager.goldMines[5].centre);
			}
		}else {
			if(mainThread.theAssetManager.constructionYards[index] != null && mainThread.theAssetManager.constructionYards[index].teamNo !=0)
				rallyPoint.set(mainThread.theAssetManager.constructionYards[index].centre.x - 2.5f, 0,  mainThread.theAssetManager.constructionYards[index].centre.z -2.5f);
		}
		
		//If the difficulty is set to normal or hard, set the rally point just outside of player's natural expansion. 
		//So if the player is going for a fast expansion and don't have much units, the AI can perform a rush attack. 
		//if(mainThread.ec.theMapAwarenessAI.canRushPlayer && frameAI < 360 && mainThread.ec.theCombatManagerAI.checkIfAIHasBiggerForce(0.75f)) {
		//	rallyPoint.set(rushRallyPointX, 0,  rushRallyPointZ);
		//}
		
		//make sure not to over produce when the resource is running low
		int maxNumOfUnitCanBeProduced =  theBaseInfo.currentCredit / 500 + 1;
		
		
		for(int i = 0; i < mainThread.theAssetManager.factories.length; i++){
			factory f = mainThread.theAssetManager.factories[i];
			if(f != null && f.teamNo !=0){
				if(!f.isIdle())
					maxNumOfUnitCanBeProduced--;
			}
		}
		
		for(int i = 0; i < mainThread.theAssetManager.constructionYards.length; i++){
			constructionYard c = mainThread.theAssetManager.constructionYards[i];
			if(c != null && c.teamNo !=0){
				if(!c.isIdle())
					maxNumOfUnitCanBeProduced--;
			}
		}
		
		
		//make decision on what unit to produce
		int numberOfPlayerGunTurrets=   mainThread.ec.theMapAwarenessAI.numberOfGunTurret_player;
		int numberOfPlayerMissileTurrets=  mainThread.ec.theMapAwarenessAI.numberOfMissileTurret_player;
		int numberOfLightTanks_player = mainThread.ec.theMapAwarenessAI.numberOfLightTanks_player;
		int numberOfRocketTanks_player = mainThread.ec.theMapAwarenessAI.numberOfRocketTanks_player;
		int numberOfStealthTanks_player = mainThread.ec.theMapAwarenessAI.numberOfStealthTanks_player;
		int numberOfHeavyTanks_player = mainThread.ec.theMapAwarenessAI.numberOfHeavyTanks_player;
		int maxNumberOfStealthTanks_playerInLastFiveMinutes =  mainThread.ec.theMapAwarenessAI.maxNumberOfStealthTanks_playerInLastFiveMinutes;
		
		boolean playerHasMostlyLightTanks = mainThread.ec.theMapAwarenessAI.playerHasMostlyLightTanks;
		boolean playerHasMostlyHeavyTanks =  mainThread.ec.theMapAwarenessAI.playerHasMostlyHeavyTanks;
		boolean playIsRushingHighTierUnits = mainThread.ec.theMapAwarenessAI.playIsRushingHighTierUnits;
		boolean playerLikelyCanNotProduceHighTierUnits = mainThread.ec.theMapAwarenessAI.playerLikelyCanNotProduceHighTierUnits;
		boolean playerDoesntHaveMassHeavyTanks = mainThread.ec.theMapAwarenessAI.playerDoesntHaveMassHeavyTanks;
		boolean playerHasManyLightTanksButNoHeavyTank = mainThread.ec.theMapAwarenessAI.playerHasManyLightTanksButNoHeavyTank;
		boolean playerHasMostlyHeavyAndStealthTanks = mainThread.ec.theMapAwarenessAI.playerHasMostlyHeavyAndStealthTanks;
		
		
		int timeToBuildHeavyTank = 400;
		int timeToBuildStealthTank = 200;
		if(mainThread.ec.theMapAwarenessAI.canRushPlayer) {
			//when AI decides to rush the player, then dont build higher tier units so it can mass produce light tanks
			timeToBuildHeavyTank = 500;
			timeToBuildStealthTank = 500;
		}
		
		boolean b1 = (numberOfRocketTanks_AI < 3 && !playerHasMostlyHeavyTanks  && (frameAI > 400 || frameAI > 170 && frameAI < 240 && mainThread.ec.theMapAwarenessAI.numberOfConstructionYard_player > 0) && !playerHasMostlyLightTanks);
		boolean b2 = (numberOfRocketTanks_AI < numberOfPlayerGunTurrets + numberOfPlayerMissileTurrets*1.5);
		if( b1 || b2){
			currentProductionOrder = produceRocketTank;
		}else if(theBaseInfo.canBuildHeavyTank && !playerHasMostlyHeavyTanks &&
				 (playerHasMostlyHeavyAndStealthTanks || (frameAI > timeToBuildHeavyTank && numberOfHeavyTanks_AI < 3) ||
				 !playerHasManyLightTanksButNoHeavyTank
				 && !playerHasMostlyLightTanks 
				 && !(numberOfHeavyTanks_player == 0 && maxNumberOfStealthTanks_playerInLastFiveMinutes < 3 &&  frameAI > 600)  
				 && !(playerHasMostlyHeavyTanks && numberOfStealthTanks_player < numberOfHeavyTanks_AI*2) 
				 && (playIsRushingHighTierUnits ||  maxNumberOfStealthTanks_playerInLastFiveMinutes*4 > numberOfHeavyTanks_AI))){
			currentProductionOrder = produceHeavyTank; 
		}else if(theBaseInfo.canBuildStealthTank && !playerHasMostlyHeavyTanks && !(numberOfStealthTanksControlledByCombatAI >= 8 &&  frameAI < 600) && !(numberOfStealthTanksControlledByCombatAI >= 16 &&  frameAI > 600)
				 && (playerHasMostlyLightTanks || playerLikelyCanNotProduceHighTierUnits || playerDoesntHaveMassHeavyTanks) && !playerHasMostlyHeavyTanks && (frameAI > timeToBuildStealthTank || numberOfLightTanks_player > 8)){
			currentProductionOrder = produceStealthTank;
		}else{
			currentProductionOrder = produceLightTank;
		}
				
		//make decision on what tech to research
		if(mainThread.ec.theBuildingManagerAI.theBaseInfo.numberOfCommunicationCenter > 0) {
			if(mainThread.ec.theDefenseManagerAI.needMissileTurret || theBaseInfo.currentCredit > 1500 && frameAI > 450) {
				if(!communicationCenter.rapidfireResearched_enemy) {
					if(communicationCenter.rapidfireResearchProgress_enemy == 255){
						communicationCenter.researchRapidfire(1);
						System.out.println("----------------------------AI starts researching rapid fire ability------------------------------------");
					}
				}
			}
			
			if(mainThread.ec.theEconomyManagerAI.numberOfharvesters >= 6 && theBaseInfo.currentCredit > 1500) {
				if(!communicationCenter.harvesterSpeedResearched_enemy) {
					if(communicationCenter.harvesterSpeedResearchProgress_enemy == 255){
						communicationCenter.researchHarvesterSpeed(1);
						System.out.println("----------------------------AI starts researching harvester  speed ability------------------------------------");
					}
				}
			}
		}
		
		if(mainThread.ec.theBuildingManagerAI.theBaseInfo.numberOfTechCenter > 0){	
					
			//Immediately  start  stealth tank upgrades  when a tech center is built
			if(!techCenter.stealthTankResearched_enemy){
				if(techCenter.stealthTankResearchProgress_enemy == 255){
					techCenter.cancelResearch(1);
					techCenter.researchStealthTank(1);
					System.out.println("----------------------------AI starts researching stealth tank------------------------------------");
				}
			}
		
			
			if(numberOfLightTanks_AI >= 15  && theBaseInfo.currentCredit > 1000){
				if(!techCenter.lightTankResearched_enemy){
					if(techCenter.lightTankResearchProgress_enemy >= 240 && techCenter.stealthTankResearchProgress_enemy >= 240 && techCenter.rocketTankResearchProgress_enemy >= 240 && techCenter.heavyTankResearchProgress_enemy >= 240){
						techCenter.researchLightTank(1);
						System.out.println("----------------------------AI starts researching light tank------------------------------------");
					}
				}
			}
			
			if(numberOfRocketTanks_AI > 2 && theBaseInfo.currentCredit > 1250 && (numberOfPlayerGunTurrets > 0 || numberOfPlayerMissileTurrets > 0 || frameAI > 600)){
				if(!techCenter.rocketTankResearched_enemy){
					if(techCenter.lightTankResearchProgress_enemy >= 240 && techCenter.stealthTankResearchProgress_enemy >= 240 && techCenter.rocketTankResearchProgress_enemy >= 240 && techCenter.heavyTankResearchProgress_enemy >= 240){

						techCenter.researchRocketTank(1);
						System.out.println("----------------------------AI starts researching rocket tank------------------------------------");
					}
				}
			}
			
			if(numberOfHeavyTanks_AI > 5 && theBaseInfo.currentCredit > 1000){
				if(!techCenter.heavyTankResearched_enemy){
					if(techCenter.lightTankResearchProgress_enemy >= 240 && techCenter.stealthTankResearchProgress_enemy >= 240 && techCenter.rocketTankResearchProgress_enemy >= 240 && techCenter.heavyTankResearchProgress_enemy >= 240){
						techCenter.researchHeavyTank(1);
						System.out.println("----------------------------AI starts researching heavy tank------------------------------------");
					}
				}
			}
			
			
		}
		
	
		for(int i = 0; i < mainThread.theAssetManager.factories.length; i++){
			factory f = mainThread.theAssetManager.factories[i];
			if(f != null && f.teamNo !=0){
				f.moveTo(rallyPoint.x, rallyPoint.z);
				if(f.isIdle()){
					if(theBaseInfo.canBuildLightTank && maxNumOfUnitCanBeProduced > 0){
						
						if(currentProductionOrder == produceLightTank)
							f.buildLightTank();
						else if(currentProductionOrder == produceRocketTank)
							f.buildRocketTank();
						else if(currentProductionOrder == produceStealthTank)
							f.buildStealthTank();
						else if(currentProductionOrder == produceHeavyTank)
							f.buildHeavyTank();
						
						maxNumOfUnitCanBeProduced--;
					}
					continue;
				}
			}
		}
		
		
		countTroopControlledByCombatAI();
		findCenterOfTroopControlledByCombatAI();

	}
	
	public void addLightTank(lightTank o){
		//check if other AI agent need light tank
		
		if(mainThread.ec.theScoutingManagerAI.needLightTank()){
			mainThread.ec.theScoutingManagerAI.addLightTank(o);
			
			return;
		}
		
		
		//add the new light tank to combat AI's command
		for(int i = 0; i < lightTanksControlledByCombatAI.length; i++){
			if(lightTanksControlledByCombatAI[i] == null || (lightTanksControlledByCombatAI[i] != null && lightTanksControlledByCombatAI[i].currentHP <=0)){
				lightTanksControlledByCombatAI[i] = o;
				mainThread.ec.theDefenseManagerAI.addUnitToDefenders(o);
				break;
			}
		}
		
		
	}
	
	public void addRocketTank(rocketTank o){
		//check if other AI agent need rocket tank
		
		//add the new rocket tank to combat AI's command
		for(int i = 0; i < rocketTanksControlledByCombatAI.length; i++){
			if(rocketTanksControlledByCombatAI[i] == null || (rocketTanksControlledByCombatAI[i] != null && rocketTanksControlledByCombatAI[i].currentHP <=0)){
				rocketTanksControlledByCombatAI[i] = o;
				break;
			}
		}
	}
	
	public void addStealthTank(stealthTank o){
		//check if other AI agent need stealth tank
		
		if(mainThread.ec.theScoutingManagerAI.needStealthTank()){
			mainThread.ec.theScoutingManagerAI.addStealthTank(o);
			return;
		}
		
		if(mainThread.ec.theBaseExpentionAI.needStealthTank()){
			mainThread.ec.theBaseExpentionAI.addStealthTank(o);
			return;
		}
		
		
		
		//add the new stealth tank to combat AI's command
		for(int i = 0; i < stealthTanksControlledByCombatAI.length; i++){
			if(stealthTanksControlledByCombatAI[i] == null || (stealthTanksControlledByCombatAI[i] != null && stealthTanksControlledByCombatAI[i].currentHP <=0)){
				stealthTanksControlledByCombatAI[i] = o;
			
				mainThread.ec.theDefenseManagerAI.addUnitToDefenders(o);
				break;
			}
		}
	}
	
	public void addHeavyTank(heavyTank o){
		//add the new heavy tank to combat AI's command
		for(int i = 0; i < heavyTanksControlledByCombatAI.length; i++){
			if(heavyTanksControlledByCombatAI[i] == null || (heavyTanksControlledByCombatAI[i] != null && heavyTanksControlledByCombatAI[i].currentHP <=0)){
				heavyTanksControlledByCombatAI[i] = o;
				break;
			}
		}
	}
	
	
	public void countTroopControlledByCombatAI(){
		numberOfCombatUnitsUnderAttack = 0;
		
		numberOfLightTanksControlledByCombatAI = 0;
		numberOfRocketTanksControlledByCombatAI = 0;
		numberOfStealthTanksControlledByCombatAI = 0;
		numberOfHeavyTanksControlledByCombatAI = 0;
		
		for(int i = 0; i < troopsControlledByCombatAI.length; i++){
			troopsControlledByCombatAI[i] = null;
		}
		
		numberOfCombatUnit = 0;
		for(int i = 0; i < lightTanksControlledByCombatAI.length; i++){
			if(lightTanksControlledByCombatAI[i] != null && lightTanksControlledByCombatAI[i].currentHP > 0){
				troopsControlledByCombatAI[numberOfCombatUnit] = lightTanksControlledByCombatAI[i];
				if(troopsControlledByCombatAI[numberOfCombatUnit].underAttackCountDown > 0)
					numberOfCombatUnitsUnderAttack++;
				numberOfCombatUnit++;
				numberOfLightTanksControlledByCombatAI++;
			}
		}
		for(int i = 0; i < rocketTanksControlledByCombatAI.length; i++){
			if(rocketTanksControlledByCombatAI[i] != null && rocketTanksControlledByCombatAI[i].currentHP > 0){
				troopsControlledByCombatAI[numberOfCombatUnit] = rocketTanksControlledByCombatAI[i];
				if(troopsControlledByCombatAI[numberOfCombatUnit].underAttackCountDown > 0)
					numberOfCombatUnitsUnderAttack++;
				numberOfCombatUnit++;
				numberOfRocketTanksControlledByCombatAI++;
			}
		}
		for(int i = 0; i < stealthTanksControlledByCombatAI.length; i++){
			if(stealthTanksControlledByCombatAI[i] != null && stealthTanksControlledByCombatAI[i].currentHP > 0){
				troopsControlledByCombatAI[numberOfCombatUnit] = stealthTanksControlledByCombatAI[i];
				if(troopsControlledByCombatAI[numberOfCombatUnit].underAttackCountDown > 0)
					numberOfCombatUnitsUnderAttack++;
				numberOfCombatUnit++;
				numberOfStealthTanksControlledByCombatAI++;
			}
		}
		for(int i = 0; i < heavyTanksControlledByCombatAI.length; i++){
			if(heavyTanksControlledByCombatAI[i] != null && heavyTanksControlledByCombatAI[i].currentHP > 0){
				troopsControlledByCombatAI[numberOfCombatUnit] = heavyTanksControlledByCombatAI[i];
				if(troopsControlledByCombatAI[numberOfCombatUnit].underAttackCountDown > 0)
					numberOfCombatUnitsUnderAttack++;
				numberOfCombatUnit++;
				numberOfHeavyTanksControlledByCombatAI++;
			}
		}
	}
	
	public void findCenterOfTroopControlledByCombatAI(){
		float centerX = 0;
		float centerZ = 0;
		
		vector centre;
		double distance = 0;
		
		numberOfUnitInCombatRadius = 0;
		numberOfUnitOutsideCombatRadius = 0;
		
		
		//when there is no combat unit there is no point to calculate the center of the combat units
		if(numberOfCombatUnit == 0){
			combatAICenterX = -1;
			combatAICenterZ = -1;
			return;
		}
		
		for(int i = 0; i <  unitInCombatRadius.length; i++){
			unitInCombatRadius[i] = null;
		}
		
		for(int i = 0; i <  unitOutsideCombatRadius.length; i++){
			unitOutsideCombatRadius[i] = null;
		}
		
		
		//calculate the center of the troops using the all the unites that are under the control of the combatAI
		if(combatAICenterX == -1){
			combatAICenterX = 0;
			combatAICenterZ = 0;
			for(int i =0; i < numberOfCombatUnit; i++){
				centre = troopsControlledByCombatAI[i].centre;
				combatAICenterX+=centre.x;
				combatAICenterZ+=centre.z;
			}
			combatAICenterX /= numberOfCombatUnit;
			combatAICenterZ /= numberOfCombatUnit;
		}
		
		//exclude the units are too far away from the center of the troops, (i.e the unites that just come out of the factory), and recalculate the center
		for(int i =0; i < numberOfCombatUnit; i++){
			centre = troopsControlledByCombatAI[i].centre;
			distance = Math.sqrt((centre.x - combatAICenterX)*(centre.x - combatAICenterX) + (centre.z - combatAICenterZ)*(centre.z - combatAICenterZ));
			if(distance < 4.5){
				centerX += centre.x;
				centerZ += centre.z;
				if(numberOfUnitInCombatRadius < unitInCombatRadius.length)
					unitInCombatRadius[numberOfUnitInCombatRadius] = troopsControlledByCombatAI[i];
				numberOfUnitInCombatRadius++;
			}else{
				if(numberOfUnitOutsideCombatRadius < unitOutsideCombatRadius.length)
					unitOutsideCombatRadius[numberOfUnitOutsideCombatRadius] = troopsControlledByCombatAI[i];
				numberOfUnitOutsideCombatRadius++;
			}
		}
		
		float unitInCombactRadiusPercentage = 1;
		if(numberOfUnitInCombatRadius + numberOfUnitOutsideCombatRadius >0)
			unitInCombactRadiusPercentage = (float)numberOfUnitInCombatRadius/(float)(numberOfUnitInCombatRadius + numberOfUnitOutsideCombatRadius);
		
		float unitInCombactRadiusPercentageThreshold = 0.7f;
		if(mainThread.ec.theCombatManagerAI.currentState == mainThread.ec.theCombatManagerAI.aggressing) {
			if(mainThread.ec.theCombatManagerAI.distanceToTarget < 6)
				unitInCombactRadiusPercentageThreshold = 0.475f;
		}
		if(numberOfCombatUnitsUnderAttack > 0)
			unitInCombactRadiusPercentageThreshold = 0.25f;
		
		//need to recalculate the center  if  there is a significant amount of combat unites that are outside of the combat radius 
		if(unitInCombactRadiusPercentage < unitInCombactRadiusPercentageThreshold){
			
			combatAICenterX = 0;
			combatAICenterZ = 0;
			for(int i =0; i < numberOfCombatUnit; i++){
				centre = troopsControlledByCombatAI[i].centre;
				combatAICenterX+=centre.x;
				combatAICenterZ+=centre.z;
			}
			combatAICenterX /= numberOfCombatUnit;
			combatAICenterZ /= numberOfCombatUnit;
			
			for(int i = 0; i <  unitInCombatRadius.length; i++){
				unitInCombatRadius[i] = null;
			}
			
			for(int i = 0; i <  unitOutsideCombatRadius.length; i++){
				unitOutsideCombatRadius[i] = null;
			}
			numberOfUnitInCombatRadius = 0;
			numberOfUnitOutsideCombatRadius = 0;
			centerX = 0;
			centerZ = 0;
			
			for(int i =0; i < numberOfCombatUnit; i++){
				centre = troopsControlledByCombatAI[i].centre;
				distance = Math.sqrt((centre.x - combatAICenterX)*(centre.x - combatAICenterX) + (centre.z - combatAICenterZ)*(centre.z - combatAICenterZ));
				if(distance < 10){
					centerX += centre.x;
					centerZ += centre.z;
					if(numberOfUnitInCombatRadius < unitInCombatRadius.length)
						unitInCombatRadius[numberOfUnitInCombatRadius] = troopsControlledByCombatAI[i];
					numberOfUnitInCombatRadius++;
				}else{
					if(numberOfUnitOutsideCombatRadius < unitOutsideCombatRadius.length)
						unitOutsideCombatRadius[numberOfUnitOutsideCombatRadius] = troopsControlledByCombatAI[i];
					numberOfUnitOutsideCombatRadius++;
				}
			}
		}
		
		combatAICenterX = centerX/numberOfUnitInCombatRadius;
		combatAICenterZ = centerZ/numberOfUnitInCombatRadius;
		
	}
}
