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


    public JsonSender(String jsonString, String url) {
        this.jsonString = jsonString;
        this.url = url;

    }

    public String send(){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // get response
        try{
            Response response = okHttpClient.newCall(request).execute();
            String data = response.body().string();
            if(response.isSuccessful())
                return data;
            else
                return "failed";
        }catch (IOException e){
            e.printStackTrace();
            return "failed";
        }
    }
}
