package com.themobileknowledge.uwbconnectapp.screens.aboutus;

import android.os.Bundle;

import com.themobileknowledge.uwbconnectapp.screens.common.actionhelper.ActionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.packagemanager.PackageManagerHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;


public class AboutUsController implements AboutUsViewImpl.Listener {

    private static final String LOG_DEMONAME = "ABOUTUS";

    private static final String SAVED_STATE_SCREEN_STATE = "SAVED_STATE_SCREEN_STATE";

    private final ScreensNavigator mScreensNavigator;
    private final PackageManagerHelper mPackageManagerHelper;
    private final ActionHelper mActionHelper;
    private AboutUsView mView;

    private Bundle mSavedInstanceState = null;
    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    private enum ScreenState {
        SCREEN_SHOWN
    }

    public AboutUsController(ScreensNavigator screensNavigator,
                             PackageManagerHelper packageManagerHelper,
                             ActionHelper actionHelper) {
        this.mScreensNavigator = screensNavigator;
        this.mPackageManagerHelper = packageManagerHelper;
        this.mActionHelper = actionHelper;
    }

    public void bindView(AboutUsView view) {
        mView = view;
    }

    public void setInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public Bundle saveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATE_SCREEN_STATE, mScreenState);
        return outState;
    }

    public void onStart() {
        if (mSavedInstanceState != null) {
            mScreenState = (ScreenState) mSavedInstanceState.getSerializable(SAVED_STATE_SCREEN_STATE);
        }

        mView.registerListener(this);

        mView.showMkUwbConnectApp(mPackageManagerHelper.getAppVersion());
        mView.showMkVisitUsLink();
        mView.showNxpTrimensionLink();
        mView.showMkContactEmail();
    }

    public void onStop() {
        mView.unregisterListener(this);
    }

    public void onBackPressed() {
        mScreensNavigator.toSelectDemoMenu();
    }

    @Override
    public void onMkVisitUsLinkClicked() {
        mActionHelper.openMobileKnowledgeUrl();
    }

    @Override
    public void onMkTwitterIconClicked() {
        mActionHelper.openMobileKnowledgeTwitterUrl();
    }

    @Override
    public void onMkLinkedinIconClicked() {
        mActionHelper.openMobileKnowledgeLinkedinUrl();
    }

    @Override
    public void onNxpTrimensionLinkClicked() {
        mActionHelper.openNxpTrimensionUrl();
    }

    @Override
    public void onMkContactEmailClicked() {
        mActionHelper.sendEmailMkContact();
    }

    @Override
    public void onMkUwbConnectAppClicked() {
        mActionHelper.openMkUwbConnectAppStore();
    }
}
