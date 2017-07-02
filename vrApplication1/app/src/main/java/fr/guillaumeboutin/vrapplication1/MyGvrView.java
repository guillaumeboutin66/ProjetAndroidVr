package fr.guillaumeboutin.vrapplication1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by guillaumeboutin on 13/06/2017.
 */

public class MyGvrView  extends GvrActivity implements GvrView.StereoRenderer {


    GvrView gvrView;

    private ImageView imageView;
    private  Bitmap img;

    private float touchX;
    private float touchY;

    private FloatBuffer cubeVertices;
    private FloatBuffer cubeColors;
    private FloatBuffer cubeFoundColors;
    private FloatBuffer cubeNormals;

    private FloatBuffer laserVertices;
    private FloatBuffer laserColors;
    private FloatBuffer laserNormals;

    private int cubeProgram;
    private int laserProgram;

    private int cubePositionParam;
    private int cubeNormalParam;
    private int cubeColorParam;
    private int cubeModelParam;
    private int cubeModelViewParam;
    private int cubeModelViewProjectionParam;
    private int cubeLightPosParam;

    private int laserPositionParam;
    private int laserNormalParam;
    private int laserColorParam;
    private int laserModelParam;
    private int laserModelViewParam;
    private int laserModelViewProjectionParam;
    private int laserLightPosParam;

    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] modelFloor;

    private float[] tempPosition;
    private float[] headRotation;


    protected float[] modelCube;
    protected float[] modelPosition;
    protected float[] modelLaser;

    private static final String TAG = "TreasureHuntActivity";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private static final int COORDS_PER_VERTEX = 3;

    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

    private static final float[] POS_MATRIX_MULTIPLY_VEC = {0, 0, 0, 1.0f};

    private static final float MIN_MODEL_DISTANCE = 7.0f;
    private static final float MAX_MODEL_DISTANCE = 15.0f;

    private final float[] lightPosInEyeSpace = new float[4];

    private float objectDistance = MAX_MODEL_DISTANCE / 2.0f;
    private float floorDepth = 20f;

    private Vibrator vibrator;

    private GvrAudioEngine gvrAudioEngine;
    private volatile int sourceId = GvrAudioEngine.INVALID_ID;
    private volatile int successSourceId = GvrAudioEngine.INVALID_ID;

    private static final String DOOR_SOUND_FILE = "door.mp3";
    private static final String CLICK_SOUND_FILE = "click.mp3";

    private boolean canHide = true;

    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        //VrPanoramaView vrPanoramaView = (VrPanoramaView) findViewById(R.id.myVRImage2);
        //vrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.pano2), null);
        //vrPanoramaView.setDisplayMode(VrPanoramaView.DisplayMode.FULLSCREEN_STEREO);
        //vrPanoramaView.resumeRendering();

        initializeGvrView();

        modelCube = new float[16];
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        modelFloor = new float[16];
        modelLaser = new float[16];
        tempPosition = new float[4];
        headRotation = new float[4];
        // Model first appears directly in front of user.
        modelPosition = new float[] {0.0f, 0.0f, -MAX_MODEL_DISTANCE / 2.0f};
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize 3D audio engine.
        gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);


    }

    public void initializeGvrView() {

        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        //Show Irritating cardboard dialog
        gvrView.setTransitionViewEnabled(false);

        if (gvrView.setAsyncReprojectionEnabled(true)) {
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

        gvrView.getAs

        setGvrView(gvrView);
    }

    @Override
    public void onPause() {
        gvrAudioEngine.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gvrAudioEngine.resume();
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }


    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        cubeVertices = bbVertices.asFloatBuffer();
        cubeVertices.put(WorldLayoutData.CUBE_COORDS);
        cubeVertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        cubeColors = bbColors.asFloatBuffer();
        cubeColors.put(WorldLayoutData.CUBE_COLORS);
        cubeColors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        cubeFoundColors = bbFoundColors.asFloatBuffer();
        cubeFoundColors.put(WorldLayoutData.CUBE_FOUND_COLORS);
        cubeFoundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        cubeNormals = bbNormals.asFloatBuffer();
        cubeNormals.put(WorldLayoutData.CUBE_NORMALS);
        cubeNormals.position(0);

        // make a laser

        ByteBuffer bbLaserVertices = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COORDS.length * 4);
        bbLaserVertices.order(ByteOrder.nativeOrder());
        laserVertices = bbLaserVertices.asFloatBuffer();
        laserVertices.put(WorldLayoutData.RECT_COORDS);
        laserVertices.position(0);

        ByteBuffer bbLaserColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COLORS.length * 4);
        bbLaserColors.order(ByteOrder.nativeOrder());
        laserColors = bbLaserColors.asFloatBuffer();
        laserColors.put(WorldLayoutData.RECT_COLORS);
        laserColors.position(0);

        ByteBuffer bbLaserNormals = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_NORMALS.length * 4);
        bbLaserNormals.order(ByteOrder.nativeOrder());
        laserNormals = bbLaserNormals.asFloatBuffer();
        laserNormals.put(WorldLayoutData.RECT_NORMALS);
        laserNormals.position(0);

        // make a floor
        /*ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        floorVertices = bbFloorVertices.asFloatBuffer();
        floorVertices.put(WorldLayoutData.FLOOR_COORDS);
        floorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        floorNormals = bbFloorNormals.asFloatBuffer();
        floorNormals.put(WorldLayoutData.FLOOR_NORMALS);
        floorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        floorColors = bbFloorColors.asFloatBuffer();
        floorColors.put(WorldLayoutData.FLOOR_COLORS);
        floorColors.position(0);*/


        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        cubeProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(cubeProgram, vertexShader);
        GLES20.glAttachShader(cubeProgram, passthroughShader);
        GLES20.glLinkProgram(cubeProgram);
        GLES20.glUseProgram(cubeProgram);

        cubePositionParam = GLES20.glGetAttribLocation(cubeProgram, "a_Position");
        cubeNormalParam = GLES20.glGetAttribLocation(cubeProgram, "a_Normal");
        cubeColorParam = GLES20.glGetAttribLocation(cubeProgram, "a_Color");

        cubeModelParam = GLES20.glGetUniformLocation(cubeProgram, "u_Model");
        cubeModelViewParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVMatrix");
        cubeModelViewProjectionParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVP");
        cubeLightPosParam = GLES20.glGetUniformLocation(cubeProgram, "u_LightPos");

        /*floorProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(floorProgram, vertexShader);
        GLES20.glAttachShader(floorProgram, gridShader);
        GLES20.glLinkProgram(floorProgram);
        GLES20.glUseProgram(floorProgram);*/

        /*floorModelParam = GLES20.glGetUniformLocation(floorProgram, "u_Model");
        floorModelViewParam = GLES20.glGetUniformLocation(floorProgram, "u_MVMatrix");
        floorModelViewProjectionParam = GLES20.glGetUniformLocation(floorProgram, "u_MVP");
        floorLightPosParam = GLES20.glGetUniformLocation(floorProgram, "u_LightPos");

        floorPositionParam = GLES20.glGetAttribLocation(floorProgram, "a_Position");
        floorNormalParam = GLES20.glGetAttribLocation(floorProgram, "a_Normal");
        floorColorParam = GLES20.glGetAttribLocation(floorProgram, "a_Color");*/

        laserProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(laserProgram, vertexShader);
        GLES20.glAttachShader(laserProgram, passthroughShader);
        GLES20.glLinkProgram(laserProgram);
        GLES20.glUseProgram(laserProgram);

        laserPositionParam = GLES20.glGetAttribLocation(laserProgram, "a_Position");
        laserNormalParam = GLES20.glGetAttribLocation(laserProgram, "a_Normal");
        laserColorParam = GLES20.glGetAttribLocation(laserProgram, "a_Color");

        laserModelParam = GLES20.glGetUniformLocation(laserProgram, "u_Model");
        laserModelViewParam = GLES20.glGetUniformLocation(laserProgram, "u_MVMatrix");
        laserModelViewProjectionParam = GLES20.glGetUniformLocation(laserProgram, "u_MVP");
        laserLightPosParam = GLES20.glGetUniformLocation(laserProgram, "u_LightPos");

        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0);

        updateModelPosition();
        shootLaser();

        // Faster loading woop woop
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        gvrAudioEngine.preloadSoundFile(DOOR_SOUND_FILE);
                        sourceId = gvrAudioEngine.createSoundObject(DOOR_SOUND_FILE);
                        gvrAudioEngine.setSoundObjectPosition(
                                sourceId, modelPosition[0], modelPosition[1], modelPosition[2]);
                        gvrAudioEngine.playSound(sourceId, true);
                        gvrAudioEngine.preloadSoundFile(CLICK_SOUND_FILE);
                    }
                })
                .start();

    }


    /**
     * I change the position of the cube
     */
    protected void updateModelPosition() {
        Matrix.setIdentityM(modelCube, 0);
        Matrix.translateM(modelCube, 0, modelPosition[0], modelPosition[1], modelPosition[2]);
        if (sourceId != GvrAudioEngine.INVALID_ID) {
            gvrAudioEngine.setSoundObjectPosition(
                    sourceId, modelPosition[0], modelPosition[1], modelPosition[2]);
        }
    }

    /**
     * Repositions and rotates the laser based on the head rotation
     */
    private void shootLaser(){
        Matrix.setIdentityM(modelLaser,0);
        Matrix.translateM(modelLaser,0, 0, -0.5f, 0);
        Matrix.rotateM(modelLaser,0, 90, 0.0f, 1.0f,0.0f);
        Matrix.rotateM(modelLaser,0, 90, 1.0f, 0.0f,0.0f);
        float pitch = (float) Math.toDegrees(headRotation[1]);
        pitch = -pitch;
        float yaw = (float) Math.toDegrees(headRotation[0]);
        Matrix.rotateM(modelLaser,0, pitch, 0.0f, 0.0f,1.0f);
        Matrix.rotateM(modelLaser,0,yaw,0.0f,1.0f,0.0f);
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        setCubeRotation();
        moveLaser();
        //very very very dirty fix, sometimes, I disgust myself.
        //GLES20.glClearColor(0.4f, 0.8f, 1.0f, 0.5f);

        //gvrView.setBackground(getResources().getDrawable(R.drawable.pano1));

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        headTransform.getHeadView(headView, 0);
        headTransform.getEulerAngles(headRotation, 0);
        gvrAudioEngine.setHeadRotation(
                headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
        // Regular update call to GVR audio engine.
        gvrAudioEngine.update();

    }

    private void setCubeRotation() {
        Matrix.rotateM(modelCube, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);
    }

    /**
     * Translates the laser on the Z-axis while checking for "collision" (it really isnt collision)
     */
    private void moveLaser(){
        Matrix.translateM(modelLaser,0,0.5f,0,0);
        boolean checkZ = modelCube[14] - 0.5f < modelLaser[14] && modelCube[14] + 0.5f > modelLaser[14];
        boolean checkY = modelCube[13] - 0.5f < modelLaser[13] && modelCube[13] + 0.5f > modelLaser[13];
        if(checkZ && checkY && canHide){
            hideObject();
            canHide = false;
        }
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawCube();

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawFloor();

        Matrix.multiplyMM(modelView,0, view,0,modelLaser,0);
        Matrix.multiplyMM(modelViewProjection,0,perspective,0,modelView,0);
        drawLaser();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {}

    public void drawCube() {
        GLES20.glUseProgram(cubeProgram);

        GLES20.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(
                cubePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, cubeVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(cubeNormalParam, 3, GLES20.GL_FLOAT, false, 0, cubeNormals);
        GLES20.glVertexAttribPointer(cubeColorParam, 4, GLES20.GL_FLOAT, false, 0,
                isLookingAtObject() ? cubeFoundColors : cubeColors);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(cubePositionParam);
        GLES20.glEnableVertexAttribArray(cubeNormalParam);
        GLES20.glEnableVertexAttribArray(cubeColorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public void drawLaser() {
        GLES20.glUseProgram(laserProgram);

        GLES20.glUniform3fv(laserLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(laserModelParam, 1, false, modelLaser, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(laserModelViewParam, 1, false, modelView, 0);

        // Set the position of the laser
        GLES20.glVertexAttribPointer(
                laserPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, laserVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(laserModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the laser, again for shading
        GLES20.glVertexAttribPointer(laserNormalParam, 3, GLES20.GL_FLOAT, false, 0, laserNormals);
        GLES20.glVertexAttribPointer(laserColorParam, 4, GLES20.GL_FLOAT, false, 0, laserColors);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(laserPositionParam);
        GLES20.glEnableVertexAttribArray(laserNormalParam);
        GLES20.glEnableVertexAttribArray(laserColorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public void drawFloor() {
        /*GLES20.glUseProgram(floorProgram);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(floorLightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(floorModelParam, 1, false, modelFloor, 0);
        GLES20.glUniformMatrix4fv(floorModelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(floorModelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glVertexAttribPointer(
                floorPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, floorVertices);
        GLES20.glVertexAttribPointer(floorNormalParam, 3, GLES20.GL_FLOAT, false, 0, floorNormals);
        GLES20.glVertexAttribPointer(floorColorParam, 4, GLES20.GL_FLOAT, false, 0, floorColors);

        GLES20.glEnableVertexAttribArray(floorPositionParam);
        GLES20.glEnableVertexAttribArray(floorNormalParam);
        GLES20.glEnableVertexAttribArray(floorColorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 24);*/
    }

    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");
        successSourceId = gvrAudioEngine.createStereoSound(CLICK_SOUND_FILE);
        gvrAudioEngine.playSound(successSourceId, false /* looping disabled */);
        if (isLookingAtObject()) {
            canHide = true;
        }
        shootLaser();

        // Bzz bzz bzz bzz
        vibrator.vibrate(50);
    }

    protected void hideObject() {
        float[] rotationMatrix = new float[16];
        float[] posVec = new float[4];

        // First rotate in XZ plane, between 90 and 270 deg away, and scale so that we vary
        // the object's distance from the user.
        float angleXZ = (float) Math.random() * 180 + 90;
        Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
        float oldObjectDistance = objectDistance;
        objectDistance =
                (float) Math.random() * (MAX_MODEL_DISTANCE - MIN_MODEL_DISTANCE) + MIN_MODEL_DISTANCE;
        float objectScalingFactor = objectDistance / oldObjectDistance;
        Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor, objectScalingFactor);
        Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, modelCube, 12);

        float angleY = (float) Math.random() * 80 - 40; // Angle in Y plane, between -40 and 40.
        angleY = (float) Math.toRadians(angleY);
        float newY = (float) Math.tan(angleY) * objectDistance;

        modelPosition[0] = posVec[0];
        modelPosition[1] = newY;
        modelPosition[2] = posVec[2];
        successSourceId = gvrAudioEngine.createStereoSound(DOOR_SOUND_FILE);
        gvrAudioEngine.playSound(successSourceId, false /* looping disabled */);
        updateModelPosition();
    }

    private boolean isLookingAtObject() {
        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, modelCube, 0);
        Matrix.multiplyMV(tempPosition, 0, modelView, 0, POS_MATRIX_MULTIPLY_VEC, 0);

        float pitch = (float) Math.atan2(tempPosition[1], -tempPosition[2]);
        float yaw = (float) Math.atan2(tempPosition[0], -tempPosition[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    private void setMapImage(Bitmap mapImage) {
        /*final ImageView mapImageL = (ImageView) findViewById(R.id.mapImageL);
        final ImageView mapImageR = (ImageView) findViewById(R.id.mapImageR);
        Bitmap cropped = Bitmap.createBitmap(mapImage, (mapImage.getWidth()-mapImage.getHeight())/2, 0,
                mapImage.getHeight(), mapImage.getHeight());
        mapImageL.setImageBitmap(cropped);
        mapImageR.setImageBitmap(cropped);
        mapImageL.setAlpha(0.6f);
        mapImageR.setAlpha(0.6f);*/
    }
}