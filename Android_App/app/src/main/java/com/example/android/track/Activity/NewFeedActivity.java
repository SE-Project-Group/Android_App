package com.example.android.track.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.android.track.Adapter.GridViewAdapter;
import com.example.android.track.Model.LitePal_Entity.MyFeed;
import com.example.android.track.View.BottomPopView;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.ImageUriParser;
import com.example.android.track.View.MyGridView;
import com.example.android.track.Util.OssInit;
import com.example.android.track.Util.OssService;
import com.example.android.track.Util.Verify;
import com.example.android.track.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.R.id.list;
import static cn.jpush.im.android.api.enums.ContentType.location;


public class NewFeedActivity extends AppCompatActivity {
    private BottomPopView bottomPopView;
    private LocationClient mLocationClient;

    private EditText content;
    private TextView tv_position;
    private MyGridView gridView;
    private ProgressDialog progressDialog;  // used to tell upload progress
    private LinearLayout mentionMenu;
    private TextView mentionNames_tv;
    private RadioGroup shareArea_group;
    private CheckBox showLocation_cb;

    private static final int ADD_PHOTO = 1;
    private static final int EDIT_PHOTO = 2;
    private String detailed_location;
    private static final int LOCATE_OK = 4;

    // used to send to server
    // select show area
    private boolean showLocation;
    private double latitude = 0;
    private double longitude = 0;
    private String text;
    private String shareArea;

    private static final String PUBLIC = "public";
    private static final String FRIEND = "friend";
    private static final String PRIVATE = "private";

    private static final int UPLOAD_OK = 3;
    private static final int UPLOAD_FAILED = 6;
    private static final int UPLOAD_PIC_OK = 5;

    private static final int CHOOSE_MENTION_OK = 7;

    // used to upload picture
    private OssService ossService;
    private List<String> pathList = new ArrayList<>();
    // used to replace photo
    private int replace_position;
    private MyFeed myFeed;
    // @ someone ids
    private List<Integer> mentionList = new ArrayList<>();
    private List<String> mentionNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_new_feed);
        // set tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.newFeedToolBar);
        toolbar.setTitleTextColor(NewFeedActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);
        tv_position = (TextView) findViewById(R.id.tv_currentPosition);
        content = (EditText) findViewById(R.id.edit_text);
        gridView=(MyGridView) findViewById(R.id.gridview);
        progressDialog = new ProgressDialog(NewFeedActivity.this);
        mentionMenu = (LinearLayout) findViewById(R.id.mention_menu);
        mentionNames_tv = (TextView) findViewById(R.id.mention_names);
        shareArea_group = (RadioGroup) findViewById(R.id.shareArea_group);
        shareArea_group.check(R.id.rb1);  // default is public


        mentionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewFeedActivity.this,ChooseMentionActivity.class);
                ArrayList arrayList1 = new ArrayList();
                arrayList1.addAll(mentionList);
                intent.putIntegerArrayListExtra("chooseList", arrayList1);

                ArrayList arrayList2 = new ArrayList();
                arrayList2.addAll(mentionNameList);
                intent.putStringArrayListExtra("chooseNames", arrayList2);

                startActivityForResult(intent, CHOOSE_MENTION_OK);
            }
        });

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
            if(city == null && district == null & street == null){
                detailed_location = "无法获取当前定位，请检查GPS及网络状态";
                return;
            }
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
                    Toast.makeText(NewFeedActivity.this, "您尚未登陆", Toast.LENGTH_SHORT).show();
                    break;
                }
                // check if share Area is selected
                int selection = shareArea_group.getCheckedRadioButtonId();
                if(selection == -1){
                    Toast.makeText(NewFeedActivity.this, "您尚未选择可见范围", Toast.LENGTH_SHORT).show();
                    break;
                }
                String content_text = content.getText().toString();
                if(pathList.size() == 0 && content_text.equals("") ){
                    Toast.makeText(NewFeedActivity.this, "您尚未编辑任何内容", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(content_text.length()>140){
                    Toast.makeText(NewFeedActivity.this, "只能编辑小于140个字哦~", Toast.LENGTH_SHORT).show();
                    break;
                }

                // check if get correct location
                if((longitude == 0 || latitude == 0) && (showLocation == true)){
                    Toast.makeText(NewFeedActivity.this, "定位信息缺失\n您可以取消显示位置或者重试", Toast.LENGTH_SHORT).show();
                    break;
                }

                // check over, show hint and begin to upload
                progressDialog.setTitle("正在拼命上传");
                progressDialog.setMessage("上传动态内容......");
                progressDialog.setCancelable(false);
                progressDialog.show();

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

                            // save in local database
                            saveMyFeed(result);
                        }
                        handler.sendMessage(message);

                    }
                }).start();
        }
        return true;
    }


    // get and check information form page
    private String generateJsonString(){
        text = content.getText().toString();

        int selection = shareArea_group.getCheckedRadioButtonId();
        switch (selection){
            case R.id.rb1:
                shareArea = PUBLIC;
                break;
            case R.id.rb2:
                shareArea = FRIEND;
                break;
            case R.id.rb3:
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

            if(showLocation == true) {
                // loaction json object
                JSONObject location = new JSONObject();
                location.put("latitude", latitude);
                location.put("longitude", longitude);
                jsonObject.put("location", location);
                jsonObject.put("position", detailed_location);
            }
            jsonObject.put("shareArea", shareArea);
            JSONArray mentionListArray = new JSONArray();
            for(Integer mentionId : mentionList){
                mentionListArray.put(mentionId);
            }
            jsonObject.put("mentionList", mentionListArray);
            jsonObject.put("picCount", pathList.size());

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
                if(resultCode != 0) {
                    List<String> newPathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    pathList = newPathList;
                    refreshGridView();
                }
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
                }
                break;

            case CHOOSE_MENTION_OK:
                // display user names on screen
                if(resultCode == RESULT_OK){
                    mentionNameList = data.getStringArrayListExtra("chooseNames");
                    String nameString = "";
                    if(mentionNameList.size() == 0)
                        mentionNames_tv.setText("");
                    else {
                        for (String name : mentionNameList) {
                            nameString += ", ";
                            nameString += name;
                        }
                        nameString = nameString.substring(2);
                        // display it
                        mentionNames_tv.setText(nameString);
                    }

                    mentionList = data.getIntegerArrayListExtra("chooseList");
                }
                break;
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
            switch (msg.what){
                case UPLOAD_OK:
                    progressDialog.setMessage("内容上传成功");
                    Bundle bundle = msg.getData();
                    if(pathList.size() == 0){     // no picture need to be upload
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    progressDialog.setMessage("上传图片......");
                    upload_pic(bundle.getString("feed_id"));
                    break;
                case UPLOAD_FAILED:
                    Toast.makeText(NewFeedActivity.this, "请求失败，请检查网络", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case LOCATE_OK:
                    TextView tv_currentPosition = (TextView) findViewById(R.id.tv_currentPosition);
                    tv_currentPosition.setText(detailed_location);
                    break;
                case UPLOAD_PIC_OK:
                    pathList.remove(0);
                    if(pathList.size() == 0){
                        progressDialog.dismiss();
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

    private void saveMyFeed(String feed_id){
        // create myFeed
        myFeed = new MyFeed();
        myFeed.setFeed_id(feed_id);
        myFeed.setText(text);
        myFeed.setDate(new Date(System.currentTimeMillis()));
        myFeed.setLatitude(latitude);
        myFeed.setLatitude(longitude);
        myFeed.setPosition(detailed_location);
        myFeed.save();
    }

}
