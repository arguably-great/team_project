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

            if (com.example.fbuteamproject.BuildConfig.FLAVOR.equals("planet0")) {

                Log.d("HEY", "Returning planet config");

                return planetConfig0;
            }
            else if(com.example.fbuteamproject.BuildConfig.FLAVOR.equals("animal0")) {

                Log.d("HEY", "Returning animal config");

                return animalConfig;
            }

            Log.d("HEY", "Came here");
            return animalConfig;
        }
    }

    static AppConfig planetConfig0;
    static AppConfig animalConfig;

    static {
        planetConfig0 = new AppConfig();

        planetConfig0.entities.add(new Entity("Mercury", "13900_Mercury_v1_l3.sfb",
                "file:///sdcard/Movies/mercury.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "planet solar system", null));
        planetConfig0.entities.add(new Entity("Venus", "Venus_1241.sfb",
                "file:///sdcard/Movies/VENUS.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "venus milky way", null));
        planetConfig0.entities.add(new Entity("Earth", "CHAHIN_EARTH.sfb",
                "file:///sdcard/Movies/earth.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "solar system earth forest", null));
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


        animalConfig = new AppConfig();

        animalConfig.entities.add(new Entity("Elephant", "Elephant.sfb","file:///sdcard/Movies/elephant.mp4",
                new Vector3(0.4f, 0.4f, 0.4f), AppConfig.getContext(), "elephant herds", Quaternion.axisAngle(new Vector3(0,1,0), -5)));
        animalConfig.entities.add(new Entity("Giraffe", "Giraffe.sfb","file:///sdcard/Movies/giraffe.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "giraffe herds animals", Quaternion.axisAngle(new Vector3(0,1,0), -5)));
        animalConfig.entities.add(new Entity("Jaguar", "Jaguar.sfb","file:///sdcard/Movies/jaguar.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "national geographic cheetah jaguar", null));
        animalConfig.entities.add(new Entity("Lion", "Lion.sfb","file:///sdcard/Movies/lion.mp4",
                new Vector3(0.3f, 0.3f, 0.3f), AppConfig.getContext(), "animal lion pride herd", Quaternion.axisAngle(new Vector3(0,1,0), 5)));
        animalConfig.entities.add(new Entity("Monkey", "SquirrelMonkey.sfb", "file:///sdcard/Movies/monkey.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "monkey orangoutan ape", Quaternion.axisAngle(new Vector3(0,1,0), 30)));
        animalConfig.entities.add(new Entity("Zebra", "Zebra.sfb","file:///sdcard/Movies/zebra.mp4",
                new Vector3(0.2f, 0.2f, 0.2f), AppConfig.getContext(), "zebra herds africa", Quaternion.axisAngle(new Vector3(0,1,0), 30)));

    }




}
