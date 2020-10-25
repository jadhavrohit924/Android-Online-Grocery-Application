package com.example.grocery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterUserOrderedItem extends RecyclerView.Adapter<AdapterUserOrderedItem.HolderOrderedItems>{

    private Context context;
    private ArrayList<ModelOrderedUser> orderedUserArrayList;

    public AdapterUserOrderedItem(Context context, ArrayList<ModelOrderedUser> orderedUserArrayList) {
        this.context = context;
        this.orderedUserArrayList = orderedUserArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.now_ordereditems,parent,false);
        return new HolderOrderedItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItems holder, int position) {
        //get data at position
        ModelOrderedUser modelOrderedUser = orderedUserArrayList.get(position);
        String getpId = modelOrderedUser.getpId();
        String name = modelOrderedUser.getName();
        String cost = modelOrderedUser.getCost();
        String price = modelOrderedUser.getPrice();
        String quantity = modelOrderedUser.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("Rs."+price);
        holder.itemPriceTv.setText("Rs."+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
    }

    @Override
    public int getItemCount() {
        return orderedUserArrayList.size();
    }

    //view holder class
    class HolderOrderedItems extends RecyclerView.ViewHolder{

        //views of now_ordereditems.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;

        public HolderOrderedItems(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
