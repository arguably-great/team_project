package com.example.fbuteamproject.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.ARComponentsShell;
import com.example.fbuteamproject.models.Planet;
import com.example.fbuteamproject.utils.DemoUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.fbuteamproject.utils.DemoUtils.checkIsSupportedDeviceOrFinish;

public class VideosActivity extends AppCompatActivity {

    private static final String TAG = VideosActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int RC_PERMISSIONS =0x123;

    private boolean installRequested;
    
    @Nullable
    private ModelRenderable videoRenderable;
    private ModelRenderable venusRenderable;
    private ModelRenderable jupiterRenderable;

    // button renderables
    private ViewRenderable buttonPauseRenderable;
    private ViewRenderable buttonResumeRenderable;
    private ViewRenderable buttonStopRenderable;

    private ViewRenderable planetTitlesRenderable;
    private ViewRenderable planetContentsRenderable;

    //Initialize ExoPlayer variables
    SimpleExoPlayer player;
    PlayerView playerView;
    boolean playWhenReady;
    int currentWindow = 0;
    long playbackPosition = 0;

    private GestureDetector gestureDetector;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar = null;

    // True once scene is loaded
    private boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    private boolean hasPlacedComponents = false;

    //Keep track of where the video has been stopped in mediaplayer
   /* private int pausedAt;
    private int videoHeight;
    private int videoWidth;
*/
    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);
    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.7f;

    //Completable Futures for model renderables
    CompletableFuture<ModelRenderable> venusStage;
    CompletableFuture<ModelRenderable> jupiterStage;
    CompletableFuture<ModelRenderable> videoStage;
    CompletableFuture<ViewRenderable> buttonPauseStage;
    CompletableFuture<ViewRenderable> buttonResumeStage;
    CompletableFuture<ViewRenderable> buttonStopStage;

    // viewrenderables for photos
    private ViewRenderable photoRenderable1;
    private ViewRenderable photoRenderable2;
    private ViewRenderable photoRenderable3;
    private ViewRenderable photoRenderable4;


    CompletableFuture<ViewRenderable> photoStage1;
    CompletableFuture<ViewRenderable> photoStage2;
    CompletableFuture<ViewRenderable> photoStage3;
    CompletableFuture<ViewRenderable> photoStage4;

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


        //initializing playerView for Exoplayer
        View layout = getLayoutInflater().inflate(R.layout.player_view, null);
        playerView = (PlayerView) layout.findViewById(R.id.video_view);



        buildPlanetRenderables();

        buildVideoRenderable();

        buildViewRenderables();

        setupRenderables();

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
                venusStage,
                jupiterStage, photoStage1, photoStage2, photoStage3, photoStage4,
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

                                photoRenderable1 = photoStage1.get();
                                photoRenderable2 = photoStage2.get();
                                photoRenderable3 = photoStage3.get();
                                photoRenderable4 = photoStage4.get();

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

        photoStage1 = ViewRenderable.builder().setView(this, R.layout.test_ar1).build();
        photoStage2 = ViewRenderable.builder().setView(this, R.layout.test_ar2).build();
        photoStage3 = ViewRenderable.builder().setView(this, R.layout.test_ar1).build();
        photoStage4 = ViewRenderable.builder().setView(this, R.layout.test_ar1).build();

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
                    //TODO set up exoPlayer again when session is resumed or restarted

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


        String genericPlanetText = "";

        genericPlanetText += "Name: PLANET_NAME\n" +
                "Distance from Sun: SUN_DIST\n" +
                "Age: PLANET_AGE\n" +
                "Padding: PADDDDDD\n" +
                "More Padding: PADDDD\n" +
                "Even MoRe PaDdInG: PADDDD\n" +
                "Padding: PADDDDDD\n" +
                "More Padding: PADDDD\n" +
                "Even MoRe PaDdInG: PADDDD\n";



        Planet venusVisual = new Planet("Venus", "VENUS TAPPED\n" + genericPlanetText, getString(R.string.venus_res), this );
        venusVisual.setRenderable(venusRenderable);

        Planet jupiterVisual = new Planet("Jupiter", "JUPITER TAPPED\n" + genericPlanetText, getString(R.string.jupiter_res), this);
        jupiterVisual.setRenderable(jupiterRenderable);

        Node photo1 = new Node();
        photo1.setRenderable(photoRenderable1);

        Node photo2 = new Node();
        photo2.setRenderable(photoRenderable1);

        Node photo3 = new Node();
        photo3.setRenderable(photoRenderable1);

        Node photo4 = new Node();
        photo4.setRenderable(photoRenderable2);

        Node photo5 = new Node();
        photo5.setRenderable(photoRenderable2);

        Node photo6 = new Node();
        photo6.setRenderable(photoRenderable2);

        Node videoNode = new Node();

        Node planetContents = new Node();
        planetContents.setRenderable(planetContentsRenderable);

        //Organizes all the components relative to each other
        Node base = new ARComponentsShell(venusVisual, jupiterVisual, photo1,
                photo2, photo3, photo4, photo5, photo6, videoNode, planetContents);



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

    }

    private void changePlanetScreenText(View nameView, View contentView, Planet currPlanet){

        File currPlanetFile = currPlanet.getPlanetFile();

        StringBuilder fileText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(currPlanetFile) );
            String line;

            while ((line = br.readLine()) != null) {
                fileText.append(line);
                fileText.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("FileDebug", e.getLocalizedMessage() );
        }

        Log.d("FileDebug", fileText.toString() );



        ( (TextView) contentView.findViewById(R.id.tvContents) ).setMovementMethod(new ScrollingMovementMethod() );

        ( (TextView) contentView.findViewById(R.id.tvContents) ).setText(fileText.toString() );

//        ((TextView) nameView).setText(currPlanet.getPlanetName() );
//        ((EditText) contentView).setText(currPlanet.getPlanetNotes() );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playVideo(Planet planetVisual, Node baseNode, ExternalTexture texture) {

        stopPlaying();

        setupExoPlayer(texture, planetVisual.getPlanetVideoResID());

        Node video = getVideoNode(baseNode);

        setVideoTexture(texture);

        startExoPlayer(texture, video);
    }

    private void setupExoPlayer(ExternalTexture texture, String videoResID) {

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);
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
        if (player != null) {
            hideSystemUi();
            if (player.getPlayWhenReady()){
                player.setPlayWhenReady(!player.getPlayWhenReady());
            } else {
                Toast.makeText(this, "Video has been paused", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Video has been stopped", Toast.LENGTH_LONG).show();
        }
    }

    public void resume(View v) {
        if (player != null) {
            hideSystemUi();
            if (!player.getPlayWhenReady()){
                player.setPlayWhenReady(true);
            } else {
                Toast.makeText(this, "Video is playing", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Video has been stopped", Toast.LENGTH_LONG).show();
        }
    }

    private void stopPlaying() {
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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


    private void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.CAMERA}, VideosActivity.RC_PERMISSIONS);
    }

}
