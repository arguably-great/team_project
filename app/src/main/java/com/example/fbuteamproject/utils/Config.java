package com.example.fbuteamproject.utils;

import com.google.ar.sceneform.Node;

import java.util.ArrayList;

public class Config {

    public static class Entity extends Node {
        private String entityID;
        private String modelPath;
        private String videoURL;
        private String albumID;
        private String notesFolder;

        Entity(String entityID, String modelPath, String videoURL) {
            this.entityID = entityID;
            this.modelPath = modelPath;
            this.videoURL = videoURL;
            this.albumID = albumID;
            this.notesFolder = notesFolder;
        }

        public String getEntityID() {
            return entityID;
        }

        public String getModelPath() {
            return modelPath;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getAlbumID() {
            return albumID;
        }

        public String getNotesFolder() {
            return notesFolder;
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
        planetConfig.entities.add(new Entity("planetOne", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetTwo", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetThree", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetFour", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetFive", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetSix", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetSeven", "DummyValue", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("planetEight", "DummyValue", "AnotherDummyValue"));


    }


}
