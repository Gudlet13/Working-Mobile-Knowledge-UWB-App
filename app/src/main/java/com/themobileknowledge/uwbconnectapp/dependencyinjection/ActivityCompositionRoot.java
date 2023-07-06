package com.themobileknowledge.uwbconnectapp.dependencyinjection;

import androidx.fragment.app.FragmentActivity;

import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.permissions.PermissionHelper;

public class ActivityCompositionRoot {

    private final CompositionRoot mCompositionRoot;
    private final FragmentActivity mActivity;
    private PermissionHelper mPermissionHelper;

    public ActivityCompositionRoot(CompositionRoot mCompositionRoot, FragmentActivity mActivity) {
        this.mCompositionRoot = mCompositionRoot;
        this.mActivity = mActivity;
    }

    public FragmentActivity getActivity() {
        return mActivity;
    }

    public PermissionHelper getPermissionHelper() {
        if(mPermissionHelper == null){
            mPermissionHelper = new PermissionHelper(getActivity());
        }
        return mPermissionHelper;
    }

    public DialogsEventBus getDialogsEventBus() {
        return mCompositionRoot.getDialogsEventBus();
    }
}
