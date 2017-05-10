package com.itp.glevinzon.capstone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

/**
 * Created by Glevinzon on 4/30/2017.
 */

public class AlertDialogToSaveRecordedAudio extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle=this.getArguments();
        String audioPath = bundle.getString("path");
//        Toast.makeText(getContext(), "Path: " + audioPath, Toast.LENGTH_LONG).show();
        return new AlertDialog.Builder(getActivity())

                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Save record")
                .setMessage("You will be prompted to supply metadata.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        RecordFormDialogFragment newFragment = RecordFormDialogFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("path", audioPath);
                        bundle.putString("mode", "record");
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "Alert Save");
                    }
                })
                .setNegativeButton("Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    public static AlertDialogToSaveRecordedAudio newInstance() {
        return new AlertDialogToSaveRecordedAudio();
    }
}