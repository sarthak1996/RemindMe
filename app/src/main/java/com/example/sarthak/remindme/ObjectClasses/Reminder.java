package com.example.sarthak.remindme.ObjectClasses;

import java.util.Comparator;

/**
 * Created by sarthak on 11/5/16.
 */
public class Reminder implements Comparator<Reminder> {
    private String title="";
    private String description="";
    private long timeInMillis=-1;
    private int day=-1;
    private int month=-1;
    private int year=-1;
    private int minutes=-1;
    private int hours=-1;

    public Reminder() {

    }

    public Reminder(String title, long timeInMillis) {
        this.timeInMillis = timeInMillis;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }


    @Override
    public int compare(Reminder lhs, Reminder rhs) {
        if(lhs.timeInMillis<rhs.timeInMillis)
            return 1;
        return -1;
    }
}
