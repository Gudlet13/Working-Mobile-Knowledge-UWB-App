package com.themobileknowledge.uwbconnectapp.screens.selectdemo;


import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface SelectDemoView extends IBaseObservableView<SelectDemoView.Listener> {

    interface Listener {
        void onUwbRangingClicked();

        void onDistanceAlertClicked();

        void onTrackerClicked();

        void onPointTriggerClicked();

        void onDrawerAboutUsClicked();

        void onDrawerSettingsClicked();

        void onDrawerLogsClicked();
    }

    boolean isDrawerOpen();

    void closeDrawer();

    void openDrawer();

    boolean onOptionsItemSelected(MenuItem item);
}
