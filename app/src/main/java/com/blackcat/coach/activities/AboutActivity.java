package com.blackcat.coach.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.utils.BaseUtils;

public class AboutActivity extends BaseNoFragmentActivity {

    private TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        configToolBar(R.mipmap.ic_back);
        mTvVersion  = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText(getResources().getString(R.string.str_app_version, BaseUtils.getVersionName(this)));
    }
}
