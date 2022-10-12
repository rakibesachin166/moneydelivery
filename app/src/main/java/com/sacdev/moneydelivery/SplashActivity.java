package com.sacdev.moneydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
       starterclass.firebaseAuth = FirebaseAuth.getInstance();
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user!=null){
                    starterclass.selfuserid = user.getUid();
                    startActivity(starterclass.changeActivity(SplashActivity.this,MainActivity.class));

                }else{
                    startActivity(starterclass.changeActivity(SplashActivity.this,LoginActivity.class));
                }
                finish();
            }

        },3000);
         }
}