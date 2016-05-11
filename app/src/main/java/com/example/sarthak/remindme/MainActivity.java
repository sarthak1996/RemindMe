package com.example.sarthak.remindme;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.sarthak.remindme.Adapters.UpcomingRemindersAdapter;

import java.util.ArrayList;

/**
 * Created by sarthak on 11/5/16.
 */
public class MainActivity extends AppCompatActivity {
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ArrayList<Reminder> reminders;
    private UpcomingRemindersAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Upcoming Reminders");
        /*RecyclerView For Upcoming Events*/
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUpComingReminders);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        initializeReminders();
        adapter = new UpcomingRemindersAdapter(reminders, MainActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Long Clicked", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initializeReminders() {
        reminders = new ArrayList<>();
        reminders.add(new Reminder("Temporary disturbance", 1203));
        reminders.add(new Reminder("Temporary disturbancealljdhkhsakdh ashdkhsakd hklahlkdh", 1203));
        reminders.add(new Reminder("Temporary disturbance", 1203));
        reminders.add(new Reminder("Temporary disturbance", 1203));
        reminders.add(new Reminder("Temporary disturbance", 1203));
        reminders.add(new Reminder("Temporary disturbance", 1203));
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}


