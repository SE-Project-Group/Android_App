package com.example.android.android_app.Util;


import com.baidu.location.BDLocation;
import com.example.android.android_app.Application.MyApplication;
import com.example.android.android_app.Model.Feed;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by thor on 2017/7/5.
 */

public class FeedRequester{
    private final static String host = "http://192.168.1.100:8080/track/rest/app/feed/";
    private Verify verify = new Verify();

    private String generatePreUrl(String resource, Boolean needLogIn){
        String prefix_url;
        if(needLogIn) {
            String sign = "";
            String user_id = verify.getUser_id();
            try {
                sign = verify.generateSign(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            prefix_url = host + resource + "?user_id=" + user_id + "&sign=" + sign;
        }
        else
            prefix_url = host + resource;

        return prefix_url;
    }



    // constructor with no argument
    public FeedRequester(){

    }



    // get aroud feed and show on map
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
        String url = pre_url + "?time=" + "2016-01-01 10:00:00";

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


    // need log in
    public List<Feed> loggedGetOnePersonFeeds(int who){
        String resource = "getFeedsLoggedIn";
        List<Feed> feedList;
        String pre_url = generatePreUrl(resource, true);

        String url = pre_url + "&who=" + who;
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


    // do not need log in
    public List<Feed> unLoggedGetOnePersonFeeds(int who){
        String resource = "getFeedsNotLoggedIn";
        List<Feed> feedList;
        String pre_url = generatePreUrl(resource, false);

        String url = pre_url + "?who=" + who;
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
        String resource = "getFollowingFeedList";
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


    // like
    public String like(String feed_id){
        String resource = "incLikeFeed";
        // check if loged in
        String url = generatePreUrl(resource, true);
        // create json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", feed_id);
            jsonObject.put("user_id", verify.getUser_id());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        String response = sender.send();
        return response;
    }


    // comment
    public String comment(String text, String feed_id, int reply_id){
        String resource = "newComment";
        // check if loged in
        String url = generatePreUrl(resource, true);
        // create json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", feed_id);
            jsonObject.put("user_id", verify.getUser_id());
            jsonObject.put("text", text);
            jsonObject.put("reply_id", reply_id);
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        String response = sender.send();
        return response;
    }





}
