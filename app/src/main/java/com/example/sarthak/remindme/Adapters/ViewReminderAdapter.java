package com.example.sarthak.remindme.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sarthak.remindme.ObjectClasses.ViewReminderObject;
import com.example.sarthak.remindme.R;

import java.util.ArrayList;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminderAdapter extends  RecyclerView.Adapter<ViewReminderAdapter.CustomAdapterViewReminder>{
    private ArrayList<ViewReminderObject> viewReminderObjects;
    private Context context;

    public ViewReminderAdapter(ArrayList<ViewReminderObject> viewReminderObjects, Context context){
        this.viewReminderObjects = viewReminderObjects;
        this.context=context;
    }

    @Override
    public CustomAdapterViewReminder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view_reminders, parent, false);
        return new CustomAdapterViewReminder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapterViewReminder holder, int position) {
        ViewReminderObject viewReminderObject = viewReminderObjects.get(position);
        holder.setTitle(viewReminderObject.getTitle());
        holder.setIcon(viewReminderObject.getIcon());
        holder.setInfo(viewReminderObject.getInfo());
    }

    @Override
    public int getItemCount() {
        return viewReminderObjects.size();
    }

    public class CustomAdapterViewReminder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView info;
        private ImageView icon;

        public CustomAdapterViewReminder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.textView_ViewReminderTitleListElement);
            info=(TextView)itemView.findViewById(R.id.textView_ViewReminderInfoListElement);
            icon=(ImageView)itemView.findViewById(R.id.imageView_viewReminderListElement);
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
            this.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),icon));
        }
    }
}
