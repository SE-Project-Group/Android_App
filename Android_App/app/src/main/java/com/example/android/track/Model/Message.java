package com.example.android.track.Model;

import java.util.Date;

/**
 * Created by jarvis on 2017/7/12.
 */

public class Message {
    private int user_id;
    private String message_text;
    private Date date;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
