package com.example.fbuteamproject.models;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.fbuteamproject.R;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Planet extends Node implements Node.OnTapListener{

    private String planetName;
    private String planetNotes;
    private String planetVideoResID;

    private Node planetNameCard;
    private final Context context;

    public Planet(String planetName, String planetNotes, Context context) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
        this.context = context;

        setOnTapListener(this);
    }

    public Planet(String planetName, String planetNotes, String planetVideoResID, Context context) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
        this.planetVideoResID = planetVideoResID;
        this.context = context;

        setOnTapListener(this);
    }


    public String getPlanetName() {
        return planetName;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public String getPlanetNotes() {
        return planetNotes;
    }

    public void setPlanetNotes(String planetNotes) {
        this.planetNotes = planetNotes;
    }

    public String getPlanetVideoResID() {
        return planetVideoResID;
    }

    public void setPlanetVideoResID(String planetVideoResID) {
        this.planetVideoResID = planetVideoResID;
    }

    public Node getPlanetNameCard() {
        return planetNameCard;
    }

    @Override
    public void onActivate() {

        if (getScene() == null) {
            throw new IllegalStateException("Scene is null!");
        }

        if (planetNameCard == null) {
            Log.d("PlanetDebug", "Setting up the infocard");
            planetNameCard = new Node();
            planetNameCard.setParent(this);
            planetNameCard.setEnabled(true);
            planetNameCard.setLocalPosition(new Vector3(0.0f, .1f, 0.0f));

            ViewRenderable.builder()
                    .setView(context, R.layout.component_planet_title)
                    .build()
                    .thenAccept(
                            (renderable) -> {
                                planetNameCard.setRenderable(renderable);
                                TextView textView = (TextView) renderable.getView();
                                textView.setText(planetName);
                                Log.d("PlanetDebug", "Name of the planet for the name card is: " + planetName);

                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load plane card view.", throwable);
                            });
        }


    }
    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        Log.d("PlanetDebug", "Registered a tap on one of the planets");
        if (planetNameCard == null) {
            Log.d("PlanetDebug", "info card is null");
            return;
        }

        planetNameCard.setEnabled(!planetNameCard.isEnabled());
        Log.d("PlanetDebug", "Flipped value of name card" );
    }


}
