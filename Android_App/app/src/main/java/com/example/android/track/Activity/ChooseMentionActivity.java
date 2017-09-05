package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.track.Adapter.FollowAdapter;
import com.example.android.track.Adapter.MentionAdapter;
import com.example.android.track.Adapter.RemindAdapter;
import com.example.android.track.Model.Follow;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thor on 2017/9/5.
 */

public class ChooseMentionActivity extends AppCompatActivity{
    private List<Follow> follows = new ArrayList<>();
    private List<Integer> chooseList = new ArrayList<>();
    private ProgressBar progressBar;

    private final static int GET_FOLLOW_OK = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mention);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Intent intent = getIntent();
        chooseList = intent.getIntegerArrayListExtra("chooseList"); // get choose list from last activity

        getAcquaintance();
    }

    private void getAcquaintance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                List<Acquaintance> acquaintanceList = DataSupport.select("*").find(Acquaintance.class);
                for(Acquaintance acquaintance : acquaintanceList){
                    Follow follow = new Follow();
                    follow.setUser_name(acquaintance.getUser_name());
                    follow.setUser_id(acquaintance.getUser_id());
                    follow.setState(acquaintance.getRelationship());
                    follow.setportrait_url(requester.getPortraitUrl(acquaintance.getUser_id()));
                }

                Message message = new Message();
                message.what = GET_FOLLOW_OK;
                handler.sendMessage(message);

            }
        }).start();
    }

    private void setRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.acquaintance_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MentionAdapter adapter = new MentionAdapter(follows, chooseList, ChooseMentionActivity.this);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FOLLOW_OK:
                    setRecyclerView();
                default:
                    break;
            }
        }
    };
}
