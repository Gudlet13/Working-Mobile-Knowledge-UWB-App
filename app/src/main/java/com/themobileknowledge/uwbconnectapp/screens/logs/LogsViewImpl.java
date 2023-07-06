package com.themobileknowledge.uwbconnectapp.screens.logs;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;

public class LogsViewImpl extends BaseObservableView<LogsView.Listener> implements LogsView {

    private TextView logsText;

    public LogsViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_logs, parent, false));
        logsText = findViewById(R.id.logs_logs);
    }

    private void notifyOnBackPressed() {
        for (Listener listener : getListeners()) {
            listener.onBackPressed();
        }
    }

    private void notifyMenuLogsClearClicked() {
        for (Listener listener : getListeners()) {
            listener.onMenuLogsClearClicked();
        }
    }

    private void notifyMenuLogsExportClicked() {
        for (Listener listener : getListeners()) {
            listener.onMenuLogsExportClicked();
        }
    }

    @Override
    public void bindMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_actionbar_logs, menu);
    }

    @Override
    public void onMenuItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                notifyOnBackPressed();
                break;
            case R.id.menu_logs_clear:
                notifyMenuLogsClearClicked();
                break;
            case R.id.menu_logs_export:
                notifyMenuLogsExportClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public void showLogs(String logs) {
        logsText.setText(logs);
    }

    @Override
    public void showEmptyLogsAlert() {
        logsText.setText(getString(R.string.logs_emptylogs));
    }
}
