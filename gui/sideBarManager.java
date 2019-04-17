package gui;

import core.mainThread;
import core.playerCommander;
import entity.*;

//this class handles player's interaction with the sidebar
public class sideBarManager {

	public playerCommander pc;
	public boolean rightMouseButtonClicked;
	public boolean leftMouseButtonClicked;
	
	public boolean cursorInBlock0;
	public boolean cursorInBlock1;
	public boolean cursorInBlock2;
	public boolean cursorInBlock3;
	public boolean cursorInBlock4;
	public boolean cursorInBlock5;
	public boolean cursorInBlock6;
	public boolean cursorInBlock7;
	public boolean cursorInBlock8;
	
	public boolean onlyFactorySelected;

	public boolean factoryRallyOnSameGoldMine;
	
	public int[] sideBarInfo;
	public int[] sideBarInfo2;
	
	 
	public sideBarManager(playerCommander pc){
		this.pc = pc;
		sideBarInfo = new int[9];
		sideBarInfo2 = new int[9];
		
		for(int i = 0; i < 9; i ++){
			sideBarInfo[i] = -1;
			sideBarInfo2[i] = -1;
		}
			
	}
	
	public void update(){		
		//reset sideBarInfo
		for(int i = 0; i < 9; i++)
			sideBarInfo[i] = -1;
		
		//check selected units;
		solidObject[] selectedUnits = pc.selectedUnits;
		solidObject selectedObject = null;
		
	
		
		int mouseX = inputHandler.mouse_x;
		int mouseY = inputHandler.mouse_y;
	
		
		int x1 = 635; int y1 = 381; 
		int x2 = 677; int y2 = 425;
		int x3 = 722; int y3 = 468;
		int x4 = 766; int y4 = 509;
		
	
		if(mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2){
			cursorInBlock0 = true;	
		}else if(mouseX > x2 && mouseX < x3 && mouseY > y1 && mouseY < y2){
			cursorInBlock1 = true;	
		}else if(mouseX > x3 && mouseX < x4 && mouseY > y1 && mouseY < y2){
			cursorInBlock2 = true;	
		}else if(mouseX > x1 && mouseX < x2 && mouseY > y2 && mouseY < y3){
			cursorInBlock3 = true;	
		}else if(mouseX > x2 && mouseX < x3 && mouseY > y2 && mouseY < y3){
			cursorInBlock4 = true;	
		}else if(mouseX > x3 && mouseX < x4 && mouseY > y2 && mouseY < y3){
			cursorInBlock5 = true;	
		}else if(mouseX > x1 && mouseX < x2 && mouseY > y3 && mouseY < y4){
			cursorInBlock6 = true;	
		}else if(mouseX > x2 && mouseX < x3 && mouseY > y3 && mouseY < y4){
			cursorInBlock7 = true;	
		}else if(mouseX > x3 && mouseX < x4 && mouseY > y3 && mouseY < y4){
			cursorInBlock8 = true;	
		}
		
		
		//side bar will be interactive only if the selected units are the same type
		for(int i = 0; i < selectedUnits.length;i++){
			if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0){
				if(selectedObject == null){
					selectedObject = selectedUnits[i];
				}else{
					if(selectedObject.type != selectedUnits[i].type){
						selectedObject = null;
						break;
					}
				}
			}
			
			if(selectedUnits[i] != null && selectedUnits[i].teamNo != 0){
				selectedObject = null;
				break;
			}
				
		}
		
		//check if any building is selected
		boolean buildingSelected = false;
		onlyFactorySelected = false;
		for(int i = 0; i < selectedUnits.length;i++){
			if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0){
				if(selectedUnits[i].type > 100 && selectedUnits[i].type != 103){
					buildingSelected = true;
				}else{
					buildingSelected = false;
				}
			}
		}
		
		
		//check if there is only one construction yard among the selected objects
		int numOfselectedConstructionYard = 0;
		solidObject selecterdConyard = null;
		for(int i = 0; i < selectedUnits.length;i++){
			if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0){
				if(selectedUnits[i].type == 104) {
					numOfselectedConstructionYard++;
					selecterdConyard = selectedUnits[i];
				}
			}
		}
		if(numOfselectedConstructionYard == 1) {
			buildingSelected = true;
			selectedObject = selecterdConyard;
		}
		
		//check if there is only one factory among the selected objects
		int numOfselectedFactory = 0;
		solidObject selecterdFactory = null;
		for(int i = 0; i < selectedUnits.length;i++){
			if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0){
				if(selectedUnits[i].type == 105) {
					numOfselectedFactory++;
					selecterdFactory = selectedUnits[i];
				}
			}
		}
		if(numOfselectedFactory >= 1 && numOfselectedConstructionYard != 1) {
			buildingSelected = true;
			selectedObject = selecterdFactory;
		}

		
		//give the player option to repair, if only building(s) are selected
		if(buildingSelected){
			
			if(cursorInBlock8 && leftMouseButtonClicked){
				for(int i = 0; i < selectedUnits.length;i++)
					if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0)
						if(selectedUnits[i].type > 100 && selectedUnits[i].type != 103)
							selectedUnits[i].isRepairing = true;
			}
			
			if(cursorInBlock8 && rightMouseButtonClicked){
				for(int i = 0; i < selectedUnits.length;i++)
					if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0)
						if(selectedUnits[i].type > 100 && selectedUnits[i].type != 103)
							selectedUnits[i].isRepairing = false;
			}
			
			
			int displayInfo = 0;
			if(cursorInBlock8){
				displayInfo = 13;
			}
			
			boolean showAutoRepairMark = true;
			for(int i = 0; i < selectedUnits.length;i++)
				if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0)
					if(selectedUnits[i].type > 100 && selectedUnits[i].type != 103 && selectedUnits[i].isRepairing == false)
						showAutoRepairMark = false;
			
			
			
			if(showAutoRepairMark)
				sideBarInfo[8] = displayInfo << 24 | 14 << 16 | 255 << 8 | 32;
			else
				sideBarInfo[8] = displayInfo << 24 | 14 << 16 | 255 << 8 | 16;
		}
	
	
		if(selectedObject != null){
			//handle construction Vehicle side bar  interaction 
			if(selectedObject.type == 3){
				boolean constructionVehicleCanBeDeployed = false;
				
				for(int i = 0; i < selectedUnits.length;i++){
					if(selectedUnits[i] != null){
						
						constructionVehicle cv = (constructionVehicle)selectedUnits[i];
						if(cv.canBeDeployed()){
							
							constructionVehicleCanBeDeployed = true;
							if(cursorInBlock0 && leftMouseButtonClicked){
								
								cv.expand();
								
							}
						}
					}
				}
				
				int displayInfo = 0;
				if(cursorInBlock0){
					displayInfo = 2;
				}
				
				
				if(constructionVehicleCanBeDeployed){
					//                   display info    texture   progress  text
					sideBarInfo[0] = displayInfo << 24 | 0 << 16 | 240 << 8 | 0;
				}else{
					sideBarInfo[0] = displayInfo << 24 | 0 << 16 | 0 << 8 | 0;
				}
			}
			
			
			//handle factory side bar interaction
			if(selectedObject.type == 105 && !(numOfselectedConstructionYard == 1)){
				onlyFactorySelected = true;
				for(int i = 0; i < selectedUnits.length; i++) {
					if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0)
						if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1 || selectedUnits[i].type == 2 || selectedUnits[i].type == 3 || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
							onlyFactorySelected = false;
							break;
						}
				}
				
				
				factoryRallyOnSameGoldMine = true;
				boolean firstFactory = true;
				goldMine o = null;
				for(int i = 0; i < selectedUnits.length;i++){
					if(selectedUnits[i] != null && selectedUnits[i].type == 105){
						factory f = (factory)(selectedUnits[i]);
						if(firstFactory){
							o = f.targetGoldMine;
							firstFactory = false;
						}else{
							if(o != f.targetGoldMine)
								factoryRallyOnSameGoldMine = false;
							break;
						}
					}
				}
				
				//can  interact with one than 1 factory at a time
				if(mainThread.pc.numberOfSelectedUnits != 0){
					factory f = null;
					for(int i = 0; i < selectedUnits.length;i++){
						if(selectedUnits[i] != null && selectedUnits[i].type == 105){
							f = (factory)selectedUnits[i];
							
							//handle light tank building progress and display info
							if(f.canBuildLightTank){
								
								//start building 
								if(cursorInBlock0 && leftMouseButtonClicked){
									f.buildLightTank();
								}
								
								//cancel buidling 
								if(cursorInBlock0 && rightMouseButtonClicked){
									
									f.cancelItemFromProductionQueue(f.lightTankType);
								}
								
								
							
								//display info
								int displayInfo = 0;
								if(cursorInBlock0){
									displayInfo = 5;
								}
								
								sideBarInfo[0] = displayInfo << 24 | 6 << 16 | f.lightTankProgress << 8 | (f.numOfLightTankOnQueue + 100);
								
							}
							
							//handle rocket tank building progress and display info
							if(f.canBuildRocketTank){
								//start building 
								if(cursorInBlock1 && leftMouseButtonClicked){
									f.buildRocketTank();
								}
								
								//cancel buidling 
								if(cursorInBlock1 && rightMouseButtonClicked){
									
									f.cancelItemFromProductionQueue(f.rocketTankType);
								}

								//display info
								int displayInfo = 0;
								if(cursorInBlock1){
									displayInfo = 6;
								}
								
								sideBarInfo[1] = displayInfo << 24 | 7 << 16 | f.rocketTankProgress << 8 | (f.numOfRocketTankOnQueue + 100);
								
							}
							
							//handle harvester building progress and display info
							if(f.canBuildHarvester){
								//start building 
								if(cursorInBlock2 && leftMouseButtonClicked){
									f.buildHarvester();
								}
								
								//cancel buidling 
								if(cursorInBlock2 && rightMouseButtonClicked){
									f.cancelItemFromProductionQueue(f.harvesterType);
								}
								
								//display info
								int displayInfo = 0;
								if(cursorInBlock2){
									displayInfo = 7;
								}
									
								sideBarInfo[2] = displayInfo << 24 | 8 << 16 | f.harvesterProgress << 8 | (f.numOfHarvesterOnQueue + 100);
							}
							
							//handle drone building progress and display info
							if(f.canBuildDrone){
								//start building 
								if(cursorInBlock3 && leftMouseButtonClicked){
									f.buildDrone();
								}
								
								//cancel buidling 
								if(cursorInBlock3 && rightMouseButtonClicked){
									f.cancelItemFromProductionQueue(f.droneType);
								}
								
								//display info
								int displayInfo = 0;
								if(cursorInBlock3){
									displayInfo = 8;
								}
								
								if(f.numOfDrones == 3){
									sideBarInfo[3] = displayInfo << 24 | 9 << 16 | 0 << 8 | (f.numOfDroneOnQueue + 100);
								}else{
									sideBarInfo[3] = displayInfo << 24 | 9 << 16 | f.droneProgress << 8 | (f.numOfDroneOnQueue + 100);
								}
							}
							
							//handle MCV building progress and display info
							if(f.canBuildMCV){
								
								//start building 
								if(cursorInBlock5 && leftMouseButtonClicked){
									f.buildMCV();
								}
								
								//cancel buidling 
								if(cursorInBlock5 && rightMouseButtonClicked){
									f.cancelItemFromProductionQueue(f.MCVType);
								}
								
								
								//display info
								int displayInfo = 0;
								if(cursorInBlock5){
									displayInfo = 10;
								}
									
								sideBarInfo[5] = displayInfo << 24 | 11 << 16 | f.MCVProgress << 8 | (f.numOfMCVOnQueue + 100);
								
							}
							
							//handle stealth building progress and display info
							if(f.canBuildStealthTank){
								//start building 
								if(cursorInBlock4 && leftMouseButtonClicked){
									f.buildStealthTank();
								}
								
								//cancel buidling 
								if(cursorInBlock4 && rightMouseButtonClicked){
									f.cancelItemFromProductionQueue(f.stealthTankType);
								}
								
								//display info
								int displayInfo = 0;
								if(cursorInBlock4){
									displayInfo = 11;
								}
								
								sideBarInfo[4] = displayInfo << 24 | 12 << 16 | f.stealthTankProgress << 8 | (f.numOfStealthTankOnQueue + 100);
							}
							
							//handle heavy tank building progress and display info
							if(f.canBuildHeavyTank){
								//start building 
								if(cursorInBlock6 && leftMouseButtonClicked){
									
									f.buildHeavyTank();
								}
								
								//cancel buidling 
								if(cursorInBlock6 && rightMouseButtonClicked){
									f.cancelItemFromProductionQueue(f.heavyTankType);
								}
								
								//display info
								int displayInfo = 0;
								if(cursorInBlock6){
									displayInfo = 19;
								}
								
								sideBarInfo[6] = displayInfo << 24 | 19 << 16 | f.heavyTankProgress << 8 | (f.numOfHeavyTankOnQueue + 100);
					
							}
							
						}
					}
				}		
			}
			
			
			//handle missile turret side bar interaction
			if(selectedObject.type == 199){
				if(communicationCenter.rapidfireResearched_player){
					
					if(cursorInBlock5 && leftMouseButtonClicked){
						for(int i = 0; i < selectedUnits.length;i++)
							if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0)
								if(selectedUnits[i].type == 199){
									missileTurret o = (missileTurret)selectedUnits[i];
									if(o.overCharge == false){
										o.overCharge = true;
										mainThread.pc.theBaseInfo.numberOfOverChargedMissileTurret++;
									}
								}
					}
					
					if(cursorInBlock5 && rightMouseButtonClicked){
						for(int i = 0; i < selectedUnits.length;i++)
							if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0)
								if(selectedUnits[i].type == 199){
									missileTurret o = (missileTurret)selectedUnits[i];
									if(o.overCharge == true){
										o.overCharge = false;
										mainThread.pc.theBaseInfo.numberOfOverChargedMissileTurret--;
									}
								}
					}
					
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock5){
						displayInfo = 17;
					}
					
					boolean showRapidfireMark = true;
					for(int i = 0; i < selectedUnits.length;i++){
						if(selectedUnits[i] != null&& selectedUnits[i].teamNo == 0){
							if(selectedUnits[i].type == 199){
								missileTurret o = (missileTurret)selectedUnits[i];
								if(!o.overCharge)
									showRapidfireMark = false;
							}
						}
					}
					
					if(showRapidfireMark)
						sideBarInfo[5] = displayInfo << 24 | 17 << 16 | 255 << 8 | 32;
					else
						sideBarInfo[5] = displayInfo << 24 | 17 << 16 | 255 << 8 | 16;
				}
			}
			
			//handle communication center side bar interaction
			if(selectedObject.type == 106){
				
				//handle harvester speed research
				if(!communicationCenter.harvesterSpeedResearched_player){
					//start researching
					if(cursorInBlock0 && leftMouseButtonClicked && communicationCenter.harvesterSpeedResearchProgress_player == 255){
						communicationCenter.researchHarvesterSpeed(0);
					}
					
					//cancel researching
					if(cursorInBlock0 && rightMouseButtonClicked && communicationCenter.harvesterSpeedResearchProgress_player != 255 && communicationCenter.harvesterSpeedResearchProgress_player != 254){
						communicationCenter.cancelResearch(0);

					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock0){
						displayInfo = 15;
					}
					
					sideBarInfo[0] = displayInfo << 24 | 16 << 16 | communicationCenter.harvesterSpeedResearchProgress_player << 8 | 0;
				}
				
				//handle rapid fire research
				if(!communicationCenter.rapidfireResearched_player){
					//start researching
					if(cursorInBlock1 && leftMouseButtonClicked && communicationCenter.rapidfireResearchProgress_player == 255){
						communicationCenter.researchRapidfire(0);
					}
					
					//cancel researching
					if(cursorInBlock1 && rightMouseButtonClicked && communicationCenter.rapidfireResearchProgress_player != 255 && communicationCenter.rapidfireResearchProgress_player != 254){
						communicationCenter.cancelResearch(0);
						
					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock1){
						displayInfo = 16;
					}
					
					sideBarInfo[1] = displayInfo << 24 | 17 << 16 | communicationCenter.rapidfireResearchProgress_player << 8 | 0;
				}
				
			}
			
			//handle tech center side bar interaction
			if(selectedObject.type == 107){
				
				//handle light tank range research
				if(!techCenter.lightTankResearched_player){
					if(cursorInBlock0 && leftMouseButtonClicked && techCenter.lightTankResearchProgress_player == 255){
						techCenter.researchLightTank(0);
					}
					
					//cancel researching
					if(cursorInBlock0 && rightMouseButtonClicked && techCenter.lightTankResearchProgress_player != 255 && techCenter.lightTankResearchProgress_player != 254){
						techCenter.cancelResearch(0);
					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock0){
						displayInfo = 20;
					}
					sideBarInfo[0] = displayInfo << 24 | 20 << 16 | techCenter.lightTankResearchProgress_player << 8 | 0;
				}
				
				//handle rocket tank damage research
				if(!techCenter.rocketTankResearched_player){
					if(cursorInBlock1 && leftMouseButtonClicked && techCenter.rocketTankResearchProgress_player == 255){
						
						techCenter.researchRocketTank(0);
					}
					
					//cancel researching
					if(cursorInBlock1 && rightMouseButtonClicked && techCenter.rocketTankResearchProgress_player != 255 && techCenter.rocketTankResearchProgress_player != 254){
						techCenter.cancelResearch(0);
					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock1){
						displayInfo = 21;
					}
					
					sideBarInfo[1] = displayInfo << 24 | 21 << 16 | techCenter.rocketTankResearchProgress_player << 8 | 0;	
				}
				
				//handle stealth multishot research
				if(!techCenter.stealthTankResearched_player){
					if(cursorInBlock2 && leftMouseButtonClicked && techCenter.stealthTankResearchProgress_player == 255){
						
						techCenter.researchStealthTank(0);
					}
					
					//cancel researching
					if(cursorInBlock2 && rightMouseButtonClicked && techCenter.stealthTankResearchProgress_player != 255 && techCenter.stealthTankResearchProgress_player != 254){
						techCenter.cancelResearch(0);
					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock2){
						displayInfo = 22;
					}
					
					sideBarInfo[2] = displayInfo << 24 | 22 << 16 | techCenter.stealthTankResearchProgress_player << 8 | 0;	
				}
				
				//handle heavy tank self repair research
				if(!techCenter.heavyTankResearched_player){
					if(cursorInBlock3 && leftMouseButtonClicked && techCenter.heavyTankResearchProgress_player == 255){
						
						techCenter.researchHeavyTank(0);
					}
					
					//cancel researching
					if(cursorInBlock3 && rightMouseButtonClicked && techCenter.heavyTankResearchProgress_player != 255 && techCenter.heavyTankResearchProgress_player != 254){
						techCenter.cancelResearch(0);
					}
					
					//display info
					int displayInfo = 0;
					if(cursorInBlock3){
						displayInfo = 23;
					}
					
					sideBarInfo[3] = displayInfo << 24 | 23 << 16 | techCenter.heavyTankResearchProgress_player << 8 | 0;	
				}
				
			}
			
			
			//handle construction yard side bar interaction
			if(selectedObject.type == 104){
				
				//can only interact with one construction yard at a time
				if(numOfselectedConstructionYard == 1){
					
					constructionYard cy = (constructionYard)selecterdConyard;
					
					
					//handle power plant building progress and display info
					if(cy.canBuildPowerPlant){
						//start building 
						if(cursorInBlock0 && leftMouseButtonClicked && cy.powerPlantProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildPowerPlant();
						}
						
						if(cursorInBlock0 && leftMouseButtonClicked && cy.powerPlantProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock0 && rightMouseButtonClicked && cy.powerPlantProgress != 255 && cy.powerPlantProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock0){
							displayInfo = 1;
						}
						
						sideBarInfo[0] = displayInfo << 24 | 1 << 16 | cy.powerPlantProgress << 8 | (cy.powerPlantProgress/240 + cy.powerPlantProgress/240 * cy.powerPlantProgress%240);
						
	
					}
					
					//handle refinery building progress and display info
					if(cy.canBuildRefinery){
						//start building 
						if(cursorInBlock1 && leftMouseButtonClicked && cy.refineryProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildRefinery();
						}
						
						if(cursorInBlock1 && leftMouseButtonClicked && cy.refineryProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock1 && rightMouseButtonClicked && cy.refineryProgress != 255 && cy.refineryProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock1){
							displayInfo = 3;
						}
						
						sideBarInfo[1] = displayInfo << 24 | 2 << 16 | cy.refineryProgress << 8 | (cy.refineryProgress/240 + cy.refineryProgress/240 * cy.refineryProgress%240);
					}
					
					//handle factory building progress and display info
					if(cy.canBuildFactory){
						//start building 
						if(cursorInBlock2 && leftMouseButtonClicked && cy.factoryProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildFactory();
						}
						
						if(cursorInBlock2 && leftMouseButtonClicked && cy.factoryProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock2 && rightMouseButtonClicked && cy.factoryProgress != 255 && cy.factoryProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock2){
							displayInfo = 4;
						}
						
						sideBarInfo[2] = displayInfo << 24 | 5 << 16 | cy.factoryProgress << 8 | (cy.factoryProgress/240 + cy.factoryProgress/240 * cy.factoryProgress%240);
						
					}
					
					//handle communication center building progress and display info
					if(cy.canBuildCommunicationCenter){
						//start building 
						if(cursorInBlock3 && leftMouseButtonClicked && cy.communicationCenterProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildCommunicationCentre();
						}
						
						if(cursorInBlock3  && leftMouseButtonClicked && cy.communicationCenterProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock3 && rightMouseButtonClicked && cy.communicationCenterProgress != 255 && cy.communicationCenterProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock3){
							displayInfo = 9;
						}
						
						sideBarInfo[3] = displayInfo << 24 | 10 << 16 | cy.communicationCenterProgress << 8 | (cy.communicationCenterProgress/240 + cy.communicationCenterProgress/240 * cy.communicationCenterProgress%240);
					}
					
					//handle turret building process and display info 
					if(cy.canBuildGunTurret){
						//start building 
						if(cursorInBlock4 && leftMouseButtonClicked && cy.gunTurretProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildGunTurret();
				
						}
						
						if(cursorInBlock4  && leftMouseButtonClicked && cy.gunTurretProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock4 && rightMouseButtonClicked && cy.gunTurretProgress != 255 && cy.gunTurretProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock4){
							displayInfo = 12;
						}
						
						sideBarInfo[4] = displayInfo << 24 | 13 << 16 | cy.gunTurretProgress << 8 | (cy.gunTurretProgress/240 + cy.gunTurretProgress/240 * cy.gunTurretProgress%240);
					}
					
					//handle missile turret building process and display info 
					if(cy.canBuildMissileTurret){
						//start building 
						if(cursorInBlock5 && leftMouseButtonClicked && cy.missileTurretProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildMissileTurret();
				
						}
						
						if(cursorInBlock5  && leftMouseButtonClicked && cy.missileTurretProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock5 && rightMouseButtonClicked && cy.missileTurretProgress != 255 && cy.missileTurretProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock5){
							displayInfo = 14;
						}
						
						sideBarInfo[5] = displayInfo << 24 | 15 << 16 | cy.missileTurretProgress << 8 | (cy.missileTurretProgress/240 + cy.missileTurretProgress/240 * cy.missileTurretProgress%240);
					}
					
					//handle tech center building process and display info
					if(cy.canBuildTechCenter){
						//start building 
						if(cursorInBlock6 && leftMouseButtonClicked && cy.techCenterProgress == 255 && !mainThread.pc.isDeployingBuilding){
							cy.buildTechCenter();
						
						}
						
						if(cursorInBlock6  && leftMouseButtonClicked && cy.techCenterProgress == 240){
							cy.needToDrawDeploymentGrid = true;
							mainThread.pc.isDeployingBuilding = true;
							mainThread.pc.selectedConstructionYard = cy;
						}
						
						//cancel buidling 
						if(cursorInBlock6 && rightMouseButtonClicked && cy.techCenterProgress != 255 && cy.techCenterProgress != 254){
							mainThread.pc.isDeployingBuilding = false;
							mainThread.pc.selectedConstructionYard = null;
							cy.needToDrawDeploymentGrid = false;
							cy.cancelBuilding();
						}
						
						//display info
						int displayInfo = 0;
						if(cursorInBlock6){
							displayInfo = 18;
						}
					
						sideBarInfo[6] = displayInfo << 24 | 18 << 16 | cy.techCenterProgress << 8 | (cy.techCenterProgress/240 + cy.techCenterProgress/240 * cy.techCenterProgress%240);
					}
					
					
				}
			}
		}
		

		
		rightMouseButtonClicked = false;
		leftMouseButtonClicked = false;
		
		cursorInBlock0 = false;
		cursorInBlock1 = false;
		cursorInBlock2 = false;
		cursorInBlock3 = false;
		cursorInBlock4 = false;
		cursorInBlock5 = false;
		cursorInBlock6 = false;
		cursorInBlock7 = false;
		cursorInBlock8 = false;
		
	}
	
	public void swapResources(){
		int[] info;
		info = sideBarInfo;
		sideBarInfo = sideBarInfo2;
		sideBarInfo2 = info;
	}
	
}
