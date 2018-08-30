package core;

//directional light from the sun
public class sunLight {
	
	public static int XZ_angle, YZ_angle;
	
	public static float sinXZ_angle, cosXZ_angle, sinYZ_angle, cosYZ_angle;
	
	public static vector position;
	
	public static int[] shadowBuffer;
	
	public static vector lightDirection;
	
	
	public static void init(){
		
		shadowBuffer = new int[1024*1024];
		
		XZ_angle = 225;
		YZ_angle = 316;
		sinXZ_angle = gameData.sin[XZ_angle];
		cosXZ_angle = gameData.cos[XZ_angle];
		sinYZ_angle = gameData.sin[YZ_angle];
		cosYZ_angle = gameData.cos[YZ_angle];
		
		lightDirection = new vector(0,0,1);
		lightDirection.rotate_YZ(YZ_angle);
		lightDirection.rotate_XZ(XZ_angle);
		lightDirection.y*=-1;
		lightDirection.x*=-1;
	
		
		position = new vector(0,0,0);
	}
	
	public static void update(){
		position.set(mainThread.my2Dto3DFactory.get3DLocation(mainThread.theAssetManager.Terrain.ground[0], 384, 208));
		position.add(lightDirection, -5);
		
		//reset shadow buffer
		shadowBuffer[0] = Integer.MAX_VALUE;
		for(int i = 1; i < 1048576; i+=i){
			System.arraycopy(shadowBuffer, 0, shadowBuffer, i, 1048576 - i >= i ? i : 1048576 - i);
			
		}
		
	}
}
