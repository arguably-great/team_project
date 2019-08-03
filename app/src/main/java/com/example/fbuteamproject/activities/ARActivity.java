package com.example.fbuteamproject.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import com.example.fbuteamproject.components.ModelComponent;
import com.example.fbuteamproject.components.NoteComponent;
import com.example.fbuteamproject.components.PhotoComponent;
import com.example.fbuteamproject.components.VideoComponent;
import com.example.fbuteamproject.layouts.EntityLayout;
import com.example.fbuteamproject.layouts.NoteLayout;
import com.example.fbuteamproject.layouts.PhotoLayout;
import com.example.fbuteamproject.layouts.VideoLayout;
import com.example.fbuteamproject.models.Photo;
import com.example.fbuteamproject.models.Planet;
import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.example.fbuteamproject.wrappers.EntityWrapper;
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
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.fbuteamproject.utils.DemoUtils.checkIsSupportedDeviceOrFinish;

/* ARActivity that populates the sceneview with model renderables and view renderables
for notes, photos and videos upon clicking on the screen.
*/

public class ARActivity extends AppCompatActivity implements EntityWrapper.EntityChangeListener {

    private static final String TAG = ARActivity.class.getSimpleName();

    private static final int RC_PERMISSIONS =0x123;

    private final int SPEECH_REQUEST_CODE = 100;

    private Planet currPlanetSelected;

    private boolean installRequested;

    @Nullable
    private ModelRenderable videoRenderable;

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
    //Completable Futures for model renderables

    CompletableFuture<ModelRenderable> venusStage;
    CompletableFuture<ModelRenderable> jupiterStage;
    CompletableFuture<ModelRenderable> videoStage;
    ModelRenderable jupiterRenderable;

    CompletableFuture<ViewRenderable> planetTitleStage;
    CompletableFuture<ViewRenderable> planetContentsStage;
    private ArrayList<Config.Entity> appEntities;
    private boolean hasTriedLoadingEntityRenderables;

    private boolean hasPlayedVideo;

    private EntityWrapper currEntitySelected;
    private EntityLayout entityLayout;
    private VideoLayout videoLayout;
    private NoteLayout noteLayout;


    private ViewRenderable entityContentRenderable;

    private ArrayList<ModelRenderable> myRenderables;

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
        setContentView(R.layout.ar_activity);

        //find the sceneview
        arSceneView = findViewById(R.id.ar_scene_view);


        videoStage = VideoComponent.buildVideoStage(this);
        videoRenderable = VideoComponent.buildModelRenderable(videoStage, this);

        NoteComponent.buildContentRenderable(this);

        Config.AppConfig configuration = Config.AppConfig.getAppConfig(this);
        appEntities = configuration.entities;

        Log.d("CONTEXT", Config.AppConfig.getContext().toString() );

//        ModelComponent.generateCompletableFuturesandModelRenderables(appEntities, this);

        entityLayout = new EntityLayout();

        buildViewRenderables();
        setupRenderables();

        currEntitySelected = new EntityWrapper();
        currEntitySelected.setListener(this);


        // TODO have photos set up on create
        // creating photo albums and stages
        album = PhotoComponent.buildAlbumPhotos(this);
        //Log.d(TAG, "here is the album "+ album);
        PhotoComponent.buildStages(album, this);



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
                            Log.d(TAG, "Trying to propagate touch input to the Scene");
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
                planetTitleStage,
                planetContentsStage)
                .handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }
                            try {
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

    /*@RequiresApi(api = Build.VERSION_CODES.N)
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
    }*/

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
                        .setView(this, R.layout.component_entity_title)
                        .build();

        planetContentsStage =
                ViewRenderable
                        .builder()
                        .setView(this, R.layout.component_entity_contents)
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

//        if (!hasFinishedLoading && !NoteComponent.getHasLoadedContentRenderable()) {
//            // We can't do anything yet.
//            return;
//        }
//
//        Log.d(TAG, "Current size of completableFutures: " + ModelComponent.GetFuturesSize());
//
//        Log.d(TAG, "Printing completable Futures");
//
//        if (ModelComponent.GetFuturesSize() == appEntities.size()) {
//
//            ArrayList<CompletableFuture<ModelRenderable>> myCompFutures = ModelComponent.getCompletableFutures();
//
//            for (int i = 0; i < ModelComponent.GetFuturesSize(); i++) {
//
//                Log.d(TAG, "My completable future" + myCompFutures.get(i));
//            }
//        }
//
//        myRenderables = ModelComponent.buildModelRenderables(ModelComponent.getCompletableFutures(), this);
//
//        for (int i = 0; i < myRenderables.size(); i++) {
//            Log.d(TAG, "Printing model renderable" + myRenderables.get(i));
//        }
//
//        if (!hasFinishedLoading) {
//            // We can't do anything yet.
//            return;
//        }
//
//        Frame frame = arSceneView.getArFrame();
//        if (frame != null) {
//            if (!hasPlacedComponents && tryPlaceComponents(tap, frame)) {
//                hasPlacedComponents = true;
//            }
//        }
//    }
        ModelComponent.generateCompletableFuturesandModelRenderables(appEntities, this);

        Frame frame = arSceneView.getArFrame();
            if (frame != null) {
                if (!hasPlacedComponents && tryPlaceComponents(tap, frame)) {
                    hasPlacedComponents = true;
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

        Node baseNode = new Node();
        entityLayout.setParent(baseNode);

        videoLayout = new VideoLayout(videoRenderable);
        videoLayout.setParent(baseNode);

        noteLayout = new NoteLayout(NoteComponent.getEntityContentRenderable());
        noteLayout.setParent(baseNode);

        //This coming line should trigger the onEntityChanged method from the included interface
        currEntitySelected.setEntity(appEntities.get(0));


        //Traverse through all of the Entities and assign their onTaps (basically just trigger listener)
        for(Config.Entity currEntity: appEntities){
            currEntity.setOnTapListener(new Node.OnTapListener() {
                @Override
                public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                    if (currEntitySelected.getEntity() != currEntity){
                        currEntitySelected.setEntity(currEntity);
                    }
                    else{
                        Toast.makeText(ARActivity.this, "Tapped on the same Entity", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //TODO - Doing this just because the code below is not needed and this is TESTING
            //TODO - BUT we also want to keep the code from before to understand what we were doing
        if (true) {
            return baseNode;
        }

        // putting renderables in correct layout
        PhotoLayout.photoNodeSetUp(baseNode);


        Planet venusVisual = new Planet("Venus", "Venus is a goddess", getString(R.string.venus_res), this );
//        venusVisual.setRenderable(modelRenderables.get(0) );

        Planet jupiterVisual = new Planet("Jupiter", "Jupiter is a god", getString(R.string.jupiter_res), this);
        jupiterVisual.setRenderable(jupiterRenderable);


        //TODO DUMMY CODE TO TEST FUNCTIONALITY OF VIDEOCOMPONENT
        //Node videoNode = new Node();

        Node planetContents = new Node();
        planetContents.setRenderable(planetContentsRenderable);

        View planetTitleView = planetTitlesRenderable.getView();
        View planetContentView = planetContentsRenderable.getView();

        //Creating Intent for Speech-To-Text
        planetContentView.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now...");

                try {
                    startActivityForResult(speechIntent, SPEECH_REQUEST_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


        //setupPlanetTapListenerVideo(venusVisual, jupiterVisual, base, planetTitleView, planetContentView, videoNode);
        //return videoNode;

        return baseNode;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupPlanetTapListenerVideo(Planet venusVisual, Planet jupiterVisual, Node baseNode, View planetTitleView, View planetContentView, Node videoNode) {

        // Create an ExternalTexture for displaying the contents of the video.
        ExternalTexture texture = new ExternalTexture();


        venusVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            currPlanetSelected = venusVisual;


            playVideo(venusVisual, texture, videoNode);
            changePlanetScreenText(planetTitleView, planetContentView, venusVisual);

            // more photo testing
//            Log.d(TAG, "PhotoComponent view renderables" + PhotoComponent.viewRenderables.size());
//            Log.d(TAG, "setupPlanetTapListenerVideo: "+ album);
//            PhotoComponent.buildStages(album, this);


            // TODO have photo layout done on entity tap

            ArrayList<CompletableFuture<ViewRenderable>> photoCompletables = PhotoComponent.getCompletableFutures();
            PhotoComponent.buildViewRenderables(photoCompletables, this);

            // putting renderables in correct layout
            PhotoLayout.photoNodeSetUp(baseNode);


        });

        jupiterVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            currPlanetSelected = jupiterVisual;


            playVideo(jupiterVisual, texture, videoNode);


            changePlanetScreenText(planetTitleView, planetContentView, jupiterVisual);

//            createPhotoNodes(jupiterRenderable1, jupiterRenderable2, jupiterRenderable3, jupiterRenderable4,
//                    jupiterRenderable5, jupiterRenderable6, baseNode);


        });

    }

    private void changePlanetScreenText(View nameView, View contentView, Planet currPlanet){

        File currPlanetFile = currPlanet.getPlanetFile();

        StringBuilder fileText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(currPlanetFile));
            String line;

            while ((line = br.readLine()) != null) {
                fileText.append(line);
                fileText.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("SolarDebug", e.getLocalizedMessage() );
        }

        Log.d("FileDebug", fileText.toString() );

        ( (TextView) contentView.findViewById(R.id.tvContents) ).setMovementMethod(new ScrollingMovementMethod() );

        ( (TextView) contentView.findViewById(R.id.tvContents) ).setText(fileText.toString() );



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playVideo(Planet planetVisual, ExternalTexture texture, Node videoNode) {

        stopPlaying();

        setupExoPlayer(texture, planetVisual.getPlanetVideoResID());

        setVideoTexture(texture);

        startExoPlayer(texture, videoNode);

        videoNode.setOnTapListener((hitTestResult, motionEvent) -> {

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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setVideoTexture(ExternalTexture texture) {

        if (videoRenderable == null || videoRenderable.getMaterial() == null || texture == null) {
            Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show();
            return;
        }
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    File currPlanetFile = currPlanetSelected.getPlanetFile();

                    try {

                        FileWriter fileWriter = new FileWriter( currPlanetFile, true);

                        ArrayList<String> speechResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        fileWriter.append("\n" + speechResult.get(0) );
                        fileWriter.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //TODO - WANT TO BE ABLE TO ALSO CHANGE THE TEXTVIEW THAT IS CURRENTLY SHOWING.

                }
                break;
            }

        }
    }

    @Override
    public void onEntityChanged() {
        //TODO - This is where the components will be called and the Handler will be made and stuff
        VideoComponent.setUpVideo(currEntitySelected.getEntity(), videoLayout.getVideoNode(),this, hasPlayedVideo);
        hasPlayedVideo = true;

        if (NoteComponent.getHasLoadedContentRenderable() ) {
            NoteComponent.changeContentView(currEntitySelected.getEntity(), noteLayout.getNoteRenderableView());
        }
    }
}
