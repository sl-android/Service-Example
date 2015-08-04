package com.example.kimjungsu.sample2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";

    public static final boolean SCAN_RECO_ONLY = true;

    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;

    public static final boolean DISCONTINUOUS_SCAN = false;

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //If a user device turns off bluetooth, request to turn it on.
        //사용자가 블루투스를 켜도록 요청합니다.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        findViewById(R.id.toggleButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ToggleButton toggle = (ToggleButton) v;

                if(toggle.isChecked() == true){

                    Intent intent = new Intent(MainActivity.this, RECOBackgroundMonitoringService.class);
                    startService(intent);
                }
                else{
                    stopService(new Intent(MainActivity.this, RECOBackgroundMonitoringService.class));
                }
            }
        });
    }
}
