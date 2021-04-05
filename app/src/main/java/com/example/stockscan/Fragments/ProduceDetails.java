package com.example.stockscan.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stockscan.MasterActivity;
import com.example.stockscan.Models.Produce;
import com.example.stockscan.R;

//TODO Add image of scan? Scrollable list of tags? Add/Remove custom tags

public class ProduceDetails extends Fragment {

    private Context parent;

    private TextView prodName, prodCode, prodBatch, prodWeight, prodExp;

    public ProduceDetails() {
    }

    public static ProduceDetails newInstance() {
        ProduceDetails fragment = new ProduceDetails();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_produce_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prodName = view.findViewById(R.id.prodName);
        prodCode = view.findViewById(R.id.prodCode);
        prodBatch = view.findViewById(R.id.prodBatch);
        prodWeight = view.findViewById(R.id.prodWeight);
        prodExp = view.findViewById(R.id.prodExpiry);
    }

    @Override
    public void onResume() {
        super.onResume();
        Produce produce = ((MasterActivity)parent).getSelectedProduce();
        String weight = String.format("%.0f%s", produce.getWeight(),"g");

        prodName.setText(produce.getName());
        prodCode.setText(produce.getProduct_code());
        prodBatch.setText(produce.getBatch());
        prodWeight.setText(weight);
        prodExp.setText(produce.getExpiry());

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = context;
    }
}