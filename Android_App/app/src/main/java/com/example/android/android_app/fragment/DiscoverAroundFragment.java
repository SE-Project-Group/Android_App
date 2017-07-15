package com.example.android.android_app.Fragment;

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
import com.example.android.android_app.Util.RequestServer;
import com.example.android.android_app.Util.RequestServerInterface;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Activity.HomeActivity;
import com.example.android.android_app.R;

import java.util.List;


import static com.example.android.android_app.R.id.feed_owner;
import static com.example.android.android_app.R.id.position;

/**
 * Created by thor on 2017/6/29.
 */

public class DiscoverAroundFragment extends Fragment implements View.OnClickListener{
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean firstLocate;
    private BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mark);
    private List<Feed> feedList;
    private static final int GET_AROUND_OK = 0;
    private static final int GET_AROUND_FAILED = 1;
    private LinearLayout ll_detail;
    private static final int LIKE_OK = 2;
    private static final int LIKE_FAILED = 3;
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

                final RequestServerInterface requestServer = new RequestServer( handler, GET_AROUND_OK, GET_AROUND_FAILED, getActivity());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        feedList = requestServer.getAround(location);
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

    private void popDetail(Marker marker){
        Feed feed =(Feed) marker.getExtraInfo().get("detail");
        ViewHolder viewHolder = null;
        if (ll_detail.getTag() == null)
        {
            viewHolder = new ViewHolder();
            viewHolder.owner = (TextView) getActivity().findViewById(feed_owner);
            viewHolder.date = (TextView) getActivity().findViewById(R.id.date);
            viewHolder.position = (TextView) getActivity().findViewById(position);
            viewHolder.feed_text = (TextView) getActivity().findViewById(R.id.feed_text);
            viewHolder.portrait = (ImageView) getActivity().findViewById(R.id.portrait);
            viewHolder.share_btn = (Button) getActivity().findViewById(R.id.share_btn);
            viewHolder.comment_btn = (Button) getActivity().findViewById(R.id.comment_btn);
            viewHolder.like_btn = (Button) getActivity().findViewById(R.id.like_btn);
            ll_detail.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) ll_detail.getTag();
        viewHolder.owner.setText(feed.getFeed_owner());
        viewHolder.date.setText(feed.getDate().toString());
        viewHolder.portrait.setImageResource(R.drawable.exp_pic);
        viewHolder.position.setText(feed.getPosition());
        viewHolder.feed_text.setText(feed.getText());
        viewHolder.picture1.setImageResource(R.drawable.exp_pic);
        viewHolder.share_btn.setText(String.valueOf(feed.getShare_cnt()));
        viewHolder.comment_btn.setText(String.valueOf(feed.getComment_cnt()));
        viewHolder.like_btn.setText(String.valueOf(feed.getLike_cnt()));
        viewHolder._id = feed.get_id(); ////////////////////

        // bind listener
        viewHolder.like_btn.setOnClickListener(this);
        viewHolder.comment_btn.setOnClickListener(this);
        viewHolder.share_btn.setOnClickListener(this);

        ll_detail.setVisibility(View.VISIBLE);
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

    // use viewHolder to store layout
    private class ViewHolder {
        String _id;
        ImageView portrait;
        TextView owner;
        TextView date;
        TextView position;
        TextView feed_text;
        ImageView picture1;
        Button share_btn;
        Button comment_btn;
        Button like_btn;

    }
    // make good use of resource

    @Override
    public void onClick(View v) {
        final ViewHolder viewHolder = (ViewHolder) ll_detail.getTag();
        switch (v.getId()){
            case R.id.share_btn:
                break;
            case R.id.comment_btn:
                break;
            case R.id.like_btn:
                final RequestServerInterface requestServer = new RequestServer(handler, LIKE_OK, LIKE_FAILED, getActivity());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestServer.like(viewHolder._id);
                    }
                }).start();
                break;
        }
    }

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
