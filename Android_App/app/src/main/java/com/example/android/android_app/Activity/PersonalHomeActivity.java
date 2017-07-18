package com.example.android.android_app.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.bumptech.glide.Glide;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Model.UserInfo;
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.Verify;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.android_app.R.drawable.like;

public class PersonalHomeActivity extends AppCompatActivity {
    private List<Feed> feeds = new ArrayList<>();
    private final static int GET_FEEDS_OK = 1;
    private final static int GET_FEEDS_FAILED = 2;
    private final static int GET_INFO_OK = 3;
    private final static int GET_INFO_FAILED = 4;

    private UserInfo userInfo;
    private RecyclerView recyclerView;
    private int user_id;

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
        getUserInfo();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        getFeeds();

    }

    private void getFeeds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestServer requestServer = new RequestServer(new Verify(PersonalHomeActivity.this));
                Message message = new Message();
                feeds = requestServer.getMyFeed();
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
                RequestServer requestServer = new RequestServer(new Verify(PersonalHomeActivity.this));
                userInfo = requestServer.getUserInfo(user_id);
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
        // show myFeeds
        FeedAdapter adapter = new FeedAdapter(getApplicationContext(), feeds);
        recyclerView.setAdapter(adapter);
    }

    private void displayUserInfo(){
        // find view
        ImageView portrait_view = (ImageView) findViewById(R.id.portrait);
        TextView user_name_view = (TextView) findViewById(R.id.user_name);
        TextView following_cnt_view = (TextView) findViewById(R.id.following_cnt);
        TextView follower_cnt_view = (TextView) findViewById(R.id.follower_cnt);
        TextView like_cnt_view = (TextView) findViewById(R.id.like_btn);
        TextView share_cnt_view = (TextView) findViewById(R.id.share_cnt);

        // load
        Glide.with(this)
                .load(userInfo.getPortrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(portrait_view);
        user_name_view.setText(userInfo.getName());
        following_cnt_view.setText(userInfo.getFollow_cnt());
        follower_cnt_view.setText(userInfo.getFollower_cnt());
        like_cnt_view.setText(userInfo.getLike_cnt());
        share_cnt_view.setText(userInfo.getShare_cnt());
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
