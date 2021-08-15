package com.sjsu.parknow.network;

public class ParkingSpotsSearchAPI {
    private ParkingSpotsSearchAPI(){};
    public static final String BASE_URL = "https://uw4xkwpyyl.execute-api.us-west-1.amazonaws.com";

    public static IParkingSpots getAPIService() {

        return RetrofitClient.getClientSearchSpots(BASE_URL).create(IParkingSpots.class);
    }

}
