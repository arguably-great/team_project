package com.example.fbuteamproject.components;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.Photo;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/* Class to build view renderables used for Photos */

public class PhotoComponent {


    private static final String TAG = "PhotoComponent";
    private static ArrayList<CompletableFuture<ViewRenderable>> completableFutures;
    public static ArrayList<ViewRenderable> viewRenderables;
    private static ViewRenderable viewRenderable;
    private static ArrayList<AtomicBoolean> hasLoaded;


    public static ArrayList<Photo> buildAlbumPhotos(Context context) {

        ArrayList<Photo> photoViews = new ArrayList<>();
        hasLoaded = new ArrayList<>();

        String[] photosAlbum = selectAlbum();

        for (int i = 0; i < photosAlbum.length; i++) {
            // adding images in photoalbum to be staged
            Photo photo = new Photo();
            photo.setName("Image " + i);
            photo.setUrl(photosAlbum[i]);
            Log.d(TAG, "urls"+ photo.getUrl());
            photoViews.add(photo);
            hasLoaded.add(new AtomicBoolean(false) );
        }

        Log.d(TAG, "buildVenusPhotos: "+ photoViews);

//        buildStages(photoViews, context);

        return photoViews;
    }

    private static String[] selectAlbum() {
        String[] photosAlbum;

            photosAlbum = new String[]{
                    "https://i.imgur.com/8mKSLC8.jpg",
                    "https://i.imgur.com/9vbrRQm.jpg",
                    "http://i.imgur.com/ovr0NAF.jpg",
                    "http://i.imgur.com/3wQcZeY.jpg",
                    "http://i.imgur.com/pSHXfu5.jpg",
                    "http://i.imgur.com/3wQcZeY.jpg",
                    "http://i.imgur.com/pSHXfu5.jpg",
                    "http://i.imgur.com/3wQcZeY.jpg"
            };

        return photosAlbum;
    }

    public static void buildStages(ArrayList<Photo> photosAlbum, Context context) {

        completableFutures = new ArrayList<>();

        for (int i = 0; i <= 6 || i < photosAlbum.size(); i++) {

            Log.d(TAG, "buildStages: " +photosAlbum.get(i));

            //create completable future for the entity
            CompletableFuture<ViewRenderable> photoStage;

            ImageView imageView = new ImageView(context);

            // load photos into glide
            Glide.with(context)
                    .load(photosAlbum.get(i).getUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .fitCenter().override(1000, 1000))
                    .into(imageView);

            Log.d(TAG, "Checking for URLs"+ photosAlbum.get(i).getUrl() );


            //build model renderable
            photoStage = ViewRenderable.builder().setView(context, imageView).build();
            Log.d(TAG, "hiya"+ photoStage );

            completableFutures.add(photoStage);
        }
         Log.d(TAG, "completable futures" + completableFutures );
    }

    public static ArrayList<ViewRenderable> buildViewRenderables(ArrayList<CompletableFuture<ViewRenderable>> completableFutures, Context context) {

        viewRenderables = new ArrayList<>();

        Log.d(TAG, completableFutures.toString());

        // get stages for view renderables
        for (int i = 0; i < completableFutures.size(); i++) {

            final int stage = i;
            // handling each completable future in array
            completableFutures.get(i).handle(
                    (notUsed, throwable) -> {
                        if (throwable != null) {
                            DemoUtils.displayError(context, "Unable to load renderable", throwable);
                            return null;
                        } try {
                            viewRenderable = completableFutures.get(stage).get();
                            hasLoaded.get(stage).set(true);

                        } catch (InterruptedException | ExecutionException ex) {
                            DemoUtils.displayError(context, "Unable to load renderable", ex);
                        }
                        return null;
                    });
            viewRenderables.add(viewRenderable);
        }

        for (int i = 0; i < viewRenderables.size(); i++) {
            Log.d(TAG, "Printing model renderable" + viewRenderable);
            Log.d(TAG, "Model renderables size is " + viewRenderables.size());
        }

        return viewRenderables;
    }

    public static ArrayList<CompletableFuture<ViewRenderable>> getCompletableFutures() {
        return completableFutures;
    }

}
