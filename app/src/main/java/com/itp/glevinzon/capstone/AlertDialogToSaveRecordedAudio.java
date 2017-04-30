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

        return new AlertDialog.Builder(getActivity())

                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Save record")
                .setMessage("You will be prompted to supply metadata.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        RecordFormDialogFragment newFragment = RecordFormDialogFragment.newInstance();
                        newFragment.show(ft, "Alert Save");
                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    public static AlertDialogToSaveRecordedAudio newInstance() {
        return new AlertDialogToSaveRecordedAudio();
    }
}