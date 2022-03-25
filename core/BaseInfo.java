package core;

//store all the information about a base, eg current credit, number of structures , current power level, tech trees and etc...

public class BaseInfo {
	public int numberOfPowerPlant;
	public int numberOfConstructionYard;
	public int numberOfRefinery;
	public int numberOfFactory;
	public int numberOfCommunicationCenter;
	public int numberOfTechCenter;
	public int numberOfGunTurret;
	public int numberOfMissileTurret, numberOfOverChargedMissileTurret;
	public boolean canBuildPowerPlant, canBuildRefinery, canBuildFactory, canBuildCommunicationCenter, canBuildTechCenter, canBuildGunTurret, canBuildMissileTurret;
	public boolean canBuildLightTank, canBuildRocketTank, canBuildDrone, canBuildStealthTank, canBuildHeavyTank, canBuildMCV, canBuildHarvester;
	
	public int currentCredit;
	public int currentPowerLevel;
	public int currentPowerConsumption;
	public int powerStatus;
	public boolean lowPower;
	
	public BaseInfo(){
		currentCredit = 5000;
		
	}
	
	public void update(){
		//update tech tree
		canBuildPowerPlant = true;
		canBuildRefinery = false;
		canBuildFactory = false;
		canBuildCommunicationCenter= false;
		canBuildTechCenter = false;
		canBuildGunTurret = false;
		canBuildMissileTurret= false;

		canBuildLightTank = true;
		canBuildRocketTank = true;
		canBuildDrone = true;
		canBuildHarvester = false;
		canBuildMCV = false;
		canBuildHeavyTank = false;
		canBuildStealthTank = false;

		if(numberOfPowerPlant > 0){
			canBuildRefinery = true;

		}

		if(numberOfRefinery > 0){
			canBuildFactory = true;
			canBuildHarvester = true;
		}

		if(numberOfFactory > 0){
			canBuildCommunicationCenter = true;
			canBuildGunTurret = true;
		}

		if(numberOfCommunicationCenter > 0){
			canBuildMissileTurret = true;
			canBuildTechCenter = true;
			canBuildStealthTank = true;
			canBuildMCV = true;
		}

		if(numberOfTechCenter > 0){
			canBuildHeavyTank = true;
		}


		reCalculatePower();

		//calculate power level and power consumption
		calculatePowerLevel();

	}

/**
	This method is extracted from the update method.
	update method above had complex method smell which is removed by this refactoring.
	complex method smell arises when a method is too long and performs multiple operations.
*/

	private void calculatePowerLevel() {
		if(currentPowerLevel == 0){
			powerStatus = -1;
		}else{
			powerStatus = currentPowerConsumption * 100 / currentPowerLevel;
		}
		if(powerStatus == -1 || powerStatus > 100)
			lowPower = true;
		else
			lowPower = false;

		if(powerStatus != -1){
			powerStatus = currentPowerConsumption << 16 | currentPowerLevel;
		}
	}

	public void reCalculatePower() {
		currentPowerLevel = numberOfPowerPlant*500 + numberOfConstructionYard*100;
		currentPowerConsumption = numberOfRefinery*150 + numberOfFactory*200 + numberOfCommunicationCenter*250 + numberOfGunTurret*100 + numberOfMissileTurret*200 + numberOfOverChargedMissileTurret*150 + numberOfTechCenter*400;
	}
	
}
