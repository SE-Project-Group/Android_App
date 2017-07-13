package com.example.android.android_app.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;

import com.example.android.android_app.Class.Feed;

import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PollingService extends Service {
    private Date last_update_time;

    public Date getLast_update_time() {
        return last_update_time;
    }

    public void setLast_update_time(Date last_update_time) {
        this.last_update_time = last_update_time;
    }

    public PollingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                onePolling();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int halfAnHour = 60 * 60 * 500; // milliseconds in one hour
        long triggerAtTime = SystemClock.elapsedRealtime() + halfAnHour; // execute a polling every half hour
        Intent i = new Intent(this, PollingService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public List<Feed> onePolling(){

        return null;
    }
}
