package com.example.activitytracking;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SimpleService extends Service {

    public static Context context;
    private static boolean running = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!running) {
            Notification notification = createNotification();
            startForeground(1, notification);
            running = true;
        }
    }



    private Notification createNotification() {
        String notificationChannelId = "ENDLESS SERVICE CHANNEL";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Endless Service channel");
        channel.enableLights(true);

        notificationManager.createNotificationChannel(channel);

        Intent i = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        Notification.Builder builder = new Notification.Builder(
                context,
                notificationChannelId);


        return builder
                .setContentTitle("Endless Service")
                .setContentText("This is your favorite endless service working")
                .setContentIntent(pendingIntent)
                .setTicker("Ticker text")
                .build();
    }
}
