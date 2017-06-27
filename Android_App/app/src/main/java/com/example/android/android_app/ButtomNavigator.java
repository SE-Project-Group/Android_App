package com.example.android.android_app;

import android.content.Context;
import android.support.design.internal.BottomNavigationMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

/**
 * Created by thor on 2017/6/27.
 */

public class ButtomNavigator extends LinearLayout {
    public ButtomNavigator(Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.bottom_navigator, this);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_btmnav_user, "用户"))
                //.addItem(new BottomNavigationItem(R.mipmap.ic_directions_bike_white_24dp, "骑行"))
                //.addItem(new BottomNavigationItem(R.mipmap.ic_directions_bus_white_24dp, "公交"))
                //.addItem(new BottomNavigationItem(R.mipmap.ic_directions_car_white_24dp, "自驾"))
                //.addItem(new BottomNavigationItem(R.mipmap.ic_directions_railway_white_24dp, "火车"))
                .initialise();//所有的设置需在调用该方法前完成


    }

}
