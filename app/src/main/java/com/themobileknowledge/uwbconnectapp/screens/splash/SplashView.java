package com.themobileknowledge.uwbconnectapp.screens.splash;


import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface SplashView extends IBaseObservableView<SplashView.Listener> {

    interface Listener {
    }

    void showAppVersion(String version);
}
