package com.example.activitytracking;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Tracker {

    private static PendingIntent mActivityTransitionsPendingIntent;

    private static String logMessage = "";

    private final String action = "DungeonsAndTrainingActivityTracking";
    static String getLog() {
        if (TransitionsReceiver.receiverLogMessage != null) {
            return TransitionsReceiver.receiverLogMessage + " | " + logMessage;
        } else {
            return logMessage;
        }
    }

    private static boolean isRunning = false;


    static public void initialize(Context unityContext, Activity unityActivity, String url, String user, String token){
        try{
            if (!isRunning) {
                isRunning = true;
                final Intent intent = new Intent().setAction("DungeonsAndTrainingActivityTracking");
                mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(unityActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Register for Transitions Updates.

                IntentFilter f = new IntentFilter();
                f.addAction("DungeonsAndTrainingActivityTracking");
                unityContext.registerReceiver(new TransitionsReceiver(url, user, token), f);
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
            }
        } catch (Throwable e) {
            logMessage = e.getMessage();
        }
    }


}


