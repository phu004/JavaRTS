package particles;

import core.*;
import entity.solidObject;

public class bullet {

	public vector centre;
	
	public solidObject target;
	
	public int damage;
	
	public int angle;
	
	public boolean isInAction;
	
	public vector iDirection, jDirection, kDirection;
	
	public vector movement;
	
	public float distanceToTarget;
	
	public float speed;
	
	public polygon3D[] polygons;
	
	public static vector[][] baseGeometry;
	
	public solidObject attacker;
	
	public static int[] tiles3x3 = new int[]{-129, -128, -127, -1, 0, 1, 127, 128, 129};
	
	public bullet(){
		centre = new vector(0,0,0);
		iDirection = new vector(1,0,0);
		jDirection = new vector(0,1,0);
		kDirection = new vector(0,0,1);
		makePolygons();
		movement = new vector(1,0,0);
		speed = 0.2f;
	}
	
	public void  setActive(int angle, int damage, solidObject target, vector centre, solidObject attacker){
		isInAction = true;
		this.angle = 360 - angle;
		this.damage = damage; 
		this.target = target;
		this.centre.set(centre);
		this.attacker = attacker;
		
		
		iDirection.set(1,0,0);
		iDirection.rotate_XZ(this.angle);
		kDirection.set(0,0,1);
		kDirection.rotate_XZ(this.angle);
		
		for(int i = 0; i < 5; i ++){
			for(int j = 0; j < 4; j ++){
				change(polygons[i].vertex3D[j], baseGeometry[i][j]);
			}
		}
		
		
		movement.set(0,0,1);
		movement.rotate_XZ(this.angle);
		movement.scale(speed);
		
		distanceToTarget = (float)Math.sqrt((target.centre.x - centre.x) * (target.centre.x - centre.x) + (target.centre.z - centre.z) * (target.centre.z - centre.z));
	}
	
	
	
	public void makePolygons(){
		polygons = new polygon3D[5]; 
		vector[] v;
		for(int i = 0; i < 5; i++){
			v = new vector[]{new vector(0, 0, 0), new vector(0, 0, 0), new vector(0, 0, 0), new vector(0, 0, 0)}; 
			polygons[i] = new polygon3D(v, v[0], v[1], v[3], null, 1f,1f,0);
			polygons[i].color = 7 + (7 << 5) + (8 << 10);
			polygons[i].diffuse_I = 127;
		}
		
		if(baseGeometry == null){
			float l = 0.003f;
			float h = 0.003f;
			float w = 0.02f;
			baseGeometry = new vector[5][];
			baseGeometry[0] = new vector[]{new vector(l, h, w), new vector(-l, h, w), new vector(-l, -h, w), new vector(l, -h, w)};
			baseGeometry[1] = new vector[]{new vector(l, h, -w), new vector(l, h, w), new vector(l, -h, w), new vector(l, -h, -w)};
			baseGeometry[2] = new vector[]{new vector(-l, h, -w), new vector(l, h, -w), new vector(l, -h, -w), new vector(-l, -h, -w)};
			baseGeometry[3] = new vector[]{new vector(-l, h, w), new vector(-l, h, -w), new vector(-l, -h, -w), new vector(-l, -h, w)};
			baseGeometry[4] = new vector[]{new vector(-l, h, w), new vector(l, h, w), new vector(l, h, -w), new vector(-l, h, -w)};
		}
		
		
		
	}
	
	public void updateAndDraw(){
		if(!isInAction)
			return;
	
		
		distanceToTarget -= speed;
		if(distanceToTarget < 0){
			
			isInAction = false;
			movement.unit();
			movement.scale(speed + distanceToTarget);
			target.currentHP -=damage;
			int previousUnderAttackCountDown = target.underAttackCountDown;
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
						if(tile[j].teamNo == targetTeamNo && tile[j].teamNo!= attacker.teamNo && tile[j].attackStatus != solidObject.isAttacking && tile[j].currentCommand != solidObject.move && tile[j].isCloaked == false
								&& 	previousUnderAttackCountDown <=30 && (tile[j].currentCommand == solidObject.StandBy || tile[j].secondaryCommand == solidObject.attackMove)){
							if(tile[j].type < 100){								
								tile[j].attack(attacker);
								tile[j].currentCommand = solidObject.attackCautiously; 
							}
						}
						
						/*if(tile[j].teamNo == teamNo && tile[j].currentCommand == solidObject.StandBy && attacker.teamNo != teamNo && tile[j].isCloaked == false){
							if(tile[j].type < 100){								
								tile[j].attack(attacker);
								tile[j].currentCommand = solidObject.attackInNumbers; 
							}
						}else if(tile[j].teamNo == teamNo && tile[j].secondaryCommand == solidObject.attackMove && attacker.teamNo != teamNo && tile[j].isCloaked == false){
							if(tile[j].attackStatus != solidObject.isAttacking || (tile[j].attackStatus == solidObject.isAttacking && tile[j].targetObject != null && tile[j].targetObject.type < 199 && tile[j].targetObject.type > 7)){
								tile[j].attack(attacker);
								tile[j].currentCommand = solidObject.attackInNumbers; 
							}
						}*/
						
					}
				}
			}
			
			
		}
		
		
		centre.add(movement);
		
		for(int i = 0; i < 5; i ++){
			for(int j = 0; j < 4; j++){
				polygons[i].vertex3D[j].add(movement);
			}
		}
		
		for(int i = 0; i < 5; i ++){
			polygons[i].update();
			polygons[i].draw();
		}
	
		if(distanceToTarget < 0){
			//spawn an explosion at the end of the bullet life
			float[] tempFloat = mainThread.theAssetManager.explosionInfo[mainThread.theAssetManager.explosionCount];	
			tempFloat[0] = centre.x;
			if(target.type > 100 && target.type < 200){
				tempFloat[1] = centre.y + 0.2f;
			}else{
				tempFloat[1] = centre.y;
			}
			tempFloat[2] = centre.z;
			tempFloat[3] = 1.5f;
			
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 6 + (gameData.getRandom()%4);
			tempFloat[7] = target.height;
			mainThread.theAssetManager.explosionCount++; 
		}
		
	}
	
	
	
	
	public void change(vector v, vector baseVector){
		v.set(centre);
		v.add(iDirection, baseVector.x);
		v.add(jDirection, baseVector.y);
		v.add(kDirection, baseVector.z);
	}
}
