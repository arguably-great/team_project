package com.example.fbuteamproject.components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.Planet;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VideoComponent {

    private static final String TAG = "VideoComponent";

    private static ModelRenderable videoRenderable;
    private static CompletableFuture<ModelRenderable> videoStage;

    //Initialize ExoPlayer variables
    private static SimpleExoPlayer player;
    private static boolean playWhenReady;
    private static int currentWindow = 0;
    private static long playbackPosition = 0;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);
    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.7f;

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

    public static void setUpVideo(Planet planetVisual, Node videoNode, Context context) {
        // Create an ExternalTexture for displaying the contents of the video.
        ExternalTexture texture = new ExternalTexture();

        playVideo(texture, planetVisual, videoNode, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void playVideo(ExternalTexture texture, Planet planetVisual, Node videoNode, Context context) {

        stopPlaying();

        setupExoPlayer(texture, planetVisual.getPlanetVideoResID(), context);

        setVideoTexture(texture, context);

        startExoPlayer(texture, videoNode);

        videoNode.setOnTapListener((hitTestResult, motionEvent) -> {

            if (player == null) {
                Toast.makeText(context, "Video not found", Toast.LENGTH_LONG).show();
                return;
            }

            player.setPlayWhenReady(!player.getPlayWhenReady());
        });
    }

    private static void stopPlaying() {
        releasePlayer();
    }

    private static void releasePlayer() {
        if (player == null) {
            return;
        }

        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
        player.release();
        player = null;

    }

    private static void setupExoPlayer(ExternalTexture texture, String videoResID, Context context) {

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());

        player.setVideoSurface(texture.getSurface());
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(videoResID);
        MediaSource mediaSource = buildMediaSource(uri, context);
        player.prepare(mediaSource, true, false);

        player.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.d(TAG, "play video listener video size changed");
            }
            @Override
            public void onRenderedFirstFrame() {
                Log.d(TAG,"play video listener render first frame");
            }
        });
    }

    private static MediaSource buildMediaSource(Uri uri, Context context) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "minerva"))).
                createMediaSource(uri);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void setVideoTexture(ExternalTexture texture, Context context) {

        if (videoRenderable == null || videoRenderable.getMaterial() == null || texture == null) {
            Toast.makeText(context, "Video not found", Toast.LENGTH_LONG).show();
            return;
        }

        videoRenderable.getMaterial().setExternalTexture("videoTexture", texture);
        videoRenderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
    }

    private static void startExoPlayer(ExternalTexture texture, Node video) {
        if (!player.getPlayWhenReady()) {

            player.setPlayWhenReady(true);

            texture.getSurfaceTexture().setOnFrameAvailableListener(
                    (SurfaceTexture surfaceTexture) -> {
                        video.setRenderable(videoRenderable);
                        texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                    });
        } else {
            video.setRenderable(videoRenderable);
        }
    }

}
