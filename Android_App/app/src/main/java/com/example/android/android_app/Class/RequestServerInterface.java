package com.example.android.android_app.Class;

import com.baidu.location.BDLocation;
import com.example.android.android_app.Model.UserInfo;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by thor on 2017/7/5.
 */

public interface RequestServerInterface {
    public void logInRequest();

    public List<Feed> getAround(BDLocation location);

    public List<Feed> getMyFeed();

    public void signUp();

    public void newFeed(String jsonString);

    public void like (String feed_id);

    public List<Feed> publicPolling(Date last_update_time);

    public List<Feed> friendPolling(Date last_update_time);

    public void logOut();



    public UserInfo queryUserInfo();
    // have not implemented
    public String modifyUserInfo(String jsonStr);

    public String removeFeed(String feed_id);

    public List<String> getFollowing();

    public List<String> getFollower();

}
