package com.app.linch.oldorm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.linch.oldorm.R;

/**
 * Created by linch on 2017/11/28.
 */

public class LoginActivity extends Activity implements View.OnClickListener,TextWatcher{
    private Button loginButton;   //登录按钮
    private EditText usernameText,passwordText; //用户、密码 编辑栏
    private ImageView usernameCancel, passwordCancel; //清除图标
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login);
       // this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.login_title);

        loginButton = (Button)findViewById(R.id.loginbutton);
        usernameText = (EditText)findViewById(R.id.username);
        passwordText = (EditText)findViewById(R.id.password);
        usernameCancel = (ImageView)findViewById(R.id.usernamecancel);
        passwordCancel = (ImageView)findViewById(R.id.passwordcancel);

        loginButton.setOnClickListener(this);
        usernameCancel.setOnClickListener(this);
        passwordCancel.setOnClickListener(this);

        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = s.toString().trim();
                if("".equals(username)){
                    usernameCancel.setVisibility(View.INVISIBLE); //清除图标不可见
                }
                else{
                    usernameCancel.setVisibility(View.VISIBLE);   //清楚图标可见
                    //
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                if("".equals(password)){
                    passwordCancel.setVisibility(View.INVISIBLE);   //清除图标可见
                }
                else{
                    passwordCancel.setVisibility(View.VISIBLE);     //清除图标不可见
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbutton:
                //Login
                break;
            case R.id.usernamecancel:
                usernameText.setText("");  //清空输入
                break;
            case R.id.passwordcancel:
                passwordText.setText("");  //清空输入
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
