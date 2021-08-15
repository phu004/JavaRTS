package gui;

import core.mainThread;
import core.postProcessingThread;
import core.vector;

public class confirmationIcon {

	public int color;
	
	public static float[] sin;  
	public static float[] cos;
	
	//centre of the icon 
	public vector centre;
	public vector tempCentre;
	
	public int frameIndex;
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	
	
	public confirmationIcon(){
		//Make sin and cos look up tables
		sin = new float[361];
		cos = new float[361];
		for(int i = 0; i < 361; i ++){
			sin[i] = (float)Math.sin(Math.PI*i/180);
			cos[i] = (float)Math.cos(Math.PI*i/180);
		}
		
		centre = new vector(0,0,0);
		tempCentre = new vector(0,0,0);
		frameIndex = -1;
	}
	
	public void setActive(float xPos, float zPos, int color){
		centre.set(xPos, -0.5001f, zPos);
		this.color = color;
		frameIndex = 20;
	}
	
	public void updateAndDraw(){
		if(frameIndex <0)
			return;
		
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
		
		int[] screen = postProcessingThread.currentScreen;
		int x, y;
		
		float a = 13f;
		float b = 11.5f;
		
		int lastIndex = -1;
		int currentIndex;
		int t = frameIndex - 10;
		
		float transparency = 0.7f;
		
		if(t < 0){
			t = 0;
			transparency = (float)frameIndex * transparency/10f;
		}
		
		
		int r1,b1,g1, r2,b2,g2, screenColor;
		
		
		//draw transparent ring
		for(int i = 0; i < 360; i+=1){
			x = (int)(tempCentre.screenX + (a * (float)(12-t)/10f +0.7f) * cos[i]);
			y = (int)(tempCentre.screenY + (b * (float)(12-t)/10f +0.7f)* sin[i]);
			
			if(x < 0 || x >= screen_width || y < 0 || y >= screen_height)
				continue;
			
			currentIndex = x + y * screen_width;
			
			if(currentIndex == lastIndex)
				continue;
			
			screenColor = screen[currentIndex];
			r1 = (int)(((screenColor & 0xff0000) >> 16) * (1f - transparency*0.5f));
			g1 = (int)(((screenColor & 0xff00) >> 8) * (1f - transparency * 0.5f));
			b1 = (int)((screenColor & 0xff) * (1f - transparency * 0.5f));
			
			r2 = (int)(((color & 0xff0000) >> 16) *  transparency * 0.5f) + r1;
			g2 = (int)(((color & 0xff00) >> 8) *  transparency * 0.5f) + g1;
			b2 = (int)((color & 0xff) *  transparency * 0.5f) + b1;
			
			screen[x + y * screen_width] = r2 << 16 | g2 << 8 | b2;
			
			lastIndex = currentIndex;
		}
		
		for(int i = 0; i < 360; i+=1){
			x = (int)(tempCentre.screenX + (a * (float)(12-t)/10f -1.7f) * cos[i]);
			y = (int)(tempCentre.screenY + (b * (float)(12-t)/10f -1.7f)* sin[i]);
			
			if(x < 0 || x >= screen_width || y < 0 || y >= screen_height)
				continue;
			
			currentIndex = x + y * screen_width;
			
			if(currentIndex == lastIndex)
				continue;
			
		
			
			screenColor = screen[currentIndex];
			r1 = (int)(((screenColor & 0xff0000) >> 16) * (1f - transparency*0.5f));
			g1 = (int)(((screenColor & 0xff00) >> 8) * (1f - transparency * 0.5f));
			b1 = (int)((screenColor & 0xff) * (1f - transparency * 0.5f));
			
			r2 = (int)(((color & 0xff0000) >> 16) *  transparency * 0.5f) + r1;
			g2 = (int)(((color & 0xff00) >> 8) *  transparency * 0.5f) + g1;
			b2 = (int)((color & 0xff) *  transparency * 0.5f) + b1;
			
			screen[x + y * screen_width] = r2 << 16 | g2 << 8 | b2;
			
			lastIndex = currentIndex;
		}
		
		
		//draw solid ring
		for(int i = 0; i < 360; i+=1){
			x = (int)(tempCentre.screenX + (a * (float)(12-t)/10f) * cos[i]);
			y = (int)(tempCentre.screenY + (b * (float)(12-t)/10f)* sin[i]);
			
			if(x < 0 || x >= screen_width || y < 0 || y >= screen_height)
				continue;
			
			screenColor = screen[x + y * screen_width];
			r1 = (int)(((screenColor & 0xff0000) >> 16) * (1f - transparency));
			g1 = (int)(((screenColor & 0xff00) >> 8) * (1f - transparency));
			b1 = (int)((screenColor & 0xff) * (1f - transparency));
			
			r2 = (int)(((color & 0xff0000) >> 16) *  transparency) + r1;
			g2 = (int)(((color & 0xff00) >> 8) *  transparency) + g1;
			b2 = (int)((color & 0xff) *  transparency) + b1;
			
			screen[x + y * screen_width] = r2 << 16 | g2 << 8 | b2;
		}
		
		for(int i = 0; i < 360; i+=1){
			x = (int)(tempCentre.screenX + (a * (float)(12-t)/10f -1) * cos[i]);
			y = (int)(tempCentre.screenY + (b * (float)(12-t)/10f- 1)* sin[i]);
			
			if(x < 0 || x >= screen_width || y < 0 || y >= screen_height)
				continue;
			screenColor = screen[x + y * screen_width];
			r1 = (int)(((screenColor & 0xff0000) >> 16) * (1f - transparency));
			g1 = (int)(((screenColor & 0xff00) >> 8) * (1f - transparency));
			b1 = (int)((screenColor & 0xff) * (1f - transparency));
			
			r2 = (int)(((color & 0xff0000) >> 16) *  transparency) + r1;
			g2 = (int)(((color & 0xff00) >> 8) *  transparency) + g1;
			b2 = (int)((color & 0xff) *  transparency) + b1;
			
			screen[x + y * screen_width] = r2 << 16 | g2 << 8 | b2;
		}
		
		frameIndex--;
		
	}
	
	
}
