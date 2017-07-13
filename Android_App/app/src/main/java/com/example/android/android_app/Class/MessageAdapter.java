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
 * Created by jarvis on 2017/7/12.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message> mMessageList;

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

    public MessageAdapter(List<Message> messageList){
        mMessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
       ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Message message = mMessageList.get(position);
        holder.user_name.setText(message.getUser_name());
        holder.message_text.setText(message.getMessage_text());
        holder.date.setText(message.getDate());
        holder.portrait.setImageResource(message.getPortrait_id());

    }

    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}
