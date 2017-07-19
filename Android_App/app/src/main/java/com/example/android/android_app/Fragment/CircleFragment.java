package com.example.android.android_app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.Util.Verify;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by thor on 2017/6/27.
 */

public class CircleFragment extends Fragment {
    private List<Feed> feedList = new ArrayList<>();
    private RecyclerView recyclerView;

    private final static int GET_FEED_FAILED = 0;
    private final static int GET_FEED_OK = 1 ;
    private final static int NOT_LOG_IN = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFeeds();
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.home_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getActivity(), feedList);
        recyclerView.setAdapter(adapter);
        // set toolbar
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.homeToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void getFeeds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Verify verify = new Verify(getActivity());
                if(!verify.getLoged()){
                    Message message = new Message();
                    message.what = NOT_LOG_IN;
                    handler.sendMessage(message);
                    return;
                }
                RequestServer requestServer = new RequestServer(new Verify(getActivity()));
                feedList = requestServer.getCircleFeed();
                Message message = new Message();
                if(feedList == null)
                    message.what = GET_FEED_FAILED;
                else
                    message.what = GET_FEED_OK;

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
                    Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                    break;
                case GET_FEED_OK:
                    initRecyclerView();
            }
        }
    };
}
