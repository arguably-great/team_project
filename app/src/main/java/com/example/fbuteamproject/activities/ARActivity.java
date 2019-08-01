package com.example.fbuteamproject.activities;

import android.Manifest;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.components.PhotoComponent;
import com.example.fbuteamproject.layouts.PhotoLayout;
import com.example.fbuteamproject.models.Photo;
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
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.fbuteamproject.utils.DemoUtils.checkIsSupportedDeviceOrFinish;

//import com.example.fbuteamproject.layouts.ARComponentsShell;

/* ARActivity that populates the sceneview with model renderables and view renderables
for notes, photos and videos upon clicking on the screen.
*/
public class ARActivity extends AppCompatActivity {

    private static final String TAG = ARActivity.class.getSimpleName();

    private static final int RC_PERMISSIONS =0x123;
    public static int entityClicked;

    private boolean installRequested;

    @Nullable
    private ModelRenderable videoRenderable;
    private ModelRenderable venusRenderable;
    private ModelRenderable jupiterRenderable;

    private ViewRenderable planetTitlesRenderable;
    private ViewRenderable planetContentsRenderable;

    //Initialize ExoPlayer variables
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow = 0;
    private long playbackPosition = 0;


    private GestureDetector gestureDetector;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar;

    public static boolean photoClicked = false;

    // True once scene is loaded
    private boolean hasFinishedLoading;

    // True once the scene has been placed.
    private boolean hasPlacedComponents;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);
    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.7f;

    //Completable Futures for model renderables
    CompletableFuture<ModelRenderable> venusStage;
    CompletableFuture<ModelRenderable> jupiterStage;
    CompletableFuture<ModelRenderable> videoStage;

    // viewrenderables for photos
    private ViewRenderable jupiterRenderable1;
    private ViewRenderable jupiterRenderable2;
    private ViewRenderable jupiterRenderable3;
    private ViewRenderable jupiterRenderable4;
    private ViewRenderable jupiterRenderable5;
    private ViewRenderable jupiterRenderable6;


    CompletableFuture<ViewRenderable> jupiterStage1;
    CompletableFuture<ViewRenderable> jupiterStage2;
    CompletableFuture<ViewRenderable> jupiterStage3;
    CompletableFuture<ViewRenderable> jupiterStage4;
    CompletableFuture<ViewRenderable> jupiterStage5;
    CompletableFuture<ViewRenderable> jupiterStage6;

//    private ViewRenderable venusRenderable1;
//    private ViewRenderable venusRenderable2;
//    private ViewRenderable venusRenderable3;
//    private ViewRenderable venusRenderable4;
//    private ViewRenderable venusRenderable5;
//    private ViewRenderable venusRenderable6;
//
//
//
//    CompletableFuture<ViewRenderable> venusStage1;
//    CompletableFuture<ViewRenderable> venusStage2;
//    CompletableFuture<ViewRenderable> venusStage3;
//    CompletableFuture<ViewRenderable> venusStage4;
//    CompletableFuture<ViewRenderable> venusStage5;
//    CompletableFuture<ViewRenderable> venusStage6;


    CompletableFuture<ViewRenderable> planetTitleStage;
    CompletableFuture<ViewRenderable> planetContentsStage;

    ArrayList<Photo> album;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        //set the content and the layout
        setContentView(R.layout.activity_videos);

        //find the sceneview
        arSceneView = findViewById(R.id.ar_scene_view);



        buildPlanetRenderables();

        buildVideoRenderable();

        buildViewRenderables();


//        album = PhotoComponent.buildAlbumPhotos(this);
//
//        //Log.d(TAG, "here is le album "+ album);
//
//        PhotoComponent.buildStages(album, this);

//        PhotoComponent.buildAlbumPhotos(this);


        album = PhotoComponent.buildAlbumPhotos(this);


        //Log.d(TAG, "here is le album "+ album);

        PhotoComponent.buildStages(album, this);


        setupRenderables();




        // listeners and click
        setupGestureDetector();


        setupTouchListener();
        setupOnUpdateListener();

        // Lastly request CAMERA permission which is required by ARCore.
        requestCameraPermission(this);

    }

    private void setupOnUpdateListener() {
        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
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

    private void setupTouchListener() {
        // Set a touch listener on the Scene to listen for taps.
        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            // If the solar system hasn't been placed yet, detect a tap and then check to see if
                            // the tap occurred on an ARCore plane to place the solar system.
                            if (!hasPlacedComponents) {
                                return gestureDetector.onTouchEvent(event);
                            }

                            // Otherwise return false so that the touch event can propagate to the scene.
                            return false;
                        });
    }

    private void setupGestureDetector() {
        //Gesture detector for tap events
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupRenderables() {
        CompletableFuture.allOf(
                videoStage,
                jupiterStage, jupiterStage1, jupiterStage2, jupiterStage3, jupiterStage4,
                jupiterStage5, jupiterStage6,
                planetTitleStage,
                planetContentsStage)
                .handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }
                            try {
                                videoRenderable = videoStage.get();
                                venusRenderable = venusStage.get();
                                jupiterRenderable = jupiterStage.get();
                                jupiterRenderable1 = jupiterStage1.get();
                                jupiterRenderable2 = jupiterStage2.get();
                                jupiterRenderable3 = jupiterStage3.get();
                                jupiterRenderable4 = jupiterStage4.get();
                                jupiterRenderable5 = jupiterStage5.get();
                                jupiterRenderable6 = jupiterStage6.get();
                                planetTitlesRenderable = planetTitleStage.get();
                                planetContentsRenderable = planetContentsStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;
                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }
                            return null;
                        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buildVideoRenderable() {
        videoStage =
                ModelRenderable
                        .builder()
                        .setSource(this, R.raw.video_screen)
                        .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buildViewRenderables() {

        planetTitleStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.component_planet_title)
                        .build();

        planetContentsStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.component_planet_contents)
                        .build();

        buildJupiterPhotos();

    }



    private void buildJupiterPhotos() {
        jupiterStage1 = ViewRenderable.builder().setView(this, R.layout.jupiter1).build();
        jupiterStage2 = ViewRenderable.builder().setView(this, R.layout.jupiter2).build();
        jupiterStage3 = ViewRenderable.builder().setView(this, R.layout.jupiter3).build();
        jupiterStage4 = ViewRenderable.builder().setView(this, R.layout.jupiter4).build();
        jupiterStage5 = ViewRenderable.builder().setView(this, R.layout.jupiter5).build();
        jupiterStage6 = ViewRenderable.builder().setView(this, R.layout.jupiter6).build();
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
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = DemoUtils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);


                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
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
        if (player != null) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (player != null) {
            releasePlayer();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            return;
        }

//        Log.d(TAG, "onSingleTap: here are my futures"+ PhotoComponent.getCompletableFutures());
//        Log.d(TAG, "onSingleTap: here are my sizes"+ PhotoComponent.getCompletableFuturesSize());
//
//        ArrayList<CompletableFuture<ViewRenderable>> myVar = PhotoComponent.getCompletableFutures();
//
//        ArrayList<ViewRenderable> myRenders = PhotoComponent.buildViewRenderables(myVar, this);
//
//        Log.d(TAG, "onSingleTap: here are my renderables"+ myRenders);


        Frame frame = arSceneView.getArFrame();

        if (frame != null) {
            if (!hasPlacedComponents && tryPlaceComponents(tap, frame)) {
                hasPlacedComponents= true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean tryPlaceComponents (MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    setupAnchor(hit);

                    return true;
                }
            }
        }
        return false;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupAnchor(HitResult hit) {
        // Create the Anchor.
        Anchor anchor = hit.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arSceneView.getScene());
        Node components = createComponents();
        components.setParent(anchorNode);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node createComponents() {

        Node base = new Node();


        Planet venusVisual = new Planet("Venus", "Venus is a goddess", getString(R.string.venus_res), this);
        setupNode(venusVisual, base, venusRenderable, new Vector3(-0.5f, 1.6f, 0.0f),new Vector3(0.2f, 0.2f, 0.2f));

        Planet jupiterVisual = new Planet("Jupiter", "Jupiter is a god", getString(R.string.jupiter_res), this);
        setupNode(jupiterVisual, base, jupiterRenderable, new Vector3(0.5f, 1.6f, 0.0f), new Vector3(0.2f, 0.2f, 0.2f));

        Node planetContents = new Node();
        setupNode(planetContents, base, planetContentsRenderable, new Vector3(0.0f, 1.25f, 0.0f), new Vector3(0.5f, 0.35f, 0.5f) );

        View planetTitleView = planetTitlesRenderable.getView();
        View planetContentView = planetContentsRenderable.getView();



        setupPlanetTapListenerVideo(venusVisual, jupiterVisual, base, planetTitleView, planetContentView);

        return base;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupPlanetTapListenerVideo(Planet venusVisual, Planet jupiterVisual, Node baseNode, View planetTitleView, View planetContentView) {

        // Create an ExternalTexture for displaying the contents of the video.
        ExternalTexture texture = new ExternalTexture();

        // create nodes for photos


        venusVisual.setOnTapListener((hitTestResult, motionEvent) -> {

            playVideo(venusVisual, baseNode, texture);
            changePlanetScreenText(planetTitleView, planetContentView, venusVisual);

//            Log.d(TAG, "LOL" + PhotoComponent.viewRenderables.size());

            Log.d(TAG, "UGH setupPlanetTapListenerVideo: "+ album);

           // PhotoComponent.buildStages(album, this);


            ArrayList<CompletableFuture<ViewRenderable>> photoCompletables = PhotoComponent.getCompletableFutures();

            PhotoComponent.buildViewRenderables(photoCompletables, this);

            // putting renderables in correct layout
            PhotoLayout.photoNodeSetUp(baseNode);


        });

        jupiterVisual.setOnTapListener((hitTestResult, motionEvent) -> {

//            entityClicked = 2;

            playVideo(jupiterVisual, baseNode, texture);
            changePlanetScreenText(planetTitleView, planetContentView, jupiterVisual);

            createPhotoNodes(jupiterRenderable1, jupiterRenderable2, jupiterRenderable3, jupiterRenderable4,
                    jupiterRenderable5, jupiterRenderable6, baseNode);
//                    node1, node2, node3, node4, node5, node6,



        });

    }

    private void createPhotoNodes(
//            Node node1, Node node2, Node node3, Node node4,
//                                  Node node5, Node node6,
                                  ViewRenderable photoRenderable1, ViewRenderable photoRenderable2, ViewRenderable photoRenderable3, ViewRenderable photoRenderable4,
                                  ViewRenderable photoRenderable5, ViewRenderable photoRenderable6, Node baseNode) {

        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();
        Node node5 = new Node();
        Node node6 = new Node();

        // setting up nodes for photos
        node1.setParent(baseNode);
        node1.setRenderable(photoRenderable1);
        node1.setLocalPosition(new Vector3(-1.0f, 1.0f, 0.0f) );
        node1.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        node2.setParent(baseNode);
        node2.setRenderable(photoRenderable2);
        node2.setLocalPosition(new Vector3(-1.5f, 0.66f, 0.0f) );
        node2.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        node3.setParent(baseNode);
        node3.setRenderable(photoRenderable3);
        node3.setLocalPosition(new Vector3(-1.0f, 0.33f, 0.0f) );
        node3.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        node4.setParent(baseNode);
        node4.setRenderable(photoRenderable4);
        node4.setLocalPosition(new Vector3(1.0f, 1.0f, 0.0f) );
        node4.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        node5.setParent(baseNode);
        node5.setRenderable(photoRenderable5);
        node5.setLocalPosition(new Vector3(1.5f, 0.66f, 0.0f) );
        node5.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

        node6.setParent(baseNode);
        node6.setRenderable(photoRenderable6);
        node6.setLocalPosition(new Vector3(1.0f, 0.33f, 0.0f) );
        node6.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));

    }

    private void changePlanetScreenText(View nameView, View contentView, Planet currPlanet){
        ((TextView) nameView).setText(currPlanet.getPlanetName() );
        ((EditText) contentView).setText(currPlanet.getPlanetNotes() );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playVideo(Planet planetVisual, Node baseNode, ExternalTexture texture) {

        stopPlaying();

        setupExoPlayer(texture, planetVisual.getPlanetVideoResID());

        Node video = getVideoNode(baseNode);

        setVideoTexture(texture);

        startExoPlayer(texture, video);

        video.setOnTapListener((hitTestResult, motionEvent) -> {

            if (player == null) {
                Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show();
                return;
            }

            player.setPlayWhenReady(!player.getPlayWhenReady());
        });
    }

    private void setupExoPlayer(ExternalTexture texture, String videoResID) {

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        player.setVideoSurface(texture.getSurface());

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(videoResID);
        MediaSource mediaSource = buildMediaSource(uri);
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

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "minerva"))).
                createMediaSource(uri);
    }

    private Node getVideoNode(Node baseNode) {
        Node video = new Node();

        setupNode(video, baseNode, videoRenderable, new Vector3(0.0f, 0.5f, 0.0f), new Vector3(
                VIDEO_HEIGHT_METERS * 2, VIDEO_HEIGHT_METERS, 1.0f));
        return video;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setVideoTexture(ExternalTexture texture) {
        assert videoRenderable != null;
        videoRenderable.getMaterial().setExternalTexture("videoTexture", texture);
        videoRenderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
    }


    private void startExoPlayer(ExternalTexture texture, Node video) {
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

    private void setupNode(Node currNode, Node baseNode, Renderable renderable, Vector3 localPos, Vector3 localScale){
        currNode.setParent(baseNode);
        currNode.setRenderable(renderable);
        currNode.setLocalPosition(localPos);
        currNode.setLocalScale(localScale);
    }

    public void pause(View v) {
        if (player == null) {
            Toast.makeText(this, "Video has been stopped", Toast.LENGTH_LONG).show();
            return;
        }
        if (!player.getPlayWhenReady()) {
            Toast.makeText(this, "Video has been paused", Toast.LENGTH_LONG).show();
            return;
        }

        player.setPlayWhenReady(!player.getPlayWhenReady());
    }

    public void resume(View v) {

        if (player == null) {
            Toast.makeText(this,"Video is playing" , Toast.LENGTH_LONG).show();
            return;
        }
        if (player.getPlayWhenReady()) {
            Toast.makeText(this, "Video has been paused", Toast.LENGTH_LONG).show();
            return;
        }

        player.setPlayWhenReady(true);
    }


    private void stopPlaying() {
        releasePlayer();
    }

    private void releasePlayer() {
        if (player == null) {
            return;
        }

        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
        player.release();
        player = null;

    }

    public void stop(View v) {
        stopPlaying();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }


    private void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.CAMERA}, ARActivity.RC_PERMISSIONS);
    }


}
