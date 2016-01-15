package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * Created by aa on 2016/1/14.
 */
public class SystemMsgActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_system_msg);
        configToolBar(R.mipmap.ic_back);


    }
}
