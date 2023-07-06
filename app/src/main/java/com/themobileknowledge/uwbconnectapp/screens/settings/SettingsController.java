package com.themobileknowledge.uwbconnectapp.screens.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog.PromptDialogEvent;
import com.themobileknowledge.uwbconnectapp.storage.preferences.PreferenceStorageHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.screens.settings.dialogs.selectsettingsdialog.SelectSettingsDialogEvent;

import java.util.ArrayList;
import java.util.List;

public class SettingsController implements SettingsViewImpl.Listener, DialogsEventBus.Listener {

    private static final String DIALOGTAG_RESETSETTINGSTODEFAULT = "DIALOGTAG_RESETSETTINGSTODEFAULT";
    private static final String DIALOGTAG_SELECTUWBCHANNEL = "DIALOGTAG_SELECTUWBCHANNEL";
    private static final String DIALOGTAG_SELECTUWBPREAMBLEINDEX = "DIALOGTAG_SELECTUWBPREAMBLEINDEX";
    private static final String DIALOGTAG_SELECTUWBROLE = "DIALOGTAG_SELECTUWBROLE";
    private static final String DIALOGTAG_SELECTUWBCONFIGTYPE = "DIALOGTAG_SELECTUWBCONFIGTYPE";

    private static final String SAVED_STATE_SCREEN_STATE = "SAVED_STATE_SCREEN_STATE";

    // UWB Settings adapters
    private List<String> uwbChannelListItems = new ArrayList<>();
    private List<String> uwbPreambleIndexListItems = new ArrayList<>();
    private List<String> uwbRoleListItems = new ArrayList<>();
    private List<String> uwbConfigTypeListItems = new ArrayList<>();

    private final ScreensNavigator mScreensNavigator;
    private final ToastsHelper mToastsHelper;
    private final PreferenceStorageHelper mPreferenceStorageHelper;
    private final DialogsManager mDialogsManager;
    private final DialogsEventBus mDialogsEventBus;

    private SettingsView mView;

    private Menu mMenu;

    private Bundle mSavedInstanceState = null;
    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    private enum ScreenState {
        SCREEN_SHOWN,
        RESETSETTINGSTODEFAULT_DIALOG_SHOWN,
        SELECTUWBCHANNEL_DIALOG_SHOWN,
        SELECTUWBPREAMBLEINDEX_DIALOG_SHOWN,
        SELECTUWBROLE_DIALOG_SHOWN,
        SELECTUWBCONFIGTYPE_DIALOG_SHOWN,
    }

    public SettingsController(ScreensNavigator screensNavigator,
                              ToastsHelper toastsHelper,
                              PreferenceStorageHelper preferenceStorageHelper,
                              DialogsManager dialogsManager,
                              DialogsEventBus dialogsEventBus) {
        mScreensNavigator = screensNavigator;
        mToastsHelper = toastsHelper;
        mPreferenceStorageHelper = preferenceStorageHelper;
        mDialogsManager = dialogsManager;
        mDialogsEventBus = dialogsEventBus;
    }

    public void bindView(SettingsView view) {
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
        mDialogsEventBus.registerListener(this);
        initializeSettings();
        showSettings();
    }

    public void onStop() {
        mView.unregisterListener(this);
        mDialogsEventBus.unregisterListener(this);
    }

    public void onBackPressed() {
        mScreensNavigator.toSelectDemoMenu();
    }

    public void onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        mView.bindMenu(mMenu);
    }

    public void onOptionsItemSelected(MenuItem item) {
        mView.onMenuItemSelected(item);
    }

    @Override
    public void onResetSettingsToDefaultClicked() {
        mScreenState = ScreenState.RESETSETTINGSTODEFAULT_DIALOG_SHOWN;
        mDialogsManager.showResetSettingsToDefaultDialog(
                DIALOGTAG_RESETSETTINGSTODEFAULT);
    }

    @Override
    public void onLogsEnabledChanged(boolean isChecked) {
        mPreferenceStorageHelper.setLogsEnabled(isChecked);
    }

    @Override
    public void onSettingsChannelClicked() {
        mScreenState = ScreenState.SELECTUWBCHANNEL_DIALOG_SHOWN;
        mDialogsManager.showSelectUwbChannelDialog(
                DIALOGTAG_SELECTUWBCHANNEL, uwbChannelListItems, uwbChannelListItems.indexOf(String.valueOf(mPreferenceStorageHelper.getUwbChannel())));
    }

    @Override
    public void onSettingsPreambleIndexClicked() {
        mScreenState = ScreenState.SELECTUWBPREAMBLEINDEX_DIALOG_SHOWN;
        mDialogsManager.showSelectUwbPreambleIndexDialog(
                DIALOGTAG_SELECTUWBPREAMBLEINDEX, uwbPreambleIndexListItems, uwbPreambleIndexListItems.indexOf(String.valueOf(mPreferenceStorageHelper.getUwbPreambleIndex())));
    }

    @Override
    public void onUwbRoleItemSelected() {
        mScreenState = ScreenState.SELECTUWBROLE_DIALOG_SHOWN;
        mDialogsManager.showSelectUwbRoleDialog(
                DIALOGTAG_SELECTUWBROLE, uwbRoleListItems, uwbRoleListItems.indexOf(mPreferenceStorageHelper.getUwbRole()));
    }

    @Override
    public void onUwbConfigTypeItemSelected() {
        mScreenState = ScreenState.SELECTUWBCONFIGTYPE_DIALOG_SHOWN;
        mDialogsManager.showSelectUwbConfigTypeDialog(
                DIALOGTAG_SELECTUWBCONFIGTYPE, uwbConfigTypeListItems, uwbConfigTypeListItems.indexOf(String.valueOf(mPreferenceStorageHelper.getUwbConfigType())));
    }

    private void initializeSettings() {
        initializeUwbChannelListItems();
        initializeUwbPreambleIndexListItems();
        initializeUwbRoleListItems();
        initializeUwbConfigTypeListItems();
    }

    private void showSettings() {
        mView.setLogsEnabled(mPreferenceStorageHelper.getLogsEnabled());
        mView.setUwbChannel(mPreferenceStorageHelper.getUwbChannel());
        mView.setUwbPreambleIndex(mPreferenceStorageHelper.getUwbPreambleIndex());
        mView.setUwbRole(mPreferenceStorageHelper.getUwbRole());
        mView.setUwbConfigType(mPreferenceStorageHelper.getUwbConfigType());

        // For Controller mode it is the OS who assigns the UWB Complex Channel
        if (mPreferenceStorageHelper.getUwbRole().equals("Controller")){
            mView.showUwbChannel(false);
            mView.showUwbPreambleIndex(false);
        } else {
            mView.showUwbChannel(true);
            mView.showUwbPreambleIndex(true);
        }
    }

    private void initializeUwbChannelListItems() {
        uwbChannelListItems.add("5");
        uwbChannelListItems.add("9");
    }

    private void initializeUwbPreambleIndexListItems() {
        uwbPreambleIndexListItems.add("9");
        uwbPreambleIndexListItems.add("10");
        uwbPreambleIndexListItems.add("11");
        uwbPreambleIndexListItems.add("12");
    }

    private void initializeUwbRoleListItems() {
        // Controller role is supported by the app but it is not available to users because there seems to be a bug
        // in the MK UWB SR150 Anchor firmware that affects the UWB Session closure mechanism
        // uwbRoleListItems.add("Controller");
        uwbRoleListItems.add("Controlee");
    }

    private void initializeUwbConfigTypeListItems() {
        uwbConfigTypeListItems.add("1");
    }

    @Override
    public void onDialogEvent(Object event) {
        if (event instanceof SelectSettingsDialogEvent) {

            switch (mScreenState) {
                case SELECTUWBCHANNEL_DIALOG_SHOWN:
                    switch (((SelectSettingsDialogEvent) event).getClickedButton()) {
                        case SELECT:
                            int selectedPosition = ((SelectSettingsDialogEvent) event).getSelectedPosition();
                            int uwbChannel = Integer.parseInt(uwbChannelListItems.get(selectedPosition));
                            mPreferenceStorageHelper.setUwbChannel(uwbChannel);
                            showSettings();

                            break;
                        case CANCEL:
                            break;
                    }
                    break;

                case SELECTUWBPREAMBLEINDEX_DIALOG_SHOWN:
                    switch (((SelectSettingsDialogEvent) event).getClickedButton()) {
                        case SELECT:
                            int selectedPosition = ((SelectSettingsDialogEvent) event).getSelectedPosition();
                            int uwbPreambleIndex = Integer.parseInt(uwbPreambleIndexListItems.get(selectedPosition));
                            mPreferenceStorageHelper.setUwbPreambleIndex(uwbPreambleIndex);
                            showSettings();

                            break;
                        case CANCEL:
                            break;
                    }
                    break;


                case SELECTUWBROLE_DIALOG_SHOWN:
                    switch (((SelectSettingsDialogEvent) event).getClickedButton()) {
                        case SELECT:
                            int selectedPosition = ((SelectSettingsDialogEvent) event).getSelectedPosition();
                            String uwbRole = uwbRoleListItems.get(selectedPosition);
                            mPreferenceStorageHelper.setUwbRole(uwbRole);
                            showSettings();

                            break;
                        case CANCEL:
                            break;
                    }
                    break;

                case SELECTUWBCONFIGTYPE_DIALOG_SHOWN:
                    switch (((SelectSettingsDialogEvent) event).getClickedButton()) {
                        case SELECT:
                            int selectedPosition = ((SelectSettingsDialogEvent) event).getSelectedPosition();
                            int uwbConfigType = Integer.parseInt(uwbConfigTypeListItems.get(selectedPosition));
                            mPreferenceStorageHelper.setUwbConfigType(uwbConfigType);
                            showSettings();

                            break;
                        case CANCEL:
                            break;
                    }
                    break;
            }

            mScreenState = ScreenState.SCREEN_SHOWN;
        } else if (event instanceof PromptDialogEvent) {
            switch (mScreenState) {
                case RESETSETTINGSTODEFAULT_DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            mPreferenceStorageHelper.clearUwbSettings();
                            showSettings();
                            break;

                        default:
                            break;
                    }

                    mScreenState = ScreenState.SCREEN_SHOWN;
                    break;
            }
        }
    }
}
