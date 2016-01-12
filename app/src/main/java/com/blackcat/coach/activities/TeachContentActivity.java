package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CoachClassAdapter;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.CoachClass;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.FinishClassParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
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

public class TeachContentActivity extends BaseActivity implements View.OnClickListener{

    public static final int SUBJECTS_TYPE_ERROR = 0;
    public static final int SUBJECTS_TYPE_TWO = 2;
    public static final int SUBJECTS_TYPE_THREE = 3;
    public static final String SUBJECT_TYPE_ID = "subject_type";

    private Reservation mReservation;
    private int mSubjectType;
    private String[] mSubjectNames;

    private ImageView mIvAvata;
    private TextView mTvName;
    private TextView mTvNum;
    private TextView mTvSubjectTitle;
    private EditText mEtTeachIntro;
    private Button mBtnSubmit;

    private ExpandGridView mEgvClasses;
    private CoachClassAdapter mAdapter;
    private List<CoachClass> mCoachClassList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DATA);
        if (mReservation == null) {
            finish();
            return;
        }
        mSubjectType = mReservation.subject.subjectid;
        setContentView(R.layout.activity_teach_content);
        configToolBar(R.mipmap.ic_back);
        initView();
        initData();
    }

    private void initView() {
        mIvAvata = (ImageView) findViewById(R.id.iv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvNum = (TextView) findViewById(R.id.tv_num);
        mTvSubjectTitle = (TextView) findViewById(R.id.tv_subject_tile);

        if (mSubjectType == SUBJECTS_TYPE_TWO) {
            mTvSubjectTitle.setText(R.string.str_subject2_title);
        }
        else if (mSubjectType == SUBJECTS_TYPE_THREE) {
            mTvSubjectTitle.setText(R.string.str_subject3_title);
        }

        mEgvClasses = (ExpandGridView)findViewById(R.id.gv_class_list);
        mEtTeachIntro = (EditText)findViewById(R.id.et_teach_intro);
        mBtnSubmit = (Button)findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_submit:
                CoachInfo coachInfo = Session.getSession();
                String learningContent = makeCheckedContent();
                String otherContent = mEtTeachIntro.getText().toString().trim();
                if (TextUtils.isEmpty(learningContent)) {
                    learningContent = otherContent;
                }else if (!TextUtils.isEmpty(otherContent)) {
                    learningContent += "," + otherContent;
                }
                finishRequest(coachInfo.coachid, mReservation._id, learningContent);
                break;
            default:
                break;

        }
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

    private Type mTokenType = new TypeToken<Result>(){}.getType();

    private void finishRequest(String coachid, String reservationid, String learningContent) {
        URI uri = URIUtil.getFinishClass();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }


        FinishClassParams params = new FinishClassParams();
        params.coachid = coachid;
        params.reservationid = reservationid;
        params.learningcontent = learningContent;

        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        String msg = response.msg;
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
                            ReservationOpOk event = new ReservationOpOk();
                            event.pos = mReservation.pos;
                            event.status = ReservationStatus.UNCOMMENTS;
                            EventBus.getDefault().post(event);
                            Intent intent = new Intent(TeachContentActivity.this, SendCommentActivity.class);
                            intent.putExtra(Constants.DATA, mReservation);
                            startActivity(intent);
                            finish();
                        } else if (response != null &&!TextUtils.isEmpty(response.msg)) {
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
