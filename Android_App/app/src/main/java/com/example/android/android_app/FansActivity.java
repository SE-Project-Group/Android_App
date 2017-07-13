package com.example.android.android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Class.FollowingAdapter;
import com.example.android.android_app.Class.User;

import java.util.ArrayList;
import java.util.List;

public class FansActivity extends AppCompatActivity {
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        initUsers();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.follwing_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FollowingAdapter adapter = new FollowingAdapter(userList);
        recyclerView.setAdapter(adapter);
    }

    private void initUsers(){
        User userA = new User("wangtao", "I love helping others",R.drawable.exp_portrait,"FOLLOW");
        userList.add(userA);
        userList.add(userA);
        userList.add(userA);
        userList.add(userA);
        userList.add(userA);
        User userB = new User("hhhhh","I love coding",R.drawable.exp_pic,"FRIEND");
        userList.add(userB);
    }
}
