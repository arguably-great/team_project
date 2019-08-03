package com.example.fbuteamproject.utils;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Config {

    public static class Entity extends Node {
        private String entityName;
        private String modelId;
        private String videoURL;
        private String albumID;
        private String notesFolder;
        private Vector3 entityScaleVector;
        private CompletableFuture<ModelRenderable> entityStage;
        private ModelRenderable entityModel;


        Entity(String entityName, String modelId, String videoURL, Vector3 entityScaleVector) {
            this.entityName = entityName;
            this.modelId = modelId;
            this.videoURL = videoURL;
            this.albumID = albumID;
            this.notesFolder = notesFolder;
            this.entityScaleVector = entityScaleVector;
            this.entityStage = null;
            this.entityModel = null;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
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

        public Vector3 getEntityScaleVector() {
            return entityScaleVector;
        }

        public void setEntityScaleVector(Vector3 entityScaleVector) {
            this.entityScaleVector = entityScaleVector;
        }

        public void setEntityStage(CompletableFuture<ModelRenderable> myEntityStage) {
            this.entityStage = myEntityStage;
        }

        public CompletableFuture<ModelRenderable> getEntityStage() {
            return entityStage;
        }

        public void setEntityModel(ModelRenderable entityModel) {
            this.entityModel = entityModel;
        }

        public ModelRenderable getEntityModel() {
            return entityModel;
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

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u",
                "https://pmdvod.nationalgeographic.com/NG_Video/596/311/1370718787631_1542234923394_1370715715931_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f)));
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ",
                "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f)));
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "https://pmdvod.nationalgeographic.com/NG_Video/863/99/1241075779988_1527179700645_1241081923914_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f)));
        planetConfig.entities.add(new Entity("Mercury", "7piUT6FHGKJ", "https://pmdvod.nationalgeographic.com/NG_Video/298/99/1304907843930_1535034558391_1304910915613_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.05f, 0.05f, 0.05f)));



/*        animalConfig = new AppConfig();

        animalConfig.entities.add(new Entity("Lion", "3XAJojWxSWz","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("Elephant", "eGI3RS52kJA","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("Horse", "5ocnVSh_ZF-","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("Pig", "bbPhEBl5Bh0","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("Wolf", "46bXrRt8pFF","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("Tiger", "54KLm0HdFWy","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));
        animalConfig.entities.add(new Entity("SquirrelMonkey", "8J_6QDW6au1","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));*/
    }


}
