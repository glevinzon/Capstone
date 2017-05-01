package com.itp.glevinzon.capstone;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

/**
 * Created by Glevinzon on 4/17/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}