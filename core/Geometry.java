package core;

//determine the drawing orders for polygons and models
public class Geometry {
	
	public static vector temp = new vector(0,0,0);
	public static vector temp1 = new vector(0,0,0);
	public static vector temp2 = new vector(0,0,0);
	
	//solutions for liner equation
	// a1 * X  + b1 * Y = c1
	// a2 * X +  b2 * Y = c2
	public static float X, Y;
	
	
	//solve liner equation with 2 variables
	public static void solveLinerEquation2D(float a1, float a2, float b1, float b2, float c1, float c2){
		if(a1 != 0){
			Y = (c2 - a2*c1/a1)/(b2 - a2*b1/a1);
			X = (c1 - b1*Y)/a1;
		}else{
			float a, b, c;
			a = a1;
			a1 = a2;
			a2 = a;
			
			b = b1;
			b1 = b2;
			b2 = b;
			
			c = c1;
			c1 = c2;
			c2 = c;
			
			Y = (c2 - a2*c1/a1)/(b2 - a2*b1/a1);
			X = (c1 - b1*Y)/a1;
		}
	}
	
	
	//find angle between 2 point with respect to  the positive y axis
	public static int findAngle(float x0, float y0, float x1, float y1){
		
		return (int)(Math.atan2(-(x1 - x0), -(y1 - y0))/Math.PI * 180) + 180;
	}
	
	//find the direction of rotation between 2 angles
	public static int findAngleDelta(int start, int finish,int maxTurnRate){
		int difference = finish - start;
		
		
		
		if(difference < 0){
			if(difference < -180){
				if(difference < -360 + maxTurnRate)
					return 360 + difference;
				else
					return maxTurnRate;
			}else{
				if(difference > -maxTurnRate)
					return difference;
				else 
					return -maxTurnRate;
			}
			
		}else{
			if(difference > 180){
				if(difference > 360 - maxTurnRate)
					return difference - 360;
				else
					return -maxTurnRate;
			}else{
				if(difference < maxTurnRate)
					return difference;
				else
					return maxTurnRate;
			}
			
		}
	}
	
	//draw dot line
	public static void drawLine(vector startPoint, vector endPoint, int color, byte shadowBit){
		
		int w = MainThread.screen_width;
		int h = MainThread.screen_height;
		int size = MainThread.screen_size;
		
		int[] screen = MainThread.screen;
		
		temp1.set(startPoint);
		temp1.y = -0.5f;
		temp1.x -= 0.07f;
		temp1.subtract(Camera.position);
		temp1.rotate_XZ(Camera.XZ_angle);
		temp1.rotate_YZ(Camera.YZ_angle);
	
		temp2.set(endPoint);
		temp2.y = -0.5f;
		temp2.subtract(Camera.position);
		temp2.rotate_XZ(Camera.XZ_angle);
		temp2.rotate_YZ(Camera.YZ_angle);
		
		if(temp1.z < 1f && temp2.z < 1f)
			return;
		
		
		if(temp1.z < 1f){
			float f = (temp2.z - 1)/(temp2.z - temp1.z);
			temp.set(temp1);
			temp.subtract(temp2);
			temp.scale(f);
			temp1.set(temp2);
			temp1.add(temp);
			
		}
		temp1.updateLocation();
		
		
		
		if(temp2.z < 1f){
			float f = (temp1.z - 1)/(temp1.z - temp2.z);
			temp.set(temp2);
			temp.subtract(temp1);
			temp.scale(f);
			temp2.set(temp1);
			temp2.add(temp);
		}
		temp2.updateLocation();
		
		int xPos1 = (int)temp1.screenX;
		int yPos1 = (int)temp1.screenY;
		
		
		int xPos2 = (int)temp2.screenX;
		int yPos2 = (int)temp2.screenY;
		
		int start = 0;
		int  x, y;
		int xDirection, yDirection;
		
		if(xPos1 < xPos2)
			xDirection = 1;
		else
			xDirection = -1;
		
		if(yPos1 < yPos2)
			yDirection = 1;
		else
			yDirection = -1;
		
		if(Math.abs(xPos2 - xPos1) > Math.abs(yPos2 - yPos1)){
			float slope = (float)(yPos2 - yPos1)/(xPos2 - xPos1);
			for(int i = 0; i <= Math.abs(xPos2 - xPos1); i ++){
				x = xPos1 + i*xDirection;
				y = (int)(yPos1 + slope*i*xDirection);
				
				if(x <0 || x > (w-1) || y < 0 || y > (h-1))
					continue;
				
				
				screen[start + x + y*w] = color;
				MainThread.shadowBitmap[start + x + y*w] = shadowBit;
			}
			
		}else{
			float slope = (float)(xPos2 - xPos1)/(yPos2 - yPos1);
			for(int i = 0; i <= Math.abs(yPos2 - yPos1); i ++){
				y = yPos1 + i*yDirection;
				x = (int)(xPos1 + slope*i*yDirection);
				
				if(x <0 || x > (w-1) || y < 0 || y > (h-1))
					continue;
				
				screen[start + x + y*w] = color;
				MainThread.shadowBitmap[start + x + y*w] = shadowBit;
				
				
			}
		}
		
		
		
	
		int index; 	
		for(int j = 0; j < 5; j++){
			for(int k = 0; k < 5; k++){
				int xPos = xPos2 - 2 + k;
				int yPos = yPos2 - 2 + j;
				if(xPos < 0 || xPos > (w-1) || yPos < 0 || yPos > (h-1))
					continue;
				
				index = xPos + yPos*w;
				
				if(index >= 0 && index < size){
					screen[index] = color;
					MainThread.shadowBitmap[index] = shadowBit;
				}
				
			}
		}
		
		
		

		
	}
	
}
