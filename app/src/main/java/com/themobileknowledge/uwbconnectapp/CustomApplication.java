package com.themobileknowledge.uwbconnectapp;

import android.app.Application;

import com.themobileknowledge.uwbconnectapp.dependencyinjection.CompositionRoot;

public class CustomApplication extends Application {
    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositionRoot = new CompositionRoot();
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }
}
