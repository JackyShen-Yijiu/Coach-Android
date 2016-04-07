package com.blackcat.coach.lib.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.blackcat.coach.R;
import com.blackcat.coach.fragments.ChildScheduleFragment;
import com.blackcat.coach.utils.LogUtil;


public class WeekViewPager extends ViewPager {

    public WeekViewPager(Context context) {
        super(context);
    }

    public WeekViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekViewPager setOnDateClickListener(
            OnDateClickListener onDateClickListener) {
        OnDateClickListener.instance = onDateClickListener;
        return this;
    }

    public void setSelectDate(Date seleDate, int currentItem) {

        Calendar currSelectDate = Calendar.getInstance();
        currSelectDate.setTime(seleDate);
        Calendar lastSelectDate = Calendar.getInstance();
        lastSelectDate.setTime(ChildScheduleFragment.selectedDate);
        int days = daysBetween(ChildScheduleFragment.selectedDate, seleDate);
        int selectItem = currentItem;
        selectItem = currentItem + days / 7;
            if (days > 0) {
                //选择日期大于之前选择的日期

                if (currSelectDate.get(Calendar.DAY_OF_WEEK) < lastSelectDate.get(Calendar.DAY_OF_WEEK)) {
                    selectItem++;
                }
            } else if (days < 0) {
                //选择日期小于之前选择的日期
                if (currSelectDate.get(Calendar.DAY_OF_WEEK) > lastSelectDate.get(Calendar.DAY_OF_WEEK)) {
                    selectItem--;
                }
            }
        this.setCurrentItem(selectItem);

        LogUtil.print(seleDate.toLocaleString() + "--" + currentItem);
        Fragment fragment = fragmentManager.findFragmentByTag(

                "android:switcher:" + R.id.calendar_exp_vp + ":" + this.getCurrentItem());
        if (fragment instanceof AppointmentWeekFragment) {
            LogUtil.print(days + "--" + (selectItem) + "-------===" + ChildScheduleFragment.selectedDate.toLocaleString());
            ((AppointmentWeekFragment) fragment).setSelectedDate(seleDate, selectItem);
        }

    }

    public int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);

        long time2 = cal.getTimeInMillis();

        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    private FragmentManager fragmentManager;

    public void setFragmentManger(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
