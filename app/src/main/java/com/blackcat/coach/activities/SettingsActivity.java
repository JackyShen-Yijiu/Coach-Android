package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blackcat.coach.R;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.events.LogoutEvent;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.utils.BaseUtils;
import com.easemob.EMCallBack;

import de.greenrobot.event.EventBus;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private View mLogoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configToolBar(R.mipmap.ic_back);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.rl_about).setOnClickListener(this);
        findViewById(R.id.rl_rating).setOnClickListener(this);
        findViewById(R.id.rl_feedback).setOnClickListener(this);
        mLogoutView = findViewById(R.id.ll_logout);
        mLogoutView.setOnClickListener(this);

        if (Session.isUserInfoEmpty()) {
            mLogoutView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.rl_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_rating:
                BaseUtils.rateAppInMarket(this);
                break;
            case R.id.ll_logout:
                Session.save(null, true);
                logout();

                //TODO, clear volley cache
                EventBus.getDefault().post(new LogoutEvent());
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void logout() {
        BlackCatHXSDKHelper.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
//                    }
//                });

            }

            @Override
            public void onProgress(int process, String status) {
            }

            @Override
            public void onError(int code, String message) {
            }
        });
    }
}
