package com.example.android.android_app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MyShareActivity extends AppCompatActivity {

    private List<Feed> feedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_share);
        initFeeds();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.my_share_View);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getApplicationContext(), feedList);
        recyclerView.setAdapter(adapter);
    }

    private void initFeeds(){
       /* Timestamp time = new Timestamp(System.currentTimeMillis());
        Feed exp = new Feed("","Root","Today is my birthday",time.toString(),0,0,0,1,R.drawable.exp_portrait);
        feedList.add(exp);
        feedList.add(exp);
        feedList.add(exp);*/
    }
}
