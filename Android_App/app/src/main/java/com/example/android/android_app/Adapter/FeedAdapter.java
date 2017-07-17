package com.example.android.android_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.android_app.Activity.CommentActivity;
import com.example.android.android_app.Activity.PersonalHomeActivity;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.RequestServer;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.List;


/**
 * Created by thor on 2017/6/28.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{
    private List<Feed> mFeedList;
    private Context context;

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

    public FeedAdapter(Context context, List<Feed> feedList){
        mFeedList = feedList;
    }

    private void toHomePage(int user_id){
        // navigate to other user's home page
        Intent intent = new Intent(context, PersonalHomeActivity.class);
        intent.putExtra("user_ud", user_id);
        context.startActivity(intent);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // bind click listener for views

        // portrait and the name of feed will navigate to their home page
        holder.portrait_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = mFeedList.get(position);
                toHomePage(feed.getUser_ID());
            }
        });
        holder.feed_owner_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = mFeedList.get(position);
                toHomePage(feed.getUser_ID());
            }
        });

        // buttons will trig some action
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                like(feed.get_id());
            }
        });
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                comment(feed.get_id());
            }
        });
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                share(feed.get_id());
            }
        });
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

        // download picture from oss server here
        holder.nineGridView.setAdapter(mNGIVAdapter);
        holder.nineGridView.setImagesData(feed.getPicUrls());
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }


    // button click activity

    private void like(final String feed_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestServer requestServer = new RequestServer();
                String response = requestServer.like(feed_id);
            }
        }).start();
    }

    private void comment(String feed_id) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("feed_id", feed_id);
        context.startActivity(intent);
    }

    private void share(String feed_id){

    }

}
