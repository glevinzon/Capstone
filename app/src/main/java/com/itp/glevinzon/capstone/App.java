package com.itp.glevinzon.capstone;

import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Keyword;
import com.itp.glevinzon.capstone.models.Tag;
import com.itp.glevinzon.capstone.utils.Utils;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Glevinzon on 4/17/2017.
 */

public class App extends Application {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private CapstoneService equationService;
    private List<Tag> data;

    @Override
    public void onCreate() {
        super.onCreate();

        equationService = CapstoneApi.getClient().create(CapstoneService.class);

        SharedPreferences capstonePref = getSharedPreferences("CapstonePref", MODE_PRIVATE);
        if (!capstonePref.contains("user_name")) {
            String username = randomAlphaNumeric(9);
            Utils.saveSharedSetting(this, HomeActivity.PREF_USER_NAME, username);
            Utils.saveSharedSetting(this, HomeActivity.PREF_USER_ROLE, "admin");
        }

        Stetho.initializeWithDefaults(this);
        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("CapstonePref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("tags");


        callTagsApi().enqueue(new Callback<Keyword>() {
            @Override
            public void onResponse(Call<Keyword> call, Response<Keyword> response) {
                // Got data. Send it to adapter
                data = fetchResults(response);
                ArrayList<String> tagList = new ArrayList<String>();
                for (int i=0; i<data.size(); i++) {
                    Tag result = data.get(i);
                    tagList.add(result.getName());
                }

                JSONArray jsArray = new JSONArray(tagList);
                editor.putString("tags", jsArray.toString());
                editor.commit();
//                Toast.makeText(getContext(), jsArray.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Keyword> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private Call<Keyword> callTagsApi() {
        int COUNT = 999;
        return equationService.getTags();
    }

    private List<Tag> fetchResults(Response<Keyword> response) {
        Keyword keywords = response.body();
        return keywords.getTags();
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}