package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.track.Application.MyApplication;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

import static com.baidu.location.d.j.s;


/**
 * Created by thor on 2017/9/7.
 */

public class ChangePwdActivity extends AppCompatActivity {
    private EditText old_pwd_et;
    private EditText new_pwd_et;
    private EditText new_pwd_ensure_et;
    private Button send_btn;

    private static final int CHANGE_SUCCESS = 1;
    private static final int CHANGE_FAILED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(ChangePwdActivity.this.getResources().getColor(R.color.gray));
        toolbar.setTitle("修改密码");
        setSupportActionBar(toolbar);


        old_pwd_et = (EditText) findViewById(R.id.old_pwd);
        new_pwd_et = (EditText) findViewById(R.id.new_pwd);
        new_pwd_ensure_et = (EditText) findViewById(R.id.new_pwd_ensure);

        send_btn = (Button) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest(){
        String old_pwd = old_pwd_et.getText().toString();
        String new_pwd = new_pwd_et.getText().toString();
        String new_pwd_ensure = new_pwd_ensure_et.getText().toString();
        // check if input is valid
        if(old_pwd.equals("") || new_pwd.equals("") || new_pwd_ensure.equals("")){
            Toast.makeText(ChangePwdActivity.this, "请将表单填写完整", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!new_pwd.equals(new_pwd_ensure)){
            Toast.makeText(ChangePwdActivity.this, "两次输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        if(new_pwd.length()<5 || new_pwd.length()>15){
            Toast.makeText(ChangePwdActivity.this, "新密码长度应为5-15位，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        else
            changeJMessagePwd(old_pwd, new_pwd); // if JMessage pwd change successful, then send change request to server
    }

    private void sendChangeRequest(final String old_pwd, final String new_pwd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                String result = requester.changePwd(old_pwd, new_pwd);
                Message message = new Message();
                if(result.equals("failed"))
                    message.what = CHANGE_FAILED;
                else if (result.equals("success"))
                    message.what = CHANGE_SUCCESS;
                handler.sendMessage(message);

            }
        }).start();
    }

    private void changeJMessagePwd(String newPassword, String oldPassword){
        JMessageClient.	updateUserPassword(oldPassword, newPassword, new BasicCallback(){
            @Override
            public void gotResult(int i, String s) {
                if(i == 0){
                    Toast.makeText(MyApplication.getContext(), "JMessage密码修改成功", Toast.LENGTH_SHORT).show();
                    sendChangeRequest(oldPassword, newPassword);  // then send request to server
                }
                else {
                    Toast.makeText(MyApplication.getContext(), s + "\n请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHANGE_SUCCESS:
                    Toast.makeText(ChangePwdActivity.this, "密码修改成功，请重新登陆", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case CHANGE_FAILED:
                    Toast.makeText(ChangePwdActivity.this, "修改失败,请检查原密码和网络状态", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
