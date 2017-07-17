package com.example.android.android_app.Model;

import java.util.List;

/**
 * Created by thor on 2017/7/15.
 */

public class UserInfo {
    private int user_id;
    private String name;
    private String portrait_url;
    private int follow_cnt;
    private int follower_cnt;
    private int like_cnt;
    private int comment_cnt;
    private int share_cnt;
    private List<Feed> recent_feeds;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollow_cnt() {
        return follow_cnt;
    }

    public void setFollow_cnt(int follow_cnt) {
        this.follow_cnt = follow_cnt;
    }

    public int getFollower_cnt() {
        return follower_cnt;
    }

    public void setFollower_cnt(int follower_cnt) {
        this.follower_cnt = follower_cnt;
    }

    public int getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt(int like_cnt) {
        this.like_cnt = like_cnt;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public int getShare_cnt() {
        return share_cnt;
    }

    public void setShare_cnt(int share_cnt) {
        this.share_cnt = share_cnt;
    }

    public List<Feed> getRecent_feeds() {
        return recent_feeds;
    }

    public void setRecent_feeds(List<Feed> recent_feeds) {
        this.recent_feeds = recent_feeds;
    }

    public String getPortrait_url() {
        return portrait_url;
    }

    public void setPortrait_url(String portrait_url) {
        this.portrait_url = portrait_url;
    }
}
