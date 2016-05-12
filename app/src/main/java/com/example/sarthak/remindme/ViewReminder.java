package com.example.sarthak.remindme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.ViewReminderAdapter;
import com.example.sarthak.remindme.ObjectClasses.ViewReminderObject;

import java.util.ArrayList;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminder extends AppCompatActivity {
    private int position=-1;
    private RecyclerView recyclerView;
    private ViewReminderAdapter adapter;
    private int icons[]={R.drawable.ic_label_black_24dp,R.drawable.ic_alarm_on_black_24dp};
    private String title[]={"Description","Time"};
    private ArrayList<ViewReminderObject> viewReminderObject;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            position=intent.getExtras().getInt(Config.reminderAt);
        }
        setContentView(R.layout.activity_view_reminder);
        if(position==-1){
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
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_ViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewReminder.this));
        initializeListObjects();
        adapter=new ViewReminderAdapter(viewReminderObject,ViewReminder.this);
        recyclerView.setAdapter(adapter);
    }
    private void initializeListObjects(){

    }
}
