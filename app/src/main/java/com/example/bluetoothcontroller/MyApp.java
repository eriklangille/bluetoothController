package com.example.bluetoothcontroller;

import android.app.Activity;
import android.app.Application;
import android.widget.ArrayAdapter;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;

import java.util.ArrayList;
import java.util.UUID;

//Main Application Class.
public class MyApp extends Application {

    /*
     * Change for the UUID that you want.
     */

    public static ArrayList<String> commandList = new ArrayList<String>();

    public static int rot_val1 = 0;
    public static int shift_val1 = 0;
    public static int shift_val2 = 0;
    public static int shift_val3 = 0;

    public ArrayAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();

        adapter = new ArrayAdapter<String>(this,R.layout.activity_listview,commandList);

        BluetoothConfiguration config = new BluetoothConfiguration(); //Configure the bluetooth settings.

        config.bluetoothServiceClass = BluetoothClassicService.class; //  BluetoothClassicService.class or BluetoothLeService.class

        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class; // BluetoothClassicService.class or BluetoothLeService.class
        config.bufferSize = 1024;
        config.characterDelimiter = '\r';
        config.deviceName = "Your App Name";
        config.callListenersInMainThread = true;

        // Bluetooth Classic
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required

        // Bluetooth LE
        config.uuidService = UUID.fromString("e7810a71-73ae-499d-8c15-faa9aef0c3f2"); // Required
        config.uuidCharacteristic = UUID.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f"); // Required
        //config.transport = BluetoothDevice.TRANSPORT_LE; // Required for dual-mode devices
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Used to filter found devices. Set null to find all devices.

        BluetoothService.init(config);
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public ArrayAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter var) {
        this.adapter = var;
    }

}