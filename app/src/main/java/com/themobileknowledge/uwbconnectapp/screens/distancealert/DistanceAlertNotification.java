package com.themobileknowledge.uwbconnectapp.screens.distancealert;

import com.themobileknowledge.uwbconnectapp.model.Accessory;

public class DistanceAlertNotification {
    private Accessory accessory;
    private int distance;
    private byte lowerDistanceThreshold;
    private long timeStamp;

    public DistanceAlertNotification(Accessory accessory, int distance, byte lowerDistanceThreshold) {
        this.accessory = accessory;
        this.distance = distance;
        this.lowerDistanceThreshold = lowerDistanceThreshold;
        this.timeStamp = System.currentTimeMillis();
    }

    public Accessory getAccessory() {
        return accessory;
    }

    public byte getLowerThreshold() {
        return lowerDistanceThreshold;
    }

    public int getDistance() {
        return distance;
    }

    public long getTimeStamp() { return timeStamp; }
}
