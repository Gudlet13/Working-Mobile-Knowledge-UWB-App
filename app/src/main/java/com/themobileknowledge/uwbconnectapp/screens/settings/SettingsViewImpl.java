package com.themobileknowledge.uwbconnectapp.screens.settings;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;


public class SettingsViewImpl extends BaseObservableView<SettingsView.Listener> implements SettingsView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SwitchCompat logsEnabledSwitch;
    private ConstraintLayout uwbRoleLayout;
    private ConstraintLayout uwbConfigTypeLayout;
    private ConstraintLayout uwbChannelLayout;
    private ConstraintLayout uwbPreambleIndexLayout;
    private View uwbChannelSeparator;
    private View uwbPreambleIndexSeparator;

    public SettingsViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_settings, parent, false));

        logsEnabledSwitch = findViewById(R.id.settings_logsenabled_switch);
        uwbChannelLayout = findViewById(R.id.settings_item_uwb_channel);
        uwbPreambleIndexLayout = findViewById(R.id.settings_item_uwb_preamble_index);
        uwbRoleLayout = findViewById(R.id.settings_item_uwb_role);
        uwbConfigTypeLayout = findViewById(R.id.settings_item_uwb_config_type);
        uwbChannelSeparator = findViewById(R.id.settings_separator_item_uwb_channel);
        uwbPreambleIndexSeparator = findViewById(R.id.settings_separator_item_uwb_preamble_index);

        logsEnabledSwitch.setOnCheckedChangeListener(this);
        uwbChannelLayout.setOnClickListener(this);
        uwbPreambleIndexLayout.setOnClickListener(this);
        uwbRoleLayout.setOnClickListener(this);
        uwbConfigTypeLayout.setOnClickListener(this);
    }

    @Override
    public void bindMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_actionbar_settings, menu);
    }

    @Override
    public void setLogsEnabled(boolean isChecked) {
        logsEnabledSwitch.setChecked(isChecked);
    }

    @Override
    public void setUwbChannel(int channel) {
        ((TextView) uwbChannelLayout.findViewById(R.id.settings_item_title)).setText(getString(R.string.settings_uwbgroup_uwbchannel_option));
        ((TextView) uwbChannelLayout.findViewById(R.id.settings_item_value)).setText(getString(R.string.settings_uwbgroup_uwbchannel_value, channel));
    }

    @Override
    public void setUwbPreambleIndex(int preambleIndex) {
        ((TextView) uwbPreambleIndexLayout.findViewById(R.id.settings_item_title)).setText(getString(R.string.settings_uwbgroup_uwbpreambleindex_option));
        ((TextView) uwbPreambleIndexLayout.findViewById(R.id.settings_item_value)).setText(getString(R.string.settings_uwbgroup_uwbpreambleindex_value, preambleIndex));
    }

    @Override
    public void setUwbRole(String role) {
        ((TextView) uwbRoleLayout.findViewById(R.id.settings_item_title)).setText(getString(R.string.settings_uwbgroup_uwbrole_option));
        ((TextView) uwbRoleLayout.findViewById(R.id.settings_item_value)).setText(getString(R.string.settings_uwbgroup_uwbrole_value, role));
    }

    @Override
    public void setUwbConfigType(int configType) {
        ((TextView) uwbConfigTypeLayout.findViewById(R.id.settings_item_title)).setText(getString(R.string.settings_uwbgroup_uwbconfigtype_option));
        ((TextView) uwbConfigTypeLayout.findViewById(R.id.settings_item_value)).setText(getString(R.string.settings_uwbgroup_uwbconfigtype_option, configType));
    }

    @Override
    public void showUwbChannel(boolean isVisible) {
        uwbChannelLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        uwbChannelSeparator.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showUwbPreambleIndex(boolean isVisible) {
        uwbPreambleIndexLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        uwbPreambleIndexSeparator.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void notifyOnBackPressed() {
        for (Listener listener : getListeners()) {
            listener.onBackPressed();
        }
    }

    private void notifyResetSettingsToDefaultClicked() {
        for (Listener listener : getListeners()) {
            listener.onResetSettingsToDefaultClicked();
        }
    }

    private void notifySettingsChannelClicked() {
        for (Listener listener : getListeners()) {
            listener.onSettingsChannelClicked();
        }
    }

    private void notifySettingsPreambleIndexClicked() {
        for (Listener listener : getListeners()) {
            listener.onSettingsPreambleIndexClicked();
        }
    }

    private void notifyUwbRoleItemSelected() {
        for (Listener listener : getListeners()) {
            listener.onUwbRoleItemSelected();
        }
    }

    private void notifyUwbConfigTypeItemSelected() {
        for (Listener listener : getListeners()) {
            listener.onUwbConfigTypeItemSelected();
        }
    }

    private void notifyLogsEnabledChanged(boolean isChecked) {
        for (Listener listener : getListeners()) {
            listener.onLogsEnabledChanged(isChecked);
        }
    }

    @Override
    public void onMenuItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                notifyOnBackPressed();
                break;
            case R.id.menu_settings_reset:
                notifyResetSettingsToDefaultClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_item_uwb_channel:
                notifySettingsChannelClicked();
                break;

            case R.id.settings_item_uwb_preamble_index:
                notifySettingsPreambleIndexClicked();
                break;

            case R.id.settings_item_uwb_role:
                notifyUwbRoleItemSelected();
                break;

            case R.id.settings_item_uwb_config_type:
                notifyUwbConfigTypeItemSelected();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.settings_logsenabled_switch:
                notifyLogsEnabledChanged(isChecked);
                break;

            default:
                break;
        }
    }
}
