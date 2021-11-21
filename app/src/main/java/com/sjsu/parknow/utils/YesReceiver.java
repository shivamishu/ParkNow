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


    }

}