package com.blackcat.coach.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blackcat.coach.R;
import com.blackcat.coach.adapters.StudentsAdapter;
import com.blackcat.coach.fragments.StudentFragment1;
import com.blackcat.coach.lib.PagerSlidingTab;

/**
 * 公告
 */
public class NoticeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        setTitle("公告");
    }

}
