package com.example.fbuteamproject.components;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VideoComponent {

    private static final String TAG = "VideoComponent";

    private static ModelRenderable videoRenderable;
    private static CompletableFuture<ModelRenderable> videoStage;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<ModelRenderable> buildVideoStage(Context context) {
        videoStage =
                ModelRenderable
                        .builder()
                        .setSource(context, R.raw.video_screen)
                        .build();

        Log.d(TAG, "Printing completable future for VIDEO");

        return videoStage;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ModelRenderable buildModelRenderable(CompletableFuture<ModelRenderable> myVideoFuture, Context context) {

        myVideoFuture.handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }try {
                        //get it from the completablefuture
                        videoRenderable = myVideoFuture.get();

                    } catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }

                    return null;
                });
        Log.d(TAG, "Printing video renderable for VIDEO");

        return videoRenderable;
    }


}
