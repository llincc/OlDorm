package com.app.linch.oldorm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.service.FetchDataService;

import org.w3c.dom.Text;

/**
 * Created by linch on 2017/11/29.
 */

public class PersonInfoUnchoosed extends Activity implements View.OnClickListener{
    private Button startchoose;
    private TextView name_value, stdid_value, gender_value, verifyid_value;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_unchoosed);

        name_value = (TextView)findViewById(R.id.name_value);
        stdid_value = (TextView)findViewById(R.id.stdid_value);
        gender_value = (TextView)findViewById(R.id.gender_value);
        verifyid_value = (TextView)findViewById(R.id.verifyid_value);

        startchoose = (Button)findViewById(R.id.startchooosebutton);

        initView();  //初始化信息

        startchoose.setOnClickListener(this);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FetchDataService.PERNENNEL_INFO:
                    PersonnelInfo personnelInfo = (PersonnelInfo)msg.obj;

                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startchooosebutton:
                Intent intent = new Intent(this, ChooseActivity.class);   //跳转选宿舍界面
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void initView(){
        String name = getSharedPreferences("info",MODE_PRIVATE).getString("name_value","无");
        String stdid = getSharedPreferences("info",MODE_PRIVATE).getString("stdid_value","无");
        String gender = getSharedPreferences("info",MODE_PRIVATE).getString("gender_value","无");
        String verifyid = getSharedPreferences("info",MODE_PRIVATE).getString("verifyid_value","无");

        name_value.setText(name);
        stdid_value.setText(stdid);
        gender_value.setText(gender);
        verifyid_value.setText(verifyid);
    }
}
