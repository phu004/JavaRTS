package entity;

import core.GameData;
import core.polygon3D;
import core.vector;

import java.awt.*;

public abstract class Tank extends SolidInfrastructure {

    /**
     * Performed pull method and variable refactoring and pulled the duplicate code from sub-classes.
     * The subclass HeavyTank, and LightTank had duplicate variables which are pulled to an intermediate class Tank
     * The above classes also had duplicate code in method fireBullet() thus perfromed extract method and pull method.
     * Extracted method spawnExplosion and pulled it to intermediate parent class Tank
     * The aforementioned classes are extended from Tank class
     * Tank class extends from the super class SolidObject.
     * */

    public vector bodyCenter, turretCenter;
    public polygon3D[] body, turret;
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

    //a bitmap representation of the vision of the tank for enemy commander
    public static boolean[] bitmapVisionForEnemy;
    public static boolean[] bitmapVisionGainFromAttackingUnit;

    //the angle that the tank have rotated between current  frame and previous frame
    public int bodyAngleSum;

    //destination angle
    public int destinationAngle;

    //whether light tank has ling of sight to its target
    public boolean hasLineOfSightToTarget;

    //the oreintation of the tank
    public int bodyAngle, turretAngle;

    public void spawnMiniExplosion(vector firingPosition){
        //spawn a mini Explosion
        float[] tempFloat = theAssetManager.explosionInfo[theAssetManager.explosionCount];
        tempFloat[0] = firingPosition.x;
        tempFloat[1] = firingPosition.y;
        tempFloat[2] = firingPosition.z;
        tempFloat[3] = 0.4f;
        tempFloat[4] = 3;
        tempFloat[5] = 0;
        tempFloat[6] = 6 + (GameData.getRandom()%4);
        tempFloat[7] = centre.y;
        theAssetManager.explosionCount++;
    }



}