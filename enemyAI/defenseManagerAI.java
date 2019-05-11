package enemyAI;

import core.baseInfo;
import entity.factory;
import core.mainThread;
import core.vector;
import entity.lightTank;
import entity.solidObject;
import entity.stealthTank;
import entity.communicationCenter;
import entity.constructionYard;
import entity.gunTurret;
import entity.missileTurret;

public class defenseManagerAI {
	public baseInfo theBaseInfo;
	
	public int frameAI;
	
	public int currentState;
	public final int booming = 0;
	public final int aggressing = 1;
	public final int defending = 2;

	public solidObject[] observers;
	 
	public solidObject[] stealthTanksControlledByCombatAI;
	public solidObject[] lightTanksControlledByCombatAI;
	
	public solidObject[] defenders;
	public int numOfDefenders;
	
	public vector direction;
	public vector threatToBaseDirection;
	
	public vector minorThreatLocation;
	public vector majorThreatLocation;
	public int majorThreatCooldown; 
	
	public boolean needGunTurret;
	public boolean needMissileTurret;
	
	public vector gunTurretDeployLocation;
	public vector missileTurretDeployLocation;
	
	
	public defenseManagerAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;
		
		observers = new solidObject[4];
		
		defenders = new solidObject[5];
		
		direction = new vector(0,0,0);
		threatToBaseDirection = new vector(0,0,0);
		
		minorThreatLocation = new vector(0,0,0);
		majorThreatLocation = new vector(0,0,0);
		
		gunTurretDeployLocation = new vector(0,0,0);
		missileTurretDeployLocation = new vector(0,0,0);	
	}
	
	
	//at 500 seconds mark, send 2 observers to the 2 western and southern side of the main base. After grabbing the northwest (or southeast, depends on which expansion get grabbed first) expansion 
	//send 2 additional observers to look for player sneak attacks
	
	
	public void processAI(){
		frameAI = mainThread.ec.frameAI;
		
		
		
		if(majorThreatCooldown > 0)
			majorThreatCooldown --;
		
		currentState = mainThread.ec.theCombatManagerAI.currentState;
		
		stealthTanksControlledByCombatAI = mainThread.ec.theUnitProductionAI.stealthTanksControlledByCombatAI;
		lightTanksControlledByCombatAI = mainThread.ec.theUnitProductionAI.lightTanksControlledByCombatAI;
		
		//after 500 seconds mark, borrow 2 stealth tanks from combat manager, and send them to guard western and southern side of the main base
		if(frameAI >= 450 && mainThread.ec.theCombatManagerAI.checkIfAIHasBiggerForce(0.8f)) {
			for(int i = 0; i < 2; i++) {
				if(observers[i] == null || observers[i].currentHP <=0) {
					for(int j = 0; j < stealthTanksControlledByCombatAI.length; j++) {
						if(stealthTanksControlledByCombatAI[j] != null && stealthTanksControlledByCombatAI[j].currentHP == 80 && stealthTanksControlledByCombatAI[j].attackStatus != solidObject.isAttacking) {
							observers[i] = stealthTanksControlledByCombatAI[j];
							stealthTanksControlledByCombatAI[j] = null;
							float xPos = 20f;
							float zPos = 30.5f;
							
							if(i == 1) {
								xPos = 30f;
								zPos = 20f;
							}
							
							
							if(frameAI > 1000) {
								xPos = 0.25f;
								zPos = 20.5f;
								
								if(i == 1) {
									xPos = 18.75f;
									zPos = 5f;
								}
							}
							
							observers[i].moveTo(xPos, zPos);
							observers[i].currentCommand = solidObject.move;
							observers[i].secondaryCommand = solidObject.StandBy;
							break;
						}
					}
				}
			}
		}
		
		//keep an eye on player units and avoid being detected
		for(int i = 0; i < observers.length; i++) {
			if(observers[i] != null) {
				
				if(!evadePlayerUnit(i)) {
					float xPos = 0;
					float zPos = 0;
					
					//if there is no player units in sight, return to patrol position
					if(i == 0) {
						if(frameAI%28 < 14) {
							xPos = 15.5f;
							zPos = 30.5f;
						}else {
							xPos = 15.5f;
							zPos = 24.5f;
						}
						
						if(frameAI > 1000) {
							if(frameAI%18 < 9) {
								xPos = 0.25f;
								zPos = 20.5f;
							}else {
								xPos = 5f;
							    zPos = 20.5f;
							}
						}
							
					}
					
					if(i == 1) {
						
						if(frameAI%30 < 15) {
							xPos = 29.25f;
							zPos = 17f;
						}else {
							xPos = 29.25f;
							zPos = 10f;
						}
						
						if(frameAI > 1000) {
							if(frameAI%14 < 7) {
								xPos = 18.75f;
								zPos = 5f;
							}else {
								xPos = 18.75f;
								zPos = 0.5f;
							}
						}
						
					}
					observers[i].moveTo(xPos, zPos);
					observers[i].currentCommand = solidObject.move;
					observers[i].secondaryCommand = solidObject.StandBy;
				}
				
			}
		}
		
		//send units to deal with minor threat on the map if there is any
		vector mainPlayerForceLocation = mainThread.ec.theMapAwarenessAI.mainPlayerForceLocation;
		vector mainPlayerForceDirection = mainThread.ec.theMapAwarenessAI.mainPlayerForceDirection;
		int mainPlayerForceSize = mainThread.ec.theMapAwarenessAI.mainPlayerForceSize;
		
		minorThreatLocation.reset();
		if(majorThreatCooldown == 0)
			majorThreatLocation.reset();
		
		// if the size of the player unit cluster is less than 7, and no heavy tanks in the cluster, then borrow some unites from combatAI to deal with the threat
		if(mainPlayerForceSize < 7 && mainPlayerForceSize>0) {
			//check if base is attacked by long ranged units
			boolean attackedByRocketTank = false;
			int numOfHeavyTanks = numOfHeavyTankAroundLocation(mainPlayerForceLocation);
			if(numOfHeavyTanks < 1) {
				for(int i = 0; i < mainThread.ec.theMapAwarenessAI.numOfAIStructures; i++) {
					if(mainThread.ec.theMapAwarenessAI.AIStructures[i].underAttackCountDown > 0 &&
					   mainThread.ec.theMapAwarenessAI.AIStructures[i].attacker != null &&
					   mainThread.ec.theMapAwarenessAI.AIStructures[i].attacker.currentHP > 0 &&
					   mainThread.ec.theMapAwarenessAI.AIStructures[i].attacker.type == 1) {
						attackedByRocketTank = true;
						minorThreatLocation.set(mainThread.ec.theMapAwarenessAI.AIStructures[i].attacker.centre);
						break;
					}
				}
			}
			
			if(!attackedByRocketTank  && playerForceIsNearBase(mainPlayerForceLocation)) {
				if(numOfHeavyTanks < 1)
					minorThreatLocation.set(mainPlayerForceLocation);
				else {
					giveBackControlOfDefendersToCombatAI();
					majorThreatLocation.set(mainPlayerForceLocation);
				}
			}
		}else if(mainPlayerForceSize >= 7){
			//if the size of player unit cluster is bigger or equal to 5 then check if the threat is a big one
			if(playerForceIsNearBase(mainPlayerForceLocation)) {
				giveBackControlOfDefendersToCombatAI();
				majorThreatCooldown = 30;
				majorThreatLocation.set(mainPlayerForceLocation);
			}else {
				float d = playerForceIsMovingTwoardsBase(mainPlayerForceLocation, mainPlayerForceDirection);
				if(d != -1) {
					giveBackControlOfDefendersToCombatAI();
					majorThreatCooldown = 30;
					majorThreatLocation.set(mainPlayerForceLocation);
					majorThreatLocation.add(mainPlayerForceDirection, d);
				}
			}
			
		}
		
		//treat player buildings that is close to the base as major threat too. 
		constructionYard[] constructionYards = mainThread.theAssetManager.constructionYards;
		boolean playerBuildingNearBase = false;
		solidObject[] playerStructures = mainThread.ec.theMapAwarenessAI.playerStructures;
		for(int i = 0; i < playerStructures.length; i++) {
			if(playerStructures[i] != null && playerStructures[i].currentHP > 0) {
				float x1 = playerStructures[i].centre.x;
				float z1 = playerStructures[i].centre.z;
				for(int j = 0; j < constructionYards.length; j++) {
					if(constructionYards[j] != null && constructionYards[j].teamNo != 0 && constructionYards[j].currentHP > 0) {
						float x2 = constructionYards[j].centre.x;
						float z2 = constructionYards[j].centre.z;
						double d = Math.sqrt((x1-x2)*(x1-x2) + (z1-z2)*(z1-z2));
						
						if(d < 4) {
							playerBuildingNearBase = true;
							majorThreatCooldown = 30;
							majorThreatLocation.set(playerStructures[i].centre);
							break;
							
						}
					}
				}
				
				if(playerBuildingNearBase) {
					break;
				}
			}
		}
		
		
		
		
		//take over controls of  defenders from combat AI to deal with minor threat
		if(minorThreatLocation.x != 0 && numOfDefenders > 0 && frameAI > 480) {
			takeOverDefendersFromCombatAI();
			
			//attack move to threat location
			for(int i =0; i < defenders.length; i++) {
				if(defenders[i] != null) {
					defenders[i].moveTo(minorThreatLocation.x, minorThreatLocation.z);
					defenders[i].currentCommand = solidObject.attackMove;
					defenders[i].secondaryCommand = solidObject.attackMove;
				}
			}
		}
		
		//check if the minor threat is being dealt with
		if(minorThreatLocation.x == 0) {
			boolean defenersInStandbyMode = true;
			for(int i = 0; i < defenders.length; i++) {
				if(defenders[i] != null && defenders[i].currentHP > 0) {
					if(defenders[i].currentCommand != 0)
						defenersInStandbyMode = false;
				}
			}
			
			//if defenders are idle then make sure that combatAI has control of the defenders.
			if(defenersInStandbyMode) {
				giveBackControlOfDefendersToCombatAI();
				
				//move back to rally point
				for(int i =0; i < defenders.length; i++) {
					if(defenders[i] != null && frameAI%20==0) {
						defenders[i].moveTo(mainThread.ec.theUnitProductionAI.rallyPoint.x, mainThread.ec.theUnitProductionAI.rallyPoint.z);
						defenders[i].currentCommand = solidObject.attackMove;
						defenders[i].secondaryCommand = solidObject.attackMove;
					}
				}
			}
		}
		
		
		
		int numOfConstructionYard = 0;
		
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentHP >0) {
				numOfConstructionYard++;
			}
		}
		
		boolean gunTurretAlreadyInQueue = false;
		boolean missileTurretAlreadyInQueue = false;
		
		//check if AI needs to build static defenses
		/*
		  build a gun turret when any of the following conditions are met:
		  	1. there are more than 1 construction yard
		  	2. there is no other gun turret being constructed at the same time
		  	3. not major threat detected
		  
		  build a missile turret when any of the following conditions are met:
		  	1. there are more than 1 construction yard
		  	2. there is no other missile turret being constructed at the same time
		  	3. major threat detected
		*/
		
		needGunTurret = false;
		needMissileTurret = false;
		
		if(numOfConstructionYard > 1) {
			for(int i = 0; i < constructionYards.length; i ++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentHP > 0){
					if(constructionYards[i].currentBuildingType == 200){
						gunTurretAlreadyInQueue = true;
						break;
					}
				}
			}
			for(int i = 0; i < constructionYards.length; i ++){
				if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentHP > 0){
					if(constructionYards[i].currentBuildingType == 199){
						missileTurretAlreadyInQueue = true;
						break;
					}
				}
			}
			
			
			if(!gunTurretAlreadyInQueue && majorThreatLocation.x ==0) {
				needGunTurret = true;
			}
			
			if(!missileTurretAlreadyInQueue && majorThreatLocation.x != 0 && (mainPlayerForceSize !=0 || playerBuildingNearBase)) {
				needMissileTurret = true;
			}
			
			
		}
		
		
		
		//check if AI needs to deploy static defense
		/*
		  Deploy gun turret if the minor/major threat is close enough to the construction yard
		 */
		
		solidObject[] AIStructures = mainThread.ec.theMapAwarenessAI.AIStructures;
		gunTurretDeployLocation.reset();
		missileTurretDeployLocation.reset();
		
		int numOfGunTurretNearThreat = 0;
		int numOfMissileTurretNearThreat = 0;
	
		
		if(minorThreatLocation.x !=0){
			for(int i = 0; i < AIStructures.length; i++) {
				if(AIStructures[i] != null && AIStructures[i].teamNo !=0 && AIStructures[i].currentHP > 0  && (AIStructures[i].type == 200 || AIStructures[i].type == 199)) {
					float d = (float)Math.sqrt((minorThreatLocation.x-AIStructures[i].centre.x)*(minorThreatLocation.x-AIStructures[i].centre.x) +
							 (minorThreatLocation.z-AIStructures[i].centre.z)*(minorThreatLocation.z-AIStructures[i].centre.z));
					
					if(AIStructures[i].type == 200 && d <= 2.5)
						numOfGunTurretNearThreat++;
					if(AIStructures[i].type == 199 && d <= 2.9)
						numOfMissileTurretNearThreat++;
				}
			}
		}
		
		if(majorThreatLocation.x !=0){
			numOfGunTurretNearThreat = 0;
			numOfMissileTurretNearThreat = 0;
			
			for(int i = 0; i < AIStructures.length; i++) {
				if(AIStructures[i] != null && AIStructures[i].teamNo !=0 && AIStructures[i].currentHP > 0 && (AIStructures[i].type == 200 || AIStructures[i].type == 199)) {
					float d = (float)Math.sqrt((majorThreatLocation.x-AIStructures[i].centre.x)*(majorThreatLocation.x-AIStructures[i].centre.x) +
							 (majorThreatLocation.z-AIStructures[i].centre.z)*(majorThreatLocation.z-AIStructures[i].centre.z));
					
					if(AIStructures[i].type == 200 && d <= 2.5)
						numOfGunTurretNearThreat++;
					if(AIStructures[i].type == 199 && d <= 2.9)
						numOfMissileTurretNearThreat++;
				}
			}
		}
				
		
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] != null && constructionYards[i].teamNo != 0 && constructionYards[i].currentHP >0) {
				
				float distanceToThreat = 999f;
			
				
				float threatX = 0;
				float threatZ = 0;
				
				if(minorThreatLocation.x !=0) {
					distanceToThreat = (float)Math.sqrt((minorThreatLocation.x-constructionYards[i].centre.x)*(minorThreatLocation.x-constructionYards[i].centre.x) +
															 (minorThreatLocation.z-constructionYards[i].centre.z)*(minorThreatLocation.z-constructionYards[i].centre.z));
					threatX = minorThreatLocation.x;
					threatZ = minorThreatLocation.z;
				}
				
				if(majorThreatLocation.x !=0) { 
					distanceToThreat = (float)Math.sqrt((majorThreatLocation.x-constructionYards[i].centre.x)*(majorThreatLocation.x-constructionYards[i].centre.x) +
															 (majorThreatLocation.z-constructionYards[i].centre.z)*(majorThreatLocation.z-constructionYards[i].centre.z));
					threatX = majorThreatLocation.x;
					threatZ = majorThreatLocation.z;
				}
				
			
				//find deploy location of gun turret
				if(threatX != 0 && distanceToThreat < 4.75 && (numOfGunTurretNearThreat < (float)mainPlayerForceSize/3  || playerBuildingNearBase)) {
					
					float d = 1.85f;  //minimum deploy distance from conyard
					if(distanceToThreat > d + gunTurret.attackRange)
						d = distanceToThreat - gunTurret.attackRange;
					if(distanceToThreat < 3.5)
						d = 1.75f;
					
					gunTurretDeployLocation.x = constructionYards[i].centre.x + (threatX - constructionYards[i].centre.x)/distanceToThreat*d;
					gunTurretDeployLocation.z = constructionYards[i].centre.z + (threatZ - constructionYards[i].centre.z)/distanceToThreat*d;
					
					
				}
				
				//find deploy location of missile turret
				if(threatX != 0 && distanceToThreat < 5.15 && (numOfMissileTurretNearThreat < mainPlayerForceSize/6 || (playerBuildingNearBase && numOfMissileTurretNearThreat < 2))) {
					
					float d = 1.65f;  //minimum deploy distance from conyard
					if(distanceToThreat > d + missileTurret.attackRange)
						d = distanceToThreat - missileTurret.attackRange;
					if(distanceToThreat < 4.75)
						d = 1.25f;
					
					missileTurretDeployLocation.x = constructionYards[i].centre.x + (threatX - constructionYards[i].centre.x)/distanceToThreat*d;
					missileTurretDeployLocation.z = constructionYards[i].centre.z + (threatZ - constructionYards[i].centre.z)/distanceToThreat*d;
				}
			}
		}
		
		//tell the factory that are closest to the  threat location to build a repair drone
		float threatX = 0;
		float threatZ = 0;

		if(minorThreatLocation.x !=0) {
			threatX = minorThreatLocation.x;
			threatZ = minorThreatLocation.z;
		}
		
		if(majorThreatLocation.x !=0) { 
			threatX = majorThreatLocation.x;
			threatZ = majorThreatLocation.z;
		}
		
		factory cloestFactory = null;
		factory[] factories = mainThread.theAssetManager.factories;
		float threatDistance = 999f;
		for(int i = 0; i < factories.length; i++) {
			if(factories[i] !=null && factories[i].teamNo !=0 && factories[i].currentHP > 0) {
				float xPos = factories[i].centre.x;
				float zPos = factories[i].centre.z;
				double d = Math.sqrt( (threatX - xPos)*(threatX - xPos) + (threatZ - zPos)*(threatZ - zPos));
				if(d < 5f && d < threatDistance) {
					threatDistance = (float)d;
					cloestFactory = factories[i];
				}
			}
		}
		if(cloestFactory != null && cloestFactory.numOfDrones == 0 && cloestFactory.numOfDroneOnQueue == 0) {
			cloestFactory.cancelItemFromProductionQueue(factory.lightTankType);
			cloestFactory.cancelItemFromProductionQueue(factory.rocketTankType);
			cloestFactory.cancelItemFromProductionQueue(factory.stealthTankType);
			cloestFactory.cancelItemFromProductionQueue(factory.heavyTankType);
			cloestFactory.buildDrone();
		}
		
		
		//enable rapid fire ability for missiles turrets
		if(communicationCenter.rapidfireResearched_enemy) {
			for(int i = 0; i < AIStructures.length; i++) {
				if(AIStructures[i] != null && AIStructures[i].currentHP > 0 && AIStructures[i].teamNo == 1 && AIStructures[i].type == 199) {
					missileTurret t = (missileTurret)AIStructures[i];
					if(t.targetObject != null && t.overCharge == false) {
						mainThread.ec.theBaseInfo.numberOfOverChargedMissileTurret++;
						t.overCharge = true;
						mainThread.ec.theBaseInfo.reCalculatePower();
						if(mainThread.ec.theBaseInfo.currentPowerConsumption > mainThread.ec.theBaseInfo.currentPowerLevel) {
							mainThread.ec.theBaseInfo.numberOfOverChargedMissileTurret--;
							t.overCharge = false;
							mainThread.ec.theBaseInfo.reCalculatePower();
						}
					}else if((t.targetObject == null || mainThread.ec.theBaseInfo.currentPowerConsumption > mainThread.ec.theBaseInfo.currentPowerLevel) && t.overCharge == true) {
						mainThread.ec.theBaseInfo.numberOfOverChargedMissileTurret--;
						t.overCharge = false;
						mainThread.ec.theBaseInfo.reCalculatePower();
					}
				}
			}
		}
		
	}
	
	public float playerForceIsMovingTwoardsBase(vector location, vector direction) {
		float threatDistance = 999f;
		for(int i = 0; i < mainThread.theAssetManager.refineries.length;i++) {
			if(mainThread.theAssetManager.refineries[i] != null && mainThread.theAssetManager.refineries[i].teamNo != 0) {
				float xPos = mainThread.theAssetManager.refineries[i].nearestGoldMine.centre.x;
				float zPos = mainThread.theAssetManager.refineries[i].nearestGoldMine.centre.z;
				
				threatToBaseDirection.set(xPos - location.x, 0, zPos - location.z);
				float d = threatToBaseDirection.getLength();
				threatToBaseDirection.unit();
				if(threatToBaseDirection.dot(direction) > 0.8) {
				
					float currentThreatDistance = Math.max(3f, d);
					
					if(currentThreatDistance <  threatDistance)
						threatDistance = currentThreatDistance;
				
				}
			}
		}
		
		if(threatDistance == 999)
			return -1;
		
		return threatDistance;
	}
	
	public void giveBackControlOfDefendersToCombatAI() {
		for(int i = 0; i < defenders.length; i++) {
			if(defenders[i] == null)
				continue;
		
			if(defenders[i].type == 6) {
				boolean alreadyControledByCombatAI = false;
				for(int j = 0; j < stealthTanksControlledByCombatAI.length; j++) {
					if(defenders[i] == stealthTanksControlledByCombatAI[j]) {
						alreadyControledByCombatAI = true;
						break;
					}
				}
				if(!alreadyControledByCombatAI)
					mainThread.ec.theUnitProductionAI.addStealthTank((stealthTank)defenders[i]);
			}else if(defenders[i].type == 0) {
				boolean alreadyControledByCombatAI = false;
				for(int j = 0; j < lightTanksControlledByCombatAI.length; j++) {
					if(defenders[i] == lightTanksControlledByCombatAI[j]) {
						alreadyControledByCombatAI = true;
						break;
					}
				}
				if(!alreadyControledByCombatAI)
					mainThread.ec.theUnitProductionAI.addLightTank((lightTank)defenders[i]);
			}
		}
	}
	
	public void takeOverDefendersFromCombatAI() {
		for(int i = 0; i < defenders.length; i++) {
			if(defenders[i] == null)
				continue;
		
			if(defenders[i].type == 6) {
				for(int j = 0; j < stealthTanksControlledByCombatAI.length; j++) {
					if(defenders[i] == stealthTanksControlledByCombatAI[j]) {
						stealthTanksControlledByCombatAI[j] = null;
						break;
					}
				}
			}else if(defenders[i].type == 0) {
				for(int j = 0; j < lightTanksControlledByCombatAI.length; j++) {
					if(defenders[i] == lightTanksControlledByCombatAI[j]) {
						lightTanksControlledByCombatAI[j] = null;
						break;
					}
				}
			}
		}
	}
	
	public boolean playerForceIsNearBase(vector location) {
		for(int i = 0; i < mainThread.ec.theMapAwarenessAI.AIStructures.length; i++) {
			if(mainThread.ec.theMapAwarenessAI.AIStructures[i] == null)
				continue;
			float xPos = mainThread.ec.theMapAwarenessAI.AIStructures[i].centre.x;
			float zPos = mainThread.ec.theMapAwarenessAI.AIStructures[i].centre.z;
			float d = (location.x -  xPos)*(location.x -  xPos) + (location.z -  zPos)*(location.z -  zPos);
			if(d < 9)
				return true;
		}
		
		return false;
	}
	
	public int numOfHeavyTankAroundLocation(vector location) {
		solidObject o = null;
		int numberOfHeaveyTankNearLocation = 0;
		for(int i = 0; i < mainThread.ec.theMapAwarenessAI.playerUnitInMinimap.length; i++) {
			o = mainThread.ec.theMapAwarenessAI.playerUnitInMinimap[i];
			if(o !=null && o.currentHP > 0 && o.type == 7 && (o.centre.x - location.x)*(o.centre.x - location.x) + (o.centre.z - location.z)*(o.centre.z - location.z) < 4)
				numberOfHeaveyTankNearLocation++;
		}
		
		return numberOfHeaveyTankNearLocation;
	}
	
	public void addUnitToDefenders(solidObject o) {
		numOfDefenders = 0;
		boolean defenersInStandbyMode = true;
		for(int i = 0; i < defenders.length; i++) {
			if(defenders[i] != null && defenders[i].currentHP > 0) {
				numOfDefenders++;
				if(defenders[i].currentCommand != 0)
					defenersInStandbyMode = false;
			}
		}
		
		
		if(numOfDefenders ==  defenders.length && (minorThreatLocation.x == 0 && defenersInStandbyMode || minorThreatLocation.x != 0 && newUnitIsCloserToThreat(o))) {
			giveBackControlOfDefendersToCombatAI();
			for(int i = defenders.length - 1; i > 0; i--)
				defenders[i] = defenders[i - 1];
			defenders[0] = o;
		}else {
			for(int i = 0; i < defenders.length; i++) {
				if(defenders[i] == null || defenders[i].currentHP <= 0) {
					defenders[i] = o;
					numOfDefenders++;
					break;
				}
			}
		}
	}
	
	public boolean newUnitIsCloserToThreat(solidObject o) {
		float d = (o.centre.x - minorThreatLocation.x)*(o.centre.x - minorThreatLocation.x) + (o.centre.z - minorThreatLocation.z)*(o.centre.z - minorThreatLocation.z);
		for(int i = 0; i < defenders.length; i++) {
			if(defenders[i] != null && defenders[i].currentHP > 0) {
				if(d > (defenders[i].centre.x - minorThreatLocation.x)*(defenders[i].centre.x - minorThreatLocation.x) + (defenders[i].centre.z - minorThreatLocation.z)*(defenders[i].centre.z - minorThreatLocation.z)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	
	public boolean evadePlayerUnit(int  observerIndex){
		//scan for hostile unit
		int[] tileCheckList = stealthTank.tileCheckList;
		
		int currentOccupiedTile = (int)(observers[observerIndex].centre.x*64)/16 + (127 - (int)(observers[observerIndex].centre.z*64)/16)*128;
		
	
		direction.set(0,0,0);
		boolean directionSet = false;
		
		for(int i = 0; i < tileCheckList.length; i++){
			if(tileCheckList[i] != Integer.MAX_VALUE){
				int index = currentOccupiedTile + tileCheckList[i];
				if(index < 0 || index >= 16384 || Math.abs(index%128 - currentOccupiedTile%128) > 20)
					continue;
				solidObject[] tile = mainThread.gridMap.tiles[index];
				
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){
						if(tile[j].teamNo != observers[observerIndex].teamNo && tile[j].teamNo != -1 && tile[j].currentHP > 0 && !tile[j].isCloaked){
					
							
							if(directionSet)
								break;
							
							double d = Math.sqrt((tile[j].centre.x - observers[observerIndex].centre.x)*(tile[j].centre.x - observers[observerIndex].centre.x)  
									           + (tile[j].centre.z - observers[observerIndex].centre.z)*(tile[j].centre.z - observers[observerIndex].centre.z));
							
							if(d < 0.75) {
								
								direction.set(observers[observerIndex].centre);
								direction.subtract(tile[j].centre);
								direction.unit();
								
								directionSet = true;
							}
						}
					}
				}
			}
		}
		
		
		if(directionSet) {
			observers[observerIndex].moveTo(observers[observerIndex].centre.x + direction.x , observers[observerIndex].centre.z + direction.z);
			observers[observerIndex].currentCommand = solidObject.move;
			observers[observerIndex].secondaryCommand = solidObject.StandBy;
		}
		
		return directionSet; 
		
	}
	
}
