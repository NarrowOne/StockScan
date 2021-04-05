package com.example.stockscan.REST;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stockscan.Adapters.InventoryAdapter;
import com.example.stockscan.Models.Produce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryRest extends RestClient{
    private static final String TAG = "InventoryRest";

    private final RecyclerView view;
    private final InventoryAdapter invAdapter;

    public InventoryRest(String reqMethod, RecyclerView recyclerView, InventoryAdapter invAdapter) {
        super(reqMethod);
        this.view = recyclerView;
        this.invAdapter = invAdapter;
    }

    @Override
    protected void onPreExecute() {

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
                if(response.has("products")){
                    List<Produce> stockList = new ArrayList<>();
                    JSONObject productsJson = response.getJSONObject("products");
                    Iterator<String> keys = productsJson.keys();

                    while (keys.hasNext()){
                        String id = keys.next();
                        JSONObject productJson = productsJson.getJSONObject(id);
                        Produce produce = new Gson().fromJson(productJson.toString(), Produce.class);
                        produce.setiD(id);
                        stockList.add(produce);
                    }
                    invAdapter.setStockList(stockList);
                    view.setAdapter(invAdapter);
                }
            } else {
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
