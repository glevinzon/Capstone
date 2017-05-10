package com.itp.glevinzon.capstone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cholo on 5/10/2017.
 */

public class AlertDialogToSaveCapturedEquation extends DialogFragment {
    private String latex = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle=this.getArguments();
        String response = bundle.getString("latex");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            latex = jsonObject.getString("latex");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(getContext(), "Path: " + audioPath, Toast.LENGTH_LONG).show();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Save record")
                .setMessage("You will be prompted to supply metadata.")
//                .setMessage("" + latex)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        RecordFormDialogFragment newFragment = RecordFormDialogFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("latex", latex);
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "Alert Save");
                    }
                })
                .setNegativeButton("Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    public static AlertDialogToSaveCapturedEquation newInstance() {
        return new AlertDialogToSaveCapturedEquation();
    }
}