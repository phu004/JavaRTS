package core;

public final class vector{
	//x, y, z component of the vector
	public float x, y, z;

	//2d position on screen (from camera point of view)
	public float screenX, screenY;
	
	//2d position on screen (from light point of view)
	public float screenX_lightspace, screenY_lightspace;
	public vector
			tempVector1 = new vector(0,0,0),
			tempVector2 = new vector(0,0,0),
			tempVector3 = new vector(0,0,0),
			tempVector4 = new vector(0,0,0),
			tempVector5 = new vector(0,0,0),
			tempVector6 = new vector(0,0,0);
	public static vector[] vertex2D;
	public static final int Z_length = 650;
	
	public static final int orthogonalScale = 330;

	public static float old_X, old_Y, old_Z, zInverse, lengthInverse;
	
	public static int half_width = mainThread.screen_width/2;
	public static int half_height = mainThread.screen_height/2;
	public static int half_width_shadowmap = mainThread.shadowmap_width/2;
	
	//z component of the vector from light space
	public float z_lightspace;
	
	public vector(float x, float y, float z){ 
		this.x = x;
		this.y = y;
		this.z = z;

		//calculate its 2D location on the screen
		updateLocation();
	}

	public void add(vector v){
		x+=v.x;
		y+=v.y;
		z+=v.z;
	}

	public void add(float a, float b, float c){
		x+=a;
		y+=b;
		z+=c;
	}

	public void add(vector v, float scaler){
		x+=v.x*scaler;
		y+=v.y*scaler;
		z+=v.z*scaler;
	}


	public void subtract(vector v){
		x-=v.x;
		y-=v.y;
		z-=v.z;
	}
	

	//amplify each component of the vector by a number
	public void scale(float d){
		x*=d;
		y*=d;
		z*=d;
	}

	//normalize the vector
	public void unit(){
		lengthInverse = 1/getLength();
		x = x*lengthInverse;
		y = y*lengthInverse;
		z = z*lengthInverse;
	}
	

	//find the magnitude of the vector
	public float getLength(){
		return (float)Math.sqrt(x*x + y*y + z*z);
	}

	//retrun the dot product of this vector with another vector
	public float dot(vector v){
		return x*v.x + y*v.y + z*v.z;
	}
	

	public void cross(vector v1, vector v2){
		x = v1.y*v2.z - v1.z*v2.y;
		y = v1.z*v2.x - v1.x*v2.z;
		z = v1.x*v2.y - v1.y*v2.x;
	}

	//rotate the vector along Y axis
	public void  rotate_XZ(int angle){
		float sin = gameData.sin[angle];
		float cos = gameData.cos[angle];
		old_X = x;
		old_Z = z;
		x = cos*old_X - sin*old_Z;
		z = sin*old_X + cos*old_Z;
	}

	//rotate the vector along X axis
	public void rotate_YZ(int angle){
		float sin = gameData.sin[angle];
		float cos = gameData.cos[angle];
		old_Y = y;
		old_Z = z;
		y = cos*old_Y - sin*old_Z;
		z = sin*old_Y + cos*old_Z;
	}
	
	//rotate the vector along Z axis
	public void rotate_XY(int angle){
		float sin = gameData.sin[angle];
		float cos = gameData.cos[angle];
		old_X = x;
		old_Y = y;
		x = cos*old_X - sin*old_Y;
		y = sin*old_X + cos*old_Y;
	}
	

	//set all the component equal to the corresponding component of a given vector
	public void set(vector v){
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	//set all the component to 0
	public void reset(){
		x = 0;
		y = 0;
		z = 0;	
	}

	public void updateLocation(){
		//find the 2D screen location of this vector
		zInverse = Z_length/z;
		screenX = x*zInverse +  half_width; 
		screenY = -y*zInverse + half_height;
		
	}
	
	public void updateLocationOrthognal(){
		//find the 2D screen location of this vector in Orthographic projection 
		screenX_lightspace = x*orthogonalScale + half_width_shadowmap; 
		screenY_lightspace = -y*orthogonalScale + half_width_shadowmap;
	}
	
	public vector myClone(){
		return new vector(x,y,z);
	}

	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}



/**
	This method is moved from polygon3D class to vector class
	This method is more interested vector class than polygon3D class
*/


	//find the approximate projection point on the clipping plane
	public  void approximatePoint(int index, vector frontPoint, polygon3D polygon3D){
		tempVector1.set(frontPoint.x - x, frontPoint.y - y, frontPoint.z - z);
		tempVector1.scale((frontPoint.z- 0.1f)/ tempVector1.z);
		vertex2D[index].set(frontPoint.x, frontPoint.y, frontPoint.z);
		vertex2D[index].subtract(tempVector1);
		vertex2D[index].updateLocation();
	}
}