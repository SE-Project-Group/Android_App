package com.example.android.android_app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.android.android_app.Class.Feed;
import com.example.android.android_app.Class.FeedAdapter;
import com.example.android.android_app.Activity.HomeActivity;
import com.example.android.android_app.Activity.SearchActivity;
import com.example.android.android_app.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by thor on 2017/6/28.
 */

public class DiscoverFragment extends Fragment {
    private List<Feed> feedList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disc_hot, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        initFeeds();
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.discHot_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getContext(), feedList);
        recyclerView.setAdapter(adapter);

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

    private void initFeeds(){
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.exp_pic);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Feed exp = new Feed("5966e9fde9266510d4d7ecc3","Root","Today is my birthday",time.toString(),0,0,0,1,R.drawable.exp_portrait);
        feedList.add(exp);
    }

}





