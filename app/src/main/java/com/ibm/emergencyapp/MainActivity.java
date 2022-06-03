package com.ibm.emergencyapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ibm.emergencyapp.util.Util;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.squareup.okhttp.OkHttpClient;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ibm.emergencyapp.util.Constants.PREF_NAME_VERIFICATION;
import static com.ibm.emergencyapp.util.Constants.VERIFICATION_TOKEN;
import static com.ibm.emergencyapp.util.Constants.VERIFICATION_TOKEN_EXPIRY;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    //    public static final String PREF_NAME_VERIFICATION = "verification";
    public static final String PREF_NAME_TERMS = "terms";
    private SharedPreferences spTermsAccepted;
    private SharedPreferences spVerificationRequired;

    private String token = null;
    private String email = null;

    private String validate = "";
    private String validate_expiry = "";

    private static final boolean DEV_MODE = false;

    // client Push SDK
    private MFPPush push = null;

    private final OkHttpClient client = new OkHttpClient();
    AuthorizationService authService;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spVerificationRequired = getSharedPreferences(PREF_NAME_VERIFICATION, MODE_PRIVATE);
        spTermsAccepted = getSharedPreferences(PREF_NAME_TERMS, MODE_PRIVATE);

        authService = new AuthorizationService(getMainActivityContext());
        context = getMainActivityContext();
        // remove comments to go to main screen
        //Intent intentTest = new Intent(this, HomeActivity.class);
        //startActivity(intentTest);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME_VERIFICATION, 0);
        validate = sharedPreferences.getString(VERIFICATION_TOKEN, "");
        validate_expiry = sharedPreferences.getString(VERIFICATION_TOKEN_EXPIRY, "");

        if (validate_expiry != null) {
            if (!validate_expiry.equals("") && !validate_expiry.equals("0")) {
                Long expiry = Long.parseLong(validate_expiry);
                Date expiration_time = new Date(expiry);

                if (expiration_time.before(new Date())) {
                    unauthenticated();
                } else {
                    if (!validate.equals("")) {
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        unauthenticated();
                    }
                }
            } else {
                unauthenticated();
            }
        } else {
            unauthenticated();
        }


    }

    private void unauthenticated() {
        if (!Util.isTermAccepted(spTermsAccepted)) {
            showTermsPopup();
        } else {
            triggerAuthentication();
        }
    }

    private void triggerAuthentication() {
        AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(BuildConfig.issuer), new AuthorizationServiceConfiguration.RetrieveConfigurationCallback() {
            @Override
            public void onFetchConfigurationCompleted(@Nullable AuthorizationServiceConfiguration serviceConfiguration, @Nullable AuthorizationException ex) {
                if (ex != null || serviceConfiguration == null) {
                    Log.e("Failure: ", "failed to fetch configuration");
                } else {
//                    Map<String, String> additionalParams = new HashMap<String, String>();
//                    additionalParams.put("AuthenticationSource", BuildConfig.authentication_source);

                    Uri redirectUri = Uri.parse(BuildConfig.redirect_uri);
                    AuthorizationRequest.Builder authRequestBuilder =
                            new AuthorizationRequest.Builder(
                                    serviceConfiguration, // the authorization service configuration
                                    BuildConfig.client_id, // the client ID, typically pre-registered and static
                                    ResponseTypeValues.CODE, // the response_type value: we want a code
                                    redirectUri); // the redirect URI to which the auth response is sent

//                    .setResponseType()
                    AuthorizationRequest authRequest = authRequestBuilder
                            .setScope(BuildConfig.authorization_scope)
                            .build();

                    Intent homeIntent = new Intent(context, HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_ONE_SHOT);
                    authService.performAuthorizationRequest(
                            authRequest,
                            homePendingIntent,
                            homePendingIntent);
                }
            }
        });

    }

    // Set the context to call in callbacks
    public Context getMainActivityContext() {
        return (Context) this;
    }


    /**
     * Show Terms and conditions when start app for the first time.
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    void showTermsPopup() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("terms");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final DialogFragment frag = TermsPopup
                .newInstance(R.string.terms_conditions_FOIPOP);
        frag.setCancelable(false);
        frag.show(ft, "terms");

    }

    void doPositiveClick() {
        Log.d(TermsPopup.class.getSimpleName(), "Positive click!");
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME_TERMS, MODE_PRIVATE).edit();
        editor.putBoolean(Util.TERM_ACCEPTED_KEY, true);
        editor.apply();
        triggerAuthentication();
    }

    public void openLogin(View view) {
        triggerAuthentication();
    }

    /**
     * inner static fragment class for Terms&Conditions popup extends DialogFragment
     */

    public static class TermsPopup extends DialogFragment {

        public TermsPopup() {
        }

        public static TermsPopup newInstance(int message) {
            TermsPopup frag = new TermsPopup();
            Bundle args = new Bundle();
            args.putInt("message", message);
            frag.setArguments(args);
            Log.i(LOG_TAG, "showTermsPopup is called here once again");
            return frag;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.terms_conditions_popup_content)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MainActivity) getActivity()).doPositiveClick();
                            dismiss();
                        }
                    })
                    .create();

        }
    }

}