package com.example.android.android_app.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.android.android_app.Adapter.CommentAdapter;
import com.example.android.android_app.Model.Comment;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.FeedRequester;
import com.example.android.android_app.Util.Verify;


import java.util.ArrayList;
import java.util.List;

//import static com.example.android.android_app.R.id.comment_send;


public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText comment_content;
    private Button send_btn;
    private RelativeLayout rl_comment;
    private String feed_id;

    private Verify verify;
    private List<Comment> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        if(verify == null)
            verify = new Verify(this);

        initComment();
        initView();
    }

    private void initComment(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requester = new FeedRequester(new Verify(CommentActivity.this));
            }
        }).start();
    }

    private void initView() {
        // init comment list
        recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CommentAdapter adapter = new CommentAdapter(commentList, this);
        recyclerView.setAdapter(adapter);

        // inti bottom comment part
        comment_content = (EditText) findViewById(R.id.comment_content);
        send_btn = (Button) findViewById(R.id.comment_send);
        rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
        setListener();
    }

    public void setListener(){
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verify.getLoged()){
                    Toast.makeText(CommentActivity.this, "not log in", Toast.LENGTH_SHORT).show();
                    return;
                }
                // send comment
                final String comment = ((EditText)findViewById(R.id.comment_content)).getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FeedRequester requester = new FeedRequester(new Verify(CommentActivity.this));
                        String result = requester.comment(comment, feed_id, 0); // 0 for comment feed
                    }
                }).start();
            }
        });
    }
}
