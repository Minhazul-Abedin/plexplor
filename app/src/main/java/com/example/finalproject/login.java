package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class login extends AppCompatActivity {
    EditText txt_email,txt_pass;
    Button login,fb,forgot,sign;
    LazyLoader lazyLoader;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email=(EditText) findViewById(R.id.txt_email);
        txt_pass=(EditText) findViewById(R.id.txt_password);
        login=(Button)findViewById(R.id.login);
        fb=(Button)findViewById(R.id.fb);
        forgot=(Button)findViewById(R.id.forgot);
        sign=(Button)findViewById(R.id.sign);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user !=null){
            finish();
            startActivity(new Intent(login.this,navigation.class));
        }
        lazyLoader=(LazyLoader)findViewById(R.id.progg);
        LazyLoader loader = new LazyLoader(this, 30, 20, ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected));
        loader.setAnimDuration(500);
        loader.setFirstDelayDuration(100);
        loader.setSecondDelayDuration(200);
        loader.setInterpolator(new LinearInterpolator());

        lazyLoader.addView(loader);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa=new Intent(login.this,forgot.class);
                startActivity(aa);
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reff=new Intent(login.this,registration.class);
                startActivity(reff);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=txt_email.getText().toString().trim();
                String pass=txt_pass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(login.this,"Please Enter Email",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(login.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                    return;
                }
                lazyLoader.setVisibility(View.VISIBLE);



                firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                lazyLoader.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    finish();
                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(getApplicationContext(),navigation.class));
                                } else {
Toast.makeText(login.this,"Wrong Username Or Password",Toast.LENGTH_LONG).show();
                                }

                                // ...
                            }
                        });

            }
        });
    }
}
