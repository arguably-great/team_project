package com.example.fbuteamproject.models;

import com.google.ar.sceneform.Node;

public class Planet extends Node {

    private String planetName;
    private String planetNotes;
    private String planetVideoResID;

    //TODO - Add other attributes here after rebasing and combining Project Features
    public Planet(String planetName, String planetNotes) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
    }

    public Planet(String planetName, String planetNotes, String planetVideoResID) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
        this.planetVideoResID = planetVideoResID;
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
}
