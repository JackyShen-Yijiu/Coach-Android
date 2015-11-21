package com.blackcat.coach.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.FeedbackParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;

public class FeedbackActivity extends BaseNoFragmentActivity {

    private EditText mEtFeedback;
    private Button mBtnCommit;

    private String mMobileversion;
    private String mNetwork;
    private String mResolution;
    private String mAppversion;
    private String mFeedbackmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        configToolBar(R.mipmap.ic_back);
        mEtFeedback = (EditText) findViewById(R.id.et_feedback);
        mBtnCommit = (Button) findViewById(R.id.btn_submit);
        mMobileversion = Build.MODEL + "/" + Build.VERSION.SDK_INT;
        mNetwork = BaseUtils.isNetTypeMobile(CarCoachApplication.getInstance()) ? "mobile" : "wifi";

        DisplayMetrics metrics = BaseUtils.getDisplayMetrics(CarCoachApplication.getInstance());
        mResolution = metrics.widthPixels + "*" + metrics.heightPixels;
        mAppversion = BaseUtils.getVersionName(CarCoachApplication.getInstance());

        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackmessage = mEtFeedback.getText().toString().trim();
                if (TextUtils.isEmpty(mFeedbackmessage)) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.feedback_empty);
                    return;
                }
                feedbackRequest();
            }
        });
    }

    private Type mType = new TypeToken<Result>() {}.getType();

    private void feedbackRequest() {
        URI uri = URIUtil.getFeedbackURI();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FeedbackParams params = new FeedbackParams();
        params.userid = Session.getSession().coachid;
        params.feedbackmessage = mFeedbackmessage;
        params.mobileversion = mMobileversion;
        params.resolution = mResolution;
        params.appversion = mAppversion;
        params.network = mNetwork;
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mType, null,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.submit_ok);
                            finish();
                        } else if (response != null && !TextUtils.isEmpty(response.msg)) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);
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
