<<<<<<< HEAD
package com.example.fbuteamproject.layouts;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

public class ARComponentsShell extends Node {


    private Node venusNode;
    private Node jupiterNode;

    private Node upperLeftImageNode;
    private Node centerLeftImageNode;
    private Node lowerLeftImageNode;

    private Node upperRightImageNode;
    private Node centerRightImageNode;
    private Node lowerRightImageNode;

    private Node videoNode;

    private Node planetContentsNode;

    //TODO - Create the Nodes and set the Renderables outside of this.
        //TODO - Later attempt to add that logic into this class


    public ARComponentsShell(Node venusNode, Node jupiterNode,
         Node upperLeftImageNode, Node centerLeftImageNode,
         Node lowerLeftImageNode, Node upperRightImageNode,
         Node centerRightImageNode, Node lowerRightImageNode,
         Node videoNode, Node planetContentsNode) {

        this.venusNode = venusNode;
        this.jupiterNode = jupiterNode;
        this.upperLeftImageNode = upperLeftImageNode;
        this.centerLeftImageNode = centerLeftImageNode;
        this.lowerLeftImageNode = lowerLeftImageNode;
        this.upperRightImageNode = upperRightImageNode;
        this.centerRightImageNode = centerRightImageNode;
        this.lowerRightImageNode = lowerRightImageNode;
        this.videoNode = videoNode;
        this.planetContentsNode = planetContentsNode;


        organizeNodes();


    }

    private void organizeNodes() {

        venusNode.setParent(this);
        venusNode.setLocalPosition(new Vector3(-0.5f, 1.6f, 0.0f) );
        venusNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );


        jupiterNode.setParent(this);
        jupiterNode.setLocalPosition(new Vector3(0.5f, 1.6f, 0.0f) );
        jupiterNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );


        upperLeftImageNode.setParent(this);
        upperLeftImageNode.setLocalPosition(new Vector3(-1.0f, 1.0f, 0.0f) );
        upperLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        centerLeftImageNode.setParent(this);
        centerLeftImageNode.setLocalPosition(new Vector3(-1.5f, 0.66f, 0.0f) );
        centerLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        lowerLeftImageNode.setParent(this);
        lowerLeftImageNode.setLocalPosition(new Vector3(-1.0f, 0.33f, 0.0f) );
        lowerLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        upperRightImageNode.setParent(this);
        upperRightImageNode.setLocalPosition(new Vector3(1.0f, 1.0f, 0.0f) );
        upperRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        centerRightImageNode.setParent(this);
        centerRightImageNode.setLocalPosition(new Vector3(1.5f, 0.66f, 0.0f) );
        centerRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        lowerRightImageNode.setParent(this);
        lowerRightImageNode.setLocalPosition(new Vector3(1.0f, 0.33f, 0.0f) );
        lowerRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        videoNode.setParent(this);
        videoNode.setLocalPosition(new Vector3(0.0f, 0.75f, 0.0f) );
        //TODO - At the moment not being used ://


        planetContentsNode.setParent(this);
        planetContentsNode.setLocalPosition(new Vector3(0.0f, 0.40f, 0.0f) );
        planetContentsNode.setLocalScale(new Vector3(1.5f, 1.0f, 0.2f) );

    }


}
=======
//package com.example.fbuteamproject.layouts;
//
//import com.google.ar.sceneform.Node;
//import com.google.ar.sceneform.math.Vector3;
//
//public class ARComponentsShell extends Node {
//
//
//    private Node venusNode;
//    private Node jupiterNode;
//
//    private Node upperLeftImageNode;
//    private Node centerLeftImageNode;
//    private Node lowerLeftImageNode;
//
//    private Node upperRightImageNode;
//    private Node centerRightImageNode;
//    private Node lowerRightImageNode;
//
//    private Node videoNode;
//
//    private Node planetContentsNode;
//
//    //TODO - Create the Nodes and set the Renderables outside of this.
//        //TODO - Later attempt to add that logic into this class
//
//
//    public ARComponentsShell(Node venusNode, Node jupiterNode,
////         Node upperLeftImageNode, Node centerLeftImageNode,
////         Node lowerLeftImageNode, Node upperRightImageNode,
////         Node centerRightImageNode, Node lowerRightImageNode,
//         Node videoNode, Node planetContentsNode) {
//
//        this.venusNode = venusNode;
//        this.jupiterNode = jupiterNode;
////        this.upperLeftImageNode = upperLeftImageNode;
////        this.centerLeftImageNode = centerLeftImageNode;
////        this.lowerLeftImageNode = lowerLeftImageNode;
////        this.upperRightImageNode = upperRightImageNode;
////        this.centerRightImageNode = centerRightImageNode;
////        this.lowerRightImageNode = lowerRightImageNode;
//        this.videoNode = videoNode;
//        this.planetContentsNode = planetContentsNode;
//
//
//        organizeNodes();
//
//
//    }
//
//    private void organizeNodes() {
//
//        venusNode.setParent(this);
//        venusNode.setLocalPosition(new Vector3(-0.5f, 1.6f, 0.0f) );
//        venusNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );
//
//
//        jupiterNode.setParent(this);
//        jupiterNode.setLocalPosition(new Vector3(0.5f, 1.6f, 0.0f) );
//        jupiterNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );
//
//
//        upperLeftImageNode.setParent(this);
//        upperLeftImageNode.setLocalPosition(new Vector3(-1.0f, 1.0f, 0.0f) );
//        upperLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        centerLeftImageNode.setParent(this);
//        centerLeftImageNode.setLocalPosition(new Vector3(-1.5f, 0.66f, 0.0f) );
//        centerLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        lowerLeftImageNode.setParent(this);
//        lowerLeftImageNode.setLocalPosition(new Vector3(-1.0f, 0.33f, 0.0f) );
//        lowerLeftImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        upperRightImageNode.setParent(this);
//        upperRightImageNode.setLocalPosition(new Vector3(1.0f, 1.0f, 0.0f) );
//        upperRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        centerRightImageNode.setParent(this);
//        centerRightImageNode.setLocalPosition(new Vector3(1.5f, 0.66f, 0.0f) );
//        centerRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        lowerRightImageNode.setParent(this);
//        lowerRightImageNode.setLocalPosition(new Vector3(1.0f, 0.33f, 0.0f) );
//        lowerRightImageNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//
//        videoNode.setParent(this);
//        videoNode.setLocalPosition(new Vector3(-0.25f, 0.5f, 0.0f) );
//
//        planetContentsNode.setParent(this);
//        planetContentsNode.setLocalPosition(new Vector3(0.0f, 1.25f, 0.0f) );
//        planetContentsNode.setLocalScale(new Vector3(0.5f, 0.35f, 0.5f) );
//
//
//    }
//
//
//}
>>>>>>> [Dynamic Photos][WIP]
