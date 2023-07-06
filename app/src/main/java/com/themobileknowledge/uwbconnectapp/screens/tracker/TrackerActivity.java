package com.themobileknowledge.uwbconnectapp.screens.tracker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class TrackerActivity extends BaseActivity {

    private TrackerController mTrackerController;

    public static void start(Context context) {
        Intent intent = new Intent(context, TrackerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackerView mView = getCompositionRoot().getBaseViewFactory().getTrackerScreenView(null);
        mTrackerController = getCompositionRoot().getTrackerController();
        mTrackerController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mTrackerController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.tracker_actionbar_title));
        }

        mTrackerController.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTrackerController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTrackerController.onStop();
    }

    @Override
    public void onBackPressed() {
        mTrackerController.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrackerController.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mTrackerController.saveInstanceState(outState));
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mTrackerController.onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mTrackerController.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}