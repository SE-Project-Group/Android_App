package com.example.android.android_app.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.Util.RequestServerInterface;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

public class PersonalHomeActivity extends AppCompatActivity {
    private List<Feed> myfeeds = new ArrayList<>();
    private final static int GET_FEEDS_OK = 0;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);
        Intent intent = getIntent();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final RequestServerInterface requestServer = new RequestServer(handler, GET_FEEDS_OK, -1, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                myfeeds = requestServer.getMyFeed();
            }
        }).start();
    }


    private void displayRecycleView(){
        // show myFeeds
        FeedAdapter adapter = new FeedAdapter(getApplicationContext(), myfeeds);
        recyclerView.setAdapter(adapter);


    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEEDS_OK:
                    displayRecycleView();
            }
        }
    };
}
