package com.itp.glevinzon.capstone;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Glevinzon on 5/6/2017.
 */

public class MathpixUUID {
    private static final String PREFS_UID_KEY = "uid";
    private static final String UID_PREFS = "uidprefs";
    private static UUID uuid;

    public static UUID uuid(Context context){
        if (uuid != null){
            return uuid;
        }

        SharedPreferences prefs = context.getSharedPreferences(UID_PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(PREFS_UID_KEY)) {
            String uid = prefs.getString(PREFS_UID_KEY, null);
            if (uid != null) {
                try {
                    uuid = UUID.fromString(uid);
                } catch (IllegalArgumentException e) {

                }
            }
        }else{
            uuid = UUID.randomUUID();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_UID_KEY, uuid.toString());
            editor.apply();
        }
        return uuid;
    }
}
