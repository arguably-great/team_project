package com.example.fbuteamproject.components;

import android.content.Context;
import android.util.Log;

import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/* Class to build view renderables used for Photos */

public class PhotoComponent {


    private static final String TAG = "PhotoComponent";
    public static ArrayList<ViewRenderable> viewRenderables;
    private static ViewRenderable viewRenderable;


    public static void buildViewRenderable(CompletableFuture<ViewRenderable> photoStage, Context context, Config.Entity currentEntity) {


        // handling each completable future in array
        photoStage.handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    } try {
                        viewRenderable = photoStage.get();
                        currentEntity.getEntityPhotos().add(viewRenderable);

                        Log.d(TAG, "CURRENT ENTITY PHOTOS IS " + currentEntity.getEntityPhotos());

                    } catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }
                    return null;
                });


        }

    }
