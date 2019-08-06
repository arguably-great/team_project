package com.example.fbuteamproject.layouts;

import android.util.Log;

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
//TODO - Add Listener Stuff here
public class PhotoLayout extends Node {

    private static final String TAG = "PhotoLayout";
    private static final int MAX_NUM_NODES = 6;
    private static ArrayList<Node> photoNodes;
    private static final Vector3 PHOTO_SCALE_VECTOR = new Vector3(0.3f, 0.3f, 0.3f);
    private static final float MAX_Y_LEVELS = 3.0f;
    private static final float MAX_X_COVERAGE_DIST = 2.5f;
    private static final float PHOTO_NODE_Z = 0.25f;

    public PhotoLayout() {
        photoNodes = new ArrayList<>();

        //TODO - Add Listener Stuff here
    }

    //TODO - Remove if new PhotoLayout stuff works
    public static ArrayList<Node> photoNodeSetUp(Node baseNode) {

        Log.d(TAG, "here are photonodes" + photoNodes);

        photoNodes = new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            // setting up nodes for photos
            Node node = new Node();
            photoNodes.add(node);

            node.setParent(baseNode);
            //node.setRenderable(PhotoComponent.viewRenderables.get(i));

            if (i == 0) {
                node.setLocalPosition(new Vector3(-1.0f, 1.0f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }

            if (i == 1) {
                node.setLocalPosition(new Vector3(1.0f, 1.0f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }

            if (i == 2) {
                node.setLocalPosition(new Vector3(-1.5f, 0.66f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }

            if (i == 3) {
                node.setLocalPosition(new Vector3(1.5f, 0.66f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

            }

            if (i == 4) {
                node.setLocalPosition(new Vector3(-1.0f, 0.33f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }


            if (i == 5) {
                node.setLocalPosition(new Vector3(1.0f, 0.33f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }
        }

        return photoNodes;

    }

    private void createPhotoNodes(ArrayList<ViewRenderable> photoRenderables) {

        //Add the constant number of nodes that the project will be using
        if (photoNodes.size() == 0) {
            for (int i = 0; i < MAX_NUM_NODES; i++) {
                photoNodes.add(new Node());
            }
            
            for (int currIndex = 0; currIndex < photoNodes.size(); currIndex++) {

                float currNodeX = (currIndex % 2 == 0) ? -MAX_X_COVERAGE_DIST / 2 : MAX_X_COVERAGE_DIST / 2;

                //Using integer division to assess what level we are currently on
                int currLevelY = currIndex / 2;

                float currNodeY = (1.0f - (currLevelY / MAX_Y_LEVELS) );

                Vector3 currLocationVector = new Vector3(currNodeX, currNodeY, PHOTO_NODE_Z);

                Node currPhotoNode = photoNodes.get(currIndex);

                currPhotoNode.setParent(this);

                currPhotoNode.setLocalPosition(currLocationVector);
                currPhotoNode.setLocalScale(PHOTO_SCALE_VECTOR);

                float currAngle = (currIndex % 2 == 0) ? -45f : 45f;

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


}

