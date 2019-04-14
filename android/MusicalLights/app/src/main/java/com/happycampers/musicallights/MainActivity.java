package com.happycampers.musicallights;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.jar.Manifest;

/**
 * Android main
 */
public class MainActivity extends AppCompatActivity {

    private TextView outputText;
    private TextView inputText;
    private Button reconnectButton;
    private Button colorPickerButton;
    private ProgressBar progressBar;
    private ArrayList<Button> modeButtons;

    private int currentColor = Color.WHITE;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String targetName = "ESP32test";
    private final String TAG = "TEST";

    private BluetoothDevice targetDevice;
    private BluetoothSocket mSocket;
    private BluetoothA2dp a2dp;
    private Visualizer visualizer;
    private byte[] audioBuffer;

    /**
     * Runs on start up
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if coming back from a solid
        Intent intent = getIntent();
        if(intent.hasExtra("color")){
            currentColor = Integer.parseInt(intent.getStringExtra("color"));
        }

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
        modeButtons.add((Button) findViewById(R.id.mode6Btn));

        modeButtons.get(0).setOnClickListener(new View.OnClickListener() {
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
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    byte red = (byte)((currentColor >> 16) & 0xff);
                    byte green = (byte)((currentColor >>  8) & 0xff);
                    byte blue = (byte)((currentColor) & 0xff);

                    // get rid of 0 byte
                    if(red == 0){
                        red = 1;
                    }
                    if(green == 0){
                        green = 0;
                    }
                    if(blue == 0){
                        blue = 1;
                    }

                    byte[] msg = {2, red, green, blue};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(2).setOnClickListener(new View.OnClickListener() {
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
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    byte red = (byte)((currentColor >> 16) & 0xff);
                    byte green = (byte)((currentColor >>  8) & 0xff);
                    byte blue = (byte)((currentColor) & 0xff);

                    // get rid of 0 byte
                    if(red == 0){
                        red = 1;
                    }
                    if(green == 0){
                        green = 0;
                    }
                    if(blue == 0){
                        blue = 1;
                    }

                    byte[] msg = {4, red, green, blue};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(4).setOnClickListener(new View.OnClickListener() {
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
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {6};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to stream\n");
                }
            }
        });
        modeButtons.get(6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] msg = {7};
                    if(mSocket == null){
                        outputText.append("No socket connection\n");
                        return;
                    }
                    mSocket.getOutputStream().write(msg);
                }
                catch(IOException e){
                    Log.d(TAG, "Could not write to stream");
                    outputText.append("Could not write to stream\n");
                }
            }
        });

        // reconnect button
        reconnectButton = (Button) findViewById((R.id.reconnectBtn));
        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BTConnectTask().execute();
            }
        });

        // color picker button
        colorPickerButton = (Button) findViewById((R.id.colorPickerBtn));
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ColorSlidersActivity.class);
                myIntent.putExtra("color", currentColor+"");
                MainActivity.this.startActivity(myIntent);

                if(mSocket != null){
                    try{
                        mSocket.close();
                    }
                    catch(IOException e){
                        Log.d(TAG, "Could not write to stream");
                        outputText.append("Could not close stream\n");
                    }
                }

                finish();
            }
        });

        // set progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // request permissions for visualizer
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // try to connect to bluetooth
        new BTConnectTask().execute();
        //new SendAudioTask().execute();
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    visualizer = new Visualizer(0);
                    visualizer.setEnabled(true);
                    audioBuffer = new byte[50];
                    //visualizer.getWaveForm(audioBuffer);
                    //outputText.append(audioBuffer.toString());

                    // new SendAudioTask().execute();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    */

    /**
     * Runs on destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //visualizer.release();
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
        colorPickerButton.setEnabled(value);
    }

    private class SendAudioTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            while(true){

                if(visualizer == null){
                    continue;
                }

                visualizer.getWaveForm(audioBuffer);

                /*
                try{
                    mSocket.getOutputStream().write(audioBuffer);
                }
                catch (IOException e){
                    Log.d(TAG, "Could not write to stream");
                }
                */
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
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

                /*
                // Get a2dp proxy
                BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
                        if (profile == BluetoothProfile.A2DP) {
                            a2dp = (BluetoothA2dp) proxy;
                            Log.d(TAG,"Bluetooth A2DP: " + a2dp);
                            Log.d(TAG,"Proxy: "+proxy);
                            Log.d(TAG,"Devices: " + a2dp.getConnectedDevices());
                        }
                    }
                    public void onServiceDisconnected(int profile) {
                        if (profile == BluetoothProfile.A2DP) {
                            a2dp = null;
                        }
                    }
                };
                adapter.getProfileProxy(MainActivity.this, mProfileListener, BluetoothProfile.A2DP);
                */
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            outputText.append(result ? "Connected!\n" : "Failed to connect\n");
            progressBar.setVisibility(View.INVISIBLE);
            toggleInput(true);
        }
    }

}

