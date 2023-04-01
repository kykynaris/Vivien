package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_scanwifi extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static String[] CheckPermission = {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CHANGE_WIFI_STATE};
    private WifiManager wifiManager;
    ///////////////////////////////////////////////////////////////
    private List<WifiData> wifiDataList = new ArrayList<>();
    private List<ScanResult> scanResults;
    /////////////////////////////////////////////////////////////////
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

        // Initialize IntenalPath
        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("Package", "Error Package name not found ", e);
        }

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

        scanResults = wifiManager.getScanResults();
        JSONObject wifiJson = new JSONObject();
        try {
            for (ScanResult scanResult : scanResults) {
                if (scanResult.level >= -80) { // filter out RSSI less than -80
                    wifiJson.put(scanResult.BSSID, scanResult.level); // Add each BSSID and RSSI value to the JSONObject
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (ScanResult scanResult : scanResults) {
            WifiData wifiData = new WifiData();
            wifiData.setBssid(scanResult.BSSID);
            wifiData.setSignalStrength(scanResult.level);
            wifiDataList.add(wifiData);
        }

        // Export Wi-Fi list to JSON file
        Exporter.exportWifiListToJson( scanResults, "wifilist.json");
        Exporter.exportWifidataToJson( wifiDataList, "WifiData.json" , wifiJson);
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