package com.themobileknowledge.uwbconnectapp.uwb.model;

import androidx.core.uwb.UwbDevice;

import io.reactivex.rxjava3.disposables.Disposable;

public class UwbRemoteDevice {

    private UwbDevice uwbDevice;
    private Disposable disposable;

    public UwbRemoteDevice() {

    }

    public UwbRemoteDevice(UwbDevice uwbDevice, Disposable disposable) {
        this.uwbDevice = uwbDevice;
        this.disposable = disposable;
    }

    public UwbDevice getUwbDevice() {
        return uwbDevice;
    }

    public void setUwbDevice(UwbDevice uwbDevice) {
        this.uwbDevice = uwbDevice;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }
}
