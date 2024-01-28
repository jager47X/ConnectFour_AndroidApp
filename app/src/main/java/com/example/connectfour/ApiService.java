package com.example.connectfour;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @GET("get-connect4-data")
    Call<List<Connect4Dto>> getData();

    @POST("post-connect4-data")
    Call<Connect4Dto> postData(@Body Connect4Dto requestData);
}