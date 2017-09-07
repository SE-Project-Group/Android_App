package com.example.android.track.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.android.track.Adapter.RemindAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.MyFeed;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Model.Remind;
import com.example.android.track.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by thor on 2017/8/3.
 */

public class RemindActivity extends AppCompatActivity {
    private List<Remind> remindList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        clearUnReadCnt(type);  // clear unread cnt store in MyApplication
        initReminds(type);

    }

    private void clearUnReadCnt(String type){
        switch (type){
            case "like":
                MyApplication.setUnReadLikenCnt(0);
                break;
            case "comment":
                MyApplication.setUnReadCommentCnt(0);
                break;
            case "share":
                MyApplication.setUnReadShareCnt(0);
                break;
            case "mention":
                MyApplication.setUnReadMentionCnt(0);
                break;
            default:
                break;
        }
    }

    private void initReminds(String type){
        if(type.equals("like")){
            List<LikeMeRecord> recordList = DataSupport.findAll(LikeMeRecord.class);
            Collections.reverse(recordList);  // reverse list
            for(LikeMeRecord record : recordList){
                Remind remind = new Remind();
                remind.setUser_id(record.getUser_id());
                remind.setUser_name(record.getUser_name());
                remind.setTime(record.getTime());
                remind.setFeed_id(record.getFeed_id());
                List<MyFeed> myFeeds = DataSupport.select("text").where("feed_id = ?", record.getFeed_id()).find(MyFeed.class);
                if(myFeeds.size() == 0)
                    continue;  // if this machine has no data about this feed, then drop it

                MyFeed myFeed = myFeeds.get(0);
                remind.setAuthor_text(myFeed.getText());
                SharedPreferences pref = getSharedPreferences("logIn_data", MODE_PRIVATE);
                remind.setAuthor_name(pref.getString("user_name", ""));
                remind.setType("like");

                remindList.add(remind);
            }
        }

        if(type.equals("comment")){
            List<CommentMeRecord> recordList = DataSupport.findAll(CommentMeRecord.class);
            Collections.reverse(recordList);  // reverse list
            for(CommentMeRecord record : recordList) {
                Remind remind = new Remind();
                remind.setUser_id(record.getUser_id());
                remind.setUser_name(record.getUser_name());
                remind.setTime(record.getTime());
                remind.setComment_text(record.getComment_text());
                remind.setComment_id(record.getComment_id());
                remind.setFeed_id(record.getFeed_id());
                List<MyFeed> myFeeds = DataSupport.select("text").where("feed_id = ?", record.getFeed_id()).find(MyFeed.class);
                if(myFeeds.size() == 0)
                    continue;  // if this machine has no data about this feed, then drop it
                MyFeed myFeed = myFeeds.get(0);
                remind.setAuthor_text(myFeed.getText());
                SharedPreferences pref = getSharedPreferences("logIn_data", MODE_PRIVATE);
                remind.setAuthor_name(pref.getString("user_name", ""));
                remind.setType("comment");

                remindList.add(remind);
            }
        }

        if(type.equals("share")){
            List<ShareMeRecord> recordList = DataSupport.findAll(ShareMeRecord.class);
            Collections.reverse(recordList);  // reverse list
            for(ShareMeRecord record : recordList) {
                Remind remind = new Remind();
                remind.setUser_id(record.getUser_id());
                remind.setUser_name(record.getUser_name());
                remind.setTime(record.getTime());
                remind.setComment_text(record.getShare_comment());
                remind.setFeed_id(record.getFeed_id());
                List<MyFeed> myFeeds = DataSupport.select("text").where("feed_id = ?", record.getFeed_id()).find(MyFeed.class);
                if(myFeeds.size() == 0)
                    continue;  // if this machine has no data about this feed, then drop it
                MyFeed myFeed = myFeeds.get(0);
                remind.setAuthor_text(myFeed.getText());
                SharedPreferences pref = getSharedPreferences("logIn_data", MODE_PRIVATE);
                remind.setAuthor_name(pref.getString("user_name", ""));
                remind.setType("share");

                remindList.add(remind);
            }
        }

        if(type.equals("mention")){
            List<MentionMeRecord> recordList = DataSupport.findAll(MentionMeRecord.class);
            Collections.reverse(recordList);  // reverse list
            for(MentionMeRecord record : recordList) {
                Remind remind = new Remind();
                remind.setUser_id(record.getUser_id());
                remind.setUser_name(record.getUser_name());
                remind.setTime(record.getTime());
                remind.setFeed_id(record.getFeed_id());
                remind.setAuthor_name(record.getUser_name());
                remind.setAuthor_text(record.getText());
                remind.setType("mention");
                remindList.add(remind);
            }
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.remind_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RemindAdapter adapter = new RemindAdapter(remindList, RemindActivity.this);
        recyclerView.setAdapter(adapter);
    }
}
