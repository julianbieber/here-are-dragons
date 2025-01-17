package com.example.activitytracking;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.FusedLocationProviderClient;

public class Tracker {

    private static PendingIntent mActivityTransitionsPendingIntent;
    private static PendingIntent mLocationUpdatePendingIntent;

    private static String logMessage = "";

    private static final String activityAction = "DungeonsAndTrainingActivityTracking";
    private static final String locationAction = "DungeonsAndTrainingLocationTracking";

    static String getLog() {
        if (ActivityReceiver.receiverLogMessage != null) {
            return ActivityReceiver.receiverLogMessage + " | " + logMessage + " | " + LocationReceiver.receiverLogMessage;
        } else {
            return logMessage;
        }
    }

    private static boolean isRunning = false;

    private static FusedLocationProviderClient locationClient;

    private static LocationRequest locationRequest;

    static public void initialize(Context unityContext, Activity unityActivity, String url, String user, String token) {
        try {
            if (!isRunning) {
                SimpleService.context = unityContext;
                Intent notificationIntent = new Intent(unityContext, SimpleService.class);
                unityActivity.startForegroundService(notificationIntent);


                isRunning = true;
                final Intent intent = new Intent().setAction(activityAction);
                mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(unityActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Register for Transitions Updates.

                IntentFilter activityFilter = new IntentFilter();
                activityFilter.addAction(activityAction);
                unityContext.registerReceiver(new ActivityReceiver(url, user, token), activityFilter);
                Task<Void> task = ActivityRecognition.getClient(unityActivity).requestActivityUpdates(1, mActivityTransitionsPendingIntent);
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        logMessage += "Transitions Api was successfully registered.";
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logMessage += "Transitions Api could NOT be registered: " + e;
                    }
                });

                isRunning = true;
                final Intent locationIntent = new Intent().setAction(locationAction);
                mLocationUpdatePendingIntent = PendingIntent.getBroadcast(unityActivity, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Register for Transitions Updates.

                IntentFilter locationFilter = new IntentFilter();
                locationFilter.addAction(locationAction);
                unityContext.registerReceiver(new LocationReceiver(url, user, token), locationFilter);

                locationClient = LocationServices.getFusedLocationProviderClient(unityContext);
                locationRequest = new LocationRequest();
                locationRequest.setInterval(1000);
                locationRequest.setFastestInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setMaxWaitTime(1500);

                if (ActivityCompat.checkSelfPermission(unityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(unityContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.exit(1);
                }
                Task<Void> locationtask = locationClient.requestLocationUpdates(locationRequest, mLocationUpdatePendingIntent);
                locationtask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d("Tracker", "Location Api was successfully registered.");
                        logMessage += "Locatio Api was successfully registered.";
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tracker", "Location Api was successfully NOT registered." + e.getMessage());
                        logMessage += "Transitions Api could NOT be registered: " + e;
                    }
                });


            }
        } catch (Throwable e) {
            logMessage = e.getMessage();
        }
    }


}


