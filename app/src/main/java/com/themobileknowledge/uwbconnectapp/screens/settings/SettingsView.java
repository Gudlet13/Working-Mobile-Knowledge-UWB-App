package com.themobileknowledge.uwbconnectapp.screens.settings;


import android.view.Menu;
import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface SettingsView extends IBaseObservableView<SettingsView.Listener> {

    interface Listener {
        void onBackPressed();

        void onResetSettingsToDefaultClicked();

        void onLogsEnabledChanged(boolean isChecked);

        void onSettingsChannelClicked();

        void onSettingsPreambleIndexClicked();

        void onUwbRoleItemSelected();

        void onUwbConfigTypeItemSelected();
    }

    void bindMenu(Menu menu);

    void onMenuItemSelected(MenuItem item);

    void setLogsEnabled(boolean isChecked);

    void setUwbChannel(int channel);

    void setUwbPreambleIndex(int preambleIndex);

    void setUwbRole(String role);

    void setUwbConfigType(int configType);

    void showUwbChannel(boolean isVisible);

    void showUwbPreambleIndex(boolean isVisible);
}
