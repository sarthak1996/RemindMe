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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.RecycleBinAdapter;
import com.example.sarthak.remindme.ObjectClasses.Note;
import com.example.sarthak.remindme.ObjectClasses.RecycleBinObject;
import com.example.sarthak.remindme.ObjectClasses.Reminder;
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
    private MenuItem restoreSelectedItems;
    private MenuItem deleteSelectedItems;
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
                if(selectedItems.size()!=0){
                    cardView = (CardView) view.findViewById(R.id.cardView_DeletedReminders);
                    if (selectedItems.get(position, false)) {
                        selectedItems.delete(position);
                        recycleBinObjects.get(position).setSelected(false);
                        if(selectedItems.size()==0){
                            deleteSelectedItems.setVisible(false);
                            restoreSelectedItems.setVisible(false);
                        }
                        Log.d("In non selected",""+position+","+selectedItems.size());
                    } else {
                        selectedItems.put(position, true);
                        recycleBinObjects.get(position).setSelected(true);
                        deleteSelectedItems.setVisible(true);
                        restoreSelectedItems.setVisible(true);
                        Log.d("In selected",""+position+","+selectedItems.size());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                cardView = (CardView) view.findViewById(R.id.cardView_DeletedReminders);
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    recycleBinObjects.get(position).setSelected(false);
                    if(selectedItems.size()==0){
                        deleteSelectedItems.setVisible(false);
                        restoreSelectedItems.setVisible(false);
                    }
                    Log.d("In non selected",""+position+","+selectedItems.size());
                } else {
                    selectedItems.put(position, true);
                    recycleBinObjects.get(position).setSelected(true);
                    deleteSelectedItems.setVisible(true);
                    restoreSelectedItems.setVisible(true);
                    Log.d("In selected",""+position+","+selectedItems.size());
                }
                adapter.notifyDataSetChanged();
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
        Config.lastRecycleId=sharedPreferences.getInt(Config.savedLastRecycleId,Integer.MIN_VALUE);
        Gson gson=new Gson();
        int offset=Config.lastRecycleId-Integer.MIN_VALUE;
        Log.d("Last Recycle ID",""+offset);
        for(int i=Integer.MIN_VALUE;i<=Config.lastRecycleId;i++){
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
            case R.id.action_delete_selected_items_forever:
                deleteForever();
                return true;
            case R.id.action_restore_selected_items:
                restoreItems();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(Config.savedLastRecycleId,Config.lastRecycleId);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycle_bin, menu);
        deleteSelectedItems = (MenuItem) menu.findItem(R.id.action_delete_selected_items_forever);
        restoreSelectedItems=(MenuItem) menu.findItem(R.id.action_restore_selected_items);
        return true;
    }

    private void restoreItems(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        SharedPreferences reminderSharedPreferences=getSharedPreferences(Config.prefName,MODE_PRIVATE);
        SharedPreferences notesSharedPreferences=getSharedPreferences(Config.notesDirectory,MODE_PRIVATE);
        SharedPreferences.Editor reminderEditor=reminderSharedPreferences.edit();
        SharedPreferences.Editor notesEditor=notesSharedPreferences.edit();
        Note note;
        Reminder reminder;
        Gson gson=new Gson();
        for(int i=0;i<recycleBinObjects.size();i++){
            if(selectedItems.get(i,false)){
                int id=recycleBinObjects.get(i).getId();
                if(recycleBinObjects.get(i).getType().equals(Config.objectNote)){
                    String json=notesSharedPreferences.getString(Config.objectNote+id,"");
                    if(json!=null && !json.isEmpty() && !json.trim().equals("")){
                        note=gson.fromJson(json,Note.class);
                        note.setVisible(true);
                        note.setSelected(false);
                        json=gson.toJson(note);
                        notesEditor.putString(Config.objectNote+id,json);
                        notesEditor.commit();

                        /*Removing object from recycle bin*/
                        /*To check this portion of code*/
                        editor.remove(Config.objectRecycle+recycleBinObjects.get(i).getSelfId());
                        editor.commit();
                    }
                }else if(recycleBinObjects.get(i).getType().equals(Config.objectReminder)){

                }
            }
        }

        for(int i=recycleBinObjects.size()-1;i>=0;i--){
            if(selectedItems.get(i,false)){
                selectedItems.delete(i);
                recycleBinObjects.remove(i);
            }
        }
        selectedItems.clear();
        deleteSelectedItems.setVisible(false);
        restoreSelectedItems.setVisible(false);
        adapter.notifyDataSetChanged();
    }

    private void deleteForever(){

    }
}
