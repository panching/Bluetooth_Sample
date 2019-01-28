package com.example.bluetooth_sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.Closeable;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BroadcastDelegate extends BroadcastReceiver implements Closeable  {
    /**
     * Callback for Bluetooth events.
     */
    private final BluetoothListener listener;

    /**
     * Tag string used for logging.
     */
    private final String TAG = "BroadcastDelegate";

    /**
     * The context of this object.
     */
    private final Context context;

    private ProgressListener progressListener;
    /**
     * Instantiates a new BroadcastReceiverDelegator.
     *
     * @param context   the context of this object.
     * @param listener  a callback for handling Bluetooth events.
     * @param bluetooth a controller for the Bluetooth.
     */
    public BroadcastDelegate(Context context, BluetoothListener listener, BluetoothHelper bluetooth, ProgressListener progressListener) {
        this.listener = listener;
        this.context = context;
        this.listener.setBluetoothController(bluetooth);
        this.progressListener = progressListener;
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(this, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Incoming intent : " + action);
        switch (action) {
            case BluetoothDevice.ACTION_FOUND :
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Device discovered! " + BluetoothHelper.deviceToString(device));
                listener.onDeviceDiscovered(device);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
                // Discovery has ended.
                progressListener.endLoading();
                Log.d(TAG, "Discovery ended.");
                listener.onDeviceDiscoveryEnd();
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED :
                // Discovery state changed.
                Log.d(TAG, "Bluetooth state changed.");
                listener.onBluetoothStatusChanged();
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED :
                // Pairing state has changed.
                Log.d(TAG, "Bluetooth bonding state changed.");
                listener.onDevicePairingEnded();
                break;
            default :
                // Does nothing.
                break;
        }
    }

    /**
     * Called when device discovery starts.
     */
    public void onDeviceDiscoveryStarted() {
        listener.onDeviceDiscoveryStarted();
    }

    /**
     * Called when device discovery ends.
     */
    public void onDeviceDiscoveryEnd() {
        listener.onDeviceDiscoveryEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        context.unregisterReceiver(this);
    }
}
