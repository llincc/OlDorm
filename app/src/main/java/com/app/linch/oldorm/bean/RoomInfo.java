package com.app.linch.oldorm.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linch on 2017/11/29.
 */
public class RoomInfo {
    private int errcode;
    private RoomData data;

    public int getErrcode() {
        return errcode;
    }

    public RoomData getData() {
        return data;
    }
}
