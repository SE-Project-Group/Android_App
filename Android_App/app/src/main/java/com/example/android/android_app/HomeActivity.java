package com.example.android.android_app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.android.android_app.fragment.DiscoverFragment;
import com.example.android.android_app.fragment.HomeFragment;
import com.example.android.android_app.fragment.MessageFragment;
import com.example.android.android_app.fragment.NewFeedFragment;
import com.example.android.android_app.fragment.UserFragment;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity{
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private NewFeedFragment newFeedFragment;
    private DiscoverFragment discoverFragment;
    private UserFragment userFragment;
    private BottomNavigationBar bottomNavigationBar;
    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setDefaultFragment();
        setBottomNavigator();
        Toolbar toolbar = (Toolbar)findViewById(R.id.discoverToolBar);
        setSupportActionBar(toolbar);
    }

    private void setDefaultFragment(){
        discoverFragment = new DiscoverFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, discoverFragment);
        transaction.commit();
    }

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

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = discoverFragment;
                switch (position) {
                    case 0:
                        f = discoverFragment;
                        break;
                    case 1:
                        if(messageFragment == null)
                            messageFragment = new MessageFragment();
                        f = messageFragment;
                        break;
                    case 2:
                        if(newFeedFragment == null)
                            newFeedFragment = new NewFeedFragment();
                        f = newFeedFragment;
                        break;
                    case 3:
                        if(homeFragment == null)
                            homeFragment = new HomeFragment();
                        f = homeFragment;
                        break;
                    case 4:
                        if(userFragment == null)
                            userFragment = new UserFragment();
                        f = userFragment;
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
    
}
