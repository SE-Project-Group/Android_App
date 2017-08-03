package com.example.android.track.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.TalkingActivity;
import com.example.android.track.Model.Message;
import com.example.android.track.R;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by jarvis on 2017/7/12.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{
    private List<Message> mConversationList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout entry_btn;
        ImageView portrait;
        TextView user_name;
        TextView date;
        TextView message_text;

        public ViewHolder(View view){
            super(view);
            entry_btn = (LinearLayout) view.findViewById(R.id.entry_btn);
            portrait = (ImageView)view.findViewById(R.id.portrait_image);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            date = (TextView)view.findViewById(R.id.date);
            message_text = (TextView)view.findViewById(R.id.message_text);
        }
    }

    public ConversationAdapter(List<Message> messageList, Context cxt){
        mConversationList = messageList;
        context = cxt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.entry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, TalkingActivity.class);
                intent.putExtra("with_who", String.valueOf(mConversationList.get(position).getUser_id()));
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Message message = mConversationList.get(position);
        holder.user_name.setText(message.getUser_name());
        holder.message_text.setText(message.getMessage_text());
        holder.date.setText(message.getDate().toString());
        // load the picture use glide
        Glide.with(context)
                .load(message.getPortrait())
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait);


    }

    @Override
    public int getItemCount(){
        return mConversationList.size();
    }
}
