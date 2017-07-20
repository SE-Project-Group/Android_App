package com.example.android.android_app.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.android.android_app.Adapter.FollowAdapter;
import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.android_app.R.id.recyclerView;

public class FollowActivity extends AppCompatActivity {
    private List<Follow> followList = new ArrayList<>();
    private int who;

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

        if(relationship.equals("following"))
            initFollowings();
        if(relationship.equals("follower"))
            initFollowers();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.follwing_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FollowAdapter adapter = new FollowAdapter(followList, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    private void initFollowings(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private void initFollowers(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FOLLOW_OK:
                    break;
                case GET_FOLLOW_FAILED:
                    break;
            }
        }
    };
}
