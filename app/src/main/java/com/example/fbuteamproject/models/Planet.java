package com.example.fbuteamproject.models;

import com.google.ar.sceneform.Node;

public class Planet extends Node {

    private String planetName;
    private String planetNotes;

    //TODO - Add other attributes here after rebasing and combining Project Features

    public Planet(String planetName, String planetNotes) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
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

}
