package com.example.fbuteamproject.activities;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.Planet;
import com.example.fbuteamproject.utils.DemoUtils;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.fbuteamproject.utils.DemoUtils.checkIsSupportedDeviceOrFinish;

public class VideosActivity extends AppCompatActivity {

    private static final String TAG = VideosActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int RC_PERMISSIONS =0x123;;

    private boolean installRequested;
    
    @Nullable
    private ModelRenderable videoRenderable;
    private ModelRenderable venusRenderable;
    private ModelRenderable jupiterRenderable;
    //private ModelRenderable saturnRenderable;

    //button renderables
    private ViewRenderable buttonPauseRenderable;
    private ViewRenderable buttonResumeRenderable;
    private ViewRenderable buttonStopRenderable;

    private ViewRenderable planetTitlesRenderable;
    private ViewRenderable planetContentsRenderable;

    private MediaPlayer mediaPlayer;

    private GestureDetector gestureDetector;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar = null;

    // True once scene is loaded
    private boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    private boolean hasPlacedComponents = false;

    //Keep track of where the video has been stopped in mediaplayer
    private int pausedAt;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);
    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.7f;
    private float videoWidth;
    private float videoHeight;

    //Completable Futures for model renderables
    CompletableFuture<ModelRenderable> venusStage;
    CompletableFuture<ModelRenderable> jupiterStage;
    //CompletableFuture<ModelRenderable> saturnStage;
    CompletableFuture<ModelRenderable> videoStage;
    CompletableFuture<ViewRenderable> buttonPauseStage;
    CompletableFuture<ViewRenderable> buttonResumeStage;
    CompletableFuture<ViewRenderable> buttonStopStage;

    CompletableFuture<ViewRenderable> planetTitleStage;
    CompletableFuture<ViewRenderable> planetContentsStage;

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

        setupRenderables();

        setupGestureDetector();
        setupTouchListener();
        setupOnUpdateListener();

        // Lastly request CAMERA permission which is required by ARCore.
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
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
                venusStage,
                jupiterStage,
                buttonPauseStage,
                buttonResumeStage,
                buttonStopStage,
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
                                buttonPauseRenderable = buttonPauseStage.get();
                                buttonResumeRenderable = buttonResumeStage.get();
                                buttonStopRenderable = buttonStopStage.get();

                                planetTitlesRenderable = planetTitleStage.get();
                                planetContentsRenderable = planetContentsStage.get();

                                //saturnRenderable = saturnStage.get();
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
        /*saturnStage =
                ModelRenderable
                        .builder()
                        .setSource(this, Uri.parse("13906_Saturn_v1_l3.sfb"))
                        .build();*/
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
        buttonPauseStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.buttonpause)
                        .build();
        buttonResumeStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.buttonresume)
                        .build();
        buttonStopStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.buttonstop)
                        .build();

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            return;
        }

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

        //base node for which everything will be relative to
        Node base = new Node();

        Node videoButtons = new Node();
        setupNode(videoButtons, base, buttonPauseRenderable, new Vector3(-0.8f, 0.5f, 0.0f), new Vector3(0.5f, 0.35f, 0.5f));

        Node videoButtons2 = new Node();
        setupNode(videoButtons2, base, buttonResumeRenderable, new Vector3(-0.8f, 0.8f, 0.0f), new Vector3(0.5f, 0.35f, 0.5f));

        Node videoButtons3 = new Node();
        setupNode(videoButtons3, base, buttonStopRenderable, new Vector3(-0.8f, 1.0f, 0.0f), new Vector3(0.5f, 0.35f, 0.5f));

        Planet venusVisual = new Planet("Venus", "Venus is a goddess", this.getResources().getIdentifier("venus","raw",this.getPackageName()));
        setupNode(venusVisual, base, venusRenderable, new Vector3(-0.5f, 1.5f, 0.0f),new Vector3(0.2f, 0.2f, 0.2f));

        Planet jupiterVisual = new Planet("Jupiter", "Jupiter is a god", this.getResources().getIdentifier("jupiter","raw",this.getPackageName()));
        setupNode(jupiterVisual, base, jupiterRenderable, new Vector3(0.0f, 1.5f, 0.0f), new Vector3(0.2f, 0.2f, 0.2f));

        /*Planet saturnVisual = new Planet("Saturn", "Saturn is another god", this.getResources().getIdentifier("saturn","raw",this.getPackageName()));
        setupNode(saturnVisual, base, saturnRenderable, new Vector3(0.5f, 1.5f, 0.0f), new Vector3(0.2f, 0.2f, 0.2f));*/

        Node planetTitles = new Node();
        setupNode(planetTitles, base, planetTitlesRenderable, new Vector3(-0.5f, 0.5f, -0.2f), new Vector3(0.5f, 0.35f, 0.5f) );

        Node planetContents = new Node();
        setupNode(planetContents, base, planetContentsRenderable, new Vector3(0.5f, 0.5f, -0.2f), new Vector3(0.5f, 0.35f, 0.5f) );

        View planetTitleView = planetTitlesRenderable.getView();
        View planetContentView = planetContentsRenderable.getView();


        setupPlanetTapListenerVideo(venusVisual, jupiterVisual, base, planetTitleView, planetContentView);

        return base;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupPlanetTapListenerVideo(Planet venusVisual, Planet jupiterVisual, Node baseNode, View planetTitleView, View planetContentView) {

        // Create an ExternalTexture for displaying the contents of the video.
        ExternalTexture texture = new ExternalTexture();

        venusVisual.setOnTapListener((hitTestResult, motionEvent) -> {

            playVideo(venusVisual, baseNode, texture);
            changePlanetScreenText(planetTitleView, planetContentView, venusVisual);
        });

        jupiterVisual.setOnTapListener((hitTestResult, motionEvent) -> {

            playVideo(jupiterVisual, baseNode, texture);
            changePlanetScreenText(planetTitleView, planetContentView, jupiterVisual);

        });

        /*saturnVisual.setOnTapListener((hitTestResult, motionEvent) -> {

            playVideo(saturnVisual, baseNode, texture);
        });*/
    }

    private void changePlanetScreenText(View nameView, View contentView, Planet currPlanet){
        ((TextView) nameView.findViewById(R.id.tvTitle) ).setText(currPlanet.getPlanetName() );
        ((TextView) contentView.findViewById(R.id.tvContents) ).setText(currPlanet.getPlanetNotes() );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playVideo(Planet planetVisual, Node baseNode, ExternalTexture texture) {

        stopPlaying();

        setupMediaPlayer(texture, planetVisual.getPlanetVideoResID());

        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();

        Node video = getVideoNode(baseNode);

        setVideoTexture(texture);

        startMediaPlayer(texture, video);
    }

    private void setupMediaPlayer(ExternalTexture texture, int videoResID) {
        // Create an Android MediaPlayer to capture the video on the external texture's surface.
        mediaPlayer = MediaPlayer.create(this, videoResID);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);
    }

    private Node getVideoNode(Node baseNode) {
        Node video = new Node();
        setupNode(video, baseNode, videoRenderable, new Vector3(0.2f, 0.2f, 0.2f), new Vector3(
                VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));
        return video;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setVideoTexture(ExternalTexture texture) {
        videoRenderable.getMaterial().setExternalTexture("videoTexture", texture);
        videoRenderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startMediaPlayer(ExternalTexture texture, Node video) {
        if (!mediaPlayer.isPlaying()) {

            mediaPlayer.start();

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
        if (mediaPlayer != null) {
            pausedAt=mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void resume(View v) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pausedAt);
            mediaPlayer.start();
        } else {
            Toast.makeText(this,"Media player has been stopped", Toast.LENGTH_LONG).show();
        }
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
                        VideosActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

}
