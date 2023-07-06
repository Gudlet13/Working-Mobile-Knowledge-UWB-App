package com.themobileknowledge.uwbconnectapp.screens.common.dialogs;

import androidx.fragment.app.DialogFragment;

import com.themobileknowledge.uwbconnectapp.dependencyinjection.ControllerCompositionRoot;
import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public abstract class BaseDialog extends DialogFragment {

    private ControllerCompositionRoot mControllerCompositionRoot;

    protected ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(
                    ((BaseActivity) requireActivity()).getActivityCompositionRoot()
            );
        }
        return mControllerCompositionRoot;
    }

}
