package com.themobileknowledge.uwbconnectapp.screens.logs;

import android.view.Menu;
import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface LogsView extends IBaseObservableView<LogsView.Listener> {

    interface Listener {
        void onBackPressed();

        void onMenuLogsClearClicked();

        void onMenuLogsExportClicked();
    }

    void bindMenu(Menu menu);

    void onMenuItemSelected(MenuItem item);

    void showEmptyLogsAlert();

    void showLogs(String logs);
}
