package com.sjsu.parknow.network;

public class PostCallAPI {
    private PostCallAPI(){};
    public static final String BASE_URL = "https://qlml5plj9k.execute-api.us-west-2.amazonaws.com";

    public static IPostCallParkingSpots getAPIService() {

        return RetrofitClient.getPostCall(BASE_URL).create(IPostCallParkingSpots.class);
    }
}
