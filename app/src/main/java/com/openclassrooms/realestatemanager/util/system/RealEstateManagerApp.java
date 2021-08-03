package com.openclassrooms.realestatemanager.util.system;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDexApplication;

import com.openclassrooms.realestatemanager.util.Constants.Constants;

public class RealEstateManagerApp extends MultiDexApplication {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        RealEstateManagerApp.sContext = getApplicationContext();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_1,
                    Constants.VERBOSE_NOTIFICATION_CHANNEL_1_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(Constants.VERBOSE_NOTIFICATION_CHANNEL_1_DESCRIPTION);

            NotificationChannel channel2 = new NotificationChannel(
                    Constants.CHANNEL_ID_2,
                    Constants.VERBOSE_NOTIFICATION_CHANNEL_2_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription(Constants.VERBOSE_NOTIFICATION_CHANNEL_2_DESCRIPTION);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.createNotificationChannel(channel2);
            }
        }
    }

    public static Context getContext() {return RealEstateManagerApp.sContext;}
}
