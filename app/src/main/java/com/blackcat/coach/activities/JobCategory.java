package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class JobCategory extends BaseActivity implements View.OnClickListener {

    private RadioGroup mGroupViewSex;
    private RadioButton mRBtnMale;
    private RadioButton mRBtnFemale;
    private Button mBtnFinish;
    private boolean regist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_category);
        configToolBar(R.mipmap.ic_back);
        mGroupViewSex = (RadioGroup)findViewById(R.id.rg_sex_choice);
        mRBtnMale = (RadioButton)findViewById(R.id.rb_male);
        mRBtnFemale = (RadioButton)findViewById(R.id.rb_female);
        regist = getIntent().getBooleanExtra("regist",false);
        if (!TextUtils.isEmpty(Session.getSession().GenderJob)) {
            if(Session.getSession().GenderJob.equals(getString(R.string.str_direct_coach))) {
                mRBtnMale.setChecked(true);
            }
            else  {
                mRBtnFemale.setChecked(true);
            }
        }
        mBtnFinish = (Button)findViewById(R.id.btn_submit);
        mBtnFinish.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                int checkedId = mGroupViewSex.getCheckedRadioButtonId();
                if (checkedId <= 0) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_job_category);
                    return;
                }
                String gender = ((RadioButton)findViewById(checkedId)).getText().toString();

                if(regist){
//
                    Intent i = new Intent(JobCategory.this,UploadCoachInfoActivity.class);
                    i.putExtra("bean",gender);
                    setResult(3, i);
                    finish();
                    return;
                }
                if (gender.equals(Session.getSession().GenderJob)) {
                    finish();
                    return;
                }

                updateRequest(gender);
                break;
        }
    }

    private void updateRequest(final String gender) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.GenderJob = gender;
        Session.getSession().updateRequest(this, params);
    }

}
