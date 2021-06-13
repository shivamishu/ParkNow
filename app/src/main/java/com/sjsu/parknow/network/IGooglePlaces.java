package com.sjsu.parknow.network;

import com.sjsu.parknow.model.GoogleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGooglePlaces {
    @GET("/maps/api/place/nearbysearch/json")
    Call<GoogleResponse> getParkingPlaces(@Query("key") String apiKey, @Query("type") String type, @Query("radius") String radius, @Query("location") String position);
}

