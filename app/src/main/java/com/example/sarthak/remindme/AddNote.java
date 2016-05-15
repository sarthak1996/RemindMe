package com.example.sarthak.remindme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthak.remindme.ObjectClasses.Note;
import com.google.gson.Gson;

/**
 * Created by sarthak on 13/5/16.
 */
public class AddNote extends AppCompatActivity {
    private EditText editTextNote;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;
    private Note note;
    private int flag = 0;
    private int noteId;

    /*
    * Flag=0 add mode
    * Flag=1 View mode
    * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        Intent intent = getIntent();
        if (intent != null) {
            if (!intent.getExtras().getString(Config.launchType).equals("ADD")) {
                flag = 1;
            }
        }

        /*Setting up toolbar*/
        toolbar = (Toolbar) findViewById(R.id.add_notes_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add a Note");

        /*Initialising variables*/
        sharedPreferences = getSharedPreferences(Config.notesDirectory, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editTextNote = (EditText) findViewById(R.id.editText_note_addNote);
        note = new Note();
        if (flag == 1) {
            noteId = intent.getExtras().getInt(Config.noteAt);
            Toast.makeText(AddNote.this, "" + noteId, Toast.LENGTH_SHORT).show();
            retrieveNote();
            editTextNote.setText(note.getNote());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Toast.makeText(AddNote.this, "", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        note.setNote(editTextNote.getText().toString());
        if (note != null && !note.getNote().isEmpty() && !note.getNote().trim().equals("")) {
            if (flag == 1) {
                saveExisitingNote();
            } else {
                saveNote();
            }
        } else {
            /*Do nothing*/
        }
        setResult(RESULT_OK,new Intent());
        finish();
    }

    private void saveNote() {
        Gson gson = new Gson();
        note.setId(Config.lastNoteId);
        String json = gson.toJson(note);
        editor.putString(Config.objectNote + Config.lastNoteId, json);
        editor.commit();
        Config.lastNoteId++;
        //Toast.makeText(AddNote.this, ""+Config.lastNoteId, Toast.LENGTH_SHORT).show();
        editor.putInt(Config.savedLastNotesId,Config.lastNoteId);
        editor.commit();
    }

    public void retrieveNote() {
        String savedNote = sharedPreferences.getString(Config.objectNote + noteId, "");
        if (savedNote != null && !savedNote.isEmpty() && !savedNote.trim().equals("")) {
            Gson gson = new Gson();
            note = gson.fromJson(savedNote, Note.class);
        }
    }
    public void saveExisitingNote(){
        Gson gson = new Gson();
        note.setId(noteId);
        String json = gson.toJson(note);
        editor.putString(Config.objectNote + noteId, json);
        editor.commit();
    }
}
