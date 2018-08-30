package gui;

//handles all the logic for user input

import java.awt.*;

import core.camera;
import core.geometry;
import core.mainThread;

public class inputHandler {

	public static int mouse_x, mouse_y,mouse_x0, mouse_x1, mouse_y0, mouse_y1, cameraMovementAngle;
	
	public static boolean mouseIsInsideScreen, leftKeyPressed, rightKeyPressed, controlKeyPressed, leftMouseButtonPressed, rightMouseButtonPressed, leftMouseButtonReleased, rightMouseButtonReleased, H_pressed,A_pressed, userIsHoldingA; 
	
	public static int numberTyped;
	
	public static final Rectangle mouseMovementArea = new Rectangle(30,20, 708, 472);
	
	public static char[] inputBuffer = new char[1024];
	public static char[] keyReleaseBuffer = new char[1024];
	
	public static int inputCounter, inputBufferIndex, keyReleaseCounter, keyReleaseBufferIndex;
	
	public static void processInput(){
		//read input char
		int theCounter = inputCounter;  
		//handle over flow
		if(inputBufferIndex > theCounter){
			while(inputBufferIndex < 1024){
				char c = inputBuffer[inputBufferIndex];
				
				
				if(c == 'h' || c == 'H'){
					H_pressed = true;
				}
				
				if(c == 'a' || c == 'A'){
					A_pressed = true;
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
			
			
			if(c == 'h' || c == 'H'){
				H_pressed = true;
			}
			
			if(c == 'a' || c == 'A'){
				A_pressed = true;
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
				if(c == 'h' || c == 'H'){
					H_pressed = false;
				}
				
				if(c == 'a' || c == 'A'){
					A_pressed = false;
					userIsHoldingA = false;
					
				}
				keyReleaseBufferIndex++;
			}
			keyReleaseBufferIndex = 0;
		}
		while(keyReleaseBufferIndex < theCounter){
			char c = keyReleaseBuffer[keyReleaseBufferIndex];
			if(c == 'h' || c == 'H'){
				H_pressed = false;
			}
			
			if(c == 'a' || c == 'A'){
				A_pressed = false;
				userIsHoldingA = false;
				
			}
			keyReleaseBufferIndex++;
		}
		
		
		//handle input when game is running
		if(mainThread.inGame){
			if(!mainThread.pc.isSelectingUnit){
				mouse_x0 = mouse_x;
				mouse_y0 = mouse_y;
				if(!mouseIsInsideScreen || !mouseMovementArea.contains(mouse_x0, mouse_y0)){
					
					if(mainThread.pc.cursorIsInMiniMap() || mainThread.pc.cursorIsInSideBar()){
						if(mouse_x0 < 10)
							camera.MOVE_LEFT = true;
						if(mouse_x0 > 758)
							camera.MOVE_RIGHT = true;
						if(mouse_y0 < 20)
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
			if(H_pressed){
				
				mainThread.pc.holdKeyPressed = true;
			}
			
			
			//handle hotheys
			if(A_pressed){
				
				if(!userIsHoldingA){
					mainThread.pc.attackKeyPressed = true;
					userIsHoldingA = true;
				}
				
			}
		}
		
		
		mouseIsInsideScreen = false;
		leftMouseButtonPressed = false;
		rightMouseButtonPressed = false;
		leftMouseButtonReleased = false;
		rightMouseButtonReleased = false;
		
		A_pressed = false;
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
