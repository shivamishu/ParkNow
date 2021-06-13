package com.sjsu.parknow.model;

import android.util.Log;

import com.sjsu.parknow.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PlaceAPI {
    public static final String apiKey = BuildConfig.MAPS_API_KEY;;
    public ArrayList<String> autoComplete(String textString){
        ArrayList<String> placesList = new ArrayList<String>();
        HttpURLConnection connection = null;
        StringBuilder res = new StringBuilder();
        char[] buffer = new char[1024];
        int read;
        try {
            String apiURL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + apiKey + "&input=" + textString;
            URL url = new URL(apiURL);
            connection = (HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            while ((read = inputStreamReader.read(buffer)) != -1){
                res.append(buffer,0, read);
            }

            Log.d("JSON", res.toString());
        } catch (IOException err){
            err.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(res.toString());
            JSONArray suggestionsArray = jsonObject.getJSONArray("predictions");
            for(int i=0; i < suggestionsArray.length(); i++){
                placesList.add(suggestionsArray.getJSONObject(i).getString("description"));
            }
        }
        catch (JSONException err){
            err.printStackTrace();
        }

        return placesList;
    }


}

