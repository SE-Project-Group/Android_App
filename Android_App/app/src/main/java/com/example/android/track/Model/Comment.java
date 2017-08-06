package com.example.android.track.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jarvis on 2017/7/18.
 */

public class Comment implements Parcelable{
    private int reply_id;
    private int comment_id;
    private String portrait_url;
    private int user_id;
    private String user_name;
    private String comment_text;
    private String time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(reply_id);
        dest.writeInt(comment_id);
        dest.writeInt(user_id);
        dest.writeString(portrait_url);
        dest.writeString(user_name);
        dest.writeString(comment_text);
        dest.writeString(time);
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>(){
        @Override
        public Comment createFromParcel(Parcel source) {
            Comment comment = new Comment();
            comment.reply_id = source.readInt();
            comment.comment_id = source.readInt();
            comment.user_id = source.readInt();
            comment.portrait_url = source.readString();
            comment.user_name = source.readString();
            comment.comment_text = source.readString();
            comment.time = source.readString();
            return comment;
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };


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

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

}
