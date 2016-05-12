package com.example.sarthak.remindme;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sarthak on 12/5/16.
 */
public class NotificationAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification=intent.getParcelableExtra(Config.NOTIFICATION);
        int id=intent.getIntExtra(Config.NOTIFICATION_ID,0);
        notificationManager.notify(id,notification);
    }
}
