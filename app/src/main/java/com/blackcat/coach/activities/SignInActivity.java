package com.blackcat.coach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.easemob.Constant;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.ScanningResult;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.SignInInfo;
import com.blackcat.coach.models.params.NewParams;
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

/**
 * Created by aa on 2016/1/15.
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener{
    private TextView sign_name,sign_adress,sign_class;
    private Button sign_commit;
    ScanningResult scanningResult;
//    private String reservationid="string,预约id";
//    private String codecreateime="String,二维码生成时间戳";
//    private String userid="String,学员id";
//    private String userlatitude="Number,学员纬度";
//    private String userlongitude="Number,学员精度";
//    private String coachlatitude="Number,教练纬度";
//    private String coachlongitude="Number,教练经度";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_main);
        configToolBar(R.mipmap.ic_back);

        scanningResult = (ScanningResult) getIntent().getSerializableExtra(Constant.SCANNING_RESULT);
        initViews();

        initData();

    }


    private void initViews() {

        sign_name=(TextView)findViewById(R.id.sign_name);
        sign_class=(TextView)findViewById(R.id.sign_class);
        sign_adress=(TextView)findViewById(R.id.sign_adress);

        sign_commit=(Button)findViewById(R.id.sign_commit);
        sign_commit.setOnClickListener(this);
    }

    private void initData() {
        sign_name.setText(scanningResult.studentName);
        sign_class.setText(scanningResult.courseProcessDesc);
        sign_adress.setText(scanningResult.locationAddress);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign_commit:
                sendCommentRequest();
//                sendCommentRequest(String.coachid,String.coachlatitude,String.coachlongitude,String codecreatetime,String reservationid,
//                    String userid,String userlatitude,String userlongitude);

    }
}
    private Type mTokenType = new TypeToken<Result>() {}.getType();

    private void sendCommentRequest() {
        URI uri = URIUtil.getSignin();
        String url = null;
        try {
            url = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        SignInInfo param = new SignInInfo();

        param.coachid = Session.getSession().coachid;
        if(CarCoachApplication.latitude!=null&&CarCoachApplication.longitude!=null){

        param.coachlatitude = CarCoachApplication.latitude;
        param.coachlongitude = CarCoachApplication.longitude;
        param.codecreatetime = scanningResult.createTime;
        param.reservationid = scanningResult.reservationId;
        param.userid = scanningResult.studentId;
        param.userlatitude = scanningResult.latitude;
        param.userlongitude = scanningResult.longitude;
        }


      //  Log.i("TAG", GsonUtils.toJson(param) + "coachid-->" + coachid + "reservationid-->" + reservationid + "level-->" + commentcontent + starlevel + learningcontent);

        Map map = new HashMap<>();
        map.put("authorization", Session.getToken());
        GsonIgnoreCacheHeadersRequest<Result> request = new GsonIgnoreCacheHeadersRequest<Result>(
                Request.Method.POST, url, GsonUtils.toJson(param), mTokenType, map,
                new Response.Listener<Result>() {

                    @Override
                    public void onResponse(Result response) {
                        if (response != null && response.type == Result.RESULT_OK&&response.data!=null) {
//                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);
//                            ReservationOpOk event = new ReservationOpOk();
//                            event.pos = mReservation.pos;
//                            event.status = ReservationStatus.FINISH;
//                            EventBus.getDefault().post(event);
                            Intent intent = new Intent(SignInActivity.this, SignInSucceed.class);
                            startActivity(intent);
                            finish();

                        } else if (!TextUtils.isEmpty(response.msg)) {
                            //ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            Intent intent = new Intent(SignInActivity.this, SignInFaild.class);
                            startActivity(intent);
                            finish();
                        }
                        if (Constants.DEBUG) {
                            VolleyLog.v("Response:%n %s", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                     //   ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.net_err);

                        Intent intent = new Intent(SignInActivity.this, SignInSucceed.class);
                        startActivity(intent);
                    }
                });
        // 请求加上Tag,用于取消请求
        request.setTag(this);
        request.setShouldCache(false);

        VolleyUtil.getQueue(this).add(request);
    }
}
