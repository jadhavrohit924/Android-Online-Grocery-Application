package com.example.saline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static Button add_saline;
    private static TextView setTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick()
    {
        add_saline=(Button)findViewById(R.id.button);
        setTxt=(TextView)findViewById(R.id.textView);

        add_saline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTxt.toString().s;
                    }
                }
        );
    }
}
