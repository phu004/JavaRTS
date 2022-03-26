//1. process building enquires from enemy commander
//2. manage building placement
//3  units production
//4. perform research

package enemyAI;

import core.BaseInfo;
import core.GameData;
import core.MainThread;
import core.vector;
import entity.*;

public class BuildingManagerAI {
	
	public BaseInfo theBaseInfo;
	public int[] buildingPlacementCheckTiles, buildingPlacementCheckTiles_2x2, buildingPlacementCheckTiles_3x3;
	public int placementTile;
	public boolean powerPlantUnderConstruction;
	public int frameAI;
	public vector tempVector;

	
	public BuildingManagerAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
		
		buildingPlacementCheckTiles = SolidObject.generateTileCheckList(13);
		buildingPlacementCheckTiles_2x2 = new int[buildingPlacementCheckTiles.length];
		buildingPlacementCheckTiles_3x3 = new int[buildingPlacementCheckTiles.length];
	
		for(int i = 0; i < buildingPlacementCheckTiles.length ; i++){
			buildingPlacementCheckTiles_2x2[i] = buildingPlacementCheckTiles[i];
			buildingPlacementCheckTiles_3x3[i] = buildingPlacementCheckTiles[i];
			
		}
		
		
		for(int i = 0; i < 400; i++){
			int temp = (GameData.getRandom() * 70) >> 10;
			int temp1 = (GameData.getRandom() * 70) >> 10;
				
			int list = buildingPlacementCheckTiles_2x2[temp];
			buildingPlacementCheckTiles_2x2[temp] = buildingPlacementCheckTiles_2x2[temp1];
			buildingPlacementCheckTiles_2x2[temp1] = list;
		}
		
		for(int i = 0; i < 400; i++){
			int temp = (GameData.getRandom() * 50 + 40*1024) >> 10;
			int temp1 = (GameData.getRandom() * 50 + 40*1024) >> 10;
				
			int list = buildingPlacementCheckTiles_3x3[temp];
			buildingPlacementCheckTiles_3x3[temp] = buildingPlacementCheckTiles_3x3[temp1];
			buildingPlacementCheckTiles_3x3[temp1] = list;
		}	
		
		tempVector = new vector(0,0,0);
	}
	
	public void addBuildingToQueue(int buildingType){
		//if the additional building will result in a lower power, then build power plant first
		if(buildingType != 101){
			if(theBaseInfo.currentPowerLevel <= getPowerConsumption(buildingType) + theBaseInfo.currentPowerConsumption && !powerPlantUnderConstruction){
				addBuildingToQueue(101);
				return;
			}
		}
		
		//check if the building is already in the queue
		boolean alreadyInQueue = false;
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		
		//can only build one none defense structure at a time
		//but can build more than  defense structure at a time if there are more construction yards
		
		
		for(int i = 0; i < constructionYards.length; i ++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
				if(constructionYards[i].currentBuildingType == buildingType){
					if(buildingType < 150){
						alreadyInQueue = true;
						break;
					}
				}
			}
		}
	
		
		//if not in the queue then 
		if(!alreadyInQueue){
			boolean hasIdleConYard = false;
			for(int i = 0; i < constructionYards.length; i ++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].isIdle()){
					constructionYards[i].build(buildingType);
					hasIdleConYard = true;
					break;
				}
			}
			
			//Prioritize Factory construction over static defense if there is no idle con yard
			if(!hasIdleConYard && buildingType == 105) {
				for(int i = 0; i < constructionYards.length; i ++){
					if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentBuildingType == 200){
						constructionYards[i].cancelBuilding();
						break;
					}
				}
				
				for(int i = 0; i < constructionYards.length; i ++){
					if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].isIdle()){
						constructionYards[i].build(105);
						break;
					}
				}
			}
		}
		
	}
	
	public void processAI(){
		frameAI = MainThread.enemyCommander.frameAI;
		
		
		powerPlantUnderConstruction = buildingUnderProduction(101);
		
		//build power plant, if the base is in lower power  status or we have more money to spend on make extra one
		if(theBaseInfo.canBuildRefinery == false || theBaseInfo.lowPower || (theBaseInfo.currentPowerConsumption >= (theBaseInfo.currentPowerLevel - 500) && theBaseInfo.currentCredit > 500 && theBaseInfo.numberOfPowerPlant >=2 && frameAI > 300)){
			addBuildingToQueue(101);
		}
			
		//build a Refinery  center if there isn't any
		if(theBaseInfo.numberOfRefinery == 0 && theBaseInfo.canBuildRefinery){
			addBuildingToQueue(102);
		}
		
		
		//build an additional Refinery if there are more production building
		//don't build more than 2 Refinery around a goldmine
		if(getNumberOfFunctionalRefinery() < theBaseInfo.numberOfConstructionYard*2 && (getNumberOfFunctionalRefinery() == 0 || theBaseInfo.numberOfFactory > 0) && theBaseInfo.canBuildRefinery && getNumberOfRefineriesNearPreferedGoldMine() < 2){
			addBuildingToQueue(102);
		}
		
		//build a Factory  if there isnt any
		if(theBaseInfo.numberOfFactory == 0 && theBaseInfo.canBuildFactory){
			addBuildingToQueue(105);
			
		}
		
		//build a gun turret if there is a need for it
		if(theBaseInfo.canBuildGunTurret && MainThread.enemyCommander.theDefenseManagerAI.needGunTurret) {
			addBuildingToQueue(200);
		}
		
		//build an addtional Factory if we have enough Harvester to sustain the production
		if(MainThread.enemyCommander.theEconomyManagerAI.numberOfharvesters/2 > theBaseInfo.numberOfFactory && theBaseInfo.canBuildFactory && theBaseInfo.numberOfFactory < 2 && theBaseInfo.currentCredit > 1300){
			addBuildingToQueue(105);
		}
		
		//build a communication center if there isnt any
		if(theBaseInfo.numberOfCommunicationCenter == 0 &&  theBaseInfo.canBuildCommunicationCenter){
			addBuildingToQueue(106);
		}
		
		//build a tech center if there isnt any
		if(theBaseInfo.numberOfTechCenter == 0 && theBaseInfo.canBuildTechCenter){
			addBuildingToQueue(107);
		}
		
	
		//build missile turret if there is a need for it
		if(theBaseInfo.canBuildMissileTurret && MainThread.enemyCommander.theDefenseManagerAI.needMissileTurret) {
			addBuildingToQueue(199);
		}
		
		
		//build more Factory if we have plenty of money in the bank
		if(theBaseInfo.currentCredit > 2200 && MainThread.enemyCommander.difficulty > 0 && theBaseInfo.canBuildFactory && theBaseInfo.numberOfFactory < 5 && theBaseInfo.numberOfFactory <= MainThread.enemyCommander.theEconomyManagerAI.numberOfharvesters/2){
			addBuildingToQueue(105);
		}
		
	
	
		//process structure building event
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
				//deploy power plant
				if(constructionYards[i].powerPlantProgress == 240){
					if(hasRoomForPlacement(101, -1)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						PowerPlant o = new PowerPlant(x*0.25f, -1f, y*0.25f, 1);
						MainThread.theAssetManager.addPowerPlant(o);
						constructionYards[i].finishDeployment();	
					}
				}
				
				//deploy Refinery
				if(constructionYards[i].refineryProgress == 240){
					
					
					if(hasRoomForPlacement(102, MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMineLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						Refinery o = new Refinery(x*0.25f + 0.125f, -1.43f, y*0.25f, 1);
						MainThread.theAssetManager.addRefinery(o);
						
						Harvester h = new Harvester(new vector(x*0.25f + 0.125f,-0.3f, y*0.25f - 0.375f), 180, 1);
						MainThread.theAssetManager.addHarvester(h);
						h.goToTheNearestGoldMine();  
						constructionYards[i].finishDeployment();
					}
				}else if(constructionYards[i].refineryProgress < 240 && frameAI > 300) {
					//if there is not enough money to finish building the Refinery, reset all other production.
				

					// first reset construction yard production
					boolean hasEnoughCredit = theBaseInfo.currentCredit > 1200 -constructionYards[i].creditSpentOnBuilding;
					if(!hasEnoughCredit) {
						for(int j = 0; j < constructionYards.length; j++) {
							if(constructionYards[j] != null && constructionYards[j] != constructionYards[i] && constructionYards[j].teamNo != 0) {
								int currentBuildingType = constructionYards[j].currentBuildingType;
								if(currentBuildingType != -1) {
									constructionYards[j].cancelBuilding();
									constructionYards[j].build(currentBuildingType);
								}
								hasEnoughCredit = theBaseInfo.currentCredit > 1200 -constructionYards[i].creditSpentOnBuilding;
								if(hasEnoughCredit)
									break;
							}
						}
					}
				
					//then reset Factory production if still dont have enough credit to finish Refinery
					hasEnoughCredit = theBaseInfo.currentCredit > 1200 -constructionYards[i].creditSpentOnBuilding;
					if(!hasEnoughCredit) {
						Factory[] factories = MainThread.theAssetManager.factories;
						for(int j = 0; j < factories.length; j++) {
							if(factories[j] != null && factories[j].teamNo != 0) {
								factories[j].cancelBuilding();
							}
							hasEnoughCredit = theBaseInfo.currentCredit > 1200 -constructionYards[i].creditSpentOnBuilding;
							if(hasEnoughCredit)
								break;
						}
					}
					
				}
				
				
				//deploy Factory
				if(constructionYards[i].factoryProgress == 240){
				
					int factoryDeployLocation = findFactoryDeployLocation();
					if(hasRoomForPlacement(105, factoryDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						Factory o = new Factory(x*0.25f + 0.125f, -1.13f, y*0.25f, 1);
						MainThread.theAssetManager.addFactory(o);
							
						constructionYards[i].finishDeployment();				
						
					}
				}
				
				//deploy communication center
				if(constructionYards[i].communicationCenterProgress == 240){
					if(hasRoomForPlacement(106, CommunicationCenter.intendedDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						CommunicationCenter o = new CommunicationCenter(x*0.25f, -1f, y*0.25f, 1);
						MainThread.theAssetManager.addCommunicationCenter(o);
							
						constructionYards[i].finishDeployment();				
						CommunicationCenter.intendedDeployLocation = -1;
					}
				}
				
				//deploy tech center
				if(constructionYards[i].techCenterProgress == 240){
					if(hasRoomForPlacement(107, TechCenter.intendedDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						TechCenter o = new TechCenter(x*0.25f, -1f, y*0.25f, 1);
						MainThread.theAssetManager.addTechCenter(o);
							
						constructionYards[i].finishDeployment();				
						TechCenter.intendedDeployLocation = -1;
					}
				}
				
				//deploy gun turret
				if(constructionYards[i].gunTurretProgress == 240) {
					float xPos = MainThread.enemyCommander.theDefenseManagerAI.gunTurretDeployLocation.x;
					float zPos = MainThread.enemyCommander.theDefenseManagerAI.gunTurretDeployLocation.z;
					int centerTile = (int)(xPos*64)/16 + (127 - (int)(zPos*64)/16)*128;
					if(xPos != 0) {
						if(hasRoomForPlacement(200, centerTile)) {
							int y = 127 - placementTile/128;
							int x = placementTile%128;
							GunTurrent o = new GunTurrent(x*0.25f + 0.125f, -0.65f, y*0.25f + 0.125f, 1);
							MainThread.theAssetManager.addGunTurret(o);
							
							constructionYards[i].finishDeployment();			
						}
					}
				}
				
				//deploy missile turret
				if(constructionYards[i].missileTurretProgress == 240) {
					float xPos = MainThread.enemyCommander.theDefenseManagerAI.missileTurretDeployLocation.x;
					float zPos = MainThread.enemyCommander.theDefenseManagerAI.missileTurretDeployLocation.z;
					int centerTile = (int)(xPos*64)/16 + (127 - (int)(zPos*64)/16)*128;
					if(xPos != 0) {
						if(hasRoomForPlacement(199, centerTile)) {
							int y = 127 - placementTile/128;
							int x = placementTile%128;
							MissileTurret o = new MissileTurret(x*0.25f + 0.125f, -0.65f, y*0.25f + 0.125f, 1);
							MainThread.theAssetManager.addMissileTurret(o);
							constructionYards[i].finishDeployment();			
						}
					}
					
				}
			}
		}
		
	}
	
	public boolean hasRoomForPlacement(int buildingType, int centerTile){
		//check placement for turrets
		if(buildingType == 199 || buildingType == 200) {
			placementTile = -1;
			
			if(checkIfBlockIsFree(centerTile)) {
				placementTile = centerTile;
			}else if(checkIfBlockIsFree(centerTile + 1)) {
				placementTile = centerTile + 1;
			}else if(checkIfBlockIsFree(centerTile - 1)) {
				placementTile = centerTile - 1;
			}else if(checkIfBlockIsFree(centerTile + 128)) {
				placementTile = centerTile + 128;
			}else if(checkIfBlockIsFree(centerTile - 128)) {
				placementTile = centerTile - 128;
			}else if(checkIfBlockIsFree(centerTile - 129)) {
				placementTile = centerTile - 129;
			}else if(checkIfBlockIsFree(centerTile - 127)) {
				placementTile = centerTile - 127;
			}else if(checkIfBlockIsFree(centerTile + 127)) {
				placementTile = centerTile + 127;
			}else if(checkIfBlockIsFree(centerTile + 129)) {
				placementTile = centerTile + 129;
			}else if(checkIfBlockIsFree(centerTile + 2)) {
				placementTile = centerTile + 2;
			}else if(checkIfBlockIsFree(centerTile - 2)) {
				placementTile = centerTile - 2;
			}else if(checkIfBlockIsFree(centerTile + 256)) {
				placementTile = centerTile + 256;
			}else if(checkIfBlockIsFree(centerTile - 256)) {
				placementTile = centerTile - 256;
			}
			
			
			if(placementTile == -1)
				return false;
			
			if(buildingType == 200) {
				return true;
			}
			
			
			
			//place missile turret behind buildings to take advantage of its long range and shoot over building ability
			float x = MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.x;
			float z = MainThread.enemyCommander.theDefenseManagerAI.majorThreatLocation.z;
			
			if(x == 0 && z == 0 || !MainThread.enemyCommander.theMapAwarenessAI.playerForceNearBase) {
				return true;
			}
			
		
			int perfectPlacementTile = -1;
			
			if(checkIfBlockIsFree(centerTile) && !hasLineOfSight(centerTile, x, z)) {
				perfectPlacementTile = centerTile;
			}else if(checkIfBlockIsFree(centerTile + 1) && !hasLineOfSight(centerTile + 1, x, z)) {
				perfectPlacementTile = centerTile + 1;
			}else if(checkIfBlockIsFree(centerTile - 1) && !hasLineOfSight(centerTile -1, x, z)) {
				perfectPlacementTile = centerTile - 1;
			}else if(checkIfBlockIsFree(centerTile + 128) && !hasLineOfSight(centerTile + 128, x, z)) {
				perfectPlacementTile = centerTile + 128;
			}else if(checkIfBlockIsFree(centerTile - 128) && !hasLineOfSight(centerTile - 128, x, z)) {
				perfectPlacementTile = centerTile - 128;
			}else if(checkIfBlockIsFree(centerTile - 129) && !hasLineOfSight(centerTile - 129, x, z)) {
				perfectPlacementTile = centerTile - 129;
			}else if(checkIfBlockIsFree(centerTile - 127) && !hasLineOfSight(centerTile-127, x, z)) {
				perfectPlacementTile = centerTile - 127;
			}else if(checkIfBlockIsFree(centerTile + 127) && !hasLineOfSight(centerTile+127, x, z)) {
				perfectPlacementTile = centerTile + 127;
			}else if(checkIfBlockIsFree(centerTile + 129) && !hasLineOfSight(centerTile + 129, x, z)) {
				perfectPlacementTile = centerTile + 129;
			}else if(checkIfBlockIsFree(centerTile + 2) && !hasLineOfSight(centerTile + 2, x, z)) {
				perfectPlacementTile = centerTile + 2;
			}else if(checkIfBlockIsFree(centerTile - 2) && !hasLineOfSight(centerTile - 2, x, z)) {
				perfectPlacementTile = centerTile - 2;
			}else if(checkIfBlockIsFree(centerTile + 256)  && !hasLineOfSight(centerTile+ 256, x, z)) {
				perfectPlacementTile = centerTile + 256;
			}else if(checkIfBlockIsFree(centerTile - 256)  && !hasLineOfSight(centerTile - 256, x, z)) {
				perfectPlacementTile = centerTile - 256;
			}
			
			if(perfectPlacementTile != -1) {
				placementTile = perfectPlacementTile;
			}
			
			
			return true;
			
		}
		
		
		//check placement for power plant
		if(buildingType == 101){
			ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] == null)
					continue;
				
				if(constructionYards[i].teamNo == 0)
					continue;
				
				centerTile = (int)(constructionYards[i].centre.x*64)/16 + (127 - (int)(constructionYards[i].centre.z*64)/16)*128;
				for(int j = 0; j < buildingPlacementCheckTiles.length; j++){
					if(buildingPlacementCheckTiles_2x2[j] != Integer.MAX_VALUE){
						
						placementTile = centerTile + buildingPlacementCheckTiles_2x2[j];
						 
						if(!checkIfBlockIsFree(placementTile)){ continue;}
						if(!checkIfBlockIsFree(placementTile+1)){ continue;}
						if(!checkIfBlockIsFree(placementTile + 128)){ continue;}
						if(!checkIfBlockIsFree(placementTile + 129)){ continue;}
						if(!checkIfBlockIsFree(placementTile - 129)){ continue;}
						if(!checkIfBlockIsFree(placementTile + 257)){ continue;}
						if(!checkIfBlockIsFree(placementTile - 127)){ continue;}
						if(!checkIfBlockIsFree(placementTile + 255)){ continue;}
						if(!checkIfBlockIsFree(placementTile + 127)){ continue;}
						
						return true;
					}
				}
				
			}
		}
		
		//check placement for Refinery
		if(buildingType == 102){
			
			boolean foundSuitableTile = false;
			int distance = 99999;
			int idealPosition = 0;
			
			for(int j = 40; j < buildingPlacementCheckTiles.length; j++){
				if(buildingPlacementCheckTiles_3x3[j] != Integer.MAX_VALUE){
					placementTile = centerTile + buildingPlacementCheckTiles_3x3[j];
				
					if(!checkIfBlockIsFree(placementTile)){ continue;}
					if(!checkIfBlockIsFree(placementTile+1)){ continue;}
					if(!checkIfBlockIsFree(placementTile+2)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 128)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 129)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 130)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 256)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 257)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 258)){ continue;}
					
					foundSuitableTile = true;
					if(Math.abs(placementTile/128 - centerTile/128) + Math.abs(placementTile%128 - centerTile%128) < distance) {
						idealPosition = placementTile;
						distance = Math.abs(placementTile/128 - centerTile/128) + Math.abs(placementTile%128 - centerTile%128);
					}
					
					if(placementTile/128 + 4< centerTile/128)
						return true;
				}
			}
			
			if(foundSuitableTile) {
				placementTile = idealPosition;
				return true;
			}
		}
		
		//check placement for Factory
		if(buildingType == 105){
			for(int j = 40; j < buildingPlacementCheckTiles.length; j++){
				if(buildingPlacementCheckTiles_3x3[j] != Integer.MAX_VALUE){
					
					placementTile = centerTile + buildingPlacementCheckTiles_3x3[j];
					 
					if(!checkIfBlockIsFree(placementTile - 128)){ continue;}
					if(!checkIfBlockIsFree(placementTile - 127)){ continue;}
					if(!checkIfBlockIsFree(placementTile - 126)){ continue;}
					if(!checkIfBlockIsFree(placementTile)){ continue;}
					if(!checkIfBlockIsFree(placementTile+1)){ continue;}
					if(!checkIfBlockIsFree(placementTile+2)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 128)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 129)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 130)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 256)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 257)){ continue;}
					if(!checkIfBlockIsFree(placementTile + 258)){ continue;}
					
					
					return true;
				}
			}
		}
		
		//check placement for communication center 
		if(buildingType == 106){
			if(centerTile == -1){
				ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
				for(int i = 0; i < constructionYards.length; i++){
					if(constructionYards[i] == null)
						continue;
					
					if(constructionYards[i].teamNo == 0)
						continue;
					
					centerTile = (int)(constructionYards[i].centre.x*64)/16 + (127 - (int)(constructionYards[i].centre.z*64)/16)*128;
					for(int j = 0; j < buildingPlacementCheckTiles.length; j++){
						if(buildingPlacementCheckTiles_2x2[j] != Integer.MAX_VALUE){
							
							placementTile = centerTile + buildingPlacementCheckTiles_2x2[j];
							
							int x  = placementTile%128;
							int y =  placementTile/128;
							
							boolean tooCloseToOtherCommunicationCenter = false;
							for(int k = 0; k < MainThread.theAssetManager.communicationCenters.length; k++){
								if(MainThread.theAssetManager.communicationCenters[k] != null && MainThread.theAssetManager.communicationCenters[k].teamNo != 0){
									int x_ = MainThread.theAssetManager.communicationCenters[k].tileIndex[0]%128;
									int y_ = MainThread.theAssetManager.communicationCenters[k].tileIndex[0]/128;
									
									if(Math.abs(x - x_)  + Math.abs(y - y_) <= 14){
										tooCloseToOtherCommunicationCenter = true;
										break;
									}
									
								}
							}
							if(tooCloseToOtherCommunicationCenter){
								continue;
							}
							 
							if(!checkIfBlockIsFree(placementTile)){ continue;}
							if(!checkIfBlockIsFree(placementTile+1)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 128)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 129)){ continue;}
							if(!checkIfBlockIsFree(placementTile - 129)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 257)){ continue;}
							if(!checkIfBlockIsFree(placementTile - 127)){ continue;}
							if(!checkIfBlockIsFree(placementTile +255)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 127)){ continue;}
							
							
							return true;
						}
					}
				}
			}
		}
		
		//check placement for tech center 
		if(buildingType == 107){
			if(centerTile == -1){
				ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
				for(int i = 0; i < constructionYards.length; i++){
					if(constructionYards[i] == null)
						continue;
					
					if(constructionYards[i].teamNo == 0)
						continue;
					
					centerTile = (int)(constructionYards[i].centre.x*64)/16 + (127 - (int)(constructionYards[i].centre.z*64)/16)*128;
					for(int j = 0; j < buildingPlacementCheckTiles.length; j++){
						if(buildingPlacementCheckTiles_2x2[j] != Integer.MAX_VALUE){
							
							placementTile = centerTile + buildingPlacementCheckTiles_2x2[j];
							 
							if(!checkIfBlockIsFree(placementTile)){ continue;}
							if(!checkIfBlockIsFree(placementTile+1)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 128)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 129)){ continue;}
							if(!checkIfBlockIsFree(placementTile - 129)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 257)){ continue;}
							if(!checkIfBlockIsFree(placementTile - 127)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 255)){ continue;}
							if(!checkIfBlockIsFree(placementTile + 127)){ continue;}
							
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean checkIfBlockIsFree(int index){
		int y = index/128;
		int x = index%128;
		
		if(y > 0 && y < 127 && x > 0 && x < 127){
			SolidObject[] tile = MainThread.gridMap.tiles[index];
			for(int j = 0; j < 5; j++){
				if(tile[j] != null){
					
					return false;
				}
			}
			
			if(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine != null){
				int location = MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine.tileIndex[1];
			
				if( index == location - 128 || index == location - 129 || index == location - 130 || index == location - 2 || 
				    index == location + 126 || index == location + 254 || index == location + 255 || index == location + 256 ||
				    index == location + 257 || index == location + 129 || index == location + 1 || index == location-127){
					return false;
				}
			}
			
			ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
					float xPos = x * 0.25f + 0.125f;
					float yPos = (127 - y)*0.25f + 0.125f;
					
					float distance = (float) Math.sqrt((constructionYards[i].centre.x - xPos)*(constructionYards[i].centre.x - xPos) + (constructionYards[i].centre.z - yPos)*(constructionYards[i].centre.z - yPos));
					if(distance <= 2.75){
						return true;
					}
				}
			}
			
			return false;
			
		}else{
			return false;
		}
	}
	
	public boolean buildingUnderProduction(int buildingType){
		boolean alreadyInQueue = false;
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		for(int i = 0; i < constructionYards.length; i ++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
				if(constructionYards[i].currentBuildingType == buildingType){
					alreadyInQueue = true;
					break;
				}
			}
		}
		return alreadyInQueue;
	}
	
	

	public int findFactoryDeployLocation(){
		ConstructionYard[] constructionYards = MainThread.theAssetManager.constructionYards;
		Factory[] factories = MainThread.theAssetManager.factories;
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] == null || constructionYards[i].teamNo == 0)
				continue;
			
			
			
			int numberOfFactories = 0;
			for(int j= 0; j < factories.length; j++){
				if(factories[j] == null || factories[j].teamNo == 0)
					continue;
				
				if(constructionYards[i].getDistance(factories[j]) < 3)
					numberOfFactories++;
			}
			
			if(numberOfFactories >= 2 && (Math.abs((constructionYards[i].centre.x /constructionYards[i].centre.z)) > 4 || Math.abs((constructionYards[i].centre.x /constructionYards[i].centre.z)) < 0.25))
				continue;
			
			if(numberOfFactories < 3){
				return (int)(constructionYards[i].centre.x*64)/16 + (127 - (int)(constructionYards[i].centre.z*64)/16)*128;
			}		
		}
		
		return -1;
	}
	
	public int getNumberOfRefineriesNearPreferedGoldMine(){
		int numberOfRefineriesNearPreferedGoldMine = 0;
		Refinery[] refineries = MainThread.theAssetManager.refineries;
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] != null && refineries[i].teamNo != 0){
				if(MainThread.enemyCommander.theEconomyManagerAI.preferedGoldMine.getDistance(refineries[i]) < 2.5){
					numberOfRefineriesNearPreferedGoldMine++;
				}
			}
		}
		
		return numberOfRefineriesNearPreferedGoldMine;
	}
	
	public int getNumberOfFunctionalRefinery(){
		int numberOfFunctionalRefinery = 0;
		
		Refinery[] refineries = MainThread.theAssetManager.refineries;
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] != null && refineries[i].teamNo != 0 && refineries[i].nearestGoldMine != null &&  refineries[i].nearestGoldMine.goldDeposite > 5000){
				numberOfFunctionalRefinery++;
			}
		}
		return numberOfFunctionalRefinery;
		
	}
	
	public int getPowerConsumption(int buildingType){
		if(buildingType == 101)
			return -500;
		else if(buildingType == 102)
			return 150;
		else if(buildingType == 105)
			return 200;
		else if(buildingType == 106)
			return 250;
		else if(buildingType == 200)
			return 100;
		else if(buildingType == 199) {
			if(CommunicationCenter.rapidfireResearched_enemy)
				return 300;
			else
				return 250;
		}else if(buildingType == 107)
			return 400;
		return 0;
	}
	
	public boolean hasLineOfSight(int tileIndex, float x1, float z1){
		float z2 = 0.25f*(127 - tileIndex/128);
		float x2 = 0.25f*(tileIndex%128);
		
		boolean hasLineOfSight = true;
		
		float dx = (x1 - x2);
		float dy = (z1 - z2);
		
		tempVector.set(dx,0,dy);
		tempVector.unit();
		tempVector.scale(0.2f);
		
		float xStart = x2;
		float yStart = z2;
		
		for(int i = 0; i < 4; i++){
			xStart+=tempVector.x;
			yStart+=tempVector.z;
			SolidObject s = MainThread.gridMap.tiles[(int)(xStart*4) + (127 - (int)(yStart*4))*128][0];
			if(s != null){
				if(s.type > 100 && s.type < 200){
					hasLineOfSight = false;
					break;
				}
			}
		}
		
		return hasLineOfSight;
	}
	
	
}
