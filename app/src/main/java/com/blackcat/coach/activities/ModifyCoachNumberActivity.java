package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class ModifyCoachNumberActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvOldCoachNum;
    private EditText mEtNewCoachNum;
    private Button  mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_coach_number);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private void initView() {
        mTvOldCoachNum = (TextView)findViewById(R.id.tv_coach_num);
        if (!TextUtils.isEmpty(Session.getSession().coachnumber)) {
            mTvOldCoachNum.setText(Session.getSession().coachnumber);
        }
        mEtNewCoachNum = (EditText)findViewById(R.id.et_new_coach_num);
        mBtnFinish = (Button)findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(this);
    }

   @Override
    public void onClick(View v) {
       int id = v.getId();
       switch (id) {
           case R.id.btn_finish:
               if (TextUtils.isEmpty(mEtNewCoachNum.getText().toString().trim())) {
                   ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_license_empty);
                   return;
               }
               String coachnumber = mEtNewCoachNum.getText().toString().trim();
               if (coachnumber.equals(Session.getSession().coachnumber)) {
                   finish();
                   return;
               }
               updateRequest(coachnumber);
               break;
       }
   }

    private void updateRequest(final String coachNumber) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.coachnumber = coachNumber;
        Session.getSession().updateRequest(this, params);
    }
}
