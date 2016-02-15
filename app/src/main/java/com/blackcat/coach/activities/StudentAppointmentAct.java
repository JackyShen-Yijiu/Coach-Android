package com.blackcat.coach.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.models.CoachCourseVO;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.ScrollTimeLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAppointmentAct extends BaseActivity {

    private ScrollTimeLayout scrollTimeLayout;

    protected Type mType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_appointment);

        initView();
    }

    private void initView() {
        scrollTimeLayout = (ScrollTimeLayout) findViewById(R.id.appointment_student_time);
        scrollTimeLayout.setColumn(4);
        mType = new TypeToken<Result<List<CoachCourseVO>>>(){}.getType();
        requestTime("2016-2-15");
    }

    /**
     * 获取某一日期的预约情况
     */
    private void requestTime(String time){
            URI uri =  URIUtil.getAppointStudentTime(time);
            String url = null;
            if (uri != null) {
                try {
                    url = uri.toURL().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (TextUtils.isEmpty(url)) {
                return;
            }

            Map map = new HashMap<>();
            map.put(NetConstants.KEY_AUTHORIZATION, Session.getToken());
//        map.put(NetConstants.STUDENT_TYPE, type+"");

            GsonIgnoreCacheHeadersRequest<Result<List<CoachCourseVO>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<CoachCourseVO>>>(
                    url, mType, map,
                    new Response.Listener<Result<List<CoachCourseVO>>>() {
                        @Override
                        public void onResponse(Result<List<CoachCourseVO>> response) {
                            List<CoachCourseVO> list = response.data;
                            LogUtil.print("list--size::"+list.size());
                            scrollTimeLayout.setData(list,1f);
//                            onFeedsResponse(response, refreshType);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError arg0) {
//                            onFeedsErrorResponse(arg0, refreshType);
                        }
                    });
            // 请求加上Tag,用于取消请求
            request.setTag(this);
//            if (refreshType == DicCode.RefreshType.R_PULL_DOWN) {
//                request.setManuallyRefresh(true);
//            } else if (refreshType == DicCode.RefreshType.R_PULL_UP) {
//                request.setShouldCache(false);
//            }

            VolleyUtil.getQueue(this).add(request);
//        }

    }


}
