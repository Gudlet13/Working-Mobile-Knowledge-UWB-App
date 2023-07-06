package com.themobileknowledge.uwbconnectapp.screens.distancealert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class DistanceAlertActivity extends BaseActivity {

    private DistanceAlertController mDistanceAlertController;

    public static void start(Context context) {
        Intent intent = new Intent(context, DistanceAlertActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DistanceAlertView mView = getCompositionRoot().getBaseViewFactory().getDistanceAlertScreenView(null);
        mDistanceAlertController = getCompositionRoot().getDistanceAlertController();
        mDistanceAlertController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mDistanceAlertController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.distancealert_actionbar_title));
        }

        mDistanceAlertController.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDistanceAlertController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDistanceAlertController.onStop();
    }

    @Override
    public void onBackPressed() {
        mDistanceAlertController.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDistanceAlertController.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mDistanceAlertController.saveInstanceState(outState));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mDistanceAlertController.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        mDistanceAlertController.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mDistanceAlertController.onBackPressed();
        return true;
    }
}