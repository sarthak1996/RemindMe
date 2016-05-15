package com.example.sarthak.remindme.ObjectClasses;

/**
 * Created by sarthak on 13/5/16.
 */
public class RecycleBinObject {
    private String type;
    private int id;
    private int selfId;
    private boolean selected;

    public RecycleBinObject() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSelfId() {
        return selfId;
    }

    public void setSelfId(int selfId) {
        this.selfId = selfId;
    }
}
