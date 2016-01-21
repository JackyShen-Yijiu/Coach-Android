package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdateMobileOk;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateMobileParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.timer.VerifyCodeTimer;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class BindPhoneActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView mTvBindPhoneNum;
    private EditText mEtNewPhoneNum;
    private EditText mEtVerifyCode;
    private TextView mTvSendCode;
    private Button   mBtnFinish;

    private Type mSmsType = new TypeToken<Result>(){}.getType();
    private VerifyCodeTimer mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mTvBindPhoneNum = (TextView)findViewById(R.id.tv_phone_num);
        mEtNewPhoneNum = (EditText)findViewById(R.id.et_new_phone);
        mEtVerifyCode = (EditText)findViewById(R.id.et_verification_code);
        mTvSendCode = (TextView)findViewById(R.id.tv_send_code);
        mTvSendCode.setOnClickListener(this);

        mBtnFinish = (Button)findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(this);

        mTvBindPhoneNum.setText(Session.getSession().mobile);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_send_code: {
//                if (TextUtils.isEmpty(mEtNewPhoneNum.getText())) {
//                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
//                    return;

                    String phone = mEtNewPhoneNum.getText().toString();
                    if (TextUtils.isEmpty(phone)) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
                        return ;
                    } else {
                        if (!CommonUtil.isMobile(phone)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_enrro);
                            return ;
                        }
                    }
                    if (phone.length() != 11) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_enrros);
                        return ;
                    }
                }
                sendSmsRequest(mEtNewPhoneNum.getText().toString());
                break;

            case R.id.btn_finish:
                if (TextUtils.isEmpty(mEtNewPhoneNum.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
                    return;
                }
                if (TextUtils.isEmpty(mEtVerifyCode.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.sms_empty);
                    return;
                }
                bindPhoneRequest(mEtNewPhoneNum.getText().toString().trim(),
                        mEtVerifyCode.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    private void sendSmsRequest(String mobile) {
        URI uri = URIUtil.getSendSms(mobile);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                url, mSmsType, null,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.register_sent);
                            mTimer = new VerifyCodeTimer(60000, 1000, BindPhoneActivity.this, mTvSendCode);
                            mTimer.start();
                            mTvSendCode.setEnabled(false);
                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
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
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }

    private Type mBindPhoneType = new TypeToken<Result>(){}.getType();

    private  void bindPhoneRequest(final String mobile, String authCode) {
        URI uri = URIUtil.getUpdateMobile(authCode, mobile);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        UpdateMobileParams params = new UpdateMobileParams();
        params.smscode = authCode;
        params.mobile = mobile;
        params.usertype = Constants.USR_TYPE_COACH;

        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mBindPhoneType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        String msg = response.msg;
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_modify_ok);
                            CoachInfo coachinfo = Session.getSession();
                            coachinfo.mobile = mobile;
                            Session.save(coachinfo, true);
                            EventBus.getDefault().post(new UpdateMobileOk());
                            //TODO
                            finish();
                        } else if (response != null &&!TextUtils.isEmpty(response.msg)) {
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
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }
}
