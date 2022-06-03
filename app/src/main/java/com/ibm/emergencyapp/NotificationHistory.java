package com.ibm.emergencyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import com.ibm.emergencyapp.adapters.historyAdapter;
import com.ibm.emergencyapp.persistence.Notification;
import com.ibm.emergencyapp.storage.SQLiteHelper;
import com.ibm.emergencyapp.util.NotificationItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nigel on 06/25/2018.
 */

public class NotificationHistory extends CustomActionBarActivity {

    Context context;

    RecyclerView history;
    private ArrayList<NotificationItems> dataItems = new ArrayList<>();
    private ArrayList<NotificationItems> dataItemsCopy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setupAdapter();
    }

    public void setupAdapter(){
        final SQLiteHelper sQLiteHelper = new SQLiteHelper(NotificationHistory.this);
        dataItems = sQLiteHelper.getItinerariesItems();
        dataItemsCopy = new ArrayList<>();

        //gets the data a week ago from today
        Date weekAgo = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekAgo);
        cal.add(Calendar.DATE, -7);
        weekAgo = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(weekAgo);
        try {
            weekAgo = df.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(NotificationItems item: dataItems){
            Date date = weekAgo;
            //convert date in db to date datatype
            try {
                date = df.parse(item.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // checks if the date from db is after or equal to date from a week ago
            if(date.after(weekAgo) || date.equals(weekAgo)){
                dataItemsCopy.add(item);
            }
        }
        Collections.reverse(dataItemsCopy);
        history = findViewById(R.id.notificationHistory);
        history.setLayoutManager(new LinearLayoutManager(context));
        history.setAdapter(new historyAdapter(dataItemsCopy,context));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notification_history;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setupAdapter();
    }
}
