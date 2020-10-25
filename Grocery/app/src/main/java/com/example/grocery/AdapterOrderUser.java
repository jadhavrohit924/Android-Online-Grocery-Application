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

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser>{

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.now_order_user,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        //get data
        ModelOrderUser modelOrderUser = orderUserList.get(position);
        final String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        final String orderTo = modelOrderUser.getOrderTo();
        String orderTime = modelOrderUser.getOrderTime();

        //get shop info
        loadShopInfo(modelOrderUser,holder);

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
                //open order details, we need two keys, orderId, OrderTo
                Intent intent = new Intent(context,OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, final HolderOrderUser holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = ""+dataSnapshot.child("shopName").getValue();
                        holder.shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    //view holder class
    class HolderOrderUser extends RecyclerView.ViewHolder{

        //views of layout
        private TextView orderIdTv,dateTv,shopNameTv,amountTv,statusTv;

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            //init views of layout
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
