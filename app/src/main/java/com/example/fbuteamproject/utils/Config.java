package com.example.fbuteamproject.utils;

import java.util.ArrayList;

public class Config {

    String entityID;
    String modelPath;
    String videoURL;
    String albumID;
    String notesFolder;

    class Entity {
        String entityID;
        String modelPath;
        String videoURL;
        String albumID;
        String notesFolder;

        Entity(String entityID, String modelPath, String videoURL, String albumID, String notesFolder) {
            this.entityID = entityID;
            this.modelPath = modelPath;
            this.videoURL = videoURL;
            this.albumID = albumID;
            this.notesFolder = notesFolder;
        }
    }

    static class AppConfig {
        ArrayList<Entity> entities = new ArrayList<>();
    }

    static AppConfig planetConfig;
    static AppConfig animalConfig;

    static {
        planetConfig = new AppConfig();

    }


}
