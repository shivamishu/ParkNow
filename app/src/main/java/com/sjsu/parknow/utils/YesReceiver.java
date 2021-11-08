package com.sjsu.parknow.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sjsu.parknow.MapsFragment;
import com.sjsu.parknow.databinding.FragmentMapsBinding;
import com.sjsu.parknow.network.IPostCallParkingSpots;

import static android.content.ContentValues.TAG;

public class YesReceiver extends BroadcastReceiver {
    private IPostCallParkingSpots iPostCallParkingSpots;
    private FragmentMapsBinding binding;
    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("Yes");
        Bundle bundle = intent.getExtras();
        String id = String.valueOf(bundle.get("ID"));
        double lat = (double) bundle.get("Lat");
        double longitude = (double) bundle.get("Long");
        String transition = String.valueOf(bundle.get("transition"));
       if("26".equals(id) && transition.equals("1") ){
           //enter geofence update occupied status
            updateSpotStatus(context,lat,longitude,"create");
        } else if("26".equals(id) && transition.equals("4")){
           //exit state update available status
           updateSpotStatus(context,lat,longitude,"update");
       }
        //update the occupied status of the parking lot user selected.

        //show notificatio to the user

//        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void updateSpotStatus(Context context,double latitude, double longitude, String transaction) {
        MapsFragment frag = new MapsFragment();
        frag.updateSpotStatus(latitude,  longitude, transaction,context);

        Log.d(TAG, "onSuccess: updated DB...");

      /* String lat = Double.toString(latitude);
        String lng = Double.toString(longitude);
        StringBuilder sb = new StringBuilder();
        sb.append(lat);
        sb.append("|");
        sb.append(lng);
        Item item = new Item();
        Payload payload = new Payload();
        item.setId(sb.toString());
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        if (transaction.equals("create")) {
            item.setParkingstatus("parked");
        } else if (transaction.equals("update")) {
            item.setParkingstatus("available");
        }


        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        item.setUserid(android_id);
        payload.setItem(item);
        Post post = new Post(transaction, "parknow", payload);
        iPostCallParkingSpots = PostCallAPI.getAPIService();
        Call<POST> call = iPostCallParkingSpots.createPost(post);
        call.enqueue(new Callback<POST>() {
            @Override
            public void onResponse(Call<POST> call, Response<POST> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(binding.mapRelLayout, "Your parking spot is updated", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<POST> call, Throwable throwable) {
                Log.e("ERROR", "Unable to call POST call API.");

            }
        });*/
    }

}