package com.app.linch.oldorm.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.linch.oldorm.activity.ActivityInterface;
import com.app.linch.oldorm.util.FetchWebTimeUtil;
import com.app.linch.oldorm.util.NetUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by linch on 2017/12/17.
 */

public class TimeAlarmerService extends Service{
    public static final int START = 21;
    public static final int END = 22;
    private static final String TAG = "TimeAlamer";
    private static final String START_TIME = "2017-12-17 15:44:00";
    private static final String END_TIME = "2018-12-17 15:48:00";

    private boolean isGetWebTime = false;
    private boolean isSetAlarmTime = false;
    private long currentTime;
    private long startTime;
    private long endTime;
    private ActivityInterface context;

    private LocalBinder binder;
    private Timer timer;    //计时任务线程
    private Timer getTimer; //获取网络时间任务，同时负责启动计时任务


    public class LocalBinder extends Binder{
        public TimeAlarmerService getService(){
            return TimeAlarmerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binder = new LocalBinder();
        Log.d(TAG, "已绑定服务");
        return binder;
    }
    private void startTimeSchedule(){
        if(isGetWebTime && isSetAlarmTime){  //只有当前事件，开始时间， 结束之间都获取了，才可以开始计时
            if(timer == null)  //创建线程
                timer = new Timer();
            Log.d(TAG, "currentTime: "+ currentTime);
            Log.d(TAG, "startTime: "+ startTime);
            Log.d(TAG, "endTime: "+ endTime);
            startTimeAlarmer(this.currentTime, this.startTime, this.endTime); //启动业务开放计时任务
            endTimeAlarmer(this.currentTime,this.startTime,this.endTime);     //启动业务关闭计时任务
            getTimer.cancel();//取消计时任务
            getTimer = null;
        }
    }
    public void timeRequest(){
        if(getTimer == null) //创建线程
            getTimer = new Timer();
        getTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(NetUtil.isConnectNet(TimeAlarmerService.this)) { //检测网络
                    Log.d(TAG, "正在获取网络时间");
                    currentTime = FetchWebTimeUtil.getWebTime();   //获取网络时间
                    if (currentTime != -1) {
                        isGetWebTime = true; //获取网络时间，开始计时
                        startTimeSchedule();    //开始业务计时
                    }
                }
            }
        },0,500); //每0.5秒执行时间获取
    }

    private void startTimeAlarmer(long currentTime, long startTime, long endTime){
        if(currentTime < startTime){
            //开始计时任务
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "服务开始");
                    sendMessage(START);
                }
            }, startTime-currentTime);
        }
        else if(currentTime >= startTime && currentTime <endTime){
            //不计时，直接发送开始消息
            sendMessage(START);
        }
    }

    private void endTimeAlarmer(long currentTime, long startTime, long endTime){
        if(currentTime < endTime){
            //开始计时任务
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "服务结束");
                    sendMessage(END);
                }
            },endTime - currentTime);   //在endTime - currentTime之后结束
        }
        else{
            //不计时，直接发送结束消息
            sendMessage(END);
        }
    }
    private void sendMessage(int FLAG){
        Message message = new Message();
        switch (FLAG){
            case START:
                Log.d(TAG, "发送开始消息");
                message.what = START;
                context.getHandler().sendMessage(message);
                break;
            case END:
                Log.d(TAG, "发送结束消息");
                message.what = END;
                context.getHandler().sendMessage(message);
                break;
            default:
                break;
        }
    }
    private long getTimeStamp(String format_date){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(format_date);
            return date.getTime();
        }
        catch (ParseException e){
            return -1;
        }
    }
    public void setAlarmTime(){
        setAlarmTime(START_TIME, END_TIME);
    }
    public void setAlarmTime(String startTime, String endTime){
        this.startTime = getTimeStamp(startTime);
        this.endTime = getTimeStamp(endTime);
        if(this.startTime == -1 || this.endTime==-1 || this.endTime < this.startTime){
            Log.d(TAG, "时间设置错误");
            this.isSetAlarmTime = false;
        }
        else{
            this.isSetAlarmTime = true;
        }
    }
    public void setContext(ActivityInterface context) {
        this.context = context;
    }


}
