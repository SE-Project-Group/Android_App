package com.example.android.android_app.Class;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.baidu.location.d.j.S;

/**
 * Created by thor on 2017/7/4.
 */

public class Sign {
    String uri;
    long user_id;
    String token;

    public Sign(String uri, long user_id) {
        this.uri = uri;
        this.user_id = user_id;
    }

    public String generateSign() throws NoSuchAlgorithmException{
        ;
        MessageDigest md=MessageDigest.getInstance("MD5");
        byte[] bs = md.digest((uri+"?token="+token).getBytes());
        return bs.toString();
    }
    public boolean getToken(Context context){
        SharedPreferences pref = context.getSharedPreferences("logIn_data",Context.MODE_PRIVATE);
        Boolean loged = pref.getBoolean("loged",false);
        token = pref.getString("token", "");
        // have not loged in
        if(loged == false || token.equals(""))
            return false;
        else
            return true;
    }

}
