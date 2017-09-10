package com.example.android.track.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.List;


import cn.jiguang.analytics.android.api.BrowseEvent;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.track.R.id.beginning;
import static com.example.android.track.R.id.position;



/**
 * Created by thor on 2017/6/28.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{
    private List<Feed> mFeedList;
    private Activity context;
    private FeedRequester requester;
    private int my_user_id;
    private boolean logged;

    private final static int LIKE_OK = 0;
    private final static int LIKE_FAILED = 1;
    private final static int CANCEL_LIKE_OK = 2;
    private final static int CANCEL_LIKE_FAILED = 3;
    private final static int SHARE_OK = 4;
    private final static int SHARE_NOT_ALLOWED = 5;
    private final static int SHARE_FAILED = 6;


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

        // share things
        LinearLayout share_area;
        TextView share_owner_name;
        TextView share_text;


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

            // share things
            share_area  = (LinearLayout) view.findViewById(R.id.share_ll);
            share_owner_name = (TextView) view.findViewById(R.id.origin_owner);
            share_text = (TextView) view.findViewById(R.id.origin_text);
        }
    }

    public FeedAdapter(Activity context, List<Feed> feedList){
        this.context = context;
        mFeedList = feedList;
        requester = new FeedRequester();
        my_user_id = Integer.valueOf(new Verify().getUser_id());
        logged = new Verify().getLoged();
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

        holder.share_owner_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Feed feed = mFeedList.get(position);
                toHomePage(feed.getShare_owner_id());
            }
        });

        // buttons will trig some action
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged){
                    Toast.makeText(context, "您需要先登录才能使用该功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);

                if(feed.getShare_feed_id().equals(""))  // not a share feed
                    share(feed.getFeed_id(), feed.getOwner_id());
                else
                    share(feed.getShare_feed_id(), feed.getOwner_id());
            }
        });
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged){
                    Toast.makeText(context, "您需要先登录才能使用该功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                int postion =  holder.getAdapterPosition();
                Feed feed = mFeedList.get(postion);
                comment(feed.getFeed_id());
            }
        });


        // like button function is a bit complecated, you should check if checked already
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged){
                    Toast.makeText(context, "您需要先登录才能使用该功能", Toast.LENGTH_SHORT).show();
                    return;
                }
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
        String text = feed.getText();
        if(text.equals(""))
            holder.feedText_view.setVisibility(View.GONE);
        else
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
                .placeholder(R.drawable.default_portrait)
                .into(holder.portrait_view);


        NineGridImageViewAdapter<String> mNGIVAdapter = new NineGridImageViewAdapter<String>(){
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String url) {
                Picasso.with(context)  // picaso scale faster
                        .load(url)
                        .placeholder(R.drawable.gray_bg)
                        .into(imageView);
            }
            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }
            @Override
            protected void onItemImageClick(Context context, int index, List<String> photoList) {
                // go to browse page
                Intent intent = new Intent(context, PhotoViewActivity.class);
                intent.putExtra("type", "feed");
                if(feed.getShare_feed_id().equals("")) // not a share feed
                    intent.putExtra("feed_id", feed.getFeed_id());
                else
                    intent.putExtra("feed_id", feed.getShare_feed_id());
                intent.putExtra("currentPosition", index);
                context.startActivity(intent);
            }
        };

        // download picture from oss server here
        holder.nineGridView.setAdapter(mNGIVAdapter);
        holder.nineGridView.setImagesData(feed.getPicUrls());

        // share Area
        String share_feed_id = feed.getShare_feed_id();
        holder.share_area.setVisibility(View.GONE);

        if(!share_feed_id.equals("")) {  // this is a share feed
            holder.share_area.setVisibility(View.VISIBLE);
            holder.share_owner_name.setText("@" + feed.getShare_owner_name());
            holder.share_text.setText(feed.getShare_text());

        }
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

    private void share(String feed_id, int owner){
        // update recyclerView
        this.notifyItemChanged(position);
        showShareDialog(feed_id, owner);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LIKE_OK:
                    Toast.makeText(context, "like success", Toast.LENGTH_SHORT).show();
                    break;
                case LIKE_FAILED:
                    Toast.makeText(context, "like failed", Toast.LENGTH_SHORT).show();
                    break;
                case CANCEL_LIKE_OK:
                    Toast.makeText(context, "cancel success", Toast.LENGTH_SHORT).show();
                    break;
                case CANCEL_LIKE_FAILED:
                    Toast.makeText(context, "cancel failed", Toast.LENGTH_SHORT).show();
                    break;
                case SHARE_OK:
                    Toast.makeText(context, "share success", Toast.LENGTH_SHORT).show();
                    break;
                case SHARE_FAILED:
                    Toast.makeText(context, "请求发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHARE_NOT_ALLOWED:
                    Toast.makeText(context, "抱歉，该动态不是公开动态\n您不能分享非公开动态", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
    };


    private void showShareDialog(String feed_id, int owner){
        AlertDialog.Builder shareDialog =
                new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_comment, null);
        shareDialog.setTitle("分享动态");
        shareDialog.setView(dialogView);
        // send comment text
        shareDialog.setPositiveButton("发送",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.edit_text);
                        // check input
                        String input = edit_text.getText().toString();
                        if(input.length() > 140){
                            Toast.makeText(context, "输入内容不能超过140字哦", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                requester = new FeedRequester();
                                String response = requester.shareFeed(feed_id, owner, edit_text.getText().toString());

                                Message message = new Message();
                                if(response.equals("success")) {
                                    message.what = SHARE_OK;
                                }
                                else if (response.equals("failed"))
                                    message.what = SHARE_FAILED;
                                else if (response.equals("not allow"))
                                    message.what = SHARE_NOT_ALLOWED;
                                else
                                    message.what =  SHARE_FAILED;
                            }
                        }).start();
                    }
                });
        // cancel
        shareDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        shareDialog.create().show();
    }

}
