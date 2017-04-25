package com.itp.glevinzon.capstone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Glevinzon on 4/25/2017.
 */

public class Upload {
    @SerializedName("success")
    @Expose
    boolean success;
    @SerializedName("message")
    @Expose
    String message;

    public String getMessage() {
        return message;
    }

    public boolean getSuccess() {
        return success;
    }
}
