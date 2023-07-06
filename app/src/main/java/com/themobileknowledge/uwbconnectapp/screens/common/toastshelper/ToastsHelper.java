package com.themobileknowledge.uwbconnectapp.screens.common.toastshelper;

import android.content.Context;
import android.widget.Toast;

public class ToastsHelper {

    private Context mContext;

    public ToastsHelper(Context mContext) {
        this.mContext = mContext;
    }

    // Add methods to show Toast or Snackbar messages
    public void notifyGenericMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
