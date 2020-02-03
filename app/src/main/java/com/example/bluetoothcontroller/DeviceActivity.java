package com.example.bluetoothcontroller;

import android.animation.ObjectAnimator;
import android.graphics.Matrix;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

public class DeviceActivity extends AppCompatActivity implements BluetoothService.OnBluetoothEventCallback {

    private static final String TAG = "DEVICE_ACTIVITY";
    public static boolean bt_connected = true;
    private TextView t_status;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private ProgressBar speed_progess;
    private ProgressBar coins_progess;
    private TextView coins_val;
    private ImageView robot;
    private ProgressBar coins_progress;
    private ImageView blue_circle;
    private boolean init = false;


    protected MyApp mMyApp;

    private static String coin_count = "0";
    private static String charge_count = "0";
    private static int rot_val = 0;


    BluetoothService service;
    BluetoothWriter writer;
    ArrayAdapter adapter;

    //Send message over bluetooth and add it to the log.
    public void sendBT(String message) {
        //if(bt_connected){
            Log.d(TAG,message);
            MyApp.commandList.add(message);
            writer.writeln(message);
        //}
    }

    //Rotates the robot image
    private void RobotRotate(float degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        robot.startAnimation(rotateAnim);
    }

    private void RobotShift(float shift) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, shift);
        animation.setDuration(0);
        //animation.setRepeatCount(5);
        //animation.setRepeatMode(2);
        animation.setFillAfter(true);
        robot.startAnimation(animation);
    }

    //Configure the onClick event for all the buttons.
    private void configLogButton(){
        Button log_button = (Button) findViewById(R.id.log_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(DeviceActivity.this, LogActivity.class)); //Opens the log activity.
            }
        });
    }

    private void configUpButton(){
        Button log_button = (Button) findViewById(R.id.up_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("up\r");
            }
        });
    }

    private void configDownButton(){
        Button log_button = (Button) findViewById(R.id.down_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("down\r");
            }
        });
    }

    private void configRightButton(){
        Button log_button = (Button) findViewById(R.id.right_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("right\r");
            }
        });
    }

    private void configLeftButton(){
        Button log_button = (Button) findViewById(R.id.left_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("left\r");
            }
        });
    }

    private void configAutoButton(){
        Button log_button = (Button) findViewById(R.id.mode_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("auto\r");
            }
        });
    }

    private void configStopButton(){
        Button log_button = (Button) findViewById(R.id.stop_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("stop\r");
            }
        });
    }

    private void configPickButton(){
        Button log_button = (Button) findViewById(R.id.pick_button);
        log_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                sendBT("pick\r");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_main);

        mMyApp = (MyApp)this.getApplicationContext();

        coins_progress = (ProgressBar) findViewById(R.id.coins_progress);

        coins_val = (TextView) findViewById(R.id.coins);
        robot = (ImageView) findViewById(R.id.model);
        blue_circle = (ImageView) findViewById(R.id.circle);
        robot.setScaleType(ImageView.ScaleType.MATRIX);

        coins_progress.setProgress(0);
        coins_progress.setMax(20);

        adapter = ((MyApp) this.getApplication()).getAdapter();

        t_status = (TextView) findViewById(R.id.status_text);
        t_status.setText(R.string.conn);

        service = BluetoothService.getDefaultInstance();

        writer = new BluetoothWriter(service);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        configLogButton();

        configUpButton();
        configRightButton();
        configLeftButton();
        configDownButton();
        configAutoButton();
        configStopButton();
        configPickButton();

        init = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        service.setOnEventCallback(this);
        mMyApp.setCurrentActivity(this);
    }

    @Override
    public void onDataRead(final byte[] buffer, final int length) {
        //Log.d(TAG, "onDataRead: " + new String(buffer, 0, length));
        final String data =new String(buffer, 0, length);
        MyApp.commandList.add(new String(buffer, 0, length));
        if(MyApp.commandList.size() > 100){
            MyApp.commandList.remove(100);
        }
        adapter.notifyDataSetChanged();
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                char first = data.charAt(0); //Modifies the UI based on the info received over bluetooth.
                if (first == '.') {
                    //Log.d("change1", "WORKS!!!!");
                    String[] values = data.split("\\.");
                    if(values.length >= 6) {
                        //Log.d("change1", "str: "+data);
                        //Log.d("change1", "abc: "+values[0]+values[1]+values[2]+values[3]+values[4]+values[5]);
                        if (values[1].equals("1") && values[2].equals("1") && MyApp.rot_val1 != 0) {
                            //matrix.postRotate((float) 0.0, 0, 0);
                            RobotRotate(0.0f);
                            //RobotShift(60f);
                            MyApp.shift_val2 = MyApp.shift_val1;
                            MyApp.shift_val1 = 1;
                            MyApp.rot_val1  = 0;
                            MyApp.shift_val3 = 1;
                        } else if (values[4].equals("1") && values[1].equals("1") && MyApp.rot_val1  != 1) {
                            RobotRotate(30.0f);
                            MyApp.rot_val1  = 1;
                            MyApp.shift_val2 = MyApp.shift_val1;
                            MyApp.shift_val1 = 0;
                            MyApp.shift_val3 = 1;
                        } else if (values[2].equals("1") && values[3].equals("1") && MyApp.rot_val1  != 2) {
                            RobotRotate(330.0f);
                            MyApp.rot_val1  = 2;
                            MyApp.shift_val2 = MyApp.shift_val1;
                            MyApp.shift_val1 = 0;
                            MyApp.shift_val3 = 1;
                        } else if (values[1].equals("0") && values[2].equals("0") && values[3].equals("0") && MyApp.rot_val1  != 0) {
                            RobotRotate(0.0f);
                            MyApp.rot_val1  = 0;
                            MyApp.shift_val2 = MyApp.shift_val1;
                            MyApp.shift_val1 = 0;
                            MyApp.shift_val3 = 1;
                        }else if (values[4].equals("1") && values[2].equals("0") && values[3].equals("1")) {
                            //RobotRotate(0.0f);
                            RobotShift(-60f);
                            MyApp.rot_val1  = 0;
                            MyApp.shift_val2 = MyApp.shift_val1;
                            MyApp.shift_val1 = -1;
                            MyApp.shift_val3 = 1;
                        }
                        if (!values[5].equals(coin_count)) {
                            //Log.d("change1", "coins: "+values[5]);
                            coin_count = values[5];
                            coins_val.setText(values[5]);
                            coins_progress.setProgress(Integer.parseInt(coin_count));
                        }

                        /*if(MyApp.shift_val3 == 1) {
                            if (MyApp.shift_val1 == 1 && MyApp.shift_val2 == -1) {
                                RobotShift(60f);
                            } else if (MyApp.shift_val1 == -1 && MyApp.shift_val2 == 1) {
                                RobotShift(-60f);
                            }else if (MyApp.shift_val1 == -1 && MyApp.shift_val2 == 0) {
                                RobotShift(-30f);
                            }else if (MyApp.shift_val1 == 1 && MyApp.shift_val2 == 0) {
                                RobotShift(30f);
                            }else if (MyApp.shift_val1 == 0 && MyApp.shift_val2 == 1) {
                                RobotShift(-30f);
                            }
                            else if (MyApp.shift_val1 == 0 && MyApp.shift_val2 == -1) {
                                RobotShift(30f);
                            }
                            MyApp.shift_val3 = 0;
                        }*/

                        //if (!values[6].equals(charge_count)) {
                            //Log.d("change1", "strength: "+values[6]);
                        //    int change = 0;//Integer.parseInt(values[6]);
                        //    float f_change = (float) (change)/300.0f;
                        //    blue_circle.setAlpha(f_change);

                        //}
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.disconnect();
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
        if(status == BluetoothStatus.NONE){
            bt_connected = false;
            //runOnUiThread(new Runnable(){
            //@Override
             //   public void run(){
             //       t_status.setText(R.string.disc);
             //   }
            //});
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
