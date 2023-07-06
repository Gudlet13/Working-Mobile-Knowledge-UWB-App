package com.themobileknowledge.uwbconnectapp.screens.selectdemo;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;

public class SelectDemoViewImpl extends BaseObservableView<SelectDemoView.Listener> implements SelectDemoView, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final DrawerLayout mDrawerLayout;
    private final NavigationView mNavigationView;
    private final ActionBarDrawerToggle mActionBarDrawerToggle;

    public SelectDemoViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_selectdemo, parent, false));
        findViewById(R.id.selectdemo_uwbranging_button).setOnClickListener(this);
        findViewById(R.id.selectdemo_distance_button).setOnClickListener(this);
        findViewById(R.id.selectdemo_tracker_button).setOnClickListener(this);
        findViewById(R.id.selectdemo_pointtrigger_button).setOnClickListener(this);

        mDrawerLayout = findViewById(R.id.selectdemo_drawerlayout);
        mNavigationView = findViewById(R.id.selectdemo_navigationView);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.actionbar_drawer_open, R.string.actionbar_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void notifyBasicClicked() {
        for (Listener listener : getListeners()) {
            listener.onUwbRangingClicked();
        }
    }

    private void notifyDistanceAlertClicked() {
        for (Listener listener : getListeners()) {
            listener.onDistanceAlertClicked();
        }
    }

    private void notifyTrackerClicked() {
        for (Listener listener : getListeners()) {
            listener.onTrackerClicked();
        }
    }

    private void notifyPointTriggerClicked() {
        for (Listener listener : getListeners()) {
            listener.onPointTriggerClicked();
        }
    }

    private void notifyDrawerAboutUsClicked() {
        for (Listener listener : getListeners()) {
            listener.onDrawerAboutUsClicked();
        }
    }

    private void notifyDrawerSettingsClicked() {
        for (Listener listener : getListeners()) {
            listener.onDrawerSettingsClicked();
        }
    }

    private void notifyDrawerLogsClicked() {
        for (Listener listener : getListeners()) {
            listener.onDrawerLogsClicked();
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectdemo_uwbranging_button:
                notifyBasicClicked();
                break;
            case R.id.selectdemo_distance_button:
                notifyDistanceAlertClicked();
                break;
            case R.id.selectdemo_tracker_button:
                notifyTrackerClicked();
                break;
            case R.id.selectdemo_pointtrigger_button:
                notifyPointTriggerClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawerItemAboutUs:
                notifyDrawerAboutUsClicked();
                break;
            case R.id.drawerItemSettings:
                notifyDrawerSettingsClicked();
                break;
            case R.id.drawerItemLogs:
                notifyDrawerLogsClicked();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.selectdemo_drawerlayout);
        drawer.closeDrawer(GravityCompat.START);

        item.setChecked(false);
        item.setCheckable(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item);
    }
}
