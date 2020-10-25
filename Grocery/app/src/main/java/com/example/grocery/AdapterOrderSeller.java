package com.example.grocery;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderSeller>{

    private Context context;
    private ArrayList<ModelOrderSeller> orderSellerList;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderSellerList) {
        this.context = context;
        this.orderSellerList = orderSellerList;
    }

    @NonNull
    @Override
    public HolderOrderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.now_order_seller,parent,false);
        return new HolderOrderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderSeller holder, int position) {
        //get data
        ModelOrderSeller modelOrderSeller = orderSellerList.get(position);
        final String orderId = modelOrderSeller.getOrderId();
        final String orderBy = modelOrderSeller.getOrderBy();
        String orderCost = modelOrderSeller.getOrderCost();
        String orderStatus = modelOrderSeller.getOrderStatus();
        final String orderTo = modelOrderSeller.getOrderTo();
        String orderTime = modelOrderSeller.getOrderTime();

        //get user info
        loadUserInfo(modelOrderSeller,holder);

        //set data
        holder.amountTv.setText("Amount: Rs"+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderId:"+orderId);
        //change order status change color
        if(orderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if(orderStatus.equals("Completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else if(orderStatus.equals("Cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        //convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formateDate = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.dateTv.setText(formateDate);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details, we need three keys, orderId, orderBy,orderTo
                Intent intent = new Intent(context,OrderDetailsSellerActivity.class);
                intent.putExtra("orderBy",orderBy);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });

    }

    private void loadUserInfo(ModelOrderSeller modelOrderSeller, final HolderOrderSeller holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderSeller.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = ""+dataSnapshot.child("name").getValue();
                        holder.nameTv.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderSellerList.size();
    }

    //view holder class
    class HolderOrderSeller extends RecyclerView.ViewHolder{

        //views of layout
        private TextView orderIdTv,dateTv,nameTv,amountTv,statusTv;

        public HolderOrderSeller(@NonNull View itemView) {
            super(itemView);

            //init views of layout
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
