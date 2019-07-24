package com.example.fbuteamproject.utils;

public class Config {

    String entityID;
    String modelPath;
    String videoURL;
    //String albumID;
    //String notesFolder;

    class Entity {
        String entityID;
        String modelPath;
        String videoURL;
        //String albumID;
        //String notesFolder;

        Entity(String entityID, String modelPath, String videoURL, String albumID, String notesFolder) {
            this.entityID = entityID;
            this.modelPath = modelPath;
            this.videoURL = videoURL;
            //this.albumID = albumID;
            //this.notesFolder = notesFolder;
        }


}
