package com.example.bluetooth_sample;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements BluetoothListener {
    private final List<BluetoothDevice> devices;
    private BluetoothHelper bluetooth;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private ProgressListener progressListener;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, ProgressListener progressListener) {
        this.inflater = LayoutInflater.from(context);
        this.devices = new ArrayList<>();
        this.progressListener = progressListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.device = devices.get(position);
        holder.deviceNameView.setText(devices.get(position).getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        devices.add(device);
        notifyDataSetChanged();
    }

    @Override
    public void onDeviceDiscoveryStarted() {
        cleanView();
    }

    @Override
    public void setBluetoothController(BluetoothHelper bluetooth) {
        this.bluetooth = bluetooth;
    }

    @Override
    public void onBluetoothStatusChanged() {
        bluetooth.onBluetoothStatusChanged();
    }

    @Override
    public void onDeviceDiscoveryEnd() {

    }

    @Override
    public void onBluetoothTurningOn() {

    }

    @Override
    public void onDevicePairingEnded() {
        progressListener.startLoading();
        if (bluetooth.isPairingInProgress()) {
            BluetoothDevice device = bluetooth.getBoundingDevice();
            switch (bluetooth.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:
                    // Still pairing, do nothing.
                    break;
                case BluetoothDevice.BOND_BONDED:
                    progressListener.endLoading();
                    progressListener.connectedDeviceInfo(device);
                    // Updates the icon for this element.
                    notifyDataSetChanged();
                    break;
                case BluetoothDevice.BOND_NONE:
                    progressListener.endLoading();
                    break;
            }
        }
    }

    /**
     * Cleans the view.
     */
    public void cleanView() {
        devices.clear();
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        BluetoothDevice device;
        TextView deviceNameView;

        ViewHolder(View itemView) {
            super(itemView);
            deviceNameView = itemView.findViewById(R.id.tvDeviceName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(device);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + BluetoothHelper.deviceToString(device) + "'";
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(BluetoothDevice device);
    }
}
