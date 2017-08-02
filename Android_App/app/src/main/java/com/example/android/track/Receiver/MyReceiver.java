package com.example.android.track.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.solver.Goal;
import android.util.Log;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Comment;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cn.jpush.android.api.JPushInterface;

import static com.baidu.location.d.j.S;

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
                    break;
                case "NewCommentMessage":
                    old_cnt = MyApplication.getUnReadCommentCnt();
                    MyApplication.setUnReadCommentCnt(old_cnt++);
                    CommentMeRecord commentMeRecord = gson.fromJson(json_message, new TypeToken<CommentMeRecord>(){}.getType());
                    commentMeRecord.setStatus("unRead");
                    commentMeRecord.save();
                    break;
                case "NewShareMessage":
                    old_cnt = MyApplication.getUnReadShareCnt();
                    MyApplication.setUnReadShareCnt(old_cnt++);
                    ShareMeRecord shareMeRecord = gson.fromJson(json_message, new TypeToken<ShareMeRecord>(){}.getType());
                    shareMeRecord.setStatus("unRead");
                    shareMeRecord.save();
                    break;
                case "NewMentionMeMessage":
                    old_cnt = MyApplication.getUnReadMentionCnt();
                    MyApplication.setUnReadMentionCnt(old_cnt++);
                    MentionMeRecord mentionMeRecord = gson.fromJson(json_message, new TypeToken<MentionMeRecord>(){}.getType());
                    mentionMeRecord.setStatus("unRead");
                    mentionMeRecord.save();
                case "NewFollowFeedMessage":
                    MyApplication.setNewFollowFeed(true);
                    break;
                default:
                    break;
            }
        }
    }
}
