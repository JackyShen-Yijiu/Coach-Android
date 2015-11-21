package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.coach.R;
import com.blackcat.coach.activities.ClassesSettingsActivity;
import com.blackcat.coach.activities.DrivingSchoolActivity;
import com.blackcat.coach.activities.PersonalInfoActivity;
import com.blackcat.coach.activities.SettingsActivity;
import com.blackcat.coach.activities.StudentsActivity;
import com.blackcat.coach.activities.TrainFieldActivity;
import com.blackcat.coach.activities.TrainingSubjectActivity;
import com.blackcat.coach.activities.VacationActivity;
import com.blackcat.coach.activities.WalletActivity;
import com.blackcat.coach.activities.WorkTimeActivity;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Session;

public class ProfileFragment extends BaseFragment implements OnClickListener {

    private ImageView mIvAvatar;
    private TextView mTvName, mTvNum, mTvSelfDesc, mTvSchoolName, mTvFieldName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container,
                false);
        initViews(rootView);
//        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
    }

    private void initViews(View rootView) {
        mIvAvatar = (ImageView) rootView.findViewById(R.id.iv_avatar);
        mTvName = (TextView) rootView.findViewById(R.id.tv_name);
        mTvNum = (TextView) rootView.findViewById(R.id.tv_num);
        mTvSelfDesc = (TextView) rootView.findViewById(R.id.tv_self_description);
        mTvSchoolName = (TextView) rootView.findViewById(R.id.tv_school_name);
        mTvFieldName = (TextView) rootView.findViewById(R.id.tv_field_name);

        RelativeLayout profileHeader = (RelativeLayout) rootView.findViewById(R.id.rl_profile_header);
        profileHeader.setOnClickListener(this);

        RelativeLayout drivingSchool = (RelativeLayout) rootView.findViewById(R.id.rl_driving_school);
        drivingSchool.setOnClickListener(this);

        RelativeLayout workTime = (RelativeLayout) rootView.findViewById(R.id.rl_work_time);
        workTime.setOnClickListener(this);

        RelativeLayout techClass = (RelativeLayout) rootView.findViewById(R.id.rl_tech_subject);
        techClass.setOnClickListener(this);

        RelativeLayout shuttle = (RelativeLayout) rootView.findViewById(R.id.rl_shuttle);
        shuttle.setOnClickListener(this);

        RelativeLayout vacation = (RelativeLayout) rootView.findViewById(R.id.rl_vacation);
        vacation.setOnClickListener(this);
        RelativeLayout students = (RelativeLayout) rootView.findViewById(R.id.rl_students);
        students.setOnClickListener(this);

        RelativeLayout wallet = (RelativeLayout) rootView.findViewById(R.id.rl_wallet);
        wallet.setOnClickListener(this);

        RelativeLayout setting = (RelativeLayout) rootView.findViewById(R.id.rl_setting);
        setting.setOnClickListener(this);

        rootView.findViewById(R.id.rl_train_field).setOnClickListener(this);
    }

    private void bindUserInfo() {
        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
//            PicassoUtil.loadImage(this, mIvAvatar, Session.getSession().headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
        }
        mTvName.setText(Session.getSession().name);
        mTvNum.setText(Session.getSession().displaycoachid);

        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
            mTvSelfDesc.setText(Session.getSession().introduction);
        }

        if (Session.getSession().driveschoolinfo != null && !TextUtils.isEmpty(Session.getSession().driveschoolinfo.name)) {
            mTvSchoolName.setText(Session.getSession().driveschoolinfo.name);
        }
        if (Session.getSession().trainfieldlinfo != null && !TextUtils.isEmpty(Session.getSession().trainfieldlinfo.name)) {
            mTvFieldName.setText(Session.getSession().trainfieldlinfo.name);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_profile_header:
                startActivity(new Intent(mActivity, PersonalInfoActivity.class));
                break;
            case R.id.rl_driving_school:
                startActivity(new Intent(mActivity, DrivingSchoolActivity.class));
                break;
            case R.id.rl_work_time:
                startActivity(new Intent(mActivity, WorkTimeActivity.class));
                break;
            case R.id.rl_shuttle:
                startActivity(new Intent(mActivity, ClassesSettingsActivity.class));
                break;
            case R.id.rl_vacation:
                startActivity(new Intent(mActivity, VacationActivity.class));
                break;
            case R.id.rl_students:
                startActivity(new Intent(mActivity, StudentsActivity.class));
                break;
            case R.id.rl_setting:
                startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case R.id.rl_wallet:
                startActivity(new Intent(mActivity, WalletActivity.class));
                break;
            case R.id.rl_tech_subject:
                startActivity(new Intent(mActivity, TrainingSubjectActivity.class));
                break;
            case R.id.rl_train_field:
                startActivity(new Intent(mActivity, TrainFieldActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUserInfo();
    }
}
