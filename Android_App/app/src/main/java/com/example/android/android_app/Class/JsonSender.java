package com.example.android.android_app.Class;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by thor on 2017/7/4.
 */

public class JsonSender {
    private String jsonString;
    private String url;
    private int msg;
    private Handler handler;
    private Context context;

    public JsonSender(String jsonString, String url, int msg, Handler handler, Context context) {
        this.jsonString = jsonString;
        this.url = url;
        this.msg = msg;
        this.handler = handler;
        this.context = context;
    }

    public void send(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                threadWork();
            }
        }).run();
    }

    public void threadWork(){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // get response
        try{
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                //Log.i(TAG, "send_json: ok ");
                // send message
                Message message = new Message();
                message.what = msg;
                handler.sendMessage(message);
            }
            else{
                // Log.i(TAG, "send_json: error");
                Toast.makeText(context, "json send error", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
