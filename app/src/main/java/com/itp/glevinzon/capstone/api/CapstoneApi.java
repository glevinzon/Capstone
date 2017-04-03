package com.itp.glevinzon.capstone.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by glen on 4/3/17.
 */

public class CapstoneApi {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://apicapstone.herokuapp.com/app/")
                    .build();
        }
        return retrofit;
    }
}
