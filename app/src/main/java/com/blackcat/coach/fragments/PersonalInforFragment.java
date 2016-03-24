package com.blackcat.coach.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.BindPhoneActivity;
import com.blackcat.coach.activities.DrivingLicenseActivity;
import com.blackcat.coach.activities.JobCategory;
import com.blackcat.coach.activities.ModifyCoachNameAct;
import com.blackcat.coach.activities.ModifyCoachNumberActivity;
import com.blackcat.coach.activities.ModifyGenderActivity;
import com.blackcat.coach.activities.ModifyIdCardActivity;
import com.blackcat.coach.activities.ModifySeniorityActivity;
import com.blackcat.coach.activities.PersionLableAct;
import com.blackcat.coach.activities.SelfIntroducationActivity;
import com.blackcat.coach.activities.TrainFieldActivity;
import com.blackcat.coach.activities.TrainingSubjectActivity;
import com.blackcat.coach.activities.WorkTimeActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.dialogs.AsyncProgressDialog;
import com.blackcat.coach.dialogs.BaseDialogWrapper;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Comment;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.LabelBean;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.qiniu.PhotoUtil;
import com.blackcat.coach.qiniu.QiniuUploadManager;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.widgets.WordWrapView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * 个人信息  增加学员评价功能
 * Created by zou on 15/10/31.
 */
public class PersonalInforFragment extends BaseListFragment<Comment> implements View.OnClickListener {

    private TextView mTvIdCard, mTvDriverLicense,mTvSenioritymTvPhone,mTvSeniority,mTvPhone,mTvSeniorityId,mTvSex,mTvIntroduction,mTvName,mTvId;
    private ImageView mIvAvatar;

    private Button btnBaoKao;

    private WordWrapView  wordWrapView;
    private TextView tv_job_category;
    private TextView mTvFieldName,mWorkTime,mSubject,mClass;

    public static PersonalInforFragment newInstance() {
        PersonalInforFragment fragment = new PersonalInforFragment();
//        fragment.mUser = user;
        return fragment;
    }

    public PersonalInforFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_personal_infor, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_COMMMENT_TEACHER, R.layout.activity_personal_info);
//        fetchUserInfo(mUser._id);
        mPage = 1;
        mType = new TypeToken<Result<List<Comment>>>(){}.getType();
//        if (Session.getSession().coachid!=null) {
//            mURI = URIUtil.getCommentPersonal(Session.getSession().coachid, mPage);
//            refresh(DicCode.RefreshType.R_INIT, mURI);
//        }
        return rootView;
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater, int adapterType, int headerLayoutRes) {
        super.initViews(rootView, inflater, adapterType, headerLayoutRes);
        mHeaderView.findViewById(R.id.rl_name).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_avatar).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_id_card).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_modify_phone).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_driving_license).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_seniority).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_coach_num).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_gender).setOnClickListener(this);
        mHeaderView.findViewById(R.id.rl_self_intro).setOnClickListener(this);
        mHeaderView.findViewById(R.id.ic_avatar).setOnClickListener(this);
        mTvIdCard = (TextView) mHeaderView.findViewById(R.id.tv_id_card);
        mTvPhone = (TextView)mHeaderView.findViewById(R.id.tv_phone);
        mTvDriverLicense = (TextView)mHeaderView.findViewById(R.id.tv_driver_license);
        mTvSeniority = (TextView)mHeaderView.findViewById(R.id.tv_seniority);
        mTvSeniorityId = (TextView)mHeaderView.findViewById(R.id.tv_coach_num);
        mTvSex = (TextView)mHeaderView.findViewById(R.id.tv_sex);
        mTvSex.setOnClickListener(this);
        mTvIntroduction = (TextView)mHeaderView.findViewById(R.id.tv_self);
        wordWrapView = (WordWrapView) mHeaderView.findViewById(R.id.view_wordwrap);
        //白色
        wordWrapView.setFirstColor(true);
        wordWrapView.showColor(true);
        wordWrapView.setOnClickListener(this);
        mTvName = (TextView) mHeaderView.findViewById(R.id.tv_name);
        mTvId = (TextView) mHeaderView.findViewById(R.id.tv_id);
        mTvName.setText(Session.getSession().name);
        mTvId.setText(Session.getSession().displaycoachid);
        //新加、
        mTvFieldName = (TextView) rootView.findViewById(R.id.tv_field_name);
        mWorkTime = (TextView) rootView.findViewById(R.id.tv_work_time);
        mSubject = (TextView) rootView.findViewById(R.id.tv_subjects);
        mClass = (TextView) rootView.findViewById(R.id.tv_class);

        RelativeLayout job_category = (RelativeLayout) rootView.findViewById(R.id.rl_job_category);
        job_category.setOnClickListener(this);
        tv_job_category=(TextView)rootView.findViewById(R.id.tv_job_category);
        tv_job_category.setOnClickListener(this);
        RelativeLayout workTime = (RelativeLayout) rootView.findViewById(R.id.rl_work_time);
        workTime.setOnClickListener(this);
        RelativeLayout techClass = (RelativeLayout) rootView.findViewById(R.id.rl_tech_subject);
        techClass.setOnClickListener(this);

        RelativeLayout trainfield = (RelativeLayout) rootView.findViewById(R.id.rl_train_field);
        trainfield.setOnClickListener(this);

        mIvAvatar = (ImageView) mHeaderView.findViewById(R.id.ic_avatar);
        if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
//            PicassoUtil.loadImage(this, mIvAvatar, Session.getSession().headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
            UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
        }
        bindViewInfo();
    }



    @Override
    public void onRefresh() {
//        mPage = 1;
//        mURI = URIUtil.getCommentPersonal(Session.getSession().coachid, mPage);
//        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
//        mPage++;
//        mURI = URIUtil.getCommentPersonal(Session.getSession().coachid, mPage);
//        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }

    private Type mTokenType = new TypeToken<Result<Comment>>() {}.getType();


    public void bindViewInfo() {
        if (!TextUtils.isEmpty(Session.getSession().name))//用户名
            mTvName.setText(Session.getSession().name);
        if (!TextUtils.isEmpty(Session.getSession().idcardnumber)) {
            mTvIdCard.setText(Session.getSession().idcardnumber);
        }
        if (!TextUtils.isEmpty(Session.getSession().mobile)){
            mTvPhone.setText(Session.getSession().mobile);
        }
        if (!TextUtils.isEmpty(Session.getSession().drivinglicensenumber)) {
            mTvDriverLicense.setText(Session.getSession().drivinglicensenumber);
        }
        if (!TextUtils.isEmpty(Session.getSession().Seniority)) {
            mTvSeniority.setText(Session.getSession().Seniority);
        }
        if (!TextUtils.isEmpty(Session.getSession().coachnumber)) {
            mTvSeniorityId.setText(Session.getSession().coachnumber);
        }

        if (!TextUtils.isEmpty(Session.getSession().Gender)) {
            mTvSex.setText(Session.getSession().Gender);
        }

        if (!TextUtils.isEmpty(Session.getSession().introduction)) {
            mTvIntroduction.setText(Session.getSession().introduction);
        }
        if (Session.getSession().trainfieldlinfo != null && !TextUtils.isEmpty(Session.getSession().trainfieldlinfo.name)) {
            mTvFieldName.setText(Session.getSession().trainfieldlinfo.name);
        }
       // 工作性质
        if (!TextUtils.isEmpty(Session.getSession().GenderJob)) {
            tv_job_category.setText(Session.getSession().GenderJob);
        }else{
            tv_job_category.setText(R.string.str_direct_coach);}


        if(Session.getSession().subject.size()>0){//可授科目
            if(Session.getSession().subject.size()==1){
                mSubject.setText(Session.getSession().subject.get(0).name);
            }else
                mSubject.setText("已设置");
        }else{
            mSubject.setText("");
        }
        if(Session.getSession().workweek.length>0){//工作时间

            mWorkTime.setText(getWorkTime(Session.getSession().workweek,Session.getSession().worktimespace.begintimeint,
                    Session.getSession().worktimespace.endtimeint));
//            Log.d("tag", "work--time:" + Session.getSession().workweek.getClass());
        }else{
            mWorkTime.setText("");
        }
        Log.d("tag", "Introduction-->" + Session.getSession().introduction);
        addTags();
    }
    private String getTime(int temp){
        if(temp<10)
            return "0"+temp+":00";
        else
            return temp+":00";
    }
    private String[] weeks = {"星期一","星期二","星期三","星期四","星期五","星期六","星期天"};

    private String getWorkTime(int[] time,int start,int end){
        for (int i : time) {
            LogUtil.print("time>>"+i);
        }
        LogUtil.print(time[0] + "<<000--->" + (time.toString()) + "End::-->" + time[time.length - 1]);

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
    /**
     * 设置标签
     */
    private void addTags(){
        if(Session.getSession().tagslist==null)
            return ;
        if(wordWrapView.getChildCount()>0){
            return;
        }
        LabelBean label = new LabelBean();
        label.tagname = "个性标签";
        List<LabelBean> list = new ArrayList<LabelBean>();
        list.add(label);
        for (LabelBean labelBean : Session.getSession().tagslist) {
            list.add(labelBean);
        }
        wordWrapView.setData(list);
        wordWrapView.removeAllViews();
//        String[] strs = {"个性标签","包接送","五星级教练","不吸烟","态度极好","免费提供水服务","不收彩礼"};
        for (int i = 0; i < list.size(); i++) {
            TextView textview = new TextView(getActivity());
            textview.setText(list.get(i).tagname);
            wordWrapView.addView(textview);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_name://姓名
                Intent i = new Intent(getActivity(), ModifyCoachNameAct.class);
//                i.putExtra("name",Session.getSession().name)
                startActivity(i);
                break;
            case R.id.rl_self_intro:
                startActivity(new Intent(getActivity(), SelfIntroducationActivity.class));
                break;
            case R.id.rl_modify_phone:
                startActivity(new Intent(getActivity(), BindPhoneActivity.class));
                break;
            case R.id.rl_avatar:
            case R.id.ic_avatar:
                if (mAvatarDialog == null) {
                    mAvatarDialog = new BaseDialogWrapper(getActivity(), getPhotoDialogView());
                    mAvatarDialog.setFullWidth(true);
                }
                mAvatarDialog.setGravity(Gravity.BOTTOM);
                mAvatarDialog.showDialog();
                break;
            case R.id.tv_take_photo:
                mAvatarDialog.dismissDialog();
                mPhotoFile = PhotoUtil.getPhotoFile(CarCoachApplication.getInstance());
                startActivityForResult(PhotoUtil.getTakePickIntent(mPhotoFile), PhotoUtil.PICTRUE_FROM_CAMERA);
                break;
            case R.id.tv_choose_from_gallery:
                mAvatarDialog.dismissDialog();
                mPhotoFile = PhotoUtil.getPhotoFile(CarCoachApplication.getInstance());
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PhotoUtil.PICTRUE_FROM_GALLERY);
                break;
            case R.id.tv_choose_cancel:
                mAvatarDialog.dismissDialog();
                break;
            case R.id.rl_id_card:
                startActivity(new Intent(getActivity(), ModifyIdCardActivity.class));
                break;
            case R.id.rl_driving_license:
                startActivity(new Intent(getActivity(),DrivingLicenseActivity.class));
                break;
            case R.id.rl_seniority:
                startActivity(new Intent(getActivity(), ModifySeniorityActivity.class));
                break;
            case R.id.rl_coach_num:
                startActivity(new Intent(getActivity(), ModifyCoachNumberActivity.class));
                break;
            case R.id.rl_gender:
                startActivity(new Intent(getActivity(), ModifyGenderActivity.class));
                break;
            case R.id.view_wordwrap:// 标签页面
                startActivity(new Intent(getActivity(),PersionLableAct.class));
                break;
            case R.id.rl_job_category://工作性质
                startActivity(new Intent(mActivity, JobCategory.class));
                break;
            case R.id.rl_work_time://工作时间
                Intent i1 = new Intent(mActivity, WorkTimeActivity.class);
//                startActivityForResult(i1,1);
                startActivity(i1);
                break;
            case R.id.rl_tech_subject://可授科目
                startActivity(new Intent(mActivity, TrainingSubjectActivity.class));
                break;
            case R.id.rl_train_field://训练场地
                startActivity(new Intent(mActivity, TrainFieldActivity.class));
                break;
            default:
                break;
        }

    }

    private File mPhotoFile;
    private AsyncProgressDialog mDialog;
    private BaseDialogWrapper mAvatarDialog;

    private View getPhotoDialogView() {
        View dialogView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_photo_picker, null);
        dialogView.findViewById(R.id.tv_choose_cancel).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_take_photo).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_choose_from_gallery).setOnClickListener(this);
        return dialogView;
    }

    private Uri mPhotoUri;


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtil.PICTRUE_FROM_CAMERA:
                // 拍好了照片 应该跳转
                // TODO 判断照片有没有拍照
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    mPhotoUri = Uri.fromFile(mPhotoFile);
                    LogUtil.print("paizhao--->");
                    loadPhoto(mPhotoUri);
                }
                break;
            case PhotoUtil.PICTRUE_FROM_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        mPhotoUri = uri;
                        loadPhoto(mPhotoUri);
                    }
                }
                break;
            default:
                break;
        }
    }



    private void loadPhoto(Uri uri) {
        if (uri == null) {
            return;
        }
        mDialog = new AsyncProgressDialog(getActivity());
        mDialog.show();
        QiniuUploadManager.getInstance().submitImage(getActivity(), mPhotoUri, new QiniuUploadManager.QiniuUploadListener() {

            @Override
            public void onSucess() {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.upload_avatar_ok);
                mDialog.dismiss();
                mDialog = null;
                Session.save(Session.getSession(), true);
                if (Session.getSession().headportrait != null && !TextUtils.isEmpty(Session.getSession().headportrait.originalpic)) {
                    UILHelper.loadImage(mIvAvatar, Session.getSession().headportrait.originalpic, false, R.mipmap.ic_avatar_small);
                }
            }

            @Override
            public void onError() {
                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.upload_avatar_err);
//                mIvAvatar.setImageResource(R.mipmap.ic_avatar_small);
                mDialog.dismiss();
                mDialog = null;
            }
        });

        ImageLoader.getInstance().displayImage(uri.toString(), mIvAvatar);
    }


}
