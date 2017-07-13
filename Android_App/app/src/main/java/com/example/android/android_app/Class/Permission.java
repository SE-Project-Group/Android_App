package com.example.android.android_app.Class;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thor on 2017/7/6.
 */

public class Permission {
    private Activity activityContext;

    public Permission(Activity activityContext) {
        this.activityContext = activityContext;
    }

    // deal with request permission
    public void getPermissions(){
        List<String> permissionList = new ArrayList<>();
        // access fine location permission
        if(ContextCompat.checkSelfPermission(activityContext, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        // access read phone state permission
        if(ContextCompat.checkSelfPermission(activityContext, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        // access write external storage permission
        if(ContextCompat.checkSelfPermission(activityContext, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // check permission list
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activityContext, permissions, 1);
        }
    }





}
