package com.example.fbuteamproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


public class MainActivity extends AppCompatActivity {

        private static final String TAG = MainActivity.class.getSimpleName();
        private ArFragment arFragment;
        private ViewRenderable testViewRenderable;

        @Nullable
        private ModelRenderable videoRenderable;
        private MediaPlayer mediaPlayer;

        // The color to filter out of the video.
        private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

        // Controls the height of the video in world space.
        private static final float VIDEO_HEIGHT_METERS = 0.85f;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (!checkIsSupportedDeviceOrFinish(this)){
                return;
            }
            ;
            setContentView(R.layout.activity_main);

            //container of the scene, find the arFragment in the layout file
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

            // Create an ExternalTexture for displaying the contents of the video.
            ExternalTexture texture = new ExternalTexture();

            try {
                Canvas canvas = texture.getSurface().lockCanvas(null);
                //playerView.draw(canvas);
                canvas.drawColor(0xff0000ff);
                texture.getSurface().unlockCanvasAndPost(canvas);
            } catch (Surface.OutOfResourcesException t) {
                Log.e(TAG, "lockCanvas failed");
            }

            // Create an Android MediaPlayer to capture the video on the external texture's surface.
            //Should be able to set the texture to YoutubePlayerView here??
           // mediaPlayer = MediaPlayer.create(this, R.raw.lion_chroma);
           // mediaPlayer.setSurface(texture.getSurface());
           // mediaPlayer.setLooping(true);

            // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
            // it can display an ExternalTexture. The material also has an implementation of a chroma key
            // filter.
            ModelRenderable.builder()
                    .setSource(this, R.raw.video_screen)
                    .build()
                    .thenAccept(
                            renderable -> {
                                videoRenderable = renderable;
                                //setYoutubeVideoPlayer texture?
                                renderable.getMaterial().setExternalTexture("videoTexture", texture);
                                renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                            })
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });

            // Build a renderable from a 2D View.
           /* ViewRenderable.builder().setView(this, R.layout.test_view)
                    .build()
                    .thenAccept(renderable -> testViewRenderable = renderable);

            YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                    getFragmentManager().findFragmentById(R.id.youtubeFragment);

            youtubeFragment.initialize(getString(R.string.youtube_api_key),
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            // do any work here to cue video, play video, etc.
                            youTubePlayer.cueVideo("5xVh-7ywKpE");
                        }
                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    });*/

            arFragment.setOnTapArPlaneListener(

                    (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {

                        if (/*testViewRenderable == null || */videoRenderable == null){
                            return;
                        }

                        Anchor anchor = hitresult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        TransformableNode lamp = new TransformableNode(arFragment.getTransformationSystem());
                        lamp.setParent(anchorNode);
                        lamp.setRenderable(testViewRenderable);
                        lamp.select();

                        // Create a node to render the video and add it to the anchor.
                        Node videoNode = new Node();
                        videoNode.setParent(anchorNode);

                        //Create a node to render the text

                        // Set the scale of the node so that the aspect ratio of the video is correct.
                        float videoWidth = 800; //mediaPlayer.getVideoWidth();
                        float videoHeight = 450; //mediaPlayer.getVideoHeight();
                        videoNode.setLocalScale(
                                new Vector3(
                                        VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));

                        // Start playing the video when the first node is placed.
                        if (false && !mediaPlayer.isPlaying()) {
                            mediaPlayer.start();

                            // Wait to set the renderable until the first frame of the  video becomes available.
                            // This prevents the renderable from briefly appearing as a black quad before the video
                            // plays.
                            texture
                                    .getSurfaceTexture()
                                    .setOnFrameAvailableListener(
                                            (SurfaceTexture surfaceTexture) -> {
                                                videoNode.setRenderable(videoRenderable);
                                                texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                                            });
                        } else {
                            videoNode.setRenderable(videoRenderable);
                        }

                    });

        }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

        public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Log.e(TAG, "Sceneform requires Android N or later");
                Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
                activity.finish();
                return false;    }

            String openGlVersionString =
                    ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                            .getDeviceConfigurationInfo()
                            .getGlEsVersion();

            if (Double.parseDouble(openGlVersionString) < 3.0) {
                Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
                Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                        .show();
                activity.finish();
                return false;
            }

            return true;
        }
    }

