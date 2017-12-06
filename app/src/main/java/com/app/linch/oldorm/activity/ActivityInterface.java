package com.app.linch.oldorm.activity;

import android.app.Activity;
import android.os.Handler;

/**
 * Created by linch on 2017/12/4.
 */

public abstract class  ActivityInterface extends Activity{
    public abstract  Handler getHandler(); //获取Handler，便于统一发送信息
}
