package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.CommentParams;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class SendCommentActivity extends BaseNoFragmentActivity {

    private Button mBtnCommit;
    private EditText mEtComment;
    private Reservation mReservation;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvProgress;
    private RatingBar mRbOverall, mRbAttitude, mRbAbility, mRbTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DATA);
        if (mReservation == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_send_comment);
        configToolBar(R.mipmap.ic_back);
        initViews();

        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if (TextUtils.isEmpty(mEtComment.getText())) {
                    ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.comment_empty);
                    return;
                }
                sendCommentRequest(String.valueOf(mRbOverall.getRating()), String.valueOf(mRbAttitude.getRating()), String.valueOf(mRbAbility.getRating()), String.valueOf(mRbTime.getRating()), mEtComment.getText().toString());
            }
        });
    }

    private void initViews() {
        mEtComment = (EditText) findViewById(R.id.et_comment);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);

        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        if (mReservation.userid != null) {
            mTvName.setText(mReservation.userid.name);
            UILHelper.loadImage(mIvAvatar, mReservation.userid.headportrait.originalpic, false, R.mipmap.ic_reservation_avatar);
        }
        mTvProgress.setText(mReservation.courseprocessdesc);

        mRbOverall = (RatingBar) findViewById(R.id.rb_overall);
        mRbAbility = (RatingBar) findViewById(R.id.rb_ability);
        mRbAttitude = (RatingBar) findViewById(R.id.rb_attitude);
        mRbTime = (RatingBar) findViewById(R.id.rb_time);
    }

    private Type mTokenType = new TypeToken<Result>() {}.getType();

    private void sendCommentRequest(String starlevel, String attitude, String ability, String time, String content) {
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
        CommentParams param = new CommentParams();
        param.coachid = Session.getSession().coachid;
        param.reservationid = mReservation._id;
        param.commentcontent = content;
        param.abilitylevel = ability;
        param.starlevel = starlevel;
        param.timelevel = time;
        param.attitudelevel = attitude;
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
