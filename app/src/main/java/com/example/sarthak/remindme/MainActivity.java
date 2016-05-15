package com.example.sarthak.remindme;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sarthak.remindme.Adapters.RemindersAdapter;
import com.example.sarthak.remindme.ObjectClasses.Reminder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sarthak on 11/5/16.
 */
public class MainActivity extends AppCompatActivity {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ArrayList<Reminder> reminders;
    private RemindersAdapter adapter;
    private RecyclerView recyclerView;
    private SparseBooleanArray selectedItems;
    private CardView cardView;
    private int lastReminderPosition;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private MenuItem deleteSelectedItems;
    private FloatingActionButton floatingActionButtonAdd;
    private String choicesAdd[]={"Note","Reminder","Location Based Reminder"};
    private int selectionMadeAdd=-1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Setting up toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Upcoming Reminders");

        sharedPreferences = getSharedPreferences(Config.prefName, MODE_PRIVATE);
        reminders = new ArrayList<>();
        selectedItems = new SparseBooleanArray();

        /*RecyclerView For Upcoming Events*/
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUpComingReminders);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        initializeReminders(0);
        adapter = new RemindersAdapter(reminders, MainActivity.this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ViewReminder.class);
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
                    cardView.setCardBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.put(position, true);
                    cardView.setCardBackgroundColor(Color.GRAY);
                    deleteSelectedItems.setVisible(true);
                }
            }
        }));


        /*Setting up the drawer*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (menuItem.getTitle().equals(MainActivity.this.getString(R.string.reminder))) {
                    Intent intent=new Intent(MainActivity.this,AllReminders.class);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(MainActivity.this.getString(R.string.notes))) {
                    Intent intent=new Intent(MainActivity.this,AllNotes.class);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(MainActivity.this.getString(R.string.recycle_bin))) {
                    Intent intent=new Intent(MainActivity.this,RecycleBin.class);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(MainActivity.this.getString(R.string.upcoming_reminder))) {
                    /*Do nothing since already in the main activity*/
                }
                return true;
            }
        });

        /*Setting up Floating action button*/
        floatingActionButtonAdd=(FloatingActionButton)findViewById(R.id.fab_add_main);
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Type of item")
                        .items(choicesAdd)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                selectionMadeAdd=which;
                                Log.d("Check",""+selectionMadeAdd);
                                switch (selectionMadeAdd){
                                    case 0:
                                        break;
                                    case 1:
                                        Intent intent = new Intent(MainActivity.this, ViewReminder.class);
                                        intent.putExtra(Config.launchType, "ADD");
                                        intent.putExtra(Config.lastReminderPosition, lastReminderPosition);
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        break;
                                    default:break;

                                }
                                return true;
                            }
                        })
                        .positiveText("Choose")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
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
            Calendar calendar = Calendar.getInstance();
            if (rem.getDay() == calendar.get(Calendar.DAY_OF_MONTH) && rem.getMonth() == calendar.get(Calendar.MONTH) && rem.getYear() == calendar.get(Calendar.YEAR))
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
            case R.id.action_delete_selected_items:
                deleteSelectedItems.setVisible(false);
                SharedPreferences sharedPreferencesRecycle=getSharedPreferences(Config.recycleBin,MODE_PRIVATE);
                SharedPreferences.Editor recycleEditor=sharedPreferencesRecycle.edit();
                SharedPreferences.Editor editor=sharedPreferences.edit();
                for(int i=0;i<selectedItems.size();i++) {
                    Gson gson = new Gson();
                    String json = gson.toJson(reminders.get(0));
                    recycleEditor.putString(Config.objectReminder+i,json);
                    recycleEditor.commit();
                    editor.remove(Config.objectReminder+i);
                    editor.commit();
                    reminders.clear();
                    NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(i);
                    initializeReminders(0);
                    adapter.notifyDataSetChanged();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        deleteSelectedItems = (MenuItem) menu.findItem(R.id.action_delete_selected_items);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        reminders.clear();
        initializeReminders(0);
        adapter.notifyDataSetChanged();
    }
}


