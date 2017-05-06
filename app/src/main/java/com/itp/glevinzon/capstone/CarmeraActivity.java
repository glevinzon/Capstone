package com.itp.glevinzon.capstone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Glevinzon on 5/6/2017.
 */

public class CarmeraActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
