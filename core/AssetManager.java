package core;

import enemyAI.*;
import entity.*;
import particles.*;

//This class stores and maintains all the entities created in the game 
public class AssetManager {
	
	public int polygonCount;
	
	public int visibleUnitCount;
	public SolidObject[] visibleUnit;

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
	
	
	public LightTank[] LightTanks;
	public HeavyTank[] HeavyTanks;
	public PalmTree[] trees;
	public int plamTreeCount;
	public PowerPlant[] PowerPlants;
	public Refinery[] refineries;
	public RocketTank[] RocketTanks;
	public Harvester[] Harvesters;
	public GoldMine[] goldMines;
	public ConstructionVehicle[] constructionVehicles;
	public ConstructionYard[] constructionYards;
	public Factory[] factories;
	public Drone[] drones;
	public CommunicationCenter[] communicationCenters;
	public TechCenter[] TechCenters;
	public StealthTank[] stealthTanks;
	public GunTurrent[] GunTurrents;
	public MissileTurret[] MissileTurrets;
	
	public terrain Terrain;
	
	public Bullet[] Bullets;
	public Rocket[] Rockets;
	public  polygon3D[] visionPolygon;
	
	public int numberOfPlayerBuildings;
	public int numberOfAIBuildings;
	
	public int screen_width;
	public int screen_height;
	public int screen_size;
	
	
	public void init(){
		
		screen_width = MainThread.screen_width;
		screen_height = MainThread.screen_height;
		screen_size = MainThread.screen_size;
		
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
		
		goldMines = new GoldMine[16];
		goldMines[0] = new GoldMine(2f,-0.515f, 1.25f, 45000);
		goldMines[1] = new GoldMine(9.5f,-0.515f, 5.5f, 45000);
		goldMines[2] = new GoldMine(2f,-0.515f, 28.25f, 60000);
		goldMines[3] = new GoldMine(26f,-0.515f, 3.5f, 60000);
		goldMines[4] = new GoldMine(29.75f,-0.515f, 30f, 45000);
		goldMines[5] = new GoldMine(22.5f,-0.515f, 25.5f, 45000);
		goldMines[6] = new GoldMine(15.75f,-0.515f, 18f, 60000);
		goldMines[7] = new GoldMine(16.25f,-0.515f, 12.25f, 60000);
		
		
		//create trees from bitmap
		trees = new PalmTree[2048];
		short[] treeBitmap = MainThread.textures[56].pixelData;
		for(int i = 0; i < 114; i++){
			for(int j = 0; j < 114; j++){
				if((treeBitmap[j + i * 128]<<10) <128 && plamTreeCount < trees.length){
					trees[plamTreeCount] = new PalmTree(j*0.28f, -0.3f, (113 -i)*0.28f);
					plamTreeCount++;
				}
				
			}
		}	
		
		visibleUnit = new SolidObject[400];
		
		MainThread.playerCommander = new PlayerCommander();
		MainThread.enemyCommander = new EnemyCommander();
		
	}
	
	
	public void prepareAssetForNewGame(){

		Camera.position.x = 3;
		Camera.position.z = -1.25f;
		Camera.view_Direction.set(0, 0, 1);
		Camera.XZ_angle = 0;
		
		
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
		
		LightTanks = new LightTank[768];
		HeavyTanks = new HeavyTank[256];
		PowerPlants = new PowerPlant[256];
		refineries = new Refinery[128];
		RocketTanks = new RocketTank[512];
		Harvesters = new Harvester[128];
		constructionVehicles = new ConstructionVehicle[64];
		constructionYards = new ConstructionYard[64];
		factories = new Factory[128];
		drones = new Drone[384];
		communicationCenters = new CommunicationCenter[128];
		TechCenters = new TechCenter[64];
		stealthTanks = new StealthTank[384];
		GunTurrents = new GunTurrent[512];
		MissileTurrets = new MissileTurret[256];
		
		goldMines[0].goldDeposite = goldMines[0].maxDeposite;
		goldMines[1].goldDeposite = goldMines[1].maxDeposite;
		goldMines[2].goldDeposite = goldMines[2].maxDeposite;
		goldMines[3].goldDeposite = goldMines[3].maxDeposite;
		goldMines[4].goldDeposite = goldMines[4].maxDeposite;
		goldMines[5].goldDeposite = goldMines[5].maxDeposite;
		goldMines[6].goldDeposite = goldMines[6].maxDeposite;
		goldMines[7].goldDeposite = goldMines[7].maxDeposite;
		
		Bullets = new Bullet[200];
		for(int i = 0; i < 200; i ++){
			Bullets[i] = new Bullet();
			
		}
		
		Rockets = new Rocket[200];
		for(int i = 0; i < 200; i ++){
			Rockets[i] = new Rocket();
			
		}

		MainThread.playerCommander.init();
		MainThread.enemyCommander.init();
		
		LightTank.tileCheckList_player = SolidObject.generateTileCheckList(5f);
		LightTank.tileCheckList_enemy = SolidObject.generateTileCheckList(5f);
		TechCenter.resetResarchStatus();
		CommunicationCenter.resetResearchStatus();
		
		
		addConstructionVehicle(new ConstructionVehicle(new vector(3.125f,-0.3f, 2.125f), 90, 0));
		addConstructionVehicle(new ConstructionVehicle(new vector(29.625f,-0.3f, 28.875f), 90, 1));
		constructionVehicles[1].expand();
		
		numberOfPlayerBuildings = 1;
	    numberOfAIBuildings = 1;
		
		//testing only
		for(int i = 0; i < 6; i ++){
			
			for(int j = 0; j < 10; j++){
				
				//if(i == 0) {
					//RocketTank l = new RocketTank(new vector(j*0.25f+ 1.125f,-0.3f, 22.125f - i*0.25f), 90, 1);
					//l.damageMultiplier =2;
					//addRocketTank(l);
				//techCenter.rocketTankResearched_enemy = true;
				
				//}else {
					//HeavyTank l = new HeavyTank(new vector(j*0.25f+ 1.125f,-0.3f, 22.125f - i*0.25f), 90, 1);
					
					//addHeavyTank(l);
				//}
				
				
			}
		}
		
		for(int i = 0; i < 10; i ++){
			
			for(int j = 0; j < 6; j++){ 
				//Harvester l = new Harvester(new vector(i*0.25f+ 1.125f,-0.3f, 17.375f - 0.25f*j), 90, 0);
				//addHarvester(l);
				//l.hasMultiShotUpgrade = true;
				//LightTank l = new LightTank(new vector(i*0.25f + 27.125f,-0.3f, 0.5f + 26.625f + j*0.25f), 90, 0);
				
				//l.attackRange = 1.99f;
		
				//LightTank.tileCheckList_player = LightTank.generateTileCheckList(6);
		
				//addLightTank(l);
				//if(j == 0 && i == 0)
				//addMissileTurret(new MissileTurret(i*0.25f -0.125f + 1, -0.65f, 0.25f + 17.125f + j*0.25f, 0));
			
			}
		}
	}
	
	public void destoryAsset() {
		Camera.view_Direction.set(0, 0, 1);
		Camera.frameIndex = 0;
		Camera.XZ_angle = 0;
		
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
		
		LightTanks = null;
		HeavyTanks = null;
		PowerPlants = null;
		refineries = null;
		RocketTanks = null;
		Harvesters = null;
		constructionVehicles = null;
		constructionYards = null;
		factories = null;
		drones = null;
		communicationCenters = null;
		TechCenters = null;
		stealthTanks = null;
		GunTurrents = null;
		MissileTurrets = null;
		
		goldMines[0].goldDeposite = goldMines[0].maxDeposite;
		goldMines[1].goldDeposite = goldMines[1].maxDeposite;
		goldMines[2].goldDeposite = goldMines[2].maxDeposite;
		goldMines[3].goldDeposite = goldMines[3].maxDeposite;
		goldMines[4].goldDeposite = goldMines[4].maxDeposite;
		goldMines[5].goldDeposite = goldMines[5].maxDeposite;
		goldMines[6].goldDeposite = goldMines[6].maxDeposite;
		goldMines[7].goldDeposite = goldMines[7].maxDeposite;
		
		Bullets = null;
		Rockets = null;
		
		MainThread.gridMap.reset();
		SolidObject.globalUniqID = 0;
		
		postProcessingThread.reset();
		
		System.gc(); 
	}
	
	
	public void addContructionYard(ConstructionYard o){
		for(int i = 0; i < constructionYards.length; i++){
			if(constructionYards[i] == null){
				constructionYards[i] = o;
				break;
			}
		}
	}
	
	public void addPowerPlant(PowerPlant o){
		for(int i = 0; i < PowerPlants.length; i++){
			if(PowerPlants[i] == null){
				PowerPlants[i] = o;
				break;
			}
		}
	}
	
	public void addRefinery(Refinery o){
		for(int i = 0; i < refineries.length; i++){
			if(refineries[i] == null){
				refineries[i] = o;
				break;
			}
		}
	}
	
	public void addFactory(Factory o){
		for(int i = 0; i < factories.length; i++){
			if(factories[i] == null){
				factories[i] = o;
				break;
			}
		}
	}
	
	public void addDrone(Drone o){
		for(int i = 0; i < drones.length; i++){
			if(drones[i] == null){
				drones[i] = o;
				break;
			}
		}
	}
	
	public void addCommunicationCenter(CommunicationCenter o){
		for(int i = 0; i < communicationCenters.length; i++){
			if(communicationCenters[i] == null){
				communicationCenters[i] = o;
				break;
			}
		}
	}
	
	public void addTechCenter(TechCenter o){
		for(int i = 0; i < TechCenters.length; i++){
			if(TechCenters[i] == null){
				TechCenters[i] = o;
				break;
			}
		}
	}
	
	public void addHarvester(Harvester o){
		for(int i = 0; i < Harvesters.length; i++){
			if(Harvesters[i] == null){
				Harvesters[i] = o;
				break;
			}
		}
	}
	
	public void addLightTank(LightTank o){
		for(int i = 0; i < LightTanks.length; i++){
			if(LightTanks[i] == null){
				LightTanks[i] = o;
				break;
			}
		}
	}
	
	public void addHeavyTank(HeavyTank o){
		for(int i = 0; i < HeavyTanks.length; i++){
			if(HeavyTanks[i] == null){
				HeavyTanks[i] = o;
				break;
			}
		}
	}
	
	public void addStealthTank(StealthTank o){
		for(int i = 0; i < stealthTanks.length; i++){
			if(stealthTanks[i] == null){
				stealthTanks[i] = o;
				break;
			}
		}
	}
	
	public void addRocketTank(RocketTank o){
		for(int i = 0; i < RocketTanks.length; i++){
			if(RocketTanks[i] == null){
				RocketTanks[i] = o;
				break;
			}
		}
	}
	
	public void addConstructionVehicle(ConstructionVehicle o){
		for(int i = 0; i < constructionVehicles.length; i++){
			if(constructionVehicles[i] == null){
				constructionVehicles[i] = o;
				break;
			}
		}
	}
	
	public void addGunTurret(GunTurrent o){
		for(int i = 0; i < GunTurrents.length; i++){
			if(GunTurrents[i] == null){
				GunTurrents[i] = o;
				break;
			}
		}
	}
	
	public void addMissileTurret(MissileTurret o){
		for(int i = 0; i < MissileTurrets.length; i++){
			if(MissileTurrets[i] == null){
				MissileTurrets[i] = o;
				break;
			}
		}
	}
	
	public void destoryAllUnit(int teamNo) {
		for(int i = 0; i < LightTanks.length; i++){
			if(LightTanks[i] != null && LightTanks[i].teamNo == teamNo){
				LightTanks[i].currentHP = 0;
				LightTanks[i].attacker = goldMines[0];
			}
		}
		
		for(int i = 0; i < RocketTanks.length; i++){
			if(RocketTanks[i] != null && RocketTanks[i].teamNo == teamNo){
				RocketTanks[i].currentHP = 0;
				RocketTanks[i].attacker = goldMines[0];
			}
		}
		
		for(int i = 0; i < stealthTanks.length; i++){
			if(stealthTanks[i] != null && stealthTanks[i].teamNo == teamNo){
				stealthTanks[i].currentHP = 0;
				stealthTanks[i].attacker = goldMines[0];
			}
		}
		
		for(int i = 0; i < HeavyTanks.length; i++){
			if(HeavyTanks[i] != null && HeavyTanks[i].teamNo == teamNo){
				HeavyTanks[i].currentHP = 0;
				HeavyTanks[i].attacker = goldMines[0];
			}
		}
		
		for(int i = 0; i < Harvesters.length; i++){
			if(Harvesters[i] != null && Harvesters[i].teamNo == teamNo){
				Harvesters[i].currentHP = 0;
				Harvesters[i].attacker = goldMines[0];
			}
		}
	}
	
	public void updateAndDraw(){
		
		
		//check end game condition
		//game ends when either player or the AI have lost all the buildings and construction vehicles
		if(MainThread.gameStarted) {
			if(!MainThread.playerVictory && !MainThread.AIVictory && !MainThread.afterMatch) {
				if(numberOfAIBuildings == 0) {
					MainThread.playerVictory = true;
					MainThread.gamePaused = true;
					destoryAllUnit(1);
				}else if(numberOfPlayerBuildings == 0) {
					MainThread.AIVictory = true;
					MainThread.gamePaused = true;
					destoryAllUnit(0);
				}
				
				
			}
			
			if(MainThread.AIVictory || MainThread.playerVictory)
				return;
		}
		
		
		polygonCount = 0;
		visibleUnitCount = 0;
		visionPolygonCount = 0;
		unitsForMiniMapCount = 0;
		explosionCount = 0;
		smokeEmmiterCount = 0;
		helixCount = 0;
		
		
		
		if(MainThread.gameStarted) {
			for(int i = 0; i < LightTanks.length; i++){
				if(LightTanks[i] != null)
					LightTanks[i].update();
			}
			
			for(int i = 0; i < HeavyTanks.length; i++){
				if(HeavyTanks[i] != null)
					HeavyTanks[i].update();
			}
			
			for(int i = 0; i < stealthTanks.length; i++){
				if(stealthTanks[i] != null)
					stealthTanks[i].update();
			}
			
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null)
					constructionYards[i].update();
			}
			
			for(int i = 0; i < PowerPlants.length; i++){
				if(PowerPlants[i] != null)
					PowerPlants[i].update();
			}
			
			for(int i = 0; i < TechCenters.length; i++){
				if(TechCenters[i] != null)
					TechCenters[i].update();
			}
			
			for(int i = 0; i < GunTurrents.length; i++){
				if(GunTurrents[i] != null)
					GunTurrents[i].update();
			}
			
			for(int i = 0; i < MissileTurrets.length; i++){
				if(MissileTurrets[i] != null)
					MissileTurrets[i].update();
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
			
			
			for(int i = 0; i < RocketTanks.length; i++){
				if(RocketTanks[i] != null)
					RocketTanks[i].update();
			}
			
			for(int i = 0; i < Harvesters.length; i++){
				if(Harvesters[i] != null)
					Harvesters[i].update();
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
		if(MainThread.gameStarted) {
			numberOfPlayerBuildings = 0;
			numberOfAIBuildings = 0;
			
			int start = (screen_height-131) * screen_width + 3;
			int start2 = (screen_height-131) * screen_width + (screen_width-133);
			for(int y = 0; y < 131; y++){
				for(int x = 0; x < 128; x ++){
					MainThread.zBuffer[start + x + y*screen_width]	= Integer.MAX_VALUE;
					MainThread.zBuffer[start2 + x + y*screen_width]	= Integer.MAX_VALUE;
				}
			}
		
			for(int i = 0; i < 200; i ++)
				Bullets[i].updateAndDraw();
			
			for(int i = 0; i < 200; i ++)
				Rockets[i].update();
			
	
			for(int i = 0; i < LightTanks.length; i++){
				if(LightTanks[i] != null)
					LightTanks[i].draw();
			}
			
			for(int i = 0; i < HeavyTanks.length; i++){
				if(HeavyTanks[i] != null)
					HeavyTanks[i].draw();
			}
			
			for(int i = 0; i < stealthTanks.length; i++){
				if(stealthTanks[i] != null)
					stealthTanks[i].draw();
			}
			
			for(int i = 0; i < PowerPlants.length; i++){
				if(PowerPlants[i] != null) {
					PowerPlants[i].draw();
					if(PowerPlants[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < GunTurrents.length; i++){
				if(GunTurrents[i] != null) {
					GunTurrents[i].draw();
					if(GunTurrents[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < MissileTurrets.length; i++){
				if(MissileTurrets[i] != null) {
					MissileTurrets[i].draw();
					if(MissileTurrets[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < communicationCenters.length; i++){
				if(communicationCenters[i] != null) {
					communicationCenters[i].draw();
					if(communicationCenters[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < TechCenters.length; i++){
				if(TechCenters[i] != null) {
					TechCenters[i].draw();
					if(TechCenters[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < refineries.length; i++){
				if(refineries[i] != null) {
					refineries[i].draw();
					if(refineries[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < RocketTanks.length; i++){
				if(RocketTanks[i] != null)
					RocketTanks[i].draw();
			}
			
			for(int i = 0; i < Harvesters.length; i++){
				if(Harvesters[i] != null)
					Harvesters[i].draw();
			}
			
			for(int i = 0; i < constructionVehicles.length; i++){
				if(constructionVehicles[i] != null) {
					constructionVehicles[i].draw();
					if(constructionVehicles[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < constructionYards.length; i++){
				if(constructionYards[i] != null) {
					constructionYards[i].draw();
					if(constructionYards[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
			
			for(int i = 0; i < 200; i ++)
				Rockets[i].draw();
			
			
			for(int i = 0; i < drones.length; i++){
				if(drones[i] != null)
					drones[i].draw();
			}
			
			for(int i = 0; i < factories.length; i++){
				if(factories[i] != null) {
					factories[i].draw();
					if(factories[i].teamNo == 0)
						numberOfPlayerBuildings++;
					else
						numberOfAIBuildings++;
				}
			}
		}
		
		for(int i = 0; i < goldMines.length; i++){
			if(goldMines[i] != null)
				goldMines[i].draw();
		}
		
		for(int i = 0; i < plamTreeCount; i++)
			trees[i].draw();
		
		Terrain.draw();
		
	
		if(MainThread.gameStarted) {
			if(MainThread.playerCommander.selectedConstructionYard != null){
				MainThread.playerCommander.selectedConstructionYard.drawDeploymentGrid();
			}
			
			for(int i = 0; i < factories.length; i++){
				if(factories[i] != null)
					factories[i].drawRallyPointLine();
			}
			
			
			//prepare selected unit list
			for(int i = 0; i < 99; i++){
				if(MainThread.playerCommander.selectedUnits[i] != null && MainThread.playerCommander.selectedUnits[i].isSelectable){
					selectedUnitsInfo[i][0] =  MainThread.playerCommander.selectedUnits[i].level << 16 | MainThread.playerCommander.selectedUnits[i].groupNo << 8 | MainThread.playerCommander.selectedUnits[i].type;
					selectedUnitsInfo[i][1] = (int) MainThread.playerCommander.selectedUnits[i].tempCentre.screenX;
					selectedUnitsInfo[i][2] = (int) MainThread.playerCommander.selectedUnits[i].tempCentre.screenY;
					if(MainThread.playerCommander.selectedUnits[i].type == 199){
						selectedUnitsInfo[i][1] = (int) MainThread.playerCommander.selectedUnits[i].screenX_gui;
						selectedUnitsInfo[i][2] = (int) MainThread.playerCommander.selectedUnits[i].screenY_gui;
					}
					
					selectedUnitsInfo[i][3] = (int) MainThread.playerCommander.selectedUnits[i].type;
					selectedUnitsInfo[i][4] = MainThread.playerCommander.selectedUnits[i].currentHP;
					selectedUnitsInfo[i][5] = MainThread.playerCommander.selectedUnits[i].progressStatus;
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
	
	//spawn a Bullet
	public void spawnBullet(int angle, int damage, SolidObject target, vector centre, SolidObject attacker){
		for(int i = 0; i < 200; i ++)
			if(!Bullets[i].isInAction){
				Bullets[i].setActive(angle, damage, target, centre, attacker);
				break;
			}
	}
	
	//spawn a Rocket
	public void spawnRocket(int angle, int damage, SolidObject target, vector centre, SolidObject attacker){
		for(int i = 0; i < 200; i ++)
			if(!Rockets[i].isInAction){
				Rockets[i].setActive(angle, damage, target, centre, attacker);
				break;
			}
	}
	
	//remove object that ceased to exist (e.g get destroyed)
	public void removeObject(SolidObject o){
		
		MainThread.playerCommander.removeDestoryedObjectFromSelection(o);
		for(int i = 0; i < LightTanks.length; i++){
			if(LightTanks[i] == o){
				LightTanks[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < stealthTanks.length; i++){
			if(stealthTanks[i] == o){
				stealthTanks[i] = null;
				return;
			}
		}
			
		for(int i = 0; i < PowerPlants.length; i++){
			if(PowerPlants[i] == o){
				PowerPlants[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < TechCenters.length; i++){
			if(TechCenters[i] == o){
				TechCenters[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < GunTurrents.length; i++){
			if(GunTurrents[i] == o){
				GunTurrents[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < MissileTurrets.length; i++){
			if(MissileTurrets[i] == o){
				MissileTurrets[i] = null;
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
		
		for(int i = 0; i < RocketTanks.length; i++){
			if(RocketTanks[i] == o){
				RocketTanks[i] = null;
				return;
			}
		}
		
		for(int i = 0; i < HeavyTanks.length; i++){
			if(HeavyTanks[i] == o){
				HeavyTanks[i] = null;
				return;
			}
		}
			
		for(int i = 0; i < Harvesters.length; i++){
			if(Harvesters[i] == o){
				Harvesters[i] = null;
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
