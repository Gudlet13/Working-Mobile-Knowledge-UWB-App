package com.themobileknowledge.uwbconnectapp.screens.distancealert.dialogs.editdistancealertthresholdsdialog;

import androidx.annotation.Nullable;

public class EditDistanceAlertThresholdsDialogEvent {

    private int mCloseRangeThreshold;
    private int mFarRangeThreshold;

    public enum Button {
        EDIT,
        CANCEL
    }

    private final Button mClickedButton;

    public EditDistanceAlertThresholdsDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
    }

    public EditDistanceAlertThresholdsDialogEvent(Button clickedButton, @Nullable int closeRangeThreshold, @Nullable int farRangeThreshold) {
        this.mClickedButton = clickedButton;
        mCloseRangeThreshold = closeRangeThreshold;
        mFarRangeThreshold = farRangeThreshold;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public int getCloseRangeThreshold() {
        return mCloseRangeThreshold;
    }

    public int getFarRangeThreshold() {
        return mFarRangeThreshold;
    }
}
