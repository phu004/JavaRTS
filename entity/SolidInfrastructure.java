package entity;

import core.polygon3D;
import core.texture;
import core.vector;

public class SolidInfrastructure extends SolidObject {

   /**
        * This method is pushed down from the parent class SolidObject
        * This method is only being used by Harvester, ConstructionYard, ConstructionVehicle,and Drone class.
        * all the remaining classes were rejecting the cloneObjects method
        * Thus an intermediate layer is introduced and above classes are extended from this layer
        * Thus the clone method would only be available to above classes.
        * This refactoring would remove the rebellious hierarchy smell.
    * */
    //clone a group of polygons (doesn't work on smooth shaded polygons)
    public polygon3D[] clonePolygons(polygon3D[] polys, boolean createNewOUV){
        int l = polys.length;

        polygon3D[] clone = new polygon3D[l];

        for(int i = 0; i < l; i++){
            if(polys[i] == null)
                continue;
            int length = polys[i].vertex3D.length;
            v = new vector[length];
            for(int j = 0; j < length; j++){
                v[j] = polys[i].vertex3D[j].myClone();
            }

            int myType = polys[i].type;
            float scaleX = polys[i].scaleX;
            float scaleY = polys[i].scaleY;
            texture myTexture = polys[i].myTexture;
            if(createNewOUV)
                clone[i] = new polygon3D(v, polys[i].origin.myClone(), polys[i].rightEnd.myClone(), polys[i].bottomEnd.myClone(), myTexture, scaleX, scaleY, myType);
            else
                clone[i] = new polygon3D(v, v[0], v[1], v[3], myTexture, scaleX, scaleY, myType);
            clone[i].shadowBias = polys[i].shadowBias;
            clone[i].diffuse_I = polys[i].diffuse_I;
            clone[i].Ambient_I = polys[i].Ambient_I;
        }


        return clone;
    }
}
