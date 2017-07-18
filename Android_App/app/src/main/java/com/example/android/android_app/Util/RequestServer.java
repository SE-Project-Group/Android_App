package com.example.android.android_app.Util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.example.android.android_app.Model.Feed;
import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.Model.UserInfo;
import com.example.android.android_app.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.baidu.location.d.j.r;


/**
 * Created by thor on 2017/7/5.
 */

public class RequestServer{
    private String host = "http://192.168.1.200:8080/track/rest/app/";
    private Handler handler;
    private int success_msg;
    private int fail_msg;
    private Activity activityContext;
    private Verify verify;

    private String generatePreUrl(String resource, Boolean needLogIn){
        String prefix_url;
        if(needLogIn) {
            String sign = "";
            String user_id = verify.getUser_id();
            if (user_id.equals("-1") && !resource.equals("feedAround"))
                return null;
            try {
                sign = verify.generateSign();
            } catch (Exception e) {
                e.printStackTrace();
            }

            prefix_url = host + resource + "?user_id=" + user_id + "&sign=" + sign;
        }
        else
            prefix_url = host + resource;

        return prefix_url;
    }

    // constructor
    public RequestServer(Handler handler, int success_msg,int fail_msg, Activity activityContext) {
        this.handler = handler;
        this.success_msg = success_msg;
        this.fail_msg = fail_msg;
        this.activityContext = activityContext;
    }

    // constructor with no argument
    public RequestServer(Verify verify){
        this.verify = verify;
    }

    // sign up
    public String signUp(String user_name,String password,String phone) {
        String resource = "clientSignup";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("user_name", user_name);
            jsonObject.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String jsonString = jsonObject.toString();
        final JsonSender sender = new JsonSender(jsonString, host + resource);
        return sender.send();
    }


    // log in
    public String logInRequest(String user_name,String password){
        String resource = "clientLogin";
        // create url
        String url = host + resource;
        url = url + "?user_name="+user_name+"&password="+password ;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();

        String token ="";
        int user_id = 0;
        try{
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            token = jsonObject.getString("token");
            user_id = jsonObject.getInt("userId");
            //Toast.makeText(LogInActivity.this, result, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            return "failed";
        }
        verify.storeToken(token, user_id);
        return "success";

    }


    public List<Feed> getAround(BDLocation location){
        String resource = "feedAround";
        String pre_url = generatePreUrl(resource, true);
        // can not log in
        List<Feed> feedList;
        String latitude_str = String.valueOf(location.getLatitude());
        String longitude_str = String.valueOf(location.getLongitude());
        OkHttpClient client = new OkHttpClient();
        String url = pre_url
                +"&latitude="+latitude_str+"&longitude="+longitude_str;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData  = response.body().string();
            Gson gson = new Gson();
            feedList = gson.fromJson(responseData, new TypeToken<List<Feed>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return feedList;
    }

    public List<Feed> getHotFeed(){
        String resource = "getFeedFromTime";
        String pre_url = generatePreUrl(resource, false);
        String url = pre_url + "&time=" + "2016-01-01 10:00:00";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        List<Feed> feedList;
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
                return null;
            String responseData = response.body().string();
            Gson gson = new Gson();
            feedList = gson.fromJson(responseData, new TypeToken<List<Feed>>(){}.getType());
            if(feedList.size() == 0)
                return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return feedList;

    }


    public List<Feed> getMyFeed(){
        String resource = "myFeed";
        List<Feed> feedList;
        // check if loged in
        String url = generatePreUrl(resource, true);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            Gson gson = new Gson();
            feedList = gson.fromJson(responseData, new TypeToken<List<Feed>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return feedList;
    }

    public List<Feed> getCircleFeed(){
        String resource = "";
        String url = generatePreUrl(resource, true);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        List<Feed> feedList;
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
                return null;
            String responseData = response.body().string();
            Gson gson = new Gson();
            feedList = gson.fromJson(responseData, new TypeToken<List<Feed>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();;
            return null;
        }
        return feedList;
    }



    public String newFeed(String jsonString){
        String resource = "newFeed";
        String url = generatePreUrl(resource, true);
        JsonSender sender = new JsonSender(jsonString, url);
        String response = sender.send();

        return response;
    }



    // new begin

    public String like(String feed_id){
        String resource = "incLikeFeed";
        // check if loged in
        String url = generatePreUrl(resource, true);
        // create json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", feed_id);
            jsonObject.put("user_id", new Verify(resource, activityContext).getUser_id());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        String response = sender.send();
        return response;
    }



    public String comment(String text, String feed_id, int reply_id){
        String resource = "newComment";
        // check if loged in
        String url = generatePreUrl(resource, true);
        // create json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", feed_id);
            jsonObject.put("user_id", new Verify(resource, activityContext).getUser_id());
            jsonObject.put("text", text);
            jsonObject.put("reply_id", reply_id);
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        String response = sender.send();
        return response;
    }


    public List<Feed> publicPolling(Date last_update_time){
        String resource = "";
        String pre_url = generatePreUrl(resource, false);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                url(pre_url + "?last_update_time" + last_update_time.toString())
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        // parse response
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseData);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public List<Feed> friendPolling(Date last_update_time){
        return null;
    }

    public void logOut(){
        String resource = "clientLogout";

        String pre_url = generatePreUrl(resource, true);
        if(pre_url == null){
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pre_url)
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()){
                return;
            }

            responseData = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public UserInfo getUserInfo(int user_id){
        String resource = "queryPersonalInfo";
        String pre_url = generatePreUrl(resource, false);
        if(pre_url == null){
            return null;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pre_url)
                .build();

        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
                return null;
            responseData  = response.body().string();
            if(responseData.equals("failed"))
                return null;
        }catch (Exception e){
            e.printStackTrace();
        }

        Gson gson = new Gson();
        UserInfo result = gson.fromJson(responseData, new TypeToken<UserInfo>(){}.getType());

        return result;
    }

    public String modifyUserInfo(String jsonStr){
        String resource =  "modifyPersonalInfo";
        String pre_url = generatePreUrl(resource, true);
        if(pre_url == null){
            return null;
        }
        JsonSender sender = new JsonSender(jsonStr, pre_url);
        String response = sender.send();
        if(response.equals("success")){
            return "success";
        }
        else
            return "failed";
    }

    public String removeFeed(String feed_id){
        String resource = "removeFeed";
        String pre_url = generatePreUrl(resource, true);
        if(pre_url == null){
            return null;
        }

        String url = pre_url + "&feed_id" + feed_id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
                return null;
            responseData = response.body().string();

        }catch (Exception e){
            e.printStackTrace();
        }
        if(responseData.equals("success"))
            return "success";
        else
            return "failed";
    }

    public List<Follow> getFollowing(int user) {
        String resource = "getFollowing";
        String pre_url = generatePreUrl(resource, true);
        if (pre_url == null)
            return null;

        // build request
        String url = pre_url + "&user=" + String.valueOf(user);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // get response
        String responseData = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            }
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // handle response
        Gson gson = new Gson();
        List<Follow> followings = gson.fromJson(responseData, new TypeToken<List<Follow>>() {}.getType());

        return followings;
    }

    public List<Follow> getFollower(int user){
        // just as same as getFollowing
        return null;
    }

}
