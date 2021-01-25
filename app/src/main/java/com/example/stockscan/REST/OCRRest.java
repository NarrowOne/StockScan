package com.example.stockscan.REST;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class OCRRest extends RestClient{
    private static final String TAG = "OCR REST Client - ";
    String url = "https://us-central1-stockscan-2d0f2.cloudfunctions.net/parse-image";

    public OCRRest(){

    }

    public void post(String encodedString, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.add("image_data", encodedString);
        post(url, params, responseHandler);
    }
}
