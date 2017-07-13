package com.example.android.android_app.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.android.android_app.Activity.FansActivity;
import com.example.android.android_app.Activity.FollowingActivity;
import com.example.android.android_app.Activity.HomeActivity;
import com.example.android.android_app.Activity.MyAlbumActivity;
import com.example.android.android_app.Activity.PersonalHomeActivity;
import com.example.android.android_app.Activity.PersonalPageActivity;
import com.example.android.android_app.Activity.PersonalSettingActivity;
import com.example.android.android_app.Activity.SettingActivity;
import com.example.android.android_app.R;



/**
 * Created by thor on 2017/6/29.
 */

public class LogedUserFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_loged_user, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //set tool bar
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.logedUserToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // setting button
        LinearLayout setting_btn = (LinearLayout) getActivity().findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        // myAlbum button
        LinearLayout myAlbum_btn = (LinearLayout) getActivity().findViewById(R.id.myAlbum_btn);
        myAlbum_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyAlbumActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout myFollowing = (LinearLayout) getActivity().findViewById(R.id.my_following_layout);
        myFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout myFollower = (LinearLayout) getActivity().findViewById(R.id.my_follower_layout);
        myFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FansActivity.class);
                startActivity(intent);
            }
        });

        ImageButton personal_setting = (ImageButton) getActivity().findViewById(R.id.personal_setting);
        personal_setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),PersonalSettingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout all_personal_feeds = (LinearLayout) getActivity().findViewById(R.id.my_feed_layout);
        all_personal_feeds.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),PersonalHomeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout logout_btn = (LinearLayout)getActivity().findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("logIn_data", Context.MODE_PRIVATE).edit();
                editor.putBoolean("loged",false);
                editor.putString("token", "");
                editor.putInt("user_id",0);
                editor.apply();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });

    }


}
