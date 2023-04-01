package org.pytorch.demo.objectdetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

public class CompareJson {
    private static final String TAG = "comparejson";
    private static String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String pathdir = basedir + "/Documents/Vivien/fileJson/";

    public static void Comparethejson(AssetManager assetManager) throws IOException {

        // Read asset file

        String AssetReader = null;
        try {
            InputStream is = assetManager.open("MacAddress.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            AssetReader = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        InputStream assetInput = Files.newInputStream(Paths.get(pathdir + "RSSI.json"));
//        InputStreamReader assetReader = new InputStreamReader(assetInput);

        // Read internal file
        InputStream internalInput = Files.newInputStream(Paths.get(pathdir + "WifiData.json"));
        InputStreamReader internalReader = new InputStreamReader(internalInput);

        // Parse asset JSON
        Gson gson = new Gson();
        JsonElement assetElement = gson.fromJson(AssetReader, JsonElement.class);
        JsonObject assetObject = assetElement.getAsJsonObject();
        JsonElement assetValue = assetObject.get("key");

        // Parse internal JSON
        JsonElement internalElement = gson.fromJson(internalReader, JsonElement.class);
        JsonObject internalObject = internalElement.getAsJsonObject();
        JsonElement internalValue = internalObject.get("key");

        // Define the threshold for the MSE
        double threshold = 0.1;

        // Compute the MSE
        double mse = 0;
        for (String key : assetObject.keySet()) {
            if (internalObject.has(key)) {
                double diff = assetObject.getAsDouble() - internalObject.getAsDouble();
                mse += diff * diff;
            }
        }
        mse /= assetObject.size();

        // Compare the MSE with the threshold
        if (mse <= threshold) {
            System.out.println("The test data is close to the actual data.");
        } else {
            System.out.println("The test data is different from the actual data.");
        }


        // Compare values
        JsonObject similarJson = new JsonObject();
        for (String key : assetObject.keySet()) {
            if (internalObject.has(key)) {
                // Compare the values of the keys
                if (assetObject.get(key).equals(internalObject.get(key))) {
                    // If the values are equal, add the key-value pair to the similarJson object
                    similarJson.add(key, internalObject.get(key));
                }
            }
        }
        try
        {
            File file = new File(pathdir, "wificompare.json");
            FileWriter fileWriter2 = new FileWriter(file, false);
            fileWriter2.write(String.valueOf(similarJson));
            fileWriter2.close();
            Log.i(TAG, "WifiCompare exported to file: " + file.getAbsolutePath());
        }catch(JsonIOException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(AssetManager assetManager) throws JSONException, IOException {

        // Define file paths
        String AssetReader = null;
        try {
            InputStream is = assetManager.open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            AssetReader = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String file1Path = AssetReader;
        String file2Path = pathdir + "WifiData.json";

        // Read the contents of the files as strings
//        String file1Content = new String(Files.readAllBytes(Paths.get(file1Path)));
        String file2Content = new String(Files.readAllBytes(Paths.get(file2Path)));

        // Parse the contents into JSON arrays
        JSONArray file1 = new JSONArray(file1Path);
        JSONArray file2 = new JSONArray(file2Content);

        // Loop through the objects in file1 array
        for (int i = 0; i < file1.length(); i++) {
            JSONObject file1Obj = file1.getJSONObject(i);

            // Loop through the objects in file2 array
            for (int j = 0; j < file2.length(); j++) {
                JSONObject file2Obj = file2.getJSONObject(j);

                // Check if any of the BSSID and RSSI values in file1Obj match those in file2Obj
                boolean matchFound = false;
                for (Iterator<String> it = file1Obj.keys(); it.hasNext(); ) {
                    String key = it.next();
                    if (key.equals("Location")) {
                        continue;
                    }
                    if (file2Obj.has(key) && file2Obj.getString(key).equals(file1Obj.getString(key))) {
                        matchFound = true;
                        break;
                    }
                }

                // If a match was found, output the Location value from file1Obj and exit the loop
                if (matchFound) {
                    System.out.println(file1Obj.getString("Location"));
                    Log.i("output","output of match -->" + file1Obj.getString("Location"));
                    break;
                }
            }
        }
    }

}

