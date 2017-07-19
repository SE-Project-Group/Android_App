package com.example.android.android_app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.android.android_app.R;
import com.example.android.android_app.Util.BottomPopView;
import com.example.android.android_app.Util.ImageUriParser;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


import static com.example.android.android_app.R.id.user_name;

public class UserInfoActivity extends AppCompatActivity {
    // View
    private ImageView portrait_view;
    private BottomPopView bottomPopView;
    private TextView birthday_tv;


    private final static int TAKE_PHOTO = 0;
    private final static int CHOOSE_PHOTO = 1;
    private int mYear, mMonth, mDay;
    private static final int DATE_DIALOG = 1;

    private Uri new_pic_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        // set tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        //portrait
        portrait_view = (ImageView) findViewById(R.id.portrait_view);
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
        birthday_tv = (TextView) findViewById(R.id.birthday_tv);
        Button date_picker_btn = (Button) findViewById(R.id.edit_birthday_btn);
        date_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });


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
            new_pic_uri = FileProvider.getUriForFile(UserInfoActivity.this, "com.example.android.Android_app.fileProvider", outputImage);
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
                    String path = new_pic_uri.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    Bitmap scaled = scaleBitmap(bitmap, 300,300);
                    portrait_view.setImageBitmap(scaled);
                }
                break;
            case CHOOSE_PHOTO :
                ImageUriParser imageUriParser = new ImageUriParser(this);
                String path = imageUriParser.parse(data.getData());
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                Bitmap scaled = scaleBitmap(bitmap, 300,300);
                portrait_view.setImageBitmap(scaled);
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
                }else if(s.length()<5){
                    textInputLayout.setError("用户名不可少于5个字符");
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
                } else if(s.length()<5){
                    textInputLayout.setError("用户名不可少于5个字符");
                    textInputLayout.setErrorEnabled(true);
                }else if(s.length()>15){
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
                } else if(s.length()<5){
                    textInputLayout.setError("用户名不可少于5个字符");
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
            birthday_tv.setText(new StringBuffer().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
        }
    };

}

