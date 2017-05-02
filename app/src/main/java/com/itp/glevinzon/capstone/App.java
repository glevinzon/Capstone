package com.itp.glevinzon.capstone;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.itp.glevinzon.capstone.utils.Utils;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

/**
 * Created by Glevinzon on 4/17/2017.
 */

public class App extends Application {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public void onCreate() {
        super.onCreate();

        String username = randomAlphaNumeric(9);
        Utils.saveSharedSetting(this, HomeActivity.PREF_USER_NAME, username);
        Utils.saveSharedSetting(this, HomeActivity.PREF_USER_ROLE, "user");

        Stetho.initializeWithDefaults(this);
        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
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