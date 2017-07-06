package com.example.android.android_app.Class;

import com.baidu.location.BDLocation;

import java.util.List;

/**
 * Created by thor on 2017/7/5.
 */

public interface RequestServerInterface {
    public void logInRequest();

    public List<Feed> getAround(BDLocation location);

    public List<Feed> getMyFeed();

    public void signUp();
}
