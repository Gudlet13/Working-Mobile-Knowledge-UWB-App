package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog;

import androidx.annotation.Nullable;

import java.util.List;

public class SelectAccessoriesDialogEvent {

    public enum Button {
        EDIT,
        SELECT,
        CANCEL
    }

    private final List<Integer> mSelectedAccessoriesPositions;
    private final int mSelectedEditAccessoryPosition;
    private final Button mClickedButton;

    public SelectAccessoriesDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
        mSelectedEditAccessoryPosition = -1;
        mSelectedAccessoriesPositions = null;
    }

    public SelectAccessoriesDialogEvent(Button clickedButton, @Nullable List<Integer> selectedAccessoriesPositions, int selectedEditAccessoryPosition) {
        this.mClickedButton = clickedButton;
        mSelectedAccessoriesPositions = selectedAccessoriesPositions;
        mSelectedEditAccessoryPosition = selectedEditAccessoryPosition;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public List<Integer> getSelectedAccessoriesPositions() {
        return mSelectedAccessoriesPositions;
    }

    public int getSelectedEditAccessoryPosition() {
        return mSelectedEditAccessoryPosition;
    }
}
