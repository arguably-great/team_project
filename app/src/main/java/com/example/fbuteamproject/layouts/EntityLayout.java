package com.example.fbuteamproject.layouts;

import com.example.fbuteamproject.utils.Config;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.ArrayList;
/*
This Class serves as one piece of the bigger overall Layout structure for the application.
    More specifically, this Class organizes the Nodes with respect to one another
    by having the EntityLayout Object be the Parent for all the Node objects created.
    This EntityLayout Object can then be placed relative to other GenericLayout Objects
 */
public class EntityLayout extends Node {

    private ArrayList<Node> entityNodes;

    private final float MAX_COVERAGE_DIST  = 1.0f;
    private final Vector3 ENTITY_SCALE_VECTOR = new Vector3(0.005f, 0.005f, 0.005f);
    private final float PLANET_Y = 0.5f;
    private final float PLANET_Z = 0.0f;

    public EntityLayout(ArrayList<Config.Entity> appEntities, ArrayList<ModelRenderable> entityViewRenderables ){

        entityNodes = new ArrayList<>();

        createEntityNodes(appEntities, entityViewRenderables);

    }

    private void createEntityNodes(ArrayList<Config.Entity> appEntities, ArrayList<ModelRenderable> entityModelRenderables) {

        float entitySplit;

        if (entityModelRenderables.size() == 1){
            entitySplit = 0;
        }
        else {
            entitySplit = (MAX_COVERAGE_DIST) / (entityModelRenderables.size() - 1);
        }

        for(int currIndex = 0; currIndex < entityModelRenderables.size(); currIndex++){

            Config.Entity currEntity = appEntities.get(currIndex);

            currEntity.setParent(this);
            currEntity.setRenderable(entityModelRenderables.get(currIndex) );

            //Calculate the X-Position for the current Node
            float currXPos = (-MAX_COVERAGE_DIST / 2) + (currIndex * entitySplit);

            currEntity.setLocalPosition(new Vector3(currXPos, PLANET_Y, PLANET_Z) );

            currEntity.setLocalScale(ENTITY_SCALE_VECTOR);

            entityNodes.add(currEntity);

        }

    }


}
