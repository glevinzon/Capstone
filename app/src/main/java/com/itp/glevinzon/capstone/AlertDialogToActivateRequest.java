package com.itp.glevinzon.capstone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Requests.Datum;
import com.itp.glevinzon.capstone.models.Requests.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cholo on 5/10/2017.
 */

public class AlertDialogToActivateRequest extends DialogFragment {
    private CapstoneService equationService;
    private List<Datum> data;
    private String eqId = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        equationService = CapstoneApi.getClient().create(CapstoneService.class);

        Bundle bundle=this.getArguments();
        eqId = bundle.getString("eqId");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Activate record")
                .setMessage("It shall be done.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callActivateRecordApi().enqueue(new Callback<Request>() {
                            @Override
                            public void onResponse(Call<Request> call, Response<Request> response) {
                                data = fetchResults(response);
                                RequestFragment.adapter.clear();
                                RequestFragment.adapter.addAll(data);
                            }

                            @Override
                            public void onFailure(Call<Request> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    private Call<Request> callActivateRecordApi() {
        return equationService.activate(Integer.parseInt(eqId));
    }

    private List<Datum> fetchResults(Response<Request> response) {
        Request keywords = response.body();
        return keywords.getData();
    }

    public static AlertDialogToActivateRequest newInstance() {
        return new AlertDialogToActivateRequest();
    }
}