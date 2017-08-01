package com.example.android.track.Application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by thor on 2017/7/19.
 */

public class MyApplication extends Application {
    private static Context context;
    private static int unReadMsgCnt;
    private static int unReadChatMsgCnt;
    private static int unReadNotificationCnt;

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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JMessageClient.logout();
    }


    public static Context getContext(){
        return context;
    }

    public static String logIn(final String user_name, final String password){
        UserRequester requester = new UserRequester();
        String result = requester.logInRequest(user_name, password);
        if(!result.equals("ERROR")){
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
        return result;
    }

    public static String logOut(){
        UserRequester requester = new UserRequester();
        String result = requester.logOut();
        if(result.equals("success")){
            JMessageClient.logout();
        }
        return result;
    }

    public static int getUnReadMsgCnt(){
        return unReadMsgCnt;
    }

    public static int getUnReadNotificationCnt(){
        return unReadNotificationCnt;
    }

    public static int getUnReadChatMsgCnt(){
        return unReadChatMsgCnt;
    }

    public static void addUnReadMsgCnt(int cnt){
        unReadMsgCnt++;
    }

    public static void addUnReadChatMsgCnt(int cnt){
        unReadChatMsgCnt++;
    }
    public static void addUnReadNotificationCnt(int cnt){
        unReadNotificationCnt++;
    }

    public static void clearUnReadMsgCnt(){
        unReadMsgCnt = 0;
    }
    public static void clearUnReadNotificationCnt(){
        unReadNotificationCnt = 0;
    }

    public static void minusUnReadChatMsgCnt(int minus){
        unReadChatMsgCnt -= minus;
    }

}
