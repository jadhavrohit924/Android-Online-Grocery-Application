package com.example.radiobutton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static RadioGroup radio_g;
    private static RadioButton radio_d;
    private static Button button_sbm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onClickListenerButton();
    }
    public void onClickListenerButton()
    {
        radio_g=(RadioGroup)findViewById(R.id.radioGroup);

        button_sbm=(Button)findViewById(R.id.Button);

        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selected_id=radio_g.getCheckedRadioButtonId();
                        radio_d=(RadioButton)findViewById(selected_id);
                        Toast.makeText(MainActivity.this, radio_d.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
