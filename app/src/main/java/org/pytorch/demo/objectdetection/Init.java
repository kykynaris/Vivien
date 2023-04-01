package org.pytorch.demo.objectdetection;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Init extends AppCompatActivity implements VoiceControl.TextToSpeechListener {
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;
    private static String[] checkpermission = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO};
    private VoiceControl texttospeech;
    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private List<WifiData> wifiDataList = new ArrayList<>();

    private static final File basedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);;
    private static final String pathdir = basedir.toString() + "/Vivien/fileJson";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, checkpermission, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }

//        TTS
        texttospeech = new VoiceControl(this, this);

//        // file Access folder
//        File directory = getApplicationContext().getFilesDir();
//        File file = new File(directory, "my_folder/my_file.txt");

    }

    @Override
    public void onTextToSpeechReady() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                texttospeech.speak("Hello, my name is Vivian. I am your navigator");
            }
        }, 3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                texttospeech.speak("Vivian would like to check your current position and please hold the phone for a moment.");
            }
        }, 7000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.startScan();
                if (ActivityCompat.checkSelfPermission(Init.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
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
                AssetManager assetManager = getAssets();
                Exporter.exportWifidataToJson( wifiDataList, "WifiData.json" , wifiJson);

                // positioning

                // Load data from wifi.json
                String wifiJsonString = loadJSONFromAsset("wifi.json");
//                String wifiAndroidJsonString = loadJSONFromAsset("wifi_android.json");

               //Load wifidata form external storage in android
                File jsonFile = new File(pathdir, "WifiData.json");
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(jsonFile));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                String line;
                while (true) {
                    try {
                        if (!((line = reader.readLine()) != null)) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stringBuilder.append(line);
                }
                String jsonString = stringBuilder.toString();

                // Parse JSON data
                JsonArray wifiJsonArray = JsonParser.parseString(wifiJsonString).getAsJsonArray();
                JsonObject wifiAndroidJsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                // Extract data from wifiAndroidJsonObject
                List<String> bssidList = new ArrayList<>();
                List<Integer> rssiList = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : wifiAndroidJsonObject.entrySet()) {
                    String bssid = entry.getKey();
                    int rssi = entry.getValue().getAsInt();
                    bssidList.add(bssid);
                    rssiList.add(rssi);
                }
                // Compare RSSI values and extract location name
                String locationName = "Unknown";
                int minRssiDiff = Integer.MAX_VALUE;
                for (JsonElement wifiJsonElement : wifiJsonArray) {
                    JsonObject wifiJsonObject = wifiJsonElement.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : wifiJsonObject.entrySet()) {
                        String location = entry.getKey();
                        JsonObject bssidRssiObject = entry.getValue().getAsJsonObject();
                        int rssiDiffSum = 0;
                        int bssidCount = 0;
                        for (String bssid : bssidList) {
                            if (bssidRssiObject.has(bssid)) {
                                int rssiWifiJson = bssidRssiObject.get(bssid).getAsInt();
                                int rssiWifiAndroid = rssiList.get(bssidList.indexOf(bssid));
                                int rssiDiff = Math.abs(rssiWifiJson - rssiWifiAndroid);
                                rssiDiffSum += rssiDiff;
                                bssidCount++;
                            }
                        }
                        if (bssidCount > 0) {
                            int rssiDiffAvg = rssiDiffSum / bssidCount;
                            if (rssiDiffAvg < minRssiDiff) {
                                minRssiDiff = rssiDiffAvg;
                                locationName = location;
                            }
                        }
                    }
                }

                // Output location name to logcat
                if (locationName != null) {
                    Log.d(TAG, "Location: " + locationName);
                    String finalLocationName = locationName;
                    TextToSpeech texts = new TextToSpeech(Init.this , new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                // Speak the location name
                                texttospeech.speak("Your location is "+finalLocationName);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Location not found");
                    texttospeech.speak("Location Not found");
                }


            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Init.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        }, 12000);
    }

    private String loadJSONFromAsset(String fileName) {
        String jsonString = null;
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonString = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
