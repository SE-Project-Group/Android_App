package com.example.android.android_app;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PersonalSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.personalPageToolBar);
        setSupportActionBar(toolbar);

        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.user_name);
        EditText user_name = textInputLayout.getEditText();
        user_name.setHint("User name");

        user_name.addTextChangedListener(new TextWatcher() {
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
}
