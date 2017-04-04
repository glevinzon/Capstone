package com.itp.glevinzon.capstone;

import android.app.Application;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

/**
 * Created by glen on 4/4/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
