package com.sjsu.parknow.network;

public class ParkingSpotsSearchAPI {
    private ParkingSpotsSearchAPI(){};
    public static final String BASE_URL = " https://hkywv8gpag.execute-api.us-west-2.amazonaws.com";
    //https://uw4xkwpyyl.execute-api.us-west-1.amazonaws.com
    public static IParkingSpots getAPIService() {

        return RetrofitClient.getClientSearchSpots(BASE_URL).create(IParkingSpots.class);
    }

}
