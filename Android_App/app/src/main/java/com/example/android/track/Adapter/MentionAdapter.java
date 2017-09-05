package com.example.android.track.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Follow;
import com.example.android.track.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by thor on 2017/9/5.
 */

public class MentionAdapter extends RecyclerView.Adapter<MentionAdapter.ViewHolder> {
    public List<Integer> chooseList;
    private List<String> chooseNames;
    private List<Follow> acquaintanceList;
    private Activity context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView portrait_view;
        TextView user_name;
        CheckBox checkBox;
        public ViewHolder(View view){
            super(view);
            portrait_view = (CircleImageView) view.findViewById(R.id.portrait);
            checkBox = (CheckBox) view.findViewById(R.id.choose_box);
            user_name = (TextView) view.findViewById(R.id.user_name_text);
        }
    }

    public MentionAdapter(List<Follow> list, List<Integer> chooseList, List<String> chooseNames, Activity context){
        this.acquaintanceList = list;
        this.chooseList = chooseList;
        this.chooseNames = chooseNames;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_mention,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    // add to choose list
                    int position = holder.getAdapterPosition();
                    Follow acquaintance = acquaintanceList.get(position);
                    chooseList.add(acquaintance.getUser_id());
                    chooseNames.add(acquaintance.getUser_name());

                }
                else{
                    // remove from choose list
                    int position = holder.getAdapterPosition();
                    Follow acquaintance = acquaintanceList.get(position);
                    chooseList.remove(acquaintance.getUser_id());
                    chooseNames.remove(acquaintance.getUser_name());
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Follow acquaintance = acquaintanceList.get(position);
        holder.user_name.setText(acquaintance.getUser_name());

        // check uses already chosen
        if(chooseList.indexOf(acquaintance.getUser_id()) != -1){
            holder.checkBox.setChecked(true);
        }

        Glide.with(MyApplication.getContext())
                .load(acquaintance.getportrait_url())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait_view);
    }

    @Override
    public int getItemCount() {
        return acquaintanceList.size();
    }
}
