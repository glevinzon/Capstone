package com.itp.glevinzon.capstone;

import android.app.Application;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

/**
 * Created by Glevinzon on 4/17/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}