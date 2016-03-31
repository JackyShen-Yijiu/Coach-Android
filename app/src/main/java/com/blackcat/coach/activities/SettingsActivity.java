package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.cache.DataCleanManager;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.events.LogoutEvent;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;

import com.blackcat.coach.models.UserSetting;
import com.blackcat.coach.models.params.SettingParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.ShSwitchView;
import com.easemob.EMCallBack;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;

import java.util.Map;

import de.greenrobot.event.EventBus;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private View mLogoutView;
    private ShSwitchView sv_appointment, sv_class_notice, sv_new_notice;
    private int settingAppointment, settingClassNotice, settingNewNotice;

    private Type mTokenType;
    private TextView cache_number;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configToolBar(R.mipmap.ic_back);
        initViews();
        initData();


    }

    private boolean firstOpen = true;
    private UserSetting userSetting;
    private void initData() {

        userSetting = Session.getSession().usersetting;

        if(userSetting !=null){
            if ("true".equals(userSetting.reservationreminder)) {
                settingAppointment = 1;
                sv_appointment.setOn(true);
            }

            if ("true".equals(userSetting.classremind)) {
                settingClassNotice =1;
                sv_class_notice.setOn(true);
            }


            if ("true".equals(userSetting.newmessagereminder)) {
                settingNewNotice = 1;
                sv_new_notice.setOn(true);
            }

            firstOpen = false;
        }


    }

    //获取设置状态
    private void obtainPersonalSetting() {
        mTokenType = new TypeToken<Result<String>>() {
        }.getType();
        URI uri = URIUtil.getSettingUri();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        SettingParams param = new SettingParams();
        param.userid = Session.getSession().coachid;
        param.usertype = "2";
        param.reservationreminder = settingAppointment + "";
        param.newmessagereminder = settingNewNotice + "";
        param.classremind = settingClassNotice + "";

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result<CoachInfo>> request = new GsonIgnoreCacheHeadersRequest<Result<CoachInfo>>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result<CoachInfo>>() {
                    @Override
                    public void onResponse(Result<CoachInfo> response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            //设置成功，保存状态
                            if(settingAppointment == 1){
                                userSetting.reservationreminder = "true";
                            }else{
                                userSetting.reservationreminder = "false";
                            }

                            if(settingClassNotice == 1){
                                userSetting.classremind = "true";
                            }else{
                                userSetting.classremind = "false";
                            }

                            if(settingNewNotice == 1){
                                userSetting.newmessagereminder = "true";
                            }else{
                                userSetting.newmessagereminder = "false";
                            }
                            Session.setUserSetting(userSetting);
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.setting_success);
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
//                        ToastHelper.getInstance(d.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }

    private void initViews() {
        findViewById(R.id.rl_about).setOnClickListener(this);
        findViewById(R.id.rl_rating).setOnClickListener(this);
        findViewById(R.id.rl_feedback).setOnClickListener(this);
        cache_number=(TextView) findViewById(R.id.cache_number);
        findViewById(R.id.rl_cache).setOnClickListener(this);
        mLogoutView = findViewById(R.id.ll_logout);
        mLogoutView.setOnClickListener(this);

        sv_appointment = (ShSwitchView) findViewById(R.id.sv_appointment);
        sv_appointment.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (!firstOpen) {
                    LogUtil.print("设置预约提醒");
                    settingAppointment = sv_appointment.isOn() ? 1 : 0;
                    obtainPersonalSetting();
                }
            }
        });
        sv_new_notice = (ShSwitchView) findViewById(R.id.sv_new_notice);
        sv_new_notice.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (!firstOpen) {
                    settingNewNotice = sv_new_notice.isOn() ? 1 : 0;
                    obtainPersonalSetting();
                }
            }
        });
        sv_class_notice = (ShSwitchView) findViewById(R.id.sv_class_notice);
        sv_class_notice.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if (!firstOpen) {
                    settingClassNotice = sv_class_notice.isOn() ? 1 : 0;
                    obtainPersonalSetting();
                }

            }
        });
        if (Session.isUserInfoEmpty()) {
            mLogoutView.setVisibility(View.INVISIBLE);
        }
        try {
            cache_number.setText(DataCleanManager.getTotalCacheSize(SettingsActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
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
            case R.id.sv_appointment://预约
                break;
            case R.id.sv_new_notice://新消息通知
                break;
            case R.id.sv_class_notice://开课提醒
                break;
            case R.id.rl_cache://清除缓存
                try {
                    //查看缓存的大小
                    Log.e("YQY", DataCleanManager.getTotalCacheSize(SettingsActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DataCleanManager.clearAllCache(SettingsActivity.this);
                try {
                    //清除后的操作
                    Log.e("YQY", DataCleanManager.getTotalCacheSize(SettingsActivity.this));
                    cache_number.setText(DataCleanManager.getTotalCacheSize(SettingsActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
