package com.example.android.track.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.LitePal_Entity.CommentMeRecord;
import com.example.android.track.Model.LitePal_Entity.LikeMeRecord;
import com.example.android.track.Model.LitePal_Entity.MentionMeRecord;
import com.example.android.track.Model.LitePal_Entity.ShareMeRecord;
import com.example.android.track.Util.AcquaintanceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static com.baidu.location.d.j.g;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * Created by thor on 2017/7/21.
 */

public class MyReceiver extends BroadcastReceiver {

    public MyReceiver() {
        super();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

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
            if(!title.equals("NewFollowFeedMessage")) // except of new following feed , other message should be add to unReadMsgCnt
                MyApplication.setUnReadMsgCnt(old_cnt + 1);
            switch (title){
                case "NewLikeMessage":
                    old_cnt = MyApplication.getUnReadLikenCnt();
                    MyApplication.setUnReadLikenCnt(old_cnt + 1);
                    MyApplication.setNewMsg(true);
                    LikeMeRecord likeMeRecord = gson.fromJson(json_message, new TypeToken<LikeMeRecord>() {}.getType());
                    likeMeRecord.setStatus("unRead");
                    likeMeRecord.save();
                    AcquaintanceManager.saveAcquaintance(likeMeRecord.getUser_id());
                    break;
                case "NewCommentMessage":
                    old_cnt = MyApplication.getUnReadCommentCnt();
                    MyApplication.setUnReadCommentCnt(old_cnt + 1);
                    MyApplication.setNewMsg(true);
                    CommentMeRecord commentMeRecord = gson.fromJson(json_message, new TypeToken<CommentMeRecord>(){}.getType());
                    commentMeRecord.setStatus("unRead");
                    commentMeRecord.save();
                    AcquaintanceManager.saveAcquaintance(commentMeRecord.getUser_id());
                    break;
                case "NewShareMessage":
                    old_cnt = MyApplication.getUnReadShareCnt();
                    MyApplication.setUnReadShareCnt(old_cnt + 1);
                    MyApplication.setNewMsg(true);
                    ShareMeRecord shareMeRecord = gson.fromJson(json_message, new TypeToken<ShareMeRecord>(){}.getType());
                    shareMeRecord.setStatus("unRead");
                    shareMeRecord.save();
                    AcquaintanceManager.saveAcquaintance(shareMeRecord.getUser_id());
                    break;
                case "NewMentionMeMessage":
                    old_cnt = MyApplication.getUnReadMentionCnt();
                    MyApplication.setUnReadMentionCnt(old_cnt + 1);
                    MyApplication.setNewMsg(true);
                    MentionMeRecord mentionMeRecord = gson.fromJson(json_message, new TypeToken<MentionMeRecord>(){}.getType());
                    mentionMeRecord.setStatus("unRead");
                    mentionMeRecord.save();
                    AcquaintanceManager.saveAcquaintance(mentionMeRecord.getUser_id());
                    break;
                case "NewFollowFeedMessage":
                    MyApplication.setNewFollowFeed(true);
                    break;
                case "NewFollowerMessage":
                    old_cnt = MyApplication.getUnReadFollowMeCnt();
                    MyApplication.setUnReadFollowMeCnt(old_cnt + 1);
                    MyApplication.setNewMsg(true);
                    try {
                        JSONObject jsonObject = new JSONObject(json_message);
                        AcquaintanceManager.saveAcquaintance(jsonObject.getInt("user_id"));
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    }

    

}
