package core;

import particles.explosion;

//The rasterizer class will draw any polygon into the screen buffer.
//The texture mapping methods will differ depends on the type of polygon,
//The universal formula for texture mapping is:
//               x = A dot W/C dot W
//               y = B dot W/C dot W
// where    A = V cross O  B = O cross U  C = U cross V,   
//          V, a vector representing the texture's y direction
//          U, a vector representing the texture's x direction
//          O, the origin of the texture 
//          W is the projection length of the texel on the clipping plane
//          x, y is the texture coordinate 
//
//Won't handle z-axis rotation. 




public class rasterizer {
	
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	public static int shadowmap_width = mainThread.shadowmap_width;
	public static int Z_length = vector.Z_length;
	public static int w_ = screen_width-1;
	public static int h_ = screen_height-1;
	public static int shadowmap_w_ = shadowmap_width -1;
	public static int shadowmap_size = shadowmap_width * shadowmap_width;
	public static int shadowmap_size_ = shadowmap_size -1;
	public static int half_width_ = screen_width/2 -1;
	public static int half_height = screen_height/2;
	public static int shadowmap_width_bit = mainThread.shadowmap_width_bit;
	
	//the z depth rage for terrain polygon, since the camera never rotate along x axis in his game, the max and min z depth values are fixed.
	public static int zTop, zBot, zDelta;
	
	//2 arrays that define the scan lines of the polygon
	public static int[] xLeft = new int[screen_height], xRight = new int[screen_height];
	
	//2 arrays that define the z depth across the polygon
	public static int[] zLeft = new int[screen_height], zRight = new int[screen_height];
	
	//2 arrays that define the reflections across the polygon
	public static vector[] RLeft = new vector[screen_height], RRight = new vector[screen_height];
	
	//2 arrays that define the intensity across the polygon
	public static int[] iLeft = new int[screen_height], iRight = new int[screen_height];
	
	//2 arrays that define the scan lines of the polygon in light space
	public static int[] xLeft_lightspace = new int[shadowmap_width], xRight_lightspace = new int[shadowmap_width];
	
	//2 arrays that define the z depth across the polygon in light space
	public static int[] zLeft_lightspace = new int[shadowmap_width], zRight_lightspace = new int[shadowmap_width];
	
	//a short array which represent zbuffer
	public static int[] zBuffer;
	
	public static int[] screen;
	
	public static int[] shadowBuffer;	
	public static byte[] shadowBitmap;
	
	public static short[] displacementBuffer;
	
	
	//init Texture coordinate vectors
	public static vector 
	W = new vector(0,0,0),
	O = new vector(0,0,0), 
	V = new vector(0,0,0), 
	U = new vector(0,0,0), 
	A = new vector(0,0,0), 
	B = new vector(0,0,0), 
	C = new vector(0,0,0),
	C_unit = new vector(0,0,0);
	
	
	//A pool of vectors which will be used for vector arithmetic
	public static vector 
		tempVector1 = new vector(0,0,0),
		tempVector2 = new vector(0,0,0),
		tempVector3 = new vector(0,0,0),
		tempVector4 = new vector(0,0,0);
	
	//the polygon that rasterizer is working on
	public static polygon3D poly;
	
	//these variables will represent their equivalents in the polygon3D class during rasterization
	public static vector[] tempVertex, vertex2D, reflections;
	public static int widthMask, heightMask, widthBits, diffuse_I;
	public static float A_offset, B_offset, C_offset;
	
	//the transparency level of the polygon. 
	public static int alpha;
	
	//the amount of vertex after clipping
	public static int visibleCount;
	
	//temporary variables that will be used in texture mapping
	public static float aDotW, bDotW, cDotW, cDotWInverse, w, textureHeight, textureWidth;
	public static int BigX, BigY, d_x, d_y, k, X1, Y1, BigDx, BigDy, dx, dy, dz, X, Y, textureIndex, temp, temp1, temp2, r,g,b, scale, yOffset, xOffset, x_right, x_left, z_left, z_right,  start, end;
	public static short I, variation;
	public static vector dReflection, startReflection, endReflection;
	public static int z_origin, dz_xdirection, dz_ydirection, XY_origin_x, XY_origin_y, dXY_xdirection_x, dXY_xdirection_y, dXY_ydirection_x, dXY_ydirection_y;
	
	public static int cloakedThreshold, modelCenterX, modelCenterY, cloaked_x, cloaked_y, cloakedShadowThreshold;
	public static byte[] cloakTexture;
	

	//initialize rasteriser 
	public static void init(){
		for(int i = 0; i < screen_height; i++ ){
			RLeft[i] = new vector(0,0,0);
			RRight[i] = new vector(0,0,0);
		}
		
		dReflection = new vector(0,0,0);
		startReflection = new vector(0,0,0);
		endReflection = new vector(0,0,0);
		screen = mainThread.screen;
		zBuffer = mainThread.zBuffer; 
		shadowBitmap = mainThread.shadowBitmap;
		shadowBuffer = sunLight.shadowBuffer;
		displacementBuffer = mainThread.displacementBuffer;
		zTop = 0;
		zBot = 0;
		zDelta = 0;
	}
	
	
	
	//start rasterization
	public static void rasterize(polygon3D polygon){
		poly = polygon;
		widthMask = poly.widthMask;
		heightMask = poly.heightMask;
		textureHeight = poly.textureHeight;
		textureWidth = poly.textureWidth;
		widthBits = poly.widthBits;
		vertex2D = poly.vertex2D;
		visibleCount = poly.visibleCount;
		
		
		//for different polygons, the texture mapping alogrithm will differ depend 
		//on the nature of the polygon in order to optimize rendering
		if(poly.type == 1){
			
			scanPolygon();
			findVectorOUV();
			if(poly.visibleInLightSpace){
				if(!poly.smoothShading){
					renderShadowedPolygon();
				}else{
					renderShadowedPolygon_smooth();
				}
			}else
				renderBasicPolygon();
		}else if(polygon.type == 0){
			scanPolygon();
			findVectorOUV();
			renderSoildPolygon();
		}else if(polygon.type == 2){
			scanPolygon();
			findVectorOUV();
			renderTerrainPolygon();
		}else if(polygon.type == 3){
			scanPolygon();
			findVectorOUV();
			renderUnderGroundPolygon();
		}else if(polygon.type == 4){
			scanPolygon();
			findVectorOUV();
			renderZbufferRemoverPolygon();
		}else if(polygon.type == 5){
			scanPolygon_Gouraud();
			findVectorOUV();
			//if(poly.visibleInLightSpace){
				renderShadowedPolygon_Gouraud();
			//}else
			//	renderBasicPolygon();
		}else if(polygon.type == 6){
			scanPolygon();
			findVectorOUV();
			renderWaterPolygon();
		}else if(polygon.type == 7){
			scanPolygon();
			findVectorOUV();
			renderLakeBottomPolygon();
		}else if(polygon.type == 8){
			scanPolygon();
			findVectorOUV();
			renderRoadSidePolygon();
		}else if(polygon.type == 9){
			scanPolygon();
			findVectorOUV();
			renderCloakedPolygon();
		}else if(polygon.type == 10){
			scanPolygon();
			findVectorOUV();
			renderDeployGridPolygon();
		}
	}
	
	//calculate O,U and V
	public static void findVectorOUV(){
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
		
		
		C_unit.cross(U, V); 
		
		w = 0x1000000/(Z_length*C_unit.dot(O));
		
		U.scale(poly.textureScaleX);
		V.scale(poly.textureScaleY);

		
		A.cross(V,O);
		B.cross(O,U);
		C.cross(U,V);
		
		
	}
		
	//convert a polygon to scan lines
	public static void scanPolygon(){
		start = screen_height;
		end = -1; 
		int startX, g, startY, endY, temp_x;
		float gradient;
		
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			vector v2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
			}else{
				v2 = vertex2D[i+1];
			}

			boolean downwards = false;

			//ensure v1.y < v2.y;
			if (v1.screenY> v2.screenY) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			float dy = v2.screenY - v1.screenY;
			
			// ignore horizontal lines
			if (dy == 0) {
				
				continue;
			}
			
			
			startY = Math.max((int)(v1.screenY) + 1, 0);
			endY = Math.min((int)(v2.screenY), h_);
			
			
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
				
		
			//calculate x increment along this edge
			gradient = (v2.screenX - v1.screenX)* 2048 /dy;
			startX = (int)((v1.screenX *2048) +  (startY - v1.screenY) * gradient);
			g = (int)(gradient);
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
	
				if(downwards){
					if(temp_x >= 0)
						xLeft[y] = temp_x;
					else
						xLeft[y] = 0;
				}else{
					if(temp_x <= w_)
						xRight[y] = temp_x;
					else
						xRight[y] = screen_width;
				}
				startX+=g;
	
			}
		}
	}
	
	//convert a polygon to scan lines
	public static void scanPolygon_Gouraud(){
		start = screen_height;
		end = -1; 
		int startX, g, startY, endY, temp_x, startDiffuse, gDiffuse, temp_diffuse;
		float gradient, diffuseGradient;
		
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			int diffuse1 = poly.diffuse[i]*2048;
			vector v2;
			int diffuse2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
				diffuse2 = poly.diffuse[0]*2048;
			}else{
				v2 = vertex2D[i+1];
				diffuse2 = poly.diffuse[i+1]*2048;
			}

			boolean downwards = false;

			//ensure v1.y < v2.y;
			if (v1.screenY> v2.screenY) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
				
				int tempDiffuse = diffuse1;
				diffuse1 = diffuse2;
				diffuse2 = tempDiffuse;
			}
			float dy = v2.screenY - v1.screenY;
			
			// ignore horizontal lines
			if (dy == 0) {
				
				continue;
			}
			
			
			startY = Math.max((int)(v1.screenY) + 1, 0);
			endY = Math.min((int)(v2.screenY), h_);
			
			
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
				
		
			//calculate x increment along this edge
			gradient = (v2.screenX - v1.screenX)* 2048 /dy;
			startX = (int)((v1.screenX *2048) +  (startY - v1.screenY) * gradient);
			g = (int)(gradient);
			
			diffuseGradient = (diffuse2 - diffuse1)/dy;
			startDiffuse = (int)(diffuse1 + (startY - v1.screenY)*diffuseGradient);
			gDiffuse = (int)(diffuseGradient);
			
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
			    temp_diffuse = startDiffuse;
			
				if(downwards){
					if(temp_x >= 0)
						xLeft[y] = temp_x;
					else
						xLeft[y] = 0;
					
					iLeft[y] = temp_diffuse;
				}else{
					if(temp_x <= w_)
						xRight[y] = temp_x;
					else
						xRight[y] = screen_width;
					iRight[y] = temp_diffuse;
				}
				startX+=g;
				startDiffuse+=gDiffuse;
			}
		}
	}
	
	//disable shadow casting for the region  within the silhouette of the polygon
	public static void renderShadowRemover(polygon3D polygon){
		poly = polygon;
		vertex2D = poly.vertex2D;
		visibleCount = poly.L;
		
		start = shadowmap_width;
		end = -1; 
		
		float gradient, dy;
		int startX, g, startY, endY, temp_x, dx;
		
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			vector v2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
			}else{
				v2 = vertex2D[i+1];
			}
			
			boolean downwards = false;
			
			
			//ensure v1.y < v2.y;
			if (v1.screenY_lightspace> v2.screenY_lightspace) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			
			dy = v2.screenY_lightspace - v1.screenY_lightspace;
			// ignore horizontal lines
			if (dy == 0) {
				continue;
			}
			
			startY = Math.max((int)(v1.screenY_lightspace) + 1, 0);
			endY = Math.min((int)(v2.screenY_lightspace), shadowmap_w_);
			
			
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
			
			//calculate x increment along this edge
			gradient = (v2.screenX_lightspace - v1.screenX_lightspace)* 2048 /dy;
			startX = (int)((v1.screenX_lightspace *2048) +  (startY - v1.screenY_lightspace) * gradient);
			g = (int)(gradient);
			
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
	
				if(downwards){
					xLeft_lightspace[y] = temp_x;
					
				}else{
					xRight_lightspace[y] = temp_x ;
					
				}
				startX+=g;
			}
			
		}
		
		int index, endX;
	
		for(int y = start; y <= end; y++){
			startX = xLeft_lightspace[y];
			endX = xRight_lightspace[y];
			dx =  endX - startX;
			if(dx <= 0)
				continue;

			index = startX + y*shadowmap_width;
			for(;startX < endX; startX++, index++){
				shadowBuffer[index] = Integer.MAX_VALUE;  //set the distance of the pixel in light space to infinite away
				
			}	
		}
	}

	
	//draw the polygon on the shadow buffer from light point of view
	public static void renderShadow(polygon3D polygon){
		poly = polygon;
		vertex2D = poly.vertex2D;
		visibleCount = poly.L;
		
		start = shadowmap_width;
		end = -1; 
		
	
		float gradient, dy;
		int startX, g, startY, endY, temp_x, startZ, dz, dx;
	
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			vector v2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
			}else{
				v2 = vertex2D[i+1];
			}
			
			boolean downwards = false;
			
			
			//ensure v1.y < v2.y;
			if (v1.screenY_lightspace> v2.screenY_lightspace) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			
			dy = v2.screenY_lightspace - v1.screenY_lightspace;
			// ignore horizontal lines
			if (dy == 0) {
				continue;
			}
			
			startY = Math.max((int)(v1.screenY_lightspace) + 1, 0);
			endY = Math.min((int)(v2.screenY_lightspace), shadowmap_w_);
			
				
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
			
			//calculate x increment along this edge
			gradient = (v2.screenX_lightspace - v1.screenX_lightspace)* 2048 /dy;
			startX = (int)((v1.screenX_lightspace *2048) +  (startY - v1.screenY_lightspace) * gradient);
			g = (int)(gradient);
			
			//calculate z depth increment along this edge
			startZ = (int)(v1.z_lightspace * 1048576);
			dz = (int)((v2.z_lightspace * 1048576  - startZ)/dy);
			startZ = (int)(startZ + (startY - v1.screenY_lightspace)*dz);
			
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
	
				if(downwards){
					xLeft_lightspace[y] = temp_x;
					zLeft_lightspace[y] = startZ;
				}else{
					xRight_lightspace[y] = temp_x ;
					zRight_lightspace[y] = startZ;
				}
				startX+=g;
				startZ+=dz;			
			}
			
		}
			
		int index, endX;
	
		for(int y = start; y <= end; y++){
			startX = xLeft_lightspace[y];
			endX = xRight_lightspace[y];
			dx =  endX - startX;
			if(dx <= 0)
				continue;
			startZ = zLeft_lightspace[y];
			dz = (zRight_lightspace[y] - startZ)/dx;
			index = startX + y*shadowmap_width;
			for(;startX < endX; startX++, index++, startZ += dz){
				if(startZ < shadowBuffer[index&shadowmap_size_]){
					shadowBuffer[index&shadowmap_size_] = startZ;
				}
			}	
		}
	}
	
	//draw the polygon on the shadow buffer from light point of view
	public static void renderCloakedShadow(polygon3D polygon){
		poly = polygon;
		vertex2D = poly.vertex2D;
		visibleCount = poly.L;
		
		start = shadowmap_width;
		end = -1; 
		
	
		float gradient, dy;
		int startX, g, startY, endY, temp_x, startZ, dz, dx;
		
		for(int i = 0; i < visibleCount; i++){
			vector v1 = vertex2D[i];
			vector v2;
			
			if(i == visibleCount -1 ){
				v2 = vertex2D[0];
			}else{
				v2 = vertex2D[i+1];
			}
			
			boolean downwards = false;
			
			
			//ensure v1.y < v2.y;
			if (v1.screenY_lightspace> v2.screenY_lightspace) {
				downwards = true;
				vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			
			dy = v2.screenY_lightspace - v1.screenY_lightspace;
			// ignore horizontal lines
			if (dy == 0) {
				continue;
			}
			
			startY = Math.max((int)(v1.screenY_lightspace) + 1, 0);
			endY = Math.min((int)(v2.screenY_lightspace), shadowmap_w_);
			
			
			if(startY < start )
				start = startY;

			if(endY > end)
				end = endY;
			
			//calculate x increment along this edge
			gradient = (v2.screenX_lightspace - v1.screenX_lightspace)* 2048 /dy;
			startX = (int)((v1.screenX_lightspace *2048) +  (startY - v1.screenY_lightspace) * gradient);
			g = (int)(gradient);
			
			//calculate z depth increment along this edge
			startZ = (int)(v1.z_lightspace * 1048576);
			dz = (int)((v2.z_lightspace * 1048576  - startZ)/dy);
			startZ = (int)(startZ + (startY - v1.screenY_lightspace)*dz);
			
			for (int y=startY; y<=endY; y++) {
				temp_x = startX>>11;
	
				if(downwards){
					xLeft_lightspace[y] = temp_x;
					zLeft_lightspace[y] = startZ;
				}else{
					xRight_lightspace[y] = temp_x ;
					zRight_lightspace[y] = startZ;
				}
				startX+=g;
				startZ+=dz;			
			}
			
		}
		
		int index, endX;
		int the_index = 0;
		
		for(int y = start; y <= end; y++){
			startX = xLeft_lightspace[y];
			endX = xRight_lightspace[y];
			dx =  endX - startX;
			if(dx <= 0)
				continue;
			startZ = zLeft_lightspace[y];
			dz = (zRight_lightspace[y] - startZ)/dx;
			index = startX + y*shadowmap_width;
			for(;startX < endX; startX++, index++, the_index++, startZ += dz){
				
				if(cloakTexture[the_index] >= cloakedShadowThreshold){
					if(startZ < shadowBuffer[index&shadowmap_size_]){
						shadowBuffer[index&shadowmap_size_] = startZ;
					}
				}
			}	
		}
	}

	//render basic polygon that can't  be shadowed (e.g polygon which back facing the light source)
	public static void renderBasicPolygon(){
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[]colorTable = gameData.colorTable[diffuse_I];
		int index;
		
	
		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		double Aoffset,Boffset,Coffset;
		
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 16, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
						
						
						if(zBuffer[index] < z_left){
							zBuffer[index] = z_left;
							textureIndex = ( ((d_x>>4) + X)&widthMask) + ((((d_y>>4) + Y)&heightMask)<<widthBits);
							screen[index] = colorTable[texture[textureIndex]];
							shadowBitmap[index]  = -127;
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
					
					if(zBuffer[index] < z_left){
						zBuffer[index] = z_left;
						textureIndex = (((d_x/offset) + X)&widthMask) + ((((d_y/offset) + Y)&heightMask)<<widthBits);
					
						shadowBitmap[index]  = -127;
					    screen[index] = colorTable[texture[textureIndex]];
						
					}
				}
				
				break;
			}
		}
	}
	

	//render polygon that below ground level
	public static void renderUnderGroundPolygon(){
		if(zTop ==0)
			calculateDepthRangeAtGround();
		
		int depth;
		
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		byte shadowLevel = 13;
		float diffuse_intensity = gameData.intensityTable[diffuse_I];
		float ambient_intensity = gameData.intensityTable[poly.Ambient_I];
		float shadow_intensity = diffuse_intensity * 13f/32f;
		
		float difference = shadow_intensity - ambient_intensity;
		if(difference < 0){
			if(difference < -0.2)
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity) - 127);
			else
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity));
		}
		int shadowBias = poly.shadowBias;
		
		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		double Aoffset,Boffset,Coffset;
		
		//recalculate the screen position of orgin, rightEnd,  bottomEnd in light space
		//if they dont fit the corners of the polygon
		if(!poly.textureFitPolygon){
			tempVector1.set(poly.origin);
			tempVector1.subtract(sunLight.position);
			tempVector1.rotate_XZ(sunLight.XZ_angle);
			tempVector1.rotate_YZ(sunLight.YZ_angle);
			tempVector1.updateLocationOrthognal();
			tempVector1.z_lightspace = tempVector1.z;
			
			tempVector2.set(poly.rightEnd);
			tempVector2.subtract(sunLight.position);
			tempVector2.rotate_XZ(sunLight.XZ_angle);
			tempVector2.rotate_YZ(sunLight.YZ_angle);
			tempVector2.updateLocationOrthognal();
			tempVector2.z_lightspace = tempVector2.z;
			
			tempVector3.set(poly.bottomEnd);
			tempVector3.subtract(sunLight.position);
			tempVector3.rotate_XZ(sunLight.XZ_angle);
			tempVector3.rotate_YZ(sunLight.YZ_angle);
			tempVector3.updateLocationOrthognal();
			tempVector3.z_lightspace = tempVector3.z;
			
			
		}else{
			tempVector1.z_lightspace = poly.origin.z_lightspace;
			tempVector2.z_lightspace = poly.rightEnd.z_lightspace;
			tempVector3.z_lightspace = poly.bottomEnd.z_lightspace;
			tempVector1.screenX_lightspace = poly.origin.screenX_lightspace;
			tempVector2.screenX_lightspace =  poly.rightEnd.screenX_lightspace;
			tempVector3.screenX_lightspace = poly.bottomEnd.screenX_lightspace;
			tempVector1.screenY_lightspace = poly.origin.screenY_lightspace;
			tempVector2.screenY_lightspace =  poly.rightEnd.screenY_lightspace;
			tempVector3.screenY_lightspace = poly.bottomEnd.screenY_lightspace;
		}
		
	
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			depth = zTop + i*zDelta;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 16, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
						
						
						if(zBuffer[index] < z_left){
							zBuffer[index] = depth + 20;
							xPos = (d_x>>4) + X;
							yPos = (d_y>>4) + Y;
							textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
							z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
							screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
							screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
							
						
							
							int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
							
						
							if(z_lightspace - shadowBuffer[size] < shadowBias){
								shadowBitmap[index]  = 32;
							}else{
								shadowBitmap[index]  = shadowLevel;
								
							}
							screen[index] = colorTable[texture[textureIndex]];
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
					
					if(zBuffer[index] < z_left){
						zBuffer[index] = depth + 20;
						xPos = (d_x/offset) + X;
						yPos = (d_y/offset) + Y;
						textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < shadowBias){
							shadowBitmap[index]  = 32;
						}else{
							shadowBitmap[index]  = shadowLevel;
						}
						screen[index] = colorTable[texture[textureIndex]];
						
					}
				}
				
				break;
			}
		}
	}
		
	
	
	
	//redner basic texture mapped polygon
	public static void renderShadowedPolygon(){
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		byte shadowLevel = 13;
		float diffuse_intensity = gameData.intensityTable[diffuse_I];
		float ambient_intensity = gameData.intensityTable[poly.Ambient_I];
		float shadow_intensity = diffuse_intensity * 13f/32f;
		
		float difference = shadow_intensity - ambient_intensity;
		if(difference < 0){
			if(difference < -0.2)
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity) - 127);
			else
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity));
		}
		int shadowBias = poly.shadowBias;
		
		
		
	
		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		double Aoffset,Boffset,Coffset;
		
		//recalculate the screen position of orgin, rightEnd,  bottomEnd in light space
		//if they dont fit the corners of the polygon
		if(!poly.textureFitPolygon){
			tempVector1.set(poly.origin);
			tempVector1.subtract(sunLight.position);
			tempVector1.rotate_XZ(sunLight.XZ_angle);
			tempVector1.rotate_YZ(sunLight.YZ_angle);
			tempVector1.updateLocationOrthognal();
			tempVector1.z_lightspace = tempVector1.z;
			
			tempVector2.set(poly.rightEnd);
			tempVector2.subtract(sunLight.position);
			tempVector2.rotate_XZ(sunLight.XZ_angle);
			tempVector2.rotate_YZ(sunLight.YZ_angle);
			tempVector2.updateLocationOrthognal();
			tempVector2.z_lightspace = tempVector2.z;
			
			tempVector3.set(poly.bottomEnd);
			tempVector3.subtract(sunLight.position);
			tempVector3.rotate_XZ(sunLight.XZ_angle);
			tempVector3.rotate_YZ(sunLight.YZ_angle);
			tempVector3.updateLocationOrthognal();
			tempVector3.z_lightspace = tempVector3.z;
			
			
		}else{
			tempVector1.z_lightspace = poly.origin.z_lightspace;
			tempVector2.z_lightspace = poly.rightEnd.z_lightspace;
			tempVector3.z_lightspace = poly.bottomEnd.z_lightspace;
			tempVector1.screenX_lightspace = poly.origin.screenX_lightspace;
			tempVector2.screenX_lightspace =  poly.rightEnd.screenX_lightspace;
			tempVector3.screenX_lightspace = poly.bottomEnd.screenX_lightspace;
			tempVector1.screenY_lightspace = poly.origin.screenY_lightspace;
			tempVector2.screenY_lightspace =  poly.rightEnd.screenY_lightspace;
			tempVector3.screenY_lightspace = poly.bottomEnd.screenY_lightspace;
		}
		
	
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
		
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 16, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
						
						if(zBuffer[index] < z_left){
							zBuffer[index] = z_left;
							xPos = (d_x>>4) + X;
							yPos = (d_y>>4) + Y;
							textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
							z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
							screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
							screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
							
						
							int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
							
						
							if(z_lightspace - shadowBuffer[size] < shadowBias){
								shadowBitmap[index]  = 32;
							}else{
								shadowBitmap[index]  = shadowLevel;
								
							}
							screen[index] = colorTable[texture[textureIndex]];
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
					
					if(zBuffer[index] < z_left){
						zBuffer[index] = z_left;
						xPos = (d_x/offset) + X;
						yPos = (d_y/offset) + Y;
						textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit) ) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < shadowBias){
							shadowBitmap[index]  = 32;
						}else{
							shadowBitmap[index]  = shadowLevel;
						}
						screen[index] = colorTable[texture[textureIndex]];
						
					}
				}
				
				break;
			}
		}
	}
	
	public static void renderShadowedPolygon_Gouraud(){
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		byte shadowLevel = 13;
		float diffuse_intensity = gameData.intensityTable[diffuse_I];
		float ambient_intensity = gameData.intensityTable[poly.Ambient_I];
		float shadow_intensity = diffuse_intensity * 13f/32f;
		
		int diffuseStart, diffuseGradient, lit;
		
		float difference = shadow_intensity - ambient_intensity;
		if(difference < 0){
			if(difference < -0.2)
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity) - 127);
			else
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity));
		}
		int shadowBias = poly.shadowBias;
		
		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		double Aoffset,Boffset,Coffset;
		
		//recalculate the screen position of orgin, rightEnd,  bottomEnd in light space
		//if they dont fit the corners of the polygon
		if(!poly.textureFitPolygon){
			tempVector1.set(poly.origin);
			tempVector1.subtract(sunLight.position);
			tempVector1.rotate_XZ(sunLight.XZ_angle);
			tempVector1.rotate_YZ(sunLight.YZ_angle);
			tempVector1.updateLocationOrthognal();
			tempVector1.z_lightspace = tempVector1.z;
			
			tempVector2.set(poly.rightEnd);
			tempVector2.subtract(sunLight.position);
			tempVector2.rotate_XZ(sunLight.XZ_angle);
			tempVector2.rotate_YZ(sunLight.YZ_angle);
			tempVector2.updateLocationOrthognal();
			tempVector2.z_lightspace = tempVector2.z;
			
			tempVector3.set(poly.bottomEnd);
			tempVector3.subtract(sunLight.position);
			tempVector3.rotate_XZ(sunLight.XZ_angle);
			tempVector3.rotate_YZ(sunLight.YZ_angle);
			tempVector3.updateLocationOrthognal();
			tempVector3.z_lightspace = tempVector3.z;
			
			
		}else{
			tempVector1.z_lightspace = poly.origin.z_lightspace;
			tempVector2.z_lightspace = poly.rightEnd.z_lightspace;
			tempVector3.z_lightspace = poly.bottomEnd.z_lightspace;
			tempVector1.screenX_lightspace = poly.origin.screenX_lightspace;
			tempVector2.screenX_lightspace =  poly.rightEnd.screenX_lightspace;
			tempVector3.screenX_lightspace = poly.bottomEnd.screenX_lightspace;
			tempVector1.screenY_lightspace = poly.origin.screenY_lightspace;
			tempVector2.screenY_lightspace =  poly.rightEnd.screenY_lightspace;
			tempVector3.screenY_lightspace = poly.bottomEnd.screenY_lightspace;
		}
		
	
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
		
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			if(iLeft[i] <0 || iLeft[i] >= 260096 || iRight[i] < 0  || iRight[i] >= 260096){
				iLeft[i] = 0;
				iRight[i] = 0;
			}
			
			diffuseStart = iLeft[i];
			diffuseGradient = (iRight[i] - iLeft[i])/dx;
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 16, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz, diffuseStart+=diffuseGradient){
						
						if(zBuffer[index] < z_left){
							zBuffer[index] = z_left;
							xPos = (d_x>>4) + X;
							yPos = (d_y>>4) + Y;
							textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
							z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
							screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
							screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
							
						
							int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
							
						
							if(z_lightspace - shadowBuffer[size] < shadowBias){
								shadowBitmap[index]  = 32;
								screen[index] = gameData.colorTable[diffuseStart >> 11][texture[textureIndex]];
							}else{
								shadowBitmap[index]  = shadowLevel;
								screen[index] = colorTable[texture[textureIndex]];
							}
							
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz, diffuseStart+=diffuseGradient){
					
					if(zBuffer[index] < z_left){
						zBuffer[index] = z_left;
						xPos = (d_x/offset) + X;
						yPos = (d_y/offset) + Y;
						textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < shadowBias){
							shadowBitmap[index]  = 32;
							screen[index] = gameData.colorTable[diffuseStart >> 11][texture[textureIndex]];
							
						}else{
							shadowBitmap[index]  = shadowLevel;
							screen[index] = colorTable[texture[textureIndex]];
						}
						
						
					}
				}
				
				break;
			}
		}
	}
	
	public static void renderShadowedPolygon_smooth(){
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		byte shadowLevel = 13;
		float diffuse_intensity = gameData.intensityTable[diffuse_I];
		float ambient_intensity = gameData.intensityTable[poly.Ambient_I];
		float shadow_intensity = diffuse_intensity * 13f/32f;
		
		float difference = shadow_intensity - ambient_intensity;
		if(difference < 0){
			if(difference < -0.2)
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity) - 127);
			else
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity));
		}
		int shadowBias = poly.shadowBias;
		
		double I_left = poly.I_left;
		double I_difference = poly.I_difference;
		int textureScaledWidth = poly.textureScaledWidth;
	
		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		double Aoffset,Boffset,Coffset;
		
		//recalculate the screen position of orgin, rightEnd,  bottomEnd in light space
		//if they dont fit the corners of the polygon
		if(!poly.textureFitPolygon){
			tempVector1.set(poly.origin);
			tempVector1.subtract(sunLight.position);
			tempVector1.rotate_XZ(sunLight.XZ_angle);
			tempVector1.rotate_YZ(sunLight.YZ_angle);
			tempVector1.updateLocationOrthognal();
			tempVector1.z_lightspace = tempVector1.z;
			
			tempVector2.set(poly.rightEnd);
			tempVector2.subtract(sunLight.position);
			tempVector2.rotate_XZ(sunLight.XZ_angle);
			tempVector2.rotate_YZ(sunLight.YZ_angle);
			tempVector2.updateLocationOrthognal();
			tempVector2.z_lightspace = tempVector2.z;
			
			tempVector3.set(poly.bottomEnd);
			tempVector3.subtract(sunLight.position);
			tempVector3.rotate_XZ(sunLight.XZ_angle);
			tempVector3.rotate_YZ(sunLight.YZ_angle);
			tempVector3.updateLocationOrthognal();
			tempVector3.z_lightspace = tempVector3.z;
			
			
		}else{
			tempVector1.z_lightspace = poly.origin.z_lightspace;
			tempVector2.z_lightspace = poly.rightEnd.z_lightspace;
			tempVector3.z_lightspace = poly.bottomEnd.z_lightspace;
			tempVector1.screenX_lightspace = poly.origin.screenX_lightspace;
			tempVector2.screenX_lightspace =  poly.rightEnd.screenX_lightspace;
			tempVector3.screenX_lightspace = poly.bottomEnd.screenX_lightspace;
			tempVector1.screenY_lightspace = poly.origin.screenY_lightspace;
			tempVector2.screenY_lightspace =  poly.rightEnd.screenY_lightspace;
			tempVector3.screenY_lightspace = poly.bottomEnd.screenY_lightspace;
		}
		
	
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
		
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 16, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
						
						
						if(zBuffer[index] < z_left){
							zBuffer[index] = z_left;
							xPos = (d_x>>4) + X;
							yPos = (d_y>>4) + Y;
							textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
							z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
							screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
							screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
							
						
							
							int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
							
						
							if(z_lightspace - shadowBuffer[size] < shadowBias){
								shadowBitmap[index]  = 32;
								int lit = (int)(I_left + I_difference * (xPos%textureScaledWidth));
								if(lit < 0)
									lit = 0;
								screen[index] = gameData.colorTable[lit][texture[textureIndex]];
							}else{
								shadowBitmap[index]  = shadowLevel;
								
								screen[index] = colorTable[texture[textureIndex]];
							}
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++, z_left+=dz){
					
					if(zBuffer[index] < z_left){
						zBuffer[index] = z_left;
						xPos = (d_x/offset) + X;
						yPos = (d_y/offset) + Y;
						textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < shadowBias){
							shadowBitmap[index]  = 32;
							
							int lit = (int)(I_left + I_difference * (xPos%textureScaledWidth));
							if(lit < 0)
								lit = 0;
							screen[index] = gameData.colorTable[lit][texture[textureIndex]];
						}else{
							shadowBitmap[index]  = shadowLevel;
							screen[index] = colorTable[texture[textureIndex]];
						}
					}
				}
				
				break;
			}
		}
		
	}
	
	//redner terrain polygon which can be shadowed but can not cast shadow
	public static void renderTerrainPolygon(){	
		if(zTop ==0) 
			calculateDepthRangeAtGround();
		
		int depth;
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		
		
		double Aoffset,Boffset,Coffset;
		
		tempVector1.set(poly.origin);
		tempVector1.subtract(sunLight.position);
		tempVector1.rotate_XZ(sunLight.XZ_angle);
		tempVector1.rotate_YZ(sunLight.YZ_angle);
		tempVector1.updateLocationOrthognal();
		tempVector1.z_lightspace = tempVector1.z;
		
		tempVector2.set(poly.rightEnd);
		tempVector2.subtract(sunLight.position);
		tempVector2.rotate_XZ(sunLight.XZ_angle);
		tempVector2.rotate_YZ(sunLight.YZ_angle);
		tempVector2.updateLocationOrthognal();
		tempVector2.z_lightspace = tempVector2.z;
		
		tempVector3.set(poly.bottomEnd);
		tempVector3.subtract(sunLight.position);
		tempVector3.rotate_XZ(sunLight.XZ_angle);
		tempVector3.rotate_YZ(sunLight.YZ_angle);
		tempVector3.updateLocationOrthognal();
		tempVector3.z_lightspace = tempVector3.z;
			
			
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			depth = zTop + i*zDelta;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			
			
			index = x_left + screen_width*i;
			int offset = x_right - x_left;
			Aoffset = A.x*offset;
			Boffset = B.x*offset;
			Coffset = C.x*offset;

			aDotW+=Aoffset;
			bDotW+=Boffset;
			cDotW+=Coffset;
			cDotWInverse = 1/cDotW;
			X1 = (int)(aDotW*cDotWInverse);
			Y1 = (int)(bDotW*cDotWInverse);
			dx = ((X1 - X) <<8 )/offset;
			dy = ((Y1 - Y) << 8)/offset;
			
		
			for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++){
				if(zBuffer[index] < depth && zBuffer[index] != 1){
					zBuffer[index] = depth;
					xPos = (d_x>>8) + X;
					yPos = (d_y>>8) + Y;
					textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
				
					z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
					screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
					screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
					
					
					if(z_lightspace - shadowBuffer[screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)] < 1){
						shadowBitmap[index]  = 32;
					}else{
						shadowBitmap[index]  = 15;
					}
					screen[index] = colorTable[texture[textureIndex]];
					
				}
			
			}
			
			
		}
	}
	
	//redner road pologons which is a special case for terrain polyongs
	public static void renderRoadSidePolygon(){
		if(zTop ==0)
			calculateDepthRangeAtGround();
		
		int depth;
		int color;
		
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		
		
		double Aoffset,Boffset,Coffset;
		
		tempVector1.set(poly.origin);
		tempVector1.subtract(sunLight.position);
		tempVector1.rotate_XZ(sunLight.XZ_angle);
		tempVector1.rotate_YZ(sunLight.YZ_angle);
		tempVector1.updateLocationOrthognal();
		tempVector1.z_lightspace = tempVector1.z;
		
		tempVector2.set(poly.rightEnd);
		tempVector2.subtract(sunLight.position);
		tempVector2.rotate_XZ(sunLight.XZ_angle);
		tempVector2.rotate_YZ(sunLight.YZ_angle);
		tempVector2.updateLocationOrthognal();
		tempVector2.z_lightspace = tempVector2.z;
		
		tempVector3.set(poly.bottomEnd);
		tempVector3.subtract(sunLight.position);
		tempVector3.rotate_XZ(sunLight.XZ_angle);
		tempVector3.rotate_YZ(sunLight.YZ_angle);
		tempVector3.updateLocationOrthognal();
		tempVector3.z_lightspace = tempVector3.z;
			
			
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			depth = zTop + i*zDelta;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			
			
			index = x_left + screen_width*i;
			int offset = x_right - x_left;
			Aoffset = A.x*offset;
			Boffset = B.x*offset;
			Coffset = C.x*offset;

			aDotW+=Aoffset;
			bDotW+=Boffset;
			cDotW+=Coffset;
			cDotWInverse = 1/cDotW;
			X1 = (int)(aDotW*cDotWInverse);
			Y1 = (int)(bDotW*cDotWInverse);
			dx = ((X1 - X) <<8 )/offset;
			dy = ((Y1 - Y) << 8)/offset;
			
			
			for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++){
				if(zBuffer[index] < depth && zBuffer[index] != 1){
					
					xPos = (d_x>>8) + X;
					yPos = (d_y>>8) + Y;
					textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
				
					
					color = colorTable[texture[textureIndex]];
					if((color&255) < 150){
						zBuffer[index] = depth;
						screen[index] = color;
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < 1){
							shadowBitmap[index]  = 32;
						}else{
							shadowBitmap[index]  = 15;
						}
					}
					
				}
			
			}
		}
	}
	
	//redner basic texture mapped polygon
	public static void renderLakeBottomPolygon(){
		
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
	
		double Aoffset,Boffset,Coffset;
		
		tempVector1.set(poly.origin);
		tempVector1.subtract(sunLight.position);
		tempVector1.rotate_XZ(sunLight.XZ_angle);
		tempVector1.rotate_YZ(sunLight.YZ_angle);
		tempVector1.updateLocationOrthognal();
		tempVector1.z_lightspace = tempVector1.z;
		
		tempVector2.set(poly.rightEnd);
		tempVector2.subtract(sunLight.position);
		tempVector2.rotate_XZ(sunLight.XZ_angle);
		tempVector2.rotate_YZ(sunLight.YZ_angle);
		tempVector2.updateLocationOrthognal();
		tempVector2.z_lightspace = tempVector2.z;
		
		tempVector3.set(poly.bottomEnd);
		tempVector3.subtract(sunLight.position);
		tempVector3.rotate_XZ(sunLight.XZ_angle);
		tempVector3.rotate_YZ(sunLight.YZ_angle);
		tempVector3.updateLocationOrthognal();
		tempVector3.z_lightspace = tempVector3.z;
			
			
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
		
	
			index = x_left + screen_width*i;
			int offset = x_right - x_left;
			Aoffset = A.x*offset;
			Boffset = B.x*offset;
			Coffset = C.x*offset;

			aDotW+=Aoffset;
			bDotW+=Boffset;
			cDotW+=Coffset;
			cDotWInverse = 1/cDotW;
			X1 = (int)(aDotW*cDotWInverse);
			Y1 = (int)(bDotW*cDotWInverse);
			dx = ((X1 - X) <<8 )/offset;
			dy = ((Y1 - Y) << 8)/offset;
			
		
			for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++){
				if(zBuffer[index]  <=1){
					zBuffer[index] = 2;
					xPos = (d_x>>8) + X;
					yPos = (d_y>>8) + Y;
					textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
				
					z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
					screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
					screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
					
					int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
					
					if(z_lightspace - shadowBuffer[size] < 1){
						shadowBitmap[index]  = 32;
					}else{
						shadowBitmap[index]  = 15;
					}
					screen[index] = colorTable[texture[textureIndex]];
					
				}
			
			}
			
			
		}
	}
	

	//render water polygon
	public static void renderWaterPolygon(){
		short[] displacementMap =  poly.myTexture.displacementMap;
		byte[] waterHeightMap = poly.myTexture.waterHeightMap;
		int index;
		double Aoffset,Boffset,Coffset;
		
		
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
		
			index = x_left + screen_width*i;
			int offset = x_right - x_left;
			Aoffset = A.x*offset;
			Boffset = B.x*offset;
			Coffset = C.x*offset;

			aDotW+=Aoffset;
			bDotW+=Boffset;
			cDotW+=Coffset;
			cDotWInverse = 1/cDotW;
			X1 = (int)(aDotW*cDotWInverse);
			Y1 = (int)(bDotW*cDotWInverse);
			dx = ((X1 - X) <<8 )/offset;
			dy = ((Y1 - Y) << 8)/offset;
			
			for( k = offset, d_x = 0, d_y = 0; k >0; k--, d_x+=dx, d_y+=dy, index++){
				if(zBuffer[index] < z_left){
					textureIndex = (((d_x>>8) + X)&widthMask) + ((((d_y>>8) + Y)&heightMask)<<widthBits);
					displacementBuffer[index] = (short)((waterHeightMap[textureIndex] <<10) | displacementMap[textureIndex]);
				}
			}
		}
	}
		
	
	
	//rendering a polygon that has a soild color, can't be shadowed
	public static void renderSoildPolygon(){
	
		int soildColor = gameData.colorTable[poly.diffuse_I][poly.color];

		for(int i = start; i <= end; i++){
			x_left = xLeft[i] ;
			x_right = xRight[i];
			dx = x_right - x_left;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			
			
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			

			if(dx == 0)
				continue;
			
		
			int temp = i * screen_width;
			x_left+=temp;
			x_right+=temp;
		
			for(int j = x_left; j < x_right; j++, z_left+=dz){
				
				
				
				if(zBuffer[j] < z_left){
					screen[j] = soildColor;
					shadowBitmap[j]  = 32;  
					zBuffer[j] = z_left;
				}
				
				
			}
		}
		
	}
	
	//rendering a polygon that has a translucent color, can't be shadowed
	public static void renderDeployGridPolygon(){
		
		int soildColor = gameData.colorTable[poly.diffuse_I][poly.color];
		
		int soildColor2 = (soildColor&0xFEFEFE)>>1;
		
		for(int i = start; i <= end; i++){
			x_left = xLeft[i] ;
			x_right = xRight[i];
			dx = x_right - x_left;
			
			if(dx == 0)
				continue;
			
			int temp = i * screen_width;
			x_left+=temp;
			x_right+=temp;
		
			for(int j = x_left; j < x_right; j++){
				screen[j] = ((screen[j]&0xFEFEFE)>>1) + soildColor2;
			}
		}
		
	}
		
	

	//set the zbuffer value within the silhouette of the polygon to zero
	public static void renderZbufferRemoverPolygon(){
		for(int i = start; i <= end; i++){
			x_left = xLeft[i] ;
			x_right = xRight[i];
			dx = x_right - x_left;

			W.set(x_left-half_width_, -i + half_height, Z_length);
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			if(dx == 0)
				continue;
			
			int temp = i * screen_width;
			x_left+=temp;
			x_right+=temp;
		
			for(int j = x_left; j < x_right; j++, z_left+=dz){
				if(zBuffer[j] < z_left){
					zBuffer[j] = 1;  //set the distance of the pixel in camera space to infinite away
				}
			}
		}
		
	}
	
	//redner basic texture mapped polygon
	public static void renderCloakedPolygon(){
		
		short[] texture = poly.myTexture.pixelData; 
		diffuse_I = poly.diffuse_I&127;
		int[] colorTable = gameData.colorTable[diffuse_I];
		
		int index, z_lightspace, screenX_lightspace, screenY_lightspace, xPos,  yPos;
		byte shadowLevel = 13;
		float diffuse_intensity = gameData.intensityTable[diffuse_I];
		float ambient_intensity = gameData.intensityTable[poly.Ambient_I];
		float shadow_intensity = diffuse_intensity * 13f/32f;
		
		float difference = shadow_intensity - ambient_intensity;
		if(difference < 0){
			if(difference < -0.2)
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity) - 127);
			else
				shadowLevel = (byte)((ambient_intensity * 32f / diffuse_intensity));
		}
		int shadowBias = poly.shadowBias;
		
		

		A_offset = A.x*16;
		B_offset = B.x*16;
		C_offset = C.x*16;
		
		float Aoffset,Boffset,Coffset;
	
		
		//recalculate the screen position of orgin, rightEnd,  bottomEnd in light space
		//if they dont fit the corners of the polygon
		if(!poly.textureFitPolygon){
			tempVector1.set(poly.origin);
			tempVector1.subtract(sunLight.position);
			tempVector1.rotate_XZ(sunLight.XZ_angle);
			tempVector1.rotate_YZ(sunLight.YZ_angle);
			tempVector1.updateLocationOrthognal();
			tempVector1.z_lightspace = tempVector1.z;
			
			tempVector2.set(poly.rightEnd);
			tempVector2.subtract(sunLight.position);
			tempVector2.rotate_XZ(sunLight.XZ_angle);
			tempVector2.rotate_YZ(sunLight.YZ_angle);
			tempVector2.updateLocationOrthognal();
			tempVector2.z_lightspace = tempVector2.z;
			
			tempVector3.set(poly.bottomEnd);
			tempVector3.subtract(sunLight.position);
			tempVector3.rotate_XZ(sunLight.XZ_angle);
			tempVector3.rotate_YZ(sunLight.YZ_angle);
			tempVector3.updateLocationOrthognal();
			tempVector3.z_lightspace = tempVector3.z;
			
			
		}else{
			tempVector1.z_lightspace = poly.origin.z_lightspace;
			tempVector2.z_lightspace = poly.rightEnd.z_lightspace;
			tempVector3.z_lightspace = poly.bottomEnd.z_lightspace;
			tempVector1.screenX_lightspace = poly.origin.screenX_lightspace;
			tempVector2.screenX_lightspace =  poly.rightEnd.screenX_lightspace;
			tempVector3.screenX_lightspace = poly.bottomEnd.screenX_lightspace;
			tempVector1.screenY_lightspace = poly.origin.screenY_lightspace;
			tempVector2.screenY_lightspace =  poly.rightEnd.screenY_lightspace;
			tempVector3.screenY_lightspace = poly.bottomEnd.screenY_lightspace;
		}
		
	
		z_origin = (int)(tempVector1.z_lightspace * 1048576);
		dz_xdirection = (int)((tempVector2.z_lightspace * 1048576 - z_origin)*poly.textureWidthInverse);
		dz_ydirection = (int)((tempVector3.z_lightspace * 1048576 - z_origin)*poly.textureHeightInverse);
		
		XY_origin_x = (int)(65536 * tempVector1.screenX_lightspace);
		XY_origin_y = (int)(65536* tempVector1.screenY_lightspace);
		dXY_xdirection_x = (int)((tempVector2.screenX_lightspace *  65536 - XY_origin_x)*poly.textureWidthInverse);
		dXY_xdirection_y = (int)((tempVector2.screenY_lightspace * 65536 - XY_origin_y)*poly.textureWidthInverse);
		dXY_ydirection_x = (int)((tempVector3.screenX_lightspace * 65536 - XY_origin_x)*poly.textureHeightInverse);
		dXY_ydirection_y = (int)((tempVector3.screenY_lightspace * 65536 - XY_origin_y)*poly.textureHeightInverse);
		
	
		
					
		for(int i = start; i <= end; i++){
			x_left=xLeft[i];
			x_right=xRight[i];
			dx = x_right - x_left;
			if(dx <= 0)
				continue;
			
			W.set(x_left-half_width_, -i + half_height, Z_length);
			aDotW = A.dot(W);
			bDotW = B.dot(W);
			cDotW = C.dot(W);
			
			//find the texture coordinate for the start pixel of the scanline
			cDotWInverse = 1/cDotW;
			X = (int)(aDotW*cDotWInverse);
			Y = (int)(bDotW*cDotWInverse);
			X1 = X;
			Y1 = Y;
			
			int temp = screen_width*i;
		
			
			z_left = (int)(C_unit.dot(W)*w);
			dz = (int)(C_unit.x*w);
			
			
			for(int j = x_left; j < x_right; j+=16){
				X = X1;
				Y = Y1;
				
				index = j + temp;
				if(x_right - j > 15){
					//find the correct texture coordinate every 16 pixels.
					//Use the interpolation values for the  pixels in between.
					aDotW+=A_offset;
					bDotW+=B_offset;
					cDotW+=C_offset;
					cDotWInverse = 1/cDotW;
					X1 = (int)(aDotW*cDotWInverse);
					Y1 = (int)(bDotW*cDotWInverse);
					dx = X1 - X;
					dy = Y1 - Y;
					
					for( k = 0, d_x = 0, d_y = 0; k <16; k++, d_x+=dx, d_y+=dy, index++, z_left+=dz){
						if(zBuffer[index] < z_left){
							
							cloaked_x = 32 + (j + k) - modelCenterX;
							cloaked_y = 32 + (i - modelCenterY);
							
							temp1 = cloaked_x + cloaked_y*64;
							if(temp1 < 0 || temp1 >=4096)
								temp1 = 0;
							
							if(cloakTexture[temp1] < cloakedThreshold){
								continue;
							}
							
							zBuffer[index] = z_left;
							xPos = (d_x>>4) + X;
							yPos = (d_y>>4) + Y;
							textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
							z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
							screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
							screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
							
						
							int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
							
						
							if(z_lightspace - shadowBuffer[size] < shadowBias){
								shadowBitmap[index]  = 32;
							}else{
								shadowBitmap[index]  = shadowLevel;
								
							}
							screen[index] = colorTable[texture[textureIndex]];
							
						}
					}
					continue;
				}
				
				int offset = x_right - j;
				Aoffset = A.x*offset;
				Boffset = B.x*offset;
				Coffset = C.x*offset;
	
				aDotW+=Aoffset;
				bDotW+=Boffset;
				cDotW+=Coffset;
				cDotWInverse = 1/cDotW;
				X1 = (int)(aDotW*cDotWInverse);
				Y1 = (int)(bDotW*cDotWInverse);
				dx = X1 - X;
				dy = Y1 - Y;
				
				for( k = 0, d_x = 0, d_y = 0; k < offset; k++, d_x+=dx, d_y+=dy, index++, z_left+=dz){
					
					if(zBuffer[index] < z_left){
						
						cloaked_x = 32 + (j + k) - modelCenterX;
						cloaked_y = 32 + (i - modelCenterY);
						
						temp1 = cloaked_x + cloaked_y*64;
						if(temp1 < 0 || temp1 >=4096)
							temp1 = 0;
						
						if(cloakTexture[temp1] < cloakedThreshold){
							continue;
						}
						
						zBuffer[index] = z_left;
						xPos = (d_x/offset) + X;
						yPos = (d_y/offset) + Y;
						textureIndex = (xPos&widthMask) + ((yPos&heightMask)<<widthBits);
						z_lightspace = z_origin + xPos * dz_xdirection + yPos * dz_ydirection;
						screenX_lightspace = (XY_origin_x + dXY_xdirection_x * xPos + dXY_ydirection_x* yPos) >> 16;
						screenY_lightspace = (XY_origin_y + dXY_xdirection_y * xPos + dXY_ydirection_y * yPos) >> 16;
						
						int size = (screenX_lightspace + (screenY_lightspace << shadowmap_width_bit)) & shadowmap_size_;
						
						if(z_lightspace - shadowBuffer[size] < shadowBias){
							shadowBitmap[index]  = 32;
						}else{
							shadowBitmap[index]  = shadowLevel;
						}
						screen[index] = colorTable[texture[textureIndex]];
						
					}
				}
				
				break;
			}
		}
	}
	
	public static void calculateDepthRangeAtGround() {
		vector v = mainThread.my2Dto3DFactory.get3DLocation(poly, screen_width/2, 0);
		v.subtract(camera.position);
		v.rotate_YZ(camera.YZ_angle);
		zTop = (int)(0x1000000/v.z);
		
		
		v = mainThread.my2Dto3DFactory.get3DLocation(poly, screen_width/2, screen_height-1);
		v.subtract(camera.position);
		v.rotate_YZ(camera.YZ_angle);
		zBot = (int)(0x1000000/v.z);
		
		zDelta = (zBot - zTop)/screen_height;
		
		explosion.zTop = zTop;
		explosion.zBot = zBot;
		explosion.zDelta = zDelta;
		
	}

}