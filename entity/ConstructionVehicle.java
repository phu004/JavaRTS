package entity;

import java.awt.Rectangle;

import core.*;
import enemyAI.EnemyCommander;


public class ConstructionVehicle extends SolidInfrastructure {

	public vector iDirectionBody, jDirectionBody, kDirectionBody;
	public vector bodyCenter;

	public static polygon3D[] body, arm, pillar, foot1, foot2, foot3, foot4;
	public polygon3D[] bodyClone, armClone, pillarClone, foot1Clone,
			foot2Clone, foot3Clone, foot4Clone;

	public static vector armCenter;
	public vector armCenterClone;
	public int armAngle;
	public int openArmCount;
	public int extendArmCount;

	public static vector pillarCenter;
	public vector pillarCenterClone;
	public int pillarAngle;
	public int pillarArmCount;
	public int footExtendCount;

	public static int maxHP = 300;

	// a screen space boundary which is used to test if the Harvester object is
	// visible from Camera point of view
	public final static Rectangle visibleBoundary = new Rectangle(-70, -25,screen_width+140, screen_height+85);

	// a screen space boundary which is used to test if the entire Harvester
	// object is within the screen
	public final static Rectangle screenBoundary = new Rectangle(40, 40, screen_width-90,screen_height-80);

	// a screen space boundary which is used to test if the vision polygon of
	// the object is visible.
	public final static Rectangle visionBoundary = new Rectangle(0, 0, 1400+(screen_width-768),1300+(screen_height-512));
	
	public final static int visionW = 500 + (screen_width-768);
	public final static int visionH = 650 + (screen_height-512);

	// a bitmap representation of the vision of the Harvester for enemy
	// commander
	public static boolean[] bitmapVisionForEnemy;

	// the oreintation of the construction vehicle
	public int bodyAngle;

	// destination angle
	public int destinationAngle;

	public static Rect border, destinationBlock, probeBlock, pointBlock;

	public int heuristicRecalculationCountDown;
	public byte[] heuristicMap;
	public boolean pathIsFound;
	public float nextNodeX, nextNodeY;
	public int bodyTurnRate = 5;

	public int jobStatus = 0;
	public final int idle = 0;
	public final int deploying = 1;

	public static vector tempVector0;
	public static vector tempVector1;
	public static vector tempVector2;
	public static vector tempVector3;

	public static int[] surrounding = new int[9];

	public ConstructionYard myConstructionYard;

	public ConstructionVehicle(vector origin, int bodyAngle, int teamNo) {
		speed = 0.009f;
		start = new vector(0, 0, 0);
		centre = origin.myClone();
		tempCentre = origin.myClone();
		this.bodyAngle = bodyAngle;
		immediateDestinationAngle = bodyAngle;
		progressStatus = -1;
		attackStatus = isAttacking;

		destinationAngle = bodyAngle;
		this.teamNo = teamNo;
		currentHP = maxHP;
		type = 3;
		if (bitmapVisionForEnemy == null) {
			bitmapVisionForEnemy = createBitmapVision(6);
		}

		ID = globalUniqID++;
		randomNumber = GameData.getRandom();
		height = centre.y + 0.5f; // ?
		theAssetManager = MainThread.theAssetManager;
		boundary2D = new Rect((int) (origin.x * 64) - 8,
				(int) (origin.z * 64) + 8, 16, 16);
		border = new Rect(0, 0, 16, 16);
		movement = new vector(0, 0, 0);
		updateOccupiedTiles(boundary2D.x1, boundary2D.y1);

		boundary2D.owner = this;
		destinationBlock = new Rect((int) (origin.x * 64) - 8,
				(int) (origin.z * 64) + 8, 16, 16);
		probeBlock = new Rect((int) (origin.x * 64) - 6,
				(int) (origin.z * 64) + 6, 12, 12);
		pointBlock = new Rect((int) (origin.x * 64) - 6,
				(int) (origin.z * 64) + 6, 12, 12);

		// create main axis in object space
		iDirection = new vector(1f, 0, 0);
		jDirection = new vector(0, 1f, 0);
		kDirection = new vector(0, 0, 1f);

		// create polygons
		makePolygons();

		heuristicMap = new byte[128 * 128];

	}

	public void makePolygons() {
		
		

		int skinTextureIndex = 42;
		int windowTexture = 43;
		int upperBodyTExture = 44;
		int armTop = 31;

		if (body == null) {
			start.y -= 0.18f;
			body = new polygon3D[87];
			v = new vector[] { createArbitraryVertex(-0.071, 0.025, 0.11),
					createArbitraryVertex(-0.071, 0.025, -0.15), createArbitraryVertex(-0.071, 0.005, -0.15),
					createArbitraryVertex(-0.071, -0.025, -0.08), createArbitraryVertex(-0.071, -0.025, 0.07),
					createArbitraryVertex(-0.071, 0.005, 0.11) };
			body[0] = new polygon3D(v, createArbitraryVertex(-0.071, 0.027, 0.11), createArbitraryVertex(-0.071,
					0.027, -0.15), createArbitraryVertex(-0.071, -0.025, 0.11), MainThread.textures[3],
					1, 1, 1);

			v = new vector[] { createArbitraryVertex(0.071, 0.005, 0.11),
					createArbitraryVertex(0.071, -0.025, 0.07), createArbitraryVertex(0.071, -0.025, -0.08),
					createArbitraryVertex(0.071, 0.005, -0.15), createArbitraryVertex(0.071, 0.025, -0.15),
					createArbitraryVertex(0.071, 0.025, 0.11) };
			body[1] = new polygon3D(v, createArbitraryVertex(0.071, 0.027, -0.15), createArbitraryVertex(0.071,
					0.027, 0.11), createArbitraryVertex(0.071, -0.025, -0.15), MainThread.textures[3],
					1, 1, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.05, -0.15), createArbitraryVertex(0.07, 0.05, -0.15),
					createArbitraryVertex(0.07, 0.015, -0.15), createArbitraryVertex(-0.07, 0.015, -0.15) };
			body[2] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.005, -0.15),
					createArbitraryVertex(-0.05, 0.005, -0.15), createArbitraryVertex(-0.05, -0.025, -0.08),
					createArbitraryVertex(-0.07, -0.025, -0.08) };
			body[3] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[3], 1,
					1, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.015, -0.15),
					createArbitraryVertex(-0.05, 0.015, -0.15), createArbitraryVertex(-0.05, 0.005, -0.15),
					createArbitraryVertex(-0.07, 0.005, -0.15) };
			body[4] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[3], 1,
					1, 1);

			v = new vector[] { createArbitraryVertex(0.05, 0.015, -0.15),
					createArbitraryVertex(0.07, 0.015, -0.15), createArbitraryVertex(0.07, 0.005, -0.15),
					createArbitraryVertex(0.05, 0.005, -0.15) };
			body[5] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[3], 1,
					1, 1);

			v = new vector[] { createArbitraryVertex(0.05, 0.005, -0.15),
					createArbitraryVertex(0.07, 0.005, -0.15), createArbitraryVertex(0.07, -0.025, -0.08),
					createArbitraryVertex(0.05, -0.025, -0.08) };
			body[6] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[3], 1,
					1, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.05, -0.15), createArbitraryVertex(0.07, 0.05, 0.11),
					createArbitraryVertex(0.07, 0.015, 0.11), createArbitraryVertex(0.07, 0.015, -0.15) };
			body[7] = new polygon3D(v, createArbitraryVertex(0.07, 0.05, -0.15), createArbitraryVertex(0.07, 0.05,
					0.11), createArbitraryVertex(0.07, 0.015, -0.15),
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.05, 0.11), createArbitraryVertex(-0.07, 0.05, -0.15),
					createArbitraryVertex(-0.07, 0.015, -0.15), createArbitraryVertex(-0.07, 0.015, 0.11) };
			body[8] = new polygon3D(v, createArbitraryVertex(-0.07, 0.05, 0.11), createArbitraryVertex(-0.07, 0.05,
					-0.15), createArbitraryVertex(-0.07, 0.015, 0.11),
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.05, 0.11), createArbitraryVertex(-0.07, 0.05, 0.11),
					createArbitraryVertex(-0.07, 0.01, 0.11), createArbitraryVertex(0.07, 0.01, 0.11) };
			body[9] = new polygon3D(v, v[2], v[3], v[1],
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.05, 0.11), createArbitraryVertex(-0.07, 0.05, 0.11),
					createArbitraryVertex(-0.07, 0.01, 0.11), createArbitraryVertex(0.07, 0.01, 0.11) };
			body[10] = new polygon3D(v, v[2], v[3], v[1],
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.05, 0.11), createArbitraryVertex(0.07, 0.05, -0.15),
					createArbitraryVertex(-0.07, 0.05, -0.15), createArbitraryVertex(-0.07, 0.05, 0.11) };
			body[11] = new polygon3D(v, v[1], v[2], v[0],
					MainThread.textures[skinTextureIndex], 1, 2f, 1);
			body[11].shadowBias = 1000;

			v = new vector[] { createArbitraryVertex(0.07, 0.08, 0.05), createArbitraryVertex(0.07, 0.08, 0.13),
					createArbitraryVertex(0.07, 0.04, 0.15), createArbitraryVertex(0.07, 0.01, 0.15),
					createArbitraryVertex(0.07, 0.01, 0.02) };
			body[12] = new polygon3D(v, createArbitraryVertex(0.07, 0.05, -0.15), createArbitraryVertex(0.07, 0.05,
					0.11), createArbitraryVertex(0.07, 0.015, -0.15),
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.01, 0.02), createArbitraryVertex(-0.07, 0.01, 0.15),
					createArbitraryVertex(-0.07, 0.04, 0.15), createArbitraryVertex(-0.07, 0.08, 0.13),
					createArbitraryVertex(-0.07, 0.08, 0.05) };
			body[13] = new polygon3D(v, createArbitraryVertex(-0.07, 0.05, 0.11), createArbitraryVertex(-0.07,
					0.05, -0.15), createArbitraryVertex(-0.07, 0.015, 0.11),
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.08, 0.05), createArbitraryVertex(0.07, 0.08, 0.05),
					createArbitraryVertex(0.07, 0.01, 0.02), createArbitraryVertex(-0.07, 0.01, 0.02) };
			body[14] = new polygon3D(v, v[2], v[3], v[1],
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(-0.07, 0.08, 0.13), createArbitraryVertex(0.07, 0.08, 0.13),
					createArbitraryVertex(0.07, 0.08, 0.05), createArbitraryVertex(-0.07, 0.08, 0.05) };
			body[15] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[skinTextureIndex], 1, 0.5f, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.04, 0.15), createArbitraryVertex(-0.07, 0.04, 0.15),
					createArbitraryVertex(-0.07, 0.01, 0.15), createArbitraryVertex(0.07, 0.01, 0.15) };
			body[16] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[skinTextureIndex], 1, 0.3f, 1);

			v = new vector[] { createArbitraryVertex(0.07, 0.08, 0.13), createArbitraryVertex(-0.07, 0.08, 0.13),
					createArbitraryVertex(-0.07, 0.04, 0.15), createArbitraryVertex(0.07, 0.04, 0.15) };
			body[17] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[windowTexture], 1, 0.6f, 1);

			double theta = Math.PI / 32;
			double r = 0.08;
			double angleOffset = Math.PI / 4 * 5 - 0.06;

			start.z -= 0.08f;

			tempVector0 = new vector(0, 0, 0);
			tempVector1 = new vector(0, 0, 0);
			tempVector2 = new vector(0, 0, 0);
			tempVector3 = new vector(0, 0, 0);

			for (int i = 0; i < 18; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta + angleOffset), 0.04,
								r * Math.sin((i + 1) * theta + angleOffset)),
						createArbitraryVertex(r * Math.cos(i * theta + angleOffset), 0.04, r
								* Math.sin(i * theta + angleOffset)),
						createArbitraryVertex(r * Math.cos(i * theta + angleOffset), 0.09, r
								* Math.sin(i * theta + angleOffset)),
						createArbitraryVertex(r * Math.cos((i + 1) * theta + angleOffset), 0.09,
								r * Math.sin((i + 1) * theta + angleOffset)) };

				if (i == 0) {

					tempVector1 = v[2].myClone();
				}

				if (i == 17) {

					tempVector3 = v[3].myClone();
				}
				body[18 + i] = new polygon3D(v, v[0], v[1], v[3],
						MainThread.textures[upperBodyTExture], 1, 1, 1);

			}
			start.z += 0.08f;

			float the_x = tempVector1.x;
			float the_y = tempVector1.y;
			float the_z = tempVector1.z;

			v = new vector[] { new vector(the_x, the_y, the_z + 0.1f),
					tempVector1.myClone(),
					new vector(the_x, the_y - 0.08f, the_z),
					new vector(the_x, the_y - 0.08f, the_z + 0.1f) };
			body[36] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 1, 1, 1);
			tempVector0 = new vector(the_x, the_y, the_z + 0.1f);

			float the_x1 = tempVector3.x;
			float the_y1 = tempVector3.y;
			float the_z1 = tempVector3.z;

			v = new vector[] { tempVector3.myClone(),
					new vector(the_x1, the_y1, the_z + 0.1f),
					new vector(the_x1, the_y1 - 0.08f, the_z + 0.1f),
					new vector(the_x1, the_y1 - 0.08f, the_z1) };
			body[37] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 1, 1, 1);
			tempVector2 = new vector(the_x1, the_y1, the_z + 0.1f);

			start.z -= 0.08f;
			v = new vector[21];
			for (int i = 0; i < 19; i++) {
				v[i] = createArbitraryVertex(r * Math.cos((18 - i) * theta + angleOffset), 0.09,
						r * Math.sin((18 - i) * theta + angleOffset));
			}
			v[19] = tempVector0.myClone();
			v[20] = tempVector2.myClone();

			body[38] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);
			body[38].Ambient_I -= 11;
			body[38].shadowBias = 10000;

			start.z += 0.08f;

			v = new vector[] {
					tempVector2.myClone(),
					tempVector0.myClone(),
					new vector(tempVector0.x, tempVector0.y - 0.08f,
							tempVector0.z),
					new vector(tempVector2.x, tempVector2.y - 0.08f,
							tempVector2.z) };
			body[39] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);

			v = new vector[] {
					new vector(the_x1, -0.04f, tempVector0.z - 0.07f),
					new vector(the_x1, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1, the_y1, tempVector0.z),
					new vector(the_x1, the_y1, tempVector0.z - 0.08f) };
			body[40] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);

			v = new vector[] {
					new vector(the_x1 - 0.05f, the_y1, tempVector0.z - 0.08f),
					new vector(the_x1 - 0.05f, the_y1, tempVector0.z),
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.07f) };
			body[41] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);

			v = new vector[] {
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.07f),
					new vector(the_x1, -0.04f, tempVector0.z - 0.07f),
					new vector(the_x1, the_y1, tempVector0.z - 0.08f),
					new vector(the_x1 - 0.05f, the_y1, tempVector0.z - 0.08f) };
			body[42] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);

			v = new vector[] {
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1, -0.04f, tempVector0.z - 0.07f),
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.07f) };
			body[43] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 2, 2, 1);

			v = new vector[] {
					new vector(the_x1 + 0.001f, -0.045f, tempVector0.z - 0.05f),
					new vector(the_x1 + 0.001f, -0.045f, tempVector0.z - 0.02f),
					new vector(the_x1 + 0.001f, the_y1, tempVector0.z - 0.005f),
					new vector(the_x1 + 0.001f, the_y1 + 0.01f,
							tempVector0.z - 0.05f) };
			body[44] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[45], 2,
					2, 1);

			v = new vector[] {
					new vector(the_x1, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1 - 0.05f, -0.04f, tempVector0.z - 0.015f),
					new vector(the_x1 - 0.05f, the_y1, tempVector0.z),
					new vector(the_x1, the_y1, tempVector0.z) };
			body[45] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[46], 1,
					1, 1);

			v = new vector[] {
					new vector(the_x1 - 0.051f, the_y1 + 0.01f,
							tempVector0.z - 0.05f),
					new vector(the_x1 - 0.051f, the_y1, tempVector0.z - 0.005f),
					new vector(the_x1 - 0.051f, -0.045f, tempVector0.z - 0.02f),
					new vector(the_x1 - 0.051f, -0.045f, tempVector0.z - 0.05f) };
			body[46] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[45], 2,
					2, 1);

			angleOffset = Math.PI * 1.65;
			theta = Math.PI / 22;
			r = 0.02;

			float h = 0.11f;
			float l = -0.12f;

			for (int i = 0; i < 16; i++) {
				v = new vector[] {
						createArbitraryVertex(0.005f, r * Math.cos((i + 1) * theta + angleOffset)
								+ h,
								r * Math.sin((i + 1) * theta + angleOffset) + l),
						createArbitraryVertex(0.005f, r * Math.cos(i * theta + angleOffset) + h,
								r * Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.005, r * Math.cos(i * theta + angleOffset) + h,
								r * Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.005f,
								r * Math.cos((i + 1) * theta + angleOffset) + h,
								r * Math.sin((i + 1) * theta + angleOffset) + l) };

				if (i == 0) {
					the_x = v[2].x;
					the_y = v[2].y;
					the_z = v[2].z;
				}
				if (i == 15) {
					the_x1 = v[0].x;
					the_y1 = v[0].y;
					the_z1 = v[0].z;
				}

				body[47 + i] = new polygon3D(v, v[0], v[1], v[3],
						MainThread.textures[25], 1, 1, 1);
			}

			v = new vector[] { new vector(the_x, the_y, the_z),
					new vector(the_x + 0.01f, the_y, the_z),
					new vector(the_x + 0.01f, the_y - 0.08f, the_z),
					new vector(the_x, the_y - 0.08f, the_z) };
			body[63] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					10, 1);

			v = new vector[] { new vector(the_x1, the_y1, the_z1),
					new vector(the_x1 - 0.01f, the_y1, the_z1),
					new vector(the_x1 - 0.01f, the_y1 - 0.08f, the_z1 + 0.01f),
					new vector(the_x1, the_y1 - 0.08f, the_z1 + 0.01f) };
			body[64] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					10, 1);

			v = new vector[19];
			for (int i = 0; i < 17; i++) {
				v[i] = createArbitraryVertex(-0.005f,
						r * Math.cos((16 - i) * theta + angleOffset) + h, r
								* Math.sin((16 - i) * theta + angleOffset) + l);
			}
			v[17] = new vector(-0.005f, the_y - 0.08f, the_z);
			v[18] = new vector(-0.005f, the_y1 - 0.08f, the_z1 + 0.01f);
			body[65] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					1, 1);

			v = new vector[19];
			for (int i = 0; i < 19; i++) {
				v[i] = body[65].vertex3D[18 - i].myClone();
				v[i].x += 0.01f;
			}
			body[66] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					1, 1);

			start.x -= 0.05;

			for (int i = 0; i < 16; i++) {
				v = new vector[] {
						createArbitraryVertex(0.005f, r * Math.cos((i + 1) * theta + angleOffset)
								+ h,
								r * Math.sin((i + 1) * theta + angleOffset) + l),
						createArbitraryVertex(0.005f, r * Math.cos(i * theta + angleOffset) + h,
								r * Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.005, r * Math.cos(i * theta + angleOffset) + h,
								r * Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.005f,
								r * Math.cos((i + 1) * theta + angleOffset) + h,
								r * Math.sin((i + 1) * theta + angleOffset) + l) };

				if (i == 0) {
					the_x = v[2].x;
					the_y = v[2].y;
					the_z = v[2].z;
				}
				if (i == 15) {
					the_x1 = v[0].x;
					the_y1 = v[0].y;
					the_z1 = v[0].z;
				}

				body[67 + i] = new polygon3D(v, v[0], v[1], v[3],
						MainThread.textures[25], 1, 1, 1);
			}

			v = new vector[] { new vector(the_x, the_y, the_z),
					new vector(the_x + 0.01f, the_y, the_z),
					new vector(the_x + 0.01f, the_y - 0.08f, the_z),
					new vector(the_x, the_y - 0.08f, the_z) };
			body[83] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					10, 1);

			v = new vector[] { new vector(the_x1, the_y1, the_z1),
					new vector(the_x1 - 0.01f, the_y1, the_z1),
					new vector(the_x1 - 0.01f, the_y1 - 0.08f, the_z1 + 0.01f),
					new vector(the_x1, the_y1 - 0.08f, the_z1 + 0.01f) };
			body[84] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					10, 1);

			v = new vector[19];
			for (int i = 0; i < 17; i++) {
				v[i] = createArbitraryVertex(-0.005f,
						r * Math.cos((16 - i) * theta + angleOffset) + h, r
								* Math.sin((16 - i) * theta + angleOffset) + l);
			}
			v[17] = new vector(-0.055f, the_y - 0.08f, the_z);
			v[18] = new vector(-0.055f, the_y1 - 0.08f, the_z1 + 0.01f);
			body[85] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					1, 1);

			v = new vector[19];
			for (int i = 0; i < 19; i++) {
				v[i] = body[85].vertex3D[18 - i].myClone();
				v[i].x += 0.01f;
			}
			body[86] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[25], 1,
					1, 1);

			start.set(0, 0, 0);
			armCenter = new vector(-0.025f, -0.06000001f, -0.12f);

			arm = new polygon3D[33];
			v = new vector[] { createArbitraryVertex(-0.02f, 0.025f, 0.23),
					createArbitraryVertex(0.02f, 0.025f, 0.23), createArbitraryVertex(0.02f, 0.025f, -0.02),
					createArbitraryVertex(-0.02f, 0.025f, -0.02) };
			arm[0] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);
			arm[0].shadowBias = 100000;

			v = new vector[] { createArbitraryVertex(-0.02f, -0.015f, -0.02),
					createArbitraryVertex(0.02f, -0.015f, -0.02), createArbitraryVertex(0.02f, -0.015f, 0.23),
					createArbitraryVertex(-0.02f, -0.015f, 0.23) };
			arm[1] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);
			arm[1].shadowBias = 100000;

			v = new vector[] { createArbitraryVertex(0.02f, 0.025f, -0.02),
					createArbitraryVertex(0.02f, 0.025f, 0.23), createArbitraryVertex(0.02f, -0.015f, 0.23),
					createArbitraryVertex(0.02f, -0.015f, -0.02) };
			arm[2] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);
			arm[2].shadowBias = 100000;

			v = new vector[] { createArbitraryVertex(-0.02f, -0.015f, -0.02),
					createArbitraryVertex(-0.02f, -0.015f, 0.23), createArbitraryVertex(-0.02f, 0.025f, 0.23),
					createArbitraryVertex(-0.02f, 0.025f, -0.02) };
			arm[3] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);
			arm[3].shadowBias = 100000;

			r = 0.02f;
			theta = Math.PI / 16;
			angleOffset = Math.PI;
			h = 0.005f;
			l = -0.02f;

			for (int i = 0; i < 16; i++) {
				v = new vector[] {
						createArbitraryVertex(0.02f, r * Math.cos((i + 1) * theta + angleOffset)
								+ h,
								r * Math.sin((i + 1) * theta + angleOffset) + l),
						createArbitraryVertex(0.02f, r * Math.cos(i * theta + angleOffset) + h, r
								* Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.02f, r * Math.cos(i * theta + angleOffset) + h,
								r * Math.sin(i * theta + angleOffset) + l),
						createArbitraryVertex(-0.02f, r * Math.cos((i + 1) * theta + angleOffset)
								+ h,
								r * Math.sin((i + 1) * theta + angleOffset) + l) };

				arm[4 + i] = new polygon3D(v, v[0], v[1], v[3],
						MainThread.textures[upperBodyTExture], 10, 10, 1);
			}

			v = new vector[17];
			for (int i = 0; i < 17; i++) {
				v[i] = createArbitraryVertex(-0.02f, r * Math.cos((16 - i) * theta + angleOffset)
						+ h, r * Math.sin((16 - i) * theta + angleOffset) + l);
			}
			arm[20] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);

			v = new vector[17];
			for (int i = 0; i < 17; i++) {
				v[i] = arm[20].vertex3D[16 - i].myClone();
				v[i].x += 0.03f;
			}
			arm[21] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);

			v = new vector[] { createArbitraryVertex(0.02f, 0.025f, 0.23),
					createArbitraryVertex(-0.02f, 0.025f, 0.23), createArbitraryVertex(-0.02f, -0.015f, 0.23),
					createArbitraryVertex(0.02f, -0.015f, 0.23) };
			arm[22] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 10, 10, 1);

			v = new vector[] { createArbitraryVertex(-0.015f, 0.02f, 0.27),
					createArbitraryVertex(0.015f, 0.02f, 0.27), createArbitraryVertex(0.015f, 0.02f, 0.03),
					createArbitraryVertex(-0.015f, 0.02f, 0.03) };
			arm[23] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[47], 10,
					10, 1);

			v = new vector[] { createArbitraryVertex(-0.015f, -0.01f, 0.03),
					createArbitraryVertex(0.015f, -0.01f, 0.03), createArbitraryVertex(0.015f, -0.01f, 0.27),
					createArbitraryVertex(-0.015f, -0.01f, 0.27) };
			arm[24] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[47], 10,
					10, 1);

			v = new vector[] { createArbitraryVertex(0.015f, 0.02f, 0.03),
					createArbitraryVertex(0.015f, 0.02f, 0.27), createArbitraryVertex(0.015f, -0.01f, 0.27),
					createArbitraryVertex(0.015f, -0.01f, 0.03) };
			arm[25] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[47], 10,
					10, 1);

			v = new vector[] { createArbitraryVertex(-0.015f, -0.01f, 0.03),
					createArbitraryVertex(-0.015f, -0.01f, 0.27), createArbitraryVertex(-0.015f, 0.02f, 0.27),
					createArbitraryVertex(-0.015f, 0.02f, 0.03) };
			arm[26] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[47], 10,
					10, 1);

			v = new vector[] { createArbitraryVertex(-0.02f, 0.025f, 0.29),
					createArbitraryVertex(0.02f, 0.025f, 0.29), createArbitraryVertex(0.02f, 0.025f, 0.27),
					createArbitraryVertex(-0.02f, 0.025f, 0.27) };
			arm[27] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1.2f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.02f, 0.025f, 0.27),
					createArbitraryVertex(0.02f, 0.025f, 0.29), createArbitraryVertex(0.02f, 0f, 0.29),
					createArbitraryVertex(0.02f, -0.015f, 0.27) };
			arm[28] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1f, 1f, 1);

			v = new vector[] { createArbitraryVertex(-0.02f, -0.015f, 0.27),
					createArbitraryVertex(-0.02f, 0f, 0.29), createArbitraryVertex(-0.02f, 0.025f, 0.29),
					createArbitraryVertex(-0.02f, 0.025f, 0.27) };
			arm[29] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.02f, 0.025f, 0.29),
					createArbitraryVertex(-0.02f, 0.025f, 0.29), createArbitraryVertex(-0.02f, 0, 0.29),
					createArbitraryVertex(0.02f, 0, 0.29) };
			arm[30] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1.2f, 1f, 1);

			v = new vector[] { createArbitraryVertex(-0.02f, 0.025f, 0.27),
					createArbitraryVertex(0.02f, 0.025f, 0.27), createArbitraryVertex(0.02f, -0.015f, 0.27),
					createArbitraryVertex(-0.02f, -0.015f, 0.27) };
			arm[31] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1.2f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.02f, 0, 0.29), createArbitraryVertex(-0.02f, 0, 0.29),
					createArbitraryVertex(-0.02f, -0.015f, 0.27), createArbitraryVertex(0.02f, -0.015f, 0.27) };
			arm[32] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[armTop],
					1.2f, 1f, 1);

			start.set(0, 0, 0);
			pillarCenter = new vector(-0.025f, -0.09000001f, -0.05f);
			pillar = new polygon3D[49];

			theta = Math.PI / 12;
			r = 0.01;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta),
								r * Math.sin((i + 1) * theta), 0.08),
						createArbitraryVertex(r * Math.cos(i * theta), r * Math.sin(i * theta),
								0.08),
						createArbitraryVertex(r * Math.cos(i * theta), r * Math.sin(i * theta), 0),
						createArbitraryVertex(r * Math.cos((i + 1) * theta),
								r * Math.sin((i + 1) * theta), 0),

				};
				pillar[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(),
						v[3].myClone(), MainThread.textures[upperBodyTExture], 4f,
						4f, 1);
			}

			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[i] = createArbitraryVertex(r * Math.cos(i * theta), r * Math.sin(i * theta),
						0.08);
			}
			pillar[24] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 4f, 4f, 1);

			r = 0.005;
			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta),
								r * Math.sin((i + 1) * theta), 0.18),
						createArbitraryVertex(r * Math.cos(i * theta), r * Math.sin(i * theta),
								0.18),
						createArbitraryVertex(r * Math.cos(i * theta), r * Math.sin(i * theta),
								0.08),
						createArbitraryVertex(r * Math.cos((i + 1) * theta),
								r * Math.sin((i + 1) * theta), 0.08),

				};
				pillar[25 + i] = new polygon3D(v, v[0].myClone(),
						v[1].myClone(), v[3].myClone(), MainThread.textures[29], 4f,
						4f, 1);
			}

			// foot 1
			foot1 = new polygon3D[53];
			theta = Math.PI / 12;
			r = 0.01;
			float w = 0.08f;
			l = 0.06f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.16,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.16,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.13,
								r * Math.sin((i + 1) * theta) + l), };
				foot1[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(),
						v[3].myClone(), MainThread.textures[upperBodyTExture], 4f,
						4f, 1);
			}
			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
						r * Math.sin(i * theta) + l);
			}
			foot1[24] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 4f, 4f, 1);

			r = 0.006f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.17,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.131,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.131,
								r * Math.sin((i + 1) * theta) + l), };
				foot1[i + 25] = new polygon3D(v, v[0].myClone(),
						v[1].myClone(), v[3].myClone(), MainThread.textures[29], 4f,
						4f, 1);
			}

			theta = Math.PI / 12;
			r = 0.014;

			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
						r * Math.sin(i * theta) + l);
			}
			foot1[49] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[33],
					4f, 4f, 1);
			foot1[49].shadowBias = 10000;

			start.x -= 0.08;

			v = new vector[] { createArbitraryVertex(0.0, -0.14, 0.065), createArbitraryVertex(0.15, -0.14, 0.065),
					createArbitraryVertex(0.15, -0.14, 0.055), createArbitraryVertex(0.0, -0.14, 0.055) };
			foot1[50] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[26],
					4f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.14, 0.065), createArbitraryVertex(0.0, -0.14, 0.065),
					createArbitraryVertex(0.0, -0.16, 0.065), createArbitraryVertex(0.15, -0.16, 0.065) };
			foot1[51] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.16, 0.055), createArbitraryVertex(0.0, -0.16, 0.055),
					createArbitraryVertex(0.0, -0.14, 0.055), createArbitraryVertex(0.15, -0.14, 0.055) };
			foot1[52] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			// foot 2
			start.set(0, 0, 0);
			foot2 = new polygon3D[53];
			theta = Math.PI / 12;
			r = 0.01;
			w = 0.08f;
			l = -0.12f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.16,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.16,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.13,
								r * Math.sin((i + 1) * theta) + l), };
				foot2[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(),
						v[3].myClone(), MainThread.textures[upperBodyTExture], 4f,
						4f, 1);
			}
			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
						r * Math.sin(i * theta) + l);
			}
			foot2[24] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 4f, 4f, 1);

			r = 0.006f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.17,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.131,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.131,
								r * Math.sin((i + 1) * theta) + l), };
				foot2[i + 25] = new polygon3D(v, v[0].myClone(),
						v[1].myClone(), v[3].myClone(), MainThread.textures[29], 4f,
						4f, 1);
			}

			theta = Math.PI / 12;
			r = 0.014;

			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
						r * Math.sin(i * theta) + l);
			}
			foot2[49] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[33],
					4f, 4f, 1);
			foot2[49].shadowBias = 10000;

			start.x -= 0.08;
			start.z -= 0.18;

			v = new vector[] { createArbitraryVertex(0.0, -0.14, 0.065), createArbitraryVertex(0.15, -0.14, 0.065),
					createArbitraryVertex(0.15, -0.14, 0.055), createArbitraryVertex(0.0, -0.14, 0.055) };
			foot2[50] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[26],
					4f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.14, 0.065), createArbitraryVertex(0.0, -0.14, 0.065),
					createArbitraryVertex(0.0, -0.16, 0.065), createArbitraryVertex(0.15, -0.16, 0.065) };
			foot2[51] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.16, 0.055), createArbitraryVertex(0.0, -0.16, 0.055),
					createArbitraryVertex(0.0, -0.14, 0.055), createArbitraryVertex(0.15, -0.14, 0.055) };
			foot2[52] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			// foot 3
			start.set(0, 0, 0);
			foot3 = new polygon3D[53];
			theta = Math.PI / 12;
			r = 0.01;
			w = -0.08f;
			l = 0.06f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.16,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.16,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.13,
								r * Math.sin((i + 1) * theta) + l), };
				foot3[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(),
						v[3].myClone(), MainThread.textures[upperBodyTExture], 4f,
						4f, 1);
			}
			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
						r * Math.sin(i * theta) + l);
			}
			foot3[24] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 4f, 4f, 1);

			r = 0.006f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.17,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.131,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.131,
								r * Math.sin((i + 1) * theta) + l), };
				foot3[i + 25] = new polygon3D(v, v[0].myClone(),
						v[1].myClone(), v[3].myClone(), MainThread.textures[29], 4f,
						4f, 1);
			}

			theta = Math.PI / 12;
			r = 0.014;

			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
						r * Math.sin(i * theta) + l);
			}
			foot3[49] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[33],
					4f, 4f, 1);
			foot3[49].shadowBias = 10000;

			start.x -= 0.08;

			v = new vector[] { createArbitraryVertex(0.0, -0.14, 0.065), createArbitraryVertex(0.15, -0.14, 0.065),
					createArbitraryVertex(0.15, -0.14, 0.055), createArbitraryVertex(0.0, -0.14, 0.055) };
			foot3[50] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[26],
					4f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.14, 0.065), createArbitraryVertex(0.0, -0.14, 0.065),
					createArbitraryVertex(0.0, -0.16, 0.065), createArbitraryVertex(0.15, -0.16, 0.065) };
			foot3[51] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.16, 0.055), createArbitraryVertex(0.0, -0.16, 0.055),
					createArbitraryVertex(0.0, -0.14, 0.055), createArbitraryVertex(0.15, -0.14, 0.055) };
			foot3[52] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			// foot 4
			start.set(0, 0, 0);
			foot4 = new polygon3D[53];
			theta = Math.PI / 12;
			r = 0.01;
			w = -0.08f;
			l = -0.12f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.16,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.16,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.13,
								r * Math.sin((i + 1) * theta) + l), };
				foot4[i] = new polygon3D(v, v[0].myClone(), v[1].myClone(),
						v[3].myClone(), MainThread.textures[upperBodyTExture], 4f,
						4f, 1);
			}
			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.13,
						r * Math.sin(i * theta) + l);
			}
			foot4[24] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[upperBodyTExture], 4f, 4f, 1);

			r = 0.006f;

			for (int i = 0; i < 24; i++) {
				v = new vector[] {
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.17,
								r * Math.sin((i + 1) * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos(i * theta) + w, -0.131,
								r * Math.sin(i * theta) + l),
						createArbitraryVertex(r * Math.cos((i + 1) * theta) + w, -0.131,
								r * Math.sin((i + 1) * theta) + l), };
				foot4[i + 25] = new polygon3D(v, v[0].myClone(),
						v[1].myClone(), v[3].myClone(), MainThread.textures[29], 4f,
						4f, 1);
			}

			theta = Math.PI / 12;
			r = 0.014;

			v = new vector[24];
			for (int i = 0; i < 24; i++) {
				v[23 - i] = createArbitraryVertex(r * Math.cos(i * theta) + w, -0.17,
						r * Math.sin(i * theta) + l);
			}
			foot4[49] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[33],
					4f, 4f, 1);
			foot4[49].shadowBias = 10000;

			start.x -= 0.08;
			start.z -= 0.18;

			v = new vector[] { createArbitraryVertex(0.0, -0.14, 0.065), createArbitraryVertex(0.15, -0.14, 0.065),
					createArbitraryVertex(0.15, -0.14, 0.055), createArbitraryVertex(0.0, -0.14, 0.055) };
			foot4[50] = new polygon3D(v, v[0], v[1], v[3], MainThread.textures[26],
					4f, 1f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.14, 0.065), createArbitraryVertex(0.0, -0.14, 0.065),
					createArbitraryVertex(0.0, -0.16, 0.065), createArbitraryVertex(0.15, -0.16, 0.065) };
			foot4[51] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

			v = new vector[] { createArbitraryVertex(0.15, -0.16, 0.055), createArbitraryVertex(0.0, -0.16, 0.055),
					createArbitraryVertex(0.0, -0.14, 0.055), createArbitraryVertex(0.15, -0.14, 0.055) };
			foot4[52] = new polygon3D(v, v[0], v[1], v[3],
					MainThread.textures[armTop], 4f, 0.5f, 1);

		}

		bodyClone = clonePolygons(body, false);

		armCenterClone = new vector(0, 0, 0);
		armClone = clonePolygons(arm, false);

		pillarCenterClone = new vector(0, 0, 0);
		pillarClone = clonePolygons(pillar, false);

		foot1Clone = clonePolygons(foot1, false);
		foot2Clone = clonePolygons(foot2, false);
		foot3Clone = clonePolygons(foot3, false);
		foot4Clone = clonePolygons(foot4, false);

		if (teamNo != 0) {
			for (int i = 0; i < body.length; i++) {
				if (body[i].myTexture.ID == 42)
					bodyClone[i].myTexture = MainThread.textures[10];

				if (body[i].myTexture.ID == upperBodyTExture)
					bodyClone[i].myTexture = MainThread.textures[48];

				if (body[i].myTexture.ID == 46)
					bodyClone[i].myTexture = MainThread.textures[50];
			}

			for (int i = 0; i < foot1.length; i++) {
				if (foot1[i].myTexture.ID == upperBodyTExture) {
					foot1Clone[i].myTexture = MainThread.textures[48];
					foot2Clone[i].myTexture = MainThread.textures[48];
					foot3Clone[i].myTexture = MainThread.textures[48];
					foot4Clone[i].myTexture = MainThread.textures[48];

				}
				if (foot1[i].myTexture.ID == armTop) {
					foot1Clone[i].myTexture = MainThread.textures[49];
					foot2Clone[i].myTexture = MainThread.textures[49];
					foot3Clone[i].myTexture = MainThread.textures[49];
					foot4Clone[i].myTexture = MainThread.textures[49];
				}
			}

			for (int i = 0; i < arm.length; i++) {
				if (arm[i].myTexture.ID == upperBodyTExture)
					armClone[i].myTexture = MainThread.textures[48];

				if (armClone[i].myTexture.ID == armTop)
					armClone[i].myTexture = MainThread.textures[49];
			}

			for (int i = 0; i < pillar.length; i++) {
				if (pillar[i].myTexture.ID == upperBodyTExture) {
					pillarClone[i].myTexture = MainThread.textures[48];

				}
			}

		}
	}

	// update the model
	public void update() {
		// check if the Harvester has finished deploying
		if (footExtendCount == 180) {
			theAssetManager.removeObject(this);
			removeFromGridMap();
			currentHP = 0;
			if (isSelected) {
				MainThread.playerCommander.addToSelection(myConstructionYard);
			}
			return;
		}

		// check if Harvester has been destroyed
		if (currentHP <= 0) {
			// spawn an Explosion when the tank is destroyed
			float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];
			tempFloat[0] = centre.x;
			tempFloat[1] = centre.y - 0.05f;
			tempFloat[2] = centre.z;
			tempFloat[3] = 2.5f;
			tempFloat[4] = 1;
			tempFloat[5] = 0;
			tempFloat[6] = 7;
			tempFloat[7] = this.height;
			theAssetManager.explosionCount++;
			theAssetManager.removeObject(this);
			removeFromGridMap();
			if(attacker.teamNo != teamNo)
				attacker.experience+=30;

			if (jobStatus == deploying) {
				myConstructionYard.currentHP = 0;
				myConstructionYard.countDownToDeath = -1;
			}

			return;
		}

		if (jobStatus == deploying && footExtendCount == 0) {
			movement.reset();
			footExtendCount = 1;
		}

		if (footExtendCount > 0) {
			footExtendCount++;
		}

		if (footExtendCount == 80)
			openArmCount = 60;

		if (openArmCount > 1) {
			openArmCount--;
		}

		if (openArmCount == 1 && extendArmCount < 30)
			extendArmCount++;

		// carry out commands given by the player or AI
		if (jobStatus != deploying && !disableUnitLevelAI)
			carryOutCommands();

		if (tightSpaceManeuverCountDown > 0)
			tightSpaceManeuverCountDown--;

		if (heuristicRecalculationCountDown > 0)
			heuristicRecalculationCountDown--;
		
		if(underAttackCountDown > 0)
			underAttackCountDown--;

		// update centre
		if (Math.abs(movement.x) + Math.abs(movement.z) < 0.25f) {
			centre.add(movement);
			boundary2D.setOrigin((int) (centre.x * 64) - 8,
					(int) (centre.z * 64) + 8);
			updateOccupiedTiles(boundary2D.x1, boundary2D.y1);
		} else {
			movement.reset();
		}

		// update center in Camera coordinate
		tempCentre.set(centre);
		tempCentre.y -= 0.2f;
		tempCentre.subtract(Camera.position);
		tempCentre.rotate_XZ(Camera.XZ_angle);
		tempCentre.rotate_YZ(Camera.YZ_angle);
		tempCentre.updateLocation();

		visionBoundary.x = (int) (tempCentre.screenX - visionW);
		visionBoundary.y = (int) (tempCentre.screenY - visionH);
		visionInsideScreen = Camera.screen.intersects(visionBoundary);

		// create vision for enemy commander
		if (teamNo == 1) {
			xPos = boundary2D.x1 / 16 - 6 + 10;
			yPos = 127 - boundary2D.y1 / 16 - 6 + 10;
			for (int y = 0; y < 13; y++) {
				for (int x = 0; x < 13; x++) {
					if (bitmapVisionForEnemy[x + y * 13])
						EnemyCommander.tempBitmap[xPos + x + (yPos + y) * 148] = true;
				}
			}
		}

		if (visionInsideScreen && teamNo == 0) {
			tempFloat = theAssetManager.visionPolygonInfo[theAssetManager.visionPolygonCount];
			tempFloat[0] = teamNo;
			tempFloat[1] = centre.x;
			tempFloat[2] = -0.4f;
			tempFloat[3] = centre.z;
			tempFloat[4] = 0;
			theAssetManager.visionPolygonCount++;
		}

		// check if the tank object is visible in mini map
		visible_minimap = theAssetManager.minimapBitmap[boundary2D.x1 / 16
				+ (127 - boundary2D.y1 / 16) * 128];
		if (teamNo == 0 || visible_minimap) {
			tempInt = theAssetManager.unitsForMiniMap[theAssetManager.unitsForMiniMapCount];
			tempInt[0] = teamNo;
			tempInt[1] = boundary2D.x1 / 16;
			tempInt[2] = 127 - boundary2D.y1 / 16;
			tempInt[3] = 0;
			if(teamNo == 0 && underAttackCountDown > 0)
				tempInt[4] = 10001;
			else
				tempInt[4] = 0;
			theAssetManager.unitsForMiniMapCount++;
		}

		// test if the tank object is visible in Camera point of view
		if (visible_minimap) {
			if (currentHP <= maxHP / 2 && (MainThread.gameFrame + ID) % 3 == 0) {
				// spawn smoke particle if the unit is badly damaged
				float[] tempFloat = theAssetManager.smokeEmmiterList[theAssetManager.smokeEmmiterCount];
				tempFloat[0] = centre.x + (float) (Math.random() / 20) - 0.025f;
				tempFloat[1] = centre.y - 0.06f;
				tempFloat[2] = centre.z + (float) (Math.random() / 20) - 0.025f;
				tempFloat[3] = 0.7f;
				tempFloat[4] = 1;
				tempFloat[5] = 11;
				tempFloat[6] = this.height;
				theAssetManager.smokeEmmiterCount++;
			}

			if (visibleBoundary
					.contains(tempCentre.screenX, tempCentre.screenY)) {
				visible = true;
				if (screenBoundary.contains(tempCentre.screenX,
						tempCentre.screenY))
					withinViewScreen = true;
				else
					withinViewScreen = false;
			} else {
				visible = false;

			}
		} else {
			MainThread.playerCommander.deSelect(this);
			visible = false;
		}

		if (visible) {
			updateGeometry();

			for (int i = 0; i < bodyClone.length; i++) {
				bodyClone[i].update_lightspace();
			}

			for (int i = 0; i < armClone.length; i++) {
				armClone[i].update_lightspace();
			}

			for (int i = 0; i < pillarClone.length; i++) {
				pillarClone[i].update_lightspace();
			}

			for (int i = 0; i < foot1Clone.length; i++) {
				foot1Clone[i].update_lightspace();
			}

			for (int i = 0; i < foot2Clone.length; i++) {
				foot2Clone[i].update_lightspace();
			}

			for (int i = 0; i < foot3Clone.length; i++) {
				foot3Clone[i].update_lightspace();
			}

			for (int i = 0; i < foot4Clone.length; i++) {
				foot4Clone[i].update_lightspace();
			}

			theAssetManager.visibleUnit[theAssetManager.visibleUnitCount] = this;
			theAssetManager.visibleUnitCount++;

		}
	}

	// carry out commands given by player or AI commander
	public void carryOutCommands() {
		if (currentCommand == StandBy) {
			resetLogicStatus();
			jobStatus = idle;

		} else if (currentCommand == move) {
			performPathFindingLogic();
		}
	}

	// use a path finder to move to desination
	public void performPathFindingLogic() {

		if (!pathIsFound && heuristicRecalculationCountDown == 0) {

			int destX = (int) (destinationX * 64) / 16;
			int destY = 127 - (int) (destinationY * 64) / 16;

			pathIsFound = PathFinder.createHeuristicMap(heuristicMap,
					occupiedTile0, occupiedTile1, occupiedTile2, occupiedTile3,
					destX, destY);
			heuristicRecalculationCountDown = 32;

			if (pathIsFound) {
				// find the first node in the path
				int nextTile0 = findAdjacentTileWithSmallestHeuristic(occupiedTile0);
				int nextTile1 = findAdjacentTileWithSmallestHeuristic(occupiedTile1);
				int nextTile2 = findAdjacentTileWithSmallestHeuristic(occupiedTile2);
				int nextTile3 = findAdjacentTileWithSmallestHeuristic(occupiedTile3);

				if (occupiedTile1 == -1)
					nextTile1 = occupiedTile1;
				if (occupiedTile2 == -1)
					nextTile2 = occupiedTile2;
				if (occupiedTile3 == -1)
					nextTile3 = occupiedTile3;

				if (nextTile0 != occupiedTile0) {
					nextNodeX = 0.125f + (nextTile0 % 128) * 0.25f;
					nextNodeY = 0.125f + (127 - (nextTile0 / 128)) * 0.25f;

				} else if (nextTile1 != occupiedTile1) {
					nextNodeX = 0.125f + (nextTile1 % 128) * 0.25f;
					nextNodeY = 0.125f + (127 - (nextTile1 / 128)) * 0.25f;

				} else if (nextTile2 != occupiedTile2) {
					nextNodeX = 0.125f + (nextTile2 % 128) * 0.25f;
					nextNodeY = 0.125f + (127 - (nextTile2 / 128)) * 0.25f;

				} else if (nextTile3 != occupiedTile3) {
					nextNodeX = 0.125f + (nextTile3 % 128) * 0.25f;
					nextNodeY = 0.125f + (127 - (nextTile3 / 128)) * 0.25f;
				}
			}
		}

		if (pathIsFound) {

			movement.reset();

			// check if the Harvester has reached next node in the path
			if (centre.x == nextNodeX && centre.z == nextNodeY) {
				// check if the Harvester has reached the destination
				int destX = (int) (destinationX * 64) / 16;
				int destY = 127 - (int) (destinationY * 64) / 16;
				int nodeX = (int) (centre.x * 64) / 16;
				int nodeY = 127 - (int) (centre.z * 64) / 16;
				if (destX == nodeX && destY == nodeY) {
					pathIsFound = false;
					resetLogicStatus();
					currentCommand = StandBy;
					return;
				} else {
					// if destination hasn't reached, find the next node
					int nextTile0 = findAdjacentTileWithSmallestHeuristic(occupiedTile0);
					nextNodeX = 0.125f + (nextTile0 % 128) * 0.25f;
					nextNodeY = 0.125f + (127 - (nextTile0 / 128)) * 0.25f;
				}
			}

			float distanceToNextNode = (float) Math.sqrt((nextNodeX - centre.x)
					* (nextNodeX - centre.x) + (nextNodeY - centre.z)
					* (nextNodeY - centre.z));
			calculateMovement();
			destinationAngle = Geometry.findAngle(centre.x, centre.z,
					nextNodeX, nextNodeY);
			immediateDestinationAngle = destinationAngle;

			if (Math.abs(bodyAngle - immediateDestinationAngle) > 45
					&& Math.abs(bodyAngle - immediateDestinationAngle) < 315) {

				int bodyAngleDelta = 360 - (Geometry.findAngleDelta(bodyAngle,
						immediateDestinationAngle, bodyTurnRate) + 360) % 360;
				bodyAngle = (bodyAngle - bodyAngleDelta + 360) % 360;
				movement.reset();

			} else {
				if (bodyAngle != immediateDestinationAngle) {
					int bodyAngleDelta = 360 - (Geometry.findAngleDelta(
							bodyAngle, immediateDestinationAngle, bodyTurnRate) + 360) % 360;
					bodyAngle = (bodyAngle - bodyAngleDelta + 360) % 360;
				}

				movement.set(nextNodeX - centre.x, 0, nextNodeY - centre.z);

				if (speed < distanceToNextNode) {
					movement.unit();
					movement.scale(speed);
				}

				// check collision
				xPos_old = boundary2D.x1;
				yPos_old = boundary2D.y1;
				xPos = (int) ((centre.x + movement.x) * 64) - 8;
				yPos = (int) ((centre.z + movement.z) * 64) + 8;
				boundary2D.setOrigin(xPos, yPos);

				Rect r = checkForCollision(boundary2D);
				boundary2D.setOrigin(xPos_old, yPos_old);

				if (r != null) {
					movement.reset();
					pathIsFound = false;

				}
			}

			return;
		}

		if (!pathIsFound) {
			if ((movement.x == 0 && movement.z == 0)
					|| MainThread.gridMap.tiles[occupiedTile0][4] != null) {
				if ((Math.abs(destinationX - centre.x)
						+ Math.abs(destinationY - centre.z) > 0.5)
						|| (jobStatus == idle)) {
					heuristicRecalculationCountDown = 64;
				}
			}
			performMovementLogic();
			avoidGettingStucked();

		}

	}

	public int findAdjacentTileWithSmallestHeuristic(int currentTile) {
		int smallestHeurstic = 127;
		int nextTile = currentTile;

		boolean[] obstacleMap = MainThread.gridMap.previousObstacleMap;

		// check north west tile
		int northWestTile = currentTile - 128 - 1;
		int northTile = currentTile - 128;
		int northEastTile = currentTile - 128 + 1;
		int eastTile = currentTile + 1;
		int southEastTile = currentTile + 1 + 128;
		int southTile = currentTile + 128;
		int southWestTile = currentTile + 128 - 1;
		int westTile = currentTile - 1;

		if (northWestTile > 0 && northWestTile < 16384
				&& obstacleMap[northTile] && obstacleMap[westTile]) {
			if (heuristicMap[northWestTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[northWestTile];
				nextTile = northWestTile;
			}
		}

		// check north tile

		if (northTile > 0 && northTile < 16384) {
			if (heuristicMap[northTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[northTile];
				nextTile = northTile;
			}
		}

		// check north east tile
		if (northEastTile > 0 && northEastTile < 16384
				&& obstacleMap[northTile] && obstacleMap[eastTile]) {
			if (heuristicMap[northEastTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[northEastTile];
				nextTile = northEastTile;
			}
		}

		// check east tile

		if (eastTile > 0 && eastTile < 16384) {
			if (heuristicMap[eastTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[eastTile];
				nextTile = eastTile;
			}
		}

		// check south east tile

		if (southEastTile > 0 && southEastTile < 16384
				&& obstacleMap[southTile] && obstacleMap[eastTile]) {
			if (heuristicMap[southEastTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[southEastTile];
				nextTile = southEastTile;
			}
		}

		// check south tile

		if (southTile > 0 && southTile < 16384) {
			if (heuristicMap[southTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[southTile];
				nextTile = southTile;
			}
		}

		// check south west tile

		if (southWestTile > 0 && southWestTile < 16384
				&& obstacleMap[southTile] && obstacleMap[westTile]) {
			if (heuristicMap[southWestTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[southWestTile];
				nextTile = southWestTile;
			}
		}

		// check west tile
		if (westTile > 0 && westTile < 16384) {
			if (heuristicMap[westTile] < smallestHeurstic) {
				smallestHeurstic = heuristicMap[westTile];
				nextTile = westTile;
			}
		}

		return nextTile;
	}

	// move to a destination position, ignore any hostile units it encounters
	public void performMovementLogic() {

		// clear things a bit
		unStableObstacle = null;

		if (newDestinationisGiven) {
			newDestinationisGiven = false;

			distanceToDesination = (float) Math.sqrt((destinationX - centre.x)
					* (destinationX - centre.x) + (destinationY - centre.z)
					* (destinationY - centre.z));
			calculateMovement();
			destinationAngle = Geometry.findAngle(centre.x, centre.z,
					destinationX, destinationY);
			immediateDestinationAngle = destinationAngle;
		}

		if (Math.abs(bodyAngle - immediateDestinationAngle) > 45
				&& Math.abs(bodyAngle - immediateDestinationAngle) < 315) {

			int bodyAngleDelta = 360 - (Geometry.findAngleDelta(bodyAngle,
					immediateDestinationAngle, bodyTurnRate) + 360) % 360;
			bodyAngle = (bodyAngle - bodyAngleDelta + 360) % 360;
			movement.reset();

		} else {
			if (bodyAngle != immediateDestinationAngle) {
				int bodyAngleDelta = 360 - (Geometry.findAngleDelta(bodyAngle,
						immediateDestinationAngle, bodyTurnRate) + 360) % 360;
				bodyAngle = (bodyAngle - bodyAngleDelta + 360) % 360;
			}

			if (currentMovementStatus == hugRight
					|| currentMovementStatus == hugLeft) {
				if (checkIfDestinationReached() == true) {
					movement.reset();
					currentCommand = StandBy;
					secondaryCommand = StandBy;
					return;
				}
				hugWalls();

				return;
			}

			if (movement.x == 0 && movement.z == 0)
				calculateMovement();
			if (distanceToDesination - speed <= 0) {
				movement.scale(speed - distanceToDesination);
				// validate movement
				currentMovementStatus = validateMovement();

				if (currentMovementStatus == freeToMove) {
					resetLogicStatus();
					currentCommand = StandBy;
					secondaryCommand = StandBy;
				} else {

					movement.reset();

				}
			} else {
				// validate movement
				currentMovementStatus = validateMovement();

				if (currentMovementStatus == freeToMove) {
					distanceToDesination -= speed;
				} else {
					movement.reset();

				}
			}
		}
	}

	public void avoidGettingStucked() {
		// if the object can't move for some period then recalculate the path
		if (movement.x == 0 && movement.z == 0) {
			stuckCount++;
		}

		if (obstacle != null) {
			if ((unStableObstacle != null || !isStable(obstacle.owner))
					&& (ID + randomNumber + MainThread.gameFrame) % 128 == 0) {
				newDestinationisGiven = true;
				currentMovementStatus = freeToMove;
				hugWallCoolDown = 0;
				stuckCount = 0;
				randomNumber = GameData.getRandom();
			}
		}

		if (stuckCount > 128) {
			newDestinationisGiven = true;
			stuckCount = 0;
			currentMovementStatus = freeToMove;
			hugWallCoolDown = 0;

		}
	}

	public void draw() {
		if (!visible)
			return;

		for (int i = 0; i < bodyClone.length; i++) {
			bodyClone[i].update();
			bodyClone[i].draw();
		}

		for (int i = 0; i < armClone.length; i++) {
			armClone[i].update();
			armClone[i].draw();
		}

		for (int i = 0; i < pillarClone.length; i++) {
			pillarClone[i].update();
			pillarClone[i].draw();
		}

		for (int i = 0; i < foot1Clone.length; i++) {
			foot1Clone[i].update();
			foot1Clone[i].draw();
		}

		for (int i = 0; i < foot2Clone.length; i++) {
			foot2Clone[i].update();
			foot2Clone[i].draw();
		}

		for (int i = 0; i < foot3Clone.length; i++) {
			foot3Clone[i].update();
			foot3Clone[i].draw();
		}

		for (int i = 0; i < foot4Clone.length; i++) {
			foot4Clone[i].update();
			foot4Clone[i].draw();
		}
	}

	public vector getMovement() {
		return movement;
	}

	public void updateGeometry() {

		for (int i = 0; i < bodyClone.length; i++) {
			bodyClone[i].origin.set(body[i].origin);

			bodyClone[i].origin.rotate_XZ(360 - bodyAngle);
			bodyClone[i].origin.add(centre);

			bodyClone[i].bottomEnd.set(body[i].bottomEnd);

			bodyClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			bodyClone[i].bottomEnd.add(centre);

			bodyClone[i].rightEnd.set(body[i].rightEnd);
			bodyClone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			bodyClone[i].rightEnd.add(centre);

			for (int j = 0; j < bodyClone[i].vertex3D.length; j++) {
				bodyClone[i].vertex3D[j].set(body[i].vertex3D[j]);
				bodyClone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				bodyClone[i].vertex3D[j].add(centre);

				bodyClone[i].normal.set(body[i].normal);
				bodyClone[i].normal.rotate_XZ(360 - bodyAngle);
				bodyClone[i].findDiffuse();
			}
		}

		// update arm center
		armCenterClone.set(armCenter);
		armCenterClone.rotate_XZ(360 - bodyAngle);
		armCenterClone.add(centre);

		if (openArmCount == 0)
			armAngle = 0;
		else
			armAngle = 360 - (60 - openArmCount);

		for (int i = 0; i < armClone.length; i++) {
			armClone[i].origin.set(arm[i].origin);
			if (i > 22)
				armClone[i].origin.z += (0.006f * extendArmCount);

			armClone[i].origin.rotate_YZ(armAngle);
			armClone[i].origin.rotate_XZ(360 - bodyAngle);
			armClone[i].origin.add(armCenterClone);

			armClone[i].bottomEnd.set(arm[i].bottomEnd);
			if (i > 22)
				armClone[i].bottomEnd.z += (0.006f * extendArmCount);
			armClone[i].bottomEnd.rotate_YZ(armAngle);
			armClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			armClone[i].bottomEnd.add(armCenterClone);

			armClone[i].rightEnd.set(arm[i].rightEnd);
			if (i > 22)
				armClone[i].rightEnd.z += (0.006f * extendArmCount);
			armClone[i].rightEnd.rotate_YZ(armAngle);
			armClone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			armClone[i].rightEnd.add(armCenterClone);

			for (int j = 0; j < armClone[i].vertex3D.length; j++) {
				armClone[i].vertex3D[j].set(arm[i].vertex3D[j]);
				if (i > 22)
					armClone[i].vertex3D[j].z += (0.006f * extendArmCount);
				armClone[i].vertex3D[j].rotate_YZ(armAngle);
				armClone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				armClone[i].vertex3D[j].add(armCenterClone);

				armClone[i].normal.set(arm[i].normal);
				armClone[i].normal.rotate_YZ(armAngle);
				armClone[i].normal.rotate_XZ(360 - bodyAngle);
				armClone[i].findDiffuse();
			}
		}

		// update pillar center
		pillarCenterClone.set(pillarCenter);
		pillarCenterClone.rotate_XZ(360 - bodyAngle);
		pillarCenterClone.add(centre);

		if (openArmCount == 0)
			pillarAngle = 350;
		else
			pillarAngle = 350 - (int) ((60 - openArmCount) * 1.3);

		for (int i = 0; i < pillarClone.length; i++) {
			pillarClone[i].origin.set(pillar[i].origin);
			pillarClone[i].origin.rotate_YZ(pillarAngle);
			pillarClone[i].origin.rotate_XZ(360 - bodyAngle);
			pillarClone[i].origin.add(pillarCenterClone);

			pillarClone[i].bottomEnd.set(pillar[i].bottomEnd);
			pillarClone[i].bottomEnd.rotate_YZ(pillarAngle);
			pillarClone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			pillarClone[i].bottomEnd.add(pillarCenterClone);

			pillarClone[i].rightEnd.set(pillar[i].rightEnd);
			pillarClone[i].rightEnd.rotate_YZ(pillarAngle);
			pillarClone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			pillarClone[i].rightEnd.add(pillarCenterClone);

			for (int j = 0; j < pillarClone[i].vertex3D.length; j++) {
				pillarClone[i].vertex3D[j].set(pillar[i].vertex3D[j]);
				pillarClone[i].vertex3D[j].rotate_YZ(pillarAngle);
				pillarClone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				pillarClone[i].vertex3D[j].add(pillarCenterClone);

				pillarClone[i].normal.set(pillar[i].normal);
				pillarClone[i].normal.rotate_YZ(pillarAngle);
				pillarClone[i].normal.rotate_XZ(360 - bodyAngle);
				pillarClone[i].findDiffuse();
			}
		}

		float footExtendDistance = footExtendCount * 0.002f;
		if (footExtendDistance > 0.08)
			footExtendDistance = 0.08f;

		float footDownDistance = 0;
		if (footExtendDistance == 0.08f)
			footDownDistance = (footExtendCount - 40) * 0.001f;
		if (footDownDistance > 0.03)
			footDownDistance = 0.03f;

		// update foot1
		for (int i = 0; i < foot1Clone.length; i++) {
			foot1Clone[i].origin.set(foot1[i].origin);
			foot1Clone[i].origin.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot1Clone[i].origin.y -= footDownDistance;
			foot1Clone[i].origin.rotate_XZ(360 - bodyAngle);
			foot1Clone[i].origin.add(centre);

			foot1Clone[i].bottomEnd.set(foot1[i].bottomEnd);
			foot1Clone[i].bottomEnd.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot1Clone[i].bottomEnd.y -= footDownDistance;
			foot1Clone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			foot1Clone[i].bottomEnd.add(centre);

			foot1Clone[i].rightEnd.set(foot1[i].rightEnd);
			foot1Clone[i].rightEnd.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot1Clone[i].rightEnd.y -= footDownDistance;
			foot1Clone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			foot1Clone[i].rightEnd.add(centre);

			for (int j = 0; j < foot1Clone[i].vertex3D.length; j++) {
				foot1Clone[i].vertex3D[j].set(foot1[i].vertex3D[j]);
				foot1Clone[i].vertex3D[j].x += footExtendDistance;

				if (i >= 25 && i <= 49)
					foot1Clone[i].vertex3D[j].y -= footDownDistance;

				foot1Clone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				foot1Clone[i].vertex3D[j].add(centre);

				foot1Clone[i].normal.set(foot1[i].normal);
				foot1Clone[i].normal.rotate_XZ(360 - bodyAngle);
				foot1Clone[i].findDiffuse();
			}
		}

		// update foot2
		for (int i = 0; i < foot2Clone.length; i++) {
			foot2Clone[i].origin.set(foot2[i].origin);
			foot2Clone[i].origin.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot2Clone[i].origin.y -= footDownDistance;
			foot2Clone[i].origin.rotate_XZ(360 - bodyAngle);
			foot2Clone[i].origin.add(centre);

			foot2Clone[i].bottomEnd.set(foot2[i].bottomEnd);
			foot2Clone[i].bottomEnd.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot2Clone[i].bottomEnd.y -= footDownDistance;
			foot2Clone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			foot2Clone[i].bottomEnd.add(centre);

			foot2Clone[i].rightEnd.set(foot2[i].rightEnd);
			foot2Clone[i].rightEnd.x += footExtendDistance;
			if (i >= 25 && i <= 49)
				foot2Clone[i].rightEnd.y -= footDownDistance;
			foot2Clone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			foot2Clone[i].rightEnd.add(centre);

			for (int j = 0; j < foot2Clone[i].vertex3D.length; j++) {
				foot2Clone[i].vertex3D[j].set(foot2[i].vertex3D[j]);
				foot2Clone[i].vertex3D[j].x += footExtendDistance;

				if (i >= 25 && i <= 49)
					foot2Clone[i].vertex3D[j].y -= footDownDistance;

				foot2Clone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				foot2Clone[i].vertex3D[j].add(centre);

				foot2Clone[i].normal.set(foot2[i].normal);
				foot2Clone[i].normal.rotate_XZ(360 - bodyAngle);
				foot2Clone[i].findDiffuse();
			}
		}

		// update foot3
		for (int i = 0; i < foot3Clone.length; i++) {
			foot3Clone[i].origin.set(foot3[i].origin);
			foot3Clone[i].origin.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot3Clone[i].origin.y -= footDownDistance;
			foot3Clone[i].origin.rotate_XZ(360 - bodyAngle);
			foot3Clone[i].origin.add(centre);

			foot3Clone[i].bottomEnd.set(foot3[i].bottomEnd);
			foot3Clone[i].bottomEnd.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot3Clone[i].bottomEnd.y -= footDownDistance;
			foot3Clone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			foot3Clone[i].bottomEnd.add(centre);

			foot3Clone[i].rightEnd.set(foot3[i].rightEnd);
			foot3Clone[i].rightEnd.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot3Clone[i].rightEnd.y -= footDownDistance;
			foot3Clone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			foot3Clone[i].rightEnd.add(centre);

			for (int j = 0; j < foot3Clone[i].vertex3D.length; j++) {
				foot3Clone[i].vertex3D[j].set(foot3[i].vertex3D[j]);
				foot3Clone[i].vertex3D[j].x -= footExtendDistance;

				if (i >= 25 && i <= 49)
					foot3Clone[i].vertex3D[j].y -= footDownDistance;

				foot3Clone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				foot3Clone[i].vertex3D[j].add(centre);

				foot3Clone[i].normal.set(foot3[i].normal);
				foot3Clone[i].normal.rotate_XZ(360 - bodyAngle);
				foot3Clone[i].findDiffuse();
			}
		}

		// update foot3
		for (int i = 0; i < foot4Clone.length; i++) {
			foot4Clone[i].origin.set(foot4[i].origin);
			foot4Clone[i].origin.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot4Clone[i].origin.y -= footDownDistance;
			foot4Clone[i].origin.rotate_XZ(360 - bodyAngle);
			foot4Clone[i].origin.add(centre);

			foot4Clone[i].bottomEnd.set(foot4[i].bottomEnd);
			foot4Clone[i].bottomEnd.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot4Clone[i].bottomEnd.y -= footDownDistance;
			foot4Clone[i].bottomEnd.rotate_XZ(360 - bodyAngle);
			foot4Clone[i].bottomEnd.add(centre);

			foot4Clone[i].rightEnd.set(foot4[i].rightEnd);
			foot4Clone[i].rightEnd.x -= footExtendDistance;
			if (i >= 25 && i <= 49)
				foot4Clone[i].rightEnd.y -= footDownDistance;
			foot4Clone[i].rightEnd.rotate_XZ(360 - bodyAngle);
			foot4Clone[i].rightEnd.add(centre);

			for (int j = 0; j < foot4Clone[i].vertex3D.length; j++) {
				foot4Clone[i].vertex3D[j].set(foot4[i].vertex3D[j]);
				foot4Clone[i].vertex3D[j].x -= footExtendDistance;

				if (i >= 25 && i <= 49)
					foot4Clone[i].vertex3D[j].y -= footDownDistance;

				foot4Clone[i].vertex3D[j].rotate_XZ(360 - bodyAngle);
				foot4Clone[i].vertex3D[j].add(centre);

				foot4Clone[i].normal.set(foot4[i].normal);
				foot4Clone[i].normal.rotate_XZ(360 - bodyAngle);
				foot4Clone[i].findDiffuse();
			}
		}
	}

	public boolean canBeDeployed() {
		if (jobStatus == deploying)
			return false;

		int position = (boundary2D.x1 + 8) / 16
				+ (127 - (boundary2D.y1 - 8 - 1) / 16) * 128;
		surrounding[0] = position - 129;
		surrounding[1] = position - 128;
		surrounding[2] = position - 127;
		surrounding[3] = position - 1;
		surrounding[4] = position;
		surrounding[5] = position + 1;
		surrounding[6] = position + 127;
		surrounding[7] = position + 128;
		surrounding[8] = position + 129;

		for (int i = 0; i < 9; i++) {
			position = surrounding[i];
			if (position / 128 > 0 && position / 128 < 127
					&& position % 128 > 0 && position % 128 < 127) {
				tile = MainThread.gridMap.tiles[position];
				for (int j = 0; j < 5; j++) {
					if (tile[j] != null && tile[j] != this) {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		return true;
	}

	public void expand() {

		jobStatus = deploying;

		float theXPos = ((boundary2D.x1 + 8) / 16 * 0.25f) + 0.125f;
		float theYPos = ((boundary2D.y1 - 8 - 1) / 16 * 0.25f) + 0.125f;

		myConstructionYard = new ConstructionYard(theXPos, -2.89f, theYPos,
				teamNo);
		myConstructionYard.isSelectable = false;
		theAssetManager.addContructionYard(myConstructionYard);

	}

	public void resetLogicStatus() {
		movement.reset();
		currentMovementStatus = freeToMove;
		stuckCount = 0;
		destinationX = centre.x;
		destinationY = centre.z;
		insideDeistinationRadiusCount = 0;
		obstacle = null;
		closeToDestination = false;

	}

	public void moveTo(float destinationX, float destinationY) {

		if (jobStatus != idle) {
			return;
		}

		resetLogicStatus();
		pathIsFound = false;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		newDestinationisGiven = true;
		heuristicRecalculationCountDown = 0;
		jobStatus = idle;

	}

	public int getMaxHp() {
		return maxHP;
	}

}
