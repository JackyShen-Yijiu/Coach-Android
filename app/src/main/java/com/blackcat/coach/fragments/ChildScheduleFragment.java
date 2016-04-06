package com.blackcat.coach.fragments;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.activities.StudyConfirmsAct;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.adapters.ScheduleReservationAdapter;
import com.blackcat.coach.caldroid.CaldroidFragment;

import com.blackcat.coach.easemob.utils.CommonUtils;
import com.blackcat.coach.events.MonthApplyEvent;
import com.blackcat.coach.lib.calendar.AppointmentDay;
import com.blackcat.coach.lib.calendar.AppointmentWeekFragment;
import com.blackcat.coach.lib.calendar.OnDateClickListener;
import com.blackcat.coach.lib.calendar.WeekViewPager;
import com.blackcat.coach.lib.calendar.adapter.WeekViewPagerAdapter;
import com.blackcat.coach.models.CoachCourseVO;
import com.blackcat.coach.models.DaytimelysReservation;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;

import com.blackcat.coach.utils.CommonUtil;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.blackcat.coach.widgets.NoScrollViewPager;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
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
import sun.bob.mcalendarview.views.ExpWeekCalendarView;
import sun.bob.mcalendarview.vo.DateData;


/**
 * 日程
 */
public class ChildScheduleFragment extends BaseListFragment<Reservation> implements View.OnClickListener {

    public static Date selectedDate = Calendar.getInstance().getTime();
    // 基本变量
    private IndexActivity mContext;




    private CaldroidFragment caldroidFragment;
    private String mCurrentDate;

//    private static ExpWeekCalendarView expCalendarView;


    private float aspect = 180f / 112f;
    private ImageView expandIv;
    private ExpCalendarView popExpCalendarView;
    private MaterialCalendarView calendarView;
    private ViewGroup popview;
    private PopupWindow popWindow;
    private TextView yearAndMonthTv;
    private DaytimelyReservationFragment reservationFragment;
    private SimpleDateFormat format;
    private WeekViewPager viewPager;
    //    private NoScrollViewPager reservationViewpager;


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

    public void onEvent(MonthApplyEvent event){
        LogUtil.print("点击今天");
//        expCalendarView.setCurrentItem(CellConfig.middlePosition);
        Calendar calendar = Calendar.getInstance();
//        DateData dateData =  new DateData(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
//        expCalendarView.travelTo(dateData);
        if (reservationFragment != null) {
//                    selectedDate = date.getDate();
            reservationFragment.setData(calendar.getTime());
        }
        viewPager.setFragmentManger(getChildFragmentManager());

        viewPager.setSelectDate(calendar.getTime(),viewPager.getCurrentItem());
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
        View rootView = inflater.inflate(R.layout.fragment_child_schedule, container, false);
//        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_SCHEDULE);

        initView(rootView);

//        initData();
        return rootView;
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        Date beginDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 20);
        Date endDate = calendar.getTime();
        List<Date> dateList = CommonUtil.getDatesBetweenTwoDate(beginDate, endDate);
        ScheduleReservationAdapter reservationAdapter = new ScheduleReservationAdapter(getChildFragmentManager(), dateList);
//        reservationViewpager.setAdapter(reservationAdapter);
//        for (int i = 0; i < dateList.size(); i++) {
//            if (CommonUtil.isSameDate(dateList.get(i), Calendar.getInstance().getTime())) {
//                reservationViewpager.setCurrentItem(i);
//            }
//        }
    }


    private void switchPage(Date selectDate){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        Date beginDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 20);
        Date endDate = calendar.getTime();
        List<Date> dateList = CommonUtil.getDatesBetweenTwoDate(beginDate, endDate);
        ScheduleReservationAdapter reservationAdapter = new ScheduleReservationAdapter(getChildFragmentManager(), dateList);
//        reservationViewpager.setAdapter(reservationAdapter);
//        for (int i = 0; i < dateList.size(); i++) {
//            if (CommonUtil.isSameDate(dateList.get(i), selectDate)) {
//                reservationViewpager.setCurrentItem(i);
//            }
//        }
    }


    private int mYear;
    private int mMonth;

    private void initView(View rootView) {

        rootView.findViewById(R.id.working_hour_confirm_but).setOnClickListener(this);
        //      Get instance.
//        expCalendarView = ((ExpWeekCalendarView) rootView.findViewById(R.id.calendar_exp));
        viewPager = (WeekViewPager) rootView.findViewById(R.id.calendar_exp_vp);

        viewPager.setAdapter(new WeekViewPagerAdapter(getChildFragmentManager()));

        viewPager.setCurrentItem(500);
        viewPager.setOnDateClickListener(new OnDateClickListener() {

            @Override
            public void onDateClick(AppointmentDay day, boolean clickbale) {
                if (clickbale) {

                    LogUtil.print("点击了" + day.year + "-" + day.month + "-" + day.day);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(day.year, day.month, day.day);

                    if (reservationFragment != null) {

                        reservationFragment.setData(calendar.getTime());
                    }
                } else {
                    // LogUtil.print("木有点击了" + day);
                }
            }
        });


        expandIv = (ImageView) rootView.findViewById(R.id.schedule_expand_calendar);
        rootView.findViewById(R.id.schedule_calendar_layout).setOnClickListener(this);
        expandIv.setOnClickListener(this);

        reservationFragment = new DaytimelyReservationFragment();
        FragmentTransaction ft =getChildFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        format = new SimpleDateFormat("yyyy-MM-dd");
        args.putString("date", format.format(Calendar.getInstance().getTime()));
        reservationFragment.setArguments(args);
        ft.replace(R.id.schedule_reservation_fl, reservationFragment, "reservation");
        ft.commit();
//        reservationViewpager = (NoScrollViewPager) rootView.findViewById(R.id.schedule_reservation_viewpager);
//
//        reservationViewpager.setNoScroll(true);
//        reservationViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                //日历切换
//                LogUtil.print("=======日历切换"+expCalendarView.getCurrentItem());
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));


        imageInit();

//      Set up listeners.
//        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
//            @Override
//            public void onMonthChange(int year, int month) {
////                YearMonthTv.setText(String.format("%d年%d月", year, month));
////                if (!(mYear == year && mMonth == month)) {
////                    mMonth = month;
////                    mYear = year;
////                    mContext.setmToolBarTitle(String.format("%d-%d", mYear, mMonth));
//////                    obtainMonthApplyData(mYear + "", mMonth + "");
////
////                }
//
//                LogUtil.print("onMonthChange---"+year+"-"+month);
//                if (yearAndMonthTv != null) {
//
//                    yearAndMonthTv.setText(String.format("%d年%d月", year, month));
//                }
//
//            }
//
//            @Override
//            public void onMonthScroll(float positionOffset) {
////                Log.i("listener", "onMonthScroll:" + positionOffset);
//            }
//        });
//
//        expCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnDateClickListener(new OnExpDateClickListener() {
//            @Override
//            public void onDateClick(View view, DateData date) {
//                super.onDateClick(view, date);
//                if (popWindow != null&&popWindow.isShowing()) {
//                    popWindow.dismiss();
//                }
//                LogUtil.print("expCalendarView---"+expCalendarView.getCurrentItem());
//                if(reservationFragment!=null){
//                    reservationFragment.setData(format.format(date.getDate()));
//                }
//                selectDate = date.getDate();
////                switchPage(date.getDate());
////                String str = formatter.format(date.getDate());
//                LogUtil.print("formatter"+date.getDate().toLocaleString());
////                if (!mCurrentDate.equals(str)) {
////                    mCurrentDate = str;
////                    mAdapter.setList(null);
////                    mAdapter.notifyDataSetChanged();
////                    mPage = 1;
////                    if (!Session.isUserInfoEmpty()) {
////                        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mCurrentDate);
////                        refresh(DicCode.RefreshType.R_INIT, mURI);
////                    }
//////                    requestTime(str);
////                }
//            }
//        });

    }

    private void imageInit() {
        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
//        expCalendarView.shrink();

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

        Type type = new TypeToken<Result<List<CoachCourseVO>>>() {
        }.getType();

        GsonIgnoreCacheHeadersRequest<Result<List<CoachCourseVO>>> request = new GsonIgnoreCacheHeadersRequest<Result<List<CoachCourseVO>>>(
                url, type, map,
                new Response.Listener<Result<List<CoachCourseVO>>>() {
                    @Override
                    public void onResponse(Result<List<CoachCourseVO>> response) {
                        if (response != null && response.type == Result.RESULT_OK) {

                            List<CoachCourseVO> list = response.data;
                            LogUtil.print("list--size::" + list.size());
                            if (list.size() > 0)
                                LogUtil.print("list--size::222" + list.get(0).getClass());
                            for (CoachCourseVO coachCourseVO : list) {
                                LogUtil.print(coachCourseVO.getCoursetime().getBegintime() + "list--size::3333" + coachCourseVO.getSelectedstudentcount());
                            }
//                        timeLayout.clearData();
//                        timeLayout.setData(list,aspect);
//                            onFeedsResponse(response, refreshType);
                        } else {
                            ToastHelper.getInstance(CarCoachApplication.getInstance()).toast(response.msg);
                        }
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


        VolleyUtil.getQueue(getActivity()).add(request);
//        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_expand_calendar:
            case R.id.schedule_calendar_layout:
                //打开popwindow
                CellConfig.Week2MonthPos = CellConfig.middlePosition;
                CellConfig.ifMonth = true;
//                expCalendarView.getCurrentItem();
//                if (popExpCalendarView != null) {
//
//                    popExpCalendarView.expand();
//                }
                openCalendarWindow();
                break;
            case R.id.calendar_stop_expand_iv:
                if (popWindow != null) {
                    popWindow.dismiss();
                }
                break;
            case R.id.working_hour_confirm_but:
                startActivity(new Intent(getActivity(), StudyConfirmsAct.class));
                LogUtil.print("工时确认");

                break;
            default:
                break;
        }
    }

    private void openCalendarWindow() {
        if (popview == null) {
            popview = (ViewGroup) View.inflate(mContext, R.layout.fragment_calendar, null);

        }

        LogUtil.print(((LinearLayout) popview.findViewById(R.id.pop_calendar_fragment)).findViewById(R.id.calendar_exp) + "llllll");
        yearAndMonthTv = (TextView) popview.findViewById(R.id.pop_calendar_fragment).findViewById(R.id.calendar_date_tv);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月");
        yearAndMonthTv.setText(format1.format(new Date()));
        popview.findViewById(R.id.pop_calendar_fragment).findViewById(R.id.calendar_stop_expand_iv).setOnClickListener(this);
        LinearLayout ExpCalendarViewLayout = (LinearLayout) popview.findViewById(R.id.pop_calendar_fragment).findViewById(R.id.pop_schedule_calendar_ll);
        popExpCalendarView = (ExpCalendarView) ExpCalendarViewLayout.getChildAt(0);
        popExpCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnDateClickListener(new OnExpDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                super.onDateClick(view, date);

//                LogUtil.print("formatter----" + date.getDate().toLocaleString());
//
//                LogUtil.print("popExpCalendarView---" + popExpCalendarView.getCurrentItem());
//                LogUtil.print(viewPager.getCurrentItem()+"formatter----" + date.getDate().toLocaleString());

                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                if (reservationFragment != null) {

//                    reservationFragment.setData(format.format(date.getDate()));

//                    selectedDate = date.getDate();
                    reservationFragment.setData(date.getDate());

                }
                viewPager.setFragmentManger(getChildFragmentManager());

                viewPager.setSelectDate(date.getDate(),viewPager.getCurrentItem());
//               switchPage(date.getDate());
            }
        });




        Toolbar toolBar = mContext.getToolBar();
        popWindow = new PopupWindow(popview,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
//        popWindow.setAnimationStyle(R.style.CustomDialog);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageInit();
            }
        });
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setAnimationStyle(R.style.popwin_anim_style);
        popWindow.showAsDropDown(toolBar);
//        popWindow.showAsDropDown(expCalendarView);

//        popWindow.showAtLocation(toolBar, Gravity.TOP,0,0);
    }

    private void popAnimIn(){
//        popWindow.
    }


}