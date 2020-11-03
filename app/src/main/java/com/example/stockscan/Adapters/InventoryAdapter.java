package com.example.stockscan.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stockscan.Models.Produce;
import com.example.stockscan.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    List<Produce> stockList;

    public InventoryAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.inventory_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String prodName = stockList.get(position).getName();

        holder.setItemName(prodName);
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void setStockList(List<Produce> stockList) {
        this.stockList = stockList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName;

        public ViewHolder(View v){
            super(v);
            itemName = v.findViewById(R.id.nameDisplay);
        }
        void setItemName(String name){
            itemName.setText(name);
        }
    }
}
