package com.example.android.track.Activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.track.Model.Feed;
import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;

import java.util.List;

public class MyAlbumActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private List<Feed> feedList;

    private final static int GET_MY_FEED_OK = 0;
    private final static int GET_MY_FEED_FAILED = 1;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_xxx);
        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(MyAlbumActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //final RequestServer requestServer = new RequestServer(new Verify(MyAlbumActivity.this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                feedList = new FeedRequester().getMyFeeds();
                // send message to main thread
                Message msg = new Message();
                if (feedList == null)
                    msg.what = GET_MY_FEED_FAILED;
                else
                    msg.what = GET_MY_FEED_OK;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_MY_FEED_OK:
                    recyclerView.setAdapter(new FeedAdapter(MyAlbumActivity.this, feedList));
                    progressBar.setVisibility(View.GONE);
                    break;
                case GET_MY_FEED_FAILED:
                    break;
            }
        }
    };


}
