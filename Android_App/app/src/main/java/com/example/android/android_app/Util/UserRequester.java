package com.example.android.android_app.Util;

import com.example.android.android_app.Model.Follow;
import com.example.android.android_app.Model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thor on 2017/7/19.
 */

public class UserRequester {
    private final static String host = "http://192.168.217.1:8080/track/rest/app/";
    private Verify verify;

    // constructor with no argument
    public UserRequester(Verify verify){
        this.verify = verify;
    }

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
                .url(url)
                .build();

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


    // log out
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

    // get someone's user info show on personal home
    public UserInfo getUserInfo(int user_id){
        String resource = "getInfo";
        String pre_url = generatePreUrl(resource, false);
        if(pre_url == null){
            return null;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pre_url + "?user_id="+ user_id)
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

    // get list of users following some one
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


    // get someone's follower
    public List<Follow> getFollower(int user){
        // just as same as getFollowing
        return null;
    }

}
