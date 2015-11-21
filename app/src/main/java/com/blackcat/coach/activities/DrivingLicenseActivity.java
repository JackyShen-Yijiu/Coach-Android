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

public class DrivingLicenseActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvOldLicense;
    private EditText mEtNewLicense;
    private Button mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_license);
        configToolBar(R.mipmap.ic_back);
        initView();
    }
    private void initView() {
        mTvOldLicense = (TextView)findViewById(R.id.tv_license_num);
        if (!TextUtils.isEmpty(Session.getSession().drivinglicensenumber)) {
            mTvOldLicense.setText(Session.getSession().drivinglicensenumber);
        }
        mEtNewLicense = (EditText)findViewById(R.id.et_new_license);
        mBtnFinish = (Button)findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(this);
    }

   @Override
   public void onClick(View v) {
       int id = v.getId();
       switch (id) {
           case R.id.btn_finish:
               if (TextUtils.isEmpty(mEtNewLicense.getText().toString().trim())) {
                   ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_license_empty);
                   return;
               }
               String license = mEtNewLicense.getText().toString().trim();
               if (license.equals(Session.getSession().drivinglicensenumber)) {
                   finish();
                   return;
               }
               updateRequest(license);
               break;
       }
   }

    private void updateRequest(final String driverLicense) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.drivinglicensenumber = driverLicense;
        Session.getSession().updateRequest(this, params);
    }
}
