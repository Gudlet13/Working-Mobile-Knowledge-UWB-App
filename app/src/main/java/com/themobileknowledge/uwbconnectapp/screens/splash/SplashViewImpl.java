package com.themobileknowledge.uwbconnectapp.screens.splash;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;

public class SplashViewImpl extends BaseObservableView<SplashView.Listener> implements SplashView {

    public SplashViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_splash, parent, false));
    }

    @Override
    public void showAppVersion(String version) {
//        ((TextView) (findViewById(R.id.splash_app_version))).setText(
//                String.format(getString(R.string.app_version), version));
    }
}
