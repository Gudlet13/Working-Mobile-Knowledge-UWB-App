package com.themobileknowledge.uwbconnectapp.screens.splash;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class SplashActivity extends BaseActivity implements SplashViewImpl.Listener {

    private SplashController mSplashController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashView mView = getCompositionRoot().getBaseViewFactory().getSplashScreenView(null);
        mSplashController = getCompositionRoot().getSplashController();
        mSplashController.bindView(mView);
        setContentView(mView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSplashController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSplashController.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSplashController.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}