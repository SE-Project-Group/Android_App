package com.example.android.android_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.button;
import static android.R.id.message;
import static com.baidu.location.d.j.H;
import static com.baidu.location.d.j.v;

public class LogInActivity extends AppCompatActivity {
    private static final int LOG_IN_OK = 0;
    private static final int LOG_IN_FAILED = 1;
    private ProgressDialog progressDialog;
    private static final String uri = "http://192.168.1.200:8080/track/rest/app/clientLogin";

    private String token;
    private long user_id;


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
                progressDialog = new ProgressDialog(LogInActivity.this);
                progressDialog.setTitle("正在登录");
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

            }
        });
        // go to sign up page
        TextView signUp_text = (TextView) findViewById(R.id.signUp_text);

        signUp_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LogInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        /*Button signUp_btn = (Button) findViewById(R.id.signUp_btn);
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });*/

    }

    private void logInRequest(){
        String user_name = ((EditText) findViewById(R.id.user_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        // send log in information to server
        String url = uri + "?user_name="+user_name+"&password="+password ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        String result = "false";
        try{
            Response response = client.newCall(request).execute();
            result = response.body().string();
            //Toast.makeText(LogInActivity.this, result, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }


        // if failed , send message to main thread
        Message message = new Message();
        if(result.equals("false")){
            message.what = LOG_IN_FAILED;
            handler.sendMessage(message);
        }
        // if success, send message to main thread
        else{
            message = new Message();
            message.what = LOG_IN_OK;
            message.arg1 = 1;   // token
            handler.sendMessage(message);
        }

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String token = "";
            int user_id = 0;
            switch (msg.what){
                case LOG_IN_OK:
                    // if success, store token and swith to Home.
                    SharedPreferences.Editor editor = getSharedPreferences("login_data", MODE_PRIVATE).edit();
                    editor.putBoolean("loged",true);
                    editor.putString("token", token);
                    editor.putInt("user_id", user_id);
                    editor.apply();
                    // restart home activity, whose startup mode is singleTask
                    Intent intent = new Intent(LogInActivity.this,HomeActivity.class );
                    startActivity(intent);
                    break;
                case LOG_IN_FAILED:
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                    // clear inputed text on view
                    ((EditText) findViewById(R.id.user_name)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                    break;
            }
        }
    };


}
