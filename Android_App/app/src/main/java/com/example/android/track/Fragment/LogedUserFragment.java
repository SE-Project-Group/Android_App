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

import com.example.android.track.Activity.ChangePwdActivity;
import com.example.android.track.Activity.HomeActivity;
import com.example.android.track.Activity.LogInActivity;
import com.example.android.track.Activity.MyAlbumActivity;
import com.example.android.track.Activity.MyCommentActivity;
import com.example.android.track.Activity.MyLikeActivity;
import com.example.android.track.Activity.MyShareActivity;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.Activity.SettingActivity;
import com.example.android.track.Activity.SignUpActivity;
import com.example.android.track.Activity.UserInfoActivity;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.Util.Verify;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.EventHandler;
import cn.smssdk.gui.RegisterPage;

import static android.R.id.edit;
import static android.app.Activity.RESULT_OK;
import static com.baidu.location.d.j.C;
import static com.example.android.track.Application.MyApplication.logOut;
import static com.example.android.track.R.id.setting_btn;


/**
 * Created by thor on 2017/6/29.
 */

public class LogedUserFragment extends Fragment{
    private static final int LOG_OUT_OK = 0;
    private static final int LOG_OUT_FAILED = 1;
    private static final int PHONE_CORRECT = 2;
    private static final int PHONE_WRONG = 3;
    private static final int VERIFY_PHONE_FAILED = 4;
    private static final int CHANGE_PWD = 5;




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
        LinearLayout edit_pwd_btn = (LinearLayout) getActivity().findViewById(R.id.edit_pwd_btn);
        edit_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPwd();
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

        LinearLayout my_comment = (LinearLayout) getActivity().findViewById(R.id.my_comment_btn);
        my_share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),MyCommentActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout logout_btn = (LinearLayout)getActivity().findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                logOut();
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

    private void logOut(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = MyApplication.logOut();
                Message message = new Message();
                if(result.equals("success"))
                    message.what = LOG_OUT_OK;
                else
                    message.what = LOG_OUT_FAILED;
                handler.sendMessage(message);
            }
        }).start();
    }


    private void editPwd(){
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    // check if phone number correct
                    verifyPhone(phone);
                }
            }
        };
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(eventHandler);
        registerPage.show(MyApplication.getContext());
    }

    private void verifyPhone(String phone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                String result = requester.verifyPhone(phone);
                Message message = new Message();
                if(result.equals("failed"))
                    message.what = VERIFY_PHONE_FAILED;
                else if (result.equals("true"))
                    message.what = PHONE_CORRECT;
                else if (result.equals("false"))
                    message.what = PHONE_WRONG;

                handler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHANGE_PWD:
                if(resultCode == RESULT_OK){
                    logOut();
                }
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOG_OUT_OK:
                    // if log out success, then delete the token store in XML file
                    new Verify().delete_token();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    break;
                case LOG_OUT_FAILED:
                    Toast.makeText(getContext(), "退出登录失败", Toast.LENGTH_SHORT).show();
                    break;
                case PHONE_CORRECT:
                    Toast.makeText(getContext(), "验证成功", Toast.LENGTH_SHORT).show();
                    Intent changePwdIntent = new Intent(getActivity(), ChangePwdActivity.class);
                    startActivityForResult(changePwdIntent, CHANGE_PWD);
                    break;
                case PHONE_WRONG:
                    Toast.makeText(getContext(), "手机号与当前账号不匹配，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case VERIFY_PHONE_FAILED:
                    Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };




}
