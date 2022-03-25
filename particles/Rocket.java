package particles;

import core.camera;
import core.gameData;
import core.geometry;
import core.mainThread;
import core.polygon3D;
import core.vector;
import entity.solidObject;

public class rocket {
	public vector centre;
	
	public solidObject target;
	
	public int damage;
	
	public int angle;
	
	public boolean isInAction;
	
	public vector iDirection, jDirection, kDirection;
	
	public vector movement;
	
	public float distanceToTarget;
	
	public float height;
	
	public float speed;
	
	public float distanceTravelled;
	
	public polygon3D[] polygons;
	
	public static polygon3D[] polygonsClone;
	
	public solidObject attacker;
	
	public boolean visible;
	
	public static vector tempCentre;
	
	public static int[] tiles3x3 = new int[]{-129, -128, -127, -1, 0, 1, 127, 128, 129};
	
	public static int screen_width = mainThread.screen_width;
	public static int screen_height = mainThread.screen_height;
	
	public rocket(){
		centre = new vector(0,0,0);
		iDirection = new vector(1,0,0);
		jDirection = new vector(0,1,0);
		kDirection = new vector(0,0,1);
		makePolygons();
		movement = new vector(0,0,1);
		if(tempCentre == null)
			tempCentre = new vector(0,0,0);
	}
	
	public void  setActive(int angle, int damage, solidObject target, vector centre, solidObject attacker){
		isInAction = true;
		this.angle = 360 - angle;
		this.damage = damage; 
		this.target = target;
		this.centre.set(centre);
		this.attacker = attacker;
		distanceTravelled = 0;
		speed = 0.005f;
		reconstructPolygons();
		
	}
	
	public void reconstructPolygons(){
		for(int i = 0; i < polygons.length; i++){
			if(polygons[i].textureFitPolygon == false){
				
				polygons[i].origin.set(polygonsClone[i].origin);
				polygons[i].origin.rotate_XZ(this.angle);
				polygons[i].origin.add(centre);
				
				
				polygons[i].bottomEnd.set(polygonsClone[i].bottomEnd);
				polygons[i].bottomEnd.rotate_XZ(this.angle);
				polygons[i].bottomEnd.add(centre);
				
				
				polygons[i].rightEnd.set(polygonsClone[i].rightEnd);
				polygons[i].rightEnd.rotate_XZ(this.angle);
				polygons[i].rightEnd.add(centre);
				
			}
			
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].set(polygonsClone[i].vertex3D[j]);
				polygons[i].vertex3D[j].rotate_XZ(this.angle);
				polygons[i].vertex3D[j].add(centre);
			}
		}
	}
	
	public void update(){
		if(!isInAction)
			return;
		
		distanceToTarget = (float)Math.sqrt((target.centre.x - centre.x) * (target.centre.x - centre.x) + (target.centre.z - centre.z) * (target.centre.z - centre.z));
		if(distanceToTarget <= 0.065){
			
			//spawn an explosion at the end of the rocket life
			float[] tempFloat = mainThread.theAssetManager.explosionInfo[mainThread.theAssetManager.explosionCount];	
			tempFloat[0] = centre.x;
			if(target.type > 100 && target.type != 200){
				
					tempFloat[1] = centre.y + 0.1f;
				
			}else{
					
					tempFloat[1] = centre.y - 0.05f;
			}
			
			tempFloat[2] = centre.z;
			tempFloat[3] = 1.5f;
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = target.height;
			mainThread.theAssetManager.explosionCount++; 
			isInAction = false;
			
			
			target.currentHP -=damage;
			target.underAttackCountDown = 120;
			target.attacker = attacker;
			
			
			int xPos = (int)(target.centre.x*64);
			int yPos = (int)(target.centre.z*64);
			int start = xPos/16 + (127 - yPos/16)*128;
			int targetTeamNo = target.teamNo;
			solidObject[] tile;
			for(int i  = 0; i < 9; i++){
				int index = start + tiles3x3[i];
				if(index > 16383 || index < 0)
					continue;
				tile = mainThread.gridMap.tiles[index];
				for(int j = 0; j < 4; j++){
					if(tile[j] != null){						
						if(tile[j].teamNo == targetTeamNo && tile[j].teamNo != attacker.teamNo && tile[j].currentCommand != solidObject.move && tile[j].attackStatus != solidObject.isAttacking && tile[j].isCloaked == false
						   && 	tile[j].currentCommand != solidObject.attackCautiously && tile[j].currentCommand != solidObject.attackInNumbers){
							if(tile[j].type < 100){								
								tile[j].attack(attacker);
								tile[j].currentCommand = solidObject.attackInNumbers; 
							}
						}
						
					}
				}	
			}
			
			return;
		}
			
		if((target.type < 100 || target.type ==200) && attacker.type == 199){
			float h =  0.08f;
			if(target.type ==200)
				h = -0.12f;
			if(centre.y + h > target.centre.y ){
				centre.y -=0.014f;
			}
		}
			
		
		
		
		if(attacker.type == 199) {
			if(speed < 0.1)
				speed*=1.5f;
		}else {
			if(speed < 0.1)
				speed*=1.4f;
		}
		
	
		distanceTravelled+=speed;
		
		
		//spawn tail particle
		if(distanceTravelled > 0.08f){
			distanceTravelled = 0;
			if(mainThread.theAssetManager.smokeEmmiterCount < 100){
				float[] tempFloat = mainThread.theAssetManager.smokeEmmiterList[mainThread.theAssetManager.smokeEmmiterCount];
				
				
				
				tempFloat[0] = centre.x + (float)Math.random()*0.04f -0.02f;
				tempFloat[1] = centre.y;
				tempFloat[2] = centre.z + (float)Math.random()*0.04f - 0.02f;
				tempFloat[3] = 1f;
				tempFloat[4] = 2;
				tempFloat[5] = 11;
				tempFloat[6] = this.height;
				
				mainThread.theAssetManager.smokeEmmiterCount++;
			}
		}
		
		angle = 360 - geometry.findAngle(centre.x, centre.z, target.centre.x, target.centre.z);
		
		movement.set(0,0,1);
		movement.rotate_XZ(angle);
		centre.add(movement, speed);
		
		reconstructPolygons();
		
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		
		visible = true;
		if(tempCentre.screenX <  -100 || tempCentre.screenX > screen_width + 100 || tempCentre.screenY < -100 || tempCentre.screenY > screen_height + 100){
			visible = false;
		}
		
		if(visible)
			for(int i = 0; i < polygons.length; i++){
				polygons[i].update_lightspace();	
			}
		
		
		
	}
	
	public void draw(){
		if(!isInAction || !visible)
			return;
		
		for(int i = 0; i < polygons.length; i ++){
			polygons[i].update();
			polygons[i].findNormal();
			polygons[i].findDiffuse();
			polygons[i].draw();
		}
	}
	
	
	
	public void makePolygons(){
		int size = 12;
		float radius = 0.005f;
		float length = 0.03f;
		
		polygons = new polygon3D[size*2]; 
		
		double theta = Math.PI/(size/2);
		
		vector[] v1 = new vector[size];
		vector[] v2 = new vector[size];
		
		for(int i = 0; i < size; i++){
			v2[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), length);
		}
		
		for(int i = 0; i < size; i++){
			v1[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), -length);
		}
		
		
		for(int i = 0; i < size; i ++){
			polygons[i] = new polygon3D(new vector[]{v1[i],v1[(i+1)%size],v2[(i+1)%size],v2[i]}, v1[i],v1[(i+1)%size], v2[i], mainThread.textures[68], 1,1,1);
			polygons[i].color = 25 + (25 << 5) + (25 << 10);
		}
		
		
		
		
		for(int i = 0; i < size; i++){
			v2[i] = put(0*Math.cos(i*theta), 0*Math.sin(i*theta), 0.05);
		}
		
		for(int i = 0; i < size; i++){
			v1[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), 0.03);
		}
		
		
		for(int i = 0; i < size; i ++){
			polygons[i + size] = new polygon3D(new vector[]{v1[i],v1[(i+1)%size],v2[(i+1)%size],v2[i]}, v1[i],v1[(i+1)%size], v2[i],  mainThread.textures[69], 1,1,1);
			polygons[i + size].color = 0 + (0 << 5) + (25 << 10);
		}
		
	
		
		
		if(polygonsClone == null){
			polygonsClone =  new polygon3D[size * 2]; 
			
			for(int i = 0; i < size; i++){
				v2[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), length);
			}
			
			for(int i = 0; i < size; i++){
				v1[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), -length);
			}
			
			
			for(int i = 0; i < size; i ++){
				polygonsClone[i] = new polygon3D(new vector[]{v1[i],v1[(i+1)%size],v2[(i+1)%size],v2[i]}, v1[i],v1[(i+1)%size], v2[i], null, 1,1,0);
				polygonsClone[i].color = 16 + (16 << 5) + (16 << 10);
			}
			
			for(int i = 0; i < size; i++){
				v2[i] = put(0*Math.cos(i*theta), 0*Math.sin(i*theta), 0.05);
			}
			
			for(int i = 0; i < size; i++){
				v1[i] = put(radius*Math.cos(i*theta), radius*Math.sin(i*theta), 0.03);
			}
			
			
			for(int i = 0; i < size; i ++){
				polygonsClone[i + size] = new polygon3D(new vector[]{v1[i],v1[(i+1)%size],v2[(i+1)%size],v2[i]}, v1[i],v1[(i+1)%size], v2[i], null, 1,1,0);
				polygonsClone[i + size].color = 0 + (0 << 5) + (25 << 10);
			}
			
		}
	
	}
	
	//create a arbitrary vertex
	public  vector put(double i, double j, double k){
		vector temp = new vector(0,0,0);
		temp.add(iDirection, (float)i);
		temp.add(jDirection, (float)j);
		temp.add(kDirection, (float)k);
		return temp;
	}
		
	
	
}
