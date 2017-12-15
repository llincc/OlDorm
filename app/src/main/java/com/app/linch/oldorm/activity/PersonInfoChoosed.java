package com.app.linch.oldorm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.app.linch.oldorm.R;

import org.w3c.dom.Text;

/**
 * Created by linch on 2017/12/1.
 */

public class PersonInfoChoosed extends Activity{
    private TextView name_value, stdid_value, gender_value, build_value, room_value, state;
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

        initView();
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
    }
}
