package com.ibm.emergencyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // Wait for a bit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

        // Start home activity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // close splash activity
        finish();

    }
}
