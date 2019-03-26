package core;

import entity.*;

// this class store the geometry for terrain objects
public class terrain {
	
	public polygon3D[] ground; 
	
	public int[] lakeObstacleIndex;
	public int lakeObstacleCount;
	
	public static int index;
	
	public int Ambient_I = 25;
	public int reflectance = 70;
	
	//lake1
	public polygon3D water1;
	public polygon3D groundRemover1;
	public polygon3D[] lake1;
	public vector lakeCenter1;
	public vector lakeCenterTemp1;
	public boolean lake1Visible;
	public int lake1PolyCount;
	public palmTree lake1Tree, lake1Tree2;

	//lake2
	public polygon3D water2;
	public polygon3D groundRemover2;
	public polygon3D[] lake2;
	public vector lakeCenter2;
	public vector lakeCenterTemp2;
	public boolean lake2Visible;
	public int lake2PolyCount;
	public goldMine goldMine2;
	
	//lake3
	public polygon3D water3;
	public polygon3D groundRemover3;
	public polygon3D[] lake3;
	public vector lakeCenter3;
	public vector lakeCenterTemp3;
	public boolean lake3Visible;
	public int lake3PolyCount;
	
	//lake 4
	public polygon3D water4;
	public polygon3D groundRemover4;
	public polygon3D[] lake4;
	public vector lakeCenter4;
	public vector lakeCenterTemp4;
	public boolean lake4Visible;
	public int lake4PolyCount;
	
	public tokenObject theToken;
	
	//road
	public polygon3D[] road;
	public vector roadDirection;
	public vector roadNormal;
	public vector roadSideDirection;
	public vector roadCorner1, roadCorner2, roadCorner3, roadCorner4;
	public vector roadCentre;
	public int roadPolygonIndex;
	public vector roadMarkCorner1, roadMarkCorner2, roadMarkCorner3, roadMarkCorner4;
	public vector roadSideCorner1, roadSideCorner2, roadSideCorner3, roadSideCorner4;
	
	//light poles
	public lightPole[] lightPoles;
	public int numOfLightPoles;
	
	public int curveAngle;

	
	public terrain(){
		ground = new polygon3D[1];
		vector[] v = new vector[]{new vector(-3f,-0.5001f,35f), new vector(35f,-0.5001f,35f), new vector(35f,-0.5001f,-3f), new vector(-3f,-0.5001f, -3f)};
		ground[0] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[0], 39f,38.15f, 2); 
		ground[0].Ambient_I = Ambient_I;
		ground[0].reflectance = reflectance;
		ground[0].findDiffuse();
		ground[0].textureFitPolygon = true; 
		lakeObstacleIndex = new int[1000];
		
		
		float x_start = 3.5f;
		float z_start = 8;
		float l = 3;
		
		float h = 3f/128*(128 - 17 - 12); 
		float w = 3f/128*(128 - 25 - 20);
		float dx = 3f/128*25;
		float dz = -3f/128*17;
		
		theToken = new tokenObject(-1, -1, -1, 0x00ffff);
		theToken.withinViewScreen = true;
		
		int waveAngle = 320;
		
		//create lake1
		v = new vector[]{new vector(x_start+dx,-0.54f,z_start + dz), new vector(x_start + dx + w,-0.54f,z_start + dz), new vector(x_start + dx + w,-0.54f,z_start + dz-h), new vector(x_start+dx,-0.54f, z_start + dz-h)};
		
		vector a1 = v[0].myClone();
		vector b1 = v[1].myClone();
		vector c1 = v[3].myClone();
		
		a1.rotate_XZ(waveAngle);
		b1.rotate_XZ(waveAngle);
		c1.rotate_XZ(waveAngle);
		
		
		water1 = new polygon3D(v, a1, b1, c1, mainThread.textures[54],w*1.2f,h*1.1f, 6); 
		
		v = new vector[]{new vector(x_start+dx,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz-h), new vector(x_start+dx,-0.5001f, z_start + dz-h)};
		groundRemover1 = new polygon3D(v, v[0], v[1], v[3], null,l,l, 4); 
		
		lake1 = createLake(mainThread.textures[55].heightmap, x_start, z_start, l , 128, 17, 12, 25, 20);
		lake1PolyCount = index + 1;
		lakeCenter1 = new vector(5f, -0.5001f, 6.5f);
		lakeCenterTemp1 = new vector(0,0,0);
		lake1Tree = new palmTree(4.983713f,-0.3028361f,6.419566f,-0.03152565f,0.03608194f,-0.030372922f,0.19448919f,-0.11764373f,187,64,148,205,281,352);
		lake1Tree2 =  new palmTree(4.983713f,-0.3028361f,6.389566f,-0.03152565f,0.11608194f,-0.010372922f,-0.29448919f,-0.11764373f,187,64,148,205,281,352);
		
		
		//create lake2
		x_start = 26;
		z_start = 25.25f;
		l = 3;
		
		h = 3f/128*(128 - 2 - 4); 
		w = 3f/128*(128 - 35 - 43);
		dx = 3f/128*35;
		dz = -3f/128*2;
		
		v = new vector[]{new vector(x_start+dx,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz-h), new vector(x_start+dx,-0.55f, z_start + dz-h)};
		
		vector a2 = v[0].myClone();
		vector b2 = v[1].myClone();
		vector c2 = v[3].myClone();
		
		a2.rotate_XZ(waveAngle);
		b2.rotate_XZ(waveAngle);
		c2.rotate_XZ(waveAngle);
		
		water2 = new polygon3D(v,a2, b2, c2, mainThread.textures[54],w*1.2f,h*1.1f, 6); 
		
		v = new vector[]{new vector(x_start+dx,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz-h), new vector(x_start+dx,-0.5001f, z_start + dz-h)};
		groundRemover2 = new polygon3D(v, v[0], v[1], v[3], null,l,l, 4); 
		
		lake2 = createLake(mainThread.textures[57].heightmap, x_start, z_start, l , 128, 2, 4, 35, 43);
		lake2PolyCount = index + 1;
		lakeCenter2 = new vector(x_start+1.5f, -0.5001f, z_start-1.5f);
		lakeCenterTemp2 = new vector(0,0,0);
		goldMine2 = new goldMine(27.5f,-0.80f, 23.75f, 30000);
		
		
		//create lake3
		x_start = 9;
		z_start = 27f;
		l = 3.5f;
		
		w = l/128*(128 - 18 - 20);
		h = l/128*(128 - 17 - 27); 
		dx = l/128*18;
		dz = -l/128*17;
		
		v = new vector[]{new vector(x_start+dx,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz-h), new vector(x_start+dx,-0.55f, z_start + dz-h)};
		vector a3 = v[0].myClone();
		vector b3 = v[1].myClone();
		vector c3 = v[3].myClone();
		
		a3.rotate_XZ(waveAngle);
		b3.rotate_XZ(waveAngle);
		c3.rotate_XZ(waveAngle);
		
		water3 = new polygon3D(v, a3, b3, c3, mainThread.textures[54],w*1.2f,h*1.1f, 6); 
		
		v = new vector[]{new vector(x_start+dx,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz-h), new vector(x_start+dx,-0.5001f, z_start + dz-h)};
		groundRemover3 = new polygon3D(v, v[0], v[1], v[3], null,l,l, 4); 
		
		lake3 = createLake(mainThread.textures[58].heightmap, x_start, z_start, l , 128, 17, 27, 18, 20);
		lake3PolyCount = index + 1;
		lakeCenter3 = new vector(x_start+1.5f, -0.5001f, z_start-1.5f);
		lakeCenterTemp3 = new vector(0,0,0);
		
		
		//create lake 4
		x_start = 25;
		z_start = 13f;
		l = 3f;
		
		w = l/128*(128 - 1 - 2);
		h = l/128*(128 - 4 - 0); 
		dx = l/128*1;
		dz = -l/128*4;
		
		v = new vector[]{new vector(x_start+dx,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz), new vector(x_start + dx + w,-0.55f,z_start + dz-h), new vector(x_start+dx,-0.55f, z_start + dz-h)};
		vector a4 = v[0].myClone();
		vector b4 = v[1].myClone();
		vector c4 = v[3].myClone();
		
		a4.rotate_XZ(waveAngle);
		b4.rotate_XZ(waveAngle);
		c4.rotate_XZ(waveAngle);
		
		water4 = new polygon3D(v, a4, b4, c4, mainThread.textures[54],w*1.2f,h*1.1f, 6); 
		
		v = new vector[]{new vector(x_start+dx,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz), new vector(x_start + dx + w,-0.5001f,z_start + dz-h), new vector(x_start+dx,-0.5001f, z_start + dz-h)};
		groundRemover4 = new polygon3D(v, v[0], v[1], v[3], null,l,l, 4); 
		
		lake4 = createLake(mainThread.textures[59].heightmap, x_start, z_start, l , 128, 4, 0, 1, 2);
		lake4PolyCount = index + 1;
		lakeCenter4 = new vector(x_start+1.5f, -0.5001f, z_start-1.5f);
		lakeCenterTemp4 = new vector(0,0,0);
		
		
		//create road
		road = new polygon3D[600];
		roadCorner1 = new vector(4f,-0.500f, -3f);
		roadCorner2 = new vector(4.36f,-0.500f,-3f);
		roadCorner3 = new vector(4f,-0.500f, -3f);
		roadCorner4 = new vector(4.36f,-0.500f,-3f);
		roadDirection = new vector(0, 0, 1);
		roadSideDirection = new vector(-1,0,0);
		roadNormal = new vector(0,1,0);
		roadCentre = new vector(0,0,0);
		roadMarkCorner1 = new vector(0,0,0);
		roadMarkCorner2 = new vector(0,0,0);
		roadMarkCorner3 = new vector(0,0,0);
		roadMarkCorner4 = new vector(0,0,0);
		
		roadSideCorner1 = new vector(0,0,0);
		roadSideCorner2 = new vector(0,0,0);
		roadSideCorner3 = new vector(0,0,0);
		roadSideCorner4 = new vector(0,0,0);
		
		lightPoles = new lightPole[100];
		
		
		createStrightRoadSection(4.5f);
		createCurvedRoadSection(0.25f,80,-4);
		createCurvedRoadSection(0.25f,40,4);
		createStrightRoadSection(5);
		createCurvedRoadSection(0.25f,40,4);
		createStrightRoadSection(0.5f);
		createCurvedRoadSection(0.25f,88,-4);
		createStrightRoadSection(1.5f);
		createCurvedRoadSection(0.25f,72,4);
		createStrightRoadSection(5f);
		createCurvedRoadSection(0.25f,64,-4);
		createCurvedRoadSection(0.25f,80,5);
		createStrightRoadSection(5f);
		
		lightPoles[5].vanish();
		lightPoles[18].vanish();
		lightPoles[21].vanish();
		lightPoles[28].vanish();
		lightPoles[40].vanish();	
	}
	
	
	public void createStrightRoadSection(float l){
		roadCorner1.set(roadCorner3);
		roadCorner2.set(roadCorner4);
		roadCentre.set(roadCorner1);
		roadCentre.add(roadCorner2);
		roadCentre.scale(0.5f);
		roadCentre.add(roadDirection, l);
		roadCorner3.set(roadCentre);
		roadCorner3.add(roadSideDirection, 0.18f);
		roadCorner4.set(roadCentre);
		roadCorner4.add(roadSideDirection, -0.18f);
		vector[] v = new vector[]{roadCorner3.myClone(), roadCorner4.myClone(), roadCorner2.myClone(), roadCorner1.myClone()};
		road[roadPolygonIndex] = new polygon3D(v, new vector(4f,-0.500f,35f), new vector(4.5f,-0.500f,35f), new vector(4f,-0.500f, -3f), mainThread.textures[61], 1f,80f, 2); 
		roadPolygonIndex++;
		
		roadSideCorner1.set(roadCorner3);
		roadSideCorner1.add(roadSideDirection, 0.03f);
		roadSideCorner4.set(roadCorner1);
		roadSideCorner4.add(roadSideDirection, 0.03f);
		v = new vector[]{roadSideCorner1.myClone(), roadCorner3.myClone(), roadCorner1.myClone(), roadSideCorner4.myClone()};
		road[roadPolygonIndex] = new polygon3D(v,v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[62], 0.2f, l, 8); 
		roadPolygonIndex++;
		
		roadSideCorner2.set(roadCorner4);
		roadSideCorner2.add(roadSideDirection, -0.03f);
		roadSideCorner3.set(roadCorner2);
		roadSideCorner3.add(roadSideDirection, -0.03f);
		v = new vector[]{roadCorner4.myClone(), roadSideCorner2.myClone(), roadSideCorner3.myClone(), roadCorner2.myClone()};
		road[roadPolygonIndex] = new polygon3D(v,v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[63], 1f, l, 8); 
		roadPolygonIndex++;
		
		
		int numberOfSegments = (int)(l/0.25f);
		roadCentre.y+=0.001f;
		for(int i = 0; i < numberOfSegments; i++){
			
			roadMarkCorner1.set(roadCentre);
			roadMarkCorner1.add(roadSideDirection, 0.008f);
			roadMarkCorner2.set(roadMarkCorner1);
			roadMarkCorner2.add(roadSideDirection, -0.016f);
			roadMarkCorner3.set(roadMarkCorner2);
			roadMarkCorner3.add(roadDirection, -0.1f);
			roadMarkCorner4.set(roadMarkCorner1);
			roadMarkCorner4.add(roadDirection, -0.1f);
			roadCentre.add(roadDirection, -0.25f);
			
			v = new vector[]{roadMarkCorner1.myClone(), roadMarkCorner2.myClone(), roadMarkCorner3.myClone(), roadMarkCorner4.myClone()};
			road[roadPolygonIndex] = new polygon3D(v, v[0], v[1], v[3] , mainThread.textures[60], 1f,1f, 1); 
			roadPolygonIndex++;
			
			if(i%4 ==0){

				
				if(numOfLightPoles == 29)
					lightPoles[numOfLightPoles] = new lightPole(roadCentre.x-roadSideDirection.x*0.26f + 0.5f, roadCentre.y, roadCentre.z - roadSideDirection.z*0.26f,(curveAngle + 90)%360);
				else
					if(numOfLightPoles%2==0)
						lightPoles[numOfLightPoles] = new lightPole(roadCentre.x+roadSideDirection.x*0.26f, roadCentre.y, roadCentre.z + roadSideDirection.z*0.26f,(curveAngle + 270)%360);
					else
						lightPoles[numOfLightPoles] = new lightPole(roadCentre.x-roadSideDirection.x*0.26f, roadCentre.y, roadCentre.z - roadSideDirection.z*0.26f,(curveAngle + 90)%360);
				numOfLightPoles++;
			}
		}
	}
	
	
	public void createCurvedRoadSection(float l, int angle, int turnRate){
		for(int i = 0; i < angle; i+=Math.abs(turnRate)){
			roadCorner1.set(roadCorner3);
			roadCorner2.set(roadCorner4);
			roadCentre.set(roadCorner1);
			roadCentre.add(roadCorner2);
			roadCentre.scale(0.5f);
			int realTurnRate = turnRate;
			if(realTurnRate < 0)
				realTurnRate = (360 + realTurnRate)%360;
			roadDirection.rotate_XZ(realTurnRate);
			
			curveAngle = (curveAngle + turnRate + 360)%360;
			
			roadCentre.add(roadDirection, l);
			roadSideDirection.rotate_XZ(realTurnRate);
			roadCorner3.set(roadCentre);
			roadCorner3.add(roadSideDirection, 0.18f);
			roadCorner4.set(roadCentre);
			roadCorner4.add(roadSideDirection, -0.18f);
			vector[] v = new vector[]{roadCorner3.myClone(), roadCorner4.myClone(), roadCorner2.myClone(), roadCorner1.myClone()};
			road[roadPolygonIndex] = new polygon3D(v, new vector(4f,-0.5f,35f), new vector(4.5f,-0.5f,35f), new vector(4f,-0.5f, -3f), mainThread.textures[61], 1f,80f, 2); 
			roadPolygonIndex++;
			
			roadSideCorner1.set(roadCorner3);
			roadSideCorner1.add(roadSideDirection, 0.03f);
			roadSideCorner4.set(roadCorner1);
			roadSideCorner4.add(roadSideDirection, 0.03f);
			v = new vector[]{roadSideCorner1.myClone(), roadCorner3.myClone(), roadCorner1.myClone(), roadSideCorner4.myClone()};
			road[roadPolygonIndex] = new polygon3D(v,v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[62], 0.2f, l*1.5f, 8); 
			roadPolygonIndex++;
			
			
			roadSideCorner2.set(roadCorner4);
			roadSideCorner2.add(roadSideDirection, -0.03f);
			roadSideCorner3.set(roadCorner2);
			roadSideCorner3.add(roadSideDirection, -0.03f);
			v = new vector[]{roadCorner4.myClone(), roadSideCorner2.myClone(), roadSideCorner3.myClone(), roadCorner2.myClone()};
			road[roadPolygonIndex] = new polygon3D(v,v[0].myClone(), v[1].myClone(), v[3].myClone(), mainThread.textures[63], 1f, l*1.5f, 8); 
			roadPolygonIndex++;
			
			roadCentre.y+=0.0001f;
			roadMarkCorner1.set(roadCentre);
			roadMarkCorner1.add(roadSideDirection, 0.008f);
			roadMarkCorner2.set(roadMarkCorner1);
			roadMarkCorner2.add(roadSideDirection, -0.016f);
			roadMarkCorner3.set(roadMarkCorner2);
			roadMarkCorner3.add(roadDirection, -0.1f);
			roadMarkCorner4.set(roadMarkCorner1);
			roadMarkCorner4.add(roadDirection, -0.1f);
			
			v = new vector[]{roadMarkCorner1.myClone(), roadMarkCorner2.myClone(), roadMarkCorner3.myClone(), roadMarkCorner4.myClone()};
			road[roadPolygonIndex] = new polygon3D(v, v[0], v[1], v[3] , mainThread.textures[60], 1f,1f, 1); 
			roadPolygonIndex++;
			
			if((i/Math.abs(turnRate))%4 ==0){
				if(numOfLightPoles%2==0)
					lightPoles[numOfLightPoles] = new lightPole(roadCentre.x+roadSideDirection.x*0.26f, roadCentre.y, roadCentre.z + roadSideDirection.z*0.26f,(curveAngle + 270)%360);
				else
					lightPoles[numOfLightPoles] = new lightPole(roadCentre.x-roadSideDirection.x*0.26f, roadCentre.y, roadCentre.z - roadSideDirection.z*0.26f,(curveAngle + 90)%360);
				numOfLightPoles++;
			}
			
			
		}
	}
	
	public polygon3D[] createLake(int[] hm, float x_start, float z_start, float l, int blocks, int xBlockStart, int xBlockEnd, int yBlockStart, int yBlockEnd){
				
		polygon3D[] lake = new polygon3D[blocks*blocks*2];
		
		//load height map
		float[] heightmap = new float[(blocks+1)*(blocks+1)];
	
		int interval = (int)(Math.sqrt(hm.length) - 1)/blocks;
		
		float baseHeight = -0.5001f;
		float heightScale = -0.001f;
		int widthPluseOne = (int)(Math.sqrt(hm.length));
		for(int i = 0; i < (blocks+1); i++){
			for(int j =0; j< (blocks+1); j++){
				if(hm[j*interval + i*interval*widthPluseOne] < 2)
					hm[j*interval + i*interval*widthPluseOne] = 0;
				heightmap[j + i * (blocks+1)] = ((float)hm[j*interval + i*interval*widthPluseOne])*heightScale +baseHeight;

			}
		}
		
		float dx = l/ blocks;
		float dz = -l/ blocks;
	
		vector tempVector0 = new vector(0,0,0);
		vector tempVector1 = new vector(0,0,0);
		vector tempVector2 = new vector(0,0,0);
		vector tempVector3 = new vector(0,0,0);
		
		vector deltaX = new vector(0,0,0);
		vector deltaZ = new vector(0,0,0);
		vector origin = new vector(0,0,0);
		vector top = new vector(0,0,0);
		vector bot = new vector(0,0,0);
		vector[] v;
		
	
		
		
		index = 0;
		
		
		byte[] diffuses = new byte[blocks * blocks * 2];
		
		for(int i = 0; i < diffuses.length; i++)
			diffuses[i] = 73;
		
		for(int i = xBlockStart; i < blocks-xBlockEnd; i++){
			for(int j = yBlockStart; j < blocks - yBlockEnd; j++){
				int block1 = j + i*(blocks+1);
				int block2 = j + 1 + i*(blocks+1);
				int block3 = j + 1 + (i +1)*(blocks+1);
				int block4 = j + (i +1)*(blocks+1);
				
				tempVector0.set(x_start + dx*j, heightmap[block1], z_start + dz*i);
				tempVector1.set(x_start + dx*(j+1), heightmap[block2], z_start + dz*i);
				tempVector2.set(x_start + dx*(j+1), heightmap[block3], z_start + dz*(i+1));
				tempVector3.set(x_start + dx*j, heightmap[block4], z_start + dz*(i+1));
				
				
				boolean sameHeight = heightmap[block1] == heightmap[block2] &&
						              heightmap[block2] == heightmap[block3] &&
						              heightmap[block3] ==  heightmap[block4];
				
				boolean belowWaterLevel =  heightmap[block1] < -0.55f && heightmap[block2] < -0.55f && heightmap[block3] < -0.55f && heightmap[block4] < -0.55f;

				
				boolean belowGround = tempVector0.y < -0.5801f && tempVector1.y < -0.5801f && tempVector2.y < -0.5801f && tempVector3.y < -0.5801f;
				
				if(belowGround){
					
					tokenObject t = new tokenObject(((int)(tempVector0.x/0.25f)) * 0.25f + 0.125f,tempVector0.y, ((int)(tempVector0.z/0.25f)) * 0.25f + 0.125f, 64 << 16 | 64 << 8 | 255);
					if(!t.noNeedForThisToken){
						lakeObstacleIndex[lakeObstacleCount] = t.tileIndex;
						lakeObstacleCount++;
					}
					
				}
				
				
				if(sameHeight){
					if(heightmap[block1] == -0.7551f)
						continue;
					
					if(heightmap[block1] == -0.5001f){
						
						v = new vector[]{tempVector0.myClone(), tempVector1.myClone(), tempVector2.myClone(),  tempVector3.myClone()}; 
						deltaX.set(tempVector0);
						deltaX.subtract(tempVector1);
						
						deltaZ.set(tempVector0);
						deltaZ.subtract(tempVector3);
						
						origin.set(tempVector0);
						origin.add(deltaX, j);
						origin.add(deltaZ, i);
						
						top.set(origin);
						top.add(deltaX, -blocks);
						
						bot.set(origin);
						bot.add(deltaZ, -blocks);
						
						lake[index] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), mainThread.textures[0], l,l,1);
						lake[index].Ambient_I = Ambient_I;
						lake[index].reflectance = reflectance;
						lake[index].findDiffuse();
						
						diffuses[j*2 + i*(blocks)*2] = (byte)lake[index].diffuse_I;
						diffuses[j*2+1 + i*(blocks)*2] = (byte)lake[index].diffuse_I;
						
						index++;
						continue;
					}
				}
				
			
				if(belowWaterLevel){
					
					v = new vector[]{tempVector0.myClone(), tempVector1.myClone(), tempVector2.myClone(),  tempVector3.myClone()};
					deltaX.set(tempVector0);
					deltaX.subtract(tempVector1);
					
					deltaZ.set(tempVector0);
					deltaZ.subtract(tempVector3);
					
					origin.set(tempVector0);
					origin.add(deltaX, j);
					origin.add(deltaZ, i);
					
					top.set(origin);
					top.add(deltaX, -blocks);
					
					bot.set(origin);
					bot.add(deltaZ, -blocks);
					
					lake[index] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), mainThread.textures[0], l,l,1);
					lake[index].Ambient_I = Ambient_I;
					lake[index].reflectance = reflectance;
					lake[index].findDiffuse();
					
					diffuses[j*2 + i*(blocks)*2] = (byte)lake[index].diffuse_I;
					diffuses[j*2+1 + i*(blocks)*2] = (byte)lake[index].diffuse_I;
					
					index++;
					continue;
				}
				
				
				v = new vector[]{tempVector0.myClone(), tempVector1.myClone(), tempVector3.myClone()};
				deltaX.set(tempVector0);
				deltaX.subtract(tempVector1);
				
				deltaZ.set(tempVector0);
				deltaZ.subtract(tempVector3);
				
				origin.set(tempVector0);
				origin.add(deltaX, j);
				origin.add(deltaZ, i);
				
				top.set(origin);
				top.add(deltaX, -blocks);
				
				bot.set(origin);
				bot.add(deltaZ, -blocks);
				
				lake[index] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), mainThread.textures[0], l,l,5);
				lake[index].Ambient_I = Ambient_I;
				lake[index].reflectance = reflectance;
				lake[index].findDiffuse();
				
				diffuses[j*2 + i*(blocks)*2] = (byte)lake[index].diffuse_I;
				
				deltaX.set(tempVector3);
				deltaX.subtract(tempVector2);
				
				deltaZ.set(tempVector1);
				deltaZ.subtract(tempVector2);
				
				origin.set(tempVector2);
				origin.add(deltaX);
				origin.add(deltaZ);
				origin.add(deltaX, j);
				origin.add(deltaZ, i);
				
				top.set(origin);
				top.add(deltaX, -blocks);
				
				bot.set(origin);
				bot.add(deltaZ, -blocks);
				
				v = new vector[]{tempVector1.myClone(), tempVector2.myClone(), tempVector3.myClone()};
				lake[index+1] = new polygon3D(v, origin.myClone(), top.myClone(), bot.myClone(), mainThread.textures[0], l,l,5);
				lake[index+1].Ambient_I = Ambient_I;
				lake[index+1].reflectance = reflectance;
				lake[index+1].findDiffuse();
				diffuses[j*2 +1 + i*(blocks)*2] = (byte)lake[index+1].diffuse_I;
				
				index+=2;
			}
		}
		
		v = new vector[]{new vector(x_start,-0.7551f,z_start), new vector(x_start + l,-0.7551f,z_start), new vector(x_start + l,-0.7551f,z_start - l), new vector(x_start,-0.7551f, z_start - l)};
		lake[index] = new polygon3D(v, v[0], v[1], v[3], mainThread.textures[0],l,l, 7); 
		lake[index].Ambient_I = Ambient_I;
		lake[index].reflectance = reflectance;
		lake[index].findDiffuse();
		
		
		for(int i = 0; i < index; i++){
			lake[i].parentObject = theToken;
		}
		
		//create smooth lake ledges
		int polygonIndex = 0;
		for(int i = xBlockStart; i < blocks-xBlockEnd; i++){
			for(int j = yBlockStart; j < blocks - yBlockEnd; j++){
				int block1 = j + i*(blocks+1);
				int block2 = j + 1 + i*(blocks+1);
				int block3 = j + 1 + (i +1)*(blocks+1);
				int block4 = j + (i +1)*(blocks+1);
				
				tempVector0.set(x_start + dx*j, heightmap[block1], z_start + dz*i);
				tempVector1.set(x_start + dx*(j+1), heightmap[block2], z_start + dz*i);
				tempVector2.set(x_start + dx*(j+1), heightmap[block3], z_start + dz*(i+1));
				tempVector3.set(x_start + dx*j, heightmap[block4], z_start + dz*(i+1));
				
				
				boolean sameHeight = heightmap[block1] == heightmap[block2] &&
						              heightmap[block2] == heightmap[block3] &&
						              heightmap[block3] ==  heightmap[block4];
				
				boolean belowWaterLevel =  heightmap[block1] < -0.55f && heightmap[block2] < -0.55f && heightmap[block3] < -0.55f && heightmap[block4] < -0.55f;

				
				
				if(sameHeight){
					if(heightmap[block1] == -0.7551f)
						continue;
					
					if(heightmap[block1] == -0.5001f){
						polygonIndex++;
						continue;
					}
				}
				
				if(belowWaterLevel){
					polygonIndex++;
					continue;
				}
				
					int currentBlockIndex = j*2 + i*(blocks)*2;
					
					lake[polygonIndex].diffuse[0] = (byte)((diffuses[currentBlockIndex]+ 
												  diffuses[currentBlockIndex - 1]  + 
												  diffuses[currentBlockIndex - 2] + 
												  diffuses[currentBlockIndex - 1 - blocks*2] + 
												  diffuses[currentBlockIndex - blocks*2] + 
												  diffuses[currentBlockIndex - blocks*2 + 1])/6);
					
					lake[polygonIndex].diffuse[1] = (byte)((diffuses[currentBlockIndex] +
												  diffuses[currentBlockIndex + 1]+
												  diffuses[currentBlockIndex + 2]+
												  diffuses[currentBlockIndex + 3 - blocks*2]+
												  diffuses[currentBlockIndex + 2 - blocks*2]+
												  diffuses[currentBlockIndex + 1 - blocks*2])/6);
					
					
					try{
					lake[polygonIndex].diffuse[2] = (byte)((diffuses[currentBlockIndex]+
												  diffuses[currentBlockIndex+1]+
												  diffuses[currentBlockIndex+blocks*2]+
												  diffuses[currentBlockIndex+blocks*2 - 1]+
												  diffuses[currentBlockIndex+blocks*2 - 2]+
												  diffuses[currentBlockIndex-1])/6);
					}catch(Exception e){
						lake[polygonIndex].diffuse[2]  = 73;
					}
					
					
					lake[polygonIndex+1].diffuse[0] = lake[polygonIndex].diffuse[1];
					
					try{
					lake[polygonIndex+1].diffuse[1] = (byte)((diffuses[currentBlockIndex+1] + 
													diffuses[currentBlockIndex + 2] +
													diffuses[currentBlockIndex + 3] + 
													diffuses[currentBlockIndex + 2 + blocks*2]+
													diffuses[currentBlockIndex + 1 + blocks*2]+
													diffuses[currentBlockIndex + blocks*2])/6);
					}catch(Exception e){
						lake[polygonIndex+1].diffuse[1] = 73;
					}
					
					lake[polygonIndex + 1].diffuse[2] = lake[polygonIndex].diffuse[2];
					
			
					polygonIndex+=2;
				
			}
		}
		
		return lake;
		
	}
	
	public void update(){
		for(int i = 0; i < ground.length; i++){
			if(ground[i] != null){
				ground[i].update();
			}
		}
		
		for(int i = 0; i < lakeObstacleCount; i++){
			mainThread.gridMap.currentObstacleMap[lakeObstacleIndex[i]] = false;
		}
	
		
		//update lake1
		lake1Visible = true;
		lakeCenterTemp1.set(lakeCenter1);
		lakeCenterTemp1.subtract(camera.position);
		lakeCenterTemp1.rotate_XZ(camera.XZ_angle);
		lakeCenterTemp1.rotate_YZ(camera.YZ_angle); 
		lakeCenterTemp1.updateLocation();
		
		if(lakeCenterTemp1.screenX > 1118 || lakeCenterTemp1.screenX < - 350 || lakeCenterTemp1.screenY < - 140 || lakeCenterTemp1.screenY > 1062){
			lake1Visible = false;
		}
			
		if(lake1Visible){
			water1.origin.x-=0.0015f;
			water1.rightEnd.x-=0.0015f;
			water1.bottomEnd.x -= 0.0015f;
			water1.origin.z-=0.0015f;
			water1.rightEnd.z-=0.0015f;
			water1.bottomEnd.z -= 0.0015f;
			
			water1.update();
			groundRemover1.update();
			for(int i = 0; i < lake1PolyCount; i++){
				lake1[i].update();
				lake1[i].update_lightspace_withoutDrawing();
			}
			
			lake1Tree.update();
			lake1Tree2.update();
		}
		
		//update lake 2
		lake2Visible = true;
		lakeCenterTemp2.set(lakeCenter2);
		lakeCenterTemp2.subtract(camera.position);
		lakeCenterTemp2.rotate_XZ(camera.XZ_angle);
		lakeCenterTemp2.rotate_YZ(camera.YZ_angle); 
		lakeCenterTemp2.updateLocation();
		
		if(lakeCenterTemp2.screenX > 1118 || lakeCenterTemp2.screenX < - 350 || lakeCenterTemp2.screenY < - 160 || lakeCenterTemp2.screenY > 1062){
			lake2Visible = false;
		}
			
		if(lake2Visible){
			water2.origin.x-=0.0015f;
			water2.rightEnd.x-=0.0015f;
			water2.bottomEnd.x -= 0.0015f;
			water2.origin.z-=0.0015f;
			water2.rightEnd.z-=0.0015f;
			water2.bottomEnd.z -= 0.0015f;
			
			water2.update();
			groundRemover2.update();
			for(int i = 0; i < lake2PolyCount; i++){
				lake2[i].update();
				lake2[i].update_lightspace_withoutDrawing();
			}
			
			goldMine2.update();
		}
		
		//update lake3
		lake3Visible = true;
		lakeCenterTemp3.set(lakeCenter3);
		lakeCenterTemp3.subtract(camera.position);
		lakeCenterTemp3.rotate_XZ(camera.XZ_angle);
		lakeCenterTemp3.rotate_YZ(camera.YZ_angle); 
		lakeCenterTemp3.updateLocation();
		
		if(lakeCenterTemp3.screenX > 1118 || lakeCenterTemp3.screenX < - 350 || lakeCenterTemp3.screenY < - 150 || lakeCenterTemp3.screenY > 962){
			lake3Visible = false;
		}
			
		
		if(lake3Visible){
			water3.origin.x-=0.0015f;
			water3.rightEnd.x-=0.0015f;
			water3.bottomEnd.x -= 0.0015f;
			water3.origin.z-=0.0015f;
			water3.rightEnd.z-=0.0015f;
			water3.bottomEnd.z -= 0.0015f;
			
			water3.update();
			groundRemover3.update();
			for(int i = 0; i < lake3PolyCount; i++){
				lake3[i].update();
				lake3[i].update_lightspace_withoutDrawing();
			}
		}
		
		//update lake 4
		lake4Visible = true;
		lakeCenterTemp4.set(lakeCenter4);
		lakeCenterTemp4.subtract(camera.position);
		lakeCenterTemp4.rotate_XZ(camera.XZ_angle);
		lakeCenterTemp4.rotate_YZ(camera.YZ_angle); 
		lakeCenterTemp4.updateLocation();
		
		if(lakeCenterTemp4.screenX > 1418 || lakeCenterTemp4.screenX < - 400 || lakeCenterTemp4.screenY < - 150 || lakeCenterTemp4.screenY > 1102){
			lake4Visible = false;
		}
		
	
			
		if(lake4Visible){
			water4.origin.x-=0.0015f;
			water4.rightEnd.x-=0.0015f;
			water4.bottomEnd.x -= 0.0015f;
			water4.origin.z-=0.0015f;
			water4.rightEnd.z-=0.0015f;
			water4.bottomEnd.z -= 0.0015f;
			
			water4.update();
			groundRemover4.update();
			for(int i = 0; i < lake4PolyCount; i++){
				lake4[i].update();
				lake4[i].update_lightspace_withoutDrawing();
			}
		}
		
		//animate water surface
		mainThread.textures[54].waterHeightMap = mainThread.textures[54].waterHeightMaps[(mainThread.gameFrame)%48];
		
		for(int i = 0; i < roadPolygonIndex; i++){
			road[i].update();
			if(road[i].type == 1)
				road[i].update_lightspace_withoutDrawing();
		}
		
		for(int i = 0; i < numOfLightPoles; i++){
			lightPoles[i].update();
		}
	}
	
	public void draw(){
		if(lake1Visible)
			groundRemover1.draw();
		
		if(lake2Visible)
			groundRemover2.draw();
		
		if(lake3Visible)
			groundRemover3.draw();
		
		if(lake4Visible)
			groundRemover4.draw();
		
		
		for(int i = 0; i < numOfLightPoles; i++){
			lightPoles[i].draw();
		}
		
		for(int i = 0; i < roadPolygonIndex; i++)
			road[i].draw();
		
		for(int i = 0; i < ground.length; i++)
			ground[i].draw();
		
		
		
		if(lake1Visible){
			for(int i = 0; i < lake1PolyCount; i++)
				lake1[i].draw();
			
			lake1Tree.draw();
			lake1Tree2.draw();
			water1.draw();
		}
			
		if(lake2Visible){
			for(int i = 0; i < lake2PolyCount; i++)
				lake2[i].draw();
			
			goldMine2.draw();
			water2.draw();
			
		}
		
		if(lake3Visible){
			for(int i = 0; i < lake3PolyCount; i++)
				lake3[i].draw();
			
			water3.draw();
			
		}
		
		if(lake4Visible){
			for(int i = 0; i < lake4PolyCount; i++){
				lake4[i].draw();
			}
			
			water4.draw();
			
		}
		
	}
	
}
