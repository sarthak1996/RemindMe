package com.example.sarthak.remindme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.RemindersAdapter;
import com.example.sarthak.remindme.ObjectClasses.Reminder;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sarthak on 12/5/16.
 */
public class AllReminders extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ArrayList<Reminder> reminders;
    private SparseBooleanArray selectedItems;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private int lastReminderPosition = 0;
    private CardView cardView;
    private RemindersAdapter adapter;
    private FloatingActionButton floatingActionButtonAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reminders);
         /*Setting up toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.all_reminders_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("All Reminders");

        sharedPreferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
        reminders = new ArrayList<>();
        selectedItems = new SparseBooleanArray();

        /*RecyclerView For Deleted Events*/
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllReminders.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAllReminders);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        initializeReminders(0);
        adapter = new RemindersAdapter(reminders, AllReminders.this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AllReminders.this, ViewReminder.class);
                intent.putExtra(Config.reminderAt, position);
                intent.putExtra(Config.launchType, "VIEW");
                intent.putExtra(Config.lastReminderPosition, lastReminderPosition);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                cardView = (CardView) view.findViewById(R.id.cardView_Reminders);
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    cardView.setSelected(false);
                } else {
                    selectedItems.put(position, true);
                    cardView.setSelected(true);
                }
            }
        }));


        /*Setting up the drawer*/
        drawerLayout = (DrawerLayout) findViewById(R.id.all_reminders_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.all_reminders_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (menuItem.getTitle().equals(AllReminders.this.getString(R.string.reminder))) {
                    /*Do nothing since already in reminder*/
                }
                if (menuItem.getTitle().equals(AllReminders.this.getString(R.string.notes))) {
                    Intent intent = new Intent(AllReminders.this, AllNotes.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }

                if (menuItem.getTitle().equals(AllReminders.this.getString(R.string.upcoming_reminder))) {
                    Intent intent = new Intent(AllReminders.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        /*Setting up fab*/
        floatingActionButtonAdd=(FloatingActionButton)findViewById(R.id.fab_add_all_reminders);
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllReminders.this, ViewReminder.class);
                intent.putExtra(Config.launchType, "ADD");
                intent.putExtra(Config.lastReminderPosition, lastReminderPosition);
                startActivity(intent);
            }
        });
    }

    private void initializeReminders(int pos) {
        int position = pos;
        while (true) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(Config.objectReminder + position, "");
            if (json == null || json.isEmpty() || json.trim().equals("")) {
                lastReminderPosition = position;
                break;
            }
            position++;
            Reminder rem = gson.fromJson(json, Reminder.class);
            reminders.add(rem);
        }
        //Collections.sort(reminders);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reminders.clear();
        initializeReminders(0);
        adapter.notifyDataSetChanged();
    }

}
