package com.sjsu.parknow.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeoFenceHelper extends ContextWrapper {
    private static final String TAG = "GeofenceHelper";

    private LatLng latLng;
    PendingIntent pendingIntent;
    PendingIntent pendingYesIntent;
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
    public LatLng getLatLng() {
        return latLng;
    }
    public PendingIntent getPendingYesIntent() {
        return pendingYesIntent;
    }

    public void setPendingYesIntent(PendingIntent pendingYesIntent) {
        this.pendingYesIntent = pendingYesIntent;
    }


    public GeoFenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build() ;
    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        setLatLng(latLng);
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

    }

    public PendingIntent getPendingIntent(LatLng latLng) {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

        intent.putExtra("ID" ,26);
        intent.putExtra("Lat" ,latLng.latitude);
        intent.putExtra("Long" ,latLng.longitude);
        sendBroadcast(intent);
        if (pendingIntent != null) {
            return pendingIntent;
        }

        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
    public PendingIntent getYesPendingIntent(double latitude, double longitude, String transition) {
        if (pendingYesIntent != null) {
            return pendingYesIntent;
        }
        Intent intent = new Intent(this, YesReceiver.class);
        intent.putExtra("ID" ,26);
        intent.putExtra("Lat" ,latitude);
        intent.putExtra("Long" ,longitude);
        intent.putExtra("transition", transition);
        pendingYesIntent = PendingIntent.getBroadcast(this, 26, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        setPendingYesIntent(pendingYesIntent);
        return pendingYesIntent;
    }
    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}

