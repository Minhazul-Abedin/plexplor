package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot extends AppCompatActivity {

    EditText fmail;
    Button resend;
    LazyLoader lazyLoader;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);


        fmail=(EditText)findViewById(R.id.forgot_email);
        resend=(Button)findViewById(R.id.resend);
        firebaseAuth=FirebaseAuth.getInstance();
        lazyLoader=(LazyLoader)findViewById(R.id.progg);
        LazyLoader loader = new LazyLoader(this, 30, 20, ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected));
        loader.setAnimDuration(500);
        loader.setFirstDelayDuration(100);
        loader.setSecondDelayDuration(200);
        loader.setInterpolator(new LinearInterpolator());

        lazyLoader.addView(loader);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String useremail=fmail.getText().toString().trim();
                if(useremail.equals("")){
                    Toast.makeText(forgot.this,"Please enter your registerd email ",Toast.LENGTH_SHORT).show();
                }
                else {
                    lazyLoader.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            lazyLoader.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(forgot.this,"Password reset email sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgot.this,login.class));
                            }
                            else{
                                Toast.makeText(forgot.this,"Error in sending password reset",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });



    }
}
