package com.example.android.track.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.Remind;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by jarvis on 2017/7/12.
 */

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ViewHolder>{
    private List<Remind> mRemindList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_portrait;
        ImageView first_pic;
        TextView time;
        TextView user_name;
        TextView remind_text;
        TextView author_text;
        TextView author_name;
        Button response_btn;
        LinearLayout feed_abstract;

        public ViewHolder(View view){
            super(view);
            user_portrait = (ImageView)view.findViewById(R.id.user_portrait);
            user_name = (TextView)view.findViewById(R.id.user_name);
            time = (TextView)view.findViewById(R.id.time);
            remind_text = (TextView)view.findViewById(R.id.remind_text);
            author_text = (TextView)view.findViewById(R.id.author_text);
            author_name = (TextView)view.findViewById(R.id.author_name);
            response_btn = (Button) view.findViewById(R.id.response_btn);
            feed_abstract = (LinearLayout) view.findViewById(R.id.feed_abstract);
        }
    }

    public RemindAdapter(List<Remind> remindList, Context cxt){
        mRemindList = remindList;
        context = cxt;
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

        holder.time.setText(remind.getTime().toString());

        holder.author_name.setText(remind.getAuthor_name());
        holder.author_text.setText(remind.getAuthor_text());

        if(remind.getType().equals("comment"))
            holder.response_btn.setVisibility(View.VISIBLE);

        // load the user portrait use glide
        List<Acquaintance> acquaintances = DataSupport.select("portrait").where("user_id = ?", String.valueOf(remind.getUser_id())).find(Acquaintance.class);
        Glide.with(context)
                .load(acquaintances.get(0).getPortrait())
                .placeholder(R.drawable.exp_pic)
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
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.edit_text);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FeedRequester requester = new FeedRequester();
                                requester.comment(edit_text.getText().toString(), feed_id, reply_id);
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

}
