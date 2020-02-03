package com.example.bluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mService;
    private boolean mScanning;

    private static final String TAG = "MAIN_ACTIVITY";

    private void configRetryButton() {
        Button button= (Button) findViewById(R.id.retry_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mService.connect(mBluetoothAdapter.getRemoteDevice("98:D3:21:F4:82:77"));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mService = BluetoothService.getDefaultInstance();

        mService.setOnScanCallback(this);
        mService.setOnEventCallback(this);

        configRetryButton();

//        startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        mService.connect(mBluetoothAdapter.getRemoteDevice("98:D3:21:F4:82:77"));
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        Log.d(TAG, "onDeviceDiscovered: " + device.getName() + " - " + device.getAddress() + " - " + Arrays.toString(device.getUuids()));
    }

    @Override
    public void onStartScan() {
        Log.d(TAG, "onStartScan");
        mScanning = true;
    }

    @Override
    public void onStopScan() {
        Log.d(TAG, "onStopScan");
        mScanning = false;
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {
        Log.d(TAG, "onDataRead");
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();

        if (status == BluetoothStatus.CONNECTED) {
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        }
    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(TAG, "onDeviceName: " + deviceName);
    }

    @Override
    public void onToast(String message) {
        Log.d(TAG, "onToast");
    }

    @Override
    public void onDataWrite(byte[] buffer) {
        Log.d(TAG, "onDataWrite");
    }
}





