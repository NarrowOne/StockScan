package com.example.stockscan.Models;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockscan.R;
import com.example.stockscan.REST.OCRRest;
import com.example.stockscan.REST.RestClient;
import com.google.gson.Gson;

import androidx.cardview.widget.CardView;

import java.util.HashMap;

public class ScanPopup {
    private static final String TAG = "ScanPopup";
    private final Context context;
    private Produce produce;

    private CardView popupContent, popupError, popupButtons;
    private TextView prodName, prodCode, prodBatch, prodWeight, prodExp;
    private ProgressBar progressBar;
    private Dialog dialog;

    private Button cancel, saveProd;

    public ScanPopup(Context context) {
        this.context = context;

        setDialog();
        setViews();
        setLoading();
    }

    private void setDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_layout);
    }

    private void setViews() {
        popupContent = dialog.findViewById(R.id.popupContent);
        popupError = dialog.findViewById(R.id.popupError);
        popupButtons = dialog.findViewById(R.id.popupButtons);

        prodName = dialog.findViewById(R.id.prodName);
        prodCode = dialog.findViewById(R.id.prodCode);
        prodBatch = dialog.findViewById(R.id.prodBatch);
        prodWeight = dialog.findViewById(R.id.prodWeight);
        prodExp = dialog.findViewById(R.id.prodExpiry);
        cancel = dialog.findViewById(R.id.retryScan);
        saveProd = dialog.findViewById(R.id.saveProd);
        progressBar = dialog.findViewById(R.id.progressBar);

        cancel.setOnClickListener(l-> dismiss());
        saveProd.setOnClickListener(l ->{
          saveScannedProduct();
        });
    }

    private void setLoading() {
        prodName.setText("Loading...");
        popupContent.setVisibility(View.INVISIBLE);
        popupButtons.setVisibility(View.GONE);
        popupError.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }


    public void addProduce(Produce produce){
        if(produce == null) return;

        this.produce = produce;

        prodName.setText(produce.getName());
        prodCode.setText(produce.getProduct_code());
        prodBatch.setText(produce.getBatch());
        prodWeight.setText(String.format("%.0f", produce.getWeight()));
        prodExp.setText(produce.getExpiry());

        progressBar.setVisibility(View.GONE);
        popupContent.setVisibility(View.VISIBLE);
    }

    private void saveScannedProduct(){
        RestClient rest = new OCRRest(this, "POST");
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "save");
        data.put("produce_details", new Gson().toJson(produce));

        rest.execute(data);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        clearFields();
        dialog.dismiss();
    }

    private void clearFields() {
        prodName.setText(R.string.produce_name_placeholder);
        prodCode.setText(R.string.product_code);
        prodBatch.setText(R.string.product_batch);
        prodWeight.setText(R.string.product_weight);
        prodExp.setText(R.string.product_expiry_date);
    }

    public void saved() {
        Toast.makeText(context, "Produce saved!", Toast.LENGTH_LONG).show();
    }

    public void showError() {
        popupContent.setVisibility(View.INVISIBLE);
        popupError.setVisibility(View.VISIBLE);
        popupButtons.setVisibility(View.VISIBLE);
        saveProd.setVisibility(View.GONE);
    }
}
