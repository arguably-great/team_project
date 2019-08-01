package com.example.fbuteamproject.utils;

import com.google.ar.sceneform.Node;

import java.util.ArrayList;

public class Config {

    public static class Entity extends Node {
        private String entityID;
        private String modelId;
        private String videoURL;
        private String albumID;
        private String notesFolder;

        Entity(String entityID, String modelId, String videoURL) {
            this.entityID = entityID;
            this.modelId = modelId;
            this.videoURL = videoURL;
            this.albumID = albumID;
            this.notesFolder = notesFolder;
        }

        public String getEntityID() {
            return entityID;
        }

        public void setEntityID(String entityID) {
            this.entityID = entityID;
        }

        public String getModelId() {
            return modelId;
        }

        public void setModelPath(String modelId) {
            this.modelId = modelId;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        public String getAlbumID() {
            return albumID;
        }

        public void setAlbumID(String albumID) {
            this.albumID = albumID;
        }

        public String getNotesFolder() {
            return notesFolder;
        }

        public void setNotesFolder(String notesFolder) {
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

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4"));
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy"));

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4"));
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy"));

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "AnotherDummyValue"));
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4"));
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy"));
        //planetConfig.entities.add(new Entity("Earth", "cWVIGfX_6aM", "AnotherDummyValue"));
        //planetConfig.entities.add(new Entity("Mercury", "7piUT6FHGKJ", "Dummyletto"));
        //planetConfig.entities.add(new Entity("Mercury", "5ovHBezIGyZ", "Dummyletto"));


    }


}
