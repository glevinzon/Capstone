package com.itp.glevinzon.capstone.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.itp.glevinzon.capstone.HomeActivity;
import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Token;
import com.itp.glevinzon.capstone.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by glen on 4/4/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    private CapstoneService equationService;
    private String prevToken = "";
    private String username = "";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
//        username = randomAlphaNumeric(9);
//        Utils.saveSharedSetting(FirebaseIDService.this, HomeActivity.PREF_USER_NAME, username);
        username = Utils.readSharedSetting(getApplicationContext(), HomeActivity.PREF_USER_NAME, null);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        equationService = CapstoneApi.getClient().create(CapstoneService.class);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("CapstonePref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        try {
            prevToken = pref.getString("device_token", null);
            if(prevToken == null){
                prevToken = "Im Groot";
            }
        }catch (Exception e) {
            Log.d(TAG, e.getMessage() + "");
        }

        editor.remove("device_token");
        editor.putString("device_token", token); // Storing string

        editor.commit(); // commit changes
        // Add custom implementation, as needed.
        callTokenApi(token).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Call<Token> callTokenApi(String token) {
        return equationService.saveToken(username, token, prevToken);
    }
}