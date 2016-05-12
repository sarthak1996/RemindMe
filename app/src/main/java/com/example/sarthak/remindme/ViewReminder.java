package com.example.sarthak.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.ViewReminderAdapter;
import com.example.sarthak.remindme.ObjectClasses.ViewReminderObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminder extends AppCompatActivity {
    private int position = -1;
    private RecyclerView recyclerView;
    private ViewReminderAdapter adapter;
    private int flag = 0;
    private Calendar calendar;
    private int icons[] = {R.drawable.ic_label_black_24dp, R.drawable.ic_alarm_on_black_24dp};
    private String title[] = {"Description", "Time"};
    private ArrayList<ViewReminderObject> viewReminderObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            if (!intent.getExtras().getString(Config.launchType).equals("ADD"))
                position = intent.getExtras().getInt(Config.reminderAt);
            else
                flag = 1;
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

        /*Setting up recycler view*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_ViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewReminder.this));
        initializeListObjects();
        adapter = new ViewReminderAdapter(viewReminderObject, ViewReminder.this);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0: //open floating edittext
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
                                callTimePickerDialog();
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
        super.onBackPressed();
        saveReminderToSharedPref();
    }

    private void saveReminderToSharedPref() {

    }

    private void callTimePickerDialog() {
        calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        };
        new TimePickerDialog(ViewReminder.this,time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
}
