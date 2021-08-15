package com.sjsu.parknow.network;

import com.sjsu.parknow.model.GoogleResponse;
import com.sjsu.parknow.model.SpotsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IParkingSpots {
    @GET("/getParkingSpots")
    Call<SpotsResponse> getParkingSpots(@Query("radius") String radius, @Query("location") String position);
}

