package com.example.fbuteamproject.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.Planet;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Plane;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PhotoSceneView extends AppCompatActivity {

    private ArSceneView arSceneView;
    private ViewRenderable photoRenderable1;
    private ViewRenderable photoRenderable2;
    private ViewRenderable photoRenderable3;
    private ViewRenderable photoRenderable4;
    private ModelRenderable venusRenderable;
    private ModelRenderable jupiterRenderable;
    private static final String TAG = PhotoSceneView.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    boolean hasFinishedLoading = false;
    boolean hasPlacedFragments = false;
    boolean installRequested;
    private static final int RC_PERMISSIONS = 0x123;
    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    // for models
    CompletableFuture<ModelRenderable> venusStage;
    CompletableFuture<ModelRenderable> jupiterStage;

    // for views
    CompletableFuture<ViewRenderable> photoStage1;
    CompletableFuture<ViewRenderable> photoStage2;
    CompletableFuture<ViewRenderable> photoStage3;
    CompletableFuture<ViewRenderable> photoStage4;



    // base URL for API
    public final static String API_BASE_URL = "http://api.flickr.com/services/rest/?method=flickr.photos.search";

    // parameter name for API key
    public final static String API_KEY_PARAM = "api_key";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.arsceneview);
        arSceneView = findViewById(R.id.ar_holder);


        buildViewRenderables();
        buildPlanetRenderables();
        createCompletableFutures();
        setupGestureDetector();
        arSceneViewOnTouchListener();
        arSceneViewOnUpdateListener();
        requestCameraPermission(this);
    }

    private void arSceneViewOnUpdateListener() {
        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        });
    }

    private void arSceneViewOnTouchListener() {
        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            // If the solar system hasn't been placed yet, detect a tap and then check to see if
                            // the tap occurred on an ARCore plane to place the solar system.
                            if (!hasPlacedFragments) {
                                Log.d(TAG, "Wanting to place Fragments for first time");
                                return gestureDetector.onTouchEvent(event);
                            }

                            // Otherwise return false so that the touch event can propagate to the scene.
                            Log.d(TAG, "The Fragments are already out there, so we wanna" +
                                    " do stuff in the scene");
                            return false;
                        });
    }

    private void setupGestureDetector() {
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                Log.d(TAG, "Just released my finger after having tapped");
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });
    }

    private void buildViewRenderables() {
        photoStage1 = ViewRenderable.builder().setView(this, R.layout.jupiter1).build();
        photoStage2 = ViewRenderable.builder().setView(this, R.layout.jupiter1).build();
        photoStage3 = ViewRenderable.builder().setView(this, R.layout.jupiter1).build();
        photoStage4 = ViewRenderable.builder().setView(this, R.layout.jupiter1).build();
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private void buildPlanetRenderables() {
        venusStage =
                ModelRenderable
                        .builder()
                        .setSource(this, Uri.parse("Venus_1241.sfb"))
                        .build();
        jupiterStage =
                ModelRenderable
                        .builder()
                        .setSource(this, Uri.parse("model.sfb"))
                        .build();
    }

    private void createCompletableFutures() {
        CompletableFuture.allOf(
                photoStage1, photoStage2, photoStage3, photoStage4, venusStage, jupiterStage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                Log.e(TAG, "Throwable found to be non-null in handle method");
                                return null;
                            }

                            try {
                                photoRenderable1 = photoStage1.get();
                                photoRenderable2 = photoStage2.get();
                                photoRenderable3 = photoStage3.get();
                                photoRenderable4 = photoStage4.get();
                                venusRenderable = venusStage.get();
                                jupiterRenderable = jupiterStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                Log.e(TAG, "Unable to load Renderables (IN CATCH STATEMENT)");
                            }

                            return null;
                        });
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            Log.d(TAG, "Not done loading Scene yet");
            return;
        }

        Log.d(TAG, "In onSingleTap and have finished loading the Scene");

        Frame frame = arSceneView.getArFrame();
        if (frame != null) {
            if (!hasPlacedFragments && tryPlaceFragments(tap, frame)) {
                hasPlacedFragments = true;
            }
        }
    }


    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private boolean tryPlaceFragments(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Log.d(TAG, "Creating anchor to try and place Fragments");
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
                    Node fragments = createFragments();
                    anchorNode.addChild(fragments);
                    return true;
                }

            }

        }
        return false;
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private Node createFragments() {

        Log.d(TAG, "In Fragment creation method");

        Node base = new Node();

        Node node1 = new Node();
        node1.setParent(base);
        node1.setRenderable(photoRenderable1);
        node1.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        node1.setLocalPosition(new Vector3(-0.5f, 0.0f, -1.0f));

        Node node2 = new Node();
        node2.setParent(base);
        node2.setRenderable(photoRenderable2);
        node2.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        node2.setLocalPosition(new Vector3(-0.5f, 1.0f, -1.0f));

        Node node3 = new Node();
        node3.setParent(base);
        node3.setRenderable(photoRenderable3);
        node3.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        node3.setLocalPosition(new Vector3(0.5f, 0.0f, -1.0f));

        Node node4 = new Node();
        node4.setParent(base);
        node4.setRenderable(photoRenderable4);
        node4.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        node4.setLocalPosition(new Vector3(0.5f, 1.0f, -1.0f));

        Planet venusVisual = new Planet("Venus", "Venus is a goddess", this.getResources().getIdentifier("venus","raw",this.getPackageName()));
        setupNode(venusVisual, base, venusRenderable, new Vector3(-0.5f, 1.5f, 0.0f),new Vector3(0.2f, 0.2f, 0.2f));

        Planet jupiterVisual = new Planet("Jupiter", "Jupiter is a god", this.getResources().getIdentifier("jupiter","raw",this.getPackageName()));
        setupNode(jupiterVisual, base, jupiterRenderable, new Vector3(0.0f, 1.5f, 0.0f), new Vector3(0.2f, 0.2f, 0.2f));


        return base;
    }


    private Session createArSession(Activity activity, boolean installRequested)
            throws UnavailableException {
        Session session = null;
        // create the session
        if (hasCameraPermission(activity)) {
            switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                case INSTALL_REQUESTED:
                    return null;
                case INSTALLED:
                    break;
            }
            session = new Session(activity);
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
        }
        return session;
    }

    private void setupNode(Node currNode, Node baseNode, Renderable renderable, Vector3 localPos, Vector3 localScale){
        currNode.setParent(baseNode);
        currNode.setRenderable(renderable);
        currNode.setLocalPosition(localPos);
        currNode.setLocalScale(localScale);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView == null) {
            return;
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = createArSession(this, installRequested);
                if (session == null) {
                    installRequested = hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                Log.e(TAG, "Some stuff went down...\n" + e.getLocalizedMessage() );
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Log.e(TAG, "Unable to get camera...");
            finish();
            return;
        }


        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (arSceneView != null) {
            arSceneView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arSceneView != null) {
            arSceneView.destroy();
        }
    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    private void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.CAMERA}, PhotoSceneView.RC_PERMISSIONS);
    }

    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        PhotoSceneView.this.findViewById(android.R.id.content),
                        "Where art thou surfaces?",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }


}

