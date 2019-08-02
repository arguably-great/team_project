package com.example.fbuteamproject.utils;

import android.content.Context;
import android.util.Log;

import com.google.ar.sceneform.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Config {

    public static class Entity extends Node {
        private String entityID;
        private String modelId;
        private String videoURL;
        private String albumID;
        private File entityFile;
        private Context context;
        private static final String TEXT_EXTENSION = ".txt";

        Entity(String entityID, String modelId, String videoURL, Context context) {
            this.entityID = entityID;
            this.modelId = modelId;
            this.videoURL = videoURL;
            this.albumID = albumID;

            this.context = context;
            this.entityFile = new File(context.getFilesDir(), entityID + TEXT_EXTENSION);

            setupFile();
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

        public File getEntityFile() {
            return entityFile;
        }

        private void setupFile(){

            if (entityFile.exists() ){
                Log.d("FileDebug", "File already exists");
                return;
            }


            Log.d("FileDebug", "About to fill up Planet with generic Notes. Doing this for " + entityID);
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(entityFile.getName(), Context.MODE_PRIVATE);
                outputStream.write(entityID.toUpperCase().getBytes() );
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "https://pmdvod.nationalgeographic.com/NG_Video/596/311/1370718787631_1542234923394_1370715715931_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy", AppConfig.getContext() ) );

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "AnotherDummyValue", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy", AppConfig.getContext() ) );

        planetConfig.entities.add(new Entity("Earth", "88CP80Kgb-u", "AnotherDummyValue", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Venus", "5ovHBezIGyZ", "https://pmdvod.nationalgeographic.com/NG_Video/204/391/1346685507950_1539814478542_1346690115566_mp4_video_1024x576_1632000_primary_audio_eng_3.mp4", AppConfig.getContext() ) );
        planetConfig.entities.add(new Entity("Mars", "dsrYdi4GZ8U", "Dummydummy", AppConfig.getContext() ) );
        //planetConfig.entities.add(new Entity("Earth", "cWVIGfX_6aM", "AnotherDummyValue"));
        //planetConfig.entities.add(new Entity("Mercury", "7piUT6FHGKJ", "Dummyletto"));
        //planetConfig.entities.add(new Entity("Mercury", "5ovHBezIGyZ", "Dummyletto"));


    }


}
