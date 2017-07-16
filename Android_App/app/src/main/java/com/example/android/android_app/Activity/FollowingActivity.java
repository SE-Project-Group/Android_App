package com.example.android.android_app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Adapter.FollowAdapter;
import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    private List<Follow> followList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        initFollows();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.follwing_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FollowAdapter adapter = new FollowAdapter(followList, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    private void initFollows(){
        Follow FollowA = new Follow("wangtao", "I love helping others",R.drawable.exp_portrait,"FOLLOWING");
        followList.add(FollowA);
        followList.add(FollowA);
        followList.add(FollowA);
        followList.add(FollowA);
        followList.add(FollowA);
        Follow FollowB = new Follow("hhhhh","I love coding",R.drawable.exp_pic,"FRIEND");
        followList.add(FollowB);
    }
}
