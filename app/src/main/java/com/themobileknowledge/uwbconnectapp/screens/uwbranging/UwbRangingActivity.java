package com.themobileknowledge.uwbconnectapp.screens.uwbranging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class UwbRangingActivity extends BaseActivity {

    private UwbRangingController mUwbRangingController;

    public static void start(Context context) {
        Intent intent = new Intent(context, UwbRangingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UwbRangingView mView = getCompositionRoot().getBaseViewFactory().getUwbRangingScreenView(null);
        mUwbRangingController = getCompositionRoot().getUwbRangingController();
        mUwbRangingController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mUwbRangingController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.uwbranging_actionbar_title));
        }

        mUwbRangingController.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUwbRangingController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUwbRangingController.onStop();
    }

    @Override
    public void onBackPressed() {
        mUwbRangingController.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUwbRangingController.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mUwbRangingController.saveInstanceState(outState));
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mUwbRangingController.onBackPressed();
        return true;
    }
}