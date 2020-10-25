package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    private String orderBy,orderId,orderTo;

    private String phone,latitude,longitude;

    private String myLatitude,myLongitude;

    //ui views
    private ImageButton backBtn,callBtn,mapBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,nameTv,totalItemsTv,amountTv,addressTv;
    private RecyclerView itemsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedSeller> orderedSellerArrayList;
    private AdapterSellerOrderedItems adapterSellerOrderedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        nameTv = findViewById(R.id.nameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);

        Intent intent =getIntent();
        orderBy = intent.getStringExtra("orderBy");//orderTo contains uid of the shop where we placed orders
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadBuyerInfo();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call to buyer
                dialPhone();
            }
        });


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load maps from seller to buyer
                openMaps();
            }
        });
    }



    private void openMaps() {
        String address = "https://maps.google.com/maps?saddr="+myLatitude + "," + myLongitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(phone))));
        Toast.makeText(this,""+phone,Toast.LENGTH_SHORT).show();
    }


    private void loadBuyerInfo() {
        //get buyer info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = ""+dataSnapshot.child("name").getValue();
                        phone = ""+dataSnapshot.child("phone").getValue();
                        nameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopInfo() {
        //get shop info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myLatitude = ""+dataSnapshot.child("latitude").getValue();
                        myLongitude = ""+dataSnapshot.child("longitude").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrderDetails() {
        //load order details

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //get data
                        String orderBy = ""+dataSnapshot.child("orderBy").getValue();
                        String orderCost = ""+dataSnapshot.child("orderCost").getValue();
                        String orderId = ""+dataSnapshot.child("orderId").getValue();
                        String orderStatus = ""+dataSnapshot.child("orderStatus").getValue();
                        String orderTime = ""+dataSnapshot.child("orderTime").getValue();
                        String orderTo = ""+dataSnapshot.child("orderTo").getValue();
                       // String deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                        latitude = ""+dataSnapshot.child("latitude").getValue();
                        longitude = ""+dataSnapshot.child("longitude").getValue();

                        //convert timestamp to proper format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString(); //eg 22/05/2020 20:15 PM

                        if(orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else if(orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        }
                        else if(orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        //set data
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("Rs."+orderCost); // + "[Including delivery fee Rs." +deliveryFee +"]");
                        dateTv.setText(formatedDate);


                        findAddress(latitude,longitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        //find address, country, city, state
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lon,1);

            String address = addresses.get(0).getAddressLine(0);//complete address
            addressTv.setText(address);
        }
        catch (Exception e){

        }
    }

    private void loadOrderedItems() {
        //init list
        orderedSellerArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedSellerArrayList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderedSeller modelOrderedSeller = ds.getValue(ModelOrderedSeller.class);
                            //add to list
                            orderedSellerArrayList.add(modelOrderedSeller);
                        }
                        //all items added to list
                        //setup adapter
                        adapterSellerOrderedItems = new AdapterSellerOrderedItems(OrderDetailsSellerActivity.this,orderedSellerArrayList);
                        //set adapter
                        itemsRv.setAdapter(adapterSellerOrderedItems);

                        //set items count
                        totalItemsTv.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
