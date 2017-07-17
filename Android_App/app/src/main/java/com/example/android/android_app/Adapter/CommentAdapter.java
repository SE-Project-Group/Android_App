package com.example.android.android_app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Comment;

import java.util.List;

/**
 * Created by thor on 2017/7/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;


    static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder (View view){
            super(view);

        }
    }

    public CommentAdapter(List<Comment> commentList, Context context){
        this.commentList = commentList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
