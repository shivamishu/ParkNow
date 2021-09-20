package com.sjsu.parknow.network;

import com.sjsu.parknow.model.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IPostCallParkingSpots
{
    @POST("/parknow/parknowresource")
    Call<POST> createPost(@Body Post post);
}
