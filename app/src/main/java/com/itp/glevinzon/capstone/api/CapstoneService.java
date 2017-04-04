package com.itp.glevinzon.capstone.api;

import com.itp.glevinzon.capstone.models.Equations;
import com.itp.glevinzon.capstone.models.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by glen on 4/3/17.
 */

public interface CapstoneService {

    @GET("equations")
    Call<Equations> getEquations(
            @Query("filter") String filter,
            @Query("page") int page,
            @Query("count") int count
    );

    @POST("tokens")
    @FormUrlEncoded
    Call<Token> saveToken(@Field("token") String token);
}
