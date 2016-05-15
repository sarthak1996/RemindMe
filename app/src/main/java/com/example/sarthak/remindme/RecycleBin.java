package com.example.sarthak.remindme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.example.sarthak.remindme.Adapters.RecycleBinAdapter;
import com.example.sarthak.remindme.ObjectClasses.RecycleBinObject;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sarthak on 13/5/16.
 */
public class RecycleBin extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ArrayList<RecycleBinObject> recycleBinObjects;
    private SparseBooleanArray selectedItems;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private int lastReminderPosition=0;
    private CardView cardView;
    private RecycleBinAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);
         /*Setting up toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.recycle_bin_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Deleted Reminders");

        sharedPreferences = getSharedPreferences(Config.recycleBin, MODE_PRIVATE);
        recycleBinObjects = new ArrayList<>();
        selectedItems = new SparseBooleanArray();

        /*RecyclerView For Deleted Events*/
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecycleBin.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDeletedReminders);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        initializeRecycleBin();
        adapter = new RecycleBinAdapter(recycleBinObjects, RecycleBin.this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                cardView = (CardView) view.findViewById(R.id.cardView_DeletedReminders);
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    recycleBinObjects.get(position).setSelected(false);

                } else {
                    selectedItems.put(position, true);
                    cardView.setSelected(true);
                }
            }
        }));


        /*Setting up the drawer*/
        drawerLayout = (DrawerLayout) findViewById(R.id.recycle_bin_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.recycle_bin_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Toast.makeText(RecycleBin.this, "", Toast.LENGTH_SHORT).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (menuItem.getTitle().equals(RecycleBin.this.getString(R.string.reminder))) {
                    Intent intent = new Intent(RecycleBin.this, ViewReminder.class);
                    intent.putExtra(Config.launchType, "ADD");
                    intent.putExtra(Config.lastReminderPosition, lastReminderPosition);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(RecycleBin.this.getString(R.string.notes))) {
                    Intent intent=new Intent(RecycleBin.this,AllNotes.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }

                if (menuItem.getTitle().equals(RecycleBin.this.getString(R.string.upcoming_reminder))) {
                    Intent intent=new Intent(RecycleBin.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

    }

    private void initializeRecycleBin() {
        int lastRecycleId=sharedPreferences.getInt(Config.savedLastRecycleId,Integer.MIN_VALUE);
        Gson gson=new Gson();
        Config.lastRecycleId=lastRecycleId;
        for(int i=Integer.MIN_VALUE;i<=lastRecycleId;i++){
            String json=sharedPreferences.getString(Config.objectRecycle+i,"");
            if(json!=null && !json.isEmpty() && !json.trim().equals("")){
                recycleBinObjects.add(gson.fromJson(json,RecycleBinObject.class));
            }
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
        recycleBinObjects.clear();
        initializeRecycleBin();
        adapter.notifyDataSetChanged();
    }

}
