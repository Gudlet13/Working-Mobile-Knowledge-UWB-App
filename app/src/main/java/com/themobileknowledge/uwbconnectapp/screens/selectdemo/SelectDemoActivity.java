package com.themobileknowledge.uwbconnectapp.screens.selectdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.themobileknowledge.uwbconnectapp.screens.common.BaseActivity;

public class SelectDemoActivity extends BaseActivity {

    private SelectDemoController mSelectDemoController;

    public static void start(Context context) {
        Intent intent = new Intent(context, SelectDemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectDemoView view = getCompositionRoot().getBaseViewFactory().getMainScreenView(null);
        mSelectDemoController = getCompositionRoot().getSelectDemoController();
        mSelectDemoController.bindView(view);
        setContentView(view.getRootView());

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSelectDemoController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSelectDemoController.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mSelectDemoController.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mSelectDemoController.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}