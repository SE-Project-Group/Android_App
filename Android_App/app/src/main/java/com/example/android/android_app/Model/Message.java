package com.example.android.android_app.Model;

/**
 * Created by jarvis on 2017/7/12.
 */

public class Message {
    private String user_name;
    private String message_text;
    private int portrait_id;
    private String date;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public int getPortrait_id() {
        return portrait_id;
    }

    public void setPortrait_id(int portrait_id) {
        this.portrait_id = portrait_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message(String user_name,String message_text, String date,int portrait_id){
        this.user_name = user_name;
        this.message_text = message_text;
        this.portrait_id = portrait_id;
        this.date = date;
    }
}
