package com.blackcat.coach.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.models.CoachCourseVO;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.User;
import com.blackcat.coach.models.params.MonthApplyData;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.ScrollTimeLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class StudentAppointmentAct extends BaseActivity implements View.OnClickListener {

    private ScrollTimeLayout scrollTimeLayout;

    protected Type mType = null;

    private Type mApplyType;

    /**
     * 请假的日期
     */
    private int[] leaveoff;
    /**
     * 有订单的日期
     */
    private int[] reservationapply;
    private float aspect = 180f / 112f;


    /*日历*/
    private int mYear;
    private int mMonth;
    private ExpCalendarView expCalendarView;
    private SimpleDateFormat formatter;

    private Button addStudent;
    private Button commit;
    private RelativeLayout studentInfo;
    private ImageView studentSelected;
    private ImageView studentImg;
    private TextView studentName;
    private TextView studentClass;
    private TextView studentContent;

    private User userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_appointment);
        configToolBar(R.mipmap.ic_back);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        initView();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        //获取当月的信息
        obtainMonthApplyData(today.get(Calendar.YEAR) + "", (today.get(Calendar.MONTH) + 1) + "");


    }

    private void initView() {
        //日历
        //      Get instance.
        expCalendarView = ((ExpCalendarView) findViewById(R.id.calendar_exp));
//        YearMonthTv = (TextView) rootView.findViewById(R.id.main_YYMM_Tv);
//        YearMonthTv.setText(Calendar.getInstance().get(Calendar.YEAR) + "年" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "月");

        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
//        mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));
        imageInit();

//      Set up listeners.
        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {
//                YearMonthTv.setText(String.format("%d年%d月", year, month));
                if (!(mYear == year && mMonth == month)) {
                    mMonth = month;
                    mYear = year;
//                    mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));
//                    obtainMonthApplyData(mYear + "", mMonth + "");
                }
                LogUtil.print(String.format("%d年%d月", year, month));

            }

            @Override
            public void onMonthScroll(float positionOffset) {
//                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });

        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnDateClickListener(new OnExpDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                super.onDateClick(view, date);
                String str = formatter.format(date.getDate());
                LogUtil.print("formatter" + str);
                requestTime(str);
//                if (!mCurrentDate.equals(str)) {
//                    mCurrentDate = str;
//                    mAdapter.setList(null);
//                    mAdapter.notifyDataSetChanged();
//                    mPage = 1;
//                    if (!Session.isUserInfoEmpty()) {
//                        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
//                        refresh(DicCode.RefreshType.R_INIT, mURI);
//                    }
//                }
            }
        });


        //预约列表
        scrollTimeLayout = (ScrollTimeLayout) findViewById(R.id.appointment_student_time);
        scrollTimeLayout.setColumn(4);
        scrollTimeLayout.setType(1);
//        scrollTimeLayout.getSelectCourseList();
        scrollTimeLayout.setOnTimeLayoutSelectedListener(new ScrollTimeLayout.OnTimeLayoutSelectedListener() {

            @Override
            public void TimeLayoutSelectedListener(CoachCourseVO coachCourseVO, boolean selected) {
                if (selected) {//选中该项 ，发起请求
//                    .getCoursetime().getTimeid();
                    requestDetail(coachCourseVO.get_id());
                }
            }
        });
        mType = new TypeToken<Result<List<CoachCourseVO>>>() {
        }.getType();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        LogUtil.print("+++" + formatter.format(new Date()));
        requestTime(formatter.format(new Date()));

        //添加学员
        addStudent = (Button) findViewById(R.id.add_apply_student);
        studentInfo = (RelativeLayout) findViewById(R.id.rl_selected_student);

        studentSelected = (ImageView) findViewById(R.id.iv_selected_student);
        studentImg = (ImageView) findViewById(R.id.iv_avatar);
        studentName = (TextView) findViewById(R.id.tv_name);
        studentClass = (TextView) findViewById(R.id.tv_class);
        studentContent = (TextView) findViewById(R.id.tv_leaning_contents);
        commit = (Button) findViewById(R.id.appointment_student_commit_btn);

        addStudent.setOnClickListener(this);
        studentInfo.setOnClickListener(this);
        commit.setOnClickListener(this);

        addStudent.setVisibility(View.VISIBLE);
        studentInfo.setVisibility(View.GONE);
    }


    private void imageInit() {
        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
        expCalendarView.shrink();

    }

    /**
     * 提交预约
     */
    private void commitAppointment(String time) {
        URI uri = URIUtil.getAppointStudentTime(time);
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
                        LogUtil.print("list--size::" + list.size());
                        scrollTimeLayout.setData(list, aspect);

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

    private void requestDetail(String courseId) {
        URI uri = URIUtil.getScheduleDetail(Session.getSession().coachid, courseId);

    }

    private void obtainMonthApplyData(final String year, final String month) {
        mApplyType = new TypeToken<Result<MonthApplyData>>() {
        }.getType();
        LogUtil.print(Session.getSession().coachid);
        URI uri = URIUtil.getMonthApplyDataUir(Session.getSession().coachid, year, month);
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
//        String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener
        GsonIgnoreCacheHeadersRequest<Result<MonthApplyData>> request = new GsonIgnoreCacheHeadersRequest<Result<MonthApplyData>>(
                Request.Method.GET, url, null, mApplyType, map,
//                url,mType,null,
                new Response.Listener<Result<MonthApplyData>>() {
                    @Override
                    public void onResponse(Result<MonthApplyData> response) {
                        if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                            if (response != null && response.data != null && response.type == Result.RESULT_OK) {
                                //请假的日期
                                leaveoff = response.data.leaveoff;
                                //有订单的日期
                                reservationapply = response.data.reservationapply;
                                ArrayList<Calendar> list = new ArrayList<Calendar>();

                                if (reservationapply != null) {
                                    for (int i = 0; i < reservationapply.length; i++) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Integer.parseInt(year), Integer.parseInt(month), reservationapply[i]);
                                        LogUtil.print(month + "iii" + c.get(Calendar.MONTH));
                                        list.add(c);
                                    }
                                    expCalendarView.setPointDisplay(list);
                                }

                            }

                        } else {
                            if (!TextUtils.isEmpty(response.msg)) {
                                ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                            }
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


    /**
     * 获取某一日期的预约情况
     */
    private void requestTime(String time) {
        URI uri = URIUtil.getAppointStudentTime(time);
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
                        LogUtil.print("list--size::" + list.size());
                        scrollTimeLayout.clearData();
                        scrollTimeLayout.setData(list, aspect);

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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_selected_student:
            case R.id.add_apply_student:
                Intent intent = new Intent(this, OrderStudentActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.appointment_student_commit_btn:

                break;
            default:
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                addStudent.setVisibility(View.GONE);
                studentInfo.setVisibility(View.VISIBLE);
                userInfo = (User) data.getSerializableExtra("student");
                if(userInfo!=null){
                    LogUtil.print("userInfo");
                    studentName.setText(userInfo.name);
                    studentClass.setText(userInfo.subjectprocess);
                    studentContent.setText(userInfo.leaningContents==null?"":userInfo.leaningContents);
                }
                break;
            default:
                break;
        }
    }


}
