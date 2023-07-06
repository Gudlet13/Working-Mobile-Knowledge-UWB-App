package com.themobileknowledge.uwbconnectapp.dependencyinjection;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.themobileknowledge.uwbconnectapp.bluetooth.BluetoothLEManagerHelper;
import com.themobileknowledge.uwbconnectapp.location.LocationManagerHelper;
import com.themobileknowledge.uwbconnectapp.logger.LoggerHelper;
import com.themobileknowledge.uwbconnectapp.screens.aboutus.AboutUsController;
import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.common.actionhelper.ActionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.packagemanager.PackageManagerHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.permissions.PermissionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.DistanceAlertController;
import com.themobileknowledge.uwbconnectapp.screens.logs.LogsController;
import com.themobileknowledge.uwbconnectapp.screens.selectdemo.SelectDemoController;
import com.themobileknowledge.uwbconnectapp.screens.settings.SettingsController;
import com.themobileknowledge.uwbconnectapp.screens.splash.SplashController;
import com.themobileknowledge.uwbconnectapp.screens.tracker.TrackerController;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.ARCoreHelper;
import com.themobileknowledge.uwbconnectapp.screens.uwbranging.UwbRangingController;
import com.themobileknowledge.uwbconnectapp.storage.database.DatabaseStorageHelper;
import com.themobileknowledge.uwbconnectapp.storage.preferences.PreferenceStorageHelper;
import com.themobileknowledge.uwbconnectapp.uwb.UwbManagerHelper;

public class ControllerCompositionRoot {

    private ActivityCompositionRoot mActivityCompositionRoot;

    public ControllerCompositionRoot(ActivityCompositionRoot mActivityCompositionRoot) {
        this.mActivityCompositionRoot = mActivityCompositionRoot;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mActivityCompositionRoot.getActivity());
    }

    public ViewFactory getBaseViewFactory() {
        return new ViewFactory(getLayoutInflater());
    }

    public SelectDemoController getSelectDemoController() {
        return new SelectDemoController(
                getScreensNavigator(),
                getToastHelper()
        );
    }

    private DialogsManager getDialogsManager() {
        return new DialogsManager(getContext(), getFragmentManager());
    }

    private FragmentActivity getActivity() {
        return mActivityCompositionRoot.getActivity();
    }

    private FragmentManager getFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    public UwbRangingController getUwbRangingController() {
        return new UwbRangingController(
                getScreensNavigator(),
                getPermissionHelper(),
                getPreferenceStorageHelper(),
                getDatabaseStorageHelper(),
                getActionHelper(),
                getLoggerHelper(),
                getToastHelper(),
                getBluetoothLEManagerHelper(),
                getLocationManagerHelper(),
                getUwbManagerHelper(),
                getDialogsManager(),
                getDialogsEventBus()
        );
    }

    public DistanceAlertController getDistanceAlertController() {
        return new DistanceAlertController(
                getScreensNavigator(),
                getPermissionHelper(),
                getPreferenceStorageHelper(),
                getDatabaseStorageHelper(),
                getActionHelper(),
                getLoggerHelper(),
                getToastHelper(),
                getBluetoothLEManagerHelper(),
                getLocationManagerHelper(),
                getUwbManagerHelper(),
                getDialogsManager(),
                getDialogsEventBus()
        );
    }

    public TrackerController getTrackerController() {
        return new TrackerController(
                getScreensNavigator(),
                getPermissionHelper(),
                getPreferenceStorageHelper(),
                getDatabaseStorageHelper(),
                getActionHelper(),
                getLoggerHelper(),
                getToastHelper(),
                getBluetoothLEManagerHelper(),
                getLocationManagerHelper(),
                getUwbManagerHelper(),
                getDialogsManager(),
                getDialogsEventBus(),
                getArCoreHelper()
        );
    }

    public SplashController getSplashController() {
        return new SplashController(
                getScreensNavigator(),
                getPermissionHelper(),
                getPackageManagerHelper(),
                getDialogsManager(),
                getDialogsEventBus()
        );
    }

    public AboutUsController getAboutUsController() {
        return new AboutUsController(
                getScreensNavigator(),
                getPackageManagerHelper(),
                getActionHelper()
        );
    }

    public SettingsController getSettingsController() {
        return new SettingsController(
                getScreensNavigator(),
                getToastHelper(),
                getPreferenceStorageHelper(),
                getDialogsManager(),
                getDialogsEventBus()
        );
    }

    public LogsController getLogsController() {
        return new LogsController(
                getScreensNavigator(),
                getLoggerHelper(),
                getToastHelper(),
                getDialogsManager(),
                getDialogsEventBus()
        );
    }

    private ScreensNavigator getScreensNavigator() {
        return new ScreensNavigator(getContext());
    }

    public LoggerHelper getLoggerHelper() {
        return new LoggerHelper(getContext());
    }

    public ToastsHelper getToastHelper() {
        return new ToastsHelper(getContext());
    }

    public PreferenceStorageHelper getPreferenceStorageHelper() {
        return new PreferenceStorageHelper(getContext());
    }

    public DatabaseStorageHelper getDatabaseStorageHelper() {
        return new DatabaseStorageHelper(getContext());
    }

    public BluetoothLEManagerHelper getBluetoothLEManagerHelper() {
        return new BluetoothLEManagerHelper(getContext());
    }

    public LocationManagerHelper getLocationManagerHelper() {
        return new LocationManagerHelper(getContext());
    }

    public UwbManagerHelper getUwbManagerHelper() {
        return new UwbManagerHelper(getContext());
    }

    public ActionHelper getActionHelper() {
        return new ActionHelper(getContext());
    }

    private PermissionHelper getPermissionHelper() {
        return mActivityCompositionRoot.getPermissionHelper();
    }

    private Context getContext() {
        return mActivityCompositionRoot.getActivity();
    }

    public DialogsEventBus getDialogsEventBus() {
        return mActivityCompositionRoot.getDialogsEventBus();
    }

    public PackageManagerHelper getPackageManagerHelper() {
        return new PackageManagerHelper(getContext());
    }

    private ARCoreHelper getArCoreHelper (){
        return new ARCoreHelper(getActivity(), getContext(), getToastHelper());
    }
}
