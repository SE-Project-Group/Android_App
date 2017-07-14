package com.example.android.android_app.Activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Class.Feed;
import com.example.android.android_app.Class.FeedAdapter;
import com.example.android.android_app.Class.RequestServer;
import com.example.android.android_app.Class.RequestServerInterface;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

public class PersonalHomeActivity extends AppCompatActivity {
    private List<Feed> myfeeds = new ArrayList<>();
    private final static int GET_FEEDS_OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);
        final RequestServerInterface requestServer = new RequestServer(handler, GET_FEEDS_OK, -1, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                myfeeds = requestServer.getMyFeed();
            }
        }).start();
    }

    private void display(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        FeedAdapter adapter = new FeedAdapter(getApplicationContext(), myfeeds);
        recyclerView.setAdapter(adapter);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEEDS_OK:
                    display();
            }
        }
    };
}
