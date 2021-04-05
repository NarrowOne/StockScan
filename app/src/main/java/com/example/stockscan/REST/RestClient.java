package com.example.stockscan.REST;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stockscan.BuildConfig;
import com.example.stockscan.Models.ScanPopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public abstract class RestClient extends AsyncTask<HashMap<String, Object>, String, String> {
    private static final String TAG = "REST Client";
    String urlPath = "https://us-central1-stockscan-2d0f2.cloudfunctions.net/stockscan-test";
    String reqMethod;

    public RestClient(String reqMethod){
        this.reqMethod = reqMethod;
    }


    @SafeVarargs
    @Override
    protected final String doInBackground(HashMap<String, Object>... hashMaps) {
        HttpURLConnection connection;
        OutputStreamWriter outWriter = null;
        DataOutputStream outData;
        BufferedReader inReader = null;
        HashMap<String, Object> data = hashMaps[0];
        StringBuilder response = new StringBuilder();

        String encodedData = getJson(data);


        try {
            URL url = new URL(urlPath);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(reqMethod);

            if (BuildConfig.DEBUG && !connection.getRequestMethod().equals(reqMethod)) {
                throw new AssertionError("Assertion failed");
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

//                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("Accept", "application/x-www-form-urlencoded");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);

            outData = new DataOutputStream(connection.getOutputStream());
            outData.writeBytes(encodedData);
            int responseCode = connection.getResponseCode();
            inReader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8094);

            String line;

            while ((line = inReader.readLine()) != null) {
                response.append(line);
            }

            connection.disconnect();
            inReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outWriter != null) {
                try {
                    outWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return response.toString();
    }

    private String getJson(HashMap<String, Object> data) {
        String json = "{\n";

        int keyCount = 0;
        for(String key : data.keySet()){
            json += "\""+key+"\" : \""+data.get(key)+"\"";
            if(keyCount != data.size()-1)
                json += ",\n";
            else
                json += "\n}";

            keyCount++;
        }

        return json;
    }

    @Override
    protected abstract void onPreExecute();
    @Override
    protected abstract void onProgressUpdate(String... values);
    @Override
    protected abstract void onPostExecute(String s);
}
