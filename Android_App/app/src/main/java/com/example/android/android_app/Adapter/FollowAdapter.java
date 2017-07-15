package com.example.android.android_app.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.R;

import java.net.URL;
import java.util.List;

import static com.baidu.location.d.j.U;

/**
 * Created by jarvis on 2017/7/6.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder>{
    private List<Follow> mUserList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait;
        TextView user_name;
        Button state;

        public ViewHolder(View view){
            super(view);
            portrait = (ImageView)view.findViewById(R.id.portrait);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            state = (Button)view.findViewById(R.id.state_btn);
        }
    }

    public FollowAdapter(List<Follow> userList){
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Follow user = mUserList.get(position);
        holder.user_name.setText(user.getUser_name());
        String state = user.getState();
        if(state.equals("following"))
            holder.state.setText("已关注");
        if(state.equals("follower"))
            holder.state.setText("关注");
        else
            holder.state.setText("互为好友");

        // load the picture use glide
        Glide.with(context)
                .load(user.getportrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait);

    }

    @Override
    public int getItemCount(){
        return mUserList.size();
    }
}
