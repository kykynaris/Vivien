package org.pytorch.demo.objectdetection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class CompareJson {
    private JSONObject Jsonfile;

    public CompareJson(InputStream inputStream) throws JSONException, IOException {
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        String json = new String(buffer, "UTF-8");
        Jsonfile = new JSONObject(json);
    }

    public boolean compare(String key, String expectedValue) throws JSONException {
        return Jsonfile.optString(key).equals(expectedValue);
    }

    public boolean compare(String key, int expectedValue) throws JSONException {
        return Jsonfile.optInt(key) == expectedValue;
    }
}

