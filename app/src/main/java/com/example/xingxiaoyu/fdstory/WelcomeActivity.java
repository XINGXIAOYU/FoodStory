package com.example.xingxiaoyu.fdstory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.xingxiaoyu.fdstory.login.LoginActivity;
import com.example.xingxiaoyu.fdstory.register.RegisterActivity;


/**
 * Created by xingxiaoyu on 17/4/16.
 */

public class WelcomeActivity extends AppCompatActivity {
    // UI references.
    private Button wel_loginButton;
    private Button wel_registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        wel_loginButton = (Button) findViewById(R.id.welcome_login);
        wel_registerButton = (Button) findViewById(R.id.welcome_register);
        wel_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                WelcomeActivity.this.startActivity(intent);
                finish();
            }
        });
        wel_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,RegisterActivity.class);
                WelcomeActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}
