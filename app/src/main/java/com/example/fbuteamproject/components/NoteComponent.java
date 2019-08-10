package com.example.fbuteamproject.components;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/* Class to build view renderables used for Notes*/

public class NoteComponent {

    public static final String START_OF_NOTE_LINE = "\n     \u2022  ";
    public static final String END_OF_NOTE_LINE = "\n";

    private static ViewRenderable entityContentRenderable;

    private static CompletableFuture<ViewRenderable> buildContentStage(Context context){

        return ViewRenderable
                .builder()
                .setView(context, R.layout.component_entity_contents)
                .build();
    }

    public static void buildContentRenderable(Context context){

        CompletableFuture<ViewRenderable> entityContentStage = buildContentStage(context);


        entityContentStage.handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }
                    try {
                        entityContentRenderable = entityContentStage.get();

                    } catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }
                    return null;
                });
    }

    public static ViewRenderable getEntityContentRenderable(){
        return entityContentRenderable;
    }

    public static void changeContentView(Config.Entity currEntity, View contentView, boolean shouldScrollToTop){

        File currEntityFile = currEntity.getEntityFile();

        StringBuilder fileText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(currEntityFile));
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                if (lineCount != 0) {
                    fileText.append(START_OF_NOTE_LINE);
                    fileText.append(line);
                    fileText.append(END_OF_NOTE_LINE);
                }
                else{
                    fileText.append("\n");
                    fileText.append(line);
                }
                //Keep track of the lines so that the first line in the file does not have additional spacing
                lineCount++;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("SolarDebug", e.getLocalizedMessage() );
        }

        Log.d("FileDebug", fileText.toString() );

        ( (EditText) contentView.findViewById(R.id.etContents) ).setMovementMethod(new ScrollingMovementMethod() );

        ( (EditText) contentView.findViewById(R.id.etContents) ).setText("");


        ( (EditText) contentView.findViewById(R.id.etContents) ).setText(fileText.toString() );

        if (shouldScrollToTop) {
            contentView.findViewById(R.id.etContents).scrollTo(0, 0);
        }

    }



}
