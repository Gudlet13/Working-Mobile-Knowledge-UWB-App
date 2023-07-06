package com.themobileknowledge.uwbconnectapp.screens.common.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public abstract class BaseView implements IBaseView {

    private View mRootView;

    @Override
    public View getRootView() {
        return mRootView;
    }

    protected void setRootView(View rootView) {
        this.mRootView = rootView;
    }

    protected <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }

    protected Context getContext() {
        return getRootView().getContext();
    }

    protected String getString(int id) {
        return getContext().getString(id);
    }

    protected String getString(int id, Object... formatArgs) {
        return getContext().getString(id, formatArgs);
    }

    protected Activity getActivity() {
        return (Activity) getContext();
    }
}
