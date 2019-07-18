package com.example.fbuteamproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


public class MainActivity extends AppCompatActivity {

        private static final String TAG = MainActivity.class.getSimpleName();
        private ArFragment arFragment;
        private ViewRenderable testViewRenderable;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (!checkIsSupportedDeviceOrFinish(this)){
                return;
            }

            setContentView(R.layout.activity_main);

            //container of the scene, find the arFragment in the layout file
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

            // Build a renderable from a 2D View.
            ViewRenderable.builder().setView(this, R.layout.test_view)
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
                    });

            arFragment.setOnTapArPlaneListener(

                    (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {

                        if (testViewRenderable == null){
                            return;
                        }

                        Anchor anchor = hitresult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        TransformableNode lamp = new TransformableNode(arFragment.getTransformationSystem());
                        lamp.setParent(anchorNode);
                        lamp.setRenderable(testViewRenderable);
                        lamp.select();
                    });

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

