package com.example.android.track.Util;

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

    public String post(){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        String result = "";

        // get response
        try{
            Response response = okHttpClient.newCall(request).execute();
            String data = response.body().string();
            if(response.isSuccessful())
                result = data;
            else
                result = "failed";
        }catch (IOException e){
            e.printStackTrace();
            result = "failed";
        }
        return result;

    }


    public String delete(){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
        String result = "";

        // get response
        try{
            Response response = okHttpClient.newCall(request).execute();
            String data = response.body().string();
            if(response.isSuccessful())
                result = data;
            else
                result = "failed";
        }catch (IOException e){
            e.printStackTrace();
            result = "failed";
        }
        return result;

    }


    public String put(){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody =RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        String result = "";

        // get response
        try{
            Response response = okHttpClient.newCall(request).execute();
            String data = response.body().string();
            if(response.isSuccessful())
                result = data;
            else
                result = "failed";
        }catch (IOException e){
            e.printStackTrace();
            result = "failed";
        }
        return result;
    }

}
