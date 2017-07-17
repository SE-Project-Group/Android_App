package com.example.android.android_app.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.Util.RequestServerInterface;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.Verify;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

public class PersonalHomeActivity extends AppCompatActivity {
    private List<Feed> myfeeds = new ArrayList<>();
    private final static int NOT_LOG_IN = 0;
    private final static int GET_FEEDS_OK = 1;
    private final static int GET_FEEDS_FAILED = 2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);
        Intent intent = getIntent();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final RequestServerInterface requestServer = new RequestServer(handler, GET_FEEDS_OK, -1, this);
        Verify verify = new Verify(this);
        if(! verify.getLoged()){
            Toast.makeText(PersonalHomeActivity.this, "not log in", Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();

                myfeeds = requestServer.getMyFeed();

                if(myfeeds == null)
                    message.what = GET_FEEDS_FAILED;
                else
                    message.what = GET_FEEDS_OK;
                handler.sendMessage(message);

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
