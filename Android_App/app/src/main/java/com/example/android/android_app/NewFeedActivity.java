package com.example.android.android_app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.android.android_app.Class.ImageUriParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.location.d.j.m;
import static com.baidu.location.d.j.t;

public class NewFeedActivity extends AppCompatActivity {
    private BottomPopView bottomPopView;
    private LocationClient mLocationClient;
    private double latitude;
    private double longtitude;
    private TextView tv_position;
    private int picture_cnt = 0;
    private ImageView picture_0;
    private ImageView new_pic;
    private Uri new_pic_uri;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_new_feed);
        // set tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.newFeedToolBar);
        setSupportActionBar(toolbar);
        tv_position = (TextView) findViewById(R.id.tv_currentPosition);
        // get location
        locate();
        // add picture function
        ImageView add_btn = (ImageView) findViewById(R.id.add_pic_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_pic();
            }
        });

    }

    // location receive listener
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // get postion
            latitude = bdLocation.getLatitude();
            longtitude = bdLocation.getLongitude();
            // set position easy to understand on screen
            String city = bdLocation.getCity();
            String district = bdLocation.getDistrict();
            String street = bdLocation.getStreet();
            String building = bdLocation.getBuildingName();
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(city).append(district).append(street).append("(").append(building).append(")");
            tv_position.setText(currentPosition);
            mLocationClient.stop();
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    // locate me
    private void locate(){
        tv_position.setText("正在获取位置....");
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        mLocationClient.start();
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


    // jedge if the user can add a picture, and let the user choose type
    // then descide which function to use ， takeNewPhoto or addFromAlbum
    private void add_pic(){
        int full = 0;
        switch (picture_cnt){
            case 0:
                picture_0  = (ImageView) findViewById(R.id.new_pic_0);
                new_pic = picture_0;
                break;
            case 1:
                Toast.makeText(NewFeedActivity.this, "达到图片数量上限", Toast.LENGTH_SHORT).show();

                full = 1;
                break;
            default:
                break;
        }
        // if the number of picture has already reach the limitation
        if(full != 0)
            return;
        setPopView();
    }


    // set PopView and get ready for the select click
    private void setPopView(){
        bottomPopView = new BottomPopView(this, findViewById(R.id.newFeedPage_root)) {
            @Override
            public void onTopButtonClick() {
                bottomPopView.dismiss();
                takeNewPhoto();
            }
            @Override
            public void onBottomButtonClick() {

                //choosePhoto();
                bottomPopView.dismiss();
                selectFromAlbum();
            }
        };
        bottomPopView.setTopText("拍照");
        bottomPopView.setBottomText("选择照片");
        // 显示底部菜单
        bottomPopView.show();
    }


    // select
    private void takeNewPhoto(){
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
            new_pic_uri = FileProvider.getUriForFile(NewFeedActivity.this, "com.example.android.Android_app.fileProvider", outputImage);
        }else{
            new_pic_uri = Uri.fromFile(outputImage);
        }

        // turn on camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, new_pic_uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    private void selectFromAlbum(){
        // open album
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        switch (requestCode){
            case TAKE_PHOTO :
                if(resultCode == RESULT_OK){
                    Bitmap bitmap = BitmapFactory.decodeFile(new_pic_uri.getPath(), options);
                    Bitmap scaled = scaleBitmap(bitmap, 300,300);
                    new_pic.setImageBitmap(scaled);
                    picture_cnt ++;
                }
                break;
            case CHOOSE_PHOTO :
                ImageUriParser imageUriParser = new ImageUriParser(this);
                String path = imageUriParser.parse(data.getData());
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                Bitmap scaled = scaleBitmap(bitmap, 300,300);
                new_pic.setImageBitmap(scaled);
                picture_cnt ++;
            default:
                break;
        }
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight){
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }
}
