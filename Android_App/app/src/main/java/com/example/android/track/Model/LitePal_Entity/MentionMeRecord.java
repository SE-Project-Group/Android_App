package com.example.android.track.Model.LitePal_Entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by thor on 2017/8/2.
 */

public class MentionMeRecord extends DataSupport {
    private int user_id;
    private String user_name;
    private Date time;
    private String feed_id;
    private String status;

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


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
