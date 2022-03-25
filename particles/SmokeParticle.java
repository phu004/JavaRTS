package particles;

import core.mainThread;
import core.postProcessingThread;
import core.vector;

public class smokeParticle {
	//size of the smoke particle 
		public float size;
		
		//which  sprite to use
		public int spriteIndex;
		
		//current frame Index;
		public int frameIndex;
		
		//life time
		public int lifeTime;
		public int animationSpeed;
		
		//centre of the smoke particle 
		public vector centre;
		public vector tempCentre;
		
		public boolean isInAction;
		public float smokeHeight;
		
		public static int[] screen;
		public static int[] zbuffer;
		
		public static int screen_width = mainThread.screen_width;
		public static int screen_height = mainThread.screen_height;
		
		
		public smokeParticle(){
			centre = new vector(0,0,0);
			tempCentre = new vector(0,0,0);
		}
		
		
		public void setActive(float x, float y, float z, float size, int animationSpeed,  int spriteIndex, float smokeHeight){
			isInAction = true;
			
			this.size = size;
			this.animationSpeed = animationSpeed;
			this.spriteIndex = spriteIndex;
			frameIndex = 0;
		
			lifeTime = 80;
			if(size == 1f)
				lifeTime = 64;
			
			this.centre.set(x,y,z);
			
			this.smokeHeight = (smokeHeight - centre.y) *300000;
		}
		
		public void updateAndDraw(){
			if(!isInAction)
				return;
			
			smokeParticle.screen = postProcessingThread.currentScreen;
			smokeParticle.zbuffer = postProcessingThread.currentZbuffer;
			
			if(size ==1.5){
				centre.y += 0.0035f;
			}else if(size == 0.7f){
				centre.y += 0.005f;
			}else if(size == 0.9f){
				centre.y += 0.0025f;
			}else if(size == 0.8f){
				centre.y -= 0.006f;
			}
			
			//update centre in camera coordinate 
			vector cameraPosition = postProcessingThread.cameraPosition;
			float X = 0,Y = 0, Z = 0, 
			camX = cameraPosition.x, camY = cameraPosition.y, camZ = cameraPosition.z,
			sinXZ = postProcessingThread.sinXZ,
			cosXZ = postProcessingThread.cosXZ,
			sinYZ = postProcessingThread.sinYZ, 
			cosYZ = postProcessingThread.cosYZ;
		
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
			
			

			int xPos = (int)tempCentre.screenX;
			int yPos = (int)tempCentre.screenY;
			
			if(xPos  > screen_width+132 || xPos < -132 || yPos > screen_height+132 || yPos < -132){
				frameIndex+=animationSpeed;
				lifeTime-=animationSpeed;
				if(lifeTime <= 0)
					isInAction = false;
				return;
			}
			
			int[] sprite = mainThread.textures[spriteIndex].smoke[frameIndex/2];
			
			
			float ratio = size*2/tempCentre.z;
			
			
			int width = 64;
			int height = 64;
			int depth = (int)(0x1000000/tempCentre.z + smokeHeight);
			
			
			int originalWidth = width;
			
			//find the size ratio between a sprite pixel and screen pixel
			float ratioInverse = 1f/ratio;
			
			width = (int)(ratio*width);
			height = (int)(ratio*height);
			
			//only draw the part of the sprite that is inside the screen
			//define boundary
			int xTop = xPos - width/2;
			int yTop = yPos - height/4*3;
			int xBot = xPos + width/2;
			int yBot = yPos + height/4;
					
			
			
			//draw sprite 
			int screenIndex = 0;
			int SpriteValue = 0;
			int screenValue = 0;
			int r,g,b;
		
			//power plant smoke particle
			if(size == 1.5){
				for(int i = yTop, y = 0; i < yBot; i++, y++){
					if(i < 0 || i >=screen_height)
						continue;
					int ratioInverseY = (int)(ratioInverse*y);
					for(int j = xTop, x = 0;  j < xBot; j++, x++){
						
						SpriteValue = sprite[(int)(ratioInverse*x) + ratioInverseY*originalWidth] & 255;
						
						if(SpriteValue == 0)
							continue;
						
						if(j < 0 || j >= screen_width)
							continue;
						screenIndex = j + i*screen_width;
						if(zbuffer[screenIndex] >= depth)
							continue;
						screenValue = screen[screenIndex];
						
						
						r = ((((screenValue & 0xff0000)>>16)*(256 - SpriteValue))>>8) + SpriteValue;
						g = ((((screenValue &0xff00)>>8)*(256 - SpriteValue))>>8) + SpriteValue;
						b = (((screenValue & 0xff)*(256 - SpriteValue))>>8) + SpriteValue;
						
						
						screen[screenIndex] = (r<<16)|(g<<8)|b;
						
					}
				}
			}else
			
			//fire smoke particle
			if(size == 0.7f){
				for(int i = yTop, y = 0; i < yBot; i++, y++){
					if(i < 0 || i >=screen_height)
						continue;
					int ratioInverseY = (int)(ratioInverse*y);
					for(int j = xTop, x = 0;  j < xBot; j++, x++){
						
						SpriteValue = sprite[(int)(ratioInverse*x) + ratioInverseY*originalWidth] & 255;
						
						if(SpriteValue == 0)
							continue;
						
						if(j < 0 || j >= screen_width)
							continue;
						screenIndex = j + i*screen_width;
						if(zbuffer[screenIndex] >= depth)
							continue;
						screenValue = screen[screenIndex];
						
	
						r = ((((screenValue & 0xff0000)>>16)*(256 - SpriteValue))>>8) ;
						g = ((((screenValue &0xff00)>>8)*(256 - SpriteValue))>>8);
						b = (((screenValue & 0xff)*(256 - SpriteValue))>>8);
						
						
						screen[screenIndex] = (r<<16)|(g<<8)|b;
						
					}
				}
			}else
			
			//rocket tail particle
			if(size == 1f){
				if(yTop >= 0 && yBot < screen_height && xTop >=  0 && xBot < screen_width){
					for(int i = yTop, y = 0; i < yBot; i++, y++){
						
						
						int ratioInverseY = (int)(ratioInverse*y) * originalWidth;
						float ratioInverseX = 0;
						
						screenIndex = xTop + i*screen_width;
						
						for(int j = xTop;  j < xBot; j++, screenIndex++, ratioInverseX+=ratioInverse){
							
							SpriteValue = sprite[(int)(ratioInverseX) + ratioInverseY];
							
							if(SpriteValue == 0)
								continue;
							
							SpriteValue = 256 - ((SpriteValue * lifeTime) >> 6);
							
							
							//if(zbuffer[screenIndex] >= depth)
							//	continue;
							screenValue = screen[screenIndex];
							
							r = ((((screenValue & 0xff0000)>>16)*SpriteValue)>>8) ;
							g = ((((screenValue & 0xff00)>>8)*SpriteValue)>>8);
							b = (((screenValue & 0xff)*SpriteValue)>>8);
							
							screen[screenIndex] = (r<<16)|(g<<8)|b;
							
						}
					}
				}
			}else
				
			//refinery smoke particle	
			if(size == 0.9f){
				for(int i = yTop, y = 0; i < yBot; i++, y++){
					if(i < 0 || i >=screen_height)
						continue;
					int ratioInverseY = (int)(ratioInverse*y);
					for(int j = xTop, x = 0;  j < xBot; j++, x++){
						
						SpriteValue = sprite[(int)(ratioInverse*x) + ratioInverseY*originalWidth] & 255;
						
						if(SpriteValue == 0)
							continue;
						
						if(j < 0 || j >= screen_width)
							continue;
						screenIndex = j + i*screen_width;
						if(zbuffer[screenIndex] >= depth)
							continue;
						screenValue = screen[screenIndex];
						
						
						r = ((((screenValue & 0xff0000)>>16)*(256 - SpriteValue))>>8) + (SpriteValue/4);
						g = ((((screenValue &0xff00)>>8)*(256 - SpriteValue))>>8) + (SpriteValue/6);
						b = (((screenValue & 0xff)*(256 - SpriteValue))>>8) + (SpriteValue/6);
						
						
						screen[screenIndex] = (r<<16)|(g<<8)|b;
						
					}
				}
			} else
			
			if(size == 0.8f){
				for(int i = yTop, y = 0; i < yBot; i++, y++){
					if(i < 0 || i >=screen_height)
						continue;
					int ratioInverseY = (int)(ratioInverse*y);
					for(int j = xTop, x = 0;  j < xBot; j++, x++){
						
						SpriteValue = sprite[(int)(ratioInverse*x) + ratioInverseY*originalWidth] & 255;
						
						if(SpriteValue == 0)
							continue;
						
						if(j < 0 || j >= screen_width)
							continue;
						screenIndex = j + i*screen_width;
						if(zbuffer[screenIndex] >= depth)
							continue;
						screenValue = screen[screenIndex];
						
						
						r = ((((screenValue & 0xff0000)>>16)*(256 - SpriteValue))>>8) + SpriteValue;
						g = ((((screenValue &0xff00)>>8)*(256 - SpriteValue))>>8) + SpriteValue;
						b = (((screenValue & 0xff)*(256 - SpriteValue))>>8) + SpriteValue;
						
						
						screen[screenIndex] = (r<<16)|(g<<8)|b;
						
					}
				}
			}
				
			
			
			
			
			frameIndex+=animationSpeed;
			lifeTime-=animationSpeed;
			if(lifeTime <= 0)
				isInAction = false;
			
		}
		
		
}
