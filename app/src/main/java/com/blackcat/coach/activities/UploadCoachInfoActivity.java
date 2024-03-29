package com.blackcat.coach.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CoachTypeAdapter;
import com.blackcat.coach.models.DrivingSchool;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.ApplyVerifyParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.DateUtil;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class UploadCoachInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtCard, mEtCarCert, mEtCoachCert, mEtName;
    private Button mBtnCommit;
    private TextView mTvSchool,mTvCoach;
    private DrivingSchool mDrivingSchool;
   private  int type = 1;
    private String schoolId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.isUserInfoEmpty()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_upload_coach_info);
        configToolBar(R.mipmap.ic_back);
        initViews();
        EventBus.getDefault().register(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void initViews() {
        findViewById(R.id.rl_choose_school).setOnClickListener(this);

        mEtCard = (EditText) findViewById(R.id.et_card);
        mEtCarCert = (EditText) findViewById(R.id.et_drive_cert);
        mEtCoachCert = (EditText) findViewById(R.id.et_coach_cert);
        mEtName = (EditText) findViewById(R.id.et_name);

        mTvCoach = (TextView) findViewById(R.id.tv_coach_type);
        mTvSchool = (TextView) findViewById(R.id.tv_drive_school);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        mBtnCommit.setOnClickListener(this);
        mTvSchool.setOnClickListener(this);

        mTvCoach.setText("直营教练");
    }

    private Type mType = new TypeToken<Result>() {
    }.getType();

    /**
     * coachtype": "0" // 教练类型 0 挂靠教练 1直营教练
     * @param name
     * @param idcardnumber
     * @param driveschoolid
     * @param drivinglicensenumber
     * @param coachnumber
     */
    private void applyVerifyRequest(String name, String idcardnumber, String driveschoolid, String drivinglicensenumber, String coachnumber,int type) {
        URI uri = URIUtil.getApplyVerify();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        ApplyVerifyParams params = new ApplyVerifyParams();
        params.coachid = Session.getSession().coachid;
        params.name = name;
        params.idcardnumber = idcardnumber;
        params.drivinglicensenumber = drivinglicensenumber;
        params.driveschoolid = driveschoolid;
        params.coachnumber = coachnumber;
        params.coachtype = String.valueOf(type);

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.sent_ok);
                                startActivity(new Intent(UploadCoachInfoActivity.this, IndexActivity.class));
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

    @Override
    public void finish() {
        startActivity(new Intent(this, IndexActivity.class));
        super.finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_commit:
                if(check()){
                    applyVerifyRequest(mEtName.getText().toString(), mEtCard.getText().toString(), mDrivingSchool.id, mEtCarCert.getText().toString(), mEtCoachCert.getText().toString(),type);
                }
                 break;
            case R.id.rl_choose_school://选择驾校

                Intent i = new Intent(this, DrivingSchoolActivity.class);
                i.putExtra("regist",true);
                startActivityForResult(i, 1);
                break;
            case R.id.rl_choose_coach_type://选择教练类型
                Intent i1 = new Intent(this, JobCategory.class);
                i1.putExtra("regist",true);
                startActivityForResult(i1,1);
//                startActivity(i1);
                break;
        }
    }

    private boolean check(){
        if (TextUtils.isEmpty(mEtCard.getText())) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.verify_id_empty);
            return false;
        }
//16
        if (!DateUtil.isIdCard(mEtCard.getText().toString().trim())) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_idcardnumber_invalid);
            return false;
        }
//        else if(mEtCard.getText().toString().length()!=15){
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_idcardnumber_invalid);
//            return false;
//        }

//        if (mEtCard.getText().toString().length()!=15 || mEtCard.getText().toString().length()!=18) {
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_idcardnumber_invalid);
//            return false;
//        }

        if (TextUtils.isEmpty(mEtName.getText())) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.verify_name_empty);
            return false;
        }

        if (mDrivingSchool == null || TextUtils.isEmpty(mDrivingSchool.name)) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.drivingschool_empty);
            return false;
        }
//                if (TextUtils.isEmpty(mEtCarCert.getText())) {
//                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.verify_drivingcard_empty);
//                    return;
//                }
        if (TextUtils.isEmpty(mEtCoachCert.getText())) {
            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.verify_coachcard_empty);
            return false;
        }
//        if(type==2){
//            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast("请选择工作性质");
//            return false;
//        }
        return true;
    }


    public void onEvent(DrivingSchool event) {
        this.mDrivingSchool = event;
        mTvSchool.setText(mDrivingSchool.name);
    }

    // http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        if(data!=null && resultCode==3){//选择 教练类型
            String name = data.getStringExtra("bean");
            LogUtil.print("name---->"+name);
            //
            if(name.equals(getString(R.string.str_direct_coach))){
                type = 1;
            }else if(name.equals(getString(R.string.str_hangon_coach))){
                type = 0;
            }else{
                type = 1;
            }

            if(null!=name)
                mTvCoach.setText(name);
        }
        if(data!=null && resultCode==2){//驾校
            mDrivingSchool = (DrivingSchool)data.getSerializableExtra("bean");
            mTvSchool.setText(mDrivingSchool.name);
            schoolId = mDrivingSchool.id;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}
