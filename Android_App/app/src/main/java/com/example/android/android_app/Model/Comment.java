package com.example.android.android_app.Model;

/**
 * Created by jarvis on 2017/7/18.
 */

public class Comment {
    private int portrait_id;
    private String user_name;
    private String comment_text;

    public Comment(int portrait_id,String user_name,String comment_text){
        this.portrait_id = portrait_id;
        this.user_name = user_name;
        this.comment_text = comment_text;
    }

    public int getPortrait_id() {
        return portrait_id;
    }

    public void setPortrait_id(int portrait_id) {
        this.portrait_id = portrait_id;
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
}
