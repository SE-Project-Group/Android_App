package com.example.android.android_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.android.android_app.fragment.DiscoverFragment;
import com.example.android.android_app.fragment.HomeFragment;
import com.example.android.android_app.fragment.MessageFragment;
import com.example.android.android_app.fragment.NewFeedFragment;
import com.example.android.android_app.fragment.UserFragment;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private NewFeedFragment newFeedFragment;
    private DiscoverFragment discoverFragment;
    private UserFragment userFragment;
    private BottomNavigationBar bottomNavigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setDefaultFragment();
        setBottomNavigator();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_btn:
                Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void setDefaultFragment(){
        homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, homeFragment);
    }

    private void setBottomNavigator(){
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);  // set mode
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setActiveColor(R.color.black);
        bottomNavigationBar.setInActiveColor(R.color.gray);

        // inflate items
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_home, "主页"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_message, "消息"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_add,"新动态"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnvg_discover, "发现"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_btmnav_user, "用户"))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = homeFragment;
                switch (position) {
                    case 0:
                        f = homeFragment;
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
                        if(discoverFragment == null)
                            discoverFragment = new DiscoverFragment();
                        f = discoverFragment;
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
