package com.example.stockscan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stockscan.MasterActivity;
import com.example.stockscan.Models.Produce;
import com.example.stockscan.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private List<Produce> stockList;
    private Context parent;

    public InventoryAdapter(Context parent){
        this.parent = parent;
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

        holder.getLayout().setOnClickListener(l->{
            ((MasterActivity)parent).changeFrag("prod_details");
            ((MasterActivity)parent).setSelectedProduce(stockList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void setStockList(List<Produce> stockList) {
        this.stockList = stockList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout layout;
        private final TextView itemName;

        public ViewHolder(View v){
            super(v);
            layout = v.findViewById(R.id.layout);
            itemName = v.findViewById(R.id.nameDisplay);
        }
        void setItemName(String name){
            itemName.setText(name);
        }
        ConstraintLayout getLayout(){return layout;}
    }
}
