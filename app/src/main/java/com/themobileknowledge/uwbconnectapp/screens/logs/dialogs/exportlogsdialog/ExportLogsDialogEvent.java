package com.themobileknowledge.uwbconnectapp.screens.logs.dialogs.exportlogsdialog;

import androidx.annotation.Nullable;

import java.util.List;

public class ExportLogsDialogEvent {

    private List<Boolean> mSelectedExportFormats;

    public enum Button {
        EXPORT,
        CANCEL
    }

    private final Button mClickedButton;

    public ExportLogsDialogEvent(Button clickedButton){
        this.mClickedButton = clickedButton;
        mSelectedExportFormats = null;
    }

    public ExportLogsDialogEvent(Button clickedButton, @Nullable List<Boolean> selectedExportFormats) {
        this.mClickedButton = clickedButton;
        mSelectedExportFormats = selectedExportFormats;
    }

    public Button getClickedButton () {
        return mClickedButton;
    }

    public List<Boolean> getSelectedExportFormats() {
        return mSelectedExportFormats;
    }

}
