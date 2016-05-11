package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarthak.remindme.R;
import com.example.sarthak.remindme.Reminder;

import java.util.ArrayList;

/**
 * Created by sarthak on 11/5/16.
 */
public class UpcomingRemindersAdapter extends RecyclerView.Adapter<UpcomingRemindersAdapter.CustomAdapterUpComingReminders> {
    private ArrayList<Reminder> reminders;
    private Context context;

    public UpcomingRemindersAdapter(ArrayList<Reminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
    }

    @Override
    public CustomAdapterUpComingReminders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_upcoming_reminders, parent, false);
        return new CustomAdapterUpComingReminders(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapterUpComingReminders holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.setTitle(reminder.getTitle());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class CustomAdapterUpComingReminders extends RecyclerView.ViewHolder{
        private TextView title;

        public CustomAdapterUpComingReminders(View view) {
            super(view);
            view.setClickable(true);
            this.title = (TextView) view.findViewById(R.id.textViewTitleUpComingEvents);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

    }
}


