package com.example.fbuteamproject.layouts;

import android.util.Log;

import com.example.fbuteamproject.components.ModelComponent;
import com.example.fbuteamproject.utils.Config;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
/*
This Class serves as one piece of the bigger overall Layout structure for the application.
    More specifically, this Class organizes the Nodes with respect to one another
    by having the EntityLayout Object be the Parent for all the Node objects created.
    This EntityLayout Object can then be placed relative to other Layout Objects
 */
public class EntityLayout extends Node implements ModelComponent.ModelCallBacksFinishedListener {

    private ArrayList<Config.Entity> entityNodes;

    private final float MAX_X_COVERAGE_DIST  = 2.5f;

    private final float PLANET_Y = 1.6f;

    public EntityLayout(){
        entityNodes = new ArrayList<>();
        ModelComponent.setListener(this);
    }

    public EntityLayout(ArrayList<Config.Entity> appEntities){

        entityNodes = new ArrayList<>();

        createEntityNodes(appEntities);

    }

    private void createEntityNodes(ArrayList<Config.Entity> appEntities) {

        float entitySplit;

        if (appEntities.size() == 1){
            entitySplit = 0;
        }
        else {
            entitySplit = (MAX_X_COVERAGE_DIST) / (appEntities.size() - 1);
        }

        for(int currIndex = 0; currIndex < appEntities.size(); currIndex++){

            Config.Entity currEntity = appEntities.get(currIndex);

            currEntity.setParent(this);
            currEntity.setRenderable(appEntities.get(currIndex).getEntityModel() );

            //Calculate the X and Z positions for the current Node
            float currXPos = (-MAX_X_COVERAGE_DIST / 2) + (currIndex * entitySplit);
            float currZPos = ( (float) (Math.pow(currXPos, 2) - MAX_X_COVERAGE_DIST/2) );

            currEntity.setLocalPosition(new Vector3(currXPos, PLANET_Y, currZPos) );
            currEntity.setLocalScale(appEntities.get(currIndex).getEntityScaleVector());

            entityNodes.add(currEntity);

        }

    }

    @Override
    public void startNodeCreation(ArrayList<Config.Entity> entities) {
        Log.d("START_CREATE", "Layout Stuff");
        createEntityNodes(entities);
    }
}
