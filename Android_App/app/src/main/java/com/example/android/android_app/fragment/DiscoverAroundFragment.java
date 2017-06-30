package com.example.android.android_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.android.android_app.HomeActivity;
import com.example.android.android_app.R;
import com.example.android.android_app.SearchActivity;

import static android.content.ContentValues.TAG;
import static com.example.android.android_app.R.id.around_btn;
import static com.example.android.android_app.R.id.hot_btn;

/**
 * Created by thor on 2017/6/29.
 */

public class DiscoverAroundFragment extends Fragment {
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean firstLocate;
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

    public void locateMe(){
        // navigate to my location on map
        BDLocation location = ((HomeActivity) getActivity()).getNow_location();
        if(firstLocate == true) {
            if (location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeGpsLocation) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(19f);
                baiduMap.animateMapStatus(update);
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
    }
}
