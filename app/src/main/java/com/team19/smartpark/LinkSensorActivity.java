package com.team19.smartpark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LinkSensorActivity extends AppCompatActivity {

    private static final String TAG = "LinkSensorActivity";
    private ProgressBar spinner;
    private WifiManager wifiManager;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;
    private TextView instructionText;
    private TextView ssidText;
    private TextView passwordText;
    private Button configButton;
    private ScanResult espwifi;
    //set up the wifi scanner
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            List<ScanResult> results = wifiManager.getScanResults();
            for (ScanResult result :
                    results) {
            }
            //filter wifi results with only ESP compatible ones else if no result put null
            espwifi = results.stream()
                    .filter(scanResult -> scanResult.SSID.startsWith("ESP")).findAny().orElse(null);

            // if a result is found
            if (espwifi != null) {
                // stop scanning and prompt the user to input the wifi credentials to be passed
                unregisterReceiver(this);
                instructionText.setText("Device found ! \n Please enter the wifi credentials to configure your sensor");
                ssidText.setVisibility(View.VISIBLE);
                passwordText.setVisibility(View.VISIBLE);
                configButton.setVisibility(View.VISIBLE);

            }
        }

    };
    private String parkingPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sensor);
        // get the parking spot path from previous activity from the intent
        Intent intent = getIntent();
        parkingPath = intent.getStringExtra("parkingPath");
        // bind all layout objects to java objects
        spinner = findViewById(R.id.progressBar);
        instructionText = findViewById(R.id.instructionText);
        ssidText = findViewById(R.id.ssidField);
        ssidText.setVisibility(View.GONE);
        passwordText = findViewById(R.id.passowordField);
        passwordText.setVisibility(View.GONE);
        configButton = findViewById(R.id.configButton);
        configButton.setVisibility(View.GONE);

        // when the link button is pressed send the config to the sensor
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                instructionText.setText("Sending Configuration to sensor");
                sendConfig(espwifi, parkingPath, ssidText.getText().toString(), passwordText.getText().toString());
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanWifi();


    }

    private void scanWifi() {

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Log.d(TAG, "scanWifi: scanning wifi");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cm != null) {
            cm.unregisterNetworkCallback(networkCallback);
        }
        Log.d(TAG, "onDestroy: network session ended");
    }


    /**
     * Establish connection with the ESP board and send to it the config file
     *
     * @param esp32
     * @param parkingPath
     * @param pSSID
     * @param pPassword
     */
    private void sendConfig(ScanResult esp32, String parkingPath, String pSSID, String pPassword) {
        boolean done = false;
        //connect to the ESP wifi securely
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "scanSuccess: found potential esp");
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(esp32.SSID);
        builder.setWpa2Passphrase("welcome123");

        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
        // set up the netweork once connected
        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
        NetworkRequest networkRequest = networkRequestBuilder.build();

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // when the network is available we send the config through an http post req
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                HttpURLConnection urlConnection = null;
                // set up the http requestion and connection
                try {
                    urlConnection = (HttpURLConnection) network.openConnection(new URL("http://192.168.4.1/config"));//esp);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                    urlConnection.setRequestProperty("Accept", "*/*");
                    urlConnection.setDoOutput(true);
                    //create the json object with all the config settings to be sent
                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("config", true);
                    jsonInput.put("ssid", pSSID);
                    jsonInput.put("password", pPassword);
                    jsonInput.put("path", parkingPath);
                    String jsonString = jsonInput.toString().replace("\\/", "/");
                    // send (post) the config
                    urlConnection.connect();

                    //write the body of the request
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    // get the response of the request
                    // if response is successfull
                    if (urlConnection.getResponseCode() == 200) {
                        //run on UI thread to let the user know  config sent successfully
                        runOnUiThread(() -> {
                            instructionText.setText("Sensor Configured successfully");
                            spinner.setVisibility(View.GONE);
                            ssidText.setVisibility(View.GONE);
                            passwordText.setVisibility(View.GONE);
                            configButton.setVisibility(View.GONE);
                        });
                        cm.unregisterNetworkCallback(networkCallback);
                        //if request is not successful
                    } else if (urlConnection.getResponseCode() == 400) {
                        runOnUiThread(() -> {
                            instructionText.setText("You entered invalid credentials");
                        });
//                        cm.unregisterNetworkCallback(networkCallback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                super.onAvailable(network);
            }
        };

        //bin network call back
        cm.requestNetwork(networkRequest, networkCallback);
    }

}