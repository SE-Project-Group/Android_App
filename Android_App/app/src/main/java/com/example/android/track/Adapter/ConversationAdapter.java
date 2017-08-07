package com.example.android.track.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.track.Activity.TalkingActivity;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.R;
import com.example.android.track.View.DragBubbleView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by jarvis on 2017/7/12.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{
    private List<Conversation> mConversationList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View conversationView;
        CircleImageView portrait;
        TextView user_name;
        TextView date;
        TextView message_text;
        DragBubbleView dragBubbleView;

        public ViewHolder(View view){
            super(view);
            conversationView = view;
            portrait = (CircleImageView) view.findViewById(R.id.portrait_image);
            user_name = (TextView)view.findViewById(R.id.user_name_text);
            date = (TextView)view.findViewById(R.id.date);
            message_text = (TextView)view.findViewById(R.id.message_text);
            dragBubbleView = (DragBubbleView) view.findViewById(R.id.dragBubbleView);
        }
    }

    public ConversationAdapter(List<Conversation> conversationList, Context cxt){
        mConversationList = conversationList;
        context = cxt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.conversationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove unread count
                int position = holder.getAdapterPosition();
                Conversation conversation = mConversationList.get(position);
                //conversation.setUnReadMessageCnt(0);

                Intent intent = new Intent(context, TalkingActivity.class);
                int target = Integer.valueOf(conversation.getTargetId());
                intent.putExtra("with_who", target);
                context.startActivity(intent);
            }
        });

        holder.dragBubbleView.setOnBubbleStateListener(new DragBubbleView.OnBubbleStateListener() {
            @Override
            public void onDrag() {
            }

            @Override
            public void onMove() {
            }

            @Override
            public void onRestore() {
            }

            @Override
            public void onDismiss() {
                // remove unread message count
                int position = holder.getAdapterPosition();
                Conversation conversation = mConversationList.get(position);
                conversation.setUnReadMessageCnt(0);
                // update adapter
                //ConversationAdapter.this.notifyItemChanged(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Conversation conversation = mConversationList.get(position);
        int user_id = Integer.valueOf(conversation.getTargetId());
        Acquaintance acquaintance = DataSupport.select("user_name").where("user_id = ?", String.valueOf(user_id))
                .findFirst(Acquaintance.class);
        if(acquaintance == null)
            Toast.makeText(context, "error get acquaintance", Toast.LENGTH_SHORT).show();
        else
            holder.user_name.setText(acquaintance.getUser_name());

        String content = conversation.getLatestText();
        if(content.length()>10){
            content = content.substring(0, 10) + "....";
        }
        holder.message_text.setText(content);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String dateStr = sdf.format(conversation.getLastMsgDate());
        holder.date.setText(dateStr);
        // load the picture use glide
        File fileDir = MyApplication.getContext().getFilesDir();
        File portrait = new File(fileDir, user_id+"_portrait");
        Glide.with(context)
                .load(portrait)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait);

        // set unread bubble
        int unreadCnt = conversation.getUnReadMsgCnt();
        if(unreadCnt == 0)
            holder.dragBubbleView.setVisibility(View.GONE);
        else if(unreadCnt > 99)
            holder.dragBubbleView.setText("99+");
        else
            holder.dragBubbleView.setText(unreadCnt+"");
    }

    @Override
    public int getItemCount(){
        return mConversationList.size();
    }
}
