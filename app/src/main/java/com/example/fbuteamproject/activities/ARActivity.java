package com.example.fbuteamproject.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbuteamproject.R;
import com.example.fbuteamproject.components.ModelComponent;
import com.example.fbuteamproject.components.NoteComponent;
import com.example.fbuteamproject.components.PhotoComponent;
import com.example.fbuteamproject.components.VideoComponent;
import com.example.fbuteamproject.layouts.EntityLayout;
import com.example.fbuteamproject.layouts.NoteLayout;
import com.example.fbuteamproject.layouts.PhotoLayout;
import com.example.fbuteamproject.layouts.VideoLayout;
import com.example.fbuteamproject.utils.Config;
import com.example.fbuteamproject.utils.DemoUtils;
import com.example.fbuteamproject.utils.FlickrApi.Api;
import com.example.fbuteamproject.utils.FlickrApi.Query;
import com.example.fbuteamproject.utils.FlickrApi.SearchQuery;
import com.example.fbuteamproject.utils.PhotoViewer;
import com.example.fbuteamproject.wrappers.EntityWrapper;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
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
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.example.fbuteamproject.utils.DemoUtils.checkIsSupportedDeviceOrFinish;

/* ARActivity that populates the sceneview with model renderables and view renderables
for notes, photos and videos upon clicking on the screen.
*/

public class ARActivity extends AppCompatActivity implements EntityWrapper.EntityChangeListener {

    private static final String TAG = ARActivity.class.getSimpleName();

    private static final int RC_PERMISSIONS =0x123;

    private final int SPEECH_REQUEST_CODE = 100;

    private boolean installRequested;

    private GestureDetector gestureDetector;

    private ArSceneView arSceneView;

    private Snackbar loadingMessageSnackbar;

    // True once scene is loaded
    private boolean hasFinishedLoading;

    // True once the scene has been placed.
    private boolean hasPlacedComponents;

    public static int loadPhotoCount;

    private ArrayList<Config.Entity> appEntities;

    //Video feature variables
    @Nullable
    private ModelRenderable videoRenderable;
    private CompletableFuture<ModelRenderable> videoStage;
    private SimpleExoPlayer player;
    private ExternalTexture texture;

    private EntityWrapper currEntitySelected;
    private EntityLayout entityLayout;
    private VideoLayout videoLayout;
    private NoteLayout noteLayout;

    //Photo Feature variables
    public static boolean photoClicked = false;

    private final QueryListener queryListener = new QueryListener(ARActivity.this);
    private List<com.example.fbuteamproject.utils.FlickrApi.Photo> currentPhotos = new ArrayList<>();
    private Query currentQuery;
    private final Set<PhotoViewer> photoViewers = new HashSet<>();
    public static Query DEFAULT_QUERY = new SearchQuery("earth planet");
    public static Query newQuery;
    public static ArrayList<CompletableFuture<ViewRenderable>> completableFutures;
    private int photoCount = 0;
    ArrayList<Node> photoNodes;

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

        //build video renderable
        videoStage = VideoComponent.buildVideoStage(this);
        videoRenderable = VideoComponent.buildModelRenderable(videoStage, this);
        // Create an ExternalTexture for displaying the contents of the video.
        texture = new ExternalTexture();

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        player.setVideoSurface(texture.getSurface());

        NoteComponent.buildContentRenderable(this);

        Config.AppConfig configuration = Config.AppConfig.getAppConfig(this);
        appEntities = configuration.entities;

        Log.d("CONTEXT", Config.AppConfig.getContext().toString() );
      
        entityLayout = new EntityLayout();


        // TODO default search so that view renderables are not null, move to entity create
        Api.get(this).registerSearchListener(queryListener);
        executeQuery(DEFAULT_QUERY);


        currEntitySelected = new EntityWrapper();
        currEntitySelected.setListener(this);

        // listeners and click
        setupGestureDetector();
        setupTouchListener();
        setupOnUpdateListener();

        // Lastly request CAMERA permission which is required by ARCore.
        requestCameraPermission(this);

    }


    private void executeQuery(Query query) {
        currentQuery = query;
        if (query == null) {
            queryListener.onSearchCompleted(null, Collections.emptyList());
            return;
        }

        Api.get(this).query(currentQuery);

    }

    public class QueryListener implements Api.QueryListener {


        Context context;

        public QueryListener(Context context) {
            this.context = context;
        }

        @Override
        public void onSearchCompleted(Query query, List<com.example.fbuteamproject.utils.FlickrApi.Photo> photos) {
            if (!isCurrentQuery(query)) {
                return;
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Search completed, got " + photos.size() + " results");
            }

            for (PhotoViewer viewer : photoViewers) {
                viewer.onPhotosUpdated(photos);
            }
            currentPhotos = photos;

            if (currentPhotos.size() > 1) {

                completableFutures = new ArrayList<>();

                for (int i = 0; i < 6; i++) {

                    Log.d(TAG, "on SearchCompleted: "+i);

                    CompletableFuture<ViewRenderable> photoStage;
                    ImageView iv = new ImageView(context);

                    Glide.with(context).load(currentPhotos.get(i)).apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .fitCenter().override(1000, 1000)).into(iv);

                    photoStage = ViewRenderable.builder().setView(context, iv).build();

                    completableFutures.add(photoStage);

                    loadPhotoCount++;

                }
            }

        }

        private boolean isCurrentQuery(Query query) {
            return currentQuery != null && currentQuery.equals(query);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void onSearchFailed(Query query, Exception e) {
            if (!isCurrentQuery(query)) {
                return;
            }

            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "Search failed", e);
            }

        }
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

        //TODO Activity lifecycle stuff (i.e. release player)

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //TODO Activity lifecycle stuff (i.e. release player)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onSingleTap(MotionEvent tap) {

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

        // photo completable futures & renderables
        ArrayList<CompletableFuture<ViewRenderable>> photoCompletables = PhotoComponent.getCompletableFutures();
        PhotoComponent.buildViewRenderables(photoCompletables, this);

        entityLayout.setParent(baseNode);

        videoLayout = new VideoLayout(videoRenderable);
        videoLayout.setParent(baseNode);

        noteLayout = new NoteLayout(NoteComponent.getEntityContentRenderable());
        noteLayout.setParent(baseNode);

        // putting renderables in correct layout
        photoNodes = PhotoLayout.photoNodeSetUp(baseNode);

        //This coming line should trigger the onEntityChanged method from the included interface
        currEntitySelected.setEntity(appEntities.get(0));

        //Traverse through all of the Entities and assign their onTaps (basically just trigger listener)
        for(Config.Entity currEntity: appEntities){
            currEntity.setOnTapListener((hitTestResult, motionEvent) -> {
                if (currEntitySelected.getEntity() != currEntity){
                    currEntitySelected.setEntity(currEntity);
                }
                else{
                    Toast.makeText(ARActivity.this, "Tapped on the same Entity", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Creating Intent for Speech-To-Text
        noteLayout.getNoteRenderableView().setOnClickListener(v -> {
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
       });

        return baseNode;
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

                    File currEntityFile = currEntitySelected.getEntity().getEntityFile();

                    try {

                        FileWriter fileWriter = new FileWriter(currEntityFile, true);

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
        VideoComponent.playVideo(texture, currEntitySelected.getEntity(), videoLayout.getVideoNode(),this, player);

        newQuery = new SearchQuery(currEntitySelected.getEntity().getEntityName() + "planet");
        Log.d(TAG, "onEntityChanged: "+ currEntitySelected.getEntity().getEntityName());

        if (photoCount != 0) {
            photoClicked = true;

        }
        photoCount++;

        Api.get(this).registerSearchListener(queryListener);
        executeQuery(newQuery);

        // photo completable futures & renderables
        ArrayList<CompletableFuture<ViewRenderable>> photoCompletables = PhotoComponent.getCompletableFutures();
        PhotoComponent.buildViewRenderables(photoCompletables, this);

        Log.d(TAG, "onEntityChanged: " + photoClicked);

        if (ARActivity.photoClicked == true && photoNodes != null) {
            Log.d(TAG, "deleting nodes");
            for (int i = 0; i < photoNodes.size(); i++) {
                Log.d(TAG, "Removing nodes" + photoNodes.get(i));
                photoNodes.get(i).setRenderable(null);
            }
        }

        for (int i = 0; i < photoNodes.size(); i++) {
            photoNodes.get(i).setRenderable(PhotoComponent.viewRenderables.get(i));
            Log.d(TAG, "onEntityChanged: here is new renderable" + PhotoComponent.viewRenderables.get(i));
        }


        if (NoteComponent.getHasLoadedContentRenderable() ) {
            NoteComponent.changeContentView(currEntitySelected.getEntity(), noteLayout.getNoteRenderableView());
        }


    }
}

