package com.example.android.track.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.track.Application.MyApplication;
import com.example.android.track.R;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;



public class LogInActivity extends AppCompatActivity {
    private static final int LOG_IN_OK = 0;
    private static final int LOG_IN_FAILED = 1;
    private ProgressDialog progressDialog;
    private EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // set ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.logInToolBar);
        toolbar.setTitleTextColor(LogInActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);

        // set button listener
        // log in button
        Button button = (Button) findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LogInActivity.this);
                progressDialog.setTitle("正在登录");
                progressDialog.setMessage("请稍后");
                progressDialog.setCancelable(false);
                progressDialog.show();
                // create a new Thread to log in
                String user_name = ((EditText) findViewById(R.id.user_name)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                logIn(user_name, password);

            }
        });
        // go to sign up page
        Button signUp_btn = (Button) findViewById(R.id.signUp_btn);

        signUp_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               signUp();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private void signUp(){
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        SMSSDK.setAskPermisionOnReadContact(true);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    // enter sign up activity
                    Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
            }
        };
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(eventHandler);
        registerPage.show(LogInActivity.this);
    }


    private void logIn(final String user_name, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = MyApplication.logIn(user_name, password);
                Message message = new Message();
                // if failed
                if(result.equals("ERROR"))
                    message.what = LOG_IN_FAILED;

                else
                    message.what = LOG_IN_OK;
                handler.sendMessage(message);
            }
        }).start();
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOG_IN_OK:
                    // restart home activity, whose startup mode is singleTask
                    Intent intent = new Intent(LogInActivity.this,HomeActivity.class );
                    startActivity(intent);
                    break;
                case LOG_IN_FAILED:
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                    // clear inputed text on view
                    ((EditText) findViewById(R.id.user_name)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                    break;
                default:
                    break;
            }
        }
    };


}
