package com.example.stockscan.REST;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;

public class RestClient {
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        new AsyncHttpClient().get(url, params, responseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        new AsyncHttpClient().get(url, params, responseHandler);
    }
}
