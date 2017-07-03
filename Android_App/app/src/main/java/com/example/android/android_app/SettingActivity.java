package com.example.android.android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingToolBar);
        setSupportActionBar(toolbar);

        LinearLayout user_security = (LinearLayout) findViewById(R.id.user_security);
        user_security.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SettingActivity.this,UserSercurityActivity.class);
                startActivity(intent);
            }
        });

    }
}
