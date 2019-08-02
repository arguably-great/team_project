package com.example.fbuteamproject.models;

/*
This Model creates Getters and Setters for Photos.
 */

public class Photo {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String name, url;

    public Photo() {
    }


}
