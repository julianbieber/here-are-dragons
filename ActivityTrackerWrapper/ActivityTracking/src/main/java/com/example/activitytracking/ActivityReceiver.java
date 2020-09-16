package com.example.activitytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityReceiver extends BroadcastReceiver {

    public ActivityReceiver() {

    };

    public static String receiverLogMessage;

    private int confidenceThreshold = 70;

    private List<Integer> supportedActivities = new ArrayList<>();

    private static Optional<DetectedActivity> currentActivity;
    private String url;
    private String user;
    private String token;

    public ActivityReceiver(String url, String user, String token) {
        if (receiverLogMessage == null) {
            receiverLogMessage = "";
            supportedActivities.add(DetectedActivity.ON_BICYCLE);
            supportedActivities.add(DetectedActivity.RUNNING);
            supportedActivities.add(DetectedActivity.ON_FOOT);
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
                receiverLogMessage += "Actiovity";
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
                .max((a1, a2) -> Integer.compare(a1.getConfidence(), a2.getConfidence())).map(a -> {
                    if (a.getType() == DetectedActivity.ON_FOOT) {
                        return new DetectedActivity(DetectedActivity.RUNNING, a.getConfidence()) ;
                    } else {
                        return a;
                    }
                });
    }

    private boolean hasNewActivityBeenStarted(DetectedActivity newActivity) {
        return currentActivity.map(detectedActivity -> detectedActivity.getType() == newActivity.getType()).orElse(true);
    }

    private void stopActivity() {
        new BackgroundDeleteRequest().execute();
    }

    private class BackgroundDeleteRequest extends AsyncTask<Void , Void, Void> {
        @Override
        protected Void doInBackground(Void... urls) {
            try {
                URL u = new URL(url + "activity");
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("X-userId", user);
                urlConnection.setRequestProperty("X-token", token);
                try {
                    InputStream s = urlConnection.getInputStream();
                    s.close();
                } finally {
                    urlConnection.disconnect();
                }

                try {
                    InputStream s = urlConnection.getInputStream();
                    s.close();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                receiverLogMessage += e.getClass().getName();
                receiverLogMessage += e.getMessage();
            }
            return null;
        }
    }

    private void startActivty(int type) {
        new BackgrounPutRequest().execute(Integer.valueOf(type));
    }

    private class BackgrounPutRequest extends AsyncTask<Integer , Void, Void> {
        @Override
        protected Void doInBackground(Integer... urls) {
            URL u = null;
            if (urls[0].intValue() == DetectedActivity.RUNNING) {
                try {
                    u = new URL(url + "activity?type=RUNNING");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            if (urls[0].intValue() == DetectedActivity.ON_BICYCLE) {
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
            return null;
        }
    }

}
