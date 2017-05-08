package com.itp.glevinzon.capstone.api;

import com.itp.glevinzon.capstone.models.Capstone;
import com.itp.glevinzon.capstone.models.Equation;
import com.itp.glevinzon.capstone.models.Keyword;
import com.itp.glevinzon.capstone.models.Requests.Request;
import com.itp.glevinzon.capstone.models.Search;
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
    @GET("tags")
    Call<Keyword> getTags();

    @GET("equations")
    Call<Capstone> getEquations(
            @Query("filter") String filter,
            @Query("page") int page,
            @Query("count") int count
    );

    @POST("tokens")
    @FormUrlEncoded
    Call<Token> saveToken(
            @Field("username") String username,
            @Field("token") String token,
            @Field("prevToken") String prevToken);

    @GET("search")
    Call<Search> search(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("count") int count
    );
    @GET("equations/related")
    Call<Search> getRelated(
            @Query("eqId")  String eqId,
            @Query("page") int page,
            @Query("count") int count
    );

    @Multipart
    @POST("equation/upload")
    Call<Upload> uploadFile(@Part MultipartBody.Part file, @Part("eqId") int eqId, @Part("deviceId") RequestBody deviceId);

    @POST("equations")
    @Multipart
    Call<Equation> saveEquation(
            @Part MultipartBody.Part file,
            @Part("username") RequestBody username,
            @Part("name") RequestBody name,
            @Part("note") RequestBody note,
            @Part("tags") RequestBody tags);

    @POST("request/activate")
    @FormUrlEncoded
    Call<Request> activate(
            @Field("id") Integer id);

    @GET("requests")
    Call<Request> getRequests(
            @Query("filter") String filter,
            @Query("page") int page,
            @Query("count") int count
    );

}
