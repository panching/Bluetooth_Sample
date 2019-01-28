package com.example.bluetooth_sample;

import java.io.Closeable;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class BluetoothHelper implements Closeable {

    /**
     * Tag string used for logging.
     */
    private static final String TAG = "BluetoothHelper";

    /**
     * Interface for Bluetooth OS services.
     */
    private final BluetoothAdapter bluetooth;

    /**
     * Class used to handle communication with OS about Bluetooth system events.
     */
    private final BroadcastDelegate broadcastDelegate;

    /**
     * The activity which is using this controller.
     */
    private final Activity context;

    /**
     * Used as a simple way of synchronization between turning on the Bluetooth and starting a
     * device discovery.
     */
    private boolean bluetoothDiscoveryScheduled;

    /**
     * Used as a temporary field for the currently bounding device. This field makes this whole
     * class not Thread Safe.
     */
    private BluetoothDevice boundingDevice;

    private ProgressListener progressListener;

    /**
     * Instantiates a new BluetoothController.
     *
     * @param context  the activity which is using this controller.
     * @param listener a callback for handling Bluetooth events.
     */
    public BluetoothHelper(Activity context,BluetoothAdapter adapter, BluetoothListener listener, ProgressListener progressListener) {
        this.context = context;
        this.bluetooth = adapter;
        this.progressListener = progressListener;
        this.broadcastDelegate = new BroadcastDelegate(context, listener, this, progressListener);
    }
    /**
     * Starts the discovery of new Bluetooth devices nearby.
     */
    public void startDiscovery() {
        broadcastDelegate.onDeviceDiscoveryStarted();

        // If another discovery is in progress, cancels it before starting the new one.
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }

        // Tries to start the discovery. If the discovery returns false, this means that the
        // bluetooth has not started yet.
        Log.d(TAG, "Bluetooth starting discovery.");
        if (!bluetooth.startDiscovery()) {
            Toast.makeText(context, "Error while starting device discovery!", Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "StartDiscovery returned false. Maybe Bluetooth isn't on?");

            // Ends the discovery.
            broadcastDelegate.onDeviceDiscoveryEnd();
        }
    }
    /**
     * Performs the device pairing.
     *
     * @param device the device to pair with.
     * @return true if the pairing was successful, false otherwise.
     */
    public boolean pair(BluetoothDevice device) {
        // Stops the discovery and then creates the pairing.
        if (bluetooth.isDiscovering()) {
            Log.d(TAG, "Bluetooth cancelling discovery.");
            bluetooth.cancelDiscovery();
        }
        Log.d(TAG, "Bluetooth bonding with device: " + deviceToString(device));
        boolean outcome = device.createBond();
        Log.d(TAG, "Bounding outcome : " + outcome);

        // If the outcome is true, we are bounding with this device.
        if (outcome) {
            this.boundingDevice = device;
        }
        return outcome;
    }

    /**
     * Checks if a device is already paired.
     *
     * @param device the device to check.
     * @return true if it is already paired, false otherwise.
     */
    public boolean isAlreadyPaired(BluetoothDevice device) {
        return bluetooth.getBondedDevices().contains(device);
    }

    /**
     * Converts a BluetoothDevice to its String representation.
     *
     * @param device the device to convert to String.
     * @return a String representation of the device.
     */
    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.broadcastDelegate.close();
    }

    /**
     * Checks if a deviceDiscovery is currently running.
     *
     * @return true if a deviceDiscovery is currently running, false otherwise.
     */
    public boolean isDiscovering() {
        return bluetooth.isDiscovering();
    }

    /**
     * Cancels a device discovery.
     */
    public void cancelDiscovery() {
        if(bluetooth != null) {
            bluetooth.cancelDiscovery();
            broadcastDelegate.onDeviceDiscoveryEnd();
        }
    }

    /**
     * Called when the Bluetooth status changed.
     */
    public void onBluetoothStatusChanged() {
        // Does anything only if a device discovery has been scheduled.
        if (bluetoothDiscoveryScheduled) {

            int bluetoothState = bluetooth.getState();
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    // Bluetooth is ON.
                    Log.d(TAG, "Bluetooth successfully enabled, starting discovery");
                    startDiscovery();
                    // Resets the flag since this discovery has been performed.
                    bluetoothDiscoveryScheduled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    // Bluetooth is OFF.
                    Log.d(TAG, "Error while turning Bluetooth on.");
                    Toast.makeText(context, "Error while turning Bluetooth on.", Toast.LENGTH_SHORT);
                    // Resets the flag since this discovery has been performed.
                    bluetoothDiscoveryScheduled = false;
                    break;
                default:
                    // Bluetooth is turning ON or OFF. Ignore.
                    break;
            }
        }
    }

    /**
     * Returns the status of the current pairing and cleans up the state if the pairing is done.
     *
     * @return the current pairing status.
     * @see BluetoothDevice#getBondState()
     */
    public int getPairingDeviceStatus() {
        if (this.boundingDevice == null) {
            throw new IllegalStateException("No device currently bounding");
        }
        int bondState = this.boundingDevice.getBondState();
        // If the new state is not BOND_BONDING, the pairing is finished, cleans up the state.
        if (bondState != BluetoothDevice.BOND_BONDING) {
            this.boundingDevice = null;
        }
        return bondState;
    }

    /**
     * Gets the name of a device. If the device name is not available, returns the device address.
     *
     * @param device the device whose name to return.
     * @return the name of the device or its address if the name is not available.
     */
    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }
        return deviceName;
    }

    /**
     * Returns if there's a pairing currently being done through this app.
     *
     * @return true if a pairing is in progress through this app, false otherwise.
     */
    public boolean isPairingInProgress() {
        return this.boundingDevice != null;
    }

    /**
     * Gets the currently bounding device.
     *
     * @return the {@link #boundingDevice}.
     */
    public BluetoothDevice getBoundingDevice() {
        return boundingDevice;
    }
}
