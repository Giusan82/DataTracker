package com.example.android.datatracker.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.datatracker.MainActivity;
import com.example.android.datatracker.R;


public class NotificationsTask {
    private static final int NOTIFICATION_PENDING_INTENT_ID = 200;
    private static final int NOTIFICATION_ID = 100;
    private static final String NOTIFICATION_CHANNEL_ID = "task_notification";

    private static PendingIntent notificationsIntent(final Context context) {
        //this intent will open the MainActivity when clicked on a notification
        final Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, NOTIFICATION_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_round);
        return largIcon;
    }

    public static void execute(Context context) {
        NotificationManager nt_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //this check if the SDK version is for Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            nt_manager.createNotificationChannel(mChannel);
        }
        //this build the notification
        int icon;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = R.drawable.ic_insert_chart_white_24dp;
        } else {
            icon = R.drawable.ic_insert_chart_24dp;
        }
        NotificationCompat.Builder nt_builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(icon)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_desc))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.notification_desc)))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setContentIntent(notificationsIntent(context))
                .setAutoCancel(true);

        //this check if the SDK version is greater than Jelly Bean but lower than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            nt_builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            nt_manager.notify(NOTIFICATION_ID, nt_builder.build());
        }
    }
}
