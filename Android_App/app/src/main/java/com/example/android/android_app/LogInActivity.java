package com.example.android.android_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.button;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // set ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.logInToolBar);
        setSupportActionBar(toolbar);

        // set button listener
        Button button = (Button) findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(LogInActivity.this);
                progressDialog.setTitle("正在注册");
                progressDialog.setMessage("请稍后");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if(logIn() == "success"){
                    Intent intent = new Intent(LogInActivity.this,HomeActivity.class );
                    startActivity(intent);
                }

            }
        });
        Button signUp_btn = (Button) findViewById(R.id.signUp_btn);
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private String logIn(){
        String user_name = ((EditText) findViewById(R.id.user_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        // send log in information to server
        String url = "http://10.0.2.2/track/clientLogin?user_name="+user_name+"&password="+password ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try{
            Response response = client.newCall(request).execute();
            String result = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }

        // get result

        // if success, get token and store it
        return "success";
    }
}
