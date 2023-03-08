package org.pytorch.demo.objectdetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity_scanwifi extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1001;

    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiListAdapter wifiListAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scanwifi);

        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(MainActivity_scanwifi.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity_scanwifi.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_CODE);
            } else {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
