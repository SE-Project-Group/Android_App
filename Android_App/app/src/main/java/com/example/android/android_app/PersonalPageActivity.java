package com.example.android.android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;


public class PersonalPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.personalPageToolBar);
        setSupportActionBar(toolbar);
    }


}
