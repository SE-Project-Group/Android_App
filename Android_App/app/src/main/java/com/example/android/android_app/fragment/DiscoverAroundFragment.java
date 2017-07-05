package com.example.android.android_app.fragment;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.android.android_app.Class.RequestServer;
import com.example.android.android_app.Class.RequestServerInterface;
import com.example.android.android_app.Feed;
import com.example.android.android_app.HomeActivity;
import com.example.android.android_app.R;

import org.json.JSONObject;

import java.util.List;


import static com.example.android.android_app.R.id.feed_owner;
import static com.example.android.android_app.R.id.position;
import static com.example.android.android_app.R.id.timestamp;

/**
 * Created by thor on 2017/6/29.
 */

public class DiscoverAroundFragment extends Fragment {
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean firstLocate;
    private BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.exp_pic);
    private String url = "http://";
    private List<Feed> feedList;
    private static final int GET_AROUND_OK = 0;
    private static final int GET_AROUND_FAILER = 1;
    private LinearLayout ll_detail;
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
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_detail.setVisibility(View.GONE);
            }
        });

        ll_detail = (LinearLayout) getActivity().findViewById(R.id.ll_detail);
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
            }
            firstLocate = false;
        }
        // Mark me on the map
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", location.getLatitude());
            jsonObject.put("longtitude", location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        final RequestServerInterface requestServer = new RequestServer(jsonString, handler, GET_AROUND_OK, GET_AROUND_FAILER, getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                feedList = requestServer.getAround(location);
            }
        }).start();

    }

    private void switch_frag(){
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DiscoverFragment fragment = ((HomeActivity)getActivity()).getDiscoverFragment();
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();
    }

    private void show_around() {
        for (Feed feed : feedList){
            LatLng ll = new LatLng(feed.getLatitude(), feed.getLongtitude());
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
                Toast.makeText(getActivity().getApplicationContext(),"show detail", Toast.LENGTH_SHORT).show();
                popDetail(marker);
                return true;
            }
        });
    }

    private void popDetail(Marker marker){
        Feed feed =(Feed) marker.getExtraInfo().get("detail");
        ViewHolder viewHolder = null;
        if (ll_detail.getTag() == null)
        {
            viewHolder = new ViewHolder();
            viewHolder.owner = (TextView) getActivity().findViewById(feed_owner);
            viewHolder.timestamp = (TextView) getActivity().findViewById(timestamp);
            viewHolder.position = (TextView) getActivity().findViewById(position);
            viewHolder.feed_text = (TextView) getActivity().findViewById(R.id.feed_text);
            viewHolder.portrait = (ImageView) getActivity().findViewById(R.id.portrait);
            viewHolder.picture1 = (ImageView) getActivity().findViewById(R.id.picture1);
            viewHolder.share_btn = (LinearLayout) getActivity().findViewById(R.id.share_btn);
            viewHolder.comment_btn = (LinearLayout) getActivity().findViewById(R.id.comment_btn);
            viewHolder.like_btn = (LinearLayout) getActivity().findViewById(R.id.like_btn);
            viewHolder.share_num = (TextView) getActivity().findViewById(R.id.share_num);
            viewHolder.comment_num = (TextView) getActivity().findViewById(R.id.comment_num);
            viewHolder.like_num = (TextView) getActivity().findViewById(R.id.like_num);
            ll_detail.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) ll_detail.getTag();
        viewHolder.owner.setText(feed.getFeed_owner());
        viewHolder.timestamp.setText(feed.getTimestamp().toString());
        viewHolder.portrait.setImageResource(R.drawable.exp_pic);
        viewHolder.position.setText(feed.getPosition());
        viewHolder.feed_text.setText(feed.getText());
        viewHolder.picture1.setImageResource(R.drawable.exp_pic);
        viewHolder.share_num.setText(String.valueOf(feed.getShare_cnt()));
        viewHolder.comment_num.setText(String.valueOf(feed.getComment_cnt()));
        viewHolder.like_num.setText(String.valueOf(feed.getLike_cnt()));
        ll_detail.setVisibility(View.VISIBLE);
    }

    // use viewHolder to store layout
    private class ViewHolder {
        ImageView portrait;
        TextView owner;
        TextView timestamp;
        TextView position;
        TextView feed_text;
        ImageView picture1;
        LinearLayout share_btn;
        LinearLayout comment_btn;
        LinearLayout like_btn;
        TextView share_num;
        TextView comment_num;
        TextView like_num;

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


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_AROUND_OK:
                   show_around();
                    break;
            }
        }
    };
}
