package com.example.android.track.Application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.android.track.Util.Verify;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by thor on 2017/7/19.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // set context
        context = getApplicationContext();
        // init JPushInterface
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        // init JMessageClient
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this, true);  // turn on message roaming

        // if logged , log in JMessage
        Verify verify = new Verify();
        if(verify.getLoged()){
            JMessageClient.login(verify.getUser_id(), verify.getUser_id(),new BasicCallback() {
                @Override
                public void gotResult(int code, String desc) {
                    if (code == 0) {
                    } else {
                        Toast.makeText(context, "jmessage login error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public static Context getContext(){
        return context;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        JMessageClient.logout();
    }
}
