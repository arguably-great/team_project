package com.example.fbuteamproject.components;

/* Class to build model renderables by making asynchronous requests to
Poly API
 */

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.utils.AsyncHttpRequest;
import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.example.fbuteamproject.utils.PolyApi;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ModelComponent {

    private static final String TAG = "ModelComponent";

    private static ArrayList<CompletableFuture<ModelRenderable>> completableFutures;

    private static ArrayList<ModelRenderable> modelRenderables;

    private static String ASSET_3D; //the url .gltf to retreive the asset

    // The API host.
    private static String HOST = "poly.googleapis.com";

    // Handler for the background thread, to which we post background thread tasks.
    private static Handler backgroundThreadHandler;

    // Our background thread, which does all of the heavy lifting so we don't block the main thread.
    private static HandlerThread backgroundThread;

    private static ModelRenderable modelRenderable;

    private static CompletableFuture<ModelRenderable> modelStage;

    private static Config.Entity currentEntity;

    // True once scene is loaded
    private static boolean hasFinishedLoading;

    public static void makePolyRequest(ArrayList<Config.Entity> entities, Context context) {

        // Create a background thread, where we will do the heavy lifting.
        backgroundThread = new HandlerThread("Worker");
        backgroundThread.start();
        backgroundThreadHandler = new Handler(backgroundThread.getLooper());

        for (int i = 0; i < entities.size(); i++) {

            currentEntity = entities.get(i);

            // Request the asset from the Poly API.
            Log.d(TAG, "Requesting asset "+ currentEntity.getEntityID());

            PolyApi.GetAsset(entities.get(i).getModelId(), backgroundThreadHandler, new AsyncHttpRequest.CompletionListener() {
                @Override
                public void onHttpRequestSuccess(byte[] responseBody) {
                    // Successfully fetched asset information.
                    parseAsset(responseBody, context);
                }
                @Override
                public void onHttpRequestFailure(int statusCode, String message, Exception exception) {
                    // Something went wrong with the request.
                    handleRequestFailure(statusCode, message, exception);
                }
            });
        }
    }

    public static void generateCompletableFutures(ArrayList<Config.Entity> entities, Context context){
        //initialize completableFutures list that will hold the stages
        completableFutures = new ArrayList<>();

        makePolyRequest(entities, context);
    }

    public static int GetFuturesSize(){ return completableFutures.size(); }

    public static ArrayList<CompletableFuture<ModelRenderable>> getCompletableFutures() {return completableFutures;}

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void buildModelStage(Context context) {

        //create completable future for the entity
        CompletableFuture<ModelRenderable> modelStage;

        //build model renderable
        modelStage =
                ModelRenderable
                        .builder()
                        .setSource(context,
                                RenderableSource
                                        .builder()
                                        .setSource(context, Uri.parse(ASSET_3D), RenderableSource.SourceType.GLTF2)
                                        .setScale(0.75f)
                                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                        .build()

                        )
                        .setRegistryId(ASSET_3D)
                        .build();

        completableFutures.add(modelStage);
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

    // NOTE: this runs on the background thread.
    private static void parseAsset(byte[] assetData, Context context) {
        Log.d(TAG, "Got asset response (" + assetData.length + " bytes). Parsing.");
        String assetBody = new String(assetData, Charset.forName("UTF-8"));
        Log.d(TAG, assetBody);
        try {
            JSONObject response = new JSONObject(assetBody);
            String displayName = response.getString("displayName");
            String authorName = response.getString("authorName");
            Log.d(TAG, "Display name: " + displayName);
            Log.d(TAG, "Author name: " + authorName);

            // The asset may have several formats (OBJ, GLTF, FBX, etc). We will look for the OBJ format.
            JSONArray formats = response.getJSONArray("formats");
            boolean foundGLTFFormat = false;
            for (int i = 0; i < formats.length(); i++) {
                JSONObject format = formats.getJSONObject(i);
                if (format.getString("formatType").equals("GLTF2")) {
                    // Found the gltf2 format. The format gives us the URL of the data files that we should
                    // download (which include the OBJ file, the MTL file and the textures). We will now
                    // request those files.
                    requestDataFiles(format, context);
                    Log.d(TAG, "FOUND GLTF FORMAT");
                    foundGLTFFormat = true;
                    break;
                }
            }
            if (!foundGLTFFormat) {
                // If this happens, it's because the asset doesn't have a representation in the OBJ
                // format. Since this simple sample code can only parse OBJ, we can't proceed.
                // But other formats might be available, so if your client supports multiple formats,
                // you could still try a different format instead.
                Log.e(TAG, "Could not find OBJ format in asset.");
            }
        } catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
    }

    // Requests the data files for the GLTF format.
    // NOTE: this runs on the background thread.
    private static void requestDataFiles(JSONObject gltfFormat, Context context) throws JSONException {
        // objFormat has the list of data files for the OBJ format (OBJ file, MTL file, textures).

        // The "root file" is the GLTF.
        JSONObject rootFile = gltfFormat.getJSONObject("root");

        ASSET_3D = rootFile.getString("url");

        Log.d(TAG, "Printing url" + ASSET_3D);

        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                buildModelStage(context);
            }
        });
    }

    // NOTE: this runs on the background thread.
    private static void handleRequestFailure(int statusCode, String message, Exception exception) {
        // NOTE: because this is a simple sample, we don't have any real error handling logic
        // other than just printing the error. In an actual app, this is where you would take
        // appropriate action according to your app's use case. You could, for example, surface
        // the error to the user or retry the request later.
        Log.e(TAG, "Request failed. Status code " + statusCode + ", message: " + message +
                ((exception != null) ? ", exception: " + exception : ""));
        if (exception != null) exception.printStackTrace();
    }

}
