package com.blackcat.coach.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.caldroid.CaldroidFragment;

import com.blackcat.coach.events.MonthApplyEvent;
import com.blackcat.coach.events.NewMessageReceiveEvent;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.MonthApplyData;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;

import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
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

import de.greenrobot.event.EventBus;
import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;


/**
 * create an instance of this fragment.
 */
public class ChildScheduleFragment extends BaseListFragment<Reservation> {

    // 基本变量
    private IndexActivity mContext;


    private  SimpleDateFormat formatter;

    private Type mApplyType ;

    /**
     * 请假的日期
     */
    private int[] leaveoff;
    /**
     * 有订单的日期
     */
    private int[] reservationapply;


    private CaldroidFragment caldroidFragment;
    private String mCurrentDate;

    private ExpCalendarView expCalendarView;

    public static ChildScheduleFragment newInstance(String param1, String param2) {
        ChildScheduleFragment fragment = new ChildScheduleFragment();
        return fragment;
    }

    public ChildScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventAsync(MonthApplyEvent event){
        obtainMonthApplyData(mYear + "", mMonth + "");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (IndexActivity) getActivity();
        mContext.setRightTitleWithoutImg(CommonUtil.getString(mContext,R.string.student_appointment));
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_child_schedule, container, false);
//       View view= View.inflate(mContext,R.layout.fragment_child_schedule_head,null);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_SCHEDULE);

        initView(rootView);
        mListView.setOnLoadMoreListener(null);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        mCurrentDate = formatter.format(new Date());// 当期日期
        mType = new TypeToken<Result<List<Reservation>>>() {
        }.getType();
//        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        //获取当月的信息
        obtainMonthApplyData(today.get(Calendar.YEAR) + "", (today.get(Calendar.MONTH)+1) + "");

        return rootView;
    }

    private int mYear;
    private int mMonth;

    private void initView(View rootView) {

        //      Get instance.
        expCalendarView = ((ExpCalendarView) rootView.findViewById(R.id.calendar_exp));
//        YearMonthTv = (TextView) rootView.findViewById(R.id.main_YYMM_Tv);
//        YearMonthTv.setText(Calendar.getInstance().get(Calendar.YEAR) + "年" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "月");

        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));
        imageInit();

//      Set up listeners.
        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {
//                YearMonthTv.setText(String.format("%d年%d月", year, month));
                if(!(mYear == year&&mMonth == month)){
                    mMonth = month;
                    mYear = year;
                    mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));
                    EventBus.getDefault().post(new MonthApplyEvent());
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
                LogUtil.print("formatter"+str);
                if (!mCurrentDate.equals(str)) {
                    mCurrentDate = str;
                    mAdapter.setList(null);
                    mAdapter.notifyDataSetChanged();
                    mPage = 1;
                    if (!Session.isUserInfoEmpty()) {
                        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
                        refresh(DicCode.RefreshType.R_INIT, mURI);
                    }
                }
            }
        });

    }

    private void imageInit() {
        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
        expCalendarView.shrink();

    }
    private void obtainMonthApplyData(final String year, final String month) {
        mApplyType = new TypeToken<Result<MonthApplyData>>(){}.getType();
        LogUtil.print(Session.getSession().coachid);
        URI uri = URIUtil.getMonthApplyDataUir(Session.getSession().coachid,year,month);
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
                                ArrayList<Calendar>  list = new ArrayList<Calendar>();

                                if(reservationapply != null){
                                    for (int i =0 ;i<reservationapply.length;i++){
                                        Calendar c=Calendar.getInstance();
                                        c.set(Integer.parseInt(year),Integer.parseInt(month),reservationapply[i]);
                                        LogUtil.print(month+"iii"+c.get(Calendar.MONTH));
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

        VolleyUtil.getQueue(getActivity()).add(request);
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater, int adapterType, int headerLayoutRes) {
        super.initViews(rootView, inflater, adapterType, headerLayoutRes);
    }


    @Override
    public void onRefresh() {
        mPage = 1;
        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
//        mPage++;
//        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
//        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }

    }
}