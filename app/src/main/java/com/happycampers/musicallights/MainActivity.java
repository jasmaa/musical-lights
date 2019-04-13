package com.happycampers.musicallights;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView outputText;
    TextView inputText;
    Button sendButton;


    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    private String targetName = "ESP32test";
    private BluetoothDevice targetDevice;
    private BluetoothSocket mSocket;

    private final String TAG = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView)findViewById(R.id.output);
        inputText = (TextView) findViewById(R.id.editText);


        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSocket.getOutputStream().write(inputText.getText().toString().getBytes());
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                }
            }
        });


        // check if bluetooth can be used
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null) {
            outputText.setText("Does not support bluetooth");
        }
        else{
            // ask to enable bluetooth
            if(!adapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // find target from paired devices
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceAddr = device.getAddress();

                if(targetName.equals(deviceName)){
                    targetDevice = device;
                }
            }

            if(targetDevice == null){
                outputText.setText(outputText.getText() + "Could not find device\n");
            }
            else{
                outputText.setText(outputText.getText() + "Found device: " + targetDevice.toString() + "\n");

                // make socket connection
                if(targetDevice.getBondState() == targetDevice.BOND_BONDED) {
                    Log.d(TAG, targetDevice.getName());

                    try {
                        mSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, "socket not created");
                        e1.printStackTrace();
                    }
                    try {
                        mSocket.connect();
                    } catch (IOException e) {
                        try {
                            mSocket.close();
                            Log.d(TAG, "Cannot connect");
                        } catch (IOException e1) {
                            Log.d(TAG, "Socket not closed");
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

