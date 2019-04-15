//1. process building enquires from enemy commander
//2. manage building placement
//3  units production
//4. perform research

package enemyAI;

import core.baseInfo;
import core.gameData;
import core.mainThread;
import core.vector;
import entity.*;

public class buildingManagerAI {
	
	public baseInfo theBaseInfo;
	public int[] buildingPlacementCheckTiles, buildingPlacementCheckTiles_2x2, buildingPlacementCheckTiles_3x3;
	public int placementTile;
	public boolean powerPlantUnderConstruction;
	public int frameIndex;

	
	public buildingManagerAI (baseInfo theBaseInfo){
		this.theBaseInfo = theBaseInfo;
		
		buildingPlacementCheckTiles = solidObject.generateTileCheckList(13);
		buildingPlacementCheckTiles_2x2 = new int[buildingPlacementCheckTiles.length];
		buildingPlacementCheckTiles_3x3 = new int[buildingPlacementCheckTiles.length];
	
		for(int i = 0; i < buildingPlacementCheckTiles.length ; i++){
			buildingPlacementCheckTiles_2x2[i] = buildingPlacementCheckTiles[i];
			buildingPlacementCheckTiles_3x3[i] = buildingPlacementCheckTiles[i];
			
		}
		
		
		for(int i = 0; i < 400; i++){
			int temp = (gameData.getRandom() * 70) >> 10;
			int temp1 = (gameData.getRandom() * 70) >> 10;
				
			int list = buildingPlacementCheckTiles_2x2[temp];
			buildingPlacementCheckTiles_2x2[temp] = buildingPlacementCheckTiles_2x2[temp1];
			buildingPlacementCheckTiles_2x2[temp1] = list;
		}
		
		for(int i = 0; i < 400; i++){
			int temp = (gameData.getRandom() * 50 + 40*1024) >> 10;
			int temp1 = (gameData.getRandom() * 50 + 40*1024) >> 10;
				
			int list = buildingPlacementCheckTiles_3x3[temp];
			buildingPlacementCheckTiles_3x3[temp] = buildingPlacementCheckTiles_3x3[temp1];
			buildingPlacementCheckTiles_3x3[temp1] = list;
		}
		
		frameIndex = 0;
		
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
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
		
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
			for(int i = 0; i < constructionYards.length; i ++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].isIdle()){
					constructionYards[i].build(buildingType);
					break;
				}
			}
		}
		
	}
	
	public void processAI(){
		frameIndex++;
		
		
		powerPlantUnderConstruction = buildingUnderProduction(101);
		
		//build power plant, if the base is in lower power  status or we have more money to spend on make extra one
		if(theBaseInfo.canBuildRefinery == false || theBaseInfo.lowPower || (theBaseInfo.currentPowerConsumption >= (theBaseInfo.currentPowerLevel - 500) && theBaseInfo.currentCredit > 500 && theBaseInfo.numberOfPowerPlant >=2 && frameIndex > 300)){
			addBuildingToQueue(101);
		}
			
		//build a refinery  center if there isn't any
		if(theBaseInfo.numberOfRefinery == 0 && theBaseInfo.canBuildRefinery){
			addBuildingToQueue(102);
		}
		
		
		//build an additional refinery if there are more production building
		//don't build more than 2 refinery around a goldmine
		if(getNumberOfFunctionalRefinery() < theBaseInfo.numberOfConstructionYard*2 && (getNumberOfFunctionalRefinery() == 0 || theBaseInfo.numberOfFactory > 0) && theBaseInfo.canBuildRefinery && getNumberOfRefineriesNearPreferedGoldMine() < 2){
			addBuildingToQueue(102);
		}
		
		//build a factory  if there isnt any
		if(theBaseInfo.numberOfFactory == 0 && theBaseInfo.canBuildFactory){
			addBuildingToQueue(105);
			
		}
		
		//build a gun turret if there is a need for it
		if(theBaseInfo.canBuildGunTurret && mainThread.ec.theDefenseManagerAI.needGunTurret) {
			addBuildingToQueue(200);
		}
		
		//build an addtional factory if we have enough harvester to sustain the production
		if(mainThread.ec.theEconomyManagerAI.numberOfharvesters/2 > theBaseInfo.numberOfFactory && theBaseInfo.canBuildFactory && theBaseInfo.numberOfFactory < 2 && theBaseInfo.currentCredit > 1300){ 
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
		if(theBaseInfo.canBuildMissileTurret && mainThread.ec.theDefenseManagerAI.needMissileTurret) {
			addBuildingToQueue(199);
		}
		
		
		//build more factory if we have plenty of money in the bank 
		if(theBaseInfo.currentCredit > 2200 && theBaseInfo.canBuildFactory && theBaseInfo.numberOfFactory < 4 && theBaseInfo.numberOfFactory <= mainThread.ec.theEconomyManagerAI.numberOfharvesters/2){
			addBuildingToQueue(105);
		}
		
	
		//process structure building event
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0){
				//deploy power plant
				if(constructionYards[i].powerPlantProgress == 240){
					if(hasRoomForPlacement(101, -1)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						powerPlant o = new powerPlant(x*0.25f, -1f, y*0.25f, 1);
						mainThread.theAssetManager.addPowerPlant(o);
						constructionYards[i].finishDeployment();	
					}
				}
				
				//deploy refinery
				if(constructionYards[i].refineryProgress == 240){
					
					
					if(hasRoomForPlacement(102, mainThread.ec.theEconomyManagerAI.preferedGoldMineLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						refinery o = new refinery(x*0.25f + 0.125f, -1.43f, y*0.25f, 1);
						mainThread.theAssetManager.addRefinery(o);
						
						harvester h = new harvester(new vector(x*0.25f + 0.125f,-0.3f, y*0.25f - 0.375f), 180, 1);
						mainThread.theAssetManager.addHarvester(h);
						h.goToTheNearestGoldMine();  
						constructionYards[i].finishDeployment();
					}
				}else if(constructionYards[i].refineryProgress < 240 && frameIndex > 300) {
					//if there is not enough money to finish building the refinery, reset all other production.
				

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
				
					//then reset factory production if still dont have enough credit to finish building
					hasEnoughCredit = theBaseInfo.currentCredit > 1200 -constructionYards[i].creditSpentOnBuilding;
					if(!hasEnoughCredit) {
						factory[] factories = mainThread.theAssetManager.factories;
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
				
				
				//deploy factory
				if(constructionYards[i].factoryProgress == 240){
				
					int factoryDeployLocation = findFactoryDeployLocation();
					if(hasRoomForPlacement(105, factoryDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						factory o = new factory(x*0.25f + 0.125f, -1.13f, y*0.25f, 1);
						mainThread.theAssetManager.addFactory(o);
							
						constructionYards[i].finishDeployment();				
						
					}
				}
				
				//deploy communication center
				if(constructionYards[i].communicationCenterProgress == 240){
					if(hasRoomForPlacement(106, communicationCenter.intendedDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						communicationCenter o = new communicationCenter(x*0.25f, -1f, y*0.25f, 1);
						mainThread.theAssetManager.addCommunicationCenter(o);
							
						constructionYards[i].finishDeployment();				
						communicationCenter.intendedDeployLocation = -1;	
					}
				}
				
				//deploy tech center
				if(constructionYards[i].techCenterProgress == 240){
					if(hasRoomForPlacement(107, techCenter.intendedDeployLocation)){
						int y = 127 - placementTile/128;
						int x = placementTile%128 + 1;
						techCenter o = new techCenter(x*0.25f, -1f, y*0.25f, 1);
						mainThread.theAssetManager.addTechCenter(o);
							
						constructionYards[i].finishDeployment();				
						techCenter.intendedDeployLocation = -1;	
					}
				}
				
				//deploy gun turret
				if(constructionYards[i].gunTurretProgress == 240) {
					float xPos = mainThread.ec.theDefenseManagerAI.gunTurretDeployLocation.x;
					float zPos = mainThread.ec.theDefenseManagerAI.gunTurretDeployLocation.z;
					int centerTile = (int)(xPos*64)/16 + (127 - (int)(zPos*64)/16)*128;
					if(xPos != 0) {
						if(hasRoomForPlacement(200, centerTile)) {
							int y = 127 - placementTile/128;
							int x = placementTile%128;
							gunTurret o = new gunTurret(x*0.25f + 0.125f, -0.65f, y*0.25f + 0.125f, 1);
							mainThread.theAssetManager.addGunTurret(o);
							
							constructionYards[i].finishDeployment();			
						}
					}
				}
				
				//deploy missile turret
				if(constructionYards[i].missileTurretProgress == 240) {
					float xPos = mainThread.ec.theDefenseManagerAI.missileTurretDeployLocation.x;
					float zPos = mainThread.ec.theDefenseManagerAI.missileTurretDeployLocation.z;
					int centerTile = (int)(xPos*64)/16 + (127 - (int)(zPos*64)/16)*128;
					if(xPos != 0) {
						if(hasRoomForPlacement(200, centerTile)) {
							int y = 127 - placementTile/128;
							int x = placementTile%128;
							missileTurret o = new missileTurret(x*0.25f + 0.125f, -0.65f, y*0.25f + 0.125f, 1);
							mainThread.theAssetManager.addMissileTurret(o);
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
			if(checkIfBlockIsFree(centerTile)) {
				placementTile = centerTile;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 1)) {
				placementTile = centerTile + 1;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 1)) {
				placementTile = centerTile - 1;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 128)) {
				placementTile = centerTile + 128;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 128)) {
				placementTile = centerTile - 128;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 129)) {
				placementTile = centerTile - 129;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 127)) {
				placementTile = centerTile - 127;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 127)) {
				placementTile = centerTile + 127;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 129)) {
				placementTile = centerTile + 129;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 2)) {
				placementTile = centerTile + 2;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 2)) {
				placementTile = centerTile - 2;
				return true;
			}else if(checkIfBlockIsFree(centerTile + 256)) {
				placementTile = centerTile + 256;
				return true;
			}else if(checkIfBlockIsFree(centerTile - 256)) {
				placementTile = centerTile - 256;
				return true;
			}
		}
		
		
		//check placement for power plant
		if(buildingType == 101){
			constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
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
		
		//check placement for refinery
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
		
		//check placement for factory
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
				constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
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
							for(int k = 0; k < mainThread.theAssetManager.communicationCenters.length; k++){
								if(mainThread.theAssetManager.communicationCenters[k] != null && mainThread.theAssetManager.communicationCenters[k].teamNo != 0){
									int x_ = mainThread.theAssetManager.communicationCenters[k].tileIndex[0]%128;
									int y_ = mainThread.theAssetManager.communicationCenters[k].tileIndex[0]/128;
									
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
				constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
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
			solidObject[] tile = mainThread.gridMap.tiles[index];
			for(int j = 0; j < 5; j++){
				if(tile[j] != null){
					
					return false;
				}
			}
			
			if(mainThread.ec.theEconomyManagerAI.preferedGoldMine != null){
				int location = mainThread.ec.theEconomyManagerAI.preferedGoldMine.tileIndex[1];
			
				if( index == location - 128 || index == location - 129 || index == location - 130 || index == location - 2 || 
				    index == location + 126 || index == location + 254 || index == location + 255 || index == location + 256 ||
				    index == location + 257 || index == location + 129 || index == location + 1 || index == location-127){
					return false;
				}
			}
			
			constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
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
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
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
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
		factory[] factories = mainThread.theAssetManager.factories;
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
		refinery[] refineries = mainThread.theAssetManager.refineries;
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] != null && refineries[i].teamNo != 0){
				if(mainThread.ec.theEconomyManagerAI.preferedGoldMine.getDistance(refineries[i]) < 2.5){
					numberOfRefineriesNearPreferedGoldMine++;
				}
			}
		}
		
		return numberOfRefineriesNearPreferedGoldMine;
	}
	
	public int getNumberOfFunctionalRefinery(){
		int numberOfFunctionalRefinery = 0;
		
		refinery[] refineries = mainThread.theAssetManager.refineries;
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
			if(communicationCenter.rapidfireResearched_enemy)
				return 300;
			else
				return 250;
		}else if(buildingType == 107)
			return 400;
		return 0;
	}
	
	
}
