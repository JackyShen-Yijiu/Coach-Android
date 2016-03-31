package com.blackcat.coach.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.AddStudentsActivity;
import com.blackcat.coach.activities.ClassesSettingsActivity;
import com.blackcat.coach.activities.MoreActivity;
import com.blackcat.coach.activities.NewDetailStudentAct;
import com.blackcat.coach.activities.SettingsActivity;
import com.blackcat.coach.activities.StudyConfirmsAct;

import com.blackcat.coach.activities.StudentsActivity1;
import com.blackcat.coach.activities.SystemMsgActivity;

import com.blackcat.coach.activities.VacationActivity;
import com.blackcat.coach.activities.WalletActivity;
import com.blackcat.coach.activities.WorkTimeActivity;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.widgets.SelectableRoundedImageView;

public class ProfileFragment extends BaseFragment implements OnClickListener {

    public static boolean CLASS_SETTING = false;

    private SelectableRoundedImageView mIvAvatar;
    private TextView mTvName, mTvSchoolName;
    private TextView mClass;
    private TextView mTvFocde;
    private TextView mWorkTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_fragment_my, container,
                false);
        initViews(rootView);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViews(View rootView) {
        mIvAvatar = (SelectableRoundedImageView) rootView.findViewById(R.id.iv_avatar);
        mIvAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvAvatar.setImageResource(R.mipmap.ic_avatar_small);
        mIvAvatar.setOval(true);


        mTvName = (TextView) rootView.findViewById(R.id.tv_name);
        mTvFocde = (TextView) rootView.findViewById(R.id.tv_focde);
        mTvSchoolName = (TextView) rootView.findViewById(R.id.tv_school_name);
        mClass = (TextView) rootView.findViewById(R.id.tv_class);
        mWorkTime = (TextView) rootView.findViewById(R.id.tv_time);

        RelativeLayout rl_class=(RelativeLayout) rootView.findViewById(R.id.rl_class);
        rl_class.setOnClickListener(this);
        RelativeLayout rl_time = (RelativeLayout) rootView.findViewById(R.id.rl_time);
        rl_time.setOnClickListener(this);

//        RelativeLayout profileHeader = (RelativeLayout) rootView.findViewById(R.id.rl_profile_header);
//        profileHeader.setOnClickListener(this);
        LinearLayout vacation = (LinearLayout) rootView.findViewById(R.id.ll_vacation);
        vacation.setOnClickListener(this);
        LinearLayout wallet = (LinearLayout) rootView.findViewById(R.id.ll_wallet);
        wallet.setOnClickListener(this);
        LinearLayout informations= (LinearLayout) rootView.findViewById(R.id.ll_informations);
        informations.setOnClickListener(this);
        LinearLayout setting = (LinearLayout) rootView.findViewById(R.id.ll_setting);
        setting.setOnClickListener(this);
        rootView.findViewById(R.id.ll_add).setOnClickListener(this);

        LinearLayout score = (LinearLayout) rootView.findViewById(R.id.ll_score);
        score.setOnClickListener(this);

        LinearLayout stat = (LinearLayout) rootView.findViewById(R.id.ll_stat);
        stat.setOnClickListener(this);





    }

    private void bindUserInfo() {
        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
        }
//        mTvName.setText(Session.getSession().name);
        if (TextUtils.isEmpty(Session.getSession().fcode)) {
            mTvFocde.setText("我的Y码:");
        }else {
            mTvFocde.setText("我的Y码:" + Session.getSession().fcode);
        }

        if (Session.getSession().driveschoolinfo != null && !TextUtils.isEmpty(Session.getSession().driveschoolinfo.name)) {
            mTvSchoolName.setText(Session.getSession().driveschoolinfo.name);
        }

        if(CLASS_SETTING)
            mClass.setText("已设置");
        else
            mClass.setText("");

        if(Session.getSession().workweek.length>0){//工作时间

            mWorkTime.setText(getWorkTime(Session.getSession().workweek,Session.getSession().worktimespace.begintimeint,
                    Session.getSession().worktimespace.endtimeint));
        }else{
            mWorkTime.setText("");
        }

    }

    private String getWorkTime(int[] time,int start,int end){
        for (int i : time) {
            LogUtil.print("time>>"+i);
        }
        LogUtil.print(time[0]+"<<000--->"+(time.toString())+"End::-->"+time[time.length-1]);

        if(time[0]+time.length-1 == time[time.length-1]){

            if(time[0]<1)
                return "已设置";
            else if(time.length==1){
                return weeks[time[0]-1]+"\n"+getTime(start)+"-"+getTime(end);
            }
            return weeks[time[0]-1] +"至"+ weeks[time[time.length-1]-1] +"\n"+getTime(start)+"-"+getTime(end) ;
        }
        return "已设置";
    }

    private String getTime(int temp){
        if(temp<10)
            return "0"+temp+":00";
        else
            return temp+":00";
    }

    private String[] weeks = {"星期一","星期二","星期三","星期四","星期五","星期六","星期天"};



    @Override
    public void onClick(View v) {
        //没有审核通过，不是设置
        if(!Session.getSession().is_validation && v.getId() !=R.id.rl_setting){
            ToastHelper.getInstance(getActivity().getApplicationContext()).toast(R.string.coach_not_avlidable);
            return ;
        }

        int id = v.getId();
        switch (id) {
//            case R.id.rl_profile_header:
//                startActivity(new Intent(mActivity, PersonalInfoActivity.class));
//                break;
//            case R.id.rl_job_category:
//                startActivity(new Intent(mActivity, JobCategory.class));
//                break;

//            case R.id.rl_driving_school:
//                startActivity(new Intent(mActivity, DrivingSchoolActivity.class));
//                break;
            case R.id.rl_class:
                startActivity(new Intent(mActivity, ClassesSettingsActivity.class));
                break;
            case R.id.ll_vacation:
                startActivity(new Intent(mActivity, VacationActivity.class));

                break;

            case R.id. ll_informations:
                startActivity(new Intent(mActivity, SystemMsgActivity.class));
                break;
            case R.id.ll_score://学员列表
                startActivity(new Intent(mActivity, MoreActivity.class));//
                break;
            case R.id.ll_setting:
                startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case R.id.ll_wallet:
                startActivity(new Intent(mActivity, WalletActivity.class));
                break;
            case R.id.rl_time:
                startActivity(new Intent(mActivity, WorkTimeActivity.class));
                break;
            case R.id.ll_add://添加
                startActivity(new Intent(getActivity(), StudyConfirmsAct.class));
                break;
            case R.id.ll_stat://统计
                startActivity(new Intent(getActivity(), AddStudentsActivity.class));
                break;

//            case R.id.rl_tech_subject:
//                startActivity(new Intent(mActivity, TrainingSubjectActivity.class));
//                break;
//            case R.id.rl_train_field:
//                startActivity(new Intent(mActivity, TrainFieldActivity.class));
//                break;
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
