package com.example.android.track.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Feed;
import com.example.android.track.Model.Remind;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.Verify;
import com.example.android.track.View.FeedDetailView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jarvis on 2017/7/12.
 */

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ViewHolder>{
    private List<Remind> mRemindList;
    private Activity context;

    private ProgressDialog progressDialog;
    private FeedRequester requester;
    private Feed feedDetail;
    private boolean logged;

    // signal
    private final static int GET_FEED_OK = 1;
    private final static int GET_FEED_FAILED = 2;
    private final static int NOT_LOGGED = 3;
    private final static int SEND_COMMENT_OK = 4;
    private final static int SEND_COMMENT_FAILED = 5;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView user_portrait;
        TextView time;
        TextView user_name;
        TextView remind_text;
        TextView author_text;
        TextView author_name;
        Button response_btn;
        LinearLayout feed_abstract;

        public ViewHolder(View view){
            super(view);
            user_portrait = (CircleImageView) view.findViewById(R.id.user_portrait);
            user_name = (TextView)view.findViewById(R.id.user_name);
            time = (TextView)view.findViewById(R.id.time);
            remind_text = (TextView)view.findViewById(R.id.remind_text);
            author_text = (TextView)view.findViewById(R.id.author_text);
            author_name = (TextView)view.findViewById(R.id.author_name);
            response_btn = (Button) view.findViewById(R.id.response_btn);
            feed_abstract = (LinearLayout) view.findViewById(R.id.feed_abstract);
        }
    }

    public RemindAdapter(List<Remind> remindList, Activity cxt){
        mRemindList = remindList;
        context = cxt;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("动态详情");
        progressDialog.setMessage("正在拼命加载");
        progressDialog.setCancelable(false);
        requester = new FeedRequester();
        feedDetail = new Feed();
        logged = new Verify().getLoged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_remind,parent,false);
        RemindAdapter.ViewHolder holder = new RemindAdapter.ViewHolder(view);

        // set on click listener
        holder.user_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int user_id = mRemindList.get(position).getUser_id();
                // navigate to other user's home page
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("user_id", user_id);
                context.startActivity(intent);
            }
        });

        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int user_id = mRemindList.get(position).getUser_id();
                // navigate to other user's home page
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("user_id", user_id);
                context.startActivity(intent);
            }
        });

        holder.response_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int reply_id = mRemindList.get(position).getComment_id();
                String feed_id = mRemindList.get(position).getFeed_id();
                showCommentDialog(feed_id, reply_id);
            }
        });

        holder.feed_abstract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();  // show hint
                int position = holder.getAdapterPosition();
                String feed_id = mRemindList.get(position).getFeed_id();
                getFeedDetail(feed_id);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RemindAdapter.ViewHolder holder, int position){
        Remind remind = mRemindList.get(position);
        holder.user_name.setText(remind.getUser_name());
        String type = remind.getType();
        // remind text
        if(type.equals("like"))
            holder.remind_text.setText("赞了我");
        if(type.equals("comment"))
            holder.remind_text.setText("评论了我：" + remind.getComment_text());

        if(type.equals("share"))
            holder.remind_text.setText("分享了我：" + remind.getComment_text());

        if(type.equals("mention"))
            holder.remind_text.setText("提到了我");

        SimpleDateFormat sdp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeFormated = sdp.format(remind.getTime());
        holder.time.setText(timeFormated);

        holder.author_name.setText(remind.getAuthor_name());
        holder.author_text.setText(remind.getAuthor_text());

        if(remind.getType().equals("comment"))
            holder.response_btn.setVisibility(View.VISIBLE);

        // load the user portrait use glide
        int user_id = remind.getUser_id();
        File fileDir = MyApplication.getContext().getFilesDir();
        File portrait = new File(fileDir, user_id+"_portrait");
        Glide.with(context)
                .load(portrait)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.default_portrait)
                .into(holder.user_portrait);

    }

    @Override
    public int getItemCount(){
        return mRemindList.size();
    }

    private void showCommentDialog(String feed_id, int reply_id){
        AlertDialog.Builder commentDialog =
                new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_comment, null);
        commentDialog.setTitle("回复评论");
        commentDialog.setView(dialogView);
        // send comment text
        commentDialog.setPositiveButton("发送",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // chech if logged
                        if(!logged){
                            Message message = new Message();
                            message.what = NOT_LOGGED;
                            handler.sendMessage(message);
                            return;
                        }
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.edit_text);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String response = requester.comment(edit_text.getText().toString(), feed_id, reply_id);
                                // send message
                                Message message = new Message();
                                if(response.equals("failed") || response.equals("status wrong"))
                                    message.what = SEND_COMMENT_FAILED;
                                else
                                    message.what = SEND_COMMENT_OK;
                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
        // cancel
        commentDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        commentDialog.create().show();
    }

    private void getFeedDetail(final String feed_id){
        // chech if logged
        if(!logged){
            Message message = new Message();
            message.what = NOT_LOGGED;
            handler.sendMessage(message);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                FeedRequester requester = new FeedRequester();
                feedDetail =  requester.getFeedDetail(feed_id);
                Message message = new Message();
                if(feedDetail == null)
                    message.what = GET_FEED_FAILED;
                else
                    message.what = GET_FEED_OK;
                handler.sendMessage(message);
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEED_OK:
                    // show feed detail
                    progressDialog.dismiss();
                    FeedDetailView feedDetailView = new FeedDetailView(context, feedDetail);
                    feedDetailView.show();
                    break;
                case GET_FEED_FAILED:
                    progressDialog.dismiss();
                    Toast.makeText(context, "获取动态失败", Toast.LENGTH_SHORT).show();
                    break;
                case SEND_COMMENT_OK:
                    Toast.makeText(context, "评论发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case SEND_COMMENT_FAILED:
                    Toast.makeText(context, "评论发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case NOT_LOGGED:
                    Toast.makeText(context, "请您先登录", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

}
