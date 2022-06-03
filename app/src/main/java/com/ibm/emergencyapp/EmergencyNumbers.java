package com.ibm.emergencyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Nigel on 06/25/2018.
 */

public class EmergencyNumbers extends CustomActionBarActivity {

    ImageView healthCall, poisonCall, qEIICall, policeCall, mentalHealthCall, stormCallGov,
            stormCall, nSPowerCall1, nSPowerCall2, metroNumber, LAENumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        metroNumber = findViewById(R.id.metro_imageview);
        metroNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024245400"));
                startActivity(intent);
            }
        });

        LAENumber = findViewById(R.id.Lae_ImageView);
        LAENumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18009522687"));
                startActivity(intent);
            }
        });

        healthCall = findViewById(R.id.HealthLink_ImageView);
        healthCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:811"));
                startActivity(intent);
            }
        });

        poisonCall = findViewById(R.id.PoisonControl_ImageView);
        poisonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024288161"));
                startActivity(intent);
            }
        });

        qEIICall = findViewById(R.id.QEII_ImageView);
        qEIICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024732700"));
                startActivity(intent);
            }
        });

        policeCall = findViewById(R.id.police_ImageView);
        policeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024905020"));
                startActivity(intent);
            }
        });

        mentalHealthCall = findViewById(R.id.MentalHelath_ImageView);
        mentalHealthCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18884298167"));
                startActivity(intent);
            }
        });

        stormCallGov = findViewById(R.id.StormLine_ImageView);
        stormCallGov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024246045"));
                startActivity(intent);
            }
        });

        stormCall = findViewById(R.id.stormLine_ImageView2);
        stormCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18774246045"));
                startActivity(intent);
            }
        });

        nSPowerCall1 = findViewById(R.id.NSPower_ImageView);
        nSPowerCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18004286230"));
                startActivity(intent);
            }
        });

        nSPowerCall2 = findViewById(R.id.NSPower_ImageView2);
        nSPowerCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9024286230"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_emergency_numbers;
    }
}
