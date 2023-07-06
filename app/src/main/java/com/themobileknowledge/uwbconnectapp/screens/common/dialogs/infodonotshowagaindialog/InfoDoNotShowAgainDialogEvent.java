package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodonotshowagaindialog;

public class InfoDoNotShowAgainDialogEvent {

    private boolean mShowInfo;

    public enum Button {
        ACCEPT,
        CANCEL
    }

    private final Button mClickedButton;

    public InfoDoNotShowAgainDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
    }

    public InfoDoNotShowAgainDialogEvent(Button clickedButton, boolean showInfo) {
        this.mClickedButton = clickedButton;
        mShowInfo = showInfo;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public boolean getShowInfo() {
        return mShowInfo;
    }
}
