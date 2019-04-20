package core;

import enemyAI.*;
import entity.*;
import gui.*;
import particles.*;

//This class stores and maintains all the entities created in the game 
public class AssetManager {
	
	public int polygonCount;
	
	public int visibleUnitCount;
	public solidObject[] visibleUnit;

	public int[][] selectedUnitsInfo;
	public int[][] selectedUnitsInfo2;
	
	public float[][] visionPolygonInfo;
	public float[][] visionPolygonInfo2;
	public int visionPolygonCount;
	
	public int[][] unitsForMiniMap;
	public int[][] unitsForMiniMap2;
	public int unitsForMiniMapCount;
	
	public boolean[] minimapBitmap;
	public boolean[] minimapBitmap2;
	
	public float[][] smokeEmmiterList;
	public float[][] smokeEmmiterList2;
	public int smokeEmmiterCount;
	
	public float[][] explosionInfo;
	public float[][] explosionInfo2;
	public int explosionCount;;
	
	public float[][] helixInfo;
	public float[][] helixInfo2;
	public int helixCount;
	
	public double[] confirmationIconInfo;
	public double[] confirmationIconInfo2;
	
	
	public lightTank[] lightTanks;
	public heavyTank[] heavyTanks;
	public palmTree[] trees;
	public int plamTreeCount;
	public powerPlant[] powerPlants;
	public refinery[] refineries;
	public rocketTank[] rocketTanks;
	public harvester[] harvesters;
	public goldMine[] goldMines;
	public constructionVehicle[] constructionVehicles;
	public constructionYard[] constructionYards;
	public factory[] factories;
	public drone[] drones;
	public communicationCenter[] communicationCenters;
	public techCenter[] techCenters;
	public stealthTank[] stealthTanks;
	public gunTurret[] gunTurrets;
	public missileTurret[] missileTurrets;
	
	public terrain Terrain;
	
	public bullet[] bullets;
	public rocket[] rockets;
	public  polygon3D[] visionPolygon;
	
	
	public void init(){
		
		//polygons which represent the area of sight for player 
		double angle = Math.PI/24;
		visionPolygon = new polygon3D[4];
		vector[] v = new vector[48];
		for(int i = 0; i < 48; i++){
			v[i] = new vector((float)Math.sin(i*angle)*1.6f, 1f, (float)Math.cos(i*angle)*1.6f);
		}
		visionPolygon[0] = new polygon3D(v, v[0], v[1], v[3], null, 1,1, 2);
		
		// vision created by an  attacking enemy unit
		v = new vector[48];
		for(int i = 0; i < 48; i++){
			v[i] = new vector((float)Math.sin(i*angle)*0.5f, -1.5f, (float)Math.cos(i*angle)*0.5f);
		}
		visionPolygon[1] = new polygon3D(v, v[0], v[1], v[3], null, 1,1, 2);
		
		//vision created by building
		v = new vector[48];
		for(int i = 0; i < 48; i++){
			v[i] = new vector((float)Math.sin(i*angle)*2.1f, 1f, (float)Math.cos(i*angle)*2.1f);
		}
		visionPolygon[2] = new polygon3D(v, v[0], v[1], v[3], null, 1,1, 2);
		
		//vision created by communication center
		v = new vector[48];
		for(int i = 0; i < 48; i++){
			v[i] = new vector((float)Math.sin(i*angle)*3.1f, 1f, (float)Math.cos(i*angle)*3.1f);
		}
		visionPolygon[3] = new polygon3D(v, v[0], v[1], v[3], null, 1,1, 2);
		
		Terrain = new terrain(); 
		
		goldMines = new goldMine[16];
		goldMines[0] = new goldMine(2f,-0.515f, 1.25f, 45000);
		goldMines[1] = new goldMine(9.5f,-0.515f, 5.5f, 45000);
		goldMines[2] = new goldMine(2f,-0.515f, 28.25f, 50000);
		goldMines[3] = new goldMine(26f,-0.515f, 3.5f, 50000);
		goldMines[4] = new goldMine(29.75f,-0.515f, 30f, 45000);
		goldMines[5] = new goldMine(22.5f,-0.515f, 25.5f, 45000);
		goldMines[6] = new goldMine(15.75f,-0.515f, 18f, 55000);
		goldMines[7] = new goldMine(16.25f,-0.515f, 12.25f, 55000);
		
		
		//create trees from bitmap
		trees = new palmTree[2048];
		short[] treeBitmap = mainThread.textures[56].pixelData;
		for(int i = 0; i < 114; i++){
			for(int j = 0; j < 114; j++){
				if((treeBitmap[j + i * 128]<<10) <128 && plamTreeCount < trees.length){
					trees[plamTreeCount] = new palmTree(j*0.28f, -0.3f, (113 -i)*0.28f);
					plamTreeCount++;
				}
				
			}
		}	
		
		visibleUnit = new solidObject[400];
		
		mainThread.pc = new playerCommander();
		mainThread.ec = new enemyCommander();
		
	}
	
	public void prepareAssetForNewGame(){
				
		camera.position.set(3,2f,-1.25f);
		camera.view_Direction.set(0, 0, 1);
		camera.XZ_angle = 0;
		
		
		selectedUnitsInfo = new int[100][6];
		selectedUnitsInfo2 = new int[100][6];
	
		visionPolygonInfo = new float[400][5];
		visionPolygonInfo2 = new float[400][5];
		
		unitsForMiniMap = new int[1000][5];
		unitsForMiniMap2 = new int[1000][5];
		
		
		smokeEmmiterList = new float[100][8];
		smokeEmmiterList2 = new float[100][8];

		minimapBitmap = new boolean[128 * 128];
		minimapBitmap2 = new boolean[128 * 128];
		
		
		explosionInfo = new float[100][8];
		explosionInfo2 = new float[100][8];
		
		helixInfo = new float[128][4];
		helixInfo2 = new float[128][4];
		
		confirmationIconInfo = new double[5];
		confirmationIconInfo2 = new double[5];
		
		lightTanks = new lightTank[768];
		heavyTanks = new heavyTank[256];
		powerPlants = new powerPlant[256];
		refineries = new refinery[128];
		rocketTanks = new rocketTank[512];
		harvesters = new harvester[128];
		constructionVehicles = new constructionVehicle[64];
		constructionYards = new constructionYard[64];
		factories = new factory[128];
		drones = new drone[384];
		communicationCenters = new communicationCenter[128];
		techCenters = new techCenter[64];
		stealthTanks = new stealthTank[384];
		gunTurrets = new gunTurret[512];
		missileTurrets = new missileTurret[256];
		
		goldMines[0].goldDeposite = goldMines[0].maxDeposite;
		goldMines[1].goldDeposite = goldMines[1].maxDeposite;
		goldMines[2].goldDeposite = goldMines[2].maxDeposite;
		goldMines[3].goldDeposite = goldMines[3].maxDeposite;
		goldMines[4].goldDeposite = goldMines[4].maxDeposite;
		goldMines[5].goldDeposite = goldMines[5].maxDeposite;
		goldMines[6].goldDeposite = goldMines[6].maxDeposite;
		goldMines[7].goldDeposite = goldMines[7].maxDeposite;
		
		bullets = new bullet[200];
		for(int i = 0; i < 200; i ++){
			bullets[i] = new bullet();
			
		}
		
		rockets = new rocket[200];
		for(int i = 0; i < 200; i ++){
			rockets[i] = new rocket();
			
		}

		mainThread.pc.init();
		mainThread.ec.init();
		
		lightTank.tileCheckList_player = solidObject.generateTileCheckList(5f);
		lightTank.tileCheckList_enemy = solidObject.generateTileCheckList(5f);
		techCenter.resetResarchStatus();
		communicationCenter.resetResearchStatus();
		
		
		addConstructionVehicle(new constructionVehicle(new vector(3.125f,-0.3f, 2.125f), 90, 0));	
		addConstructionVehicle(new constructionVehicle(new vector(29.625f,-0.3f, 28.875f), 90, 1));	
		constructionVehicles[1].expand();
 
		
		//testing only
		for(int i = 0; i < 6; i ++){
			
			for(int j = 0; j < 10; j++){
				
				//if(i == 0) {
					//rocketTank l = new rocketTank(new vector(j*0.25f+ 1.125f,-0.3f, 22.125f - i*0.25f), 90, 1);
					//l.damageMultiplier =2;
					//addRocketTank(l);
				//techCenter.rocketTankResearched_enemy = true;
				
				//}else {
					//heavyTank l = new heavyTank(new vector(j*0.25f+ 1.125f,-0.3f, 22.125f - i*0.25f), 90, 1);
					
					//addHeavyTank(l);
				//}
				
				
			}
		}
		
		for(int i = 0; i < 10; i ++){
			
			for(int j = 0; j < 6; j++){ 
				//lightTank l = new lightTank(new vector(i*0.25f+ 1.125f,-0.3f, 17.375f - 0.25f*j), 90, 0);
				//addLightTank(l);
				//l.hasMultiShotUpgrade = true;
				//lightTank l = new lightTank(new vector(i*0.25f + 1.125f,-0.3f, 0.5f + 18.625f + j*0.25f), 90, 0);
				
				//l.attackRange = 1.99f;
		
				//lightTank.tileCheckList_player = lightTank.generateTileCheckList(6);
		
				//addLightTank(l);
				//if(j == 0 && i == 0)
				//addGunTurret(new gunTurret(i*0.25f -0.125f + 28, -0.65f, 0.25f + 28.125f + j*0.25f, 1));
			
			}
		}
	}
	
	public void destoryAsset() {
		camera.view_Direction.set(0, 0, 1);
		camera.frameIndex = 0;
		camera.XZ_angle = 0;
		
		selectedUnitsInfo = null;
		selectedUnitsInfo2 = null;
	
		visionPolygonInfo = null;
		visionPolygonInfo2 = null;
		
		unitsForMiniMap = null;
		unitsForMiniMap2 = null;
		
		
		smokeEmmiterList = null;
		smokeEmmiterList2 = null;

		minimapBitmap = null;
		minimapBitmap2 = null;
		
		
		explosionInfo = null;
		explosionInfo2 = null;
		helixInfo = null;
		helixInfo2 = null;
		
		confirmationIconInfo = null;
		confirmationIconInfo2 = null;
		
		lightTanks = null;
		heavyTanks = null;
		powerPlants = null;
		refineries = null;
		rocketTanks = null;
		harvesters = null;
		constructionVehicles = null;
		constructionYards = null;
		factories = null;
		drones = null;
		communicationCenters = null;
		techCenters = null;
		stealthTanks = null;
		gunTurrets = null;
		missileTurrets = null;
		
		goldMines[0].goldDeposite = goldMines[0].maxDeposite;
		goldMines[1].goldDeposite = goldMines[1].maxDeposite;
		goldMines[2].goldDeposite = goldMines[2].maxDeposite;
		goldMines[3].goldDeposite = goldMines[3].maxDeposite;
		goldMines[4].goldDeposite = goldMines[4].maxDeposite;
		goldMines[5].goldDeposite = goldMines[5].maxDeposite;
		goldMines[6].goldDeposite = goldMines[6].maxDeposite;
		goldMines[7].goldDeposite = goldMines[7].maxDeposite;
		
		bullets = null;		
		rockets = null;
		
		mainThread.gridMap.reset();
	}
	
	
	public void addContructionYard(constructionYard o){
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] == null){
				constructionYards[i] = o;
				break;
			}
		}
	}
	
	public void addPowerPlant(powerPlant o){
		for(int i = 0; i < powerPlants.length; i++){
			if(powerPlants[i] == null){
				powerPlants[i] = o;
				break;
			}
		}
	}
	
	public void addRefinery(refinery o){
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] == null){
				refineries[i] = o;
				break;
			}
		}
	}
	
	public void addFactory(factory o){
		for(int i = 0; i < factories.length; i++){
			if(factories[i] == null){
				factories[i] = o;
				break;
			}
		}
	}
	
	public void addDrone(drone o){
		for(int i = 0; i < drones.length; i++){
			if(drones[i] == null){
				drones[i] = o;
				break;
			}
		}
	}
	
	public void addCommunicationCenter(communicationCenter o){
		for(int i = 0; i < communicationCenters.length; i++){
			if(communicationCenters[i] == null){
				communicationCenters[i] = o;
				break;
			}
		}
	}
	
	public void addTechCenter(techCenter o){
		for(int i = 0; i < techCenters.length; i++){
			if(techCenters[i] == null){
				techCenters[i] = o;
				break;
			}
		}
	}
	
	public void addHarvester(harvester o){
		for(int i = 0; i < harvesters.length; i++){
			if(harvesters[i] == null){
				harvesters[i] = o;
				break;
			}
		}
	}
	
	public void addLightTank(lightTank o){
		for(int i = 0; i < lightTanks.length; i++){
			if(lightTanks[i] == null){
				lightTanks[i] = o;
				break;
			}
		}
	}
	
	public void addHeavyTank(heavyTank o){
		for(int i = 0; i < heavyTanks.length; i++){
			if(heavyTanks[i] == null){
				heavyTanks[i] = o;
				break;
			}
		}
	}
	
	public void addStealthTank(stealthTank o){
		for(int i = 0; i < stealthTanks.length; i++){
			if(stealthTanks[i] == null){
				stealthTanks[i] = o;
				break;
			}
		}
	}
	
	public void addRocketTank(rocketTank o){
		for(int i = 0; i < rocketTanks.length; i++){
			if(rocketTanks[i] == null){
				rocketTanks[i] = o;
				break;
			}
		}
	}
	
	public void addConstructionVehicle(constructionVehicle o){
		for(int i = 0; i < constructionVehicles.length; i++){
			if(constructionVehicles[i] == null){
				constructionVehicles[i] = o;
				break;
			}
		}
	}
	
	public void addGunTurret(gunTurret o){
		for(int i = 0; i < gunTurrets.length; i++){
			if(gunTurrets[i] == null){
				gunTurrets[i] = o;
				break;
			}
		}
	}
	
	public void addMissileTurret(missileTurret o){
		for(int i = 0; i < missileTurrets.length; i++){
			if(missileTurrets[i] == null){
				missileTurrets[i] = o;
				break;
			}
		}
	}
	
	
	public void updateAndDraw(){
		polygonCount = 0;
		visibleUnitCount = 0;
		visionPolygonCount = 0;
		unitsForMiniMapCount = 0;
		explosionCount = 0;
		smokeEmmiterCount = 0;
		helixCount = 0;
		
		
		
		if(mainThread.gameStarted) {
			for(int i = 0; i < lightTanks.length; i++){
				if(lightTanks[i] != null)
					lightTanks[i].update();
			}
			
			for(int i = 0; i < heavyTanks.length; i++){
				if(heavyTanks[i] != null)
					heavyTanks[i].update();
			}
			
			for(int i = 0; i < stealthTanks.length; i++){
				if(stealthTanks[i] != null)
					stealthTanks[i].update();
			}
			
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null)
					constructionYards[i].update();
			}
			
			for(int i = 0; i < powerPlants.length; i++){
				if(powerPlants[i] != null)
					powerPlants[i].update();
			}
			
			for(int i = 0; i < techCenters.length; i++){
				if(techCenters[i] != null)
					techCenters[i].update();
			}
			
			for(int i = 0; i < gunTurrets.length; i++){
				if(gunTurrets[i] != null)
					gunTurrets[i].update();
			}
			
			for(int i = 0; i < missileTurrets.length; i++){
				if(missileTurrets[i] != null)
					missileTurrets[i].update();
			}
			
			for(int i = 0; i < communicationCenters.length; i++){
				if(communicationCenters[i] != null)
					communicationCenters[i].update();
			}
			
			for(int i = 0; i < refineries.length; i++){
				if(refineries[i] != null)
					refineries[i].update();
			}
			
			for(int i = 0; i < factories.length; i++){
				if(factories[i] != null)
					factories[i].update();
			}
			
			for(int i = 0; i < drones.length; i++){
				if(drones[i] != null)
					drones[i].update();
			}
			
			
			for(int i = 0; i < rocketTanks.length; i++){
				if(rocketTanks[i] != null)
					rocketTanks[i].update();
			}
			
			for(int i = 0; i < harvesters.length; i++){
				if(harvesters[i] != null)
					harvesters[i].update();
			}
			
			for(int i = 0; i < constructionVehicles.length; i++){
				if(constructionVehicles[i] != null)
					constructionVehicles[i].update();
			}
			
	
			
		}
		
		for(int i = 0; i < plamTreeCount; i++)
			trees[i].update();
		
		for(int i = 0; i < 10; i++){
			if(goldMines[i] != null)
				goldMines[i].update();
		}
		
		Terrain.update();
		
		
		//start drawing
		//maximize the zbuffer value in the  area that are occupied by  UI, so the drawing process will not waste time filling the pixels which would eventually get overdrawn 
		if(mainThread.gameStarted) {
			int start = 381 * 768 + 3;
			int start2 = 381 * 768 + 635;
			for(int y = 0; y < 131; y++){
				for(int x = 0; x < 128; x ++){
					mainThread.zBuffer[start + x + y*768]	= Integer.MAX_VALUE;
					mainThread.zBuffer[start2 + x + y*768]	= Integer.MAX_VALUE;
				}
			}
		
		
	
		
		
			for(int i = 0; i < 200; i ++)
				bullets[i].updateAndDraw();
			
			for(int i = 0; i < 200; i ++)
				rockets[i].update();
			
	
			for(int i = 0; i < lightTanks.length; i++){
				if(lightTanks[i] != null)
					lightTanks[i].draw();
			}
			
			for(int i = 0; i < heavyTanks.length; i++){
				if(heavyTanks[i] != null)
					heavyTanks[i].draw();
			}
			
			for(int i = 0; i < stealthTanks.length; i++){
				if(stealthTanks[i] != null)
					stealthTanks[i].draw();
			}
			
			for(int i = 0; i < powerPlants.length; i++){
				if(powerPlants[i] != null)
					powerPlants[i].draw();
			}
			
			for(int i = 0; i < gunTurrets.length; i++){
				if(gunTurrets[i] != null)
					gunTurrets[i].draw();
			}
			
			for(int i = 0; i < missileTurrets.length; i++){
				if(missileTurrets[i] != null)
					missileTurrets[i].draw();
			}
			
			for(int i = 0; i < communicationCenters.length; i++){
				if(communicationCenters[i] != null)
					communicationCenters[i].draw();
			}
			
			for(int i = 0; i < techCenters.length; i++){
				if(techCenters[i] != null)
					techCenters[i].draw();
			}
			
			for(int i = 0; i < refineries.length; i++){
				if(refineries[i] != null)
					refineries[i].draw();
			}
			
			for(int i = 0; i < rocketTanks.length; i++){
				if(rocketTanks[i] != null)
					rocketTanks[i].draw();
			}
			
			for(int i = 0; i < harvesters.length; i++){
				if(harvesters[i] != null)
					harvesters[i].draw();
			}
			
			for(int i = 0; i < constructionVehicles.length; i++){
				if(constructionVehicles[i] != null)
					constructionVehicles[i].draw();
			}
			
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null)
					constructionYards[i].draw();
			}
			
			for(int i = 0; i < 200; i ++)
				rockets[i].draw();
			
			
			for(int i = 0; i < drones.length; i++){
				if(drones[i] != null)
					drones[i].draw();
			}
			
			for(int i = 0; i < factories.length; i++){
				if(factories[i] != null)
					factories[i].draw();
			}
		}
		
		for(int i = 0; i < goldMines.length; i++){
			if(goldMines[i] != null)
				goldMines[i].draw();
		}
		
		for(int i = 0; i < plamTreeCount; i++)
			trees[i].draw();
		
		Terrain.draw();
		
	
		if(mainThread.gameStarted) {
			if(mainThread.pc.selectedConstructionYard != null){
				mainThread.pc.selectedConstructionYard.drawDeploymentGrid();
			}
			
			for(int i = 0; i < factories.length; i++){
				if(factories[i] != null)
					factories[i].drawRallyPointLine();
			}
			
			
			//prepare selected unit list
			for(int i = 0; i < 99; i++){
				if(mainThread.pc.selectedUnits[i] != null && mainThread.pc.selectedUnits[i].isSelectable){
					selectedUnitsInfo[i][0] =  mainThread.pc.selectedUnits[i].level << 16 | mainThread.pc.selectedUnits[i].groupNo << 8 | mainThread.pc.selectedUnits[i].type;
					selectedUnitsInfo[i][1] = (int)mainThread.pc.selectedUnits[i].tempCentre.screenX;
					selectedUnitsInfo[i][2] = (int)mainThread.pc.selectedUnits[i].tempCentre.screenY;
					if(mainThread.pc.selectedUnits[i].type == 199){
						selectedUnitsInfo[i][1] = (int)mainThread.pc.selectedUnits[i].screenX_gui;
						selectedUnitsInfo[i][2] = (int)mainThread.pc.selectedUnits[i].screenY_gui;
					}
					
					selectedUnitsInfo[i][3] = (int)mainThread.pc.selectedUnits[i].type;
					selectedUnitsInfo[i][4] = mainThread.pc.selectedUnits[i].currentHP;
					selectedUnitsInfo[i][5] = mainThread.pc.selectedUnits[i].progressStatus;
				}else{
					selectedUnitsInfo[i][0] = -1;
				}
			}
		}
	
	}
	
	
	//swap the resources that are held by the main thread and the post processing thread
	public void swapResources(){
		int[][] list;
		list = selectedUnitsInfo;
		selectedUnitsInfo = selectedUnitsInfo2;
		selectedUnitsInfo2 = list;
		
		float[][] floatList;
		floatList = visionPolygonInfo;
		visionPolygonInfo = visionPolygonInfo2;
		visionPolygonInfo2 = floatList;
		
		int[][] unitList;
		unitList = unitsForMiniMap;
		unitsForMiniMap = unitsForMiniMap2;
		unitsForMiniMap2 = unitList;
		
		boolean[] bitmap;
		bitmap = minimapBitmap;
		minimapBitmap = minimapBitmap2;
		minimapBitmap2 = bitmap;
		
		float[][] emmiterList;
		emmiterList = smokeEmmiterList;
		smokeEmmiterList = smokeEmmiterList2;
		smokeEmmiterList2 = emmiterList;
		
		float[][] explosionList;
		explosionList = explosionInfo;
		explosionInfo = explosionInfo2;
		explosionInfo2 = explosionList;
		
		double[] iconInfo;
		iconInfo = confirmationIconInfo;
		confirmationIconInfo = confirmationIconInfo2;
		confirmationIconInfo2 = iconInfo;
	}
	
	//spawn a bullet
	public void spawnBullet(int angle, int damage, solidObject target, vector centre, solidObject attacker){
		for(int i = 0; i < 200; i ++)
			if(!bullets[i].isInAction){
				bullets[i].setActive(angle, damage, target, centre, attacker);
				break;
			}
	}
	
	//spawn a rocket
	public void spawnRocket(int angle, int damage, solidObject target, vector centre, solidObject attacker){
		for(int i = 0; i < 200; i ++)
			if(!rockets[i].isInAction){
				rockets[i].setActive(angle, damage, target, centre, attacker);
				break;
			}
	}
	
	//remove object that ceased to exist (e.g get destroyed)
	public void removeObject(solidObject o){
		
		mainThread.pc.removeDestoryedObjectFromSelection(o);
		for(int i = 0; i < lightTanks.length; i++){
			if(lightTanks[i] == o){
				lightTanks[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < stealthTanks.length; i++){
			if(stealthTanks[i] == o){
				stealthTanks[i] = null;
				return;
			}
		}
			
		for(int i = 0; i < powerPlants.length; i++){
			if(powerPlants[i] == o){
				powerPlants[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < techCenters.length; i++){
			if(techCenters[i] == o){
				techCenters[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < gunTurrets.length; i++){
			if(gunTurrets[i] == o){
				gunTurrets[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < missileTurrets.length; i++){
			if(missileTurrets[i] == o){
				missileTurrets[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < communicationCenters.length; i++){
			if(communicationCenters[i] == o){
				communicationCenters[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] == o){
				refineries[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < factories.length; i++){
			if(factories[i] == o){
				factories[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < drones.length; i++){
			if(drones[i] == o){
				drones[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < rocketTanks.length; i++){
			if(rocketTanks[i] == o){
				rocketTanks[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < heavyTanks.length; i++){
			if(heavyTanks[i] == o){
				heavyTanks[i] = null;
				return;
			}
		}
			
		for(int i = 0; i < harvesters.length; i++){
			if(harvesters[i] == o){
				harvesters[i] = null;
				return;
			}
		}	
		
		for(int i = 0; i < constructionVehicles.length; i++){
			if(constructionVehicles[i] == o){
				constructionVehicles[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] == o){
				constructionYards[i] = null;
				return;
			}
		}	
		
	}
}
