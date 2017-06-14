package fr.guillaumeboutin.vrapplication1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by guillaumeboutin on 13/06/2017.
 */

public class MyGvrView  extends GvrActivity implements GvrView.StereoRenderer {


    GvrView gvrView;

    private ImageView imageView;
    private  Bitmap img;

    private Vibrator vibrator;


    private float touchX;
    private float touchY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr);
        //setContentView(gvrView);

        imageView = new ImageView(this);

        gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        gvrView.setRenderer(this);

        gvrView.enableCardboardTriggerEmulation();
        gvrView.setAsyncReprojectionEnabled(true);
        setGvrView(gvrView);

        gvrView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:

                    {
                        touchX = event.getX();
                        touchY = event.getY();
                        Log.e("Position","Position X : "+touchX+" Position Y : "+touchY);
                        onCardboardTrigger();
                    }
                    break;
                }

                return false;
            }
        });

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        img = BitmapFactory.decodeResource(getResources(), R.drawable.pano1, options);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        //Creation of a beautiful blue sky
        //GLES30.glClearColor(0.529411765f, 0.807843137f, 0.980392157f, 1.0f);
    }

    @Override
    public void onDrawEye(Eye eye) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


        setMapImage(img);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onRendererShutdown() {

    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        if (isLookingAtObject()) {
            // TODO Change VrView
        }

        // Always give user feedback
        vibrator.vibrate(50);
    }

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     *
     * @return true if the user is looking at the object.
     */
    private boolean isLookingAtObject() {
        // Convert object space to camera space. Use the headView from onNewFrame.

        //TODO detection isLooking

        return false;
    }

    private void setMapImage(Bitmap mapImage) {
        final ImageView mapImageL = (ImageView) findViewById(R.id.mapImageL);
        final ImageView mapImageR = (ImageView) findViewById(R.id.mapImageR);
        Bitmap cropped = Bitmap.createBitmap(mapImage, (mapImage.getWidth()-mapImage.getHeight())/2, 0,
                mapImage.getHeight(), mapImage.getHeight());
        mapImageL.setImageBitmap(cropped);
        mapImageR.setImageBitmap(cropped);
        mapImageL.setAlpha(0.6f);
        mapImageR.setAlpha(0.6f);
    }
}