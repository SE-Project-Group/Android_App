package com.example.android.track.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.ClientInfo;
import com.example.android.track.R;
import com.example.android.track.Util.AcquaintanceManager;
import com.example.android.track.View.BottomPopView;
import com.example.android.track.Util.ImageUriParser;
import com.example.android.track.Util.OssInit;
import com.example.android.track.Util.OssService;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.jpush.im.android.api.model.UserInfo.Field.birthday;
import static cn.jpush.im.android.api.model.UserInfo.Field.gender;
import static com.example.android.track.R.id.default_activity_button;
import static com.example.android.track.R.id.user_name;

public class UserInfoActivity extends AppCompatActivity {
    // View
    private CircleImageView portrait_view;
    private BottomPopView bottomPopView;
    private TextView birthday_tv;
    private EditText user_name_et;
    private RadioGroup gender_group;
    private EditText email_et;
    private ProgressDialog progressDialog;

    private final static int TAKE_PHOTO = 0;
    private final static int CHOOSE_PHOTO = 1;
    private int mYear, mMonth, mDay;
    private static final int DATE_DIALOG = 2;

    private Uri new_portrait_uri;
    private String portrait_path;

    private UserRequester requester = new UserRequester();
    private ClientInfo clientInfo;

    private final static int GET_INFO_OK = 3;
    private final static int GET_INFO_FAILED = 4;
    private final static int UPLOAD_INFO_OK = 5;
    private final static int UPLOAD_INFO_FAILED = 6;
    private final static int UPLOAD_PIC_OK = 7;
    private final static int EXIST_NAME = 8;

    // record if has changed
    //private Boolean infoChanged = false; do not check info , upload anyway
    private Boolean portraitChanged = false;

    // set toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_toolbar,menu);  // need modify
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        // set tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(UserInfoActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(UserInfoActivity.this);

        // set retur button
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.send_24);        // need modify
        }
        initUserInfo();

        //portrait
        portrait_view = (CircleImageView) findViewById(R.id.portrait_view);
        portrait_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopView();
            }
        });

        // user name
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(user_name);
        setUserNameChecker(textInputLayout);

        // date picker
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        Button date_picker_btn = (Button) findViewById(R.id.edit_birthday_btn);
        date_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });


    }

    // set button listener for tool bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.upload_btn:
                upload();
                break;
        }
        return true;
    }

    private void initUserInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Verify verify = new Verify();
                int user_id = Integer.valueOf(verify.getUser_id());
                clientInfo = requester.getClientInfo(user_id);
                Message message = new Message();
                if(clientInfo == null)
                    message.what = GET_INFO_FAILED;
                else
                    message.what = GET_INFO_OK;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void setInfo(){
        String user_name = clientInfo.getUser_name();
        String birthday = clientInfo.getBirthday();
        String gender = clientInfo.getGender();
        String email = clientInfo.getEmail();
        user_name_et = (EditText) findViewById(R.id.user_name_et);
        birthday_tv = (TextView) findViewById(R.id.birthday_tv);
        gender_group = (RadioGroup) findViewById(R.id.gender_group);
        email_et = (EditText) findViewById(R.id.email_et);

        user_name_et.setText(user_name);
        if(birthday.equals(""))         // set default birthday
            birthday = "2017-09-01";
        birthday_tv.setText(birthday);
        if(gender.equals("male"))
            gender_group.check(R.id.male);
        else
            gender_group.check(R.id.female);
        email_et.setText(email);

        // load portrait
        Glide.with(this)
                .load(clientInfo.getPortrait_url())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.default_portrait)
                .into(portrait_view);
    }

    private void upload() {
        // check user_name
        String user_name = user_name_et.getText().toString();
        String gender = "male";
        if(gender_group.getCheckedRadioButtonId()==R.id.female)
            gender = "female";
        String birthday = birthday_tv.getText().toString();
        String email = email_et.getText().toString();

        if(user_name.equals("") || user_name.length()>15){
            Toast.makeText(UserInfoActivity.this, "用户名不符合规范", Toast.LENGTH_SHORT).show();
            return;
        }

        // show dialog
        progressDialog.setTitle("正在拼命上传");
        progressDialog.setMessage("上传个人信息......");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // upload info

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", user_name);
            jsonObject.put("gender", gender);
            jsonObject.put("birthday", birthday);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String jsonString = jsonObject.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = requester.modifyClientInfo(jsonString);
                Message message = new Message();
                if (result.equals("failed"))
                    message.what = UPLOAD_INFO_FAILED;
                else if (result.equals("exist name"))
                    message.what = EXIST_NAME;
                else {
                    message.what = UPLOAD_INFO_OK;
                }
                handler.sendMessage(message);
            }
        }).start();

    }

    
    // set PopView and get ready for the select click
    private void setPopView(){
        bottomPopView = new BottomPopView(this, findViewById(R.id.userInfo_root)) {
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
            new_portrait_uri = FileProvider.getUriForFile(UserInfoActivity.this, "com.example.android.track.fileProvider", outputImage);
        }else{
            new_portrait_uri = Uri.fromFile(outputImage);
        }

        // turn on camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, new_portrait_uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void selectFromAlbum(){
        // open album
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        switch (requestCode){
            case TAKE_PHOTO :
                if(resultCode == RESULT_OK){
                    String path = new_portrait_uri.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    Bitmap scaled = scaleBitmap(bitmap, 300,300);
                    portrait_view.setImageBitmap(scaled);
                    portrait_path = path;
                }
                portraitChanged = true;
                break;
            case CHOOSE_PHOTO :
                if(resultCode == RESULT_OK) {
                    ImageUriParser imageUriParser = new ImageUriParser(this);
                    String path = imageUriParser.parse(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    Bitmap scaled = scaleBitmap(bitmap, 300, 300);
                    portrait_view.setImageBitmap(scaled);
                    portrait_path = path;
                    portraitChanged = true;
                }
                break;
            default:
                break;
        }
    }


    // check user name spell
    private void setUserNameChecker(final TextInputLayout textInputLayout){
        EditText user_name_et = textInputLayout.getEditText();
        user_name_et.setHint("User name");
        user_name_et.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    textInputLayout.setError("用户名不可为空");
                    textInputLayout.setErrorEnabled(true);
                }else if(s.length()>15){
                    textInputLayout.setError("用户名不可多于15个字符");
                    textInputLayout.setErrorEnabled(true);
                }
                else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0) {
                    textInputLayout.setError("用户名不可为空");
                    textInputLayout.setErrorEnabled(true);
                }
                else if(s.length()>15){
                    textInputLayout.setError("用户名不可多于15个字符");
                    textInputLayout.setErrorEnabled(true);
                }
                else {
                    textInputLayout.setErrorEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
                if (s.length()==0) {
                    textInputLayout.setError("用户名不可为空");
                    textInputLayout.setErrorEnabled(true);
                }else if(s.length()>15){
                    textInputLayout.setError("用户名不可多于15个字符");
                    textInputLayout.setErrorEnabled(true);
                }else {
                    textInputLayout.setErrorEnabled(false);
                }
            }
        });
    }



    // birthday date picker
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            // set text view
            birthday_tv.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        }
    };



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case GET_INFO_FAILED:
                    Toast.makeText(UserInfoActivity.this, "GET INFO failed", Toast.LENGTH_SHORT).show();
                    break;
                case GET_INFO_OK:
                    setInfo();
                    break;
                case UPLOAD_INFO_OK:
                    JMessageClient.getUserInfo(new Verify().getUser_id(), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if(i == 0){
                                String user_name = user_name_et.getText().toString();
                                userInfo.setNickname(user_name);
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if(i == 0) {
                                            //Toast.makeText(MyApplication.getContext(), "修改昵称成功", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(MyApplication.getContext(), "修改聊天昵称失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    progressDialog.setMessage("上传个人信息成功");
                    // upload new portrait
                    if (portraitChanged) {
                        progressDialog.setMessage("上传新头像......");
                        OssService ossService = new OssInit().initOSS(getApplicationContext(), handler, UPLOAD_PIC_OK);
                        Verify verify = new Verify();
                        String user_id = verify.getUser_id();
                        ossService.asyncPutImage(user_id + "_portrait", portrait_path);
                    }
                    else {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;
                case UPLOAD_INFO_FAILED:
                    Toast.makeText(UserInfoActivity.this, "修改信息失败", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case UPLOAD_PIC_OK:
                    AcquaintanceManager.saveAcquaintance(Integer.valueOf(new Verify().getUser_id()));
                    progressDialog.setMessage("新头像上传成功");
                    progressDialog.dismiss();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case EXIST_NAME:
                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "用户名已有人使用，请重新编辑",Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

}

