package com.themobileknowledge.uwbconnectapp.uwb;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.uwb.RangingCapabilities;
import androidx.core.uwb.RangingParameters;
import androidx.core.uwb.RangingResult;
import androidx.core.uwb.UwbAddress;
import androidx.core.uwb.UwbClientSessionScope;
import androidx.core.uwb.UwbComplexChannel;
import androidx.core.uwb.UwbControleeSessionScope;
import androidx.core.uwb.UwbControllerSessionScope;
import androidx.core.uwb.UwbDevice;
import androidx.core.uwb.UwbManager;
import androidx.core.uwb.rxjava3.UwbClientSessionScopeRx;
import androidx.core.uwb.rxjava3.UwbManagerRx;

import com.themobileknowledge.uwbconnectapp.oob.model.UwbDeviceConfigData;
import com.themobileknowledge.uwbconnectapp.oob.model.UwbPhoneConfigData;
import com.themobileknowledge.uwbconnectapp.utils.Utils;
import com.themobileknowledge.uwbconnectapp.uwb.model.UwbRemoteDevice;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class UwbManagerHelper {

    private static final String TAG = UwbManagerHelper.class.getSimpleName();

    private static final int CONTROLLER_ROLE = 0x00;
    private static final int CONTROLEE_ROLE = 0x01;

    // UWB Role map as per Android OoB Specification
    public static Map<String, Integer> uwbRoleMap;

    static {
        uwbRoleMap = new HashMap<>();
        uwbRoleMap.put("Controller", CONTROLLER_ROLE);
        uwbRoleMap.put("Controlee", CONTROLEE_ROLE);
    }

    // Default UWB Ranging configuration parameters
    public static final int UWB_CHANNEL = 9;
    public static final int UWB_PREAMBLE_INDEX = 10;
    public static final int PREFERRED_UWB_PHONE_ROLE = CONTROLEE_ROLE;
    public static final int PREFERRED_UWB_PROFILE_ID = RangingParameters.CONFIG_UNICAST_DS_TWR;

    // Configurable parameters
    private int mUwbChannel = UWB_CHANNEL;
    private int mUwbPreambleIndex = UWB_PREAMBLE_INDEX;
    private int mPreferredUwbProfileId = PREFERRED_UWB_PROFILE_ID;
    private int mPreferredUwbPhoneRole = PREFERRED_UWB_PHONE_ROLE;

    private Context mContext;
    private UwbManager mUwbManager = null;

    private Listener mListener = null;

    // List with all active Uwb Ranging sessions
    private HashMap<String, UwbRemoteDevice> mUwbRemoteDeviceList = new HashMap<>();

    public interface Listener {
        void onRangingCapabilities(RangingCapabilities rangingCapabilities);

        void onRangingStarted(String address, UwbPhoneConfigData uwbPhoneConfigData);

        void onRangingResult(String address, RangingResult rangingResult);

        void onRangingError(Throwable error);

        void onRangingComplete();
    }

    /**
     * UwbManagerHelper class is the entry point for UWB technology.
     *
     * @param context Application context
     */
    public UwbManagerHelper(final Context context) {
        this.mContext = context;

        // Create the Uwb Manager if supported by this device
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature("android.hardware.uwb")) {
            mUwbManager = UwbManager.createInstance(context);
        }
    }

    /**
     * Register UWB listener
     *
     * @param listener Listener
     */
    public void registerListener(Listener listener) {
        this.mListener = listener;
    }

    /**
     * Unregister UWB listener
     */
    public void unregisterListener() {
        this.mListener = null;
    }

    /**
     * Checks if UWB is supported on this device
     *
     * @return True if UWB is supported, else false
     */
    public boolean isSupported() {
        return mUwbManager != null;
    }

    /**
     * Checks if UWB is enabled on this device
     *
     * @return True if UWB is enabled, else false
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * UWB channel to be used to establish the UWB ranging session
     *
     * @param uwbChannel UWB channel
     */
    public void setUwbChannel(int uwbChannel) {
        mUwbChannel = uwbChannel;
    }

    /**
     * UWB preamble index to be used to establish the UWB ranging session
     *
     * @param uwbPreambleIndex UWB preamble index
     */
    public void setUwbPreambleIndex(int uwbPreambleIndex) {
        mUwbPreambleIndex = uwbPreambleIndex;
    }

    /**
     * Preferred UWB role to be used to establish the UWB ranging session
     *
     * @param uwbRole UWB role
     */
    public void setPreferredUwbRole(String uwbRole) {
        // Update preferred role given that provided key exists
        Integer hashUwbRole = uwbRoleMap.get(uwbRole);
        if (hashUwbRole != null) {
            mPreferredUwbPhoneRole = hashUwbRole;
        }
    }

    /**
     * Preferred UWB config type to be used to establish the UWB ranging session
     *
     * @param uwbConfigType UWB config type
     */
    public void setPreferredUwbProfileId(int uwbConfigType) {
        mPreferredUwbProfileId = uwbConfigType;
    }

    /**
     * Starts an UWB ranging session with an UWB device
     *
     * @param remoteAddress       Remote Bluetooth LE MAC Address
     * @param uwbDeviceConfigData UWB device configuration data received from the remote UWB device
     * @return true if UWB ranging session successfully started, else false
     */
    public boolean startRanging(String remoteAddress, UwbDeviceConfigData uwbDeviceConfigData) {

        if (mUwbManager == null) {
            Log.e(TAG, "UWB Manager is not available in this device");
            return false;
        }

        if (remoteAddress == null || remoteAddress.isEmpty()) {
            Log.e(TAG, "remote address is not set");
            return false;
        }

        if (uwbDeviceConfigData == null) {
            Log.e(TAG, "uwbDeviceConfigData is not set");
            return false;
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.UWB_RANGING) == PackageManager.PERMISSION_GRANTED) {
            Thread t = new Thread(() -> {
                UwbComplexChannel uwbComplexChannel;
                UwbControllerSessionScope uwbControllerSessionScope;
                UwbControleeSessionScope uwbControleeSessionScope;
                UwbAddress uwbAddress;
                Flowable<RangingResult> flowable;
                byte selectUwbDeviceRangingRole = selectUwbDeviceRangingRole(uwbDeviceConfigData.getSupportedDeviceRangingRoles());
                String str2 = TAG;
                Log.d(str2, "Uwb device supported ranging roles: " + ((int) uwbDeviceConfigData.getSupportedDeviceRangingRoles()) + ", selected role for UWB device: " + ((int) selectUwbDeviceRangingRole));
                byte selectUwbProfileId = selectUwbProfileId(uwbDeviceConfigData.getSupportedUwbProfileIds());
                Log.d(str2, "Uwb device supported UWB profile IDs: " + uwbDeviceConfigData.getSupportedUwbProfileIds() + ", selected UWB profile ID: " + ((int) selectUwbProfileId));
                try {
                    if (selectUwbDeviceRangingRole == 0) {
                        Log.d(str2, "Android device will act as Controlee!");
                        UwbControleeSessionScope blockingGet = UwbManagerRx.controleeSessionScopeSingle(this.mUwbManager).blockingGet();
                        uwbAddress = blockingGet.getLocalAddress();
                        uwbComplexChannel = new UwbComplexChannel(this.mUwbChannel, this.mUwbPreambleIndex);
                        uwbControllerSessionScope = null;
                        uwbControleeSessionScope = blockingGet;
                    } else {
                        Log.d(str2, "Android device will act as Controller!");
                        UwbControllerSessionScope blockingGet2 = UwbManagerRx.controllerSessionScopeSingle(this.mUwbManager).blockingGet();
                        uwbAddress = blockingGet2.getLocalAddress();
                        uwbControllerSessionScope = blockingGet2;
                        uwbComplexChannel = blockingGet2.getUwbComplexChannel();
                        uwbControleeSessionScope = null;
                    }
                    int nextInt = new Random().nextInt();
                    UwbControleeSessionScope uwbControleeSessionScope2 = uwbControleeSessionScope;
                    UwbDevice uwbDevice = new UwbDevice(new UwbAddress(uwbDeviceConfigData.getDeviceMacAddress()));
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(uwbDevice);
                    Log.d(str2, "UWB SessionId: " + nextInt);
                    Log.d(str2, "UWB Local Address: " + uwbAddress);
                    Log.d(str2, "UWB Remote Address: " + uwbDevice);
                    Log.d(str2, "UWB Channel: " + uwbComplexChannel.getChannel());
                    Log.d(str2, "UWB Preamble Index: " + uwbComplexChannel.getPreambleIndex());
                    byte[] hexStringtoByteArray = Utils.hexStringtoByteArray("0807010203040506");
                    Log.d(str2, "Configure ranging parameters for Profile ID: " + ((int) selectUwbProfileId));
                    UwbAddress uwbAddress2 = uwbAddress;
                    UwbControllerSessionScope uwbControllerSessionScope2 = uwbControllerSessionScope;
                    RangingParameters rangingParameters = new RangingParameters(selectUwbProfileId, nextInt, hexStringtoByteArray, uwbComplexChannel, arrayList, RangingParameters.RANGING_UPDATE_RATE_FREQUENT);
                    if (selectUwbDeviceRangingRole == 0) {
                        Log.d(str2, "Configure controlee flowable");
                        flowable = UwbClientSessionScopeRx.rangingResultsFlowable(uwbControleeSessionScope2, rangingParameters);
                    } else {
                        Log.d(str2, "Configure controller flowable");
                        flowable = UwbClientSessionScopeRx.rangingResultsFlowable(uwbControllerSessionScope2, rangingParameters);
                    }
                    this.mUwbRemoteDeviceList.put(remoteAddress, new UwbRemoteDevice(uwbDevice, (Disposable) flowable.delay(100L, TimeUnit.MILLISECONDS).subscribeWith(new DisposableSubscriber<RangingResult>() { // from class: com.themobileknowledge.uwbconnectapp.uwb.UwbManagerHelper.1
                        @Override // io.reactivex.rxjava3.subscribers.DisposableSubscriber
                        public void onStart() {
                            request(1L);
                        }
                        public void onNext(RangingResult rangingResult) {
                            String addressFromUwbDevice = UwbManagerHelper.this.getAddressFromUwbDevice(rangingResult.getDevice());
                            if (addressFromUwbDevice != null) {
                                UwbManagerHelper.this.onRangingResult(addressFromUwbDevice, rangingResult);
                            }
                            request(1L);
                        }
                        @Override // org.reactivestreams.Subscriber
                        public void onError(Throwable th) {
                            UwbManagerHelper.this.onRangingError(th);
                        }
                        @Override // org.reactivestreams.Subscriber
                        public void onComplete() {
                            UwbManagerHelper.this.onRangingComplete();
                        }
                    })));
                    UwbPhoneConfigData uwbPhoneConfigData = new UwbPhoneConfigData();
                    uwbPhoneConfigData.setSpecVerMajor((short) 256);
                    uwbPhoneConfigData.setSpecVerMinor((short) 0);
                    uwbPhoneConfigData.setSessionId(nextInt);
                    uwbPhoneConfigData.setPreambleIndex((byte) uwbComplexChannel.getPreambleIndex());
                    uwbPhoneConfigData.setChannel((byte) uwbComplexChannel.getChannel());
                    uwbPhoneConfigData.setProfileId(selectUwbProfileId);
                    uwbPhoneConfigData.setDeviceRangingRole((byte) (1 << selectUwbDeviceRangingRole));
                    uwbPhoneConfigData.setPhoneMacAddress(uwbAddress2.getAddress());
                    onRangingStarted(remoteAddress, uwbPhoneConfigData);
                } catch (Exception e) {
                    Log.e(TAG, "UWB Ranging configuration exception: " + e.getMessage());
                    onRangingError(e);
                }
            });

            t.start();
            return true;
        } else {
            Log.e(TAG, "Missing required permission to start UWB ranging");
            return false;
        }
    }

    /**
     * Stops an UWB ranging session
     *
     * @param remoteAddress Remote Bluetooth LE MAC Address
     * @return true if UWB ranging session successfully stopped, else false
     */
    public boolean stopRanging(String remoteAddress) {
        Log.d(TAG, "Proceeed to stop connection with device " + remoteAddress);

        try {
            UwbRemoteDevice uwbRemoteDevice = mUwbRemoteDeviceList.get(remoteAddress);
            if (uwbRemoteDevice == null || uwbRemoteDevice.getDisposable() == null) {
                Log.e(TAG, "UWB Ranging session not started or disposable not initialized.");
                return false;
            }

            uwbRemoteDevice.getDisposable().dispose();
            mUwbRemoteDeviceList.remove(remoteAddress);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception while closing UWB Ranging session: " + e.getMessage());
        }

        return false;
    }

    /**
     * Closes an UWB ranging session
     *
     * @param remoteAddress Remote Bluetooth LE MAC Address
     * @return true if UWB ranging session successfully closed, else false
     */
    public boolean close(String remoteAddress) {
        Log.d(TAG, "Proceeed to close connection with device " + remoteAddress);

        try {
            UwbRemoteDevice uwbRemoteDevice = mUwbRemoteDeviceList.get(remoteAddress);
            if (uwbRemoteDevice == null || uwbRemoteDevice.getDisposable() == null) {
                Log.e(TAG, "UWB Ranging session not started or disposable not initialized.");
                return false;
            }

            uwbRemoteDevice.getDisposable().dispose();
            mUwbRemoteDeviceList.remove(remoteAddress);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception while closing UWB Ranging session: " + e.getMessage());
        }

        return false;
    }

    /**
     * Returns UWB Ranging Capabilities for current device
     *
     * @return true if UWB ranging capabilities were successfully retrieved, else false
     */
    public boolean getRangingCapabilities() {
        if (mUwbManager == null) {
            Log.e(TAG, "UWB Manager is not available in this device");
            return false;
        }

        Thread t = new Thread(() -> {
            try {
                Single<UwbClientSessionScope> uwbClientSessionScopeSingle = UwbManagerRx.clientSessionScopeSingle(mUwbManager);
                UwbClientSessionScope uwbClientSessionScope = uwbClientSessionScopeSingle.blockingGet();
                onRangingCapabilities(uwbClientSessionScope.getRangingCapabilities());
            } catch (Exception e) {
                Log.e(TAG, "Exception while getting UWB Ranging capabilities: " + e.getMessage());
                onRangingCapabilities(null);
            }
        });

        t.start();
        return true;
    }

    private byte selectUwbProfileId(int supportedUwbProfileIds) {
        // First try to use preferred Uwb Profile ID selection
        if (BigInteger.valueOf(supportedUwbProfileIds).testBit(mPreferredUwbProfileId)) {
            return (byte) mPreferredUwbProfileId;
        }

        if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.CONFIG_UNICAST_DS_TWR)) {
            return (byte) RangingParameters.CONFIG_UNICAST_DS_TWR;
        } else if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.CONFIG_MULTICAST_DS_TWR)) {
            return (byte) RangingParameters.CONFIG_MULTICAST_DS_TWR;
        } else if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.UWB_CONFIG_ID_3)) {
            return (byte) RangingParameters.UWB_CONFIG_ID_3;
        }

        return 0;
    }

    private byte selectUwbDeviceRangingRole(int supportedUwbDeviceRangingRoles) {
        // First try to use preferred Uwb Role selection
        if (mPreferredUwbPhoneRole == CONTROLLER_ROLE && (((supportedUwbDeviceRangingRoles >> CONTROLEE_ROLE) & 1) != 0)) {
            return CONTROLEE_ROLE;
        } else if (mPreferredUwbPhoneRole == CONTROLEE_ROLE && (((supportedUwbDeviceRangingRoles >> CONTROLLER_ROLE) & 1) != 0)) {
            return CONTROLLER_ROLE;
        }

        if (((supportedUwbDeviceRangingRoles >> CONTROLLER_ROLE) & 1) != 0) {
            return CONTROLEE_ROLE;
        } else if (((supportedUwbDeviceRangingRoles >> CONTROLEE_ROLE) & 1) != 0) {
            return CONTROLLER_ROLE;
        }

        return CONTROLLER_ROLE;
    }

    private String getAddressFromUwbDevice(UwbDevice uwbDevice) {
        for (Map.Entry<String, UwbRemoteDevice> entry : mUwbRemoteDeviceList.entrySet()) {
            String key = entry.getKey();
            UwbRemoteDevice uwbRemoteDevice = entry.getValue();

            if (uwbRemoteDevice.getUwbDevice().getAddress().toString().equals(uwbDevice.getAddress().toString())) {
                return key;
            }
        }

        return null;
    }

    private void onRangingStarted (final String address, final UwbPhoneConfigData uwbPhoneConfigData) {
        // Send callback to app on the UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mListener != null) {
                mListener.onRangingStarted(address, uwbPhoneConfigData);
            }
        });
    }

    private void onRangingResult (final String address, final RangingResult rangingResult) {
        // Send callback to app on the UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mListener != null) {
                mListener.onRangingResult(address, rangingResult);
            }
        });
    }

    private void onRangingError (final Throwable error) {
        // Send callback to app on the UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mListener != null) {
                mListener.onRangingError(error);
            }
        });
    }

    private void onRangingComplete () {
        // Send callback to app on the UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mListener != null) {
                mListener.onRangingComplete();
            }
        });
    }

    private void onRangingCapabilities (final RangingCapabilities rangingCapabilities) {
        // Send callback to app on the UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mListener != null) {
                mListener.onRangingCapabilities(rangingCapabilities);
            }
        });
    }
}