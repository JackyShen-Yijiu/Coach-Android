package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdatePwdOk;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

import de.greenrobot.event.EventBus;

public class ForgetPwdActivity extends BaseNoFragmentActivity implements View.OnClickListener {

    private EditText mEtPhoneNum, mEtVerifyCode;
    private TextView mTvSendCode;
    private Button mBtnNext;

    private Type mSmsType = new TypeToken<Result>(){}.getType();
    private VerifyCodeTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        configToolBar(R.mipmap.ic_back);
        initViews();
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        mEtPhoneNum = (EditText) findViewById(R.id.et_phonenum);
        mEtVerifyCode = (EditText) findViewById(R.id.et_verification_code);
        mTvSendCode = (TextView) findViewById(R.id.tv_send_code);
        mTvSendCode.setOnClickListener(this);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_send_code:

                    String phone = mEtPhoneNum.getText().toString();
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
                    sendSmsRequest(mEtPhoneNum.getText().toString());
                    break;
            case R.id.btn_next:
                if (TextUtils.isEmpty(mEtVerifyCode.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.sms_empty);
                    return;
                }
                Intent intent = new Intent(this, ModifyPwdActivity.class);
                intent.putExtra(Constants.ID, mEtPhoneNum.getText().toString());
                intent.putExtra(Constants.DATA, mEtVerifyCode.getText().toString());
                startActivity(intent);
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
                            mTimer = new VerifyCodeTimer(60000, 1000);
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

    public class VerifyCodeTimer extends CountDownTimer {

        public VerifyCodeTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mTvSendCode.setText(R.string.register_get_code);
            mTvSendCode.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // some script here
            mTvSendCode.setText(String.format(
                    getResources().getString(R.string.register_time_left),
                    millisUntilFinished / 1000));
        }
    }

    public void onEvent(UpdatePwdOk event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
