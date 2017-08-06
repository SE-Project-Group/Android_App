package com.example.android.track.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.track.Application.MyApplication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.jpush.android.api.JPushInterface;
import sun.misc.BASE64Encoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thor on 2017/7/7.
 */

public class Verify {
    Context context = MyApplication.getContext();
    String prefix;


    public Verify(){
    }

    public Verify(String prefix){
        this.prefix = prefix;
    }

    public String generateSign(String resource) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
        SharedPreferences pref = context.getSharedPreferences("logIn_data", MODE_PRIVATE);
        Boolean loged = pref.getBoolean("loged",false);
        String token = pref.getString("token", "");
        return token;
    }

    public String getUser_id(){
        SharedPreferences pref = context.getSharedPreferences("logIn_data", MODE_PRIVATE);
        int user_id = pref.getInt("user_id", 0);
        String str = String.valueOf(user_id);
        return str;
    }

    public String getUser_pwd(){
        SharedPreferences pref = context.getSharedPreferences("logIn_data", MODE_PRIVATE);
        String pwd = pref.getString("password", "");
        return pwd;
    }

    public boolean getLoged(){
        SharedPreferences pref = context.getSharedPreferences("logIn_data", MODE_PRIVATE);
        Boolean loged = pref.getBoolean("loged",false);
        return loged;
    }

    public void storeToken(String token, int user_id, String user_name, String password){
        SharedPreferences.Editor editor = context.getSharedPreferences("logIn_data", MODE_PRIVATE).edit();
        editor.putBoolean("loged",true);
        editor.putString("token", token);
        editor.putInt("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("password", password);
        editor.apply();
    }


    public void delete_token(){
        SharedPreferences.Editor editor = context.getSharedPreferences("logIn_data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("loged", false);
        editor.putString("token", "");
        editor.putInt("user_id", 0);
        editor.apply();

        //delete alias
        JPushInterface.setAlias(context, "", null);

    }
}
