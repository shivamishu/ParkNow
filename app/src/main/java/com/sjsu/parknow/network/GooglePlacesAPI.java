package com.sjsu.parknow.network;

public class GooglePlacesAPI {
    private GooglePlacesAPI(){};
    public static final String BASE_URL = "https://maps.googleapis.com";

    public static IGooglePlaces getAPIService() {

        return com.sjsu.parknow.network.RetrofitClient.getClientGoogle(BASE_URL).create(IGooglePlaces.class);
    }

}
