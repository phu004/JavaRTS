package enemyAI;

import core.AssetManager;
import core.baseInfo;
import core.mainThread;
import entity.goldMine;
import entity.solidObject;
import core.vector;

//1. scan revealed area for player's units and building
//2. keep track of player's units
//3. create strategic information based on the quantity/location  of player's units


public class mapAwarenessAI {
	
	public baseInfo theBaseInfo;
	
	public int numberOfLightTanks_player, numberOfLightTanks_AI, numberOfLightTanksOnMinimap_player;
	public int numberOfStealthTanks_player, numberOfStealthTanks_AI, numberOfStealthTanksOnMinimap_player;
	public int numberOfRocketTanks_player, numberOfRocketTanks_AI, numberOfRocketTanksOnMinimap_player;
	public int numberOfHeavyTanks_player, numberOfHeavyTanks_AI, numberOfHeavyTanksOnMinimap_player;
	public int numberOfPlayerUnitsOnMinimap;
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
	
	public boolean playerHasMostlyLightTanks;
	public boolean playerHasMostlyHeavyTanks;
	public boolean playIsRushingHighTierUnits;
	public boolean playerLikelyCanNotProduceHighTierUnits;
	public boolean playerDoesntHaveMassHeavyTanks;
	public boolean playerIsRushingLightTank;
	public boolean playerHasManyLightTanksButNoHeavyTank;
	
	public solidObject[] mapAsset;
	public boolean[] visionMap;
	public AssetManager theAssetManager;
	public solidObject[] playerUnitInMinimap;
	public solidObject[] playerStaticDefenceInMinimap;
	public solidObject[] playerStructures;
	
	public goldMine[] goldMines; 
	public int targetPlayerExpension;
	public int[] playerExpensionInfo;
	public int numberOfplayerMiningBases;
	
	public vector mainPlayerForceLocation;
	public vector mainPlayerForceDirection;
	public int mainPlayerForceSize;
	public vector[] playerForceLocations;
	public vector[] playerForceDirections;
	public int[] playerForceSize;
	
	public mapAwarenessAI(baseInfo theBaseInfo, boolean[] visionMap){
		this.theBaseInfo = theBaseInfo;
		this.visionMap = visionMap;
		
		mapAsset = new solidObject[1024];
		playerUnitInMinimap = new solidObject[128];
		playerStaticDefenceInMinimap = new solidObject[64];
		playerStructures = new solidObject[256];
		
		goldMines = mainThread.theAssetManager.goldMines;
		playerExpensionInfo = new int[goldMines.length];
		
		mainPlayerForceLocation = new vector(0,0,0);
		mainPlayerForceDirection = new vector(0,0,0);
		mainPlayerForceSize = 0;
		playerForceLocations = new vector[3];
		playerForceDirections = new vector[3];
		playerForceSize = new int[3];
		
		for(int i = 0; i < 3; i++) {
			playerForceLocations[i] = new vector(0,0,0);
			playerForceDirections[i] = new vector(0,0,0);
			playerForceSize[i] = 0;
		}
		
	}
	
	public void processAI(){
		theAssetManager = mainThread.theAssetManager;				
		
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
		
		//clear enemy info from previous frame
		for(int i = 0; i < playerUnitInMinimap.length; i++)
			playerUnitInMinimap[i] = null;
		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++)
			playerStaticDefenceInMinimap[i] = null;
		for(int i = 0; i < playerStructures.length; i++)
			playerStructures[i] = null;
		
		
		for(int i = 0; i < theAssetManager.lightTanks.length; i++){
			if(theAssetManager.lightTanks[i] != null && theAssetManager.lightTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.lightTanks[i].occupiedTile0]){
					numberOfLightTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.lightTanks[i]);
					if(mapAsset[theAssetManager.lightTanks[i].ID] == null) {
						mapAsset[theAssetManager.lightTanks[i].ID] = theAssetManager.lightTanks[i];
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=1;
					}
				}
			}else if(theAssetManager.lightTanks[i] != null && theAssetManager.lightTanks[i].teamNo !=0){
				numberOfLightTanks_AI++;
				if(mapAsset[theAssetManager.lightTanks[i].ID] == null){
					mainThread.ec.theUnitProductionAI.addLightTank(theAssetManager.lightTanks[i]);
					mapAsset[theAssetManager.lightTanks[i].ID] = theAssetManager.lightTanks[i];
				}
			}
		}
		
		for(int i = 0; i < theAssetManager.rocketTanks.length; i++){
			if(theAssetManager.rocketTanks[i] != null && theAssetManager.rocketTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.rocketTanks[i].occupiedTile0]){
					numberOfRocketTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.rocketTanks[i]);
					if(mapAsset[theAssetManager.rocketTanks[i].ID] == null) {
						mapAsset[theAssetManager.rocketTanks[i].ID] = theAssetManager.rocketTanks[i];
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=1.5;
					}
				}
			}else if(theAssetManager.rocketTanks[i] != null && theAssetManager.rocketTanks[i].teamNo !=0){
				numberOfRocketTanks_AI++;
				if(mapAsset[theAssetManager.rocketTanks[i].ID] == null){
					mainThread.ec.theUnitProductionAI.addRocketTank(theAssetManager.rocketTanks[i]);
					mapAsset[theAssetManager.rocketTanks[i].ID] = theAssetManager.rocketTanks[i];
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
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=2;
					}
				}
			}else if(theAssetManager.stealthTanks[i] != null && theAssetManager.stealthTanks[i].teamNo !=0){
				numberOfStealthTanks_AI++;
				if(mapAsset[theAssetManager.stealthTanks[i].ID] == null){
					mainThread.ec.theUnitProductionAI.addStealthTank(theAssetManager.stealthTanks[i]);
					mapAsset[theAssetManager.stealthTanks[i].ID] = theAssetManager.stealthTanks[i];
				}
			}
			
		}
		
		for(int i = 0; i < theAssetManager.heavyTanks.length; i++){
			if(theAssetManager.heavyTanks[i] != null && theAssetManager.heavyTanks[i].teamNo ==0){
				if(visionMap[theAssetManager.heavyTanks[i].occupiedTile0]){
					numberOfHeavyTanksOnMinimap_player++;
					addPlayerUnitInMinimap(theAssetManager.heavyTanks[i]);
					if(mapAsset[theAssetManager.heavyTanks[i].ID] == null) {
						mapAsset[theAssetManager.heavyTanks[i].ID] = theAssetManager.heavyTanks[i];
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=3.5;
					}
				}
			}else if(theAssetManager.heavyTanks[i] != null && theAssetManager.heavyTanks[i].teamNo !=0){
				numberOfHeavyTanks_AI++;
				if(mapAsset[theAssetManager.heavyTanks[i].ID] == null){
					mainThread.ec.theUnitProductionAI.addHeavyTank(theAssetManager.heavyTanks[i]);
					mapAsset[theAssetManager.heavyTanks[i].ID] = theAssetManager.heavyTanks[i];
				}
			}
		}
		
		for(int i = 0; i < theAssetManager.harvesters.length; i++){
			if(theAssetManager.harvesters[i] != null && theAssetManager.harvesters[i].teamNo ==0){
				if(visionMap[theAssetManager.harvesters[i].occupiedTile0]){
					addPlayerUnitInMinimap(theAssetManager.harvesters[i]);
					mapAsset[theAssetManager.harvesters[i].ID] = theAssetManager.harvesters[i];
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
		for(int i = 0; i < theAssetManager.gunTurrets.length; i++){
			if(theAssetManager.gunTurrets[i] != null && theAssetManager.gunTurrets[i].teamNo ==0){
				if(visionMap[theAssetManager.gunTurrets[i].tileIndex[0]]){
					if(mapAsset[theAssetManager.gunTurrets[i].ID] == null) {
						mapAsset[theAssetManager.gunTurrets[i].ID] = theAssetManager.gunTurrets[i];
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=1;
					}
				}
			}
		}
		
		for(int i = 0; i < theAssetManager.missileTurrets.length; i++){
			if(theAssetManager.missileTurrets[i] != null && theAssetManager.missileTurrets[i].teamNo ==0){
				if(visionMap[theAssetManager.missileTurrets[i].tileIndex[0]]){
					if(mapAsset[theAssetManager.missileTurrets[i].ID] == null) {
						mapAsset[theAssetManager.missileTurrets[i].ID] = theAssetManager.missileTurrets[i];
						mainThread.ec.theCombatManagerAI.offScreenPlayerForceStrength-=2;
					}
				}
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
			}
		}
		
		for(int i = 0; i < theAssetManager.techCenters.length; i++){
			if(theAssetManager.techCenters[i] != null && theAssetManager.techCenters[i].teamNo ==0){
				for(int j = 0; j < 4; j++){
					if(visionMap[theAssetManager.techCenters[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.techCenters[i].ID] == null)
							mapAsset[theAssetManager.techCenters[i].ID] = theAssetManager.techCenters[i];
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < theAssetManager.powerPlants.length; i++){
			if(theAssetManager.powerPlants[i] != null && theAssetManager.powerPlants[i].teamNo ==0){
				for(int j = 0; j < 4; j++){
					if(visionMap[theAssetManager.powerPlants[i].tileIndex[j]]){
						if(mapAsset[theAssetManager.powerPlants[i].ID] == null)
							mapAsset[theAssetManager.powerPlants[i].ID] = theAssetManager.powerPlants[i];
						break;
					}
				}
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
					if(mapAsset[i].type < 100)
						numberOfPlayerUnitDestroyed++;
					else
						numberOfPlayerBuildingDestroyed++;
				}
			}
			
		}
		
		//analyze the enemy units composition 
		
		float lightTankRatio = (float)(numberOfLightTanks_player)/(numberOfLightTanks_player + numberOfRocketTanks_player + numberOfStealthTanks_player + numberOfHeavyTanks_player + 1);
		
		playerHasMostlyLightTanks = numberOfLightTanks_player > 5 &&  lightTankRatio > 0.8f;
		playerHasMostlyHeavyTanks = numberOfHeavyTanks_player > 3 && (float)(numberOfHeavyTanks_player)/(numberOfLightTanks_player + numberOfRocketTanks_player + numberOfStealthTanks_player + numberOfHeavyTanks_player) > 0.6f;
		playerHasManyLightTanksButNoHeavyTank = lightTankRatio > 0.5 && lightTankRatio <=0.8 && numberOfHeavyTanks_player < 3;

		playIsRushingHighTierUnits = mainThread.frameIndex/30 > 250 && mainThread.frameIndex/30 < 400 
			                             && mainThread.ec.theMapAwarenessAI.numberOfTechCenter_player >0 
			                             && mainThread.ec.theMapAwarenessAI.numberOfMissileTurret_player < 2 
			                             && mainThread.ec.theMapAwarenessAI.numberOfGunTurret_player < 4
			                             && numberOfLightTanks_player + numberOfRocketTanks_player + numberOfStealthTanks_player < 5;
	    	
        playerLikelyCanNotProduceHighTierUnits = mainThread.ec.theMapAwarenessAI.numberOfTechCenter_player == 0 && mainThread.ec.theMapAwarenessAI.numberOfHeavyTanks_player == 0; 
        playerDoesntHaveMassHeavyTanks =  (float)numberOfHeavyTanks_player/( 1 + numberOfLightTanks_AI + numberOfRocketTanks_player + numberOfStealthTanks_player)  < 0.2f;
        
        playerIsRushingLightTank = mainThread.frameIndex/30 > 300 && mainThread.frameIndex/30 < 600 && ((playerLikelyCanNotProduceHighTierUnits && numberOfStealthTanks_player < 3) || playerHasMostlyLightTanks);
        
        
        //advanced counting of player units
        if(numberOfStealthTanks_player > maxNumberOfStealthTanks_playerInLastFiveMinutes) {
        	maxNumberOfStealthTanks_playerInLastFiveMinutes = numberOfStealthTanks_player;
        	fiveMinuteTimer = 300;
        }
        if(fiveMinuteTimer > 0)
        	fiveMinuteTimer--;
        else
        	maxNumberOfStealthTanks_playerInLastFiveMinutes = 0;
        
        
        findTheMostVulnerablePlayerBase();
        
        findPlayerForceLocation();
       
	}
	
	
	public void addPlayerUnitInMinimap(solidObject o){
		for(int i = 0; i < playerUnitInMinimap.length; i++){
			if(playerUnitInMinimap[i] == null){
				playerUnitInMinimap[i] = o;
				numberOfPlayerUnitsOnMinimap++;
				break; 
			}
		}
	}
	
	public void addPlayerStaticDefenceInMinimap(solidObject o){
		for(int i = 0; i < playerStaticDefenceInMinimap.length; i++){
			if(playerStaticDefenceInMinimap[i] == null){
				playerStaticDefenceInMinimap[i] = o;
				break;
			}
		}
	}
	
	public void addPlayerStructure(solidObject o){
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
			playerExpensionInfo[i] = findplayexpensionDefenseScore(goldMines[i], 3);
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
	
	public int findplayexpensionDefenseScore(goldMine g, float r){
		if(g == null)
			return 0;
		
		solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
		solidObject[] playerStaticDefence = mainThread.ec.theMapAwarenessAI.playerStaticDefenceInMinimap;
		
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
		if(g == mainThread.ec.theBaseExpentionAI.expensionGoldMine && playexpensionDefenseScore > 0){
			return -1;
		}
		
		return playexpensionDefenseScore;
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
		
		
		if(numberOfPlayerUnitsOnMinimap < 5)
			return;	
		
		
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
		
		//System.out.println(mainPlayerForceSize  + "   "  + mainPlayerForceLocation + "    "  + mainPlayerForceDirection);
	}

	
}
