package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.track.Adapter.MyImageAdapter;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.View.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by thor on 2017/8/9.
 */

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener{

    private PhotoViewPager mViewPager;
    private int currentPosition;
    private MyImageAdapter adapter;
    private TextView mTvImageCount;
    private TextView mTvSaveImage;
    private List<String> Urls;
    private String feed_id;
    private int user_id;
    private String type;

    private final static int GET_URL_OK = 1;
    private final static int GET_URL_FAILED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        initView();
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition", 0);
        type = intent.getStringExtra("type");

        if(type.equals("feed")) {
            feed_id = intent.getStringExtra("feed_id");
            getUrls();
        }
        if(type.equals("user")){
            user_id = intent.getIntExtra("user_id", 0);
            getUrls();
        }

    }

    private void getUrls(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(type.equals("feed")) {
                    FeedRequester requester = new FeedRequester();
                    Urls = requester.getBigPhotoUrls(feed_id);
                    Message message = new Message();
                    if(Urls == null)
                        message.what = GET_URL_FAILED;
                    else
                        message.what = GET_URL_OK;
                    handler.sendMessage(message);
                }
                if(type.equals("user")){
                    UserRequester requester = new UserRequester();
                    String response = requester.getPortraitUrl(user_id);
                    Message message = new Message();
                    if(response.equals("failed"))
                        message.what = GET_URL_FAILED;
                    else {
                        Urls = new ArrayList<String>();
                        Urls.add(response);
                        message.what = GET_URL_OK;
                    }
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_URL_OK:
                    showPhotos();
                    break;
                case GET_URL_FAILED:
                    Toast.makeText(PhotoViewActivity.this, "获取原图失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {
        mViewPager = (PhotoViewPager) findViewById(R.id.view_pager_photo);
        mTvImageCount = (TextView) findViewById(R.id.tv_image_count);
        mTvSaveImage = (TextView) findViewById(R.id.tv_save_image_photo);
        mTvSaveImage.setOnClickListener(this);
    }

    private void showPhotos() {
        adapter = new MyImageAdapter(Urls, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition, false);
        mTvImageCount.setText(currentPosition+1 + "/" + Urls.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                mTvImageCount.setText(currentPosition + 1 + "/" + Urls.size());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save_image_photo:
                Toast.makeText(PhotoViewActivity.this, "save", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
