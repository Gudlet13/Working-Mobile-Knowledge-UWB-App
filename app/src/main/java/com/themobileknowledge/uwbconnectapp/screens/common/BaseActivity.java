package com.themobileknowledge.uwbconnectapp.screens.common;

import androidx.appcompat.app.AppCompatActivity;

import com.themobileknowledge.uwbconnectapp.CustomApplication;
import com.themobileknowledge.uwbconnectapp.dependencyinjection.ActivityCompositionRoot;
import com.themobileknowledge.uwbconnectapp.dependencyinjection.ControllerCompositionRoot;

public class BaseActivity extends AppCompatActivity {

    private ControllerCompositionRoot mControllerCompositionRoot;
    private ActivityCompositionRoot mActivityCompositionRoot;

    protected ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(getActivityCompositionRoot());
        }
        return mControllerCompositionRoot;
    }

    public ActivityCompositionRoot getActivityCompositionRoot(){
        if (mActivityCompositionRoot == null){
            mActivityCompositionRoot = new ActivityCompositionRoot(
                    ((CustomApplication) getApplication()).getCompositionRoot(),
                    this
            );
        }
        return mActivityCompositionRoot;
    };

}
