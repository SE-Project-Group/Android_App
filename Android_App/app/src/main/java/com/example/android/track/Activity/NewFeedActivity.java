package com.example.android.track.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.android.track.Adapter.GridViewAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Util.BottomPopView;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.ImageUriParser;
import com.example.android.track.Util.MyGridView;
import com.example.android.track.Util.OssInit;
import com.example.android.track.Util.OssService;
import com.example.android.track.Util.Verify;
import com.example.android.track.R;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.R.attr.path;
import static android.R.id.list;
import static com.baidu.location.d.j.A;
import static com.baidu.location.d.j.n;
import static com.baidu.location.d.j.v;


public class NewFeedActivity extends AppCompatActivity {
    private BottomPopView bottomPopView;
    private LocationClient mLocationClient;
    private TextView tv_position;
    private MyGridView gridView;

    private static final int ADD_PHOTO = 1;
    private static final int EDIT_PHOTO = 2;
    private String detailed_location;
    private static final int LOCATE_OK = 4;

    // used to send to server
    // select show area
    private boolean showLocation;
    private double latitude;
    private double longitude;
    private String text;
    private String shareArea;

    private static final String PUBLIC = "public";
    private static final String FRIEND = "friend";
    private static final String PRIVATE = "private";

    private static final int UPLOAD_OK = 3;
    private static final int UPLOAD_FAILED = 6;
    private static final int UPLOAD_PIC_OK = 5;

    // used to upload picture
    private OssService ossService;
    private List<String> pathList = new ArrayList<>();

    private int replace_position;

    // @ someone ids
    private JSONArray mentionList = new JSONArray();

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
        gridView=(MyGridView) findViewById(R.id.gridview);
        refreshGridView();

        // get location
        locate();
    }

    private void refreshGridView(){
        GridViewAdapter pictureAdapter = new GridViewAdapter(pathList, this);
        gridView.setAdapter(pictureAdapter);
    }

    // location receive listener
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // get postion
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            // set position easy to understand on screen
            String city = bdLocation.getCity();
            String district = bdLocation.getDistrict();
            String street = bdLocation.getStreet();
            String building = bdLocation.getBuildingName();
            StringBuilder currentPosition = new StringBuilder();
            // if has building name , display it
            if(building != null)
                currentPosition.append(city).append(district).append(street).append("(").append(building).append(")");
            else
                currentPosition.append(city).append(district).append(street);

            detailed_location = currentPosition.toString();
            mLocationClient.stop();
            Message msg = new Message();
            msg.what = LOCATE_OK;
            handler.sendMessage(msg);
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

    // send button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_btn:
                Verify verify = new Verify();
                // check log status
                if(!verify.getLoged()){
                    Toast.makeText(NewFeedActivity.this, "not log in", Toast.LENGTH_SHORT).show();
                    break;
                }
                final String jsonString = generateJsonString();
                final FeedRequester requester = new FeedRequester();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = requester.newFeed(jsonString);
                        Message message = new Message();
                        if(result.equals("failed"))
                            message.what = UPLOAD_FAILED;

                        // if success, new feed interface will return the feed_id of the new feed
                        else {
                            message.what = UPLOAD_OK;
                            Bundle bundle = new Bundle();
                            bundle.putString("feed_id", result);
                            message.setData(bundle);
                        }
                        handler.sendMessage(message);

                    }
                }).start();
        }
        return true;
    }


    // get and check information form page
    private String generateJsonString(){
        EditText edit_text = (EditText) findViewById(R.id.edit_text);
        text = edit_text.getText().toString();

        RadioGroup shareArea_group = (RadioGroup) findViewById(R.id.shareArea_group);
        int selection = shareArea_group.getCheckedRadioButtonId();
        switch (selection){
            case 1:
                shareArea = PUBLIC;
                break;
            case 2:
                shareArea = FRIEND;
                break;
            case 3:
                shareArea = PRIVATE;
        }
        CheckBox showLocation_btn = (CheckBox) findViewById(R.id.showLocation_btn);
        if(showLocation_btn.isChecked())
            showLocation = true;
        else
            showLocation = false;

        //create json
        Verify verify = new Verify();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("userId" ,verify.getUser_id());
            jsonObject.put("text", text);
            jsonObject.put("showLocation", showLocation);
            // loaction json object
            JSONObject location = new JSONObject();
            location.put("latitude", latitude);
            location.put("longitude", longitude);
            jsonObject.put("location", location);

            jsonObject.put("shareArea", shareArea);
            jsonObject.put("mentionList", mentionList);
            jsonObject.put("picCount", pathList.size());
            jsonObject.put("position", detailed_location);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();  // use to send

    }


    // set PopView and get ready for the select click
    public void setPopView(int position){
        bottomPopView = new BottomPopView(this, findViewById(R.id.newFeedPage_root)) {
            @Override
            public void onTopButtonClick() {
                bottomPopView.dismiss();
                editPhoto(position);
            }
            @Override
            public void onBottomButtonClick() {
                bottomPopView.dismiss();
                deletePhoto(position);
            }
        };
        bottomPopView.setTopText("美化");
        bottomPopView.setBottomText("删除");
        // 显示底部菜单
        bottomPopView.show();
    }

    private void editPhoto(int position){
        Uri imageUri = Uri.parse(pathList.get(position));
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(imageUri)
                .build();
        replace_position = position;
        startActivityForResult(imageEditorIntent, EDIT_PHOTO);
    }

    private void deletePhoto(int position){
        pathList.remove(position);
        refreshGridView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_PHOTO:
                List<String> newPathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                pathList = newPathList;
                refreshGridView();
                break;
            case EDIT_PHOTO:
                Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                if(editedImageUri==null)
                    break;
                else {
                    String newPath = new ImageUriParser(NewFeedActivity.this).parse(editedImageUri);
                    pathList.remove(replace_position);
                    pathList.add(replace_position, newPath);
                    refreshGridView();
                    break;
                }
            default:
                break;
        }
    }


    private void upload_pic(String feed_id){
        ossService = new OssInit().initOSS(getApplicationContext(), handler, UPLOAD_PIC_OK);
        int count = pathList.size();
        for(int i = 0; i < count; i++){
            ossService.asyncPutImage(feed_id+"_"+String.valueOf(i+1),pathList.get(i));
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int count = pathList.size();
            switch (msg.what){
                case UPLOAD_OK:
                    Bundle bundle = msg.getData();
                    if(count == 0){     // no picture need to be upload
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    upload_pic(bundle.getString("feed_id"));
                    break;
                case UPLOAD_FAILED:
                    Toast.makeText(NewFeedActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    break;
                case LOCATE_OK:
                    TextView tv_currentPosition = (TextView) findViewById(R.id.tv_currentPosition);
                    tv_currentPosition.setText(detailed_location);
                    break;
                case UPLOAD_PIC_OK:
                    count --;
                    if(count == 0){
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        // go back to home activity
                        Intent intent = new Intent(NewFeedActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
