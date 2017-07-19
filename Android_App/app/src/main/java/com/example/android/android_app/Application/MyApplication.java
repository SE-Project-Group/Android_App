package com.example.android.android_app.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by thor on 2017/7/19.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
