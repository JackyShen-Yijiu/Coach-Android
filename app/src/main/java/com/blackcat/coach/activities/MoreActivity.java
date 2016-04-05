package com.blackcat.coach.activities;

import android.os.Bundle;

import com.blackcat.coach.R;

/**
 * Created by aa on 2016/3/31.
 */
public class MoreActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        configToolBar(R.mipmap.ic_back);
    }
}
