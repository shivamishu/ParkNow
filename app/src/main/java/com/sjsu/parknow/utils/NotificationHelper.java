package com.sjsu.parknow.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.sjsu.parknow.R;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {

    private static final String TAG = "NotificationHelper";
    GeoFenceHelper geofenceHelper;
    double latitude;
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    LatLng latLng;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            geofenceHelper = new GeoFenceHelper(base);
            createChannels();

        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.sjsu.parknow" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(String title, String body, Class activityName) {
        String transition="1";
        Intent intent = new Intent(this, activityName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    if (!title.equals("Have you parked?")){
        transition = "4";
    }
        PendingIntent pendingIntent1 = geofenceHelper.getYesPendingIntent(latitude,longitude,transition);

       // Intent yesIntent = new Intent(this, YesReceiver.class);
      //  yesIntent.putExtra("Yes", "Yes");
      //  PendingIntent yesPedingIntent = PendingIntent.getActivity(this,26,yesIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent.getActivity(this, 26, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

       /* Intent noIntent = new Intent(this, NearbyFragment.class);
        PendingIntent noPendingIntent = PendingIntent.getActivity(this, 7, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(body)
                //.setContentTitle("Park Now")
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_parked_car)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.BLUE)
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher_round, "Yes", pendingIntent1)
                .addAction(R.mipmap.ic_launcher, "No", null)
                //.setStyle(new NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(body))
                //.setFullScreenIntent(pendingIntent,true)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);

    }

}