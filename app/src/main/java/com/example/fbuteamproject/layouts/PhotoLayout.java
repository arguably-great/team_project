package com.example.fbuteamproject.layouts;

import android.util.Log;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;


/*
This Class serves as one piece of the bigger overall Layout structure for the application.
    More specifically, this Class organizes the Photos in AR.
    This PhotoLayout dynamically creates nodes based on the number of photos.
 */

public class PhotoLayout{

    private static final String TAG = "PhotoLayout";
    private static ArrayList<Node> photoNodes;

    public static ArrayList<Node> photoNodeSetUp(Node baseNode) {

        Log.d(TAG, "here are photonodes"+ photoNodes);

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

            if (i == 1){
                node.setLocalPosition(new Vector3(1.0f, 1.0f, 0.0f) );
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }

            if (i == 2) {
                node.setLocalPosition(new Vector3(-1.5f, 0.66f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }

            if (i == 3) {
                node.setLocalPosition(new Vector3(1.5f, 0.66f, 0.0f) );
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

            }

            if (i == 4) {
                node.setLocalPosition(new Vector3(-1.0f, 0.33f, 0.0f));
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }


            if (i == 5){
                node.setLocalPosition(new Vector3(1.0f, 0.33f, 0.0f) );
                node.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
            }
        }

        return photoNodes;

        }


}

