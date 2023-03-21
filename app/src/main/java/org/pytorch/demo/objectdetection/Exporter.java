package org.pytorch.demo.objectdetection;

import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exporter {

    private static final String TAG = "WifiListExporter";
    private String BSSID_text;
    private String SSID_text;
    private int level_rssi;
    private int Epochtime;
    private static String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String pathdir = basedir + "/Documents/Vivien/fileJson";

    public static boolean exportWifiListToJson(List<ScanResult> wifiList, String fileName) {
        // Convert the WiFi list to JSON format
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(wifiList);

        // Save the JSON string to a file in internal storage
        try {
            File file = new File( pathdir , fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.close();
            Log.i(TAG, "WiFi list exported to file: " + file.getAbsolutePath());
            return true;
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportWifidataToJson(List<WifiData> wifiDataList , String fileName) {
        // Convert the WiFi list to JSON format
        Gson gson = new Gson();
        String jsonString = gson.toJson(wifiDataList);

        // Save the JSON string to a file in internal storage
        try {
            File file = new File( pathdir , fileName);
            FileWriter fileWriter2 = new FileWriter(file , false);
            fileWriter2.write(jsonString);
            fileWriter2.close();
            Log.i(TAG, "WiFi list exported to file: " + file.getAbsolutePath());
            return true;
        }catch (JsonIOException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportObjectToJson(ArrayList<Result> object, String fileName) {
        // Convert the WiFi list to JSON format
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(object);

        // Save the JSON string to a file in internal storage
        try {
            File file = new File( pathdir , fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.close();
            return true;
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}