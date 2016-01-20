package com.blackcat.coach.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.R;
import com.blackcat.coach.activities.IndexActivity;
import com.blackcat.coach.activities.LoginActivity;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.caldroid.CaldroidFragment;
import com.blackcat.coach.caldroid.CaldroidListener;
import com.blackcat.coach.lib.calendar.adapter.CalendarGridView;
import com.blackcat.coach.lib.calendar.adapter.CalendarGridViewAdapter;
import com.blackcat.coach.lib.calendar.adapter.WeekNameAdapter;
import com.blackcat.coach.lib.calendar.util.CalendarUtil;
import com.blackcat.coach.lib.calendar.util.NumberHelper;
import com.blackcat.coach.models.CoachInfo;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.Reservation;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.models.params.LoginParams;
import com.blackcat.coach.models.params.MonthApplyData;
import com.blackcat.coach.net.GsonIgnoreCacheHeadersRequest;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.BaseUtils;
import com.blackcat.coach.utils.Constants;
import com.blackcat.coach.utils.GsonUtils;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.ToastHelper;
import com.blackcat.coach.utils.VolleyUtil;
import com.google.gson.reflect.TypeToken;
import android.view.View.OnTouchListener;

import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.animation.Animation.AnimationListener;

import cn.jpush.android.api.JPushInterface;

/**
 * create an instance of this fragment.
 */
public class ChildScheduleFragment extends BaseListFragment<Reservation> implements OnTouchListener {


    /**
     * 日历布局ID
     */
    private static final int CAL_LAYOUT_ID = 55;
    // 判断手势用
    private static final int SWIPE_MIN_DISTANCE = 120;

    private static final int SWIPE_MAX_OFF_PATH = 250;

    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    /**
     * 用于传递选中的日期
     */
    private static final String MESSAGE = "msg";

    // 动画
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    GestureDetector mGesture = null;

    /**
     * 今天按钮
     */
    // private Button mTodayBtn;

    /**
     * 上一个月按钮
     */
    private ImageView mPreMonthImg;

    /**
     * 下一个月按钮
     */
    private ImageView mNextMonthImg;

    /**
     * 用于显示今天的日期
     */
    private TextView mDayMessage;

    /**
     * 用于装截日历的View
     */
    private RelativeLayout mCalendarMainLayout;

    // 基本变量
    private Context mContext;
    /**
     * 上一个月View
     */
    private GridView firstGridView;

    /**
     * 当前月View
     */
    private GridView currentGridView;

    /**
     * 下一个月View
     */
    private GridView lastGridView;

    /**
     * 当前显示的日历
     */
    private Calendar calStartDate = Calendar.getInstance();

    /**
     * 选择的日历
     */
    private Calendar calSelected = Calendar.getInstance();

    /**
     * 今日
     */
    private Calendar calToday = Calendar.getInstance();

    /**
     * 当前界面展示的数据源
     */
    private CalendarGridViewAdapter currentGridAdapter;

    /**
     * 预装载上一个月展示的数据源
     */
    private CalendarGridViewAdapter firstGridAdapter;

    /**
     * 预装截下一个月展示的数据源
     */
    private CalendarGridViewAdapter lastGridAdapter;

    //
    /**
     * 当前视图月
     */
    private int mMonthViewCurrentMonth = 0;

    /**
     * 当前视图年
     */
    private int mMonthViewCurrentYear = 0;

    /**
     * 起始周
     */
    private int iFirstDayOfWeek = Calendar.MONDAY;

    private WeekNameAdapter week_adapter;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_child_schedule, container, false);
        initViews(rootView, inflater, CommonAdapter.AdapterType.TYPE_ADAPTER_SCHEDULE);

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        mCurrentDate = formatter.format(new Date());// 当期日期
        mType = new TypeToken<Result<List<Reservation>>>() {
        }.getType();
        mPage = 1;
        if (!Session.isUserInfoEmpty()) {
            mURI = URIUtil.getScheduleList(Session.getSession().coachid, mPage, mCurrentDate);
            refresh(DicCode.RefreshType.R_INIT, mURI);
        }
        mContext = getActivity();
        initView(rootView);
        initData();

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        CreateGirdView();
        //获取当月的信息
        obtainMonthApplyData(today.get(Calendar.YEAR) + "", (today.get(Calendar.MONTH) + 1) + "");

//        // Setup caldroid fragment
//        // **** If you want normal CaldroidFragment, use below line ****
//        caldroidFragment = new CaldroidFragment();
//
//
//        // Setup arguments
//        // If Activity is created after rotation
//        if (savedInstanceState != null) {
//            caldroidFragment.restoreStatesFromKey(savedInstanceState,
//                    "CALDROID_SAVED_STATE");
//        }
//        // If activity is created from fresh
//        else {
//            Bundle args = new Bundle();
//            Calendar cal = Calendar.getInstance();
//            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
//            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
//            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
//            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
//
//            // Uncomment this to customize startDayOfWeek
//            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
//            // CaldroidFragment.TUESDAY); // Tuesday
//
//            // Uncomment this line to use Caldroid in compact mode
//             args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
//
//            // Uncomment this line to use dark theme
////            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
//
//            caldroidFragment.setArguments(args);
//        }

        // Attach to the activity
//        FragmentTransaction t = getChildFragmentManager().beginTransaction();
//        t.replace(R.id.calendar1, caldroidFragment);
//        t.commit();
//
//        // Setup listener
//        final CaldroidListener listener = new CaldroidListener() {
//
//            @Override
//            public void onSelectDate(Date date, View view) {
//                caldroidFragment.clearSelectedDates();
//                caldroidFragment.setSelectedDate(date);
//                caldroidFragment.refreshView();
//                String str = formatter.format(date);
//                if (!mCurrentDate.equals(str)) {
//                    mCurrentDate = str;
//                    mAdapter.setList(null);
//                    mAdapter.notifyDataSetChanged();
//                    mPage = 1;
//                    if (!Session.isUserInfoEmpty()) {
//                        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mPage, mCurrentDate);
//                        refresh(DicCode.RefreshType.R_INIT, mURI);
//                    }
//                }
//            }
//
//            @Override
//            public void onChangeMonth(int month, int year) {
//            }
//
//            @Override
//            public void onLongClickDate(Date date, View view) {
//            }
//
//            @Override
//            public void onCaldroidViewCreated() {
//                if (caldroidFragment.getLeftArrowButton() != null) {
//                }
//            }
//
//        };
//
//        // Setup Caldroid
//        caldroidFragment.setCaldroidListener(listener);

        return rootView;
    }

    private void obtainMonthApplyData(String year,String month) {
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
//                                update();
                                CreateGirdView();
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


    private void initData() {
        String[] weekNameStrings = { "日", "一", "二", "三", "四", "五", "六" };
        List<String> data_list = new ArrayList<String>();
        for (int i = 0; i < weekNameStrings.length; i++) {
            data_list.add(weekNameStrings[i]);
        }
        week_adapter = new WeekNameAdapter(mContext, data_list);
        mWeekName.setAdapter(week_adapter);

        updateStartDateForMonth();

        generateContetView(mCalendarMainLayout);
        slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils
                .loadAnimation(mContext, R.anim.slide_left_out);
        slideRightIn = AnimationUtils
                .loadAnimation(mContext, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(mContext,
                R.anim.slide_right_out);

        slideLeftIn.setAnimationListener(animationListener);
        slideLeftOut.setAnimationListener(animationListener);
        slideRightIn.setAnimationListener(animationListener);
        slideRightOut.setAnimationListener(animationListener);

        mGesture = new GestureDetector(mContext, new GestureListener());

    }

    /**
     * 用于初始化控件
     */
    private void initView( View rootView) {
        // mTodayBtn = (Button) findViewById(R.id.today_btn);
        mDayMessage = (TextView) rootView.findViewById(R.id.calendar_month_year_textview);
        mCalendarMainLayout = (RelativeLayout) rootView.findViewById(R.id.calendar_main);
        mPreMonthImg = (ImageView) rootView.findViewById(R.id.calendar_left_arrow);
        mNextMonthImg = (ImageView) rootView.findViewById(R.id.calendar_right_arrow);
        mWeekName = (GridView) rootView.findViewById(R.id.calendar_week);
        // mTodayBtn.setOnClickListener(onTodayClickListener);
        mPreMonthImg.setOnClickListener(onPreMonthClickListener);

        mNextMonthImg.setOnClickListener(onNextMonthClickListener);
    }






    /**
     * 用于加载到当前的日期的事件
     */
    private View.OnClickListener onTodayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calStartDate = Calendar.getInstance();
            calSelected = Calendar.getInstance();
            updateStartDateForMonth();
            generateContetView(mCalendarMainLayout);
        }
    };

    /**
     * 用于加载上一个月日期的事件
     */
    private View.OnClickListener onPreMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewFlipper.setInAnimation(slideRightIn);
            viewFlipper.setOutAnimation(slideRightOut);
            viewFlipper.showPrevious();
            setPrevViewItem();
        }
    };

    /**
     * 用于加载下一个月日期的事件
     */
    private View.OnClickListener onNextMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long start = System.currentTimeMillis();
            viewFlipper.setInAnimation(slideLeftIn);
            viewFlipper.setOutAnimation(slideLeftOut);
            viewFlipper.showNext();
            setNextViewItem();
            LogUtil.print("next--end-->"+(System.currentTimeMillis()-start));
        }
    };

    /**
     * 主要用于生成发前展示的日历View
     *
     * @param layout
     *            将要用于去加载的布局
     */
    private void generateContetView(RelativeLayout layout) {
        // 创建一个垂直的线性布局（整体内容）
        viewFlipper = new ViewFlipper(getActivity());
        viewFlipper.setId(CAL_LAYOUT_ID);
        calStartDate = getCalendarStartDate();
//        CreateGirdView();

        RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(viewFlipper, params_cal);

        LinearLayout br = new LinearLayout(mContext);
        RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 1);
        params_br.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
        // br.setBackgroundColor(getResources().getColor(
        // R.color.calendar_background));
        layout.addView(br, params_br);
    }

    /**
     * 用于创建当前将要用于展示的View
     */
    private void CreateGirdView() {
        LogUtil.print("calendar---create");
        Calendar firstCalendar = Calendar.getInstance(); // 临时
        Calendar currentCalendar = Calendar.getInstance(); // 临时
        Calendar lastCalendar = Calendar.getInstance(); // 临时
        firstCalendar.setTime(calStartDate.getTime());
        currentCalendar.setTime(calStartDate.getTime());
        lastCalendar.setTime(calStartDate.getTime());

        firstGridView = new CalendarGridView(mContext);
        firstCalendar.add(Calendar.MONTH, -1);
//        obtainMonthApplyData(firstCalendar.get(Calendar.YEAR) + "", (firstCalendar.get(Calendar.MONTH) + 1) + "");
        firstGridAdapter = new CalendarGridViewAdapter(getActivity(), firstCalendar,leaveoff,reservationapply);
        firstGridView.setAdapter(firstGridAdapter);// 设置菜单Adapter
        firstGridView.setId(CAL_LAYOUT_ID);
        LogUtil.print("first00-->" + firstCalendar.get(Calendar.MONTH));
//        obtainMonthApplyData(currentCalendar.get(Calendar.YEAR) + "", (currentCalendar.get(Calendar.MONTH) + 1) + "");
        currentGridView = new CalendarGridView(mContext);
        currentGridAdapter = new CalendarGridViewAdapter(getActivity(), currentCalendar,leaveoff,reservationapply);
        currentGridView.setAdapter(currentGridAdapter);// 设置菜单Adapter
        currentGridView.setId(CAL_LAYOUT_ID);
        LogUtil.print("first11-->" + currentCalendar.get(Calendar.MONTH));
        lastGridView = new CalendarGridView(mContext);
        lastCalendar.add(Calendar.MONTH, 1);
//        obtainMonthApplyData(lastCalendar.get(Calendar.YEAR) + "", (lastCalendar.get(Calendar.MONTH) + 1) + "");
        lastGridAdapter = new CalendarGridViewAdapter(getActivity(), lastCalendar,leaveoff,reservationapply);
        lastGridView.setAdapter(lastGridAdapter);// 设置菜单Adapter
        lastGridView.setId(CAL_LAYOUT_ID);
        LogUtil.print("first22-->" + lastCalendar.get(Calendar.MONTH));
        currentGridView.setOnTouchListener(this);
        firstGridView.setOnTouchListener(this);
        lastGridView.setOnTouchListener(this);

        if (viewFlipper.getChildCount() != 0) {
            viewFlipper.removeAllViews();
        }

        viewFlipper.addView(currentGridView);
        viewFlipper.addView(lastGridView);
        viewFlipper.addView(firstGridView);

        String s = calStartDate.get(Calendar.YEAR)
                + "年"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1)+"月";
        mDayMessage.setText(s);

//        lastGridAdapter.setData();
    }

    private void update(){
        LogUtil.print("calendar---update");
        Calendar firstCalendar = Calendar.getInstance();

        Calendar currentCalendar = Calendar.getInstance(); // 临时
        Calendar lastCalendar = Calendar.getInstance(); // 临时
        firstCalendar.setTime(calStartDate.getTime());
        currentCalendar.setTime(calStartDate.getTime());
        lastCalendar.setTime(calStartDate.getTime());

        //第二个
        currentCalendar.add(Calendar.MONTH, -1);
        currentGridAdapter.setData(currentCalendar, leaveoff, reservationapply);
        currentGridAdapter.notifyDataSetChanged();
//        LogUtil.print("--第2个-->" + currentCalendar.get(Calendar.MONTH));
        //第一个
        firstCalendar.add(Calendar.MONTH, -2);
        firstGridAdapter.setData(firstCalendar, leaveoff, reservationapply);
        firstGridAdapter.notifyDataSetChanged();
//        LogUtil.print("--第一个-->" + firstCalendar.get(Calendar.MONTH));
        //第三个
//        lastCalendar.add(Calendar.MONTH, 1);
        lastGridAdapter.setData(lastCalendar, leaveoff, reservationapply);
        lastGridAdapter.notifyDataSetChanged();
//        LogUtil.print("--第3个-->" + lastCalendar.get(Calendar.MONTH));
        String s = calStartDate.get(Calendar.YEAR)
                + "年"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1)+"月";
        mDayMessage.setText(s);

    }

    private void updateGirdView() {

        Calendar firstCalendar = Calendar.getInstance(); // 临时
        Calendar currentCalendar = Calendar.getInstance(); // 临时
        Calendar lastCalendar = Calendar.getInstance(); // 临时
        firstCalendar.setTime(calStartDate.getTime());
        currentCalendar.setTime(calStartDate.getTime());
        lastCalendar.setTime(calStartDate.getTime());

        firstGridAdapter.setData(firstCalendar, leaveoff, reservationapply);
        firstGridView = new CalendarGridView(mContext);
        firstCalendar.add(Calendar.MONTH, -1);
        firstGridAdapter.notifyDataSetChanged();
//        obtainMonthApplyData(firstCalendar.get(Calendar.YEAR) + "", (firstCalendar.get(Calendar.MONTH) + 1) + "");

        firstGridAdapter = new CalendarGridViewAdapter(getActivity(), firstCalendar,leaveoff,reservationapply);
        firstGridView.setAdapter(firstGridAdapter);// 设置菜单Adapter
        firstGridView.setId(CAL_LAYOUT_ID);


        currentGridAdapter.setData(currentCalendar,leaveoff,reservationapply);

//        obtainMonthApplyData(currentCalendar.get(Calendar.YEAR) + "", (currentCalendar.get(Calendar.MONTH) + 1) + "");
        currentGridView = new CalendarGridView(mContext);
        currentGridAdapter = new CalendarGridViewAdapter(getActivity(), currentCalendar,leaveoff,reservationapply);
        currentGridView.setAdapter(currentGridAdapter);// 设置菜单Adapter
        currentGridView.setId(CAL_LAYOUT_ID);

//        lastGridView = new CalendarGridView(mContext);
//        lastGridAdapter.setData();
        lastCalendar.add(Calendar.MONTH, 1);
//        obtainMonthApplyData(lastCalendar.get(Calendar.YEAR) + "", (lastCalendar.get(Calendar.MONTH) + 1) + "");

//        lastGridAdapter = new CalendarGridViewAdapter(getActivity(), lastCalendar,leaveoff,reservationapply);
//        lastGridView.setAdapter(lastGridAdapter);// 设置菜单Adapter
        lastGridView.setId(CAL_LAYOUT_ID);

        currentGridView.setOnTouchListener(this);
        firstGridView.setOnTouchListener(this);
        lastGridView.setOnTouchListener(this);

        if (viewFlipper.getChildCount() != 0) {
            viewFlipper.removeAllViews();
        }

        viewFlipper.addView(currentGridView);
        viewFlipper.addView(lastGridView);
        viewFlipper.addView(firstGridView);

        String s = calStartDate.get(Calendar.YEAR)
                + "年"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1)+"月";
        mDayMessage.setText(s);
    }

    /**
     * 上一个月
     */
    private void setPrevViewItem() {
        mMonthViewCurrentMonth--;// 当前选择月--
        // 如果当前月为负数的话显示上一年
        if (mMonthViewCurrentMonth == -1) {
            mMonthViewCurrentMonth = 11;
            mMonthViewCurrentYear--;
        }
        obtainMonthApplyData(mMonthViewCurrentYear + "", (mMonthViewCurrentMonth + 1) + "");
        calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
        calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth); // 设置月
        calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear); // 设置年

    }

    /**
     * 下一个月
     */
    private void setNextViewItem() {
        mMonthViewCurrentMonth++;
        if (mMonthViewCurrentMonth == 12) {
            mMonthViewCurrentMonth = 0;
            mMonthViewCurrentYear++;
        }
        obtainMonthApplyData(mMonthViewCurrentYear + "", (mMonthViewCurrentMonth + 1) + "");
        LogUtil.print("mMonthViewCurrentMonth--"+mMonthViewCurrentMonth);
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear);

    }

    /**
     * 根据改变的日期更新日历 填充日历控件用
     */
    private void updateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
        mMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
        mMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

        String s = calStartDate.get(Calendar.YEAR)
                + "年"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1)+"月";
        mDayMessage.setText(s);
        // 星期一是2 星期天是1 填充剩余天数
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

    }

    /**
     * 用于获取当前显示月份的时间
     *
     * @return 当前显示月份的时间
     */
    private Calendar getCalendarStartDate() {
        calToday.setTimeInMillis(System.currentTimeMillis());
        calToday.setFirstDayOfWeek(iFirstDayOfWeek);

        if (calSelected.getTimeInMillis() == 0) {
            calStartDate.setTimeInMillis(System.currentTimeMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        } else {
            calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        }

        return calStartDate;
    }

    AnimationListener animationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // 当动画完成后调用
            CreateGirdView();
//            update();
        }
    };
    private GridView mWeekName;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
         return mGesture.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                    viewFlipper.showNext();
                    setNextViewItem();
                    return true;

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    viewFlipper.showPrevious();
                    setPrevViewItem();
                    return true;

                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 得到当前选中的是第几个单元格
            int pos = currentGridView.pointToPosition((int) e.getX(),
                    (int) e.getY());
            LinearLayout txtDay = (LinearLayout) currentGridView
                    .findViewById(pos + 5000);
            if (txtDay != null) {
                if (txtDay.getTag() != null) {
                    Date date = (Date) txtDay.getTag();
                    // if (CalendarUtil.compare(date, Calendar.getInstance()
                    // .getTime())) {
                    calSelected.setTime(date);
                    currentGridAdapter.setSelectedDate(calSelected);
                    currentGridAdapter.notifyDataSetChanged();
                    firstGridAdapter.setSelectedDate(calSelected);
                    firstGridAdapter.notifyDataSetChanged();

                    lastGridAdapter.setSelectedDate(calSelected);
                    lastGridAdapter.notifyDataSetChanged();
                    String week = CalendarUtil.getWeek(calSelected);
                    String message = CalendarUtil.getDay(calSelected) + " 农历"
                            + new CalendarUtil(calSelected).getDay() + " "
                            + week;
                    String str = formatter.format(date);
                if (!mCurrentDate.equals(str)) {
                    mCurrentDate = str;
                    mAdapter.setList(null);
                    mAdapter.notifyDataSetChanged();
                    mPage = 1;
                    if (!Session.isUserInfoEmpty()) {
                    LogUtil.print(message);
                        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mPage, mCurrentDate);
                        refresh(DicCode.RefreshType.R_INIT, mURI);
                    }
                }
//                    Toast.makeText(mContext,
//                            "您选择的日期为:" + message, Toast.LENGTH_SHORT).show();
                    // }
                    // else {
                    // Toast.makeText(getApplicationContext(),
                    // "选择的日期不能小于今天的日期", Toast.LENGTH_SHORT).show();
                    // }
                }
            }

            Log.i("TEST", "onSingleTapUp -  pos=" + pos);

            return false;
        }
    }


    @Override
    protected void initViews(View rootView, LayoutInflater inflater,
                             int adapterType) {
        super.initViews(rootView, inflater, adapterType);
    }


    @Override
    public void onRefresh() {
        mPage = 1;
        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mPage, mCurrentDate);
        refresh(DicCode.RefreshType.R_PULL_DOWN, mURI);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        mURI = URIUtil.getScheduleList(Session.getSession().coachid, mPage, mCurrentDate);
        refresh(DicCode.RefreshType.R_PULL_UP, mURI);
    }

    @Override
    protected void onFeedsErrorResponse(VolleyError arg0, int refreshType) {
        super.onFeedsErrorResponse(arg0, refreshType);
        if (refreshType == DicCode.RefreshType.R_PULL_UP) {
            mPage--;
        }
    }
}
