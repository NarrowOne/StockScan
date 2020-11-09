package com.example.stockscan.Models;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;

public class ScanPopup {
    private static final String TAG = "ScanPopup";

    private CardView popup;
    private Button cancel, saveProd;
    private TextView prodName, prodCode, prodBatch, prodWeight, prodExp;

    public ScanPopup(CardView popup, Button cancel,
                     Button saveProd, TextView prodName,
                     TextView prodCode, TextView prodBatch,
                     TextView prodWeight, TextView prodExp) {
        this.popup = popup;
        this.cancel = cancel;
        this.saveProd = saveProd;
        this.prodName = prodName;
        this.prodCode = prodCode;
        this.prodBatch = prodBatch;
        this.prodWeight = prodWeight;
        this.prodExp = prodExp;

        popup.setOnClickListener(l-> popup.setVisibility(View.GONE));
        saveProd.setOnClickListener(l ->{
//          saveScannedProduct();
            popup.setVisibility(View.GONE);
        });
    }

    public void addProduce(Produce produce){
        popup.setVisibility(View.VISIBLE);

        if(produce == null) return;

        prodName.setText(produce.getName());
        prodCode.setText(produce.getProdCode());
        prodBatch.setText(produce.getBatch());
        prodWeight.setText(String.format("%.0f", produce.getWeight()));
        prodExp.setText(produce.getExpiryDate());
    }

        private void saveScannedProduct(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        double weight = Double.parseDouble(prodWeight.getText().toString()) *1000;

        Map<String, Object> scannedDetails = new HashMap<>();
        scannedDetails.put("Name", prodName.getText().toString());
        scannedDetails.put("Product Code", prodCode.getText().toString());
        scannedDetails.put("Weight", String.format("%.0f", weight));
        scannedDetails.put("Batch", prodBatch.getText().toString());
        scannedDetails.put("Expiry", prodExp.getText().toString());
//        scannedDetails.put("Tags", "Meat, Pork");

        db.collection("Users")
                .document("Test")
                .collection("Stock")
                .add(scannedDetails)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
//                        Toast.makeText(context, "Product Recorded", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e(TAG, "Error recording product", task.getException());
                    }
                });
    }

    public CardView getPopup() {
        return popup;
    }

    public void setPopup(CardView popup) {
        this.popup = popup;
    }

    public Button getCancel() {
        return cancel;
    }

    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }

    public Button getSaveProd() {
        return saveProd;
    }

    public void setSaveProd(Button saveProd) {
        this.saveProd = saveProd;
    }

    public TextView getProdName() {
        return prodName;
    }

    public void setProdName(TextView prodName) {
        this.prodName = prodName;
    }

    public TextView getProdCode() {
        return prodCode;
    }

    public void setProdCode(TextView prodCode) {
        this.prodCode = prodCode;
    }

    public TextView getProdBatch() {
        return prodBatch;
    }

    public void setProdBatch(TextView prodBatch) {
        this.prodBatch = prodBatch;
    }

    public TextView getProdWeight() {
        return prodWeight;
    }

    public void setProdWeight(TextView prodWeight) {
        this.prodWeight = prodWeight;
    }

    public TextView getProdExp() {
        return prodExp;
    }

    public void setProdExp(TextView prodExp) {
        this.prodExp = prodExp;
    }
}
