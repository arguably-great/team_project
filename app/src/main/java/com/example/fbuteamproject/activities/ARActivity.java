package com.example.fbuteamproject.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.fragments.NoteContentFragment;
import com.example.fbuteamproject.interfaces.PassNoteToActivityListener;
import com.example.fbuteamproject.models.Note;
import com.example.fbuteamproject.models.Planet;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
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
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.parceler.Parcels;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARActivity extends AppCompatActivity implements PassNoteToActivityListener {

    //TODO - Bring this back if needed
//    private FragmentManager fragmentManager;

    private final String APP_TAG = "NoteActivity";
    private static final int RC_PERMISSIONS = 0x123;
    private boolean installRequested;


    private ArSceneView arSceneView;
    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    private ModelRenderable earthRenderable;
    private ModelRenderable marsRenderable;
    private ModelRenderable neptuneRenderable;

    private ViewRenderable noteTitlesRenderable;
    private ViewRenderable noteContentsRenderable;

    // True once scene is loaded
    private boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    private boolean hasPlacedComponents = false;


//    private ArrayList<Note> notesTest;
//    private NoteTitleAdapter noteTitleAdapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO - Make sure to change this when finished
        setContentView(R.layout.activity_ar);

        arSceneView = findViewById(R.id.ar_scene_view);

        //Build the ModelRenderables
        CompletableFuture<ModelRenderable> earthStage =
                ModelRenderable.builder().setSource(this, Uri.parse("CHAHIN_EARTH.sfb") ).build();
        CompletableFuture<ModelRenderable> marsStage =
                ModelRenderable.builder().setSource(this, Uri.parse("1239 Mars.sfb") ).build();
        CompletableFuture<ModelRenderable> neptuneStage =
                ModelRenderable.builder().setSource(this, Uri.parse("Neptune.sfb") ).build();


        //Build the ViewRenderables
        CompletableFuture<ViewRenderable> noteTitleStage =
                ViewRenderable.builder().setView(this, R.layout.fragment_note_title).build();

        CompletableFuture<ViewRenderable> noteContentsStage =
                ViewRenderable.builder().setView(this, R.layout.fragment_note_contents).build();

        CompletableFuture.allOf(
                    earthStage,
                    marsStage,
                    neptuneStage,
                    noteTitleStage,
                    noteContentsStage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                Log.e(APP_TAG, "Throwable found to be non-null in handle method");
                                return null;
                            }

                            try {
                                earthRenderable = earthStage.get();
                                marsRenderable = marsStage.get();
                                neptuneRenderable = neptuneStage.get();
                                noteTitlesRenderable = noteTitleStage.get();
                                noteContentsRenderable = noteContentsStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                Log.e(APP_TAG, "Unable to load Renderables (IN CATCH STATEMENT)");
                            }

                            return null;
                        });

        // Set up a tap gesture detector.
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                Log.d(APP_TAG, "Just released my finger after having tapped");
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        // Set a touch listener on the Scene to listen for taps.
        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            // If the solar system hasn't been placed yet, detect a tap and then check to see if
                            // the tap occurred on an ARCore plane to place the solar system.
                            if (!hasPlacedComponents) {
                                Log.d(APP_TAG, "Wanting to place Fragments for first time");
                                return gestureDetector.onTouchEvent(event);
                            }

                            // Otherwise return false so that the touch event can propagate to the scene.
                            Log.d(APP_TAG, "The Fragments are already out there, so we wanna" +
                                    " do stuff in the scene");
                            return false;
                        });


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

        requestCameraPermission(this);


        //TODO = Bring this back if needed
//        fragmentManager = getSupportFragmentManager();
//
//        NoteTitleFragment initFragment = new NoteTitleFragment();
//
//        initFragment.setPassNoteListener(this);
//
//        fragmentManager
//                .beginTransaction()
//                .add(R.id.fragmentContainer, initFragment)
//                .commit();





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
                Log.e(APP_TAG, "Some stuff went down...\n" + e.getLocalizedMessage() );
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Log.e(APP_TAG, "Unable to get camera...");
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



    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            Log.d(APP_TAG, "Not done loading Scene yet");
            return;
        }

        Log.d(APP_TAG, "In onSingleTap and have finished loading the Scene");

        Frame frame = arSceneView.getArFrame();
        if (frame != null) {
            if (!hasPlacedComponents && tryPlaceComponents(tap, frame)) {
                hasPlacedComponents = true;
            }
        }
    }


    private boolean tryPlaceComponents(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Log.d(APP_TAG, "Creating anchor to try and place Fragments");
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
                    Node fragments = createComponents();
                    anchorNode.addChild(fragments);
                    return true;
                }

            }

        }

        return false;
    }


    private Node createComponents() {

        Log.d(APP_TAG, "In Component creation method");

        Node base = new Node();

        Node noteTitles = new Node();
        noteTitles.setParent(base);
        noteTitles.setLocalPosition(new Vector3(-0.5f, 0.5f, 0.0f));

        Node noteVisual = new Node();
        noteVisual.setParent(noteTitles);
        noteVisual.setRenderable(noteTitlesRenderable);
        noteVisual.setLocalScale(new Vector3(0.5f, 0.35f, 0.5f));

        Node noteContents = new Node();
        noteContents.setParent(base);
        noteContents.setRenderable(noteContentsRenderable);
        noteContents.setLocalPosition(new Vector3(0.5f, 0.5f, 0.0f));
        noteContents.setLocalScale(new Vector3(0.5f, 0.35f, 0.5f));


        Planet earthVisual = new Planet("Earth", "The grandest of places! I hear all the life is here ;)");
        earthVisual.setParent(base);
        earthVisual.setRenderable(earthRenderable);
        earthVisual.setLocalPosition(new Vector3(-0.5f, 1.5f, 0.0f) );
        earthVisual.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );

        Planet marsVisual = new Planet("Mars","Pretty nifty place. Elon likes it a lot, I'm pretty sure :0");
        marsVisual.setParent(base);
        marsVisual.setRenderable(marsRenderable);
        marsVisual.setLocalPosition(new Vector3(0.0f, 1.5f, 0.0f) );
        marsVisual.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );

        Planet neptuneVisual = new Planet("Neptune","King of the Sea?? More like last planet in our Solar System LMAO XD");
        neptuneVisual.setParent(base);
        neptuneVisual.setRenderable(neptuneRenderable);
        neptuneVisual.setLocalPosition(new Vector3(0.5f, 1.5f, 0.0f) );
        neptuneVisual.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f) );


        View noteTitleView = noteTitlesRenderable.getView();
        View noteContentView = noteContentsRenderable.getView();

//        RecyclerView rvTest = noteTitleView.findViewById(R.id.rvNoteTitles);
//
//        notesTest = new ArrayList<>();
//
//        noteTitleAdapter = new NoteTitleAdapter(notesTest);
//
//        rvTest.setAdapter(noteTitleAdapter);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
//
//        rvTest.setLayoutManager(layoutManager);
//
//        generateNotes();


        earthVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            ((TextView) noteTitleView.findViewById(R.id.tvTitle) ).setText(earthVisual.getPlanetName() );
            ((TextView) noteContentView.findViewById(R.id.tvContents) ).setText(earthVisual.getPlanetNotes() );
        });

        marsVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            ((TextView) noteTitleView.findViewById(R.id.tvTitle) ).setText(marsVisual.getPlanetName() );
            ((TextView) noteContentView.findViewById(R.id.tvContents) ).setText(marsVisual.getPlanetNotes() );
        });

        neptuneVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            ((TextView) noteTitleView.findViewById(R.id.tvTitle) ).setText( neptuneVisual.getPlanetName() );
            ((TextView) noteContentView.findViewById(R.id.tvContents) ).setText(neptuneVisual.getPlanetNotes() );
        });




        noteTitleView.setOnClickListener(new View.OnClickListener() {
            int count = 0;

            @Override
            public void onClick(View v) {
                ((TextView) noteContentView.findViewById(R.id.tvContents)).setText(String.format(getResources().getString(R.string.fragment_click_num), count + 1));
                count += 1;
            }
        });

        return base;
    }


//    private void generateNotes() {
//
//        for(int i = 0; i < 20; i++){
//            String noteTitle = "NOTE " + (i+1);
//
//            String noteContent = "INSIDE OF NOTE " + (i+1);
//
//            Note note = new Note(noteTitle, noteContent);
//
//            notesTest.add(note);
//            noteTitleAdapter.notifyItemInserted(notesTest.size() - 1);
//        }
//
//    }





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

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }


    private Session createArSession(Activity activity, boolean installRequested)
            throws UnavailableException {
        Session session = null;
        // if we have the camera permission, create the session
        if (hasCameraPermission(activity)) {
            switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                case INSTALL_REQUESTED:
                    return null;
                case INSTALLED:
                    break;
            }
            session = new Session(activity);
            // IMPORTANT!!!  ArSceneView requires the `LATEST_CAMERA_IMAGE` non-blocking update mode.
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
        }
        return session;
    }

    private boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.CAMERA}, ARActivity.RC_PERMISSIONS);
    }




    //TODO - Bring this back if needed
    @Override
    public void returnSelectedNote(Note selectedNoted) {
        Bundle args = new Bundle();
        args.putParcelable("note", Parcels.wrap(selectedNoted) );
        NoteContentFragment contentFragment = new NoteContentFragment();
        contentFragment.setArguments(args);

//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.fragmentContainer, contentFragment)
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }



}
