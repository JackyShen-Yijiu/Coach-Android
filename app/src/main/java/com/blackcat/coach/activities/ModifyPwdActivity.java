package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdatePwdOk;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.params.UpdatePwdParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

import de.greenrobot.event.EventBus;

public class ModifyPwdActivity extends BaseNoFragmentActivity {

    private EditText mEtPwd, mEtPwd2;
    private Button mBtnCommit;

    private String mSms, mPhone;
    private Type mType = new TypeToken<Result>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        configToolBar(R.mipmap.ic_back);
        mPhone = getIntent().getStringExtra(Constants.ID);
        mSms = getIntent().getStringExtra(Constants.DATA);
        initViews();
    }

    private void initViews() {
        mEtPwd = (EditText) findViewById(R.id.et_password);
        mEtPwd2 = (EditText) findViewById(R.id.et_password2);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtPwd.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.pwd_empty);
                    return;
                } else if (!mEtPwd.getText().toString().equals(mEtPwd2.getText().toString())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.pwd_not_match);
                    return;
                }
                sendSmsRequest(mPhone, mEtPwd.getText().toString(), mSms);
            }
        });
    }

    private void sendSmsRequest(String mobile, String pwd, String sms) {
        URI uri = URIUtil.getFindPwd();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        UpdatePwdParams params = new UpdatePwdParams();
        params.mobile = mobile;
        params.password = BaseUtils.getMD5code(pwd);
        params.smscode = sms;
        params.usertype = Constants.USR_TYPE_COACH;
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mType, null,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.update_pwd_ok);
                            EventBus.getDefault().post(new UpdatePwdOk());
                            finish();
                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
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
