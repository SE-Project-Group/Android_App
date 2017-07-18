package com.example.android.android_app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.android_app.R;

import org.w3c.dom.Comment;

import java.util.List;

/**
 * Created by thor on 2017/7/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<com.example.android.android_app.Model.Comment> commentList;


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait;
        TextView user_name;
        TextView comment_text;
        public ViewHolder (View view){
            super(view);
            portrait = (ImageView)view.findViewById(R.id.portrait_image);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            comment_text = (TextView)view.findViewById(R.id.comment_text);

        }
    }

    public CommentAdapter(List<com.example.android.android_app.Model.Comment> commentList, Context context){
        this.commentList = commentList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        com.example.android.android_app.Model.Comment comment = commentList.get(position);
        holder.portrait.setImageResource(comment.getPortrait_id());
        holder.comment_text.setText(comment.getComment_text());
        holder.user_name.setText(comment.getUser_name());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
