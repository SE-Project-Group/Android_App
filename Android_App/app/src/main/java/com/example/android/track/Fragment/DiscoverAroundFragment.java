package com.example.android.track.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.android.track.Model.Feed;
import com.example.android.track.Activity.HomeActivity;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.Verify;
import com.example.android.track.View.FeedDetailView;

import java.util.List;


/**
 * Created by thor on 2017/6/29.
 */

public class DiscoverAroundFragment extends Fragment{
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean firstLocate;
    private BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mark);


    private List<Feed> feedList;
    private static final int GET_AROUND_OK = 0;
    private static final int GET_AROUND_FAILED = 1;

    private boolean loggedIn = false;

    FeedRequester requester;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disc_around, container, false);
        // set firstLocate
        firstLocate = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set toolBar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.discoverToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set homeactivity around fragment
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.setAroundFragment(true);

        Verify verify = new Verify();
        if(verify.getLoged()) {
            loggedIn = true;
            requester = new FeedRequester();
        }
        else {  // not log in , can not use like comment and share button
            requester = new FeedRequester();
        }
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
    }

    private void startLocate(){
        // get BaiduMap at the first time
        if(baiduMap == null) {
            baiduMap = mapView.getMap();
            baiduMap.setMyLocationEnabled(true);
        }
        // initialize options， set scan span and CoorType (Baidu Map use BD09LL location)
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(2000);
        option.setCoorType("bd09ll");
        ((HomeActivity) getActivity()).getmLocationClient().setLocOption(option);

        //start
        ((HomeActivity) getActivity()).getmLocationClient().start();

    }

    // used by home activity
    public void locateMe(){
        // navigate to my location on map
        final BDLocation location = ((HomeActivity) getActivity()).getNow_location();
        if(firstLocate == true) {
            if (location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeGpsLocation) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(19f);
                baiduMap.animateMapStatus(update);

                // get aroudnd feed
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        feedList = requester.getAround(location);
                        Message msg = new Message();
                        if(feedList == null)
                            msg.what = GET_AROUND_FAILED;
                        else
                            msg.what = GET_AROUND_OK;
                        handler.sendMessage(msg);
                    }
                }).start();

            }
            firstLocate = false;
        }
        // Mark me on the map
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
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

    private void show_around() {
        for (Feed feed : feedList) {
            LatLng ll = new LatLng(feed.getLatitude(), feed.getLongitude());
            // 图标
            OverlayOptions overlayOptions = new MarkerOptions().position(ll)
                    .icon(mMarkerIcon)
                    .anchor(0.5f, 1f)//覆盖物的对齐点，0.5f,0.5f为覆盖物的中心点
                    .perspective(false)
                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                    .zIndex(5);
            Marker marker = (Marker) baiduMap.addOverlay(overlayOptions);
            Bundle bundle = new Bundle();
            bundle.putSerializable("detail", feed);
            marker.setExtraInfo(bundle);
        }

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                popDetail(marker);
                return true;
            }
        });
    }

    private void popDetail(Marker marker) {
        Feed feed = (Feed) marker.getExtraInfo().get("detail");
        FeedDetailView feedDetailView = new FeedDetailView(getActivity(), feed);
        feedDetailView.show();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_AROUND_OK:
                   show_around();
                    break;
                case GET_AROUND_FAILED:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        firstLocate = true;
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
    }
}
