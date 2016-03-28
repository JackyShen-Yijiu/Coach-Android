package com.blackcat.coach.adapters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blackcat.coach.fragments.DaytimelyReservationFragment;
import com.blackcat.coach.fragments.ItemFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by aa on 2016/1/9.
 */
public class ScheduleReservationAdapter extends FragmentPagerAdapter {

    private List<Date> dates;
    private DaytimelyReservationFragment fragment;


    public ScheduleReservationAdapter(FragmentManager fm, List<Date> dates) {
        super(fm);
        this.dates = dates;
    }


    @Override
    public Fragment getItem(int position) {

        if (fragment == null || (!fragment.isAdded())) {
            fragment = new DaytimelyReservationFragment();
        }
        Bundle args = new Bundle();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        args.putString("date",format.format(dates.get(position)));
        fragment.setArguments(args);
//        fragment.setData(dates.get(position));
//        if(fragments[position].isAdded()){
//            return fragments[position];
//        }
//        Bundle args = new Bundle();
//        args.putInt("type", position);
////        args.putString("type", titles[position]);
//        fragments[position].setArguments(args);
//        return fragments[position];

        return fragment;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

}
