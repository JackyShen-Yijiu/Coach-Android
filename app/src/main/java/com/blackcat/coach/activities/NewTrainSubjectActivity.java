package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.UpdateCoachParams;
import com.blackcat.coach.utils.ToastHelper;

public class NewTrainSubjectActivity extends BaseActivity implements View.OnClickListener {

    private RadioGroup mGroupViewSex;
    private RadioButton mRBtnMale;
    private RadioButton mRBtnFemale;
    private Button mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_subject);
        configToolBar(R.mipmap.ic_back);
        mGroupViewSex = (RadioGroup)findViewById(R.id.rg_sex_choice);
        mRBtnMale = (RadioButton)findViewById(R.id.rb_male);
        mRBtnFemale = (RadioButton)findViewById(R.id.rb_female);
        if (!TextUtils.isEmpty(Session.getSession().GenderOne)) {
            if(Session.getSession().GenderOne.equals(getString(R.string.str_two_sbject))) {
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
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_subject_train);
                    return;
                }
                String gender = ((RadioButton)findViewById(checkedId)).getText().toString();
                if (gender.equals(Session.getSession().GenderOne)) {
                    finish();
                    return;
                }
                updateRequest(gender);
                break;
        }
    }

    private void updateRequest(final String gender) {
        UpdateCoachParams params = new UpdateCoachParams(Session.getSession());
        params.GenderOne = gender;
        Session.getSession().updateRequest(this, params);
    }

}
