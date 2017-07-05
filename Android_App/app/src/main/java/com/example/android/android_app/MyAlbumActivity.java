package com.example.android.android_app;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.android.android_app.Class.RequestServer;
import com.example.android.android_app.Class.RequestServerInterface;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAlbumActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private List<Feed> feedList;

    private final static int GET_MY_FEED_OK = 0;
    private final static int GET_MY_FEED_FAILED = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.myAlbumToolBar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.pb_myAlbum);
        final RequestServerInterface requestServer = new RequestServer(handler, GET_MY_FEED_OK, GET_MY_FEED_FAILED, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                feedList = requestServer.getMyFeed();
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_MY_FEED_OK:
                    break;
                case GET_MY_FEED_FAILED:
                    break;
            }
        }
    };


}
