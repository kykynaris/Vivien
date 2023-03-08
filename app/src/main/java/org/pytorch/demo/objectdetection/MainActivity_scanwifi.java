package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_scanwifi extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static String[] CheckPermission = {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CHANGE_WIFI_STATE};
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiListAdapter wifiListAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scanwifi);

        // Initialize Gson
        gson = new GsonBuilder().setPrettyPrinting().create();

        // Check if Wi-Fi permissions are granted
        Button scanButton = findViewById(R.id.scan_Button);
        scanButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(MainActivity_scanwifi.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity_scanwifi.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity_scanwifi.this, CheckPermission ,PERMISSIONS_REQUEST_CODE);
            } else {
                // Permissions already granted, scan for Wi-Fi networks
                scanWifi();
            }
        });


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiListAdapter = new WifiListAdapter(new ArrayList<>());
        RecyclerView wifiListRecyclerView = findViewById(R.id.wifiListRecyclerView);
        wifiListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wifiListRecyclerView.setAdapter(wifiListAdapter);

    }

    @SuppressLint("MissingPermission")
    private void scanWifi() {
        wifiManager.startScan();

        handler.postDelayed(() -> {
            wifiList = wifiManager.getScanResults();
            wifiListAdapter.updateWifiList(wifiList);
        }, 1000);
        // Export Wi-Fi list to JSON file
        WifiListExporter.exportWifiListToJson( wifiList, "wifilist.json");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, scan for Wi-Fi networks
                scanWifi();
            } else {
                // Permissions denied, show error message and finish the activity
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}