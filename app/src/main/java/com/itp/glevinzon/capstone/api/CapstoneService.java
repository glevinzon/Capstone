package com.itp.glevinzon.capstone.api;

import com.itp.glevinzon.capstone.models.Equations;
import com.itp.glevinzon.capstone.models.Token;
import com.itp.glevinzon.capstone.models.Upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("search")
    Call<Equations> search(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("count") int count
    );
    @GET("equations/related")
    Call<Equations> getRelated(
            @Query("eqId")  String eqId,
            @Query("page") int page,
            @Query("count") int count
    );

    @Multipart
    @POST("equation/upload")
    Call<Upload> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name, @Part("eqId") int eqId, @Part("deviceId") String deviceId);
}
