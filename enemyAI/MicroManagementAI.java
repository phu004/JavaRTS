package enemyAI;

import core.BaseInfo;
import core.MainThread;
import entity.RocketTank;
import entity.SolidObject;
import entity.TechCenter;

//micro manage the units on the battle field to trade units better against player

public class MicroManagementAI {
	
	public BaseInfo theBaseInfo;
	
	
	public int currentState;
	public final int booming = 0;
	public final int aggressing = 1;
	public final int defending = 2;
	
	public SolidObject[] playerUnitInMinimap;
	public SolidObject[] unitInCombatRadius;
	public SolidObject[] playerStaticDefenceInMinimap;
	
	public float combatCenterX;
	public float combatCenterZ;

	public int numberOfPlayerUnitsOnMinimap;
	
	public MicroManagementAI(){
		this.theBaseInfo = MainThread.enemyCommander.theBaseInfo;
	}
	
	public void processAI(){
	
		unitInCombatRadius = MainThread.enemyCommander.theUnitProductionAI.unitInCombatRadius;
		playerUnitInMinimap = MainThread.enemyCommander.theMapAwarenessAI.playerUnitInMinimap;
		playerStaticDefenceInMinimap = MainThread.enemyCommander.theMapAwarenessAI.playerStaticDefenceInMinimap;
		
		currentState = MainThread.enemyCommander.theCombatManagerAI.currentState;
		
		combatCenterX = MainThread.enemyCommander.theUnitProductionAI.combatAICenterX;
		combatCenterZ = MainThread.enemyCommander.theUnitProductionAI.combatAICenterZ;
		
		numberOfPlayerUnitsOnMinimap = MainThread.enemyCommander.theMapAwarenessAI.numberOfPlayerUnitsOnMinimap;
		
		float x1 = 0;
		float x2 = 0;
		float z1 = 0;
		float z2 = 0;
		
		
			
			
		//focus fire on the player unit with the least hit points
		for(int i = 0; i < unitInCombatRadius.length; i ++){
			
			if(unitInCombatRadius[i] == null  || unitInCombatRadius[i].currentHP <=0)
				continue;
			
			//micro  Rocket tanks, so they don't overkill targets,
			if(unitInCombatRadius[i].type == 1) {
				
				float myRange= (unitInCombatRadius[i].attackRange) * (unitInCombatRadius[i].attackRange);
				float scanRange = (unitInCombatRadius[i].attackRange + 1.5f) * (unitInCombatRadius[i].attackRange+1.5f);
				
				//Prioritize searching for  targets among static defenses
				boolean suitableTargertFound = false;
				float distanceToDesination = 999;
				SolidObject target = null;
				for(int j = 0; j < playerStaticDefenceInMinimap.length; j++) {
					if(playerStaticDefenceInMinimap[j] != null && !playerStaticDefenceInMinimap[j].willDieFromIncomingAttack()){
						x1 = playerStaticDefenceInMinimap[j].centre.x;
						x2 = unitInCombatRadius[i].centre.x;
						z1 = playerStaticDefenceInMinimap[j].centre.z;
						z2 = unitInCombatRadius[i].centre.z;
						float d = (x1 - x2)*(x1 - x2) +  (z1 - z2)*(z1 - z2);
						if(d < scanRange && d < distanceToDesination){
							distanceToDesination = d;
							target = playerStaticDefenceInMinimap[j];
							
						}
					}
				}
				
				if(target != null) {
					unitInCombatRadius[i].attack(target);
					
					unitInCombatRadius[i].currentCommand = SolidObject.attackCautiously;
					unitInCombatRadius[i].secondaryCommand = SolidObject.StandBy;
					
					suitableTargertFound = true;
					
					if(distanceToDesination < myRange) {
						int myDamage = unitInCombatRadius[i].myDamage;
						if(TechCenter.rocketTankResearched_enemy) {
							myDamage*=2;
						}
						myDamage = (int)(myDamage* RocketTank.damageAginstBuildingMulitplier);
						
						target.incomingDamage+=myDamage*2;
					}
				}
				
				
				
				if(suitableTargertFound)
					continue;
				
				
				//if Rocket tank has no target or the target will die from incoming attack, find a new target
				if(unitInCombatRadius[i].targetObject != null && !unitInCombatRadius[i].targetObject.willDieFromIncomingAttack()) {
					
					int myDamage = unitInCombatRadius[i].myDamage;
					if(unitInCombatRadius[i].targetObject .type > 100) {
						if(TechCenter.rocketTankResearched_enemy) {
							myDamage*=2;
						}
						myDamage = (int)(myDamage* RocketTank.damageAginstBuildingMulitplier);
					}
					
					unitInCombatRadius[i].targetObject.incomingDamage+=myDamage*2;
					
				}else {

					//find targets among moving unites
					for(int j=0; j < numberOfPlayerUnitsOnMinimap; j++){  
						if(playerUnitInMinimap[j] != null && !playerUnitInMinimap[j].willDieFromIncomingAttack()){
							x1 = playerUnitInMinimap[j].centre.x;
							x2 = unitInCombatRadius[i].centre.x;
							z1 = playerUnitInMinimap[j].centre.z;
							z2 = unitInCombatRadius[i].centre.z;
							distanceToDesination = (x1 - x2)*(x1 - x2) +  (z1 - z2)*(z1 - z2);
							
							if(distanceToDesination < myRange){
								unitInCombatRadius[i].attack(playerUnitInMinimap[j]);
								playerUnitInMinimap[j].incomingDamage+=unitInCombatRadius[i].myDamage*2;
								break;
							}
						}
					}
				}
				
				continue;
			}
			
	
			//if(unitInCombatRadius[i].type == 0)
			//	unitInCombatRadius[i].groupAttackRange = 1.35f;
			
			//if(unitInCombatRadius[i].level > 0)
			//	unitInCombatRadius[i].groupAttackRange = unitInCombatRadius[i].attackRange;
			
			float myRange= unitInCombatRadius[i].attackRange * unitInCombatRadius[i].attackRange;
			
			SolidObject target = null;
			SolidObject currentTarget = unitInCombatRadius[i].targetObject;
			int targetHP = 99999;
			int level = 0;
			float distanceToDesination = 99999;
			
		
			for(int j=0; j < numberOfPlayerUnitsOnMinimap; j++){  
				if(playerUnitInMinimap[j] != null  && playerUnitInMinimap[j].currentHP > 0){
					
					if((playerUnitInMinimap[j].getMaxHp() / playerUnitInMinimap[j].currentHP > 4 && !(currentTarget != null &&  currentTarget.currentHP < playerUnitInMinimap[j].currentHP))  || playerUnitInMinimap[j].level > level){
						x1 = playerUnitInMinimap[j].centre.x;
						x2 = unitInCombatRadius[i].centre.x;
						z1 = playerUnitInMinimap[j].centre.z;
						z2 = unitInCombatRadius[i].centre.z;
						distanceToDesination = (x1 - x2)*(x1 - x2) +  (z1 - z2)*(z1 - z2);
						if(distanceToDesination < myRange){
							if(hasLineOfSight(distanceToDesination, x1, x2, z1, z2, playerUnitInMinimap[j])){
								target = playerUnitInMinimap[j];
								break;
							}
						}
					}
					
					
					if(targetHP >= playerUnitInMinimap[j].currentHP || (targetHP == playerUnitInMinimap[j].currentHP && playerUnitInMinimap[j].ID%5 == 0)){
						x1 = playerUnitInMinimap[j].centre.x;
						x2 = unitInCombatRadius[i].centre.x;
						z1 = playerUnitInMinimap[j].centre.z;
						z2 = unitInCombatRadius[i].centre.z;
						distanceToDesination = (x1 - x2)*(x1 - x2) +  (z1 - z2)*(z1 - z2);
						if( distanceToDesination < myRange){
							if(hasLineOfSight(distanceToDesination, x1, x2, z1, z2, playerUnitInMinimap[j])){
								target = playerUnitInMinimap[j];
								targetHP = playerUnitInMinimap[j].currentHP;
							}
						}
					}
				}
			} 
			
			if(target !=null ){
				unitInCombatRadius[i].attack(target);
				unitInCombatRadius[i].currentCommand = SolidObject.attackCautiously;
				unitInCombatRadius[i].attackStatus = SolidObject.isAttacking;
				unitInCombatRadius[i].closeToDestination = true;
			}
			
		}
		
		//reset incoming damage for all units
		for(int i = 0; i <  MainThread.enemyCommander.theMapAwarenessAI.mapAsset.length; i++) {
			if(MainThread.enemyCommander.theMapAwarenessAI.mapAsset[i] != null)
				MainThread.enemyCommander.theMapAwarenessAI.mapAsset[i].incomingDamage = 0;
		}
			
	}
	
	public boolean hasLineOfSight(float distanceToDesination, float x1, float x2, float z1, float z2, SolidObject targetObject){
		boolean hasLineOfSight = true;
		int numberOfIterations = (int)(Math.sqrt(distanceToDesination) * 8);
		float dx = (x1 - x2)/numberOfIterations;
		float dy = (z1 - z2)/numberOfIterations;
		float xStart = x2;
		float yStart = z2;
		
		for(int i = 0; i < numberOfIterations; i++){
			xStart+=dx;
			yStart+=dy;
			SolidObject s = MainThread.gridMap.tiles[(int)(xStart*4) + (127 - (int)(yStart*4))*128][0];
			if(s != null){
				if(s.type > 100 && s.type < 200 && s != targetObject){
					hasLineOfSight = false;
					break;
				}
			}
		}
		
		return hasLineOfSight;
	}
	
}
