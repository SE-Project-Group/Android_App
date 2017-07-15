package com.example.android.android_app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.android_app.Model.Message;
import com.example.android.android_app.R;

import java.util.List;

/**
 * Created by jarvis on 2017/7/12.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message> mMessageList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait;
        TextView user_name;
        TextView date;
        TextView message_text;

        public ViewHolder(View view){
            super(view);
            portrait = (ImageView)view.findViewById(R.id.portrait_image);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            date = (TextView)view.findViewById(R.id.date);
            message_text = (TextView)view.findViewById(R.id.message_text);
        }
    }

    public MessageAdapter(List<Message> messageList, Context cxt){
        mMessageList = messageList;
        context = cxt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
       ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Message message = mMessageList.get(position);
        holder.user_name.setText(message.getUser_name());
        holder.message_text.setText(message.getMessage_text());
        holder.date.setText(message.getDate().toString());
        // load the picture use glide
        Glide.with(context)
                .load(message.getPortrait_url())
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait);


    }

    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}
