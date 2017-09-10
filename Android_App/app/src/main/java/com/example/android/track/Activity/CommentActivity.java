package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.android.track.Adapter.CommentAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Comment;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.Verify;


import java.util.List;

//import static com.example.android.android_app.R.id.comment_send;


public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText comment_content;
    private Button send_btn;
    private String feed_id;

    private Verify verify;
    private List<Comment> commentList;

    // message
    private final static int GET_COMMENTS_OK = 0;
    private final static int GET_COMMENTS_FAILED = 1;
    private final static int SEND_OK = 2;
    private final static int SEND_FAILED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(CommentActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        if(verify == null)
            verify = new Verify();

        // inti bottom comment part
        comment_content = (EditText) findViewById(R.id.comment_content);
        send_btn = (Button) findViewById(R.id.comment_send);
        setListener();

        initComment();
    }

    public void initComment(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requester = new FeedRequester();
                commentList = requester.getCommentList(feed_id);
                Message message = new Message();
                if(commentList == null)
                    message.what = GET_COMMENTS_FAILED;
                else
                    message.what = GET_COMMENTS_OK;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void setCommentList(){
        // init comment list
        recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CommentAdapter adapter = new CommentAdapter(commentList, this, feed_id, false);
        recyclerView.setAdapter(adapter);
    }


    public void setListener(){
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verify.getLoged()){
                    Toast.makeText(CommentActivity.this, "您尚未登陆，无法使用此功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String comment = comment_content.getText().toString();
                // check input
                if(comment.length() > 140){
                    Toast.makeText(CommentActivity.this, "输入内容不能超过140字哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                // send comment
                send_btn.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FeedRequester requester = new FeedRequester();
                        String result = requester.comment(comment, feed_id, 0); // 0 for comment feed
                        Message message = new Message();
                        if(result.equals("success"))
                            message.what = SEND_OK;
                        else
                            message.what = SEND_FAILED;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_COMMENTS_OK:
                    setCommentList();
                    break;
                case GET_COMMENTS_FAILED:
                    Toast.makeText(MyApplication.getContext(), "获取动态列表哦请求失败\n请检查网络状态", Toast.LENGTH_SHORT).show();
                    break;
                case SEND_OK:
                    Toast.makeText(MyApplication.getContext(), "评论成功", Toast.LENGTH_SHORT).show();
                    comment_content.setText("");  // clear input area
                    send_btn.setClickable(true);
                    initComment();
                    break;
                case  SEND_FAILED:
                    Toast.makeText(MyApplication.getContext(), "请求失败，请检查网络状态", Toast.LENGTH_SHORT).show();
                    send_btn.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    };
}
