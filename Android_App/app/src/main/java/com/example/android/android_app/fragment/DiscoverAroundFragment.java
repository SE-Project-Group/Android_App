package com.example.android.android_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.example.android.android_app.HomeActivity;
import com.example.android.android_app.R;
import com.example.android.android_app.SearchActivity;

import static com.example.android.android_app.R.id.around_btn;
import static com.example.android.android_app.R.id.hot_btn;

/**
 * Created by thor on 2017/6/29.
 */

public class DiscoverAroundFragment extends Fragment {
    private MapView mapView;
    private BaiduMap baiduMap;
    private Thread markThread;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disc_around, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set toolBar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.discoverToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set top select button listener
        Button hot_btn = (Button) getActivity().findViewById(R.id.hot_btn);
        hot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_frag ();
            }
        });

        //set Map
        mapView = (MapView) getActivity().findViewById(R.id.bmapView);
        // set location on map
        startLocate();

        //use another thread to mark me on map every 5 s
        markThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Thread.sleep(5000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    markMeOnMap();
                }
            }
        });
        markThread.start();

    }

    private void startLocate(){
        // get BaiduMap at the first time
        if(baiduMap == null) {
            baiduMap = mapView.getMap();
            baiduMap.setMyLocationEnabled(true);
        }
        // initialize options
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        ((HomeActivity) getActivity()).getmLocationClient().setLocOption(option);

        //start
        ((HomeActivity) getActivity()).getmLocationClient().start();
    }

    private void markMeOnMap(){
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        BDLocation location = ((HomeActivity) getActivity()).getNow_location();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void switch_frag(){
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DiscoverFragment fragment = ((HomeActivity)getActivity()).getDiscoverFragment();
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();
    }

    // make good use of resource
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        ((HomeActivity) getActivity()).getmLocationClient().stop();
        baiduMap.setMyLocationEnabled(false);
        markThread.interrupt();
    }
}
