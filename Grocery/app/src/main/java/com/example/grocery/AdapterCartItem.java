package com.example.grocery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout now_cartItem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.now_cartitem,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, final int position) {
        //get data
        ModelCartItem modelCartItem = cartItems.get(position);
        final String id = modelCartItem.getId();
        String getpId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        final String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceEachTv.setText(""+price);

        //handle remove click listener, delete item from cart
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will create table if not exists, but in that case will must exits
                EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn("Item_Id",new String[]{"text","unique"})
                        .addColumn("Item_PID",new String[]{"text","not null"})
                        .addColumn("Item_Name",new String[]{"text","not null"})
                        .addColumn("Item_Price_Each",new String[]{"text","not null"})
                        .addColumn("Item_Price",new String[]{"text","not null"})
                        .addColumn("Item_Quantity",new String[]{"text","not null"})
                        .doneTableColumn();

                easyDB.deleteRow(1,id);     //column no 1 is item id
                Toast.makeText(context,"Removed from cart...",Toast.LENGTH_SHORT).show();


                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((ShopDetailsActivity)context).allTotalPriceTv.getText().toString().trim().replace("Rs.","")));
                double totalPrice = tx - Double.parseDouble(cost.replace("Rs.",""));
                double deliveryfee = Double.parseDouble((((ShopDetailsActivity)context).deliveryFee.replace("Rs.","")));
                double sTotalPrice = Double.parseDouble(String.format("%.2f",totalPrice)) - Double.parseDouble(String.format("%.2f",deliveryfee));
                ((ShopDetailsActivity)context).allTotalPrice=0.00;
                ((ShopDetailsActivity)context).sTotalTv.setText("Rs."+String.format("%.2f",sTotalPrice));
                ((ShopDetailsActivity)context).allTotalPriceTv.setText("Rs."+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalPrice))));

                //after removing item from cart, update cart count
                ((ShopDetailsActivity)context).cartCount();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    //view holder class
    class HolderCartItem extends RecyclerView.ViewHolder{

        //ui views of now_cartItem.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv,itemRemoveTv;


        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);

        }
    }
}
