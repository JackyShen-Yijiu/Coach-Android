package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CoachClassAdapter;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.CoachClass;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CommentParams;
import com.blackcat.coach.models.params.NewParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.ExpandGridView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class SendCommentActivity extends BaseNoFragmentActivity {

    public static final int SUBJECTS_TYPE_ERROR = 0;
    public static final int SUBJECTS_TYPE_TWO = 2;
    public static final int SUBJECTS_TYPE_THREE = 3;
    public static final String SUBJECT_TYPE_ID = "subject_type";

    private Button mBtnCommit;
    private EditText mEtComment;
    private Reservation mReservation;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvProgress;
    private RatingBar mRbOverall;
    private TextView mTvSubjectTitle;
    private int mSubjectType;
    private ExpandGridView mEgvClasses;
    private String[] mSubjectNames;
    private List<CoachClass> mCoachClassList;
    private CoachClassAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DATA);
        if (mReservation == null) {
            finish();
            return;
        }
        mSubjectType = mReservation.subject.subjectid;
        setContentView(R.layout.aaaa_new_teach_content);
        configToolBar(R.mipmap.ic_back);
        initViews();
        initData();
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                String learningContent = makeCheckedContent();
                if (TextUtils.isEmpty(mEtComment.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.comment_empty);
                    return;
                }

               // sendCommentRequest(String.valueOf(mRbOverall.getRating()),mEtComment.getText().toString());
                sendCommentRequest(Session.getSession().coachid,mReservation._id,mEtComment.getText().toString(), String.valueOf(mRbOverall.getRating()),learningContent);
            }
        });
    }
    private String makeCheckedContent() {
        String learningContent = "";
        for(CoachClass coachClass : mCoachClassList) {
            if(coachClass.isChecked()) {
                String name = coachClass.getClassName();
                if(!TextUtils.isEmpty(learningContent)) {
                    learningContent += "," + name;
                }else {
                    learningContent = name;
                }
            }
        }
        return learningContent;
    }


    private void initViews() {
        mEtComment = (EditText) findViewById(R.id.et_comment);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        //修改
        mTvSubjectTitle = (TextView) findViewById(R.id.tv_subject_tile);
        if (mSubjectType == SUBJECTS_TYPE_TWO) {
            mTvSubjectTitle.setText(R.string.str_subject2_title);
        }
        else if (mSubjectType == SUBJECTS_TYPE_THREE) {
            mTvSubjectTitle.setText(R.string.str_subject3_title);
        }
        mEgvClasses = (ExpandGridView)findViewById(R.id.gv_class_list);



        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        if (mReservation.userid != null) {
            mTvName.setText(mReservation.userid.name);
            UILHelper.loadImage(mIvAvatar, mReservation.userid.headportrait.originalpic, false, R.mipmap.ic_reservation_avatar);
        }
        mTvProgress.setText(mReservation.courseprocessdesc);

        mRbOverall = (RatingBar) findViewById(R.id.rb_overall);


    }

    private void initData() {
        if(mSubjectType == SUBJECTS_TYPE_TWO) {
            mSubjectNames = getResources().getStringArray(R.array.subject2);
        }
        else if (mSubjectType == SUBJECTS_TYPE_ERROR) {
            mSubjectNames = getResources().getStringArray(R.array.subject3);
        }

        mCoachClassList = new ArrayList<>();
        for (int i = 0; i < mSubjectNames.length; i++) {
            CoachClass coachClass = new CoachClass(mSubjectNames[i], false);
            mCoachClassList.add(coachClass);
        }

        mAdapter = new CoachClassAdapter(this, mCoachClassList);
        mEgvClasses.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private Type mTokenType = new TypeToken<Result>() {}.getType();

    private void sendCommentRequest(String coachid, String reservationid, String commentcontent, String starlevel, String learningcontent) {
        URI uri = URIUtil.getCoachComment();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        NewParams param = new NewParams();

        param.coachid = coachid;
        param.reservationid = reservationid;
        param.commentcontent = commentcontent;
        param.starlevel = starlevel;
        param.learningcontent = learningcontent;

        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {

                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            ReservationOpOk event = new ReservationOpOk();
                            event.pos = mReservation.pos;
                            event.status = ReservationStatus.FINISH;
                            EventBus.getDefault().post(event);
                            finish();
                        } else if (!TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
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
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }
}
