package com.example.android.track.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.track.Adapter.MyImageAdapter;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;
import com.example.android.track.View.PhotoViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.id.message;


/**
 * Created by thor on 2017/8/9.
 */

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener{

    private PhotoViewPager mViewPager;
    private int currentPosition;
    private MyImageAdapter adapter;
    private TextView mTvImageCount;
    private Button save_btn;
    private List<String> Urls;
    private String feed_id;
    private int user_id;
    private String type;

    private final static int GET_URL_OK = 1;
    private final static int GET_URL_FAILED = 2;
    private final static int SAVE_PHOTO_OK = 3;
    private final static int SAVE_PHOTO_FAILED = 4;

    private ProgressDialog progressDialog;

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
                case SAVE_PHOTO_OK:
                    progressDialog.dismiss();
                    Toast.makeText(PhotoViewActivity.this, "下载成功",Toast.LENGTH_SHORT).show();
                case SAVE_PHOTO_FAILED:
                    progressDialog.dismiss();
                    Toast.makeText(PhotoViewActivity.this, "下载失败",Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    private void initView() {
        mViewPager = (PhotoViewPager) findViewById(R.id.view_pager_photo);
        mTvImageCount = (TextView) findViewById(R.id.tv_image_count);
        save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
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

    private void downloadPhoto(final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();

                FeedRequester requester = new FeedRequester();
                String urlString = requester.getOriginPhotoUrl(fileName);
                //String urlString = "http://sjtutest.oss-cn-shanghai.aliyuncs.com/logo.png";
                // download
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // save file if connection get success
                    if (conn.getResponseCode() == 200) {
                        // get album directory
                        File dir = new File(Environment.getExternalStorageDirectory(),"track_download");
                        if (!dir.exists()){
                            dir.mkdirs();
                        }
                        // name it
                        String rel="";
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date curDate = new Date(System.currentTimeMillis());
                        rel = formatter.format(curDate);
                        File file = new File(dir, rel + ".jpg");

                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.close();

                        //发广播告诉相册有图片需要更新
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);

                        // send message
                        message.what = SAVE_PHOTO_OK;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        message.what = SAVE_PHOTO_FAILED;
                        handler.sendMessage(message);
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    message.what = SAVE_PHOTO_FAILED;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_btn:
                // check log in status
                if(!new Verify().getLoged()){
                    Toast.makeText(PhotoViewActivity.this, "not log in", Toast.LENGTH_SHORT).show();
                    break;
                }
                // show progress dialog
                progressDialog = new ProgressDialog(PhotoViewActivity.this);
                progressDialog.setTitle("保存原图");
                progressDialog.setMessage("正在拼命下载......");
                progressDialog.setCancelable(true);
                progressDialog.show();

                // begin download
                String fileName = "";
                if (type.equals("feed")){
                    fileName = feed_id + "_" + (currentPosition+1);
                }
                if (type.equals("user")){
                    fileName = user_id + "_portrait";
                }
                downloadPhoto(fileName);
                break;
        }
    }

}
