package com.example.android.android_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.internal.BottomNavigationMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

/**
 * Created by thor on 2017/6/27.
 */

public class BottomNavigator extends LinearLayout {
    private BottomNavigationBar bottomNavigationBar;
    public BottomNavigator(Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.bottom_navigator, this);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar); // set layout file
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


        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        Intent intent_0 = new Intent((Activity) getContext(),HomeActivity.class);
                        ((Activity) getContext()).startActivity(intent_0);
                        break;
                    case 1:
                        Intent intent_1 = new Intent((Activity) getContext(),MessageActivity.class);
                        ((Activity) getContext()).startActivity(intent_1);
                        break;
                    case 2:
                        Intent intent_2 = new Intent((Activity) getContext(),NewFeedActivity.class);
                        ((Activity) getContext()).startActivity(intent_2);
                        break;
                    case 3:
                        Intent intent_3 = new Intent((Activity) getContext(),DiscoverActivity.class);
                        ((Activity) getContext()).startActivity(intent_3);
                        break;
                    case 4:
                        Intent intent_4 = new Intent((Activity) getContext(),UserActivity.class);
                        ((Activity) getContext()).startActivity(intent_4);
                        break;
                    default:
                        break;
                }
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

