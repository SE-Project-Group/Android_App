package com.example.android.android_app;

import android.content.Intent;
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
    }
}
