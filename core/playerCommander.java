package core;

import java.awt.Rectangle;

import entity.constructionVehicle;
import entity.constructionYard;
import entity.factory;
import entity.solidObject;
import gui.inputHandler;
import audio.SoundEngine;
import audio.SoundEngine.Sfx;

//this class interprets  player's inputs and turns them into commands that can be issued to game  units 
public class playerCommander {

	public solidObject[] selectedUnits;
	
	public solidObject[][] groups;
	
	public boolean leftMouseButtonPressed, rightMouseButtonPressed, leftMouseButtonReleased, rightMouseButtonReleased, attackKeyPressed, toggleConyard, toggleFactory, holdKeyPressed, controlKeyPressed, deployKeyPressed;
	
	public int numberTyped;
	
	public boolean isSelectingUnit, isMovingViewWindow;
	
	public int startX, startY, endX, endY;
	
	public Rectangle area, areaSmall;
	
	public vector clickPoint;
	
	public AssetManager theAssetManager; 
	
	public int numberOfSelectedUnits;
	
	public int doubleClickCountDown;
	
	public boolean doubleClicked;
	
	public int doubleNumberPressCountdown;
	public int pressedNumber;
	public boolean doubleNumberPressed;
	public int selectedIndex;
	
	
	public sideBarManager theSideBarManager;
	
	public boolean isDeployingBuilding;
	public constructionYard selectedConstructionYard;
	
	public baseInfo theBaseInfo;

	//tracks the previous frame's credit total so a change (from any of the many scattered
	//currentCredit++/-- sites across buildings/units) can be detected in one place and turned into a
	//classic RTS money-counter tick, throttled so a fast trickle (e.g. construction cost draining
	//every frame) doesn't spam a sound 45 times a second. earn and spend use separate cooldowns
	//(rather than one shared one) so an earn tick is never suppressed just because a spend tick
	//happened to fire a moment earlier - earning always wins: spend is muted for the whole duration
	//of an earn sound instead of just being throttled like it throttles itself
	private int previousCredit = -1;
	private int creditEarnCooldown;
	private int creditSpendCooldown;
	private int earnSoundBusyFrames;
	
	public boolean mouseOverSelectableUnit;
	public int mouseOverUnitType;
	public int mouseOverUnitTeam;
	public boolean mouseOverUnitIsSelected;
	public boolean mouseOverUnitCanDeploy;
	public boolean hasConVehicleSelected;
	public boolean hasHarvesterSelected;
	public boolean hasTroopsSelected;
	public boolean hasTowerSelected;
	
	public int screen_width;
	public int screen_height;
	public int screen_size;
	
	public void init(){
		
		screen_width = mainThread.screen_width;
		screen_height = mainThread.screen_height;
		screen_size = mainThread.screen_size;
		
		selectedUnits = new solidObject[100];
		groups = new solidObject[5][100];
		area = new Rectangle();
		areaSmall = new Rectangle();
		clickPoint = new vector(0,0,0);
		theAssetManager = mainThread.theAssetManager;
		theSideBarManager = new sideBarManager(this);
		theBaseInfo = new baseInfo();
		
	}
	
	
	
	
	public void update(){
		theBaseInfo.update();

		if(creditEarnCooldown > 0)
			creditEarnCooldown--;
		if(creditSpendCooldown > 0)
			creditSpendCooldown--;
		if(earnSoundBusyFrames > 0)
			earnSoundBusyFrames--;

		if(previousCredit == -1){
			previousCredit = theBaseInfo.currentCredit;
		}else if(theBaseInfo.currentCredit != previousCredit){
			if(theBaseInfo.currentCredit > previousCredit){
				if(creditEarnCooldown == 0){
					SoundEngine.play(Sfx.CREDIT_EARN);
					creditEarnCooldown = 3;
					earnSoundBusyFrames = 38; //~820ms at 45fps, matches the earn clip's length so spend stays muted while it rings out
				}
			}else if(creditSpendCooldown == 0 && earnSoundBusyFrames == 0){
				SoundEngine.play(Sfx.CREDIT_SPEND);
				creditSpendCooldown = 3;
			}
			previousCredit = theBaseInfo.currentCredit;
		}


		if(isDeployingBuilding){
			
			if(leftMouseButtonPressed && !cursorIsInMiniMap() && !cursorIsInSideBar() && selectedConstructionYard.dg.canBeDeployed){
				//create a new building
				selectedConstructionYard.createBuilding();
				isDeployingBuilding = false;
				selectedConstructionYard.needToDrawDeploymentGrid = false;
				selectedConstructionYard.finishDeployment();
				selectedConstructionYard = null;
				leftMouseButtonPressed = false;
			}
			
			if(rightMouseButtonReleased && !inputHandler.isDraggingCamera){
				isDeployingBuilding = false;
				selectedConstructionYard.needToDrawDeploymentGrid = false;
				selectedConstructionYard = null;
				rightMouseButtonReleased = false;
			}else{
				if(!cursorIsInMiniMap()){
					theSideBarManager.update();
					leftMouseButtonPressed = false;
					rightMouseButtonReleased = false;
					isMovingViewWindow = false;
					return;
				}
			}
		}
		
		if(doubleClickCountDown > 0)
			doubleClickCountDown--;
		if(doubleClickCountDown == 0)
			doubleClicked = false;
		
		if(doubleNumberPressCountdown > 0)
			doubleNumberPressCountdown--;
		if(doubleNumberPressCountdown == 0){
			pressedNumber  = 255;
			
		}
			
		
		if(numberTyped != 0){
		
			if(controlKeyPressed){
				for(int i = 0; i < 100; i ++){
					if(groups[numberTyped-1][i] != null){
						groups[numberTyped-1][i].groupNo = 255;
						groups[numberTyped-1][i] = null;
					}
				}
				
				//check if the current selection has mobile unit, if that is the case grouping should exclude buildings
				boolean hasMobileUnit = false;
				for(int i = 0; i < 100; i ++){
					if(selectedUnits[i] != null){
						if(selectedUnits[i].teamNo == 0){
							if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1 || selectedUnits[i].type == 2 || selectedUnits[i].type == 3 || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
								hasMobileUnit = true;
								break;
							}
						}
					}
				}
					
					
				for(int i = 0; i < 100; i ++){
					if(selectedUnits[i] != null){
						if(selectedUnits[i].teamNo == 0){
							if(hasMobileUnit) {
								if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1 || selectedUnits[i].type == 2 || selectedUnits[i].type == 3 || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
									groups[numberTyped-1][i] = selectedUnits[i];
									selectedUnits[i].groupNo = numberTyped-1;
									selectedUnits[i].isSelected = true;
									removeFromOtherGroup(selectedUnits[i], numberTyped-1);
								}
							}else {
							
								groups[numberTyped-1][i] = selectedUnits[i];
								selectedUnits[i].groupNo = numberTyped-1;
								selectedUnits[i].isSelected = true;
								removeFromOtherGroup(selectedUnits[i], numberTyped-1);
							}
							
						
						}
					}
				}
				
				
			}else{
				if(numberTyped == pressedNumber){
					doubleNumberPressed = true;
					doubleNumberPressCountdown = 30;
					
				}else{
					doubleNumberPressed = false;
					pressedNumber = numberTyped;
					doubleNumberPressCountdown = 10;
				}
						
				numberOfSelectedUnits = 0;
				for(int i = 0; i < 100; i ++){
					if(selectedUnits[i] != null)
						selectedUnits[i].isSelected = false;
					selectedUnits[i] = null;
					if(groups[numberTyped-1][i] != null)
						if(groups[numberTyped-1][i].currentHP > 0){
							selectedUnits[i] = groups[numberTyped-1][i];
							groups[numberTyped-1][i].isSelected = true;
							numberOfSelectedUnits++;
						}
				}
				
				
				//center camera to the one of the selected units
				if(doubleNumberPressed){
					solidObject selectedUnit = null;
					int nullCount = 0;
					
					for(int j = 0; j < 100; j++){
						if(selectedUnits[(j+selectedIndex)%100] != null){
							selectedUnit = selectedUnits[(j+selectedIndex)%100];
							selectedIndex++;
							break;
						}else{
							nullCount++;
						}
					}
					
					selectedIndex+=nullCount;
				
					if(selectedUnit != null){
						
						camera.position.x = selectedUnit.centre.x - camera.view_Direction.x * 3;
						camera.position.z = selectedUnit.centre.z - camera.view_Direction.z * 3;
					}
					
				}
			}
		}
			
		
		
		if(leftMouseButtonPressed){
			if(cursorIsInSideBar())
				theSideBarManager.leftMouseButtonClicked = true;
			
			
			if(attackKeyPressed){
				//if the click lands on empty ground perform "attack move" for all selected unit
				//if the click lands on an unit, then attack that unit regardless if it is friend or foe
				
				boolean performAttack = false;
				if(numberOfSelectedUnits > 0){
					performAttack = true;
				}
					
				if(performAttack){
					
					if(cursorIsInMiniMap()){
						clickPoint.set(0.25f*(inputHandler.mouse_x-3), 0, 0.25f*(127-(inputHandler.mouse_y-(screen_height-131))));
						attackMoveSelectUnit(clickPoint.x, clickPoint.z);
						
					}else if(cursorIsInSideBar()){
						theSideBarManager.leftMouseButtonClicked = true;
						
					}else{
						clickPoint.set(mainThread.my2Dto3DFactory.get3DLocation(theAssetManager.Terrain.ground[0], inputHandler.mouse_x, inputHandler.mouse_y)); 
						float x = clickPoint.x;
						float y = clickPoint.z;
						
						//if click outside map boundary, move units to the closest valid tile 
						if(x < 0.25 || x > 31.75 || y < 0.25 || y > 31.75){
							if( x <  0.25)
								x = 0.125f;
							if(x > 31.75)
								x = 31.875f;
							if(y < 0.25)
								y = 0.125f;
							if(y > 31.75)
								y = 31.875f;
						}
						
						int xPos = (int)(x*64);
						int yPos = (int)(y*64);
						int index = xPos/16 + (127-yPos/16)*128;
						boolean clickOnEmptyGround = true;
						for(int i = 0; i < mainThread.gridMap.tiles[index].length; i++){
							
							if(mainThread.gridMap.tiles[index][i] != null){
								
								if(mainThread.gridMap.tiles[index][i].boundary2D.contains(xPos, yPos)){
									if(mainThread.gridMap.tiles[index][i].visible_minimap){			
										attackUnit(mainThread.gridMap.tiles[index][i]);
										clickOnEmptyGround = false;
										break;
									}
								}
							}
						}
						
						if(clickOnEmptyGround){
						
							attackMoveSelectUnit(x, y);
							
						}
						
						
					}
				}
				
				
				attackKeyPressed = false;
				
			}else{
				if(cursorIsInMiniMap()){
					isMovingViewWindow = true;
				}else{
					
					if(doubleClickCountDown == 0)
						doubleClickCountDown = 15;
					else
						doubleClicked = true;
					
					if(!cursorIsInSideBar())
						isSelectingUnit = true;
					startX = inputHandler.mouse_x;
					startY = inputHandler.mouse_y;
				}
			}
			
		}
		
		if(isMovingViewWindow){
			camera.position.x = 0.25f*(inputHandler.mouse_x-3) - camera.view_Direction.x * 3;
			camera.position.z = 0.25f*(127-(inputHandler.mouse_y-(screen_height-131))) - camera.view_Direction.z * 3;
			
			
		}
		
		if(isSelectingUnit){
			endX = inputHandler.mouse_x;
			endY = inputHandler.mouse_y;

			if(startX < 0)
				startX = 0;
			if(startX > screen_width - 1)
				startX = screen_width - 1;
			if(startY < 0)
				startY = 0;
			if(startY > screen_height - 1)
				startY = screen_height - 1;
			if(endX > screen_width - 1)
				endX = screen_width - 1;
			if(endX < 0)
				endX = 0;
			if(endY > screen_height - 1)
				endY = screen_height - 1;
			if(endY < 0)
				endY = 0;
			
			int width = Math.abs(endX - startX);
			int height = Math.abs(endY - startY);
			int xPos = Math.min(startX, endX);
			int yPos = Math.min(startY, endY);
			area.setBounds(xPos, yPos,width, height);
		}
		
		if(leftMouseButtonReleased){
			
			
			if(isMovingViewWindow){
				isMovingViewWindow = false;
			}
			
			
			if(isSelectingUnit){
				isSelectingUnit = false;
				int width = Math.abs(endX - startX);
				int height = Math.abs(endY - startY);
				int xPos = Math.min(startX, endX) - 50;
				int yPos = Math.min(startY, endY) -20;
				int xPos_small = Math.min(startX, endX) - 30;
				int yPos_small = Math.min(startY, endY) -30;
				
				if(width < 20 || height < 20){
					area.setBounds(xPos, yPos, 100, 100);
					areaSmall.setBounds(xPos_small, yPos_small, 60,60);
					selectUnit(area, areaSmall);
				}else
					selectMultipleUnits(area);
			}
			
		}
		
		if(rightMouseButtonReleased && !inputHandler.isDraggingCamera){
			attackKeyPressed = false;

			if(cursorIsInMiniMap()){
				clickPoint.set(0.25f*(inputHandler.mouse_x-3), 0, 0.25f*(127-(inputHandler.mouse_y-(screen_height-131))));
			}else{
				clickPoint.set(mainThread.my2Dto3DFactory.get3DLocation(theAssetManager.Terrain.ground[0], inputHandler.mouse_x, inputHandler.mouse_y)); 
			}
			
			if(!cursorIsInSideBar()){
				maneuverUnit();
			}else{
				theSideBarManager.rightMouseButtonClicked = true;
			}
			
		}
		
		if(holdKeyPressed){
			
			if(attackKeyPressed) {
	
				attackKeyPressed = false;
				
			}else {
			
				attackKeyPressed = false;
				holdAllSelectedUnit();
			}
		}
		
		if(toggleConyard) {
			int selectedConyardID = -1;
			//deselect all the selected construction yards;
			for(int i = 0; i < selectedUnits.length; i++) {
				if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0) {
					if(selectedUnits[i].type == 104) {
						selectedConyardID = selectedUnits[i].ID;
						deSelect(selectedUnits[i]);
					}else if(selectedUnits[i].type == 105) {
						deSelect(selectedUnits[i]);
					}
				}
			}
		
			//toggle to a different conyard
			constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
			int conyardIndex = -1;
			
			if(selectedConyardID != -1) {	
				for(int i = 0; i < constructionYards.length; i++) {
					if(constructionYards[i] != null && constructionYards[i].ID == selectedConyardID) {
						conyardIndex = i;
						break;
					}
				}
			}else {
				for(int i = 0; i < constructionYards.length; i++) {
					if(constructionYards[i] != null && constructionYards[i].teamNo == 0 && constructionYards[i].currentHP > 0) {
						conyardIndex = i;
						break;
					}
				}
			}
			
			if(conyardIndex != -1) {
				for(int i = conyardIndex+1; i < constructionYards.length + conyardIndex + 1; i++) {
					int index = i%constructionYards.length;
					if(constructionYards[index] != null && constructionYards[index].teamNo == 0 && constructionYards[index].currentHP > 0 && constructionYards[index].isSelectable) {
						addToSelection(constructionYards[index]);
						
						break;
					}
				}
			}
			
			toggleConyard = false;
		}
		
		if(toggleFactory) {
			int selectedConyardID = -1;
			//deselect all the selected condyard and factory;
			for(int i = 0; i < selectedUnits.length; i++) {
				if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0 ) {
					if(selectedUnits[i].type == 105) {
						selectedConyardID = selectedUnits[i].ID;
						deSelect(selectedUnits[i]);
					}else if(selectedUnits[i].type == 104) {
						deSelect(selectedUnits[i]);
					}
				}
			}
			
			//toggle to a different conyard
			factory[] factories = mainThread.theAssetManager.factories;
			int factoryIndex = -1;
				
			if(selectedConyardID != -1) {	
				for(int i = 0; i < factories.length; i++) {
					if(factories[i] != null && factories[i].ID == selectedConyardID) {
						factoryIndex = i;
						break;
					}
				}
			}else {
				for(int i = 0; i < factories.length; i++) {
					if(factories[i] != null && factories[i].teamNo == 0 && factories[i].currentHP > 0) {
						factoryIndex = i;
						break;
					}
				}
			}
		
			if(factoryIndex != -1) {
				for(int i = factoryIndex+1; i < factories.length + factoryIndex + 1; i++) {
					int index = i%factories.length;
					if(factories[index] != null && factories[index].teamNo == 0 && factories[index].currentHP > 0) {
						addToSelection(factories[index]);
						
						break;
					}
				}
			}
			
			toggleFactory = false;
		}

		if(deployKeyPressed) {
			int numOfSelectedConstructionYard = 0;
			constructionYard selectedConyard = null;
			for(int i = 0; i < selectedUnits.length; i++) {
				if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0 && selectedUnits[i].type == 104) {
					numOfSelectedConstructionYard++;
					selectedConyard = (constructionYard)selectedUnits[i];
				}
			}

			if(numOfSelectedConstructionYard == 1) {
				constructionYard cy = selectedConyard;
				if(cy.powerPlantProgress == 240 || cy.refineryProgress == 240 || cy.factoryProgress == 240
						|| cy.communicationCenterProgress == 240 || cy.gunTurretProgress == 240
						|| cy.missileTurretProgress == 240 || cy.techCenterProgress == 240) {
					cy.needToDrawDeploymentGrid = true;
					isDeployingBuilding = true;
					selectedConstructionYard = cy;
				}
			}

			//deploy any selected construction vehicle(s) that are ready to expand into a construction yard
			for(int i = 0; i < selectedUnits.length; i++) {
				if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0 && selectedUnits[i].type == 3) {
					constructionVehicle cv = (constructionVehicle)selectedUnits[i];
					if(cv.canBeDeployed()) {
						cv.expand();
					}
				}
			}

			deployKeyPressed = false;
		}


		//display health bar when mouse cursor hover over a unit
		if(!isSelectingUnit){
			startX = inputHandler.mouse_x;
			startY = inputHandler.mouse_y;
			
			
			
			int xPos = startX - 50;
			int yPos = startY -20;
			int xPos_small = startX - 30;
			int yPos_small = startY -30;
			
			area.setBounds(xPos, yPos, 100, 100);
			areaSmall.setBounds(xPos_small, yPos_small, 60,60);
			addMouseHoverUnitToDisplayInfo(area, areaSmall);
		}
	
		theSideBarManager.update();
		
		hasConVehicleSelected = false;
		hasHarvesterSelected = false;
		hasTroopsSelected = false;
		hasTowerSelected = false;
		
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0 && selectedUnits[i].currentHP > 0){
				if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1 || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
					hasTroopsSelected = true;
				}else if(selectedUnits[i].type == 2) {
					hasHarvesterSelected = true;
				}else if(selectedUnits[i].type == 3) {
					//don't show the move cursor for a construction vehicle that's already mid-deploy -
					//it's no longer something you'd issue a move order to
					constructionVehicle cv = (constructionVehicle)selectedUnits[i];
					if(cv.jobStatus != cv.deploying){
						hasConVehicleSelected = true;
					}
				}else if(selectedUnits[i].type == 200 || selectedUnits[i].type == 199) {
					hasTowerSelected = true;
				}
					
			}
		}
		
		
		
		leftMouseButtonPressed = false;
		rightMouseButtonPressed = false;
		leftMouseButtonReleased = false;
		rightMouseButtonReleased = false;
		holdKeyPressed = false;
		controlKeyPressed = false;
		numberTyped = 0;
	}

	
	
	
	public boolean cursorIsInMiniMap(){
		return inputHandler.mouse_x >=3 && inputHandler.mouse_x <=131 && inputHandler.mouse_y >= (screen_height-131) && inputHandler.mouse_y <= (screen_height - 3);
	}
	
	public boolean cursorIsInSideBar(){
		return inputHandler.mouse_x >=(screen_width - 131) && inputHandler.mouse_x <=(screen_width - 3) && inputHandler.mouse_y >= (screen_height-131) && inputHandler.mouse_y <= (screen_height - 3);
	}
	
	
	
	public void removeDestoryedObjectFromSelection(solidObject o){
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] == o){
				selectedUnits[i] = null;
				numberOfSelectedUnits--;
			}
				
		}
	}
	
	public void holdAllSelectedUnit(){
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0){
						selectedUnits[i].hold(); 
				}
			}
		}
	}
	
	public void moveSelectedUnit(float x, float y){
		
		boolean moveableUnitSelected = false;
		int numOfConYardSelected = 0;
		int numOfMobileUnitSelected = 0;
		
		float groupCenterX = 0;
		float groupCenterY = 0;
		float directionX = 0;
		float directionY = 0;
		
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0){
					if(selectedUnits[i].type < 100 || selectedUnits[i].type == 105){
						selectedUnits[i].moveTo(x, y); 
						selectedUnits[i].currentCommand = solidObject.move;
						selectedUnits[i].secondaryCommand = solidObject.StandBy;
						moveableUnitSelected = true;
					}
					
					if(selectedUnits[i].type == 104)
						numOfConYardSelected++;
					
					if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1 || selectedUnits[i].type == 2 || selectedUnits[i].type == 3 || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
						numOfMobileUnitSelected++;
						groupCenterX += selectedUnits[i].centre.x;
						groupCenterY += selectedUnits[i].centre.z;
					}
				}
			}
		}
		
		groupCenterX/=numOfMobileUnitSelected;
		groupCenterY/=numOfMobileUnitSelected;
		
		directionX = x - groupCenterX;
		directionY = y - groupCenterY;
		
		float innerCircleRadius = (float)numOfMobileUnitSelected/8;
				
		if(directionX*directionX + directionY*directionY >  innerCircleRadius/2) {
			for(int i = 0; i < selectedUnits.length; i++) {
				if(selectedUnits[i] != null && selectedUnits[i].teamNo == 0){
					int type = selectedUnits[i].type;
					if(type == 0 || type == 1 || type == 6 || type == 7) {
						float distance_x = selectedUnits[i].centre.x- groupCenterX;
						float distance_y = selectedUnits[i].centre.z - groupCenterY;
		
						if(distance_x*distance_x + distance_y*distance_y < innerCircleRadius) {					
							selectedUnits[i].moveTo(selectedUnits[i].centre.x + directionX, selectedUnits[i].centre.z + directionY); 
							selectedUnits[i].currentCommand = solidObject.move;
							selectedUnits[i].secondaryCommand = solidObject.StandBy;
						}
					}
				}
			}
		
		}
		
		
		//draw move confirmation if a mobile unit is given move order
		if(moveableUnitSelected && !(numOfConYardSelected == 1 && numOfMobileUnitSelected == 0)){
			theAssetManager.confirmationIconInfo[0] = 1;
			theAssetManager.confirmationIconInfo[1] = x;
			theAssetManager.confirmationIconInfo[2] = y;
			theAssetManager.confirmationIconInfo[3] = 0xbb22;
			SoundEngine.play(Sfx.MOVE);
		}
	}
	
	
	public void attackMoveSelectUnit(float x, float y){
		boolean mobileUnitSelected = false;
		int numOfMobileUnitSelected = 0;
		float groupCenterX = 0;
		float groupCenterY = 0;
		float directionX = 0;
		float directionY = 0;
		
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0){					
					if(selectedUnits[i].type == 0 || selectedUnits[i].type == 1  || selectedUnits[i].type == 6 || selectedUnits[i].type == 7) {
						numOfMobileUnitSelected++;
						groupCenterX += selectedUnits[i].centre.x;
						groupCenterY += selectedUnits[i].centre.z;
					}
				}
			}
		}
		
		groupCenterX/=numOfMobileUnitSelected;
		groupCenterY/=numOfMobileUnitSelected;
		
		directionX = x - groupCenterX;
		directionY = y - groupCenterY;
		
		float innerCircleRadius = (float)numOfMobileUnitSelected/8;
		boolean clickInsideGroup = directionX*directionX + directionY*directionY >  innerCircleRadius/2;
		
		
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0 && selectedUnits[i].type != 2 && selectedUnits[i].type != 3 && selectedUnits[i].type < 100){  //not harvesters or MCVs or any buildings
					
					float distance_x = selectedUnits[i].centre.x- groupCenterX;
					float distance_y = selectedUnits[i].centre.z - groupCenterY;
					
					if(distance_x*distance_x + distance_y*distance_y < innerCircleRadius && clickInsideGroup) {	
						selectedUnits[i].attackMoveTo(selectedUnits[i].centre.x + directionX, selectedUnits[i].centre.z + directionY); 
						
					}else {
						selectedUnits[i].attackMoveTo(x, y); 
					}
					
					selectedUnits[i].currentCommand = solidObject.attackMove;
					selectedUnits[i].secondaryCommand = solidObject.attackMove;
					
					mobileUnitSelected = true;
				}
			}
		}
		
		//draw attack move confirmation if a mobile unit is given attack  move order
		if(mobileUnitSelected){
			theAssetManager.confirmationIconInfo[0] = 1;
			theAssetManager.confirmationIconInfo[1] = x;
			theAssetManager.confirmationIconInfo[2] = y;
			theAssetManager.confirmationIconInfo[3] = 0xcc2222;
			SoundEngine.play(Sfx.ATTACK);
		}
				
	}
	
	public void addMouseHoverUnitToDisplayInfo(Rectangle unitArea, Rectangle unitAreaSmall){
		solidObject theSelected = null;
		int theSelectedIndex = -1;
		mouseOverSelectableUnit = false;
		mouseOverUnitIsSelected = false;
		mouseOverUnitCanDeploy = false;
		for(int i = 0; i < theAssetManager.visibleUnitCount; i++){
			if(unitArea.contains(theAssetManager.visibleUnit[i].tempCentre.screenX,  theAssetManager.visibleUnit[i].tempCentre.screenY)){
				if((theAssetManager.visibleUnit[i].type < 100 || theAssetManager.visibleUnit[i].type >= 199) && !unitAreaSmall.contains(theAssetManager.visibleUnit[i].tempCentre.screenX,  theAssetManager.visibleUnit[i].tempCentre.screenY))
					continue;

				if(theAssetManager.visibleUnit[i].type < 100 || theAssetManager.visibleUnit[i].type >= 199){
					theSelected = theAssetManager.visibleUnit[i];
					theSelectedIndex = i;
					break;
				}

				theSelected = theAssetManager.visibleUnit[i];
				theSelectedIndex = i;
			}
		}

		if(theSelected != null  && theSelected.isSelectable && !cursorIsInMiniMap() && !cursorIsInSideBar()) {
			mouseOverSelectableUnit = true;
			mouseOverUnitType = theSelected.type;
			mouseOverUnitTeam = theSelected.teamNo;
			if(theSelected.isSelected) {
				mouseOverUnitIsSelected = true;
				if(theSelected.type == 3 && ((constructionVehicle)theSelected).canBeDeployed()){
					mouseOverUnitCanDeploy = true;
				}
			}

			//show the health bar/group/progress block for the hovered unit too, even if it isn't
			//selected - reuses the same "treat as selected for display" bit the draw loop checks,
			//restoring the original hover-preview behaviour within the new all-visible-units data flow
			if(!theSelected.isSelected && theSelectedIndex >= 0 && theSelectedIndex < theAssetManager.allUnitsInfo.length){
				theAssetManager.allUnitsInfo[theSelectedIndex][0] |= (1<<24);
			}
		}

	}
	
	public void selectUnit(Rectangle unitArea, Rectangle unitAreaSmall){
		
		
		solidObject theSelected = null;
		
		for(int i = 0; i < theAssetManager.visibleUnitCount; i++){
			if(unitArea.contains(theAssetManager.visibleUnit[i].tempCentre.screenX,  theAssetManager.visibleUnit[i].tempCentre.screenY)){
				if((theAssetManager.visibleUnit[i].type < 100 || theAssetManager.visibleUnit[i].type >= 199) && !unitAreaSmall.contains(theAssetManager.visibleUnit[i].tempCentre.screenX,  theAssetManager.visibleUnit[i].tempCentre.screenY))
					continue;
				
				if(theAssetManager.visibleUnit[i].type < 100 || theAssetManager.visibleUnit[i].type >= 199){
					theSelected = theAssetManager.visibleUnit[i];
					break;
				}
				
				theSelected = theAssetManager.visibleUnit[i];	
			}
		}
		
		if(theSelected != null){

			//clicking your own already-selected, ready-to-deploy construction vehicle deploys it
			//into a construction yard instead of just re-selecting it
			if(theSelected.type == 3 && theSelected.isSelected && ((constructionVehicle)theSelected).canBeDeployed()){
				((constructionVehicle)theSelected).expand();
				return;
			}

			if(!controlKeyPressed)
				deSelectAll();
			
			if(theSelected.isSelected && controlKeyPressed && !doubleClicked){
				deSelect(theSelected);
				return;
			}
			
			addToSelection(theSelected);
			theSelected.isSelected = true;
			SoundEngine.play(Sfx.SELECT);

			if(doubleClicked){
				int type = theSelected.type;
				for(int j = 0; j < theAssetManager.visibleUnitCount; j++){
					if(theAssetManager.visibleUnit[j] != theSelected && theAssetManager.visibleUnit[j].type == type && theAssetManager.visibleUnit[j].teamNo == 0){
						
						addToSelection(theAssetManager.visibleUnit[j]);
						theAssetManager.visibleUnit[j].isSelected = true;
					}
						
				}
				
				doubleClicked = false;
				doubleClickCountDown = 0;
			}
		}else {
			deSelectAll();
		}
		
	}
	
	public void selectMultipleUnits(Rectangle area){
		
		boolean unitIsSelected = false;;
		for(int i = 0; i < theAssetManager.visibleUnitCount; i++){
			if(theAssetManager.visibleUnit[i].teamNo == 0 && area.contains(theAssetManager.visibleUnit[i].tempCentre.screenX,  theAssetManager.visibleUnit[i].tempCentre.screenY)){
				if(!unitIsSelected){
					if(!controlKeyPressed)
						deSelectAll();
					unitIsSelected = true;
				}
				addToSelection(theAssetManager.visibleUnit[i]);
				theAssetManager.visibleUnit[i].isSelected = true;
			}
		}
		if(unitIsSelected)
			SoundEngine.play(Sfx.SELECT);
	}

	public void addToSelection(solidObject o){
		//dont add gold mine to select units
		//if(o.type == 103)
		//	return;
		
		if(!o.isSelectable) 
			return;
		
		deSelect(o);
		for(int i = 0; i < 100; i++){
			if(selectedUnits[i] == null){
				selectedUnits[i] = o;
				o.isSelected = true;
				if(o.teamNo == 0)
					numberOfSelectedUnits++;
				break;
			}
		}
	}
	
	public void deSelect(solidObject o){
		for(int i = 0; i < 100; i++){
			if(selectedUnits[i] == o){
				selectedUnits[i].isSelected = false;
				selectedUnits[i] = null;
				numberOfSelectedUnits--;
				break;
			}
		}
	}
	
	public void deSelectAll(){
		for(int i = 0; i < 100; i++){
			if(selectedUnits[i] != null){
				selectedUnits[i].isSelected = false;
				selectedUnits[i] = null;
			}
			
			
			
		}
		numberOfSelectedUnits = 0;
	}
	
	public void selectGroup(int groupNo){
		for(int i = 0; i < 100; i++){
			selectedUnits[i] = groups[groupNo][i];
		}
	}
	
	public void maneuverUnit(){
		
		
		float x = clickPoint.x;
		float y = clickPoint.z;
	
		//if click outside map boundary, move units to the closest valid tile 
		if(x < 0.25 || x > 31.75 || y < 0.25 || y > 31.75){
			if( x <  0.25)
				x = 0.125f;
			if(x > 31.75)
				x = 31.885f;
			if(y < 0.25)
				y = 0.125f;
			if(y > 31.75)
				y = 31.885f;
			moveSelectedUnit(x,y);
			return;
		}
		
		//find out if the click point lands on a friend unit or an enemy unit or empty ground
		int xPos = (int)(clickPoint.x*64);
		int yPos = (int)(clickPoint.z*64);
		int index = xPos/16 + (127-yPos/16)*128;
		
		for(int i = 0; i < mainThread.gridMap.tiles[index].length; i++){
			if(mainThread.gridMap.tiles[index][i] != null){
				if(mainThread.gridMap.tiles[index][i].boundary2D.contains(xPos, yPos)){
					//handle right click on a gold mine
					if(mainThread.gridMap.tiles[index][i].type == 103){
						harvestMine(mainThread.gridMap.tiles[index][i]);
						return;
					}else if(mainThread.gridMap.tiles[index][i].type == 102 && mainThread.gridMap.tiles[index][i].teamNo == 0){
						returnToRefinery(mainThread.gridMap.tiles[index][i]);
						
						return;
					}else if(mainThread.gridMap.tiles[index][i].teamNo != 0 && mainThread.gridMap.tiles[index][i].visible_minimap && !cursorIsInMiniMap()){
						//the enemy is only clickable if its visible in minimap
						attackUnit(mainThread.gridMap.tiles[index][i]);
						return;
					}
					
				}
			}
		}
		
		moveSelectedUnit(x,y);
		
	}
	
	public void attackUnit(solidObject o){
		if(o.isCloaked && o.teamNo != 0)
			return;
			
		boolean combatUnitSelected = false;
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null && selectedUnits[i].teamNo != -1){  // make sure it won't target gold mine
				if(selectedUnits[i].teamNo == 0 && selectedUnits[i] != o && selectedUnits[i].type != 2 && selectedUnits[i].type != 3 &&  (selectedUnits[i].type < 100 || selectedUnits[i].type >=199)){  //can't attack self
					selectedUnits[i].attack(o); 
					if(numberOfSelectedUnits <= 4)
						selectedUnits[i].currentCommand = solidObject.attackCautiously;
					else
						selectedUnits[i].currentCommand = solidObject.attackInNumbers; 
					selectedUnits[i].secondaryCommand = solidObject.StandBy;
					
					combatUnitSelected = true;
				}
			}
		}
		
		//draw attack  confirmation if a combat  unit is given attack   order
		if(combatUnitSelected){
			theAssetManager.confirmationIconInfo[0] = 1;
			theAssetManager.confirmationIconInfo[1] = o.centre.x;
			theAssetManager.confirmationIconInfo[2] = o.centre.z;
			theAssetManager.confirmationIconInfo[3] = 0xcc2222;
			SoundEngine.play(Sfx.ATTACK);
		}
		
	}
	
	public void harvestMine(solidObject o){
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0 && (selectedUnits[i].type  == 2 || selectedUnits[i].type  == 105)){  //must be a harvester/factory to perform such a move
					selectedUnits[i].harvest(o);
					theAssetManager.confirmationIconInfo[0] = 1;
					theAssetManager.confirmationIconInfo[1] = o.centre.x;
					theAssetManager.confirmationIconInfo[2] = o.centre.z;
					theAssetManager.confirmationIconInfo[3] = 0xbbbb00;
					SoundEngine.play(Sfx.HARVEST);
				}
			}
		}
	}
	
	public void returnToRefinery(solidObject o){
		for(int i = 0; i < selectedUnits.length; i++){
			if(selectedUnits[i] != null){
				if(selectedUnits[i].teamNo == 0 && selectedUnits[i].type  == 2){  //must be a harvester to perform such a move
					selectedUnits[i].returnToRefinery(o);
					theAssetManager.confirmationIconInfo[0] = 1;
					theAssetManager.confirmationIconInfo[1] = o.centre.x;
					theAssetManager.confirmationIconInfo[2] = o.centre.z;
					theAssetManager.confirmationIconInfo[3] = 0xbbbb00;
					SoundEngine.play(Sfx.HARVEST);
				}
			}
		}
	}
	

	public void removeFromOtherGroup(solidObject o, int groupNumber){
		for(int i = 0; i < groups.length; i++){
			if(i != groupNumber){
				for(int j = 0; j < groups[i].length; j++){
					if(groups[i][j] == o){
						groups[i][j] = null;
						break;
					}
				}
			}
		}
	}
	
	
}


