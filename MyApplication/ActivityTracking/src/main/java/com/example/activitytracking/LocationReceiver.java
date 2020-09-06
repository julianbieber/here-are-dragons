package com.example.activitytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationReceiver extends BroadcastReceiver {

    public static String receiverLogMessage;


    private String url;
    private String user;
    private String token;

    public LocationReceiver(String url, String user, String token) {
        if (receiverLogMessage == null) {
            receiverLogMessage = "";
            this.url = url;
            this.user = user;
            this.token = token;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                List<Location> locations = result.getLocations();
                for (Location location: locations) {
                    sendLocation(location.getLongitude(), location.getLatitude());
                }
            }
        } catch (Throwable e) {
            receiverLogMessage += e.getMessage();
        }
    }



    private void sendLocation(double longitude, double latitude) {
        try {
            URL u = new URL(url + "Position");
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("X-userId", user);
            urlConnection.setRequestProperty("X-token", token);
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write("{\"longitude\":");
            wr.write(String.valueOf(longitude));
            wr.write(",\"latitude\":");
            wr.write(String.valueOf(latitude));
            wr.write("}");
            wr.flush();

            try {
                InputStream s = urlConnection.getInputStream();
                s.close();
            } finally {
                urlConnection.disconnect();
                wr.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            receiverLogMessage += e.getMessage();
        }

    }

}
