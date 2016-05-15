package com.example.sarthak.remindme.ObjectClasses;

/**
 * Created by sarthak on 13/5/16.
 */
public class Note {
    private String note;
    private int id;
    private boolean visible;
    private boolean selected;

    public Note() {
        note = "";
        id = Integer.MIN_VALUE;
        visible = true;
        selected=false;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
