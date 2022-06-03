package com.ibm.emergencyapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Nigel on 06/25/2018.
 *
 * Modified by chao on 12/15/2018.
 */

public class SocialMedia extends CustomActionBarActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_socal_media;
    }

    public void govWebsite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.novascotia.ca/connect/"));
        startActivity(browserIntent);
    }

    public void hrmWebsite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.halifax.ca/home/social-media"));
        startActivity(browserIntent);
    }

}
