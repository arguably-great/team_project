package com.example.fbuteamproject.utils;

import android.content.Context;
import android.util.Log;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Config {

    public static class Entity extends Node {
        private Quaternion rotation;
        private String entityName;
        private String modelId;
        private String videoURL;
        private String keyWord;
        private File entityFile;
        private Context context;
        private static final String TEXT_EXTENSION = ".txt";
        private Vector3 entityScaleVector;
        private CompletableFuture<ModelRenderable> entityStage;
        private ModelRenderable entityModel;
        private ArrayList<ViewRenderable> entityPhotos;


        Entity(String entityName, String modelId, String videoURL, Vector3 entityScaleVector, Context context, String keyWord, Quaternion rotation){
            this.entityName = entityName;
            this.modelId = modelId;
            this.videoURL = videoURL;
            this.keyWord = keyWord;
            this.entityScaleVector = entityScaleVector;
            this.entityStage = null;
            this.entityModel = null;
            this.entityPhotos = new ArrayList<>();
            this.context = context;
            this.rotation = rotation;
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

        public String getKeyWord(){
            return keyWord;
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

        public Quaternion getEntityRotation() {
            return rotation;
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

            return planetConfig0;

        }
    }

    static AppConfig planetConfig0;
    static AppConfig animalConfig;

    static {
        planetConfig0 = new AppConfig();


        planetConfig0 = new AppConfig();


        planetConfig0.entities.add(new Entity("Mercury", "13900_Mercury_v1_l3.sfb",
                "file:///sdcard/Movies/mercury.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet solar system", null));
        planetConfig0.entities.add(new Entity("Venus", "Venus_1241.sfb",
                "file:///sdcard/Movies/VENUS.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "venus milky way", null));
        planetConfig0.entities.add(new Entity("Earth", "CHAHIN_EARTH.sfb",
                "file:///sdcard/Movies/earth.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "solar system earth", null));
        planetConfig0.entities.add(new Entity("Mars", "Mars.sfb",
                "file:///sdcard/Movies/mars.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "red mars planet solar system", null));
        planetConfig0.entities.add(new Entity("Jupiter", "model.sfb",
                "file:///sdcard/Movies/jupiter.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet jupiter solar system", null));
        planetConfig0.entities.add(new Entity("Saturn", "13906_Saturn_v1_l3.sfb",
                "file:///sdcard/Movies/saturn.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet solar system saturn rings", new Quaternion(90, 0, 10, 90)));
        planetConfig0.entities.add(new Entity("Uranus", "13907_Uranus_v2_l3.sfb",
                "file:///sdcard/Movies/uranus.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet uranus solar system", new Quaternion(90, 0, 10, 95)));
        planetConfig0.entities.add(new Entity("Neptune", "Neptune.sfb",
                "file:///sdcard/Movies/neptune.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet solar system neptune moon", null));


        /*planetConfig1 = new AppConfig();
        planetConfig1.entities.add(new Entity("Venus", "5ovHBezIGyZ",
                "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f), AppConfig.getContext()));
        planetConfig1.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "https://pmdvod.nationalgeographic.com/NG_Video/863/99/1241075779988_1527179700645_1241081923914_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.005f, 0.005f, 0.005f), AppConfig.getContext()));
        planetConfig1.entities.add(new Entity("Mercury", "7piUT6FHGKJ", "https://pmdvod.nationalgeographic.com/NG_Video/298/99/1304907843930_1535034558391_1304910915613_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",
                new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext()));*/

        animalConfig = new AppConfig();

        animalConfig.entities.add(new Entity("Lion", "3XAJojWxSWz","https://pmdvod.nationalgeographic.com/NG_Video/493/847/1087143491881_1509643865712_1087150659568_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal ", null));
        animalConfig.entities.add(new Entity("Elephant", "eGI3RS52kJA","https://pmdvod.nationalgeographic.com/NG_Video/392/819/1142872131764_1516653097532_1142871619814_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal ", null));
        animalConfig.entities.add(new Entity("Horse", "5ocnVSh_ZF-","https://pmdvod.nationalgeographic.com/NG_Video/722/723/1251665987846_1528499941303_1251668547971_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal ", null));
        animalConfig.entities.add(new Entity("Pig", "bbPhEBl5Bh0","https://pmdvod.nationalgeographic.com/NG_Video/336/479/1152476739923_1517604362758_1152482371523_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4",new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal ", null));
        animalConfig.entities.add(new Entity("Wolf", "46bXrRt8pFF", "https://pmdvod.nationalgeographic.com/NG_Video/508/71/1453304387732_1551912110015_1453312067615_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal ", null));
        animalConfig.entities.add(new Entity("Tiger", "54KLm0HdFWy","https://pmdvod.nationalgeographic.com/NG_Video/854/911/Ohio_Tigers_wild_1800.mp4", new Vector3(0.05f, 0.05f, 0.05f), AppConfig.getContext(), "animal", null));

    }




}
