package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarthak.remindme.ObjectClasses.Reminder;
import com.example.sarthak.remindme.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sarthak on 11/5/16.
 */
public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.CustomAdapterUpComingReminders> {
    private ArrayList<Reminder> reminders;
    private Context context;
    private Random random;

    public RemindersAdapter(ArrayList<Reminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
    }

    @Override
    public CustomAdapterUpComingReminders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_reminders, parent, false);
        return new CustomAdapterUpComingReminders(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapterUpComingReminders holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.setTitle(reminder.getTitle());
        holder.setDescription(reminder.getDescription());
        String modifiedText = "" + reminder.getDay() + " " + getMonth(reminder.getMonth()) + " , " + reminder.getYear();
        holder.setDate(modifiedText);
        if (reminder.getMinutes() == -1 || reminder.getHours() == -1)
            modifiedText = "All day";
        else
            modifiedText=""+reminder.getHours()+":"+reminder.getMinutes();
            holder.setTime(modifiedText);
    }

    private String getMonth(int month) {
        String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class CustomAdapterUpComingReminders extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView date;
        private TextView time;

        public CustomAdapterUpComingReminders(View view) {
            super(view);
            view.setClickable(true);
            this.title = (TextView) view.findViewById(R.id.textViewTitleEvents);
            this.description = (TextView) view.findViewById(R.id.textView_RemindersDescription);
            this.date = (TextView) view.findViewById(R.id.textView_RemindersDate);
            this.time = (TextView) view.findViewById(R.id.textView_RemindersTime);
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
    }
}


