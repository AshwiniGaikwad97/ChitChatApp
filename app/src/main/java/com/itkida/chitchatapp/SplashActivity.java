package com.itkida.chitchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth auth = FirebaseAuth.getInstance();
               //startActivity(new Intent(SplsashActivity.this, LoginPhoneNumberActivity.class));
               Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if(auth.getCurrentUser()==null){
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
               startActivity(intent);
                finish();

            }
        },2000);
    }
}