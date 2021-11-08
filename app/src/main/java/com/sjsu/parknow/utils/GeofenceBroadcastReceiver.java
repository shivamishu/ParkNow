package com.sjsu.parknow.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.sjsu.parknow.NearbyFragment;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();
       // String message = intent.getStringExtra("Yes");
        //if("Yes".equals(message)) {

            //testFn();
      //  }
       /*if(intent.getIdentifier().equals("26")){
            Toast.makeText(context, "Yes clicked", Toast.LENGTH_SHORT).show();
        } else if(intent.getIdentifier().equals("7")){
            Toast.makeText(context, "No clicked", Toast.LENGTH_SHORT).show();
        }*/
        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();
        notificationHelper.setLatitude(location.getLatitude());
        notificationHelper.setLongitude(location.getLongitude());
        switch (transitionType) {


            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Have you parked?", "", NearbyFragment.class);
                break;
           /* case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", NearbyFragment.class);
                break;*/
            case Geofence.GEOFENCE_TRANSITION_EXIT:

                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Are you exiting the parking spot?", "", NearbyFragment.class);
                break;
        }
    }
}