package com.blackcat.coach.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.blackcat.coach.activities.JobCategory;
import com.blackcat.coach.activities.NewTrainSubjectActivity;
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
<<<<<<< HEAD
import com.blackcat.coach.utils.LogUtil;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
=======
>>>>>>> 3f0e3753cf53b45dae3e15537766dadf4593568a

public class ProfileFragment extends BaseFragment implements OnClickListener {

    private ImageView mIvAvatar;
<<<<<<< HEAD
    private TextView mTvName, mTvNum, mTvSelfDesc, mTvSchoolName, mTvFieldName;
    private TextView mWorkTime,mSubject,mClass,mCode;
=======
    private TextView mTvName, mTvNum, mTvSelfDesc, mTvSchoolName, mTvFieldName,tv_job_category;
    private TextView mWorkTime,mSubject,mClass;
>>>>>>> 3f0e3753cf53b45dae3e15537766dadf4593568a

    public static boolean CLASS_SETTING = false;
    private TextView mTvFocde;

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
        mTvFocde = (TextView) rootView.findViewById(R.id.tv_focde);
      //  mTvSelfDesc = (TextView) rootView.findViewById(R.id.tv_self_description);
        mTvSchoolName = (TextView) rootView.findViewById(R.id.tv_school_name);
        mTvFieldName = (TextView) rootView.findViewById(R.id.tv_field_name);

        mCode  = (TextView) rootView.findViewById(R.id.tv_ycode);
        mWorkTime = (TextView) rootView.findViewById(R.id.tv_work_time);
        mSubject = (TextView) rootView.findViewById(R.id.tv_subjects);
        mClass = (TextView) rootView.findViewById(R.id.tv_class);

        RelativeLayout profileHeader = (RelativeLayout) rootView.findViewById(R.id.rl_profile_header);
        profileHeader.setOnClickListener(this);

        RelativeLayout drivingSchool = (RelativeLayout) rootView.findViewById(R.id.rl_driving_school);
        drivingSchool.setOnClickListener(this);

        //工作性质（新加）
        RelativeLayout job_category = (RelativeLayout) rootView.findViewById(R.id.rl_job_category);
        job_category.setOnClickListener(this);

        tv_job_category=(TextView)rootView.findViewById(R.id.tv_job_category);
        tv_job_category.setOnClickListener(this);

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
        mTvNum.setText(Session.getSession().mobile);
        mTvFocde.setText(Session.getSession().fcode);

//        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
//            mTvSelfDesc.setText(Session.getSession().introduction);
//        }

        if (Session.getSession().driveschoolinfo != null && !TextUtils.isEmpty(Session.getSession().driveschoolinfo.name)) {
            mTvSchoolName.setText(Session.getSession().driveschoolinfo.name);
        }
        if (Session.getSession().trainfieldlinfo != null && !TextUtils.isEmpty(Session.getSession().trainfieldlinfo.name)) {
            mTvFieldName.setText(Session.getSession().trainfieldlinfo.name);
        }
        if(Session.getSession().workweek.length>0){//工作时间
            mWorkTime.setText("已设置");
            Log.d("tag", "work--time:" + Session.getSession().workweek.getClass());
        }else{
            mWorkTime.setText("");
        }

//        if(Session.getSession().subject.size()>0){//可授科目
//            mSubject.setText("已设置");
//        }else{
//            mSubject.setText("");
//        }
        //授课


            if (!TextUtils.isEmpty(Session.getSession().GenderOne)) {
                mSubject.setText(Session.getSession().GenderOne);
            }else {
                mSubject.setText("已设置");
            }


        if(CLASS_SETTING)
            mClass.setText("已设置");
        else
            mClass.setText("");

//        工作性质
        if (!TextUtils.isEmpty(Session.getSession().Gender)) {
            tv_job_category.setText(Session.getSession().Gender);
        }


    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_profile_header:
                startActivity(new Intent(mActivity, PersonalInfoActivity.class));
                break;
            case R.id.rl_job_category:
                startActivity(new Intent(mActivity, JobCategory.class));
                break;

            case R.id.rl_driving_school:
                startActivity(new Intent(mActivity, DrivingSchoolActivity.class));
                break;
            case R.id.rl_work_time://工作时间
                Intent i1 = new Intent(mActivity, WorkTimeActivity.class);
//                startActivityForResult(i1,1);
                startActivity(i1);
                break;
            case R.id.rl_shuttle:
                startActivity(new Intent(mActivity, ClassesSettingsActivity.class));
                break;
            case R.id.rl_vacation:
                startActivity(new Intent(mActivity, VacationActivity.class));
                break;
            case R.id.rl_students://学员列表
                startActivity(new Intent(mActivity, StudentsActivity.class));
                break;
            case R.id.rl_setting:
                startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case R.id.rl_wallet:
                startActivity(new Intent(mActivity, WalletActivity.class));
                break;
            case R.id.rl_tech_subject:
                startActivity(new Intent(mActivity, NewTrainSubjectActivity.class));
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
