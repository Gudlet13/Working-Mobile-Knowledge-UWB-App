package com.themobileknowledge.uwbconnectapp.screens.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.themobileknowledge.uwbconnectapp.screens.aboutus.AboutUsView;
import com.themobileknowledge.uwbconnectapp.screens.aboutus.AboutUsViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.DistanceAlertView;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.DistanceAlertViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem.DistanceAlertItemViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.logs.LogsView;
import com.themobileknowledge.uwbconnectapp.screens.logs.LogsViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.selectdemo.SelectDemoView;
import com.themobileknowledge.uwbconnectapp.screens.selectdemo.SelectDemoViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.settings.SettingsView;
import com.themobileknowledge.uwbconnectapp.screens.settings.SettingsViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.splash.SplashView;
import com.themobileknowledge.uwbconnectapp.screens.splash.SplashViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.tracker.TrackerView;
import com.themobileknowledge.uwbconnectapp.screens.tracker.TrackerViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.uwbranging.UwbRangingView;
import com.themobileknowledge.uwbconnectapp.screens.uwbranging.UwbRangingViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.listitems.SelectAccessoriesDialogItemViewImpl;

public class ViewFactory {
    private final LayoutInflater mLayoutInflater;

    public ViewFactory(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
    }

    public SelectDemoView getMainScreenView(@Nullable ViewGroup parent) {
        return new SelectDemoViewImpl(getLayoutInflater(), parent);
    }

    public SplashView getSplashScreenView(@Nullable ViewGroup parent) {
        return new SplashViewImpl(getLayoutInflater(), parent);
    }

    public UwbRangingView getUwbRangingScreenView(@Nullable ViewGroup parent) {
        return new UwbRangingViewImpl(getLayoutInflater(), parent, this);
    }

    public DistanceAlertView getDistanceAlertScreenView(@Nullable ViewGroup parent) {
        return new DistanceAlertViewImpl(getLayoutInflater(), parent, this);
    }

    public TrackerView getTrackerScreenView(@Nullable ViewGroup parent) {
        return new TrackerViewImpl(getLayoutInflater(), parent, this);
    }

    public AboutUsView getAboutUsScreenView(@Nullable ViewGroup parent) {
        return new AboutUsViewImpl(getLayoutInflater(), parent);
    }

    public SettingsView getSettingsScreenView(@Nullable ViewGroup parent) {
        return new SettingsViewImpl(getLayoutInflater(), parent);
    }

    public LogsView getLogsScreenView(@Nullable ViewGroup parent) {
        return new LogsViewImpl(getLayoutInflater(), parent);
    }

    public SelectAccessoriesDialogItemViewImpl getSelectAccessoriesDialogItemView(ViewGroup parent) {
        return new SelectAccessoriesDialogItemViewImpl(getLayoutInflater(), parent);
    }

    public DistanceAlertItemViewImpl getDistanceAlertItemView(ViewGroup parent) {
        return new DistanceAlertItemViewImpl(getLayoutInflater(), parent);
    }

    private LayoutInflater getLayoutInflater(){
        return mLayoutInflater;
    }

}
