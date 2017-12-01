package com.app.linch.oldorm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.adapter.ChooseAdpter;
import com.app.linch.oldorm.bean.LoginResponse;
import com.app.linch.oldorm.bean.RoomInfo;
import com.app.linch.oldorm.service.FetchDataService;
import com.app.linch.oldorm.util.NetUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linch on 2017/11/30.
 */

public class ChooseActivity extends Activity implements View.OnClickListener,View.OnTouchListener{
    private ExpandableListView mainlistview = null;
    private List<String> parents = null;
    private Map<String, List<String>> childs= null;
    private ChooseAdpter listAdapter;
    private LinearLayout otherInfoLayout;
    private Button submit;

    private int choosenum;
    private List<String> stdidList;
    private List<String> verifyidList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

        submit = (Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(this);

        choosenum = 0; // 选宿舍人数初始为0
        otherInfoLayout = (LinearLayout)findViewById(R.id.otherInfo_container); //其他信息布局
       // otherInfoLayout.child
        mainlistview = (ExpandableListView) findViewById(R.id.numberChosse); //可展开列表
        initData(); //初始化列表参数
        listAdapter = new ChooseAdpter(parents,childs,this);
        mainlistview.setAdapter(listAdapter);
        //列表展开监控
        mainlistview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition == 1){
                    //发送人数更新请求
                    buildingRequest();
                    //更新人数
                }
                //展开这个列表时，其他列表收缩
                for (int i = 0; i < parents.size(); i++) {
                    if (groupPosition != i) {
                        mainlistview.collapseGroup(i);    //收缩第i号列表
                    }
                }
            }
        });
        //列表子项点击控制
        mainlistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                listAdapter.changeParentName(groupPosition,childPosition); //改变父组件名称
                mainlistview.collapseGroup(groupPosition);    //点击完收缩列表
                if(groupPosition == 0){ //如果选择的是人数
                     //addItem(); //添加一个控件
                    itemControl(childPosition);
                    choosenum = childPosition + 1; // 选宿舍人数 = childPosition + 1
                }
                return true;
            }
        });
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Toast.makeText(this,"网络OK",Toast.LENGTH_SHORT).show();
            buildingRequest();
        }
        else{
            Toast.makeText(this,"没有网络",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 消息控制
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FetchDataService.ROOM_DATA:
                    RoomInfo roomInfo = (RoomInfo)msg.obj;
                    //ystem.out.println("Roomdata"+roomInfo.getData().get_b5());
                    listAdapter.updateBuildNumber(roomInfo.getData().toList()); //更新楼的空床数
                    break;
                default:
                    break;
            }
    }};
    @Override
    public void onClick(View v) {
        for(int groupPosition=0 ; groupPosition<2; groupPosition++){
            mainlistview.collapseGroup(groupPosition);
        }
        switch (v.getId()){
            case R.id.submit_button:
                submitRequest();
                break;
            default:
                break;

        }
    }

    /**
     * 触摸事件监听
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //一触摸输出框 所有列表收缩
        for(int groupPosition=0 ; groupPosition<2; groupPosition++){
            mainlistview.collapseGroup(groupPosition);
        }
        return false;
    }

    // 初始化数据
    private void initData() {
        parents = new ArrayList<String>();
        parents.add("人数选择");
        parents.add("选择宿舍");

        childs = new HashMap<String, List<String>>();

        List<String> list1 = new ArrayList<String>();
        list1.add("1人");
        list1.add("2人");
        list1.add("3人");
        list1.add("4人");
        childs.put("人数选择", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("5号楼");
        list2.add("13号楼");
        list2.add("14号楼");
        list2.add("8号楼");
        list2.add("9号楼");
        childs.put("选择宿舍", list2);


    }
    private void itemControl(int childNum){
        int childCount = otherInfoLayout.getChildCount();
        System.out.println("childCount"+childCount+"  childNum"+childNum);
        if(childCount >childNum){
            //remove
            removeItem(childNum,childCount);
        }
        else{
            addItem(childNum, childCount);
        }
    }
    private void addItem(int childNum, int childCount) {
        for (int i = childCount; i < childNum; i++){ // 添加childNum - childCount个子项
            //为其他人信息添加一项
            View child = LayoutInflater.from(this).inflate(R.layout.choose_add_person_item, otherInfoLayout, false);
            ((EditText)child.findViewById(R.id.other_stdid)).setOnTouchListener(this);     //设置触摸事件
            ((EditText)child.findViewById(R.id.other_verifyid)).setOnTouchListener(this); //设置触摸事件
            child.setOnClickListener(this);  //设置点击事件
            otherInfoLayout.addView(child);
        }
    }
    private void removeItem(int childNum, int childCount){
        for(int i=childCount-1; i > childNum-1; i--){ // 减少childNum - childCount个子项
            otherInfoLayout.removeViewAt(i);
        }
    }
    private boolean itemSetStudentID(){
        boolean flag = true;

        stdidList = new ArrayList<>();
        verifyidList = new ArrayList<>();

        View child;
        String stdid;
        String verifyid;
        int childCount = otherInfoLayout.getChildCount();
        for(int i=0; i < childCount; i++){
            child = otherInfoLayout.getChildAt(i);
            stdid = ((EditText)child.findViewById(R.id.other_stdid)).getText().toString().trim();
            verifyid = ((EditText)child.findViewById(R.id.other_verifyid)).getText().toString().trim();
            if("".equals(stdid)||"".equals(verifyid)){
                Toast.makeText(this, "学号和验证码不能为空", Toast.LENGTH_SHORT);
                return false;  //返回错误
            }
            else{
                stdidList.add(stdid);
                verifyidList.add(verifyid);
            }
        }
        return true; //返回成功
    }

    /**
     * 生成选宿舍请求Url
     * @return
     */
    private String generateAddress(){
        if(choosenum == 0 || stdidList==null || verifyidList==null ){
             return "";
        }
        String address = "https://api.mysspku.com/index.php/V1/MobileCourse/SelectRoom?";
        address += "&"+ String.valueOf(choosenum);
        address += "&"+ getSharedPreferences("info",MODE_PRIVATE).getString("stdid_value","1");
        for(int i=0; i<stdidList.size(); i++){
             address += "&" + stdidList.get(i) + "&" + verifyidList.get(i);
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher((String)listAdapter.getGroup(1));
        if(matcher.find()){
            address += "&" + matcher.find();
        }
        else{
            return "";
        }
        return address;
    }
    private void buildingRequest(){
        String gender = getSharedPreferences("info",MODE_PRIVATE).getString("gender","2");
        String address = String.format("https://api.mysspku.com/index.php/V1/MobileCourse/getRoom?gender=%s",gender);
        new Thread(new FetchDataService(address, FetchDataService.ROOM_DATA, FetchDataService.REQUEST_GET,this)).start();
    }
    private void submitRequest(){
        String address = generateAddress();
        if("".equals(address)){
            Toast.makeText(this, "信息不完整，请检查输入", Toast.LENGTH_SHORT);
        }
        else{
            //发送选宿舍请求
            new Thread(new FetchDataService(address,FetchDataService.CHOOSE_RESULT, FetchDataService.REQUEST_POST, this)).start();
        }
    }
    public Handler getHandler() {
        return handler;
    }
}
