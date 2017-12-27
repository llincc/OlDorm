package com.app.linch.oldorm.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.linch.oldorm.R;
import com.app.linch.oldorm.app.MyApplication;
import com.app.linch.oldorm.util.SharePreferencedOp;

import org.w3c.dom.Text;

/**
 * Created by linch on 2017/12/1.
 */

public class InfoChoosed extends Activity implements View.OnClickListener{
    private static final String TAG = "InfoChoosed";
    private TextView name_value, stdid_value, gender_value, build_value, room_value, state;

    private ImageView  menu;
    private PopupMenu popupMenu;
    private PopupWindow popWindow;

    private boolean mIsExit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_choosed);

        name_value = (TextView)findViewById(R.id.name_value_c);
        stdid_value = (TextView)findViewById(R.id.stdid_value_c);
        gender_value = (TextView)findViewById(R.id.gender_value_c);
        state = (TextView)findViewById(R.id.state_value_c);
        build_value = (TextView)findViewById(R.id.build_value_c);
        room_value = (TextView)findViewById(R.id.room_value_c);
        menu = (ImageView)findViewById(R.id.info_menu_c);
        menu.setOnClickListener(this);

        initPopupWindow();
        initPopupMenu();
        initView();
    }

    private void initPopupWindow(){
        View contentView = LayoutInflater.from(InfoChoosed.this).inflate(R.layout.exit_window, null);
        popWindow  = new PopupWindow(contentView);
        popWindow.setContentView(contentView);
        popWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //popWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) R.drawable.map));
        TextView exit_sure = (TextView)contentView.findViewById(R.id.exit_sure);
        TextView exit_cancel = (TextView)contentView.findViewById(R.id.exit_cancel);

        exit_sure.setOnClickListener(this);
        exit_cancel.setOnClickListener(this);

        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

    }
    private void initPopupMenu(){

        popupMenu = new PopupMenu(this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.simple_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_change:
                        SharePreferencedOp.clearConfig("info", MODE_PRIVATE); //清空记录
                        Intent intent = new Intent(InfoChoosed.this, LoginActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "转到登录界面");
                        finish();
                        break;
                    case R.id.item_exit:
                        backgroundAlpha(0.5f);
                        showPopWindow();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void showPopWindow(){
        View rootView = LayoutInflater.from(InfoChoosed.this).inflate(R.layout.info_choosed, null);
        popWindow.showAtLocation(rootView, Gravity.CENTER,0,0);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info_menu_c:
                //不能在Oncreate（）里面使用，那时候activity可能还没运行，这样会报错
                popupMenu.show();
                break;
            case R.id.exit_sure:
                popWindow.dismiss();
                MyApplication.getInstance().popAllActivityExceptOne(InfoChoosed.class); //销毁堆栈中其他activity
                finish();
                break;
            case R.id.exit_cancel:
                popWindow.dismiss();
                break;
            default:
                break;

        }
    }

    private void initView(){
        String name = getSharedPreferences("info",MODE_PRIVATE).getString("name_value","无");
        String stdid = getSharedPreferences("info",MODE_PRIVATE).getString("stdid_value","0000000000");
        String gender = getSharedPreferences("info",MODE_PRIVATE).getString("gender_value","男");
        String room = getSharedPreferences("info",MODE_PRIVATE).getString("room_value","无");
        String build = getSharedPreferences("info",MODE_PRIVATE).getString("build_value","0000000000");

        name_value.setText(name);
        stdid_value.setText(stdid);
        gender_value.setText(gender);

        state.setText(R.string.info_state_c_value);

        room_value.setText(room);
        build_value.setText(build);
    }    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
