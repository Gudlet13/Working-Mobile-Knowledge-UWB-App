package com.themobileknowledge.uwbconnectapp.screens.common.dialogs;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog.PromptDialog;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.dialogs.editdistancealertthresholdsdialog.EditDistanceAlertThresholdsDialog;
import com.themobileknowledge.uwbconnectapp.screens.logs.dialogs.exportlogsdialog.ExportLogsDialog;
import com.themobileknowledge.uwbconnectapp.screens.settings.dialogs.selectsettingsdialog.SelectSettingsDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.editaccessorynamedialog.EditAccessoryAliasDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodonotshowagaindialog.InfoDoNotShowAgainDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialogItem;

import java.util.List;

public class DialogsManager {

    private Context mContext;
    private FragmentManager mFragmentManager;

    public DialogsManager(Context mContext, FragmentManager mFragmentManager) {
        this.mContext = mContext;
        this.mFragmentManager = mFragmentManager;
    }

    public void showSelectAccessoriesDialog(@Nullable String tag, List<SelectAccessoriesDialogItem> itemsList, int maxAllowedAccessories) {
        SelectAccessoriesDialog selectAccessoriesDialog = SelectAccessoriesDialog.newSelectAccessoriesDialog(
                getString(R.string.selectaccessoriesdialog_title),
                getString(R.string.selectaccessoriesdialog_message),
                getString(R.string.selectaccessoriesdialog_positive_button_text),
                getString(R.string.selectaccessoriesdialog_negative_button_text)
        );

        selectAccessoriesDialog.show(mFragmentManager, tag);
        selectAccessoriesDialog.setMaxAllowedAccessories(maxAllowedAccessories);
        selectAccessoriesDialog.bindAccessoriesList(itemsList);
    }

    public void cancelSelectAccessoriesDialog(String tag) {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if ((fragment.getTag().equals(tag)) && (fragment instanceof SelectAccessoriesDialog)) {
                ((SelectAccessoriesDialog) fragment).dismiss();
            }
        }
    }

    public void updateSelectAccessoriesDialog(String tag) {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if ((fragment.getTag().equals(tag)) && (fragment instanceof SelectAccessoriesDialog)) {
                ((SelectAccessoriesDialog) fragment).refresh();
            }
        }
    }

    public void showPairingInfoDialog(@Nullable String tag) {
        InfoDoNotShowAgainDialog infoDoNotShowAgainDialog = InfoDoNotShowAgainDialog.newInfoDoNotShowAgainDialog(
                getString(R.string.pairinginfodialog_title),
                getString(R.string.pairinginfodialog_message),
                getString(R.string.pairinginfodialog_positive_button_text)
        );

        infoDoNotShowAgainDialog.show(mFragmentManager, tag);
    }

    public void showSelectUwbChannelDialog(@Nullable String tag, List<String> optionsList, int uwbChannel) {
        SelectSettingsDialog selectSettingsDialog = SelectSettingsDialog.newSelectSettingsDialog(
                getString(R.string.selectsettingsdialog_uwbchannel_title),
                getString(R.string.selectsettingsdialog_uwbchannel_message),
                getString(R.string.selectsettingsdialog_uwbchannel_positive_button_text),
                getString(R.string.selectsettingsdialog_uwbchannel_negative_button_text)
        );

        selectSettingsDialog.bindOptionsList(optionsList);
        selectSettingsDialog.setSelectedOption(uwbChannel);
        selectSettingsDialog.show(mFragmentManager, tag);
    }

    public void showSelectUwbPreambleIndexDialog(@Nullable String tag, List<String> optionsList, int position) {
        SelectSettingsDialog selectSettingsDialog = SelectSettingsDialog.newSelectSettingsDialog(
                getString(R.string.selectsettingsdialog_uwbpreambleindex_title),
                getString(R.string.selectsettingsdialog_uwbpreambleindex_message),
                getString(R.string.selectsettingsdialog_uwbpreambleindex_positive_button_text),
                getString(R.string.selectsettingsdialog_uwbpreambleindex_negative_button_text)
        );

        selectSettingsDialog.bindOptionsList(optionsList);
        selectSettingsDialog.setSelectedOption(position);
        selectSettingsDialog.show(mFragmentManager, tag);
    }

    public void showSelectUwbRoleDialog(@Nullable String tag, List<String> optionsList, int position) {
        SelectSettingsDialog selectSettingsDialog = SelectSettingsDialog.newSelectSettingsDialog(
                getString(R.string.selectsettingsdialog_uwbrole_title),
                getString(R.string.selectsettingsdialog_uwbrole_message),
                getString(R.string.selectsettingsdialog_uwbrole_positive_button_text),
                getString(R.string.selectsettingsdialog_uwbrole_negative_button_text)
        );

        selectSettingsDialog.bindOptionsList(optionsList);
        selectSettingsDialog.setSelectedOption(position);
        selectSettingsDialog.show(mFragmentManager, tag);
    }

    public void showSelectUwbConfigTypeDialog(@Nullable String tag, List<String> optionsList, int position) {
        SelectSettingsDialog selectSettingsDialog = SelectSettingsDialog.newSelectSettingsDialog(
                getString(R.string.selectsettingsdialog_uwbconfigtype_title),
                getString(R.string.selectsettingsdialog_uwbconfigtype_message),
                getString(R.string.selectsettingsdialog_uwbconfigtype_positive_button_text),
                getString(R.string.selectsettingsdialog_uwbconfigtype_negative_button_text)
        );

        selectSettingsDialog.bindOptionsList(optionsList);
        selectSettingsDialog.setSelectedOption(position);
        selectSettingsDialog.show(mFragmentManager, tag);
    }

    public void showEditAccessoryAliasDialog(@Nullable String tag, Accessory accessory) {
        EditAccessoryAliasDialog editAccessoryAliasDialog = EditAccessoryAliasDialog.newEditAccessoryAliasDialog(
                getString(R.string.editaccessorynamedialog_title),
                getString(R.string.editaccessorynamedialog_message),
                getString(R.string.editaccessorynamedialog_positive_button_text),
                getString(R.string.editaccessorynamedialog_negative_button_text)
        );

        editAccessoryAliasDialog.bindAccessory(accessory);
        editAccessoryAliasDialog.show(mFragmentManager, tag);
    }

    public void showResetSettingsToDefaultDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.settingsresettodefaultdialog_title),
                getString(R.string.settingsresettodefaultdialog_message),
                getString(R.string.settingsresettodefaultdialog_positive_button_text),
                getString(R.string.settingsresettodefaultdialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showLogsClearDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.logscleardialog_title),
                getString(R.string.logscleardialog_message),
                getString(R.string.logscleardialog_positive_button_text),
                getString(R.string.logscleardialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showLogsExportDialog(@Nullable String tag) {
        ExportLogsDialog exportLogsDialog = ExportLogsDialog.newExportLogsDialog(
                getString(R.string.exportlogsdialog_title),
                getString(R.string.exportlogsdialog_message),
                getString(R.string.exportlogsdialog_positive_button_text),
                getString(R.string.exportlogsdialog_negative_button_text)
        );

        exportLogsDialog.show(mFragmentManager, tag);
    }

    public void showRequiredPermissionsMissingDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.requiredpermissionsmissingdialog_title),
                getString(R.string.requiredpermissionsmissingdialog_message),
                getString(R.string.requiredpermissionsmissingdialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showPermissionsDeclinedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.permissionsdeclineddialog_title),
                getString(R.string.permissionsdeclineddialog_message),
                getString(R.string.permissionsdeclineddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showPermissionsDeclinedDontAskAgainDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.permissionsdeclineddonttaskagaindialog_title),
                getString(R.string.permissionsdeclineddonttaskagaindialog_message),
                getString(R.string.permissionsdeclineddonttaskagaindialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showConfirmCloseDemoDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.confirmclosedemodialog_title),
                getString(R.string.confirmclosedemodialog_message),
                getString(R.string.confirmclosedemodialog_positive_button_text),
                getString(R.string.confirmclosedemodialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showBluetoothNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.bluetoothnotsupporteddialog_title),
                getString(R.string.bluetoothnotsupporteddialog_message),
                getString(R.string.bluetoothnotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showUwbNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.uwbnotsupporteddialog_title),
                getString(R.string.uwbnotsupporteddialog_message),
                getString(R.string.uwbnotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showLocationNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.locationnotsupporteddialog_title),
                getString(R.string.locationnotsupporteddialog_message),
                getString(R.string.locationnotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showBluetoothNotEnabledDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.bluetoothnotenableddialog_title),
                getString(R.string.bluetoothnotenableddialog_message),
                getString(R.string.bluetoothnotenableddialog_positive_button_text),
                getString(R.string.bluetoothnotenableddialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showUwbNotEnabledDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.uwbnotenableddialog_title),
                getString(R.string.uwbnotenableddialog_message),
                getString(R.string.uwbnotenableddialog_positive_button_text),
                getString(R.string.uwbnotenableddialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showLocationNotEnabledDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.locationnotenableddialog_title),
                getString(R.string.locationnotenableddialog_message),
                getString(R.string.locationnotenableddialog_positive_button_text),
                getString(R.string.locationnotenableddialog_negative_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showConnectionLostDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.connectionlostdialog_title),
                getString(R.string.connectionlostdialog_message),
                getString(R.string.connectionlostdialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showEditDistanceAlertThresholdsDialog(@Nullable String tag, int closeRangeThreshold, int farRangeThreshold) {
        EditDistanceAlertThresholdsDialog editDistanceAlertThresholdsDialog = EditDistanceAlertThresholdsDialog.newEditDistanceAlertThresholdsDialog(
                getString(R.string.editdistancealertthresholdsdialog_title),
                getString(R.string.editdistancealertthresholdsdialog_message),
                getString(R.string.editdistancealertthresholdsdialog_positive_button_text),
                getString(R.string.editdistancealertthresholdsdialog_negative_button_text)
        );

        editDistanceAlertThresholdsDialog.bindThresholds(closeRangeThreshold, farRangeThreshold);
        editDistanceAlertThresholdsDialog.show(mFragmentManager, tag);
    }

    public void showCameraPermissionDeclinedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.camerapermissiondeclineddialog_title),
                getString(R.string.camerapermissiondeclineddialog_message),
                getString(R.string.camerapermissiondeclineddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showCameraPermissionDeclinedDontAskAgainDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.camerapermissiondeclineddonttaskagaindialog_title),
                getString(R.string.camerapermissiondeclineddonttaskagaindialog_message),
                getString(R.string.camerapermissiondeclineddonttaskagaindialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showARCoreNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.arcorenotsupporteddialog_title),
                getString(R.string.arcorenotsupporteddialog_message),
                getString(R.string.arcorenotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showDistanceNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.distancenotsupporteddialog_title),
                getString(R.string.distancenotsupporteddialog_message),
                getString(R.string.distancenotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showDirectionNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.directionnotsupporteddialog_title),
                getString(R.string.directionnotsupporteddialog_message),
                getString(R.string.directionnotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showAzimuthNotSupportedDialog(@Nullable String tag) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(
                getString(R.string.azimuthnotsupporteddialog_title),
                getString(R.string.azimuthnotsupporteddialog_message),
                getString(R.string.azimuthnotsupporteddialog_positive_button_text)
        );

        promptDialog.show(mFragmentManager, tag);
    }

    public void showElevationNotSupportedDialog(@Nullable String tag) {
        InfoDoNotShowAgainDialog infoDoNotShowAgainDialog = InfoDoNotShowAgainDialog.newInfoDoNotShowAgainDialog(
                getString(R.string.elevationnotsupporteddialog_title),
                getString(R.string.elevationnotsupporteddialog_message),
                getString(R.string.elevationnotsupporteddialog_positive_button_text)
        );

        infoDoNotShowAgainDialog.show(mFragmentManager, tag);
    }

    public void showMultipleSessionsNotSupportedDialog(@Nullable String tag) {
        InfoDoNotShowAgainDialog infoDoNotShowAgainDialog = InfoDoNotShowAgainDialog.newInfoDoNotShowAgainDialog(
                getString(R.string.multiplesessionsnotsupporteddialog_title),
                getString(R.string.multiplesessionsnotsupporteddialog_message),
                getString(R.string.multiplesessionsnotsupporteddialog_positive_button_text)
        );

        infoDoNotShowAgainDialog.show(mFragmentManager, tag);
    }

    private String getString(int stringId) {
        return mContext.getString(stringId);
    }

    private String getString(int stringId, Object... param) {
        return mContext.getString(stringId, param);
    }
}
