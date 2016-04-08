package com.blackcat.coach.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.UpdatePwdOk;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.NewLoginParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.LoginRelative;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class NewLoginActivity extends BaseNoFragmentActivity implements View.OnClickListener {

    private EditText mEtPhoneNum, mEtVerifyCode;
    private TextView mTvSendCode;
    private Button mBtnLogin;

    private Type mSmsType = new TypeToken<Result>(){}.getType();
    private VerifyCodeTimer mTimer;
    private TextView tv_proto;
    private  android.app.AlertDialog  alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.new_activity_login);
        initViews();
    }

    private int keyHeight = 300;

//    @Override
    protected void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        //old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值

//      System.out.println(oldLeft + " " + oldTop +" " + oldRight + " " + oldBottom);
//      System.out.println(left + " " + top +" " + right + " " + bottom);


        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起


    }

    private void initViews() {


        mEtPhoneNum = (EditText) findViewById(R.id.et_phonenum);
        mEtVerifyCode = (EditText) findViewById(R.id.et_verification_code);
        mTvSendCode = (TextView) findViewById(R.id.tv_send_code);
        mTvSendCode.setOnClickListener(this);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);

        tv_proto = (TextView) findViewById(R.id.tv_proto);
        tv_proto.setOnClickListener(this);
        final ImageView img = (ImageView) findViewById(R.id.iv_1);
        final com.blackcat.coach.widgets.LoginRelative rl = (com.blackcat.coach.widgets.LoginRelative) findViewById(R.id.new_act_login_rl);
        final LinearLayout l1 = (LinearLayout) findViewById(R.id.new_act_login_r2);
        rl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){


            @Override
            public void onGlobalLayout(){

//                if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
////                    LogUtil.print("root::>"+rl.getRootView().getHeight()+"Height;;>>"+rl.getHeight());
//                    Toast.makeText(NewLoginActivity.this, "监听到软键盘弹起...777", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(NewLoginActivity.this, "监听到软件盘关闭...8888", Toast.LENGTH_SHORT).show();
//                }

                //比较Activity根布局与当前布局的大小
                int heightDiff = rl.getRootView().getHeight()- rl.getHeight();

                if(heightDiff >100){

                    //大小超过100时，一般为显示虚拟键盘事件
//                    Toast.makeText(NewLoginActivity.this, "监听到软键盘弹起...3333", Toast.LENGTH_SHORT).show();
                }else{
//                    Toast.makeText(NewLoginActivity.this, "监听到软件盘关闭...444", Toast.LENGTH_SHORT).show();
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏

                }
            }
        });


//        findViewById(R.id.new_act_login_r2).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right,
//                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//
//
//                Toast.makeText(NewLoginActivity.this, top+"监听到软键盘弹起..2222."+oldTop, Toast.LENGTH_SHORT).show();
//
//                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
//
//                    Toast.makeText(NewLoginActivity.this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
//
//                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
//
//                    Toast.makeText(NewLoginActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        rl.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right,
//                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Toast.makeText(NewLoginActivity.this, top+"监听到软键盘弹起..1111."+oldTop, Toast.LENGTH_SHORT).show();
//
//                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
//
//                    Toast.makeText(NewLoginActivity.this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
//
//                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
//
//                    Toast.makeText(NewLoginActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//

//
//
//        rl.setStateListener(new LoginRelative.KeyBordStateListener() {
//            @Override
//            public void statechange(int state) {
//                switch(state){
//                    case LoginRelative.KEYBORAD_HIDE:
//                        img.setVisibility(View.VISIBLE);
//                        break;
//                    case LoginRelative.KEYBORAD_SHOW:
//                        img.setVisibility(View.GONE);
//                        break;
//                }
//            }
//        });

    }

    private boolean keybord(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        imm.
        return true;
        }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_send_code:

                mTvSendCode.setBackgroundResource(R.drawable.background_border_gree);

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
            case R.id.btn_login:
                String username = mEtPhoneNum.getText().toString().trim();
                String yzm = mEtVerifyCode.getText().toString().trim();

                if (TextUtils.isEmpty(mEtPhoneNum.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.phonenum_empty);
                    return;
                }
                if (TextUtils.isEmpty(mEtVerifyCode.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.sms_empty);
                    return;
                }
                loginRequest(username, yzm);
                break;
            case R.id.tv_proto:
                Intent intent = new Intent(this, TermsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_true:
                alertDialog.dismiss();
                break ;
        }
    }
    private Type mTokenType = new TypeToken<Result<CoachInfo>>() {}.getType();

    private void loginRequest(String mobile, final String yzm) {
        URI uri = URIUtil.getNewLoginUri();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        NewLoginParams param = new NewLoginParams();
        param.mobile = mobile;
        param.smscode = yzm;
//        param.password = BaseUtils.getMD5code(pwd);

        GsonIgnoreCacheHeadersRequest<Result<CoachInfo>> request = new GsonIgnoreCacheHeadersRequest<Result<CoachInfo>>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, null,
                new Response.Listener<Result<CoachInfo>>() {
                    @Override
                    public void onResponse(Result<CoachInfo> response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.login_ok);
                            Session.save(response.data, true);
                            JPushInterface.setAlias(CarCoachApplication.getInstance(), Session.getSession().coachid, null);//"123"
                            String _pwd = (response.data.password).toString().trim();
                            hxLogin(response.data.coachid, _pwd);
                            startActivity(new Intent(NewLoginActivity.this, IndexActivity.class));
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
    private void showCoachDialog() {

        android.app.AlertDialog .Builder builder = new  android.app.AlertDialog .Builder(NewLoginActivity.this);
        LayoutInflater layout = LayoutInflater.from(this);
        View sudokulistView = layout.inflate(R.layout.new_dialog, null);
        builder.setView(sudokulistView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

        WindowManager.LayoutParams params =
                alertDialog.getWindow().getAttributes();
        params.width = (int)(CommonUtil.getWindowsWidth(this)*0.75);
        params.height = (int)(params.width*0.6);
        alertDialog.getWindow().setAttributes(params);

        TextView tv_true = (TextView) sudokulistView
                .findViewById(R.id.tv_true);
        tv_true.setOnClickListener(this);


    }
    private void hxLogin(final String username, final String _pwd) {//环信登录
        LogUtil.print(username+"-ddddd----"+_pwd);
//    if (BlackCatHXSDKHelper.getInstance().isLogined()) {
//        return;
//    }

        EMChatManager.getInstance().login(username, _pwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                CarCoachApplication.getInstance().setUsername(username);
                CarCoachApplication.getInstance().setPassword(_pwd);
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


    private void sendSmsRequest(String mobile) {
        URI uri = URIUtil.getNewSendSms(mobile);
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
                            if (response.msg.contains("您的手机号不属于联盟驾校")){
                                LogUtil.print("asdasd");
                                showCoachDialog();
                            }
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
            mTvSendCode.setBackgroundResource(R.drawable.background_border_blue);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // some script here
            mTvSendCode.setText(String.format(
                    getResources().getString(R.string.register_time_left),
                    millisUntilFinished / 1000));
            mTvSendCode.setTextColor(Color.parseColor("#757575"));
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
