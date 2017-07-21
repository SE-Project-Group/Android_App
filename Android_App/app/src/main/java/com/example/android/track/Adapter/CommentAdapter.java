package com.example.android.track.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Model.Comment;
import com.example.android.track.R;

import java.util.List;

/**
 * Created by thor on 2017/7/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity context;
    private List<Comment> commentList;


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait_view;
        TextView user_name_view;
        TextView time_view;
        TextView comment_view;
        Button reply_btn;
        Button conversation_btn;
        public ViewHolder (View view){
            super(view);
            portrait_view = (ImageView)view.findViewById(R.id.portrait_image);
            user_name_view = (TextView)view.findViewById(R.id.user_name_text);
            comment_view = (TextView)view.findViewById(R.id.comment_text);
            time_view = (TextView) view.findViewById(R.id.time_text);
            reply_btn = (Button) view.findViewById(R.id.reply_btn);
            conversation_btn = (Button) view.findViewById(R.id.conversation_btn);
        }
    }

    public CommentAdapter(List<Comment> commentList, Activity context){
        this.commentList = commentList;
        this.context = context;
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
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        Glide.with(context)
                .load(comment.getPortrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait_view);
        holder.comment_view.setText(comment.getComment_text());
        holder.user_name_view.setText(comment.getUser_name());
        holder.time_view.setText(comment.getTime());
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
}
