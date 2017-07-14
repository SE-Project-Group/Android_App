package com.example.android.android_app.Class;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.example.android.android_app.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.resource;
import static android.R.id.message;
import static android.content.Context.MODE_PRIVATE;
import static com.baidu.location.d.j.S;
import static com.baidu.location.d.j.ac;
import static com.baidu.location.d.j.n;

/**
 * Created by thor on 2017/7/5.
 */

public class RequestServer implements RequestServerInterface{
    private String host = "http://106.15.188.135:8080/track/rest/app/";
    private Handler handler;
    private int success_msg;
    private int fail_msg;
    private Activity activityContext;

    private String generatePreUrl(String resource){
        String sign = "";
        Verify verify = new Verify(resource, activityContext);
        String user_id = verify.getUser_id();
        if(user_id.equals("-1") && !resource.equals("feedAround"))
            return null;
        try {
            sign = verify.generateSign();
        }catch (Exception e){
            e.printStackTrace();
        }

        String prefix_url = host+resource+"?user_id="+user_id+"&sign="+sign;
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
    public RequestServer(){
    }

    public void logInRequest(){
        String resource = "clientLogin";
        String token ="";
        int user_id = 0;
        String url = host + resource;

        String user_name = ((EditText) activityContext.findViewById(R.id.user_name)).getText().toString();
        String password = ((EditText) activityContext.findViewById(R.id.password)).getText().toString();
        // send log in information to server
        url = url + "?user_name="+user_name+"&password="+password ;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        String responseData = "";
        try{
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            token = jsonObject.getString("token");
            user_id = jsonObject.getInt("userId");
            //Toast.makeText(LogInActivity.this, result, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        // if failed , send message to main thread
        Message message = new Message();
        if(responseData.equals("ERROR") || responseData.equals("")){
            message.what = fail_msg;
            handler.sendMessage(message);
        }
        // if success, send message to main thread
        else{
            // if success, store token
            SharedPreferences.Editor editor =activityContext.getSharedPreferences("logIn_data", MODE_PRIVATE).edit();
            editor.putBoolean("loged",true);
            editor.putString("token", token);
            editor.putInt("user_id", user_id);
            editor.apply();
            // send message to main thread
            message = new Message();
            message.what = success_msg;
            handler.sendMessage(message);
        }
    }

    public List<Feed> getAround(BDLocation location){
        String resource = "feedAround";
        String pre_url = generatePreUrl(resource);
        // can not log in
        List<Feed> feedList = new ArrayList<>();
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
        }
        return feedList;
    }

    public List<Feed> getMyFeed(){
        String resource = "myFeed";
        List<Feed> feedList = new ArrayList<>();
        // check if loged in
        String pre_url = generatePreUrl(resource);
        if(pre_url == null){
            Toast.makeText(activityContext, "您尚未登陆", Toast.LENGTH_SHORT).show();
            return feedList;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pre_url).build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            Gson gson = new Gson();
            feedList = gson.fromJson(responseData, new TypeToken<List<Feed>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return feedList;
    }

    public void signUp(){
        String resource = "clientSignup";
        EditText et_userName = (EditText) activityContext.findViewById(R.id.et_userName);
        EditText et_password = (EditText) activityContext.findViewById(R.id.et_password);
        EditText et_phone = (EditText) activityContext.findViewById(R.id.et_phone);
        EditText et_password_confirm = (EditText) activityContext.findViewById(R.id.et_password_confirm);
        String userName = et_userName.getText().toString();
        String password = et_password.getText().toString();
        String phone = et_phone.getText().toString();
        String password_confirm = et_password_confirm.getText().toString();

        if(phone.equals("") || userName.equals("") || password.equals("") || !password.equals(password_confirm)) {
            Toast.makeText(activityContext, "表单信息有误", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("user_name", userName);
            jsonObject.put("password", password);
        }catch (Exception e){
            e.printStackTrace();
        }
        final String jsonString = jsonObject.toString();
        final JsonSender sender = new JsonSender(jsonString, host+resource);
        String response_data = sender.send();
        if(response_data.equals("existing phone"))
            Toast.makeText(activityContext, "手机号已经被注册", Toast.LENGTH_SHORT).show();
        if(response_data.equals("existing user name"))
            Toast.makeText(activityContext, "用户名已存在", Toast.LENGTH_SHORT).show();
        if(response_data.equals("success")) {
            Message message = new Message();
            message.what = success_msg;
            handler.sendMessage(message);
        }
    }

    public void newFeed(String jsonString){
        String resource = "newFeed";
        String pre_url = generatePreUrl(resource);
        if(pre_url == null){
            Toast.makeText(activityContext, "您尚未登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        JsonSender sender = new JsonSender(jsonString, pre_url);
        String response = sender.send();
        if(response.equals("status wrong"))
            Toast.makeText(activityContext, "您尚未登录", Toast.LENGTH_SHORT).show();

        else if (!response.equals("failed")){
            Message message = new Message();
            message.what = success_msg;
            Bundle bundle = new Bundle();
            bundle.putString("feed_id", response);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    public void like(String feed_id){
        String resource = "incLikeFeed";
        // check if loged in
        String pre_url = generatePreUrl(resource);
        if(pre_url == null){
            Toast.makeText(activityContext, "您尚未登陆", Toast.LENGTH_SHORT).show();
            return;
        }
        // create json
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", feed_id);
            jsonObject.put("user_id", new Verify(resource, activityContext).getUser_id());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonSender sender = new JsonSender(jsonObject.toString(), pre_url);
        String response = sender.send();
        if(response.equals("success")){
            Message message = new Message();
            message.what = success_msg;
            handler.sendMessage(message);
        }
    }

    public void comment(String text, String feed_id, int reply_id){
        String resource = "newComment";
        // check if loged in
        String pre_url = generatePreUrl(resource);
        if(pre_url == null){
            Toast.makeText(activityContext, "您尚未登陆", Toast.LENGTH_SHORT).show();
            return;
        }
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
        JsonSender sender = new JsonSender(jsonObject.toString(), pre_url);
        String response = sender.send();
        if(response.equals("success")){
            Message message = new Message();
            message.what = success_msg;
            handler.sendMessage(message);
        }
    }

    public List<Feed> publicPolling(Date last_update_time){
        String resource = "";
        String pre_url = generatePreUrl(resource);

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

        String pre_url = generatePreUrl(resource);
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

        Message message = new Message();
        if(responseData.equals("success"))
            message.what = success_msg;


        else
            message.what = fail_msg;

        handler.sendMessage(message);
    }

}
