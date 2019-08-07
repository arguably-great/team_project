package com.example.fbuteamproject.utils;

import android.content.Context;
import android.util.Log;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Config {

    public static class Entity extends Node {
        private String entityName;
        private String modelId;
        private String videoURL;
        private File entityFile;
        private Context context;
        private static final String TEXT_EXTENSION = ".txt";
        private Vector3 entityScaleVector;
        private CompletableFuture<ModelRenderable> entityStage;
        private ModelRenderable entityModel;
        private ArrayList<ViewRenderable> entityPhotos;


        Entity(String entityName, String modelId, String videoURL, Vector3 entityScaleVector, Context context) {
            this.entityName = entityName;
            this.modelId = modelId;
            this.videoURL = videoURL;
            this.entityScaleVector = entityScaleVector;
            this.entityStage = null;
            this.entityModel = null;
            this.entityPhotos = new ArrayList<>();
            this.context = context;
            this.entityFile = new File(context.getFilesDir(), entityName + TEXT_EXTENSION);

            setupFile();
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

        public File getEntityFile() {
            return entityFile;
        }

        private void setupFile(){

            if (entityFile.exists() ){
                Log.d("FileDebug", "File already exists");
                return;
            }

            Log.d("FileDebug", "About to fill up Planet with generic Notes. Doing this for " + entityName);
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(entityFile.getName(), Context.MODE_PRIVATE);
                outputStream.write(entityName.toUpperCase().getBytes() );
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        public ArrayList<ViewRenderable> getEntityPhotos() {
            return entityPhotos;
        }

        public void setEntityPhotos(ArrayList<ViewRenderable> entityPhotos) {
            this.entityPhotos = entityPhotos;
        }
    }

    public static class AppConfig {
        public ArrayList<Entity> entities = new ArrayList<>();
        private static Context context;

        public static Context getContext() {
            return context;
        }

        private static void setContext(Context newContext) {
            context = newContext;
        }

        public static AppConfig getAppConfig(Context newContext) {
            setContext(newContext);
            return planetConfig;
        }
    }

    static AppConfig planetConfig;
    static AppConfig animalConfig;

    static {
        planetConfig = new AppConfig();

      
        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u",
                "https://pmdvod.nationalgeographic.com/NG_Video/596/311/1370718787631_1542234923394_1370715715931_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f), AppConfig.getContext()));
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ",
                "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f), AppConfig.getContext()));
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "https://pmdvod.nationalgeographic.com/NG_Video/863/99/1241075779988_1527179700645_1241081923914_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f), AppConfig.getContext()));
        planetConfig.entities.add(new Entity("Mercury", "7piUT6FHGKJ", "https://pmdvod.nationalgeographic.com/NG_Video/298/99/1304907843930_1535034558391_1304910915613_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));

        animalConfig = new AppConfig();

        animalConfig.entities.add(new Entity("Lion", "3XAJojWxSWz","https://pmdvod.nationalgeographic.com/NG_Video/493/847/1087143491881_1509643865712_1087150659568_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        animalConfig.entities.add(new Entity("Elephant", "eGI3RS52kJA","https://pmdvod.nationalgeographic.com/NG_Video/392/819/1142872131764_1516653097532_1142871619814_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        animalConfig.entities.add(new Entity("Horse", "5ocnVSh_ZF-","https://pmdvod.nationalgeographic.com/NG_Video/722/723/1251665987846_1528499941303_1251668547971_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        animalConfig.entities.add(new Entity("Pig", "bbPhEBl5Bh0","https://pmdvod.nationalgeographic.com/NG_Video/336/479/1152476739923_1517604362758_1152482371523_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        animalConfig.entities.add(new Entity("Wolf", "46bXrRt8pFF", "https://pmdvod.nationalgeographic.com/NG_Video/508/71/1453304387732_1551912110015_1453312067615_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        animalConfig.entities.add(new Entity("Tiger", "54KLm0HdFWy","https://pmdvod.nationalgeographic.com/NG_Video/854/911/Ohio_Tigers_wild_1800.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));
        //animalConfig.entities.add(new Entity("SquirrelMonkey", "8J_6QDW6au1","http://fng-ads.fox.com/fw_ads/content/m/1/116450/80/5185488/NATGEO_WLS3_PROMO_HD_MASTER_1288593_435.mp4"));

    }




}
