package com.example.android.track.Util;

import android.content.Context;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.ClientInfo;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.UserInfo;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;

import static cn.jpush.im.android.api.enums.ContentType.file;


/**
 * Created by thor on 2017/8/7.
 */

public class AcquaintanceManager {
    public static void  saveAcquaintance(int user_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Acquaintance> acquaintances = DataSupport.select("*").where("user_id = ?", String.valueOf(user_id)).find(Acquaintance.class);
                int myId = Integer.valueOf(new Verify().getUser_id());  // user_id == myId means user is change portrait
                if(acquaintances.size() == 0 || user_id == myId){      // have no save
                    if(user_id == myId) {
                        File fileDir = MyApplication.getContext().getFilesDir();
                        File myPortrait = new File(fileDir, user_id + "_portrait");
                        if (myPortrait.exists()) {
                            myPortrait.delete();
                        }
                    }
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
                            // if it is change portrait, then remove old data first
                            if(user_id == myId){
                                acquaintances.get(0).delete();
                            }
                            Acquaintance acquaintance = new Acquaintance();
                            acquaintance.setUser_id(user_id);
                            UserInfo userInfo = new UserRequester().getHomeInfo(user_id);
                            if(userInfo != null)
                                acquaintance.setUser_name(userInfo.getName());
                            else
                                acquaintance.setUser_name("stranger");
                            acquaintance.setRelationship(userInfo.getRelationship());
                            acquaintance.setUpdateTime(new Date(System.currentTimeMillis()));
                            acquaintance.save();
                        }
                        else
                            return;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else   // if have already save this user, just return
                    return;
            }
        }).start();

    }

}
