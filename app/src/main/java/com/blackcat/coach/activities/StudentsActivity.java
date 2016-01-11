package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * 学员列表
 */
public class StudentsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        configToolBar(R.mipmap.ic_back);
        initView();

    }

    private void initView() {

    }

}
