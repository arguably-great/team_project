package com.example.fbuteamproject.components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/* Class to set-up Video player */
public class VideoComponent {

    private static final String TAG = "VideoComponent";

    private static ModelRenderable videoRenderable;
    private static CompletableFuture<ModelRenderable> videoStage;

    //Initialize ExoPlayer variables
    private static SimpleExoPlayer myPlayer;
    private static boolean playWhenReady;
    private static int currentWindow = 0;
    private static long playbackPosition = 0;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

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

        return videoRenderable;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void playVideo(ExternalTexture texture, Config.Entity currEntity, Node videoNode, Context context, SimpleExoPlayer player) {

        myPlayer = player;

        if (currEntity.getVideoURL().substring(0, 4).equals("file")) {

            prepareExoPlayerFromFileUri(Uri.parse(currEntity.getVideoURL()));

        } else if (currEntity.getVideoURL().substring(0, 4).equals("http")) {

            setupVideoSource(currEntity.getVideoURL(), context);

        } else {
            Toast.makeText(context, "Cannot find video", Toast.LENGTH_LONG).show();
            return;
        }

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

    private static void releasePlayer() {
        if (myPlayer == null) {
            return;
        }

        playbackPosition = myPlayer.getCurrentPosition();
        currentWindow = myPlayer.getCurrentWindowIndex();
        playWhenReady = myPlayer.getPlayWhenReady();

    }

    private static void setupVideoSource(String videoResID, Context context) {

        if (myPlayer == null) {
            Log.d(TAG, "Player not ready");
            return;
        }

        myPlayer.setPlayWhenReady(playWhenReady);
        myPlayer.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(videoResID);
        MediaSource mediaSource = buildMediaSource(uri, context);
        myPlayer.prepare(mediaSource, true, false);

        myPlayer.addVideoListener(new VideoListener() {
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


    private static void prepareExoPlayerFromFileUri(Uri uri){
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        myPlayer.prepare(audioSource);
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

        if (myPlayer == null) {
            Log.d(TAG, "Player not ready");
            return;
        }

        if (!myPlayer.getPlayWhenReady()) {
            myPlayer.setPlayWhenReady(true);
            myPlayer.addListener(new Player.DefaultEventListener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.d(TAG, "onLoadingChanged " + isLoading);
                    super.onLoadingChanged(isLoading);
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    super.onPlayerStateChanged(playWhenReady, playbackState);
                    Log.d(TAG, "onPlayerStateChanged " + playbackState);
                }
            });

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
