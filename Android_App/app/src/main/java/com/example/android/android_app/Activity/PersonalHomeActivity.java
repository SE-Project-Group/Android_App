package com.example.android.android_app.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Model.UserInfo;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.FeedRequester;
import com.example.android.android_app.Util.UserRequester;
import com.example.android.android_app.Util.Verify;

import java.util.ArrayList;
import java.util.List;


public class PersonalHomeActivity extends AppCompatActivity {

    private List<Feed> feeds = new ArrayList<>();
    private final static int GET_FEEDS_OK = 1;
    private final static int GET_FEEDS_FAILED = 2;
    private final static int GET_INFO_OK = 3;
    private final static int GET_INFO_FAILED = 4;

    private UserInfo userInfo;
    private RecyclerView recyclerView;
    private int user_id;

    private Boolean ME = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);
        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // get who's home
        Intent intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);
        // check if myself
        Verify verify = new Verify();
        if (user_id == Integer.valueOf(verify.getUser_id()))
            ME = true;


        getUserInfo();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // decide which interface to user depend on my identification now
        // not me
        if(!ME){
            if (verify.getLoged())
                loggedGetOthersFeeds();
            else
                unLoggedGetOthersFeeds();
        }
        // it's me
        else{
            loggedGetOthersFeeds();       // all of my feed
        }


    }

    // need verify
    private void loggedGetOthersFeeds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requestServer = new FeedRequester();
                Message message = new Message();
                feeds = requestServer.loggedGetOnePersonFeeds(user_id);
                if(feeds == null)
                    message.what = GET_FEEDS_FAILED;
                else
                    message.what = GET_FEEDS_OK;
                handler.sendMessage(message);

            }
        }).start();
    }


    private void unLoggedGetOthersFeeds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requester = new FeedRequester();
                Message message = new Message();
                feeds = requester.unLoggedGetOnePersonFeeds(user_id);
                if(feeds == null)
                    message.what = GET_FEEDS_FAILED;
                else
                    message.what = GET_FEEDS_OK;
                handler.sendMessage(message);

            }
        }).start();
    }

    private void getUserInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                userInfo = requester.getUserInfo(user_id);
                Message message = new Message();
                if(userInfo == null)
                    message.what = GET_INFO_FAILED;
                else
                    message.what = GET_INFO_OK;
                handler.sendMessage(message);
            }
        }).start();
    }


    private void displayRecycleView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(PersonalHomeActivity.this, feeds);
        recyclerView.setAdapter(adapter);
    }

    private void displayUserInfo(){
        // find view
        ImageView portrait_view = (ImageView) findViewById(R.id.portrait);
        TextView user_name_view = (TextView) findViewById(R.id.user_name);
        TextView following_cnt_view = (TextView) findViewById(R.id.following_cnt);
        TextView follower_cnt_view = (TextView) findViewById(R.id.follower_cnt);
        TextView like_cnt_view = (TextView) findViewById(R.id.like_cnt);
        TextView share_cnt_view = (TextView) findViewById(R.id.share_cnt);

        // load
        Glide.with(this)
                .load(userInfo.getPortrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(portrait_view);
        user_name_view.setText(userInfo.getName());
        int temp = userInfo.getFollow_cnt();
        following_cnt_view.setText(String.valueOf(temp));
        temp = userInfo.getFollower_cnt();
        follower_cnt_view.setText(String.valueOf(temp));
        temp = userInfo.getLike_cnt();
        like_cnt_view.setText(String.valueOf(temp));
        temp = userInfo.getShare_cnt();
        share_cnt_view.setText(String.valueOf(temp));
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEEDS_OK:
                    displayRecycleView();
                    break;
                case GET_INFO_OK:
                    displayUserInfo();
            }
        }
    };
}
