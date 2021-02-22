package com.example.stockscan.REST;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stockscan.Models.ScanPopup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

public class OCRRest extends AsyncTask<HashMap<String, Object>, String, String> {
    private static final String TAG = "OCR REST Client - ";
    String urlPath = "https://us-central1-stockscan-2d0f2.cloudfunctions.net/parse-image";
    private ScanPopup popup;
    private HashMap<String, Object> responseMap;
    private int responseLineNum;


    public OCRRest(ScanPopup popup){
        this.popup = popup;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        responseMap = new HashMap<>();
        responseLineNum = 0;
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
        if(data.containsKey("image_data")) {
            String imageData = (String) data.get("image_data");
            String encodedData = "{\"image_data\" : \""+imageData+"\"}";

            try {
                URL url = new URL(urlPath);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

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
        }else{
            response.append("error");
        }
        return response.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        responseMap.put(String.valueOf(responseLineNum), values[0]);
        responseLineNum++;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Gson gson = new Gson();
        JSONObject response = gson.fromJson(s, JSONObject.class);
        try {
            if (!response.has("error")) {

                for (Iterator<String> it = response.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Log.d(TAG, "{\"" + key + "\" : \"" + response.get(key) + "\"}");
                }

            } else {
                Log.e(TAG, String.valueOf(response.get("error")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void post(String encodedString, AsyncHttpResponseHandler responseHandler) {
//        RequestParams params = new RequestParams();
//        params.add("image_data", encodedString);
////        post(url, params, responseHandler);
//    }
//
//    public void post(String encodedString){
//        String json = "{\"image_data\" : \""+encodedString+"\"}";
//        post(url, "application/json", json);
//    }
}
