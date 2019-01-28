package com.example.bluetooth_sample;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener, View.OnClickListener, ProgressListener {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MainActivity";
    private MaterialDialog progressDialog;
    private ProgressListener progressListener;

    private Context context = this;
    private RecyclerViewAdapter recyclerViewAdapter;
    private BluetoothHelper bluetooth;
    private Button detectButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // callback listener
        progressListener = this;
        detectButton = (Button) findViewById(R.id.main_detect_button);
        detectButton.setOnClickListener(this);
        recyclerView = findViewById(R.id.main_wrapper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new RecyclerViewAdapter(this, progressListener);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.loading)
                .cancelable(false)
                .content(R.string.wait)
                .progress(true, 0)
                .theme(Theme.LIGHT)
                .build();
    }

    @Override
    protected void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stoops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Stops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
        // Cleans the view.
        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.cleanView();
        }
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        Log.d(TAG, "Item clicked : " + BluetoothHelper.deviceToString(device));
        if (bluetooth.isAlreadyPaired(device)) {
            Toast.makeText(this, "Device " + device.getName() + " already paired!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Device already paired!");
        } else {
            progressListener.startLoading();
            Log.d(TAG, "Device not paired. Pairing.");
            boolean outcome = bluetooth.pair(device);

            // Prints a message to the user.
            String deviceName = BluetoothHelper.getDeviceName(device);
            if (outcome) {
                // The pairing has started, shows a progress dialog.
                Log.d(TAG, "Showing pairing dialog");
            } else {
                progressDialog.hide();
                Log.d(TAG, "Error while pairing with device " + deviceName + "!");
                Toast.makeText(this, "Error while pairing with device " + deviceName + "!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_detect_button:
                scanDevices();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, getResources().getString(R.string.permission_granted));
                } else {
                    errorBuilder(getResources().getString(R.string.notice), getResources().getString(R.string.functionality_limited));
                }
                return;
            }
        }
    }

    @Override
    public void startLoading() {
        progressDialog.show();
    }

    @Override
    public void endLoading() {
        progressDialog.hide();
    }

    @Override
    public void connectedDeviceInfo(BluetoothDevice device) {
        try {
            Intent intent = new Intent(this, DeviceActivity.class);
            Bundle bundle = new Bundle();
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
            StringBuilder sb = new StringBuilder();
            for (ParcelUuid uuid : uuids) {
                sb.append(uuid.getUuid().toString() + "\n");
            }
            DeviceModel deviceModel = new DeviceModel(device.getName(), device.getAddress(), sb.toString());
            bundle.putSerializable(Variable.DEVICE.toString(), deviceModel);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanDevices() {
        if (!permissionDetector()) {
            return;
        }

        detectButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progressListener.startLoading();
        this.bluetooth = new BluetoothHelper(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter, progressListener);
        if (!bluetooth.isDiscovering()) {
            // Starts the discovery.
            bluetooth.startDiscovery();
        } else {
            Toast.makeText(context, R.string.device_discovery_stopped, Toast.LENGTH_SHORT).show();
            bluetooth.cancelDiscovery();
        }
    }

    private boolean permissionDetector() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                errorBuilder(getResources().getString(R.string.notice), getResources().getString(R.string.location_access));
                return false;
            }
        }

        if (adapter == null) {
            errorBuilder(getResources().getString(R.string.notice), getResources().getString(R.string.bluetooth_not_found));
            return false;
        } else if (!adapter.isEnabled()) {
            errorBuilder(getResources().getString(R.string.notice), getResources().getString(R.string.bluetooth_disable));
            return false;
        } else {
            return true;
        }
    }

    private void errorBuilder(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }
}
