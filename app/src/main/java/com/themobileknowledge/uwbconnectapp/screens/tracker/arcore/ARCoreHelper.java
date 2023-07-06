package com.themobileknowledge.uwbconnectapp.screens.tracker.arcore;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseObservable;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.helpers.CompassHelper;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.helpers.DisplayRotationHelper;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.helpers.TrackingStateHelper;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.AnchorRemoteDevice;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Coordinates;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Direction;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.rendering.BackgroundRenderer;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.rendering.SampleRender;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.rendering.SpecularCubemapFilter;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.Framebuffer;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.GLError;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.Mesh;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.Shader;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.Texture;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.utils.VertexBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ARCoreHelper extends BaseObservable<ARCoreHelper.Listener> implements SampleRender.Renderer {

    private static final String TAG = ARCoreHelper.class.getSimpleName();

    private Activity mActivity;
    private Context mContext;
    private ToastsHelper mToastsHelper;

    private Session mSession;
    private Config mConfig;
    private Frame mFrame;
    private Camera mCamera;
    private boolean isInitialized = false;
    private boolean mUserRequestedInstall = true;

    // List with all connected UWB Anchor Remote Devices
    private HashMap<String, AnchorRemoteDevice> mAnchorRemoteDevices = new HashMap<>();

    // If set to true, the ARCore will display anchors using Google's ARCore sample objects
    private final boolean showARCoreSampleAnchors = false;
    private final boolean showARCoreAccessoryPosition = true;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView mSurfaceView;

    // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
    // constants.
    private static final float[] sphericalHarmonicFactors = {
            0.282095f,
            -0.325735f,
            0.325735f,
            -0.325735f,
            0.273137f,
            -0.273137f,
            0.078848f,
            -0.273137f,
            0.136569f,
    };

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100f;

    private static final int CUBEMAP_RESOLUTION = 16;
    private static final int CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES = 32;

    private DisplayRotationHelper displayRotationHelper;
    private final TrackingStateHelper trackingStateHelper;
    private CompassHelper compassHelper;
    private SampleRender render;

    private BackgroundRenderer backgroundRenderer;
    private Framebuffer virtualSceneFramebuffer;
    private boolean hasSetTextureNames = false;

    // Point Cloud
    private VertexBuffer pointCloudVertexBuffer;

    // Virtual object (ARCore pawn)
    private Mesh virtualObjectMesh;
    private Shader virtualObjectShader;
    private Texture virtualObjectAlbedoTexture;

    // Environmental HDR
    private Texture dfgTexture;
    private SpecularCubemapFilter cubemapFilter;

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16]; // view x model
    private final float[] modelViewProjectionMatrix = new float[16]; // projection x view x model
    private final float[] sphericalHarmonicsCoefficients = new float[9 * 3];
    private final float[] viewInverseMatrix = new float[16];
    private final float[] worldLightDirection = {0.0f, 0.0f, 0.0f, 0.0f};
    private final float[] viewLightDirection = new float[4]; // view x world light direction

    private int surfaceViewWidth = 0;
    private int surfaceViewHeight = 0;

    public interface Listener {
        void onARCoreAccessoryPosition(Accessory accessory, Position position, Coordinates coordinates, Direction direction);

        void onARCoreUnknownAccessoryPosition(Accessory accessory, Position position);
    }

    public ARCoreHelper(Activity activity, Context context, ToastsHelper toastsHelper) {
        super();

        mActivity = activity;
        mContext = context;
        mToastsHelper = toastsHelper;

        displayRotationHelper = new DisplayRotationHelper(context);
        trackingStateHelper = new TrackingStateHelper(activity);
        compassHelper = new CompassHelper(context, displayRotationHelper);
    }

    public void setSurfaceView(GLSurfaceView surfaceView) {
        mSurfaceView = surfaceView;

        // Initialize renderer
        render = new SampleRender(mSurfaceView, this, mActivity.getAssets());
    }

    public boolean isInstalled() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(mContext);
        return availability == ArCoreApk.Availability.SUPPORTED_INSTALLED;
    }

    public boolean isSupported() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(mContext);
        return availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED
                || availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD;
    }

    public void requestInstall() {
        try {
            ArCoreApk.InstallStatus installStatus = ArCoreApk.getInstance().requestInstall(mActivity, mUserRequestedInstall);
            if (installStatus == ArCoreApk.InstallStatus.INSTALL_REQUESTED) {
                mUserRequestedInstall = false;
            }
        } catch (UnavailableDeviceNotCompatibleException |
                 UnavailableUserDeclinedInstallationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean initializeSession() {
        Exception exception = null;
        String message = null;
        try {
            mSession = new Session(mContext);
        } catch (UnavailableArcoreNotInstalledException e) {
            message = "Please install ARCore";
            exception = e;
        } catch (UnavailableApkTooOldException e) {
            message = "Please update ARCore";
            exception = e;
        } catch (UnavailableSdkTooOldException e) {
            message = "Please update this app";
            exception = e;
        } catch (Exception e) {
            message = "The device could not start AR";
            exception = e;
        }

        if (message != null) {
            Log.e(TAG, "There is a problem while preparing AR: " + exception.getMessage());
            mToastsHelper.notifyGenericMessage(message);

            return false;
        }

        mConfig = new Config(mSession);
        mConfig.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        mSession.configure(mConfig);

        isInitialized = true;
        return true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void resume() {
        if (mSession != null) {
            try {
                mSession.resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
        }

        mSurfaceView.onResume();
        displayRotationHelper.onResume();
        compassHelper.onResume();
    }

    public void pause() {
        if (mSession != null) {
            mSession.pause();
        }

        mSurfaceView.onPause();
        displayRotationHelper.onPause();
        compassHelper.onPause();
    }

    public void close() {
        if (mSession != null) {
            mSession.close();
        }
    }

    @Override
    public void onSurfaceCreated(SampleRender render) {
        // Prepare the rendering objects. This involves reading shaders and 3D model files, so may throw
        // an IOException.
        try {
            backgroundRenderer = new BackgroundRenderer(render);
            virtualSceneFramebuffer = new Framebuffer(render, /*width=*/ 1, /*height=*/ 1);

            cubemapFilter =
                    new SpecularCubemapFilter(
                            render, CUBEMAP_RESOLUTION, CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES);
            // Load DFG lookup table for environmental lighting
            dfgTexture =
                    new Texture(
                            render,
                            Texture.Target.TEXTURE_2D,
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            /*useMipmaps=*/ false);
            // The dfg.raw file is a raw half-float texture with two channels.
            final int dfgResolution = 64;
            final int dfgChannels = 2;
            final int halfFloatSize = 2;

            ByteBuffer buffer =
                    ByteBuffer.allocateDirect(dfgResolution * dfgResolution * dfgChannels * halfFloatSize);
            try (InputStream is = mContext.getAssets().open("models/dfg.raw")) {
                is.read(buffer.array());
            }
            // SampleRender abstraction leaks here.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dfgTexture.getTextureId());
            GLError.maybeThrowGLException("Failed to bind DFG texture", "glBindTexture");
            GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_2D,
                    /*level=*/ 0,
                    GLES30.GL_RG16F,
                    /*width=*/ dfgResolution,
                    /*height=*/ dfgResolution,
                    /*border=*/ 0,
                    GLES30.GL_RG,
                    GLES30.GL_HALF_FLOAT,
                    buffer);
            GLError.maybeThrowGLException("Failed to populate DFG texture", "glTexImage2D");

            // four entries per vertex: X, Y, Z, confidence
            pointCloudVertexBuffer =
                    new VertexBuffer(render, /*numberOfEntriesPerVertex=*/ 4, /*entries=*/ null);
            final VertexBuffer[] pointCloudVertexBuffers = {pointCloudVertexBuffer};

            // Virtual object to render (ARCore pawn)
            virtualObjectAlbedoTexture =
                    Texture.createFromAsset(
                            render,
                            "models/pawn_albedo.png",
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB);
            Texture virtualObjectPbrTexture =
                    Texture.createFromAsset(
                            render,
                            "models/pawn_roughness_metallic_ao.png",
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.LINEAR);

            virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj");
            virtualObjectShader =
                    Shader.createFromAssets(
                                    render,
                                    "shaders/environmental_hdr.vert",
                                    "shaders/environmental_hdr.frag",
                                    /*defines=*/ new HashMap<String, String>() {
                                        {
                                            put(
                                                    "NUMBER_OF_MIPMAP_LEVELS",
                                                    Integer.toString(cubemapFilter.getNumberOfMipmapLevels()));
                                        }
                                    })
                            .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                            .setTexture("u_RoughnessMetallicAmbientOcclusionTexture", virtualObjectPbrTexture)
                            .setTexture("u_Cubemap", cubemapFilter.getFilteredCubemapTexture())
                            .setTexture("u_DfgTexture", dfgTexture);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read a required asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(SampleRender render, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        virtualSceneFramebuffer.resize(width, height);

        // Store surfaceView dimensions that will be used to compute the 2D position
        surfaceViewWidth = width;
        surfaceViewHeight = height;
    }

    @Override
    public void onDrawFrame(SampleRender render) {
        if (mSession == null) {
            Log.e(TAG, "Session is null!");
            return;
        }

        // Texture names should only be set once on a GL thread unless they change. This is done during
        // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
        // initialized during the execution of onSurfaceCreated.
        if (!hasSetTextureNames) {
            mSession.setCameraTextureNames(new int[]{backgroundRenderer.getCameraColorTexture().getTextureId()});
            hasSetTextureNames = true;
        }

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(mSession);

        // Obtain the current frame from the AR Session. When the configuration is set to
        // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
        // camera framerate.
        try {
            mFrame = mSession.update();
            compassHelper.onUpdate(mFrame);
            mCamera = mFrame.getCamera();
        } catch (CameraNotAvailableException e) {
            Log.e(TAG, "Camera not available during onDrawFrame", e);
            return;
        }

        // Update BackgroundRenderer state to match the depth settings.
        try {
            backgroundRenderer.setUseDepthVisualization(render, false);
            backgroundRenderer.setUseOcclusion(render, false);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read a required asset file", e);
            return;
        }

        // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
        // used to draw the background camera image.
        backgroundRenderer.updateDisplayGeometry(mFrame);

        // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
        trackingStateHelper.updateKeepScreenOnFlag(mCamera.getTrackingState());

        // -- Draw background
        if (mFrame.getTimestamp() != 0) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            backgroundRenderer.drawBackground(render);
        }

        // If tracking is stopped we can ignore this
        if (mCamera.getTrackingState() == TrackingState.STOPPED) {
            Log.e(TAG, "Camera is not tracking! State: " + mCamera.getTrackingState());
            return;
        }

        // Get projection matrix and camera matrix
        mCamera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR);
        mCamera.getViewMatrix(viewMatrix, 0);

        // Update lighting parameters in the shader
        updateLightEstimation(mFrame.getLightEstimate(), viewMatrix);

        // Visualize anchors
        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);

        try {

            for (Map.Entry<String, AnchorRemoteDevice> set : mAnchorRemoteDevices.entrySet()) {
                AnchorRemoteDevice anchorRemoteDevice = set.getValue();
                Anchor anchor = anchorRemoteDevice.getAnchor();
                Accessory accessory = anchorRemoteDevice.getAccessory();
                Position position = anchorRemoteDevice.getPosition();

                // Show Google's ARCore Anchor sample object or accessory information on top of camera matrix
                // depending on app configuration
                if (showARCoreSampleAnchors) {

                    // Skip anchor null situations when initial position is not reliable enough to create the anchor
                    if (anchor != null) {

                        // Get the current pose of an Anchor in world space. The Anchor pose is updated
                        // during calls to session.update() as ARCore refines its estimate of the world.
                        anchor.getPose().toMatrix(modelMatrix, 0);

                        // Calculate model/view/projection matrices
                        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

                        // Update shader properties and draw
                        virtualObjectShader.setMat4("u_ModelView", modelViewMatrix);
                        virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
                        virtualObjectShader.setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture);

                        // Draw anchor
                        render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer);
                    }
                }

                if (showARCoreAccessoryPosition) {
                    // Ignore very high value angles which are usually not reliable enough
                    if (anchor != null && position.getAzimuth() > -60 && position.getAzimuth() < 60 && position.getElevation() > -60 && position.getElevation() < 60) {
                        Coordinates coordinates = getScreenCoordinates(anchor.getPose());
                        Direction direction = getScreenDirection(anchor.getPose());
                        notifyUpdatePositionAccessory(accessory, position, coordinates, direction);
                    } else {
                        notifyUnknownPositionAccessory(accessory, position);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error drawing anchor on screen: " + e.getMessage());
        }

        // Compose the virtual scene with the background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);
    }

    public void updateARCorePositionAccessory(Accessory accessory, Position position) {
        Log.d(TAG, "Accessory: " + accessory.getMac() + ", position update received");

        float distance = position.getDistance();
        float azimuth = position.getAzimuth();
        float elevation = position.getElevation();

        try {
            Anchor newAnchor = null;

            // Ignore very high value angles which are usually not reliable enough
            if (azimuth > -60 && azimuth < 60 && elevation > -60 && elevation < 60) {
                Pose newPose = createPoseFromUwbPosition(distance, azimuth, elevation);
                newAnchor = mSession.createAnchor(
                        mFrame.getCamera().getDisplayOrientedPose()
                                .compose(Pose.makeTranslation(newPose.tx(), newPose.ty(), newPose.tz()))
                                .extractTranslation());
            }

            if (mAnchorRemoteDevices.containsKey(accessory.getMac())) {
                AnchorRemoteDevice anchorRemoteDevice = mAnchorRemoteDevices.get(accessory.getMac());
                if (anchorRemoteDevice != null) {
                    if (newAnchor != null) {
                        Anchor anchor = anchorRemoteDevice.getAnchor();
                        if (anchor != null) {
                            anchor.detach();
                        }

                        anchorRemoteDevice.setAnchor(newAnchor);
                    }

                    // Update anchor information
                    anchorRemoteDevice.setPosition(position);
                    mAnchorRemoteDevices.put(accessory.getMac(), anchorRemoteDevice);
                }
            } else {
                Log.d(TAG, "Create new anchor for this accessory");
                AnchorRemoteDevice newAnchorRemoteDevice = new AnchorRemoteDevice(accessory, position, newAnchor);
                mAnchorRemoteDevices.put(accessory.getMac(), newAnchorRemoteDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeARCoreAccessory(Accessory accessory) {
        mAnchorRemoteDevices.remove(accessory.getMac());
    }

    public void removeARCoreAccessories() {
        mAnchorRemoteDevices.clear();
    }

    private void notifyUpdatePositionAccessory(Accessory accessory, Position position, Coordinates coordinates, Direction direction) {
        for (Listener listener : getListeners()) {
            listener.onARCoreAccessoryPosition(accessory, position, coordinates, direction);
        }
    }

    private void notifyUnknownPositionAccessory(Accessory accessory, Position position) {
        for (Listener listener : getListeners()) {
            listener.onARCoreUnknownAccessoryPosition(accessory, position);
        }
    }

    private Pose createPoseFromUwbPosition(float distance, float azimuth, float elevation) {
        Log.d(TAG, "Create new Pose. Distance: " + distance + ", Azimuth: " + azimuth + ", Elevation: " + elevation);

        // Maths to compute x, y and z axes
        float x = (float) (Math.sin(Math.toRadians(azimuth)) * distance);
        float y = (float) (Math.sin(Math.toRadians(elevation)) * distance);
        float z = (float) ((float) 0 - Math.sqrt(
                Math.pow(distance, 2) - Math.pow(x, 2) - Math.pow(y, 2)
        ));

        float[] pos = {x, y, z};
        float[] rotation = {0, 0, 0, 1};
        return new Pose(pos, rotation);
    }

    public Coordinates getScreenCoordinates(Pose pose) {
        // Matrix to compute coordinates
        float[] model = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        float[] matrix = new float[16];
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Do the maths
        float tX = model[0] * pose.tx() + model[4] * pose.ty() + model[8] * pose.tz() + model[12] * 1f;
        float tY = model[1] * pose.tx() + model[5] * pose.ty() + model[9] * pose.tz() + model[13] * 1f;
        float tZ = model[2] * pose.tx() + model[6] * pose.ty() + model[10] * pose.tz() + model[14] * 1f;
        float tW = model[3] * pose.tx() + model[7] * pose.ty() + model[11] * pose.tz() + model[15] * 1f;
        float tmpX = matrix[0] * tX + matrix[4] * tY + matrix[8] * tZ + matrix[12] * tW;
        float tmpY = matrix[1] * tX + matrix[5] * tY + matrix[9] * tZ + matrix[13] * tW;
        float tmpZ = matrix[2] * tX + matrix[6] * tY + matrix[10] * tZ + matrix[14] * tW;
        float tmpW = matrix[3] * tX + matrix[7] * tY + matrix[11] * tZ + matrix[15] * tW;
        tmpX /= tmpW;
        tmpY /= tmpW;
        tmpZ /= tmpW;
        tmpX = tmpX * 0.5f + 0.5f;
        tmpY = tmpY * 0.5f + 0.5f;
        tmpZ = tmpZ * 0.5f + 0.5f;
        tmpX = tmpX * surfaceViewWidth + 0.0f;
        tmpY = tmpY * surfaceViewHeight + 0.0f;

        return new Coordinates((int) tmpX, surfaceViewHeight - (int) tmpY);
    }

    public Direction getScreenDirection(Pose pose) {
        // Matrix to compute coordinates
        float[] model = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        float[] matrix = new float[16];
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Do the maths
        float tX = model[0] * pose.tx() + model[4] * pose.ty() + model[8] * pose.tz() + model[12] * 1f;
        float tY = model[1] * pose.tx() + model[5] * pose.ty() + model[9] * pose.tz() + model[13] * 1f;
        float tZ = model[2] * pose.tx() + model[6] * pose.ty() + model[10] * pose.tz() + model[14] * 1f;
        float tW = model[3] * pose.tx() + model[7] * pose.ty() + model[11] * pose.tz() + model[15] * 1f;
        float tmpX = matrix[0] * tX + matrix[4] * tY + matrix[8] * tZ + matrix[12] * tW;
        float tmpY = matrix[1] * tX + matrix[5] * tY + matrix[9] * tZ + matrix[13] * tW;
        float tmpZ = matrix[2] * tX + matrix[6] * tY + matrix[10] * tZ + matrix[14] * tW;
        float tmpW = matrix[3] * tX + matrix[7] * tY + matrix[11] * tZ + matrix[15] * tW;
        tmpX /= tmpW;
        tmpY /= tmpW;
        tmpZ /= tmpW;
        tmpX = tmpX * 0.5f + 0.5f;
        tmpY = tmpY * 0.5f + 0.5f;
        tmpZ = tmpZ * 0.5f + 0.5f;
        tmpX = tmpX * surfaceViewWidth + 0.0f;
        tmpY = tmpY * surfaceViewHeight + 0.0f;

        if (tmpX < 0) {
            return Direction.IS_LEFT;
        } else if (tmpX > surfaceViewWidth) {
            return Direction.IS_RIGHT;
        }

        if (tmpY < 0) {
            return Direction.IS_DOWN;
        } else if (tmpY > surfaceViewHeight) {
            return Direction.IS_UP;
        }

        return Direction.IS_VISIBLE;
    }

    /**
     * Update state based on the current frame's light estimation.
     */
    private void updateLightEstimation(LightEstimate lightEstimate, float[] viewMatrix) {
        if (lightEstimate.getState() != LightEstimate.State.VALID) {
            virtualObjectShader.setBool("u_LightEstimateIsValid", false);
            return;
        }
        virtualObjectShader.setBool("u_LightEstimateIsValid", true);

        // Matrix.invertM(viewInverseMatrix, 0, viewMatrix, 0);
        virtualObjectShader.setMat4("u_ViewInverse", viewInverseMatrix);

        updateMainLight(
                lightEstimate.getEnvironmentalHdrMainLightDirection(),
                lightEstimate.getEnvironmentalHdrMainLightIntensity(),
                viewMatrix);
        updateSphericalHarmonicsCoefficients(
                lightEstimate.getEnvironmentalHdrAmbientSphericalHarmonics());
        cubemapFilter.update(lightEstimate.acquireEnvironmentalHdrCubeMap());
    }

    private void updateMainLight(float[] direction, float[] intensity, float[] viewMatrix) {
        // We need the direction in a vec4 with 0.0 as the final component to transform it to view space
        worldLightDirection[0] = direction[0];
        worldLightDirection[1] = direction[1];
        worldLightDirection[2] = direction[2];
        Matrix.multiplyMV(viewLightDirection, 0, viewMatrix, 0, worldLightDirection, 0);
        virtualObjectShader.setVec4("u_ViewLightDirection", viewLightDirection);
        virtualObjectShader.setVec3("u_LightIntensity", intensity);
    }

    private void updateSphericalHarmonicsCoefficients(float[] coefficients) {
        // Pre-multiply the spherical harmonics coefficients before passing them to the shader. The
        // constants in sphericalHarmonicFactors were derived from three terms:
        //
        // 1. The normalized spherical harmonics basis functions (y_lm)
        //
        // 2. The lambertian diffuse BRDF factor (1/pi)
        //
        // 3. A <cos> convolution. This is done to so that the resulting function outputs the irradiance
        // of all incoming light over a hemisphere for a given surface normal, which is what the shader
        // (environmental_hdr.frag) expects.
        //
        // You can read more details about the math here:
        // https://google.github.io/filament/Filament.html#annex/sphericalharmonics

        if (coefficients.length != 9 * 3) {
            throw new IllegalArgumentException(
                    "The given coefficients array must be of length 27 (3 components per 9 coefficients");
        }

        // Apply each factor to every component of each coefficient
        for (int i = 0; i < 9 * 3; ++i) {
            sphericalHarmonicsCoefficients[i] = coefficients[i] * sphericalHarmonicFactors[i / 3];
        }
        virtualObjectShader.setVec3Array(
                "u_SphericalHarmonicsCoefficients", sphericalHarmonicsCoefficients);
    }
}
