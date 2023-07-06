package com.themobileknowledge.uwbconnectapp.screens.uwbranging;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.uwb.RangingCapabilities;
import androidx.core.uwb.RangingResult;

import com.themobileknowledge.uwbconnectapp.bluetooth.BluetoothLEManagerHelper;
import com.themobileknowledge.uwbconnectapp.location.LocationManagerHelper;
import com.themobileknowledge.uwbconnectapp.logger.LoggerHelper;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.oob.OoBTlvHelper;
import com.themobileknowledge.uwbconnectapp.oob.model.UwbDeviceConfigData;
import com.themobileknowledge.uwbconnectapp.oob.model.UwbPhoneConfigData;
import com.themobileknowledge.uwbconnectapp.screens.common.actionhelper.ActionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.editaccessorynamedialog.EditAccessoryAliasDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodonotshowagaindialog.InfoDoNotShowAgainDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog.PromptDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialogItem;
import com.themobileknowledge.uwbconnectapp.screens.common.permissions.PermissionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.storage.database.DatabaseStorageHelper;
import com.themobileknowledge.uwbconnectapp.storage.preferences.PreferenceStorageHelper;
import com.themobileknowledge.uwbconnectapp.utils.Utils;
import com.themobileknowledge.uwbconnectapp.uwb.UwbManagerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UwbRangingController implements UwbRangingViewImpl.Listener, DialogsEventBus.Listener, BluetoothLEManagerHelper.Listener, UwbManagerHelper.Listener {

    private static final String TAG = UwbRangingController.class.getSimpleName();

    private static final String LOG_DEMONAME = "UwbRanging";

    private static final String DIALOGTAG_SELECTUWBACCESSORIES = "DIALOGTAG_SELECTUWBACCESSORIES";
    private static final String DIALOGTAG_PAIRINGINFO = "DIALOGTAG_PAIRINGINFO";
    private static final String DIALOGTAG_CONFIRMCLOSEDEMO = "DIALOGTAG_CONFIRMCLOSEDEMO";
    private static final String DIALOGTAG_REQUIREDPERMISSIONSMISSING = "DIALOGTAG_REQUIREDPERMISSIONSMISSING";
    private static final String DIALOGTAG_BLUETOOTHNOTSUPPORTED = "DIALOGTAG_BLUETOOTHNOTSUPPORTED";
    private static final String DIALOGTAG_UWBNOTSUPPORTED = "DIALOGTAG_UWBNOTSUPPORTED";
    private static final String DIALOGTAG_LOCATIONNOTSUPPORTED = "DIALOGTAG_LOCATIONNOTSUPPORTED";
    private static final String DIALOGTAG_BLUETOOTHNOTENABLED = "DIALOGTAG_BLUETOOTHNOTENABLED";
    private static final String DIALOGTAG_UWBNOTENABLED = "DIALOGTAG_UWBNOTENABLED";
    private static final String DIALOGTAG_LOCATIONNOTENABLED = "DIALOGTAG_LOCATIONNOTENABLED";
    private static final String DIALOGTAG_CONNECTIONLOST = "DIALOGTAG_CONNECTIONLOST";
    private static final String DIALOGTAG_EDITACCESSORYNAME = "DIALOGTAG_EDITACCESSORYNAME";

    private static final String SAVED_STATE_SCREEN_STATE = "SAVED_STATE_SCREEN_STATE";
    private Bundle mSavedInstanceState = null;

    private static final int MAX_ALLOWED_SELECT_ACCESSORIES = 1;

    private static final int BLE_EXPIRED_ACCESSORIES_REMOVE_REFRESH_TIMEOUT_MSECS = 1000;
    private static final int BLE_EXPIRED_ACCESSORIES_REMOVE_TIMEOUT_MSECS = 5000;
    private static final int BLE_CONNECT_TIMEOUT_MSECS = 5000;
    private static final int LEGACY_OoB_SUPPORT_TIMEOUT_MSECS = 2000;

    private final ScreensNavigator mScreensNavigator;
    private final PermissionHelper mPermissionHelper;
    private final PreferenceStorageHelper mPreferenceStorageHelper;
    private final DatabaseStorageHelper mDatabaseStorageHelper;
    private final ActionHelper mActionHelper;
    private final LoggerHelper mLoggerHelper;
    private final ToastsHelper mToastsHelper;
    private final BluetoothLEManagerHelper mBluetoothLEManagerHelper;
    private final LocationManagerHelper mLocationManagerHelper;
    private final UwbManagerHelper mUwbManagerHelper;
    private final DialogsManager mDialogsManager;
    private final DialogsEventBus mDialogsEventBus;
    private UwbRangingView mView;

    private List<SelectAccessoriesDialogItem> mSelectAccessoryList = new ArrayList<>();
    private List<Accessory> mAccessoriesList = new ArrayList<>();

    private HashMap<String, Timer> mTimerAccessoriesConnectList = new HashMap<>();
    private HashMap<String, Timer> mTimerAccessoriesLegacyOoBSupportList = new HashMap<>();
    private Timer timerBleExpiredAccessories;

    private enum ScreenState {
        SCREEN_SHOWN,
        CONFIRMCLOSEDEMO_DIALOG_SHOWN,
        PAIRINGINFO_DIALOG_SHOWN,
        REQUIREDPERMISSIONSMISSING_DIALOG_SHOWN,
        BLUETOOTHNOTSUPPORTED_DIALOG_SHOWN,
        UWBNOTSUPPORTED_DIALOG_SHOWN,
        LOCATIONNOTSUPPORTED_DIALOG_SHOWN,
        BLUETOOTHNOTENABLED_DIALOG_SHOWN,
        UWBNOTENABLED_DIALOG_SHOWN,
        LOCATIONNOTENABLED_DIALOG_SHOWN,
        SELECTUWBACCESSORIES_DIALOG_SHOWN,
        CONNECTIONLOST_DIALOG_SHOWN,
        EDITACCESSORYNAME_DIALOG_SHOWN,
    }

    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    public UwbRangingController(ScreensNavigator screensNavigator,
                                PermissionHelper permissionHelper,
                                PreferenceStorageHelper preferenceStorageHelper,
                                DatabaseStorageHelper databaseStorageHelper,
                                ActionHelper actionHelper,
                                LoggerHelper loggerHelper,
                                ToastsHelper toastshelper,
                                BluetoothLEManagerHelper bluetoothLEManagerHelper,
                                LocationManagerHelper locationManagerHelper,
                                UwbManagerHelper uwbManagerHelper,
                                DialogsManager dialogsManager,
                                DialogsEventBus dialogsEventBus) {
        this.mScreensNavigator = screensNavigator;
        this.mPermissionHelper = permissionHelper;
        this.mPreferenceStorageHelper = preferenceStorageHelper;
        this.mDatabaseStorageHelper = databaseStorageHelper;
        this.mActionHelper = actionHelper;
        this.mLoggerHelper = loggerHelper;
        this.mToastsHelper = toastshelper;
        this.mBluetoothLEManagerHelper = bluetoothLEManagerHelper;
        this.mLocationManagerHelper = locationManagerHelper;
        this.mUwbManagerHelper = uwbManagerHelper;
        this.mDialogsManager = dialogsManager;
        this.mDialogsEventBus = dialogsEventBus;
    }

    public void bindView(UwbRangingView view) {
        mView = view;
    }

    public void setInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public Bundle saveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATE_SCREEN_STATE, mScreenState);
        return outState;
    }

    public void onCreate() {
        mLoggerHelper.setDemoName(LOG_DEMONAME);
        if (mSavedInstanceState != null) {
            mScreenState = (ScreenState) mSavedInstanceState.getSerializable(SAVED_STATE_SCREEN_STATE);
        }

        applySettings();

        mBluetoothLEManagerHelper.registerListener(this);
        mUwbManagerHelper.registerListener(this);
    }

    public void onStart() {
        mView.registerListener(this);
        mDialogsEventBus.registerListener(this);
        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_START);
    }

    public void onStop() {
        mView.unregisterListener(this);
        mDialogsEventBus.unregisterListener(this);
        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_STOP);
    }

    public void onBackPressed() {
        if (mAccessoriesList.size() > 0) {
            mScreenState = ScreenState.CONFIRMCLOSEDEMO_DIALOG_SHOWN;
            mDialogsManager.showConfirmCloseDemoDialog(DIALOGTAG_CONFIRMCLOSEDEMO);
        } else {
            mScreensNavigator.toSelectDemoMenu();
        }
    }

    public void onDestroy() {
        mBluetoothLEManagerHelper.unregisterListener();
        mUwbManagerHelper.unregisterListener();

        bleStopDeviceScan();
        bleClose();
        uwbClose();
        cancelTimerBleExpiredAccessories();
        cancelTimerBleConnect();
        cancelTimerAccessoriesLegacyOoBSupport();

        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_FINISHED);
    }

    @Override
    public void onSelectAccessoryButtonClicked() {
        bleClose();
        uwbClose();
        cancelTimerBleExpiredAccessories();
        cancelTimerBleConnect();
        cancelTimerAccessoriesLegacyOoBSupport();

        // Show pairing info dialog as long as the user is interested in it
        if (mPreferenceStorageHelper.getShowPairingInfo()) {
            showPairingInfoDialog();
        } else {
            showSelectAccessoriesDialog();
        }
    }

    @Override
    public void onSelectedAccessorySelectAccessoryClicked() {
        showSelectAccessoriesDialog();
    }

    @Override
    public void onSelectedAccessoryRemoveClicked() {
        Accessory accessory = getAccessoryFromIndex(0);
        if (accessory != null) {
            bleClose(accessory);
            uwbClose(accessory);
            cancelTimerBleConnect(accessory);
            cancelTimerAccessoriesLegacyOoBSupport(accessory);

            // Remove accessory from list
            mAccessoriesList.remove(accessory);
        }

        // Let's restart the demo
        showSelectAccessoryText();
    }

    @Override
    public void onBluetoothLEStateChanged(int state) {
        // No need to handle all other states, only state on to state off transaction
        if (state == BluetoothAdapter.STATE_OFF) {
            // Close all sessions
            bleClose();
            uwbClose();
            cancelTimerBleExpiredAccessories();
            cancelTimerBleConnect();
            cancelTimerAccessoriesLegacyOoBSupport();

            // Clear list with ongoing connections
            mAccessoriesList.clear();

            // Let's restart the demo
            showSelectAccessoryText();
            cancelSelectAccessoriesDialog();

            mScreenState = ScreenState.SCREEN_SHOWN;
        }
    }

    @Override
    public void onBluetoothLEDeviceBonded(String name, String address) {
        Accessory accessory = mDatabaseStorageHelper.getAliasedAccessory(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        // Ignore if already connected
        for (Accessory connectedAccessory : mAccessoriesList) {
            if (connectedAccessory.getMac().equals(accessory.getMac())) {
                return;
            }
        }

        bleClose();
        uwbClose();

        // Clear list with ongoing connections
        mAccessoriesList.clear();

        // Show selected accessory info to user
        bleConnectToDevice(accessory);
        showSelectedAccessory(accessory);
    }

    @Override
    public void onBluetoothLEDeviceScanned(String name, String address) {
        Accessory accessory = mDatabaseStorageHelper.getAliasedAccessory(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        // If Accessory Name or Accesory Mac address is null or empty, ignore the notification
        if (accessory.getMac() == null || accessory.getMac().isEmpty()
                || accessory.getName() == null || accessory.getName().isEmpty()) {
            return;
        }

        // When a device is already connected and user wants to switch to another device, ignore it
        for (Accessory connectedAccessory : mAccessoriesList) {
            if (connectedAccessory.getMac().equals(accessory.getMac())) {
                return;
            }
        }

        // Suitable for more complex routines like don't show devices already tracked
        for (SelectAccessoriesDialogItem selectAccessory : mSelectAccessoryList) {
            if (selectAccessory.getMac().equals(accessory.getMac())) {
                selectAccessory.setTimeStamp(System.currentTimeMillis());
                return;
            }
        }

        // Log event
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_SCANNED, accessory);

        boolean isBondedDevice = mBluetoothLEManagerHelper.isBondedDevice(accessory.getMac());
        SelectAccessoriesDialogItem selectAccessoriesDialogItem = new SelectAccessoriesDialogItem(accessory.getName(), accessory.getMac(), accessory.getAlias(), System.currentTimeMillis(), isBondedDevice, false);
        mSelectAccessoryList.add(selectAccessoriesDialogItem);
        updateSelectAccessoriesDialog();
    }

    @Override
    public void onBluetoothLEDeviceConnected(String name, String address) {
        Accessory accessory = mDatabaseStorageHelper.getAliasedAccessory(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        mAccessoriesList.add(accessory);

        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_CONNECTED, accessory);
        showSelectedAccessory(accessory);
        cancelTimerBleConnect(accessory);

        // Let's proceed with the UWB session configuration
        transmitStartUwbRangingConfiguration(accessory);
    }

    @Override
    public void onBluetoothLEDeviceDisconnected(String address) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(address);
        if (accessory == null) {
            Log.e(TAG, "Unexpected Bluetooth LE address");
            return;
        }

        // Close sessions
        bleClose(accessory);
        uwbClose(accessory);
        cancelTimerBleConnect(accessory);
        cancelTimerAccessoriesLegacyOoBSupport(accessory);

        // Remove from list
        mAccessoriesList.remove(accessory);

        // Display dialog to inform about lost connection
        mScreenState = ScreenState.CONNECTIONLOST_DIALOG_SHOWN;
        mDialogsManager.showConnectionLostDialog(DIALOGTAG_CONNECTIONLOST);
    }

    @Override
    public void onBluetoothLEDataReceived(String address, byte[] data) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(address);
        if (accessory == null) {
            Log.e(TAG, "Unexpected Bluetooth LE address");
            return;
        }

        byte messageId = data[0];
        if (messageId == OoBTlvHelper.MessageId.uwbDeviceConfigurationData.getValue()) {
            // UWB Accessories running legacy firmware will not respond to new OoB protocol initialize message
            // uwbDeviceConfigurationData message indicates that UWB Ranging session configuration is about to start
            cancelTimerAccessoriesLegacyOoBSupport(accessory);

            byte[] deviceConfigData = OoBTlvHelper.getTagValue(data, OoBTlvHelper.MessageId.uwbDeviceConfigurationData.getValue());
            startRanging(accessory, deviceConfigData);
        } else if (messageId == OoBTlvHelper.MessageId.uwbDidStart.getValue()) {
            uwbRangingSessionStarted(accessory);
        } else if (messageId == OoBTlvHelper.MessageId.uwbDidStop.getValue()) {
            uwbRangingSessionStopped(accessory);
        } else {
            throw new IllegalArgumentException("Unexpected value");
        }
    }

    @Override
    public void onRangingStarted(String address, UwbPhoneConfigData uwbPhoneConfigData) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(address);
        if (accessory == null) {
            Log.e(TAG, "Unexpected Bluetooth LE address");
            return;
        }

        transmitUwbPhoneConfigData(accessory, uwbPhoneConfigData);
    }

    @Override
    public void onRangingResult(String address, RangingResult rangingResult) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(address);
        if (accessory == null) {
            Log.e(TAG, "Unexpected Bluetooth LE address");
            return;
        }

        if (rangingResult instanceof RangingResult.RangingResultPosition) {
            RangingResult.RangingResultPosition rangingResultPosition = (RangingResult.RangingResultPosition) rangingResult;
            if (rangingResultPosition.getPosition().getDistance() != null
                    && rangingResultPosition.getPosition().getAzimuth() != null) {

                float distance = rangingResultPosition.getPosition().getDistance().getValue();
                float azimuth = rangingResultPosition.getPosition().getAzimuth().getValue();

                // Elevation might be null in some Android phones
                if (rangingResultPosition.getPosition().getElevation() != null) {
                    float elevation = rangingResultPosition.getPosition().getElevation().getValue();
                    log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_RESULT,
                            accessory,
                            String.valueOf((int) (distance * 100)),
                            String.valueOf((int) azimuth),
                            String.valueOf((int) elevation));
                } else {
                    log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_RESULT,
                            accessory,
                            String.valueOf((int) (distance * 100)),
                            String.valueOf((int) azimuth),
                            "");
                }

                Position position = new Position(distance, azimuth);
                updateSelectedAccessoryPosition(accessory, position);
            }
        } else if (rangingResult instanceof RangingResult.RangingResultPeerDisconnected) {
            bleClose(accessory);
            uwbClose(accessory);
            cancelTimerBleConnect(accessory);
            cancelTimerAccessoriesLegacyOoBSupport(accessory);

            // Remove the accessory from the list
            mAccessoriesList.remove(accessory);

            // Display dialog to inform about lost connection
            mScreenState = ScreenState.CONNECTIONLOST_DIALOG_SHOWN;
            mDialogsManager.showConnectionLostDialog(DIALOGTAG_CONNECTIONLOST);
        }
    }

    @Override
    public void onRangingError(Throwable error) {
        // Close sessions
        bleClose();
        uwbClose();
        cancelTimerBleExpiredAccessories();
        cancelTimerBleConnect();
        cancelTimerAccessoriesLegacyOoBSupport();

        // Clear list with ongoing connections
        mAccessoriesList.clear();

        log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_ERROR);
        new Handler(Looper.getMainLooper()).post(() -> displayRangingError(error));
    }

    @Override
    public void onRangingComplete() {
        // Nothing to do
    }

    @Override
    public void onRangingCapabilities(RangingCapabilities rangingCapabilities) {
        // Nothing to do
    }

    private boolean checkPermissions() {
        return mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_SCAN)
                && mPermissionHelper.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
                && mPermissionHelper.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && mPermissionHelper.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && mPermissionHelper.hasPermission(Manifest.permission.UWB_RANGING);
    }

    private void applySettings() {
        mLoggerHelper.setLogsEnabled(mPreferenceStorageHelper.getLogsEnabled());
        mUwbManagerHelper.setUwbChannel(mPreferenceStorageHelper.getUwbChannel());
        mUwbManagerHelper.setUwbPreambleIndex(mPreferenceStorageHelper.getUwbPreambleIndex());
        mUwbManagerHelper.setPreferredUwbRole(mPreferenceStorageHelper.getUwbRole());
        mUwbManagerHelper.setPreferredUwbProfileId(mPreferenceStorageHelper.getUwbConfigType());
    }

    private boolean bleClose() {
        for (Accessory accessory : mAccessoriesList) {
            mBluetoothLEManagerHelper.close(accessory.getMac());
            log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_DISCONNECTED, accessory);
        }

        return true;
    }

    private boolean uwbClose() {
        for (Accessory accessory : mAccessoriesList) {
            mUwbManagerHelper.close(accessory.getMac());
            log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_PEER_DISCONNECTED, accessory);
        }

        return true;
    }

    private boolean bleClose(Accessory accessory) {
        mBluetoothLEManagerHelper.close(accessory.getMac());
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_DISCONNECTED, accessory);
        return true;
    }

    private boolean uwbClose(Accessory accessory) {
        mUwbManagerHelper.close(accessory.getMac());
        log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_PEER_DISCONNECTED, accessory);
        return true;
    }

    private boolean bleStartDeviceScan() {

        if (!checkPermissions()) {
            mScreenState = ScreenState.REQUIREDPERMISSIONSMISSING_DIALOG_SHOWN;
            mDialogsManager.showRequiredPermissionsMissingDialog(DIALOGTAG_REQUIREDPERMISSIONSMISSING);
            return false;
        }

        if (!mBluetoothLEManagerHelper.isSupported()) {
            mScreenState = ScreenState.BLUETOOTHNOTSUPPORTED_DIALOG_SHOWN;
            mDialogsManager.showBluetoothNotSupportedDialog(DIALOGTAG_BLUETOOTHNOTSUPPORTED);
            return false;
        }

        if (!mUwbManagerHelper.isSupported()) {
            mScreenState = ScreenState.UWBNOTSUPPORTED_DIALOG_SHOWN;
            mDialogsManager.showUwbNotSupportedDialog(DIALOGTAG_UWBNOTSUPPORTED);
            return false;
        }

        if (!mLocationManagerHelper.isSupported()) {
            mScreenState = ScreenState.LOCATIONNOTSUPPORTED_DIALOG_SHOWN;
            mDialogsManager.showLocationNotSupportedDialog(DIALOGTAG_LOCATIONNOTSUPPORTED);
            return false;
        }

        if (!mBluetoothLEManagerHelper.isEnabled()) {
            mScreenState = ScreenState.BLUETOOTHNOTENABLED_DIALOG_SHOWN;
            mDialogsManager.showBluetoothNotEnabledDialog(DIALOGTAG_BLUETOOTHNOTENABLED);
            return false;
        }

        if (!mUwbManagerHelper.isEnabled()) {
            mScreenState = ScreenState.UWBNOTENABLED_DIALOG_SHOWN;
            mDialogsManager.showUwbNotEnabledDialog(DIALOGTAG_UWBNOTENABLED);
            return false;
        }

        if (!mLocationManagerHelper.isEnabled()) {
            mScreenState = ScreenState.LOCATIONNOTENABLED_DIALOG_SHOWN;
            mDialogsManager.showLocationNotEnabledDialog(DIALOGTAG_LOCATIONNOTENABLED);
            return false;
        }

        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_SCAN_START);
        return mBluetoothLEManagerHelper.startLeDeviceScan();
    }

    private boolean bleStopDeviceScan() {
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_SCAN_STOP);
        return mBluetoothLEManagerHelper.stopLeDeviceScan();
    }

    private void bleConnectToDevice(Accessory accessory) {
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_CONNECTING, accessory);
        mBluetoothLEManagerHelper.connect(accessory.getMac());

        clearAccessoryPosition();
        startTimerBleConnect(accessory);
    }

    private boolean transmitStartUwbRangingConfiguration(Accessory accessory) {
        byte[] startUwbRangingConfigurationTlv = OoBTlvHelper.buildTlv(
                OoBTlvHelper.MessageId.initialize.getValue());

        // UWB Accessories running legacy firmware will not respond to new OoB protocol initialize message
        // This timer is used for legacy UWB Accessories support
        startTimerAccessoriesLegacyOoBSupport(accessory);

        return mBluetoothLEManagerHelper.transmit(accessory.getMac(), startUwbRangingConfigurationTlv);
    }

    private boolean transmitLegacyStartUwbRangingConfiguration(Accessory accessory) {
        byte[] legacyStartUwbRangingConfigurationTlv = OoBTlvHelper.buildTlv(
                OoBTlvHelper.MessageIdLegacy.initialize.getValue(),
                Utils.byteToByteArray(OoBTlvHelper.DevTypeLegacy.android.getValue()));

        return mBluetoothLEManagerHelper.transmit(accessory.getMac(), legacyStartUwbRangingConfigurationTlv);
    }

    private boolean transmitUwbPhoneConfigData(Accessory accessory, UwbPhoneConfigData uwbPhoneConfigData) {
        byte[] transmitUwbPhoneConfigDataTlv = OoBTlvHelper.buildTlv(
                OoBTlvHelper.MessageId.uwbPhoneConfigurationData.getValue(),
                uwbPhoneConfigData.toByteArray());

        return mBluetoothLEManagerHelper.transmit(accessory.getMac(), transmitUwbPhoneConfigDataTlv);
    }

    private boolean transmitUwbRangingStop(Accessory accessory) {
        byte[] transmitUwbRangingStopTlv = OoBTlvHelper.buildTlv(
                OoBTlvHelper.MessageId.stop.getValue());

        return mBluetoothLEManagerHelper.transmit(accessory.getMac(), transmitUwbRangingStopTlv);
    }

    private boolean startRanging(Accessory accessory, byte[] deviceConfigData) {
        Log.d(TAG, "Start ranging with accessory: " + accessory.getMac());
        final UwbDeviceConfigData uwbDeviceConfigData = UwbDeviceConfigData.fromByteArray(deviceConfigData);
        return mUwbManagerHelper.startRanging(accessory.getMac(), uwbDeviceConfigData);
    }

    private boolean stopRanging(Accessory accessory) {
        Log.d(TAG, "Stop ranging with accessory: " + accessory.getMac());
        return mUwbManagerHelper.stopRanging(accessory.getMac());
    }

    private void uwbRangingSessionStarted(Accessory accessory) {
        Log.d(TAG, "Ranging started with accessory: " + accessory.getMac());
        log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_START);
    }

    private void uwbRangingSessionStopped(Accessory accessory) {
        Log.d(TAG, "Ranging stopped with accessory: " + accessory.getMac());
        log(LoggerHelper.LogEvent.LOG_EVENT_UWB_RANGING_STOP);
    }

    private void cancelTimerBleExpiredAccessories() {
        if (timerBleExpiredAccessories != null) {
            timerBleExpiredAccessories.purge();
            timerBleExpiredAccessories.cancel();
        }
    }

    private void startTimerBleExpiredAccessories() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                removeBleExpiredAccessories();
            }
        };

        timerBleExpiredAccessories = new Timer();
        timerBleExpiredAccessories.schedule(tt, BLE_EXPIRED_ACCESSORIES_REMOVE_REFRESH_TIMEOUT_MSECS, BLE_EXPIRED_ACCESSORIES_REMOVE_REFRESH_TIMEOUT_MSECS);
    }

    private void cancelTimerBleConnect() {
        for (Timer timerAccessoryConnect : mTimerAccessoriesConnectList.values()) {
            timerAccessoryConnect.purge();
            timerAccessoryConnect.cancel();
        }

        mTimerAccessoriesConnectList.clear();
    }

    private void cancelTimerBleConnect(Accessory accessory) {
        Timer timerAccessoryConnect = mTimerAccessoriesConnectList.get(accessory.getMac());
        if (timerAccessoryConnect != null) {
            timerAccessoryConnect.purge();
            timerAccessoryConnect.cancel();
        }

        mTimerAccessoriesConnectList.remove(accessory.getMac());
    }

    private void startTimerBleConnect(final Accessory accessory) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "BluetoothLE Connect timeout fired!");
                bleConnectTimeout(accessory.getMac());
            }
        };

        Timer timerAccessoryConnect = new Timer();
        timerAccessoryConnect.schedule(tt, BLE_CONNECT_TIMEOUT_MSECS);
        mTimerAccessoriesConnectList.put(accessory.getMac(), timerAccessoryConnect);
    }

    private void cancelTimerAccessoriesLegacyOoBSupport() {
        for (Timer timerAccessoryLegacyOoBSupport : mTimerAccessoriesLegacyOoBSupportList.values()) {
            timerAccessoryLegacyOoBSupport.purge();
            timerAccessoryLegacyOoBSupport.cancel();
        }

        mTimerAccessoriesConnectList.clear();
    }

    private void cancelTimerAccessoriesLegacyOoBSupport(Accessory accessory) {
        Timer timerAccessoryLegacyOoBSupport = mTimerAccessoriesLegacyOoBSupportList.get(accessory.getMac());
        if (timerAccessoryLegacyOoBSupport != null) {
            timerAccessoryLegacyOoBSupport.purge();
            timerAccessoryLegacyOoBSupport.cancel();
        }

        mTimerAccessoriesLegacyOoBSupportList.remove(accessory.getMac());
    }

    private void startTimerAccessoriesLegacyOoBSupport(final Accessory accessory) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Legacy OoB support timeout fired!");
                legacyOoBSupportTimeout(accessory.getMac());
            }
        };

        Timer timerAccessoryLegacyOoBSupport = new Timer();
        timerAccessoryLegacyOoBSupport.schedule(tt, LEGACY_OoB_SUPPORT_TIMEOUT_MSECS);
        mTimerAccessoriesLegacyOoBSupportList.put(accessory.getMac(), timerAccessoryLegacyOoBSupport);
    }

    private void bleConnectTimeout(String remoteAddress) {
        // Need to remove the timeout that is linked to this remoteAddress key
        Accessory tempAccessory = new Accessory(null, remoteAddress, null);
        cancelTimerBleConnect(tempAccessory);

        showSelectAccessoryText();
    }

    private void legacyOoBSupportTimeout(String remoteAddress) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(remoteAddress);
        if (accessory != null) {
            transmitLegacyStartUwbRangingConfiguration(accessory);
            cancelTimerAccessoriesLegacyOoBSupport(accessory);
        }
    }

    private void removeBleExpiredAccessories() {
        List<SelectAccessoriesDialogItem> expiredAccessories = new ArrayList<>();
        for (SelectAccessoriesDialogItem item : mSelectAccessoryList) {
            if ((item.getTimeStamp() + BLE_EXPIRED_ACCESSORIES_REMOVE_TIMEOUT_MSECS) < System.currentTimeMillis()) {
                expiredAccessories.add(item);
            }
        }

        if (!expiredAccessories.isEmpty()) {
            mSelectAccessoryList.removeAll(expiredAccessories);
            updateSelectAccessoriesDialog();
        }
    }

    private void displayRangingError(Throwable error) {
        error.printStackTrace();

        // Display dialog to inform about lost connection
        mScreenState = ScreenState.CONNECTIONLOST_DIALOG_SHOWN;
        mDialogsManager.showConnectionLostDialog(DIALOGTAG_CONNECTIONLOST);
    }

    private void showPairingInfoDialog() {
        mScreenState = ScreenState.PAIRINGINFO_DIALOG_SHOWN;
        mDialogsManager.showPairingInfoDialog(DIALOGTAG_PAIRINGINFO);
    }

    private void showEditAccessoryAliasDialog(Accessory accessory) {
        mScreenState = ScreenState.EDITACCESSORYNAME_DIALOG_SHOWN;
        mDialogsManager.showEditAccessoryAliasDialog(DIALOGTAG_EDITACCESSORYNAME, accessory);
    }

    private void showSelectAccessoryText() {
        new Handler(Looper.getMainLooper()).post(() -> mView.showSelectAccessoryText());
    }

    private void clearAccessoryPosition() {
        new Handler(Looper.getMainLooper()).post(() -> mView.clearAccessoryPosition());
    }

    private void updateSelectedAccessoryPosition(Accessory accessory, Position position) {
        new Handler(Looper.getMainLooper()).post(() -> mView.updateSelectedAccessoryPosition(
                accessory,
                position));
    }

    private void showSelectedAccessory(Accessory accessory) {
        new Handler(Looper.getMainLooper()).post(() -> mView.showSelectedAccessory(accessory));
    }

    private void showSelectAccessoriesDialog() {
        mSelectAccessoryList.clear();

        if (bleStartDeviceScan()) {
            mScreenState = ScreenState.SELECTUWBACCESSORIES_DIALOG_SHOWN;
            mDialogsManager.showSelectAccessoriesDialog(DIALOGTAG_SELECTUWBACCESSORIES, mSelectAccessoryList, MAX_ALLOWED_SELECT_ACCESSORIES);

            // Start timer to handle expired accessories
            startTimerBleExpiredAccessories();
        }
    }

    private void updateSelectAccessoriesDialog() {
        new Handler(Looper.getMainLooper()).post(() -> mDialogsManager.updateSelectAccessoriesDialog(DIALOGTAG_SELECTUWBACCESSORIES));
    }

    private void cancelSelectAccessoriesDialog() {
        new Handler(Looper.getMainLooper()).post(() -> mDialogsManager.cancelSelectAccessoriesDialog(DIALOGTAG_SELECTUWBACCESSORIES));
    }

    private Accessory getAccessoryFromBluetoothLeAddress(String address) {
        for (Accessory accessory : mAccessoriesList) {
            if (accessory.getMac().equals(address)) {
                return accessory;
            }
        }

        return null;
    }

    private Accessory getAccessoryFromIndex(int index) {
        if (mAccessoriesList == null || mAccessoriesList.size() == 0) {
            return null;
        }

        if (mAccessoriesList.size() < index) {
            return null;
        }

        return mAccessoriesList.get(index);
    }

    private void log(LoggerHelper.LogEvent event) {
        mLoggerHelper.log(event.toString());
    }

    private void log(LoggerHelper.LogEvent event, Accessory accessory) {
        if (accessory.getAlias() == null || accessory.getAlias().isEmpty()) {
            mLoggerHelper.log(event.toString(), accessory.getName(), accessory.getMac());
        } else {
            mLoggerHelper.log(event.toString(), accessory.getAlias(), accessory.getMac());
        }
    }

    private void log(LoggerHelper.LogEvent event, Accessory accessory, String distance, String azimuth, String elevation) {
        if (accessory.getAlias() == null || accessory.getAlias().isEmpty()) {
            mLoggerHelper.log(event.toString(), accessory.getName(), accessory.getMac(), distance, azimuth, elevation);
        } else {
            mLoggerHelper.log(event.toString(), accessory.getAlias(), accessory.getMac(), distance, azimuth, elevation);
        }
    }

    @Override
    public void onDialogEvent(Object event) {
        if (event instanceof SelectAccessoriesDialogEvent) {
            bleStopDeviceScan();
            cancelTimerBleExpiredAccessories();
            cancelTimerBleConnect();
            cancelTimerAccessoriesLegacyOoBSupport();

            switch (((SelectAccessoriesDialogEvent) event).getClickedButton()) {
                case EDIT:
                    int selectedAccessoryEditPosition = ((SelectAccessoriesDialogEvent) event).getSelectedEditAccessoryPosition();
                    if (selectedAccessoryEditPosition >= 0) {
                        Accessory accessory = mSelectAccessoryList.get(selectedAccessoryEditPosition);
                        showEditAccessoryAliasDialog(accessory);
                    }

                    break;

                case SELECT:
                    List<Integer> selectedAccessoriesPositions = ((SelectAccessoriesDialogEvent) event).getSelectedAccessoriesPositions();
                    if (selectedAccessoriesPositions.size() > 0) {
                        bleClose();
                        uwbClose();

                        // Clear list with ongoing connections
                        mAccessoriesList.clear();

                        int selectedAccessoryPosition = selectedAccessoriesPositions.get(0);
                        Accessory accessory = mSelectAccessoryList.get(selectedAccessoryPosition);
                        bleConnectToDevice(accessory);

                        // Show selected accessory info to user
                        showSelectedAccessory(accessory);
                    }

                    break;
                case CANCEL:
                    break;
            }

            mSelectAccessoryList.clear();
            mScreenState = ScreenState.SCREEN_SHOWN;
        } else if (event instanceof EditAccessoryAliasDialogEvent) {
            switch (((EditAccessoryAliasDialogEvent) event).getClickedButton()) {
                case EDIT:
                    String accessoryAlias = ((EditAccessoryAliasDialogEvent) event).getAccessoryAlias();
                    Accessory accessory = ((EditAccessoryAliasDialogEvent) event).getAccessory();

                    if (accessoryAlias == null || accessoryAlias.isEmpty()) {
                        mToastsHelper.notifyGenericMessage("Invalid alias!");
                    } else {
                        if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                            mDatabaseStorageHelper.updateAccessoryAlias(accessory, accessoryAlias);
                        } else {
                            accessory.setAlias(accessoryAlias);
                            mDatabaseStorageHelper.insertAccessory(accessory);
                        }

                        // Update selected tag info and refresh accessory with alias
                        if (mAccessoriesList.size() > 0) {
                            Accessory connectedAccessory = mAccessoriesList.get(0);
                            if (connectedAccessory.getMac().equals(accessory.getMac())) {
                                accessory.setAlias(accessoryAlias);
                                showSelectedAccessory(accessory);
                                mAccessoriesList.set(0, accessory);
                            }
                        }
                    }

                    break;

                case CANCEL:
                    break;
            }

            // Show select tag dialog again
            showSelectAccessoriesDialog();
        } else if (event instanceof PromptDialogEvent) {
            switch (mScreenState) {
                case BLUETOOTHNOTSUPPORTED_DIALOG_SHOWN:
                case UWBNOTSUPPORTED_DIALOG_SHOWN:
                case LOCATIONNOTSUPPORTED_DIALOG_SHOWN:
                    mScreenState = ScreenState.SCREEN_SHOWN;
                    break;

                case BLUETOOTHNOTENABLED_DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            mActionHelper.enableBluetooth();
                            break;

                        default:
                            mScreenState = ScreenState.SCREEN_SHOWN;
                            break;
                    }

                    break;

                case LOCATIONNOTENABLED_DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            mActionHelper.enableLocation();
                            break;

                        default:
                            mScreenState = ScreenState.SCREEN_SHOWN;
                            break;
                    }

                    break;

                case UWBNOTENABLED_DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            mActionHelper.enableUwb();
                            break;

                        default:
                            mScreenState = ScreenState.SCREEN_SHOWN;
                            break;
                    }

                    break;

                case CONFIRMCLOSEDEMO_DIALOG_SHOWN:
                    switch (((PromptDialogEvent) event).getClickedButton()) {
                        case POSITIVE:
                            mScreensNavigator.toSelectDemoMenu();
                            break;

                        default:
                            mScreenState = ScreenState.SCREEN_SHOWN;
                            break;
                    }

                    break;

                case CONNECTIONLOST_DIALOG_SHOWN:
                    mScreenState = ScreenState.SCREEN_SHOWN;
                    showSelectAccessoryText();

                    break;
            }
        } else if (event instanceof InfoDoNotShowAgainDialogEvent) {
            switch (((InfoDoNotShowAgainDialogEvent) event).getClickedButton()) {
                case ACCEPT:
                    boolean showInfo = ((InfoDoNotShowAgainDialogEvent) event).getShowInfo();
                    mPreferenceStorageHelper.setShowPairingInfo(showInfo);

                    break;
                case CANCEL:
                    break;
            }

            showSelectAccessoriesDialog();
        }
    }
}
