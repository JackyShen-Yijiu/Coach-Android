package com.blackcat.coach.activities;

import android.app.*;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.StudyConfirmAdapter;
import com.blackcat.coach.events.ReservationOpOk;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.ReservationStatus;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 工时确认
 */
public class StudyConfirmsAct extends BaseActivity {

    public static final int SUBJECTS_TYPE_ERROR = 0;
    public static final int SUBJECTS_TYPE_TWO = 2;
    public static final int SUBJECTS_TYPE_THREE = 3;

    private ExpandableListView lv;

    private StudyConfirmAdapter adapter;

    private List<Reservation> list = new ArrayList<Reservation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_studyconfirms);
        configToolBar(R.mipmap.ic_back);
        initView();
        request();
    }

    private void initView() {
        lv = (ExpandableListView) findViewById(R.id.act_study_confirm_expandableListView);
//        lv.setDescendantFocusability();
        adapter = new StudyConfirmAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {


                return false;
            }
        });
    }

    private Type mTokenType = new TypeToken<Result<List<Reservation>>>() {}.getType();

    /**
     * 获取未完成 学员列表
     */
        private void request() {
            URI uri = URIUtil.getStudyConfirm();
            String url = null;
            try {
                url = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(url)) {
                return;
            }

//            ConfirmParams param = new ConfirmParams();
//            param.coachid = Session.getSession().coachid;

//            Toast.makeText(StudyConfirmsAct.this, "request", Toast.LENGTH_SHORT).show();
            Map map = new HashMap<>();
            map.put("authorization", Session.getToken());
            GsonIgnoreCacheHeadersRequest<Result<List<Reservation>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<Reservation>>>(
                    Request.Method.GET, url, GsonUtils.toJson(null), mTokenType, map,
                    new Response.Listener<Result<List<Reservation>>>() {

                        @Override
                        public void onResponse(Result response) {
                            if (response != null && response.type == Result.RESULT_OK) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(R.string.op_ok);

                                list = (List<Reservation>) response.data;
                                adapter.setData(list);

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
    class ConfirmParams{
        String coachid;
    }


    /**
     * 提交数据
     */
    private void requestPost(){

    }


}
