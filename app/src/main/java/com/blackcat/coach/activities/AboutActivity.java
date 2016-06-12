package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.models.User;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.LogUtil;

public class AboutActivity extends BaseNoFragmentActivity implements View.OnClickListener{

    private TextView mTvVersion,tv_proto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        configToolBar(R.mipmap.ic_back);
        mTvVersion  = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText(getResources().getString(R.string.str_app_version, BaseUtils.getVersionName(this)));
        tv_proto=(TextView)findViewById(R.id.tv_proto);
        tv_proto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_proto:
                Intent intent = new Intent(AboutActivity.this, TermsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
