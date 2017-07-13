package com.example.android.android_app.Class;

/**
 * Created by jarvis on 2017/7/4.
 */

public class User {
    private String user_name;
    private String last_feed;
    private int portrait_id;
    private String state;

    public String getState() {
        return state;
    }

    public String getUser_name() {
        return user_name;
    }
    public String getLast_feed() {
        return last_feed;
    }
    public int getPortrait_id() {
        return portrait_id;
    }

    public User(String user_name,String last_feed, int portrait_id,String state){
        this.user_name = user_name;
        this.last_feed = last_feed;
        this.portrait_id = portrait_id;
        this.state = state;
    }

}
