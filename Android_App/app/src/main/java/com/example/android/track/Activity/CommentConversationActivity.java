package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.android.track.Adapter.CommentAdapter;
import com.example.android.track.Model.Comment;
import com.example.android.track.R;

import java.util.List;

/**
 * Created by thor on 2017/8/5.
 */

public class CommentConversationActivity extends AppCompatActivity {

    private String feed_id;
    private List<Comment> commentList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // get conversation List
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        commentList = bundle.getParcelableArrayList("conversation");
        feed_id = bundle.getString("feed_id");

        recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CommentAdapter adapter = new CommentAdapter(commentList, this, feed_id, true);
        recyclerView.setAdapter(adapter);
    }
}
