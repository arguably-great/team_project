package com.example.fbuteamproject.models;

import com.google.ar.sceneform.Node;

public class Planet extends Node {

    private String planetName;
    private String planetNotes;
private int planetVideoResID;

    //TODO - Add other attributes here after rebasing and combining Project Features
    public Planet(String planetName, String planetNotes) {
        this.planetName = planetName;
        this.planetNotes = planetNotes;
    }

    public Planet(String planetName, String planetNotes, int planetVideoResID) {
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

    public int getPlanetVideoResID() {
        return planetVideoResID;
    }

    public void setPlanetVideoResID(int planetVideoResID) {
        this.planetVideoResID = planetVideoResID;
    }
}
