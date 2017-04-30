package com.itp.glevinzon.capstone;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Glevinzon on 4/30/2017.
 */

public class RecordFormDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialogfragment_recordform, container,
                false);
        getDialog().setTitle("New Record");

        return rootView;
    }


    public static RecordFormDialogFragment newInstance() {
        return new RecordFormDialogFragment();
    }
}