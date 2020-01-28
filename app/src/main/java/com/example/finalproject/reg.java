package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class reg extends AppCompatActivity {
Button bt_reg,bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        bt_reg=(Button)findViewById(R.id.bt_reg);
        bt_login=(Button)findViewById(R.id.bt_login);
        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rnk=new Intent(reg.this,registration.class);
                startActivity(rnk);
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lnn=new Intent(reg.this,login.class);
                startActivity(lnn);
            }
        });
    }
}
