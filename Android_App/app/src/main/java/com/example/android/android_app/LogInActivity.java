package com.example.android.android_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.button;
import static com.baidu.location.d.j.H;
import static com.baidu.location.d.j.v;

public class LogInActivity extends AppCompatActivity {
    private static final int LOG_IN_OK = 0;
    private static final int LOG_IN_FAILED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // set ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.logInToolBar);
        setSupportActionBar(toolbar);

        // set button listener
        // log in button
        Button button = (Button) findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(LogInActivity.this);
                progressDialog.setTitle("正在注册");
                progressDialog.setMessage("请稍后");
                progressDialog.setCancelable(false);
                progressDialog.show();
                // create a new Thread to log in
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logInRequest();
                    }
                }).start();

                //if(logIn() == "success"){
                //    Intent intent = new Intent(LogInActivity.this,HomeActivity.class );
                //    startActivity(intent);
                //}

            }
        });
        // go to sign up page
        Button signUp_btn = (Button) findViewById(R.id.signUp_btn);
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void logInRequest(){
        String user_name = ((EditText) findViewById(R.id.user_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        // send log in information to server
        String url = "http://1507c590.all123.net:8080/track/rest/app/clientLogin?user_name=565&password=44";
        //String url = "http://10.0.2.2/track/clientLogin?user_name="+user_name+"&password="+password ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try{
            Response response = client.newCall(request).execute();
            String result = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }

        // get result////////////////////////////need write

        // if failed , send message to main thread
        Message message = new Message();
        message.what = LOG_IN_FAILED;
        handler.sendMessage(message);

        // if success, send message to main thread
        message = new Message();
        message.what = LOG_IN_OK;
        message.arg1 = 1;   // token
        handler.sendMessage(message);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOG_IN_OK:
                    // if success, store token and swith to Home.


                    Intent intent = new Intent(LogInActivity.this,HomeActivity.class );
                    startActivity(intent);
                    break;
                case LOG_IN_FAILED:
                    Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                    // clear inputed text on view
                    ((EditText) findViewById(R.id.user_name)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                    break;
            }
        }
    };
}
