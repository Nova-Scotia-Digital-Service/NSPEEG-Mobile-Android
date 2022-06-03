package com.ibm.emergencyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ibm.emergencyapp.util.Util;

import static com.ibm.emergencyapp.util.Constants.PREF_NAME_VERIFICATION;
import static com.ibm.emergencyapp.util.Constants.USER_VERIFIED;
import static com.ibm.emergencyapp.util.Constants.VERIFICATION_TOKEN;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage.LOG_TAG;

public class TokenActivity extends AppCompatActivity {

    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);


        SharedPreferences sharedPreferences =  getSharedPreferences(PREF_NAME_VERIFICATION, 0);
        token = sharedPreferences.getString(VERIFICATION_TOKEN, "");

    }

    /**
     * Verify the token that was entered.
     * @param v
     */
    public void submitToken(View v) {
        // verify token
        EditText tokenEdit = findViewById(R.id.enterTokenBox);

        Log.d("TOKEN IS: " + token, LOG_TAG);
        Log.d("GIVEN: " + tokenEdit.getText().toString(), LOG_TAG);

        if (token.equals(tokenEdit.getText().toString())) {
            // email successfully validated

            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME_VERIFICATION, MODE_PRIVATE).edit();
            editor.putBoolean(USER_VERIFIED, true);
            editor.apply();

            Util.setExpireTime(getSharedPreferences("prefs", 0));

            Intent intent = new Intent(this, HomeActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();

        } else {

            TextView text = findViewById(R.id.enterTokenText);
            text.setText(R.string.tokenErrorText);
            text.setTextColor(getResources().getColor(R.color.red));

        }


    }
}
