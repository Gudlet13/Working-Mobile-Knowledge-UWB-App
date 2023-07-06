package com.themobileknowledge.uwbconnectapp.screens.common.actionhelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.themobileknowledge.uwbconnectapp.R;

public class ActionHelper {

    private final Context mContext;

    public ActionHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void openMobileKnowledgeUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.mk_website)));
        mContext.startActivity(intent);
    }

    public void openMobileKnowledgeLinkedinUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.mk_linkedin_website)));
        mContext.startActivity(intent);
    }

    public void openMobileKnowledgeTwitterUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.mk_twitter_website)));
        mContext.startActivity(intent);
    }

    public void openNxpTrimensionUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.nxp_trimension_website)));
        mContext.startActivity(intent);
    }

    public void sendEmailMkContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mContext.getResources().getString(R.string.mk_contact_email)));
        mContext.startActivity(intent);
    }

    public void openMkUwbConnectAppStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.mk_appstore)));
        mContext.startActivity(intent);
    }

    public void enableBluetooth() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        mContext.startActivity(intent);
    }

    public void enableLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
    }

    public void enableUwb() {
        // No specific action defined by Google for UWB Settings
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        mContext.startActivity(intent);
    }
}
