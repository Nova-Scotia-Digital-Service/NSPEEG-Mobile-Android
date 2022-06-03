package com.ibm.emergencyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ibm.emergencyapp.adapters.historyAdapter;
import com.ibm.emergencyapp.persistence.Notification;
import com.ibm.emergencyapp.persistence.NotificationDAO;
import com.ibm.emergencyapp.storage.SQLiteHelper;


public class NotificationActivity extends AppCompatActivity {


    private NotificationDAO dao;
    private SQLiteHelper sqLiteHelper;

    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // Create notification dao
        dao = NotificationDAO.getInstance(getApplicationContext());
        sqLiteHelper = new SQLiteHelper(getApplicationContext());

        Intent intent = getIntent();
        this.notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView textView = findViewById(R.id.notification_activity_content);
        TextView dateView = findViewById(R.id.notification_activity_title);

        if(historyAdapter.fromHistory){
            historyAdapter.fromHistory = false;
            textView.setText(historyAdapter.description);
            dateView.setText(historyAdapter.date);
        }
        else {
            Notification note = dao.get(this.notificationId);

            if (note == null) {
                // go to parent activity
                finish();
                return;
            }

            textView.setText(note.content);
            textView.invalidate();

            dateView.setText(note.date);
            dateView.invalidate();
        }

        //noteView.invalidate();
    }

    public void dismiss(View view) {
        sqLiteHelper.delete(notificationId);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
