package com.blackcat.coach.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.fragments.ItemFragment;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.HandleClassParams;
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

public class CancelClassActivity extends BaseActivity implements
        View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    RadioGroup mRadioGroup;
    EditText mEtCancelIntro;
    Button  mBtnSubmit;
    Reservation mReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReservation = (Reservation) getIntent().getSerializableExtra(Constants.DATA);
        if (mReservation == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_cancel_class);
        configToolBar(R.mipmap.ic_back);
        initView();
    }

    private  void initView() {
        mRadioGroup = (RadioGroup)findViewById(R.id.rg_cancel_reason);
        mEtCancelIntro = (EditText)findViewById(R.id.et_cancel_intro);
        mBtnSubmit = (Button)findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id) {
            case R.id.btn_submit:
                CoachInfo coachInfo = Session.getSession();
                int checkedId = mRadioGroup.getCheckedRadioButtonId();
                String cancelreason = ((RadioButton)findViewById(checkedId)).getText().toString().trim();
                String cancelcontent = ((EditText)findViewById(R.id.et_cancel_intro)).getText().toString().trim();

                cancelClassRequest(coachInfo.coachid, mReservation._id,
                        Constants.HANDLE_TYPE_CANCEL, cancelreason, cancelcontent);
                break;
            default:
                break;
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId){

    }

    private Type mTokenType = new TypeToken<Result>(){}.getType();

    private void cancelClassRequest(String coachid, String reservationid, int handletype,
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
            map.put("authorization", Session.getToken());

            GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                    Request.Method.POST, url, GsonUtils.toJson(params), mTokenType, map,
                    new Response.Listener<Result>() {
                        @Override
                        public void onResponse(Result response) {
                            String msg = response.msg;
                            if (response != null && response.type == Result.RESULT_OK) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.str_cancel_ok);
                                ReservationOpOk event = new ReservationOpOk();
                                event.pos = mReservation.pos;
                                event.status = ReservationStatus.APPLYREFUSE;
                                EventBus.getDefault().post(event);
                                //新订单 刷新
                                ItemFragment.REFRESH0 = true;
                                //已取消 刷新
                                ItemFragment.REFRESH2 = true;
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
}
