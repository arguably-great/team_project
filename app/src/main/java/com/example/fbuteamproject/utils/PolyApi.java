package com.example.fbuteamproject.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.example.fbuteamproject.R;

/**
 * Methods that call the Poly API.
 */
public class PolyApi {

    private static String TAG = "PolySample";

    // The API host.
    private static String HOST = "poly.googleapis.com";

    //url
    private String URL;

    /**
     * Gets the asset with the given ID.
     * @param assetId The ID of the asset to get.
     * @param handler The handler on which to call the listener.
     * @param completionListener The listener to call when the asset request is completed.
     */
    public static void GetAsset(Context context, String assetId, Handler handler,
                                AsyncHttpRequest.CompletionListener completionListener) {

        String API_KEY = context.getString(R.string.poly_api_key);

        // Let's check if the developer (that's you!) correctly replaced the API_KEY with their own
        // API key in this file. If not, complain.
        if (API_KEY.startsWith("***")) {
            Log.e(TAG, "***** API KEY WAS NOT SET.");
            Log.e(TAG, "***** Please enter your API key in PolyApi.java");
            completionListener.onHttpRequestFailure(0, "API Key not set! Check PolyApi.java", null);
            return;
        }

        // Build the URL to the asset. It should be something like:
        //   https://poly.googleapis.com/v1/assets/ASSET_ID_HERE?key=YOUR_API_KEY_HERE
        String url = new Uri.Builder()
                .scheme("https")
                .authority(HOST)
                .appendPath("v1")
                .appendPath("assets")
                .appendPath(assetId)
                .appendQueryParameter("key", API_KEY)
                .build().toString();

        // Send an asynchronous request.
        AsyncHttpRequest request = new AsyncHttpRequest(url, handler, completionListener);
        request.send();
    }
}
