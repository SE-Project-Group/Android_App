package com.example.android.android_app.Class;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.example.android.android_app.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.resource;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thor on 2017/7/5.
 */

public class RequestServer implements RequestServerInterface{
    private String host = "http://192.168.1.200:8080/track/rest/app/";
    private Handler handler;
    private int success_msg;
    private int fail_msg;
    private Activity activityContext;

    private String generatePreUrl(String resource){
        String sign = "";
        Verify verify = new Verify(resource, activityContext);
        try {
            sign = verify.generateSign();
        }catch (Exception e){
            e.printStackTrace();
        }
        String user_id = verify.getUser_id();
        String prefix_url = host+resource+"?user_ID="+user_id+"&sign="+sign;
        return prefix_url;
    }

    public RequestServer(Handler handler, int success_msg,int fail_msg, Activity activityContext) {
        this.handler = handler;
        this.success_msg = success_msg;
        this.fail_msg = fail_msg;
        this.activityContext = activityContext;
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
            user_id = jsonObject.getInt("user_id");
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
            SharedPreferences.Editor editor =activityContext.getSharedPreferences("login_data", MODE_PRIVATE).edit();
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

        List<Feed> feedList = new ArrayList<>();
        String latitude_str = String.valueOf(location.getLatitude());
        String longitude_str = String.valueOf(location.getLongitude());
        OkHttpClient client = new OkHttpClient();
        String url = generatePreUrl(resource)
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
        String resource = "MyFeed";

        List<Feed> feedList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(host+resource).build();
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
        final JsonSender sender = new JsonSender(jsonString, host+resource, success_msg, handler, activityContext);
        sender.send();
    }

    public void newFeed(String jsonString){
        String resource = "NewFeed";
        JsonSender sender = new JsonSender(jsonString, generatePreUrl(resource), success_msg, handler, activityContext);
        sender.send();
    }

    public void like(String feed_id){

    }

}
