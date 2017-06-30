package com.example.android.android_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.location.d.j.m;

public class NewFeedActivity extends AppCompatActivity {
    private int picture_cnt = 0;
    private ImageView picture_0;
    private ImageView new_pic;
    private Uri new_pic_uri;
    private static final int TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newFeedToolBar);
        setSupportActionBar(toolbar);
        ImageView add_btn = (ImageView) findViewById(R.id.add_pic_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_pic();
                picture_cnt ++;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_feed_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_btn:
                Toast.makeText(getApplicationContext(), "send", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void add_pic(){
        switch (picture_cnt){
            case 0:
                picture_0  = (ImageView) findViewById(R.id.new_pic_0);
                new_pic = picture_0;
                break;
            default:
                break;
        }

        // get ready for storage photo
        File outputImage = new File(getExternalCacheDir(), "new_taken_photo.png");
        try{
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        // create image URI
        if(Build.VERSION.SDK_INT >= 24){ // lower than Android 7.0
            new_pic_uri = FileProvider.getUriForFile(NewFeedActivity.this, "com.example.android.uidesign.fileProvider", outputImage);
        }else{
            new_pic_uri = Uri.fromFile(outputImage);
        }

        // turn on camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, new_pic_uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        switch (requestCode){
            case  TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Bitmap bitmap1 = BitmapFactory.decodeFile(new_pic_uri.getPath(), options);
                    new_pic.setImageBitmap(bitmap1);
                }
                break;
            default:
                break;
        }
    }
}
