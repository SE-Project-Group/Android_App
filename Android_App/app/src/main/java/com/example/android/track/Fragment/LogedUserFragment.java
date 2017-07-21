package com.example.android.track.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.track.Activity.HomeActivity;
import com.example.android.track.Activity.MyAlbumActivity;
import com.example.android.track.Activity.MyLikeActivity;
import com.example.android.track.Activity.MyShareActivity;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Activity.SettingActivity;
import com.example.android.track.Activity.UserInfoActivity;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;


/**
 * Created by thor on 2017/6/29.
 */

public class LogedUserFragment extends Fragment{
    private static final int LOG_OU_OK = 0;
    private static final int LOG_OUT_FAILED = 1;




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

        setClickListener();
    }


    private void setClickListener(){
        // personal home button
        LinearLayout personal_home_btn = (LinearLayout) getActivity().findViewById(R.id.personal_home_btn);
        personal_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalHomeActivity.class);
                Verify verify = new Verify();
                int user_id = Integer.valueOf(verify.getUser_id());
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });


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



        LinearLayout my_like = (LinearLayout) getActivity().findViewById(R.id.my_like_btn);
        my_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),MyLikeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout my_share = (LinearLayout) getActivity().findViewById(R.id.my_share_btn);
        my_share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),MyShareActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout logout_btn = (LinearLayout)getActivity().findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserRequester requester = new UserRequester();
                        String result = requester.logOut();
                        Message message = new Message();
                        if(result.equals("success"))
                            message.what = LOG_OU_OK;
                        else
                            message.what = LOG_OUT_FAILED;
                        handler.sendMessage(message);
                    }
                }).start();

            }
        });

        LinearLayout edit_info_btn = (LinearLayout) getActivity().findViewById(R.id.edit_info_btn);
        edit_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOG_OU_OK:
                    delete_token();
                    break;
                case LOG_OUT_FAILED:
                    Toast.makeText(getContext(), "退出登录失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void delete_token(){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("logIn_data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("loged", false);
        editor.putString("token", "");
        editor.putInt("user_id", 0);
        editor.apply();
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }


}
