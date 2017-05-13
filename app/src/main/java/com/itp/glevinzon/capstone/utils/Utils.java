package com.itp.glevinzon.capstone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.itp.glevinzon.capstone.R;
import com.mapzen.speakerbox.Speakerbox;

/**
 * Created by Glevinzon on 5/1/2017.
 */

public class Utils {
    private static final String PREFERENCES_FILE = "CapstonePref";


    public static int getToolbarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
        return height;
    }


    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static Speakerbox instanceSpeakerbox(Speakerbox sb){
        Speakerbox speakerbox = sb;
        speakerbox.remix("\\sin", "sine");
        speakerbox.remix("\\alpha", "alpha");
        speakerbox.remix("\\pi", "pie");
        speakerbox.remix("\\cos", "cosine");
        speakerbox.remix("^2", "squared");
        speakerbox.remix("\\beta", "beta");
        speakerbox.remix("\\tan", "tangent");
        speakerbox.remix("\\sqrt", "square root");
        speakerbox.remix("\\gamma", "gamma");
        speakerbox.remix("\\operatorname{sin}", "sine");
        speakerbox.remix("\\operatorname{cos}", "cosine");
        speakerbox.remix("\\operatorname{tan}", "tangent");
        return speakerbox;
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }
}
