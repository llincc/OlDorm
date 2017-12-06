package com.app.linch.oldorm.util;

import com.app.linch.oldorm.bean.ChooseResult;
import com.app.linch.oldorm.bean.LoginResponse;
import com.app.linch.oldorm.bean.PersonnelInfo;
import com.app.linch.oldorm.bean.RoomInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by linch on 2017/11/29.
 */

public class JSONparser {
    private static Gson gson = new Gson();
    public static RoomInfo parseRoomData(String jsondata){
        return gson.fromJson(jsondata,RoomInfo.class);
    }
    public static PersonnelInfo parsePersonnelInfo(String jsondata){
        return gson.fromJson(jsondata,PersonnelInfo.class);
    }
    public static LoginResponse parseLoginResponse(String jsondata){
        return gson.fromJson(jsondata,LoginResponse.class);
    }
    public static ChooseResult parseChooseResponse(String jsondata){
        //Type mapType = new TypeToken<Map<String,String>>(){}.getType();
        // Map map = gson.fromJson(jsondata , mapType);
        //if (map.get("error_code")!=null)   return Integer.parseInt((String)map.get("error_code"));
       // else return -1;
        return gson.fromJson(jsondata, ChooseResult.class);
    }
}
