package com.blackcat.coach.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.blackcat.coach.R;
import com.blackcat.coach.adapters.CommonAdapter;
import com.blackcat.coach.models.DicCode;
import com.blackcat.coach.models.OrderMsg;
import com.blackcat.coach.models.Result;
import com.blackcat.coach.models.Session;
import com.blackcat.coach.net.NetConstants;
import com.blackcat.coach.net.URIUtil;
import com.blackcat.coach.utils.LogUtil;
import com.blackcat.coach.utils.SpHelper;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

/**
 * Created by yyhui on 2016/3/28.
 */
public class CalendarDialogFragment extends BaseFragment implements View.OnClickListener {

    private ExpCalendarView popExpCalendarView;
    private ViewGroup view;
    private TextView date;

    public static CalendarDialogFragment newInstance() {
        CalendarDialogFragment fragment = new CalendarDialogFragment();
        return fragment;
    }

    public CalendarDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (ViewGroup) View.inflate(getActivity(), R.layout.schedule_calendar_popwindow, null);

        LinearLayout popLayout = (LinearLayout) view.findViewById(R.id.pop_schedule_calendar_ll);
        popExpCalendarView = new ExpCalendarView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        popLayout.addView(popExpCalendarView, params);
//        popExpCalendarView.setId();
//        popLayout.addView(popExpCalendarView);



        CellConfig.Week2MonthPos = CellConfig.middlePosition;
        CellConfig.ifMonth = true;
        popExpCalendarView.expand();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        LogUtil.print("CalendarDialogFragmentssssssssss");
    }

    private void initView() {
        date = (TextView) view.findViewById(R.id.calendar_date_tv);
        view.findViewById(R.id.calendar_stop_expand_iv).setOnClickListener(this);
        popExpCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {
//                YearMonthTv.setText(String.format("%d年%d月", year, month));
                date.setText(String.format("%d年%d月", year, month));
            }

            @Override
            public void onMonthScroll(float positionOffset) {
//                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });

//        popExpCalendarView.setOnDateClickListener(new OnExpDateClickListener()).setOnDateClickListener(new OnExpDateClickListener() {
//            @Override
//            public void onDateClick(View view, DateData date) {
//                super.onDateClick(view, date);
//                LogUtil.print("formatter-------");
//                if(onDateChangeListener!=null){
//                    onDateChangeListener.OnDateSelected();
//                }
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_stop_expand_iv:

                if(onDateChangeListener!=null){
                    onDateChangeListener.onDisMiss();
                }
            break;

            default:
                break;
        }
    }


    private OnDateChangeListener onDateChangeListener;
    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener){
        this.onDateChangeListener = onDateChangeListener;
    }

    public interface OnDateChangeListener{
        void onDisMiss();
        void OnDateSelected();
    }

}
