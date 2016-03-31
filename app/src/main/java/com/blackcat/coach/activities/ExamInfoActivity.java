package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * Created by yyhui on 2016/3/31.
 */
public class ExamInfoActivity extends BaseNoFragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_info);
        configToolBar(R.mipmap.ic_back);
    }


}
