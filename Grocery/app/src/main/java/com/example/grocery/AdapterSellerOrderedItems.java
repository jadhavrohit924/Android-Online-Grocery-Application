package com.example.grocery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterSellerOrderedItems extends RecyclerView.Adapter<AdapterSellerOrderedItems.HolderSellerOrderedItems>{

    private Context context;
    private ArrayList<ModelOrderedSeller> orderedSellerList;

    public AdapterSellerOrderedItems(Context context, ArrayList<ModelOrderedSeller> orderedSellerList) {
        this.context = context;
        this.orderedSellerList = orderedSellerList;
    }

    @NonNull
    @Override
    public HolderSellerOrderedItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.now_ordereditemsseller,parent,false);
        return new HolderSellerOrderedItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellerOrderedItems holder, int position) {
        //get data at position
        ModelOrderedSeller modelOrderedSeller = orderedSellerList.get(position);
        String getpId = modelOrderedSeller.getpId();
        String name = modelOrderedSeller.getName();
        String cost = modelOrderedSeller.getCost();
        String price = modelOrderedSeller.getPrice();
        String quantity = modelOrderedSeller.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("Rs."+price);
        holder.itemPriceTv.setText("Rs."+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
    }

    @Override
    public int getItemCount() {
        return orderedSellerList.size();
    }

    //view holder class
    class HolderSellerOrderedItems extends RecyclerView.ViewHolder{

        //views of now_ordereditemsseller.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;

        public HolderSellerOrderedItems(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
