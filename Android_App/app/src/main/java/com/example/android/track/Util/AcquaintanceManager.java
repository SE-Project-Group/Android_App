package com.example.android.track.Util;

import android.content.Context;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.ClientInfo;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.UserInfo;

import org.litepal.crud.DataSupport;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;


/**
 * Created by thor on 2017/8/7.
 */

public class AcquaintanceManager {
    public static void  saveAcquaintance(int user_id){
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
                else
                    return;
            }
        }).start();

    }
}
