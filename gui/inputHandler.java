package gui;

//handles all the logic for user input

import java.awt.*;

import core.camera;
import core.geometry;
import core.mainThread;

public class inputHandler {

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
	
	public static char[] inputBuffer = new char[64];
	public static char[] keyReleaseBuffer = new char[64];
	
	public static int inputCounter, inputBufferIndex, keyReleaseCounter, keyReleaseBufferIndex;
	
	public static int escapePressedCooldown;
	
	public static void processInput(){
		
		if(escapePressedCooldown > 0)
			escapePressedCooldown --;
		
		if(mainThread.capturedMouse)
			mouseIsInsideScreen = true;
		
		//read input char
		int theCounter = inputCounter;  
		
		mainThread.currentInputChar = 255;
			
		while(inputBufferIndex < theCounter){
			char c = inputBuffer[inputBufferIndex];
			mainThread.currentInputChar = c;
			
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
		
		//clear key input buffer
		inputBufferIndex = 0;
		inputCounter = 0;
		
		//read released char
		theCounter = keyReleaseCounter;
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
		
		//clear key release buffer
		keyReleaseBufferIndex = 0;
		keyReleaseCounter = 0;
		
		//handle input when game is running
		if(!mainThread.gamePaused && mainThread.gameStarted){
			camera.MOVE_LEFT = false;
			camera.MOVE_RIGHT = false;
			camera.MOVE_UP = false;
			camera.MOVE_DOWN = false;
			camera.TURN_LEFT = false;
			camera.TURN_RIGHT = false;
			
			if(!mainThread.pc.isSelectingUnit){
				mouse_x0 = mouse_x;
				mouse_y0 = mouse_y;
				if(!mouseIsInsideScreen || !mouseMovementArea.contains(mouse_x0, mouse_y0)){
					
					if(mainThread.pc.cursorIsInMiniMap() || mainThread.pc.cursorIsInSideBar() || mainThread.capturedMouse){
						if(mouse_x0 < 10)
							camera.MOVE_LEFT = true;
						if(mouse_x0 > 758)
							camera.MOVE_RIGHT = true;
						if(mouse_y0 < 10)
							camera.MOVE_UP = true;
						if(mouse_y0 > 502)
							camera.MOVE_DOWN = true;
						
					}else{
					
						if(mouse_x0 < 40)
							camera.MOVE_LEFT = true;
						if(mouse_x0 > 728)
							camera.MOVE_RIGHT = true;
						if(mouse_y0 < 40)
							camera.MOVE_UP = true;
						if(mouse_y0 > 472)
							camera.MOVE_DOWN = true;
					}
					
					
					if(camera.MOVE_LEFT || camera.MOVE_RIGHT || camera.MOVE_UP || camera.MOVE_DOWN){
						int angle = geometry.findAngle(mouse_x0, mouse_y0, mouse_x1, mouse_y1);
						if(angle != 0){
							cameraMovementAngle = angle;
						}
						
					
						if(mouse_x0 < 250 && mouse_y0 > 362 && cameraMovementAngle > 105 && cameraMovementAngle < 165){
							camera.MOVE_LEFT = true;
							camera.MOVE_DOWN = true;
						}
						
						if(mouse_x0 < 250 && mouse_y0 < 150 && cameraMovementAngle < 75){
							camera.MOVE_LEFT = true;
							camera.MOVE_UP = true;
						}
						
						if(mouse_x0 > 518 && mouse_y0 < 150 && cameraMovementAngle > 285){
							camera.MOVE_RIGHT = true;
							camera.MOVE_UP = true;
						}
						
						if(mouse_x0 > 518 && mouse_y0 > 362 && cameraMovementAngle < 255 && cameraMovementAngle > 195){
							camera.MOVE_RIGHT = true;
							camera.MOVE_DOWN = true;
						}
						
					}
				}
				
				
				if(leftKeyPressed){
					camera.TURN_LEFT = true;
				}
				
				if(rightKeyPressed){
					camera.TURN_RIGHT = true;
				}
				
				mouse_x1 = mouse_x0;
				mouse_y1 = mouse_y0;
			}
		
			
			if(controlKeyPressed){
				mainThread.pc.controlKeyPressed = true; 
			}
			
			if(numberTyped != 0){
				mainThread.pc.numberTyped = numberTyped;
				
			}
			
			//handles left click
			if(leftMouseButtonPressed){
				mainThread.pc.leftMouseButtonPressed = true;
				
			}
			
			if(leftMouseButtonReleased){
				mainThread.pc.leftMouseButtonReleased = true;
			}
			
			//handles right click
			if(rightMouseButtonPressed){
				mainThread.pc.rightMouseButtonPressed = true;
			}
			
			if(rightMouseButtonReleased){
				mainThread.pc.rightMouseButtonReleased = true;
			}
			
			//handle hotheys
			if(S_pressed){
				
				mainThread.pc.holdKeyPressed = true;
			}
			
			
			//handle hotheys
			if(A_pressed){
				if(!userIsHoldingA){
					mainThread.pc.attackKeyPressed = true;
					userIsHoldingA = true;
				}
				
			}
			if(C_pressed) {
				if(!userIsHoldingC) {
					mainThread.pc.toggleConyard = true;
					userIsHoldingC = true;
				}
			}
			if(F_pressed) {
				if(!userIsHoldingF) {
					mainThread.pc.toggleFactory = true;
					userIsHoldingF = true;
				}
			}
			
			
			//handle escape key
			if(escapeKeyPressed && escapePressedCooldown == 0 && mainThread.menuStatus != mainThread.helpMenu) {
				mainThread.gamePaused = true;  //if game is running, pause the game when esc key is hit
				escapePressedCooldown = 5;
				
			}
			
		}else {
			//handle event when game is paused
			if(((escapeKeyPressed && escapePressedCooldown == 0)|| mainThread.buttonAction == "unpauseGame")   
					&& mainThread.gamePaused && mainThread.gameStarted 
					&& mainThread.menuStatus != mainThread.helpMenu
					&& mainThread.menuStatus != mainThread.optionMenu
					&& mainThread.menuStatus != mainThread.highscoreMenu) {
				
				if(!mainThread.AIVictory && ! mainThread.playerVictory) {
					mainThread.gamePaused = false; //if game is paused, unpause the game when esc key is hit
					escapePressedCooldown = 5;
				}
				
			}
			
			//quit the game when the quit button is pressed
			if(!mainThread.gameStarted) {
				if(mainThread.buttonAction == "quitGame")
					System.exit(0);
				
				if(mainThread.buttonAction == "easyGame") {
					mainThread.gameStarted = true;
					mainThread.gameFrame = 0;
					mainThread.ec.difficulty = 0;
				}else if(mainThread.buttonAction == "normalGame") {
					mainThread.gameStarted = true;
					mainThread.gameFrame = 0;
					mainThread.ec.difficulty = 1;
				}else if(mainThread.buttonAction == "hardGame") {
					mainThread.gameStarted = true;
					mainThread.gameFrame = 0;
					mainThread.ec.difficulty = 2;
				}
				
				
				
				
				
				if(mainThread.buttonAction == "enableFogOfWar") {
					mainThread.fogOfWarDisabled = false;
					
				}else if(mainThread.buttonAction == "disableFogOfWar") {
					mainThread.fogOfWarDisabled = true;
					
				}
			}
			
			//abort current game when the abort button is pressed
			if(mainThread.gameStarted && mainThread.buttonAction == "abortGame") {
				mainThread.gameStarted = false;
				mainThread.gameFrame = 0;
				mainThread.gamePaused = false;
				mainThread.AIVictory = false;
				mainThread.playerVictory = false;
				mainThread.afterMatch = false;
				mainThread.theAssetManager.destoryAsset();
				
				camera.MOVE_LEFT = false;
				camera.MOVE_RIGHT = false;
				camera.MOVE_UP = false;
				camera.MOVE_DOWN = false;
				camera.TURN_LEFT = false;
				camera.TURN_RIGHT = false;
			}
			
			if(mainThread.gameStarted && mainThread.buttonAction == "backToMap") {
				mainThread.AIVictory = false;
				mainThread.playerVictory = false;
				mainThread.afterMatch = true;
				mainThread.gamePaused = false;
				
			}
			
			//toggle mouse capture mode
			if(mainThread.buttonAction == "enableMouseCapture") {
				mainThread.capturedMouse = true;
			}else if(mainThread.buttonAction == "disableMouseCapture") {
				mainThread.capturedMouse = false;
			}
			

		}
		

		if(leftMouseButtonReleased)
			mainThread.leftMouseButtonReleased = true;
		
		if(escapeKeyPressed)
			mainThread.escapeKeyPressed = true;
		
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
	}
	
	public static void handleKeyRelease(char c){
		keyReleaseBuffer[keyReleaseCounter] = c;
		keyReleaseCounter++;
	}
	
}
