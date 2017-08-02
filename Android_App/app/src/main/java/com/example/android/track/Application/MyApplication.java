package com.example.android.track.Application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;

import org.litepal.LitePal;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by thor on 2017/7/19.
 */

public class MyApplication extends Application implements IAdobeAuthClientCredentials{

    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID      = "5c7de475a1a449d2b3c366259a71ccd9";
    private static final String CREATIVE_SDK_CLIENT_SECRET  = "3762c36d-2b5a-46ac-9281-266e037d0ed1";
    private static final String CREATIVE_SDK_REDIRECT_URI   = "ams+37a109c271525d2c2e89c8acc25b0836a27462a8://adobeid/5c7de475a1a449d2b3c366259a71ccd9";
    private static final String[] CREATIVE_SDK_SCOPES       = {"email", "profile", "address"};


    private static Context context;
    private static int unReadMsgCnt;
    private static int unReadLikenCnt;
    private static int unReadCommentCnt;
    private static int unReadShareCnt;
    private static int unReadMentionCnt;
    private static int unReadChatMsgCnt;
    private static boolean newFollowFeed = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // adobe creative sdk initialize
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
        // initialize Litepal
        LitePal.initialize(this);
        // need
        LitePal.getDatabase();

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
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String[] getAdditionalScopesList() {
        return CREATIVE_SDK_SCOPES;
    }

    @Override
    public String getRedirectURI() {
        return CREATIVE_SDK_REDIRECT_URI;
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
            // then create SQLite tables
            LitePal.getDatabase();
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

    public static int getUnReadMsgCnt() {
        return unReadMsgCnt;
    }

    public static void setUnReadMsgCnt(int unReadMsgCnt) {
        MyApplication.unReadMsgCnt = unReadMsgCnt;
    }

    public static int getUnReadLikenCnt() {
        return unReadLikenCnt;
    }

    public static void setUnReadLikenCnt(int unReadLikenCnt) {
        MyApplication.unReadLikenCnt = unReadLikenCnt;
    }

    public static int getUnReadCommentCnt() {
        return unReadCommentCnt;
    }

    public static void setUnReadCommentCnt(int unReadCommentCnt) {
        MyApplication.unReadCommentCnt = unReadCommentCnt;
    }

    public static int getUnReadShareCnt() {
        return unReadShareCnt;
    }

    public static void setUnReadShareCnt(int unReadShareCnt) {
        MyApplication.unReadShareCnt = unReadShareCnt;
    }

    public static int getUnReadMentionCnt() {
        return unReadMentionCnt;
    }

    public static void setUnReadMentionCnt(int unReadMentionCnt) {
        MyApplication.unReadMentionCnt = unReadMentionCnt;
    }

    public static int getUnReadChatMsgCnt() {
        return unReadChatMsgCnt;
    }

    public static void setUnReadChatMsgCnt(int unReadChatMsgCnt) {
        MyApplication.unReadChatMsgCnt = unReadChatMsgCnt;
    }

    public static boolean hasNewFollowFeed() {
        return newFollowFeed;
    }

    public static void setNewFollowFeed(boolean newFollowFeed) {
        MyApplication.newFollowFeed = newFollowFeed;
    }
}
