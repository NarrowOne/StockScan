package com.example.stockscan.REST;

import android.util.Log;

import com.example.stockscan.Models.Produce;
import com.example.stockscan.Models.ScanPopup;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class OCRRest extends RestClient{
    private static final String TAG = "OCR REST Client";
    private final ScanPopup popup;
    private final String reqMethod;

    public OCRRest(ScanPopup popup, String reqMethod){
        super(reqMethod);
        this.popup = popup;
        this.reqMethod = reqMethod;
    }

    @Override
    protected void onPreExecute() {
        popup.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String s) {

        if(s == null || s.matches("")) return;

        try {
            JSONObject response = new JSONObject(s);

            if (!response.has("error")) {
                if(response.has("produce_details")){

                    JSONObject prod = response.getJSONObject("produce_details");
                    String produceDetails = prod.toString();
                    popup.addProduce(new Gson().fromJson(produceDetails, Produce.class));

                }else if(response.has("crud_result")){

                    JSONObject result = response.getJSONObject("crud_result");
                    if(result.getBoolean("error")){
                        popup.showError();
                    }else{
                        popup.saved();
                    }

                }
            } else {
                popup.showError();
                Log.e(TAG, String.valueOf(response.get("error")));
            }

            JSONObject log = response.getJSONObject("Log");
            for (Iterator<String> it = log.keys(); it.hasNext(); ) {
                String key = it.next();
                Log.i(TAG, "{\"" + key + "\" : \"" + log.get(key) + "\"}");
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
