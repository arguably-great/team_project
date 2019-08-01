package com.example.fbuteamproject.layouts;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class NoteLayout extends Node {

    private static final float NOTE_HEIGHT = 0.75f;
    private static final Vector3 NOTE_LOCATION_VECTOR = new Vector3(0.0f, 0.4f, 0.0f);
    private static final Vector3 NOTE_SCALE_VECTOR = new Vector3(NOTE_HEIGHT * 2, NOTE_HEIGHT * 1.5f, 1.0f);
    private Node noteNode;

    public NoteLayout(){
        this.noteNode = new Node();
    }

    public NoteLayout(ViewRenderable noteRenderable){

        this.noteNode = new Node();

        createVideoNode(noteRenderable);

    }

    public void createVideoNode(ViewRenderable noteRenderable) {
        noteNode.setParent(this);
        noteNode.setRenderable(noteRenderable);
        noteNode.setLocalScale(NOTE_LOCATION_VECTOR);
        noteNode.setLocalScale(NOTE_SCALE_VECTOR);

    }
}