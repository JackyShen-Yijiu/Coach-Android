package com.blackcat.coach.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.DrivingSchool;
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

import de.greenrobot.event.EventBus;

public class SelfIntroducationActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtSelfInfo;
    private Button mBtnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_introducation);
        configToolBar(R.mipmap.ic_back);

        mEtSelfInfo = (EditText)findViewById(R.id.et_self_intro);
        mBtnSubmit = (Button)findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
            mEtSelfInfo.setText(Session.getSession().introduction);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                if (TextUtils.isEmpty(mEtSelfInfo.getText().toString().trim())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_self_info_empty);
                    return;
                }
                updateRequest(mEtSelfInfo.getText().toString().trim());
                break;
        }
    }

    private void updateRequest(final String selfInfo) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.introduction = selfInfo;
        Session.getSession().updateRequest(this, params);
    }

}
