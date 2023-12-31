package com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;

public class CompassHelper implements SensorEventListener {
    private float[] accumulated = new float[3];
    private Pose deviceToWorld;
    private final DisplayRotationHelper rotationHelper;
    private SensorManager sensorManager;

    private final float DECAY_RATE = 0.9f;
    private final float SQRT_HALF = (float) Math.sqrt(0.5f);
    private final float VALID_TRESHOLD = 0.1f;

    public CompassHelper(Context context, DisplayRotationHelper rotationHelper) {
        this.rotationHelper = rotationHelper;
        sensorManager = context.getSystemService(SensorManager.class);
    }

    public void onResume() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor
                .TYPE_MAGNETIC_FIELD), 200000 /* 5Hz */);
    }

    public void onUpdate(Frame frame) {
        deviceToWorld = getDevicePose(frame).extractRotation();
        if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            for (int i = 0; i < 3; ++i) {
                accumulated[i] = 0;
            }
        }
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    public void getFieldDirection(float[] out) {
        System.arraycopy(accumulated, 0, out, 0, 3);
    }

    public boolean rotationValid() {
        return (accumulated[0] * accumulated[0] + accumulated[2] * accumulated[2]) > VALID_TRESHOLD;
    }


    private Pose getDevicePose(Frame frame) {
        return frame.getCamera().getDisplayOrientedPose().compose(
                Pose.makeInterpolated(
                        Pose.IDENTITY,
                        Pose.makeRotation(0, 0, SQRT_HALF, SQRT_HALF),
                        rotationHelper.getRotation()));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] mAccelerometerData = new float[3];
        float[] mMagnetometerData = new float[3];
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];
        float[] newValues = new float[3];
        newValues[0] = 50f;
        newValues[1] = 10f;
        newValues[2] = -2.0f;

//        if (sensorEvent.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD) return;
        float[] rotated = new float[3];
        if (deviceToWorld != null) {
//      deviceToWorld.rotateVector(sensorEvent.values, 0, rotated, 0);
            deviceToWorld.rotateVector(newValues, 0, rotated, 0);
        }
        for (int i = 0; i < 3; ++i) {
            accumulated[i] = accumulated[i] * DECAY_RATE + rotated[i];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
