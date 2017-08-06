package com.example.android.track.View;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.Model.Feed;
import com.example.android.track.R;



import java.util.ArrayList;
import java.util.List;



/**
 * Created by thor on 2017/8/5.
 */

public class FeedDetailView {
    private Activity mContext;
    private Feed feed;

    private View dialogView;

    /**
     * @param context
     */
    public FeedDetailView(Activity context, Feed feed){
        this.mContext = context;
        this.feed = feed;

        init();
    }
    
    private void init(){
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        dialogView = mInflater.inflate(R.layout.view_feed_detail, null);
        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.recyclerView);

        List<Feed> feedList = new ArrayList<>();
        feedList.add(feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(mContext, feedList);
        recyclerView.setAdapter(adapter);
    }


    /**
     * 显示底部对话框
     */
    public void show() {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(mContext);
        final View view = dialogView;
        dialog.setTitle("动态详情");
        dialog.setView(dialogView);
        dialog.create().show();
    }

}
