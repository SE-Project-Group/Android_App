package com.example.android.android_app.Class;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by thor on 2017/7/7.
 */

public class Verify {
    Activity context;
    String resource;
    String prefix = "track/rest/app/";

    public Verify(String resource, Activity context) {
        this.resource = resource;
        this.context = context;
    }

    public String generateSign() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String token = getToken();
        if(token.equals(""))
            return "";
        MessageDigest md=MessageDigest.getInstance("MD5");
        byte[] bs = md.digest((prefix + resource + "?token=" + token).getBytes("UTF-8"));
        String rightSign = new String(bs,"UTF-8");
        return rightSign;
    }

    private String getToken(){
        SharedPreferences pref = context.getSharedPreferences("logIn_data", Context.MODE_PRIVATE);
        Boolean loged = pref.getBoolean("loged",false);
        String token = pref.getString("token", "");
        return token;
    }

    public String getUser_id(){
        SharedPreferences pref = context.getSharedPreferences("logIn_data",Context.MODE_PRIVATE);
        Boolean loged = pref.getBoolean("loged",false);
        int user_id = pref.getInt("user_id", -1);
        String str = String.valueOf(user_id);
        return str;
    }
}
