package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.RegisterOkEvent;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.RegisterParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.timer.VerifyCodeTimer;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

import de.greenrobot.event.EventBus;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPhoneNum, mEtVerifyCode, mEtPwd, mEtPwd2, mEtInivte;
    private TextView mTvSendCode;
    private TextView tv_proto;
    private Button mBtnRegister;

    private Type mSmsType = new TypeToken<Result>(){}.getType();
    private Type mRegisterType = new TypeToken<Result<CoachInfo>>(){}.getType();
    private VerifyCodeTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        configToolBar(R.mipmap.ic_back);
        initViews();
    }

    private void initViews() {
        mEtPhoneNum = (EditText) findViewById(R.id.et_phonenum);
        mEtVerifyCode = (EditText) findViewById(R.id.et_verification_code);
        mEtPwd = (EditText) findViewById(R.id.et_password);
        mEtPwd2 = (EditText) findViewById(R.id.et_password2);
        mEtInivte = (EditText) findViewById(R.id.et_invite_code);
        mTvSendCode = (TextView) findViewById(R.id.tv_send_code);
        mTvSendCode.setOnClickListener(this);
        tv_proto = (TextView) findViewById(R.id.tv_proto);
        tv_proto.setOnClickListener(this);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_send_code:
//                if (TextUtils.isEmpty(mEtPhoneNum.getText())) {
//                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
//                    return;
//                }


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
            case R.id.tv_proto:
                Intent intent = new Intent(this, TermsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_register:
                if (TextUtils.isEmpty(mEtPhoneNum.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
                    return;
                }
                if (TextUtils.isEmpty(mEtVerifyCode.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.sms_empty);
                    return;
                }
                if (TextUtils.isEmpty(mEtPwd.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.pwd_empty);
                    return;
                }
                String password = mEtPwd.getText().toString();
                String conPass = mEtPwd2.getText().toString();
                if (!conPass.equals(password)) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.pwd_not_match);
                    return ;
                }
                registerRequest(mEtPhoneNum.getText().toString(), mEtVerifyCode.getText().toString(), mEtPwd.getText().toString(), mEtInivte.getText().toString());
//                registerAccount();
                EventBus.getDefault().post(new RegisterOkEvent());
                //
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
                new Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.register_sent);
                            mTimer = new VerifyCodeTimer(60000, 1000, RegisterActivity.this, mTvSendCode);
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
                new ErrorListener() {
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

    private void registerRequest(String mobile, String authCode, String pwd, String invite) {
        URI uri = URIUtil.getRegister(mobile, authCode, pwd, invite);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        RegisterParams params = new RegisterParams();
        params.mobile = mobile;
        params.smscode = authCode;
        params.usertype = Constants.USR_TYPE_COACH;
        params.password = BaseUtils.getMD5code(pwd);
        params.referrerCode = authCode;

        GsonIgnoreCacheHeadersRequest<Result<CoachInfo>> request = new GsonIgnoreCacheHeadersRequest<Result<CoachInfo>>(
                Request.Method.POST, url, GsonUtils.toJson(params), mRegisterType, null,
                new Listener<Result<CoachInfo>>() {
                    @Override
                    public void onResponse(Result<CoachInfo> response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK && response.data != null) {
                                Session.save(response.data, true);
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.register_ok);
                                startActivity(new Intent(RegisterActivity.this, UploadCoachInfoActivity.class));
                                EventBus.getDefault().post(new RegisterOkEvent());
                                finish();
                            } else if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            }
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new ErrorListener() {
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


    private void registerAccount() {
        final String phoneNum = mEtPhoneNum.getText().toString().trim();
        final String pwd = mEtPwd.getText().toString().trim();
        String pwd2 = mEtPwd2.getText().toString().trim();

        if(phoneNum.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()){
            return;
        }

        if(!pwd.equals(pwd2)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(phoneNum, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "registered successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }catch (final EaseMobException e ){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int errorCode = e.getErrorCode();
                            switch (errorCode) {
                                case EMError.NONETWORK_ERROR:
                                    Toast.makeText(getApplicationContext(), "non network error", Toast.LENGTH_SHORT).show();
                                    break;
                                case EMError.USER_ALREADY_EXISTS:
                                    Toast.makeText(getApplicationContext(), "user already exists error", Toast.LENGTH_SHORT).show();
                                    break;
                                case EMError.UNAUTHORIZED:
                                    Toast.makeText(getApplicationContext(), "unauthorized error", Toast.LENGTH_SHORT).show();
                                    break;
                                case EMError.ILLEGAL_USER_NAME:
                                    Toast.makeText(getApplicationContext(), "illegal user name error", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "registered error", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
