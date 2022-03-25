package core;

import entity.solidObject;

public class polygon3D {
	//The vertex of the polygon with the respect of the world/camera coordinate
	public vector[] vertex3D, tempVertex;
	
	//The vertex of the polygon after clipping
	public static vector[] vertex2D;
		
	//the normal of the polygon with the respect of the world/camera coordinate
	public vector normal;
	
	//the centre of the polygon with the respect of the world/camera coordinate
	public vector centre;
	
	//The number of vertex
	public byte L;
	
	//whether the polygon is completely bounded by the screen
	public boolean withinViewScreen;
	
	//These 3 vectors map the 3 corners of the texture to the world coordinate
	public vector origin, rightEnd, bottomEnd;
	
	//texture that is bind to the polygon
	public texture myTexture;
	
	//only need to shade if the current fragment's depth in light space is far enough from the lightmap value
	//the threshold value is related to the orientation of the polygon in light space
	//if the normal of the polygon is almost perpendicular to the light direction, then we need a bigger threshold 
	//to avoid shadow acne
	public int shadowBias; 
	
	//Information about the texture
	public int  heightMask, widthMask, widthBits, heightBits;
	public float textureWidth, textureHeight;
	public float textureWidthInverse, textureHeightInverse;
	
	//The size of one texel
	public float textureScaleX, textureScaleY;
		

	//the number of times texture repeats itself along the polygon
	public float scaleX, scaleY;
	
	//the 3D  object which this polygon belongs to
	public solidObject parentObject;
	
	//A pool of vectors which will be used for vector arithmetic
	public static vector 
		tempVector1 = new vector(0,0,0),
		tempVector2 = new vector(0,0,0),
		tempVector3 = new vector(0,0,0),
		tempVector4 = new vector(0,0,0),
		tempVector5 = new vector(0,0,0),
		tempVector6 = new vector(0,0,0);
	
	
	//whether the polygon is visible
	public boolean visible;
	
	//whether the polygon is visible in light space
	public boolean visibleInLightSpace;
	
	//number of vertices are behind of the clip plane
	public int  numberOfVerticesBehindClipPlane;
	
	//the amount of vertex after clipping
	public  int visibleCount;
	
	//type of the polygon
	public byte type; 
	
	//the diffuse/ambient intensity of this polygon
	public int diffuse_I;
	public int Ambient_I = 16;     //the default ambient intensity is 16
	public int reflectance = 96;
	
	
	//diffuse value at vertex (only for polygons with 3 vertex)
	public byte[] diffuse = new byte[3];
	
	//default light source
	public vector lightDirection = sunLight.lightDirection;
	
	//the color of polygon if it is defined as soild 
	public int color;
	
	//light map texture  for this polygon
	//public int lightMapTextureIndex;
	
	//max texel change rate in x direction;
	//public int max_dx = 512;
	
	//Whether  origin, rightEnd and bottomEnd vectors match exactly the corners of the polygon
	public boolean textureFitPolygon;
	
	public boolean smoothShading;
	public int textureScaledWidth;
	public double I_left, I_right, I_difference;
	public static boolean recreateTextureCoordinateFlag;
	
	//Constuctor of the polygon class, it will only accept convex polygons	
	public polygon3D(vector[] vertex3D, vector origin,  vector  rightEnd, vector bottomEnd,  texture myTexture, float scaleX, float scaleY, int type){
		this.type = (byte)type;
		this.vertex3D = vertex3D;
		this.myTexture = myTexture;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		L = (byte)vertex3D.length;
		diffuse_I = 31;
		shadowBias = 30000;
		
		if(recreateTextureCoordinateFlag){
			origin = origin.myClone();
			rightEnd = rightEnd.myClone();
			bottomEnd = bottomEnd.myClone();
		}
		
		//test if  origin, rightEnd and bottomEnd vectors match exactly the corners of the polygon
		for(int i = 0; i < L; i++){
			if(vertex3D[i] == origin){
				textureFitPolygon = true;
				break;
			}
			textureFitPolygon = false;
		}
		if(textureFitPolygon){
			for(int i = 0; i < L; i++){
				if(vertex3D[i] == rightEnd){
					textureFitPolygon = true;
					break;
				}
				textureFitPolygon = false;
			}
		}
		if(textureFitPolygon){
			for(int i = 0; i < L; i++){
				if(vertex3D[i] == bottomEnd){
					textureFitPolygon = true;
					break;
				}
				textureFitPolygon = false;
			}
		}
		
		
		//set the tempVertex to the vertex3D
		tempVertex = new vector[L];
		for(int i = 0; i < L; i++){
			tempVertex[i] = new vector(0,0,0);
			tempVertex[i].set(vertex3D[i]);
		}
		
		//find normal vector of the polygon (in world coordinate)
		normal = new vector(0,0,0);
		findNormal();
		
		
		//find centre of the polygon (in world coordinate)
		centre = new vector(0,0,0); 
		for(int i = 0; i < tempVertex.length; i++)
			centre.add(tempVertex[i]);
		centre.scale(1.0f/tempVertex.length);
		
		
		if(origin != null){
			this.origin = origin;
			this.rightEnd = rightEnd;
			 this.bottomEnd = bottomEnd;
			
		}
		
		//get the texture information if the polygon is bonded with a texture
		if(myTexture != null){

			textureWidth = scaleX* myTexture.width;
			textureHeight =scaleY*myTexture.height;
			textureWidthInverse = 1f/textureWidth;
			textureHeightInverse = 1f/textureHeight;
			
			heightMask = myTexture.heightMask;
			widthMask = myTexture.widthMask;
			widthBits = myTexture.widthBits;
			heightBits = myTexture.heightBits;
			
			textureScaledWidth = (int)(myTexture.width*scaleX);
		
			//find the size of one texel in the world coordinate
			tempVector1.set(origin);
			tempVector1.subtract(rightEnd);
			float l = tempVector1.getLength();
			textureScaleX = l/myTexture.width;

			tempVector1.set(origin);
			tempVector1.subtract(bottomEnd);
			l = tempVector1.getLength();
			textureScaleY = l/myTexture.height;
			
			textureScaleX = textureScaleX/scaleX;
			textureScaleY = textureScaleY/scaleY;
		}else{
			textureScaleX = 1;
			textureScaleY = 1;
		}
		
		//init vertex2D, notice that the size of vertex2D is bigger than vertex3D, because after clipping
		//it is possilbe to generate one more vertex for the polygon.
		vertex2D = new vector[L+1];
		for(int i = 0; i < L+1; i++)
			vertex2D[i] = new vector(0,0,0);
		
		
		//find the initial diffuse intensity of this polygon
		findDiffuse();
	}
	
	//update this polygon based on camera movement in each frame
	public void update(){		
		
		//back face culling
		tempVector1.set(camera.position);
		tempVector1.subtract(vertex3D[0]);
		if(tempVector1.dot(normal) <= 0){
			visible = false;
			
			return;
		}
				
		//translate vertex from world space to camera space
		float x = 0,y = 0, z = 0, 
		camX = camera.position.x, camY = camera.position.y, camZ = camera.position.z,
		sinXZ = camera.sinXZ_angle,
		cosXZ = camera.cosXZ_angle,
		sinYZ = camera.sinYZ_angle, 
		cosYZ = camera.cosYZ_angle;
	
		
		withinViewScreen = false;
		visible = true;
		if(parentObject != null){
			if(parentObject.withinViewScreen){
				for(int i = 0; i < L; i++){
					tempVector5 =  vertex3D[i];
					tempVector6 = vertex2D[i];
					
					//shifting
					x = tempVector5.x - camX;
				 	y = tempVector5.y - camY;
					z = tempVector5.z - camZ;
					
					//rotating
					tempVector6.x = cosXZ*x - sinXZ*z;
					tempVector6.z = sinXZ*x + cosXZ*z;
					
					z = tempVector6.z;
					
					tempVector6.y = cosYZ*y - sinYZ*z;
					tempVector6.z = sinYZ*y + cosYZ*z;
					tempVector6.updateLocation();
				}
				
				withinViewScreen = true;
				visible = true;
				visibleCount = L;
				return;
			}
		}
		
		for(int i = 0; i < L; i++){
			//shifting
			x = vertex3D[i].x - camX;
		 	y = vertex3D[i].y - camY;
			z = vertex3D[i].z - camZ;
			
			//rotating
			tempVertex[i].x = cosXZ*x - sinXZ*z;
			tempVertex[i].z = sinXZ*x + cosXZ*z;
			
			z = tempVertex[i].z;
			
			tempVertex[i].y = cosYZ*y - sinYZ*z;
			tempVertex[i].z = sinYZ*y + cosYZ*z;
		}
		
		//find the number of vertices that are behind  clip plane
		numberOfVerticesBehindClipPlane = 0;
		for(int i = 0; i < L; i++){
			if(tempVertex[i].z <= 0.1){
				numberOfVerticesBehindClipPlane++;
			}
		}
		
		//if all vertices
		if(numberOfVerticesBehindClipPlane == L){
			visible = false;
			return;
		}
		
		
		findClipping();
		
	}
	
	//update vision polygon
	public void update_visionPolygon(){
		vector cameraPosition = postProcessingThread.cameraPosition;
		
				
		//translate vertex from world space to camera space
		float x = 0,y = 0, z = 0, 
		camX = cameraPosition.x, camY = cameraPosition.y, camZ = cameraPosition.z,
		sinXZ = postProcessingThread.sinXZ,
		cosXZ = postProcessingThread.cosXZ,
		sinYZ = postProcessingThread.sinYZ, 
		cosYZ = postProcessingThread.cosYZ;
	
		
		
		for(int i = 0; i < L; i++){
			//shifting
			x = vertex3D[i].x - camX;
		 	y = vertex3D[i].y - camY;
			z = vertex3D[i].z - camZ;
			
			//rotating
			vertex2D[i].x = cosXZ*x - sinXZ*z;
			vertex2D[i].z = sinXZ*x + cosXZ*z;
			
			z = vertex2D[i].z;
			
			vertex2D[i].y = cosYZ*y - sinYZ*z;
			vertex2D[i].z = sinYZ*y + cosYZ*z;
			
			if(vertex2D[i].z < 0.01)
				vertex2D[i].z = 0.01f;
			vertex2D[i].updateLocation();
		}
		
	
		visible = true;
		visibleCount = L;
		return;
	}
	
	
	
	
	//update the polygon in light space
	public void update_lightspace(){
		//back face culling
		visibleInLightSpace = true;
		float normalDotLight = normal.dot(sunLight.lightDirection);
		
		if(normalDotLight >= 0){
			visibleInLightSpace = false;
			return;
		}
		
		
		//translate vertex from world space to light space
		float x = 0,y = 0, z = 0, 
		sunX = sunLight.position.x, sunY = sunLight.position.y, sunZ = sunLight.position.z,
		sinXZ = sunLight.sinXZ_angle,
		cosXZ = sunLight.cosXZ_angle,
		sinYZ = sunLight.sinYZ_angle, 
		cosYZ = sunLight.cosYZ_angle;
		
		
		for(int i = 0; i < L; i++){
			tempVector5 =  vertex3D[i];
			tempVector6 = vertex2D[i];
			
			//shifting
			x = tempVector5.x - sunX;
		 	y = tempVector5.y - sunY;
			z = tempVector5.z - sunZ;
			
			//rotating
			tempVector6.x = cosXZ*x - sinXZ*z;
			tempVector6.z = sinXZ*x + cosXZ*z;
			
			z = tempVector6.z;
			
			tempVector6.y = cosYZ*y - sinYZ*z;
			tempVector6.z = sinYZ*y + cosYZ*z;
			tempVector6.updateLocationOrthognal();
			tempVector6.z_lightspace = tempVector6.z;
			
			tempVector5.z_lightspace = tempVector6.z;
			tempVector5.screenX_lightspace = tempVector6.screenX_lightspace;
			tempVector5.screenY_lightspace = tempVector6.screenY_lightspace;
		}
		
		if(type == 1){
			rasterizer.renderShadow(this);
		}else{
			if(type == 4)
				rasterizer.renderShadowRemover(this);
			else if(type == 9)
				rasterizer.renderCloakedShadow(this);
		}
		
	}
	
	public void update_lightspace_withoutDrawing(){
		//back face culling
		visibleInLightSpace = true;
		if(sunLight.lightDirection.dot(normal) > 0){
			visibleInLightSpace = false;
			return;
		}
		
		//translate vertex from world space to light space
		float x = 0,y = 0, z = 0, 
		sunX = sunLight.position.x, sunY = sunLight.position.y, sunZ = sunLight.position.z,
		sinXZ = sunLight.sinXZ_angle,
		cosXZ = sunLight.cosXZ_angle,
		sinYZ = sunLight.sinYZ_angle, 
		cosYZ = sunLight.cosYZ_angle;
		
		for(int i = 0; i < L; i++){
			//shifting
			x = vertex3D[i].x - sunX;
		 	y = vertex3D[i].y - sunY;
			z = vertex3D[i].z - sunZ;
			
			//rotating
			vertex2D[i].x = cosXZ*x - sinXZ*z;
			vertex2D[i].z = sinXZ*x + cosXZ*z;
			
			z = vertex2D[i].z;
			
			vertex2D[i].y = cosYZ*y - sinYZ*z;
			vertex2D[i].z = sinYZ*y + cosYZ*z;
			vertex2D[i].updateLocationOrthognal();
			vertex2D[i].z_lightspace = vertex2D[i].z;
			
			vertex3D[i].z_lightspace = vertex2D[i].z;
			vertex3D[i].screenX_lightspace = vertex2D[i].screenX_lightspace;
			vertex3D[i].screenY_lightspace = vertex2D[i].screenY_lightspace;
		}		
	}
	
	
	

	
	//clipping 
	public  void findClipping(){
		visibleCount = 0;
		//the clipping algorithm iterate through all the vertex of the polygons, if it finds
		//a vertex which is behind the clipping plane(z = 0.001), then generate 2 new vertex on the
		//clipping plane
		
		for(int i = 0; i < L; i++){
			if(tempVertex[i].z >= 0.1){
				vertex2D[visibleCount].set(tempVertex[i]);
				vertex2D[visibleCount].updateLocation();
				visibleCount++;
			} else{
				int index = (i+L - 1)%L;
				if(tempVertex[index].z >= 0.1005){
					tempVertex[i].approximatePoint(visibleCount, tempVertex[index], this);
					visibleCount++;
				}
				index = (i+1)%L;
				if(tempVertex[index].z >= 0.1005){
					tempVertex[i].approximatePoint(visibleCount, tempVertex[index], this);
					visibleCount++;
				}
			}
		}
	}


	//find diffuse intensity of this polygon
	public void findDiffuse(){		
		//calculate the diffuse intensity from the light source	
		tempVector1.set(-lightDirection.x, -lightDirection.y, -lightDirection.z);
		double I = normal.dot(tempVector1);
		
		diffuse_I = Ambient_I + (int)(I*reflectance);
		
		if(I < 0)
			diffuse_I = Ambient_I;
		
	}
	
	//create a smooth 1 dimensional shade map for the polygon. Only works if the polygon belongs to a
	//cylindrical object.
	public void createShadeSpan(vector theCenter, vector v0, vector v1){
		smoothShading = true;
		
		tempVector1.set(v0);
		tempVector1.subtract(theCenter);
		tempVector1.unit();
		tempVector2.set(v1);
		tempVector2.subtract(theCenter);
		tempVector2.unit();
		
		tempVector3.set(-lightDirection.x, -lightDirection.y, -lightDirection.z);
		
		I_left = tempVector1.dot(tempVector3)*reflectance + Ambient_I;
		if(I_left < Ambient_I)
			I_left = Ambient_I;
		
		I_right = tempVector2.dot(tempVector3)*reflectance + Ambient_I;
		if(I_right < Ambient_I)
			I_right = Ambient_I;
		
		I_difference = (I_right - I_left)/textureScaledWidth;
	}
	
	public void findNormal(){
		tempVector1.set(vertex3D[1]);
		tempVector1.subtract(vertex3D[0]);
		tempVector2.set(vertex3D[2]);
		tempVector2.subtract(vertex3D[1]);
		normal.cross(tempVector1, tempVector2);
		normal.unit();

	}
	
	public void draw(){
		//send this polygon to rasterizer
		if(visible){
			mainThread.theAssetManager.polygonCount++;
			rasterizer.rasterize(this);
		}
	}
}
