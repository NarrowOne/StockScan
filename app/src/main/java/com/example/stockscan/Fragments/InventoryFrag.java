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
import com.example.stockscan.REST.InventoryRest;
import com.example.stockscan.REST.RestClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryFrag extends Fragment {

    private static String TAG = "InventoryFrag";

    private Context parent;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent);
        invRecycler.setLayoutManager(layoutManager);


        invAdapter = new InventoryAdapter(parent);
        getStocklist();
    }

    @Override
    public void onResume() {
        super.onResume();
        invAdapter = new InventoryAdapter(parent);

        getStocklist();
    }

    private void getStocklist() {
        RestClient client = new InventoryRest("POST", invRecycler, invAdapter);
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "get_all");
        client.execute(data);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.parent = context;
    }
}