package com.example.sarthak.remindme;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sarthak.remindme.Adapters.ViewReminderAdapter;
import com.example.sarthak.remindme.ObjectClasses.ReminderAndNotes;
import com.example.sarthak.remindme.ObjectClasses.ViewReminderObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminder extends AppCompatActivity {
    private int position = -1;
    private RecyclerView recyclerView;
    private ViewReminderAdapter adapter;
    private ReminderAndNotes reminder;
    private int lastReminderPosition;
    private int flag = 0;
    private Calendar calendar;
    private int icons[] = {R.drawable.ic_label_black_24dp, R.drawable.ic_alarm_on_black_24dp};
    private String title[] = {"Description", "Time"};
    private ArrayList<ViewReminderObject> viewReminderObject;
    private String modifiedText;
    private View itemView;
    private TextView textViewTitle;
    private SharedPreferences.Editor prefEditor;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton fabEditTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            if (!intent.getExtras().getString(Config.launchType).equals("ADD"))
                position = intent.getExtras().getInt(Config.reminderAt);
            else
                flag = 1;
            lastReminderPosition = intent.getExtras().getInt(Config.lastReminderPosition);
        }
        setContentView(R.layout.activity_view_reminder);
        if (position == -1 && flag == 0) {
            Toast.makeText(ViewReminder.this, "Unexpected Error occured", Toast.LENGTH_SHORT).show();
            finish();
        }

        /*Setting up toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewReminder_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        reminder = new ReminderAndNotes();
        sharedPreferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
        textViewTitle = (TextView) findViewById(R.id.textView_ViewReminderTitle);
        if (flag == 0) {
            retrieveReminder(position);
            textViewTitle.setText(reminder.getTitle());
        }
        /*Setting up recycler view*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_ViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewReminder.this));
        initializeListObjects();
        adapter = new ViewReminderAdapter(viewReminderObject, ViewReminder.this, flag, position);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                itemView = view;
                switch (position) {
                    case 0:
                        /*Opening Dialog to add description*/
                        boolean wrapInScrollView = true;
                        new MaterialDialog.Builder(ViewReminder.this)
                                .title("Description")
                                .customView(R.layout.dialog_edit_title, wrapInScrollView)
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.editText_ViewReminderTitle);
                                        reminder.setDescription(editText.getText().toString());
                                        TextView description = (TextView) itemView.findViewById(R.id.textView_ViewReminderInfoListElement);
                                        description.setText(editText.getText().toString());
                                    }
                                })
                                .show();
                        break;
                    case 1:
                        calendar = Calendar.getInstance();
                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                reminder.setDay(dayOfMonth);
                                reminder.setYear(year);
                                reminder.setMonth(monthOfYear);
                                reminder.setTimeInMillis(calendar.getTimeInMillis());
                                modifiedText = "" + reminder.getDay() + " " + getMonth(reminder.getMonth()) + " , " + reminder.getYear();
                                updateText(R.id.textView_ViewReminderInfoListElement, modifiedText, itemView, false, "@");
                                callTimePickerDialog(itemView);
                            }
                        };
                        new DatePickerDialog(ViewReminder.this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        /*Setting up floating action button*/
        fabEditTitle = (FloatingActionButton) findViewById(R.id.fab_editTitle_Reminder);
        fabEditTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(ViewReminder.this)
                        .title("Title")
                        .customView(R.layout.dialog_edit_title, wrapInScrollView)
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.editText_ViewReminderTitle);
                                reminder.setTitle(editText.getText().toString());
                                textViewTitle.setText(editText.getText().toString());
                            }
                        })
                        .show();
            }
        });
    }

    private void initializeListObjects() {
        viewReminderObject = new ArrayList<>();
        if (flag == 1) {
            for (int i = 0; i < icons.length; i++)
                viewReminderObject.add(new ViewReminderObject(icons[i], title[i], "Click to add " + title[i]));
        } else {
            for (int i = 0; i < icons.length; i++)
                viewReminderObject.add(new ViewReminderObject(icons[i], title[i], "Get Sharred Pref from postiotion"));
        }
    }

    @Override
    public void onBackPressed() {
        if (reminder != null && !reminder.equals("") && reminder.getTitle().trim().length() != 0) {
            if (reminder.getDay() != -1 && reminder.getMonth() != -1 && reminder.getYear() != -1) {
                super.onBackPressed();
                if (flag == 1) {
                    saveReminderToSharedPref(lastReminderPosition);
                } else {
                    saveReminderToSharedPref(position);
                }
            } else {
                Toast.makeText(ViewReminder.this, "Enter date", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void saveReminderToSharedPref(int pos) {
        prefEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminder);
        prefEditor.putString(Config.objectReminder + pos, json);
        prefEditor.commit();
    }

    private void callTimePickerDialog(View view) {
        calendar = Calendar.getInstance();
        itemView = view;
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                reminder.setHours(hourOfDay);
                reminder.setMinutes(minute);
                reminder.setTimeInMillis(calendar.getTimeInMillis());
                modifiedText += "@" + reminder.getHours() + ":" + reminder.getMinutes();
                updateText(R.id.textView_ViewReminderInfoListElement, modifiedText, itemView, true, "@");
                createNotification();
            }
        };
        reminder.setMinutes(-1);
        reminder.setHours(-1);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView_clock_date_time_viewReminder);
        imageView.setVisibility(View.GONE);
        TextView timeTextView = (TextView) itemView.findViewById(R.id.textView_ViewReminderTime);
        timeTextView.setVisibility(View.GONE);
        new TimePickerDialog(ViewReminder.this, time, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();

    }

    private void updateText(int id, String text, View view, boolean split, String regex) {
        TextView textView = (TextView) view.findViewById(id);
        String[] splitArray = {};
        if (split) {
            splitArray = text.split(regex);
            /*Make the visibility of the icon of time*/
            if (regex.equals("@")) {
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView_clock_date_time_viewReminder);
                imageView.setVisibility(View.VISIBLE);
                TextView timeTextView = (TextView) view.findViewById(R.id.textView_ViewReminderTime);
                timeTextView.setVisibility(View.VISIBLE);
                timeTextView.setText(splitArray[1]);
                textView.setText(splitArray[0]);
            }
        } else {
            textView.setText(text);
        }
        return;
    }

    private String getMonth(int month) {
        String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    private void createNotification() {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(reminder.getTitle());
        builder.setContentText(reminder.getDescription());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setSound(uri);
        PendingIntent intent = PendingIntent.getActivity(ViewReminder.this, 0, new Intent(ViewReminder.this, MainActivity.class), 0);
        builder.setContentIntent(intent);
        Intent remindMeAgainIntent=new Intent(ViewReminder.this, RemindMeAgainNotification.class);
        remindMeAgainIntent.putExtra(Config.reminderAt,position);
        PendingIntent remindMeAgainIn = PendingIntent.getActivity(ViewReminder.this, 0,remindMeAgainIntent , PendingIntent.FLAG_UPDATE_CURRENT);
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

    private void retrieveReminder(int position) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Config.objectReminder + position, "");
        if (json != null && !json.equals("") && !json.isEmpty()) {
            reminder = gson.fromJson(json, ReminderAndNotes.class);
        }
    }

}
