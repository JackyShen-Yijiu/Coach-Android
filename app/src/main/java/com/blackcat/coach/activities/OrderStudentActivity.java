package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * 学员预约列表
 */
public class OrderStudentActivity extends BaseActivity {

    /**
     *   1-学员预约时，点击返回
     *   2-
     */
    public static int orderStudentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_student);
        configToolBar(R.mipmap.ic_back);

    }


}
