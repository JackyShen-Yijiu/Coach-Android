package com.blackcat.coach.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.events.UpdateCoachInfoError;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Comment;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zou on 15/10/31.
 */
public class DetailStudentFragment extends BaseListFragment<Comment> implements View.OnClickListener {
    private User mUser;
    private TextView mStudentName, mStudetnId,mtvStudentPhone;
    private TextView mTvSchoolName, mTvCarModel, mTvProgress, mTvPlace;
    private ImageView mIvImage;

    private Button btnBaoKao;

    public static DetailStudentFragment newInstance(User user) {
        DetailStudentFragment fragment = new DetailStudentFragment();
        fragment.mUser = user;
        return fragment;
    }

    public DetailStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mType = new TypeToken<Result<List<Comment>>>(){}.getType();
        View rootView = inflater.inflate(R.layout.fragment_detail_student, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_COMMENT, R.layout.layout_student_detail_header);
        fetchUserInfo(mUser._id);
        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getCommentList(mUser._id, mPage);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        return rootView;
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater, int adapterType, int headerLayoutRes) {
        super.initViews(rootView, inflater, adapterType, headerLayoutRes);
        mStudentName = (TextView) mHeaderView.findViewById(R.id.student_detail_name);
        mtvStudentPhone = (TextView) mHeaderView.findViewById(R.id.student_detail_phone);
        mStudetnId = (TextView) mHeaderView.findViewById(R.id.tv_id);
        mTvSchoolName = (TextView) mHeaderView.findViewById(R.id.tv_school_name);
        mTvCarModel = (TextView) mHeaderView.findViewById(R.id.tv_car_type);
        mTvProgress = (TextView) mHeaderView.findViewById(R.id.tv_progress);
        mTvPlace = (TextView) mHeaderView.findViewById(R.id.tv_place);
        mIvImage = (ImageView) mHeaderView.findViewById(R.id.iv_image);

//        btnBaoKao = (Button) rootView.findViewById(R.id.student_detail_btn);
//        btnBaoKao.setOnClickListener(this);
//        bindStudentInfo();
    }

    private void bindStudentInfo() {
        if (!TextUtils.isEmpty(mUser.name)) {
            mStudentName.setText(mUser.name);
        }
        Resources res = getResources();
//        if (!TextUtils.isEmpty(mUser.displayuserid)) {
//            mStudetnId.setText(res.getString(R.string.info_id, mUser.displayuserid));
//        }
        if(null!=mUser.mobile)
            mtvStudentPhone.setText(mUser.mobile);
        if (mUser.carmodel != null) {
            mTvCarModel.setText(res.getString(R.string.info_type, mUser.carmodel.code));
        }
        if (mUser.applyschoolinfo != null) {
            mTvSchoolName.setText(res.getString(R.string.info_school_name, mUser.applyschoolinfo.name));
        }
        if (!TextUtils.isEmpty(mUser.subjectprocess)) {
            mTvProgress.setText(mUser.subjectprocess);
        }
        if (!TextUtils.isEmpty(mUser.address)) {
            mTvPlace.setText(res.getString(R.string.info_place, mUser.address));
        }
        if (mUser.headportrait != null && !TextUtils.isEmpty(mUser.headportrait.originalpic)) {
            //TODO
//            PicassoUtil.loadImage(mActivity, mIvImage, mUser.headportrait.originalpic, R.dimen.top_image_w, R.dimen.top_image_h, false, R.mipmap.ic_avatar_large);
            UILHelper.loadImage(mIvImage, mUser.headportrait.originalpic, false, R.mipmap.ic_avatar_large);
        } else {

        }
//        LogUtil.print("leavecourseCount-->"+mUser);
//
        LogUtil.print("leavecourseCount-->"+mUser.subject);
        if(mUser.leavecoursecount.equals("0") && mUser.subject!=null){//报名,显示按钮
            btnBaoKao.setText("可报考"+mUser.subject.name);
            btnBaoKao.setVisibility(View.VISIBLE);
        }else{//隐藏 按钮
            btnBaoKao.setVisibility(View.GONE);
        }

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

    private Type mTokenType = new TypeToken<Result<User>>() {}.getType();

    private void fetchUserInfo(String userId) {
        URI uri = URIUtil.getUserInfo(userId);
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

        GsonIgnoreCacheHeadersRequest<Result<User>> request = new GsonIgnoreCacheHeadersRequest<Result<User>>(
                Request.Method.GET, url, null, mTokenType, map,
                new Response.Listener<Result<User>>() {
                    @Override
                    public void onResponse(Result<User> response) {
                        if (response != null && response.data != null) {
                            mUser = response.data;
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
//            case R.id.student_detail_btn:
//                BaoKao(mUser._id);
//                break;
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
        params.userid = userId;

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
                                btnBaoKao.setVisibility(View.GONE);
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
        String userid;
    }
}
