//This AI agent will perform hit and run tactics against the player.
//It will try to harass plahyer's mineral line, and destroy player's building from a distance.
//It will most likely act at the same time when the AI's main attack force is launching an attack, 

package enemyAI;

import core.baseInfo;
import core.mainThread;

public class harassmentAI {
	
	public baseInfo theBaseInfo;
	
	public int gameTime;

	
	public harassmentAI(){
		this.theBaseInfo = mainThread.ec.theBaseInfo;
		
		
	}
	
	public void processAI(){
		
		gameTime++;
	
		
	}
	
	
}
