package com.themobileknowledge.uwbconnectapp.screens.distancealert;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.uwb.RangingCapabilities;
import androidx.core.uwb.RangingResult;

import com.themobileknowledge.uwbconnectapp.bluetooth.BluetoothLEManagerHelper;
import com.themobileknowledge.uwbconnectapp.location.LocationManagerHelper;
import com.themobileknowledge.uwbconnectapp.logger.LoggerHelper;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.oob.OoBTlvHelper;
import com.themobileknowledge.uwbconnectapp.oob.model.UwbDeviceConfigData;
import com.themobileknowledge.uwbconnectapp.oob.model.UwbPhoneConfigData;
import com.themobileknowledge.uwbconnectapp.screens.common.actionhelper.ActionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsManager;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.editaccessorynamedialog.EditAccessoryAliasDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodonotshowagaindialog.InfoDoNotShowAgainDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog.PromptDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.common.permissions.PermissionHelper;
import com.themobileknowledge.uwbconnectapp.screens.common.screensnavigator.ScreensNavigator;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.DistanceAlertRecyclerItem;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.dialogs.editdistancealertthresholdsdialog.EditDistanceAlertThresholdsDialogEvent;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem.DistanceAlertItemViewImpl;
import com.themobileknowledge.uwbconnectapp.storage.database.DatabaseStorageHelper;
import com.themobileknowledge.uwbconnectapp.storage.preferences.PreferenceStorageHelper;
import com.themobileknowledge.uwbconnectapp.utils.Utils;
import com.themobileknowledge.uwbconnectapp.uwb.UwbManagerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DistanceAlertController implements DistanceAlertViewImpl.Listener, DistanceAlertItemViewImpl.Listener, DialogsEventBus.Listener, BluetoothLEManagerHelper.Listener, UwbManagerHelper.Listener, LocationManagerHelper.Listener {

    private static final String TAG = DistanceAlertController.class.getSimpleName();

    private static final String LOG_DEMONAME = "DistanceAlert";

    private static final String DIALOGTAG_CONFIRMCLOSEDEMO = "DIALOGTAG_CONFIRMCLOSEDEMO";
    private static final String DIALOGTAG_PAIRINGINFO = "DIALOGTAG_PAIRINGINFO";
    private static final String DIALOGTAG_REQUIREDPERMISSIONSMISSING = "DIALOGTAG_REQUIREDPERMISSIONSMISSING";
    private static final String DIALOGTAG_BLUETOOTHNOTSUPPORTED = "DIALOGTAG_BLUETOOTHNOTSUPPORTED";
    private static final String DIALOGTAG_UWBNOTSUPPORTED = "DIALOGTAG_UWBNOTSUPPORTED";
    private static final String DIALOGTAG_LOCATIONNOTSUPPORTED = "DIALOGTAG_LOCATIONNOTSUPPORTED";
    private static final String DIALOGTAG_BLUETOOTHNOTENABLED = "DIALOGTAG_BLUETOOTHNOTENABLED";
    private static final String DIALOGTAG_UWBNOTENABLED = "DIALOGTAG_UWBNOTENABLED";
    private static final String DIALOGTAG_LOCATIONNOTENABLED = "DIALOGTAG_LOCATIONNOTENABLED";
    private static final String DIALOGTAG_EDITACCESSORYNAME = "DIALOGTAG_EDITACCESSORYNAME";
    private static final String DIALOGTAG_EDITTHRESHOLDS = "DIALOGTAG_EDITTHRESHOLDS";
    private static final String DIALOGTAG_MULTIPLESESSIONSNOTSUPPORTED = "DIALOGTAG_MULTIPLESESSIONSNOTSUPPORTED";

    private static final String SAVED_STATE_SCREEN_STATE = "SAVED_STATE_SCREEN_STATE";
    private Bundle mSavedInstanceState = null;

    // Due to limitations on current uwb-rxjava3:1.0.0-alpha04 library, only one session is supported
    private static int MAX_ALLOWED_ACCESSORIES = 5;

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

    private DistanceAlertView mView;

    private Menu mMenu;

    private List<DistanceAlertRecyclerItem> mDistanceAlertItemList = new ArrayList<>();
    private List<Accessory> mAccessoriesList = new ArrayList<>();
    private List<Accessory> mAccessoriesConnectingList = new ArrayList<>();

    private HashMap<String, Timer> mTimerAccessoriesConnectList = new HashMap<>();
    private HashMap<String, Timer> mTimerAccessoriesLegacyOoBSupportList = new HashMap<>();

    private int mLimitCloseRangeThreshold;
    private int mLimitFarRangeThreshold;

    private enum ScreenState {
        SCREEN_SHOWN,
        CONFIRMCLOSEDEMO_DIALOG_SHOWN,
        PAIRINGINFO_DIALOG_SHOWN,
        DISTANCEALERTDEMO_RUNNING,
        REQUIREDPERMISSIONSMISSING_DIALOG_SHOWN,
        BLUETOOTHNOTSUPPORTED_DIALOG_SHOWN,
        UWBNOTSUPPORTED_DIALOG_SHOWN,
        LOCATIONNOTSUPPORTED_DIALOG_SHOWN,
        BLUETOOTHNOTENABLED_DIALOG_SHOWN,
        UWBNOTENABLED_DIALOG_SHOWN,
        LOCATIONNOTENABLED_DIALOG_SHOWN,
        EDITACCESSORYNAME_DIALOG_SHOWN,
        EDITTHRESHOLDS_DIALOG_SHOWN,
        MULTIPLESESSIONSNOTSUPPORTED_DIALOG_SHOWN,
    }

    private ScreenState mScreenState = ScreenState.SCREEN_SHOWN;

    public DistanceAlertController(ScreensNavigator screensNavigator,
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

    public void bindView(DistanceAlertView view) {
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

        mLimitCloseRangeThreshold = mPreferenceStorageHelper.getDistanceAlertCloseRangeThreshold();
        mLimitFarRangeThreshold = mPreferenceStorageHelper.getDistanceAlertFarRangeThreshold();
        mView.bindDistanceAlertItemList(mDistanceAlertItemList);
        initializeRecyclerItemList();

        mBluetoothLEManagerHelper.registerListener(this);
        mLocationManagerHelper.registerListener(this);
        mUwbManagerHelper.registerListener(this);
    }

    public void onStart() {
        if (mPreferenceStorageHelper.getShowMultipleSessionsNotSupported()) {
            // Inform the user about current limitation on multiple ranging sessions
            showMultipleSessionsNotSupportedDialog();
        } else {
            if (mPreferenceStorageHelper.getShowPairingInfo()) {
                // Show pairing info dialog as long as the user is interested in it
                showPairingInfoDialog();
            } else {
                // Proceed to start demo
                startDistanceAlertDemo();
            }
        }

        mView.registerListener(this);
        mDialogsEventBus.registerListener(this);
        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_START);
    }

    public void onStop() {
        mView.unregisterListener(this);
        mDialogsEventBus.unregisterListener(this);
        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_STOP);

        // Stop BLE Scan
        // As per Google's documentation, new UWB ranging sessions cannot start
        // and ranging notifications might not get to the app while app is in the background
        bleStopDeviceScan();
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
        mLocationManagerHelper.unregisterListener();
        mUwbManagerHelper.unregisterListener();

        bleClose();
        uwbClose();
        cancelTimerBleConnect();
        cancelTimerAccessoriesLegacyOoBSupport();

        log(LoggerHelper.LogEvent.LOG_EVENT_DEMO_FINISHED);
    }

    public void onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        mView.bindMenu(mMenu);
    }

    public void onOptionsItemSelected(MenuItem item) {
        mView.onMenuItemSelected(item);
    }

    @Override
    public void onMenuSettingsClicked() {
        showEditDistanceAlertThresholdsDialog();
    }

    @Override
    public void onAccessoryEditClicked(Accessory accessory) {
        showEditAccessoryAliasDialog(accessory);
    }

    @Override
    public void onLocationStateChanged(boolean enabled) {
        if (enabled) {
            startDistanceAlertDemo();
        } else {
            for (Accessory accessory : mAccessoriesList) {
                log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_DISCONNECTED, accessory);
            }

            // Close sessions
            bleClose();
            uwbClose();
            cancelTimerBleConnect();
            cancelTimerAccessoriesLegacyOoBSupport();

            mAccessoriesList.clear();
            mAccessoriesConnectingList.clear();

            // Let's clean the screen
            initializeRecyclerItemList();
            updateDistanceAlertView();

            mScreenState = ScreenState.SCREEN_SHOWN;
        }
    }

    @Override
    public void onBluetoothLEStateChanged(int state) {
        if (state == BluetoothAdapter.STATE_ON) {
            startDistanceAlertDemo();
        } if (state == BluetoothAdapter.STATE_OFF) {
            bleClose();
            uwbClose();
            cancelTimerBleConnect();
            cancelTimerAccessoriesLegacyOoBSupport();

            mAccessoriesList.clear();
            mAccessoriesConnectingList.clear();

            // Let's clean the screen
            initializeRecyclerItemList();
            updateDistanceAlertView();

            mScreenState = ScreenState.SCREEN_SHOWN;
        }
    }

    @Override
    public void onBluetoothLEDeviceBonded(String name, String address) {
        Accessory accessory = mDatabaseStorageHelper.getAliasedAccessory(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        // If we are already connected to this device we should skip it
        for (Accessory connectedAccessory: mAccessoriesList) {
            if (accessory.getMac().equals(connectedAccessory.getMac())) {
                return;
            }
        }

        // If we are already connecting to this device we should skip it
        for (Accessory connectingAccessory: mAccessoriesConnectingList) {
            if (accessory.getMac().equals(connectingAccessory.getMac())) {
                return;
            }
        }

        // Add the accessory to the list of connecting devices, otherwise we will try to connect while connecting
        mAccessoriesConnectingList.add(accessory);

        mBluetoothLEManagerHelper.connect(accessory.getMac());
        startTimerBleConnect(accessory);
    }

    @Override
    public void onBluetoothLEDeviceScanned(String name, String address) {
        Accessory accessory = mDatabaseStorageHelper.getAliasedAccessory(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        // If Accessory Name or Accessory Mac address is null or empty, ignore the notification
        if (accessory.getMac() == null || accessory.getMac().isEmpty()
                || accessory.getName() == null || accessory.getName().isEmpty()) {
            return;
        }

        // If we are already connected to this device we should skip it
        for (Accessory connectedAccessory: mAccessoriesList) {
            if (accessory.getMac().equals(connectedAccessory.getMac())) {
                return;
            }
        }

        // If we are already connecting to this device we should skip it
        for (Accessory connectingAccessory: mAccessoriesConnectingList) {
            if (accessory.getMac().equals(connectingAccessory.getMac())) {
                return;
            }
        }

        // Limit the number of accessories that we can track in parallel
        if (mAccessoriesList.size() + mAccessoriesConnectingList.size() >= MAX_ALLOWED_ACCESSORIES) {
            return;
        }

        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_SCANNED, accessory);
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_CONNECTING, accessory);

        // Add the accessory to the list of connecting devices, otherwise we will try to connect while connecting
        mAccessoriesConnectingList.add(accessory);

        mBluetoothLEManagerHelper.connect(accessory.getMac());
        startTimerBleConnect(accessory);
    }

    @Override
    public void onBluetoothLEDeviceConnected(String name, String address) {
        Accessory accessory = getConnectingAccessoryFromBluetoothLeAddress(address);
        if (accessory == null) {
            accessory = new Accessory(name, address, null);
        }

        // Connected to the accessory
        log(LoggerHelper.LogEvent.LOG_EVENT_BLE_DEV_CONNECTED, accessory);
        mAccessoriesList.add(accessory);

        // Remove from connecting list so that we can reconnect later if needed
        mAccessoriesConnectingList.remove(accessory);

        // Cancel the timer now that we are connected to the remote device
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

        mAccessoriesList.remove(accessory);

        // Remove the notification entry
        for (DistanceAlertRecyclerItem item : mDistanceAlertItemList) {
            if (!item.isThresholdLine()) {
                if (item.getNotification().getAccessory().getMac().equals(accessory.getMac())) {
                    mDistanceAlertItemList.remove(item);
                    break;
                }
            }
        }

        // Let's update the screen
        updateDistanceAlertView();

        // Inform the user about connection lost
        showConnectionLostToast(accessory);
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
            Log.e(TAG, "Unexpected messageId received!");
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
                float distance =rangingResultPosition.getPosition().getDistance().getValue();
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

                byte threshold;
                int distanceCms = (int) (distance * 100);

                if (distanceCms <= mLimitCloseRangeThreshold) {
                    threshold = 2;
                } else if (distanceCms >= mLimitFarRangeThreshold) {
                    threshold = 0;
                } else {
                    threshold = 1;
                }

                DistanceAlertNotification notification = new DistanceAlertNotification(accessory, distanceCms, threshold);
                onDistanceAlertAccessoryNotification(notification);
            }
        } else if (rangingResult instanceof RangingResult.RangingResultPeerDisconnected) {
            bleClose(accessory);
            uwbClose(accessory);
            cancelTimerBleConnect(accessory);
            cancelTimerAccessoriesLegacyOoBSupport(accessory);

            mAccessoriesList.remove(accessory);

            // Remove the notification entry
            for (DistanceAlertRecyclerItem item : mDistanceAlertItemList) {
                if (!item.isThresholdLine()) {
                    if (item.getNotification().getAccessory().getMac().equals(accessory.getMac())) {
                        mDistanceAlertItemList.remove(item);
                        break;
                    }
                }
            }

            // Let's update the screen
            updateDistanceAlertView();

            // Inform the user about connection lost
            showConnectionLostToast(accessory);
        }
    }

    @Override
    public void onRangingError(Throwable error) {
        // Close sessions
        bleClose();
        uwbClose();
        cancelTimerBleConnect();
        cancelTimerAccessoriesLegacyOoBSupport();

        mAccessoriesList.clear();
        mAccessoriesConnectingList.clear();

        // Let's clean the screen
        initializeRecyclerItemList();
        updateDistanceAlertView();

        mScreenState = ScreenState.SCREEN_SHOWN;

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

    private void startDistanceAlertDemo() {
        mScreenState = ScreenState.DISTANCEALERTDEMO_RUNNING;
        bleStartDeviceScan();
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
        Accessory accessory = getConnectingAccessoryFromBluetoothLeAddress(remoteAddress);
        if (accessory != null) {
            mAccessoriesConnectingList.remove(accessory);
            cancelTimerBleConnect(accessory);
        }
    }

    private void legacyOoBSupportTimeout(String remoteAddress) {
        Accessory accessory = getAccessoryFromBluetoothLeAddress(remoteAddress);
        if (accessory != null) {
            transmitLegacyStartUwbRangingConfiguration(accessory);
            cancelTimerAccessoriesLegacyOoBSupport(accessory);
        }
    }

    private void initializeRecyclerItemList() {
        mDistanceAlertItemList.clear();
        mDistanceAlertItemList.add(new DistanceAlertRecyclerItem(mLimitFarRangeThreshold));
        mDistanceAlertItemList.add(new DistanceAlertRecyclerItem(mLimitCloseRangeThreshold));
    }

    private void displayRangingError(Throwable error) {
        error.printStackTrace();

        // Inform the user that all connections are now closed
        mToastsHelper.notifyGenericMessage("UWB error, closed all opened sessions!");
    }

    private void onDistanceAlertAccessoryNotification(DistanceAlertNotification notification) {
        processNotification(notification);
        sortRecyclerItems();
        updateDistanceAlertView();
    }

    private void processNotification(DistanceAlertNotification notification) {
        for (DistanceAlertRecyclerItem item : mDistanceAlertItemList) {
            if (!item.isThresholdLine()) {
                if (item.getNotification().getAccessory().getMac().equals(notification.getAccessory().getMac())) {
                    item.setNotification(notification);
                    return;
                }
            }
        }

        mDistanceAlertItemList.add(new DistanceAlertRecyclerItem(notification));
    }

    private void sortRecyclerItems() {
        Collections.sort(mDistanceAlertItemList, new Comparator<DistanceAlertRecyclerItem>() {
            @Override
            public int compare(DistanceAlertRecyclerItem t1, DistanceAlertRecyclerItem t2) {
                if (t1.isThresholdLine() && t2.isThresholdLine()) {
                    return t2.getThresholdLimit() - t1.getThresholdLimit();
                } else if (t1.isThresholdLine()) {
                    return t2.getNotification().getDistance() - t1.getThresholdLimit();
                } else if (t2.isThresholdLine()) {
                    return t2.getThresholdLimit() - t1.getNotification().getDistance();
                } else {
                    return t2.getNotification().getDistance() - t1.getNotification().getDistance();
                }
            }
        });
    }

    private void showMultipleSessionsNotSupportedDialog() {
        mScreenState = ScreenState.MULTIPLESESSIONSNOTSUPPORTED_DIALOG_SHOWN;
        mDialogsManager.showMultipleSessionsNotSupportedDialog(DIALOGTAG_MULTIPLESESSIONSNOTSUPPORTED);
    }

    private void showPairingInfoDialog() {
        mScreenState = ScreenState.PAIRINGINFO_DIALOG_SHOWN;
        mDialogsManager.showPairingInfoDialog(DIALOGTAG_PAIRINGINFO);
    }

    private void showEditDistanceAlertThresholdsDialog() {
        mScreenState = ScreenState.EDITTHRESHOLDS_DIALOG_SHOWN;
        mDialogsManager.showEditDistanceAlertThresholdsDialog(DIALOGTAG_EDITTHRESHOLDS, mLimitCloseRangeThreshold, mLimitFarRangeThreshold);
    }

    private void showEditAccessoryAliasDialog(final Accessory accessory) {
        mScreenState = ScreenState.EDITACCESSORYNAME_DIALOG_SHOWN;
        mDialogsManager.showEditAccessoryAliasDialog(DIALOGTAG_EDITACCESSORYNAME, accessory);
    }

    private void showConnectionLostToast(final Accessory accessory) {
        // Display toast to inform about lost connection
        if (accessory != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                    mToastsHelper.notifyGenericMessage("Connection lost with accessory: " + accessory.getAlias());
                } else {
                    mToastsHelper.notifyGenericMessage("Connection lost with accessory: " + accessory.getName());
                }
            });
        }
    }

    private void updateDistanceAlertView() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mView.update();
            }
        });
    }

    private Accessory getAccessoryFromBluetoothLeAddress(String address) {
        for (Accessory accessory : mAccessoriesList) {
            if (accessory.getMac().equals(address)) {
                return accessory;
            }
        }

        return null;
    }

    private Accessory getConnectingAccessoryFromBluetoothLeAddress(String address) {
        for (Accessory accessory : mAccessoriesConnectingList) {
            if (accessory.getMac().equals(address)) {
                return accessory;
            }
        }

        return null;
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
        if (event instanceof EditAccessoryAliasDialogEvent) {
            switch (((EditAccessoryAliasDialogEvent) event).getClickedButton()) {
                case EDIT:
                    Accessory accessory = ((EditAccessoryAliasDialogEvent) event).getAccessory();
                    String accessoryAlias = ((EditAccessoryAliasDialogEvent) event).getAccessoryAlias();
                    accessory.setAlias(accessoryAlias);

                    if (accessoryAlias == null || accessoryAlias.isEmpty()) {
                        mToastsHelper.notifyGenericMessage("Invalid alias!");
                    } else {
                        if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                            mDatabaseStorageHelper.updateAccessoryAlias(accessory, accessoryAlias);
                        } else {
                            accessory.setAlias(accessoryAlias);
                            mDatabaseStorageHelper.insertAccessory(accessory);
                        }
                    }

                    int i = 0;
                    for (Accessory connectedAccessory : mAccessoriesList) {
                        if (accessory.getMac().equals(connectedAccessory.getMac())) {
                            mAccessoriesList.set(i, accessory);
                            break;
                        }

                        i++;
                    }

                    break;

                case CANCEL:
                    break;
            }
        } else if (event instanceof EditDistanceAlertThresholdsDialogEvent) {
            switch (((EditDistanceAlertThresholdsDialogEvent) event).getClickedButton()) {
                case EDIT:
                    int closeRangeThreshold = ((EditDistanceAlertThresholdsDialogEvent) event).getCloseRangeThreshold();
                    int farRangeThreshold = ((EditDistanceAlertThresholdsDialogEvent) event).getFarRangeThreshold();

                    if (closeRangeThreshold > 0 && farRangeThreshold > 0 && farRangeThreshold > closeRangeThreshold) {
                        mLimitCloseRangeThreshold = closeRangeThreshold;
                        mLimitFarRangeThreshold = farRangeThreshold;

                        mPreferenceStorageHelper.setDistanceAlertCloseRangeThreshold(closeRangeThreshold);
                        mPreferenceStorageHelper.setDistanceAlertFarRangeThreshold(farRangeThreshold);

                        // Initialize recycler to update thresholds
                        initializeRecyclerItemList();
                        updateDistanceAlertView();
                    } else {
                        mToastsHelper.notifyGenericMessage("Invalid thresholds!");
                    }

                    break;

                case CANCEL:
                    break;
            }
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
            }
        } else if (event instanceof InfoDoNotShowAgainDialogEvent) {
            switch (mScreenState) {
                case PAIRINGINFO_DIALOG_SHOWN:
                    switch (((InfoDoNotShowAgainDialogEvent) event).getClickedButton()) {
                        case ACCEPT:
                            boolean showInfo = ((InfoDoNotShowAgainDialogEvent) event).getShowInfo();
                            mPreferenceStorageHelper.setShowPairingInfo(showInfo);

                            break;
                        case CANCEL:
                            break;
                    }

                    // Proceed to start demo
                    startDistanceAlertDemo();

                    break;

                case MULTIPLESESSIONSNOTSUPPORTED_DIALOG_SHOWN:
                    switch (((InfoDoNotShowAgainDialogEvent) event).getClickedButton()) {
                        case ACCEPT:
                            boolean showInfo = ((InfoDoNotShowAgainDialogEvent) event).getShowInfo();
                            mPreferenceStorageHelper.setShowMultipleSessionsNotSupported(showInfo);

                            break;
                        case CANCEL:
                            break;
                    }

                    if (mPreferenceStorageHelper.getShowPairingInfo()) {
                        // Show pairing info dialog as long as the user is interested in it
                        showPairingInfoDialog();
                    } else {
                        // Proceed to start demo
                        startDistanceAlertDemo();
                    }

                    break;
            }
        }
    }
}
