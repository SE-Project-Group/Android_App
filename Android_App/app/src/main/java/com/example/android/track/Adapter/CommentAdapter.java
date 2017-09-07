package com.example.android.track.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.CommentActivity;
import com.example.android.track.Activity.CommentConversationActivity;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Model.Comment;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by thor on 2017/7/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity context;
    private List<Comment> commentList;
    private String feed_id;
    private boolean conversation;


    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView portrait_view;
        TextView user_name_view;
        TextView time_view;
        TextView comment_view;
        Button reply_btn;
        Button conversation_btn;
        public ViewHolder (View view){
            super(view);
            portrait_view = (CircleImageView) view.findViewById(R.id.portrait_image);
            user_name_view = (TextView)view.findViewById(R.id.user_name_text);
            comment_view = (TextView)view.findViewById(R.id.comment_text);
            time_view = (TextView) view.findViewById(R.id.time_text);
            reply_btn = (Button) view.findViewById(R.id.reply_btn);
            conversation_btn = (Button) view.findViewById(R.id.conversation_btn);
        }
    }

    public CommentAdapter(List<Comment> commentList, Activity context, String feed_id, boolean conversation){
        this.commentList = commentList;
        this.context = context;
        this.feed_id = feed_id;
        this.conversation = conversation;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.portrait_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int user_id = commentList.get(position).getUser_id();
                navigateToHome(user_id);
            }
        });
        holder.user_name_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int user_id = commentList.get(position).getUser_id();
                navigateToHome(user_id);
            }
        });
        holder.reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                int reply_id = commentList.get(position).getComment_id();
                showCommentDialog(reply_id);
            }
        });
        holder.conversation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                showConversation(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        Glide.with(context)
                .load(comment.getPortrait_url())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait_view);
        holder.comment_view.setText(comment.getComment_text());
        holder.user_name_view.setText(comment.getUser_name());
        holder.time_view.setText(comment.getTime());

        // if already in conversation List , do not allow click this button again
        if(conversation){
            holder.conversation_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // navigate to this user's home page
    private void navigateToHome(int user_id){
        Intent intent = new Intent(context, PersonalHomeActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    private void showCommentDialog(int reply_id){
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
                                // refresh comment list after send a new comment
                                ((CommentActivity) context).initComment();
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

    private void showConversation(int position){
        List<Comment> commentConversation =  new ArrayList<>();
        // get a multiply
        List<Comment> multiply = new ArrayList<>();
        multiply.addAll(commentList);

        Comment comment = multiply.get(position);
        commentConversation.add(comment);

        // get two sublist
        List<Comment> comments = new ArrayList<>();
        comments.addAll(multiply.subList(0, position));
        List<Comment> replys = new ArrayList<>();
        replys.addAll(multiply.subList(position+1, multiply.size()));
        Collections.reverse(comments);

        // get before comment
        int reply_id = comment.getReply_id();
        while (reply_id != 0){
            for(int i = 0; i < comments.size(); i++){
                Comment temp = comments.get(i);
                comments.remove(0);
                if(temp.getComment_id() == reply_id){
                    commentConversation.add(0, temp);
                    reply_id = temp.getReply_id();
                    break;
                }
            }
        }

        int comment_id = comment.getComment_id();
        while(replys.size()!= 0){
            for(int i = 0; i < replys.size(); i++){
                Comment temp = replys.get(i);
                replys.remove(0);
                if(temp.getReply_id() == comment_id){
                    commentConversation.add(temp);
                    comment_id = temp.getComment_id();
                    break;
                }
            }
        }

        Intent intent = new Intent(context, CommentConversationActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<Comment> conversation = new ArrayList<>();
        conversation.addAll(commentConversation);
        bundle.putParcelableArrayList("conversation",conversation);
        bundle.putString("feed_id", feed_id);
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }
}
