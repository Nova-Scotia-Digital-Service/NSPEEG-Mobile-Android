package com.ibm.emergencyapp.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ibm.emergencyapp.MainActivity;
import com.ibm.emergencyapp.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private String TAG = "Timers";

    private static final int ONE_DAY_SECONDS = 86400;

    //private static final int ONE_DAY_SECONDS = 30;

    private static final String LOG_TAG =
            NotificationService.class.getSimpleName();


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate(){
        Log.e(TAG, "onCreate");


    }

    @Override
    public void onDestroy(){
        Log.e(TAG, "onDestroy");
        stopTimerTask();
        super.onDestroy();


    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public  void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first day the TimerTask will run every day
        timer.schedule(timerTask, ONE_DAY_SECONDS, ONE_DAY_SECONDS *1000); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        checkExpired();

                    }
                });
            }
        };
    }

    public void checkExpired() {

        // check if account is expired

        if (Util.isVerificationRequired(getSharedPreferences("prefs", 0))) {

            Log.d("Reverification required", LOG_TAG);

            // see https://developer.android.com/training/notify-user/build-notification.html



            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.pns_logo)
                            .setContentTitle(getString(R.string.revalidateTitle))
                            .setContentText(getString(R.string.revalidateText));


            Intent resultIntent = new Intent(this, MainActivity.class);

            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            mBuilder.setContentIntent(resultPendingIntent);


            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.cancelAll();

            // Builds the notification and issues it.
            mNotifyMgr.notify(notificationIdAtomicInt.incrementAndGet(), mBuilder.build());
        }
    }

    // used for notification ID
    private final static AtomicInteger notificationIdAtomicInt = new AtomicInteger(0);

}
