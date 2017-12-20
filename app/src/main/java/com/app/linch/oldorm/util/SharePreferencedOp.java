package com.app.linch.oldorm.util;

import android.content.SharedPreferences;

import com.app.linch.oldorm.app.MyApplication;

/**
 * Created by linch on 2017/12/18.
 */

public class SharePreferencedOp {

    public static void clearConfig(String configname,int MODE) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(configname, MODE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    public static String getElement(String configname, int MODE, String elementname){
        return MyApplication.getInstance().getSharedPreferences(configname, MODE).getString(elementname, "");
    }
}
