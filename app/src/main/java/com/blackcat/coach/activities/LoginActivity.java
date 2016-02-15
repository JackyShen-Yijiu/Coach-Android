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
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.events.RegisterOkEvent;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.LoginParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPhoneNum;
    private EditText mEtPwd;
    private Button mBtnLogin;
    private TextView mTvForgetPwd, mTvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        EventBus.getDefault().register(this);
    }

    @Override
    protected boolean hasActionbarShadow() {
        return false;
    }

    private void initViews() {
        mEtPhoneNum = (EditText) findViewById(R.id.et_phonenum);
        mEtPwd = (EditText) findViewById(R.id.et_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mTvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
        mTvForgetPwd.setOnClickListener(this);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forget_pwd:
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                String username = mEtPhoneNum.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.pwd_empty);
                    return;
                }
                loginRequest(username, pwd);
                //TODO
                //hxLogin(username, pwd);
                break;
        }
    }

    private Type mTokenType = new TypeToken<Result<CoachInfo>>() {}.getType();

    private void loginRequest(String mobile, final String pwd) {
        URI uri = URIUtil.getLoginUri();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        LoginParams param = new LoginParams();
        param.mobile = mobile;
        param.usertype = Constants.USR_TYPE_COACH;
        param.password = BaseUtils.getMD5code(pwd);

        GsonIgnoreCacheHeadersRequest<Result<CoachInfo>> request = new GsonIgnoreCacheHeadersRequest<Result<CoachInfo>>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, null,
                new Listener<Result<CoachInfo>>() {
                    @Override
                    public void onResponse(Result<CoachInfo> response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.login_ok);
                            Session.save(response.data, true);
                            JPushInterface.setAlias(CarCoachApplication.getInstance(), Session.getSession().coachid, null);//"123"
                            String _pwd = mEtPwd.getText().toString().trim();
                            hxLogin(response.data.coachid, BaseUtils.getMD5code(_pwd));
                            startActivity(new Intent(LoginActivity.this, IndexActivity.class));
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            } else {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                            }
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

    public void onEvent(RegisterOkEvent event) {
        LogUtil.print("onevent-->finish");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private void hxLogin(final String username, final String pwd) {//环信登录
//    if (BlackCatHXSDKHelper.getInstance().isLogined()) {
//        return;
//    }

        EMChatManager.getInstance().login(username, pwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                CarCoachApplication.getInstance().setUsername(username);
                CarCoachApplication.getInstance().setPassword(pwd);
                System.out.println("HX onSuccess");
            }
            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println("HX onError");
            }

            @Override
            public void onProgress(int i, String s) {
                System.out.println("HX onProgress");
            }
        });
    }

}
