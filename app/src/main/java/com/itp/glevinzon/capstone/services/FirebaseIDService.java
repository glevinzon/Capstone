package com.itp.glevinzon.capstone.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by glen on 4/4/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    private CapstoneService equationService;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        equationService = CapstoneApi.getClient().create(CapstoneService.class);
        // TODO: Implement this method to send any registration to your app's servers.
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String prevToken = pref.getString("device_token", null);
        sendRegistrationToServer(refreshedToken, prevToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String prevToken) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

//        editor.putBoolean("key_name", true); // Storing boolean - true/false
        editor.remove("device_token");
        editor.putString("device_token", token); // Storing string
//        editor.putInt("key_name", "int value"); // Storing integer
//        editor.putFloat("key_name", "float value"); // Storing float
//        editor.putLong("key_name", "long value"); // Storing long
//
        editor.commit(); // commit changes
        // Add custom implementation, as needed.
        callTokenApi(token, prevToken).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.d(TAG, "Glevinzon was here! : " + response.body().getDeviceToken());
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Call<Token> callTokenApi(String token, String prevToken) {
        return equationService.saveToken(token, prevToken);
    }
}