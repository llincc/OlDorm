package com.app.linch.oldorm.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linch on 2017/12/1.
 */

public class RoomData {
    @SerializedName("5")
    private int _b5;  //5号楼空床数
    @SerializedName("13")
    private int _b13; //13号楼空床数
    @SerializedName("14")
    private int _b14; //14号楼空床数
    @SerializedName("8")
    private int _b8;  //8号楼空床数
    @SerializedName("9")
    private int _b9;  //9号楼空床数

    public int get_b5() {
        return _b5;
    }

    public int get_b13() {
        return _b13;
    }

    public int get_b14() {
        return _b14;
    }

    public int get_b8() {
        return _b8;
    }

    public int get_b9() {
        return _b9;
    }

    public List<Integer> toList(){
        List<Integer> buildlist = new ArrayList<>();
        buildlist.add(get_b5());
        buildlist.add(get_b13());
        buildlist.add(get_b14());
        buildlist.add(get_b8());
        buildlist.add(get_b9());
        return buildlist;
    }
}
