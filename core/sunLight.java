package core;

//directional light from the sun
public class sunLight {
	
	public static int XZ_angle, YZ_angle;
	
	public static float sinXZ_angle, cosXZ_angle, sinYZ_angle, cosYZ_angle;
	
	public static vector position;
	
	public static int[] shadowBuffer;
	
	public static vector lightDirection;
	
	public static int screen_width = MainThread.screen_width;
	public static int screen_height = MainThread.screen_height;
	public static int shadowmap_width = MainThread.shadowmap_width;
	public static int shadowmap_size = shadowmap_width * shadowmap_width;
	
	
	public static void init(){
		
		shadowBuffer = new int[shadowmap_size];
		
		XZ_angle = 225;
		YZ_angle = 316;
		sinXZ_angle = GameData.sin[XZ_angle];
		cosXZ_angle = GameData.cos[XZ_angle];
		sinYZ_angle = GameData.sin[YZ_angle];
		cosYZ_angle = GameData.cos[YZ_angle];
		
		lightDirection = new vector(0,0,1);
		lightDirection.rotate_YZ(YZ_angle);
		lightDirection.rotate_XZ(XZ_angle);
		lightDirection.y*=-1;
		lightDirection.x*=-1;
	
		
		position = new vector(0,0,0);
	}
	
	public static void update(){
		position.set(MainThread.my2Dto3DFactory.get3DLocation(MainThread.theAssetManager.Terrain.ground[0], screen_width/2, screen_height*13/32));
		position.add(lightDirection, -5);
		
		//reset shadow buffer
		shadowBuffer[0] = Integer.MAX_VALUE;
		for(int i = 1; i < shadowmap_size; i+=i){
			System.arraycopy(shadowBuffer, 0, shadowBuffer, i, shadowmap_size - i >= i ? i : shadowmap_size - i);
			
		}
		
	}
}
