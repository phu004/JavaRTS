package enemyAI;

import core.BaseInfo;
import core.GameData;
import core.MainThread;
import core.vector;
import entity.*;

//decide which unit to produce to counter player's force
//keep track of the units that are under control by combatAI.

public class UnitProductionAI {
	
	public BaseInfo theBaseInfo;
	
	public LightTank[] lightTanksControlledByCombatAI;
	public RocketTank[] rocketTanksControlledByCombatAI;
	public StealthTank[] stealthTanksControlledByCombatAI;
	public HeavyTank[] heavyTanksControlledByCombatAI;
	public SolidObject[] troopsControlledByCombatAI;
	
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
	
	public SolidObject[] unitInCombatRadius;
	public SolidObject[] unitOutsideCombatRadius;
	
	public float rushRallyPointX, rushRallyPointZ;
	
	public int frameAI;
	
	public UnitProductionAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
		rallyPoint = new vector(0,0,0);
		
		
		rushRallyPointX = 9.5f+2;
		rushRallyPointZ = 5.5f+2;
	
		
		
		lightTanksControlledByCombatAI = new LightTank[192];
		rocketTanksControlledByCombatAI = new RocketTank[72];
		stealthTanksControlledByCombatAI = new StealthTank[96];
		heavyTanksControlledByCombatAI = new HeavyTank[60];
		
		troopsControlledByCombatAI = new SolidObject[512];
		unitInCombatRadius = new SolidObject[384];
		unitOutsideCombatRadius = new SolidObject[128];
		
		combatAICenterX = -1;
		combatAICenterZ = -1;
	
	}
	

	
	public void processAI(){
		frameAI = MainThread.enemyCommander.frameAI;
		
		//set the rally point to near the construction yard which is closest to the AI player's starting position
		float x = 0;
		float z = 999999;
		
		
		int numberOfLightTanks_AI = MainThread.enemyCommander.theUnitProductionAI.numberOfLightTanksControlledByCombatAI;
		int numberOfRocketTanks_AI = MainThread.enemyCommander.theUnitProductionAI.numberOfRocketTanksControlledByCombatAI;
		int numberOfStealthTanks_AI = MainThread.enemyCommander.theUnitProductionAI.numberOfStealthTanksControlledByCombatAI;
		int numberOfHeavyTanks_AI = MainThread.enemyCommander.theUnitProductionAI.numberOfHeavyTanksControlledByCombatAI;
		boolean unitCountLow = MainThread.enemyCommander.theCombatManagerAI.unitCountLow;
		
		int index = 0;
		for(int i = 0; i < MainThread.theAssetManager.constructionYards.length; i++){
			if(MainThread.theAssetManager.constructionYards[i] != null && MainThread.theAssetManager.constructionYards[i].currentHP > 0 &&  MainThread.theAssetManager.constructionYards[i].teamNo != 0){
				if(unitCountLow && MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.x != 0) {
					float xPos1 = MainThread.theAssetManager.constructionYards[i].centre.x;
					float zPos1 = MainThread.theAssetManager.constructionYards[i].centre.z;
					float xPos2 = MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.x;
					float zPos2 = MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.z;
					float d = (xPos1 - xPos2) * (xPos1 - xPos2) + (zPos1 - zPos2) * (zPos1 - zPos2);
					if(d < 9) {
						continue;
					}
				}
				
				index = i;
				if(MainThread.theAssetManager.constructionYards[i].centre.z < z && MainThread.theAssetManager.constructionYards[i].centre.z > 7 && MainThread.theAssetManager.constructionYards[i].centre.x > 7){
					x = MainThread.theAssetManager.constructionYards[i].centre.x;
					z = MainThread.theAssetManager.constructionYards[i].centre.z;
				}
			}
		}
		if(z != 999999) {
			
			rallyPoint.set(x - 2f, 0, z - 1.5f);
			
			if(frameAI < 240 && MainThread.enemyCommander.difficulty == 2) {
				rallyPoint.set(MainThread.theAssetManager.goldMines[5].centre);
			}
		}else {
			if(MainThread.theAssetManager.constructionYards[index] != null && MainThread.theAssetManager.constructionYards[index].teamNo !=0)
				rallyPoint.set(MainThread.theAssetManager.constructionYards[index].centre.x - 2.5f, 0,  MainThread.theAssetManager.constructionYards[index].centre.z -2.5f);
		}
		
		//If the difficulty is set to normal or hard, set the rally point just outside of player's natural expansion. 
		//So if the player is going for a fast expansion and don't have much units, the AI can perform a rush attack. 
		//if(MainThread.ec.theMapAwarenessAI.canRushPlayer && frameAI < 360 && MainThread.ec.theCombatManagerAI.checkIfAIHasBiggerForce(0.75f)) {
		//	rallyPoint.set(rushRallyPointX, 0,  rushRallyPointZ);
		//}
		
		//make sure not to over produce when the resource is running low
		int maxNumOfUnitCanBeProduced =  theBaseInfo.currentCredit / 500 + 1;
		
		
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			Factory f = MainThread.theAssetManager.factories[i];
			if(f != null && f.teamNo !=0){
				if(!f.isIdle())
					maxNumOfUnitCanBeProduced--;
			}
		}
		
		for(int i = 0; i < MainThread.theAssetManager.constructionYards.length; i++){
			ConstructionYard c = MainThread.theAssetManager.constructionYards[i];
			if(c != null && c.teamNo !=0){
				if(!c.isIdle())
					maxNumOfUnitCanBeProduced--;
			}
		}
		
		
		if(MainThread.enemyCommander.difficulty > 0) {
			//make decision on what unit to produce
			int numberOfPlayerGunTurrets=   MainThread.enemyCommander.theMapAwarenessAI.numberOfGunTurret_player;
			int numberOfPlayerMissileTurrets=  MainThread.enemyCommander.theMapAwarenessAI.numberOfMissileTurret_player;
			int numberOfLightTanks_player = MainThread.enemyCommander.theMapAwarenessAI.numberOfLightTanks_player;
			int numberOfRocketTanks_player = MainThread.enemyCommander.theMapAwarenessAI.numberOfRocketTanks_player;
			int numberOfStealthTanks_player = MainThread.enemyCommander.theMapAwarenessAI.numberOfStealthTanks_player;
			int numberOfHeavyTanks_player = MainThread.enemyCommander.theMapAwarenessAI.numberOfHeavyTanks_player;
			int maxNumberOfStealthTanks_playerInLastFiveMinutes =  MainThread.enemyCommander.theMapAwarenessAI.maxNumberOfStealthTanks_playerInLastFiveMinutes;
			
			boolean playerHasMostlyLightTanks = MainThread.enemyCommander.theMapAwarenessAI.playerHasMostlyLightTanks;
			boolean playerHasMostlyHeavyTanks =  MainThread.enemyCommander.theMapAwarenessAI.playerHasMostlyHeavyTanks;
			boolean playIsRushingHighTierUnits = MainThread.enemyCommander.theMapAwarenessAI.playIsRushingHighTierUnits;
			boolean playerLikelyCanNotProduceHighTierUnits = MainThread.enemyCommander.theMapAwarenessAI.playerLikelyCanNotProduceHighTierUnits;
			boolean playerDoesntHaveMassHeavyTanks = MainThread.enemyCommander.theMapAwarenessAI.playerDoesntHaveMassHeavyTanks;
			boolean playerHasManyLightTanksButNoHeavyTank = MainThread.enemyCommander.theMapAwarenessAI.playerHasManyLightTanksButNoHeavyTank;
			boolean playerHasMostlyHeavyAndStealthTanks = MainThread.enemyCommander.theMapAwarenessAI.playerHasMostlyHeavyAndStealthTanks;
			boolean playerHasMostlyLightAndStealthTanks = MainThread.enemyCommander.theMapAwarenessAI.playerHasMostlyLightAndStealthTanks;
			boolean playerArmyCanBeCounteredWithLightTanks = MainThread.enemyCommander.theMapAwarenessAI.playerArmyCanBeCounteredWithLightTanks;
			boolean playerArmyCanBeCounteredWithStealthTanks = MainThread.enemyCommander.theMapAwarenessAI.playerArmyCanBeCounteredWithStealthTanks;
			
			int timeToBuildHeavyTank = 500;
			int timeToBuildStealthTank = 200;
			if(MainThread.enemyCommander.theMapAwarenessAI.canRushPlayer) {
				//when AI decides to rush the player, then dont build higher tier units so it can mass produce light tanks
				timeToBuildHeavyTank = 500;
				timeToBuildStealthTank = 300;
			}
			
			boolean b1 = (numberOfRocketTanks_AI < 3 && !playerHasMostlyHeavyTanks  && (frameAI > 400 || frameAI > 170 && frameAI < 240 && MainThread.enemyCommander.theMapAwarenessAI.numberOfConstructionYard_player > 0) && !playerHasMostlyLightTanks);
			boolean b2 = (numberOfRocketTanks_AI < numberOfPlayerGunTurrets + numberOfPlayerMissileTurrets*1.5);
			if( b1 || b2){
				currentProductionOrder = produceRocketTank;
			}else if(theBaseInfo.canBuildHeavyTank && numberOfHeavyTanksControlledByCombatAI < 20 && !(numberOfStealthTanksControlledByCombatAI < 1) && !playerHasMostlyHeavyTanks && !playerHasMostlyLightTanks && !playerHasMostlyLightAndStealthTanks && !playerArmyCanBeCounteredWithLightTanks && !playerArmyCanBeCounteredWithStealthTanks &&
					 (playerHasMostlyHeavyAndStealthTanks || (frameAI > timeToBuildHeavyTank && numberOfHeavyTanks_AI < 3) ||
					 !playerHasManyLightTanksButNoHeavyTank
					 && !(numberOfHeavyTanks_player == 0 && maxNumberOfStealthTanks_playerInLastFiveMinutes < 3 &&  frameAI > 600)  
					 && !(playerHasMostlyHeavyTanks && numberOfStealthTanks_player < numberOfHeavyTanks_AI*2) 
					 && (playIsRushingHighTierUnits ||  maxNumberOfStealthTanks_playerInLastFiveMinutes*4 > numberOfHeavyTanks_AI))){
				currentProductionOrder = produceHeavyTank; 
			}else if(theBaseInfo.canBuildStealthTank && ((numberOfStealthTanksControlledByCombatAI < 1)  || (playerDoesntHaveMassHeavyTanks && !playerHasMostlyHeavyTanks && !playerArmyCanBeCounteredWithLightTanks && !(numberOfStealthTanksControlledByCombatAI >= 9 &&  frameAI < 600) && !(numberOfStealthTanksControlledByCombatAI >= 18 &&  frameAI > 600)
					 && (playerHasMostlyLightTanks || playerLikelyCanNotProduceHighTierUnits || playerDoesntHaveMassHeavyTanks || playerHasMostlyLightAndStealthTanks) && !playerHasMostlyHeavyTanks && (frameAI > timeToBuildStealthTank || numberOfLightTanks_player > 8)))){
				currentProductionOrder = produceStealthTank;
			}else{
				currentProductionOrder = produceLightTank;
			}
			
			//make decision on what tech to research
			if(MainThread.enemyCommander.theBuildingManagerAI.theBaseInfo.numberOfCommunicationCenter > 0 && MainThread.enemyCommander.difficulty > 1) {
				if(MainThread.enemyCommander.theDefenseManagerAI.needMissileTurret || theBaseInfo.currentCredit > 1500 && frameAI > 450) {
					if(!CommunicationCenter.rapidfireResearched_enemy) {
						if(CommunicationCenter.rapidfireResearchProgress_enemy == 255){
							CommunicationCenter.researchRapidfire(1);
							System.out.println("----------------------------AI starts researching rapid fire ability------------------------------------");
						}
					}
				}
				
				if(MainThread.enemyCommander.theEconomyManagerAI.numberOfharvesters >= 6 && theBaseInfo.currentCredit > 1500 &&  MainThread.enemyCommander.difficulty > 1) {
					if(!CommunicationCenter.harvesterSpeedResearched_enemy) {
						if(CommunicationCenter.harvesterSpeedResearchProgress_enemy == 255){
							CommunicationCenter.researchHarvesterSpeed(1);
							System.out.println("----------------------------AI starts researching Harvester  speed ability------------------------------------");
						}
					}
				}
			}
			
			if(MainThread.enemyCommander.theBuildingManagerAI.theBaseInfo.numberOfTechCenter > 0){
						
				//Immediately  start  stealth tank upgrades  when a tech center is built
				if(!TechCenter.stealthTankResearched_enemy){
					if(TechCenter.stealthTankResearchProgress_enemy == 255){
						TechCenter.cancelResearch(1);
						TechCenter.researchStealthTank(1);
						System.out.println("----------------------------AI starts researching stealth tank------------------------------------");
					}
				}
			
				
				if(numberOfLightTanks_AI >= 15  && theBaseInfo.currentCredit > 1000){
					if(!TechCenter.lightTankResearched_enemy){
						if(TechCenter.lightTankResearchProgress_enemy >= 240 && TechCenter.stealthTankResearchProgress_enemy >= 240 && TechCenter.rocketTankResearchProgress_enemy >= 240 && TechCenter.heavyTankResearchProgress_enemy >= 240){
							TechCenter.researchLightTank(1);
							System.out.println("----------------------------AI starts researching light tank------------------------------------");
						}
					}
				}
				
				if(numberOfRocketTanks_AI > 2 && theBaseInfo.currentCredit > 1250 && (numberOfPlayerGunTurrets > 0 || numberOfPlayerMissileTurrets > 0 || frameAI > 600)){
					if(!TechCenter.rocketTankResearched_enemy){
						if(TechCenter.lightTankResearchProgress_enemy >= 240 && TechCenter.stealthTankResearchProgress_enemy >= 240 && TechCenter.rocketTankResearchProgress_enemy >= 240 && TechCenter.heavyTankResearchProgress_enemy >= 240){
	
							TechCenter.researchRocketTank(1);
							System.out.println("----------------------------AI starts researching Rocket tank------------------------------------");
						}
					}
				}
				
				if(numberOfHeavyTanks_AI > 5 && theBaseInfo.currentCredit > 1000){
					if(!TechCenter.heavyTankResearched_enemy){
						if(TechCenter.lightTankResearchProgress_enemy >= 240 && TechCenter.stealthTankResearchProgress_enemy >= 240 && TechCenter.rocketTankResearchProgress_enemy >= 240 && TechCenter.heavyTankResearchProgress_enemy >= 240){
							TechCenter.researchHeavyTank(1);
							System.out.println("----------------------------AI starts researching heavy tank------------------------------------");
						}
					}
				}
				
				
			}
		}else {
			int roll = GameData.getRandom();
			if(roll < 612) {
				currentProductionOrder = produceLightTank;
			}else if(roll >= 612 && roll < 700) {
				currentProductionOrder = produceRocketTank;
			}else if(roll >= 700 && roll < 900) {
				currentProductionOrder = produceStealthTank;
			}else {
				currentProductionOrder = produceHeavyTank;
			}
			
		}
		
	
		for(int i = 0; i < MainThread.theAssetManager.factories.length; i++){
			Factory f = MainThread.theAssetManager.factories[i];
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
	
	public void addLightTank(LightTank o){
		//check if other AI agent need light tank
		
		if(MainThread.enemyCommander.theScoutingManagerAI.needLightTank()){
			MainThread.enemyCommander.theScoutingManagerAI.addLightTank(o);
			
			return;
		}
		
		
		//add the new light tank to combat AI's command
		for(int i = 0; i < lightTanksControlledByCombatAI.length; i++){
			if(lightTanksControlledByCombatAI[i] == null || (lightTanksControlledByCombatAI[i] != null && lightTanksControlledByCombatAI[i].currentHP <=0)){
				lightTanksControlledByCombatAI[i] = o;
				if(MainThread.enemyCommander.difficulty > 0)
					MainThread.enemyCommander.theDefenseManagerAI.addUnitToDefenders(o);
				break;
			}
		}
		
		
	}
	
	public void addRocketTank(RocketTank o){
		//check if other AI agent need Rocket tank
		
		//add the new Rocket tank to combat AI's command
		for(int i = 0; i < rocketTanksControlledByCombatAI.length; i++){
			if(rocketTanksControlledByCombatAI[i] == null || (rocketTanksControlledByCombatAI[i] != null && rocketTanksControlledByCombatAI[i].currentHP <=0)){
				rocketTanksControlledByCombatAI[i] = o;
				break;
			}
		}
	}
	
	public void addStealthTank(StealthTank o){
		//check if other AI agent need stealth tank
		
		if(MainThread.enemyCommander.theScoutingManagerAI.needStealthTank()){
			MainThread.enemyCommander.theScoutingManagerAI.scout.addStealthTank(o, MainThread.enemyCommander.theScoutingManagerAI);
			return;
		}
		
		if(MainThread.enemyCommander.theBaseExpentionAI.needStealthTank()){
			MainThread.enemyCommander.theBaseExpentionAI.addStealthTank(o);
			return;
		}
		
		
		
		//add the new stealth tank to combat AI's command
		for(int i = 0; i < stealthTanksControlledByCombatAI.length; i++){
			if(stealthTanksControlledByCombatAI[i] == null || (stealthTanksControlledByCombatAI[i] != null && stealthTanksControlledByCombatAI[i].currentHP <=0)){
				stealthTanksControlledByCombatAI[i] = o;
				if(MainThread.enemyCommander.difficulty > 0)
					MainThread.enemyCommander.theDefenseManagerAI.addUnitToDefenders(o);
				break;
			}
		}
	}
	
	public void addHeavyTank(HeavyTank o){
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
		
		//exclude the units are too far away from the center of the troops, (i.e the unites that just come out of the Factory), and recalculate the center
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
		if(MainThread.enemyCommander.theCombatManagerAI.currentState == MainThread.enemyCommander.theCombatManagerAI.aggressing) {
			if(MainThread.enemyCommander.theCombatManagerAI.distanceToTarget < 6)
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
