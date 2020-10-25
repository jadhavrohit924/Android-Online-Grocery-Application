package com.example.registrationpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;

public class MainActivity extends AppCompatActivity {
    private static EditText email;
    private static EditText pass;
    private static Button btn;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=(EditText)findViewById(R.id.Email);
        pass=(EditText)findViewById(R.id.password);
        btn=(Button)findViewById(R.id.register);
        auth=FirebaseAuth.getInstance();
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text_email=email.getText().toString();
                        String text_pass=pass.getText().toString();

                        if(TextUtils.isEmpty(text_email )|| TextUtils.isEmpty(text_pass)){
                            Toast.makeText(MainActivity.this,"Empty Email or Password",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            register(text_email,text_pass);
                        }

                    }
                }
        );
    }

    private void register(String text_email, String text_pass) {
        auth.createUserWithEmailAndPassword(text_email,text_pass).addOnCompleteListener( this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            Toast.makeText(MainActivity.this,"Registering sucessful",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Registration Failed!",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
