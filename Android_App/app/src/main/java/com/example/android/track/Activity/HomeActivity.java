package com.example.android.track.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Fragment.CircleFragment;
import com.example.android.track.Fragment.UnlogUserFragment;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Util.AcquaintanceManager;
import com.example.android.track.Util.Permission;
import com.example.android.track.R;
import com.example.android.track.Fragment.DiscoverAroundFragment;
import com.example.android.track.Fragment.DiscoverFragment;
import com.example.android.track.Fragment.LogedUserFragment;
import com.example.android.track.Fragment.MessageFragment;
import com.example.android.track.Util.Verify;

import java.util.Date;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;

public class HomeActivity extends AppCompatActivity{
    private BDLocation now_location;
    private CircleFragment circleFragment;
    private MessageFragment messageFragment;
    private DiscoverFragment discoverFragment;
    private DiscoverAroundFragment discoverAroundFragment;
    private LogedUserFragment logedUserFragment;
    private UnlogUserFragment unlogUserFragment;
    private BottomNavigationBar bottomNavigationBar;
    private LocationClient mLocationClient;

    // used to update bottome bar
    private final static int UPDATE_BOTTOM_BAR = 1;
    private static boolean frontPage;

    public boolean loged;

    private int old_postion = 0;
    private boolean aroundFragment = false;

    public void setAroundFragment(boolean aroundFragment) {
        this.aroundFragment = aroundFragment;
    }

    public LocationClient getmLocationClient() {
        return mLocationClient;
    }
    public DiscoverFragment getDiscoverFragment() {
        return discoverFragment;
    }
    public DiscoverAroundFragment getDiscoverAroundFragment() {
        return discoverAroundFragment;
    }

    // new discoverAroundFragment instance when switch fragment
    public void setDiscoverAroundFragment(DiscoverAroundFragment discoverAroundFragment) {
        this.discoverAroundFragment = discoverAroundFragment;
    }

    public BDLocation getNow_location() {
        return now_location;
    }

    // location receive listener
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            now_location = bdLocation;
            String info = "latitude:" + String.valueOf(now_location.getLatitude())
                    + "   longtitude:" + String.valueOf(now_location.getLongitude());
            Log.d("Location", info);
            discoverAroundFragment.locateMe();
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);  // receive JMessage also
        // get location client
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        // get permissons
        Permission permission = new Permission(HomeActivity.this);
        permission.getPermissions();
        Toolbar toolbar = (Toolbar)findViewById(R.id.discoverToolBar);
        toolbar.setTitleTextColor(HomeActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);
        setBottomNavigator(MyApplication.getUnReadMsgCnt()
                + JMessageClient.getAllUnReadMsgCount(), MyApplication.hasNewFollowFeed());
        setDefaultFragment();
        checkReceiver();
    }

    // when come back from other avtivity, set default fragment and reset bottom navigation bar
    // when first time run onResume, set default fragment and set bottom navigation bar
    @Override
    protected void onResume() {
        super.onResume();
        // get login info from file in onResume , so loged can change when come back from login activity
        if(!new Verify().getLoged())
            loged = false;
        else
            loged = true;

        frontPage = true;
        // update unread cnt every time come from other page
        bottomNavigationBar.clearAll();
        setBottomNavigator(MyApplication.getUnReadMsgCnt()
                + JMessageClient.getAllUnReadMsgCount(), MyApplication.hasNewFollowFeed());
        bottomNavigationBar.selectTab(old_postion);
    }

    @Override
    protected void onPause() {
        frontPage = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    // set default fragment to discover fragment
    private void setDefaultFragment(){
        if (discoverFragment == null)
            discoverFragment = new DiscoverFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, discoverFragment);
        transaction.commit();
        old_postion = 0;

    }

    // set bottom navigation bar
    public void setBottomNavigator(int msgCnt, boolean followFeed){
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);  // set mode
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setActiveColor(R.color.black);
        bottomNavigationBar.setInActiveColor(R.color.gray);

        // inflate items
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_discover, "发现"));

        if(msgCnt > 0) {
            BadgeItem msgBadge = new BadgeItem()
                    .setBorderWidth(2)//Badge的Border(边界)宽度
                    //.setBorderColor("#FF0000")//Badge的Border颜色
                    //.setBackgroundColor("#9ACD32")//Badge背景颜色
                    .setGravity(Gravity.RIGHT | Gravity.TOP)//位置，默认右上角
                    .setText(String.valueOf(msgCnt))//显示的文本
                    //.setTextColor("#F0F8FF")//文本颜色
                    .setAnimationDuration(400)
                    .setHideOnSelect(true);//当选中状态时消失，非选中状态显示
            bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_message, "消息").setBadgeItem(msgBadge));
        }
        else
            bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_message, "消息"));


        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_add,"新动态"));

        if(followFeed){
                BadgeItem circleBadge = new BadgeItem()
                        .setBorderWidth(2)//Badge的Border(边界)宽度
                        .setGravity(Gravity.RIGHT | Gravity.TOP)//位置，默认右上角
                        .setText("new")//显示的文本
                        .setAnimationDuration(400)
                        .setHideOnSelect(true);//当选中状态时消失，非选中状态显示
            bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_circle, "圈子").setBadgeItem(circleBadge));
        }
        else
            bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_circle, "圈子"));

        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnav_user, "我的"));
        bottomNavigationBar.initialise();

        // bottom navigator bar listener
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = discoverFragment;
                switch (position) {
                    case 0:
                        if(aroundFragment){
                            if(discoverAroundFragment == null)
                                discoverAroundFragment = new DiscoverAroundFragment();
                            f = discoverAroundFragment;
                        }
                        else
                            f = discoverFragment;
                        old_postion = bottomNavigationBar.getCurrentSelectedPosition();
                        break;
                    case 1:
                        if(messageFragment == null)
                            messageFragment = new MessageFragment();
                        f = messageFragment;
                        old_postion = bottomNavigationBar.getCurrentSelectedPosition();
                        break;
                    case 2:
                        Intent intent = new Intent(HomeActivity.this, NewFeedActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        if(circleFragment == null)
                            circleFragment = new CircleFragment();
                        f = circleFragment;
                        old_postion = bottomNavigationBar.getCurrentSelectedPosition();
                        break;
                    case 4:
                        // check if the user has log in first
                        if(loged == true) {
                            if (logedUserFragment == null)
                                logedUserFragment = new LogedUserFragment();
                            f = logedUserFragment;
                        }
                        else{
                            if(unlogUserFragment == null)
                                unlogUserFragment = new UnlogUserFragment();
                            f = unlogUserFragment;
                        }

                        old_postion = bottomNavigationBar.getCurrentSelectedPosition();
                        break;
                    default:
                        break;
                }
                transaction.replace(R.id.fragment, f);
                transaction.commit();
            }
            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中
            }
            @Override
            public void onTabReselected(int position) {//选中 -> 选中
            }
        });
    }

    private void checkReceiver(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(5000); // sleep 5s
                        // update when has new message or new follow feed
                        if(MyApplication.hasNewMsg() || MyApplication.hasNewFollowFeed()){
                            Message message = new Message();
                            message.what = UPDATE_BOTTOM_BAR;
                            handler.sendMessage(message);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            // show message
                            Toast.makeText(this, "必须同意所有权限才可以使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_BOTTOM_BAR:
                    if (!frontPage)
                        break;
                    bottomNavigationBar.clearAll();
                    setBottomNavigator(MyApplication.getUnReadMsgCnt() +
                            JMessageClient.getAllUnReadMsgCount(), MyApplication.hasNewFollowFeed());
                    bottomNavigationBar.selectTab(old_postion);
                    MyApplication.setNewMsg(false);
                    MyApplication.setNewFollowFeed(false);
                    break;
            }
        }
    };

    public void onEvent(MessageEvent event){
        cn.jpush.im.android.api.model.Message message = event.getMessage();
        // download portrait and user_name
        int user_id  = Integer.valueOf(message.getTargetID());
        AcquaintanceManager.saveAcquaintance(user_id);

        // update unread count
        MyApplication.setNewMsg(true);
    }

}
