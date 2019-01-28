package com.example.bluetooth_sample;

import android.bluetooth.BluetoothDevice;

public interface ProgressListener {

    void startLoading();

    void endLoading();

    void connectedDeviceInfo(BluetoothDevice device);
}
