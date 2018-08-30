package entity;

import java.awt.Rectangle;

import core.*;

//small flying unit capable of repairing tanks

public class drone extends solidObject{

	public vector iDirectionBody, jDirectionBody, kDirectionBody;
	
	public vector bodyCenter;
	
	public static polygon3D[] polys;
	
	public static vector engine1Center, engine2Center;
	
	public int fan1Angle, fan2Angle;
	
	public final static Rectangle visibleBoundary = new Rectangle(-100,-150,1068, 812);
	
	//a screen space boundary which is used to test if the entire  object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40,40,688, 432);
	
	//screen space boundary which is used to test if the shadow of the  object is within the screen
	public final static Rectangle shadowBoundary1 = new Rectangle(0,0,768, 512);
	
	
	public int bodyAngle, destinationAngle;
	
	//index of the tiles to check when the drone is idle
	public static int[] tileCheckList;
	
	public factory myFactory;
	
	public static int numOfPolygons;
	
	public float heightVariance;
	
	public int randomNumber;
	
	public solidObject targetUnit;
	
	public vector idlePosition;
	
	public static final int returnToIdlePosition = 0;
	public static final int healUnit = 1;
	
	public static final int turnRate = 5;
	public static final float maxSpeed = 0.04f;
	
	public float serviceRadius;
	
	public static vector armCenter, armDirection;
	public vector armCenterClone, armDirectionClone;
		
		
	public drone(vector origin, int bodyAngle, factory myFactory){
		//register itself in factory and find out idle location
		idlePosition = new vector(0,0,0);
		
		serviceRadius = 3.5f;
		
		for(int i = 0; i < 3; i++){
			if(myFactory.myDrones[i] == null){
				myFactory.myDrones[i] = this;
				if(i == 0){
					idlePosition.set(myFactory.centre.x, 0, myFactory.centre.z+0.15f);
				}else if(i == 1){
					idlePosition.set(myFactory.centre.x - 0.3f, 0, myFactory.centre.z-0.25f);
				}else{
					idlePosition.set(myFactory.centre.x + 0.2f, 0, myFactory.centre.z-0.25f);
				}
				break;
			}
		}
		
		
		
		speed = 0f;
		currentHP = 20;
		start = origin.myClone();
		centre = origin.myClone();
		tempCentre = origin.myClone();
		bodyCenter = origin.myClone();
		this.bodyAngle = 360 -bodyAngle;
		this.immediateDestinationAngle = bodyAngle;
		
		destinationAngle = bodyAngle;
		
		teamNo = myFactory.teamNo;
		this.myFactory = myFactory;
		
		//drone does't have any collision boundary, and its unselectable
		type = 5;  
		isSelectable = false;
		
		height = centre.y + 0.5f;  //?
		theAssetManager = mainThread.theAssetManager; 
		
		movement = new vector(0,0,0);
		
	
	
		//create main axis in object space
		iDirection = new vector(1f,0,0);   
		jDirection = new vector(0,1f,0);   
		kDirection = new vector(0,0,1f);  
		
		iDirection.rotate_XZ(360-bodyAngle);
		jDirection.rotate_XZ(360-bodyAngle);
		kDirection.rotate_XZ(360-bodyAngle);
		
	
		//create polygons 
		makePolygons();

	
		if(tileCheckList == null){
			tileCheckList = generateTileCheckList(11f);   
		}
		
		randomNumber = (int)(Math.random()*360);
	}
	
	public void makePolygons(){
		if(polys == null){
			polys = new polygon3D[171];
		
			vector[] v;
			
			engine1Center = new vector(0.041f, 0, 0);
			engine2Center = new vector(-0.041f, 0, 0);
			
			
			//create engine fan
			start.reset();
			float delta = (float)Math.PI/4;
			v = new vector[]{
					put(0.027*Math.cos(1*delta), -0.01, 0.027*Math.sin(1*delta)),
					put(0.027*Math.cos((0)*delta), -0.01, 0.027*Math.sin((0)*delta)),
					put(0.00001*Math.cos((0)*delta), -0.01, 0.00001*Math.sin((0)*delta)),
					put(0.00001*Math.cos(1*delta), -0.01, 0.00001*Math.sin(1*delta)),
			};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			v = new vector[]{
					put(0.027*Math.cos(5*delta), -0.01, 0.027*Math.sin(5*delta)),
					put(0.027*Math.cos((4)*delta), -0.01, 0.027*Math.sin((4)*delta)),
					put(0.00001*Math.cos((4)*delta), -0.01, 0.00001*Math.sin((4)*delta)),
					put(0.00001*Math.cos(5*delta), -0.01, 0.00001*Math.sin(5*delta)),
			};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			
			v = new vector[]{
					put(0.027*Math.cos(3*delta), -0.01, 0.027*Math.sin(3*delta)),
					put(0.027*Math.cos((2)*delta), -0.01, 0.027*Math.sin((2)*delta)),
					put(0.00001*Math.cos((2)*delta), -0.01, 0.00001*Math.sin((2)*delta)),
					put(0.00001*Math.cos(3*delta), -0.01, 0.00001*Math.sin(3*delta)),
			};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			v = new vector[]{
					put(0.027*Math.cos(7*delta), -0.01, 0.027*Math.sin(7*delta)),
					put(0.027*Math.cos((6)*delta), -0.01, 0.027*Math.sin((6)*delta)),
					put(0.00001*Math.cos((6)*delta), -0.01, 0.00001*Math.sin((6)*delta)),
					put(0.00001*Math.cos(7*delta), -0.01, 0.00001*Math.sin(7*delta)),
			};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			
			//create main body section
			v = new vector[]{ put(-0.016, 0, -0.006),put(-0.016, 0, 0.006), put(-0.015, 0, 0.013), put(0.015, 0, 0.013), put(0.016, 0, 0.006), put(0.016, 0, -0.006),  put(0.015, 0, -0.013),put(-0.015, 0, -0.013)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{put(-0.016, 0, -0.006), put(-0.015, 0, -0.013), put(-0.015, -0.015, -0.013), put(-0.016, -0.015, -0.006)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{ put(0.016, -0.015, -0.006), put(0.015, -0.015, -0.013), put(0.015, 0, -0.013),put(0.016, 0, -0.006)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{put(-0.016, -0.015, 0.006), put(-0.015, -0.015, 0.013), put(-0.015, 0, 0.013), put(-0.016, 0, 0.006)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{put(0.016, 0, 0.006), put(0.015, 0, 0.013), put(0.015, -0.015, 0.013), put(0.016, -0.015, 0.006)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{put(0.015, 0, 0.013), put(-0.015, 0, 0.013), put(-0.015, -0.015, 0.013), put(0.015, -0.015, 0.013)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			v = new vector[]{put(0.015, -0.015, -0.013), put(-0.015, -0.015, -0.013), put(-0.015, 0, -0.013), put(0.015, 0, -0.013)};
			addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			
			//left engine
			float r = 0.03f;
			delta = (float)Math.PI/8;
			
			start.x-=0.041f;

			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r*Math.cos(i*delta), 0.01, r*Math.sin(i*delta)),
								 put(r*Math.cos((i+1)*delta), 0.01, r*Math.sin((i+1)*delta)),
								 put(r*Math.cos((i+1)*delta), -0.013,  r*Math.sin((i+1)*delta)),
								 put(r*Math.cos(i*delta), -0.013, r*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			float r2 = 0.026f;
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r2*Math.cos(i*delta), -0.013, r2*Math.sin(i*delta)),
						 		put(r2*Math.cos((i+1)*delta), -0.013,  r2*Math.sin((i+1)*delta)),
						 		put(r2*Math.cos((i+1)*delta), 0.01, r2*Math.sin((i+1)*delta)),
						 		put(r2*Math.cos(i*delta), 0.01, r2*Math.sin(i*delta))

								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			for(int i = 0; i < 16; i++){
				v = new vector[]{
						put(r2*Math.cos(i*delta), 0.01, r2*Math.sin(i*delta)),
						put(r2*Math.cos((i+1)*delta), 0.01, r2*Math.sin((i+1)*delta)),
						put(r*Math.cos((i+1)*delta), 0.01, r*Math.sin((i+1)*delta)),
						put(r*Math.cos(i*delta), 0.01, r*Math.sin(i*delta)),
				};
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			}
			
			float r3 = 0.005f;
			float r4 = 0.0001f;
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r4*Math.cos(i*delta), 0.01, r4*Math.sin(i*delta)),
								 put(r4*Math.cos((i+1)*delta), 0.01, r4*Math.sin((i+1)*delta)),
								 put(r3*Math.cos((i+1)*delta), -0.01,  r3*Math.sin((i+1)*delta)),
								 put(r3*Math.cos(i*delta), -0.01, r3*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			//right engine
			start.x+=0.082f;
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r*Math.cos(i*delta), 0.01, r*Math.sin(i*delta)),
								 put(r*Math.cos((i+1)*delta), 0.01, r*Math.sin((i+1)*delta)),
								 put(r*Math.cos((i+1)*delta), -0.013,  r*Math.sin((i+1)*delta)),
								 put(r*Math.cos(i*delta), -0.013, r*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r2*Math.cos(i*delta), -0.013, r2*Math.sin(i*delta)),
						 		put(r2*Math.cos((i+1)*delta), -0.013,  r2*Math.sin((i+1)*delta)),
						 		put(r2*Math.cos((i+1)*delta), 0.01, r2*Math.sin((i+1)*delta)),
						 		put(r2*Math.cos(i*delta), 0.01, r2*Math.sin(i*delta))

								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			for(int i = 0; i < 16; i++){
				v = new vector[]{
						put(r2*Math.cos(i*delta), 0.01, r2*Math.sin(i*delta)),
						put(r2*Math.cos((i+1)*delta), 0.01, r2*Math.sin((i+1)*delta)),
						put(r*Math.cos((i+1)*delta), 0.01, r*Math.sin((i+1)*delta)),
						put(r*Math.cos(i*delta), 0.01, r*Math.sin(i*delta)),
				};
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			}
			
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r4*Math.cos(i*delta), 0.01, r4*Math.sin(i*delta)),
								 put(r4*Math.cos((i+1)*delta), 0.01, r4*Math.sin((i+1)*delta)),
								 put(r3*Math.cos((i+1)*delta), -0.01,  r3*Math.sin((i+1)*delta)),
								 put(r3*Math.cos(i*delta), -0.01, r3*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
				
			}
			
			//repair arm
			start.x-=0.041f;
			r2 = 0.008f;
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r2*Math.cos(i*delta), 0, r2*Math.sin(i*delta)),
						put(r2*Math.cos((i+1)*delta), 0, r2*Math.sin((i+1)*delta)),
						put(r2*Math.cos((i+1)*delta), -0.08,  r2*Math.sin((i+1)*delta)),
						put(r2*Math.cos(i*delta), -0.08, r2*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			}
			
			
			
			iDirection.rotate_YZ(340);
			jDirection.rotate_YZ(340);
			kDirection.rotate_YZ(340);
			
			start.y-=0.075f;
			r3 = 0.007f;
			for(int i = 0; i < 16; i++){
				v = new vector[]{put(r2*Math.cos(i*delta), 0, r2*Math.sin(i*delta)),
						put(r2*Math.cos((i+1)*delta), 0, r2*Math.sin((i+1)*delta)),
						put(r3*Math.cos((i+1)*delta), -0.04,  r3*Math.sin((i+1)*delta)),
						put(r3*Math.cos(i*delta), -0.04, r3*Math.sin(i*delta))
								};
				
				addPolygon(polys, new polygon3D(v, v[0], v[1], v[3], mainThread.textures[26], 1f,1f,1));
			}
			
			armCenter = put(0,-0.04, 0);
			armDirection = jDirection.myClone();
			armDirection.y*=-1;
			
		}
		polygons = clonePolygons(polys, false);
		armCenterClone = armCenter.myClone();
		armDirectionClone = armDirection.myClone();
		
		
	}
	
	
	
	//update the model 
	public void update(){
		
		//check if factory where the drone is spawned has been destroyed
		if(myFactory.currentHP <= 0){
			
			if(targetUnit != null)
				targetUnit.myHealer = null;
			
			//spawn an explosion when the object is destroyed
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];	
			tempFloat[0] = centre.x;
			tempFloat[1] = centre.y;
			tempFloat[2] = centre.z;
			tempFloat[3] = 0.99f;
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 7;
			tempFloat[7] = this.height;
			theAssetManager.explosionCount++; 
		
			theAssetManager.removeObject(this); 
			
			return;
			
		}
		
		//handle AI
		
		if(!disableUnitLevelAI){
			if(currentCommand == returnToIdlePosition){
				
				if(centre.y <=0.05){
					centre.y+=0.005f;
				}
				
				if(idlePosition.x != centre.x || idlePosition.z != centre.z){
					double distanceToDestination = Math.sqrt((idlePosition.x - centre.x)* (idlePosition.x - centre.x) + (idlePosition.z - centre.z)*(idlePosition.z - centre.z));
					
					if(distanceToDestination >= 0.2f){
						if(speed <= maxSpeed)
							speed+=0.002f;
					}
					if(distanceToDestination < 0.4f){
						if(speed >= 0.03f)
							speed-=0.002f;
					}
					if(distanceToDestination < 0.25f){
						if(speed >= 0.02f)
							speed-=0.002f;
					}
					if(distanceToDestination < 0.1f){
						if(speed >= 0.01f)
							speed-=0.002f;
					}
					
					//move to idle position
					if(distanceToDestination <= speed){
						centre.x = idlePosition.x;
						centre.z = idlePosition.z;
						speed = 0;
					}else{
						movement.set(idlePosition.x - centre.x, 0, idlePosition.z - centre.z);
						movement.unit();
						movement.scale(speed);
						centre.add(movement);
					}
					
					//face idle position
					destinationAngle = geometry.findAngle(centre.x, centre.z, idlePosition.x, idlePosition.z);
					int angleDelta = 360 - (geometry.findAngleDelta(bodyAngle, destinationAngle, turnRate) + 360)%360;
					bodyAngle= (bodyAngle - angleDelta + 360)%360;
					if(Math.abs(bodyAngle - destinationAngle) <= turnRate)
						bodyAngle = destinationAngle;
				
				}
				
				//scan for nearby damaged unit
				currentOccupiedTile = (int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128;
				
				for(int i = 0; i < tileCheckList.length; i++){
					if(targetUnit != null)
						break;
					
					if(tileCheckList[i] != Integer.MAX_VALUE){
						int index = currentOccupiedTile + tileCheckList[i];
						if(index < 0 || index >= 16384)
							continue;
						tile = mainThread.gridMap.tiles[index];
						
						for(int j = 0; j < 4; j++){
							if(tile[j] != null){
								if(tile[j].teamNo == teamNo && tile[j].currentHP < tile[j].getMaxHp() && getDistance(myFactory, tile[j]) < serviceRadius && tile[j].myHealer == null){
									
									currentCommand = healUnit;
									targetUnit = tile[j];
									targetUnit.myHealer = this;
									break;
								}
							}
						}
					}
				}
				
			}else if(currentCommand == healUnit){
				if(targetUnit.currentHP <=0 || getDistance(myFactory, targetUnit) >= serviceRadius){
					targetUnit.myHealer = null;
					targetUnit = null;
					currentCommand = returnToIdlePosition;
				}else{
					tempVector.set(targetUnit.centre);
					if(tempVector.x != centre.x || tempVector.z != centre.z){
						double distanceToDestination = Math.sqrt((tempVector.x - centre.x)* (tempVector.x - centre.x) + (tempVector.z - centre.z)*(tempVector.z - centre.z));
						
						if(distanceToDestination >= 0.2f){
							if(speed <= maxSpeed)
								speed+=0.002f;
							
							if(centre.y <=0.1){
								centre.y+=0.01f;
							}
						}else{
							if(centre.y > - 0.15){
								centre.y-=0.01f;
							}
							
						}
						
						if(distanceToDestination < 0.4f){
							if(speed >= 0.03f)
								speed-=0.002f;
							
							//heal unit
							if(targetUnit.currentHP < targetUnit.getMaxHp() || targetUnit.underAttackCountDown > 60){
								if(mainThread.frameIndex%5 == 1  && centre.y <=-0.1){
									targetUnit.currentHP+=5;
									if(targetUnit.currentHP > targetUnit.getMaxHp())
										targetUnit.currentHP = targetUnit.getMaxHp();
								}
								if(mainThread.frameIndex%2==0 && centre.y <=-0.15){
									//spawn a healing steam particle
									float[] tempFloat = theAssetManager.smokeEmmiterList[theAssetManager.smokeEmmiterCount];
									tempFloat[0] = armCenterClone.x + (float)(Math.random()/20) - 0.025f;
									tempFloat[1] = armCenterClone.y;
									tempFloat[2] = armCenterClone.z + (float)(Math.random()/20) - 0.025f;
									tempFloat[3] = 0.8f;
									tempFloat[4] = 1;
									tempFloat[5] = 11;
									tempFloat[6] = this.height;
									theAssetManager.smokeEmmiterCount++;
								}
							}else{
								targetUnit.myHealer = null;
								targetUnit = null;
								currentCommand = returnToIdlePosition;
							}
						}
						if(distanceToDestination < 0.25f){
							if(speed >= 0.02f)
								speed-=0.002f;
							
							
						}
						if(distanceToDestination < 0.1f){
							if(speed >= 0.01f)
								speed-=0.002f;
							
							
						}
						
					
						if(distanceToDestination >= 0.06f){
						
							movement.set(tempVector.x - centre.x, 0, tempVector.z - centre.z);
							movement.unit();
							movement.scale(speed);
							centre.add(movement);
						}
						
						//face idle position
						destinationAngle = geometry.findAngle(centre.x, centre.z, tempVector.x, tempVector.z);
						int angleDelta = 360 - (geometry.findAngleDelta(bodyAngle, destinationAngle, turnRate) + 360)%360;
						bodyAngle= (bodyAngle - angleDelta + 360)%360;
						if(Math.abs(bodyAngle - destinationAngle) <= turnRate)
							bodyAngle = destinationAngle;
					
					}
				}
			}
		}
		
		
			
		fan1Angle+=60;
		fan2Angle+=300;
		fan1Angle = fan1Angle%360;
		fan2Angle = fan2Angle%360;
		
		heightVariance = gameData.sin[((mainThread.frameIndex+randomNumber)*5)%360] * 0.01f;
		
			
		//update center in camera coordinate
		tempCentre.set(centre);
		tempCentre.subtract(camera.position);
		tempCentre.rotate_XZ(camera.XZ_angle);
		tempCentre.rotate_YZ(camera.YZ_angle); 
		tempCentre.updateLocation();
		
		//check if the tank object is visible in mini map
		visible_minimap = theAssetManager.minimapBitmap[(int)(centre.x*64)/16 + (127 - (int)(centre.z*64)/16)*128];
	
	

			
		

	
		//test if the object is visible in camera point of view
		if(visibleBoundary.contains(tempCentre.screenX, tempCentre.screenY) && myFactory.isRevealed && visible_minimap){
			visible = true;
			
			for(int i = 0; i < numOfPolygons; i++){
				polygons[i].update_lightspace();	
			}
			
			//update centre
			updateGeometry();
			
			
			if(screenBoundary.contains(tempCentre.screenX, tempCentre.screenY))
				withinViewScreen = true;
			else
				withinViewScreen = false;
			
			
			

		}else{
			visible = false;
		}
			
	
	}
	
	public void updateGeometry(){
		int bodyAngle_ = 360 - bodyAngle;
		//rotate fans
		for(int j = 0; j < polygons[0].vertex3D.length; j++){
			polygons[0].vertex3D[j].set(polys[0].vertex3D[j]);
			polygons[0].vertex3D[j].rotate_XZ(fan1Angle);
			polygons[0].vertex3D[j].add(engine1Center);
			polygons[0].vertex3D[j].rotate_XZ(bodyAngle_);
			polygons[0].vertex3D[j].add(centre);
			polygons[0].vertex3D[j].y+=heightVariance;
			
			polygons[1].vertex3D[j].set(polys[1].vertex3D[j]);
			polygons[1].vertex3D[j].rotate_XZ(fan1Angle);
			polygons[1].vertex3D[j].add(engine1Center);
			polygons[1].vertex3D[j].rotate_XZ(bodyAngle_);
			polygons[1].vertex3D[j].add(centre);
			polygons[1].vertex3D[j].y+=heightVariance;
			
			polygons[2].vertex3D[j].set(polys[2].vertex3D[j]);
			polygons[2].vertex3D[j].rotate_XZ(fan2Angle);
			polygons[2].vertex3D[j].add(engine2Center);
			polygons[2].vertex3D[j].rotate_XZ(bodyAngle_);
			polygons[2].vertex3D[j].add(centre);
			polygons[2].vertex3D[j].y+=heightVariance;
			
			polygons[3].vertex3D[j].set(polys[3].vertex3D[j]);
			polygons[3].vertex3D[j].rotate_XZ(fan2Angle);
			polygons[3].vertex3D[j].add(engine2Center);
			polygons[3].vertex3D[j].rotate_XZ(bodyAngle_);
			polygons[3].vertex3D[j].add(centre);
			polygons[3].vertex3D[j].y+=heightVariance;
		}
		
		
		//update main body
		for(int i =4; i < numOfPolygons; i++){
			
			for(int j = 0; j < polygons[i].vertex3D.length; j++){
				polygons[i].vertex3D[j].set(polys[i].vertex3D[j]);
				polygons[i].normal.set(polys[i].normal);
				polygons[i].normal.rotate_XZ(bodyAngle_);
				polygons[i].vertex3D[j].rotate_XZ(bodyAngle_);
				polygons[i].vertex3D[j].add(centre);
				polygons[i].vertex3D[j].y+=heightVariance;
				
				
				polygons[i].findDiffuse();
				
			}
		}
		
		//update arm
		armCenterClone.set(armCenter);
		armCenterClone.rotate_XZ(bodyAngle_);
		armCenterClone.add(centre);
		armCenterClone.y+=heightVariance;
		
		armDirectionClone.set(armDirection);
		armDirectionClone.rotate_XZ(bodyAngle_);
	}
	
	public void draw(){
		
		if(!visible)
			return;
		
		for(int i = 0; i < numOfPolygons; i++){
			polygons[i].update();
			polygons[i].draw();
			
		}
		
		
	}
	
	public int  addPolygon(polygon3D[] polys, polygon3D poly){
		for(int i = 0; i < polys.length; i++){
			if(polys[i] == null){
				polys[i] = poly;
				numOfPolygons++;
				return i;
			}
		}
		return -1;
	}
	
	public float getDistance(solidObject o1, solidObject o2){
		return (float)Math.sqrt((o1.centre.x - o2.centre.x)*(o1.centre.x - o2.centre.x) +   (o1.centre.z - o2.centre.z)*(o1.centre.z - o2.centre.z));
	}

}
