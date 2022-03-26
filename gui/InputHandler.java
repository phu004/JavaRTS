package gui;

//handles all the logic for user input

import java.awt.*;

import core.Camera;
import core.Geometry;
import core.MainThread;

public class InputHandler {

	public static int mouse_x, mouse_y,mouse_x0, mouse_x1, mouse_y0, mouse_y1, cameraMovementAngle;
	
	public static boolean mouseIsInsideScreen, userIsHoldingA, userIsHoldingC, userIsHoldingF;
	
	public static boolean leftKeyPressed, 
	                      rightKeyPressed, 
	                      controlKeyPressed, 
	                      leftMouseButtonPressed, 
	                      rightMouseButtonPressed, 
	                      leftMouseButtonReleased, 
	                      rightMouseButtonReleased, 
	                      S_pressed,
	                      A_pressed,
	                      C_pressed,
	                      F_pressed,
	                      escapeKeyPressed,
	                      escapeKeyReleased;
	            
	                  
	public static int numberTyped;
	
	public static final Rectangle mouseMovementArea = new Rectangle(30,20, 708, 472);
	
	public static char[] inputBuffer = new char[1024];
	public static char[] keyReleaseBuffer = new char[1024];
	
	public static int inputCounter, inputBufferIndex, keyReleaseCounter, keyReleaseBufferIndex;
	
	public static int escapePressedCooldown;
	
	public static int screen_width = MainThread.screen_width;
	public static int screen_height = MainThread.screen_height;
	
	public static void processInput(){
		
		if(escapePressedCooldown > 0)
			escapePressedCooldown --;
		
		if(MainThread.capturedMouse)
			mouseIsInsideScreen = true;
		
		//read input char
		int theCounter = inputCounter;  
		
		MainThread.currentInputChar = 255;
		
		//handle over flow
		if(inputBufferIndex > theCounter){
			while(inputBufferIndex < 1024){
				char c = inputBuffer[inputBufferIndex];
				MainThread.currentInputChar = c;
				
				
				if(c == 's' || c == 'S'){
					
					S_pressed = true;
				}
				
				if(c == 'a' || c == 'A'){
					A_pressed = true;
				}
				
				if(c == 'c' || c == 'C'){
					
					C_pressed = true;
				}
				
				if(c == 'f' || c == 'F'){
					F_pressed = true;
				}
				
				
				if(c >=49 && c <=53){
					numberTyped = c - 48;
				}
				
				
				inputBufferIndex++;
				
			}
			inputBufferIndex = 0;
		}
		
		
		while(inputBufferIndex < theCounter){
			char c = inputBuffer[inputBufferIndex];
			MainThread.currentInputChar = c;
			
			if(c == 's' || c == 'S'){
				S_pressed = true;
			}
			
			if(c == 'a' || c == 'A'){
				A_pressed = true;
			}
			
			if(c == 'c' || c == 'C'){
				
				C_pressed = true;
			}
			
			if(c == 'f' || c == 'F'){
				F_pressed = true;
			}
			
			if(c >=49 && c <=53){
				numberTyped = c - 48;
			}
			
			inputBufferIndex++;
		}
	
		
		//read released char
		theCounter = keyReleaseCounter;
		//handle over flow
		if(keyReleaseBufferIndex > theCounter){
			while(keyReleaseBufferIndex < 1024){
				char c = keyReleaseBuffer[keyReleaseBufferIndex];
				
				if(c == 's' || c == 'S'){
					S_pressed = false;
				}
				
				if(c == 'a' || c == 'A'){
					A_pressed = false;
					userIsHoldingA = false;
					
				}
				if(c == 'c' || c == 'C'){
					C_pressed = false;
					userIsHoldingC = false;
					
				}
				if(c == 'f' || c == 'F'){
					F_pressed = false;
					userIsHoldingF = false;
				}
				keyReleaseBufferIndex++;
			}
			keyReleaseBufferIndex = 0;
		}
		while(keyReleaseBufferIndex < theCounter){
			char c = keyReleaseBuffer[keyReleaseBufferIndex];
			
			if(c == 's' || c == 'S'){
				S_pressed = false;
			}
			
			if(c == 'a' || c == 'A'){
				A_pressed = false;
				userIsHoldingA = false;
			}
			if(c == 'c' || c == 'C'){
				C_pressed = false;
				userIsHoldingC = false;
				
			}
			if(c == 'f' || c == 'F'){
				F_pressed = false;
				userIsHoldingF = false;
			}
			
			keyReleaseBufferIndex++;
		}
		
		
		
		//handle input when game is running
		if(!MainThread.gamePaused && MainThread.gameStarted){
			Camera.MOVE_LEFT = false;
			Camera.MOVE_RIGHT = false;
			Camera.MOVE_UP = false;
			Camera.MOVE_DOWN = false;
			Camera.TURN_LEFT = false;
			Camera.TURN_RIGHT = false;
			
			if(!MainThread.playerCommander.isSelectingUnit){
				mouse_x0 = mouse_x;
				mouse_y0 = mouse_y;
				if(!mouseIsInsideScreen || !mouseMovementArea.contains(mouse_x0, mouse_y0)){
					
					if(MainThread.playerCommander.cursorIsInMiniMap() || MainThread.playerCommander.cursorIsInSideBar() || MainThread.capturedMouse){
						if(mouse_x0 < 10)
							Camera.MOVE_LEFT = true;
						if(mouse_x0 > screen_width-10)
							Camera.MOVE_RIGHT = true;
						if(mouse_y0 < 10)
							Camera.MOVE_UP = true;
						if(mouse_y0 > screen_height-10)
							Camera.MOVE_DOWN = true;
						
					}else{
					
						if(mouse_x0 < 40)
							Camera.MOVE_LEFT = true;
						if(mouse_x0 > screen_width - 40)
							Camera.MOVE_RIGHT = true;
						if(mouse_y0 < 40)
							Camera.MOVE_UP = true;
						if(mouse_y0 > screen_height-40)
							Camera.MOVE_DOWN = true;
					}
					
					
					if(Camera.MOVE_LEFT || Camera.MOVE_RIGHT || Camera.MOVE_UP || Camera.MOVE_DOWN){
						int angle = Geometry.findAngle(mouse_x0, mouse_y0, mouse_x1, mouse_y1);
						if(angle != 0){
							cameraMovementAngle = angle;
						}
						
					
						if(mouse_x0 < 250*screen_width/768 && mouse_y0 > 362*screen_height/512 && cameraMovementAngle > 105 && cameraMovementAngle < 165){
							Camera.MOVE_LEFT = true;
							Camera.MOVE_DOWN = true;
						}
						
						if(mouse_x0 < 250*screen_width/768 && mouse_y0 < 150*screen_height/512 && cameraMovementAngle < 75){
							Camera.MOVE_LEFT = true;
							Camera.MOVE_UP = true;
						}
						
						if(mouse_x0 > 518*screen_width/768 && mouse_y0 < 150*screen_height/512 && cameraMovementAngle > 285){
							Camera.MOVE_RIGHT = true;
							Camera.MOVE_UP = true;
						}
						
						if(mouse_x0 > 518*screen_width/768 && mouse_y0 > 362*screen_height/512 && cameraMovementAngle < 255 && cameraMovementAngle > 195){
							Camera.MOVE_RIGHT = true;
							Camera.MOVE_DOWN = true;
						}
						
					}
				}
				
				
				if(leftKeyPressed){
					Camera.TURN_LEFT = true;
				}
				
				if(rightKeyPressed){
					Camera.TURN_RIGHT = true;
				}
				
				mouse_x1 = mouse_x0;
				mouse_y1 = mouse_y0;
			}
		
			
			if(controlKeyPressed){
				MainThread.playerCommander.controlKeyPressed = true;
			}
			
			if(numberTyped != 0){
				MainThread.playerCommander.numberTyped = numberTyped;
				
			}
			
			//handles left click
			if(leftMouseButtonPressed){
				MainThread.playerCommander.leftMouseButtonPressed = true;
				
			}
			
			if(leftMouseButtonReleased){
				MainThread.playerCommander.leftMouseButtonReleased = true;
			}
			
			//handles right click
			if(rightMouseButtonPressed){
				MainThread.playerCommander.rightMouseButtonPressed = true;
			}
			
			if(rightMouseButtonReleased){
				MainThread.playerCommander.rightMouseButtonReleased = true;
			}
			
			//handle hotheys
			if(S_pressed){
				
				MainThread.playerCommander.holdKeyPressed = true;
			}
			
			
			//handle hotheys
			if(A_pressed){
				if(!userIsHoldingA){
					MainThread.playerCommander.attackKeyPressed = true;
					userIsHoldingA = true;
				}
				
			}
			if(C_pressed) {
				if(!userIsHoldingC) {
					MainThread.playerCommander.toggleConyard = true;
					userIsHoldingC = true;
				}
			}
			if(F_pressed) {
				if(!userIsHoldingF) {
					MainThread.playerCommander.toggleFactory = true;
					userIsHoldingF = true;
				}
			}
			
			
			//handle escape key
			if(escapeKeyPressed && escapePressedCooldown == 0 && MainThread.menuStatus != MainThread.helpMenu) {
				MainThread.gamePaused = true;  //if game is running, pause the game when esc key is hit
				escapePressedCooldown = 5;
				
			}
			
		}else {
			//handle event when game is paused
			if(((escapeKeyPressed && escapePressedCooldown == 0)|| MainThread.buttonAction == "unpauseGame")
					&& MainThread.gamePaused && MainThread.gameStarted
					&& MainThread.menuStatus != MainThread.helpMenu
					&& MainThread.menuStatus != MainThread.optionMenu
					&& MainThread.menuStatus != MainThread.highscoreMenu) {
				
				if(!MainThread.AIVictory && ! MainThread.playerVictory) {
					MainThread.gamePaused = false; //if game is paused, unpause the game when esc key is hit
					escapePressedCooldown = 5;
				}
				
			}
			
			//quit the game when the quit Button is pressed
			if(!MainThread.gameStarted) {
				if(MainThread.buttonAction == "quitGame")
					System.exit(0);
				
				if(MainThread.buttonAction == "easyGame") {
					MainThread.gameStarted = true;
					MainThread.gameFrame = 0;
					MainThread.enemyCommander.difficulty = 0;
				}else if(MainThread.buttonAction == "normalGame") {
					MainThread.gameStarted = true;
					MainThread.gameFrame = 0;
					MainThread.enemyCommander.difficulty = 1;
				}else if(MainThread.buttonAction == "hardGame") {
					MainThread.gameStarted = true;
					MainThread.gameFrame = 0;
					MainThread.enemyCommander.difficulty = 2;
				}
				
				
				
				
				
				if(MainThread.buttonAction == "enableFogOfWar") {
					MainThread.fogOfWarDisabled = false;
					
				}else if(MainThread.buttonAction == "disableFogOfWar") {
					MainThread.fogOfWarDisabled = true;
					
				}
			}
			
			//abort current game when the abort Button is pressed
			if(MainThread.gameStarted && MainThread.buttonAction == "abortGame") {
				MainThread.gameStarted = false;
				MainThread.gameFrame = 0;
				MainThread.gamePaused = false;
				MainThread.AIVictory = false;
				MainThread.playerVictory = false;
				MainThread.afterMatch = false;
				MainThread.theAssetManager.destoryAsset();
				
				Camera.MOVE_LEFT = false;
				Camera.MOVE_RIGHT = false;
				Camera.MOVE_UP = false;
				Camera.MOVE_DOWN = false;
				Camera.TURN_LEFT = false;
				Camera.TURN_RIGHT = false;
			}
			
			if(MainThread.gameStarted && MainThread.buttonAction == "backToMap") {
				MainThread.AIVictory = false;
				MainThread.playerVictory = false;
				MainThread.afterMatch = true;
				MainThread.gamePaused = false;
				
			}
			
			//toggle mouse capture mode
			if(MainThread.buttonAction == "enableMouseCapture") {
				MainThread.capturedMouse = true;
			}else if(MainThread.buttonAction == "disableMouseCapture") {
				MainThread.capturedMouse = false;
			}
			

		}
		

		if(leftMouseButtonReleased)
			MainThread.leftMouseButtonReleased = true;
		
		if(escapeKeyPressed)
			MainThread.escapeKeyPressed = true;
		
		mouseIsInsideScreen = false;
		leftMouseButtonPressed = false;
		rightMouseButtonPressed = false;
		leftMouseButtonReleased = false;
		rightMouseButtonReleased = false;
		escapeKeyPressed = false;
		
		
		A_pressed = false;
		S_pressed = false;
		C_pressed = false;
		F_pressed = false;
		numberTyped = 0;
		
	}
	
	public static void readCharacter(char c){
		inputBuffer[inputCounter] = c;
		inputCounter++;
		if(inputCounter == 1024)
			inputCounter = 0;
		
		
	}
	
	public static void handleKeyRelease(char c){
		keyReleaseBuffer[keyReleaseCounter] = c;
		keyReleaseCounter++;
		if(keyReleaseCounter == 1024)
			keyReleaseCounter = 0;
	}
	
}
