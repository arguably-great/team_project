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

public class PhotoComponent {


    private static final String TAG = "PhotoComponent";
    public static ArrayList<CompletableFuture<ViewRenderable>> completableFutures;
    public static ArrayList<ViewRenderable> viewRenderables;
    public static ViewRenderable viewRenderable;


    public static void buildVenusPhotos(Context context) {

        ArrayList<Photo> data = new ArrayList<>();
        String IMGS[] = {
                "https://i.imgur.com/8mKSLC8.jpg",
                "https://i.imgur.com/9vbrRQm.jpg"
        };

        for (int i = 0; i < IMGS.length; i++) {
            //	Adding images & title to POJO class and storing in Array (our data)
            Photo imageModel = new Photo();
            imageModel.setName("Image " + i);
            imageModel.setUrl(IMGS[i]);
            data.add(imageModel);

        }

        Log.d(TAG, "buildVenusPhotos: "+ data);

        buildStages(data, context);

        buildViewRenderables(completableFutures, context);
    }


     public static ArrayList<CompletableFuture<ViewRenderable>> buildStages(ArrayList<Photo> data, Context context) {

        completableFutures = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {

            //create completable future for the entity
            CompletableFuture<ViewRenderable> photoStage;

            ImageView imageView = new ImageView(context);

            // load photos into glide
            Glide.with(context)
                    .load(data.get(i).getUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .fitCenter())
                    .into(imageView);

            Log.d(TAG, "Yooo"+ data.get(i).getUrl() );

            //build model renderable
            photoStage = ViewRenderable.builder().setView(context, imageView).build();
            Log.d(TAG, "hiya"+ photoStage );

            completableFutures.add(photoStage);

        }


         Log.d(TAG, "completable futures" + completableFutures );
         return completableFutures;
    }



    public static ArrayList<ViewRenderable> buildViewRenderables(ArrayList<CompletableFuture<ViewRenderable>> completableFutures, Context context) {

        viewRenderables = new ArrayList<>();

        Log.d(TAG, completableFutures.toString());


        // get stages for view renderables
        for (int i = 0; i < completableFutures.size(); i++) {

            final int stage = i;
            completableFutures.get(i).handle(
                    (notUsed, throwable) -> {
                        if (throwable != null) {
                            DemoUtils.displayError(context, "Unable to load renderable", throwable);
                            return null;
                        } try {
                            viewRenderable = completableFutures.get(stage).get();
                            //TODO check if each viewRenderable is complete

                        } catch (InterruptedException | ExecutionException ex) {
                            DemoUtils.displayError(context, "Unable to load renderable", ex);
                        }

                        return null;
                    });
            //adding it to top-level modelrenderables array
            viewRenderables.add(viewRenderable);
        }

        for (int i = 0; i < viewRenderables.size(); i++) {
            Log.d(TAG, "Printing model renderable" + viewRenderable);
            Log.d(TAG, "Model renderables size is " + viewRenderables.size());
        }

        return viewRenderables;
    }
}
