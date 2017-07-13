package com.example.android.android_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.example.android.android_app.Class.Permission;
import com.example.android.android_app.R;
import com.example.android.android_app.fragment.DiscoverAroundFragment;
import com.example.android.android_app.fragment.DiscoverFragment;
import com.example.android.android_app.fragment.HomeFragment;
import com.example.android.android_app.fragment.LogedUserFragment;
import com.example.android.android_app.fragment.MessageFragment;
import com.example.android.android_app.fragment.UserFragment;

import static android.R.attr.data;
import static com.baidu.location.d.j.p;

public class HomeActivity extends AppCompatActivity{
    private BDLocation now_location;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private DiscoverFragment discoverFragment;
    private DiscoverAroundFragment discoverAroundFragment;
    private LogedUserFragment logedUserFragment;
    private UserFragment userFragment;
    private BottomNavigationBar bottomNavigationBar;
    private SearchView mSearchView;
    private LocationClient mLocationClient;

    public boolean loged;
    private int old_postion = 0;
    private static final int NEW_FEED = 1;

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
        // get location client
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        // get permissons
        Permission permission = new Permission(HomeActivity.this);
        permission.getPermissions();
        Toolbar toolbar = (Toolbar)findViewById(R.id.discoverToolBar);
        setSupportActionBar(toolbar);
        setBottomNavigator();
        setDefaultFragment();
    }

    // when come back from other avtivity, set default fragment and reset bottom navigation bar
    // when first time run onResume, set default fragment and set bottom navigation bar
    @Override
    protected void onResume() {
        super.onResume();
        // get login info from file in onResume , so loged can change when come back from login activity
        SharedPreferences pref = getSharedPreferences("logIn_data", MODE_PRIVATE);
        if(pref.getBoolean("loged",false) == false)
            loged = false;
        else
            loged = true;

        bottomNavigationBar.selectTab(old_postion);
    }

    // set default fragment to discover fragment
    private void setDefaultFragment(){
        if (discoverFragment == null)
            discoverFragment = new DiscoverFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, discoverFragment);
        transaction.commit();
        old_postion = bottomNavigationBar.getCurrentSelectedPosition();
    }

    // set bottom navigation bar
    private void setBottomNavigator(){
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);  // set mode
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setActiveColor(R.color.black);
        bottomNavigationBar.setInActiveColor(R.color.gray);

        // inflate items
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_discover, "发现"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_message, "消息"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_add,"新动态"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_circle, "圈子"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnav_user, "我的"))
                .initialise();

        // bottom navigator bar listener
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = discoverFragment;
                switch (position) {
                    case 0:
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
                        if(homeFragment == null)
                            homeFragment = new HomeFragment();
                        f = homeFragment;
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
                            if(userFragment == null)
                                userFragment = new UserFragment();
                            f = userFragment;
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

}
