package com.ibm.emergencyapp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tara on 10/26/2017.
 */

public class Util {

    private static final String EXPIRE_DATE_KEY = "EXPIRY_DATE_";
    public static final String TERM_ACCEPTED_KEY = "term_accepted";


    // for debug only
    public static final boolean DISABLE_VERIFICATION = false;
    private static final boolean DEBUG_EXPIRY = false;



    private static SimpleDateFormat iso8601Format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static final String LOG_TAG =
            Util.class.getSimpleName();


    /**
     * Return true of user accepted Term&Conditions on this device before
     * @param sharedPreferences
     * @return
     */

    public static boolean isTermAccepted(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(TERM_ACCEPTED_KEY, false);
    }

    /**
     * Returns true if the user must be email verified
     * @param sharedPreferences
     * @return boolean
     */
    public static boolean isVerificationRequired(SharedPreferences sharedPreferences) {

        if (DISABLE_VERIFICATION) {
            return false;
        }

        Long expireDate = sharedPreferences.getLong(EXPIRE_DATE_KEY, 0);

        Log.d(LOG_TAG, "Expiration date: " + expireDate);

        if (expireDate == 0) {

            // user has not verified
            Log.d(LOG_TAG, "User not verified yet");
            return true;
        }

        long now = (new Date()).getTime();

        Log.d(LOG_TAG, "NOW: " + iso8601Format.format(now));
        Log.d(LOG_TAG, "EXPIRE: " + iso8601Format.format(expireDate));
        Log.d(LOG_TAG, "Account expired?: " + ( now > expireDate));

        return ( now > expireDate );
    }


    public static void setExpireTime(SharedPreferences sharedPreferences) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        Log.d(LOG_TAG, "Email verified time: " + iso8601Format.format(cal.getTimeInMillis()));

        // TODO: Make this 1 year

        if (DEBUG_EXPIRY) {
            cal.add(Calendar.MINUTE, 1);
        } else {
            cal.add(Calendar.YEAR, 1);
        }
        Log.d("Email expire time: " + iso8601Format.format(cal.getTimeInMillis()), LOG_TAG);


        editor.putLong(EXPIRE_DATE_KEY, cal.getTimeInMillis());

        editor.commit();
    }



    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
