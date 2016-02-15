package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * 学员预约列表
 */
public class OrderStudentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_student);
        configToolBar(R.mipmap.ic_back);

    }


}
