package com.example.fbuteamproject.layouts;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

/*
This Class serves as one piece of the bigger overall Layout structure for the application.
    More specifically, this Class organizes the Video in AR.
    This VideoLayout Object can then be placed relative to other Layout Objects
 */
public class VideoLayout extends Node {

    private static final float VIDEO_HEIGHT_PHONE = 0.7f;
    private static final float VIDEO_HEIGHT_TABLET = 0.9f;
    private static final Vector3 VIDEO_LOCATION_VECTOR = new Vector3(0.0f, 0.6f, -1.0f);
    private static final Vector3 VIDEO_SCALE_VECTOR = new Vector3(VIDEO_HEIGHT_TABLET * 2, VIDEO_HEIGHT_TABLET, 1.0f);
    private Node videoNode;


    public VideoLayout(ModelRenderable videoRenderable){

        this.videoNode = new Node();

        createVideoNode(videoRenderable);

    }

    private void createVideoNode(ModelRenderable videoRenderable) {
        videoNode.setParent(this);
        videoNode.setRenderable(videoRenderable);
        videoNode.setLocalPosition(VIDEO_LOCATION_VECTOR);
        videoNode.setLocalScale(VIDEO_SCALE_VECTOR);

    }

    public Node getVideoNode() {
        return videoNode;
    }
}
