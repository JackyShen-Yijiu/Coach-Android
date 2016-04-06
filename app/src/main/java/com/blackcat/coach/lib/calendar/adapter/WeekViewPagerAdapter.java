package com.blackcat.coach.lib.calendar.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.lib.calendar.AppointmentWeekFragment;

/**
 * Created by yyhui on 2016/4/5.
 */
public class WeekViewPagerAdapter extends FragmentPagerAdapter {

    public WeekViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        AppointmentWeekFragment fragment = new AppointmentWeekFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }



//    @Override
//    public void setPrimaryItem(ViewGroup container, int position,
//                               Object object) {
//        // currentFragment = (AppointmentWeekFragment) object;
//        super.setPrimaryItem(container, position, object);
//    }

    @Override
    public int getCount() {
        return 1000;
    }


}
