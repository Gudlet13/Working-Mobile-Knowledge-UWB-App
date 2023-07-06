package com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters;

import com.themobileknowledge.uwbconnectapp.screens.distancealert.DistanceAlertNotification;

public class DistanceAlertRecyclerItem {

    private DistanceAlertNotification mNotification;
    private boolean isThresholdLine;
    private int mThresholdLimit;

    private DistanceAlertRecyclerItem() {
    }

    public DistanceAlertRecyclerItem(DistanceAlertNotification notification) {
        mNotification = notification;
        isThresholdLine = false;
    }

    public DistanceAlertRecyclerItem(int thresholdLimit) {
        mThresholdLimit = thresholdLimit;
        isThresholdLine = true;
    }

    public DistanceAlertNotification getNotification() {
        return mNotification;
    }


    public void setNotification(DistanceAlertNotification notification) {
        this.mNotification = notification;
    }

    public boolean isThresholdLine() {
        return isThresholdLine;
    }

    public int getThresholdLimit() {
        return mThresholdLimit;
    }
}
