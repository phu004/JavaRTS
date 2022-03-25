package particles;

import core.mainThread;
import core.postProcessingThread;
import core.vector;

public class explosion {
	//size of the explosion 
	public float size;
	
	//which explosion sprite to use
	public int spriteIndex;
	
	//current frame Index;
	public int frameIndex;
	
	//type of explosion 
	public int type;
	
	//life time
	public int lifeTime;
	public int animationSpeed;
	
	
	//centre of explosion 
	public vector centre;
	public vector tempCentre;
	
	public boolean isInAction;
	
	public float explosionHeight;
	
	public int auraIndex;
	
	public static int zTop, zBot, zDelta;
	
	public int xStart, yStart;
	
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	
	
	public explosion(){
		centre = new vector(0,0,0);
		tempCentre = new vector(0,0,0);
	}
	
	public void setActive(float x, float y, float z, float size, int animationSpeed, int type, int spriteIndex, float explosionHeight){
		isInAction = true;
		
		this.size = size;
		this.animationSpeed = animationSpeed;
		this.spriteIndex = spriteIndex;
		frameIndex = 0;
		auraIndex = 0;
		if(size >= 2){
			lifeTime = 16 + 3;  //cause a little bit delay
			this.centre.set(x,y,z);
		}else{
			lifeTime = 16;
			if(size < 1)
				this.centre.set(x,y,z);
			else
				this.centre.set(x+(float)Math.random()*0.1f -0.05f,y,z+(float)Math.random()*0.1f - 0.05f);
		}
		this.explosionHeight = (explosionHeight - centre.y) *300000;
		xStart = 0;
		yStart = 0;
		
	}
	
	public void updateAndDrawExplosionAura(){
		if(!isInAction || lifeTime > 16)
			return;
		
		//update centre in camera coordinate 
		vector cameraPosition = postProcessingThread.cameraPosition;
		float X = 0,Y = 0, Z = 0, 
		camX = cameraPosition.x, camY = cameraPosition.y, camZ = cameraPosition.z,
		sinXZ = postProcessingThread.sinXZ,
		cosXZ = postProcessingThread.cosXZ,
		sinYZ = postProcessingThread.sinYZ, 
		cosYZ = postProcessingThread.cosYZ;
	
		
		
		
		//draw explosion aura sprite if the explosion is big enough
		if(size >= 1){
			X = centre.x - camX;
			Y = -0.5f - camY;
			Z = centre.z - camZ;
				
			//rotating
			tempCentre.x = cosXZ*X - sinXZ*Z;
			tempCentre.z = sinXZ*X + cosXZ*Z;
				
			Z = tempCentre.z;
				
			tempCentre.y = cosYZ*Y - sinYZ*Z;
			tempCentre.z = sinYZ*Y + cosYZ*Z;
			tempCentre.updateLocation();
			
			
			short[] sprite = mainThread.textures[1].explosionAura[frameIndex];
			float ratioX = size*4f/tempCentre.z;
			float ratioY = size*3.6f/tempCentre.z;
			int xPos = (int)tempCentre.screenX;
			int yPos = (int)tempCentre.screenY;
			int originalWidth = 128;
			int width = 128 - frameIndex*10;
			int height = 128 - frameIndex*10;
			xStart +=5;
			yStart +=5;
			
			int depth; 
			
			int[] zbuffer = postProcessingThread.currentZbuffer;
			byte[] smoothedShadowBitmap = postProcessingThread.smoothedShadowBitmap;
			
			//find the size ratio between a sprite pixel and screen pixel
			float ratioInverseX = 1f/ratioX;
			float ratioInverseY = 1f/ratioY;
			
			width = (int)(ratioX*width);
			height = (int)(ratioY*height);
			
			
			//only draw the part of the sprite that is inside the screen
			//define boundary
			int xTop = xPos - width/2;
			int yTop = yPos - height/2;
			int xBot = xPos + width/2;
			int yBot = yPos + height/2;
			
			//draw sprite 
			int screenIndex = 0;
			int SpriteValue = 0;
			
			for(int i = yTop, y = yStart; i < yBot; i++, y++){
				if(i < 0 || i >=screen_height)
					continue;
				
				depth = zTop + i*zDelta;
				int ratioInverseY_Times_Y_Times_originalWidth = (int)(ratioInverseY*y)*originalWidth;
				
				for(int j = xTop, x = xStart;  j < xBot; j++, x++){
					
					
					if(j < 0 || j >= screen_width)
						continue;
					screenIndex = j + i*screen_width;
					
					
					if(zbuffer[screenIndex] - depth > 30000)
						continue;
					
					SpriteValue = sprite[((int)(ratioInverseX*x) + ratioInverseY_Times_Y_Times_originalWidth)& 0x3fff] ;
				
					if(SpriteValue > smoothedShadowBitmap[screenIndex])
						smoothedShadowBitmap[screenIndex] = (byte)SpriteValue;
					
				}
			}
		}
		
		X = centre.x - camX;
		Y = centre.y - camY;
		Z = centre.z - camZ;
			
		//rotating
		tempCentre.x = cosXZ*X - sinXZ*Z;
		tempCentre.z = sinXZ*X + cosXZ*Z;
			
		Z = tempCentre.z;
			
		tempCentre.y = cosYZ*Y - sinYZ*Z;
		tempCentre.z = sinYZ*Y + cosYZ*Z;
		tempCentre.updateLocation();
		
	}
	

	
	//draw exlopsion sprite
	public void drawExplosionSprite(){
		if(!isInAction)
			return;
		
		
		if(lifeTime <=16){
			int[] sprite = mainThread.textures[spriteIndex].explosions[frameIndex];
			float ratio = size*2/tempCentre.z;
			int xPos = (int)tempCentre.screenX;
			int yPos = (int)tempCentre.screenY;
			int width = 64;
			int height = 64;
			int depth = (int)(0x1000000/tempCentre.z + explosionHeight);
			
			int[] screen = postProcessingThread.currentScreen;
			int[] zbuffer = postProcessingThread.currentZbuffer;
			int originalWidth = width;
			
			//find the size ratio between a sprite pixel and screen pixel
			float ratioInverse = 1f/ratio;
			
			width = (int)(ratio*width);
			height = (int)(ratio*height);
			
			//only draw the part of the sprite that is inside the screen
			//define boundary
			int xTop = xPos - width/2;
			int yTop = yPos - height/2;
			int xBot = xPos + width/2;
			int yBot = yPos + height/2;
					
			//draw sprite 
			int screenIndex = 0;
			int SpriteValue = 0;
			int screenValue = 0;
			int MASK7Bit = 0xFEFEFF;
			int overflow = 0;
			int pixel = 0;
			
			
			for(int i = yTop, y = 0; i < yBot; i++, y++){
				if(i < 0 || i >=screen_height)
					continue;
				int ratioInverseY = (int)(ratioInverse*y);
				for(int j = xTop, x = 0;  j < xBot; j++, x++){
					if(j < 0 || j >= screen_width)
						continue;
					screenIndex = j + i*screen_width;
					if(zbuffer[screenIndex] >= depth)
						continue;
					
					SpriteValue = sprite[(int)(ratioInverse*x) + ratioInverseY*originalWidth];
					if(SpriteValue != 0)
						screenValue = (screen[screenIndex]&0xFEFEFE)>>1;
					else
						continue;
					
					pixel=(SpriteValue&MASK7Bit)+(screenValue&MASK7Bit);
					overflow=pixel&0x1010100;
					overflow=overflow-(overflow>>8);
					screen[screenIndex] = overflow|pixel;
					
				}
			}
			frameIndex+=animationSpeed;
		}
		
		
		lifeTime-=animationSpeed;
		if(lifeTime <= 0)
			isInAction = false;
	}
}
