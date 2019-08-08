package com.example.fbuteamproject.layouts;

import android.util.Log;

import com.example.fbuteamproject.activities.ARActivity;
import com.example.fbuteamproject.components.PhotoComponent;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;


/*
This Class serves as one piece of the bigger overall Layout structure for the application.
    More specifically, this Class organizes the Photos in AR.
    This PhotoLayout dynamically creates nodes based on the number of photos.
 */
public class PhotoLayout extends Node implements ARActivity.PhotoCallbacksFinishedListener {

    private static final String TAG = "PhotoLayout";
    private static final int MAX_NUM_NODES = 12;
    public static final int NUM_COLS = 4;
    private static ArrayList<Node> photoNodes;
    private static final Vector3 PHOTO_SCALE_VECTOR = new Vector3(0.3f, 0.3f, 0.3f);
    private static final float MAX_Y_LEVELS = 3.0f;
    private static final float MAX_X_COVERAGE_DIST = 2.5f;
    private static final float PHOTO_NODE_Z = 0.25f;

    public PhotoLayout() {
        photoNodes = new ArrayList<>();
        PhotoComponent.setListener(this);
    }

    //TODO - Code will work better after rebasing with Sophia's Stuff

    private void createPhotoNodes(ArrayList<ViewRenderable> photoRenderables) {

        //Add the constant number of nodes that the project will be using
        if (photoNodes.size() == 0) {
            for (int i = 0; i < MAX_NUM_NODES; i++) {
                photoNodes.add(new Node());
            }
            
            for (int currIndex = 0; currIndex < photoNodes.size(); currIndex++) {

                int currRow = currIndex % NUM_COLS;
                float currNodeX;
                float currNodeZ;
                float currAngle;

                //TODO - Dont keep these as magic numbers. ADD FLEXIBILITY OR CONSTANTS
                    //TODO - Also make sure to check this code AFTER Sophia puts her stuff on master
                switch(currRow){
                    case 0:
                        currNodeX = -MAX_X_COVERAGE_DIST / 2;
                        currNodeZ = -0.25f;
                        currAngle = 45f;
                        break;

                    case 1:
                        currNodeX = -MAX_X_COVERAGE_DIST / 2;
                        currNodeZ = 0.25f;
                        currAngle = 45f;
                        break;

                    case 2:
                        currNodeX = (MAX_X_COVERAGE_DIST / 2) - 0.3f;
                        currNodeZ = 0.25f;
                        currAngle = -45f;
                        break;

                    case 3:
                        currNodeX = MAX_X_COVERAGE_DIST / 2 + 0.3f;
                        currNodeZ = 0.0f;
                        currAngle = -45f;
                        break;

                    default:
                        currNodeX = 0.0f;
                        currNodeZ = -0.25f;
                        currAngle = 0.0f;
                }

                //Using integer division to assess what level we are currently on
                int currLevelY = currIndex / NUM_COLS;

                float currNodeY = (1.0f - (currLevelY / MAX_Y_LEVELS) );

                Vector3 currLocationVector = new Vector3(currNodeX, currNodeY, currNodeZ);

                Node currPhotoNode = photoNodes.get(currIndex);

                currPhotoNode.setParent(this);

                currPhotoNode.setLocalPosition(currLocationVector);
                currPhotoNode.setLocalScale(PHOTO_SCALE_VECTOR);

                currPhotoNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), currAngle) );
            }
        }

        //In every case (even after initial creation), we will want to clear
        //current ViewRenderables and put in the new ones that we have gotten
        for (int currIndex = 0; currIndex < photoNodes.size(); currIndex++) {
            Node currNode = photoNodes.get(currIndex);

            ViewRenderable currViewRenderable = photoRenderables.get(currIndex);

            //Resetting the ViewRenderable before setting the new Renderable
            currNode.setRenderable(null);
            currNode.setRenderable(currViewRenderable);
        }

    }


    @Override
    public void startPhotoNodeCreation(ArrayList<ViewRenderable> photoRenderables) {
        Log.d(TAG, "Should be ready to start PhotoNodeCreation");
        createPhotoNodes(photoRenderables);
    }
}

