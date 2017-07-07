package com.example.android.android_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class PersonalPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.personalPageToolBar);
        setSupportActionBar(toolbar);

        ImageButton personal_setting = (ImageButton) findViewById(R.id.personal_setting);
        personal_setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PersonalPageActivity.this,PersonalSettingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout all_personal_feeds = (LinearLayout) findViewById(R.id.all_personal_feeds_layout);
        all_personal_feeds.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PersonalPageActivity.this,PersonalHomeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout my_feed = (LinearLayout)findViewById(R.id.my_feed_layout);
        my_feed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(PersonalPageActivity.this, PersonalHomeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout logout_btn = (LinearLayout)findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor = getSharedPreferences("login_data",MODE_PRIVATE).edit();
                editor.putBoolean("loged",false);
                editor.putString("token", "");
                editor.putInt("user_id",0);
                editor.apply();
                Intent intent = new Intent(PersonalPageActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
