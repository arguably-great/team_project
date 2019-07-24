package com.example.fbuteamproject.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.adapters.PhotosAdapter;
import com.example.fbuteamproject.models.Photo;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {

    private static final String TAG = PhotosActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    PhotosAdapter photoAdapter;
    ArrayList<Photo> photos;
    RecyclerView rvPhotos;
    Context context;
    Photo photo;

    private ViewRenderable viewRenderable;
    private ArFragment arFragment;

    ArrayList<Photo> data = new ArrayList<>();
    public static String IMGS[] = {
// Your image URLs here
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }


        setContentView(R.layout.activity_ux);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_holder);


        arFragment.setOnTapArPlaneListener(this::onTapPlane);





        ViewRenderable.builder()
                .setView(this, R.layout.jupiter1)
                .build()
                .thenAccept(renderable -> viewRenderable = renderable);


//        View rvView = viewRenderable.getView();
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

//        rvPhotos = findViewById(R.id.rvPhotos);
//        rvPhotos.setHasFixedSize(true);
//        rvPhotos.setLayoutManager(layoutManager);
//        PhotosAdapter adapter = new PhotosAdapter(this, getPhotos());
//        rvPhotos.setAdapter(adapter);

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

    private void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (viewRenderable == null) {
            return;
        }

        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable andy and add it to the anchor.
        TransformableNode viewScreen = new TransformableNode(arFragment.getTransformationSystem());
        viewScreen.setParent(anchorNode);
        viewScreen.setRenderable(viewRenderable);
        viewScreen.select();
    }
}
