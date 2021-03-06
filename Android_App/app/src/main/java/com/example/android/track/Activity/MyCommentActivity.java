package com.example.android.track.Activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.Model.Feed;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thor on 2017/9/6.
 */

public class MyCommentActivity extends AppCompatActivity {

    private List<Feed> feedList = new ArrayList<>();

    private ProgressBar progressBar;

    private static final int GET_FEED_OK = 1;
    private static final int GET_FEED_FAILED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_xxx);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolBar);
        toolbar.setTitleTextColor(MyCommentActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // check internet
        ConnectivityManager mConnectivityManager = (ConnectivityManager) MyCommentActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            if (!mNetworkInfo.isAvailable()) {
                Toast.makeText(MyCommentActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        } else {
            Toast.makeText(MyCommentActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        initFeeds();

    }

    private void initFeeds() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requester = new FeedRequester();
                feedList = requester.getMyCommentFeeds();
                Message message = new Message();

                if(feedList == null)
                    message.what = GET_FEED_FAILED;
                else
                    message.what = GET_FEED_OK;

                handler.sendMessage(message);
            }
        }).start();
    }

    private void setRecyclerView(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.myRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(MyCommentActivity.this, feedList);
        recyclerView.setAdapter(adapter);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEED_FAILED:
                    Toast.makeText(MyCommentActivity.this, "get feed failed", Toast.LENGTH_SHORT);
                    break;
                case GET_FEED_OK:
                    setRecyclerView();
                    progressBar.setVisibility(View.GONE);
            }
        }
    };
}
