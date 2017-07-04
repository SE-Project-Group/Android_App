package com.example.android.android_app;


import org.w3c.dom.Comment;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by thor on 2017/6/28.
 */

public class Feed {

    private int feed_id;
    private String feed_owner;
    private Timestamp timestamp;
    private String text;
    private int like_cnt;
    private int share_cnt;
    private int comment_cnt;
    private List<Integer> pic_id_list;
    private int portrait_id;
    private String position;

    public Feed(String position,int feed_id, String feed_owner, Timestamp timestamp, String text, int like_cnt, int comment_cnt,int share_cnt, List<Integer> pic_id_list, int portrait_id) {
        this.position = position;
        this.feed_id = feed_id;
        this.feed_owner = feed_owner;
        this.timestamp = timestamp;
        this.text = text;
        this.like_cnt = like_cnt;
        this.share_cnt = share_cnt;
        this.comment_cnt = comment_cnt;
        this.pic_id_list = pic_id_list;
        this.portrait_id = portrait_id;
    }

    public int getFeed_id() {
        return feed_id;
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
