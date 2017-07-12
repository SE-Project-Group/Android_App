package com.example.android.android_app.Class;

/**
 * Created by jarvis on 2017/7/12.
 */

public class Remind {
    private String user_name;
    private String author_name;
    private String time;
    private int user_portrait;
    private int author_portrait;
    private String remind_text;
    private String author_text;

    public Remind(String user_name,String author_name,String time, int author_portrait,int user_portrait,String remind_text,String author_text){
        this.user_name = user_name;
        this.author_name = author_name;
        this.time = time;
        this.author_portrait = author_portrait;
        this.user_portrait = user_portrait;
        this.remind_text = remind_text;
        this.author_text = author_text;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUser_portrait() {
        return user_portrait;
    }

    public void setUser_portrait(int user_portrait) {
        this.user_portrait = user_portrait;
    }

    public int getAuthor_portrait() {
        return author_portrait;
    }

    public void setAuthor_portrait(int author_portrait) {
        this.author_portrait = author_portrait;
    }

    public String getRemind_text() {
        return remind_text;
    }

    public void setRemind_text(String remind_text) {
        this.remind_text = remind_text;
    }

    public String getAuthor_text() {
        return author_text;
    }

    public void setAuthor_text(String author_text) {
        this.author_text = author_text;
    }
}
