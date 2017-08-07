package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.track.Model.Feed;
import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.Model.UserInfo;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


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
    private Boolean LOGGED = false;

    //view
    private Button add_follow_btn;
    private Button cancel_follow_btn;
    private Button also_follow_btn;
    private Button cancel_friend_btn;

    //requester
    private UserRequester userRequester = new UserRequester();

    //message
    private final static int FOLLOW_ACTION_FAILED = 5;
    private final static int CANCEL_FOLLOW_SUCCESS = 6;
    private final static int ADD_FOLLOW_SUCCESS = 7;
    private final static int ALSO_FOLLOW_SUCCESS = 8;
    private final static int CANCEL_FRIEND_SUCCESS = 9;


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

        initHomeInfo();
        setFollowActivityButton();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // decide which interface to user depend on my identification now
        // not me
        if(!ME){
            if (verify.getLoged()) {
                LOGGED = true;
                loggedGetOthersFeeds();
            }
            else
                unLoggedGetOthersFeeds();
        }
        // it's me
        else{
            loggedGetOthersFeeds();       // all of my feed
        }


    }

    private void setFollowActivityButton(){
        TextView his_following_btn = (TextView) findViewById(R.id.his_following_btn);
        TextView his_follower_btn = (TextView) findViewById(R.id.his_follower_btn);
        his_following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalHomeActivity.this, FollowActivity.class);
                intent.putExtra("relationship", "following");
                intent.putExtra("who", user_id);
                startActivity(intent);
            }
        });

        his_follower_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalHomeActivity.this, FollowActivity.class);
                intent.putExtra("relationship", "follower");
                intent.putExtra("who", user_id);
                startActivity(intent);
            }
        });
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

    private void initHomeInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfo = userRequester.getHomeInfo(user_id);
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
        CircleImageView portrait_view = (CircleImageView) findViewById(R.id.portrait);
        TextView user_name_view = (TextView) findViewById(R.id.user_name);
        TextView following_cnt_view = (TextView) findViewById(R.id.following_cnt);
        TextView follower_cnt_view = (TextView) findViewById(R.id.follower_cnt);
        TextView like_cnt_view = (TextView) findViewById(R.id.like_cnt);
        TextView share_cnt_view = (TextView) findViewById(R.id.share_cnt);

        // load portrait
        Glide.with(this)
                .load(userInfo.getPortrait_url())
                .asBitmap()
                .centerCrop()
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


        // if it's not my own home page, then set follow function button and chat button
        if(!ME)
            initInteractButton();
    }

    // if it's not my own home page, then set follow function button and chat button
    // to visible and set click listener for them
    private void initInteractButton(){
        boolean logged = new Verify().getLoged();
        // set correct follow button
        add_follow_btn = (Button) findViewById(R.id.add_follow_btn);
        cancel_follow_btn = (Button) findViewById(R.id.cancel_follow_btn);
        also_follow_btn = (Button) findViewById(R.id.also_follow_btn);
        cancel_friend_btn = (Button) findViewById(R.id.cancel_friend_btn);
        String relationship = userInfo.getRelationship();
        if(relationship.equals("stranger"))
            add_follow_btn.setVisibility(View.VISIBLE);
        if(relationship.equals("following"))
            cancel_follow_btn.setVisibility(View.VISIBLE);
        if(relationship.equals("followed"))
            also_follow_btn.setVisibility(View.VISIBLE);
        if(relationship.equals("friend"))
            cancel_friend_btn.setVisibility(View.VISIBLE);

        add_follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow(user_id, "stranger");
            }
        });
        also_follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow(user_id, "followed");
            }
        });
        cancel_follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFollow(user_id, "following");
            }
        });
        cancel_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFollow(user_id, "friend");
            }
        });


        // chat button
        Button chat_btn = (Button) findViewById(R.id.chat_btn);
        chat_btn.setVisibility(View.VISIBLE);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged){
                    Toast.makeText(PersonalHomeActivity.this, "not login", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(PersonalHomeActivity.this, TalkingActivity.class);
                intent.putExtra("with_who", user_id);
                startActivity(intent);
            }
        });
    }


    // follow
    private void follow(final int who, final String relationship){
        if(!LOGGED){
            Toast.makeText(PersonalHomeActivity.this, "not log in", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = userRequester.follow(who);
                Message message = new Message();
                if(result.equals("success")){
                    if(relationship.equals("stranger"))
                        message.what = ADD_FOLLOW_SUCCESS;
                    else
                        message.what = ALSO_FOLLOW_SUCCESS;
                }
                else
                    message.what = FOLLOW_ACTION_FAILED;
                handler.sendMessage(message);
            }
        }).start();
    }


    // cancel follow
    private void cancelFollow(final int who, final String relationship){
        if(!LOGGED){
            Toast.makeText(PersonalHomeActivity.this, "not log in", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = userRequester.cancel_follow(who);
                Message message = new Message();
                if(result.equals("success")) {
                    if(relationship.equals("friend"))
                        message.what = CANCEL_FRIEND_SUCCESS;
                    else
                        message.what = CANCEL_FOLLOW_SUCCESS;
                }
                else
                    message.what = FOLLOW_ACTION_FAILED;
                handler.sendMessage(message);
            }
        }).start();
    }

    // handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEEDS_OK:
                    displayRecycleView();
                    break;
                case GET_INFO_OK:
                    displayUserInfo();
                    break;
                case FOLLOW_ACTION_FAILED:
                    Toast.makeText(PersonalHomeActivity.this, "follow action failed", Toast.LENGTH_SHORT).show();
                    break;
                case CANCEL_FOLLOW_SUCCESS:
                    cancel_follow_btn.setVisibility(View.GONE);
                    add_follow_btn.setVisibility(View.VISIBLE);
                    break;
                case CANCEL_FRIEND_SUCCESS:
                    cancel_friend_btn.setVisibility(View.GONE);
                    also_follow_btn.setVisibility(View.VISIBLE);
                    break;
                case ADD_FOLLOW_SUCCESS:
                    add_follow_btn.setVisibility(View.GONE);
                    cancel_follow_btn.setVisibility(View.VISIBLE);
                    break;
                case ALSO_FOLLOW_SUCCESS:
                    also_follow_btn.setVisibility(View.GONE);
                    cancel_friend_btn.setVisibility(View.VISIBLE);

            }
        }
    };
}
