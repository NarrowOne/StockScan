package com.example.stockscan.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stockscan.Adapters.InventoryAdapter;
import com.example.stockscan.MasterActivity;
import com.example.stockscan.Models.Produce;
import com.example.stockscan.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventoryFrag extends Fragment {

    private static String TAG = "InventoryFrag";

    private Context context;

    private RecyclerView invRecycler;
    private InventoryAdapter invAdapter;

    private FirebaseFirestore db;

    public InventoryFrag() {
        // Required empty public constructor
    }

    public static InventoryFrag newInstance() {
        return new InventoryFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        ImageView newScan = view.findViewById(R.id.newScanBtn);
        newScan.setOnClickListener(l -> ((MasterActivity)getActivity()).changeFrag("scan"));

        invRecycler = view.findViewById(R.id.inventoryRecycler);
        invRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        invRecycler.setLayoutManager(layoutManager);


        invAdapter = new InventoryAdapter();
        getStocklist();
    }

    @Override
    public void onResume() {
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}