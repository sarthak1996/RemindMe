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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.NotesAdapter;
import com.example.sarthak.remindme.ObjectClasses.Note;
import com.example.sarthak.remindme.ObjectClasses.RecycleBinObject;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sarthak on 13/5/16.
 */
public class AllNotes extends AppCompatActivity {
    private final int ADD_NOTES_ACTIVITY = 1;
    private SharedPreferences sharedPreferences;
    private ArrayList<Note> notes;
    private SparseBooleanArray selectedItems;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private int lastReminderPosition = 0;
    private CardView cardView;
    private NotesAdapter adapter;
    private MenuItem deleteSelectedItems;
    private FloatingActionButton floatingActionButtonAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);

        /*Setting up toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.all_notes_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("All Notes");

        sharedPreferences = getSharedPreferences(Config.notesDirectory, MODE_PRIVATE);
        notes = new ArrayList<>();
        selectedItems = new SparseBooleanArray();

        /*RecyclerView For Deleted Events*/
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllNotes.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAllNotes);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        initializeNotes();
        adapter = new NotesAdapter(notes, AllNotes.this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(selectedItems.size()==0) {
                    Intent intent = new Intent(AllNotes.this, AddNote.class);
                    intent.putExtra(Config.noteAt, notes.get(position).getId());
                    intent.putExtra(Config.launchType, "VIEW");
                    startActivityForResult(intent, ADD_NOTES_ACTIVITY);
                }else{
                    cardView = (CardView) view.findViewById(R.id.cardView_notes);
                    if (selectedItems.get(position, false)) {
                        selectedItems.delete(position);
                        notes.get(position).setSelected(false);
                        if(selectedItems.size()==0){
                            deleteSelectedItems.setVisible(false);
                        }
                        Log.d("In non selected",""+position+","+selectedItems.size());
                    } else {
                        selectedItems.put(position, true);
                        notes.get(position).setSelected(true);
                        deleteSelectedItems.setVisible(true);
                        Log.d("In selected",""+position+","+selectedItems.size());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                cardView = (CardView) view.findViewById(R.id.cardView_notes);
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    notes.get(position).setSelected(false);
                    if(selectedItems.size()==0){
                        deleteSelectedItems.setVisible(false);
                    }
                    Log.d("In non selected",""+position+","+selectedItems.size());
                } else {
                    selectedItems.put(position, true);
                    notes.get(position).setSelected(true);
                    deleteSelectedItems.setVisible(true);
                    Log.d("In selected",""+position+","+selectedItems.size());
                }
                adapter.notifyDataSetChanged();
            }
        }));


        /*Setting up the drawer*/
        drawerLayout = (DrawerLayout) findViewById(R.id.all_notes_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.all_notes_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                if (menuItem.getTitle().equals(AllNotes.this.getString(R.string.reminder))) {
                    Intent intent = new Intent(AllNotes.this, AllReminders.class);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(AllNotes.this.getString(R.string.notes))) {
                    /*Do nothing since already in notes*/
                }
                if (menuItem.getTitle().equals(AllNotes.this.getString(R.string.upcoming_reminder))) {
                    Intent intent = new Intent(AllNotes.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (menuItem.getTitle().equals(AllNotes.this.getString(R.string.recycle_bin))) {
                    Intent intent = new Intent(AllNotes.this, RecycleBin.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        /*Setting up fab*/
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.fab_add_all_notes);
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllNotes.this, AddNote.class);
                intent.putExtra(Config.launchType, "ADD");
                startActivityForResult(intent, ADD_NOTES_ACTIVITY);
            }
        });
    }

    private void initializeNotes() {
        Gson gson = new Gson();
        Config.lastNoteId=sharedPreferences.getInt(Config.savedLastNotesId, Integer.MIN_VALUE);
        for (int i = Integer.MIN_VALUE; i <= Config.lastNoteId; i++) {
            String savedNote = sharedPreferences.getString(Config.objectNote + i, "");
            if (savedNote != null && !savedNote.isEmpty() && !savedNote.trim().equals("")) {
                Note retrievedNote = gson.fromJson(savedNote, Note.class);
                if (retrievedNote.isVisible())
                    notes.add(retrievedNote);
            }
        }
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
                deleteSelectedNotes();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(AllNotes.this, "heheh", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case ADD_NOTES_ACTIVITY:
                // Toast.makeText(AllNotes.this, "lala", Toast.LENGTH_SHORT).show();
                notes.clear();
                initializeNotes();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Config.savedLastNotesId, Config.lastNoteId);
        editor.commit();
    }

    public void deleteSelectedNotes(){
        Gson gson=new Gson();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        SharedPreferences recycleSharedPref=getSharedPreferences(Config.recycleBin,MODE_PRIVATE);
        SharedPreferences.Editor recycleEditor=recycleSharedPref.edit();

        for(int i=0;i<notes.size();i++){
            if(selectedItems.get(i,false)){
                Note modifiedNote=notes.get(i);
                modifiedNote.setVisible(false);

                String json= gson.toJson(modifiedNote);
                editor.putString(Config.objectNote+modifiedNote.getId(),json);
                editor.commit();


                RecycleBinObject obj=new RecycleBinObject();
                obj.setType(Config.objectNote);
                obj.setId(modifiedNote.getId());
                obj.setSelfId(Config.lastRecycleId);
                json=gson.toJson(obj);
                Config.lastRecycleId=recycleSharedPref.getInt(Config.savedLastRecycleId,Integer.MIN_VALUE);
                recycleEditor.putString(Config.objectRecycle+Config.lastRecycleId,json);
                recycleEditor.commit();
                Config.lastRecycleId++;
                recycleEditor.putInt(Config.savedLastRecycleId,Config.lastRecycleId);
                recycleEditor.commit();

            }
        }
        for (int i=notes.size()-1;i>=0;i--){
            if(selectedItems.get(i,false)) {
                selectedItems.delete(i);
                Log.d("In delete Notes",""+notes.get(i).getNote());
                notes.remove(i);
            }
        }
        selectedItems.clear();
        deleteSelectedItems.setVisible(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        deleteSelectedItems = (MenuItem) menu.findItem(R.id.action_delete_selected_items);
        return true;
    }
}
