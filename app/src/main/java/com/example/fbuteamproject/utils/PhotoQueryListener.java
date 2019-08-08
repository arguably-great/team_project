package com.example.fbuteamproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbuteamproject.activities.ARActivity;
import com.example.fbuteamproject.components.PhotoComponent;
import com.example.fbuteamproject.utils.FlickrApi.Api;
import com.example.fbuteamproject.utils.FlickrApi.Photo;
import com.example.fbuteamproject.utils.FlickrApi.Query;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PhotoQueryListener {

    private static final String TAG = "PhotoListener";
    private static int loadPhotoCount;
    private static final Set<PhotoViewer> photoViewers = new HashSet<>();
    private static List<Photo> currentPhotos = new ArrayList<>();



    public static class QueryListener implements Api.QueryListener {

        Context context;
        private static final int PHOTO_NUMBER = 6;
        private int PHOTO_WIDTH = 1000;
        private int PHOTO_HEIGHT = 600;


        public QueryListener(Context context) {
            this.context = context;

        }

        @Override
        public void onSearchCompleted(Query query, List<Photo> photos) {

            Log.d(TAG, "Search completed");

            if (!isCurrentQuery(query)) {
                return;
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Search completed, got " + photos.size() + " results");
            }

            for (PhotoViewer viewer : photoViewers) {
                viewer.onPhotosUpdated(photos);
            }
            currentPhotos = photos;

            if (currentPhotos.size() > PHOTO_NUMBER) {

                for (int i = 0; i < PHOTO_NUMBER; i++) {

                    Log.d(TAG, "on SearchCompleted: "+ i);

                    CompletableFuture<ViewRenderable> photoStage;
                    ImageView iv = new ImageView(context);

                    RequestOptions options = new RequestOptions()
                            .override(PHOTO_WIDTH, PHOTO_HEIGHT)
                            .centerCrop();

                    Log.d(TAG, "Here is a photo "+ currentPhotos.get(i));

                    Glide.with(context).load(currentPhotos.get(i))
                            .apply(options)
                            .into(iv);

                    photoStage = ViewRenderable.builder().setView(context, iv).build();

                    loadPhotoCount++;

                    Log.d(TAG, "Current photo count is " + loadPhotoCount);

                    photoStage.thenApply(viewRenderable -> {

                        Log.d(TAG, "Current entity is " + ARActivity.currEntitySelected.getEntity());

                        PhotoComponent.buildViewRenderable(photoStage, context, ARActivity.currEntitySelected.getEntity());

                        loadPhotoCount--;
                        Log.d(TAG, "Current photo count is " + loadPhotoCount);

                        if(loadPhotoCount == 0) {

                            ArrayList<ViewRenderable> myViews = ARActivity.currEntitySelected.getEntity().getEntityPhotos();

                            Log.d(TAG, "Current entity photos are " + myViews);

                            PhotoComponent.listener.startPhotoNodeCreation(myViews);
                        }

                        return null;
                    });

                }
            }

        }

        private boolean isCurrentQuery(Query query) {
            return PhotoComponent.currentQuery != null && PhotoComponent.currentQuery.equals(query);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void onSearchFailed(Query query, Exception e) {
            if (!isCurrentQuery(query)) {
                return;
            }

            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "Search failed", e);
            }

        }
    }
}
