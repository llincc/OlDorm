package com.app.linch.oldorm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.app.MyApplication;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.service.FetchDataService;

/**
 * Created by linch on 2017/12/4.
 */

public class ChooseResultActivity extends ActivityInterface implements View.OnClickListener{
    private static final int DIRECT_DELAY = 11;
    private ImageView result_image;
    private TextView  result_message;
    private Button result_button;
    private int result_code;
    private boolean getinfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        result_image = (ImageView)findViewById(R.id.result_img);
        result_message = (TextView)findViewById(R.id.result_text);
        result_button = (Button)findViewById(R.id.result_button);

        result_code = getResultCode();   //得到结果代码

        result_button.setOnClickListener(this);

        initView();


    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FetchDataService.PERNENNEL_INFO:
                    getinfo = true;
                    PersonnelInfo personnelInfo = (PersonnelInfo)msg.obj;
                    setPersoninfo(personnelInfo);
                    break;
                case DIRECT_DELAY: //5秒后跳转信号
                    if(!isFinishing()) directControl();
                    break;
                default:
                    break;
            }
    }};
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.result_button:
                directControl();
                break;
            default:
                break;
        }
    }

    /**
     * 根据结果初始化界面
     */
    private void initView(){
        if(result_code == 0){
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.succeed));
            getinfo = false;
            personinfoRequest(getSharedPreferences("info",MODE_PRIVATE).getString("stdid_value","")); //请求选的宿舍号
            result_message.setText(R.string.result_succeed_tip);

            result_button.setText(R.string.result_button_succeed);
            handler.sendEmptyMessageDelayed(DIRECT_DELAY, 5000);//5秒跳转
        }
        else{
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.failed));
            result_message.setText(R.string.result_failed_tip);

            result_button.setText(R.string.result_button_failed);
            handler.sendEmptyMessageDelayed(DIRECT_DELAY, 5000);//5秒跳转
        }
    }
    private int getResultCode(){
        return  this.getIntent().getIntExtra("result_code",1); // 0 选择成功, 非0失败
    }

    private void personinfoRequest(String studentid){
        //地址
        String address = String.format("https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid=%s",studentid);
        //发送个人信息请求
        new Thread(new FetchDataService(address, FetchDataService.PERNENNEL_INFO, FetchDataService.REQUEST_GET, this)).start();
    }
    private void setPersoninfo(PersonnelInfo personnelInfo){
        SharedPreferences.Editor editor = getSharedPreferences("info",MODE_PRIVATE).edit(); //保存个人信息到sharePreference
        editor.putString("room_value", personnelInfo.getData().getRoom() != null ? personnelInfo.getData().getRoom() :"xxx");           //房间号
        editor.putString("build_value", personnelInfo.getData().getBuilding() != null ? personnelInfo.getData().getBuilding() : "xxx");      //楼号
        editor.commit();
    }
    private void directSucess(){
        Intent intent = new Intent(this, InfoChoosed.class);     //跳转到基础信息页面 -- 已选宿舍
        startActivity(intent);
        finish(); //结束当前Activity
    }
    private void directFail(){
        Intent intent = new Intent(this, InfoUnchoosed.class);   //跳转到基础信息页面 -- 未选宿舍宿舍
        startActivity(intent);
        finish(); //结束当前Activity
    }
    private  void directControl(){
         if(result_code == 0 && getinfo){         //需要获得宿舍信息才能跳转
             directSucess();
         }
         else if(result_code == 0 && !getinfo){  //如果还没获得宿舍信息，则再次发送请求
             personinfoRequest(getSharedPreferences("info",MODE_PRIVATE).getString("stdid_value",""));
         }
         else if(result_code == 1 ){
             directFail();
         }
    }
    @Override
    protected void onDestroy() {
        MyApplication.getInstance().popAllActivityExceptOne(ChooseResultActivity.class); //销毁堆栈中其他activity
        super.onDestroy();
    }
    //禁止回退，防止出现重复选宿舍的情况
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        return;
        //super.onBackPressed();
    }
    @Override
    public Handler getHandler() {
        return handler;
    }
}
