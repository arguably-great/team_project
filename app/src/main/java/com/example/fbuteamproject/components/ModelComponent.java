package com.example.fbuteamproject.components;

/* Class to build model renderables by making asynchronous requests to
Poly API
 */

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ModelComponent {

    private static final String TAG = "ModelComponent";
    private static ArrayList<CompletableFuture<ModelRenderable>> completableFutures;
    private static ArrayList<ModelRenderable> modelRenderables;

    private static ModelRenderable modelRenderable;

    // True once scene is loaded
    private static boolean hasFinishedLoading;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<CompletableFuture<ModelRenderable>> buildModelStages(ArrayList<Config.Entity> entities, Context context) {

        completableFutures = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {

            //create completable future for the entity
            CompletableFuture<ModelRenderable> modelStage;

            //build model renderable
            modelStage =
                    ModelRenderable
                            .builder()
                            .setSource(context, Uri.parse("Venus_1241.sfb"))
                            .build();

            completableFutures.add(modelStage);
        }

        for (int i = 0; i < completableFutures.size(); i++) {
            Log.d(TAG, "Printing completable future");
            Log.d(TAG, String.valueOf(completableFutures.get(i)));
        }

        return completableFutures;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<ModelRenderable> buildModelRenderables(ArrayList<CompletableFuture<ModelRenderable>> myFutures, Context context) {

        modelRenderables = new ArrayList<>();

        for (int i = 0; i < myFutures.size(); i++) {

            int finalI = i;
            myFutures.get(i).handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(context, "Unable to load renderable", throwable);
                                return null;
                            }try {
                                //get it from the completablefuture
                                modelRenderable = myFutures.get(finalI).get();

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(context, "Unable to load renderable", ex);
                            }

                            return null;
                        });

        //adding it to top-level modelrenderables array
        modelRenderables.add(modelRenderable);
    }

    for (int i = 0; i < modelRenderables.size(); i++) {
        Log.d(TAG, "Printing model renderable");
        Log.d(TAG, "Model renderables size is " + modelRenderables.size());
    }

        return modelRenderables;
    }

    public static CompletableFuture<ModelRenderable>[] convert(ArrayList<CompletableFuture<ModelRenderable>> myList){
        return myList.toArray(new CompletableFuture[myList.size()]);
    }


}
