package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.android.track.Adapter.FollowAdapter;
import com.example.android.track.Model.Follow;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;

import java.util.List;

public class FollowActivity extends AppCompatActivity {
    private List<Follow> followList;
    private int who;
    private RecyclerView recyclerView;

    // message
    private final static int GET_FOLLOW_OK = 0;
    private final static int GET_FOLLOW_FAILED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String relationship = intent.getStringExtra("relationship");
        who = intent.getIntExtra("who",0);

        if(relationship.equals("following")) // his following list
            initFollowings();
        if(relationship.equals("follower")) // his follower list
            initFollowers();

    }

    private void initFollowings(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                followList = requester.getFollowing(who);
                Message message = new Message();
                if(followList == null)
                    message.what = GET_FOLLOW_FAILED;
                else
                    message.what = GET_FOLLOW_OK;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initFollowers(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                followList = requester.getFollower(who);
                Message message = new Message();
                if(followList == null)
                    message.what = GET_FOLLOW_FAILED;
                else
                    message.what = GET_FOLLOW_OK;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void setRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.follow_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FollowAdapter adapter = new FollowAdapter(followList, FollowActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FOLLOW_OK:
                    setRecyclerView();
                    break;
                case GET_FOLLOW_FAILED:
                    Toast.makeText(FollowActivity.this, "get follow list failed", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };
}
