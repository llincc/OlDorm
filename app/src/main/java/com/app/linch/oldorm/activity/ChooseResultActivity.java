package com.app.linch.oldorm.activity;

import android.app.Activity;
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
import com.app.linch.oldorm.bean.ChooseResult;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.bean.RoomInfo;
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
                    directControl();
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
            result_message.setText("宿舍选择成功，5秒后自动跳转...");

            result_button.setText("查看信息");
            handler.sendEmptyMessageDelayed(DIRECT_DELAY, 5000);//5秒跳转
        }
        else{
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.failed));
            result_message.setText("宿舍选择失败，5秒后自动跳转...");

            result_button.setText("返回重选");
            handler.sendEmptyMessageDelayed(DIRECT_DELAY, 5000);//5秒跳转
        }
    }
    private int getResultCode(){
        return  this.getIntent().getIntExtra("result_code",1); // 0 选择成功
    }

    /**
     * 跳转控制
     */
//    private void directControl(){
//        if(result_code == 0){
//            SharedPreferences.Editor editor = getSharedPreferences("info",MODE_PRIVATE).edit(); //保存个人信息到sharePreference
//
//            editor.putString("name_value", personnelInfo.getData().getName());        //姓名
//            editor.putString("stdid_value", personnelInfo.getData().getStudentid());  //学号
//            editor.putString("gender_value", personnelInfo.getData().getGender());    //性别
//            editor.putString("verifyid_value",personnelInfo.getData().getVcode());    //验证码
//            editor.putString("location_value", personnelInfo.getData().getLocation());//位置
//            editor.putString("grade_value",personnelInfo.getData().getGrade());        //年级
//            if (personnelInfo.getData().getBuilding() == null || personnelInfo.getData().getRoom() == null){  //未选宿舍的
//                editor.commit();
//                Intent intent = new Intent(this, PersonInfoUnchoosed.class);   //跳转到基础信息页面 -- 未选宿舍
//                startActivity(intent);
//            }
//            else { //已选宿舍的
//                editor.putString("room_value", personnelInfo.getData().getRoom());           //房间号
//                editor.putString("build_value", personnelInfo.getData().getBuilding());  //楼号
//                editor.commit();
//                Intent intent = new Intent(this, PersonInfoChoosed.class);   //跳转到基础信息页面 -- 已选宿舍
//                startActivity(intent);
//            }
//        }
//        else{
//            //跳转为个人信息界面（未选择宿舍）
//            Intent intent = new Intent(this,  PersonInfoUnchoosed.class);
//            startActivity(intent);
//        }
//    }
    private void personinfoRequest(String studentid){
        //地址
        String address = String.format("https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid=%s",studentid);
        //发送个人信息请求
        new Thread(new FetchDataService(address, FetchDataService.PERNENNEL_INFO, FetchDataService.REQUEST_GET, this)).start();
    }
    private void setPersoninfo(PersonnelInfo personnelInfo){
        SharedPreferences.Editor editor = getSharedPreferences("info",MODE_PRIVATE).edit(); //保存个人信息到sharePreference
        editor.putString("room_value", personnelInfo.getData().getRoom() != null ? personnelInfo.getData().getRoom() :"");           //房间号
        editor.putString("build_value", personnelInfo.getData().getBuilding() != null ? personnelInfo.getData().getBuilding() : "");      //楼号
        editor.commit();
    }
    private void directSucess(){
        Intent intent = new Intent(this, PersonInfoChoosed.class);   //跳转到基础信息页面 -- 已选宿舍
        startActivity(intent);
    }
    private void directFail(){
        Intent intent = new Intent(this, PersonInfoUnchoosed.class);   //跳转到基础信息页面 -- 已选宿舍
        startActivity(intent);
        finish();
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
