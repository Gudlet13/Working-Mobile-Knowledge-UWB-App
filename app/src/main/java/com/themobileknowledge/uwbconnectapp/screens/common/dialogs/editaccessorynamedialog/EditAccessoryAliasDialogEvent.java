package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.editaccessorynamedialog;

import androidx.annotation.Nullable;

import com.themobileknowledge.uwbconnectapp.model.Accessory;

public class EditAccessoryAliasDialogEvent {

    private Accessory mAccessory;
    private String mAccessoryAlias;

    public enum Button {
        EDIT,
        CANCEL
    }

    private final Button mClickedButton;

    public EditAccessoryAliasDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
    }

    public EditAccessoryAliasDialogEvent(Button clickedButton, @Nullable Accessory accessory, @Nullable String accessoryAlias) {
        this.mClickedButton = clickedButton;
        mAccessoryAlias = accessoryAlias;
        mAccessory = accessory;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public String getAccessoryAlias() {
        return mAccessoryAlias;
    }

    public Accessory getAccessory() {
        return mAccessory;
    }
}
