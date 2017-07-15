package com.example.android.android_app.Model;

import java.util.Date;


/**
 * Created by jarvis on 2017/7/12.
 */

public class Remind {
    private int user_id;
    private String user_name;
    private String user_portrait_url;
    private String author_name;
    private Date time;
    private String author_text;
    private String feed_id;
    private String type; // comment, like, share

    private String first_pic_url;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_portrait_url() {
        return user_portrait_url;
    }

    public void setUser_portrait_url(String user_portrait_url) {
        this.user_portrait_url = user_portrait_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthor_text() {
        return author_text;
    }

    public void setAuthor_text(String author_text) {
        this.author_text = author_text;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirst_pic_url() {
        return first_pic_url;
    }

    public void setFirst_pic_url(String first_pic_url) {
        this.first_pic_url = first_pic_url;
    }
}
