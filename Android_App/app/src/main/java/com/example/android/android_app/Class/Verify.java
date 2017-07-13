package com.example.android.android_app.Class;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.baidu.platform.comapi.map.B;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import sun.misc.BASE64Encoder;

import static android.R.attr.value;
import static com.baidu.location.d.j.H;
import static com.baidu.location.d.j.S;

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

    public Verify(Activity context){
        this.context = context;
    }

    public String generateSign() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String token = getToken();
        if(token.equals(""))
            return "";
        MessageDigest md=MessageDigest.getInstance("MD5");
        String str = prefix + resource + "?token=" + token;
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String raw_sign = base64en.encode(md.digest(str.getBytes("utf-8")));
        // some special char can not be contained in URL , replace them with hex
        String sign;
        raw_sign = raw_sign.replace("%", "%25");
        raw_sign = raw_sign.replace("+","%2B");
        raw_sign = raw_sign.replace("/", "%2F");
        raw_sign = raw_sign.replace(" ","%20" );
        raw_sign = raw_sign.replace("?","%3F");
        raw_sign = raw_sign.replace("#", "%23");
        raw_sign = raw_sign.replace("&", "%26");
        raw_sign = raw_sign.replace("=", "%3D");

        return raw_sign;
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
