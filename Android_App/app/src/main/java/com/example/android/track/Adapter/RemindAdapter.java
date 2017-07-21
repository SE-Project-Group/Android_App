package com.example.android.track.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Model.Remind;
import com.example.android.track.R;

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

        public ViewHolder(View view){
            super(view);
            user_portrait = (ImageView)view.findViewById(R.id.user_portrait);
            first_pic = (ImageView) view.findViewById(R.id.first_pic);
            user_name = (TextView)view.findViewById(R.id.user_name);
            time = (TextView)view.findViewById(R.id.time);
            remind_text = (TextView)view.findViewById(R.id.remind_text);
            author_text = (TextView)view.findViewById(R.id.author_text);
            author_name = (TextView)view.findViewById(R.id.author_name);
            response_btn = (Button) view.findViewById(R.id.response_btn);
        }
    }

    public RemindAdapter(List<Remind> remindList, Context cxt){
        mRemindList = remindList;
        context = cxt;
    }

    @Override
    public RemindAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_remind,parent,false);
        RemindAdapter.ViewHolder holder = new RemindAdapter.ViewHolder(view);
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
            holder.remind_text.setText("评论了我");

        if(type.equals("share"))
            holder.remind_text.setText("分享了");

        holder.time.setText(remind.getTime().toString());

        // load the user portrait use glide
        Glide.with(context)
                .load(remind.getUser_portrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(holder.user_portrait);

        holder.author_name.setText(remind.getAuthor_name());
        holder.author_text.setText(remind.getAuthor_text());

        // load first pic with glide
        Glide.with(context)
                .load(remind.getFirst_pic_url())
                .placeholder(R.drawable.exp_pic)
                .into(holder.first_pic);
    }

    @Override
    public int getItemCount(){
        return mRemindList.size();
    }
}
