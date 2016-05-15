package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarthak.remindme.Config;
import com.example.sarthak.remindme.ObjectClasses.Note;
import com.example.sarthak.remindme.ObjectClasses.RecycleBinObject;
import com.example.sarthak.remindme.ObjectClasses.Reminder;
import com.example.sarthak.remindme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sarthak on 13/5/16.
 */
public class RecycleBinAdapter extends RecyclerView.Adapter<RecycleBinAdapter.CustomAdapterDeletedReminders> {
    private ArrayList<RecycleBinObject> recycleBinObjects;
    private Context context;
    private Random random;
    private Note note;
    private Reminder reminder;

    public RecycleBinAdapter(ArrayList<RecycleBinObject> recycleBinObjects, Context context) {
        this.recycleBinObjects = recycleBinObjects;
        this.context = context;
    }

    @Override
    public CustomAdapterDeletedReminders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recycle_bin, parent, false);
        return new CustomAdapterDeletedReminders(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapterDeletedReminders holder, int position) {
        RecycleBinObject recycleBinObject = recycleBinObjects.get(position);
        if(recycleBinObject.getType().equals(Config.objectNote)){
            getNote(recycleBinObject);
            holder.setTitle(note.getNote());
            if(recycleBinObject.isSelected()){
                holder.getCardView().setCardBackgroundColor(Color.GRAY);
            }else{
                holder.getCardView().setCardBackgroundColor(Color.WHITE);
            }
        }
//        holder.setTitle(reminder.getTitle());
//        holder.setDescription(reminder.getDescription());
//        String modifiedText = "" + reminder.getDay() + " " + getMonth(reminder.getMonth()) + " , " + reminder.getYear();
//        holder.setDate(modifiedText);
//        if (reminder.getMinutes() == -1 || reminder.getHours() == -1)
//            modifiedText = "All day";
//        else
//            modifiedText=""+reminder.getHours()+":"+reminder.getMinutes();
//        holder.setTime(modifiedText);
    }

    private void getNote(RecycleBinObject recycleBinObject){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Config.notesDirectory,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=sharedPreferences.getString(Config.objectNote+recycleBinObject.getId(),"");
        if(json!=null && !json.isEmpty() && !json.trim().equals("")){
            note=gson.fromJson(json,Note.class);
        }
    }

    private String getMonth(int month) {
        String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    @Override
    public int getItemCount() {
        return recycleBinObjects.size();
    }

    public class CustomAdapterDeletedReminders extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView date;
        private TextView time;
        private CardView cardView;

        public CustomAdapterDeletedReminders(View view) {
            super(view);
            view.setClickable(true);
            this.title = (TextView) view.findViewById(R.id.textViewTitleDeletedEvents);
            this.description = (TextView) view.findViewById(R.id.textView_DeletedRemindersDescription);
            this.date = (TextView) view.findViewById(R.id.textView_DeletedRemindersDate);
            this.time = (TextView) view.findViewById(R.id.textView_DeletedRemindersTime);
            this.cardView=(CardView)view.findViewById(R.id.cardView_DeletedReminders);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }

        public void setDate(String date) {
            this.date.setText(date);
        }

        public void setTime(String time) {
            this.time.setText(time);
        }

        public CardView getCardView(){
            return cardView;
        }
    }
}


