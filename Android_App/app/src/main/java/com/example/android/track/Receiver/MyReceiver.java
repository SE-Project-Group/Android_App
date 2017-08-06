package com.example.android.track.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.ClientInfo;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Util.UserRequester;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import cn.jpush.android.api.JPushInterface;

import static cn.jpush.im.android.api.enums.ContentType.file;
import static com.baidu.location.d.j.S;
import static com.baidu.location.d.j.g;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * Created by thor on 2017/7/21.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        // receive custom message
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            //String external = bundle.getString(JPushInterface.EXTRA_EXTRA);

            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String title = message.split("#")[0];   // get title
            String json_message = message.replace(title+"#", ""); // remove title
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();


            int old_cnt = MyApplication.getUnReadMsgCnt();
            MyApplication.setUnReadMsgCnt(old_cnt + 1);
            switch (title){
                case "NewLikeMessage":
                    old_cnt = MyApplication.getUnReadLikenCnt();
                    MyApplication.setUnReadLikenCnt(old_cnt + 1);
                    LikeMeRecord likeMeRecord = gson.fromJson(json_message, new TypeToken<LikeMeRecord>() {}.getType());
                    likeMeRecord.setStatus("unRead");
                    likeMeRecord.save();
                    savePortrait(likeMeRecord.getUser_id(), likeMeRecord.getUser_name());
                    break;
                case "NewCommentMessage":
                    old_cnt = MyApplication.getUnReadCommentCnt();
                    MyApplication.setUnReadCommentCnt(old_cnt++);
                    CommentMeRecord commentMeRecord = gson.fromJson(json_message, new TypeToken<CommentMeRecord>(){}.getType());
                    commentMeRecord.setStatus("unRead");
                    commentMeRecord.save();
                    savePortrait(commentMeRecord.getUser_id(), commentMeRecord.getUser_name());
                    break;
                case "NewShareMessage":
                    old_cnt = MyApplication.getUnReadShareCnt();
                    MyApplication.setUnReadShareCnt(old_cnt++);
                    ShareMeRecord shareMeRecord = gson.fromJson(json_message, new TypeToken<ShareMeRecord>(){}.getType());
                    shareMeRecord.setStatus("unRead");
                    shareMeRecord.save();
                    savePortrait(shareMeRecord.getUser_id(), shareMeRecord.getUser_name());
                    break;
                case "NewMentionMeMessage":
                    old_cnt = MyApplication.getUnReadMentionCnt();
                    MyApplication.setUnReadMentionCnt(old_cnt++);
                    MentionMeRecord mentionMeRecord = gson.fromJson(json_message, new TypeToken<MentionMeRecord>(){}.getType());
                    mentionMeRecord.setStatus("unRead");
                    mentionMeRecord.save();
                    savePortrait(mentionMeRecord.getUser_id(), mentionMeRecord.getUser_name());
                    break;
                case "NewFollowFeedMessage":
                    MyApplication.setNewFollowFeed(true);
                    break;
                default:
                    break;
            }
        }
    }

    private void  savePortrait(int user_id, String user_name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Acquaintance> acquaintances = DataSupport.select("*").where("user_id = ?", String.valueOf(user_id)).find(Acquaintance.class);
                if(acquaintances.size() == 0){      // have no save
                    String urlString = new UserRequester().getPortraitUrl(user_id);
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);

                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = MyApplication.getContext().openFileOutput(user_id+"_portrait", Context.MODE_PRIVATE);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            is.close();
                            fos.close();
                            // storage in SQLite
                            Acquaintance acquaintance = new Acquaintance();
                            acquaintance.setUser_id(user_id);
                            acquaintance.setUser_name(user_name);
                            acquaintance.setUpdateTime(new Date(System.currentTimeMillis()));
                            acquaintance.save();
                        }
                        else
                            return;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                    return;
            }
        }).start();

    }
}
