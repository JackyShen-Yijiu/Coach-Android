package com.blackcat.coach.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ModifySeniorityActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvOldSeniority;
    private EditText mEtNewSeniority;
    private Button mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_seniority);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mTvOldSeniority = (TextView)findViewById(R.id.tv_seniority_num);
        if (!TextUtils.isEmpty(Session.getSession().Seniority)) {
            mTvOldSeniority.setText(Session.getSession().Seniority);
        }
        mEtNewSeniority = (EditText)findViewById(R.id.et_new_seniority);
        mBtnFinish = (Button)findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_finish:
                if (TextUtils.isEmpty(mEtNewSeniority.getText().toString().trim())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_license_empty);
                    return;
                }
                String seniority = mEtNewSeniority.getText().toString().trim() + "年";
                if (seniority.equals(Session.getSession().Seniority)) {
                    finish();
                    return;
                }
                updateRequest(seniority);
                break;
        }
    }

    private void updateRequest(final String seniority) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.Seniority = seniority;
        Session.getSession().updateRequest(this, params);
    }
}
