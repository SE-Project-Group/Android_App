package com.example.android.android_app.fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.android.android_app.Feed;
import com.example.android.android_app.FeedAdapter;
import com.example.android.android_app.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thor on 2017/6/27.
 */

public class HomeFragment extends Fragment {
    private List<Feed> feedList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFeeds();
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.home_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(feedList);
        recyclerView.setAdapter(adapter);
        // set toolbar
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.homeToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void initFeeds(){
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.exp_pic);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Feed exp = new Feed("SJTU",0,"ROOT",time, "Today is my birthday",0,0,list,R.drawable.exp_portrait);
        feedList.add(exp);
    }
}
