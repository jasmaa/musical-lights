package com.happycampers.musicallights;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Android main
 */
public class MainActivity extends AppCompatActivity {

    private TextView outputText;
    private TextView inputText;
    private Button reconnectButton;
    private ProgressBar progressBar;
    private ArrayList<Button> modeButtons;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String targetName = "ESP32test";
    private BluetoothDevice targetDevice;
    private BluetoothSocket mSocket;

    private final String TAG = "TEST";

    /**
     * Runs on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // widgets
        outputText = (TextView)findViewById(R.id.output);
        outputText.setMovementMethod(new ScrollingMovementMethod());

        // set button listeners
        modeButtons = new ArrayList<Button>();
        modeButtons.add((Button) findViewById(R.id.mode0Btn));
        modeButtons.add((Button) findViewById(R.id.mode1Btn));
        modeButtons.add((Button) findViewById(R.id.mode2Btn));
        modeButtons.add((Button) findViewById(R.id.mode3Btn));
        modeButtons.add((Button) findViewById(R.id.mode4Btn));
        modeButtons.add((Button) findViewById(R.id.mode5Btn));
        modeButtons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {0};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });
        modeButtons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {1};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });
        modeButtons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {2};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });
        modeButtons.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {3};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });
        modeButtons.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {4};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });
        modeButtons.get(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {5};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to steam\n");
                }
            }
        });

        reconnectButton = (Button) findViewById((R.id.reconnectBtn));
        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BTConnectTask().execute();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // try to connect to bluetooth
        new BTConnectTask().execute();
    }

    /**
     * Runs on destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Toggle UI
     * @param value
     */
    private void toggleInput(boolean value){
        for(Button btn : modeButtons){
            btn.setEnabled(value);
        }
        reconnectButton.setEnabled(value);
    }

    /**
     * Async bluetooth connection task
     */
    private class BTConnectTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            toggleInput(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;

            // check if bluetooth can be used
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if(adapter == null) {
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

                    if(targetName.equals(device.getName())){
                        targetDevice = device;
                    }
                }

                if(targetDevice == null){
                }
                else{
                    // make socket connection
                    if(targetDevice.getBondState() == targetDevice.BOND_BONDED) {
                        Log.d(TAG, targetDevice.getName());
                        try {
                            mSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                            success = true;
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

            try {
                Thread.sleep(2000);
            }
            catch (Exception e){

            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            outputText.append(result ? "Connected!" : "Failed to connect\n");
            progressBar.setVisibility(View.INVISIBLE);
            toggleInput(true);
        }
    }
}

