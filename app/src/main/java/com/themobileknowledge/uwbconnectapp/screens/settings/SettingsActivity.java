package com.themobileknowledge.uwbconnectapp.screens.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;


public class SettingsActivity extends BaseActivity {

    private SettingsController mSettingsController;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsView mView = getCompositionRoot().getBaseViewFactory().getSettingsScreenView(null);
        mSettingsController = getCompositionRoot().getSettingsController();
        mSettingsController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mSettingsController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.settings_actionbar_title));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSettingsController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSettingsController.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mSettingsController.saveInstanceState(outState));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mSettingsController.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        mSettingsController.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mSettingsController.onBackPressed();
        return true;
    }
}