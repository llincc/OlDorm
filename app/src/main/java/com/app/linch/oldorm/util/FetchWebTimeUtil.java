package com.app.linch.oldorm.util;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

/**
 * Created by linch on 2017/12/17.
 */

public class FetchWebTimeUtil {
    public static final String DEFAULT_URL = "http://www.baidu.com";
    public static long getWebTime(){
        return getWebTime(DEFAULT_URL);
    }
    public static long getWebTime(String webUrl){
        Log.d("WebTime", "正在获取网络时间");
        try{
            URL url = new URL(webUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            long webtime = connection.getDate();
            return webtime;
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return -1;
    }
}
