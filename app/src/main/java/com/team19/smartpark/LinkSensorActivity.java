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
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: new scan");
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            Log.d(TAG, "onReceive: " + success);
            List<ScanResult> results = wifiManager.getScanResults();
            for (ScanResult result :
                    results) {
                Log.d(TAG, "onReceive: " + result.SSID);
            }
            espwifi = results.stream()
                    .filter(scanResult -> scanResult.SSID.startsWith("ESP")).findAny().orElse(null);
            Log.d(TAG, "onReceive: " + espwifi);
            if (espwifi != null) {
                unregisterReceiver(this);
                instructionText.setText("Device found ! \n Please enter the wifi credentials to configure your sensor");
                ssidText.setVisibility(View.VISIBLE);
                passwordText.setVisibility(View.VISIBLE);
                configButton.setVisibility(View.VISIBLE);

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sensor);

        spinner = findViewById(R.id.progressBar);

        instructionText = findViewById(R.id.instructionText);
        ssidText = findViewById(R.id.ssidField);
        ssidText.setVisibility(View.GONE);
        passwordText = findViewById(R.id.passowordField);
        passwordText.setVisibility(View.GONE);
        configButton = findViewById(R.id.configButton);
        configButton.setVisibility(View.GONE);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                instructionText.setText("Sending Configuration to sensor");
                sendConfig(espwifi);
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

    private void sendConfig(ScanResult esp32) {
        boolean done = false;
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "scanSuccess: found potential esp");
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(esp32.SSID);
        builder.setWpa2Passphrase("12345678");

        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
        NetworkRequest networkRequest = networkRequestBuilder.build();

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                //Use this network object to Send request.
                //eg - Using OkHttp library to create a service request
                //Service is an OkHttp interface where we define docs. Please read OkHttp docs
                HttpURLConnection urlConnection = null;
                try {
                    Log.d(TAG, "onAvailable: sending config");
                    urlConnection = (HttpURLConnection) network.openConnection(new URL("http://192.168.4.1/config"));//esp);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                    urlConnection.setRequestProperty("Accept", "*/*");
                    urlConnection.setDoOutput(true);
                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("config", true);
                    jsonInput.put("ssid", "dlink");
                    jsonInput.put("password", "12345678");
                    jsonInput.put("path", "/users/parkings/id/spots/L023");
                    String jsonString = jsonInput.toString();
                    urlConnection.connect();
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        Log.d(TAG, "onAvailable: sending");
                        byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                        Log.d(TAG, "onAvailable: should be sent now");
                    }

                    Log.d(TAG, "onAvailable: " + urlConnection.getResponseCode());
                    if (urlConnection.getResponseCode() == 200) {
                        runOnUiThread(() -> {
                            instructionText.setText("Sensor Configured successfully");
                            spinner.setVisibility(View.GONE);
                            ssidText.setVisibility(View.GONE);
                            passwordText.setVisibility(View.GONE);
                            configButton.setVisibility(View.GONE);
                        });
                        cm.unregisterNetworkCallback(networkCallback);
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


        cm.requestNetwork(networkRequest, networkCallback);
    }

}