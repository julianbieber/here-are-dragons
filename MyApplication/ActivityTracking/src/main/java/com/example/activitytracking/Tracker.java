package com.example.activitytracking;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tracker {

    private static boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    private static final String TRANSITIONS_RECEIVER_ACTION =  "com.example.activitytracking.TRANSITIONS_RECEIVER_ACTION";
    private static PendingIntent mActivityTransitionsPendingIntent;
    private static TransitionsReceiver mTransitionsReceiver;

    private static String logMessage = "";

    static String getLog() {
        if (TransitionsReceiver.receiverLogMessage != null) {
            return TransitionsReceiver.receiverLogMessage + " | " + logMessage;
        } else {
            return logMessage;
        }
    }

    static Context context;
    static Activity activity;

    static public void initialize(Context unityContext, Activity unityActivity){
        try{
            if (checkPermission()) {
                if (context == null) {
                    context = unityContext;
                    activity = unityActivity;

                    final Intent intent = new Intent().setAction("action--");
                    mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Register for Transitions Updates.

                    IntentFilter f = new IntentFilter();
                    f.addAction("action--");
                    context.registerReceiver(new TransitionsReceiver(), f);
                    Thread.sleep(1000);
                    Task<Void> task = ActivityRecognition.getClient(activity).requestActivityUpdates(1, mActivityTransitionsPendingIntent);
                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            logMessage += "Transitions Api was successfully registered.";
                            context.sendBroadcast(intent);
                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            logMessage += "Transitions Api could NOT be registered: " + e;
                        }
                    });
                }
            } else {
                logMessage = "no permission";
            }
        } catch (Throwable e) {
            logMessage = e.getMessage();
        }
    }

    public static boolean checkPermission() {
        if (runningQOrLater) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
            );
        } else {
            return true;
        }
    }



}


