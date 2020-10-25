package com.example.recycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private final AppCompatActivity activity = MainActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutContact;
    private TextInputLayout textInputLayoutAddress;
    private TextInputLayout textInputLayoutId;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextContact;
    private TextInputEditText textInputEditTextAddress;
    private TextInputEditText textInputEditTextId;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewList;

    private InputValidation inputValidation;
    private Data data;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initObjects();
        initListeners();
    }

    //Initialize Views
    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutContact = (TextInputLayout) findViewById(R.id.textInputLayoutContact);
        textInputLayoutAddress = (TextInputLayout) findViewById(R.id.textInputLayoutAddress);
        textInputLayoutId = (TextInputLayout) findViewById(R.id.textInputLayoutId);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextContact = (TextInputEditText) findViewById(R.id.textInputEditTextContact);
        textInputEditTextAddress = (TextInputEditText) findViewById(R.id.textInputEditTextAddress);
        textInputEditTextId = (TextInputEditText) findViewById(R.id.textInputEditTextId);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewList = (AppCompatTextView) findViewById(R.id.appCompatTextViewList);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewList.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        inputValidation = new InputValidation(activity);
        data = new Data();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                break;

            case R.id.appCompatTextViewList:
                Intent accountsIntent = new Intent(activity, ListActivity.class);
                accountsIntent.putExtra("ID", textInputEditTextId.getText().toString().trim());
                accountsIntent.putExtra("NAME", textInputEditTextName.getText().toString().trim());
                accountsIntent.putExtra("CONTACT", textInputEditTextContact.getText().toString().trim());
                accountsIntent.putExtra("ADDRESS", textInputEditTextAddress.getText().toString().trim());
                emptyInputEditText();
                startActivity(accountsIntent);
                break;
        }
    }

    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextContact.setText(null);
        textInputEditTextAddress.setText(null);
    }
}