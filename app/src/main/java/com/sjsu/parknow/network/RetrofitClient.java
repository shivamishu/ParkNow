package com.sjsu.parknow.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitGoogle = null;
    private static Retrofit retrofitSearchSpots = null;
    private static Retrofit retrofitPostCall = null;

    public static Retrofit getClientGoogle(String baseUrl) {
        if (retrofitGoogle==null) {
            retrofitGoogle = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitGoogle;
    }
    public static Retrofit getClientSearchSpots(String baseUrl) {
        if (retrofitSearchSpots==null) {
            retrofitSearchSpots = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitSearchSpots;
    }

    public static Retrofit getPostCall(String baseUrl) {
        if (retrofitPostCall==null) {
            retrofitPostCall = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitPostCall;
    }
}
