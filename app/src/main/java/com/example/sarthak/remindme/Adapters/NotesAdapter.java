package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarthak.remindme.ObjectClasses.Note;
import com.example.sarthak.remindme.R;

import java.util.ArrayList;

/**
 * Created by sarthak on 13/5/16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.CustomNotesAdapter>  {
    private ArrayList<Note> notes;
    private Context context;

    public NotesAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @Override
    public CustomNotesAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_notes,parent,false);
        return new CustomNotesAdapter(view);
    }

    @Override
    public void onBindViewHolder(CustomNotesAdapter holder, int position) {
        Note note=notes.get(position);
        holder.setNotesText(note.getNote());
        if(note.isSelected()){
            holder.getCardView().setCardBackgroundColor(Color.GRAY);
        }else{
            holder.getCardView().setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    public class CustomNotesAdapter extends RecyclerView.ViewHolder{
        private TextView notesText;
        private CardView cardView;
        public CustomNotesAdapter(View itemView) {
            super(itemView);
            notesText=(TextView)itemView.findViewById(R.id.textViewNoteText);
            cardView=(CardView)itemView.findViewById(R.id.cardView_notes);
        }
        public void setNotesText(String text){
            this.notesText.setText(text);
        }
        public CardView getCardView(){
            return cardView;
        }
    }
}
