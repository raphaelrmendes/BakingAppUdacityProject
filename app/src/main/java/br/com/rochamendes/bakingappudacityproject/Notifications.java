package br.com.rochamendes.bakingappudacityproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {
    public static final String CHANNEL_1_ID = "Videos Playing";
    public static final String CHANNEL_2_ID = "Updating Recipes";
    public static final String CHANNEL_3_ID = "New Recipes";

    public static final int ID_Notification_Playing = 0;
    public static final int ID_Notification_Updating = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Videos Playing",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("Videos that are playing at the moment");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Update Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("Keeps you aware of updating");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "New Recipes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel3.setDescription("New Recipes available for you");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}