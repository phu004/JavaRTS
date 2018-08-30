package enemyAI;

import core.baseInfo;
import core.mainThread;
import core.vector;
import entity.goldMine;
import entity.solidObject;
import entity.techCenter;

//this agent makes all the high level combat decisions
public class combatManagerAI {
	public baseInfo theBaseInfo;
	
	public int frameAI;
	
	public int currentState;
	public final int booming = 0;
	public final int aggressing = 1;
	public final int defending = 2;

	
	public goldMine[] goldMines; 
	
	float distanceToTarget;
	
	public vector gatherPoint, attackDirection, attackPosition;
	
	
	public solidObject unNeutralizedEntity;

	public solidObject[] team;
	
	public int numberOfUnitInCombatRadius;
	public int numberOfUnitOutsideCombatRadius;
	public float unitInCombactRadiusPercentage;
	
	public solidObject[] unitInCombatRadius;
	public solidObject[] unitOutsideCombatRadius;
	public solidObject[] troopsControlledByCombatAI;
	public solidObject[] playerUnitInMinimap;
	public vector playerForceCenter;
	public vector adjustedAttackDirection;
	
	public int withdrawUnitOutsideCombatRadiusCooldown;
	public float maxPlayerForceStrengthRoundAttacker;
	
	public float combatCenterX, combatCenterZ;
	public float myRallyPointX, myRallyPointZ;
	public boolean rallyPointChanged;
	boolean shouldDefenceAggressively;
	
	
	public float offScreenPlayerForceStrength;
	
	
	public combatManagerAI(baseInfo theBaseInfo){
		this.theBaseInfo = theBaseInfo;
	
		
		goldMines = mainThread.theAssetManager.goldMines;
		
		gatherPoint = new vector(-1,-1,-1);
		attackDirection = new vector(0,0,0);
		attackPosition = new vector(0,0,0);
		playerForceCenter = new vector(0,0,0);
		adjustedAttackDirection = new vector(0,0,0);
		
	}
	
	//check if player has expansions
	//if player has expansion(s) then find the one that is least defended
	//compare player force with its own force
	//if AI thinks its has a comparable or greater force than the player, attack player's expansion and switch to aggressing mode
	//if AI is under significant threat, switch to defending mode
	
	public void processAI(){
		frameAI++;
		
		//assume player force gets stronger as time goes by
		if(offScreenPlayerForceStrength < 0)
			offScreenPlayerForceStrength = 0;
		if(frameAI > 150)
			offScreenPlayerForceStrength+=0.075f;
		
		
		//checkIfAIHasBiggerForce(1);
		
		if(withdrawUnitOutsideCombatRadiusCooldown > 0){
			withdrawUnitOutsideCombatRadiusCooldown --;
		}else{
			maxPlayerForceStrengthRoundAttacker = 0;
		}
		
		team = mainThread.ec.theUnitProductionAI.troopsControlledByCombatAI;
		numberOfUnitInCombatRadius = mainThread.ec.theUnitProductionAI.numberOfUnitInCombatRadius;
		numberOfUnitOutsideCombatRadius = mainThread.ec.theUnitProductionAI.numberOfUnitOutsideCombatRadius;
		unitInCombatRadius = mainThread.ec.theUnitProductionAI.unitInCombatRadius;
		unitOutsideCombatRadius = mainThread.ec.theUnitProductionAI.unitOutsideCombatRadius;
		troopsControlledByCombatAI = mainThread.ec.theUnitProductionAI.troopsControlledByCombatAI;
		playerUnitInMinimap = mainThread.ec.theMapAwarenessAI.playerUnitInMinimap;
		shouldDefenceAggressively  = checkIfAIHasBiggerForce(1.6f);
	
		
		
		combatCenterX = mainThread.ec.theUnitProductionAI.combatAICenterX;
		combatCenterZ = mainThread.ec.theUnitProductionAI.combatAICenterZ;
		
		if(Float.isNaN(combatCenterX) || Float.isNaN(combatCenterZ)) {
		//	combatCenterX = 0;
		//	combatCenterZ= 0;
		}
		
	
		boolean frontalTroopIverwhelmed = false;
		boolean shouldAttack = false;
		boolean playerHasBecomeStrongerThanAIDuringMarching = false;
		
		
		rallyPointChanged = false;
		if(myRallyPointX != mainThread.ec.theUnitProductionAI.rallyPoint.x){
			myRallyPointX = mainThread.ec.theUnitProductionAI.rallyPoint.x;
			myRallyPointZ = mainThread.ec.theUnitProductionAI.rallyPoint.z;
			rallyPointChanged = true;
		}
		
		
		if(currentState == booming){
			
			//enemy AI compares its own  force with player's force, then make a decision whether it should attack or not 
			//only start comparing after 6 minutes mark, or player is rushing with light tank delay attack mark to around 10 minutes mark
			int attackTime = 360;
			if(mainThread.ec.theMapAwarenessAI.playerIsRushingLightTank)
				attackTime = 540;
			else
				attackTime = 360;
			
			int targetPlayerExpension = mainThread.ec.theMapAwarenessAI.targetPlayerExpension;
			
			if(frameAI > attackTime) {
				if(targetPlayerExpension == 2 || targetPlayerExpension == 4 || targetPlayerExpension == 6 || targetPlayerExpension == 7)
					shouldAttack = checkIfAIHasBiggerForce(1.2f);
				else
					shouldAttack = checkIfAIHasBiggerForce(0.9f);
			}
			
			
			if(shouldAttack){
				if(targetPlayerExpension != -1){
					currentState = aggressing;
					attackDirection.set(goldMines[targetPlayerExpension].centre.x - combatCenterX, 0, goldMines[targetPlayerExpension].centre.z - combatCenterZ);
					attackDirection.unit();
					attackPosition.set(goldMines[targetPlayerExpension].centre);
					attackPosition.add(attackDirection);
					return;
				}else{
					//if no enemy structure found around gold mines, set attack position to a revealed enemy building or unit
					solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
					solidObject[] playerUnitInMinimap = mainThread.ec.theMapAwarenessAI.playerUnitInMinimap;
					attackDirection.set(0,0,0);
					for(int i = 0; i < playerStructures.length; i++){
						if(playerStructures[i] != null && playerStructures[i].currentHP > 0){
							currentState = aggressing;
							attackDirection.set(playerStructures[i].centre.x - combatCenterX, 0, playerStructures[i].centre.z - combatCenterZ);
							attackDirection.unit();
							attackPosition.set(playerStructures[i].centre);
							attackPosition.add(attackDirection);
							return;
						}
					}
					
					if(attackDirection.x == 0 && attackDirection.z ==0){
						for(int i = 0; i < playerUnitInMinimap.length; i++){
							if(playerUnitInMinimap[i] != null && playerUnitInMinimap[i].currentHP > 0){
								currentState = aggressing;
								attackDirection.set(playerUnitInMinimap[i].centre.x - combatCenterX, 0, playerUnitInMinimap[i].centre.z - combatCenterZ);
								attackDirection.unit();
								attackPosition.set(playerUnitInMinimap[i].centre);
								attackPosition.add(attackDirection);
								return;
							}
						}
					}
					
				
					//if AI couldn't find a player base nor player's unit then set the attack position to the player spawning point 
					currentState = aggressing;
					attackDirection.set(goldMines[0].centre.x - combatCenterX, 0, goldMines[0].centre.z - combatCenterZ);
					attackDirection.unit();
					attackPosition.set(goldMines[0].centre);
					attackPosition.add(attackDirection);
					
				}
			}else {
				
				//check if there are any player units/structures near the combat center
				solidObject[] playerUnitInMinimap = mainThread.ec.theMapAwarenessAI.playerUnitInMinimap;
				solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
				
				for(int i = 0; i < playerUnitInMinimap.length; i++) {
					if(playerUnitInMinimap[i] != null && playerUnitInMinimap[i].currentHP > 0) {
						double d = Math.sqrt((combatCenterX - playerUnitInMinimap[i].centre.x)*(combatCenterX - playerUnitInMinimap[i].centre.x) + (combatCenterZ - playerUnitInMinimap[i].centre.z)*(combatCenterZ - playerUnitInMinimap[i].centre.z));
						if(d < 5){
							currentState = aggressing;
							attackDirection.set(playerUnitInMinimap[i].centre.x - combatCenterX, 0, playerUnitInMinimap[i].centre.z - combatCenterZ);
							attackDirection.unit();
							attackPosition.set(playerUnitInMinimap[i].centre);
							
							if(shouldDefenceAggressively)
								attackPosition.add(attackDirection);
							
							return;
						}
					}
				}
			}
			
			if(currentState != aggressing) {
				if(rallyPointChanged || Math.abs(myRallyPointX - combatCenterX) > 1 || Math.abs(myRallyPointZ - combatCenterZ) > 1){
					for(int i =0 ; i < troopsControlledByCombatAI.length; i++){
						if(troopsControlledByCombatAI[i] != null && troopsControlledByCombatAI[i].currentHP > 0){
							troopsControlledByCombatAI[i].attackMoveTo(myRallyPointX, myRallyPointZ);
							troopsControlledByCombatAI[i].currentCommand = solidObject.attackMove;
							troopsControlledByCombatAI[i].secondaryCommand = solidObject.attackMove;
						}
					}
				}
			}
		}else if(currentState == aggressing){
			
			
			attackDirection.set(attackPosition.x - combatCenterX, 0, attackPosition.z - combatCenterZ);
			distanceToTarget = attackDirection.getLength();
			attackDirection.unit();
	
			//check if the target position has been neutralized
			solidObject[] playerUnitInMinimap = mainThread.ec.theMapAwarenessAI.playerUnitInMinimap;
			solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
			unNeutralizedEntity = null;
			
			//look for revealed player building structures
			for(int i = 0; i < playerStructures.length; i++){
				if(playerStructures[i] != null){
					if((playerStructures[i].centre.x - attackPosition.x)*(playerStructures[i].centre.x - attackPosition.x) + (playerStructures[i].centre.z - attackPosition.z)*(playerStructures[i].centre.z - attackPosition.z) < 16){
						unNeutralizedEntity = playerStructures[i];
						attackPosition.set(playerStructures[i].centre);
						break;
					}
				}
			}
			
			//if there is no player structure found, then look for player units
			boolean needResetAttackPosition = false;
			if(unNeutralizedEntity == null){
				for(int i = 0; i < playerUnitInMinimap.length; i++){
					if(playerUnitInMinimap[i] != null){
						unNeutralizedEntity = playerUnitInMinimap[i];
						needResetAttackPosition = true;
						if((playerUnitInMinimap[i].centre.x - attackPosition.x)*(playerUnitInMinimap[i].centre.x - attackPosition.x) + (playerUnitInMinimap[i].centre.z - attackPosition.z)*(playerUnitInMinimap[i].centre.z - attackPosition.z) < 16){
							needResetAttackPosition = false;
							break;
						}
					}
				}
			}
			if(needResetAttackPosition) {
				attackPosition.set(unNeutralizedEntity.centre);
			}
			
			
			//if front portion of the  troops are under attack, set the attack position to the attacker
			for(int i = 0; i < numberOfUnitInCombatRadius; i++){
				if(unitInCombatRadius[i].underAttackCountDown > 0){
					attackPosition.set(unitInCombatRadius[i].attacker.centre);
					unNeutralizedEntity = unitInCombatRadius[i].attacker;
					break;
				}
			}
			
			//check if the front portion of the troops are overwhelmed by player force
			float playerForceStrengthNearCombatCenter = checkPlayerForceStrengthAroundOnePoint(playerUnitInMinimap, combatCenterX +  attackDirection.x, combatCenterZ + attackDirection.z, 4);
			if((playerForceStrengthNearCombatCenter+1)/(getAIForceStrength(unitInCombatRadius)+1) > 2f){
				frontalTroopIverwhelmed = true;
			}
			
			
			
			//check if the player force has become stronger than the AI during the marching towards attack position
			//System.out.println("distanceToTarget: "  + distanceToTarget);
			if(checkIfAIHasBiggerForce(1) == false && distanceToTarget > 5){
				playerHasBecomeStrongerThanAIDuringMarching = true;
			}
			
			
			
			//check if the troops has encountered a concentration of enemy static defense
			
			

			//send units to attack-move to target position
			if(!playerHasBecomeStrongerThanAIDuringMarching && !frontalTroopIverwhelmed && (unNeutralizedEntity != null  || distanceToTarget > 2)){
				
				//if the tail portion of the troops are under attack, send them back to base instead of throwing them into player's trap
				float AIForceStrengthOutsideCombatRadius = getAIForceStrength(unitOutsideCombatRadius);
				maxPlayerForceStrengthRoundAttacker = 0;
				for(int i = 0; i < numberOfUnitOutsideCombatRadius; i++){
					if(unitOutsideCombatRadius[i].underAttackCountDown > 0 && unitOutsideCombatRadius[i].attacker != null){
						//check the number of hostile units around the attacker
						playerForceStrengthNearCombatCenter = checkPlayerForceStrengthAroundOnePoint(playerUnitInMinimap, unitOutsideCombatRadius[i].attacker.centre.x, unitOutsideCombatRadius[i].attacker.centre.z, 4);
						if(playerForceStrengthNearCombatCenter > maxPlayerForceStrengthRoundAttacker)
							maxPlayerForceStrengthRoundAttacker = playerForceStrengthNearCombatCenter;
						
					}
					
					//check if there are too many hostile units
					if(maxPlayerForceStrengthRoundAttacker > AIForceStrengthOutsideCombatRadius){
						if(withdrawUnitOutsideCombatRadiusCooldown == 0)
							withdrawUnitOutsideCombatRadiusCooldown = 30;
						break;
					}
				}
			
				if(withdrawUnitOutsideCombatRadiusCooldown > 0){
					team  = unitInCombatRadius; //exclude the tail portion of the troops from the attack force
					for(int i = 0; i < troopsControlledByCombatAI.length; i++){
						//send the tail portion of the troops to rally point
						if(troopsControlledByCombatAI[i] != null && troopsControlledByCombatAI[i].currentHP > 0){
							troopsControlledByCombatAI[i].attackMoveTo(mainThread.ec.theUnitProductionAI.rallyPoint.x, mainThread.ec.theUnitProductionAI.rallyPoint.z);
							troopsControlledByCombatAI[i].currentCommand = solidObject.attackMove;
							troopsControlledByCombatAI[i].secondaryCommand = solidObject.attackMove;
						}
					}
				}
				
				
				if(unNeutralizedEntity != null){
					attackDirection.set(unNeutralizedEntity.centre.x - combatCenterX, 0, unNeutralizedEntity.centre.z - combatCenterZ);
					distanceToTarget = attackDirection.getLength();
					attackDirection.unit();
				}
				everyoneAttackTargetPosition();
				
			}else{
				//if target position has been neutralized, change status to booming
				currentState = booming;
				
				
				float gatherPointX, gatherPointZ;
				
				//if the change of state is caused by heavy causality of the AI player then move move every unit back to rally point
				if(frontalTroopIverwhelmed || playerHasBecomeStrongerThanAIDuringMarching){
					gatherPointX = mainThread.ec.theUnitProductionAI.rallyPoint.x;
					gatherPointZ = mainThread.ec.theUnitProductionAI.rallyPoint.z;
				}else {
					//if the AI really couldn't find any targets then gather every units at the current combat center
					gatherPointX = combatCenterX;
					gatherPointZ = combatCenterZ;
					
				}
				
				for(int i = 0; i < numberOfUnitOutsideCombatRadius; i++){
					//send the tail portion of the troops to rally point
					unitOutsideCombatRadius[i].attackMoveTo(gatherPointX, gatherPointZ);
					unitOutsideCombatRadius[i].currentCommand = solidObject.attackMove;
					unitOutsideCombatRadius[i].secondaryCommand = solidObject.attackMove;
				}
	
				for(int i = 0; i < numberOfUnitInCombatRadius; i++){
					//send the tail portion of the troops to rally point
					if(unitInCombatRadius[i].attackStatus != solidObject.isAttacking){
						unitInCombatRadius[i].attackMoveTo(gatherPointX, gatherPointZ);
						unitInCombatRadius[i].currentCommand = solidObject.attackMove;
						unitInCombatRadius[i].secondaryCommand = solidObject.attackMove;
					}
				}
			}
		}
	}
	
	
	//attack a target location with all the forces
	public void everyoneAttackTargetPosition(){		
		//Find a gather location for an  attack. The attacking units will first try to gather around a location that in between the target location and the attack force's center point.  
		//When the majority attacking units arrive around the gather point, the attack will proceed towards the target location. 
		//The purpose of doing this is to keep the attack units together when across long distance,
		
		float teamRadius = (float)Math.sqrt(mainThread.ec.theUnitProductionAI.numberOfCombatUnit)/2.5f;
		
		if(distanceToTarget < 3 + teamRadius  && unNeutralizedEntity != null){
			//adjust the attack location for better engagement
			playerForceCenter.set(0,0,0);
			int numOfPlayerUnitsInMinimap = 0;
			for(int i = 0; i < playerUnitInMinimap.length; i++){
				if(playerUnitInMinimap[i] != null && playerUnitInMinimap[i].currentHP > 0){
					float playerCenterX = playerUnitInMinimap[i].centre.x;
					float playerCenterZ = playerUnitInMinimap[i].centre.z;
					
					if((playerCenterX - combatCenterX)*(playerCenterX - combatCenterX) + (playerCenterZ - combatCenterZ)*(playerCenterZ - combatCenterZ) < 9){
						playerForceCenter.x+=playerCenterX;
						playerForceCenter.z+=playerCenterZ;
						numOfPlayerUnitsInMinimap++;
					}
				}
			}
			
			if(numOfPlayerUnitsInMinimap >0){
				playerForceCenter.x/=numOfPlayerUnitsInMinimap;
				playerForceCenter.z/=numOfPlayerUnitsInMinimap;
				
			}else{
				playerForceCenter.x= attackPosition.x;
				playerForceCenter.z= attackPosition.z;
			}
			
			
			
			adjustedAttackDirection.set(playerForceCenter.x - combatCenterX, 0, playerForceCenter.z - combatCenterZ);
			adjustedAttackDirection.unit();
			adjustedAttackDirection.scale(20);
			
			for(int i = 0; i < mainThread.ec.theUnitProductionAI.numberOfCombatUnit; i++){
				if(team[i] != null && team[i].currentHP > 0){
					if(!((team[i].secondaryDestinationX == attackPosition.x && team[i].secondaryDestinationY ==  attackPosition.z)
						|| (team[i].secondaryDestinationX ==  unNeutralizedEntity.centre.x && team[i].secondaryDestinationY ==  unNeutralizedEntity.centre.z))){
						
						if(team[i].attackStatus != solidObject.isAttacking){
						
							team[i].attackMoveTo(playerForceCenter.x + adjustedAttackDirection.x, playerForceCenter.z + adjustedAttackDirection.z); 
							team[i].currentCommand = solidObject.attackMove;
							team[i].secondaryCommand = solidObject.attackMove;
							
						}
						
						team[i].secondaryDestinationX = attackPosition.x;
						team[i].secondaryDestinationY = attackPosition.z;	
					}
				}
			}
			
		}else{
			for(int i = 0; i < 3; i++){
				gatherPoint.set(combatCenterX + attackDirection.x*(teamRadius+1*i), 0, combatCenterZ + attackDirection.z*(teamRadius+ 1*i));
				//if the gather point is inside a water body them move the gather point forward
				int tileIndex = (int)(gatherPoint.x*64)/16 + (127 - ((int)(gatherPoint.z*64))/16)*128;
				if(tileIndex >= mainThread.gridMap.tiles.length || tileIndex < 0)
					break;
				if(mainThread.gridMap.tiles[tileIndex][0] != null && mainThread.gridMap.tiles[tileIndex][0].type == 4)
					continue;
				else
					break;
			}
			
			

			for(int i = 0; i < mainThread.ec.theUnitProductionAI.numberOfCombatUnit; i++){
				if(team[i] != null && team[i].currentHP > 0){
					//stop chasing player unit if it has got out of sight 
					if(team[i].targetObject != null && team[i].targetObject.currentHP >0) {
						int targetPositionIndex = (int)(team[i].targetObject.centre.x*64)/16 + (127 - (int)(team[i].targetObject.centre.z*64)/16)*128;
						
						if(team[i].attackStatus != solidObject.isAttacking && team[i].underAttackCountDown  == 0 && (!mainThread.ec.visionMap[targetPositionIndex] || team[i].targetObject.isCloaked))
							team[i].targetObject = null;
					}
					
					//marching forward
					if((team[i].targetObject == null || team[i].targetObject.currentHP <=0) && !(team[i].currentMovementStatus ==  solidObject.hugRight || team[i].currentMovementStatus == solidObject.hugLeft)){
				
						double d = Math.sqrt((team[i].centre.x -  combatCenterX)*(team[i].centre.x -  combatCenterX) + (team[i].centre.z -  combatCenterZ)*(team[i].centre.z -  combatCenterZ))*3;
						
						if(d > teamRadius){
							team[i].attackMoveTo(gatherPoint.x, gatherPoint.z); 
						
						}else{
					
							team[i].attackMoveTo(team[i].centre.x + attackDirection.x*teamRadius, team[i].centre.z + attackDirection.z*teamRadius); 
							
						}

						team[i].currentCommand = solidObject.attackMove;
						team[i].secondaryCommand = solidObject.attackMove;
						
					}	
				}
			}
		}
		
		//make sure idle units are send to attack unNeutralized target
		if(unNeutralizedEntity  != null){
			for(int i = 0; i < mainThread.ec.theUnitProductionAI.numberOfCombatUnit; i++){
				if(team[i] != null && team[i].currentHP > 0){
					if(team[i].currentCommand == solidObject.StandBy || (team[i].targetObject == null && (team[i].secondaryDestinationX != unNeutralizedEntity.centre.x || team[i].secondaryDestinationY != unNeutralizedEntity.centre.z))){
						float d = (team[i].centre.x - attackPosition.x)*(team[i].centre.x - attackPosition.x) + (team[i].centre.z - attackPosition.z)*(team[i].centre.z - attackPosition.z);
						if(d < 9){
							team[i].attackMoveTo(unNeutralizedEntity.centre.x, unNeutralizedEntity.centre.z);  
							team[i].currentCommand = solidObject.attackMove;
							team[i].secondaryCommand = solidObject.attackMove;
							
						}
					}
				}
			}
		}
		
	}
	
	public float checkPlayerForceStrengthAroundOnePoint(solidObject[] listOfUnits, float x, float z, double distanceThreshold){
		float playerForceStrength = 0;
		for(int j = 0; j < listOfUnits.length; j++){
			
			if(listOfUnits[j] != null && listOfUnits[j].currentHP > 0){
				double d = (listOfUnits[j].centre.x - x)*(listOfUnits[j].centre.x - x) 
						 + (listOfUnits[j].centre.z - z)*(listOfUnits[j].centre.z - z);
				if(d < distanceThreshold){
					if(listOfUnits[j].type == 0 || listOfUnits[j].type == 1)
						playerForceStrength+=1;
					else if(listOfUnits[j].type == 6)
						playerForceStrength+=1.5f;
					else if(listOfUnits[j].type == 7)
						playerForceStrength+=3;
				}
			}
		}
		return playerForceStrength;
	}
	
	public float getAIForceStrength(solidObject[] listOfUnits){
		float AIForceStrength = 0;
		for(int j = 0; j < listOfUnits.length; j++){
			if(listOfUnits[j] != null && listOfUnits[j].currentHP > 0){
				if(listOfUnits[j].type == 0 || listOfUnits[j].type == 1)
					AIForceStrength+=1;
				else if(listOfUnits[j].type == 6)
					AIForceStrength+=1.5f;
				else if(listOfUnits[j].type == 7)
					AIForceStrength+=3;
			}
		}
		return AIForceStrength;
	}
	
	public boolean checkIfAIHasBiggerForce(float ratio){
		
		int numberOfLightTanks_AI = mainThread.ec.theUnitProductionAI.numberOfLightTanksControlledByCombatAI;
		int numberOfRocketTanks_AI = mainThread.ec.theUnitProductionAI.numberOfRocketTanksControlledByCombatAI;
		int numberOfStealthTanks_AI = mainThread.ec.theUnitProductionAI.numberOfStealthTanksControlledByCombatAI;
		int numberOfHeavyTanks_AI = mainThread.ec.theUnitProductionAI.numberOfHeavyTanksControlledByCombatAI;
				
		int numberOfLightTanks_player = mainThread.ec.theMapAwarenessAI.numberOfLightTanks_player;
		int numberOfRocketTanks_player = mainThread.ec.theMapAwarenessAI.numberOfRocketTanks_player;
		int numberOfStealthTanks_player = mainThread.ec.theMapAwarenessAI.numberOfStealthTanks_player;
		int numberOfHeavyTanks_player = mainThread.ec.theMapAwarenessAI.numberOfHeavyTanks_player;
		
		float m3 = 1.5f;
		
		if(techCenter.stealthTankResearched_enemy == true && numberOfHeavyTanks_player < 4  && numberOfStealthTanks_AI  > numberOfStealthTanks_player * 2)
			m3+=0.5f;
			
		
		if(techCenter.stealthTankResearched_enemy == true && mainThread.ec.theMapAwarenessAI.playerHasMostlyLightTanks){
			m3+=0.5f;
		}
		
		float m1 = 1;
		
	
		
		if(mainThread.ec.theMapAwarenessAI.playerHasMostlyHeavyTanks){
			m1 = 1.2f;
			
			if(techCenter.lightTankResearched_enemy == true && numberOfStealthTanks_player < 5){
				m1=1.75f;
				
			}
		}
		
		
		double enemyAIForceStrength = m1*numberOfLightTanks_AI + 0.75f*numberOfRocketTanks_AI + m3*(numberOfStealthTanks_AI-mainThread.ec.theBaseExpentionAI.numberOfStealthTankScout) +  3* numberOfHeavyTanks_AI;
		
		double playerForceStrength = offScreenPlayerForceStrength + numberOfLightTanks_player + 0.75f*numberOfRocketTanks_player + 1.5*numberOfStealthTanks_player +  3* numberOfHeavyTanks_player;
		
		//System.out.println("offScreenPlayerForceStrength" + offScreenPlayerForceStrength +  "    "  + "enemyAIForceStrength " + enemyAIForceStrength + "    "  + "playerForceStrength" + playerForceStrength);
		
		return enemyAIForceStrength > 0 && playerForceStrength/enemyAIForceStrength < ratio;
			
		
		
	}
	
	
}