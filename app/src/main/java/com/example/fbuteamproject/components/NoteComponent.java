package com.example.fbuteamproject.components;

import android.content.Context;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NoteComponent {

    private static final String TAG = "NoteComponent";
    private static ViewRenderable entityContentRenderable;

    private static boolean hasLoadedContentRenderable;

    private static CompletableFuture<ViewRenderable> buildContentStage(Context context){

        return ViewRenderable
                .builder()
                .setView(context, R.layout.component_entity_contents)
                .build();
    }

    public static ViewRenderable buildContentRenderable(Context context){

        CompletableFuture<ViewRenderable> entityContentStage = buildContentStage(context);


        entityContentStage.handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }
                    try {
                        entityContentRenderable = entityContentStage.get();
                        hasLoadedContentRenderable = true;

                    } catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }
                    return null;
                });


        return entityContentRenderable;
    }

    public static boolean getHasLoadedContentRenderable(){
        return hasLoadedContentRenderable;
    }

}
