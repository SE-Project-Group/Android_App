package com.example.android.android_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.R.attr.button;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
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

        // get result

        // if success, get token and store it
        return "success";
    }
}
