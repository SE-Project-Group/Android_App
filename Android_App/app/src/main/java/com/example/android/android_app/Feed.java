package com.example.android.android_app;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by thor on 2017/6/28.
 */

public class Feed implements Serializable{
    private String feed_owner;
    private Timestamp timestamp;
    private String text;
    private int like_cnt;
    private int share_cnt;
    private int comment_cnt;
    private List<Integer> pic_id_list;
    private int portrait_id;
    private String position;
    private double latitude;
    private double longtitude;

    public Feed(String position,String feed_owner, Timestamp timestamp, String text, int like_cnt, int comment_cnt,int share_cnt, List<Integer> pic_id_list, int portrait_id) {
        this.position = position;
        this.feed_owner = feed_owner;
        this.timestamp = timestamp;
        this.text = text;
        this.like_cnt = like_cnt;
        this.share_cnt = share_cnt;
        this.comment_cnt = comment_cnt;
        this.pic_id_list = pic_id_list;
        this.portrait_id = portrait_id;
    }

    public Feed(String feed_owner, Timestamp timestamp, String text, int like_cnt, int share_cnt, int comment_cnt, List<Integer> pic_id_list, int portrait_id, String position, double latitude, double longtitude) {
        this.feed_owner = feed_owner;
        this.timestamp = timestamp;
        this.text = text;
        this.like_cnt = like_cnt;
        this.share_cnt = share_cnt;
        this.comment_cnt = comment_cnt;
        this.pic_id_list = pic_id_list;
        this.portrait_id = portrait_id;
        this.position = position;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongtitude(){
        return longtitude;
    }

    public String getFeed_owner() {
        return feed_owner;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public int getLike_cnt() {
        return like_cnt;
    }

    public int getShare_cnt() {
        return share_cnt;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public List<Integer> getPic_id_list() {
        return pic_id_list;
    }

    public int getPortrait_id(){
        return portrait_id;
    }

    public String getPosition(){
        return position;
    }
}
