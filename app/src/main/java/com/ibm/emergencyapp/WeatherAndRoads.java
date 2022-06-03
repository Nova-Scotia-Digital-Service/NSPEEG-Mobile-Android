package com.ibm.emergencyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Nigel on 06/25/2018.
 */

public class WeatherAndRoads extends CustomActionBarActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_weather_and_roads;
    }

    public void roadsNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:18774246045"));
        startActivity(intent);
    }

    public void roadsSite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.novascotia.ca"));
        startActivity(browserIntent);
    }


}
