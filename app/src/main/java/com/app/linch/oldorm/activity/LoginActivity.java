package com.app.linch.oldorm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.bean.LoginResponse;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.service.FetchDataService;
import com.app.linch.oldorm.util.NetUtil;

/**
 * Created by linch on 2017/11/28.
 */

public class LoginActivity extends ActivityInterface implements View.OnClickListener{
    private static final  String TAG = "LOGIN";

    private Button loginButton;   //登录按钮
    private EditText usernameText,passwordText; //用户、密码 编辑栏
    private ImageView usernameCancel, passwordCancel; //清除图标
    private static int timer = 0;

    private String username, password;

    private boolean mIsExit; //退出程序标识
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FetchDataService.LOGIN_RESPONSE:
                    loginButton.setClickable(true);
                    LoginResponse loginResponse = (LoginResponse) msg.obj;
                    if(loginResponse.getErrcode() == 0) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT);
                        loginSucceed();
                    }
                    else {
                        loginFail();
                    }
                    break;
                case FetchDataService.PERNENNEL_INFO:
                    PersonnelInfo personnelInfo = (PersonnelInfo)msg.obj;
                    if(personnelInfo.getErrcode() != 0){
                        Toast.makeText(LoginActivity.this, "获取个人信息错误", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        directControl(personnelInfo);  //跳转控制
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbutton:
                if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE ){
                    if(loginCheck()){
                        loginButton.setClickable(false); //将不能被再次出发
                        loginRequest(); //发送登录请求
                    }
                    else{
                        Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this,"网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.usernamecancel:
                usernameText.setText("");  //清空输入
                break;
            case R.id.passwordcancel:
                passwordText.setText("");  //清空输入
                break;
        }
    }

    /**
     * 输入简单格式检查
     * @return
     */
    private boolean loginCheck(){
        return (!"".equals(usernameText.getText().toString().trim()))&&(!"".equals(passwordText.getText().toString().trim()));
    }

    /**
     * 发送登录请求
     */
    private void loginRequest(){
        username = usernameText.getText().toString().trim(); //用户名赋值。 防止请求过程中修改用户名
        password = passwordText.getText().toString().trim(); //
        final String  requsturl = "https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="+username+"&password="+password;
        new Thread(new FetchDataService(requsturl,FetchDataService.LOGIN_RESPONSE, FetchDataService.REQUEST_GET, this)).start();
    }

    /**
     * 登录成功，发送个人信息请求
     */
    private void loginSucceed(){
        //地址
        String address = String.format("https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid=%s",username);
        //发送个人信息请求
        new Thread(new FetchDataService(address, FetchDataService.PERNENNEL_INFO, FetchDataService.REQUEST_GET, this)).start();
    }
    private void loginFail(){
        Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
    }
    private void directControl(PersonnelInfo personnelInfo){
        SharedPreferences.Editor editor = getSharedPreferences("info",MODE_PRIVATE).edit(); //保存个人信息到sharePreference
        String studentid = personnelInfo.getData().getStudentid();
        editor.putString("name_value", personnelInfo.getData().getName());        //姓名
        editor.putString("stdid_value", personnelInfo.getData().getStudentid());  //学号
        editor.putString("gender_value", personnelInfo.getData().getGender());    //性别
        editor.putString("verifyid_value",personnelInfo.getData().getVcode());    //验证码
        editor.putString("location_value", personnelInfo.getData().getLocation());//位置
        editor.putString("grade_value",personnelInfo.getData().getGrade());        //年级
        if (personnelInfo.getData().getBuilding() == null || personnelInfo.getData().getRoom() == null){  //未选宿舍的
            editor.putString("room_value", "");       //房间号    覆盖可能存在的内容（防止多账户登录导致的混乱）
            editor.putString("build_value", "");      //楼号
            editor.commit();
            Intent intent = new Intent(this, InfoUnchoosed.class);   //跳转到基础信息页面 -- 未选宿舍
            startActivity(intent);
        }
        else { //已选宿舍的
            editor.putString("room_value", personnelInfo.getData().getRoom());       //房间号
            editor.putString("build_value", personnelInfo.getData().getBuilding());  //楼号
            editor.commit();
            Intent intent = new Intent(this, InfoChoosed.class);   //跳转到基础信息页面 -- 已选宿舍
            startActivity(intent);
        }
        Log.d(TAG, "跳转到个人信息界面");
        finish();

    }

    /**
     * 点击两次退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Handler getHandler() {
        return handler;
    }
}
