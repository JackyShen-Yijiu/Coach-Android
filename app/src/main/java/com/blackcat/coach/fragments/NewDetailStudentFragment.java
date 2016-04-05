package com.blackcat.coach.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.AboutActivity;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.activities.NewDetailStudentAct;
import com.blackcat.coach.activities.SendCommentActivity;
import com.blackcat.coach.activities.TermsActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.events.UpdateCoachInfoError;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Comment;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.StudentDetailVo;
import com.blackcat.coach.models.Subject;
import com.blackcat.coach.models.Subjectone;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.PullToRefreshView;
import com.blackcat.coach.widgets.SelectableRoundedImageView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zou on 16/3/30.
 */
public class NewDetailStudentFragment extends BaseListFragment<Comment> implements View.OnClickListener {
    private User mUser;
    private TextView mTvCarModel, mTvProgress, mTvPlace;
    private SelectableRoundedImageView mIvImage;
    private TextView mTvContent,mTvguiding,mTvwancheng,mTvgoumai,mTvyixue;
    private ImageView iv_chat;
    private ImageView iv_phone;
    private LinearLayout ll_title_iv;
    private TextView exam_number,exam_pass,exam_grade;
    private TextView exam_number_2,exam_pass_2,exam_grade_2;
    private TextView exam_number_3,exam_pass_3,exam_grade_3;
    private TextView exam_number_4,exam_pass_4,exam_grade_4;
    private RelativeLayout fresh_header;


    public static NewDetailStudentFragment newInstance(User user) {
        NewDetailStudentFragment fragment = new NewDetailStudentFragment();
        fragment.mUser = user;
        return fragment;
    }

    public NewDetailStudentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<Comment>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.fragment_detail_student, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_COMMENT, R.layout.new_student_detail_header);



        mPage = 1;
        if (mUser!=null) {
            fetchUserInfo(mUser._id);
            mURI = URIUtil.getCommentList(mUser._id,mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }else{
            fetchUserInfo("56e95ace17a0206355ec380b");
            mURI = URIUtil.getCommentList("56e95ace17a0206355ec380b",mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        return rootView;
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater, int adapterType, int headerLayoutRes) {
        super.initViews(rootView, inflater, adapterType, headerLayoutRes);



        mTvCarModel = (TextView) mHeaderView.findViewById(R.id.tv_car_type);
        mTvProgress = (TextView) mHeaderView.findViewById(R.id.tv_subject_progress);
        mTvContent = (TextView) mHeaderView.findViewById(R.id.tv_subject_content);

        mTvPlace = (TextView) mHeaderView.findViewById(R.id.tv_place);

        mIvImage = (SelectableRoundedImageView) mHeaderView.findViewById(R.id.iv_image);
        mIvImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvImage.setImageResource(R.mipmap.ic_avatar_small);
        mIvImage.setOval(true);
        //课时
        mTvguiding = (TextView) mHeaderView.findViewById(R.id.guiding);
        mTvwancheng = (TextView) mHeaderView.findViewById(R.id.wancheng);
        mTvgoumai = (TextView) mHeaderView.findViewById(R.id.goumai);
        mTvyixue = (TextView) mHeaderView.findViewById(R.id.yixue);

        iv_chat=(ImageView)mHeaderView.findViewById(R.id.iv_chat);
        iv_phone=(ImageView)mHeaderView.findViewById(R.id.iv_phone);
        iv_chat.setOnClickListener(this);
        iv_phone.setOnClickListener(this);
//图片
        ll_title_iv=(LinearLayout)mHeaderView.findViewById(R.id.ll_title_iv);
//        bindStudentInfo();

//考试信息
        exam_grade=(TextView)mHeaderView.findViewById(R.id.exam_grade);
        exam_number=(TextView)mHeaderView.findViewById(R.id.exam_number);
        exam_pass=(TextView)mHeaderView.findViewById(R.id.exam_pass);

        exam_grade_2=(TextView)mHeaderView.findViewById(R.id.exam_grade_2);
        exam_number_2=(TextView)mHeaderView.findViewById(R.id.exam_number_2);
        exam_pass_2=(TextView)mHeaderView.findViewById(R.id.exam_pass_2);

        exam_grade_3=(TextView)mHeaderView.findViewById(R.id.exam_grade_3);
        exam_number_3=(TextView)mHeaderView.findViewById(R.id.exam_number_3);
        exam_pass_3=(TextView)mHeaderView.findViewById(R.id.exam_pass_3);

        exam_grade_4=(TextView)mHeaderView.findViewById(R.id.exam_grade_4);
        exam_number_4=(TextView)mHeaderView.findViewById(R.id.exam_number_4);
        exam_pass_4=(TextView)mHeaderView.findViewById(R.id.exam_pass_4);
    }

    private void bindStudentInfo() {
        Resources res = getResources();



        if (mUser.carmodel!=null && mUser.carmodel.name!= null) {
            mTvCarModel.setText(mUser.carmodel.name);
        }
        if (!TextUtils.isEmpty(mUser.subject.name)) {
            mTvProgress.setText(mUser.subject.name);
        }

        if (!TextUtils.isEmpty(mUser.address)) {
            mTvPlace.setText(mUser.address);
        }
        if (mUser.headportrait!= null && !TextUtils.isEmpty(mUser.headportrait.originalpic)) {
            //TODO
            UILHelper.loadImage(mIvImage, mUser.headportrait.originalpic, false, R.mipmap.ic_avatar_large);
        } else {
        }
        //课时
        if (getCurrentSubject(mUser.subject)!=null ){
            mTvContent.setText(getCurrentSubject(mUser.subject).progress);

            mTvguiding.setText("规定："+getCurrentSubject(mUser.subject).officialfinishhours+"课时");
            mTvwancheng.setText("完成："+getCurrentSubject(mUser.subject).finishcourse+"课时");
            mTvgoumai.setText("购买："+getCurrentSubject(mUser.subject).officialhours+"课时");
            mTvyixue.setText("已学："+getCurrentSubject(mUser.subject).totalcourse+"课时");
        }


        //进度图片
        if (mUser.subject.subjectid==1){
            ll_title_iv.setBackgroundResource(R.mipmap.progress_bar0);
        }else if (mUser.subject.subjectid==2){
            ll_title_iv.setBackgroundResource(R.mipmap.progress_bar1);
        }else if (mUser.subject.subjectid==3){
            ll_title_iv.setBackgroundResource(R.mipmap.progress_bar2);
        }else if (mUser.subject.subjectid==4){
            ll_title_iv.setBackgroundResource(R.mipmap.progress_bar3);
        }
        //考试信息
        exam_number.setText(mUser.examinationinfo.subjectone.testcount+"次");
        exam_grade.setText(mUser.examinationinfo.subjectone.score+"分");
        exam_pass.setText(mUser.examinationinfo.subjectone.examinationresultdesc+"");

        exam_number_2.setText(mUser.examinationinfo.subjecttwo.testcount+"次");
        exam_grade_2.setText(mUser.examinationinfo.subjecttwo.score+"分");
        exam_pass_2.setText(mUser.examinationinfo.subjecttwo.examinationresultdesc+"");

        exam_number_3.setText(mUser.examinationinfo.subjectthree.testcount+"次");
        exam_grade_3.setText(mUser.examinationinfo.subjectthree.score+"分");
        exam_pass_3.setText(mUser.examinationinfo.subjectthree.examinationresultdesc+"");

        exam_number_4.setText(mUser.examinationinfo.subjectfour.testcount+"次");
        exam_grade_4.setText(mUser.examinationinfo.subjectfour.score+"分");
        exam_pass_4.setText(mUser.examinationinfo.subjectfour.examinationresultdesc+"");
    }

    public Subjectone getCurrentSubject(Subject subject){
        switch(subject.subjectid){
            case 1:
                return mUser.subject1;
            case 2:
                return mUser.subjecttwo;
            case 3:
                return mUser.subjectthree;
            case 4:
                return mUser.subject4;
        }
        return null;
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        mURI = URIUtil.getCommentList(mUser._id, mPage);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getCommentList(mUser._id, mPage);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }

    private Type mTokenType = new TypeToken<Result<StudentDetailVo>>() {}.getType();

    private void fetchUserInfo(final String userId) {
        URI uri = URIUtil.getStudentInfo(userId);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result<StudentDetailVo>> request = new GsonIgnoreCacheHeadersRequest<Result<StudentDetailVo>>(
                Request.Method.GET, url, null, mTokenType, map,
                new Response.Listener<Result<StudentDetailVo>>() {
                    @Override
                    public void onResponse(Result<StudentDetailVo> response) {
                        if (response != null && response.data != null) {
                            StudentDetailVo vo = response.data;
                            mUser = vo.studentinfo;

                            bindStudentInfo();
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(mActivity).add(request);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.iv_phone:
                if (!TextUtils.isEmpty(mUser.mobile)) {
                    BaseUtils.callSomebody(getActivity(), mUser.mobile);
                }
                break;
            case R.id.iv_chat:
                LogUtil.print("213123");
                ((NewDetailStudentAct)getActivity()).intoChat();
                break;

        }
    }

    /**
     * 该学员报考
     * @param userId
     */
    private void BaoKao(String userId){
        Type type = new TypeToken<Result>() {}.getType();
        URI uri = URIUtil.getremindexam();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        RemindExam params = new RemindExam();
        params.coachid = String.valueOf(Session.getSession().coachid);
        params._id = userId;

        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
        //？
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), type, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null) {
                            if (response.type == Result.RESULT_OK) {

                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
//                                Session.getSession().updateCoachInfo(params);
//                                Session.save(Session.getSession(), true);
//                                EventBus.getDefault().post(new UpdateCoachInfoOk());
//                                activity.finish();
                            } else if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                                EventBus.getDefault().post(new UpdateCoachInfoError());
                            }
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                            EventBus.getDefault().post(new UpdateCoachInfoError());
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
                        EventBus.getDefault().post(new UpdateCoachInfoError());
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(mActivity).add(request);
    }

    class RemindExam{
        String coachid;
        String _id;
    }
}
