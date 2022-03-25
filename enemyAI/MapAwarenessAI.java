package enemyAI;

import core.AssetManager;
import core.BaseInfo;
import core.MainThread;
import entity.Harvester;
import entity.SolidObject;
import entity.ConstructionYard;
import entity.GoldMine;
import core.vector;

//1. scan revealed area for player's units and building
//2. keep track of player's units
//3. create strategic information based on the quantity/location  of player's units


public class MapAwarenessAI {

	public BaseInfo theBaseInfo;
	public int frameAI;

	public int numberOfLightTanks_player, numberOfLightTanks_AI, numberOfLightTanksOnMinimap_player;
	public int numberOfStealthTanks_player, numberOfStealthTanks_AI, numberOfStealthTanksOnMinimap_player;
	public int numberOfRocketTanks_player, numberOfRocketTanks_AI, numberOfRocketTanksOnMinimap_player;
	public int numberOfHeavyTanks_player, numberOfHeavyTanks_AI, numberOfHeavyTanksOnMinimap_player;
	public int numberOfPlayerUnitsOnMinimap;
	public int totalNumberOfPlayerUnits;
	public int numberOfGunTurret_player;
	public int numberOfMissileTurret_player;
	public int numberOfFactory_player;
	public int numberOfRefinery_player;
	public int numberOfConstructionYard_player;
	public int numberOfCommunicationCenter_player;
	public int numberOfTechCenter_player;
	public int numberOfPowerPlant_player;

	public int maxNumberOfStealthTanks_playerInLastFiveMinutes;
	public int fiveMinuteTimer;

	public int numberOfPlayerUnitDestroyed;
	public int numberOfPlayerBuildingDestroyed;
	public int numberOfPlayerUnitDestroyedInPreviousFrame;
	public int numberOfPlayerBuildingDestroyedPreviousFrame;
	public int playerAssetDestoryedCountDown;

	public boolean playerHasMostlyLightTanks;
	public boolean playerHasMostlyHeavyTanks;
	public boolean playIsRushingHighTierUnits;
	public boolean playerLikelyCanNotProduceHighTierUnits;
	public boolean playerDoesntHaveMassHeavyTanks;
	public boolean playerArmyCanBeCounteredWithLightTanks;
	public boolean playerArmyCanBeCounteredWithStealthTanks;
	public boolean playerIsRushingLightTank;
	public boolean playerHasManyLightTanksButNoHeavyTank;
	public boolean playerHasMostlyHeavyAndStealthTanks;
	public boolean playerHasMostlyLightAndStealthTanks;
	public boolean canRushPlayer;
	public boolean playerIsFastExpanding;
	public boolean playerForceNearBase;

	public SolidObject[] mapAsset;
	public boolean[] visionMap;
	public AssetManager theAssetManager;
	public SolidObject[] playerUnitInMinimap;
	public SolidObject[] playerStaticDefenceInMinimap;
	public SolidObject[] playerStructures;
	public int numOfAIStructures;
	public SolidObject[] AIStructures;

	public GoldMine[] goldMines;
	public int targetPlayerExpension;
	public int[] playerExpensionInfo;
	public int numberOfplayerMiningBases;

	public vector mainPlayerForceLocation;
	public vector mainPlayerForceDirection;
	public int mainPlayerForceSize;
	public vector[] playerForceLocations;
	public vector[] playerForceDirections;
	public int[] playerForceSize;
	public vector playerNaturalLocation;

	public vector[] playerStaticDefenseLocations;
	public int[] playerStaticDefenseSize;
	public int[] playerStaticDefenseStrength;

	public MapAwarenessAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
		this.visionMap = MainThread.enemyCommander.visionMap;

		mapAsset = new SolidObject[1024];
		playerUnitInMinimap = new SolidObject[128];
		playerStaticDefenceInMinimap = new SolidObject[64];
		playerStructures = new SolidObject[256];
		AIStructures = new SolidObject[128];

		goldMines = MainThread.theAssetManager.goldMines;
		playerExpensionInfo = new int[goldMines.length];
		playerNaturalLocation = goldMines[1].centre;

		mainPlayerForceLocation = new vector(0,0,0);
		mainPlayerForceDirection = new vector(0,0,0);
		mainPlayerForceSize = 0;
		playerForceLocations = new vector[3];
		playerForceDirections = new vector[3];
		playerForceSize = new int[3];

		for(int i = 0; i < playerForceLocations.length; i++) {
			playerForceLocations[i] = new vector(0,0,0);
			playerForceDirections[i] = new vector(0,0,0);
			playerForceSize[i] = 0;
		}

		playerStaticDefenseLocations = new vector[3];
		playerStaticDefenseSize = new int[3];
		playerStaticDefenseStrength = new int[3];

		for(int i = 0; i < playerStaticDefenseLocations.length; i++) {
			playerStaticDefenseLocations[i] = new vector(0,0,0);
			playerStaticDefenseSize[i] = 0;
			playerStaticDefenseStrength[i] = 0;
		}
	}

	public void processAI(){
		frameAI = MainThread.enemyCommander.frameAI;

		theAssetManager = MainThread.theAssetManager;

		//the number of player's military units in AI's vision
		numberOfLightTanksOnMinimap_player = 0;
		numberOfRocketTanksOnMinimap_player = 0;
		numberOfStealthTanksOnMinimap_player = 0;
		numberOfHeavyTanksOnMinimap_player = 0;
		numberOfPlayerUnitsOnMinimap = 0;

		//the total number of player's unit that are detected by AI
		numberOfLightTanks_player = 0;
		numberOfRocketTanks_player = 0;
		numberOfStealthTanks_player = 0;
		numberOfHeavyTanks_player = 0;
		numberOfGunTurret_player = 0;
		numberOfMissileTurret_player = 0;
		numberOfFactory_player = 0;
		numberOfRefinery_player = 0;
		numberOfConstructionYard_player = 0;
		numberOfCommunicationCenter_player = 0;
		numberOfTechCenter_player = 0;

		//the total number of AI military  units
		numberOfLightTanks_AI = 0;
		numberOfRocketTanks_AI = 0;
		numberOfStealthTanks_AI = 0;
		numberOfHeavyTanks_AI = 0;

		//clear info from previous frame
		for(int i = 0; i < playerUnitInMinimap.length; i++)
			playerUnitInMinimap[i] = null;
		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++)
			playerStaticDefenceInMinimap[i] = null;
		for(int i = 0; i < playerStructures.length; i++)
			playerStructures[i] = null;
		for(int i = 0; i < AIStructures.length; i++)
			AIStructures[i] = null;
		numOfAIStructures = 0;

		if(playerAssetDestoryedCountDown > 0)
			playerAssetDestoryedCountDown--;


		for(int i = 0; i < theAssetManager.LightTanks.length; i++){
			if(theAssetManager.LightTanks[i] != null && theAssetManager.LightTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.LightTanks[i].occupiedTile0]){
					numberOfLightTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.LightTanks[i]);
					if(mapAsset[theAssetManager.LightTanks[i].ID] == null) {
						mapAsset[theAssetManager.LightTanks[i].ID] = theAssetManager.LightTanks[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=1;
					}
				}
			}else if(theAssetManager.LightTanks[i] != null && theAssetManager.LightTanks[i].teamNo !=0){
				numberOfLightTanks_AI++;
				if(mapAsset[theAssetManager.LightTanks[i].ID] == null){
					MainThread.enemyCommander.theUnitProductionAI.addLightTank(theAssetManager.LightTanks[i]);
					mapAsset[theAssetManager.LightTanks[i].ID] = theAssetManager.LightTanks[i];
				}
			}
		}

		for(int i = 0; i < theAssetManager.RocketTanks.length; i++){
			if(theAssetManager.RocketTanks[i] != null && theAssetManager.RocketTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.RocketTanks[i].occupiedTile0]){
					numberOfRocketTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.RocketTanks[i]);
					if(mapAsset[theAssetManager.RocketTanks[i].ID] == null) {
						mapAsset[theAssetManager.RocketTanks[i].ID] = theAssetManager.RocketTanks[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=1.5;
					}
				}
			}else if(theAssetManager.RocketTanks[i] != null && theAssetManager.RocketTanks[i].teamNo !=0){
				numberOfRocketTanks_AI++;
				if(mapAsset[theAssetManager.RocketTanks[i].ID] == null){
					MainThread.enemyCommander.theUnitProductionAI.addRocketTank(theAssetManager.RocketTanks[i]);
					mapAsset[theAssetManager.RocketTanks[i].ID] = theAssetManager.RocketTanks[i];
				}
			}
		}

		for(int i = 0; i < theAssetManager.stealthTanks.length; i++){
			if(theAssetManager.stealthTanks[i] != null && theAssetManager.stealthTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.stealthTanks[i].occupiedTile0] && !theAssetManager.stealthTanks[i].isCloaked){
					numberOfStealthTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.stealthTanks[i]);
					if(mapAsset[theAssetManager.stealthTanks[i].ID] == null) {
						mapAsset[theAssetManager.stealthTanks[i].ID] = theAssetManager.stealthTanks[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=2;
					}
				}
			}else if(theAssetManager.stealthTanks[i] != null && theAssetManager.stealthTanks[i].teamNo !=0){
				numberOfStealthTanks_AI++;
				if(mapAsset[theAssetManager.stealthTanks[i].ID] == null){
					MainThread.enemyCommander.theUnitProductionAI.addStealthTank(theAssetManager.stealthTanks[i]);
					mapAsset[theAssetManager.stealthTanks[i].ID] = theAssetManager.stealthTanks[i];
				}
			}

		}

		for(int i = 0; i < theAssetManager.HeavyTanks.length; i++){
			if(theAssetManager.HeavyTanks[i] != null && theAssetManager.HeavyTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.HeavyTanks[i].occupiedTile0]){
					numberOfHeavyTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.HeavyTanks[i]);
					if(mapAsset[theAssetManager.HeavyTanks[i].ID] == null) {
						mapAsset[theAssetManager.HeavyTanks[i].ID] = theAssetManager.HeavyTanks[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=3.5;
					}
				}
			}else if(theAssetManager.HeavyTanks[i] != null && theAssetManager.HeavyTanks[i].teamNo !=0){
				numberOfHeavyTanks_AI++;
				if(mapAsset[theAssetManager.HeavyTanks[i].ID] == null){
					MainThread.enemyCommander.theUnitProductionAI.addHeavyTank(theAssetManager.HeavyTanks[i]);
					mapAsset[theAssetManager.HeavyTanks[i].ID] = theAssetManager.HeavyTanks[i];
				}
			}
		}

		for(int i = 0; i < theAssetManager.Harvesters.length; i++){
			if(theAssetManager.Harvesters[i] != null && theAssetManager.Harvesters[i].teamNo ==0){
				if(visionMap[theAssetManager.Harvesters[i].occupiedTile0]){
					//addPlayerUnitInMinimap(theAssetManager.Harvesters[i]);
					mapAsset[theAssetManager.Harvesters[i].ID] = theAssetManager.Harvesters[i];
				}
			}
		}

		for(int i = 0; i < theAssetManager.constructionVehicles.length; i++){
			if(theAssetManager.constructionVehicles[i] != null && theAssetManager.constructionVehicles[i].teamNo ==0){
				if(visionMap[theAssetManager.constructionVehicles[i].occupiedTile0]){
					addPlayerUnitInMinimap(theAssetManager.constructionVehicles[i]);
					mapAsset[theAssetManager.constructionVehicles[i].ID] = theAssetManager.constructionVehicles[i];
				}
			}
		}


		//add revealed player's building to mapAsset
		for(int i = 0; i < theAssetManager.GunTurrents.length; i++){
			if(theAssetManager.GunTurrents[i] != null && theAssetManager.GunTurrents[i].teamNo ==0){
				if(visionMap[theAssetManager.GunTurrents[i].tileIndex[0]]){
					if(mapAsset[theAssetManager.GunTurrents[i].ID] == null) {
						mapAsset[theAssetManager.GunTurrents[i].ID] = theAssetManager.GunTurrents[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=1;
					}
				}
			}else if(theAssetManager.GunTurrents[i] != null && theAssetManager.GunTurrents[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.GunTurrents[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.MissileTurrets.length; i++){
			if(theAssetManager.MissileTurrets[i] != null && theAssetManager.MissileTurrets[i].teamNo ==0){
				if(visionMap[theAssetManager.MissileTurrets[i].tileIndex[0]]){
					if(mapAsset[theAssetManager.MissileTurrets[i].ID] == null) {
						mapAsset[theAssetManager.MissileTurrets[i].ID] = theAssetManager.MissileTurrets[i];
						MainThread.enemyCommander.theCombatManagerAI.unrevealedPlayerForceStrength-=2;
					}
				}
			}else if(theAssetManager.MissileTurrets[i] != null && theAssetManager.MissileTurrets[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.MissileTurrets[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.factories.length; i++){
			if(theAssetManager.factories[i] != null && theAssetManager.factories[i].teamNo ==0){
				for(int j = 0; j < 6; j++){
					if(visionMap[theAssetManager.factories[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.factories[i].ID] == null)
							mapAsset[theAssetManager.factories[i].ID] = theAssetManager.factories[i];
						break;
					}
				}
			}else if(theAssetManager.factories[i] != null && theAssetManager.factories[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.factories[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.refineries.length; i++){
			if(theAssetManager.refineries[i] != null && theAssetManager.refineries[i].teamNo ==0){
				for(int j = 0; j < 6; j++){
					if(visionMap[theAssetManager.refineries[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.refineries[i].ID] == null)
							mapAsset[theAssetManager.refineries[i].ID] = theAssetManager.refineries[i];
						break;
					}
				}
			}else if(theAssetManager.refineries[i] != null && theAssetManager.refineries[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.refineries[i];
				numOfAIStructures++;
			}
		}


		for(int i = 0; i < theAssetManager.constructionYards.length; i++){
			if(theAssetManager.constructionYards[i] != null && theAssetManager.constructionYards[i].teamNo ==0){
				for(int j = 0; j < 9; j++){
					if(visionMap[theAssetManager.constructionYards[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.constructionYards[i].ID] == null)
							mapAsset[theAssetManager.constructionYards[i].ID] = theAssetManager.constructionYards[i];
						break;
					}
				}
			}else if(theAssetManager.constructionYards[i] != null && theAssetManager.constructionYards[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.constructionYards[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.communicationCenters.length; i++){
			if(theAssetManager.communicationCenters[i] != null && theAssetManager.communicationCenters[i].teamNo ==0){
				for(int j = 0; j < 4; j++){
					if(visionMap[theAssetManager.communicationCenters[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.communicationCenters[i].ID] == null)
							mapAsset[theAssetManager.communicationCenters[i].ID] = theAssetManager.communicationCenters[i];
						break;
					}
				}
			}else if(theAssetManager.communicationCenters[i] != null && theAssetManager.communicationCenters[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.communicationCenters[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.TechCenters.length; i++){
			if(theAssetManager.TechCenters[i] != null && theAssetManager.TechCenters[i].teamNo ==0){
				for(int j = 0; j < 4; j++){
					if(visionMap[theAssetManager.TechCenters[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.TechCenters[i].ID] == null)
							mapAsset[theAssetManager.TechCenters[i].ID] = theAssetManager.TechCenters[i];
						break;
					}
				}
			}else if(theAssetManager.TechCenters[i] != null && theAssetManager.TechCenters[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.TechCenters[i];
				numOfAIStructures++;
			}
		}

		for(int i = 0; i < theAssetManager.PowerPlants.length; i++){
			if(theAssetManager.PowerPlants[i] != null && theAssetManager.PowerPlants[i].teamNo ==0){
				for(int j = 0; j < 4; j++){
					if(visionMap[theAssetManager.PowerPlants[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.PowerPlants[i].ID] == null)
							mapAsset[theAssetManager.PowerPlants[i].ID] = theAssetManager.PowerPlants[i];
						break;
					}
				}
			}else if(theAssetManager.PowerPlants[i] != null && theAssetManager.PowerPlants[i].teamNo !=0) {
				AIStructures[numOfAIStructures] = theAssetManager.PowerPlants[i];
				numOfAIStructures++;
			}
		}


		numberOfPlayerUnitDestroyed = 0;
		numberOfPlayerBuildingDestroyed = 0;


		for(int i = 0; i < mapAsset.length; i++){
			if(mapAsset[i] != null && mapAsset[i].teamNo == 0){
				if(mapAsset[i].currentHP>0){
					if(mapAsset[i].type == 0 )
						numberOfLightTanks_player++;
					else if(mapAsset[i].type == 1)
						numberOfRocketTanks_player++;
					else if(mapAsset[i].type == 6)
						numberOfStealthTanks_player++;
					else if(mapAsset[i].type == 7)
						numberOfHeavyTanks_player++;
					else{
						mapAsset[i].isRevealed_AI = true;
						if(mapAsset[i].type == 200){
							numberOfGunTurret_player++;
							addPlayerStaticDefenceInMinimap(mapAsset[i]);
							addPlayerStructure(mapAsset[i]);
						}else if(mapAsset[i].type == 199){
							addPlayerStaticDefenceInMinimap(mapAsset[i]);
							addPlayerStructure(mapAsset[i]);
							numberOfMissileTurret_player++;
						}else if(mapAsset[i].type == 105){
							addPlayerStructure(mapAsset[i]);
							numberOfFactory_player++;
						}else if(mapAsset[i].type == 102){
							numberOfRefinery_player++;
							addPlayerStructure(mapAsset[i]);
						}else if(mapAsset[i].type == 104){
							addPlayerStructure(mapAsset[i]);
							numberOfConstructionYard_player++;
						}else if(mapAsset[i].type == 106){
							addPlayerStructure(mapAsset[i]);
							numberOfCommunicationCenter_player++;
						}else if(mapAsset[i].type == 107){
							addPlayerStructure(mapAsset[i]);
							numberOfTechCenter_player++;
						}else if(mapAsset[i].type == 101){
							addPlayerStructure(mapAsset[i]);
							numberOfPowerPlant_player++;
						}
					}
				}else{
					if(mapAsset[i].attacker != null && mapAsset[i].attacker.teamNo != 0) {
						if(mapAsset[i].type < 100)
							numberOfPlayerUnitDestroyed++;
						else
							numberOfPlayerBuildingDestroyed++;
					}
				}
			}
		}

		if(numberOfPlayerUnitDestroyed > numberOfPlayerUnitDestroyedInPreviousFrame || numberOfPlayerBuildingDestroyed > numberOfPlayerBuildingDestroyedPreviousFrame) {
			playerAssetDestoryedCountDown = 30;
		}

		numberOfPlayerUnitDestroyedInPreviousFrame = numberOfPlayerUnitDestroyed;
		numberOfPlayerBuildingDestroyedPreviousFrame = numberOfPlayerBuildingDestroyed;

		//analyze the enemy units composition
		totalNumberOfPlayerUnits = numberOfLightTanks_player + numberOfRocketTanks_player + numberOfStealthTanks_player + numberOfHeavyTanks_player;

		float lightTankRatio = (float)(numberOfLightTanks_player)/(totalNumberOfPlayerUnits + 1);

		playerHasMostlyLightTanks = (numberOfLightTanks_player > 5 &&  lightTankRatio > 0.8f) || (frameAI < 420 && numberOfLightTanks_player > 1 && lightTankRatio >= 0.75f);
		playerHasMostlyHeavyTanks = numberOfHeavyTanks_player > 1 && (float)(numberOfHeavyTanks_player)/(totalNumberOfPlayerUnits) > 0.8f;
		playerHasManyLightTanksButNoHeavyTank = lightTankRatio > 0.5  && numberOfHeavyTanks_player < 3;

		playIsRushingHighTierUnits = MainThread.gameFrame/30 > 250 && MainThread.gameFrame/30 < 400
				&& MainThread.enemyCommander.theMapAwarenessAI.numberOfTechCenter_player >0
				&& MainThread.enemyCommander.theMapAwarenessAI.numberOfMissileTurret_player < 2
				&& MainThread.enemyCommander.theMapAwarenessAI.numberOfGunTurret_player < 4
				&& numberOfLightTanks_player + numberOfRocketTanks_player + numberOfStealthTanks_player < 5;

		playerLikelyCanNotProduceHighTierUnits = MainThread.enemyCommander.theMapAwarenessAI.numberOfTechCenter_player == 0 && MainThread.enemyCommander.theMapAwarenessAI.numberOfHeavyTanks_player == 0;
		playerDoesntHaveMassHeavyTanks =  (float)numberOfHeavyTanks_player/( 1 + numberOfLightTanks_AI + numberOfRocketTanks_player + numberOfStealthTanks_player)  < 0.2f;

		playerIsRushingLightTank = MainThread.gameFrame/30 > 300 && MainThread.gameFrame/30 < 600 && ((playerLikelyCanNotProduceHighTierUnits && numberOfStealthTanks_player < 3) || playerHasMostlyLightTanks);

		playerHasMostlyHeavyAndStealthTanks = (maxNumberOfStealthTanks_playerInLastFiveMinutes >=3 ) && (float)(numberOfHeavyTanks_player + numberOfStealthTanks_player)/totalNumberOfPlayerUnits > 0.85f;

		playerHasMostlyLightAndStealthTanks = numberOfLightTanks_player > 5 && maxNumberOfStealthTanks_playerInLastFiveMinutes >=3 && (float)(numberOfLightTanks_player + numberOfStealthTanks_player)/totalNumberOfPlayerUnits > 0.85f;

		if(frameAI < 600)
			playerArmyCanBeCounteredWithLightTanks = false;
		else {
			playerArmyCanBeCounteredWithLightTanks = maxNumberOfStealthTanks_playerInLastFiveMinutes < 6 && (float)(numberOfHeavyTanks_player+ numberOfRocketTanks_player)/(totalNumberOfPlayerUnits + 1) > 0.85f;
		}

		playerArmyCanBeCounteredWithStealthTanks =  (float)(numberOfLightTanks_player+ numberOfRocketTanks_player)/(totalNumberOfPlayerUnits + 1) > 0.85f;


		//advanced counting of player units
		if(numberOfStealthTanks_player > maxNumberOfStealthTanks_playerInLastFiveMinutes) {
			maxNumberOfStealthTanks_playerInLastFiveMinutes = numberOfStealthTanks_player;
			fiveMinuteTimer = 300;
		}
		if(fiveMinuteTimer > 0)
			fiveMinuteTimer--;
		else
			maxNumberOfStealthTanks_playerInLastFiveMinutes = 0;


		//if player fast expand then AI can rush the player
		canRushPlayer = false;
		if(frameAI >= 240 && frameAI < 360) {

			for(int i = 0; i < playerStructures.length; i++) {
				if(playerStructures[i] != null && playerStructures[i].currentHP > 0) {
					float x1 = playerStructures[i].centre.x;
					float z1 = playerStructures[i].centre.z;
					float x2 = playerNaturalLocation.x;
					float z2 = playerNaturalLocation.z;

					if(Math.sqrt((x1-x2)*(x1-x2) + (z1-z2)*(z1-z2)) < 3.5f) {
						playerIsFastExpanding = true;
						break;
					}
				}
			}

			Harvester[] Harvesters = MainThread.theAssetManager.Harvesters;
			for(int i = 0; i < Harvesters.length; i++) {
				if(Harvesters[i] != null && Harvesters[i].currentHP > 0) {
					float x1 = Harvesters[i].centre.x;
					float z1 = Harvesters[i].centre.z;
					float x2 = playerNaturalLocation.x;
					float z2 = playerNaturalLocation.z;

					if(Math.sqrt((x1-x2)*(x1-x2) + (z1-z2)*(z1-z2)) < 3.5f) {
						playerIsFastExpanding = true;
						break;
					}
				}


			}

		}

		if(playerIsFastExpanding) {

			if(MainThread.enemyCommander.theCombatManagerAI.checkIfAIHasBiggerForce(0.5f))
				canRushPlayer = true;
		}

		findTheMostVulnerablePlayerBase();

		findPlayerForceLocation();

		findPlayerStaticDefense();

	}


	public void addPlayerUnitInMinimap(SolidObject o){
		for(int i = 0; i < playerUnitInMinimap.length; i++){
			if(playerUnitInMinimap[i] == null){
				playerUnitInMinimap[i] = o;
				numberOfPlayerUnitsOnMinimap++;
				break;
			}
		}
	}

	public void addPlayerStaticDefenceInMinimap(SolidObject o){
		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++){
			if(playerStaticDefenceInMinimap[i] == null){
				playerStaticDefenceInMinimap[i] = o;
				break;
			}
		}
	}

	public void addPlayerStructure(SolidObject o){
		for(int i = 0; i < playerStructures.length; i++){
			if(playerStructures[i] == null){
				playerStructures[i] = o;
				break;
			}
		}
	}

	public void findTheMostVulnerablePlayerBase(){

		//check if there are any player's structure around each gold mine.
		for(int i = 0; i < goldMines.length; i++){
			playerExpensionInfo[i] = findplayexpensionDefenseScore(goldMines[i], 3.5f);
		}

		//compute the target player expansion defense score
		targetPlayerExpension = -1;
		int playExpensionDefenseScore = 999999;


		numberOfplayerMiningBases = 0;
		for(int i = 0; i < playerExpensionInfo.length; i++){
			if(goldMines[i] != null && goldMines[i].goldDeposite > 5000 && playerExpensionInfo[i] != 0){
				numberOfplayerMiningBases++;
				if(playerExpensionInfo[i] < playExpensionDefenseScore){
					playExpensionDefenseScore = playerExpensionInfo[i];
					targetPlayerExpension = i;
				}

			}
		}


		//if a player expansion exists on the path to the target expansion is already been taken by player's force,
		//then mark it as the target expansion instead
		if(targetPlayerExpension == 0){
			if(playerExpensionInfo[5] >0)
				targetPlayerExpension = 5;
			else if(playerExpensionInfo[6] >0)
				targetPlayerExpension = 6;
			else if(playerExpensionInfo[7] >0)
				targetPlayerExpension = 7;
			else if(playerExpensionInfo[1] > 0)
				targetPlayerExpension = 1;
		}else if(targetPlayerExpension == 1){
			if(playerExpensionInfo[5] >0)
				targetPlayerExpension = 5;
			else if(playerExpensionInfo[6] >0)
				targetPlayerExpension = 6;
			else if(playerExpensionInfo[7] >0)
				targetPlayerExpension = 7;
		}else if(targetPlayerExpension == 7){
			if(playerExpensionInfo[5] >0)
				targetPlayerExpension = 5;
			else if(playerExpensionInfo[6] >0)
				targetPlayerExpension = 6;
		}else if(targetPlayerExpension == 6){
			if(playerExpensionInfo[5] >0)
				targetPlayerExpension = 5;
		}

		//if(targetPlayerExpension != -1)
		//	return playerExpensionInfo[targetPlayerExpension];

		//return 0;
	}

	public int findplayexpensionDefenseScore(GoldMine g, float r){
		if(g == null)
			return 0;

		SolidObject[] playerStructures = MainThread.enemyCommander.theMapAwarenessAI.playerStructures;
		SolidObject[] playerStaticDefence = MainThread.enemyCommander.theMapAwarenessAI.playerStaticDefenceInMinimap;

		float x = g.centre.x;
		float z = g.centre.z;

		int playexpensionDefenseScore = 0;
		for(int i = 0 ; i < playerStructures.length; i++){
			if(playerStructures[i]!= null && Math.abs(playerStructures[i].centre.x - x) < r && Math.abs(playerStructures[i].centre.z - z) < r)
				playexpensionDefenseScore++;
		}

		for(int i = 0 ; i < playerStaticDefence.length; i++){
			if(playerStaticDefence[i]!= null && Math.abs(playerStructures[i].centre.x - x) < r && Math.abs(playerStructures[i].centre.z - z) < r){
				if(playerStaticDefence[i].type == 200)   //gun turret will increase player base's defense score
					playexpensionDefenseScore+=1000;
				if(playerStaticDefence[i].type == 199)   //missile turret will increase player base's defense score even futher
					playexpensionDefenseScore+=3000;
			}
		}


		//if the player already takes the expansion which the enemy AI plans to expand to, assign zero score to this expansion
		if(g == MainThread.enemyCommander.theBaseExpentionAI.expensionGoldMine && playexpensionDefenseScore > 0){
			return -1;
		}

		return playexpensionDefenseScore;
	}

	//find the center of the clusters of player's static defenses. As well as the size of the clusters.
	public void findPlayerStaticDefense() {
		for(int i = 0; i < playerStaticDefenseLocations.length; i++) {
			playerStaticDefenseLocations[i].set(0,0,0);
			playerStaticDefenseSize[i] = 0;
			playerStaticDefenseStrength[i] = 0;
		}

		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++) {
			if(playerStaticDefenceInMinimap[i] == null)
				continue;
			float xPos = playerStaticDefenceInMinimap[i].centre.x;
			float zPos = playerStaticDefenceInMinimap[i].centre.z;

			for(int j = 0; j < playerStaticDefenseLocations.length; j++) {
				//always add the player static defense location to the empty list
				if(playerStaticDefenseLocations[j].x == 0) {
					playerStaticDefenseLocations[j].add(playerStaticDefenceInMinimap[i].centre);
					playerStaticDefenseSize[j]++;
					if(playerStaticDefenceInMinimap[j].type == 200)
						playerStaticDefenseStrength[j]+=2;
					if(playerStaticDefenceInMinimap[j].type == 199)
						playerStaticDefenseStrength[j]+=6;
					break;
				}
				float centerX = playerStaticDefenseLocations[j].x/playerStaticDefenseSize[j];
				float centerZ = playerStaticDefenseLocations[j].z/playerStaticDefenseSize[j];
				float d = (centerX - xPos) * (centerX - xPos) + (centerZ - zPos) * (centerZ - zPos);
				//if the player static defence is close enough to the cluster center then add it to the list
				if(d < 4) {
					playerStaticDefenseLocations[j].add(playerStaticDefenceInMinimap[i].centre);
					playerStaticDefenseSize[j]++;
					if(playerStaticDefenceInMinimap[j].type == 200)
						playerStaticDefenseStrength[j]+=2;
					if(playerStaticDefenceInMinimap[j].type == 199)
						playerStaticDefenseStrength[j]+=6;
					break;
				}
			}

		}

		for(int i = 0; i < playerStaticDefenseLocations.length; i++) {
			if(playerStaticDefenseSize[i] > 0)
				playerStaticDefenseLocations[i].set(playerStaticDefenseLocations[i].x/playerStaticDefenseSize[i], 0, playerStaticDefenseLocations[i].z/playerStaticDefenseSize[i]);

		}



	}

	//find the center of the biggest cluster of player units that are visible on the minimap. It will tells the AI which area is in danger of being attacked.
	public void findPlayerForceLocation(){
		mainPlayerForceLocation.set(0,0,0);
		mainPlayerForceDirection.set(0,0,0);
		mainPlayerForceSize = 0;

		for(int i = 0; i < playerForceLocations.length; i++) {
			playerForceLocations[i].set(0,0,0);
			playerForceDirections[i].set(0,0,0);
			playerForceSize[i] = 0;
		}

		for(int i = 0; i < playerUnitInMinimap.length; i++) {
			if(playerUnitInMinimap[i] == null)
				continue;
			float xPos = playerUnitInMinimap[i].centre.x;
			float zPos = playerUnitInMinimap[i].centre.z;

			for(int j = 0; j < playerForceLocations.length; j++) {
				//always add the player unit location to the empty list
				if(playerForceLocations[j].x == 0) {
					playerForceLocations[j].add(playerUnitInMinimap[i].centre);
					playerForceSize[j]++;
					playerForceDirections[j].add(playerUnitInMinimap[i].movement);
					break;
				}
				float centerX = playerForceLocations[j].x/playerForceSize[j];
				float centerZ = playerForceLocations[j].z/playerForceSize[j];
				float d = (centerX - xPos) * (centerX - xPos) + (centerZ - zPos) * (centerZ - zPos);
				//if the player unit is close enough to the force center then add it to the list
				if(d < 4) {
					playerForceLocations[j].add(playerUnitInMinimap[i].centre);
					playerForceSize[j]++;
					playerForceDirections[j].add(playerUnitInMinimap[i].movement);
					break;
				}
			}
		}

		for(int i = 0; i < playerForceLocations.length; i++) {
			if(playerForceSize[i] > mainPlayerForceSize) {
				mainPlayerForceSize = playerForceSize[i];
				mainPlayerForceLocation.set(playerForceLocations[i].x/mainPlayerForceSize,0,playerForceLocations[i].z/mainPlayerForceSize);
				mainPlayerForceDirection.set(playerForceDirections[i].x/mainPlayerForceSize, 0, playerForceDirections[i].z/mainPlayerForceSize);
			}
		}

		if(mainPlayerForceDirection.getLength() < 0.001) {
			mainPlayerForceDirection.reset();
		}else {
			mainPlayerForceDirection.unit();
		}

		//check if player force is near any of AI's base
		playerForceNearBase = false;
		float x = mainPlayerForceLocation.x;
		float z = mainPlayerForceLocation.z;
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		for(int i = 0; i < constructionYards.length; i++) {
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentHP > 0) {
				double d = Math.sqrt((constructionYards[i].centre.x - x)*(constructionYards[i].centre.x - x) + (constructionYards[i].centre.z - z)*(constructionYards[i].centre.z - z));
				if(d < 4.75) {
					playerForceNearBase = true;
					break;
				}

			}
		}
	}


}
