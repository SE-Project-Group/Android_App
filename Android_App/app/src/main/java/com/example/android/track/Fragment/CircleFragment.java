package com.example.android.track.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.Model.Feed;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.Verify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by thor on 2017/6/27.
 */

public class CircleFragment extends Fragment {
    private List<Feed> feedList = new ArrayList<>();
    private List<Feed> moreFeeds = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private FeedAdapter feedAdapter;

    private final static int GET_FEED_FAILED = 0;
    private final static int GET_AFTER_FEED_OK = 1 ;
    private final static int GET_BEFORE_FEED_OK = 2;
    private final static int NOT_LOG_IN = 3;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set toolbar
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.homeToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // init recyclerView
        initRecyclerView();

        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String dateStr = sdf.format(nowTime);
        getFeeds("before", dateStr);


        // set refresh layout
        swipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swip_refresh);
        swipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swip_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(feedList.size() == 0){
                    // if have no friend feed , refresh again
                    Date nowTime = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String dateStr = sdf.format(nowTime);
                    getFeeds("before", dateStr);
                }

                String last_date = feedList.get(0).getDate();
                getFeeds("after", last_date);
            }
        });
    }

    private void getFeeds(String direction, String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // chech if logged in
                Verify verify = new Verify();
                if (!verify.getLoged()) {
                    Message message = new Message();
                    message.what = NOT_LOG_IN;
                    handler.sendMessage(message);
                    return;
                }

                FeedRequester requester = new FeedRequester();
                moreFeeds = requester.getCircleFeed(direction, time);
                Message message = new Message();
                if (feedList == null)
                    message.what = GET_FEED_FAILED;

                else {
                    if(direction.equals("after"))
                        message.what = GET_AFTER_FEED_OK;
                    else if(direction.equals("before"))
                        message.what = GET_BEFORE_FEED_OK;
                }


            }
        }).start();
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.discHot_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getActivity(), feedList);
        recyclerView.setAdapter(adapter);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOT_LOG_IN:
                    Toast.makeText(getActivity(), "not log in", Toast.LENGTH_SHORT).show();
                    break;
                case GET_FEED_FAILED:
                    Toast.makeText(getActivity(), "get feed failed", Toast.LENGTH_SHORT).show();
                    break;
                case GET_AFTER_FEED_OK:
                    feedList.addAll(0, moreFeeds);
                    feedAdapter.notifyItemRangeInserted(0, moreFeeds.size());
                    break;
                case GET_BEFORE_FEED_OK:
                    feedList.addAll(moreFeeds);
                    feedAdapter.notifyItemRangeInserted(feedList.size()-1, moreFeeds.size());
                    break;
                default:
                    break;
            }
        }
    };
}
