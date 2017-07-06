package com.example.android.android_app.Class;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.android_app.R;

import java.util.List;

/**
 * Created by jarvis on 2017/7/6.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder>{
    private List<User> mUserList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait;
        TextView user_name;
        TextView last_feed;

        public ViewHolder(View view){
            super(view);
            portrait = (ImageView)view.findViewById(R.id.portrait);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            last_feed = (TextView)view.findViewById(R.id.last_feed_text);
        }
    }

    public FollowingAdapter(List<User> userList){
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.following_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        User user = mUserList.get(position);
        holder.portrait.setImageResource(user.getPortrait_id());
        holder.user_name.setText(user.getUser_name());
        holder.last_feed.setText(user.getLast_feed());
    }

    @Override
    public int getItemCount(){
        return mUserList.size();
    }
}
