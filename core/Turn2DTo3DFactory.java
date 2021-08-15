package core;

//takes a pixel position from a rasterized polygon and  find its 3D location in world space

public class Turn2DTo3DFactory {
	
	public  vector O, U, V, W, A, B, C;
	
	public  vector location3D;
	
	public  float  X, Y;
	
	public  void init(){
		O = new vector(0,0,0);
		U = new vector(0,0,0);
		V = new vector(0,0,0);
		W = new vector(0,0,0);
		A = new vector(0,0,0);
		B = new vector(0,0,0);
		C = new vector(0,0,0);
		location3D = new vector(0,0,0);
	}

	public  vector get3DLocation(polygon3D poly, int x, int y){
		O.set(poly.origin);
		O.subtract(camera.position);
		O.rotate_XZ(camera.XZ_angle);
		O.rotate_YZ(camera.YZ_angle);

		U.set(poly.rightEnd);
		U.subtract(camera.position);
		U.rotate_XZ(camera.XZ_angle);
		U.rotate_YZ(camera.YZ_angle);
		
		

		V.set(poly.bottomEnd);
		V.subtract(camera.position);
		V.rotate_XZ(camera.XZ_angle);
		V.rotate_YZ(camera.YZ_angle);
		
		U.subtract(O);
		U.unit();
		
		V.subtract(O);
		V.unit();
		
		A.cross(V,O);
		B.cross(O,U);
		C.cross(U,V);
		
		W.set(x-mainThread.screen_width/2, -y + mainThread.screen_height/2, vector.Z_length);
		
		X = A.dot(W)/C.dot(W);
		Y = B.dot(W)/C.dot(W);
		
		O.set(poly.origin);
		U.set(poly.rightEnd);
		V.set(poly.bottomEnd);
		
		U.subtract(O);
		V.subtract(O);
		
		
		X/=(U.getLength());
		Y/=(V.getLength());
		
		location3D.set(O);
		location3D.add(U, X);
		location3D.add(V, Y);
		
		return location3D;
	}
	
}
