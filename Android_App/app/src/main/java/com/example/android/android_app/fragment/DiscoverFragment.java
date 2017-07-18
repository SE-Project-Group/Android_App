package com.example.android.android_app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Adapter.FeedAdapter;
import com.example.android.android_app.Activity.HomeActivity;
import com.example.android.android_app.Activity.SearchActivity;
import com.example.android.android_app.R;
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.Util.Verify;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by thor on 2017/6/28.
 */

public class DiscoverFragment extends Fragment {
    private List<Feed> feedList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private final static int GET_FEED_OK = 0;
    private final static int GET_FEED_FAILED = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disc_hot, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set tool bar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.discoverToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Button search_btn = (Button) getActivity().findViewById(R.id.search_toolBar_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        Button around_btn = (Button) getActivity().findViewById(R.id.around_btn);
        around_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_frag ();
            }
        });

        // set Recycle View
        getFeeds();

        // set refresh layout
        swipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swip_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_feed();
            }
        });


    }

    private void switch_frag(){
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DiscoverAroundFragment fragment = ((HomeActivity)getActivity()).getDiscoverAroundFragment();
        if(fragment == null){
            fragment = new DiscoverAroundFragment();
            ((HomeActivity)getActivity()).setDiscoverAroundFragment(fragment);
        }
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();
    }

    // use feedlist to initialize recycleView
    private void initRecycleView(){
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.discHot_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getContext(), feedList);
        recyclerView.setAdapter(adapter);
    }


    private void getFeeds(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestServer requestServer = new RequestServer(new Verify(getActivity()));
                feedList = requestServer.getHotFeed();
                Message message = new Message();
                if(feedList == null)
                    message.what = GET_FEED_FAILED;
                else
                    message.what = GET_FEED_OK;

                handler.sendMessage(message);
            }
        }).start();
    }


    private void refresh_feed(){
        return;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FEED_OK:
                    initRecycleView();
                    break;
                case GET_FEED_FAILED:
                    Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}





