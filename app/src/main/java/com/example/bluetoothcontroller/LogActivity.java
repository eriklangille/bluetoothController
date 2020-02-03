package com.example.bluetoothcontroller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.icu.util.Output;
import android.util.Log;
import android.view.WindowManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;


public class LogActivity extends AppCompatActivity {

    private static final String TAG = "LOG_ACTIVITY";
    BluetoothWriter writer;
    BluetoothService service;
    ArrayAdapter adapter;

    public void sendBT(String message) {
        Log.d(TAG,message);
        MyApp.commandList.add(message);
        writer.writeln(message);
    }

    private void configBackButton(){
        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configSendButton(){
        final OutputStream out;
        Button send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                EditText edit_text = (EditText) findViewById(R.id.editText);
                String content = edit_text.getText().toString();
                if(!content.equals("")){
                    Log.d(TAG,"MSG: "+content);
                    sendBT(content);
                    edit_text.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        configBackButton();
        configSendButton();

        service = BluetoothService.getDefaultInstance();
        writer = new BluetoothWriter(service);

        adapter = ((MyApp) this.getApplication()).getAdapter();//new ArrayAdapter<String>(this,R.layout.activity_listview,DeviceActivity.commandList);

        ListView listView = (ListView) findViewById(R.id.list_log);
        listView.setAdapter(adapter);

        //MainActivity.commandList

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}
