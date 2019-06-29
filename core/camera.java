package core;


import java.awt.*;

public class camera{
	public static vector position;

	public static vector view_Direction;
	
	public static vector left, right, left_, right_;

	public static boolean MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, TURN_LEFT, TURN_RIGHT;
	
	public static int XZ_angle, YZ_angle;

	public static float sinXZ_angle, cosXZ_angle, sinYZ_angle, cosYZ_angle;
	
	public static final vector viewDirection = new vector(0, 0, 1);
	
	//a rectangle that represents the screen area
	public static final Rectangle screen = new Rectangle(0,0,768, 512);
	
	public static vector cameraMovement;
	
	public static int frameIndex;
	
	
	public camera(vector p, int XZ, int YZ){
		view_Direction = new vector(0, 0, 1);
		position = p;
		XZ_angle = XZ;
		YZ_angle = YZ;
		
		left = new vector(0,0,0);
		right = new vector(0,0,0);
		left_ = new vector(0, -1, 0);
		right_ = new vector(0, 1, 0);
	}

	public void update(){
		frameIndex++;
		
		if(!mainThread.gameStarted) {
			
			//when game has not started, use a "fly through" as the background for the menu
			if(frameIndex == 1) {
				position.z = 2.5f;
				position.x = 9;
				cameraMovement = new vector(-0.01f,0,0);
			}
			
			
			if(frameIndex > 90 && frameIndex%400 >= 0 && frameIndex%400 < 90) {
				XZ_angle+=1;
				cameraMovement.rotate_XZ(359);
			}
			
			position.add(cameraMovement);
		} 
		
		
		position.add(view_Direction.x*3, 0 , view_Direction.z*3);
		
		
		if(TURN_RIGHT){
			XZ_angle+=1;
		
			
		}

		if(TURN_LEFT){
			XZ_angle-=1;
		}
		
		float x = position.x;
		float z = position.z;
		
		if(MOVE_LEFT){
			left.cross(view_Direction, left_); 
			left.unit();
			position.add(left, -0.1f);
			
			
		}

		if(MOVE_RIGHT){
			right.cross(view_Direction, right_);    
			right.unit();
			position.add(right, -0.1f);
		
		}
			
		if(MOVE_UP){
			vector up = new vector(view_Direction.x, 0, view_Direction.z);
			up.unit();
			position.add(up, 0.1f); 
		}
		
		if(MOVE_DOWN){
			vector down = new vector(view_Direction.x, 0, view_Direction.z);
			down.unit();
			position.add(down, -0.1f);
		}
		
		//make sure the camera never leaves the map
		if(position.x < 0.5){
			position.x = 0.5f;
			
		}
		
		if(position.z < 0.5){
			position.z = 0.5f;
		
		}
		
		if(position.x > 31.5){
			position.x = 31.5f;
		
		}
		if(position.z > 31.5){
			position.z = 31.5f;
			
		}

	
		
		XZ_angle = (XZ_angle + 360) % 360;
		YZ_angle = (YZ_angle + 360) % 360;
		sinXZ_angle = gameData.sin[XZ_angle];
		cosXZ_angle = gameData.cos[XZ_angle];
		sinYZ_angle = gameData.sin[YZ_angle];
		cosYZ_angle = gameData.cos[YZ_angle];
		
		
		
		view_Direction.set(viewDirection);
		view_Direction.rotate_YZ(YZ_angle);
		view_Direction.rotate_XZ(XZ_angle);
		view_Direction.y*=-1;
		view_Direction.x*=-1;
		view_Direction.unit();
		
		position.add(-view_Direction.x*3, 0 , -view_Direction.z*3);
		
		
		
		
	}

}