package com.themobileknowledge.uwbconnectapp.screens.splash;

import android.Manifest;
import android.os.Handler;
import android.os.Looper;

import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.packagemanager.PackageManagerHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.permissions.PermissionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;

public class SplashController implements SplashViewImpl.Listener, PermissionHelper.Listener, DialogsEventBus.Listener {

    private static final long SPLASH_ACTIVITY_DURATION = 2000;
    private static final int REQUEST_CODE = 55;

    private static final String DIALOGTAG_PERMISSIONDECLINED = "DIALOGTAG_PERMISSIONDECLINED";
    private static final String DIALOGTAG_PERMISSIONDECLINEDDONTASKAGAIN = "DIALOGTAG_PERMISSIONDECLINEDDONTASKAGAIN";

    private final ScreensNavigator mScreensNavigator;
    private final PermissionHelper mPermissionHelper;
    private final PackageManagerHelper mPackageManagerHelper;
    private final DialogsManager mDialogsManager;
    private final DialogsEventBus mDialogsEventBus;

    private SplashView mView;

    private int mGrantedPermissions = 0;
    private final String[] permissionsList = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.UWB_RANGING
    };

    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    private enum ScreenState {
        SCREEN_SHOWN,
        PERMISSIONDECLINED_DIALOG_SHOWN,
        PERMISSIONDECLINEDDONTASKAGAIN_DIALOG_SHOWN,
    }

    public SplashController(ScreensNavigator screensNavigator,
                            PermissionHelper permissionHelper,
                            PackageManagerHelper packageManagerHelper,
                            DialogsManager dialogsManager,
                            DialogsEventBus dialogsEventBus) {
        mScreensNavigator = screensNavigator;
        mPermissionHelper = permissionHelper;
        mPackageManagerHelper = packageManagerHelper;
        mDialogsManager = dialogsManager;
        mDialogsEventBus = dialogsEventBus;
    }

    public void bindView(SplashView view) {
        mView = view;
    }

    public void onStart() {
        mPermissionHelper.registerListener(this);
        mDialogsEventBus.registerListener(this);

        showAppVersion();
        if (checkPermissions()) {
            executeTransitionTimer();
        } else {
            requestPermissions();
        }
    }

    public void onStop() {
        mPermissionHelper.unregisterListener(this);
        mDialogsEventBus.unregisterListener(this);
    }

    private void showAppVersion() {
        mView.showAppVersion(mPackageManagerHelper.getAppVersion());
    }

    private void executeTransitionTimer() {
        new Handler(Looper.getMainLooper()).postDelayed(screenTransition, SPLASH_ACTIVITY_DURATION);
    }

    private final Runnable screenTransition = new Runnable() {
        @Override
        public void run() {
            mScreensNavigator.toSelectDemoMenu();
        }
    };

    private boolean checkPermissions() {
        return mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_SCAN)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
                && mPermissionHelper.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && mPermissionHelper.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && mPermissionHelper.hasPermission(Manifest.permission.UWB_RANGING);
    }

    private void requestPermissions() {
        mPermissionHelper.requestPermissions(permissionsList, REQUEST_CODE);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(String permission, int requestCode) {
        mGrantedPermissions++;
        if(mGrantedPermissions == permissionsList.length) {
            executeTransitionTimer();
        }
    }

    @Override
    public void onPermissionDeclined(String permission, int requestCode) {
        // Do not show the dialog if already shown
        if (mScreenState == ScreenState.SCREEN_SHOWN) {
            mScreenState = ScreenState.PERMISSIONDECLINED_DIALOG_SHOWN;
            mDialogsManager.showPermissionsDeclinedDialog(DIALOGTAG_PERMISSIONDECLINED);
        }
    }

    @Override
    public void onPermissionDeclinedDontAskAgain(String permission, int requestCode) {
        // Do not show the dialog if already shown
        if (mScreenState == ScreenState.SCREEN_SHOWN) {
            mScreenState = ScreenState.PERMISSIONDECLINEDDONTASKAGAIN_DIALOG_SHOWN;
            mDialogsManager.showPermissionsDeclinedDontAskAgainDialog(DIALOGTAG_PERMISSIONDECLINEDDONTASKAGAIN);
        }
    }

    @Override
    public void onDialogEvent(Object event) {
        executeTransitionTimer();
    }
}
