package com.example.fbuteamproject.utils;

import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Asynchronous HTTP request.
 *
 * This object sends an HTTP request asynchronously and calls the supplied callback when
 * the result of the request is available.
 */
public class AsyncHttpRequest {
    private static final String TAG = "PolySample";

    // The URL of the request.
    private URL url;

    // The listener to call when the request is complete.
    private CompletionListener listener;

    // The handler on which to post a call to the listener.
    private Handler handler;

    // If true, the request was started.
    private boolean requestStarted;

    /**
     * Listener for HTTP request completion.
     */
    public interface CompletionListener {
        /**
         * Called to indicate that the asynchronous HTTP request finished successfully.
         * @param responseBody The body of the response.
         */
        void onHttpRequestSuccess(byte[] responseBody);

        /**
         * Called to indicate that there was a failure in the asynchronous HTTP request.
         * @param statusCode The status code, if a response was received. Otherwise, 0.
         * @param message The error message.
         * @param exception The exception that caused the failure, if any. Otherwise, null.
         */
        void onHttpRequestFailure(int statusCode, String message, Exception exception);
    }

    /**
     * Creates a new AsyncHttpRequest for the given URL.
     * @param url The URL of the request.
     * @param handler The handler on which the listener should be called.
     * @param listener The listener to call when the request completes.
     */
    AsyncHttpRequest(String url, Handler handler, CompletionListener listener) {
        this.handler = handler;
        this.listener = listener;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid URL: " + url);
            listener.onHttpRequestFailure(0, "Invalid URL: " + url, ex);
        }
    }

    /**
     * Sends the request.
     *
     * After the request completes, the listener specified in the constructor will be called
     * to report the result of the request. This method does not block, it returns immediately.
     */
    void send() {
        if (requestStarted) {
            throw new IllegalStateException("AsyncHttpRequest can only be sent once.");
        }
        requestStarted = true;
        new Thread(() -> backgroundMain()).start();
    }

    // Main method for background thread.
    private void backgroundMain() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                postFailure(responseCode,
                        "Request to " + url + " failed with HTTP status code " + responseCode, null);
                return;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            copyStream(connection.getInputStream(), outputStream);
            postSuccess(outputStream.toByteArray());
        } catch (Exception ex) {
            postFailure(0, "Exception while processing request to " + url, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Posts a failure callback to the listener.
    private void postFailure(final int statusCode, final String message, final Exception exception) {
        handler.post(() -> listener.onHttpRequestFailure(statusCode, message, exception));
    }

    // Posts a success callback to the listener.
    private void postSuccess(final byte[] responseBody) {
        handler.post(() -> listener.onHttpRequestSuccess(responseBody));
    }

    // Copies the entire contents of the given input stream to the given output stream.
    private static int copyStream(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[16384];
        int totalBytes = 0;
        int bytesReadThisTime;
        while ((bytesReadThisTime = inputStream.read(buffer, 0, buffer.length)) > 0) {
            outputStream.write(buffer, 0, bytesReadThisTime);
            totalBytes += bytesReadThisTime;
        }
        return totalBytes;
    }
}
