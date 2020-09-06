package com.example.activitytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransitionsReceiver extends BroadcastReceiver {

    public static String receiverLogMessage;

    private int confidenceThreshold = 70;

    private List<Integer> supportedActivities = new ArrayList<>();

    private static Optional<DetectedActivity> currentActivity;
    private String url;
    private String user;
    private String token;

    public TransitionsReceiver(String url, String user, String token) {
        if (receiverLogMessage == null) {
            receiverLogMessage = "";
            supportedActivities.add(DetectedActivity.ON_BICYCLE);
            supportedActivities.add(DetectedActivity.RUNNING);
            currentActivity = Optional.empty();
            this.url = url;
            this.user = user;
            this.token = token;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                Optional<DetectedActivity> newActivity = extractCurrentActivity(result);
                if (newActivity.isPresent()){
                    receiverLogMessage += newActivity.get().getType() + "\n";
                    if (hasNewActivityBeenStarted(newActivity.get())) {
                        if (currentActivity.isPresent()) {
                            stopActivity();
                        }
                        startActivty(newActivity.get().getType());
                    }
                } else {
                    if (currentActivity.isPresent()) {
                        stopActivity();
                    }
                }
                currentActivity = newActivity;

            } else {
                receiverLogMessage += "ActivityTransitionResult does not have a result";
            }
        } catch (Throwable e) {
            receiverLogMessage += e.getMessage();
        }
    }

    private Optional<DetectedActivity> extractCurrentActivity(ActivityRecognitionResult result) {
        return result.getProbableActivities()
                .stream()
                .filter( a -> supportedActivities.contains(a.getType()))
                .filter(a -> a.getConfidence() >= confidenceThreshold )
                .max((a1, a2) -> Integer.compare(a1.getConfidence(), a2.getConfidence()));
    }

    private boolean hasNewActivityBeenStarted(DetectedActivity newActivity) {
        return currentActivity.map(detectedActivity -> detectedActivity.getType() == newActivity.getType()).orElse(true);
    }

    private void stopActivity() {
        try{
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("X-userId", user);
            urlConnection.setRequestProperty("X-token", token);
            try {
                InputStream s = urlConnection.getInputStream();
                s.close();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            receiverLogMessage += e.getMessage();
        }
    }

    private void startActivty(int type) {
        URL u = null;
        if (type == DetectedActivity.RUNNING) {
            try {
                u = new URL(url + "activity?type=RUNNING");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (type == DetectedActivity.ON_BICYCLE) {
            try {
                u = new URL(url + "activity?type=CYCLING");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (u != null) {
            try{
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("X-userId", user);
                urlConnection.setRequestProperty("X-token", token);
                try {
                    InputStream s = urlConnection.getInputStream();
                    s.close();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                receiverLogMessage += e.getMessage();
            }
        }
    }

}
