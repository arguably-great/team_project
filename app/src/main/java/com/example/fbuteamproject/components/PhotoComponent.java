package com.example.fbuteamproject.components;

import android.content.Context;
import android.util.Log;

import com.example.fbuteamproject.activities.ARActivity;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.fbuteamproject.activities.ARActivity.completableFutures;

/* Class to build view renderables used for Photos */

public class PhotoComponent {


    private static final String TAG = "PhotoComponent";
    public static ArrayList<ViewRenderable> viewRenderables;
    private static ViewRenderable viewRenderable;
    private static ArrayList<AtomicBoolean> hasLoaded;


    public static ArrayList<ViewRenderable> buildViewRenderables(ArrayList<CompletableFuture<ViewRenderable>> completableFutures, Context context) {

        viewRenderables = new ArrayList<>();

        // get stages for view renderables
        for (int i = 0; i < 6; i++) {

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

            ARActivity.loadPhotoCount--;
            Log.d("COUNT", "Photo count Decremented to: " + ARActivity.loadPhotoCount);
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
