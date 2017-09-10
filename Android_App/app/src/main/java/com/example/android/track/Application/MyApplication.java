package com.example.android.track.Application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.example.android.track.Activity.TalkingActivity;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.MyFeed;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Util.AcquaintanceManager;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.mob.tools.utils.Data;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Set;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.analytics.android.api.LoginEvent;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static android.R.attr.data;
import static android.R.id.message;
import static cn.jpush.im.android.api.enums.ContentType.file;
import static com.baidu.location.d.j.U;
import static com.baidu.location.d.j.ac;

/**
 * Created by thor on 2017/7/19.
 */

public class MyApplication extends MobApplication implements IAdobeAuthClientCredentials{

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
    private static int unReadFollowMeCnt;
    private static boolean newFollowFeed = false;
    private static boolean newMsg = false;

    private Verify verify;

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

        verify = new Verify();


        // init JPushInterface
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        // init JMessage
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this, true);  // turn on message roaming

        // init JAnalytics
        JAnalyticsInterface.setDebugMode(true);
        JAnalyticsInterface.init(this);
        JAnalyticsInterface.initCrashHandler(this);  // turn on upload data


        // check internet
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                if(!mNetworkInfo.isAvailable()){
                    Toast.makeText(context, "当前网络不可用，很多功能将无法使用", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else {
                return;
            }
        }


        if(verify.getLoged()){
            // set Alias
            JPushInterface.setAlias(MyApplication.getContext(),verify.getUser_id(), new TagAliasCallback(){
                @Override
                public void gotResult(int responseCode, String alias, Set<String> tags) {
                    if (responseCode == 0) {
                        //Toast.makeText(context, "jpush setAlias success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "推送服务初始化失败\n您可能无法收取通知", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // login JMessageClient
        if(verify.getLoged()){
            JMessageClient.login(verify.getUser_id(), verify.getUser_pwd(),new BasicCallback() {
                @Override
                public void gotResult(int code, String desc) {
                    if (code == 0) {
                        //Toast.makeText(context, "jmessage login success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "消息服务初始化失败\n您可能无法使用聊天功能", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // receive new message

        // check if have my portrait file , if not , download
        int my_id = Integer.valueOf(verify.getUser_id());
        Acquaintance acquaintance = DataSupport.select("user_name").where("user_id = ?", verify.getUser_id())
                .findFirst(Acquaintance.class);
        if(acquaintance == null)
            AcquaintanceManager.saveAcquaintance(my_id);

        // upload open app analytics data
        LoginEvent lEvent = new LoginEvent("normal",true);
        if(verify.getLoged())
            lEvent.addKeyValue("user_id", verify.getUser_id());
        else
            lEvent.addKeyValue("user_id", "not login");
        JAnalyticsInterface.onEvent(this, lEvent);

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
        JMessageClient.logout();
        super.onTerminate();
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
                // set jmessage
                JMessageClient.login(verify.getUser_id(), password,new BasicCallback() {
                    @Override
                    public void gotResult(int code, String desc) {
                        if (code == 0) {
                            //Toast.makeText(context, "jmessage login success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "消息服务初始化失败\n您可能无法使用聊天功能", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // set jpush
                JPushInterface.setAlias(context,verify.getUser_id(), new TagAliasCallback(){
                    @Override
                    public void gotResult(int responseCode, String alias, Set<String> tags) {
                        if (responseCode == 0) {
                            //Toast.makeText(context, "jpush setAlias success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "推送服务初始化失败\n您可能无法收取通知", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // check if have my portrait file , if not , download
                int my_id = Integer.valueOf(verify.getUser_id());
                Acquaintance acquaintance = DataSupport.select("user_name").where("user_id = ?", verify.getUser_id())
                        .findFirst(Acquaintance.class);
                if(acquaintance == null)
                    AcquaintanceManager.saveAcquaintance(my_id);
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
            JPushInterface.setAlias(context, "", new TagAliasCallback(){
                @Override
                public void gotResult(int responseCode, String alias, Set<String> tags) {
                    if (responseCode == 0) {
                        //Toast.makeText(context, "jpush alias logout success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "推送服务退订异常\n您仍将收到通知", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return result;
    }

    public static String signUp(String user_name, String pwd, String phone){
        UserRequester requester = new UserRequester();
        String response_data = requester.signUp(user_name, pwd, phone);

        if(response_data.equals("existing phone"))
            return response_data;
        if(response_data.equals("existing user name"))
            return response_data;
        else{
            String user_id = response_data;
            JMessageClient.register(user_id, pwd, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if(i == 0){
                        //Toast.makeText(MyApplication.getContext(), "JMessage注册成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MyApplication.getContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            JMessageClient.getUserInfo(user_id, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if(i == 0){
                        userInfo.setNickname(user_name);
                        JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if(i == 0) {
                                    //Toast.makeText(MyApplication.getContext(), "修改昵称成功", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(MyApplication.getContext(), "修改昵称失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            return "success";
        }
    }

    // when log out, clear all database
    public static void clearData(){
        // clear database
        DataSupport.deleteAll(Acquaintance.class);
        DataSupport.deleteAll(CommentMeRecord.class);
        DataSupport.deleteAll(LikeMeRecord.class);
        DataSupport.deleteAll(MentionMeRecord.class);
        DataSupport.deleteAll(MyFeed.class);
        DataSupport.deleteAll(ShareMeRecord.class);

        // clear portrait cache

        File fileDir = MyApplication.getContext().getFilesDir();
        for (File file : fileDir.listFiles()) {
            if (file.isFile() && (file.getName().indexOf("_portrait") != -1))
                file.delete(); // 删除所有portrait文件
        }

        return;
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

    public static boolean hasNewFollowFeed() {
        return newFollowFeed;
    }

    public static void setNewFollowFeed(boolean newFollowFeed) {
        MyApplication.newFollowFeed = newFollowFeed;
    }

    public static boolean hasNewMsg() {
        return newMsg;
    }

    public static void setNewMsg(boolean newMsg) {
        MyApplication.newMsg = newMsg;
    }

    public static int getUnReadFollowMeCnt() {
        return unReadFollowMeCnt;
    }

    public static void setUnReadFollowMeCnt(int unReadFollowMeCnt) {
        MyApplication.unReadFollowMeCnt = unReadFollowMeCnt;
    }
}
