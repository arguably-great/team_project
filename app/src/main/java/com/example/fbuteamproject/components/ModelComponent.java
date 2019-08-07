package com.example.fbuteamproject.components;

/* Class to build model renderables by making asynchronous requests to
Poly API*/

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.webkit.URLUtil;

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

    private static ModelRenderable modelRenderable;

    private static HandlerThread backgroundThread;

    private static Handler backgroundThreadHandler;

    private static ModelCallBacksFinishedListener listener;
    static int count;

    public static void makePolyRequest(Config.Entity currentEntity, Context context) {

        // Create a background thread, where we will do the heavy lifting.
        backgroundThread = new HandlerThread("Worker");
        backgroundThread.start();
        backgroundThreadHandler = new Handler(backgroundThread.getLooper());

//        for (int i = 0; i < entities.size(); i++) {

        //Config.Entity currentEntity = entities.get(i);

        // Request the asset from the Poly API.
        Log.d(TAG, "Requesting asset "+ currentEntity.getEntityName());

        PolyApi.GetAsset(context, currentEntity.getModelId(),backgroundThreadHandler, new AsyncHttpRequest.CompletionListener() {
            @Override
            public void onHttpRequestSuccess(byte[] responseBody) {
                // Successfully fetched asset information.
                parseAsset(responseBody, context, currentEntity);
            }
            @Override
            public void onHttpRequestFailure(int statusCode, String message, Exception exception) {
                // Something went wrong with the request.
                handleRequestFailure(statusCode, message, exception);
            }
        });
//        }
    }

    public static void generateCompletableFuturesandModelRenderables(ArrayList<Config.Entity> entities, Context context){

        for (int i = 0; i < entities.size(); i++) {

            String modelId = entities.get(i).getModelId();

            if (modelId.substring(modelId.length() - 4).equals(".sfb")) {

                Log.d(TAG, "FOUND SFB FILE, DEFAULT TO LOCAL");

                buildModelStage(context, modelId, entities.get(i));

            } else {

                Log.d(TAG, "FOUND ASSET 3D ID, MAKE NETWORK CALL");

                makePolyRequest(entities.get(i), context);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void buildModelStage(final Context context, String assetURL, final Config.Entity currentEntity) {

        //create completable future for the entity
        CompletableFuture<ModelRenderable> modelStage;

        boolean isHttpURL = URLUtil.isHttpUrl(assetURL);

        if (isHttpURL) {
            modelStage =
                    ModelRenderable
                            .builder()
                            .setSource(context,
                                    RenderableSource
                                            .builder()
                                            .setSource(context, Uri.parse(assetURL), RenderableSource.SourceType.GLTF2)
                                            .setScale(0.75f)
                                            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                            .build()

                            )
                            .setRegistryId(assetURL)
                            .build();
        } else {
            modelStage = ModelRenderable.builder().setSource(context, Uri.parse(assetURL)).build();
        }

        //increment the completable future count
        count++;
        Log.d("COUNT", "Count Incremented to: " + count);

        currentEntity.setEntityStage(modelStage);

        modelStage.thenApply(modelRenderable -> {

            buildModelRenderable(currentEntity, context);
            count--;
            Log.d("COUNT", "Count Decremented to: " + count);
            if (count == 0) {
                listener.startNodeCreation(Config.AppConfig.getAppConfig(context).entities);
            }
            return null;
        });

        Log.d("COUNT", "Count Decremented to: " + count);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void buildModelRenderable(Config.Entity currentEntity, Context context) {

        CompletableFuture<ModelRenderable> currEntityStage = currentEntity.getEntityStage();

        currentEntity.getEntityStage().handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }
                    try {
                        //get it from the completablefuture
                        modelRenderable = currEntityStage.get();
                        currentEntity.setEntityModel(modelRenderable);

                    } catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }

                    return null;
                });

    }

    // NOTE: this runs on the background thread.
    private static void parseAsset(byte[] assetData, Context context, Config.Entity currentEntity) {
        Log.d(TAG, "Got asset response (" + assetData.length + " bytes). Parsing.");
        String assetBody = new String(assetData, Charset.forName("UTF-8"));
        //Log.d(TAG, assetBody);
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
                    // download (which include the OBJ file, the MTL file and the textures).
                    requestDataFiles(format, context, currentEntity);
                    Log.d(TAG, "FOUND GLTF FORMAT");
                    foundGLTFFormat = true;
                    break;
                }
            }
            if (!foundGLTFFormat) {
                Log.e(TAG, "Could not find OBJ format in asset.");
            }
        } catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
    }

    // Requests the data files for the GLTF format.
    // NOTE: this runs on the background thread.
    private static void requestDataFiles(JSONObject gltfFormat, Context context, Config.Entity currentEntity) throws JSONException {
        // objFormat has the list of data files for the OBJ format (OBJ file, MTL file, textures).

        // The "root file" is the GLTF.
        JSONObject rootFile = gltfFormat.getJSONObject("root");

        String ASSET_3D = rootFile.getString("url");

        Log.d(TAG, "Printing url" + ASSET_3D);

        ((Activity)context).runOnUiThread(() -> {
            Log.d("UI thread", "I am the UI thread");
            buildModelStage(context, ASSET_3D, currentEntity);
        });
    }

    // NOTE: this runs on the background thread.
    private static void handleRequestFailure(int statusCode, String message, Exception exception) {
        //TODO more precise error handling logic
        Log.e(TAG, "Request failed. Status code " + statusCode + ", message: " + message +
                ((exception != null) ? ", exception: " + exception : ""));
        if (exception != null) exception.printStackTrace();
    }

    public static void setListener(ModelCallBacksFinishedListener newListener){
        listener = newListener;
    }


    public interface ModelCallBacksFinishedListener {
        void startNodeCreation(ArrayList<Config.Entity> entities);
    }


}
