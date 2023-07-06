package com.themobileknowledge.uwbconnectapp.screens.selectdemo;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;

public class SelectDemoController implements SelectDemoViewImpl.Listener {

    private final ScreensNavigator mScreensNavigator;
    private final ToastsHelper mToastsHelper;
    private SelectDemoView mView;

    public SelectDemoController(ScreensNavigator mScreensNavigator,
                                ToastsHelper toastshelper) {
        this.mScreensNavigator = mScreensNavigator;
        this.mToastsHelper = toastshelper;
    }

    public void bindView(SelectDemoView view) {
        mView = view;
    }

    public void onStart() {
        mView.registerListener(this);
    }

    public void onStop() {
        mView.unregisterListener(this);
    }

    public boolean onBackPressed() {
        if (mView.isDrawerOpen()) {
            mView.closeDrawer();
            return false;
        } else {
            return true;
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return mView.onOptionsItemSelected(item);
    }

    @Override
    public void onUwbRangingClicked() {
        mScreensNavigator.toUwbRangingDemo();
    }

    @Override
    public void onDistanceAlertClicked() {
        mScreensNavigator.toDistanceAlertDemo();
    }

    @Override
    public void onTrackerClicked() {
        mScreensNavigator.toTrackingDemo();
    }

    @Override
    public void onPointTriggerClicked() {
        mToastsHelper.notifyGenericMessage("Coming soon!");
    }

    @Override
    public void onDrawerAboutUsClicked() {
        mScreensNavigator.toAboutUs();
    }

    @Override
    public void onDrawerSettingsClicked() {
        mScreensNavigator.toSettings();
    }

    @Override
    public void onDrawerLogsClicked() {
        mScreensNavigator.toLogs();
    }
}
