package com.themobileknowledge.uwbconnectapp.screens.common.packagemanager;

import android.content.Context;
import android.content.pm.PackageManager;

public class PackageManagerHelper {

    private Context mContext;

    public PackageManagerHelper(Context mContext) {
        this.mContext = mContext;
    }

    public String getAppVersion() {
        PackageManager packageManager = mContext.getPackageManager();
        String packageName = mContext.getPackageName();

        try {
            return packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
