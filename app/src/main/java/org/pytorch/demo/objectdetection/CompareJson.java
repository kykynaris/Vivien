package org.pytorch.demo.objectdetection;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class CompareJson {
//    private JSONObject Jsonfile;

//    public CompareJson(InputStream inputStream) throws JSONException, IOException {
//        byte[] buffer = new byte[inputStream.available()];
//        inputStream.read(buffer);
//        inputStream.close();
//        String json = new String(buffer, "UTF-8");
//        Jsonfile = new JSONObject(json);
//    }
//
//    public boolean compare(String key, String expectedValue) throws JSONException {
//        return Jsonfile.optString(key).equals(expectedValue);
//    }
//
//    public boolean compare(String key, int expectedValue) throws JSONException {
//        return Jsonfile.optInt(key) == expectedValue;
//    }
    private Context context;
    public CompareJson(Context context) {
        this.context = context;
    }
    private JSONObject getJsonFromAsset(String fileName) {
        String jsonString;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
        e.printStackTrace();
        return null;
        }

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
        e.printStackTrace();
        return null;
        }
    }

    private JSONObject getJsonFromInternal(String fileName) {
        String jsonString;
        try {
            InputStream inputStream = context.openFileInput(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

