package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {

    //declare ui views
    private ImageView shopIv;
    private TextView shopNameTv,phoneTv,emailTv,openCloseTv,deliveryFeeTv,addressTv,filteredProductsTv,cartCountTv;
    private ImageButton callBtn,mapBtn,cartBtn,backBtn,filterProductBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String shopUid;
    private String myLatitude,myLongitude,myPhone;
    private String shopName, shopPhone, shopEmail, shopAddress, shopLatitude,shopLongitude;
    public String deliveryFee;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductUser adapterProductUser;

    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        //init ui views
        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv =findViewById(R.id.cartCountTv);


        //init pro dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //get uid of the shop from the intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = firebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();


        //declare it to class level and init in onCreate
        easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn("Item_Id",new String[]{"text","unique"})
                .addColumn("Item_PID",new String[]{"text","not null"})
                .addColumn("Item_Name",new String[]{"text","not null"})
                .addColumn("Item_Price_Each",new String[]{"text","not null"})
                .addColumn("Item_Price",new String[]{"text","not null"})
                .addColumn("Item_Quantity",new String[]{"text","not null"})
                .doneTableColumn();


        //each shop has its own products and orders so if user add items to cart and go back and open cart in different shop then cart shound be different
        //so delete cart data whenever user open this activity
        deleteCartData();
        cartCount();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goto previous activity
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show cart dialog
                showCartDialog();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get item selected
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadShopProducts();
                                }
                                else {
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
    }

    private void deleteCartData() {
        easyDB.deleteAllDataFromTable(); //delete all records from cart
    }

    public void cartCount(){
        //public to access in adapter
        //get cart count
        int count=easyDB.getAllData().getCount();
        if(count <= 0){
            //no items in cart, hide cart count
            cartCountTv.setVisibility(View.GONE);
        }
        else {
            //have items in cart, show cart count
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);
        }
    }

    public double allTotalPrice = 0.00;
    public TextView sTotalTv,dFeeTv,allTotalPriceTv;
    private void showCartDialog() {
        //init list
        cartItemList = new ArrayList<>();

        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
         sTotalTv = view.findViewById(R.id.sTotalTv);
         dFeeTv = view.findViewById(R.id.dFeeTv);
         allTotalPriceTv = view.findViewById(R.id.totalTv);
        final RecyclerView cartItemsRv = view.findViewById(R.id.cartItemRv);
        Button checkOutBtn = view.findViewById(R.id.checkOutBtn);

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set views to dialog
        builder.setView(view);

        shopNameTv.setText(shopName);

        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn("Item_Id",new String[]{"text","unique"})
                .addColumn("Item_PID",new String[]{"text","not null"})
                .addColumn("Item_Name",new String[]{"text","not null"})
                .addColumn("Item_Price_Each",new String[]{"text","not null"})
                .addColumn("Item_Price",new String[]{"text","not null"})
                .addColumn("Item_Quantity",new String[]{"text","not null"})
                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartItemList.add(modelCartItem);
        }
        //setup adapter
        adapterCartItem = new AdapterCartItem(this,cartItemList);
        //set to recyclerView
        cartItemsRv.setAdapter(adapterCartItem);


        dFeeTv.setText("Rs."+deliveryFee);
        sTotalTv.setText("Rs."+String.format("%.2f",allTotalPrice));
        allTotalPriceTv.setText("Rs."+(allTotalPrice + Double.parseDouble(deliveryFee.replace("Rs.",""))));

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //reset total price on dialog dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

        //place order
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address
                if(myLatitude.equals("")||myLatitude.equals("null")||myLongitude.equals("")||myLongitude.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this,"Please enter address in your profile before placing order",Toast.LENGTH_SHORT).show();
                    return; //don't procede further
                }
                //first validate phone number
                if(myPhone.equals("")||myPhone.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this,"Please enter phone number in your profile before placing order",Toast.LENGTH_SHORT).show();
                    return; //don't procede further
                }

                if(cartItemList.size() == 0){
                    Toast.makeText(ShopDetailsActivity.this,"Cart is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });

    }

    private void submitOrder() {
        //show progress dialog
        progressDialog.setMessage("Placing order...");
        progressDialog.show();


        //for order id and time
        final String timestamp = ""+System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("Rs.","");

        //add latitude, longitude of users to each order | delete previous order from firebase or add manually to them

        //setup order data
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","In Progress"); //In progress/completed/canceled
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("latitude",""+myLatitude);
        hashMap.put("longitude",""+myLongitude);

        //add to db
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //order info added now add order items
                        for(int i=0; i<cartItemList.size(); i++){
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String,String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId",pId);
                            hashMap1.put("id",id);
                            hashMap1.put("cost",cost);
                            hashMap1.put("name",name);
                            hashMap1.put("price",price);
                            hashMap1.put("quantity",quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this,"Order placed successfully...",Toast.LENGTH_SHORT).show();

                        //after placing orders open order details page
                        //open order details, we need two keys, orderId, OrderTo
                        Intent intent = new Intent(ShopDetailsActivity.this,OrderDetailsUserActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timestamp);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void dialPhone(){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this,""+shopPhone,Toast.LENGTH_SHORT).show();
    }

    private void openMap() {
        String address = "https://maps.google.com/maps?saddr="+myLatitude + "," + myLongitude + "&daddr=" + shopLatitude + "," + shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){

                            // get user data
                            String name=""+ds.child("name").getValue();
                            String email=""+ds.child("email").getValue();
                            myPhone=""+ds.child("phone").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            String city=""+ds.child("city").getValue();
                            myLatitude=""+ds.child("latitude").getValue();
                            myLongitude=""+ds.child("longitude").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get shop data
                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                shopEmail = ""+dataSnapshot.child("email").getValue();
                shopPhone = ""+dataSnapshot.child("phone").getValue();
                shopAddress = ""+dataSnapshot.child("address").getValue();
                shopLatitude = ""+dataSnapshot.child("latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("longitude").getValue();
                 deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopOpen = ""+dataSnapshot.child("shopOpen").getValue();

                //set data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Delivery Fee: Rs."+deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if(shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }
                else {
                    openCloseTv.setText("Close");
                }
                try{
                        Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadShopProducts() {
        //init list
        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //clear list before adding items
                        productsList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProducts modelProduct = ds.getValue(ModelProducts.class);
                            productsList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        //set adapter
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
