package com.example.android.android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAlbumActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private String url = "http://192.168.1.13:8088/track/rest/app/MyFeed?user_id=8888";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.myAlbumToolBar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.pb_myAlbum);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getMyFeeds();
            }
        }).start();
    }

    private void getMyFeeds(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            Log.d("RESPONSE", "getMyFeeds: "+responseData);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
