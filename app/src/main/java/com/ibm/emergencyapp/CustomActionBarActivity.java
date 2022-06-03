package com.ibm.emergencyapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

/**
 * Adds the custom action bar with the NS logo
 *
 * Created by tara on 10/30/2017.
 *
 * Modified by chao on 12/15/2018.
 * 1. Made class abstract
 * 2. add abstract method getLayourResourceID
 * 3. Created two new classes: setupActionBard and setupSeekbar
 * 4. Overrided two classes: onResume and onOptionItemSelected
 *
 */

abstract class CustomActionBarActivity extends AppCompatActivity {

    SeekBar seekBar;
    int trackedProgress = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        setupActionBar();
        setupSeekbar();
    }

    protected abstract int getLayoutResourceId();

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        View view = getLayoutInflater().inflate(R.layout.action_bar_view, null);
        int color = getResources().getColor(R.color.white);
        //actionBar.setBackgroundDrawable(new ColorDrawable(R.color.white) );
        actionBar.setBackgroundDrawable(new ColorDrawable(color) );
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(view, layout);
    }

    //method set up seekbar on activities that extend from customActionBarActivity
    private void setupSeekbar() {

        seekBar = findViewById(R.id.slide_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int alpha = (progress * (255 / 100));
                seekBar.getThumb().setAlpha(255 - alpha);
                if (progress < 5){
                    seekBar.setProgress(4);
                }
                if (progress > 95){
                    seekBar.setProgress(95);
                }
                if(progress > trackedProgress + 20){
                    seekBar.setProgress(4);
                    trackedProgress = 4;
                }
                else{
                    trackedProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                trackedProgress = seekBar.getProgress();
                if (seekBar.getProgress() < 90) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar));
                    seekBar.setProgress(4);
                } else {
                    seekBar.setProgress(100);
                    seekBar.getThumb().setAlpha(0);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:911"));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        seekBar.setProgress(4);
        seekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
