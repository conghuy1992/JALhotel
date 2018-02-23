package com.baristapushsdk.entity;

public class Message {
    private String mess_id;
    private String date;
    private String title;
    private String message;
    private String link_open;
    private int state_open;

    public String getMess_id() {
        return mess_id;
    }

    public void setMess_id(String mess_id) {
        this.mess_id = mess_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getState_open() {
        return state_open;
    }

    public void setState_open(int state_open) {
        this.state_open = state_open;
    }

    public String getLink_open() {
        return link_open;
    }

    public void setLink_open(String link_open) {
        this.link_open = link_open;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
