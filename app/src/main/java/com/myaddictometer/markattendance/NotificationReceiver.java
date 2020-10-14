package com.myaddictometer.markattendance;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channel_id = context.getString(R.string.CHANNEL_ID);
        long time = intent.getLongExtra(context.getString(R.string.EXTRA_TIME_KEY), 0);

        String title = "Mark Attendance";
        String message = "It's time to mark your attendance. Click to open app.";
        notify_user(context, channel_id, SettingsActivity.SettingsFragment.NOTIFICATION_ID, title, message);

    }

    private void notify_user(Context context, String CHANNEL_ID, int notifID, String title, String message){
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setAction("ACTION_OK");
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message).setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notifyPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notifID, builder.build());
    }
}
