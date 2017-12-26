package com.app.linch.oldorm.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.Stack;

/**
 * Created by linch on 2017/12/18.
 */

public class MyApplication extends Application{
    private  static  final String TAG = "MyAPP";
    private  static MyApplication myApplication;

    private static Stack<Activity> activityStack;  //Activity堆栈

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static MyApplication getInstance(){
        if(myApplication == null){
            myApplication = new MyApplication();
        }
        return myApplication;
    }

    //结束栈顶Activity
    public void popActivity(Activity activity){
        if(activity != null){
            Log.d(TAG, "销毁: "+ activity.getLocalClassName());
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity(){
        if(activityStack.empty())
            return null;
        Activity activity = activityStack.lastElement();
        return activity;
    }

    //将Activity堆入栈中
    public void pushActivity(Activity activity){
        if(activityStack == null){
            activityStack = new Stack<Activity>();
            Log.d(TAG, "创建Activity堆栈");
        }
        activityStack.add(activity);
    }

    //退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls){
        while(true){
            Activity activity=currentActivity();
            if(activity==null){
                break;
            }
            if(activity.getClass().equals(cls) ){
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 结束是销毁堆栈
     */
    @Override
    public void onTerminate() {
        if(activityStack != null){
            activityStack.removeAllElements();
            activityStack = null;
        }
        super.onTerminate();
    }
}
