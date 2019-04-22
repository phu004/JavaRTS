package enemyAI;

import core.baseInfo;
import core.mainThread;

public class enemyCommander {
	
	//vision map represents the vision of the  enemy commander
	public static boolean[] visionMap;
	
	public static boolean[] tempBitmap;
	
	public baseInfo theBaseInfo;
	
	public buildingManagerAI theBuildingManagerAI;
	public economyManagerAI  theEconomyManagerAI;
	public mapAwarenessAI theMapAwarenessAI;
	public unitProductionAI theUnitProductionAI;
	public baseExpensionAI theBaseExpentionAI;
	public scoutingManagerAI theScoutingManagerAI;
	public defenseManagerAI theDefenseManagerAI;
	public combatManagerAI theCombatManagerAI;
	public microManagementAI theMicroManagementAI;
	public harassmentAI theHarassmentAI;
	public int difficulty;
	
	
	public void init(){
		
		//init vision map
		visionMap = new boolean[128*128];
	
		tempBitmap = new boolean[148 * 148];
		
		theBaseInfo = new baseInfo();
		
		theBuildingManagerAI = new buildingManagerAI();
		theEconomyManagerAI = new economyManagerAI();
		theMapAwarenessAI = new mapAwarenessAI();
		theUnitProductionAI = new unitProductionAI();
		theBaseExpentionAI = new baseExpensionAI();
		theScoutingManagerAI = new scoutingManagerAI();
		theDefenseManagerAI = new defenseManagerAI();
		theCombatManagerAI = new combatManagerAI();
		theMicroManagementAI = new microManagementAI();
		theHarassmentAI = new harassmentAI();
		
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
		
		
		drawVisionMap();
		
		//reset tempBitmap;
		for(int i = 0 ;i < tempBitmap.length; i++){
			tempBitmap[i] = false;
		}
	}
	
	public void drawVisionMap(){
		int pos = 2 + 20 * 768;
		boolean tile;
		int[] screen = mainThread.screen2;
		for(int i = 0; i < 128; i++){
			for(int j = 0; j < 128; j++){
				tile = visionMap[j + i*128];
				if(!tile)
					screen[pos + j + i*768] = 0; 
				
			}
		}
	}
	
	
	public void thinkHardLikeHumanPlayer(){
		//the order is important!!
		if(mainThread.gameFrame % 30 == 0){
			theMapAwarenessAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 1){
			theBuildingManagerAI.processAI();
		} 
		
		if(mainThread.gameFrame % 30 == 2){
			theEconomyManagerAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 3){
			theScoutingManagerAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 4){
			theUnitProductionAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 5){
			theBaseExpentionAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 6){
			theCombatManagerAI.processAI();   
		}
		
		if(mainThread.gameFrame % 30 == 7){
			theDefenseManagerAI.processAI();
		}
		
		if(mainThread.gameFrame % 30 == 8) {
			theHarassmentAI.processAI();
		}
		
		//if(mainThread.frameIndex % 5  == 0){
			theMicroManagementAI.processAI();
		//}
		
	}
	
}
