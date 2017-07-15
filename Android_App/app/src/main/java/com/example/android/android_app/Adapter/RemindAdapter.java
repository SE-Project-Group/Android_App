package com.example.android.android_app.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.android_app.Model.Remind;
import com.example.android.android_app.R;

import java.util.List;

/**
 * Created by jarvis on 2017/7/12.
 */

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ViewHolder>{
    private List<Remind> mRemindList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_portrait;
        ImageView author_portrait;
        TextView time;
        TextView user_name;
        TextView remind_text;
        TextView author_text;
        TextView author_name;

        public ViewHolder(View view){
            super(view);
            user_portrait = (ImageView)view.findViewById(R.id.user_portrait);
            author_portrait = (ImageView)view.findViewById(R.id.author_portrait);
            user_name = (TextView)view.findViewById(R.id.user_name);
            time = (TextView)view.findViewById(R.id.time);
            remind_text = (TextView)view.findViewById(R.id.remind_text);
            author_text = (TextView)view.findViewById(R.id.author_text);
            author_name = (TextView)view.findViewById(R.id.author_name);
        }
    }

    public RemindAdapter(List<Remind> remindList){
        mRemindList = remindList;
    }

    @Override
    public RemindAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.remind_item,parent,false);
        RemindAdapter.ViewHolder holder = new RemindAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RemindAdapter.ViewHolder holder, int position){
        Remind remind = mRemindList.get(position);
        holder.user_portrait.setImageResource(remind.getUser_portrait());
        holder.author_name.setText(remind.getAuthor_name());
        holder.author_text.setText(remind.getAuthor_text());
        holder.author_portrait.setImageResource(remind.getAuthor_portrait());
        holder.user_name.setText(remind.getUser_name());
        holder.remind_text.setText(remind.getRemind_text());
        holder.time.setText(remind.getTime());
    }

    @Override
    public int getItemCount(){
        return mRemindList.size();
    }
}
