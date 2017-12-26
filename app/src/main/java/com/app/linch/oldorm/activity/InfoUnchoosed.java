package com.app.linch.oldorm.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.app.MyApplication;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.service.FetchDataService;
import com.app.linch.oldorm.service.TimeAlarmerService;
import com.app.linch.oldorm.util.SharePreferencedOp;

/**
 * Created by linch on 2017/11/29.
 */

public class InfoUnchoosed extends ActivityInterface implements View.OnClickListener{
    private static final String TAG = "InfoUnchoosed";

    private Button startchoose;
    private TextView name_value, stdid_value, gender_value, verifyid_value;
    private ServiceConnection serviceConnection;
    private TimeAlarmerService timeAlarmerService;

    private ImageView menu;
    private PopupMenu popupMenu;
    private PopupWindow popWindow;

    private boolean mIsExit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_unchoosed);

        name_value = (TextView)findViewById(R.id.name_value);
        stdid_value = (TextView)findViewById(R.id.stdid_value);
        gender_value = (TextView)findViewById(R.id.gender_value);
        verifyid_value = (TextView)findViewById(R.id.verifyid_value);

        startchoose = (Button)findViewById(R.id.start_chooose_button);
        startchoose.setOnClickListener(this);

        menu = (ImageView)findViewById(R.id.info_menu);
        menu.setOnClickListener(this);

        initService();
        initView();  //初始化信息
        initPopupMenu();
        initPopupWindow();

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FetchDataService.PERNENNEL_INFO:
                    PersonnelInfo personnelInfo = (PersonnelInfo)msg.obj;

                    break;
                case TimeAlarmerService.START://可以选宿舍（在时间范围内）
                    Log.d(TAG, "业务开始");
                    startchoose.setBackgroundColor(getResources().getColor(R.color.volid));  //设置按钮颜色
                    startchoose.setEnabled(true);  //设置按钮为可点击
                    startchoose.setText(R.string.info_start_button_text);
                    break;
                case TimeAlarmerService.END:  //不可选宿舍（不在时间范围内）
                    Log.d(TAG, "业务结束");
                    startchoose.setBackgroundColor(getResources().getColor(R.color.involid));
                    startchoose.setEnabled(false); //设置为不可点击
                    startchoose.setText(R.string.info_end_button_text);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_chooose_button:
                Intent intent = new Intent(this, ChooseActivity.class);   //跳转选宿舍界面
                startActivity(intent);
                MyApplication.getInstance().pushActivity(this);
                //finishActivity(); // 结束当前Activity
                break;
            case R.id.info_menu:
                popupMenu.show();
                break;
            case R.id.exit_sure:
                popWindow.dismiss();
                finish();
                break;
            case R.id.exit_cancel:
                popWindow.dismiss();
                break;
            case R.id.item_exit:
                backgroundAlpha(0.5f);
                showPopWindow();
                break;
            default:
                break;
        }
    }

    private void initPopupWindow(){
        View contentView = LayoutInflater.from(InfoUnchoosed.this).inflate(R.layout.exit_window, null);
        popWindow  = new PopupWindow(contentView);
        popWindow.setContentView(contentView);
        popWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //popWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) R.drawable.map));
        TextView exit_sure = (TextView)contentView.findViewById(R.id.exit_sure);
        TextView exit_cancel = (TextView)contentView.findViewById(R.id.exit_cancel);

        exit_sure.setOnClickListener(this);
        exit_cancel.setOnClickListener(this);

        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

    }
    private void initPopupMenu(){

        popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.simple_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_change:
                        SharePreferencedOp.clearConfig("info", MODE_PRIVATE); //清空记录
                        Intent intent = new Intent(InfoUnchoosed.this, LoginActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "转到登录界面");
                        finish(); //结束Activity
                        break;
                    case R.id.item_exit:
                        backgroundAlpha(0.5f);
                        showPopWindow();
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
    }
    /**
     * 初始化服务
     */
    private void initService(){
        Log.d(TAG, "初始化服务");
        //初始化链接
        serviceConnection = new ServiceConnection() {
            /**
             * 与服务器端交互的接口方法 绑定服务的时候被回调，在这个方法获取绑定Service传递过来的IBinder对象，
             * 通过这个IBinder对象，实现宿主和Service的交互。
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG,"绑定计时服务");
                //获取绑定的Service实例
                TimeAlarmerService.LocalBinder binder = (TimeAlarmerService.LocalBinder)service;
                timeAlarmerService = binder.getService();
                timeAlarmerService.setContext(InfoUnchoosed.this);
                timeAlarmerService.setAlarmTime();
                //timeAlarmerService.setAlarmTime(starttime, endtime);
                timeAlarmerService.timeRequest(); // 开始计时任务
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                timeAlarmerService = null;
            }
        };
        Intent intent = new Intent(InfoUnchoosed.this, TimeAlarmerService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);  //绑定服务
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                finish();
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

    private void showPopWindow(){
        View rootView = LayoutInflater.from(InfoUnchoosed.this).inflate(R.layout.info_choosed, null);
        popWindow.showAtLocation(rootView, Gravity.CENTER,0,0);

    }
    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        MyApplication.getInstance().popAllActivityExceptOne(InfoUnchoosed.class); //销毁堆栈中其他activity
        super.onDestroy();
    }
    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    @Override
    public Handler getHandler() {
        return handler;
    }
}
