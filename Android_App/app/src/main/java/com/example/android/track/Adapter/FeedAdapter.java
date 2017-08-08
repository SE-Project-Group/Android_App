package com.example.android.track.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.C;
import com.bumptech.glide.Glide;
import com.example.android.track.Activity.CommentActivity;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Activity.PhotoViewActivity;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Feed;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.Verify;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.track.R.id.portrait;
import static com.example.android.track.R.id.position;
import static java.lang.System.in;
import static java.lang.System.load;


/**
 * Created by thor on 2017/6/28.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{
    private List<Feed> mFeedList;
    private Activity context;
    private FeedRequester requester;
    private int my_user_id;

    private final static int LIKE_OK = 0;
    private final static int LIKE_FAILED = 1;
    private final static int CANCEL_LIKE_OK = 2;
    private final static int CANCEL_LIKE_FAILED = 3;


    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView portrait_view;
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
            portrait_view = (CircleImageView) view.findViewById(R.id.portrait);
            feed_owner_view = (TextView) view.findViewById(R.id.feed_owner);
            date_view = (TextView) view.findViewById(R.id.date);
            position_view = (TextView) view.findViewById(position);
            feedText_view = (TextView) view.findViewById(R.id.feed_text);
            share_btn = (Button)view.findViewById(R.id.share_btn);
            comment_btn = (Button)view.findViewById(R.id.comment_btn);
            like_btn = (Button)view.findViewById(R.id.like_btn);
            nineGridView = (NineGridImageView<String>) view.findViewById(R.id.nine_gridView);
        }
    }

    public FeedAdapter(Activity context, List<Feed> feedList){
        this.context = context;
        mFeedList = feedList;
        requester = new FeedRequester();
        my_user_id = Integer.valueOf(new Verify().getUser_id());
    }

    private void toHomePage(int user_id){
        // navigate to other user's home page
        Intent intent = new Intent(context, PersonalHomeActivity.class);
        intent.putExtra("user_id", user_id);
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
                toHomePage(feed.getOwner_id());
            }
        });
        holder.feed_owner_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = mFeedList.get(position);
                toHomePage(feed.getOwner_id());
            }
        });

        // buttons will trig some action
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                share(feed.getFeed_id());
            }
        });
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                comment(feed.getFeed_id());
            }
        });


        // like button function is a bit complecated, you should check if checked already
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update view
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                // used to liked
                if(!feed.getLiked()) {
                    int new_cnt = Integer.valueOf(holder.like_btn.getText().toString()) + 1;
                    holder.like_btn.setText(String.valueOf(new_cnt));
                    int liked_color = MyApplication.getContext().getResources().getColor(R.color.orange);
                    holder.like_btn.setTextColor(liked_color);
                    like(feed.getFeed_id(), position);
                    feed.setLiked(true);
                }
                else{
                    int new_cnt = Integer.valueOf(holder.like_btn.getText().toString()) - 1;
                    holder.like_btn.setText(String.valueOf(new_cnt));
                    int unliked_color = MyApplication.getContext().getResources().getColor(R.color.gray);
                    holder.like_btn.setTextColor(unliked_color);
                    cancelLike(feed.getFeed_id(), position);
                    feed.setLiked(false);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feed feed = mFeedList.get(position);
        holder.feed_owner_view.setText(feed.getOwner_name());
        holder.feedText_view.setText(feed.getText());
        String temp = String.valueOf(feed.getComment_cnt());
        holder.comment_btn.setText(temp);
        temp = String.valueOf(feed.getLike_cnt());
        holder.like_btn.setText(temp);
        // if liked , set number color to orange
        if(feed.getLiked()){
            int liked_color = MyApplication.getContext().getResources().getColor(R.color.orange);
            holder.like_btn.setTextColor(liked_color);
        }
        // if this is my own feed, do not allow like
        if(feed.getOwner_id() == my_user_id){
            holder.like_btn.setClickable(false);
        }
        temp = String.valueOf(feed.getShare_cnt());
        holder.share_btn.setText(temp);
        holder.position_view.setText(feed.getPosition());
        temp = feed.getDate().toString();
        holder.date_view.setText(temp);
        // load portrait
        Glide.with(context)
                .load(feed.getPortrait_url())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.exp_pic)
                .into(holder.portrait_view);


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
                Intent intent = new Intent(context, PhotoViewActivity.class);
                intent.putExtra("type", "feed");
                intent.putExtra("feed_id", feed.getFeed_id());
                intent.putExtra("currentPosition", index);
                context.startActivity(intent);
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

    private void like(final String feed_id, int position){
        // update recyclerView
        this.notifyItemChanged(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = requester.like(feed_id);
                Message message = new Message();
                if(response.equals("success")) {
                    message.what = LIKE_OK;
                }
                else
                    message.what = LIKE_FAILED;
            }
        }).start();
    }

    private void cancelLike(final String feed_id, int position){
        // update recyclerView
        this.notifyItemChanged(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = requester.cancelLike(feed_id);
                Message message = new Message();
                if(response.equals("success")) {
                    message.what = CANCEL_LIKE_OK;
                }
                else
                    message.what = CANCEL_LIKE_FAILED;
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LIKE_OK:
                    Toast.makeText(MyApplication.getContext(), "like success", Toast.LENGTH_SHORT).show();
                    break;
                case LIKE_FAILED:
                    Toast.makeText(MyApplication.getContext(), "like failed", Toast.LENGTH_SHORT).show();
                    break;
                case CANCEL_LIKE_OK:
                    Toast.makeText(MyApplication.getContext(), "cancel success", Toast.LENGTH_SHORT).show();
                    break;
                case CANCEL_LIKE_FAILED:
                    Toast.makeText(MyApplication.getContext(), "cancel failed", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

}
