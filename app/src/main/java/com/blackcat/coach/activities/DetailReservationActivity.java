package com.blackcat.coach.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.easemob.BlackCatHXSDKHelper;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.imgs.UILHelper;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.HandleClassParams;
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
import java.util.Map;

import de.greenrobot.event.EventBus;

public class DetailReservationActivity extends BaseNoFragmentActivity implements View.OnClickListener {
    private Reservation mReservation;
    private ReservationStatus reservationstate;
    private TextView toolbar_title;
    private ImageView mIvAvatar;
    private TextView mTvStudentName, mTvStudentNum;
    private TextView mTvProgress, mTvDate, mTvTrainField, mTvPickPlace,tv_progress_one;
    private Button mBtnSend, mBtnAccept, mBtnRefuse;
    private MapView mMapView;
    private View mBottomView, mApplyOpView;
    private LinearLayout ll_change_reson;
    private TextView tv_reason;
    private TextView tv_ground,tv_style;
    //private HandleClassParams handleClassParams;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DETAIL);
     //   Log.i("TAG11",);
        if (mReservation == null) {
            finish();
            return;
        }
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
//        try{
         SDKInitializer.initialize(getApplicationContext());
//        }catch(java.lang.UnsatisfiedLinkError e){
//            e.printStackTrace();
//        }

        setContentView(R.layout.activity_detail_reservation);
        configToolBar(R.mipmap.ic_back);
        initViews();
        fetchReservationRequest(mReservation._id);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DETAIL);
        if (mReservation == null) {
            finish();
            return;
        }
        fetchReservationRequest(mReservation._id);
    }

    private void initViews() {
        mMapView = (MapView) findViewById(R.id.map_view);
        toolbar_title=(TextView)findViewById(R.id.toolbar_title);
        mBottomView = findViewById(R.id.fl_bottom);
        mApplyOpView = findViewById(R.id.ll_applying);
        tv_style=(TextView)findViewById(R.id.tv_style);
        ll_change_reson=(LinearLayout)findViewById(R.id.ll_change_reson);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvStudentName = (TextView) findViewById(R.id.tv_name);
     //   mTvStudentNum = (TextView) findViewById(R.id.tv_num);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);

        tv_progress_one=(TextView)findViewById(R.id.tv_progress_one);

        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvTrainField = (TextView) findViewById(R.id.tv_train_field);

        //请假原因
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_ground=(TextView)findViewById(R.id.tv_ground);

       mTvPickPlace = (TextView) findViewById(R.id.tv_place);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnRefuse = (Button) findViewById(R.id.btn_refuse);
        mBtnRefuse.setOnClickListener(this);
        mBtnAccept = (Button) findViewById(R.id.btn_accept);
        mBtnAccept.setOnClickListener(this);

        bindReservationInfo();
    }

    private void bindReservationInfo() {
        Resources res = getResources();
        if (mReservation.userid != null) {
            if (!TextUtils.isEmpty(mReservation.userid.name)) {
                mTvStudentName.setText(mReservation.userid.name);
            }
            if (!TextUtils.isEmpty(mReservation.userid.displayuserid)) {
              //  mTvStudentNum.setText(res.getString(R.string.str_student_id, mReservation.userid.displayuserid));
            }
            if (mReservation.userid.headportrait != null && !TextUtils.isEmpty(mReservation.userid.headportrait.originalpic)) {
                //TODO
//                PicassoUtil.loadImage(this, mIvAvatar, mReservation.userid.headportrait.originalpic, R.dimen.avatar_size, R.dimen.avatar_size, false, R.mipmap.ic_avatar_small);
                UILHelper.loadImage(mIvAvatar, mReservation.userid.headportrait.originalpic, false, R.mipmap.ic_avatar_small);
            } else {
                mIvAvatar.setImageResource(R.mipmap.ic_avatar_small);
            }
        } else {
            mIvAvatar.setImageResource(R.mipmap.ic_avatar_small);
        }

        if (!TextUtils.isEmpty(mReservation.shuttleaddress)) {
            mTvTrainField.setText(res.getString(R.string.str_pick_place, mReservation.shuttleaddress));
        }

        if(mReservation.cancelreason !=null){

            tv_reason.setText(mReservation.cancelreason.reason);
            tv_ground.setText(mReservation.cancelreason.cancelcontent);
        }


        if(mReservation.trainfieldlinfo !=null){

            mTvPickPlace.setText(res.getString(R.string.str_train_field,mReservation.trainfieldlinfo.name));
        }



        if (!TextUtils.isEmpty(mReservation.classdatetimedesc)) {
            mTvDate.setText(mReservation.classdatetimedesc);
        }
        if (!TextUtils.isEmpty(mReservation.courseprocessdesc)) {
            mTvProgress.setText(mReservation.courseprocessdesc);
            tv_progress_one.setText(mReservation.courseprocessdesc);
        }

        switch (mReservation.getReservationstate()) {
            case APPLYING:

                mMapView.setVisibility(View.GONE);
                mBtnSend.setVisibility(View.GONE);
                mBtnRefuse.setText("取消");
                toolbar_title.setText("新订单");
                ll_change_reson.setVisibility(View.GONE);

                if ("true".equals(Session.getUserSetting().classremind)) {
                    mBtnAccept.setEnabled(false);
                }
                tv_style.setText(R.string.reservation_applying);

                break;

            case APPLYCANCEL://学生取消

                mMapView.setVisibility(View.GONE);
                toolbar_title.setText("已取消");
                ll_change_reson.setVisibility(View.VISIBLE);
                tv_style.setText(R.string.reservation_applycancel);
                mBtnSend.setVisibility(View.GONE);
            case APPLYREFUSE://教练拒绝或者取消(已取消)
                //已取消
                ll_change_reson.setVisibility(View.VISIBLE);
//                mBottomView.setVisibility(View.INVISIBLE);
                mApplyOpView.setVisibility(View.INVISIBLE);
                mBtnSend.setText(R.string.reservation_canceled);
                mMapView.setVisibility(View.GONE);
                toolbar_title.setText("已取消");
                mBtnSend.setEnabled(false);
                mBtnSend.setVisibility(View.GONE);
                tv_style.setText(R.string.reservation_applyrefuse);
                break;
            case FINISH:
                //已完成
//                mBottomView.setVisibility(View.INVISIBLE);
                ll_change_reson.setVisibility(View.GONE);
                mApplyOpView.setVisibility(View.INVISIBLE);
                mBtnSend.setVisibility(View.GONE);
                toolbar_title.setText("已学完");
                mMapView.setVisibility(View.GONE);
                tv_style.setText(R.string.reservation_finish);
                break;


            case APPLYCONFIRM:
                toolbar_title.setText("新订单");
                mBtnSend.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                ll_change_reson.setVisibility(View.GONE);

                if ("true".equals(Session.getUserSetting().classremind)) {
                    mBtnRefuse.setVisibility(View.VISIBLE);
                    mBtnAccept.setVisibility(View.VISIBLE);
                    //白色接受按钮不可以点击
                    mBtnAccept.setClickable(false);
                    mBtnRefuse.setText("拒绝");
                    mBtnAccept.setBackgroundResource(R.drawable.refuse_btn_bg);
                    mBtnRefuse.setBackgroundColor(getResources().getColor(R.color.blue));
                    mBtnAccept.setTextColor(getResources().getColor(R.color.text_333));
                    mBtnRefuse.setTextColor(getResources().getColor(R.color.white));
                }else{
                    mBtnRefuse.setVisibility(View.GONE);
                    mBtnAccept.setVisibility(View.GONE);
                }
                tv_style.setText(R.string.reservation_confirm);
                break;

            case UNCONFIRMFINISH:
                ll_change_reson.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                mApplyOpView.setVisibility(View.INVISIBLE);
                mBtnSend.setVisibility(View.VISIBLE);
                mBtnSend.setText(R.string.reservation_btn_confirm);
                toolbar_title.setText("已学完");

                tv_style.setText(R.string.reservation_unconfirmfinish);
                break;

            case UNCOMMENTS:
                ll_change_reson.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                mApplyOpView.setVisibility(View.INVISIBLE);
                mBtnSend.setVisibility(View.VISIBLE);
                mBtnSend.setText(R.string.reservation_btn_comment);
                toolbar_title.setText("待评价");

                tv_style.setText(R.string.reservation_uncomments);
                break;

            case UNSINGIN:
                mBtnSend.setVisibility(View.GONE);
                ll_change_reson.setVisibility(View.GONE);
                mBtnRefuse.setVisibility(View.GONE);
                mBtnAccept.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                tv_style.setText(R.string.reservation_leakage_class);
                toolbar_title.setText("已漏课");
                break;
            case SIGNIN:
                tv_style.setText(R.string.reservation_sign_in);
                toolbar_title.setText("已签到");
                mBtnSend.setVisibility(View.GONE);
                ll_change_reson.setVisibility(View.GONE);
                mBtnRefuse.setVisibility(View.GONE);
                mBtnAccept.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                break;
//            case ONCOMMENTED://评论成功(已完成)
//                ll_change_reson.setVisibility(View.GONE);
//                mMapView.setVisibility(View.GONE);
//                mBtnSend.setVisibility(View.VISIBLE);
//                mBtnSend.setText(R.string.reservation_oncomments);
//                mApplyOpView.setVisibility(View.INVISIBLE);
//                toolbar_title.setText("已完成");
//                mBtnSend.setEnabled(false);
//                break;
        }

    }

    private void showBtn(){

    }


    private Type mType = new TypeToken<Result<Reservation>>(){}.getType();
    private void fetchReservationRequest(String reservationId) {
        URI uri = URIUtil.getReservationInfo(reservationId);
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
        GsonIgnoreCacheHeadersRequest<Result<Reservation>> request = new GsonIgnoreCacheHeadersRequest<Result<Reservation>>(
                Request.Method.GET, url, null, mType, map,
                new Response.Listener<Result<Reservation>>() {
                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK) {
                            int pos = mReservation.pos;
                            mReservation = (Reservation) response.data;
                            mReservation.pos = pos;
                            bindReservationInfo();
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
        LogUtil.print("123123"+mReservation._id);
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_send:
                Intent intent = null;
                switch (mReservation.getReservationstate()) {
                    case APPLYCONFIRM:
                        //取消课程
                        intent = new Intent(this, CancelClassActivity.class);
                        intent.putExtra(Constants.DATA, mReservation);
                        startActivity(intent);
                        break;
                    case UNCONFIRMFINISH:
                        if (mReservation.subject != null) {
                            if (mReservation.subject.subjectid == 2 || mReservation.subject.subjectid == 3) {
                                intent = new Intent(this, SendCommentActivity.class);
                                intent.putExtra(Constants.DATA, mReservation);
                                startActivity(intent);
                            }
                        }
                        break;
                    case UNCOMMENTS:
                        intent = new Intent(this, SendCommentActivity.class);
                        intent.putExtra(Constants.DATA, mReservation);
                        startActivity(intent);
                        break;
                }
                break;
            case R.id.btn_accept:
                acceptClassRequest(Session.getSession().coachid, mReservation._id, 3, "", "");
                break;
            case R.id.btn_refuse:
                //拒绝课程
                intent = new Intent(this, RefuseClassActivity.class);
                intent.putExtra(Constants.DATA, mReservation);
                startActivity(intent);
                break;
        }
    }


    private Type mTokenType = new TypeToken<Result>(){}.getType();

    private void acceptClassRequest(String coachid, String reservationid, int handletype,
                                    String cancelreason, String cancelcontent) {
        URI uri = URIUtil.getHandleClass(coachid, reservationid,
                handletype, cancelreason, cancelcontent);
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        HandleClassParams params = new HandleClassParams();
        params.coachid = coachid;
        params.reservationid = reservationid;
        params.cancelreason = cancelreason;
        params.cancelcontent = cancelcontent;
        params.handletype = handletype;





        Map map = new HashMap<>();
        map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());

        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(params), mTokenType, map,
                new Response.Listener<Result>() {
                    @Override
                    public void onResponse(Result response) {
                        String msg = response.msg;
                        if (response != null && response.type == Result.RESULT_OK) {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_accept_ok);
                            ReservationOpOk event = new ReservationOpOk();
                            event.pos = mReservation.pos;
                            event.status = ReservationStatus.APPLYCONFIRM;
                            EventBus.getDefault().post(event);
                            //TODO
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_reservation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_detail_chat) {
            LogUtil.print("OnOptionsItemSelected-->"+BlackCatHXSDKHelper.getInstance().isLogined());
            if (!BlackCatHXSDKHelper.getInstance().isLogined()) {
                return true;
            }
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Constant.MESSAGE_USERID_ATR_KEY, mReservation.userid._id);
            intent.putExtra(Constant.MESSAGE_NAME_ATTR_KEY, mReservation.userid.name);
            if (mReservation.userid.headportrait != null &&
                    !TextUtils.isEmpty( mReservation.userid.headportrait.originalpic)) {
                intent.putExtra(Constant.MESSAGE_AVATAR_ATTR_KEY, mReservation.userid.headportrait.originalpic);
            }
            intent.putExtra(Constant.CHAT_FROM_TYPE, Constant.CHAT_FROM_RESERVATION);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(ReservationOpOk event) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
