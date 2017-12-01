package com.app.linch.oldorm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.linch.oldorm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linch on 2017/11/30.
 */

public class ChooseAdpter extends BaseExpandableListAdapter{
    private List<String> parents;
    private Map<String, List<String>> childs;
    private List<Integer> buildnumber;
    private Context context;
    public ChooseAdpter(List<String> parents, Map<String,List<String>> childs, Context context){
        this.parents = parents;
        this.childs = childs;
        this.context = context;
        initBuildNumber(); //初始化空床位数量

    }
    //返回父控件个数
    @Override
    public int getGroupCount() {
        return parents.size();
    }
    //返回当前父控件中子控件的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return childs.get(parents.get(groupPosition)).size();
    }
    //返回父控件标识
    @Override
    public Object getGroup(int groupPosition) {
        return parents.get(groupPosition);
    }
    //返回子控件标识
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(parents.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.choose_parent, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.number_parent);
        tv.setText(parents.get(groupPosition));

        ImageView arrowimage = (ImageView) convertView.findViewById(R.id.arrow);
        if(isExpanded){
            arrowimage.setImageDrawable(convertView.getResources().getDrawable(R.drawable.down)); //箭头朝下
        }
        else{
            arrowimage.setImageDrawable(convertView.getResources().getDrawable(R.drawable.right));//箭头朝右
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child = (String)getChild(groupPosition,childPosition);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(groupPosition == 0)      convertView = inflater.inflate(R.layout.choose_number_child,null);
        else if(groupPosition ==1)  convertView = inflater.inflate(R.layout.choose_build_child,null);


        if(groupPosition == 0){
            TextView number_child = (TextView)convertView.findViewById(R.id.number_child);
            number_child.setText(child);
        }
        else if(groupPosition == 1){
            //楼号
            TextView build_child = (TextView)convertView.findViewById(R.id.build_child);
            build_child.setText(child);
            //空余床数
            TextView build = (TextView)convertView.findViewById(R.id.build);
            build.setText(String.format("%d余量", buildnumber.get(childPosition)));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    //改变父组件名称
    public void changeParentName(int groupPosition, int childPosition){
        String group = (String)getGroup(groupPosition);
        String newname = (String)getChild(groupPosition,childPosition);
        List<String> groupchild = childs.get(group);
        childs.remove(group);
        childs.put(newname,groupchild);
        parents.set(groupPosition,newname);
    }

    private void initBuildNumber(){
        buildnumber = new ArrayList<Integer>();
        for(int i=0; i<5; i++){
            buildnumber.add(0);
        }
    }

    /**
     * 更新楼的空床数
     * @param buildnumber
     */
    public void updateBuildNumber(List<Integer> buildnumber){
        if(buildnumber == null || buildnumber.size() != this.buildnumber.size()){
            Log.d("NumberAdapter","参数出错");
            return;
        }
        this.buildnumber = buildnumber;
    }
}
