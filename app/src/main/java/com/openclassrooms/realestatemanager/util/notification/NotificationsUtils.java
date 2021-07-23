package com.openclassrooms.realestatemanager.util.notification;

import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.openclassrooms.realestatemanager.R;

import static com.openclassrooms.realestatemanager.util.Constants.CHANNEL_ID_1;
import static com.openclassrooms.realestatemanager.util.Constants.CHANNEL_ID_2;

public class NotificationsUtils extends ContextWrapper {

    private NotificationManagerCompat notificationManager;
    private final Context mContext;


    public NotificationsUtils(Context base) {
        super(base);
        mContext = base;
        notificationManager = NotificationManagerCompat.from(base);
    }

    public void showWarning(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_1);
        builder
                .setSmallIcon(R.drawable.ic_circle_notifications_24)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public void showMessage(Context context, String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_info_24)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(2, notification);
    }
}
