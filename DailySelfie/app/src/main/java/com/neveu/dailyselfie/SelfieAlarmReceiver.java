package com.neveu.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SelfieAlarmReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "SelfieAlarmReceiver";

    // Notification Text Elements
    private final CharSequence mContentText = "Time to take another selfie...";
    private CharSequence mContentTitle;

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    public SelfieAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mContentTitle == null)
        {
            mContentTitle = DailySelfieActivity.getApplicationName(context);
        }

        mNotificationIntent = new Intent(context, DailySelfieActivity.class);
        mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the Notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(mContentText)
                .setContentText(mContentText)
                .setContentTitle(mContentTitle)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true)
                .setContentIntent(mContentIntent);

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

     }
}
