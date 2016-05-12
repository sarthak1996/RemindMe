package com.example.sarthak.remindme.ObjectClasses;

/**
 * Created by sarthak on 12/5/16.
 */
public class ViewReminderObject {
    private int icon;
    private String title;
    private String info;

    public ViewReminderObject(int icon, String title, String info){
        this.icon=icon;
        this.info=info;
        this.title=title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
