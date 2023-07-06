package com.themobileknowledge.uwbconnectapp.screens.logs;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.logger.LoggerHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog.PromptDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.screens.logs.dialogs.exportlogsdialog.ExportLogsDialogEvent;

import java.util.List;


public class LogsController implements LogsViewImpl.Listener, DialogsEventBus.Listener {

    // Helper title for logs display
    private final static String logsHeader = "[Time][Demo][Event][DevName][DevMac][Distance][Azimuth][Elevation]\n";

    // Maximum lines of logs to be shown in the screen
    // A very large amount of lines will have an impact when showing the logs
    private final static int MAX_LOGS_LINES = 100;

    private static final String DIALOGTAG_LOGSCLEARTAG = "DIALOGTAG_LOGSCLEARTAG";
    private static final String DIALOGTAG_LOGSEXPORTTAG = "DIALOGTAG_LOGSEXPORTTAG";

    private static final String SAVED_STATE_SCREEN_STATE = "SAVED_STATE_SCREEN_STATE";

    private final ScreensNavigator mScreensNavigator;
    private final LoggerHelper mLoggerHelper;
    private final ToastsHelper mToastsHelper;
    private final DialogsManager mDialogsManager;
    private final DialogsEventBus mDialogsEventBus;

    private LogsView mView;

    private Menu mMenu;

    private Bundle mSavedInstanceState = null;
    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    private enum ScreenState {
        SCREEN_SHOWN,
        DIALOG_LOGSCLEAR__DIALOG_SHOWN,
        DIALOG_LOGSEXPORT__DIALOG_SHOWN,
    }

    public LogsController(ScreensNavigator mScreensNavigator,
                          LoggerHelper loggerHelper,
                          ToastsHelper toastshelper,
                          DialogsManager dialogsManager,
                          DialogsEventBus dialogsEventBus) {
        this.mScreensNavigator = mScreensNavigator;
        this.mLoggerHelper = loggerHelper;
        this.mToastsHelper = toastshelper;
        this.mDialogsManager = dialogsManager;
        this.mDialogsEventBus = dialogsEventBus;
    }

    public void bindView(LogsView view) {
        mView = view;
    }

    public void setInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public Bundle saveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATE_SCREEN_STATE, mScreenState);
        return outState;
    }

    public void onStart() {
        if (mSavedInstanceState != null) {
            mScreenState = (ScreenState) mSavedInstanceState.getSerializable(SAVED_STATE_SCREEN_STATE);
        }

        mView.registerListener(this);
        mDialogsEventBus.registerListener(this);

        showLogs();
    }

    public void onStop() {
        mView.unregisterListener(this);
        mDialogsEventBus.unregisterListener(this);
    }

    public void onBackPressed() {
        mScreensNavigator.toSelectDemoMenu();
    }

    public void onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        mView.bindMenu(mMenu);
    }

    public void onOptionsItemSelected(MenuItem item) {
        mView.onMenuItemSelected(item);
    }

    private void showLogs() {
        String logs = mLoggerHelper.readLogs(MAX_LOGS_LINES);
        if (logs != null && !logs.isEmpty()) {
            mView.showLogs(logsHeader.concat(logs));
        } else {
            mView.showEmptyLogsAlert();
        }
    }

    private void clearLogs() {
        mLoggerHelper.clearLogs();
        mView.showEmptyLogsAlert();
    }

    private void exportLogs(List<Boolean> selectedExportFormats) {
        // Txt format
        if (selectedExportFormats.get(0)) {
            mLoggerHelper.exportLogsTxt();
        }

        // CSV format
        if (selectedExportFormats.get(1)) {
            mLoggerHelper.exportLogsCsv();
        }

        mToastsHelper.notifyGenericMessage("Logs exported");
    }

    @Override
    public void onMenuLogsClearClicked() {
        mScreenState = ScreenState.DIALOG_LOGSCLEAR__DIALOG_SHOWN;
        mDialogsManager.showLogsClearDialog(DIALOGTAG_LOGSCLEARTAG);
    }

    @Override
    public void onMenuLogsExportClicked() {
        mScreenState = ScreenState.DIALOG_LOGSEXPORT__DIALOG_SHOWN;
        mDialogsManager.showLogsExportDialog(DIALOGTAG_LOGSEXPORTTAG);
    }

    @Override
    public void onDialogEvent(Object event) {
        if (event instanceof ExportLogsDialogEvent) {
            switch (((ExportLogsDialogEvent) event).getClickedButton()) {
                case EXPORT:
                    exportLogs (((ExportLogsDialogEvent) event).getSelectedExportFormats());
                    break;
                case CANCEL:
                    break;
            }

            mScreenState = ScreenState.SCREEN_SHOWN;
        } else if (event instanceof PromptDialogEvent) {
            switch (mScreenState) {
                case DIALOG_LOGSCLEAR__DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            clearLogs();
                            break;

                        default:
                            break;
                    }

                    break;
            }
        }
    }
}
