package com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator;

import android.content.Context;

import com.themobileknowledge.uwbconnectapp.screens.aboutus.AboutUsActivity;
import com.themobileknowledge.uwbconnectapp.screens.tracker.TrackerActivity;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.DistanceAlertActivity;
import com.themobileknowledge.uwbconnectapp.screens.uwbranging.UwbRangingActivity;
import com.themobileknowledge.uwbconnectapp.screens.logs.LogsActivity;
import com.themobileknowledge.uwbconnectapp.screens.selectdemo.SelectDemoActivity;
import com.themobileknowledge.uwbconnectapp.screens.settings.SettingsActivity;

public class ScreensNavigator {

    private Context mContext;

    public ScreensNavigator(Context mContext) {
        this.mContext = mContext;
    }

    public void toUwbRangingDemo() {
        UwbRangingActivity.start(mContext);
    }

    public void toDistanceAlertDemo() {
        DistanceAlertActivity.start(mContext);
    }

    public void toTrackingDemo() {
        TrackerActivity.start(mContext);
    }

    public void toSelectDemoMenu() {
        SelectDemoActivity.start(mContext);
    }

    public void toAboutUs() {
        AboutUsActivity.start(mContext);
    }

    public void toSettings() {
        SettingsActivity.start(mContext);
    }

    public void toLogs() {
        LogsActivity.start(mContext);
    }
}
