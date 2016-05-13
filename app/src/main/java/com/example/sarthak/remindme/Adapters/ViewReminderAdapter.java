package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sarthak.remindme.Config;
import com.example.sarthak.remindme.ObjectClasses.ReminderAndNotes;
import com.example.sarthak.remindme.ObjectClasses.ViewReminderObject;
import com.example.sarthak.remindme.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminderAdapter extends RecyclerView.Adapter<ViewReminderAdapter.CustomAdapterViewReminder> {
    private ArrayList<ViewReminderObject> viewReminderObjects;
    private ReminderAndNotes reminder;
    private Context context;
    private int flag;
    private int position;
    private SharedPreferences sharedPreferences;

    public ViewReminderAdapter(ArrayList<ViewReminderObject> viewReminderObjects, Context context, int flag,int position) {
        this.viewReminderObjects = viewReminderObjects;
        this.context = context;
        this.flag = flag;
        this.position=position;
    }

    @Override
    public CustomAdapterViewReminder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view_reminders, parent, false);
        sharedPreferences = context.getSharedPreferences(Config.prefName, Context.MODE_PRIVATE);
        reminder = new ReminderAndNotes();
        return new CustomAdapterViewReminder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapterViewReminder holder, int position) {
        ViewReminderObject viewReminderObject = viewReminderObjects.get(position);
        holder.setTitle(viewReminderObject.getTitle());
        holder.setIcon(viewReminderObject.getIcon());
        if (flag == 1) {
            holder.setInfo(viewReminderObject.getInfo());
        }else{
            switch (position){
                case 0: holder.setInfo(reminder.getDescription());
                    break;
                case 1: holder.updateRetrievedDate();
                    break;
                default:break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return viewReminderObjects.size();
    }

    private void retrieveReminder(int position) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Config.objectReminder + position, "");
        if (json != null && !json.equals("") && !json.isEmpty()) {
            reminder = gson.fromJson(json, ReminderAndNotes.class);
        }
    }

    public class CustomAdapterViewReminder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView info;
        private ImageView icon;
        private TextView time;
        private ImageView iconClock;
        private View view;
        public CustomAdapterViewReminder(View itemView) {
            super(itemView);
            view=itemView;
            if (flag == 0) {
                retrieveReminder(position);
            }
            title = (TextView) itemView.findViewById(R.id.textView_ViewReminderTitleListElement);
            info = (TextView) itemView.findViewById(R.id.textView_ViewReminderInfoListElement);
            icon = (ImageView) itemView.findViewById(R.id.imageView_viewReminderListElement);
        }
        public void updateRetrievedDate(){
            /*Making the time visible*/
            time=(TextView)view.findViewById(R.id.textView_ViewReminderTime);
            iconClock=(ImageView)view.findViewById(R.id.imageView_clock_date_time_viewReminder);
            if(reminder.getMinutes()!=-1 && reminder.getHours()!=-1){
                time.setText(reminder.getHours()+":"+reminder.getMinutes());
                String modifiedText = "" + reminder.getDay() + " " + getMonth(reminder.getMonth()) + " , " + reminder.getYear();
                time.setVisibility(View.VISIBLE);
                info.setText(modifiedText);
                iconClock.setVisibility(View.VISIBLE);
            }
        }

        private String getMonth(int month) {
            String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            return months[month];
        }


        public String getTitle() {
            return title.getText().toString();
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public String getInfo() {
            return info.getText().toString();
        }

        public void setInfo(String info) {
            this.info.setText(info);
        }

        public void setIcon(int icon) {
            this.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), icon));
        }
    }
}
