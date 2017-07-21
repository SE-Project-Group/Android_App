package com.example.android.track.Model;

/**
 * Created by jarvis on 2017/7/18.
 */

public class Comment {
    private int reply_id;
    private String portrait_url;
    private int user_id;
    private String user_name;
    private String comment_text;
    private String time;

    public Comment(int reply_id, String portrait_url, int user_id, String user_name, String comment_text, String time) {
        this.reply_id = reply_id;
        this.portrait_url = portrait_url;
        this.user_id = user_id;
        this.user_name = user_name;
        this.comment_text = comment_text;
        this.time = time;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }

    public String getPortrait_url() {
        return portrait_url;
    }

    public void setPortrait_url(String portrait_url) {
        this.portrait_url = portrait_url;
    }

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

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
