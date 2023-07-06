package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog;

public class PromptDialogEvent {

    public enum Button {
        POSITIVE,
        NEGATIVE,
        CANCELLED
    }

    private final Button mClickedButton;

    public PromptDialogEvent(Button mClickedButton) {
        this.mClickedButton = mClickedButton;
    }

    public Button getClickedButton() {
        return mClickedButton;
    }

}
