package particles;

import core.*;
import core.Camera;

//a partical system that resemble a section  a railgun trail
public class Helix {
	//particles
	public vector[] particles;
	
	//direction of particles
	public vector[] directions;
	
	//color of particles
	public int[] colors;
	
	public static int ALPHA=0xFF000000; 
	
	public static vector temp1, temp2, iDirection, jDirection, kDirection;
	
	public vector centre;

	public boolean isInAction;
	
	public int lifeSpan;
	
	public static int screen_width = MainThread.screen_width;
	public static int screen_height = MainThread.screen_height;
	
	public Helix(){
		if(temp1 == null){
			temp1 = new vector(0,0,0);
			temp2 = new vector(0,0,0);
			
			iDirection = new vector(1,0,0);
			iDirection = new vector(0,1,0);
			kDirection = new vector(0,0,1);
		}
		
		centre = new vector(0,0,0);
		particles = new vector[20];
		directions = new vector[20];
		
		for(int i = 0; i < 20; i ++){
			particles[i] = new vector(0,0,0);
			directions[i] = new vector(0,0,0);
		}
		
		colors = new int[20];
	}
	
	public void setActive(float x, float y, float z, int angle){
		
		centre.set(x,y,z);
		
		angle+=360;
		angle%=360;
		angle=360 - angle; 
		
		isInAction = true;
		 
		lifeSpan = 40;
		
		int zAxisRotation = 0;
		
		temp1.set(centre);
		temp2.set(kDirection);
		temp2.rotate_XZ(angle);
		temp2.scale(0.05f);
		temp1.subtract(temp2);
		temp2.scale(0.1f);
		for(int i = 0; i < particles.length; i++){
			directions[i].set(iDirection);
			directions[i].rotate_XY(zAxisRotation);
			directions[i].rotate_XZ(angle);
			directions[i].scale(0.01f);
			particles[i].set(temp1);
			particles[i].add(directions[i]);
			directions[i].scale(0.06f);
			colors[i] = ((int)(58 - 20* GameData.sin[zAxisRotation]*0.9) << 16)| ((int)(130 - 40* GameData.sin[zAxisRotation]*0.9) << 8)| (int)(185 - 40* GameData.sin[zAxisRotation]*0.9);
		
			zAxisRotation+=18;
			temp1.add(temp2);
			
		
		}
		
		
	}
	
	public void updateAndDraw(){
		if(!isInAction)
			return;
		
		
		int[] screen = postProcessingThread.currentScreen;
		
		//animate particles
		for(int i = 0; i < particles.length; i++)
			particles[i].add(directions[i]);
				
	
		//find centre in Camera coordinate
		temp1.set(centre);
		temp1.subtract(Camera.position);
		temp1.rotate_XZ(Camera.XZ_angle);
		temp1.rotate_YZ(Camera.YZ_angle);
		temp1.updateLocation();
		
		boolean outsideScreen = temp1.screenX < -10 || temp1.screenX > screen_width + 10 || temp1.screenY < -10 || temp1.screenY > screen_height + 10;
		
		if(!outsideScreen){
			
			
			int position = 0;
			int color = 0;
			int r = 0; int b = 0; int g = 0;
			int alpha = 0;
			
			//find the size of the particle
			double size = 1/temp1.z;
		
			int spriteIndex = 0;
			if(size < 0.3){
				spriteIndex = 1;
			}else if(size < 0.35 ){
				spriteIndex = 1;
			}else if(size < 0.4){
				spriteIndex = 2;
			}else if(size < 0.45){
				spriteIndex = 3;
			}else if(size < 0.5){
				spriteIndex = 4;
			}else if(size < 0.55){
				spriteIndex = 4;
			}else if(size <= 0.6){
				spriteIndex = 4;
			}
			
			
			for(int i = 19; i >=0; i--){
				temp1.set(particles[i]);
				temp1.subtract(Camera.position);
				temp1.rotate_XZ(Camera.XZ_angle);
				temp1.rotate_YZ(Camera.YZ_angle);
				temp1.updateLocation();
				
				
				if(temp1.screenX >= 2 && temp1.screenX < screen_width -2 && temp1.screenY >=2 && temp1.screenY < screen_height -2){
					int centre = (int)temp1.screenX + ((int)temp1.screenY)*screen_width;
					
					alpha = 100;
					alpha = alpha - alpha*lifeSpan/40+ 155;
				
					for(int j = 0; j < GameData.size[spriteIndex].length; j++){
						position = centre + GameData.size[spriteIndex][j];
					
							int bkgrd =screen[position];
							
							color = colors[i];
							
							
							r=(alpha*(((bkgrd>>16)&255)-((color>>16)&255))>>8)+((color>>16)&255);
							g=(alpha*(((bkgrd>>8)&255)-((color>>8)&255))>>8)+((color>>8)&255);
							b=(alpha*((bkgrd&255)-(color&255))>>8)+(color&255);
							
							screen[position]=  ALPHA|(r<<16)|(g<<8)|b;
							
					
					}
				}
			}
		}
		
		lifeSpan--;
		if(lifeSpan <= 0)
			isInAction = false;
		
	}
	
}
