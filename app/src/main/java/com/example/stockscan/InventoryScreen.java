package com.example.stockscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.stockscan.Adapters.InventoryAdapter;
import com.example.stockscan.Models.Produce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public class InventoryScreen extends AppCompatActivity {

    private static String TAG = "InventoryScreen";

    private ImageView newScan;
    private RecyclerView invRecycler;
    private InventoryAdapter invAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private  FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_screen);
        db = FirebaseFirestore.getInstance();

        newScan = findViewById(R.id.newScanBtn);
        newScan.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanScreen.class);
            startActivity(intent);
        });

        invRecycler = findViewById(R.id.inventoryRecycler);
        invRecycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        invRecycler.setLayoutManager(layoutManager);


        invAdapter = new InventoryAdapter();
        getStocklist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStocklist();
    }

    private void getStocklist() {
        List<Produce> stockList = new ArrayList<>();

        db.collection("Users")
                .document("Test")
                .collection("Stock")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Log.d(TAG, document.getId() + "=>" + document.getData());

                            String id = document.getId();
                            String prodName = document.get("Name").toString();
                            String prodID = document.get("Product Code").toString();
                            String batch = document.get("Batch").toString();
                            int weight = Integer.parseInt(document.get("Weight").toString());
                            String expiryDate = document.get("Expiry").toString();
                            String[] tags = document.get("Tags").toString().split(",");

                            Produce produce = new Produce(id, prodName, prodID, batch,
                                    weight, expiryDate, tags);

                            stockList.add(produce);
                        }

                        invAdapter.setStockList(stockList);
                        invRecycler.setAdapter(invAdapter);
                    }
                });

    }


}