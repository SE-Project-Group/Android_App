package com.example.android.track.Util;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.ClientInfo;
import com.example.android.track.Model.Comment;
import com.example.android.track.Model.Feed;
import com.example.android.track.Model.Follow;
import com.example.android.track.Model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thor on 2017/7/19.
 */

public class UserRequester {
    private final static String host = "http://106.15.188.135:8080/track/rest/app/user/";
    private Verify verify = new Verify("/track/rest/app/user/");

    // constructor with no argument
    public UserRequester(){}

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
            return "failed";
        }
        final String jsonString = jsonObject.toString();
        final JsonSender sender = new JsonSender(jsonString, host + resource);
        String result =  sender.post();
        return result;
    }


    // log in
    public String logInRequest(String user_name,String password){
        String resource = "clientLogin";
        // create url
        String url = host + resource;
        url = url + "?user_name="+user_name+"&password="+password ;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        String token ="";
        int user_id = 0;
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())  // this in most time means redis server is fucked...
                return "ERROR";
            String responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            token = jsonObject.getString("token");
            user_id = jsonObject.getInt("userId");
            //Toast.makeText(LogInActivity.this, result, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }
        verify.storeToken(token, user_id, user_name, password);
        AcquaintanceManager.saveAcquaintance(user_id);

        return "success";

    }


    // log out
    public String logOut(){
        String resource = "clientLogout";

        String url = generatePreUrl(resource, true);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()){
                return "failed";
            }
            responseData = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
    }



    // get someone's user info show on personal home
    public UserInfo getHomeInfo(int who){
        String resource = "getHomeInfo";
        String pre_url = generatePreUrl(resource, false);

        String url = pre_url + "?user_id=" + verify.getUser_id() + "&who=" + who;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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


    // get list of users following some one
    public List<Follow> getFollowing(int user) {
        String resource = "getFollowing";
        String pre_url = generatePreUrl(resource, true);

        // build request
        String url = pre_url + "&who=" + String.valueOf(user);
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


    // get someone's follower
    public List<Follow> getFollower(int user){
        String resource = "getFollower";
        String pre_url = generatePreUrl(resource, true);

        // build request
        String url = pre_url + "&who=" + String.valueOf(user);
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
        List<Follow> followers = gson.fromJson(responseData, new TypeToken<List<Follow>>() {}.getType());

        return followers;
    }

    public String follow(int who_id){
        String resource = "newFollow";
        String url = generatePreUrl(resource, true);
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("followId", who_id);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        return sender.post();
    }


    public String cancel_follow(int who_id){
        String resource = "deleteFollow";
        String url = generatePreUrl(resource, true);
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("followId", who_id);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        JsonSender sender = new JsonSender(jsonString,url);
        return sender.delete();
    }

    // get client info , include user name, id , gender, birthday and email
    public ClientInfo getClientInfo(int user_id){
        String resource = "queryPersonalInfo";
        String pre_url = generatePreUrl(resource, false);
        String url = pre_url + "?user_id=" + user_id;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
            if(!response.isSuccessful())
                return null;
            if(responseData.equals("failed"))
                return null;
        }catch (IOException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();
        return gson.fromJson(responseData, new TypeToken<ClientInfo>() {}.getType());
    }

    // modify client info
    public String modifyClientInfo(String jsonStr){
        String resource = "modifyPersonalInfo";
        String url = generatePreUrl(resource, true);
        JsonSender sender = new JsonSender(jsonStr, url);
        String response = sender.put();
        if(response.equals("success")){
            return "success";
        }
        else
            return "failed";
    }

    public String getPortraitUrl(int user_id){
        String resource = "getPortraitUrl";
        String pre_url = generatePreUrl(resource, false);
        String url = pre_url + "?user_id=" + user_id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String result = "";
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful())
                result = response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    public List<Follow> searchUser(String query){
        String resource = "searchUser";
        String pre_url = generatePreUrl(resource, false);
        String url = pre_url + "?query=" + query + "&user_id=" + verify.getUser_id();

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
        }catch (IOException e){
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Follow> result =  gson.fromJson(responseData, new TypeToken<List<Follow>>(){}.getType());
        return result;
    }

    public String getBigPortraitUrl(int user_id){
        String resource = "getBigPortraitUrl";
        String pre_ur = generatePreUrl(resource, false);
        String url = pre_ur + "?user_id=" + user_id;

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
        }catch (IOException e){
            e.printStackTrace();
        }

        return  responseData;
    }

    public String verifyPhone(String phone){
        String resource = "verifyPhone";
        String pre_ur = generatePreUrl(resource, true);
        String url = pre_ur + "&phone=" + phone;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
                return "failed";
            responseData = response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }

        return responseData;
    }

    public String changePwd(String old_pwd, String new_pwd){
        String resource = "changePwd";
        String url = generatePreUrl(resource, true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_pwd", old_pwd);
            jsonObject.put("new_pwd", new_pwd);
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), url);
        String response = sender.put();
        return response;
    }


}
