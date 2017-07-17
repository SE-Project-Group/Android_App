package com.example.android.android_app.Util;

import com.baidu.location.BDLocation;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.Model.UserInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by thor on 2017/7/5.
 */

public interface RequestServerInterface {
    public String logInRequest();

    public List<Feed> getAround(BDLocation location);

    public List<Feed> getMyFeed();

    public void signUp();

    public void newFeed(String jsonString);

    public String like (String feed_id);

    public List<Feed> publicPolling(Date last_update_time);

    public List<Feed> friendPolling(Date last_update_time);

    public void logOut();

    public UserInfo queryUserInfo();

    public String modifyUserInfo(String jsonStr);

    public String removeFeed(String feed_id);

    public List<Follow> getFollowing(int user);

    public List<Follow> getFollower(int user);

}
