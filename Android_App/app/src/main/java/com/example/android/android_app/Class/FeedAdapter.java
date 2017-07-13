package com.example.android.android_app.Class;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.android_app.R;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by thor on 2017/6/28.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private List<Feed> mFeedList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView portrait_view;
        TextView feed_owner_view;
        TextView date_view;
        TextView position_view;
        TextView feedText_view;
        Button share_btn;
        Button comment_btn;
        Button like_btn;
        NineGridImageView<String> nineGridView;


        public ViewHolder (View view){
            super(view);
            portrait_view = (ImageView) view.findViewById(R.id.portrait);
            feed_owner_view = (TextView) view.findViewById(R.id.feed_owner);
            date_view = (TextView) view.findViewById(R.id.date);
            position_view = (TextView) view.findViewById(R.id.position);
            feedText_view = (TextView) view.findViewById(R.id.feed_text);
            share_btn = (Button)view.findViewById(R.id.share_btn);
            comment_btn = (Button)view.findViewById(R.id.comment_btn);
            like_btn = (Button)view.findViewById(R.id.like_btn);
            nineGridView = (NineGridImageView<String>) view.findViewById(R.id.nine_gridView);
        }
    }

    public FeedAdapter(List<Feed> feedList){
        mFeedList = feedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feed feed = mFeedList.get(position);
        holder.feed_owner_view.setText(feed.getFeed_owner());
        holder.feedText_view.setText(feed.getText());
        String temp = String.valueOf(feed.getComment_cnt());
        holder.comment_btn.setText(temp);
        temp = String.valueOf(feed.getLike_cnt());
        holder.like_btn.setText(temp);
        temp = String.valueOf(feed.getShare_cnt());
        holder.share_btn.setText(temp);
        holder.position_view.setText(feed.getPosition());
        temp = feed.getDate().toString();
        holder.date_view.setText(temp);
        holder.portrait_view.setImageResource(R.drawable.exp_pic);

        NineGridImageViewAdapter<String> mNGIVAdapter = new NineGridImageViewAdapter<String>(){
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String url) {
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.exp_pic)
                        .into(imageView);
            }
            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }
            @Override
            protected void onItemImageClick(Context context, int index, List<String> photoList) {
        /*showBigPicture(context, photoList.get(index).getBigUrl());*/
            }
        };

        holder.nineGridView.setAdapter(mNGIVAdapter);
        List<String> list = new ArrayList<>();
        String url = "http://sjtutest.oss-cn-shanghai.aliyuncs.com/example.jpg?Expires=1499965472&OSSAccessKeyId=TMP.AQEBZ1G9oXVo3NDsdp8KS2MF3DRQZfKzIhubMoWA4bVCJ2nLcfZpsv6BAAoAAAAwLAIUMpRgyaIrhm3RBdPicSUslAv-yiECFFaMEolCx2q4YYWNW80PHsVa2EuF&Signature=5%2FD3YmIOjEt0akldIhupCPuBZus%3D";
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        list.add(url);
        holder.nineGridView.setImagesData(list);
        // download picture from oss server here
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }


}
