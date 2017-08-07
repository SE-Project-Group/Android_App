package com.example.android.track.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.R;
import com.example.android.track.Util.Verify;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.track.R.id.portrait;

/**ta
 * Created by jarvis on 2017/7/19.
 */

public class TalkingAdapter extends RecyclerView.Adapter<TalkingAdapter.ViewHolder>{
    private List<Message> mMessageList;
    private int target_id;
    private File hisPortrait;
    private File myPortrait;
    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout send_layout;
        RelativeLayout receive_layout;
        TextView send_text;
        TextView receive_text;
        CircleImageView his_portrait_view;
        CircleImageView my_portrait_view;

        public ViewHolder(View view){
            super(view);
            send_layout = (RelativeLayout)view.findViewById(R.id.send_layout);
            receive_layout = (RelativeLayout)view.findViewById(R.id.receive_layout);
            send_text = (TextView) view.findViewById(R.id.talking_send);
            receive_text = (TextView) view.findViewById(R.id.talking_receive);
            his_portrait_view = (CircleImageView) view.findViewById(R.id.his_portrait);
            my_portrait_view = (CircleImageView) view.findViewById(R.id.my_portrait);
        }
    }

    public TalkingAdapter(List<Message> talkingList, int target){
        mMessageList = talkingList;
        target_id = target;
        File fileDir = MyApplication.getContext().getFilesDir();
        hisPortrait = new File(fileDir, target_id+"_portrait");
        myPortrait = new File(fileDir, new Verify().getUser_id()+"_portrait");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_talking,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Message message = mMessageList.get(position);
        if(message.getDirect() == MessageDirect.receive){
            //if it is receive
            holder.receive_layout.setVisibility(View.VISIBLE);
            holder.send_layout.setVisibility(View.GONE);
            TextContent content = (TextContent) message.getContent();
            holder.receive_text.setText(content.getText());
            // load potrait
            Glide.with(MyApplication.getContext())
                    .load(hisPortrait)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.exp_pic)
                    .into(holder.his_portrait_view);
        }else{
            //if it is send
            holder.send_layout.setVisibility(View.VISIBLE);
            holder.receive_layout.setVisibility(View.GONE);
            TextContent content = (TextContent) message.getContent();
            holder.send_text.setText(content.getText());
            // load portrait
            Glide.with(MyApplication.getContext())
                    .load(myPortrait)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.exp_pic)
                    .into(holder.my_portrait_view);
        }
    }

    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}
