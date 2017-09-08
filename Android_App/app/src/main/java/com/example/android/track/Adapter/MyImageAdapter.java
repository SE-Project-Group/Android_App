package com.example.android.track.Adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.android.track.R;
import com.example.android.track.Util.Verify;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.jiguang.analytics.android.api.BrowseEvent;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;

/**
 * Created by thor on 2017/8/8.
 */

public class MyImageAdapter extends PagerAdapter {
    public static final String TAG = MyImageAdapter.class.getSimpleName();
    private List<String> imageUrls;
    private AppCompatActivity activity;

    private int last_position;
    private BrowseEvent bEvent;
    private long last_begin_time;

    public MyImageAdapter(List<String> imageUrls, AppCompatActivity activity) {
        this.imageUrls = imageUrls;
        this.activity = activity;
        last_position = -1;  // -1 means begin
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // browse analytics data upload
        if(last_position != -1){  // if its not from feed page, then upload browse data of last photo
            String last_url = imageUrls.get(last_position);
            // get file_name subString
            int begin_index = last_url.indexOf("/");
            int end_index = last_url.indexOf("?");
            String subStirng = last_url.substring(begin_index+1, end_index);
            // get duration
            long now_time = System.currentTimeMillis();
            long duration = now_time - last_begin_time;
            duration = duration/1000;  // transfer to second

            bEvent = new BrowseEvent(subStirng, subStirng, "photo", duration);
            bEvent.addKeyValue("browser_id", "" + new Verify().getUser_id());
            JAnalyticsInterface.onEvent(activity, bEvent);
        }


        last_position = position; // update old position
        last_begin_time = System.currentTimeMillis();  // update begin time

        // show item
        String url = imageUrls.get(position);
        PhotoView photoView = new PhotoView(activity);
        Picasso.with(activity)
                .load(url)
                .placeholder(R.drawable.default_photo)
                .into(photoView);
        container.addView(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                activity.finish();
            }
        });
        return photoView;
    }

    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}