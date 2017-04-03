package com.itp.glevinzon.capstone.api;

import com.itp.glevinzon.capstone.models.Equations;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
