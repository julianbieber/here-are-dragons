package com.example.activitytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LocationReceiver extends BroadcastReceiver {

    public static String receiverLogMessage;

    public LocationReceiver() {

    }


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
            receiverLogMessage += e.getClass().getName();
        }
    }



    private void sendLocation(double longitude, double latitude) {
        new BackgroundRequest().execute(String.valueOf(longitude), String.valueOf(latitude));
    }

    private class BackgroundRequest extends AsyncTask<String , Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            try {
                URL u = new URL(url + "Position");
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("X-userId", user);
                urlConnection.setRequestProperty("X-token", token);
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write("{\"longitude\":");
                wr.write(urls[0]);
                wr.write(",\"latitude\":");
                wr.write(urls[1]);
                wr.write("}");
                wr.flush();

                try {
                    InputStream s = urlConnection.getInputStream();
                    s.close();
                } finally {
                    urlConnection.disconnect();
                    wr.close();
                }
            } catch (IOException e) {
                receiverLogMessage += e.getClass().getName();
                receiverLogMessage += e.getMessage();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
