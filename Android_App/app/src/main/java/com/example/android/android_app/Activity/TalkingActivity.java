package com.example.android.android_app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.android.android_app.Adapter.TalkingAdapter;
import com.example.android.android_app.Model.Talking;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

public class TalkingActivity extends AppCompatActivity {

    private List<Talking> talkingList = new ArrayList<>();
    private EditText talking_input;
    private Button send;
    private RecyclerView talking_recycler_view;
    private TalkingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talking);

        initTalking();

        talking_input = (EditText)findViewById(R.id.msg_input);
        send = (Button)findViewById(R.id.send);
        talking_recycler_view = (RecyclerView) findViewById(R.id.msg_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        talking_recycler_view.setLayoutManager(layoutManager);
        adapter = new TalkingAdapter(talkingList);
        talking_recycler_view.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String content = talking_input.getText().toString();
                if(!"".equals(content)){
                    Talking talking = new Talking(content,Talking.TYPE_SEND);
                    talkingList.add(talking);
                    adapter.notifyItemInserted(talkingList.size() - 1);
                    talking_recycler_view.scrollToPosition(talkingList.size()-1);
                    talking_input.setText("");
                }
            }
        });
    }

    private void initTalking(){
        Talking talking1 = new Talking("Hey WANGTAO is a son.",Talking.TYPE_RECEIVED);
        talkingList.add(talking1);
        Talking talking2 = new Talking("Yes he is my son!",Talking.TYPE_SEND);
        talkingList.add(talking2);
        Talking talking3 = new Talking("Ha ha ha ha",Talking.TYPE_RECEIVED);
        talkingList.add(talking3);
        talkingList.add(talking3);
        talkingList.add(talking3);
        talkingList.add(talking3);
        talkingList.add(talking3);
    }
}
