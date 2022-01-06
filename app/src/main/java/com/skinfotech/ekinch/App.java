package com.skinfotech.ekinch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.utils.GlobalSettings;

public class App extends Application {

    private GlobalSettings globalSettings;


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.FCM_CHANNEL_ID,
                    Constants.FCM_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public GlobalSettings getGlobalSettings() {
        if (globalSettings == null) {
            globalSettings = new GlobalSettings();
        }
        return globalSettings;
    }
}