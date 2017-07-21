package com.example.android.track.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.track.Model.Remind;
import com.example.android.track.Adapter.RemindAdapter;
import com.example.android.track.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LikeRemindActivity extends AppCompatActivity {
    private List<Remind> remindList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention_remind);

        initReminds();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.remind_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RemindAdapter adapter = new RemindAdapter(remindList, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    private void initReminds(){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Remind remindA = new Remind();
        remindList.add(remindA);
        remindList.add(remindA);
        remindList.add(remindA);
        remindList.add(remindA);
    }
}
