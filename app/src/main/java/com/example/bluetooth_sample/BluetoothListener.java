package com.example.bluetooth_sample;

import android.bluetooth.BluetoothDevice;

public interface BluetoothListener {

    /**
     * Called when a new device has been found.
     *
     * @param device the device found.
     */
    void onDeviceDiscovered(BluetoothDevice device);

    /**
     * Called when device discovery starts.
     */
    void onDeviceDiscoveryStarted();

    /**
     *
     * @param bluetooth the controller for the Bluetooth.
     */
    void setBluetoothController(BluetoothHelper bluetooth);

    /**
     * Called when discovery ends.
     */
    void onDeviceDiscoveryEnd();

    /**
     * Called when the Bluetooth status changes.
     */
    void onBluetoothStatusChanged();

    /**
     * Called when the Bluetooth has been enabled.
     */
    void onBluetoothTurningOn();

    /**
     * Called when a device pairing ends.
     */
    void onDevicePairingEnded();

}
