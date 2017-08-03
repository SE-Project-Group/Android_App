package com.example.android.track.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Util.UserRequester;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

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
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String json_message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Gson gson = new Gson();

            int old_cnt = MyApplication.getUnReadMsgCnt();
            MyApplication.setUnReadMsgCnt(old_cnt++);
            switch (title){
                case "NewLikeMessage":
                    old_cnt = MyApplication.getUnReadLikenCnt();
                    MyApplication.setUnReadLikenCnt(old_cnt++);
                    LikeMeRecord likeMeRecord = gson.fromJson(json_message, new TypeToken<LikeMeRecord>(){}.getType());
                    likeMeRecord.setStatus("unRead");
                    likeMeRecord.save();
                    savePortrait(likeMeRecord.getUser_id());
                    break;
                case "NewCommentMessage":
                    old_cnt = MyApplication.getUnReadCommentCnt();
                    MyApplication.setUnReadCommentCnt(old_cnt++);
                    CommentMeRecord commentMeRecord = gson.fromJson(json_message, new TypeToken<CommentMeRecord>(){}.getType());
                    commentMeRecord.setStatus("unRead");
                    commentMeRecord.save();
                    savePortrait(commentMeRecord.getUser_id());
                    break;
                case "NewShareMessage":
                    old_cnt = MyApplication.getUnReadShareCnt();
                    MyApplication.setUnReadShareCnt(old_cnt++);
                    ShareMeRecord shareMeRecord = gson.fromJson(json_message, new TypeToken<ShareMeRecord>(){}.getType());
                    shareMeRecord.setStatus("unRead");
                    shareMeRecord.save();
                    savePortrait(shareMeRecord.getUser_id());
                    break;
                case "NewMentionMeMessage":
                    old_cnt = MyApplication.getUnReadMentionCnt();
                    MyApplication.setUnReadMentionCnt(old_cnt++);
                    MentionMeRecord mentionMeRecord = gson.fromJson(json_message, new TypeToken<MentionMeRecord>(){}.getType());
                    mentionMeRecord.setStatus("unRead");
                    mentionMeRecord.save();
                    savePortrait(mentionMeRecord.getUser_id());
                    break;
                case "NewFollowFeedMessage":
                    MyApplication.setNewFollowFeed(true);
                    break;
                default:
                    break;
            }
        }
    }

    private void  savePortrait(int user_id){
        List<Acquaintance> acquaintances = DataSupport.select("*").where("user_id = ?", String.valueOf(user_id)).find(Acquaintance.class);
        if(acquaintances.size() == 0){      // have no save
            String urlString = new UserRequester().getPortraitUrl(user_id);
            byte[] result = null;
            try {
                URL url = new URL(urlString);
                DataInputStream dataInputStream = new DataInputStream(url.openStream());
                File temp = new File(user_id + "_portrait");
                FileOutputStream fileOutputStream = new FileOutputStream(temp);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = dataInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bufferedOutputStream.write(result);
                dataInputStream.close();
                fileOutputStream.close();
                bufferedOutputStream.close();
                temp.delete();

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else
            return;
    }
}
