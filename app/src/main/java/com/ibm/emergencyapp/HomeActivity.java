package com.ibm.emergencyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ibm.emergencyapp.persistence.Notification;
import com.ibm.emergencyapp.persistence.NotificationDAO;
import com.ibm.emergencyapp.storage.SQLiteHelper;
import com.ibm.emergencyapp.util.NotificationService;
import com.ibm.emergencyapp.util.Util;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import okio.Okio;

import static com.ibm.emergencyapp.util.Constants.PREF_NAME_VERIFICATION;
import static com.ibm.emergencyapp.util.Constants.VERIFICATION_TOKEN;
import static com.ibm.emergencyapp.util.Constants.VERIFICATION_TOKEN_EXPIRY;

/**
 * Created by tara on 10/17/2017.
 */

public class HomeActivity extends AppCompatActivity {


    SeekBar seekBar;
    boolean notify = false;
    int trackedProgress = 4;

    View noteView;
    ViewGroup layout;

    private static final String LOG_TAG =
            HomeActivity.class.getSimpleName();

    private ExecutorService mExecutor;
    AuthorizationService authService;
    // Client Push SDK
    private MFPPush push = null;

    private NotificationDAO dao;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();

    public static final String EXTRA_MESSAGE = "com.ibm.emergencyapp.extra.MESSAGE";
    public static final String LOGIN_MESSAGE = "Please try again, if problem persists, please contact admin.";

    private final List<String> valid_auth_sources = Arrays.asList("tests.novascotia.ca", "sts.novascotia.ca", "ststest.nshealth.ca", "sts.nshealth.ca");
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_grid);

        mExecutor = Executors.newSingleThreadExecutor();
        authService = new AuthorizationService(this);
        seekBar = findViewById(R.id.slide_seekbar);
        sharedPreferences = getSharedPreferences(PREF_NAME_VERIFICATION, MODE_PRIVATE);


        AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
        if (ex != null) {
            //TODO implement logic in case of error
            callUnauthorized("Unable to refresh your login. " + LOGIN_MESSAGE);
        } else {
            if (resp != null) {
                exchangeAuthorizationCode(resp);
            } else {
                //TODO implement logic in case of error
                callUnauthorized("Unable to login at the moment. " + LOGIN_MESSAGE);
            }
        }
    }

    @MainThread
    public void unauthorized(String toast_message) {
        sharedPreferences.edit().remove(VERIFICATION_TOKEN).remove(VERIFICATION_TOKEN_EXPIRY).apply();

        Toast.makeText(this, toast_message != null ? toast_message : "Login Failed", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @MainThread
    public void callUnauthorized(String toast_message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                unauthorized(toast_message);
            }
        });
    }

    @MainThread
    private void fetchUserInfo(String accessToken, String accessTokenExpirationTime) {

        URL userInfoEndpoint;
        try {
            userInfoEndpoint = new URL(BuildConfig.user_endpoint);

            mExecutor.submit(() -> {
                try {
                    HttpURLConnection conn =
                            (HttpURLConnection) userInfoEndpoint.openConnection();

                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    conn.setInstanceFollowRedirects(false);

                    String response = Okio.buffer(Okio.source(conn.getInputStream()))
                            .readString(Charset.forName("UTF-8"));

                    JSONObject userInfo = new JSONObject(response);

                    mUserInfoJson.set(userInfo);



                    if (userInfo.has("AuthenticationSource")) {
                        String authenticationSource = userInfo.getString("AuthenticationSource");
                        if (!valid_auth_sources.contains(authenticationSource)) {
                            callUnauthorized("You do not have permissions to access this Application. " + LOGIN_MESSAGE);
                        }
                    } else {
                        callUnauthorized("Unable to login at the moment. User Info cannot be retrieved " + LOGIN_MESSAGE);
                    }


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(VERIFICATION_TOKEN, accessToken);
                    editor.putString(VERIFICATION_TOKEN_EXPIRY, accessTokenExpirationTime);
                    editor.apply();
                    setup();

                } catch (IOException ioEx) {
                    callUnauthorized("Fetching user info failed. " + LOGIN_MESSAGE);
                } catch (JSONException jsonEx) {
                    callUnauthorized("Failed to parse user info. " + LOGIN_MESSAGE);
                }
            });
        } catch (MalformedURLException urlEx) {
            callUnauthorized("Unable to Login at the moment. Endpoint is not correctly configured. " + LOGIN_MESSAGE);

        }
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        authService.performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                        if (ex != null) {
                            callUnauthorized("Unable to Login at the moment. Token Request did not succeed. " + LOGIN_MESSAGE);
                        } else {
                            if (response != null) {
                                fetchUserInfo(response.accessToken, Long.toString(response.accessTokenExpirationTime != null ?  response.accessTokenExpirationTime : 0));
                            } else {
                                callUnauthorized("Unable to Login at the moment. Token Request did not succeed. " + LOGIN_MESSAGE);
                            }
                        }
                    }
                }
        );
    }

    @MainThread
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.coordinator),
                message,
                Snackbar.LENGTH_SHORT)
                .show();
    }


    public void setup(){
        // Initialize the SDK
        BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);
        //Initialize client Push SDK
        push = MFPPush.getInstance();
        push.initialize(getApplicationContext(), BuildConfig.appGUID, BuildConfig.clientSecret);
        //Register Android devices
        push.registerDevice(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                //handle successful device registration here

                Log.w(LOG_TAG, "DEVICE REGISTERED SUCCESSFULLY: " + response);
            }

            @Override
            public void onFailure(MFPPushException ex) {
                //handle failure in device registration here

                Log.w(LOG_TAG, "FAILED TO REGISTER DEVICE: " + ex);

            }
        });

        // Create notification dao
        dao = NotificationDAO.getInstance(getApplicationContext());

        // display all notifications
        ArrayList<Notification> all = dao.getAll();

        for (Notification n : all) {

            displayNotification(n.date, n.content, n.id);
        }

        // start notification service
        if (!Util.isServiceRunning(this, NotificationService.class)) {
            Intent serviceIntent = new Intent(this, NotificationService.class);
            startService(serviceIntent);
        }
        notify = true;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int alpha = (progress * (255 / 100));
                seekBar.getThumb().setAlpha(255 - alpha);
                if (progress < 5) {
                    seekBar.setProgress(4);
                }
                if (progress > 95) {
                    seekBar.setProgress(95);
                }
                if (progress > trackedProgress + 20) {
                    seekBar.setProgress(4);
                    trackedProgress = 4;
                } else {
                    trackedProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                trackedProgress = seekBar.getProgress();
                if (seekBar.getProgress() < 90) {
                    //Drawable draw = resize(getResources().getDrawable(R.drawable.thumb_seekbar));
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

    public void whenToCall911(View v) {
        Intent intent = new Intent(HomeActivity.this, WhenToCall911.class);
        startActivity(intent);
    }

    public void usingApp(View v) {
        Intent intent = new Intent(HomeActivity.this, UsingTheApp.class);
        startActivity(intent);
    }

    public void security(View v) {
        Intent intent = new Intent(HomeActivity.this, SecurityActivity.class);
        HomeActivity.this.finish();
        startActivity(intent);
    }

    public void history(View v) {
        Intent intent = new Intent(HomeActivity.this, NotificationHistory.class);
        startActivity(intent);
    }

    public void roadsAndWeather(View v) {
        Intent intent = new Intent(HomeActivity.this, WeatherAndRoads.class);
        startActivity(intent);
    }

    public void emergencyNumbers(View v) {
        Intent intent = new Intent(HomeActivity.this, EmergencyNumbers.class);
        startActivity(intent);
    }

    public void media(View v) {
        Intent intent = new Intent(HomeActivity.this, SocialMedia.class);
        startActivity(intent);
    }

    public void hazards(View v) {
        Intent intent = new Intent(HomeActivity.this, ThreatHazards.class);
        startActivity(intent);
    }

    public void terms(View v) {
        Intent intent = new Intent(HomeActivity.this, TermConditions.class);
        startActivity(intent);
    }

    //Handles the notification when it arrives
    MFPPushNotificationListener notificationListener = new MFPPushNotificationListener() {
        @Override
        public void onReceive(final MFPSimplePushNotification message) {
            // Handle Push Notification
            Log.w(LOG_TAG, "RECEIVED NOTIFICATION: " + message.getAlert());
            Log.w(LOG_TAG, "RECEIVED NOTIFICATION: " + message.toString());

            String content = message.getAlert();
            Date date = new Date();

            // delete all notifications so we only ever display the latest one
            dao.deleteAll();
            ViewGroup layout = findViewById(R.id.activity_home_header);

            if (noteView != null) {
                layout.removeView(noteView);
            }


            Notification note = dao.insert(date, content);

            displayNotification(note.date, note.content, note.id);

            SQLiteHelper sQLiteHelper = new SQLiteHelper(HomeActivity.this);
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c);
            sQLiteHelper.insertRecord("Alert", note.content, formattedDate);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (push != null) {
            push.listen(notificationListener);
        }
        seekBar.setProgress(4);
        seekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (push != null) {
            push.hold();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * called when close button on notification is clicked
     *
     * @param view
     */
    public void closeNotification(View view) {

        dao.delete((int) ((View) view.getParent()).getTag());

        ViewGroup layout = findViewById(R.id.activity_home_header);
        layout.removeView(noteView);
        layout.invalidate();
    }


    /**
     * called when notification is clicked
     *
     * @param view
     */
    public void clickNotification(View view) {

        int notificationId = (int) view.getTag();

        Log.w(LOG_TAG, "notificationId " + notificationId);

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(EXTRA_MESSAGE, notificationId);
        startActivity(intent);

    }

    /**
     * @param date
     * @param content
     * @param rowId
     */
    private void displayNotification(String date, String content, int rowId) {

        ViewGroup layout = findViewById(R.id.activity_home_header);

        LayoutInflater layoutInflater = getLayoutInflater();

        noteView = layoutInflater.inflate(R.layout.notification_view, layout, false);
        TextView descriptionTextView = noteView.findViewById(R.id.notification_description);
        descriptionTextView.setText(content);

        // set current time as title
        TextView titleTextView = noteView.findViewById(R.id.notification_title);
        titleTextView.setText(date);

        noteView.setTag(rowId);

        // add view to layout
        layout.addView(noteView, 1);

        layout.invalidate();
    }

    public void notificationHistory(View view) {
        Intent intent = new Intent(HomeActivity.this, NotificationHistory.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mExecutor.shutdownNow();
    }

}
