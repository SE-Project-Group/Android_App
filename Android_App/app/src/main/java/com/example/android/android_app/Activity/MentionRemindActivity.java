package com.example.android.android_app.Activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.android_app.Adapter.RemindAdapter;
import com.example.android.android_app.Model.Remind;
import com.example.android.android_app.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MentionRemindActivity extends AppCompatActivity {

    private List<Remind> remindList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention_remind);

        initReminds();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.remind_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RemindAdapter adapter = new RemindAdapter(remindList);
        recyclerView.setAdapter(adapter);
    }

    private void initReminds(){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Remind remindA = new Remind("wangtao","xiezhentao",time.toString(),R.drawable.exp_portrait,R.drawable.user_albumn,"@了我","I love coding");
        remindList.add(remindA);
        remindList.add(remindA);
        remindList.add(remindA);
        remindList.add(remindA);
    }
}
