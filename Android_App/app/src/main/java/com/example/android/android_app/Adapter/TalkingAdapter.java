package com.example.android.android_app.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.android_app.Model.Talking;
import com.example.android.android_app.R;

import java.util.List;

/**ta
 * Created by jarvis on 2017/7/19.
 */

public class TalkingAdapter extends RecyclerView.Adapter<TalkingAdapter.ViewHolder>{
    private List<Talking> mTalkingList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout send_layout;
        LinearLayout receive_layout;
        TextView send_text;
        TextView receive_text;

        public ViewHolder(View view){
            super(view);
            send_layout = (LinearLayout)view.findViewById(R.id.send_layout);
            receive_layout = (LinearLayout)view.findViewById(R.id.receive_layout);
            send_text = (TextView) view.findViewById(R.id.talking_send);
            receive_text = (TextView) view.findViewById(R.id.talking_receive);
        }
    }

    public TalkingAdapter(List<Talking> talkingList){
        mTalkingList = talkingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_talking,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Talking talking = mTalkingList.get(position);
        if(talking.getType() == Talking.TYPE_RECEIVED){
            //if it is receive
            holder.receive_layout.setVisibility(View.VISIBLE);
            holder.send_layout.setVisibility(View.GONE);
            holder.receive_text.setText(talking.getContent());
        }else{
            //if it is send
            holder.send_layout.setVisibility(View.VISIBLE);
            holder.receive_layout.setVisibility(View.GONE);
            holder.send_text.setText(talking.getContent());
        }
    }

    @Override
    public int getItemCount(){
        return mTalkingList.size();
    }
}
