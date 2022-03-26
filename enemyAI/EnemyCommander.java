package enemyAI;

import core.BaseInfo;
import core.MainThread;

public class EnemyCommander {
	
	//vision map represents the vision of the  enemy commander
	public static boolean[] visionMap;
	
	public static boolean[] tempBitmap;
	
	public BaseInfo theBaseInfo;
	
	public BuildingManagerAI theBuildingManagerAI;
	public EconomyManagerAI theEconomyManagerAI;
	public MapAwarenessAI theMapAwarenessAI;
	public UnitProductionAI theUnitProductionAI;
	public BaseExpensionAI theBaseExpentionAI;
	public ScoutingManagerAI theScoutingManagerAI;
	public DefenseManagerAI theDefenseManagerAI;
	public CombatManagerAI theCombatManagerAI;
	public MicroManagementAI theMicroManagementAI;
	public HarassmentAI theHarassmentAI;
	public int difficulty;
	
	public int frameAI;
	
	public void init(){
		
		//init vision map
		visionMap = new boolean[128*128];
	
		tempBitmap = new boolean[148 * 148];
		
		theBaseInfo = new BaseInfo();
		
		theBuildingManagerAI = new BuildingManagerAI();
		theEconomyManagerAI = new EconomyManagerAI();
		theMapAwarenessAI = new MapAwarenessAI();
		theUnitProductionAI = new UnitProductionAI();
		theBaseExpentionAI = new BaseExpensionAI();
		theScoutingManagerAI = new ScoutingManagerAI();
		theDefenseManagerAI = new DefenseManagerAI();
		theCombatManagerAI = new CombatManagerAI();
		theMicroManagementAI = new MicroManagementAI();
		theHarassmentAI = new HarassmentAI();
		
	}
	
	
	public void update(){
		theBaseInfo.update();
		
		
		//generate visionMap based on the map information of the previous frame
		for(int y = 0; y < 128; y++){
			for(int x = 0; x < 128; x++){
				visionMap[x + y*128] = tempBitmap[x + 10 + (y + 10)*148];
			}
		}
		
		
		//process high level enemy AI
		thinkHardLikeHumanPlayer();
		
		
		//drawVisionMap();
		
		//reset tempBitmap;
		for(int i = 0 ;i < tempBitmap.length; i++){
			tempBitmap[i] = false;
		}
	}
	
	public void drawVisionMap(){
		int pos = 2 + 20 * 768;
		boolean tile;
		int[] screen = MainThread.screen2;
		for(int i = 0; i < 128; i++){
			for(int j = 0; j < 128; j++){
				tile = visionMap[j + i*128];
				if(!tile)
					screen[pos + j + i*768] = 0; 
				
			}
		}
	}
	
	
	public void thinkHardLikeHumanPlayer(){
		frameAI = MainThread.gameFrame/30;
		
		//the order is important!!
		if(MainThread.gameFrame % 30 == 0){
			theMapAwarenessAI.processAI();
		}
		
		if(MainThread.gameFrame % 30 == 1){
			theBuildingManagerAI.processAI();
		} 
		
		if(MainThread.gameFrame % 30 == 2){
			theEconomyManagerAI.processAI();
		}
		
		if(MainThread.gameFrame % 30 == 3){
			if(difficulty > 0)
				theScoutingManagerAI.processAI();
		}
		
		if(MainThread.gameFrame % 30 == 4){
			theUnitProductionAI.processAI();
		}
		
		if(MainThread.gameFrame % 30 == 5){
			theBaseExpentionAI.processAI();
		}
		
		if(MainThread.gameFrame % 30 == 6){
			theCombatManagerAI.processAI();   
		}
		
		if(MainThread.gameFrame % 30 == 7){
			if(difficulty > 0)
				theDefenseManagerAI.processAI();
		}
		
		
			
		
		
		if(difficulty == 2){
			theHarassmentAI.processAI();
			theMicroManagementAI.processAI();
		}
		
	}
	
}
