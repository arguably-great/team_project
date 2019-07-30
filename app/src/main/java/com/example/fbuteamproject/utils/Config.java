package com.example.fbuteamproject.utils;

import java.util.ArrayList;

public class Config {

    String entityID;
    String modelPath;
    String videoURL;
    String albumID;
    String notesFolder;

    public static class Entity {
        String entityID;
        String modelPath;
        String videoURL;
        String albumID;
        String notesFolder;

        Entity(String entityID, String modelPath, String videoURL) {
            this.entityID = entityID;
            this.modelPath = modelPath;
            this.videoURL = videoURL;
            this.albumID = albumID;
            this.notesFolder = notesFolder;
        }

    }

    public static class AppConfig {
        public ArrayList<Entity> entities = new ArrayList<>();

        public static AppConfig getAppConfig() {
            return planetConfig;
        }
    }

    static AppConfig planetConfig;
    static AppConfig animalConfig;

    static {
        planetConfig = new AppConfig();
        planetConfig.entities.add(new Entity("myvenus", "DummyValue", "AnotherDummyValue"));

    }


}
