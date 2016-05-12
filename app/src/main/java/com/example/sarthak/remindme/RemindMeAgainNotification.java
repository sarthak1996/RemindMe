package com.example.sarthak.remindme;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.TimePicker;

import com.example.sarthak.remindme.ObjectClasses.Reminder;
import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by sarthak on 12/5/16.
 */
public class RemindMeAgainNotification extends AppCompatActivity {
    private Calendar calendar;
    private Reminder reminder;
    private SharedPreferences sharedPreferences;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.md_dialog_basic);
        sharedPreferences=getSharedPreferences(Config.prefName,MODE_PRIVATE);
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                reminder.setHours(hourOfDay);
                reminder.setMinutes(minute);
                reminder.setTimeInMillis(calendar.getTimeInMillis());
                createNotification();
            }
        };
        reminder.setMinutes(-1);
        reminder.setHours(-1);
        new TimePickerDialog(RemindMeAgainNotification.this, time, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();

    }
    private void retrieveReminder(int position) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Config.objectReminder + position, "");
        if (json != null && !json.equals("") && !json.isEmpty()) {
            reminder = gson.fromJson(json, Reminder.class);
        }
    }
    private void createNotification() {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(reminder.getTitle());
        builder.setContentText(reminder.getDescription());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setSound(uri);
        PendingIntent intent = PendingIntent.getActivity(RemindMeAgainNotification.this, 0, new Intent(RemindMeAgainNotification.this, MainActivity.class), 0);
        builder.setContentIntent(intent);

        PendingIntent remindMeAgainIn = PendingIntent.getActivity(RemindMeAgainNotification.this, 0, new Intent(RemindMeAgainNotification.this, RemindMeAgainNotification.class), 0);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_alarm_on_black_24dp, "Remind me again at", remindMeAgainIn).build();
        builder.addAction(action);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent notificationIntent = new Intent(this, NotificationAlarm.class);
        notificationIntent.putExtra(Config.NOTIFICATION_ID, position);
        notificationIntent.putExtra(Config.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
    }
}
