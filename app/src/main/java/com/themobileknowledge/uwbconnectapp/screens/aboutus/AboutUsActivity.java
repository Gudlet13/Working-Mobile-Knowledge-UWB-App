package com.themobileknowledge.uwbconnectapp.screens.aboutus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class AboutUsActivity extends BaseActivity {

    private AboutUsController mAboutUsController;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AboutUsView mView = getCompositionRoot().getBaseViewFactory().getAboutUsScreenView(null);
        mAboutUsController = getCompositionRoot().getAboutUsController();
        mAboutUsController.bindView(mView);
        setContentView(mView.getRootView());

        if (savedInstanceState != null) {
            mAboutUsController.setInstanceState(savedInstanceState);
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.aboutus_actionbar_title));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAboutUsController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAboutUsController.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mAboutUsController.saveInstanceState(outState));
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        mAboutUsController.onBackPressed();
        return true;
    }
}