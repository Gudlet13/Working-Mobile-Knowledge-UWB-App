package com.themobileknowledge.uwbconnectapp.screens.settings.dialogs.selectsettingsdialog;

import androidx.annotation.Nullable;

public class SelectSettingsDialogEvent {

    private int mSelectedPosition;

    public enum Button {
        SELECT,
        CANCEL
    }

    private final Button mClickedButton;

    public SelectSettingsDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
    }

    public SelectSettingsDialogEvent(Button clickedButton, @Nullable int selectedPosition) {
        this.mClickedButton = clickedButton;
        mSelectedPosition = selectedPosition;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

}
