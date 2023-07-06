package com.themobileknowledge.uwbconnectapp.screens.logs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class LogsActivity extends BaseActivity {

    private LogsController mLogsController;

    public static void start(Context context) {
        Intent intent = new Intent(context, LogsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogsView mView = getCompositionRoot().getBaseViewFactory().getLogsScreenView(null);
        mLogsController = getCompositionRoot().getLogsController();
        mLogsController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mLogsController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.logs_actionbar_title));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLogsController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLogsController.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mLogsController.saveInstanceState(outState));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mLogsController.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        mLogsController.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mLogsController.onBackPressed();
        return true;
    }
}