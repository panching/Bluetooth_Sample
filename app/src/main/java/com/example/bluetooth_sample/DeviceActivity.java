package com.example.bluetooth_sample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = "DeviceActivity";
    private TextView deviceName;
    private TextView deviceMac;
    private TextView deviceUUID;
    private DeviceModel deviceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        deviceModel = (DeviceModel) getIntent().getSerializableExtra(Variable.DEVICE.toString());
        deviceName = (TextView) findViewById(R.id.tvDeviceNameVal);
        deviceMac = (TextView) findViewById(R.id.tvDeviceMacVal);
        deviceUUID = (TextView) findViewById(R.id.tvDeviceUUIDVal);
        initValues();
    }

    private void initValues() {
        deviceName.setText(deviceModel.getName());
        deviceMac.setText(deviceModel.getMac());
        deviceUUID.setText(deviceModel.getUUID());
    }

}
