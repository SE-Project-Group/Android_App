package com.example.android.track.Model.LitePal_Entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by thor on 2017/8/3.
 */

public class Acquaintance extends DataSupport {
    private int user_id;
    private String user_name;
    private Date updateTime;
    private String relationship;

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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
