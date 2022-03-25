package gui;

import core.*;
import entity.SolidObject;
import entity.ConstructionYard;

public class DeployGrid {
	public polygon3D[] polygons;
	public boolean canBeDeployed;
	public int[] gridArea;
	public vector gridOneCenter;
	public vector iDirection, jDirection, kDirection, start;
	public vector clickPoint;
	public ConstructionYard cy;
	public int gridOneIndex;
	public DeployGrid(){
		gridArea = new int[9];
		iDirection = new vector(1,0,0);
		jDirection = new vector(0,1,0);
		kDirection = new vector(0,0,1);
		start = new vector(0,0,0);
		clickPoint = new vector(0,0,0);
		
		makeGrid();
	}
	
	public void makeGrid(){
		polygons = new polygon3D[9];
		
		double w = 0;
		double h = 0;
		
		vector[] v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[0] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.25;
		h = 0;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[1] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.5;
		h = 0;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[2] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0;
		h = -0.25;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[3] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.25;
		h = -0.25;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[4] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.5;
		h = -0.25;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[5] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0;
		h = -0.5;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[6] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.25;
		h = -0.5;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[7] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		w = 0.5;
		h = -0.5;
		
		v = new vector[]{put(-0.1 + w, -0.5, 0.12 + h), put(0.1 + w, -0.5, 0.12 + h), put(0.12 + w, -0.5, 0.1 + h), put(0.12 + w, -0.5, -0.1 + h), put(0.1 + w, -0.5, -0.12 + h), put(-0.1 + w, -0.5, -0.12 + h), put(-0.12 + w, -0.5, -0.1 + h), put(-0.12 + w, -0.5, 0.1 + h)};
		polygons[8] = new polygon3D(v, v[0], v[1], v [3], null, 1f,1f,10);
		
		for(int i = 0; i < 9; i++)
			polygons[i].color = 31 << 5;
		
		gridOneCenter = new vector(0,0,0);
	}
	
	public void update(){
		cy = MainThread.playerCommander.selectedConstructionYard;
		clickPoint.set(MainThread.my2Dto3DFactory.get3DLocation(MainThread.theAssetManager.Terrain.ground[0], InputHandler.mouse_x, InputHandler.mouse_y));
		
		gridOneIndex = (int)(clickPoint.x*4) + (127 - (int)(clickPoint.z*4))*128;
		
		clickPoint.x = (float)((int)(clickPoint.x*4))/4 + 0.125f;
		clickPoint.z = (float)((int)(clickPoint.z*4))/4 + 0.125f;
		
		float dx = clickPoint.x - gridOneCenter.x;
		float dz = clickPoint.z - gridOneCenter.z;
		
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 8; j++){
				polygons[i].vertex3D[j].x+=dx;
				polygons[i].vertex3D[j].z+=dz;
			}
			
		}
		
		gridOneCenter.x = clickPoint.x;
		gridOneCenter.z = clickPoint.z;
		
		if(cy != null){
			canBeDeployed = true;
			if(cy.powerPlantProgress == 240 || cy.communicationCenterProgress == 240 || cy.techCenterProgress == 240){
				gridArea[0] = 1;    if(!checkIfBlockIsFree(gridOneIndex)){ gridArea[0] = 2; canBeDeployed = false;}
				gridArea[1] = 1;    if(!checkIfBlockIsFree(gridOneIndex+1)){ gridArea[1] = 2; canBeDeployed = false;}
				gridArea[2] = 0;
				gridArea[3] = 1;    if(!checkIfBlockIsFree(gridOneIndex + 128)){ gridArea[3] = 2; canBeDeployed = false;}
				gridArea[4] = 1;    if(!checkIfBlockIsFree(gridOneIndex + 129)){ gridArea[4] = 2; canBeDeployed = false;}
				gridArea[5] = 0;
				gridArea[6] = 0;
				gridArea[7] = 0;
				gridArea[8] = 0;
			}else if(cy.refineryProgress == 240 || cy.factoryProgress == 240){
				gridArea[0] = 1;    if(!checkIfBlockIsFree(gridOneIndex)){ gridArea[0] = 2; canBeDeployed = false;}
				gridArea[1] = 1;    if(!checkIfBlockIsFree(gridOneIndex+1)){ gridArea[1] = 2; canBeDeployed = false;}
				gridArea[2] = 1;	if(!checkIfBlockIsFree(gridOneIndex+2)){ gridArea[2] = 2; canBeDeployed = false;}
				gridArea[3] = 1;    if(!checkIfBlockIsFree(gridOneIndex + 128)){ gridArea[3] = 2; canBeDeployed = false;}
				gridArea[4] = 1;    if(!checkIfBlockIsFree(gridOneIndex + 129)){ gridArea[4] = 2; canBeDeployed = false;}
				gridArea[5] = 1;	if(!checkIfBlockIsFree(gridOneIndex + 130)){ gridArea[5] = 2; canBeDeployed = false;}
				gridArea[6] = 1;	if(!checkIfBlockIsFree(gridOneIndex + 256)){ gridArea[6] = 2; canBeDeployed = false;}
				gridArea[7] = 1;	if(!checkIfBlockIsFree(gridOneIndex + 257)){ gridArea[7] = 2; canBeDeployed = false;}
				gridArea[8] = 1;	if(!checkIfBlockIsFree(gridOneIndex + 258)){ gridArea[8] = 2; canBeDeployed = false;}
			}else if(cy.gunTurretProgress == 240 || cy.missileTurretProgress == 240){
				gridArea[0] = 1;    if(!checkIfBlockIsFree(gridOneIndex)){ gridArea[0] = 2; canBeDeployed = false;}
				gridArea[1] = 0;    
				gridArea[2] = 0;
				gridArea[3] = 0;   
				gridArea[4] = 0;    
				gridArea[5] = 0;
				gridArea[6] = 0;
				gridArea[7] = 0;
				gridArea[8] = 0;
			}
			
			
		}
	}
	
	public void draw(){
		for(int i = 0; i < 9; i++){
			if(gridArea[i] >= 1){
				polygons[i].update();
				
				if(gridArea[i] == 1)
					polygons[i].color = 31 << 5;
				else
					polygons[i].color = 31 << 10;
				
				polygons[i].draw();
			}
		}
		
	}
	
	public boolean checkIfBlockIsFree(int index){
		int y = index/128;
		int x = index%128;
		
		if(y > 0 && y < 127 && x > 0 && x < 127){
			SolidObject[] tile = MainThread.gridMap.tiles[index];
			for(int j = 0; j < 5; j++){
				if(tile[j] != null){
					return false;
				}
			}
			
			ConstructionYard[] cys = MainThread.theAssetManager.constructionYards;
			
			for(int i = 0; i < cys.length; i++){
				if(cys[i] != null && cys[i].teamNo == 0){
					float xPos = x * 0.25f + 0.125f;
					float yPos = (127 - y)*0.25f + 0.125f;
					
					float distance = (float) Math.sqrt((cys[i].centre.x - xPos)*(cys[i].centre.x - xPos) + (cys[i].centre.z - yPos)*(cys[i].centre.z - yPos));
					if(distance <= 2.75)
						return true;
				}
			}
		}
			
		return false;
		
	}
	
	//create a arbitrary vertex
	public  vector put(double i, double j, double k){
		vector temp = start.myClone();
		temp.add(iDirection, (float)i);
		temp.add(jDirection, (float)j);
		temp.add(kDirection, (float)k);
		return temp;
	}
	
}
